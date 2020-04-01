package it.webSocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * @ServerEndpoint da un nome all'end point Questo può essere acceduto via
 *                 ws://localhost:8080/myfirstws/echo "localhost" è l'indirizzo
 *                 dell'host dove è deployato il server ws, "myfirstws" è il
 *                 nome del package ed "echo" è l'indirizzo specifico di questo
 *                 endpoint
 */
@ServerEndpoint(value = "/echo/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class EchoServer {

	private Session session;
	private static Set<EchoServer> chatEndpoints = Collections.synchronizedSet(new HashSet());
	// = new CopyOnWriteArraySet<EchoServer>();
	private static HashMap<String, String> users = new HashMap<String, String>();

	/**
	 * @throws EncodeException
	 * @throws IOException
	 * @OnOpen questo metodo ci permette di intercettare la creazione di una nuova
	 *         sessione. La classe session permette di inviare messaggi ai client
	 *         connessi. Nel metodo onOpen, faremo sapere all'utente che le
	 *         operazioni di handskake sono state completate con successo ed è
	 *         quindi possibile iniziare le comunicazioni.
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {
		System.out.println(session.getId() + " ha aperto una connessione");

		this.session = session;
		chatEndpoints.add(this);
		users.put(session.getId(), username);

		Message message = new Message();
		message.setFrom(username);
		message.setContent("Connected!");
		broadcast(message);
	}

	/**
	 * Quando un client invia un messaggio al server questo metodo intercetterà tale
	 * messaggio e compierà le azioni di conseguenza. In questo caso l'azione è
	 * rimandare una eco del messaggi indietro.
	 * 
	 * @throws EncodeException
	 */
	@OnMessage
	public void onMessage(Message message, Session session) throws EncodeException {
		System.out.println("Ricevuto messaggio da: " + session.getId() + ": " + message);

		message.setFrom(users.get(session.getId()));
		broadcast(message);

	}

	/**
	 * Metodo che intercetta la chiusura di una connessine da parte di un client
	 * 
	 * Nota: non si possono inviare messaggi al client da questo metodo
	 * 
	 * @throws EncodeException
	 * @throws IOException
	 */
	@OnClose
	public void onClose(Session session) {
		chatEndpoints.remove(this);
		Message message = new Message();
		message.setFrom(users.get(session.getId()));
		message.setContent("Disconnected!");
		broadcast(message);
	}

	private void broadcast(Message message) {

		for (EchoServer ec : chatEndpoints) {
			try {
				ec.session.getBasicRemote().sendObject(message);
			} catch (IOException | EncodeException ex) {
				ex.printStackTrace();
			}
		}

	}

}