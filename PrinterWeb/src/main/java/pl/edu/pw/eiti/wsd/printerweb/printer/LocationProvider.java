package pl.edu.pw.eiti.wsd.printerweb.printer;

import java.io.Serializable;

public class LocationProvider implements Serializable {

    public Location getCurrentLocation() {
        return new Location(1, 2, 3);
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
    }

    public int calculateDistance(Location sourceLocation, Location location) {
        return 1;
    }
}
