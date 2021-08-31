package br.com.dht;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.dht.aplication.BrokerMQTT;
import br.com.dht.aplication.Client;
import br.com.dht.aplication.Node;

@SpringBootApplication
public class Main {

	public static void main(String[] args) throws Exception {
		// Cria Broker
		BrokerMQTT broker = new BrokerMQTT();
		broker.start();

		System.out.println("Iniciando processo de inicialização");
		for (int i = 1; i <= 8; i++) {
			Node node = new Node();
			node.start();
		}

		Client client = new Client();
		client.start();

	}

}
