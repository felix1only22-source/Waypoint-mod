package com.xenbravo.waypointmod.waypoint;

public class Waypoint {
    private final String name;
    private final double x, y, z;

    public Waypoint(String name, double x, double y, double z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double distanceTo(double px, double py, double pz) {
        double dx = x - px, dy = y - py, dz = z - pz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public String getName() { return name; }
    public double getX()    { return x; }
    public double getY()    { return y; }
    public double getZ()    { return z; }

    public String toLine() {
        return name.replace("|", "_") + "|" + x + "|" + y + "|" + z;
    }

    public static Waypoint fromLine(String line) {
        try {
            String[] p = line.split("\\|");
            return new Waypoint(p[0],
                Double.parseDouble(p[1]),
                Double.parseDouble(p[2]),
                Double.parseDouble(p[3]));
        } catch (Exception e) {
            return null;
        }
    }
}
