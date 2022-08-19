package me.swipez.verletphysicsplugin;

import me.swipez.verletphysicsplugin.physics.Point;
import me.swipez.verletphysicsplugin.physics.Stick;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Parser {

    static List<Point> parsedPoints = new ArrayList<>();
    static List<Connection> parsedConnections = new ArrayList<>();

    static List<Stick> parsedSticks = new ArrayList<>();
    static HashMap<Location, Point> blockPoints = new HashMap<>();

    static Location firstPoint;

    public static void parse(Location firstCorner, Location secondCorner){
        firstPoint = firstCorner;
        parsedConnections.clear();
        parsedSticks.clear();
        parsedPoints.clear();
        blockPoints.clear();

        // Write code that gets all the blocks between these two corners
        for (int x = firstCorner.getBlockX(); x <= secondCorner.getBlockX(); x++){
            for (int y = firstCorner.getBlockY(); y <= secondCorner.getBlockY(); y++){
                for (int z = firstCorner.getBlockZ(); z <= secondCorner.getBlockZ(); z++){
                    Block block = firstCorner.getWorld().getBlockAt(x, y, z);
                    if (block.getType().equals(Material.RED_CONCRETE)){
                        Point point =  new Point(block.getLocation().clone().add(0.5, -0.5, 0.5), new Vector(0, 0, 0));
                        parsedPoints.add(point);
                        blockPoints.put(block.getLocation(), point);
                    }

                    if (block.getType().equals(Material.YELLOW_CONCRETE)){
                        Point point =  new Point(block.getLocation().clone().add(0.5, -0.5, 0.5), new Vector(0, 0, 0), true);
                        parsedPoints.add(point);
                        blockPoints.put(block.getLocation(), point);
                    }
                }
            }
        }

        // Sticks
        for (int x = firstCorner.getBlockX(); x <= secondCorner.getBlockX(); x++){
            for (int y = firstCorner.getBlockY(); y <= secondCorner.getBlockY(); y++){
                for (int z = firstCorner.getBlockZ(); z <= secondCorner.getBlockZ(); z++){
                    Block block = firstCorner.getWorld().getBlockAt(x, y, z);
                    if (block.getType().equals(Material.RED_CONCRETE) || block.getType().equals(Material.YELLOW_CONCRETE)){
                        checkForStick(block.getLocation().clone());
                    }
                }
            }
        }
    }

    public static void save(String name){
        VerletPhysicsPlugin.storage.getConfig().set(name, null);
        for (Point point : parsedPoints){
            Location location = point.currentPostion.subtract(firstPoint);
            String string = location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ()+";"+point.locked;
            VerletPhysicsPlugin.storage.getConfig().set(name+".point."+string, "point");
        }
        for (Connection connection : parsedConnections){
            String string = connection.index1+";"+connection.index2;
            VerletPhysicsPlugin.storage.getConfig().set(name+".connection."+string, "connection");
        }
        try {
            VerletPhysicsPlugin.storage.saveConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load(Location origin, String name, double scale){
        Set<String> points = VerletPhysicsPlugin.storage.getConfig().getConfigurationSection(name+".point").getKeys(false);
        Set<String> connections = VerletPhysicsPlugin.storage.getConfig().getConfigurationSection(name+".connection").getKeys(false);

        List<Point> tempPoints = new ArrayList<>();
        List<Stick> tempSticks = new ArrayList<>();

        for (String string : points) {
            String[] split = string.split(";");
            double x = Double.parseDouble(split[0]) * scale;
            double y = Double.parseDouble(split[1]) * scale;
            double z = Double.parseDouble(split[2]) * scale;
            boolean isStatic = Boolean.parseBoolean(split[3]);
            Location location = origin.clone().add(x, y, z);
            Point point = new Point(location, new Vector(0, 0, 0), isStatic);
            tempPoints.add(point);
        }

        for (String string : connections) {
            String[] split = string.split(";");
            int index1 = Integer.parseInt(split[0]);
            int index2 = Integer.parseInt(split[1]);
            tempSticks.add(new Stick(tempPoints.get(index1), tempPoints.get(index2)));
        }

        VerletPhysicsPlugin.points.addAll(tempPoints);
        VerletPhysicsPlugin.sticks.addAll(tempSticks);
    }

    private static boolean isConnected(Point point1, Point point2){
        for (Stick stick : parsedSticks){
            if (stick.point1.equals(point1) && stick.point2.equals(point2)){
                return true;
            }
            if (stick.point1.equals(point2) && stick.point2.equals(point1)){
                return true;
            }
        }
        return false;

    }

    private static void checkForStick(Location location){
        Point currentPoint = blockPoints.get(location);
        for (int x = -1; x <= 1; x++){
            for (int y = -1; y <= 1; y++){
                for (int z = -1; z <= 1; z++){
                    Block block = location.clone().add(x, y, z).getBlock();
                    if (block.getType().equals(Material.LIME_CONCRETE)){
                        Block recursedBlock = recursiveCheck(block, block.getLocation().clone().subtract(location.clone()));
                        if (recursedBlock != null){
                            Point point = blockPoints.get(recursedBlock.getLocation());
                            if (point != null){
                                if (!isConnected(point, currentPoint)){
                                    parsedSticks.add(new Stick(point, currentPoint));
                                    parsedConnections.add(new Connection(parsedPoints.indexOf(point), parsedPoints.indexOf(currentPoint)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static Block recursiveCheck(Block origin, Location addition){
        if (origin.getWorld().getBlockAt(origin.getLocation().add(addition)).getType().equals(Material.LIME_CONCRETE)){
            return recursiveCheck(origin.getWorld().getBlockAt(origin.getLocation().add(addition)), addition);
        } else if (origin.getWorld().getBlockAt(origin.getLocation().add(addition)).getType().equals(Material.RED_CONCRETE) || origin.getWorld().getBlockAt(origin.getLocation().add(addition)).getType().equals(Material.RED_CONCRETE)){
            return origin.getWorld().getBlockAt(origin.getLocation().add(addition));
        }
        return null;
    }

    static class Connection {
        public int index1;
        public int index2;

        public Connection(int index1, int index2){
            this.index1 = index1;
            this.index2 = index2;
        }
    }
}
