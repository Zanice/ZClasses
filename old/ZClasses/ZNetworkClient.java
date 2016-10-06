package ZClasses;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ZNetworkClient extends Thread {
	private final String ID_ASSIGN_SERIAL = "xjfq0nn2fkl9:";
	
	private ZNetworkClientListener controller;
	private String id;
	private String address;
	private int port;
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public ZNetworkClient(String address, int port, ZNetworkClientListener controller) {
		this.address = address;
		this.port = port;
		if (controller == null) 
			this.controller = new DefaultClientController();
		else
			this.controller = controller;
	}
	
	public String getID() { return id; }
	
	public void run() {
		if (connect())
			interact();
	}
	
	private boolean connect() {
		try {
			connection = new Socket(address, port);
		} catch (IOException e) {
			controller.onConnectionFailure(this, e);
			return false;
		}
		controller.onConnectionSuccess(this);
		if ((!setupOutput())||(!setupInput())) {
			controller.onStreamSetupFailure(this);
			return false;
		}
		controller.onStreamSetupSuccess(this);
		return true;
	}
	
	private void interact() {
		Object packet = null;
		while (true) {
			try {
				packet = input.readObject();
			} catch (Exception e) {
				controller.onConnectionInputStreamInterruption(this, e);
				return;
			}
			try {
				if ((packet instanceof String)&&((String) packet).substring(0, 13).equals(ID_ASSIGN_SERIAL)) {
					id = ((String) packet).substring(13);
					controller.onConnectionIDAssigned(this, id);
				}
				else
					controller.onConnectionInputReceived(this, packet);
			} catch (Exception e) {
				controller.onConnectionInputReceived(this, packet);
			}
		}
	}
	
	private boolean setupInput() {
		try {
			input = new ObjectInputStream(connection.getInputStream());
		} catch (Exception e) {
			controller.onConnectionInputStreamSetupFailure(this, e);
			return false;
		}
		controller.onConnectionInputStreamSetupSuccess(this);
		return true;
	}
	
	private boolean setupOutput() {
		try {
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
		} catch (Exception e) {
			controller.onConnectionOutputStreamSetupFailure(this, e);
			return false;
		}
		controller.onConnectionOutputStreamSetupSuccess(this);
		return true;
	}
	
	public void sendPacket(Object packet) {
		try {
			output.writeObject(packet);
			output.flush();
		} catch (IOException e) {
			controller.onConnectionOutputSendFailure(this, packet, e);
		}
	}
	
	public void disconnect() {
		try {
			connection.close();
		} catch (IOException ioe) {
			connection = null;
		}
		interrupt();
	}
	
	private class DefaultClientController extends ZNetworkClientListener {
		public void onConnectionSuccess(ZNetworkClient client) {}
		public void onConnectionFailure(ZNetworkClient client, IOException e) {}
		public void onStreamSetupSuccess(ZNetworkClient client) {}
		public void onStreamSetupFailure(ZNetworkClient client) {}
		public void onConnectionInputStreamSetupSuccess(ZNetworkClient input) {}
		public void onConnectionInputStreamSetupFailure(ZNetworkClient input, Exception e) {}
		public void onConnectionInputStreamInterruption(ZNetworkClient input, Exception e) {}
		public void onConnectionInputReceived(ZNetworkClient input, Object packet) {}
		public void onConnectionOutputStreamSetupSuccess(ZNetworkClient output) {}
		public void onConnectionOutputStreamSetupFailure(ZNetworkClient output, Exception e) {}
		public void onConnectionOutputSendFailure(ZNetworkClient output, Object packet, IOException e) {}
		public void onConnectionIDAssigned(ZNetworkClient client, String id) {}
	}
}
