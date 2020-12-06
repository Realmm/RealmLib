package net.jamesandrew.realmlib.inventory;

import net.jamesandrew.realmlib.nbt.NBT;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class Icon implements Seedable, Updateable {

    private IconExecution execution;
    private String unseededTitle = "";
    private Set<String> perms = new HashSet<>();
    private Map<String, Object> metadata = new HashMap<>();


    /*
    5,153,632 different combinations of seeds that can be attached to the end of an ItemStack
    For a bug to occur, a player would need to have the same inventory panel seed,
    item to have the same slot as the clicked object,
    as well as the exact same seed on the object
    The minimum chance of that occurring is 1.83 x 10^-9 (about 0.00000000183%)
     */
    private final String seed = generateInvisibleSeed(5);
    private ItemStack item;

    public Icon(ItemStack item, IconExecution execution) {
        Validate.notNull(item, "Item cannot be null");
        this.execution = execution;
        this.item = item;
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(seedTitle());
        this.item.setItemMeta(meta);
    }

    public Icon(ItemStack item) {
        this(item, null);
    }

//    public abstract Class<? extends Icon> clone();

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public void addPermission(String perm) {
        perms.add(perm);
    }

    public void removePermission(String perm) {
        perms.remove(perm);
    }

    public Collection<String> getPermissions() {
        return Collections.unmodifiableCollection(perms);
    }

    public boolean hasPermission(Player p) {
        return !requiresPermission() || perms.stream().allMatch(p::hasPermission);
    }

    public boolean requiresPermission() {
        return !perms.isEmpty();
    }

    public void setExecution(IconExecution execution) {
        this.execution = execution;
    }

    public void execute(Player p) {
        if (!hasExecution()) return;
        execution.onExec(p, this);
    }

    public void setTitle(String title) {
        this.unseededTitle = title;
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getSeededTitle(title));
        this.item.setItemMeta(meta);
    }

    public void setMetaData(Map<String, Object> map) {
        this.metadata = map;
    }

    public void addMetaData(String s, Object o) {
        metadata.put(s, o);
    }

    public Map<String, Object> getMetaData() {
        return metadata;
    }

    public Object getMetaDataObject(String s) {
        return metadata.get(s);
    }

    public String getUnseededTitle() {
        return unseededTitle;
    }

    public void setLore(String... lore) {
        setLore(Arrays.asList(lore));
    }

    public void setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        this.item.setItemMeta(meta);
    }

    public List<String> getLore() {
        return item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();
    }

    public String getSeededTitle() {
        return item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "";
    }

    public String seedTitle() {
        return item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() + seed : seed;
    }

    public String getSeededTitle(String title) {
        return title + seed;
    }

    @Override
    public String getSeed() {
        return seed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Icon)) return false;

        final Icon other = (Icon) obj;

        if (!seed.equals(other.getSeed())) return false;
        return Objects.equals(getItem(), other.getItem());
    }

    protected IconExecution getExecution() {
        return execution;
    }

    public boolean hasExecution() {
        return execution != null;
    }

    public void clearExecution() {
        execution = null;
    }

    public abstract void setSlot(int slot);

    public abstract int getSlot();

}
