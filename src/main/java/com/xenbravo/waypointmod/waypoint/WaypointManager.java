package com.xenbravo.waypointmod.waypoint;

import com.xenbravo.waypointmod.WaypointMod;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class WaypointManager {

    private static final WaypointManager INSTANCE = new WaypointManager();
    private final List<Waypoint> waypoints = new ArrayList<>();
    private boolean loaded = false;

    public static WaypointManager getInstance() { return INSTANCE; }

    private Path getSavePath(MinecraftClient client) {
        return client.runDirectory.toPath()
            .resolve("xenwaypoints")
            .resolve("waypoints.txt");
    }

    public List<Waypoint> getWaypoints(MinecraftClient client) {
        if (!loaded) {
            loadFrom(client);
            loaded = true;
        }
        return waypoints;
    }

    public void openNamingScreen(MinecraftClient client) {
        if (client.player == null) return;
        double x = client.player.getX();
        double y = client.player.getY();
        double z = client.player.getZ();
        client.execute(() ->
            client.setScreen(new WaypointNamingScreen(x, y, z))
        );
    }

    public void addWaypoint(Waypoint wp, MinecraftClient client) {
        getWaypoints(client); // ensure loaded
        waypoints.add(wp);
        saveTo(client);
    }

    private void saveTo(MinecraftClient client) {
        try {
            Path path = getSavePath(client);
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            for (Waypoint wp : waypoints) lines.add(wp.toLine());
            Files.write(path, lines);
        } catch (IOException e) {
            WaypointMod.LOGGER.error("[XenWaypoints] Save error: " + e.getMessage());
        }
    }

    private void loadFrom(MinecraftClient client) {
        try {
            Path path = getSavePath(client);
            if (!Files.exists(path)) return;
            waypoints.clear();
            for (String line : Files.readAllLines(path)) {
                if (line.isBlank()) continue;
                Waypoint wp = Waypoint.fromLine(line);
                if (wp != null) waypoints.add(wp);
            }
            WaypointMod.LOGGER.info("[XenWaypoints] Loaded " + waypoints.size() + " waypoints.");
        } catch (IOException e) {
            WaypointMod.LOGGER.error("[XenWaypoints] Load error: " + e.getMessage());
        }
    }
}
