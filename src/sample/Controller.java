package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.scene.control.TextArea;


import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class Controller {
    private ArrayList<Node> nodeCollection = new ArrayList<Node>();
    private ArrayList<Node<Link>> avoidedLinks = new ArrayList<>();
    private ArrayList<Node<Link>> wayPoints = new ArrayList<>();

    @FXML
    private TreeView<String> treeView;
    @FXML
    private TreeView<String> specialList;
    @FXML
    private Label label1;
    @FXML
    private Label avoidError;
    @FXML
    private Label waypointError;
    @FXML
    private AnchorPane anchor1;
    @FXML
    private TextField beginning;
    @FXML
    private TextField destination;
    @FXML
    private TextField avoid;
    @FXML
    private TextField waypoint;
    @FXML
    private CheckBox quickest;
    @FXML
    private CheckBox shortest;
    @FXML
    private CheckBox oneRoute;
    @FXML
    private CheckBox twoRoute;
    @FXML
    private CheckBox threeRoute;
    @FXML
    private Label tickBoxError;

    /**
     * Method called when openImage button is pressed
     * Opens file chooser
     * @param actionEvent
     */
    public void openFile(ActionEvent actionEvent) {
        System.out.println("Open an image");
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);
        loadCSV(file);
    }

    /**
     * Method to load csv files
     * @param file
     */
    public void loadCSV(File file){
        File f = file;

        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String str;
            do {
                str = br.readLine();
                if (str != null) {
                    System.out.println(str);
                    String[] tokens = str.split(",");
                    System.out.println(tokens.length);
                    if (!tokens[0].equals("Link")) {
                        boolean isRoad = true;
                        int distance = 0;
                        for(int i = 0; i < tokens.length; i +=1){
                           tokens[i] = tokens[i].replaceAll("\\s+","");
                        }
                        try {
                            distance = Integer.parseInt(tokens[0]);
                        } catch (Exception e) {
                            isRoad = false;
                        }
                        if (isRoad) {
                            String id = tokens[1];
                            String type = tokens[2];
                            nodeCollection.add(new Node(new Road(type, id, distance)));
                        } else {
                            String name = tokens[0];
                            String id = tokens[1];
                            String type = tokens[2];
                            nodeCollection.add(new Node(new City(type, id, name)));
                        }
                    }
                    else{
                        String id = tokens[1];
                        String firstLetter = "";
                        firstLetter = id.substring(0, 1);
                        if(firstLetter.equals("C")){
                            Node<City> city = findCityId(id);
                            for(int i = 2; i < tokens.length; i+=1){
                                Node<Road> road = findRoadId(tokens[i]);
                                if(road!=null) {
                                    city.addLink(road);
                                }
                            }
                        }
                        else{
                            Node<Road> road = findRoadId(id);
                            for(int i = 2; i < tokens.length; i+=1){
                                Node<City> city = findCityId(tokens[i]);
                                road.addLink(city);
                            }
                        }
                    }
                }
            } while (str != null) ;
                br.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        anchor1.setPrefHeight((nodeCollection.size()*5)*12);
    }

    public Node<City> findCityId(String id){
        for(int i =0; i < nodeCollection.size(); i+=1){
            Node<City> a = nodeCollection.get(i);
                try {
                    if (a.getData().getId().equals(id)) {
                        return a;
                    }
                }catch (Exception e){

            }
        }
        return null;
    }


    public Node<Road> findRoadId(String id){
        for(int i =0; i < nodeCollection.size(); i+=1){
            Node<Road> a = nodeCollection.get(i);
                try {
                    if (a.getData().getId().equals(id)) {
                        return a;
                    }
                }catch (Exception e){
                    }
        }
        return null;
    }


    public void search(ActionEvent actionEvent) {
        String startCityName = beginning.getText().replaceAll("\\s+", "");
        String endCityName = destination.getText().replaceAll("\\s+", "");
        Node startCity = findCityName(startCityName);
        Node endCity = findCityName(endCityName);
        boolean isAvoided = false;
        for (int i = 0; i < avoidedLinks.size(); i++) {
            if (avoidedLinks.get(i).equals(startCity)) {
                isAvoided = true;
                label1.setText("Start City is Avoided");
            }
            else if(avoidedLinks.get(i).equals(endCity)){
                isAvoided = true;
                label1.setText("End City is Avoided");
            }
        }
        if (!isAvoided) {
            if (startCity == null && endCity == null) {
                label1.setText("Beginning and Destination not found");
            } else if (startCity == null) {
                label1.setText("Beginning not found");
            } else if (endCity == null) {
                label1.setText("Destination not found");
            } else {
                if (!shortest.isSelected() && !quickest.isSelected()) {
                    tickBoxError.setText("Please Select \nOption");
                } else if (twoRoute.isSelected() || threeRoute.isSelected()) {
                    label1.setText("");
                    tickBoxError.setText("");
                    if (quickest.isSelected()) {
                        multipleQuickestSearch(getNumberOfRoute(), startCity, endCity);
                    } else {
                        multipleShortestSearch(getNumberOfRoute(), startCity, endCity);
                    }
                } else {
                    label1.setText("");
                    tickBoxError.setText("");
                    if (quickest.isSelected()) {
                        singleQuickestSearch(startCity, endCity);
                    } else {
                        singleShortestSearch(startCity, endCity);
                    }
                }
            }
        }
    }

    public void singleQuickestSearch(Node startCity, Node endcity){
        /*
        String[] list = new String[4];
        list[0] = "quickest";
        list[1] = "s";
        list[2] = "Root info in here";
        list[3] = "s";
        */

        /*
        Path list = Node.findShortestPath(startCity, endcity, wayPoints, avoidedLinks, "quickest");
        TreeItem<String> root = new TreeItem<>("Root");
        for(int i = 0; i < list.size(); i+=1) {
            TreeItem<String> itemChild = new TreeItem(list.get(i));
            itemChild.setExpanded(false);
            //root is the parent of itemChild
            root.getChildren().add(itemChild);
        }
        treeView.setRoot(root);
        */

        Path list = Node.findShortestPath(startCity, endcity, wayPoints, avoidedLinks, "quickest");
        TreeItem<String> root = new TreeItem<>("Quickest Route");
        int totalDistance = 0;
        for(int i = 0; i < list.size(); i+=1) {
            String str = "";
            if(list.get(i).getData() instanceof City){
                str = ((City) list.get(i).getData()).getName();
            }
            else if(list.get(i).getData() instanceof Road){
                str = (((Road) list.get(i).getData()).getId() +  "->" + ((Road) list.get(i).getData()).getDistance()+ "km");
                totalDistance += ((Road) list.get(i).getData()).getDistance();
            }
            TreeItem<String> itemChild = new TreeItem<>(str);
            itemChild.setExpanded(false);
            //root is the parent of itemChild
            root.getChildren().add(itemChild);
        }
        TreeItem<String> totalDist = new TreeItem<>("Total Distance: " + totalDistance+ "km");
        root.getChildren().add(totalDist);
        treeView.setRoot(root);
    }

    public void singleShortestSearch(Node startCity, Node endCity){
        Path list = Node.findShortestPath(startCity, endCity, wayPoints, avoidedLinks, "shortest");
        TreeItem<String> root = new TreeItem<>("Route");
        int totalDistance = 0;
        for(int i = 0; i < list.size(); i+=1) {
            String str = "";
            if(list.get(i).getData() instanceof City){
                str = ((City) list.get(i).getData()).getName();
            }
            else if(list.get(i).getData() instanceof Road){
                str = (((Road) list.get(i).getData()).getId() +  "->" + ((Road) list.get(i).getData()).getDistance()+ "km");
                totalDistance += ((Road) list.get(i).getData()).getDistance();
            }
            TreeItem<String> itemChild = new TreeItem<>(str);
            itemChild.setExpanded(false);
            //root is the parent of itemChild
            root.getChildren().add(itemChild);
        }
        TreeItem<String> totalDist = new TreeItem<>("Total Distance: " + totalDistance+ "km");
        root.getChildren().add(totalDist);
        treeView.setRoot(root);
    }

    public void multipleQuickestSearch(int num, Node start, Node finish){
        Path[] paths =  Node.findNShortestPath(start, finish, wayPoints, avoidedLinks, num, "quickest");
        //System.out.println("Finding " + num + " Routes. From: " + start.getData().getName() + " to: " + finish.getData().getName());
        TreeItem<String> mainItem = new TreeItem<>("Quickest Routes");
        TreeItem<String>[] routes = new TreeItem[num];
        for(int j = 0; j < paths.length; j+=1) {
            TreeItem<String> root = new TreeItem<String>("Route " + (j + 1));
            Path list = paths[j];
            // If there's not enough paths generated, ignore the empty path.
            if (list != null) {
                int totalDistance = 0;
                for (int i = 0; i < list.size(); i += 1) {
                    String str = "";
                    if (list.get(i).getData() instanceof City) {
                        str = ((City) list.get(i).getData()).getName();
                    } else if (list.get(i).getData() instanceof Road) {
                        str = (((Road) list.get(i).getData()).getId() + "->" + ((Road) list.get(i).getData()).getDistance()+ "km");
                        totalDistance += ((Road) list.get(i).getData()).getDistance();
                    }
                    TreeItem<String> itemChild = new TreeItem<>(str);
                    itemChild.setExpanded(false);
                    //root is the parent of itemChild
                    root.getChildren().add(itemChild);
                    routes[j] = root;
                }
                TreeItem<String> totalDist = new TreeItem<>("Total Distance: " + totalDistance + "km");
                routes[j].getChildren().add(totalDist);
            }
        }
        mainItem.getChildren().addAll(routes);
        treeView.setRoot(mainItem);
    }

    public void multipleShortestSearch(int num, Node start, Node finish){
        Path[] paths =  Node.findNShortestPath(start, finish, wayPoints, avoidedLinks, num, "shortest");
        //System.out.println("Finding " + num + " Routes. From: " + start.getData().getName() + " to: " + finish.getData().getName());
        TreeItem<String> mainItem = new TreeItem<>("Routes");
        TreeItem<String>[] routes = new TreeItem[num];
        for(int j = 0; j < paths.length; j+=1) {
            TreeItem<String> root = new TreeItem<String>("Route " + (j + 1));
            Path list = paths[j];
            // If there's not enough paths generated, ignore the empty path.
            if (list != null) {
                int totalDistance = 0;
                for (int i = 0; i < list.size(); i += 1) {
                    String str = "";
                    if (list.get(i).getData() instanceof City) {
                        str = ((City) list.get(i).getData()).getName();
                    } else if (list.get(i).getData() instanceof Road) {
                        str = (((Road) list.get(i).getData()).getId() + "->" + ((Road) list.get(i).getData()).getDistance()+ "km");
                        totalDistance += ((Road) list.get(i).getData()).getDistance();
                    }
                    TreeItem<String> itemChild = new TreeItem<>(str);
                    itemChild.setExpanded(false);
                    //root is the parent of itemChild
                    root.getChildren().add(itemChild);
                    routes[j] = root;
                }
                TreeItem<String> totalDist = new TreeItem<>("Total Distance: " + totalDistance + "km");
                routes[j].getChildren().add(totalDist);
            }
        }
        mainItem.getChildren().addAll(routes);
        treeView.setRoot(mainItem);
    }

    public Node<City> findCityName(String name){
        for(int i =0; i < nodeCollection.size(); i+=1){
            Node<City> a = nodeCollection.get(i);
            try {
                if (a.getData().getName().equals(name)) {
                    return a;
                }
            }catch (Exception e){

            }
        }
        return null;
    }

    public void clearAvoid(ActionEvent actionEvent) {
        avoidedLinks.clear();
        addSpeciallists();
    }

    public void validateTick(ActionEvent actionEvent) {
        if(!shortest.equals(actionEvent.getSource()) && shortest.isSelected()){
            shortest.setSelected(false);
        }
        if(!quickest.equals(actionEvent.getSource()) && quickest.isSelected()){
            quickest.setSelected(false);
        }
    }

    public void validateRouteTick(ActionEvent actionEvent) {
        if(!oneRoute.equals(actionEvent.getSource()) && oneRoute.isSelected()){
            oneRoute.setSelected(false);
        }
        if(!twoRoute.equals(actionEvent.getSource()) && twoRoute.isSelected()){
            twoRoute.setSelected(false);
        }
        if(!threeRoute.equals(actionEvent.getSource()) && threeRoute.isSelected()){
            threeRoute.setSelected(false);
        }
    }

    public int getNumberOfRoute(){
        int num;
        if(oneRoute.isSelected()){
            num = 1;
        }
        else if(twoRoute.isSelected()){
            num = 2;
        }
        else{
            num = 3;
        }
        return num;
    }

    public void addWaypoint(ActionEvent actionEvent) {
        waypointError.setText("");
        String placeName = waypoint.getText().replaceAll("\\s+","");;
        Node link = findCityName(placeName);
        if(link == null){
            link = findRoadId(placeName);
        }
        if(link != null){
            boolean isDuplicate = false;
            for(int i = 0; i < wayPoints.size(); i+=1){
                if(wayPoints.get(i).equals(link)){
                    isDuplicate = true;
                }
            }
            if(isDuplicate){
                waypointError.setText("Already Waypoint");
            }else {
                boolean isAvoided = false;
                for (int j = 0; j < avoidedLinks.size(); j += 1) {
                    if (avoidedLinks.get(j).equals(link)) {
                        isAvoided = true;
                    }
                }
                if (isAvoided) {
                    waypointError.setText("City is Avoided");
                } else
                    wayPoints.add(link);
                    addSpeciallists();
                }
            }else{
            waypointError.setText("No Place Found");
        }
    }

    public void clearWaypoints(ActionEvent actionEvent) {
        wayPoints.clear();
        addSpeciallists();
    }

 public void addAvoid(ActionEvent actionEvent) {
        avoidError.setText("");
        String linkName = avoid.getText().replaceAll("\\s+","");;
        Node link = findCityName(linkName);
            if(link == null){
                link = findRoadId(linkName);
            }
        if(link != null){
            boolean isDuplicate = false;
            for(int i = 0; i < avoidedLinks.size(); i+=1){
                if(avoidedLinks.get(i).equals(link)){
                    isDuplicate = true;
                }
            }
            if(isDuplicate){
                avoidError.setText("City Already Avoided");
            }else {
                boolean isWaypoint = false;
                for (int j = 0; j < wayPoints.size(); j += 1) {
                    if (wayPoints.get(j).equals(link)) {
                        isWaypoint = true;
                    }
                }
                if (isWaypoint) {
                    avoidError.setText("City is Waypoint");
                } else {
                    avoidedLinks.add(link);
                    addSpeciallists();
                }
            }
        } else {
                avoidError.setText("Place not found");
            }
        }

    public void addSpeciallists(){
        TreeItem<String> root = new TreeItem<>("Other Options");
        TreeItem<String> waypointList = new TreeItem<>("Waypoints");
        for (int i = 0; i < wayPoints.size(); i += 1) {
            TreeItem<String> itemChild = null;
            //str = str + avoidedCities.get(i).getData().getName() + "\n";
            if (wayPoints.get(i).getData() instanceof City) {
                itemChild = new TreeItem<>(((City) wayPoints.get(i).getData()).getName());
            } else{
                itemChild = new TreeItem<>((wayPoints.get(i).getData()).getId());
            }
                itemChild.setExpanded(false);
                //root is the parent of itemChild
                waypointList.getChildren().add(itemChild);
        }
        TreeItem<String> avoid = new TreeItem<>("Avoid");
        for (int i = 0; i < avoidedLinks.size(); i += 1) {
            TreeItem<String> itemChild = null;
            //str = str + avoidedCities.get(i).getData().getName() + "\n";
            if (avoidedLinks.get(i).getData() instanceof City) {
                itemChild = new TreeItem<>(((City) avoidedLinks.get(i).getData()).getName());
            } else {
                itemChild = new TreeItem<>((avoidedLinks.get(i).getData()).getId());
            }
                itemChild.setExpanded(false);
                //root is the parent of itemChild
                avoid.getChildren().add(itemChild);
        }
        root.getChildren().addAll(avoid, waypointList);
        specialList.setRoot(root);
    }

    /**
     * Generates page for about section
     * @param actionEvent
     */
    public void displayAboutInfo(ActionEvent actionEvent) {
        try {
            Stage aboutStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("about.fxml"));
            aboutStage.setTitle("About");
            aboutStage.setScene(new Scene(root, 289, 159));
            aboutStage.show();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}

