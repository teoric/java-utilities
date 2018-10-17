package org.korpora.useful;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BinaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.validation.constraints.NotNull;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Some collected utilities
 *
 * @author Bernhard Fisseni <bernhard.fisseni@uni-due.de>
 */
public class Utilities {
	/**
	 * Strip space from String – Unicode-aware.
	 *
	 * @deprecated since Java 11, use #{@link String#strip}.  If sure Unicode does not matter, use #{@link String#trim()}.
	 * @param s
	 *   an innocent String
	 * @return the stripped s
	 */
	@Deprecated
	public static String stripSpace(@NotNull String s){
		return s.replaceFirst("^\\p{Z}+",  "")
				   .replaceFirst("\\p{Z}+$",  "");
	}

	private static Pattern nonEmptyPattern = Pattern.compile("\\P{Space}");
	/**
	 * Determine if String is non-empty – use String.isEmpty()
	 * @deprecated since Java 1.6
	 * @param s
	 *   an innocent string
	 * @return whether s is empty (contains only space)
	 */
	@Deprecated
	public static boolean isEmpty(@NotNull String s){
		return ! nonEmptyPattern.matcher(s).find();
	}

	/**
	 * Make Array from {@link NodeList}
	 * @deprecated – often a {@link NodeListIterable.NodeListIterator} does as well
	 * @see #toIterator(NodeList)
	 * @param list
	 *   a DOM NodeList
	 * @return
	 *   a corresponding Array
	 */
	@Deprecated
    public static Node[] toArray(@NotNull NodeList list) {
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
	 * Make {@link List} of {@link Node}s from {@link NodeList}
     * @deprecated – often a {@link NodeListIterable.NodeListIterator} does as well
     * @see #toIterator(NodeList)
	 * @param list
	 *   a DOM NodeList
	 * @return
	 *   a corresponding List
	 */
	@Deprecated
    public static List<Node> toList(@NotNull NodeList list) {
		Node[] arr = toArray(list);
		if (list == null){
			return null;
		}
		return Arrays.asList(arr);
	}

	/**
	 * make Java 8+ {@link Stream} of a {@link NodeList}
	 * @param list – the {@link NodeList}
	 * @return the {@link Stream}{@code <Node>}
	 */
	public static Stream<Node> toStream(NodeList list){
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                    new NodeListIterable(list).iterator(),
                    Spliterator.DISTINCT |
                    Spliterator.IMMUTABLE |
                    Spliterator.NONNULL), false);
    }

	/**
	 * make an {@link Iterator} of a {@link NodeList}
	 * @param list – the {@link NodeList}
	 * @return the {@link Iterator}
	 */
    public static Iterator<Node> toIterator(@NotNull NodeList list){
	    return new NodeListIterable(list).iterator();
	}

	/**
	 * Make a {@link HashMap} with attributes from a DOM {@link Element} node
	 * @param el
	 *   a DOM {@link Element}
	 * @return a {@link HashMap} containing {@code el}'s attribute-value pairs
	 */
	public static HashMap<String, String> attributeMap(@NotNull Element el){
		NamedNodeMap amap = el.getAttributes();
		HashMap<String, String> attr = new HashMap<>();
		for (int i=0; i < amap.getLength(); i++){
			Node n = amap.item(i);
			attr.put(n.getNodeName(), n.getNodeValue());
		}
		return attr;
	}

	/**
	 * make list of attribute Names
	 * @param el – an DOM {@link Element} node
	 * @return list of attribute names
	 */
	public static List<String> attributeList(@NotNull Element el){
		NamedNodeMap amap = el.getAttributes();
		List<String> attr = new ArrayList<>();
		for (int i=0; i < amap.getLength(); i++){
			Node n = amap.item(i);
			attr.add(n.getNodeName());
		}
		return attr;
	}

	/**
	 * for use in third place of {@link java.util.stream.Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, java.util.function.Supplier)}.
	 * end merging if duplicate key found.
	 */
	public static final BinaryOperator<String> strCollider =
			(u, v) -> {
				throw new IllegalStateException(String.format("Duplicate key «%s»", u));
	};

    /**
     * for use in third place of {@link java.util.stream.Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, java.util.function.Supplier)}.
     * end merging if duplicate key found.
     */
	public static final BinaryOperator<Integer> intCollider =
			(u, v) -> {
				throw new IllegalStateException(String.format("Duplicate key «%s»", u.toString()));
	};

    /**
     * for use in third place of {@link java.util.stream.Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, java.util.function.Supplier)}.
     * end merging if duplicate key found.
     */
    public static final BinaryOperator<?> anyCollider =
            (u, v) -> {
                throw new IllegalStateException(String.format("Duplicate key «%s»", u.toString()));
    };

}
