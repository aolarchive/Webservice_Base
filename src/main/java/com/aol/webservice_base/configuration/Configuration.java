/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.util.reflection.ReflectionHelper;
import com.aol.webservice_base.util.types.TypesHelper;
import com.aol.webservice_base.util.xpath.XPathHelper;

/*
 * Configuration
 *
 * For testing your configurations w/o running your app: See ValidateConfig and
 * testConfig.sh in /src/tools
 *
 * This class will control the input of the configuration (config.xml) for the
 * application Key features:
 * 1. It will build up a set of Objects and store them for use by components
 * 2. It will be leveraged by the servlets to initialize their state (using components)
 * 3. Preferences are made for variables by environment
 *    (eliminating multiple config files)
 * 4. Value attributes can be used or contents of tag are used (attributes preferred)
 * 5. Child objects can either be references (for reuse) or inlined
 * 6. Ability to import other xml files (must be well formed, non-fragments)
 * 7. Objects can inherit from templates, which allows reuse of common configurations
 *    Example: read and write databases might have many of the same config
 * 8. Alias of shortened names of classes to full class name
 * 9. Defines (similar to C) for fast object configuration 1
 * 10. Supports referencing properties in values format: ${propertyName}
 *
 * NOTES:
 * 1. Variables set are by setter. Setters must be named:
 *    "setMemberName()" for configuration variable "memberName"
 * 2. files are picked up from WEB-INF/classes unless CONFIG_PATH_MODIFIER is defined
 *    CONFIG_PATH_MODIFIER may be ABSOLUTE or RELATIVE to WEB-INF/classes and end with a /
 *
 *
 * USAGE: ======
 *
 * DEFINE : Similar to how C has a #define (to create objects)
 * <define> have the following child nodes:
 * * <value> where the content must be in CDATA and use $1 $2 $3... for replacements
 *
 * * <define> have the following attributes:
 * * id - unique id to use for define looking up
 *
 *
 * ALIAS: allows you to use shorthand for a java class
 * <alias> nodes have the following attributes
 * * from - nickname for a java class
 * * to - full namespaced java class name
 *
 *
 * TEMPLATE - template for creating objects that have reused member values
 * <template> nodes are not objects by themselves, but look like objects but are
 * used as the basis for an object or servlet values from a template may be
 * overriden by subsequent the object configuration
 *
 * <template> nodes have the following attributes:
 * * id - unique id to use for template looking up
 * * template - nested template
 *
 *
 * OBJECT - creates instance of an object
 * <object> nodes have the following attributes:
 * * define - value is id of template to look up
 * * v1, v2, v3... Defined to match $1, $2, $3... - OR -
 * * ref - value is the key to look up the object - OR -
 * * factory - (optional) used to invoke factory method to create object instead of "new"
 * * * may take parameters:
 * * * <params><param type="type" value="value"/><param type="ref" value="id" /></params>
 * * template - (optional) template on which to base this object - always applicable
 * * id - unique id to use for object looking (only needed when not inlined)
 * * class - full class path to find class to create instance (can be alias)
 * * init - (optional) method to call after object values are all set
 *
 * STATIC - initializes static members
 * <static> nodes have the following attributes:
 * * class - full class path to find class to create instance (can be alias)
 * * value - value to set variable as (alternatively can be child <value>)
 *
 * SERVLET - (similar to object) configuration for a servlet which is requested
 * at init time
 *
 * <servlet> nodes function similarly to objects
 *
 * OTHER / COMMON <var> nodes have the following attributes:
 * * name - name within object for which to call setter - so 'x' calls 'setX()'
 * * value - value to set variable as (alternatively can be child <value>)
 * * env - (optional) to specify environment
 *
 * <var> nodes have:
 * * child node of <object> or <list> or <map> or <value> (if not as attribute)
 *
 * <list> nodes have the following:
 * * child nodes of <object> or <value>
 *
 * <map> nodes have the following:
 * * child nodes of <pair>
 * <pair> nodes have the following:
 * * child node of <key>
 * * child node of <value>
 *
 * <import> have full <config> document and following attributes:
 * * resource - filename of included resource
 *
 *
 * * EXAMPLE: ========== <config> <!-- triggers selection by matching attribute,
 * if possible --> <env>DEV</env>
 *
 * <!-- named items that can be reused by multiple servlets... or not -->
 *    <object name="validatorParamServlet1" class="com.aol.webservice_base.validator.ParamValidator">
 *       <var name="paramName" />
 *       <var name="paramName2" match="regex" />
 *       <var name="paramName3" min="0" max="100" />
 *    </object>
 *    <object name="validatorAkes" class="com.aol.webservice_base.validator.AKESValidator" init="init">
 *       <var name="host" env="DEV"><value>http://server.for.dev</value></var>
 *       <var name="host" env="QA"><value>http://server.for.qa</value></var>
 *       <var name="host" env="PROD"><value>"http://server.for.prod</value></var>
 *       <var name="timeoutMs" value="10000" />
 *    </object>
 *
 * <import resource="otherObjects" />
 *
 * <!-- configuration for each servlet -->
 *    <servlet class="com.aol.your.app.derived.from.BaseServlet">
 *       <var name="validators">
 *           <list>
 *              <object ref="validatorParamServlet1" />
 *              <object ref="validatorAkes" />
 *           </list>
 *       </var>
 *       <var name="varName">value</var>
 *    </servlet>
 * </config>
 */
public class Configuration {

    protected static final Logger logger = Logger.getLogger(Configuration.class);
    protected static Configuration config = null;
    protected Document configDocument = null;
    protected String env = null;
    protected ArrayList<String> imports;
    protected Map<String, String> defines;
    protected Map<String, String> aliases;
    protected Map<String, Node> templates;
    protected Map<String, Object> objects;
    protected Map<String, ArrayList<Object>> objectsByCategory;
    private static final String CONFIG_PATH_MODIFIER = "CONFIG_PATH_MODIFIER";
    private static final String XML_HEADER = "<?xml version='1.0' encoding='utf-8'?>";
    private static boolean invalidConfig = false;

    protected Configuration() {
        initialize();
    }

    public static synchronized Configuration getInstance() {
   	 if (invalidConfig) {
   		 return null;
   	 } else {
   		 if (config == null) {
   			 config = new Configuration();
   		 }
   		 return config;
   	 }
    }

    /**
     * Gets the configuration stream. This method is overridden for introducing
     * test cases it gets the configuration file input stream
     *
     * @param filename
     *           the filename
     *
     * @return the configuration stream
     * @throws ConfigurationException
     */
    protected InputStream getConfigurationStream(String filename) throws ConfigurationException {
        InputStream openedStream = null;

        String pathModifier = System.getProperty(CONFIG_PATH_MODIFIER);

        // do not use the path modifier if filename is specified
        if ((pathModifier == null) || filename.startsWith("/")) {
            pathModifier = "";
        } else if (!pathModifier.endsWith("/")) {
            throw new ConfigurationException("CONFIG_PATH_MODIFIER does not end with / : " + pathModifier);
        }

        pathModifier.trim();

        try {
            openedStream = new FileInputStream(pathModifier + filename);
        } catch (FileNotFoundException e) {
        }

        if (openedStream == null) {
            // Try in webapp dir
            openedStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathModifier + filename);
        }

        if (openedStream == null) {
            System.out.println("Unable to open file: " + pathModifier + filename);
        }
        return openedStream;
    }

    /**
     * Get objects by named category
     *
     * @parm String the category
     *
     * @return ArrayList<Object> the objects
     */
    public ArrayList<Object> getObjectsByCategory(String category) {
        return objectsByCategory.get(category);
    }

    /**
     * Get an object by Id
     *  @parm String the object ID
     *
     * @return Object> the object
     *
     */
    public Object getObjectById(String id) {
        return objects.get(id);
    }

    /**
     * Xml to string.
     *
     * @param node
     *           the node
     *
     * @return the string
     */
    protected static String xmlToString(Node node) {
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Initialize.
     */
    protected void initialize() {
        imports = new ArrayList<String>();
        defines = new TreeMap<String, String>();
        aliases = new TreeMap<String, String>();
        templates = new TreeMap<String, Node>();
        objects = new TreeMap<String, Object>();
        objectsByCategory = new TreeMap<String, ArrayList<Object>>();

        InputStream is = null;
        try {
            // File will be in WEB-INF/classes (config.xml)
            is = getConfigurationStream(Constants.CONFIG_FILE);
            if (is == null) {
                throw new ConfigurationException("No configuration found");
            } else {
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder docBuilder = domFactory.newDocumentBuilder();
                configDocument = docBuilder.parse(is);

                // determine the environment to use
                env = resolveXpathExpressionValue(configDocument, "/config/env/text()");
                if (env != null) {
                    env = env.trim();
                }
                if ((env == null) || (env.length() == 0)) {
                    throw new ConfigurationException("env is not set");
                }

                // resolve any imported xml data
                if (logger.isDebugEnabled()) {
                    logger.debug("Config BEFORE import with env=" + env + ": " + xmlToString(configDocument.getFirstChild()));
                }

                // import any files
                resolveImports(configDocument);

                // set up internal data structures
                setupDefines();
                setupAliases();
                setupTemplates();

                // Alter the document based on config
                resolveAliases();
                resolveDefines();

                if (logger.isDebugEnabled()) {
                    System.out.println("Config AFTER resolved: " + xmlToString(configDocument.getFirstChild()));
                }

                // create the configuration
                createObjects();
            }
        } catch (Throwable t) {
      	  invalidConfig = true;
      	  t.printStackTrace();
       	  logger.error(t);
      	  throw new ExceptionInInitializerError(t);
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    logger.error("Problem closing config file: " + e);
                }
            }
        }
    }

    /**
     * Resolve imports. Duplicate imports are ignored
     *
     * @param document
     *           the document
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     * @throws ParserConfigurationException
     *            the parser configuration exception
     */
    protected void resolveImports(Document document) throws XPathExpressionException, ConfigurationException, ParserConfigurationException {
        NodeList importResourceNodes = XPathHelper.getXpathExpressionNodeList(document, "/config/import");
        for (int i = 0; i < importResourceNodes.getLength(); i++) {
            Node importResourceNode = importResourceNodes.item(i);

            attrNotAllowed(importResourceNode, "env", "import");	// no ENV allowed

            String resourceFile = XPathHelper.getXpathExpressionValue(importResourceNode, "@resource");
            if (resourceFile == null) {
                throw new ConfigurationException("import does not contain required resource attribute");
            }

            // track that an imported file is not doubly included
            if (imports.contains(resourceFile)) {
                logger.info("IMPORT: '" + resourceFile + "' already imported");
                continue;
            } else {
                imports.add(resourceFile);
            }

            // File will be in WEB-INF/classes (config.xml)
            InputStream is = getConfigurationStream(resourceFile);
            if (is == null) {
                throw new ConfigurationException("No configuration found for import: " + resourceFile);
            } else {
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder importedDocBuilder = domFactory.newDocumentBuilder();
                Document importedDocument = null;
                try {
                    importedDocument = importedDocBuilder.parse(is);
                } catch (Exception e) {
                    throw new ConfigurationException("Problem parsing import: " + resourceFile);
                }
                resolveImports(importedDocument);

                Node importParentNode = importResourceNode.getParentNode();

                // create a comment node to indicate start of injection
                Comment comment = document.createComment("Start Import: " + resourceFile);
                importParentNode.insertBefore(comment, importResourceNode);

                // copy all the config nodes into the new document
                NodeList importedConfigNodes = XPathHelper.getXpathExpressionNodeList(importedDocument, "/config/*");
                if (importedConfigNodes.getLength() == 0) {
                    throw new ConfigurationException("No configuration data found in import: " + resourceFile);
                }
                for (int j = 0; j < importedConfigNodes.getLength(); j++) {
                    Node importedConfigNode = document.importNode(importedConfigNodes.item(j), true);
                    importParentNode.insertBefore(importedConfigNode, importResourceNode);
                }

                // create a comment node to indicate end of injection
                comment = document.createComment("End Import: " + resourceFile);
                importParentNode.insertBefore(comment, importResourceNode);

                // remove the import node since it was just inserted over
                importParentNode.removeChild(importResourceNode);
            }
        }
    }

    /**
     * Setup aliases.
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected void setupAliases() throws XPathExpressionException, ConfigurationException {
        NodeList aliasNodes = XPathHelper.getXpathExpressionNodeList(configDocument, "/config/alias");
        for (int i = 0; i < aliasNodes.getLength(); i++) {
            Node aliasNode = aliasNodes.item(i);

            attrNotAllowed(aliasNode, "env", "alias");	// no ENV allowed

            // get name and class of object
            String from = XPathHelper.getXpathExpressionValue(aliasNode, "@from");
            if (from == null) {
                throw new ConfigurationException("alias does not have from attribute specified");
            }

            if (aliases.containsKey(from)) {
                throw new ConfigurationException("alias duplicate: '" + from + "'");
            }

            String to = XPathHelper.getXpathExpressionValue(aliasNode, "@to");
            if (to == null) {
                throw new ConfigurationException("alias from: '" + from + "' does not have 'to' specified");
            }

            aliases.put(from, to);
        }
    }

    /**
     * Setup defines.
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected void setupDefines() throws XPathExpressionException, ConfigurationException {
        NodeList defineNodes = XPathHelper.getXpathExpressionNodeList(configDocument, "/config/define");
        for (int i = 0; i < defineNodes.getLength(); i++) {
            Node defineNode = defineNodes.item(i);

            attrNotAllowed(defineNode, "env", "define");	// no ENV allowed

            // get name and class of object
            String name = XPathHelper.getXpathExpressionValue(defineNode, "@id");
            if (name == null) {
                throw new ConfigurationException("define node does not have id specified");
            }

            if (defines.containsKey(name)) {
                throw new ConfigurationException("define duplicate: '" + name + "'");
            }

            String definition = XPathHelper.getXpathExpressionValue(defineNode, "value/text()");
            if (definition == null) {
                throw new ConfigurationException("define name: '" + name + "' does not have a data value specified");
            }

            // test that the data we have can be used to create a valid document
            parseTextIntoDocument("define name: '" + name + "'", definition);

            defines.put(name, definition);
        }
    }

    /**
     * Parses the text into document.
     *
     * @param errorIdentifier
     *           the error identifier
     * @param text
     *           the text
     *
     * @return the document
     *
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected static Document parseTextIntoDocument(String errorIdentifier, String text) throws ConfigurationException {
        try {
            text = XML_HEADER + text;
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder;
            docBuilder = domFactory.newDocumentBuilder();
            return docBuilder.parse(new ByteArrayInputStream(text.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new ConfigurationException(errorIdentifier + " could not be parsed into valid XML: " + text);
        }
    }

    /**
     * Setup templates.
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected void setupTemplates() throws XPathExpressionException, ConfigurationException {
        String finder = "/config/template[not(@env)]" + "|" + "/config/template[@env='" + env + "']";
        NodeList templateNodes = XPathHelper.getXpathExpressionNodeList(configDocument, finder);
        Map<String, Node> nodes = getEnvNodes(templateNodes, "template");

        for (int i = 0; i < templateNodes.getLength(); i++) {
            Node templateNode = templateNodes.item(i);
            // get name and class of object
            String templateId = XPathHelper.getXpathExpressionValue(templateNode, "@id");
            if (templateNode != nodes.get(templateId)) {
                continue;
            }

            // ensure the template has a class
            String templateClassName = XPathHelper.getXpathExpressionValue(templateNode, "@class");
            if (templateClassName == null) {
                throw new ConfigurationException("Class not specified for template id : " + templateId);
            }

            // Store away template node. Makes for easy copying
            templates.put(templateId, templateNode);
        }
    }

    /**
     * Resolve aliases.
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     */
    protected void resolveAliases() throws XPathExpressionException {
        // replace all aliases
        NodeList classAttributes = XPathHelper.getXpathExpressionNodeList(configDocument, "//@class");
        for (int i = 0; i < classAttributes.getLength(); i++) {
            Node classNode = classAttributes.item(i);
            for (String from : aliases.keySet()) {
                if (classNode.getNodeValue().equals(from)) {
                    classNode.setNodeValue(aliases.get(from));
                    break;
                }
            }
        }
    }

    /**
     * Resolve defines.
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected void resolveDefines() throws XPathExpressionException, ConfigurationException {
        NodeList objectDefineNodes = XPathHelper.getXpathExpressionNodeList(configDocument, "//object[@define]");
        for (int i = 0; i < objectDefineNodes.getLength(); i++) {
            Node objectNode = objectDefineNodes.item(i);
            String define = XPathHelper.getXpathExpressionValue(objectNode, "@define");
            String defineText = defines.get(define);
            if (defineText == null) {
                throw new ConfigurationException("Could not find define: " + define);
            }

            // find all the $num in the text and then ensure we have attributes for
            // each one, replacing as we go
            Pattern definePattern = Pattern.compile("(\\$[0-9]+)");
            Matcher defineMatcher = definePattern.matcher(defineText);
            StringBuffer sb = new StringBuffer(defineText.length());
            while (defineMatcher.find()) {
                String replace = defineMatcher.group(1);
                String attributeXpath = "@v" + replace.substring(1);
                String attributeValue = XPathHelper.getXpathExpressionValue(objectNode, attributeXpath);
                if (attributeValue == null) {
                    throw new ConfigurationException("Could not find corresponding attribute value for " + replace + " using define " + define);
                }
                defineMatcher.appendReplacement(sb, attributeValue);
            }
            defineMatcher.appendTail(sb);

            // create document based on define
            Document definedDocument = parseTextIntoDocument("Define: " + define, sb.toString());

            Node definedNode = configDocument.importNode(definedDocument.getFirstChild(), true);

            XPathHelper.replaceXPathNode(objectNode, definedNode);
        }
    }

    /**
     * Creates the objects and sets up statics
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws InstantiationException
     *            the instantiation exception
     * @throws IllegalAccessException
     *            the illegal access exception
     * @throws ClassNotFoundException
     *            the class not found exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected void createObjects() throws XPathExpressionException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            ConfigurationException {
        String finder = "/config/object[not(@env)]" + "|" + "/config/object[@env='" + env + "']|/config/static[not(@env)]" + "|" + "/config/static[@env='" + env + "']";
        NodeList objAndStaticNodes = XPathHelper.getXpathExpressionNodeList(configDocument, finder);

        Map<String, Node> nodes = getEnvNodes(objAndStaticNodes, "object");

        for (int i = 0; i < objAndStaticNodes.getLength(); i++) {
            Node objNode = objAndStaticNodes.item(i);
            
            if ("static".equals(objNode.getNodeName())) {
            	populateStatic(objNode);
            } else {
	            // get name and class of object
	            String id = XPathHelper.getXpathExpressionValue(objNode, "@id");
	            if (objNode != nodes.get(id)) {
	                continue;
	            }
	            Object object = createObject(objNode);
	
	            objects.put(id, object);
	
	            String category = XPathHelper.getXpathExpressionValue(objNode, "@category");
	            if (category != null) {
	                ArrayList<Object> objs = objectsByCategory.get(category);
	                if (objs == null) {
	                    objs = new ArrayList<Object>();
	                    objectsByCategory.put(category, objs);
	                }
	                objs.add(object);
	            }
            }
        }
    }

    protected Object[] getParams(String objectId, Node node) throws ConfigurationException {
        try {
            NodeList params = XPathHelper.getXpathExpressionNodeList(node, "params/param");
            if ((params == null) || (params.getLength() == 0)) {
                return null;
            }

            Object[] outputParams = new Object[params.getLength()];
            for (int i = 0; i < params.getLength(); i++) {
                Node param = params.item(i);
                Node typeNode = XPathHelper.getXpathExpressionNode(param, "@type");
                if (typeNode == null) {
                    throw new ConfigurationException(this.getClass().getName() + " for id: " + objectId + " cause: parameters / parameter type not specified");
                }
                Node valueNode = XPathHelper.getXpathExpressionNode(param, "@value");
                if (valueNode == null) {
                    throw new ConfigurationException(this.getClass().getName() + " for id: " + objectId + " cause: parameters / parameter value not specified");
                }

                String type = typeNode.getTextContent();
                String text = valueNode.getTextContent();
                Object value = (type.equals("ref")) ? objects.get(text) : TypesHelper.performSwitch(text, TypesHelper.determineSwitcher(type));
                outputParams[i] = value;
            }
            return outputParams;
        } catch (XPathExpressionException e) {
            throw new ConfigurationException(this.getClass().getName() + " for id: " + objectId + " cause: invalid params");
        }
    }

    protected List<String> getNestedTemplates(String className, Node node) throws XPathExpressionException, ConfigurationException {
        String templateName = XPathHelper.getXpathExpressionValue(node, "@template");
        if (templateName != null) {
            String templateClass = XPathHelper.getXpathExpressionValue(node, "@class");
            if (templateClass == null) {
                throw new ConfigurationException("class not specified for template: " + templateName);
            } else if (!templateClass.equals(className)) {
                throw new ConfigurationException("mismatched template: expected class: " + className + " got: " + templateName + " for template: " + templateName);
            }

            List<String> nestedTemplates = new ArrayList<String>();
            Node templateNode = templates.get(templateName);
            if (templateNode == null) {
                throw new ConfigurationException("template not defined: " + templateName);
            }

            List<String> childNestedTemplates = getNestedTemplates(className, templateNode);
            if (childNestedTemplates != null) {
                nestedTemplates.addAll(childNestedTemplates);
            }
            nestedTemplates.add(templateName);
            return nestedTemplates;
        }
        return null;
    }

    /**
     * Creates the object.
     *
     * @param node
     *           the node
     *
     * @return the object
     *
     * @throws InstantiationException
     *            the instantiation exception
     * @throws IllegalAccessException
     *            the illegal access exception
     * @throws ClassNotFoundException
     *            the class not found exception
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected Object createObject(Node node) throws InstantiationException, IllegalAccessException, ClassNotFoundException,
            XPathExpressionException, ConfigurationException {
        String className = XPathHelper.getXpathExpressionValue(node, "@class");

        String objectId = XPathHelper.getXpathExpressionValue(node, "@id");
        if (objectId == null) {
            objectId = "<< inlined >>";
        }

        if (className == null) {
            throw new ConfigurationException("class not specified for object for id: " + objectId);
        }

        // determine if the template specifies the class name
        // use one or the other (object or template) but if both specified, they
        // must match
        List<Node> templateNodes = null;

        List<String> templateNames = getNestedTemplates(className, node);

        if (templateNames != null) {
            templateNodes = new ArrayList<Node>(templateNames.size());
            for (String templateName : templateNames) {
                Node templateNode = templates.get(templateName);
                if (templateNode == null) {
                    throw new ConfigurationException("can not find template: " + templateName + " for id: " + objectId);
                }

                templateNodes.add(templateNode);
            }
        }

        // log the name of object being created
        if (logger.isInfoEnabled()) {
            logger.info("Creating object: " + objectId + " of class: " + className);
        }

        // check if this object is created by a factory
        Object object = null;
        String factoryName = XPathHelper.getXpathExpressionValue(node, "@factory");
        if (factoryName == null) {
            // create an object of the class and store it away in our object list
            object = Class.forName(className).newInstance();
        } else {
            Object[] params = getParams(objectId, node);

            try {
                Method factoryMethod = findMethod(className, factoryName, params);
                object = factoryMethod.invoke(null, params);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ConfigurationException(e.getClass().getName() + " for factory: " + factoryName + " on class: " + className + " for id: " + objectId + " cause: " + e.getMessage());
            }
        }

        // apply any configured templates
        if (templateNodes != null) {
            // populate this object with template config
            populateObject(object, templateNodes);
        }

        // apply objects own configuration (over optional template config)
        populateObject(object, node);

        // TODO: Potentially validate all values are set in the class
        // validateObject(object);

        // initialize the object if configured to do so
        initObject(object, templateNodes);
        initObject(object, node);

        return object;
    }

    /**
     * Initialize servlet with configuration This is requested by BaseServlet
     * upon initialization
     *
     * @param instance
     *           the instance
     *
     * @throws ConfigurationException
     *            the configuration exception
     */
    public void initializeServlet(Object instance) throws ConfigurationException {
		 try {
			 String className = instance.getClass().getName();
			 Node servletNode = XPathHelper.getXpathExpressionNode(configDocument.getFirstChild(), "/config/servlet[@class='"
					 + className + "']");
			 if (servletNode == null) {
				 throw new ConfigurationException("Could not find expected servlet config: " + className);
			 } else {
				 populateObject(instance, servletNode);
				 initObject(instance, servletNode);
			 }
		 } catch (Exception e) {
			 throw new ConfigurationException(e.getMessage());
		 }
    }

    protected void doPopulation(Node node, String className, Object object)  throws XPathExpressionException, ConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException {
   	 Class<?> clazz = Class.forName(className);
       // get all the objects setters - and find values for them
       List<Method> methods = ReflectionHelper.getSetterMethods(clazz);

       // We do no-env vars first and then overwrite them with the env vars
       String[] finders = {"var[not(@env)]", "var[@env='" + env + "']"};
       for (String finder : finders) {
           NodeList varNodes = XPathHelper.getXpathExpressionNodeList(node, finder);
           for (int i = 0; i < varNodes.getLength(); i++) {
               Node varNode = varNodes.item(i);
               String varName = XPathHelper.getXpathExpressionValue(varNode, "@name");
               if (varName == null) {
                   throw new ConfigurationException("Variable does not have name in configuration for class: " + className);
               }

               Object varValue = null;

               // try to get object reference
               varValue = parseRef(varNode, varName, className);

               // no object, try to get inlined object (or referenced object)
               if (varValue == null) {
                   Node objectNode = XPathHelper.getXpathExpressionNode(varNode, "object");
                   varValue = parseObject(objectNode, varName, className);
               }

               // no object, so check to see if it's a list
               if (varValue == null) {
                   Node list = XPathHelper.getXpathExpressionNode(varNode, "list");
                   varValue = populateObjectList(list, varName, className);
               }

               // no object, so check to see if it's a map
               if (varValue == null) {
                   Node map = XPathHelper.getXpathExpressionNode(varNode, "map");
                   varValue = populateObjectMap(map, varName, className);
               }

               // still no child object, so try to get value
               if (varValue == null) {
                   // Otherwise, we'll extract the value
                   varValue = resolveXpathExpressionValue(varNode, "@value");
                   if (varValue == null) {
                       varValue = resolveXpathExpressionValue(varNode, "value/text()");
                   }

                   // No value found - so throw problem
                   if (varValue == null) {
                       throw new ConfigurationException("Variable: " + varName + " does not have a value defined in configuration for class: " + className);
                   }
               }

               // try to find a setter for configured variable
               boolean isSet = false;
               for (Method method : methods) {
                   // determine information about this
                   String methodName = method.getName();
                   String methodMatch = ReflectionHelper.getSetter(varName);
                   Class<?>[] methodParams = method.getParameterTypes();
                   if (methodName.matches(methodMatch) && (methodParams.length == 1)) { // TODO: do other param validation
                       // now, based on the type expected by the setter, transform
                       // the string
                       Class<?> paramType = methodParams[0];
                       String paramTypeName = paramType.getCanonicalName();
                       String switcher = TypesHelper.determineSwitcher(paramTypeName);
                       if (paramTypeName.equals("java.lang.String") || !switcher.equals(TypesHelper.TYPE_OBJECT)) {
                           isSet = setValue(method, object, varValue, switcher);
                       } else {
                           List<String> genericTypes = new ArrayList<String>();
                           Type[] genericParameterTypes = method.getGenericParameterTypes();
                           for (Type genericParameterType : genericParameterTypes) {
                               if (genericParameterType instanceof ParameterizedType) {
                                   ParameterizedType aType = (ParameterizedType) genericParameterType;
                                   Type[] parameterArgTypes = aType.getActualTypeArguments();
                                   for (Type parameterArgType : parameterArgTypes) {
                                       Class<?> parameterArgClass = (Class<?>) parameterArgType;
                                       genericTypes.add(parameterArgClass.getName());
                                   }
                               }
                           }

                           if (varValue instanceof List<?>) {
                               varValue = convertArray((List<?>) varValue, genericTypes.get(0));
                           }
                           if (varValue instanceof Map<?, ?>) {
                               varValue = convertMap((Map<?, ?>) varValue, genericTypes.get(0), genericTypes.get(1));
                           }

                           if (paramTypeName.endsWith("[]")) {
                               // TODO
                               throw new ConfigurationException("Handling of arrays [] does not work yet");
                               // isSet = setValue(method, object,
                               // ((List<?>)varValue).toArray((?[])
                               // Array.newInstance(paramClass, 0)),
                               // TypesHelper.TYPE_OBJECT);
                           } else {
                               // we assume this is some Object-based item, so let java
                               // do the work
                               isSet = setValue(method, object, varValue, TypesHelper.TYPE_OBJECT);
                           }
                       }
                   }

                   if (isSet) {
                       break;
                   }
               }
               // problem if we didn't set the variable
               if (!isSet) {
                   throw new ConfigurationException("Unable to set variable: " + className + "." + varName + " to value: " + varValue);
               }
           }
       }   	 
    }
    
    /**
     * Populate object.
     *
     * @param object
     *           the object
     * @param node
     *           the node
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     * @throws InstantiationException
     *            the instantiation exception
     * @throws IllegalAccessException
     *            the illegal access exception
     * @throws ClassNotFoundException
     *            the class not found exception
     */
    protected void populateObject(Object object, Node node) throws XPathExpressionException, ConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        String className = XPathHelper.getXpathExpressionValue(node, "@class");

        doPopulation(node, className, object);
    }

    protected void populateStatic(Node node) throws XPathExpressionException, ConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException {
       String className = XPathHelper.getXpathExpressionValue(node, "@class");
       if (className == null)
      	 throw new ConfigurationException("Class not specified for static");

       doPopulation(node, className, null);
   }
    
    protected Object getNodeValue(Node node, String containerType, String keyOrValue, String varName, String className) throws XPathExpressionException, ConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (node == null) {
            return null;
        }

        Object itemValue = null;
        String nodeName = node.getNodeName();
        if (nodeName.equals("object")) {
            itemValue = parseObject(node, varName, className);
        } else if (nodeName.equals(keyOrValue)) {
            itemValue = resolveXpathExpressionValue(node, "@value");
            if (itemValue == null) {
                itemValue = resolveXpathExpressionValue(node, "text()");
            }
        }

        // We didn't get anything we could use, so fail
        if (itemValue == null) {
            throw new ConfigurationException("Unknown item (" + nodeName + ") in " + containerType + " for variable: " + varName + " in class: " + className);
        }

        return itemValue;
    }

    protected ArrayList<Object> populateObjectList(Node list, String varName, String className) throws XPathExpressionException, InstantiationException, IllegalAccessException, ClassNotFoundException, ConfigurationException {
        if (list == null) {
            return null;
        }

        NodeList listChildren = list.getChildNodes();
        ArrayList<Object> listValues = new ArrayList<Object>(listChildren.getLength());
        for (int i = 0; i < listChildren.getLength(); i++) {
            Node listChild = listChildren.item(i);

            // only care about element nodes here
            if (listChild.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            // try to get inlined object (or referenced object) or
            // value
            Object listItemValue = getNodeValue(listChild, "list", "value", varName, className);

            listValues.add(listItemValue);
        }
        return listValues;
    }

    protected HashMap<Object, Object> populateObjectMap(Node map, String varName, String className) throws XPathExpressionException, InstantiationException, IllegalAccessException, ClassNotFoundException, ConfigurationException {
        if (map == null) {
            return null;
        }

        NodeList pairs = XPathHelper.getXpathExpressionNodeList(map, "pair");
        HashMap<Object, Object> mapValues = new HashMap<Object, Object>();
        for (int i = 0; i < pairs.getLength(); i++) {
            Node pair = pairs.item(i);

            Node childObjectNode;
            Node keyNode = XPathHelper.getXpathExpressionNode(pair, "key");
            if (keyNode == null) {
                throw new ConfigurationException("Missing key in map pair for variable: " + varName + " in class: " + className);
            }
            childObjectNode = XPathHelper.getXpathExpressionNode(keyNode, "object");
            if (childObjectNode != null) {
                keyNode = childObjectNode;
            }


            Node valueNode = XPathHelper.getXpathExpressionNode(pair, "value");
            if (valueNode != null) {
                childObjectNode = XPathHelper.getXpathExpressionNode(valueNode, "object");
                if (childObjectNode != null) {
                    valueNode = childObjectNode;
                }
            }

            Object key = getNodeValue(keyNode, "map-key", "key", varName, className);
            Object value = getNodeValue(valueNode, "map-value", "value", varName, className);

            mapValues.put(key, value);
        }
        return mapValues;
    }

    protected void populateObject(Object object, List<Node> nodes) throws XPathExpressionException, ConfigurationException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        if (nodes != null) {
            for (Node node : nodes) {
                populateObject(object, node);
            }
        }
    }

    protected static String resolveXpathExpressionValue(Object xprContext, String xpExpression) throws ConfigurationException, XPathExpressionException {
        String value = XPathHelper.getXpathExpressionValue(xprContext, xpExpression);
        if (value == null) {
            return value;
        }

        String strValue = (String) value;
        Pattern envExtractor = Pattern.compile("(\\$\\{(.*?)\\})");
        Matcher envMatcher = envExtractor.matcher(strValue);
        StringBuffer sb = new StringBuffer(strValue.length());
        while (envMatcher.find()) {
            String desiredProperty = envMatcher.group(2).trim();
            String propertyValue = System.getProperty(desiredProperty);
            if (propertyValue == null) {
                throw new ConfigurationException("Could not find property value " + desiredProperty);
            }
            envMatcher.appendReplacement(sb, propertyValue);
        }
        envMatcher.appendTail(sb);

        return sb.toString();
    }

    protected void initObject(Object object, Node node) throws ConfigurationException, XPathExpressionException {
        if (node == null) {
            return;
        }

        // obtain an initMethod to be run after the object is configured
        String initMethodName = XPathHelper.getXpathExpressionValue(node, "@init");
        if (initMethodName != null) {
            String className = object.getClass().getName();

            if (logger.isInfoEnabled()) {
                logger.info("Calling init method: " + initMethodName + " on class: " + className);
            }

            Method initMethod = findMethod(className, initMethodName, null);
            try {
                initMethod.invoke(object);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ConfigurationException(e.getClass().getName() + " for object: "
                        + initMethodName + " on class: " + className
                        + " " + e.getMessage());
            }
        }
    }

    protected void initObject(Object object, List<Node> nodes) throws ConfigurationException, XPathExpressionException {
        if (nodes != null) {
            for (Node node : nodes) {
                initObject(object, node);
            }
        }
    }

    /**
     * Convert array to ensure contents are all of expected type
     *
     * @param list
     *           the list
     * @param expectedType
     *           the expected type
     *
     * @return the list<?>
     */
    protected List<?> convertArray(List<?> list, String expectedType) {
        String switcher = TypesHelper.determineSwitcher(expectedType);
        if (switcher.equals(TypesHelper.TYPE_OBJECT)) {
            return list;
        } else {
            ArrayList<Object> newList = new ArrayList<Object>();
            for (Object o : list) {
                newList.add(TypesHelper.performSwitch((String) o, switcher));
            }
            return newList;
        }
    }

    /**
     * Convert map to ensure contents are all of expected type
     *
     * @param map the map
     * @param expectedKeyType the expected key type
     * @param expectedValueType the expected value type
     * @return the map<?,?>
     */
    protected Map<?, ?> convertMap(Map<?, ?> map, String expectedKeyType, String expectedValueType) {
        String switcherKey = TypesHelper.determineSwitcher(expectedKeyType);
        String switcherValue = TypesHelper.determineSwitcher(expectedValueType);
        if (switcherKey.equals(TypesHelper.TYPE_OBJECT) && switcherValue.equals(TypesHelper.TYPE_OBJECT)) {
            return map;
        } else {
            Map<Object, Object> newMap = new HashMap<Object, Object>();
            for (Object key : map.keySet()) {
                Object newValue = map.get(key);
                if (!switcherValue.equals(TypesHelper.TYPE_OBJECT)) {
                    newValue = TypesHelper.performSwitch((String) newValue, switcherValue);
                }

                Object newKey = key;
                if (!switcherKey.equals(TypesHelper.TYPE_OBJECT)) {
                    newKey = TypesHelper.performSwitch((String) key, switcherKey);
                }

                newMap.put(newKey, newValue);
            }
            return newMap;
        }
    }

    /**
     * Parses the ref.
     *
     * @param node
     *           the node
     * @param varName
     *           the var name
     * @param className
     *           the class name
     *
     * @return the object
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected Object parseRef(Node node, String varName, String className) throws XPathExpressionException, ConfigurationException {
        Object referenceContent = null;
        String objReference = XPathHelper.getXpathExpressionValue(node, "@ref");
        if (objReference != null) {
            referenceContent = objects.get(objReference);
            if (referenceContent == null) {
                throw new ConfigurationException("Variable " + varName + " trying to reference " + objReference
                        + " which does not pre-exist for class: " + className);
            }
        }
        return referenceContent;
    }

    /**
     * Parses the object.
     *
     * @param node
     *           the node
     * @param varName
     *           the var name
     * @param className
     *           the class name
     *
     * @return the object
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws InstantiationException
     *            the instantiation exception
     * @throws IllegalAccessException
     *            the illegal access exception
     * @throws ClassNotFoundException
     *            the class not found exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected Object parseObject(Node node, String varName, String className) throws XPathExpressionException, InstantiationException,
            IllegalAccessException, ClassNotFoundException, ConfigurationException {
        if (node != null) {
            Object refObject = parseRef(node, varName, className);
            if (refObject != null) {
                return refObject;
            } else {
                return createObject(node);
            }
        }
        return null;
    }

    /**
     * Determines if one class can be set from another
     *
     * @param classTarget
     *           the class target
     * @param classFrom
     *           the class from
     *
     * @return true, if successful
     */
    protected static boolean canAssign(Class<?> classTarget, Class<?> classFrom) {
        if (classTarget.isAssignableFrom(classFrom)) {
            return true;
        }

        String typeTarget = TypesHelper.determineSwitcher(classTarget.getName());
        String typeFrom = TypesHelper.determineSwitcher(classFrom.getName());

        // Do not allow TYPE_OBJECT to be assignable - any matches would've been handled by isAssignableFrom()
        if (typeTarget.equals(TypesHelper.TYPE_OBJECT) || typeFrom.equals(TypesHelper.TYPE_OBJECT)) {
            return false;
        }

        if (typeTarget.equals(typeFrom)) {
            return true;
        }

        return false;
    }

    /**
     * Sets the value of an objects member (via reflection)
     *
     * @param method
     *           the method to run
     * @param object
     *           the object on which to run the method
     * @param value
     *           the value of the method parameter
     * @param typeConversion
     *           the type conversion
     *
     * @return true, if successful
     *
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected boolean setValue(Method method, Object object, Object value, String typeConversion) throws ConfigurationException {
        try {
            Object o = value;
            if (value instanceof String) {
                o = TypesHelper.performSwitch((String) value, typeConversion);
            }

            Class<?> paramType = method.getParameterTypes()[0];

            // ensure we can do the assignment
            if (canAssign(paramType, o.getClass())) {
                method.invoke(object, o);
                return true;
            } else {
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            String error = e.getClass().getName() + " " + e.getMessage() + " Problem invoking " + object.getClass().getName() + "."
                    + method.getName() + "() with value [" + value + "] of type " + value.getClass().getName() + " conversion: "
                    + typeConversion;
            logger.error(error);
            throw new ConfigurationException(error);
        }
    }

    /**
     * Find method.
     *
     * @param className
     *           the class name
     * @param methodName
     *           the method name
     *
     * @return the method
     *
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected Method findMethod(String className, String methodName, Object[] params) throws ConfigurationException {
        try {
            if ((params == null) || (params.length == 0)) {
                return Class.forName(className).getMethod(methodName);
            } else {
                Method[] methods = Class.forName(className).getMethods();
                for (Method checkMethod : methods) {
                    if (checkMethod.getName().equals(methodName)) {
                        Class<?>[] paramTypes = checkMethod.getParameterTypes();
                        if (paramTypes.length != params.length) {
                            continue;
                        }

                        boolean match = true;
                        for (int i = 0; i < paramTypes.length; i++) {
                            if (!canAssign(paramTypes[i], params[i].getClass())) {
                                match = false;
                                break;
                            }
                        }

                        if (match) {
                            return checkMethod;
                        } else {
                            continue;
                        }
                    }
                }
            }

            StringBuilder sbError = new StringBuilder();
            sbError.append(this.getClass().getName()).append(" for method: ").append(methodName).append(" on class: ").append(className).append(" param types: ");
            for (Object param : params) {
                sbError.append(param.getClass().getName()).append(" ");
            }
            throw new ConfigurationException(sbError.toString());
        } catch (ConfigurationException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConfigurationException(e.getClass().getName() + " for method: " + methodName + " on class: " + className + " " + e.getMessage());
        }
    }

    /**
     * get nodes for both no env and matching env
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected void attrNotAllowed(Node node, String attr, String kind) throws XPathExpressionException, ConfigurationException {
        String value = XPathHelper.getXpathExpressionValue(node, "@" + attr);
        if (value != null) {
            throw new ConfigurationException(kind + " node may not have " + attr + " specified.");
        }
    }

    /**
     * get nodes for both no env and matching env
     *
     * @throws XPathExpressionException
     *            the x path expression exception
     * @throws ConfigurationException
     *            the configuration exception
     */
    protected Map<String, Node> getEnvNodes(NodeList nodeList, String kind) throws XPathExpressionException, ConfigurationException {
        Map<String, Node> nodes = new TreeMap<String, Node>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            
            // only process the kind of node we are getting
            if (!kind.equals(node.getNodeName()))
            	continue;

            String id = XPathHelper.getXpathExpressionValue(node, "@id");
            if (id == null) {
                throw new ConfigurationException("id for " + kind + " is not specified");
            }

            String nodeEnv = XPathHelper.getXpathExpressionValue(node, "@env");
            Node oldNode = nodes.get(id);

            if (oldNode == null) {
                nodes.put(id, node);
                continue;
            }

            // error if node redefined with same env
            String oldNodeEnv = XPathHelper.getXpathExpressionValue(oldNode, "@env");

            if (nodeEnv == null) {
                nodeEnv = "";
            }
            if (oldNodeEnv == null) {
                oldNodeEnv = "";
            }
            if (oldNodeEnv.equals(nodeEnv)) {
                throw new ConfigurationException("Config already has " + kind + " id: " + id);
            }

            // prefer the env= node
            if (nodeEnv != null) {
                nodes.put(id, node);
            }
        }

        return nodes;
    }
}
