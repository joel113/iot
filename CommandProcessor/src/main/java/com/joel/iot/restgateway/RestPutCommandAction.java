package com.joel.iot.restgateway;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.joel.iot.commands.RestPutCommand;

import rx.functions.Action1;

public class RestPutCommandAction<T> implements Action1<RestPutCommand> {

	@Override
	public void call(RestPutCommand restPutCommand) {
		try {
			submitPut(restPutCommand.getUrl(), restPutCommand.getBody());
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

    private String submitPut(String url, String content) throws Exception {
  		CloseableHttpResponse response = null;
  		String responseString = null;
  		CloseableHttpClient client = HttpClients.createDefault();
  		HttpPut put = new HttpPut(url);
  		StringEntity body =new StringEntity(content,"UTF-8");
  		put.setEntity(body);
  		response = client.execute(put);
  		HttpEntity entity = response.getEntity();
  		responseString = EntityUtils.toString(entity);
  		response.close();
  		return responseString;
  	}

}
