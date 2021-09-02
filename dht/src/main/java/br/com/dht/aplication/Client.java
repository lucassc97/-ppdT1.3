package br.com.dht.aplication;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Client extends Thread {

	private final static String QUEUE_GET = "get";
	private final static String QUEUE_GET_OK = "get_ok";

	private final static String QUEUE_PUT = "put";
	private final static String QUEUE_PUT_OK = "put_ok";

	/* Publica o get da chave */
	public void pubGet(String key) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.queueDeclare(QUEUE_GET, false, false, false, null);

			channel.basicPublish("", QUEUE_GET, null, key.getBytes(StandardCharsets.UTF_8));
			System.out.println(" [x] Send Client Get'" + key + "'");
		}
	}

	/* Verifica se há conteudo a ser consumido */
	public void subGetOk() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_GET_OK, false, false, false, null);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");
			System.out.println(" [x] Received Client GetOK: '" + message + "' ");
		};
		channel.basicConsume(QUEUE_GET_OK, true, deliverCallback, consumerTag -> {
		});
	}

	/* Publica o put da chave */
	public void pubPut(String key, String value) throws Exception, TimeoutException {
		String mensagem = key + " , " + value;

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.queueDeclare(QUEUE_PUT, false, false, false, null);

			channel.basicPublish("", QUEUE_PUT, null, mensagem.getBytes(StandardCharsets.UTF_8));
			System.out.println(" [x] Send Client Put '" + mensagem + "'");
		}
	}

	/* Verifica se há conteudo a ser consumido */
	public void subPutOk() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_PUT_OK, false, false, false, null);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");
			System.out.println(" [x] Received Client PutOK: '" + message + "' ");
		};
		channel.basicConsume(QUEUE_PUT_OK, true, deliverCallback, consumerTag -> {
		});
	}

	@Override
	public void run() {
		try {
			subGetOk();
			subPutOk();

			Thread.sleep(1000);
			System.out.println(" [xxx]");
			pubPut("0", "Borda do mundo!");

			Thread.sleep(1000);
			System.out.println(" [xxx]");
			pubGet("0");

			Thread.sleep(1000);
			System.out.println(" [xxx]");
			pubPut("229495846", "Olá Mundo!");

			Thread.sleep(1000);
			System.out.println(" [xxx]");
			pubGet("229495846");

			Thread.sleep(1000);
			System.out.println(" [xxx]");
			pubPut("4294967295", "Aqui é a Borda do Mundo!");

			Thread.sleep(1000);
			System.out.println(" [xxx]");
			pubGet("4294967295");

			Thread.sleep(1000);
			System.out.println(" [xxx]");
			System.out.println("Utilize a interface padrão (via console):");
			System.out.println("get(key) ou put(key, value)");

			Scanner sc = new Scanner(System.in);

			while (true) {
				if (sc.hasNextLine()) {
					String console = sc.nextLine().trim();

					System.out.println(console);

					if (console.contains("put(") && console.lastIndexOf(")") == console.length() - 1) {
						int indice = console.indexOf(",");

						String chave = console.substring(4, indice).trim();
						String valor = console.substring(indice + 1, console.length() - 1).trim();

						System.out.println("chave: '" + chave + "'");
						System.out.println("valor: '" + valor + "'");

						pubPut(chave, valor);

					} else if (console.contains("get(") && console.lastIndexOf(")") == console.length() - 1) {
						String chave = console.substring(4, console.length() - 1).trim();

						System.out.println("chave: '" + chave + "'");

						pubGet(chave);

					} else {
						System.out.println("ERROR, Formato de entrada invalido.");
						System.out.println("Digite: get(key) ou put(key, value)");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
