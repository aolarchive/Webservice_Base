package com.aol.webservice_base.renderer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.renderer.support.SerializerData;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.view.serializer.SerializerException;
import com.aol.webservice_base.view.serializer.XMLSerializer;

public class XMLTest {
	protected XMLSerializer serializer;	
	
	@Before
	public void setupRenderer() {
		serializer = new XMLSerializer();
	}

	@Test
	public void testSimple() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.SimpleClass());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><i>1</i></response>";
		
		
		int converted = resultsCheck.getBytes("UTF-8").length;
		int igot = byteReturn.length;
		
		Assert.assertTrue(xml.equals(resultsCheck));
	}

	@Test
	public void testSimpleNull() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.NullClass());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response></response>";
		Assert.assertTrue(xml.equals(xml));
	}		
	
	@Test
	public void testNested() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.NestedClass());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><hello>hello</hello><one><i>1</i></one><two><i>1</i></two><zero><i>1</i></zero></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}

	@Test
	public void testArray() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ArrayClass(false));
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><items><item><i>1</i></item><item><i>2</i></item></items></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}		
	
	@Test
	public void testArrayNull() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ArrayClass(true));
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><items><item><i>1</i></item><item></item></items></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}			
	
	@Test
	public void testIntArray() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.IntArrayClass());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><items><item>1</item><item>2</item></items></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}			
	
	@Test
	public void testList() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_S());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><items><item><i>1</i></item><item><i>2</i></item></items></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}	

	@Test
	public void testListies() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_IES());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><bunnies><bunny><i>1</i></bunny><bunny><i>2</i></bunny></bunnies></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}		
	
	@Test
	public void testListes() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_ES());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><messages><message><i>1</i></message><message><i>2</i></message></messages></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}		

	@Test
	public void testListses() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_SES());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><classes><class><i>1</i></class><class><i>2</i></class></classes></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}		

	@Test
	public void testQuoted() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.QuotesTest());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><quoted>This string has quotes \" and '</quoted></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}		

	@Test
	public void testLessThan() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.LessThanTest());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><lessThan>This string has &#60;</lessThan></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}		

	@Test
	public void testGreaterThan() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.GreaterThanTest());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><greaterThan>This string has &#62;</greaterThan></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}			
	
	@Test
	public void testCDATA() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.CDATATest());
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><cdata>This string has &#60;![CDATA[</cdata></response>";
		Assert.assertTrue(xml.equals(resultsCheck));
	}		
	
	@Test 
	public void testRequestState() throws SerializerException, IOException {
		RequestState requestState = new RequestState(null);
		byte byteReturn[] = serializer.process(requestState);
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response><data></data><statusCode>200</statusCode><statusText>Ok</statusText></response>";
		Assert.assertEquals(resultsCheck, xml);
	}

	@Test 
	public void testRequestStateNamespace() throws SerializerException, IOException {
		RequestState requestState = new RequestState(null);
		requestState.setXmlNamespace("http://nothing.com/namespace.xml");
		byte byteReturn[] = serializer.process(requestState);
		String xml = new String(byteReturn, "utf-8");
		String resultsCheck = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
		resultsCheck += "<response xmlns=\"http://nothing.com/namespace.xml\"><data></data><statusCode>200</statusCode><statusText>Ok</statusText></response>";
		Assert.assertEquals(resultsCheck, xml);
	}
}
