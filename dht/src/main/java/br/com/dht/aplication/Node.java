package br.com.dht.aplication;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Node extends Thread {

	private Long nodeId;
	private Long ant;
	private Long intervalo;
	private Map<Long, String> hash = new HashMap<Long, String>();

	private final static String QUEUE_JOIN = "join";

	private final static String QUEUE_GET = "get";
	private final static String QUEUE_GET_OK = "get_ok";

	private final static String QUEUE_PUT = "put";
	private final static String QUEUE_PUT_OK = "put_ok";

	/* Publica o nodeId para o broker */
	public void pubJoin() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.queueDeclare(QUEUE_JOIN, false, false, false, null);
			String message = String.valueOf(nodeId);
			channel.basicPublish("", QUEUE_JOIN, null, message.getBytes(StandardCharsets.UTF_8));
			System.out.println(" [x] Send Node Join: '" + message);
		}
	}

	/* Verifica o broker está com todos os nós */
	public void subJoin() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_JOIN + nodeId, false, false, false, null);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");

			message = message.substring(1, message.length() - 1);

			List<String> list = Arrays.asList(message.split(", "));

			int x = list.indexOf(nodeId.toString());

			if (x == 0) {
				ant = Long.valueOf(list.get(list.size() - 1));
			} else {
				ant = Long.valueOf(list.get(x - 1));
			}

			System.out.println(" [x] Receivede Node Join: " + x + " nodeId: " + nodeId + " ant: " + ant);
		};

		channel.basicConsume(QUEUE_JOIN + nodeId, true, deliverCallback, consumerTag -> {
		});
	}

	/* Verifica se há conteudo a ser consumido */
	public void subGet() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_GET + nodeId, false, false, false, null);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String mensagem = new String(delivery.getBody(), "UTF-8");

			Long key = Long.valueOf(mensagem);

			if ((key > ant && key <= nodeId) //
					|| (ant > nodeId) && ((nodeId >= key && key >= 0) || (intervalo > key && key > ant))) {
				try {
					System.out.println(" [x] Received Node: '" + mensagem + "' nodeId: " + nodeId);
					pubGetOk(mensagem);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		channel.basicConsume(QUEUE_GET + nodeId, true, deliverCallback, consumerTag -> {
		});
	}

	/* Publica ok para o get do cliente */
	public void pubGetOk(String key) throws Exception, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.queueDeclare(QUEUE_GET_OK, false, false, false, null);

			String message;
			if (hash.containsKey(Long.valueOf(key)))
				message = hash.get(Long.valueOf(key));
			else {
				message = "Null";
			}
			channel.basicPublish("", QUEUE_GET_OK, null, message.getBytes(StandardCharsets.UTF_8));
			System.out.println(" [x] Send node GetOK: '" + message + "' nodeId: " + nodeId);
		}
	}

	/* Verifica se há conteudo a ser consumido */
	public void subPut() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_PUT + nodeId, false, false, false, null);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String mensagem = new String(delivery.getBody(), "UTF-8");

			int indice = mensagem.indexOf(" , ");
			String chave = mensagem.substring(0, indice);
			String valor = mensagem.substring(indice + 3, mensagem.length() - 1);

			Long key = Long.valueOf(chave);

			if ((key > ant && key <= nodeId) //
					|| (ant > nodeId) && ((nodeId >= key && key >= 0) || (intervalo > key && key > ant))) {
				try {
					System.out
							.println(" [x] Received Node Put nodeId: " + nodeId + " key: " + key + " value: " + valor);
					pubPutOk(chave, valor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		channel.basicConsume(QUEUE_PUT + nodeId, true, deliverCallback, consumerTag -> {
		});
	}

	/* Publica ok para o put do cliente */
	public void pubPutOk(String key, String value) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.queueDeclare(QUEUE_PUT_OK, false, false, false, null);

			this.hash.put(Long.valueOf(key), value);

			channel.basicPublish("", QUEUE_PUT_OK, null, key.getBytes(StandardCharsets.UTF_8));
			System.out.println(" [x] Send node PutOK: '" + key + "' nodeId: " + nodeId);
		}
	}

	@Override
	public void run() {
		try {
			/* Gerando id aleatorio */
			int n = 32;
			this.intervalo = (long) Math.pow(2, n);
			this.nodeId = (long) (Math.random() * (this.intervalo));

			/* Publica Seu id */
			pubJoin();

			Thread.sleep(1000);

			/* Lê o anterior */
			subJoin();

			Thread.sleep(1000);

			/**/
			subGet();

			subPut();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
