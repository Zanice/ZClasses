package ZClasses;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ZNetworkServerConnection {
	private final String ID_ASSIGN_SERIAL = "xjfq0nn2fkl9:";
	
	private ZNetworkServerListener controller;
	private ZNetworkServer owner;
	private String id;
	private String ip;
	private Socket socket;
	private boolean connected;
	private Input input;
	private Output output;
	
	public ZNetworkServerConnection(String id, Socket socket, ZNetworkServerListener controller, ZNetworkServer owner) {
		this.id = id;
		this.ip = socket.getInetAddress().toString();
		this.socket = socket;
		this.controller = controller;
		this.owner = owner;
		connected = true;
		input = new Input(this);
		input.start();
		output = new Output(this);
		output.start();
		this.owner.log("( " + id + " ) Streams set up correctly.");
	}
	
	public String getID() { return id; }
	public String getIP() { return ip; }
	
	public boolean isConnected() {
		return connected;
	}
	
	public void send(Object packet) {
		output.sendPacket(packet);
	}
	
	public void close() {
		try {
			connected = false;
			socket.close();
		} catch (IOException e) {
			owner.log("( " + id + " ) << ERROR: Connection did not close successfully. >>");
			controller.onConnectionCloseFailure(this, e);
		}
		owner.log("( " + id + " ) Connection closed successfully.");
		controller.onConnectionCloseSuccess(this);
	}
	
	public String toString() {
		return socket.toString();
	}
	
	private class Input extends Thread {
		private ZNetworkServerConnection owner;
		private ObjectInputStream input;
		
		private Input(ZNetworkServerConnection parent) {
			owner = parent;
		}
		
		public void run() {
			try {
				input = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				owner.owner.log("( " + id + " ) << ERROR: Connection input stream setup failed. >>");
				controller.onConnectionInputStreamSetupFailure(owner, e);
				return;
			}
			owner.owner.log("( " + id + " ) Connection input stream set up successfully.");
			controller.onConnectionInputStreamSetupSuccess(owner);
			while (true) {
				Object packet = null;
				try {
					packet = input.readObject();
					controller.onConnectionInputReceived(owner, packet);
				} catch (IOException e) {
					owner.owner.log("( " + id + " ) Connection disconnected.");
					controller.onConnectionInputStreamInterruption(owner, e);
					return;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class Output extends Thread {
		private ZNetworkServerConnection owner;
		private ObjectOutputStream output;
		
		private Output(ZNetworkServerConnection parent) {
			owner = parent;
		}
		
		public void run() {
			try {
				output = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				owner.owner.log("( " + id + " ) << ERROR: Connection output stream setup failed. >>");
				controller.onConnectionOutputStreamSetupFailure(owner, e);
				return;
			}
			owner.owner.log("( " + id + " ) Connection output stream set up successfully.");
			controller.onConnectionOutputStreamSetupSuccess(owner);
			sendPacket(ID_ASSIGN_SERIAL + id);
			controller.onSetupInfoTransfer(owner, id);
			owner.owner.log("( " + id + " ) Connection data initialization successful.");
		}
		
		
		public void sendPacket(Object packet) {
			try {
				output.writeObject(packet);
				output.flush();
			} catch (IOException e) {
				owner.owner.log("( " + id + " ) << ERROR: Connection failed to send packet. >>");
				controller.onConnectionOutputSendFailure(owner, packet, e);
			}
		}
	}
}
