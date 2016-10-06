package ZClasses;

import java.io.IOException;

public abstract class ZNetworkClientListener {
	public ZNetworkClientListener() {}
	
	public abstract void onConnectionSuccess(ZNetworkClient client);
	public abstract void onConnectionFailure(ZNetworkClient client, IOException e);
	public abstract void onStreamSetupSuccess(ZNetworkClient client);
	public abstract void onStreamSetupFailure(ZNetworkClient client);
	public abstract void onConnectionInputStreamSetupSuccess(ZNetworkClient input);
	public abstract void onConnectionInputStreamSetupFailure(ZNetworkClient input, Exception e);
	public abstract void onConnectionInputStreamInterruption(ZNetworkClient input, Exception e);
	public abstract void onConnectionInputReceived(ZNetworkClient input, Object packet);
	public abstract void onConnectionOutputStreamSetupSuccess(ZNetworkClient output);
	public abstract void onConnectionOutputStreamSetupFailure(ZNetworkClient output, Exception e);
	public abstract void onConnectionOutputSendFailure(ZNetworkClient output, Object packet, IOException e);
	public abstract void onConnectionIDAssigned(ZNetworkClient client, String id);
}
