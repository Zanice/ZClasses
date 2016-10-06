package ZClasses;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ZNetworkServer extends Thread {
	private ZNetworkServerListener controller;
	
	private String name;
	private int port;
	private int throttle;
	private ServerLogger logger;
	private ServerSocket serversocket;
	private InetAddress host;
	private Socket socket;
	private ArrayList<ZNetworkServerConnection> connections;
	private ZIdGenerator idGenerator;
	
	public ZNetworkServer(String name, int port, int throttle, ZNetworkServerListener controller, boolean logserver) {
		this.name = name;
		this.port = port;
		this.throttle = throttle;
		if (controller == null)
			this.controller = new DefaultServerController();
		else
			this.controller = controller;
		connections = new ArrayList<ZNetworkServerConnection>();
		idGenerator = new ZIdGenerator();
		
		//File Log Setup
		if (logserver) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			logger = new ServerLogger("SERVERLOG-'" + this.name + "'-" + dateFormat.format(new Date()) + ".txt");
		}
		
		//Server Setup
		try {
			host = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			log("<< ERROR: Could not get host address. >>");
			this.controller.onHostAddressQueryFailure(this, e);
			return;
		}
		log("Server host address is: " + host.toString());
		this.controller.onHostAddressQuerySuccess(this);
		try {
			serversocket = new ServerSocket(port, 0, host);
		} catch (IOException e) {
			log("<< ERROR: Could not open the server socket. >>");
			this.controller.onServerSocketFailure(this, e);
			return;
		}
		log("Socket opened successfully");
		this.controller.onServerSocketSuccess(this);
	}
	
	public int getPort() { return port; }
	public int getThrottle() { return throttle; }
	public String getHost() { return host.toString(); }
	public ArrayList<ZNetworkServerConnection> getConnections() { return connections; }
	
	public void run() {
		log("Server started.");
		controller.onServerStart(this);
		while (true) {
			for (int i = 0; i < connections.size(); i++) {
				if (!connections.get(i).isConnected()) {
					ZNetworkServerConnection connection = connections.remove(i);
					log("Connection " + connection.getID() + " was terminated.");
					controller.onConnectionTermination(this);
				}
			}
			try {
				socket = serversocket.accept();
			} catch (IOException e) {
				log("<< ERROR: Failed to accept a new client. >>");
				controller.onConnectionAcceptanceFailure(this, e);
				return;
			}
			ZNetworkServerConnection newconnection = new ZNetworkServerConnection(idGenerator.newId(), socket, controller, this);
			connections.add(newconnection);
			log("Added new connection: " + newconnection.getID());
			controller.onNewConnection(this, newconnection);
			try {
				Thread.sleep(throttle);
			} catch (InterruptedException e) {
				log("<< ERROR: Server was interrupted. >>");
				controller.onServerInterruption(this, e);
			}
		}
	}
	
	public void log(String text) {
		if (logger != null)
			logger.logToFile(text);
		controller.onServerLog(this, text);
	}
	
	public ZNetworkServerConnection findByID(String id) {
		for (ZNetworkServerConnection connection : connections) {
			if (connection.getID().equals(id))
				return connection;
		}
		return null;
	}
	
	public void rpc(Object packet) {
		for (ZNetworkServerConnection connection : connections)
			connection.send(packet);
	}
	
	private class ServerLogger extends ZFileWriter {
		private String file;
		private DateFormat timeFormat = new SimpleDateFormat("[HH:mm:ss] ");
		
		public ServerLogger(String file) {
			this.file = file;
		}
		
		public void logToFile(String text) {
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
				out.println(timeFormat.format(new Date()) + text);
			} catch (IOException e) {
				new File(file);
				e.printStackTrace();
				//logToFile(text);
			}
		}
	}
	
	private class DefaultServerController extends ZNetworkServerListener {
		public void onHostAddressQuerySuccess(ZNetworkServer server) {}
		public void onHostAddressQueryFailure(ZNetworkServer server, UnknownHostException e) {}
		public void onServerSocketSuccess(ZNetworkServer server) {}
		public void onServerSocketFailure(ZNetworkServer server, IOException e) {}
		public void onServerStart(ZNetworkServer server) {}
		public void onServerInterruption(ZNetworkServer server, InterruptedException e) {}
		public void onNewConnection(ZNetworkServer server, ZNetworkServerConnection connection) {}
		public void onConnectionTermination(ZNetworkServer server) {}
		public void onConnectionAcceptanceFailure(ZNetworkServer server, IOException e) {}
		public void onConnectionCloseSuccess(ZNetworkServerConnection connection) {}
		public void onConnectionCloseFailure(ZNetworkServerConnection connection, IOException e) {}
		public void onConnectionInputStreamSetupSuccess(ZNetworkServerConnection input) {}
		public void onConnectionInputStreamSetupFailure(ZNetworkServerConnection input, IOException e) {}
		public void onConnectionInputStreamInterruption(ZNetworkServerConnection input, Exception e) {}
		public void onConnectionInputReceived(ZNetworkServerConnection input, Object packet) {}
		public void onConnectionOutputStreamSetupSuccess(ZNetworkServerConnection output) {}
		public void onConnectionOutputStreamSetupFailure(ZNetworkServerConnection output, IOException e) {}
		public void onConnectionOutputSendFailure(ZNetworkServerConnection output, Object packet, IOException e) {}
		public void onSetupInfoTransfer(ZNetworkServerConnection output, String id) {}
		public void onServerLog(ZNetworkServer server, String text) {}
	}
}
