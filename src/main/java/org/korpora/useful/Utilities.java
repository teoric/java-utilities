package org.korpora.useful;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BinaryOperator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Some collected utilities
 * 
 * @author bfi
 */
public final class Utilities {
	/**
	 * Strip space from String
	 * @param s
	 *   an innocent String
	 * @return the stripped s
	 */
	@Deprecated
	public static String stripSpace(String s){
		return s.replaceFirst("^\\p{Space}+",  "")
				   .replaceFirst("\\p{Space}+$",  "");
	}

	private static Pattern nonEmptyPattern = Pattern.compile("\\P{Space}");
	/**
	 * Determine if String is non-empty
	 * @param s
	 *   an innocent string
	 * @return whether s is empty (contains only space)
	 */
	@Deprecated
	public static boolean isEmpty(String s){
		return ! nonEmptyPattern.matcher(s).find();
	}
	
	/**
	 * Make Array from NodeList
	 * @param list
	 *   a DOM NodeList
	 * @return
	 *   a corresponding Array
	 */
	public static Node[] toArray(NodeList list) {
		// from http://www.java2s.com/Code/Java/XML/ConvertNodeListToNodeArray.htm
		if (list == null){
			return null;
		}
	    int length = list.getLength();
	    Node[] copy = new Node[length];
	    for (int n = 0; n < length; ++n)
	        copy[n] = list.item(n);
	    return copy;
	}
	
	/**
	 * Make List from NodeList
	 * @param list
	 *   a DOM NodeList
	 * @return
	 *   a corresponding List
	 */
	public static List<Node> toList(NodeList list) {
		Node[] arr = toArray(list); 
		if (list == null){
			return null;
		}
		return Arrays.asList(arr);
	}

    public static Stream<Node> toStream(NodeList list){
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                    new NodeListIterable(list).iterator(),
                    Spliterator.DISTINCT |
                    Spliterator.IMMUTABLE |
                    Spliterator.NONNULL), false);
    }

    public static Iterator<Node> toIterator(NodeList list){
	    return new NodeListIterable(list).iterator();
	}
	
	/**
	 * Make a HashMap with attributes from Element
	 * @param el
	 *   a DOM Element
	 * @return a HashMap containing {@code el}'s attribute-value pairs
	 */
	public static HashMap<String, String> attributeMap(Element el){
		NamedNodeMap amap = el.getAttributes();
		HashMap<String, String> attr = new HashMap<>();
		for (int i=0; i < amap.getLength(); i++){
			Node n = amap.item(i);
			attr.put(n.getNodeName(), n.getNodeValue());
		}
		return attr;
	}

	public static List<String> attributeList(Element el){
		NamedNodeMap amap = el.getAttributes();
		List<String> attr = new ArrayList<>();
		for (int i=0; i < amap.getLength(); i++){
			Node n = amap.item(i);
			attr.add(n.getNodeName());
		}
		return attr;
	}
	
	public static final BinaryOperator<String> strCollider =
			(u, v) -> {
				throw new IllegalStateException(String.format("Duplicate key «%s»", u));
	};

	public static final BinaryOperator<Integer> intCollider =
			(u, v) -> {
				throw new IllegalStateException(String.format("Duplicate key «%s»", u.toString()));
	};

}
