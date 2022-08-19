package me.swipez.verletphysicsplugin;

import me.swipez.verletphysicsplugin.physics.Point;
import me.swipez.verletphysicsplugin.physics.Stick;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.swipez.verletphysicsplugin.VerletPhysicsPlugin.points;
import static me.swipez.verletphysicsplugin.VerletPhysicsPlugin.sticks;

public class TestCommand implements CommandExecutor {

    public static List<UUID> physMode = new ArrayList<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length == 1){
                switch (args[0]) {
                    case "simulate":
                        VerletPhysicsPlugin.pauseSim = !VerletPhysicsPlugin.pauseSim;
                        player.sendMessage(VerletPhysicsPlugin.pauseSim ? "Simulation paused" : "Simulation resumed");
                        break;
                    case "mode":
                        if (!physMode.contains(player.getUniqueId())) {
                            physMode.add(player.getUniqueId());
                        } else {
                            physMode.remove(player.getUniqueId());
                        }
                        player.sendMessage(physMode.contains(player.getUniqueId()) ? "Simulation Mode Active" : "Simulation Mode Disabled");
                        break;
                    case "clear":
                        for (Point point : points) {
                            point.remove();
                        }

                        for (Stick stick : sticks) {
                            stick.destroy();
                        }
                        points.clear();
                        sticks.clear();
                        player.sendMessage("Points and Sticks cleared");
                        break;
                }
            }
            else if (args.length > 1){
                if (args[0].equals("save")){
                    String name = args[1];
                    Parser.parse(TestListener.pointOne, TestListener.pointTwo);
                    Parser.save(name);
                    player.sendMessage(ChatColor.GREEN+"Saved structure to "+name);
                }
                if (args[0].equals("load")){
                    String name = args[1];
                    double scale = 1;
                    if (args.length == 3){
                        scale = Double.parseDouble(args[2]);
                    }
                    Parser.load(player.getLocation(), name, scale);
                }
            }
        }
        return true;
    }
}
