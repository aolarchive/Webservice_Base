package com.aol.webservice_base.renderer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.renderer.support.SerializerData;
import com.aol.webservice_base.view.serializer.JSONSerializer;
import com.aol.webservice_base.view.serializer.SerializerException;

public class JSONTest {
	protected JSONSerializer serializer;	
		
	@Before
	public void setupRenderer() {
		serializer = new JSONSerializer();
	}

	@Test
	public void testSimple() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.SimpleClass());
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{\"i\":1}}"));
	}	
	
	@Test
	public void testSimpleNull() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.NullClass());
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{}}"));
	}		
	
	@Test
	public void testNested() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.NestedClass());
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{\"hello\":\"hello\",\"one\":{\"i\":1},\"two\":{\"i\":1},\"zero\":{\"i\":1}}}"));
	}

	@Test
	public void testArray() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ArrayClass(false));
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{\"items\":[{\"i\":1},{\"i\":2}]}}"));
	}		
	
	@Test
	public void testArrayNull() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ArrayClass(true));
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{\"items\":[{\"i\":1},{}]}}"));
	}			
	
	@Test
	public void testIntArray() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.IntArrayClass());
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{\"items\":[1,2]}}"));
	}				
	
	@Test
	public void testList() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_S());
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{\"items\":[{\"i\":1},{\"i\":2}]}}"));
	}	

	@Test
	public void testListies() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_IES());
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{\"bunnies\":[{\"i\":1},{\"i\":2}]}}"));
	}		
	
	@Test
	public void testListes() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_ES());
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{\"messages\":[{\"i\":1},{\"i\":2}]}}"));
	}		

	@Test
	public void testListses() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_SES());
		String json = new String(byteReturn, "utf-8");
		Assert.assertTrue(json.equals("{\"response\":{\"classes\":[{\"i\":1},{\"i\":2}]}}"));
	}		

	@Test
	public void testQuoted() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.QuotesTest());
		String json = new String(byteReturn, "utf-8");
		String resultsCheck = "{\"response\":{\"quoted\":\"This string has quotes \\\" and '\"}}";
		Assert.assertTrue(json.equals(resultsCheck));
	}		

	@Test
	public void testLessThan() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.LessThanTest());
		String json = new String(byteReturn, "utf-8");
		String resultsCheck = "{\"response\":{\"lessThan\":\"This string has <\"}}";
		Assert.assertTrue(json.equals(resultsCheck));
	}		

	@Test
	public void testGreaterThan() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.GreaterThanTest());
		String json = new String(byteReturn, "utf-8");
		String resultsCheck = "{\"response\":{\"greaterThan\":\"This string has >\"}}";
		Assert.assertTrue(json.equals(resultsCheck));
	}			
	
	@Test 
	public void testCDATA() throws IOException, UnsupportedEncodingException, SerializerException {		
		byte byteReturn[] = serializer.process(new SerializerData.CDATATest());
		String json = new String(byteReturn, "utf-8");
		String resultsCheck = "{\"response\":{\"cdata\":\"This string has <![CDATA[\"}}";
		Assert.assertTrue(json.equals(resultsCheck));
	}	
}
