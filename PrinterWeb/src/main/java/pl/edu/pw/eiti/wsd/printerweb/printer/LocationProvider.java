package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.io.Serializable;

public class LocationProvider implements Serializable {

    int x = 1, y = 2, floor = 3;

    public Location getCurrentLocation() {
        return new Location(x, y, floor);
    }

    public LocationProvider() {
    }

    public LocationProvider(int x, int y, int floor){
        this.x = x;
        this.y = y;
        this.floor = floor;
    }

    public static class Location implements Serializable {

        private int x;

        private int y;

        private int floor;

        private Location(int x, int y, int floor) {
            this.x = x;
            this.y = y;
            this.floor = floor;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getFloor() {
            return floor;
        }

        String serializeLocation(){
            return x + ";" + y + ";" + floor;
        }
    }

    public int calculateDistance(Location sourceLocation, Location location) {
        return 1;
    }

    static Location deseralizeLocation(String strLocation){
        String[] split = strLocation.split(";");
        if(split.length != 3)
            throw new IllegalStateException("Deserialization faild: " + strLocation);

        return new Location(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
    }
}
