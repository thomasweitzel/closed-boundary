# Build closed boundary and calculate its orientation for e.g. OpenStreetMap (OSM) data

## Overview

This project implements an algorithm for building a closed boundary out of a set of ways and tries to determine
its orientation (clockwise or counterclockwise).

## License

This software is licensed under the [MIT License](LICENSE).

## Definitions

- Node: a 2D point having a longitude (double), a latitude (double), and an identifier (e.g. a name)
- Way: an ordered collection of two or more nodes (e.g. a list); if you would draw lines between all nodes, there would be no intersections
- Boundary: an ordered collection of ways; the last node of each way is the first node of the next way - except for the last node; no intersections, too
- Closed Boundary: a boundary where the last node of the last way is the first node of the first way and there're no intersections
- Berlin (West) Problem: having a boundary like the Federal Republic of Germany (FRG, West Germany) from 1949 to 1990; the mainland and Berlin (West) had more than one closed boundary

## Process

We try to build a closed boundary from a set of ways and calculate its orientation (clockwise or counterclockwise).

First the set of ways is used to build the closed boundary by starting with one way, finding its ending node and
using this ending node to find another way that either starts or ends with this node (the next way).
If the other way ends with this node, the way is reversed (the order of its nodes is turned around).
This process continues until there's either no other way left or there's no next way to use.

The result is then validated and (if validation was successful) used to calculate the orientation of the resulting
closed boundary. This is done by trying to find up to four candidate nodes for the calculation of the determinant.
If at least three nodes are found, the determinant is calculated. The value of the determinant (> 0, 0, or < 0)
translates directly into the orientation of the closed boundary.

It works on a couple of preconditions: the ways have to form a closed boundary which must not have gaps.
At least four nodes are needed to calculate the orientation, and even then it might not be found if too many
nodes are on a straight line (collinear).

## Getting involved

To access the source, just clone the [project](https://github.com/thomasweitzel/closed-boundary)
