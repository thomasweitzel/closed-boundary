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

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * A simple set of tests for the logic. At the same time they show how to use the classes.
 *
 * First a set of ways is set up and then passed to the constructor. The building of the closed boundary either fails
 * with an exception (IllegalArgumentException) if the set of ways cannot be used to build a closed boundary, or not.
 *
 * In some cases there might be a warning (Berlin (West) problem) were we have a closed boundary but there're sill ways
 * left, possibly forming another closed boundary - or more than one.
 */
public class ClosedBoundaryTest {

    @Test
    public void testBoundaryHandCrafted() {
        Set<Way> waySet = new LinkedHashSet<>();
        waySet.add(new Way(Arrays.asList(new Node("4", 14, 11), new Node("1", 10, 13)))); // 4 -> 1
        waySet.add(new Way(Arrays.asList(new Node("4", 14, 11), new Node("2", 15,  4)))); // 4 -> 2
        waySet.add(new Way(Arrays.asList(new Node("5", 10,  3), new Node("2", 15,  4)))); // 5 -> 2
        waySet.add(new Way(Arrays.asList(new Node("5", 10,  3), new Node("8",  8,  1)))); // 5 -> 8
        waySet.add(new Way(Arrays.asList(new Node("6",  4,  3), new Node("8",  8,  1)))); // 6 -> 8
        waySet.add(new Way(Arrays.asList(new Node("6",  4,  3), new Node("3",  6,  5)))); // 6 -> 3
        waySet.add(new Way(Arrays.asList(new Node("7",  5, 11), new Node("1", 10, 13)))); // 7 -> 1
        waySet.add(new Way(Arrays.asList(new Node("7",  5, 11), new Node("3",  6,  5)))); // 7 -> 3

        ClosedBoundary closedBoundary = new ClosedBoundary(waySet);
        assertTrue(closedBoundary.getDeterminant() > 0);
        assertTrue(closedBoundary.isCounterclockwise());

        // Sanity check: what if we reverse the boundary?
        ClosedBoundary reversedClosedBoundary = new ClosedBoundary(new HashSet<>(closedBoundary.getReversedWayList()));
        assertTrue(reversedClosedBoundary.getDeterminant() < 0);
        assertTrue(reversedClosedBoundary.isClockwise());
    }

    @Test
    public void testBoundaryFirstQuadrantSquare() {
        Set<Way> waySet = new LinkedHashSet<>();
        waySet.add(new Way(Arrays.asList(new Node("100", 0, 0), new Node("101", 0, 1)))); // 100 -> 101
        waySet.add(new Way(Arrays.asList(new Node("101", 0, 1), new Node("102", 1, 1)))); // 101 -> 102
        waySet.add(new Way(Arrays.asList(new Node("102", 1, 1), new Node("103", 1, 0)))); // 102 -> 103
        waySet.add(new Way(Arrays.asList(new Node("103", 1, 0), new Node("100", 0, 0)))); // 103 -> 100

        ClosedBoundary closedBoundary = new ClosedBoundary(waySet);
        assertTrue(closedBoundary.getDeterminant() < 0);
        assertTrue(closedBoundary.isClockwise());

        // Sanity check: what if we reverse the boundary?
        ClosedBoundary reversedClosedBoundary = new ClosedBoundary(new HashSet<>(closedBoundary.getReversedWayList()));
        assertTrue(reversedClosedBoundary.getDeterminant() > 0);
        assertTrue(reversedClosedBoundary.isCounterclockwise());
    }

    @Test
    public void testBoundaryTriangle() {
        Set<Way> waySet = new LinkedHashSet<>();
        waySet.add(new Way(Arrays.asList(new Node("200", 0, 0), new Node("201", 0, 1)))); // 200 -> 201
        waySet.add(new Way(Arrays.asList(new Node("201", 0, 1), new Node("202", 1, 1)))); // 201 -> 202
        waySet.add(new Way(Arrays.asList(new Node("202", 1, 1), new Node("200", 0, 0)))); // 202 -> 203

        ClosedBoundary closedBoundary = new ClosedBoundary(waySet);
        assertTrue(closedBoundary.getDeterminant() < 0);
        assertTrue(closedBoundary.isClockwise());

        // Sanity check: what if we reverse the boundary?
        ClosedBoundary reversedClosedBoundary = new ClosedBoundary(new HashSet<>(closedBoundary.getReversedWayList()));
        assertTrue(reversedClosedBoundary.getDeterminant() > 0);
        assertTrue(reversedClosedBoundary.isCounterclockwise());
    }

    @Test
    public void testBoundaryCollinear() {
        Set<Way> waySet = new LinkedHashSet<>();
        waySet.add(new Way(Arrays.asList(new Node("300", 0, 0), new Node("301", 0, 1)))); // 300 -> 301
        waySet.add(new Way(Arrays.asList(new Node("301", 0, 1), new Node("302", 0, 2)))); // 301 -> 302
        waySet.add(new Way(Arrays.asList(new Node("302", 0, 2), new Node("303", 0, 1)))); // 302 -> 303
        waySet.add(new Way(Arrays.asList(new Node("303", 0, 1), new Node("300", 0, 0)))); // 303 -> 300

        ClosedBoundary closedBoundary = new ClosedBoundary(waySet);
        assertTrue(closedBoundary.getDeterminant() == 0);
        assertTrue(!closedBoundary.isClockwise() && !closedBoundary.isCounterclockwise());

        // Sanity check: what if we reverse the boundary?
        ClosedBoundary reversedClosedBoundary = new ClosedBoundary(new HashSet<>(closedBoundary.getReversedWayList()));
        assertTrue(reversedClosedBoundary.getDeterminant() == 0);
        assertTrue(!reversedClosedBoundary.isClockwise() && !closedBoundary.isCounterclockwise());
    }

    @Test
    public void testBoundarySquareAroundOrigin() {
        Set<Way> waySet = new LinkedHashSet<>();
        waySet.add(new Way(Arrays.asList(new Node("400", -1, -1), new Node("401",  1, -1)))); // 400 -> 401
        waySet.add(new Way(Arrays.asList(new Node("401",  1, -1), new Node("402",  1,  1)))); // 401 -> 402
        waySet.add(new Way(Arrays.asList(new Node("402",  1,  1), new Node("403", -1,  1)))); // 402 -> 403
        waySet.add(new Way(Arrays.asList(new Node("403", -1,  1), new Node("400", -1, -1)))); // 403 -> 400

        ClosedBoundary closedBoundary = new ClosedBoundary(waySet);
        assertTrue(closedBoundary.getDeterminant() > 0);
        assertTrue(closedBoundary.isCounterclockwise());

        // Sanity check: what if we reverse the boundary?
        ClosedBoundary reversedClosedBoundary = new ClosedBoundary(new HashSet<>(closedBoundary.getReversedWayList()));
        assertTrue(reversedClosedBoundary.getDeterminant() < 0);
        assertTrue(reversedClosedBoundary.isClockwise());
    }

    @Test
    public void testBoundaryTooShortCollinear() {
        Set<Way> waySet = new LinkedHashSet<>();
        waySet.add(new Way(Arrays.asList(new Node("500", -1, -1), new Node("501",  1,  1)))); // 500 -> 501
        waySet.add(new Way(Arrays.asList(new Node("501",  1,  1), new Node("500", -1, -1)))); // 501 -> 500

        ClosedBoundary closedBoundary = new ClosedBoundary(waySet);
        assertTrue(closedBoundary.getDeterminant() == 0);
        assertTrue(!closedBoundary.isClockwise() && !closedBoundary.isCounterclockwise());

        // Sanity check: what if we reverse the boundary?
        ClosedBoundary reversedClosedBoundary = new ClosedBoundary(new HashSet<>(closedBoundary.getReversedWayList()));
        assertTrue(reversedClosedBoundary.getDeterminant() == 0);
        assertTrue(!reversedClosedBoundary.isClockwise() && !closedBoundary.isCounterclockwise());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBoundaryNotClosed() {
        Set<Way> waySet = new LinkedHashSet<>();
        waySet.add(new Way(Arrays.asList(new Node("600", -1, -1), new Node("601",  1, -1)))); // 600 -> 601
        waySet.add(new Way(Arrays.asList(new Node("601",  1, -1), new Node("602",  1,  1)))); // 601 -> 602
        waySet.add(new Way(Arrays.asList(new Node("603", -1,  1), new Node("600", -1, -1)))); // 603 -> 600

        new ClosedBoundary(waySet);
    }

    @Test
    public void testBoundaryEmpty() {
        Set<Way> waySet = new LinkedHashSet<>();

        ClosedBoundary closedBoundary = new ClosedBoundary(waySet);
        assertTrue(closedBoundary.getDeterminant() == 0);
        assertTrue(!closedBoundary.isClockwise() && !closedBoundary.isCounterclockwise());
    }

    @Test
    public void testBoundarySingleNode() {
        Set<Way> waySet = new LinkedHashSet<>();
        waySet.add(new Way(Arrays.asList(new Node("700", 0, 0), new Node("700", 0, 0)))); // 700 -> 700

        ClosedBoundary closedBoundary = new ClosedBoundary(waySet);
        assertTrue(closedBoundary.getDeterminant() == 0);
        assertTrue(!closedBoundary.isClockwise() && !closedBoundary.isCounterclockwise());
    }

    @Test
    public void testBoundarySingleClosedWay() {
        Set<Way> waySet = new LinkedHashSet<>();
        waySet.add(new Way(Arrays.asList(new Node("800", 0, 0), new Node("801", 1, 0), new Node("802", 1, 1), new Node("802", 0, 1), new Node("800", 0, 0)))); // 800 -> ... -> 800

        ClosedBoundary closedBoundary = new ClosedBoundary(waySet);
        assertTrue(closedBoundary.getDeterminant() > 0);
        assertTrue(closedBoundary.isCounterclockwise());

        // Sanity check: what if we reverse the boundary?
        ClosedBoundary reversedClosedBoundary = new ClosedBoundary(new HashSet<>(closedBoundary.getReversedWayList()));
        assertTrue(reversedClosedBoundary.getDeterminant() < 0);
        assertTrue(reversedClosedBoundary.isClockwise());
    }

}
