package sample;

/**
 * Road class used to store data about a single road which has a distance cost associated with it.
 */
public class Road extends Link {

    private int distance;

    public Road(String type, String id, int distance){
        super(type, id);
        this.setDistance(distance);
    }

    //---------------------------------//
    //-------Getters and Setters------//
    //--------------------------------//

    public void setDistance(int distance) {
        // Limiting the maximum distance to 999.
        if (distance > 999) {
            this.distance = 999;
        }
        else {
            this.distance = distance;
        }
    }


    public int getDistance() {
        return distance;
    }
}
