package org.korpora.useful;

import java.util.Iterator;

import javax.validation.constraints.NotNull;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Make Iterable for NodeList
 */

public class NodeListIterable implements Iterable<Node> {

    private NodeList list;

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


    public NodeListIterable(@NotNull NodeList nodes) {
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
