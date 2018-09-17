package sample;

import java.util.*;

/**
 * Node class implementing the basic adjacency list to connect cities and roads together.
 *
 * @param <T>
 */
public class Node<T> {

    private T data;
    private ArrayList<Node> links;

    public Node(T data) {
        this.setData(data);
        this.setLinks(new ArrayList<>());
    }

    /**
     * Generating multiple paths from a beginning and goal node.
     *
     * @param firstNode - Beginning node.
     * @param goalNode - Destination node.
     * @param waypoints - List of links to goto (prior to destination).
     * @param avoidedPoints - List of links to avoid.
     * @param permutations - Number of paths generated.
     * @param mode - "quickest" generates the path that takes the least amount of time to get through.
     * @param <T>
     * @return
     */
    public static <T> Path[] findNShortestPath(Node<T> firstNode, Node<T> goalNode, List<Node<T>> waypoints, List<Node<T>> avoidedPoints, int permutations, String mode) {
        Path[] nPaths = new Path[permutations];

        /*
            Cloning the list of points to avoid as new points will be added in this method.
            This prevents the list of points to avoid outside of this method to be affected.
         */
        List<Node<T>> pointsToAvoid = new ArrayList<>(avoidedPoints);

        for (int n = 0; n < permutations; n++) {
            // Generating paths a number of times, with each route not taking the same as the previous.
            Path newPath = findShortestPath(firstNode, goalNode, waypoints, pointsToAvoid, mode);

            if (newPath != null && newPath.size() > 1) {
                nPaths[n] = (Path) newPath.clone();

                // Allowing the starting and goal nodes to be traversed in the next permutation.
                newPath.remove(0);
                newPath.remove(newPath.size() - 1);

                pointsToAvoid.addAll((List) newPath);
                pointsToAvoid.removeAll(waypoints);

                // Allowing roads to be traversed through in different permutations, provided that they're not in the original points to avoid list.
                pointsToAvoid.removeIf(node -> !avoidedPoints.contains(node) && node.getData() instanceof Road);
            }
        }

        return nPaths;
    }

    /**
     * Generates the shortest/quickest path from one node to another node using Dijkstra's Algorithm.
     *
     * @param firstNode - Beginning node.
     * @param goalNode - Destination node.
     * @param waypoints - List of links to goto (prior to destination).
     * @param pointsToAvoid - List of links to avoid.
     * @param mode - "quickest" generates the path that takes the least amount of time to get through.
     * @return
     */
    public static <T> Path findShortestPath(Node<T> firstNode, Node<T> goalNode, List<Node<T>> waypoints, List<Node<T>> pointsToAvoid, String mode) {
        Path shortestPath = new Path();

        /*
            Data requiring to be stored to achieve DA;
                traversedNodes - List of nodes traversed.
                notTraversed - List of nodes not traversed.
                traversalCosts - Each node and its associated time cost to travel to the node.
         */
        List<Node<T>> traversedNodes = new ArrayList<>();
        List<Node<T>> notTraversedNodes = new ArrayList<>();
        Map<Node<T>, Integer> traversalCosts = new HashMap<>();

        if (pointsToAvoid != null) {
            // The points in the points to avoid list will not be searched.
            traversedNodes.addAll(pointsToAvoid);
        }

        if (waypoints != null && !waypoints.isEmpty()) {
            // Recursively traversing to the given waypoints first before reaching the goal node if specified.

            waypoints.add(0, firstNode);
            waypoints.add(goalNode);

            for (int i = 1; i < waypoints.size(); i++) {
                // Getting the path from the previous waypoint to the current waypoint.
                Path toCurrentWaypoint = findShortestPath(waypoints.get(i - 1), waypoints.get(i), null, pointsToAvoid, mode);

                if (toCurrentWaypoint != null) {

                    if (i != 1) {
                        // Preventing duplicate points being added.
                        toCurrentWaypoint.remove(0);
                    }

                    shortestPath.addAll(toCurrentWaypoint);
                }
            }

            // Only consider paths that actually reach the goal node and not just certain waypoints.
            if (shortestPath.get(shortestPath.size() - 1) != goalNode) {
                return null;
            }

            return shortestPath;
        } else {
            notTraversedNodes.add(firstNode);
            traversalCosts.put(firstNode, 0);

            do {
                Node<T> thisNode = notTraversedNodes.remove(0);
                traversedNodes.add(thisNode);

                if (thisNode.equals(goalNode)) {
                    // Destination node found, start to traverse back to the origin beginning node.

                    shortestPath.add(thisNode);
                    // The traversal cost will have already been set while reaching to the goal node.
                    shortestPath.setCost(traversalCosts.get(thisNode));

                    // Keep finding the previous path until the first node is found.
                    while (thisNode != firstNode) {
                        boolean prevPathFound = false;

                        for (Node<T> prevNode : traversedNodes) {

                            // Some nodes may not have associated costs as they may be added from the points to avoid list.
                            if (traversalCosts.containsKey(prevNode)) {

                                for (Node<T> childNode : prevNode.getLinks()) {
                                    if (childNode.equals(thisNode)) {
                                        int childDistance = 0, childSpeed = 1;

                                        if (childNode.getData() instanceof Road) {
                                            childDistance = ((Road) childNode.getData()).getDistance();
                                            childSpeed = mode == "quickest" ? Link.ROAD_SPEEDS.get(((Road) childNode.getData()).getType()) : 1;
                                        }

                                        // For debugging purposes;
                                        if (traversalCosts.get(thisNode) == null) {
                                            System.out.println("This node is null");
                                        } else if (traversalCosts.get(prevNode) == null) {
                                            System.out.println("Prev node is null");
                                            if (prevNode.getData() instanceof City) {
                                                System.out.println(((City) prevNode.getData()).getName());
                                            } else if (prevNode.getData() instanceof Road) {
                                                System.out.println(((Road) prevNode.getData()).getId());
                                            }
                                        }

                                        if (traversalCosts.get(thisNode) - (childDistance / childSpeed) == traversalCosts.get(prevNode)) {
                                            // Previous node with the cheapest cost found.

                                            shortestPath.add(0, prevNode);
                                            thisNode = prevNode;
                                            prevPathFound = true;

                                            break;
                                        }
                                    }
                                }
                            }

                            if (prevPathFound) {
                                break;
                            }
                        }
                    }

                    return shortestPath;
                }

                // Current node is not the destination node.

                for (Node<T> childNode : thisNode.getLinks()) {
                    if (!traversedNodes.contains(childNode)) {
                        // Adding child nodes to be checked and setting their associated time cost (distance/speed) to travel.

                        int childDistance = 0, childSpeed = 1;

                        if (childNode.getData() instanceof Road) {
                            childDistance = ((Road) childNode.getData()).getDistance();
                            childSpeed = mode == "quickest" ? Link.ROAD_SPEEDS.get(((Road) childNode.getData()).getType()) : 1;
                        }

                        // Setting the traversal time cost to be the lower of the two; previous travel cost and new travel cost.
                        traversalCosts.put(
                                childNode,
                                Integer.min(traversalCosts.get(childNode) == null ? Integer.MAX_VALUE : traversalCosts.get(childNode), traversalCosts.get(thisNode) + (childDistance / childSpeed))
                        );

                        notTraversedNodes.add(childNode);
                    }
                }

                // Making sure the least traversal expensive node is checked first.
                Collections.sort(
                        notTraversedNodes,
                        (node1, node2) -> traversalCosts.get(node1) - traversalCosts.get(node2)
                );

            } while (!notTraversedNodes.isEmpty());
        }

        // Search failed.

        return null;
    }

    //---------------------------------//
    //-------Getters and Setters------//
    //--------------------------------//

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ArrayList<Node> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Node> links) {
        this.links = links;
    }

    public void addLink(Node node){
        links.add(node);
    }
}
