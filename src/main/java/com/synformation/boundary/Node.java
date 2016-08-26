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

import java.util.Objects;

/**
 * A simple Node that has a longitude and a latitude and an id.
 *
 * It overrides the equals method of Object by treating two nodes with the same value for the nodeId as equal.
 * You might want to change that by establishing that having the same longitude and latitude means equality.
 * In this case it might be more practical to use a limited number of digits.
 */
public class Node {

    private final String nodeId;
    private final double longitude;
    private final double latitude;

    public Node(final String nodeId, final double longitude, final double latitude) {
        Preconditions.checkNotNull(nodeId, "Argument 'nodeId' should not be null");
        this.nodeId = nodeId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getNodeId() {
        return nodeId;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Node)) {
            return false;
        }
        Node otherNode = (Node) obj;
        return this.nodeId.equals(otherNode.getNodeId());

        /*
        // Alternatively you could base equality on the equality of longitude and latitude; then you have to change hashCode(), too
        return this.longitude == otherNode.getLongitude() && this.getLatitude() == otherNode.getLatitude();
        */
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId());

        /*
        // Alternatively you could base the hash code on longitude and latitude
        return Objects.hash(getLongitude(), getLatitude());
        */
    }

}
