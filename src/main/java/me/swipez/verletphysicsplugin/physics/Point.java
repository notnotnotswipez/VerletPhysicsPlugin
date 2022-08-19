package me.swipez.verletphysicsplugin.physics;

import me.swipez.verletphysicsplugin.Utils;
import me.swipez.verletphysicsplugin.VerletPhysicsPlugin;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Point {

    public Location currentPostion;
    public Location previousPosition;
    double gravity = 0.1;
    double friction = 0.999;
    double bounciness = 0.8;
    ArmorStand representation;
    public boolean locked = false;

    boolean displayAsParticle = false;

    public static HashMap<Point, Point> pointCollisions = new HashMap<>();
    public Point(Location currentPostion, Vector velocity){
        this.currentPostion = currentPostion;
        this.previousPosition = currentPostion.clone().subtract(velocity);
        locked = false;
    }

    public Point(Location currentPostion, Vector velocity, boolean locked){
        this.currentPostion = currentPostion;
        this.previousPosition = currentPostion.clone().subtract(velocity);
        this.locked = locked;
    }

    public Vector calculateVelocity(){
        return currentPostion.toVector().subtract(previousPosition.toVector()).multiply(friction);
    }

    public void constrain(){
        if (locked){
            return;
        }
        Vector velocity = calculateVelocity();

        if (currentPostion.getBlock().getType().isSolid() && previousPosition.getBlock().getType().isSolid()){
            previousPosition = currentPostion.clone().add(0, 0.2, 0);
        }

        if (currentPostion.getBlock().getType().isSolid()) {
            RayTraceResult result = currentPostion.getWorld().rayTraceBlocks(previousPosition, currentPostion.toVector().subtract(previousPosition.toVector()).normalize(), 3, FluidCollisionMode.NEVER, true);
            if (result != null) {
                currentPostion = result.getHitPosition().toLocation(currentPostion.getWorld());
                switch (result.getHitBlockFace()){
                    case UP:
                    case DOWN:
                        previousPosition.setY(currentPostion.getY() + (velocity.getY() * bounciness));
                        currentPostion.add(0, 0.005f, 0);
                        break;
                    case EAST:
                    case WEST:
                        previousPosition.setX(currentPostion.getX() + (velocity.getX() * bounciness));
                        break;
                    case NORTH:
                    case SOUTH:
                        previousPosition.setZ(currentPostion.getZ() + (velocity.getZ() * bounciness));
                        break;
                }
            }
        }
    }

    public void simulate(){
        if (locked){
            return;
        }
        Vector velocity = calculateVelocity();
        velocity.subtract(new Vector(0, gravity, 0));

        previousPosition = currentPostion.clone();
        currentPostion = currentPostion.clone().add(velocity);
        Vector euler = toEulerAngle(velocity);
        if (representation != null){
            representation.setHeadPose(new EulerAngle(euler.getX() + 90, euler.getY(), euler.getZ()));
        }
    }
    public void display(){
        if (!displayAsParticle){
            if (representation == null){
                representation = currentPostion.getWorld().spawn(currentPostion, ArmorStand.class, armorStand -> {
                    armorStand.setVisible(false);
                    armorStand.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    armorStand.setInvulnerable(true);
                    armorStand.setArms(true);
                    armorStand.setGravity(false);
                });
            }
            representation.teleport(currentPostion.clone().subtract(new Vector(0, 1.7, 0)));
        }
        else {
            currentPostion.getWorld().spawnParticle(Particle.HEART, currentPostion, 0);
        }
    }

    // Create a method that converts a vector to a euler angle
    public static Vector toEulerAngle(Vector vector){
        double yaw = Math.atan2(vector.getZ(), vector.getX());
        double pitch = Math.atan2(vector.getY(), Math.sqrt(vector.getX() * vector.getX() + vector.getZ() * vector.getZ()));
        return new Vector(pitch, yaw, 0);
    }

    public void remove(){
        representation.remove();
    }
}
