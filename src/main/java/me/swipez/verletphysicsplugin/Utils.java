package me.swipez.verletphysicsplugin;

import org.bukkit.util.Vector;

public class Utils {

    public static Vector reflect(Vector v, Vector n) {
        return v.subtract(n.multiply(2 * v.dot(n)));
    }
}
