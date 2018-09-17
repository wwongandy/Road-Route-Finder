package sample;

/**
 * City class used to store data about a single city/town in the Road Route Finder system.
 */
public class City extends Link {

    private String name;

    public City(String type, String id, String name) {
        super(type, id);
        this.setName(name);
    }

    //---------------------------------------------//
    //-------------Getters and Setters-------------//
    //---------------------------------------------//

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name == null) {
            // Limiting the max name length to 30 characters.
            int maxLength = (name.length() < 30) ? name.length() : 30;
            name = name.substring(0, maxLength);
            this.name = name;
        }
        else {
            if (name.length() < 30){
                this.name = name;
            }
        }
    }
}
