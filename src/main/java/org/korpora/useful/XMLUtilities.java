package org.korpora.useful;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.DOMOutputter;
import org.jdom2.util.IteratorIterable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * utility class for handling XML documents with (j)DOM
 *
 * @author bfi
 *
 */
public class XMLUtilities {
    private XMLUtilities() {
    }

    /**
     * Make a {@link HashMap} with attributes from a DOM {@link Element} node
     *
     * @param el
     *     a DOM {@link Element}
     * @return a {@link HashMap} containing {@code el}'s attribute-value pairs
     */
    public static Map<String, String> attributeMap(Element el) {
        if (el == null) {
            throw new IllegalArgumentException();
        }
        NamedNodeMap attributeMap = el.getAttributes();
        HashMap<String, String> attributes = new HashMap<>();
        for (int i = 0; i < attributeMap.getLength(); i++) {
            Node n = attributeMap.item(i);
            attributes.put(n.getNodeName(), n.getNodeValue());
        }
        return attributes;
    }

    /**
     * make list of attribute Names
     *
     * @param el
     *     – an DOM {@link Element} {@link Node}
     * @return list of attribute names
     */
    public static List<String> attributeList(Element el) {
        if (el == null) {
            throw new IllegalArgumentException();
        }
        NamedNodeMap attributeMap = el.getAttributes();
        List<String> attributes = new ArrayList<>();
        for (int i = 0; i < attributeMap.getLength(); i++) {
            Node n = attributeMap.item(i);
            attributes.add(n.getNodeName());
        }
        return attributes;
    }

    /**
     * convert DOM {@link Document} to JDOM {@link org.jdom2.Document}
     *
     * @param input
     *     DOM document
     * @return JDOM document
     */
    public static org.jdom2.Document convertDOMtoJDOM(
            org.w3c.dom.Document input) {
        DOMBuilder builder = new DOMBuilder();
        return builder.build(input);
    }

    /**
     * convert JDOM document to DOM {@link Document}
     *
     * @param jdomDoc
     *     JDOM document
     * @return DOM document
     * @throws JDOMException
     *     on occasion
     */
    public static org.w3c.dom.Document convertJDOMToDOM(
            org.jdom2.Document jdomDoc) throws JDOMException {

        DOMOutputter outPutter = new DOMOutputter();
        return outPutter.output(jdomDoc);
    }

    /**
     * remove all children of XML DOM {@link Element}
     *
     * @param el
     *     XML DOM {@link Element}
     * @return cleaned XML DOM {@link Element}
     */
    public static Element cleanElement(Element el) {
        while (el.getFirstChild() != null) {
            el.removeChild(el.getFirstChild());
        }
        return el;
    }

    /**
     * convert XML DOM {@link Document} to {@link String} representation
     * including an XML declaration.
     *
     * @param doc
     *     the XML document
     * @param indent
     *     whether to indent
     * @return string representation
     */
    public static String documentToString(Document doc, boolean indent) {
        return elementToString(doc.getDocumentElement(), indent, true);
    }

    /**
     * convert XML DOM {@link Document} to {@link String} representation
     *
     * @param doc
     *     the XML document
     * @param indent
     *     whether to indent
     * @param declaration
     *     whether to output an XML declaration
     * @return string representation
     */
    public static String documentToString(Document doc, boolean indent,
            boolean declaration) {
        return elementToString(doc.getDocumentElement(), indent, declaration);
    }

    /**
     * convert XML DOM {@link Element} to {@link String} representation
     *
     * @param el
     *     the XML element
     * @param indent
     *     whether to indent
     * @param declaration
     *     whether to output an XML declaration
     * @return string representation
     */
    public static String elementToString(Element el, boolean indent,
            boolean declaration) {
        TransformerFactory stf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = stf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT,
                    indent ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                    declaration ? "yes" : "no");
            DOMSource source = new DOMSource(el);
            StringWriter outStream = new StringWriter();
            StreamResult out = new StreamResult(outStream);
            transformer.transform(source, out);
            return outStream.toString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * convert XML DOM {@link Element} to {@link String} representation without
     * XML declaration
     *
     * @param el
     *     the XML element
     * @param indent
     *     whether to indent
     * @return string representation
     */
    public static String elementToString(Element el, boolean indent) {
        return elementToString(el, indent, false);
    }

    /**
     * convert XML DOM {@link Element} to {@link String} representation without
     * XML declaration and indentation
     *
     * @param el
     *     the XML element
     * @return string representation
     */
    public static String elementToString(Element el) {
        return elementToString(el, false, false);
    }

    /**
     * make a string representation for an XML element (JDOM version)
     *
     * @param element
     *     the {@link org.jdom2.Element}
     * @return the {@link String} representation
     */
    public static String elementToString(org.jdom2.Element element) {
        return elementToString(element, false);
    }

    /**
     * make a string representation for an XML element (JDOM version)
     *
     * @param element
     *     the {@link org.jdom2.Element}
     * @param prettyPrint
     *     whether to pretty-print
     * @return the {@link String} representation
     */
    public static String elementToString(org.jdom2.Element element,
            boolean prettyPrint) {
        org.jdom2.output.XMLOutputter xmlOutputter = new org.jdom2.output.XMLOutputter();
        if (prettyPrint) {
            xmlOutputter.setFormat(org.jdom2.output.Format.getPrettyFormat());
        }
        return xmlOutputter.outputString(element);
    }

    /**
     * deeply search for Element in given DOM Document
     *
     * @param doc
     *     the parent
     * @param name
     *     the sought tag name
     * @param nameSpace
     *     the namespace
     * @return the first matching element or null
     */
    public static Element getElementByTagNameNS(Document doc, String nameSpace,
            String name) {
        return getElementByTagNameNS(doc.getDocumentElement(), nameSpace, name);
    }

    /**
     * deeply search for Element in given DOM Element
     *
     * @param el
     *     the parent
     * @param tagName
     *     the sought tag name
     * @param nameSpace
     *     the namespace
     * @return the first matching element or null
     */
    public static Element getElementByTagNameNS(Element el, String nameSpace,
            String tagName) {
        Element element = null;
        NodeList elements = el.getElementsByTagNameNS(nameSpace, tagName);
        if (elements.getLength() > 0) {
            element = (Element) elements.item(0);
        }
        return element;
    }

    /**
     * get an XML DOM {@link Element} by ID, using any attribute with local name
     * "id"
     *
     * @param doc
     *     XML DOM {@link Document}
     * @param id
     *     the ID
     * @return the Element
     */
    public static Element getElementByID(Document doc, String id) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String idXPath = "//*[@*[local-name() = 'id' and namespace-uri() = '"
                + "http://www.w3.org/XML/1998/namespace" + "' and string() = '"
                + id + "']]";
        String idXXPath = "//*[@*[local-name() = 'id'" + " and string() = '"
                + id + "']]";
        try {
            NodeList res = (NodeList) xPath.compile(idXPath).evaluate(doc,
                    XPathConstants.NODESET);
            if (res.getLength() > 1)
                throw new RuntimeException("ambiguous ID " + id);
            else if (res.getLength() == 1)
                return (Element) res.item(0);
            else {
                res = (NodeList) xPath.compile(idXXPath).evaluate(doc,
                        XPathConstants.NODESET);
                if (res.getLength() > 1)
                    throw new RuntimeException("ambiguous ID " + id);
                else
                    return (Element) res.item(0);
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException("XPATH ERROR");
        }

    }

    /**
     * deeply search for XML DOM {@link Element} in given DOM {@link Element}
     *
     * @param doc
     *     the parent XML DOM {@link Document}
     * @param tagName
     *     the sought-after tag name
     * @return the first matching {@link Element} or null
     */
    public static Element getElementByTagName(Document doc, String tagName) {
        return getElementByTagName(doc.getDocumentElement(), tagName);
    }

    /**
     * deeply search for XML DOM {@link Element} in given DOM {@link Element}
     *
     * @param el
     *     the parent XML DOM {@link Document}
     * @param tagName
     *     the sought-after tag name
     * @return the first matching {@link Element} or null
     */
    public static Element getElementByTagName(Element el, String tagName) {
        Element element = null;
        NodeList elements = el.getElementsByTagName(tagName);
        if (elements.getLength() > 0) {
            element = (Element) elements.item(0);
        }
        return element;
    }

    /**
     * deeply search for {@link org.jdom2.Element} in given JDOM2
     * {@link org.jdom2.Element}
     *
     * @param el
     *     the parent {@link org.jdom2.Element}
     * @param tagName
     *     the sought tag name
     * @param ns
     *     the namespace (or null)
     * @return the first matching {@link org.jdom2.Element} or null
     */
    public static org.jdom2.Element getElementByTagName(org.jdom2.Element el,
            String tagName, Namespace ns) {
        org.jdom2.Element element = null;
        IteratorIterable<org.jdom2.Element> elements = el
                .getDescendants(new ElementFilter(tagName, ns));
        if (elements.hasNext()) {
            element = elements.next();
        }
        return element;
    }

    /**
     * deeply search for {@link org.jdom2.Element} in given JDOM2
     * {@link org.jdom2.Element}
     *
     * @param el
     *     the parent {@link org.jdom2.Element}
     * @param tagName
     *     the sought tag name
     * @return the first matching {@link org.jdom2.Element} or null
     */
    public static org.jdom2.Element getElementByTagName(org.jdom2.Element el,
            String tagName) {
        return getElementByTagName(el, tagName, null);
    }

    /**
     * Make Array from {@link NodeList}
     *
     * @param list
     *     a DOM NodeList
     * @return a corresponding Array
     * @see #toIterator(NodeList)
     * @deprecated – often a {@link NodeListIterable.NodeListIterator} works
     * just as well
     */
    @Deprecated
    public static Node[] toArray(NodeList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        // from
        // http://www.java2s.com/Code/Java/XML/ConvertNodeListToNodeArray.htm
        int length = list.getLength();
        Node[] copy = new Node[length];
        for (int n = 0; n < length; ++n)
            copy[n] = list.item(n);
        return copy;
    }

    /**
     * Make {@link List} of {@link Node}s from {@link NodeList}
     *
     * @param list
     *     a DOM NodeList
     * @return a corresponding List
     * @see #toIterator(NodeList)
     */
    @Deprecated
    public static List<Node> toList(NodeList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        return XMLUtilities.toStream(list).collect(Collectors.toList());
    }

    /**
     * make Java 8+ {@link Stream} of a {@link NodeList}
     *
     * @param list
     *     – the {@link NodeList}
     * @return the {@link Stream}{@code <}{@link Node}{@code >}
     */
    public static Stream<Node> toStream(NodeList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        return StreamSupport.stream(Spliterators
                .spliteratorUnknownSize(new NodeListIterable(list).iterator(),
                        Spliterator.DISTINCT
                                // | Spliterator.IMMUTABLE
                                // | Spliterator.ORDERED
                                | Spliterator.NONNULL),
                false);
    }

    /**
     * Make {@link Stream} of {@link Element}s from {@link NodeList}
     *
     * @param list
     *     a DOM NodeList
     * @return a corresponding List of Elements
     * @see #toIterator(NodeList)
     */
    public static Stream<Element> toElementStream(NodeList list) {
        return XMLUtilities.toStream(list).map(w -> (Element) w);
    }

    /**
     * Make {@link List} of {@link Element}s from {@link NodeList}
     *
     * @param list
     *     a DOM NodeList
     * @return a corresponding List of Elements
     * @see #toIterator(NodeList)
     */
    public static List<Element> toElementList(NodeList list) {
        return XMLUtilities.toStream(list).map(w -> (Element) w)
                .collect(Collectors.toList());
    }

    /**
     * make an {@link Iterator} of a {@link NodeList}
     *
     * @param list
     *     – the {@link NodeList}
     * @return the {@link Iterator}
     */
    public static Iterator<Node> toIterator(NodeList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        return new NodeListIterable(list).iterator();
    }

    /**
     * insert a {@link Node} as first child of an {@link Element}
     *
     * @param el
     *     the element
     * @param n
     *     the node
     */
    public static void insertAtBeginningOf(Node n, Element el) {
        Node first = el.getFirstChild();
        if (first != null) {
            el.insertBefore(n, first);
        } else {
            el.appendChild(n);
        }
    }

    /**
     * insert a {@link Node} immediately before an {@link Element}
     *
     * @param n
     *     the node
     * @param el
     *     the Element
     */
    public static void insertBeforeMe(Node n, Element el) {
        el.getParentNode().insertBefore(n, el);
    }

    /**
     * insert a {@link Node} immediately after an {@link Element}
     *
     * @param n
     *     the node
     * @param el
     *     the Element
     */
    public static void insertAfterMe(Node n, Element el) {
        Element par = (Element) el.getParentNode();
        Node nextSibling = el.getNextSibling();
        assert n != null;
        if (nextSibling == null)
            par.appendChild(n);
        else
            par.insertBefore(n, nextSibling);
    }

    /**
     * output an XML DOM {@link Document}
     *
     * @param outStream
     *     an OutputStream
     * @param doc
     *     a XML DOM document
     * @param indent
     *     whether to indent the file
     */
    public static void outputXML(OutputStream outStream, Document doc,
            boolean indent) {
        outputXML(outStream, doc.getDocumentElement(), indent);
    }

    /**
     * output an XML DOM {@link Element}
     *
     * @param outStream
     *     an {@link OutputStream}
     * @param el
     *     a XML DOM {@link Element}
     * @param indent
     *     whether to indent the output
     */
    public static void outputXML(OutputStream outStream, Element el,
            boolean indent) {
        TransformerFactory stf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = stf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT,
                    indent ? "yes" : "no");
            DOMSource source = new DOMSource(el);
            StreamResult out = new StreamResult(outStream);
            transformer.transform(source, out);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * parse XML document from {@link InputSource} to a DOM Document
     *
     * @param input
     *     contains a document
     * @return a DOM document
     * @throws ParserConfigurationException
     *     on occasion
     * @throws SAXException
     *     on occasion
     * @throws IOException
     *     on occasion
     */
    public static Document parseXML(InputSource input)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        return builder.parse(input);
    }

    /**
     * parse XML to DOM from {@link InputStream}
     *
     * @param input
     *     some XML {@link InputStream}
     * @return DOM document
     * @throws ParserConfigurationException
     *     in case of problems
     * @throws SAXException
     *     in case of problems
     * @throws IOException
     *     in case of problems
     */
    public static Document parseXML(InputStream input)
            throws ParserConfigurationException, SAXException, IOException {
        return parseXML(new InputSource(input));
    }

    /**
     * * parse XML to DOM from {@link File}
     *
     * @param input
     *     XML {@link File}
     * @return DOM document
     * @throws ParserConfigurationException
     *     in case of problems
     * @throws SAXException
     *     in case of problems
     * @throws IOException
     *     in case of problems
     */
    public static Document parseXML(File input)
            throws ParserConfigurationException, SAXException, IOException {
        return parseXML(new InputSource(
                new BOMInputStream(new FileInputStream(input))));
    }

    /**
     * * * parse XML to DOM from {@link Path}
     *
     * @param input
     *     {@link Path} to XML file
     * @return DOM document
     * @throws ParserConfigurationException
     *     in case of problems
     * @throws SAXException
     *     in case of problems
     * @throws IOException
     *     in case of problems
     */
    public static Document parseXML(Path input)
            throws ParserConfigurationException, SAXException, IOException {
        return parseXML(input.toFile());
    }

    /**
     * * parse XML to DOM from {@link String}
     *
     * @param input
     *     {@link String} containing XML
     * @return DOM {@link Document}
     * @throws ParserConfigurationException
     *     in case of problems
     * @throws SAXException
     *     in case of problems
     * @throws IOException
     *     in case of problems
     */
    public static Document parseXML(String input)
            throws ParserConfigurationException, SAXException, IOException {
        return parseXML(new InputSource(new StringReader(input)));
    }

    /**
     * parse XML document from {@link InputSource} to JDOM
     * {@link org.jdom2.Document}
     *
     * @param input
     *     {@link InputSource} containing a document
     * @return a JDOM {@link org.jdom2.Document}
     * @throws JDOMException
     *     on occasion
     * @throws IOException
     *     on occasion
     */
    public static org.jdom2.Document parseXMLviaJDOM(InputSource input)
            throws JDOMException, IOException {
        org.jdom2.input.SAXBuilder saxBuilder = new org.jdom2.input.SAXBuilder();
        return saxBuilder.build(input);
    }

    /**
     * parse XML document from {@link InputStream} to {@link org.jdom2.Document}
     *
     * @param input
     *     {@link InputStream} containing XML
     * @return JDOM {@link org.jdom2.Document}
     * @throws JDOMException
     *     in case of problems
     * @throws IOException
     *     in case of problems
     */
    public static org.jdom2.Document parseXMLviaJDOM(InputStream input)
            throws JDOMException, IOException {
        return parseXMLviaJDOM(new InputSource(input));
    }

    /**
     * parse XML document from {@link String} to JDOM {@link org.jdom2.Document}
     *
     * @param input
     *     {@link File} containing XML
     * @return JDOM {@link org.jdom2.Document}
     * @throws JDOMException
     *     in case of problems
     * @throws IOException
     *     in case of problems
     */
    public static org.jdom2.Document parseXMLviaJDOM(File input)
            throws JDOMException, IOException {
        return parseXMLviaJDOM(new InputSource(
                new BOMInputStream(new FileInputStream(input))));
    }

    /**
     * parse XML document from {@link Path} to JDOM {@link org.jdom2.Document}
     *
     * @param input
     *     {@link Path} to XML document
     * @return JDOM {@link org.jdom2.Document}
     * @throws JDOMException
     *     in case of problems
     * @throws IOException
     *     in case of problems
     */
    public static org.jdom2.Document parseXMLviaJDOM(Path input)
            throws JDOMException, IOException {
        return parseXMLviaJDOM(input.toFile());
    }

    /**
     * parse XML document from {@link String} to JDOM {@link org.jdom2.Document}
     *
     * @param doc
     *     {@link String} containing XML
     * @return JDOM {@link org.jdom2.Document}
     * @throws JDOMException
     *     on occasion
     * @throws IOException
     *     on occasion
     */
    public static org.jdom2.Document readJDOMFromString(String doc)
            throws JDOMException, IOException {
        org.jdom2.input.SAXBuilder saxBuilder = new org.jdom2.input.SAXBuilder();
        java.io.StringReader sr = new java.io.StringReader(doc);
        return saxBuilder.build(sr);
    }

    /**
     * make a {@link org.jdom2.Content} list from XML text
     *
     * @param tx
     *     the text
     * @return the list of JDOM2 XML {@link org.jdom2.Content}
     */
    public static List<org.jdom2.Content> makeContentList(String tx) {
        List<org.jdom2.Content> ret;
        try {
            ret = readJDOMFromString("<X>" + tx + "</X>").getRootElement()
                    .removeContent();
        } catch (JDOMException | IOException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * replace the content of an XML DOM {@link org.jdom2.Element} with the XML
     * Content list resulting from a parse of some text
     *
     * @param el
     *     the XML DOM {@link org.jdom2.Element}
     * @param tx
     *     the XML text to replace the content of el
     */
    public static void replaceContentWithParse(org.jdom2.Element el,
            String tx) {
        el.removeContent();
        el.setContent(makeContentList(tx));
    }

}
