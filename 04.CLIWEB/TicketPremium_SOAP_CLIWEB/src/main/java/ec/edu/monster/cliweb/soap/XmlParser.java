package ec.edu.monster.cliweb.soap;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlParser {

    public static List<Map<String, String>> parseList(String xml, String tagName) {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            NodeList nodes = doc.getElementsByTagName(tagName);
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                Map<String, String> map = new HashMap<>();
                NodeList children = el.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    if (children.item(j) instanceof Element) {
                        Element child = (Element) children.item(j);
                        map.put(child.getTagName(), child.getTextContent());
                    }
                }
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String parseSingleValue(String xml, String tagName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            NodeList nodes = doc.getElementsByTagName(tagName);
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
