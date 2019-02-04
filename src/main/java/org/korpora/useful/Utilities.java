package org.korpora.useful;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BinaryOperator;
import java.util.regex.Pattern;
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

import org.apache.commons.lang3.StringUtils;
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
import org.xml.sax.SAXException;

/**
 * Some collected utilities
 *
 * @author Bernhard Fisseni (bernhard.fisseni@uni-due.de)
 */
public class Utilities {

    // Java is crazy: \p{Z} does not work as intended
    private static final Pattern SPACE = Pattern
            .compile("[\\p{javaWhitespace}]+", Pattern.MULTILINE);
    private static final Pattern SPACE_START = Pattern
            .compile("\\A" + SPACE + "+", Pattern.MULTILINE);
    private static final Pattern SPACE_END = Pattern.compile("" + SPACE + "\\Z",
            Pattern.MULTILINE);

    /**
     * Strip space from String – Unicode-aware.
     *
     * since Java 11, use #{@link String}::strip. If sure Unicode does not
     * matter, use #{@link String#trim()}.
     *
     * @deprecated use #{@link StringUtils#strip(String)}
     * @param s
     *            an innocent String
     * @return the stripped s
     */
    @Deprecated
    public static String stripSpace(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        String ret = SPACE_START.matcher(s).replaceAll("");
        ret = SPACE_END.matcher(ret).replaceAll("");
        System.err.println("[" + s + " -> " + ret + "]");
        return ret;
    }

    /**
     * Remove space from String – Unicode-aware.
     *
     * @param s
     *            an innocent String
     * @return the stripped s
     */
    public static String removeSpace(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        return SPACE.matcher(s).replaceAll("");
    }

    private static final Pattern NON_EMPTY = Pattern.compile("\\P{Space}");

    /**
     * Determine if String is non-empty, i.e., contains non-white-space content
     *
     * @param s
     *            an innocent string
     * @return whether s is empty (contains only space)
     */
    public static boolean isEmpty(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        return !NON_EMPTY.matcher(s).find();
    }

    /**
     * Make Array from {@link NodeList}
     *
     * @deprecated – often a {@link NodeListIterable.NodeListIterator} works
     *             just as well
     * @see #toIterator(NodeList)
     * @param list
     *            a DOM NodeList
     * @return a corresponding Array
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
     * @see #toIterator(NodeList)
     * @param list
     *            a DOM NodeList
     * @return a corresponding List
     */
    @Deprecated
    public static List<Node> toList(NodeList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        return Utilities.toStream(list).collect(Collectors.toList());
    }

    /**
     * make Java 8+ {@link Stream} of a {@link NodeList}
     *
     * @param list
     *            – the {@link NodeList}
     * @return the {@link Stream}{@code <Node>}
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
     * @see #toIterator(NodeList)
     * @param list
     *            a DOM NodeList
     * @return a corresponding List of Elements
     */
    public static Stream<Element> toElementStream(NodeList list) {
        return Utilities.toStream(list).map(w -> (Element) w);
    }

    /**
     * Make {@link List} of {@link Element}s from {@link NodeList}
     *
     * @see #toIterator(NodeList)
     * @param list
     *            a DOM NodeList
     * @return a corresponding List of Elements
     */
    public static List<Element> toElementList(NodeList list) {
        return Utilities.toStream(list).map(w -> (Element) w)
                .collect(Collectors.toList());
    }

    /**
     * make an {@link Iterator} of a {@link NodeList}
     *
     * @param list
     *            – the {@link NodeList}
     * @return the {@link Iterator}
     */
    public static Iterator<Node> toIterator(NodeList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }
        return new NodeListIterable(list).iterator();
    }

    /**
     * Make a {@link HashMap} with attributes from a DOM {@link Element} node
     *
     * @param el
     *            a DOM {@link Element}
     * @return a {@link HashMap} containing {@code el}'s attribute-value pairs
     */
    public static HashMap<String, String> attributeMap(Element el) {
        if (el == null) {
            throw new IllegalArgumentException();
        }
        NamedNodeMap amap = el.getAttributes();
        HashMap<String, String> attr = new HashMap<>();
        for (int i = 0; i < amap.getLength(); i++) {
            Node n = amap.item(i);
            attr.put(n.getNodeName(), n.getNodeValue());
        }
        return attr;
    }

    /**
     * make list of attribute Names
     *
     * @param el
     *            – an DOM {@link Element} node
     * @return list of attribute names
     */
    public static List<String> attributeList(Element el) {
        if (el == null) {
            throw new IllegalArgumentException();
        }
        NamedNodeMap amap = el.getAttributes();
        List<String> attr = new ArrayList<>();
        for (int i = 0; i < amap.getLength(); i++) {
            Node n = amap.item(i);
            attr.add(n.getNodeName());
        }
        return attr;
    }

    /**
     * insert a node as first child of an element
     *
     * @param el
     *            the element
     * @param n
     *            the node
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
     * insert a node immediately before an Element
     *
     * @param n
     *            the node
     * @param el
     *            the Element
     */
    public static void insertBeforeMe(Node n, Element el) {
        el.getParentNode().insertBefore(n, el);
    }

    /**
     * for use in third place of
     * {@link java.util.stream.Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, java.util.function.Supplier)}.
     * end merging if duplicate key found.
     */
    public static final BinaryOperator<String> strCollider = (u, v) -> {
        throw new IllegalStateException(String.format("Duplicate key «%s»", u));
    };

    /**
     * for use in third place of
     * {@link java.util.stream.Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, java.util.function.Supplier)}.
     * end merging if duplicate key found.
     */
    public static final BinaryOperator<Integer> intCollider = (u, v) -> {
        throw new IllegalStateException(
                String.format("Duplicate key «%s»", u.toString()));
    };

    /**
     * for use in third place of
     * {@link java.util.stream.Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, java.util.function.Supplier)}.
     * end merging if duplicate key found.
     */
    public static final BinaryOperator<?> anyCollider = (u, v) -> {
        throw new IllegalStateException(
                String.format("Duplicate key «%s»", u.toString()));
    };

    /**
     * output an XML document
     *
     * @param outStream
     *            an OutputStream
     * @param doc
     *            a XML DOM document
     * @param indent
     *            whether to indent the file
     */
    public static void outputXML(OutputStream outStream, Document doc,
            boolean indent) {
        outputXML(outStream, doc.getDocumentElement(), indent);
    }

    /**
     * output an XML document
     *
     * @param outStream
     *            an OutputStream
     * @param el
     *            a XML DOM element
     * @param indent
     *            whether to indent the file
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
     * convert XML DOM document to String representation including an XML
     * declaration.
     *
     * @param doc
     *            the XML document
     * @param indent
     *            whether to indent
     * @return string representation
     */
    public static String documentToString(Document doc, boolean indent) {
        return elementToString(doc.getDocumentElement(), indent, true);
    }

    /**
     * convert XML DOM document to String representation
     *
     * @param doc
     *            the XML document
     * @param indent
     *            whether to indent
     * @param declaration
     *            whether to output an XML declaration
     * @return string representation
     */
    public static String documentToString(Document doc, boolean indent,
            boolean declaration) {
        return elementToString(doc.getDocumentElement(), indent, declaration);
    }

    /**
     * convert XML DOM element to String representation
     *
     * @param el
     *            the XML element
     * @param indent
     *            whether to indent
     * @param declaration
     *            whether to output an XML declaration
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
     * convert XML DOM element to String representation without XML declaration
     *
     * @param el
     *            the XML element
     * @param indent
     *            whether to indent
     * @return string representation
     */
    public static String elementToString(Element el, boolean indent) {
        return elementToString(el, indent, true);
    }

    /*
     * two convenience methods from
     * https://gist.github.com/sachin-handiekar/1346229
     */
    /**
     * convert DOM document to JDOM document
     *
     * @param input
     *            DOM document
     * @return JDOM document
     */
    public static org.jdom2.Document convertDOMtoJDOM(
            org.w3c.dom.Document input) {
        DOMBuilder builder = new DOMBuilder();
        org.jdom2.Document output = builder.build(input);
        return output;
    }

    /**
     * convert JDOM document to DOM document
     *
     * @param jdomDoc
     *            JDOM document
     * @return DOM document
     * @throws JDOMException
     *             on occasion
     */
    public static org.w3c.dom.Document convertJDOMToDOM(
            org.jdom2.Document jdomDoc) throws JDOMException {

        DOMOutputter outputter = new DOMOutputter();
        return outputter.output(jdomDoc);
    }

    /**
     * parse XML document from {@link InputStream} to a DOM Document
     *
     * @param input
     *            contains a document
     * @return a DOM document
     * @throws ParserConfigurationException
     *             on occasion
     * @throws SAXException
     *             on occasion
     * @throws IOException
     *             on occasion
     */
    public static Document parseXML(InputStream input)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        return builder.parse(input);
    }

    /**
     * parse XML document from {@link InputStream} to JDOM document
     *
     * @param input
     *            contains a document
     * @return a JDOM document on occasion
     * @throws JDOMException
     *             on occasion
     * @throws IOException
     *             on occasion
     */
    public static org.jdom2.Document parseXMLviaJDOM(InputStream input)
            throws JDOMException, IOException {
        org.jdom2.input.SAXBuilder saxBuilder = new org.jdom2.input.SAXBuilder();
        return saxBuilder.build(input);
    }

    /**
     * parse XML document from {@link String} to JDOM document
     *
     * @param docString
     *            the document content
     * @return JDOM document on occasion
     * @throws JDOMException
     *             on occasion
     * @throws IOException
     *             on occasion
     */
    public static org.jdom2.Document readJDOMFromString(String docString)
            throws JDOMException, IOException {
        org.jdom2.input.SAXBuilder saxBuilder = new org.jdom2.input.SAXBuilder();
        java.io.StringReader sr = new java.io.StringReader(docString);
        return saxBuilder.build(sr);
    }

    /**
     * make a string representation for an element (JDOM version)
     *
     * @param element
     *            the element
     * @return the string representation
     */
    public static String elementToString(org.jdom2.Element element) {
        return elementToString(element, false);
    }

    /**
     * make a string representation for an element (JDOM version)
     *
     * @param element
     *            the element
     * @param prettyPrint
     *            whether to pretty-print
     * @return the string representation
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
     * increase a counter in a Map
     *
     * @param <T>
     *            the type of the counted thing
     * @param map
     *            the map
     * @param key
     *            the counted thing
     */
    public static <T> void incCounter(Map<? super T, Integer> map, T key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    /**
     * make a Content list from XML text
     *
     * @param tx
     *            the text
     * @return the list of XML Content
     */
    public static List<org.jdom2.Content> makeContentList(String tx) {
        List<org.jdom2.Content> ret = new ArrayList<>();
        try {
            ret = readJDOMFromString("<X>" + tx + "</X>").getRootElement()
                    .removeContent();
        } catch (JDOMException | IOException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * replace the content of an element with the XML Content list resulting
     * from a parse of some text
     *
     * @param el
     *            the element
     * @param tx
     *            the XML text
     */
    public static void replaceContentWithParse(org.jdom2.Element el,
            String tx) {
        el.removeContent();
        el.setContent(makeContentList(tx));
    }

    /**
     * deeply search for Element in given DOM Document
     *
     * @param doc
     *            the parent
     * @param name
     *            the sought tag name
     * @param nameSpace
     *            the namespace
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
     *            the parent
     * @param name
     *            the sought tag name
     * @param nameSpace
     *            the namespace
     * @return the first matching element or null
     */
    public static Element getElementByTagNameNS(Element el, String nameSpace,
            String name) {
        Element element = null;
        NodeList elements = el.getElementsByTagNameNS(nameSpace, name);
        if (elements.getLength() > 0) {
            // System.err.println("FOUND: " + name);
            element = (Element) elements.item(0);
        }
        return element;
    }

    /**
     * deeply search for Element in given DOM Document
     *
     * @param doc
     *            the parent
     * @param name
     *            the sought tag name
     * @return the first matching element or null
     */
    public static Element getElementByTagName(Document doc, String name) {
        return getElementByTagName(doc.getDocumentElement(), name);
    }

    /**
     * deeply search for Element in given DOM Element
     *
     * @param el
     *            the parent
     * @param name
     *            the sought tag name
     * @return the first matching element or null
     */
    public static Element getElementByTagName(Element el, String name) {
        Element element = null;
        NodeList elements = el.getElementsByTagName(name);
        if (elements.getLength() > 0) {
            element = (Element) elements.item(0);
        }
        return element;
    }

    public static Element cleanElement(Element el) {
        while (el.getFirstChild() != null) {
            el.removeChild(el.getFirstChild());
        }
        return el;
    }

    /**
     * deeply search for Element in given JDOM2 Element
     *
     * @param el
     *            the parent
     * @param name
     *            the sought tag name
     * @param ns
     *            the namespace (or null)
     * @return the first matching element or null
     */
    public static org.jdom2.Element getElementByTagName(org.jdom2.Element el,
            String name, Namespace ns) {
        org.jdom2.Element element = null;
        IteratorIterable<org.jdom2.Element> elements = el
                .getDescendants(new ElementFilter(name, ns));
        if (elements.hasNext()) {
            element = elements.next();
        }
        return element;
    }

    /**
     * deeply search for Element in given JDOM2 Element
     *
     * @param el
     *            the parent
     * @param name
     *            the sought tag name
     * @return the first matching Element or null
     */
    public static org.jdom2.Element getElementByTagName(org.jdom2.Element el,
            String name) {
        return getElementByTagName(el, name, null);
    }

}
