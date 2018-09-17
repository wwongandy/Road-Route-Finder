package sample;

import java.util.HashMap;

/**
 * Link super class storing shared data between cities and roads.
 */
public class Link {

    /**
     * HashMap storing all the road identifiers and their associated speed limit.
     *
     * Sample usage: ROAD_SPEEDS.get(link.getType()) assuming link is of type Link.
     */
    public static HashMap<String, Integer> ROAD_SPEEDS = new HashMap<String, Integer>() {{
        put("M", 120);
        put("R", 80);
        put("N", 100);
        put("Town", 0);
        put("City", 0);
    }};

    private String type;
    private String id;

    public Link(String type, String id) {
        setType(type);
        setId(id);
    }

    //---------------------------------------------//
    //-------------Getters and Setters-------------//
    //---------------------------------------------//

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type.equals("Town") || type.equals("City") || type.equals("M") || type.equals("N") || type.equals("R") || type.equals("L")) {
            this.type = type;
        }
        else {
            this.type = "Unspecified";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (this.id == null) {
            // Limiting the max id length to 4 characters.
            int maxLength = (id.length() < 4) ? id.length() : 4;
            id = id.substring(0, maxLength);
            this.id = id;
        }
        else {
            if (id.length() < 5){
                this.id = id;
            }
        }
    }
}
