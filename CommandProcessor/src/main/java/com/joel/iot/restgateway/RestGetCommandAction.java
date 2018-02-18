package com.joel.iot.restgateway;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.joel.iot.commands.RestGetCommand;

import rx.functions.Action1;

public class RestGetCommandAction<T> implements Action1<RestGetCommand>  {

	@Override
	public void call(RestGetCommand restGetCommand) {	
		try {
			String sessionId = getSessionId();
			submitGet(String.format(restGetCommand.getUrl(), sessionId));
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private String submitGet(String url) throws Exception {
		CloseableHttpResponse response = null;
		String responseString = null;
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		response = client.execute(get);
		HttpEntity entity = response.getEntity();
		responseString = EntityUtils.toString(entity);
		response.close();
		return responseString;
	}

	public String getSessionId() throws Exception {
		String url = "http://fritz.box/login_sid.lua";
		String response = submitGet(url);
		Document document = getDocumentFromResponse(response);
		String sid = getSidFromDocument(document);
		if (sid.equals("0000000000000000")) {
			String challenge = getChallengeFromDocument(document);
			String urlWithDigest = "http://fritz.box/login_sid.lua?username=%s&response=%s";
			String digest = challenge + "-" + getMd5(challenge + "-" + "tz7188uo");
			String responseWithDigest = submitGet(String.format(urlWithDigest, "joel", digest));
			Document documentWithDigest = getDocumentFromResponse(responseWithDigest);
			sid = getSidFromDocument(documentWithDigest);
		}
		return sid;
	}

	private String getMd5(String request) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] bytesOfMessage = request.getBytes("UTF-16LE");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] thedigest = md.digest(bytesOfMessage);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < thedigest.length; i++) {
			sb.append(Integer.toString((thedigest[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	private String getChallengeFromDocument(Document document) {
		NodeList list = document.getElementsByTagName("Challenge");
		Node challengeNode = list.item(0);
		String challenge = challengeNode.getTextContent();
		return challenge;
	}

	private String getSidFromDocument(Document document) {
		NodeList list = document.getElementsByTagName("SID");
		Node sidNode = list.item(0);
		String sid = sidNode.getTextContent();
		return sid;
	}

	private Document getDocumentFromResponse(String response)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(response)));
		return document;
	}

}
