package com.gruppometa.sbnmarc;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Base64Utils;

public class BasicAuthorizationInterceptor implements ClientHttpRequestInterceptor {
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private final String username;

	private final String password;

	public BasicAuthorizationInterceptor(String username, String password) {
		this.username = username;
		this.password = (password == null ? "" : password);
	}

	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		String token = Base64Utils.encodeToString((this.username + ":" + this.password).getBytes(UTF_8));
		request.getHeaders().add("Authorization", "Basic " + token);
		return execution.execute(request, body);
	}

}
