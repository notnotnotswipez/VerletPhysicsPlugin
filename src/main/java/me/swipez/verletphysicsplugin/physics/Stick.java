package me.swipez.verletphysicsplugin.physics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Stick {

    public Point point1;
    public Point point2;
    double length;
    double stiffness = 1.5;

    List<UUID> armorStands = new ArrayList<>();

    boolean displayAsParticles = false;

    public Stick(Point point1, Point point2, double length){
        this.point1 = point1;
        this.point2 = point2;
        this.length = length;
    }

    public Stick(Point point1, Point point2){
        this.point1 = point1;
        this.point2 = point2;
        this.length = point1.currentPostion.distance(point2.currentPostion);
    }

    public void simulate(){
        double totalDistance = point1.currentPostion.toVector().distance(point2.currentPostion.toVector());
        double dx = point1.currentPostion.getX() - point2.currentPostion.getX();
        double dy = point1.currentPostion.getY() - point2.currentPostion.getY();
        double dz = point1.currentPostion.getZ() - point2.currentPostion.getZ();
        double difference = totalDistance - length;
        double percent = (difference / totalDistance / 2) * stiffness;
        double offsetX = dx * percent;
        double offsetY = dy * percent;
        double offsetZ = dz * percent;

        if (!point1.locked){
            point1.currentPostion.setX(point1.currentPostion.getX() - offsetX);
            point1.currentPostion.setY(point1.currentPostion.getY() - offsetY);
            point1.currentPostion.setZ(point1.currentPostion.getZ() - offsetZ);
        }
        if (!point2.locked){
            point2.currentPostion.setX(point2.currentPostion.getX() + offsetX);
            point2.currentPostion.setY(point2.currentPostion.getY() + offsetY);
            point2.currentPostion.setZ(point2.currentPostion.getZ() + offsetZ);
        }
    }

    public void display(){
        if (!displayAsParticles){
            for (UUID uuid : armorStands) {
                Bukkit.getEntity(uuid).remove();
            }
            armorStands.clear();
        }
        Vector direction = point2.currentPostion.toVector().subtract(point1.currentPostion.toVector());
        Location start = point1.currentPostion.clone();
        int parts = 5;
        double percent = direction.length() / parts / direction.length();
        for (int i = 0; i < parts; i++){
            start.add(direction.clone().multiply(percent));
            if (!displayAsParticles){
                ArmorStand dummy = start.getWorld().spawn(start.clone().subtract(0, 1.7, 0), ArmorStand.class, armorStand -> {
                    armorStand.setVisible(false);
                    armorStand.getEquipment().setHelmet(new ItemStack(Material.RED_WOOL));
                    armorStand.setInvulnerable(true);
                    armorStand.setArms(true);
                    armorStand.setGravity(false);
                });
                armorStands.add(dummy.getUniqueId());
            }
            else {
                start.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, start.clone(), 0);
            }
        }
    }

    public void destroy(){
        for (UUID uuid : armorStands) {
            Bukkit.getEntity(uuid).remove();
        }
    }
}
