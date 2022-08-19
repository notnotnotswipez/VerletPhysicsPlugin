package me.swipez.verletphysicsplugin;

import me.swipez.verletphysicsplugin.physics.Point;
import me.swipez.verletphysicsplugin.physics.Stick;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class TestListener implements Listener {

    HashMap<UUID, Point> lastSelectedPoint = new HashMap<>();
    public static Location pointOne = null;
    public static Location pointTwo = null;

    @EventHandler
    public void onPlayerInteracts(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (TestCommand.physMode.contains(player.getUniqueId())){
            if (!player.getInventory().getItemInMainHand().getType().equals(Material.STICK)){
                return;
            }
            if (event.hasBlock()){
                return;
            }
            event.setCancelled(true);
            if (event.getAction().toString().toLowerCase().contains("right")){
                VerletPhysicsPlugin.points.add(new Point(player.getLocation().clone().add(player.getLocation().getDirection().normalize()), new Vector(0, 0, 0)));
            }
            if (event.getAction().toString().toLowerCase().contains("left")){
                if (!lastSelectedPoint.containsKey(player.getUniqueId())){
                    Point point = findClosestPointAroundLocation(player.getLocation().clone().add(player.getLocation().getDirection().normalize()));
                    if (point != null){
                        lastSelectedPoint.put(player.getUniqueId(), point);
                        player.sendMessage(ChatColor.GREEN+"First point selected");
                    }
                }
                else {
                    Point closestPoint = findClosestPointAroundLocation(player.getLocation().clone().add(player.getLocation().getDirection().normalize()));
                    if (closestPoint != null){
                        if (lastSelectedPoint.get(player.getUniqueId()) != closestPoint){
                            VerletPhysicsPlugin.sticks.add(new Stick(lastSelectedPoint.get(player.getUniqueId()), closestPoint, lastSelectedPoint.get(player.getUniqueId()).currentPostion.distance(closestPoint.currentPostion)));
                            lastSelectedPoint.remove(player.getUniqueId());
                            player.sendMessage(ChatColor.RED+"Stick created");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBlockInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!TestCommand.physMode.contains(player.getUniqueId())) {
            return;
        }
        if (!event.hasBlock()) {
            return;
        }
        Block block = event.getClickedBlock();
        if (player.getInventory().getItemInMainHand().getType().equals(Material.STICK)){
            event.setCancelled(true);
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                pointOne = block.getLocation().clone();
                player.sendMessage(ChatColor.GREEN+"First scan point selected.");
            }
            else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                pointTwo = block.getLocation().clone();
                player.sendMessage(ChatColor.GREEN+"Second scan point selected.");
            }
        }
    }

    public Point findClosestPointAroundLocation(Location check){
        Point closestPoint = null;
        double closestDistance = Double.MAX_VALUE;
        for (Point point : VerletPhysicsPlugin.points){
            double distance = point.currentPostion.distance(check);
            if (distance < closestDistance){
                closestDistance = distance;
                closestPoint = point;
            }
        }

        return closestPoint;
    }
}
