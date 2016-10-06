package ZClasses;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ZHttp {
	private static final String _charset = java.nio.charset.StandardCharsets.UTF_8.name();
	
	private String url;
	private HttpParameter[] params;
	
	public ZHttp(String url) throws IllegalArgumentException {
		this.url = url;
	}
	
	public String HttpRequest(String urltag, String[][] parameters) throws MalformedURLException, IOException {
		params = null;
		String parameterstring = "";
		String requesturl;
		if (parameters != null) {
			params = new HttpParameter[parameters.length];
			try {
				for (int i = 0; i < params.length; i++) {
					params[i] = new HttpParameter(parameters[i][0], parameters[i][1]);
					if (parameters[i].length > 2)
						throw new IllegalArgumentException();
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				throw new IllegalArgumentException();
			}
			
			for (int i = 0; i < params.length; i++) {
				parameterstring += params[i].toURLParameter();
				if (i < params.length - 1)
					parameterstring += "&";
			}
			
			if (parameterstring.length() != 0)
				parameterstring = "?" + parameterstring;
		}
		
		requesturl = url + urltag + parameterstring;
		URLConnection connection = new URL(requesturl).openConnection();
		connection.setRequestProperty("Accept-Charset", _charset);
		
		BufferedReader reader;
		try {
			reader =  new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		String line;
		String data = "";
		while ((line = reader.readLine()) != null)
			data += line;
		
		return data;
	}
	
	private class HttpParameter {
		String name;
		String value;
		
		public HttpParameter(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		public String toURLParameter() throws UnsupportedEncodingException {
			return String.format("%s=%s", name, URLEncoder.encode(value, _charset));
		}
	}
}
