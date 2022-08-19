package me.swipez.verletphysicsplugin;

import me.swipez.verletphysicsplugin.physics.Point;
import me.swipez.verletphysicsplugin.physics.Stick;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class VerletPhysicsPlugin extends JavaPlugin {

    public static List<Point> points;
    public static List<Stick> sticks;

    public static boolean pauseSim = false;

    public static ConfigGenerator storage;

    @Override
    public void onEnable() {
        File mainFolder = new File(getDataFolder().getPath());
        if (!mainFolder.exists()){
            mainFolder.mkdir();
        }
        storage = new ConfigGenerator(getDataFolder(), "stored_shapes");
        points = new ArrayList<>();
        sticks = new ArrayList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!pauseSim){
                    updatePoints();
                    for (int i = 0; i < 10; i++){
                        updateSticks();
                        constrainPoints();
                    }
                }
                renderPoints();
                renderSticks();
            }
        }.runTaskTimer(this, 1, 1);
        getCommand("physics").setExecutor(new TestCommand());
        getServer().getPluginManager().registerEvents(new TestListener(), this);
    }

    public void constrainPoints(){
        for (Point point : points) {
            point.constrain();
        }
    }

    public void updatePoints(){
        for (Point point : points) {
            point.simulate();
        }
    }

    public void renderPoints(){
        for (Point point : points) {
            point.display();
        }
    }

    public void renderSticks(){
        for (Stick stick : sticks) {
            stick.display();
        }
    }

    public void updateSticks(){
        for (Stick stick : sticks) {
            stick.simulate();
        }
    }

    @Override
    public void onDisable() {
        for (Point point : points) {
            point.remove();
        }

        for (Stick stick : sticks) {
            stick.destroy();
        }
    }
}
