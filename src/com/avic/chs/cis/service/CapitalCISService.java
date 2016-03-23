package com.avic.chs.cis.service;

import org.dom4j.Document;
import org.dom4j.io.DOMReader;

import com.chs.cws.SOAPResponse;

public class CapitalCISService extends AbstractImportExportService{
	
	public Document getCHSSecurityData() throws Exception{
		String payload = "<SecurityMgr/>";
		
		SOAPResponse response = sent(payload, server.getSecurityDataExporterService());
		doResponse(response);
		DOMReader reader = new DOMReader();
		Document doc = reader.read(response.getSoapResponse());
		return doc;
	}

	private void doResponse(SOAPResponse response) throws Exception{
		if(response == null){
			throw new Exception("CIS Error：Response is blank or NULL");
		}else if(response.hasFault()) {
			throw new Exception("CIS Error："+response.getFaultMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		CapitalCISService service = new CapitalCISService();
		service.getCHSSecurityData();
	}
	
}
