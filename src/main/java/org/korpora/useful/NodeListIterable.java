package org.korpora.useful;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Make Iterable for NodeList
 */

@SuppressWarnings("WeakerAccess")
public class NodeListIterable implements Iterable<Node> {

    private NodeList list;

    /**
     * Make Iterable for NodeList
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

    public class NodeListIterator implements Iterator<Node> {

        int pos = -1;

        @Override
        public boolean hasNext() {
            return (pos + 1 < list.getLength());
        }

        @Override
        public Node next() {
            return (pos++ < list.getLength()) ? list.item(pos) : null;
        }

    }

}
