package com.example.jpa_h2;

import com.example.jpa_h2.model.FileParser;
import com.example.jpa_h2.model.MessageParser;
import com.example.jpa_h2.model.WebsocketClientEndpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class JpaH2Application {

	public static void main(String[] args) {
		FileParser.initialize();

		try {
			// open websocket
			final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("wss://ws.finnhub.io?token=c3jfck2ad3i82raod360"));

			// add listener
			clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
				public void handleMessage(String message) {

					MessageParser.getPrices(message);

				}
			});

			// send message to websocket
			//clientEndPoint.sendMessage("{\"type\":\"subscribe\",\"symbol\":\"AAPL\"}");
			/*clientEndPoint.sendMessage("{\"type\":\"subscribe\",\"symbol\":\"AMZN\"}");
			clientEndPoint.sendMessage("{\"type\":\"subscribe\",\"symbol\":\"BINANCE:BTCUSDT\"}");
			clientEndPoint.sendMessage("{\"type\":\"subscribe\",\"symbol\":\"IC MARKETS:1\"}");*/

			// wait 5 seconds for messages from websocket
			Thread.sleep(5000);

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		} catch (URISyntaxException ex) {
			System.err.println("URISyntaxException exception: " + ex.getMessage());
		}

		SpringApplication.run(JpaH2Application.class, args);

	}

}
