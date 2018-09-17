package sample;

import java.util.ArrayList;

/**
 * Class to store an array list of weighted nodes for usage in Dijkstra's Algorithm.
 */
public class Path extends ArrayList<Node> {

    int cost;

    public Path() {
        setCost(0);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
