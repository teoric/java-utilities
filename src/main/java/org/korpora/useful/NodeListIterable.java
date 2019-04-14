package org.korpora.useful;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;

/**
 * Make Iterable for NodeList
 */

@SuppressWarnings("WeakerAccess")
public class NodeListIterable implements Iterable<Node> {

    private NodeList list;

    class NodeListIterator implements Iterator<Node> {

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


    public NodeListIterable(NodeList nodes) {
        if (nodes == null) {
            throw new IllegalArgumentException();
        }
        list = nodes;
    }

    public int size() {
        return list.getLength();
    }

    public Node get(int i) {
        return (i < list.getLength()) ? list.item(i) : null;
    }

    @Override
    public Iterator<Node> iterator() {
        return new NodeListIterator();
    }

}
