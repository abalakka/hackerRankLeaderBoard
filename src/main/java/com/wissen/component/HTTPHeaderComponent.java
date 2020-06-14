package com.wissen.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class HTTPHeaderComponent
{
	@Value("${cookie.jigar}")
	String jigarCookie;

	@Value("${cookie.anirudh}")
	String anirudhCookie;

	public HttpHeaders getHttpHeaders(boolean trackingForGrads)
	{
		HttpHeaders headers = new HttpHeaders();

		if (trackingForGrads)
			headers.set("Cookie", "{" + jigarCookie + "}");
		else
			headers.set("Cookie", "{" + anirudhCookie + "}");

		headers.add("user-agent", "PostmanRuntime/7.23.0");
		return headers;
	}

}
