package com.avic.chs.cis.cws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.chs.cws.SOAPRequest;

public class CHSRequest extends SOAPRequest{
	
	private Document payLoad;
	
	public CHSRequest() throws SOAPException {
		super();
	}

	public void build(boolean authentication,String attachmentPath) throws SOAPException, IOException{
		this.getRequest().getSOAPBody().addDocument(this.payLoad);

	    if (authentication) {
	      SOAPEnvelope envelope = this.getRequest().getSOAPPart().getEnvelope();
	      SOAPHeader header = envelope.getHeader();
	      if (header == null) {
	        header = envelope.addHeader();
	      }

	      Name username = envelope.createName("chsusername", "chs", "chs");

	      SOAPHeaderElement usernameHeaderElement = header.addHeaderElement(username);
	      usernameHeaderElement.addTextNode(this.getUsername());

	      Name password = envelope.createName("chspassword", "chs", "chs");

	      SOAPHeaderElement passwordHeaderElement = header.addHeaderElement(password);
	      passwordHeaderElement.addTextNode(this.getPassword());
	    
	      FileInputStream attachmentStream = new FileInputStream(new File(attachmentPath));
		  this.getRequest().addAttachmentPart(createGZIPAttachment(this,"design",attachmentStream));
		  attachmentStream.close();
	    }
	   
	}

	@Override
	public void setPayload(String payload)
    throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException{
	    
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
	    Document soapPayload = docBuilder.parse(new ByteArrayInputStream(payload.getBytes("UTF-8")));
	    this.setSoapPayload(soapPayload);
	    this.payLoad = soapPayload;
  }


	public AttachmentPart createGZIPAttachment(SOAPRequest request, String messageID, InputStream data) throws IOException{
		ByteArrayOutputStream compressedByteStream = new ByteArrayOutputStream();
        GZIPOutputStream compressedStream = new GZIPOutputStream(compressedByteStream);
        PrintWriter printWriterc = new PrintWriter(new OutputStreamWriter(compressedStream, "UTF8"));

        // read the stream and compress it, we must read it as UTF-8. dts0100807283
        InputStreamReader streamReader = new InputStreamReader(data, "UTF8");
        while (streamReader.ready())
        {
            char[] buffer = new char[1000];
            int numberOfReadBytes = streamReader.read(buffer, 0, 1000);
			if (numberOfReadBytes > 0) {
				printWriterc.write(buffer, 0, numberOfReadBytes);
			}
		}

        printWriterc.flush();
        compressedStream.finish();
        compressedStream.flush();
        final ByteArrayInputStream result = new ByteArrayInputStream(compressedByteStream.toByteArray());

        DataHandler dh = new DataHandler(new DataSource() {
        	
			@Override
			public OutputStream getOutputStream() throws IOException {
				throw new IOException("Cannot write to this data source");
			}
			
			@Override
			public String getName() {
				return "Inputstream data source";
			}
			
			@Override
			public InputStream getInputStream() throws IOException {
				return result;
			}
			
			@Override
			public String getContentType() {
				return "application/gzip";
			}
		});
        
        AttachmentPart attachment = request.getRequest().createAttachmentPart(dh);
        attachment.setContentId(messageID);

        return attachment;
    }
	
}
