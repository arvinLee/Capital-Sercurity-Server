package com.avic.chs.cis.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.dom4j.Element;
import org.xml.sax.SAXException;

import com.avic.chs.cis.cws.CHSRequest;
import com.chs.cws.CisServer;
import com.chs.cws.SOAPClient;
import com.chs.cws.SOAPRequest;
import com.chs.cws.SOAPResponse;

/**
 *
 */
public abstract class AbstractImportExportService {

	protected CisServer server;	
	private String username;
	private String password;
	private String host;
	private String port;
	
	public AbstractImportExportService() {
		this.server = new CisServer();
		Properties p = new Properties();
		try {
			p.load(getClass().getResourceAsStream("/cis.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.username = p.getProperty("user");
		this.password = p.getProperty("passwd");
		this.host = p.getProperty("host", "127.0.0.1");
		this.server.setHost(this.host);
		this.port = p.getProperty("port", "49901");
		this.server.setPort(Integer.valueOf(this.port));
	
	}
	
	protected SOAPResponse sent(Element payload, URL serviceUrl) throws SOAPException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		SOAPRequest request = new SOAPRequest();
		request.setUsername(username);
		request.setPassword(password);
		request.setPayload(payload.asXML());
		request.build(true);
		SOAPClient client = new SOAPClient();
		return client.callService(request, serviceUrl);
	}
	
	protected SOAPResponse sent(String payload, URL serviceUrl) throws SOAPException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException{
		SOAPRequest request = new SOAPRequest();
		request.setUsername(username);
		request.setPassword(password);
		request.setPayload(payload);
		request.build(true);
		SOAPClient client = new SOAPClient();
		return client.callService(request, serviceUrl);
	}
	
	protected SOAPResponse sent(String payload,String attachmentPath,URL serviceUrl) throws SOAPException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException{
		CHSRequest request = new CHSRequest();
		request.setUsername(username);
		request.setPassword(password);
		request.setPayload(payload);
		request.build(true,attachmentPath);
		SOAPClient client = new SOAPClient();
		return client.callService(request, serviceUrl);
	}
}
