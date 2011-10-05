package com.aol.webservice_base.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import com.aol.webservice_base.configuration.support.Config;
import com.aol.webservice_base.configuration.support.ConfigData;
import com.aol.webservice_base.configuration.support.ConfigServlet;
import com.aol.webservice_base.configuration.support.StaticData;

public class ConfigurationTest {
	Config config = null;
	protected static final String NO_ENV1_CONFIG_PREFIX = "<config>";
	protected static final String NO_ENV2_CONFIG_PREFIX = "<config><env></env>";
	protected static final String CONFIG_PREFIX = "<config><env>TEST</env>";
	protected static final String CONFIG_SUFFIX = "</config>";
	protected static final String VALID_CLASS = "com.aol.webservice_base.configuration.support.ConfigData";
	protected static final String STATIC_CLASS = "com.aol.webservice_base.configuration.support.StaticData";
	protected static final String ABBR_VALID_CLASS = "ConfigData";
	protected static final String NEED_CDATA_VALUE = "   [a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
	protected static final String ALIASES_ENV = "<alias env=\"TEST\" />";
	protected static final String ALIASES_NO_FROM = "<alias to=\"" + VALID_CLASS + "\" />";
	protected static final String ALIASES_NO_TO = "<alias from=\"" + ABBR_VALID_CLASS + "\" />";
	protected static final String ALIASES = "<alias from=\"" + ABBR_VALID_CLASS + "\" to=\"" + VALID_CLASS + "\" />";
	protected static final Integer FIVE = new Integer(5);

	@Before
	public void resetConfig() {
		ConfigData.resetInstance();
		Config.resetInstance();		
	}

	@Test (expected=NullPointerException.class)
	public void testNullConfig() throws Throwable {
		try {
			Config.setConfiguration(null);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test (expected=SAXParseException.class)
	public void testEmptyConfig() throws Throwable {
		try {
			Config.setConfiguration("");
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected=ConfigurationException.class)
	public void testInvalidConfig() throws Throwable {
		try {
			Config.setConfiguration("<nothing></nothing>");
			config = (Config)Config.getTestInstance();
		} catch (Throwable e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test 
	public void simpleConfig() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}
	
	@Test (expected = ConfigurationException.class) 
	public void noEnv1Config() throws Throwable {
		try {
			String configText = NO_ENV1_CONFIG_PREFIX;
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected = ConfigurationException.class)
	public void noEnv2Config() throws Throwable {
		try {
			String configText = NO_ENV2_CONFIG_PREFIX;
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected=ClassNotFoundException.class)
	public void config1InvalidClass() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"com.aol.webservice_base.configuration.NotConfigurationTest\" id=\"invalidClassTest\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();		
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected=ConfigurationException.class)
	public void configNoNameClass() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object></object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test (expected=ConfigurationException.class)
	public void configNoClassSpecified() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object id=\"noClass\"></object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test (expected=ConfigurationException.class)
	public void configNoNameVar() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"badMember\">";
			configText += "<var><value>55</value></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected=ConfigurationException.class)
	public void config1ObjectBadMember() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"badMember\">";
			configText += "<var name=\"notExist\"><value>55</value></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected=ConfigurationException.class)
	public void configVarIncorrectly() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"incorrectVars\">";
			configText += "<var name=\"numInt\"><value>one</value></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configAllVars() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"allVars\">";
			configText += "<var name=\"bool\"><value>true</value></var>";
			configText += "<var name=\"boolClass\"><value>true</value></var>";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "<var name=\"numLong\"><value>2</value></var>";
			configText += "<var name=\"numDouble\"><value>3.3</value></var>";
			configText += "<var name=\"numFloat\"><value>4.4</value></var>";
			configText += "<var name=\"numIntClass\"><value>5</value></var>";
			configText += "<var name=\"numLongClass\"><value>6</value></var>";
			configText += "<var name=\"numDoubleClass\"><value>7.7</value></var>";
			configText += "<var name=\"numFloatClass\"><value>8.8</value></var>";
			configText += "<var name=\"string\"><value>string</value></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("allVars");
			Assert.assertEquals(true, configData.bool);
			Assert.assertEquals(Boolean.TRUE, configData.boolClass);
			Assert.assertEquals(1, configData.numInt);
			Assert.assertEquals(2, configData.numLong);
			Assert.assertEquals(3.3D, configData.numDouble);
			Assert.assertEquals(4.4F, configData.numFloat);
			Assert.assertEquals(Integer.valueOf(5), configData.numIntClass);
			Assert.assertEquals(Long.valueOf(6), configData.numLongClass);
			Assert.assertEquals(7.7D, configData.numDoubleClass);
			Assert.assertEquals(8.8F, configData.numFloatClass);
			Assert.assertEquals("string", configData.string);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configPreferEnvironment() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"preferVars\">";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "<var name=\"numInt\" env=\"TEST\"><value>2</value></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("preferVars");
			Assert.assertEquals(2, configData.numInt);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected=ConfigurationException.class)
	public void configReferencedNonExistObject() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"parent\">";
			configText += "<var name=\"userObj\" ref=\"child\" />";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}	
	}

	@Test
	public void configReferencedObject() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"child\">";
			configText += "</object>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"parent\">";
			configText += "<var name=\"userObj\"><object ref=\"child\" /></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataParent = (ConfigData)config.getObject("parent");
			ConfigData configDataChild = (ConfigData)config.getObject("child");
			Assert.assertEquals(configDataParent.userObj, configDataChild);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configInternalObject() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"parent\">";
			configText += " <var name=\"userObj\">";
			configText += "  <object class=\"" + VALID_CLASS + "\">";
			configText += "   <var name=\"string\" value=\"INTERNAL\" />";
			configText += "  </object>";
			configText += " </var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataParent = (ConfigData)config.getObject("parent");			
			Assert.assertEquals("INTERNAL", ((ConfigData)configDataParent.userObj).string);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configListReferences() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"oneObj\">";
			configText += "</object>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"twoObj\">";
			configText += "</object>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"listObj\">";
			configText += "<var name=\"listUserObj\">";
			configText += "<list>";
			configText += "<object ref=\"oneObj\" monkey=\"nope\" />";
			configText += "<object ref=\"twoObj\" />";
			configText += "</list>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataOne = (ConfigData)config.getObject("oneObj");
			ConfigData configDataTwo = (ConfigData)config.getObject("twoObj");
			ConfigData configDataList = (ConfigData)config.getObject("listObj");
			Assert.assertEquals(2, configDataList.listUserObj.size());
			Assert.assertEquals(configDataOne, configDataList.listUserObj.get(0));
			Assert.assertEquals(configDataTwo, configDataList.listUserObj.get(1));			
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test (expected = ConfigurationException.class)
	public void configDefineNoInfo() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<define>";
			configText += "</define>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}			

	@Test (expected = ConfigurationException.class)
	public void configDefineMissingValue() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<define id=\"defined\">";
			configText += "</define>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}			

	@Test (expected = ConfigurationException.class)
	public void configDefineEmptyValue() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<define id=\"defined\">";
			configText += "<value></value>";
			configText += "</define>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}				

	@Test (expected = ConfigurationException.class)
	public void configDefineInvalidValue() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<define id=\"defined\">";
			configText += "<value>invalid</value>";
			configText += "</define>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}				
	
	@Test
	public void configDefined() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<define id=\"defined\">";
			configText += "<value><![CDATA[";
			configText += "<object class=\"$1\" id=\"$2\">";
			configText += "<var name=\"numInt\"><value>$3</value></var>";
			configText += "</object>";
			configText += "]]></value>";
			configText += "</define>";
			configText += "<object define=\"defined\" v1=\"" + VALID_CLASS + "\" v2=\"oneObj\" v3=\"1\" />";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataOne = (ConfigData)config.getObject("oneObj");
			Assert.assertEquals(1, configDataOne.numInt);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}		

	@Test (expected = ConfigurationException.class)
	public void configNotDefined() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object define=\"defined\" v1=\"" + VALID_CLASS + "\" v2=\"oneObj\" v3=\"1\" />";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataOne = (ConfigData)config.getObject("oneObj");
			Assert.assertEquals(1, configDataOne.numInt);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}			
	
	@Test
	public void configListEmpty() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"listObj\">";
			configText += "<var name=\"listStrings\">";
			configText += "<list>";
			configText += "</list>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataList = (ConfigData)config.getObject("listObj");
			Assert.assertEquals(0, configDataList.listStrings.size());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configListDefines() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<define id=\"defined\">";
			configText += "<value><![CDATA[";
			configText += "<object class=\"$1\" id=\"$2\">";
			configText += "<var name=\"numInt\"><value>$3</value></var>";
			configText += "</object>";
			configText += "]]></value>";
			configText += "</define>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"listObj\">";
			configText += "<var name=\"listUserObj\">";			
			configText += "<list>";			
			configText += "<object define=\"defined\" v1=\"" + VALID_CLASS + "\" v2=\"oneObj\" v3=\"1\" />";
			configText += "<object define=\"defined\" v1=\"" + VALID_CLASS + "\" v2=\"twoObj\" v3=\"2\" />";
			configText += "</list>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataList = (ConfigData)config.getObject("listObj");
			Assert.assertEquals(2, configDataList.listUserObj.size());
			ConfigData configDataOne = (ConfigData)configDataList.listUserObj.get(0);
			Assert.assertEquals(1, configDataOne.numInt);			
			ConfigData configDataTwo = (ConfigData)configDataList.listUserObj.get(1);
			Assert.assertEquals(2, configDataTwo.numInt);			
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}		
	
	@Test
	public void configListStrings() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"listObj\">";
			configText += "<var name=\"listStrings\">";
			configText += "<list>";
			configText += "<value>monkey</value>";
			configText += "<value>kangaroo</value>";
			configText += "</list>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataList = (ConfigData)config.getObject("listObj");
			Assert.assertEquals(2, configDataList.listStrings.size());
			Assert.assertEquals("monkey", configDataList.listStrings.get(0));
			Assert.assertEquals("kangaroo", configDataList.listStrings.get(1));			
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configListInteger() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"listInt\">";
			configText += "<var name=\"listIntClass\">";
			configText += "<list>";
			configText += "<value>1</value>";
			configText += "<value>2</value>";
			configText += "</list>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataList = (ConfigData)config.getObject("listInt");
			Assert.assertEquals(2, configDataList.listIntClass.size());
			Assert.assertEquals(Integer.valueOf(1), configDataList.listIntClass.get(0));
			Assert.assertEquals(Integer.valueOf(2), configDataList.listIntClass.get(1));			
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configMapEmpty() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"mapObj\">";
			configText += "<var name=\"mapStrings\">";
			configText += "<map>";
			configText += "</map>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataMap = (ConfigData)config.getObject("mapObj");
			Assert.assertEquals(0, configDataMap.mapStrings.size());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configMapDefines() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<define id=\"defined\">";
			configText += "<value><![CDATA[";
			configText += "<object class=\"$1\" id=\"$2\">";
			configText += "<var name=\"numInt\"><value>$3</value></var>";
			configText += "</object>";
			configText += "]]></value>";
			configText += "</define>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"mapObj\">";
			configText += "<var name=\"mapUserObj\">";			
			configText += "<map>";			
			configText += "<pair>";
			configText += "<key><object define=\"defined\" v1=\"" + VALID_CLASS + "\" v2=\"oneObj\" v3=\"1\" /></key>";
			configText += "<value><object define=\"defined\" v1=\"" + VALID_CLASS + "\" v2=\"twoObj\" v3=\"2\" /></value>";
			configText += "</pair>";
			configText += "</map>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataMap = (ConfigData)config.getObject("mapObj");
			Assert.assertEquals(1, configDataMap.mapUserObj.size());
			ConfigData configDataOne = (ConfigData)configDataMap.mapUserObj.keySet().toArray()[0];
			Assert.assertEquals(1, configDataOne.numInt);			
			ConfigData configDataTwo = (ConfigData)configDataMap.mapUserObj.get(configDataOne);
			Assert.assertEquals(2, configDataTwo.numInt);			
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}		

	@Test
	public void configMapStrings() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"mapObj\">";
			configText += "<var name=\"mapStrings\">";
			configText += "<map>";
			configText += "<pair>";
			configText += "<key>monkey</key><value>monkeyValue</value>";
			configText += "</pair>";
			configText += "<pair>";
			configText += "<key>kangaroo</key><value>kangarooValue</value>";
			configText += "</pair>";
			configText += "</map>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataMap = (ConfigData)config.getObject("mapObj");
			Assert.assertEquals(2, configDataMap.mapStrings.size());
			Assert.assertTrue(configDataMap.mapStrings.containsKey("monkey"));
			Assert.assertEquals("monkeyValue", configDataMap.mapStrings.get("monkey"));
			Assert.assertTrue(configDataMap.mapStrings.containsKey("kangaroo"));
			Assert.assertEquals("kangarooValue", configDataMap.mapStrings.get("kangaroo"));			
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configMapInteger() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"mapInt\">";
			configText += "<var name=\"mapIntClass\">";
			configText += "<map>";
			configText += "<pair>";
			configText += "<key>1</key><value>11</value>";
			configText += "</pair>";
			configText += "<pair>";
			configText += "<key>2</key><value>22</value>";
			configText += "</pair>";
			configText += "</map>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataMap = (ConfigData)config.getObject("mapInt");
			Assert.assertEquals(2, configDataMap.mapIntClass.size());
			Set<Integer> keys = configDataMap.mapIntClass.keySet();
			Assert.assertTrue(configDataMap.mapIntClass.containsKey(Integer.valueOf(1)));
			Assert.assertEquals(Integer.valueOf(11), configDataMap.mapIntClass.get(1));
			Assert.assertTrue(configDataMap.mapIntClass.containsKey(Integer.valueOf(2)));
			Assert.assertEquals(Integer.valueOf(22), configDataMap.mapIntClass.get(2));			
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected = ConfigurationException.class)
	public void configMapNoKey() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"mapObj\">";
			configText += "<var name=\"mapStrings\">";
			configText += "<map>";
			configText += "<pair>";
			configText += "<value>monkeyValue</value>";
			configText += "</pair>";
			configText += "</map>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataMap = (ConfigData)config.getObject("mapObj");
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configMapNoValue() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"mapObj\">";
			configText += "<var name=\"mapStrings\">";
			configText += "<map>";
			configText += "<pair>";
			configText += "<key>monkey</key>";
			configText += "</pair>";
			configText += "</map>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataMap = (ConfigData)config.getObject("mapObj");
			Assert.assertEquals(1, configDataMap.mapStrings.size());
			Assert.assertTrue(configDataMap.mapStrings.containsKey("monkey"));
			Assert.assertNull(configDataMap.mapStrings.get("monkey"));
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test 
	public void init() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"initTest\" init=\"init\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test 
	public void initStatic() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"initTest\" init=\"initStatic\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	
	
	@Test (expected=ConfigurationException.class)
	public void initFail() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"initTest\" init=\"initFail\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected=Error.class) 
	public void servletInitNotExist() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;			
			configText += "<servlet class=\"notExist\">";
			configText += "</servlet>";
			configText += CONFIG_SUFFIX;			
			Config.setConfiguration(configText);
			ConfigServlet servlet = new ConfigServlet();
			servlet.init();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test 
	public void servletInit() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object id=\"obj\" class=\"" + VALID_CLASS + "\">";
			configText += "</object>";
			configText += "<servlet class=\"com.aol.webservice_base.configuration.support.ConfigServlet\">";
			configText += "</servlet>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			ConfigServlet servlet = new ConfigServlet(); 
			servlet.init();			
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected=ConfigurationException.class)
	public void configTemplateNoNameFail() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<template class=\"" + VALID_CLASS + "\">";
			configText += "</template>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test (expected=ConfigurationException.class)
	public void configTemplateNoClassFail() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<template id=\"noClassTemplate\">";
			configText += "</template>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	
	
	@Test (expected=ConfigurationException.class)
	public void configTemplateSameNameFail() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<template class=\"" + VALID_CLASS + "\" id=\"duplicateTemplate\">";
			configText += "</template>";
			configText += "<template class=\"" + VALID_CLASS + "\" id=\"duplicateTemplate\">";
			configText += "</template>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	
	
	@Test (expected=ConfigurationException.class)
	public void configTemplateNotFound() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"allVars\" template=\"allVarsTemplate\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}
	
	@Test (expected=ConfigurationException.class)
	public void configTemplateSameNameEnvFail() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<template env=\"TEST\" class=\"" + VALID_CLASS + "\" id=\"duplicateTemplateInEnv\">";
			configText += "</template>";
			configText += "<template env=\"TEST\" class=\"" + VALID_CLASS + "\" id=\"duplicateTemplateInEnv\">";
			configText += "</template>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	
	
	@Test 
	public void configTemplateSameNameInEnvOk() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<template class=\"" + VALID_CLASS + "\" id=\"duplicateTemplate\">";
			configText += "</template>";
			configText += "<template env=\"TEST\" class=\"" + VALID_CLASS + "\" id=\"duplicateTemplate\">";
			configText += "</template>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	
	
	@Test
	public void configTemplate() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<template class=\"" + VALID_CLASS + "\" id=\"allVarsTemplate\">";
			configText += "<var name=\"bool\"><value>true</value></var>";
			configText += "<var name=\"boolClass\"><value>true</value></var>";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "<var name=\"numLong\"><value>2</value></var>";
			configText += "<var name=\"numDouble\"><value>3.3</value></var>";
			configText += "<var name=\"numFloat\"><value>4.4</value></var>";
			configText += "<var name=\"numIntClass\"><value>5</value></var>";
			configText += "<var name=\"numLongClass\"><value>6</value></var>";
			configText += "<var name=\"numDoubleClass\"><value>7.7</value></var>";
			configText += "<var name=\"numFloatClass\"><value>8.8</value></var>";
			configText += "<var name=\"string\"><value>string</value></var>";
			configText += "</template>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"allVars\" template=\"allVarsTemplate\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("allVars");
			Assert.assertEquals(true, configData.bool);
			Assert.assertEquals(Boolean.TRUE, configData.boolClass);
			Assert.assertEquals(1, configData.numInt);
			Assert.assertEquals(2, configData.numLong);
			Assert.assertEquals(3.3D, configData.numDouble);
			Assert.assertEquals(4.4F, configData.numFloat);
			Assert.assertEquals(Integer.valueOf(5), configData.numIntClass);
			Assert.assertEquals(Long.valueOf(6), configData.numLongClass);
			Assert.assertEquals(7.7D, configData.numDoubleClass);
			Assert.assertEquals(8.8F, configData.numFloatClass);
			Assert.assertEquals("string", configData.string);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configTemplateOverride() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<template class=\"" + VALID_CLASS + "\" id=\"allVarsTemplate\">";
			configText += "<var name=\"bool\"><value>true</value></var>";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "</template>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"allVars\" template=\"allVarsTemplate\">";
			configText += "<var name=\"bool\"><value>false</value></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("allVars");
			Assert.assertEquals(false, configData.bool);
			Assert.assertEquals(1, configData.numInt);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configTemplateNesting() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<template class=\"" + VALID_CLASS + "\" id=\"baseTemplate\">";
			configText += "<var name=\"bool\"><value>true</value></var>";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "</template>";
			configText += "<template class=\"" + VALID_CLASS + "\" id=\"inheritedTemplate\" template=\"baseTemplate\">";
			configText += "<var name=\"numInt\"><value>2</value></var>";
			configText += "</template>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"allVars\" template=\"inheritedTemplate\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("allVars");
			Assert.assertEquals(true, configData.bool);
			Assert.assertEquals(2, configData.numInt);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	
	
	@Test
	public void configTemplateNestingAliased() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<alias from=\"ALIAS_CLASS\" to=\"" + VALID_CLASS + "\" />";
			configText += "<template class=\"" + VALID_CLASS + "\" id=\"baseTemplate\">";
			configText += "<var name=\"bool\"><value>true</value></var>";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "</template>";
			configText += "<template class=\"ALIAS_CLASS\" id=\"inheritedTemplate\" template=\"baseTemplate\">";
			configText += "<var name=\"numInt\"><value>2</value></var>";
			configText += "</template>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"allVars\" template=\"inheritedTemplate\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("allVars");
			Assert.assertEquals(true, configData.bool);
			Assert.assertEquals(2, configData.numInt);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test (expected=ConfigurationException.class)
	public void configTemplateInvalidNesting() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<template class=\"" + VALID_CLASS + "\" id=\"baseTemplate\">";
			configText += "<var name=\"bool\"><value>true</value></var>";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "</template>";
			configText += "<template class=\"Java.util.String\" id=\"inheritedTemplate\" template=\"baseTemplate\">";
			configText += "<var name=\"numInt\"><value>2</value></var>";
			configText += "</template>";
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"allVars\" template=\"inheritedTemplate\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("allVars");
			Assert.assertEquals(true, configData.bool);
			Assert.assertEquals(2, configData.numInt);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test (expected=SAXParseException.class)
	public void configNeedCDATA() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"name\">";
			configText += " <var name=\"string\" >";
			configText += "  <value>";
			configText +=     NEED_CDATA_VALUE;
			configText += "  </value>";
			configText += " </var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configCDATA() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"name\">";
			configText += " <var name=\"string\" >";
			configText += "  <value><![CDATA[" + NEED_CDATA_VALUE + "]]>";
			configText += "  </value>";
			configText += " </var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("name");
			Assert.assertEquals(NEED_CDATA_VALUE, configData.string);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configCDATAWhitespace() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"name\">";
			configText += " <var name=\"string\" >";
			configText += "  <value>    <![CDATA[" + NEED_CDATA_VALUE + "]]>";
			configText += "  </value>";
			configText += " </var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("name");
			Assert.assertEquals(NEED_CDATA_VALUE, configData.string);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}		

	@Test (expected = ConfigurationException.class)
	public void configAliasHasEnv() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;			
			configText += ALIASES_ENV;
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}		
	
	@Test (expected = ConfigurationException.class)
	public void configAliasNoFrom() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;			
			configText += ALIASES_NO_FROM;
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}		
	
	@Test (expected = ConfigurationException.class)
	public void configAliasNoTo() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;			
			configText += ALIASES_NO_TO;
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}		

	@Test (expected = ConfigurationException.class)
	public void configAliasDuplicate() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += ALIASES;
			configText += ALIASES;
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}			
	
	@Test
	public void configAlias() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += ALIASES;
			configText += "<object class=\"" + ABBR_VALID_CLASS + "\" id=\"name\">";
			configText += " <var name=\"string\" >";
			configText += "  <value>    <![CDATA[" + NEED_CDATA_VALUE + "]]>";
			configText += "  </value>";
			configText += " </var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("name");
			Assert.assertEquals(NEED_CDATA_VALUE, configData.string);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}		

	@Test (expected = ConfigurationException.class)
	public void testParseTextFail() throws ConfigurationException {
		String text = "THIS SHOULD FAIL";
		Config.exposedParseTextIntoDocument(text);	
	}

	@Test
	public void testParseText() throws ConfigurationException {
		String text = "<object class=\"com.aol.webservice_base.persistence.helper.QueryParameter\"><var name=\"type\" value=\"$1\" /><var name=\"varName\" value=\"$2\" /></object>";
		Config.exposedParseTextIntoDocument(text);
	}

	@Test
	public void configByFactory() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"instance\" factory=\"getInstance\">";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("instance");
			Assert.assertSame(ConfigData.getInstance(), configData);
			Assert.assertEquals(1, configData.numInt);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected = ConfigurationException.class)
	public void configByFactoryProtected() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"instance\" factory=\"getInstanceProtected\">";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}				
	
	@Test (expected = ConfigurationException.class)
	public void configByFactoryDNE() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"instance\" factory=\"getInstanceNotExist\">";
			configText += "<var name=\"numInt\"><value>1</value></var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}
	
	@Test
	public void configItemList() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"listObj\">";
			configText += "<var name=\"items\">";
			configText += "<list>";
			configText += "<value>monkey</value>";
			configText += "<value>kangaroo</value>";
			configText += "</list>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataList = (ConfigData)config.getObject("listObj");
			Assert.assertEquals(2, configDataList.items.size());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configItemSingle() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"listObj\">";
			configText += "<var name=\"items\">";
			configText += "<value>monkey</value>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataList = (ConfigData)config.getObject("listObj");
			Assert.assertEquals(1, configDataList.items.size());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configItem2List() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"listObj\">";
			configText += "<var name=\"items2\">";
			configText += "<list>";
			configText += "<value>monkey</value>";
			configText += "<value>kangaroo</value>";
			configText += "</list>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataList = (ConfigData)config.getObject("listObj");
			Assert.assertEquals(2, configDataList.items2.size());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configItem2Single() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"listObj\">";
			configText += "<var name=\"items2\">";
			configText += "<value>monkey</value>";
			configText += "</var>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configDataList = (ConfigData)config.getObject("listObj");
			Assert.assertEquals(1, configDataList.items2.size());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}			

	@Test
	public void canAssignTest() throws Throwable {
		try {
			Assert.assertTrue(Configuration.canAssign(Integer.class, Integer.class));
			Assert.assertTrue(Configuration.canAssign(int.class, int.class));
			Assert.assertTrue(Configuration.canAssign(Integer.class, int.class));
			Assert.assertTrue(Configuration.canAssign(int.class, Integer.class));

			Assert.assertTrue(Configuration.canAssign(Long.class, Long.class));
			Assert.assertTrue(Configuration.canAssign(long.class, long.class));
			Assert.assertTrue(Configuration.canAssign(Long.class, long.class));
			Assert.assertTrue(Configuration.canAssign(long.class, Long.class));

			Assert.assertTrue(Configuration.canAssign(Boolean.class, Boolean.class));
			Assert.assertTrue(Configuration.canAssign(boolean.class, boolean.class));
			Assert.assertTrue(Configuration.canAssign(Boolean.class, boolean.class));
			Assert.assertTrue(Configuration.canAssign(boolean.class, Boolean.class));			

			Assert.assertTrue(Configuration.canAssign(Double.class, Double.class));
			Assert.assertTrue(Configuration.canAssign(double.class, double.class));
			Assert.assertTrue(Configuration.canAssign(Double.class, double.class));
			Assert.assertTrue(Configuration.canAssign(double.class, Double.class));			

			Assert.assertTrue(Configuration.canAssign(Float.class, Float.class));
			Assert.assertTrue(Configuration.canAssign(float.class, float.class));
			Assert.assertTrue(Configuration.canAssign(Float.class, float.class));
			Assert.assertTrue(Configuration.canAssign(float.class, Float.class));						

			Assert.assertFalse(Configuration.canAssign(String.class, Object.class));
			Assert.assertTrue(Configuration.canAssign(Object.class, String.class));
			
			Assert.assertTrue(Configuration.canAssign(List.class, ArrayList.class));
			Assert.assertFalse(Configuration.canAssign(ArrayList.class, List.class));
			
			Assert.assertFalse(Configuration.canAssign(ArrayList.class, TreeMap.class));
	} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}			

	@Test
	public void configByFactoryParam() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"instance\" factory=\"configDataFactory\">";
			configText += "<params><param type=\"int\" value=\"3\"/></params>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("instance");
			Assert.assertEquals(3, configData.numInt);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test
	public void configByFactoryParams() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"instance\" factory=\"configDataFactory\">";
			configText += "<params><param type=\"int\" value=\"3\"/><param type=\"long\" value=\"3\"/></params>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			ConfigData configData = (ConfigData)config.getObject("instance");
			Assert.assertEquals(3, configData.numInt);
			Assert.assertEquals(3, configData.numLong);
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test (expected=ConfigurationException.class)
	public void configByFactoryParamsFlipped() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"instance\" factory=\"configDataFactory\">";
			configText += "<params><param type=\"long\" value=\"3\"/><param type=\"int\" value=\"3\"/></params>";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			config.getObject("instance");
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}	

	@Test (expected=ConfigurationException.class)
	public void configByFactoryParamBadValue() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<object class=\"" + VALID_CLASS + "\" id=\"instance\" factory=\"configDataFactory\" paramType=\"string\" paramValue=\"3\">";
			configText += "</object>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			config.getObject("instance");
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}
	
	@Test
	public void configStatic() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static class=\"" + STATIC_CLASS + "\">";
			configText += "<var name=\"intValue\"><value>5</value></var>";
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configStaticAlt() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static class=\"" + STATIC_CLASS + "\" value=\"5\">";
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configStaticAltEnv1() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static env=\"TEST\" class=\"" + STATIC_CLASS + "\" value=\"5\">";
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configStaticAltEnv2() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static env=\"TEST\" class=\"" + STATIC_CLASS + "\" value=\"5\">";
			configText += "</static>";
			configText += "<static class=\"" + STATIC_CLASS + "\" value=\"6\">";
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configStaticAltEnv3() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static class=\"" + STATIC_CLASS + "\" value=\"6\">";
			configText += "</static>";
			configText += "<static env=\"TEST\" class=\"" + STATIC_CLASS + "\" value=\"5\">";			
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configStaticEnv1() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static class=\"" + STATIC_CLASS + "\">";
			configText += "<var name=\"intValue\" env=\"TEST\"><value>5</value></var>";
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configStaticEnv2() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static class=\"" + STATIC_CLASS + "\">";
			configText += "<var name=\"intValue\"><value>6</value></var>";
			configText += "<var name=\"intValue\" env=\"TEST\"><value>5</value></var>";
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test
	public void configStaticEnv3() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static class=\"" + STATIC_CLASS + "\">";
			configText += "<var name=\"intValue\" env=\"TEST\"><value>5</value></var>";
			configText += "<var name=\"intValue\"><value>6</value></var>";			
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}
	
	@Test (expected=ConfigurationException.class)
	public void configStaticMissingClass() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static>";
			configText += "<var name=\"intValueNotExist\"><value>5</value></var>";
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	
	@Test (expected=ClassNotFoundException.class)
	public void configStaticBadClass() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static class=\"" + STATIC_CLASS + "notExist\">";
			configText += "<var name=\"intValue\"><value>5</value></var>";
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}

	@Test (expected=ConfigurationException.class)
	public void configStaticBadMember() throws Throwable {
		try {
			String configText = CONFIG_PREFIX;
			configText += "<static class=\"" + STATIC_CLASS + "\">";
			configText += "<var name=\"intValueNotExist\"><value>5</value></var>";
			configText += "</static>";
			configText += CONFIG_SUFFIX;
			Config.setConfiguration(configText);
			config = (Config)Config.getTestInstance();
			Assert.assertEquals(FIVE, StaticData.getIntValue());
		} catch (ExceptionInInitializerError e) {
			Throwable cause = e.getCause();			
			throw(cause);
		}
	}
}


