package br.com.dht.aplication;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class BrokerMQTT extends Thread {

	private List<Long> listId = new ArrayList<Long>();

	private final static String QUEUE_JOIN = "join";

	private final static String QUEUE_GET = "get";

	private final static String QUEUE_PUT = "put";

	public void subJoin() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_JOIN, false, false, false, null);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");

			Long nodeId = Long.valueOf(message);
			if (listId.size() < 8 && !listId.contains(nodeId))
				listId.add(nodeId);

			System.out.println(" [x] Received Broker Join: '" + message + "' " + listId.toString());
		};
		channel.basicConsume(QUEUE_JOIN, true, deliverCallback, consumerTag -> {
		});
	}

	public void pubJoin() throws Exception {
		String message = listId.toString();
		System.out.println(" [x] Send Broker Join: '" + message + "'");

		for (Long nodeId : listId) {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
				channel.queueDeclare(QUEUE_JOIN + nodeId, false, false, false, null);
				channel.basicPublish("", QUEUE_JOIN + nodeId, null, message.getBytes(StandardCharsets.UTF_8));
			}
		}
	}

	public void subGet() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_GET, false, false, false, null);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String key = new String(delivery.getBody(), "UTF-8");

			System.out.println(" [x] Received Broker: '" + key + "' ");

			try {
				pubGet(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		channel.basicConsume(QUEUE_GET, true, deliverCallback, consumerTag -> {
		});
	}

	public void pubGet(String key) throws Exception {
		System.out.println(" [x] Send Broker: '" + key + "'");

		for (Long nodeId : listId) {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
				channel.queueDeclare(QUEUE_GET + nodeId, false, false, false, null);
				channel.basicPublish("", QUEUE_GET + nodeId, null, key.getBytes(StandardCharsets.UTF_8));
			}
		}
	}

	public void subPut() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_PUT, false, false, false, null);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String menssagem = new String(delivery.getBody(), "UTF-8");

			System.out.println(" [x] Received Broker Put: '" + menssagem + "' ");

			try {
				pubPut(menssagem);
			} catch (Exception e) {
				e.printStackTrace();
			}

		};
		channel.basicConsume(QUEUE_PUT, true, deliverCallback, consumerTag -> {
		});
	}

	public void pubPut(String menssagem) throws Exception {
		System.out.println(" [x] Send Broker Put: '" + menssagem + "'");

		for (Long nodeId : listId) {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
				channel.queueDeclare(QUEUE_PUT + nodeId, false, false, false, null);
				channel.basicPublish("", QUEUE_PUT + nodeId, null, menssagem.getBytes(StandardCharsets.UTF_8));
			}
		}
	}

	@Override
	public void run() {
		try {
			subJoin();

			while (listId.size() < 8) {
				Thread.sleep(200);
			}
			Collections.sort(listId);
			System.out.println("[xxx] Lista Ordenada: '" + listId.toString());

			// Concluindo etapa de Boot Concluida
			pubJoin();

			subGet();
			subPut();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
