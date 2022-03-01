package org.korpora.useful;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An Iterable for NodeList
 */

@SuppressWarnings("WeakerAccess")
public class NodeListIterable implements Iterable<Node> {

    private final NodeList list;

    /**
     * Make an {@link Iterable} for NodeList
     *
     * @param nodes NodeList
     * @throws IllegalArgumentException in case of null NodeList
     */
    public NodeListIterable(NodeList nodes) {
        if (nodes == null) {
            throw new IllegalArgumentException();
        }
        list = nodes;
    }

    /**
     * get size of underlying NodeList
     *
     * @return size
     */
    public int size() {
        return list.getLength();
    }

    /**
     * get i^th element of underlying NodeList
     *
     * @param i 0-based index
     * @return i^th element
     */
    public Node get(int i) {
        return (i < list.getLength()) ? list.item(i) : null;
    }

    @Override
    public Iterator<Node> iterator() {
        return new NodeListIterator();
    }

    /**
     * an iterator for NodeLists
     *
     * @author bfi
     */
    public class NodeListIterator implements Iterator<Node> {

        int position = -1;

        @Override
        public boolean hasNext() {
            return (position + 1 < list.getLength());
        }

        @Override
        public Node next() {
            if (position++ < list.getLength())
                return list.item(position);
            else
                throw new NoSuchElementException();
        }

    }

}
