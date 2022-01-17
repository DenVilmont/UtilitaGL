package com.main.java.Conf;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.Blob;
import java.util.UUID;

public class XmlUtil {
    public static String getStringFromBlob(Blob blob){
        try {
            byte[] blobData = blob.getBytes(1, (int) blob.length());
            String xml = new String(blobData, "utf-8");
            xml = xml.replaceAll("\t|\n", "");

            return xml;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Document getDocumentFromString(String stringXML){
        stringXML = stringXML.replaceAll("\t|\n", "");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(stringXML.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Node getNode(Document xmlDocument, String path){
        try {
            String[] pathArr = path.split("/");
            String xpathStr  = "";
            for (int i = 0; i < pathArr.length; i++) {
                if (pathArr[i].equals("*")){
                    xpathStr  += "/*";
                }else {
                    xpathStr  += "/*[local-name()='" + pathArr[i] + "']";
                }
            }
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xPath = xpathFactory.newXPath();
            XPathExpression xPathExpression = xPath.compile(xpathStr);
            return (Node) xPathExpression.evaluate(xmlDocument, XPathConstants.NODE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringFromDocument(Document xmlDocument) {
        try {
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(xmlDocument.getDocumentElement());

            trans.transform(source, result);
            String xmlString = sw.toString();
            return xmlString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getRandomGUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String xmlFormat(String input, int indent) {
        try {
            Document doc = getDocumentFromString(input);
            return getStringFromDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
