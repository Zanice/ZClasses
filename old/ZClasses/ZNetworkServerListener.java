package ZClasses;

import java.io.IOException;
import java.net.UnknownHostException;

public abstract class ZNetworkServerListener {
	public ZNetworkServerListener() {}
	
	public abstract void onHostAddressQuerySuccess(ZNetworkServer server);
	public abstract void onHostAddressQueryFailure(ZNetworkServer server, UnknownHostException e);
	public abstract void onServerSocketSuccess(ZNetworkServer server);
	public abstract void onServerSocketFailure(ZNetworkServer server, IOException e);
	public abstract void onServerStart(ZNetworkServer server);
	public abstract void onServerInterruption(ZNetworkServer server, InterruptedException e);
	public abstract void onNewConnection(ZNetworkServer server, ZNetworkServerConnection connection);
	public abstract void onConnectionTermination(ZNetworkServer server);
	public abstract void onConnectionAcceptanceFailure(ZNetworkServer server, IOException e);
	public abstract void onConnectionCloseSuccess(ZNetworkServerConnection connection);
	public abstract void onConnectionCloseFailure(ZNetworkServerConnection connection, IOException e);
	public abstract void onConnectionInputStreamSetupSuccess(ZNetworkServerConnection input);
	public abstract void onConnectionInputStreamSetupFailure(ZNetworkServerConnection input, IOException e);
	public abstract void onConnectionInputStreamInterruption(ZNetworkServerConnection input, Exception e);
	public abstract void onConnectionInputReceived(ZNetworkServerConnection input, Object packet);
	public abstract void onConnectionOutputStreamSetupSuccess(ZNetworkServerConnection output);
	public abstract void onConnectionOutputStreamSetupFailure(ZNetworkServerConnection output, IOException e);
	public abstract void onConnectionOutputSendFailure(ZNetworkServerConnection output, Object packet, IOException e);
	public abstract void onSetupInfoTransfer(ZNetworkServerConnection output, String id);
	public abstract void onServerLog(ZNetworkServer server, String text);
}
