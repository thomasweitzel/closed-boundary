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
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class tries to build a closed boundary from a set of ways and calculates its orientation (clockwise or
 * counterclockwise).
 *
 * First the set of ways is used to build the closed boundary by starting with one way, finding its ending node and
 * using this ending node to find another way that either starts or ends with this node (the next way).
 * If the other way ends with this node, the way is reversed (the order of its nodes is turned around).
 * This process continues until there's either no other way left or there's no next way to use.
 *
 * The result is then validated and (if validation was successful) used to calculate the orientation of the resulting
 * closed boundary. This is done by trying to find up to four candidate nodes for the calculation of the determinant.
 * If at least three nodes are found, the determinant is calculated. The value of the determinant (> 0, 0, or < 0)
 * translates directly into the orientation of the closed boundary.
 *
 * This class works on a couple of preconditions: the ways have to form a closed boundary which must not have gaps.
 * At least four nodes are needed to calculate the orientation, and even then it might not be found if too many
 * nodes are on a straight line (collinear).
 */
public class ClosedBoundary {

    private final static Logger log = Logger.getLogger(ClosedBoundary.class);

    private final Set<Way> waySet;
    private final List<Way> wayList;
    private final double determinant;

    public ClosedBoundary(final Set<Way> waySet) {
        // Make a copy
        this.waySet = new LinkedHashSet<>(waySet);
        // Do the work
        this.wayList = buildClosedBoundary();
        this.determinant = calculateDeterminant();
    }

    // Get the determinant
    public double getDeterminant() {
        return determinant;
    }

    // If way list orientation is clockwise
    public boolean isClockwise() {
        return determinant < 0;
    }

    // If way list orientation is counterclockwise
    public boolean isCounterclockwise() {
        return determinant > 0;
    }

    // Get the way list
    public List<Way> getWayList() {
        return wayList;
    }

    // Get the reversed way list
    public List<Way> getReversedWayList() {
        List<Way> reversedListView = Lists.reverse(wayList);
        List<Way> newReversedList = reversedListView.stream().map(way -> new Way(reverseNodeList(way.getNodes()))).collect(Collectors.toCollection(LinkedList::new));
        return newReversedList;
    }

    // Try to build a closed boundary
    private List<Way> buildClosedBoundary() {
        List<Way> wayList = new LinkedList<>();

        if (!waySet.isEmpty()) {
            // Start with "first" way, doesn't matter where it is
            Way currentWay = waySet.iterator().next();
            wayList.add(currentWay);
            waySet.remove(currentWay);

            while (!waySet.isEmpty() && currentWay != null) {
                Node nextNode = currentWay.getEndNode();
                currentWay = findNextWayAndRemove(nextNode);
                // The content of currentWay might be null
                if (currentWay != null) {
                    wayList.add(currentWay);
                }
            }

            // Check if we have a closed boundary (start node of first way is end node of last way
            if (wayList.size() > 0) {
                Preconditions.checkArgument(wayList.get(0).getStartNode().equals(wayList.get(wayList.size() - 1).getEndNode()), "Boundary is NOT closed");
            }

            // Check is there're still ways left over
            if (!waySet.isEmpty()) {
                log.warn(String.format("There're still %d ways left over, we might have a 'Berlin (West)' problem", waySet.size()));
            }

            logWays(wayList);
        }
        return wayList;
    }

    // Helper method to find next way that starts or ends with the node; if found removes it from the set
    private Way findNextWayAndRemove(final Node node) {
        if (waySet.isEmpty()) {
            return null; // Nothing to find anymore
        }
        // First try to find start node
        Iterator<Way> iter = waySet.iterator();
        while (iter.hasNext()) {
            Way way = iter.next();
            if (way.getStartNode().equals(node)) {
                waySet.remove(way);
                return way; // We found the next way by start node
            }
        }
        // Ok, wasn't successful, try end node
        iter = waySet.iterator();
        while (iter.hasNext()) {
            Way way = iter.next();
            if (way.getEndNode().equals(node)) {
                waySet.remove(way);
                return new Way(reverseNodeList(way.getNodes())); // We found the next way by end node, so we have to turn the way around
            }
        }

        log.error("No next way found!");
        return null;
    }

    // Calculates determinant
    private double calculateDeterminant() {
        List<Node> detNodes = findDetNodes(wayList);
        double det = det(detNodes);
        logDeterminant(det);
        return det;
    }

    // Calculates determinant, see: https://en.wikipedia.org/wiki/Curve_orientation; negative -> clockwise, positive -> counterclockwise, 0 -> nodes are collinear
    private double det(final List<Node> nodes) {
        double det = 0;

        if (nodes != null && nodes.size() >= 3) {
            det = det(nodes.get(0), nodes.get(1), nodes.get(2));

            // We can try out more options if we have one more candidate node
            if (det == 0 && nodes.size() == 4) {
                det = det(nodes.get(1), nodes.get(2), nodes.get(3)); // Leave out node 0
                if (det == 0) {
                    det = det(nodes.get(0), nodes.get(2), nodes.get(3)); // Leave out node 1
                    if (det == 0) {
                        det = det(nodes.get(0), nodes.get(1), nodes.get(3)); // Leave out node 2
                    }
                }
            }
        }
        return det;
    }

    // Calculates determinant of three nodes
    private double det(final Node a, final Node b, final Node c) {
        return
                (b.getLongitude() - a.getLongitude()) * (c.getLatitude() - a.getLatitude()) -
                (c.getLongitude() - a.getLongitude()) * (b.getLatitude() - a.getLatitude());
    }

    // Logs the determinant
    private void logDeterminant(final double determinant) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("The determinant: %.2f -> %s", determinant, determinant > 0 ? "counterclockwise" : determinant < 0 ? "clockwise" : "collinear"));
        }
    }

    // Logs all ways in the list (in order)
    private void logWays(final List<Way> wayList) {
        if (log.isDebugEnabled()) {
            for (Way way : wayList) {
                log.debug(String.format("Way %s (%.2f/%.2f) -> %s (%.2f/%.2f)",
                        way.getStartNode().getNodeId(),
                        way.getStartNode().getLongitude(),
                        way.getStartNode().getLatitude(),
                        way.getEndNode().getNodeId(),
                        way.getEndNode().getLongitude(),
                        way.getEndNode().getLatitude()
                ));
            }
        }
    }

    // Logs all nodes in the list (in order)
    private void logDetNodes(final List<Node> nodeList) {
        if (log.isDebugEnabled()) {
            for (Node node : nodeList) {
                log.debug(String.format("Detnode %s (%.2f/%.2f)",
                        node.getNodeId(),
                        node.getLongitude(),
                        node.getLatitude()
                ));
            }
        }
    }

    // Reverses node list
    private List<Node> reverseNodeList(final List<Node> nodeList) {
        List<Node> reversedListView = Lists.reverse(nodeList);
        List<Node> newReversedList = new LinkedList<>();
        newReversedList.addAll(reversedListView);
        return newReversedList;
    }

    // Find determinant nodes (in order) for calculating determinant (we need at least three)
    private List<Node> findDetNodes(final List<Way> wayList) {
        // Collect all nodes into one list
        List<Node> allNodeList = new LinkedList<>();
        for (Way way : wayList) {
            allNodeList.addAll(way.getNodes());
        }

        List<Node> unorderedDetNodeList = new LinkedList<>();
        unorderedDetNodeList.add(getSmallestLongitudeNode(allNodeList));
        unorderedDetNodeList.add(getBiggestLongitudeNode(allNodeList));
        unorderedDetNodeList.add(getSmallestLatitudeNode(allNodeList));
        unorderedDetNodeList.add(getBiggestLatitudeNode(allNodeList));

        // Order the candidate nodes for the determinant calculation
        List<Node> orderedDetNodeList = new LinkedList<>();
        allNodeList.stream().filter(node -> unorderedDetNodeList.contains(node) && !orderedDetNodeList.contains(node)).forEach(orderedDetNodeList::add);

        logDetNodes(orderedDetNodeList);
        return orderedDetNodeList;
    }

    // Id of no node
    private static final String NO_ID = "NO_ID";

    // Get node with smallest longitude; if there're more than one, pick the one with the smallest latitude
    private Node getSmallestLongitudeNode(final List<Node> nodeList) {
        Node node = new Node(NO_ID, Double.MAX_VALUE, Double.MAX_VALUE);
        for (Node n : nodeList) {
            if (n.getLongitude() < node.getLongitude() || (n.getLongitude() == node.getLongitude() && n.getLatitude() < node.getLatitude())) {
                node = n;
            }
        }
        return node;
    }

    // Get node with biggest longitude; if there're more than one, pick the one with the biggest latitude
    private Node getBiggestLongitudeNode(final List<Node> nodeList) {
        Node node = new Node(NO_ID, -Double.MAX_VALUE, -Double.MAX_VALUE);
        for (Node n : nodeList) {
            if (n.getLongitude() > node.getLongitude() || (n.getLongitude() == node.getLongitude() && n.getLatitude() > node.getLatitude())) {
                node = n;
            }
        }
        return node;
    }

    // Get node with smallest latitude; if there're more than one, pick the one with the biggest longitude
    private Node getSmallestLatitudeNode(final List<Node> nodeList) {
        Node node = new Node(NO_ID, -Double.MAX_VALUE, Double.MAX_VALUE);
        for (Node n : nodeList) {
            if (n.getLatitude() < node.getLatitude() || (n.getLatitude() == node.getLatitude() && n.getLongitude() > node.getLongitude())) {
                node = n;
            }
        }
        return node;
    }

    // Get node with biggest latitude; if there're more than one, pick the one with the smallest longitude
    private Node getBiggestLatitudeNode(final List<Node> nodeList) {
        Node node = new Node(NO_ID, Double.MAX_VALUE, -Double.MAX_VALUE);
        for (Node n : nodeList) {
            if (n.getLatitude() > node.getLatitude() || (n.getLatitude() == node.getLatitude() && n.getLongitude() < node.getLongitude())) {
                node = n;
            }
        }
        return node;
    }

}
