/*
MIT License

Copyright (c) 2016 Thomas Weitzel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.synformation.boundary;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * A simple Way that has an ordered list of nodes.
 *
 * This implementation uses the first and the last node of this list as criteria for equality.
 * Feel free to change it if you wish.
 */
public class Way {

    private final List<Node> nodes;

    public Way(final List<Node> nodes) {
        Preconditions.checkNotNull(nodes, "Argument 'nodes' should not be null");
        Preconditions.checkArgument(nodes.size() > 1, "There should be at least two (2) nodes in a way");
        this.nodes = nodes;
    }

    public Node getStartNode() {
        return nodes.get(0);
    }

    public Node getEndNode() {
        return nodes.get(nodes.size() - 1);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Way)) {
            return false;
        }
        Way otherWay = (Way) obj;
        return getStartNode().equals(otherWay.getStartNode()) && getEndNode().equals(otherWay.getEndNode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartNode().hashCode(), getEndNode().hashCode());
    }

}
