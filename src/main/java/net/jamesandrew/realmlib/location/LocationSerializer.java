package net.jamesandrew.realmlib.location;

import net.jamesandrew.commons.number.Number;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationSerializer {

    private static final String split = "@";

    public static String serialize(Location location) {
        String world = location.getWorld().getName();
        String x = String.valueOf(location.getBlockX());
        String y = String.valueOf(location.getBlockY());
        String z = String.valueOf(location.getBlockZ());
        String pitch = String.valueOf(location.getPitch());
        String yaw = String.valueOf(location.getYaw());

        return String.join(split, world, x, y, z, pitch, yaw);
    }

    public static Location deserialize(String s) {
        String[] args = s.split(split);

        if (!isFormatted(s))
            throw new IllegalArgumentException("Incorrect string attempted to be used to deserialize for a location");

        World world = Bukkit.getWorld(args[0]);
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);
        float pitch = Float.parseFloat(args[4]);
        float yaw = Float.parseFloat(args[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static Location modifyLoc(Location location, double x, double y, double z) {
        double locX = location.getX();
        double locY = location.getY();
        double locZ = location.getZ();

        return new Location(location.getWorld(), locX + x, locY + y, locZ + z, location.getYaw(), location.getPitch());
    }

    public static boolean isFormatted(String s) {
        String[] args = s.split(split);
        if (args.length != 6) return false;

        if (Bukkit.getWorld(args[0]) == null) return false;

        if (!Number.isInt(args[1])) return false;
        if (!Number.isInt(args[2])) return false;
        if (!Number.isInt(args[3])) return false;
        if (!Number.isDouble(args[4])) return false;
        if (!Number.isDouble(args[5])) return false;

        return true;
    }
}