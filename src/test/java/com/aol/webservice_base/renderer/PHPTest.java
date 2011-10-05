package com.aol.webservice_base.renderer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.renderer.support.SerializerData;
import com.aol.webservice_base.view.serializer.PHPSerializer;
import com.aol.webservice_base.view.serializer.SerializerException;

public class PHPTest {
	protected PHPSerializer serializer;	
	
	@Before
	public void setupRenderer() {
		serializer = new PHPSerializer();
	}

	@Test
	public void testSimple() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.SimpleClass());
		String php = new String(byteReturn, "utf-8");
		Assert.assertTrue(php.equals("O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}"));
	}	

	@Test
	public void testSimpleNull() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.NullClass());
		String php = new String(byteReturn, "utf-8");
		Assert.assertTrue(php.equals("O:9:\"NullClass\":1:{s:1:\"s\";N;}"));
	}	
	
	@Test
	public void testNested() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.NestedClass());
		String php = new String(byteReturn, "utf-8");
		Assert.assertTrue(php.equals("O:11:\"NestedClass\":4:{s:5:\"hello\";s:5:\"hello\";s:3:\"one\";O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}s:3:\"two\";O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}s:4:\"zero\";O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}}"));
	}

	@Test
	public void testArray() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.ArrayClass(false));
		String php = new String(byteReturn, "utf-8");
		Assert.assertTrue(php.equals("O:10:\"ArrayClass\":1:{s:5:\"items\";a:2:{i:0;O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}i:1;O:11:\"SimpleClass\":1:{s:1:\"i\";i:2;}}}"));
	}		

	@Test
	public void testArrayNull() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.ArrayClass(true));
		String php = new String(byteReturn, "utf-8");
		Assert.assertTrue(php.equals("O:10:\"ArrayClass\":1:{s:5:\"items\";a:2:{i:0;O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}i:1;N;}}"));
	}			
	
	@Test
	public void testIntArray() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.IntArrayClass());
		String php = new String(byteReturn, "utf-8");
		Assert.assertTrue(php.equals("O:13:\"IntArrayClass\":1:{s:5:\"items\";a:2:{i:0;i:1;i:1;i:2;}}"));
	}		

	@Test
	public void testList() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_S());
		String php = new String(byteReturn, "utf-8");
		Assert.assertTrue(php.equals("O:11:\"ListClass_S\":1:{s:5:\"items\";a:2:{i:0;O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}i:1;O:11:\"SimpleClass\":1:{s:1:\"i\";i:2;}}}"));
	}	

	@Test
	public void testListies() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_IES());
		String php = new String(byteReturn, "utf-8");
		Assert.assertTrue(php.equals("O:13:\"ListClass_IES\":1:{s:7:\"bunnies\";a:2:{i:0;O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}i:1;O:11:\"SimpleClass\":1:{s:1:\"i\";i:2;}}}"));
	}		
	
	@Test
	public void testListes() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_ES());
		String php = new String(byteReturn, "utf-8");System.out.println(php);
		Assert.assertTrue(php.equals("O:12:\"ListClass_ES\":1:{s:8:\"messages\";a:2:{i:0;O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}i:1;O:11:\"SimpleClass\":1:{s:1:\"i\";i:2;}}}"));
	}		

	@Test
	public void testListses() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.ListClass_SES());
		String php = new String(byteReturn, "utf-8");
		Assert.assertTrue(php.equals("O:13:\"ListClass_SES\":1:{s:7:\"classes\";a:2:{i:0;O:11:\"SimpleClass\":1:{s:1:\"i\";i:1;}i:1;O:11:\"SimpleClass\":1:{s:1:\"i\";i:2;}}}"));
	}		

	@Test
	public void testQuoted() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.QuotesTest());
		String php = new String(byteReturn, "utf-8");
		String resultsCheck = "O:10:\"QuotesTest\":1:{s:6:\"quoted\";s:30:\"This string has quotes \" and '\";}";
		Assert.assertTrue(php.equals(resultsCheck));
	}		

	@Test
	public void testLessThan() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.LessThanTest());
		String php = new String(byteReturn, "utf-8");
		String resultsCheck = "O:12:\"LessThanTest\":1:{s:8:\"lessThan\";s:17:\"This string has <\";}";;
		Assert.assertTrue(php.equals(resultsCheck));
	}		

	@Test
	public void testGreaterThan() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.GreaterThanTest());
		String php = new String(byteReturn, "utf-8");
		String resultsCheck = "O:15:\"GreaterThanTest\":1:{s:11:\"greaterThan\";s:17:\"This string has >\";}";
		Assert.assertTrue(php.equals(resultsCheck));
	}			
	
	@Test 
	public void testCDATA() throws SerializerException, IOException {		
		byte byteReturn[] = serializer.process(new SerializerData.CDATATest());
		String php = new String(byteReturn, "utf-8");
		String resultsCheck = "O:9:\"CDATATest\":1:{s:5:\"cdata\";s:25:\"This string has <![CDATA[\";}";
		Assert.assertTrue(php.equals(resultsCheck));
	}	

}
