package net.jamesandrew.realmlib.location;

import net.jamesandrew.commons.number.Number;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationSerializer {

    public static String serialize(Location location) {
        String world = location.getWorld().getName();
        String x = String.valueOf(location.getBlockX());
        String y = String.valueOf(location.getBlockY());
        String z = String.valueOf(location.getBlockZ());

        return world + "@" + x + "@" + y + "@" + z;
    }

    public static Location deserialize(String s) {
        String[] args = s.split("@");

        if (!isFormatted(s))
            throw new IllegalArgumentException("Incorrect string attempted to be used to deserialize for a location");

        World world = Bukkit.getWorld(args[0]);
        int x = Integer.valueOf(args[1]);
        int y = Integer.valueOf(args[2]);
        int z = Integer.valueOf(args[3]);

        return new Location(world, x, y, z);
    }

    public static Location modifyLoc(Location location, double x, double y, double z) {
        double locX = location.getX();
        double locY = location.getY();
        double locZ = location.getZ();

        return new Location(location.getWorld(), locX + x, locY + y, locZ + z);
    }

    public static boolean isFormatted(String s) {
        String[] args = s.split("@");
        if (args.length != 4) return false;

        if (Bukkit.getWorld(args[0]) == null) return false;

        if (!Number.isInt(args[1])) return false;
        if (!Number.isInt(args[2])) return false;
        if (!Number.isInt(args[3])) return false;

        return true;
    }
}