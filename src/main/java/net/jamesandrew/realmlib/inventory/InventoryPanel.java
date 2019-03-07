package net.jamesandrew.realmlib.inventory;

import net.jamesandrew.commons.container.Container;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents a single Inventory
 */
public class InventoryPanel implements Seedable, Updateable {

    private Inventory inventory;
    private final String title, seed;
    private Paginator paginator;
    private final InventorySize size;
    private Set<MenuIcon> iconsInInventory = new HashSet<>();

    public InventoryPanel(String title, InventorySize size, Paginator paginator) {
        this.title = title;
        this.seed = generateInvisibleSeed(3);
        this.size = size;
        setPaginator(paginator);
    }

    public void open(Player p) {
        Validate.notNull(paginator, "Paginator not set on InventoryPanel");
        Validate.notNull(inventory, "Inventory not created");
        p.openInventory(inventory);
    }

    public void close(Player p) {
        Validate.notNull(paginator, "Paginator not set on InventoryPanel");
        Validate.notNull(inventory, "Inventory not created");
        p.closeInventory();
    }

    public String getSeededTitle() {
        Validate.notNull(paginator, "Paginator null for InventoryPanel");
        return title + paginator.getSeed() + seed;
    }

    public String getTitle() {
        return title;
    }

    private void setPaginator(Paginator paginator) {
        Validate.isTrue(this.paginator == null, "Paginator already set on InventoryPanel");
        this.paginator = paginator;
        createInventory();
    }

    private void createInventory() {
        Validate.notNull(paginator, "Paginator not set on InventoryPanel");
        Validate.isTrue(inventory == null, "Inventory already created for the InventoryPanel");
        inventory = Bukkit.createInventory(null, size.getSize(), getSeededTitle());
    }

    public Map<Integer, MenuIcon> addIcon(MenuIcon... icons) {
        Validate.notNull(paginator, "Paginator not set on InventoryPanel");
        Validate.notNull(inventory, "Inventory not created");
        Validate.isTrue(!Container.hasDuplicate(Arrays.asList(icons)), "Added duplicate items into inventory");
        Map<Integer, MenuIcon> iconMap = new HashMap<>();
        Set<MenuIcon> iconSet = new HashSet<>(Arrays.asList(icons));

        Iterator iter = iconSet.iterator();
        outer:
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack current = inventory.getContents()[i];

            while(iter.hasNext()) {
                MenuIcon icon = (MenuIcon) iter.next();

                if (current != null) {
                    iconMap.put(i, icon);
                    iter.remove();
                    continue outer;
                }

                ItemStack[] stacks = inventory.getContents();
                stacks[i] = icon.getItem();
                icon.setSlot(i);
                saveIcon(icon);

                inventory.setContents(stacks);

                if (inventory.firstEmpty() == -1) break outer;
            }
        }

        return iconMap;
    }

    public void removeIconExact(MenuIcon icon) {
        removeIcon(icon, i -> i == icon.getItem());
    }

    public void removeIconSimilar(MenuIcon icon) {
        removeIcon(icon, i -> i.equals(icon.getItem()));
    }

    private void removeIcon(MenuIcon icon, Predicate<? super ItemStack> filter) {
        Validate.notNull(paginator, "Paginator not set on InventoryPanel");
        Validate.notNull(inventory, "Inventory not created");
        Optional<ItemStack> oItem = Arrays.stream(inventory.getContents()).filter(Objects::nonNull).filter(filter).findFirst();
        oItem.ifPresent(inventory::removeItem);
        if (oItem.isPresent()) {
            icon.setSlot(-1);
            deleteIcon(icon);
        }
    }

    public void setIcon(MenuIcon icon, int slot) {
        Validate.notNull(paginator, "Paginator not set on InventoryPanel");
        Validate.notNull(inventory, "Inventory not created");
        Validate.isTrue(!iconsInInventory.contains(icon), "Icon already in inventory");
        inventory.setItem(slot, icon.getItem());
        icon.setSlot(slot);
        saveIcon(icon);
    }

    public void setIcon(MenuIcon icon, int... slots) {
        Arrays.stream(slots).forEach(s -> {
            MenuIcon i = icon.clone();
            setIcon(i, s);
        });
    }

    public void fill(MenuIcon icon) {
        for (int i = 0; i < size.getSize(); i++) setIcon(icon.clone(), i);
    }

    private void saveIcon(MenuIcon icon) {
        Iterator<MenuIcon> iter = iconsInInventory.iterator();
        while(iter.hasNext()) {
            MenuIcon m = iter.next();
            if (m.getSlot() == icon.getSlot()) iconsInInventory.remove(icon);
        }
        iconsInInventory.add(icon);
        if (!icon.containsPanel(this)) icon.addPanel(this);
    }

    private void deleteIcon(MenuIcon icon) {
        iconsInInventory.remove(icon);
        icon.removePanel(this);
    }

    public Collection<MenuIcon> getIcons() {
        return Collections.unmodifiableCollection(iconsInInventory);
    }

    @Override
    public void update() {
        if (paginator == null) return;
        List<InventoryPanel> panels = new ArrayList<>();

        paginator.getPanels().forEach(p -> {
            if (p == this) {
                Iterator<MenuIcon> icons = p.getIcons().iterator();
                while(icons.hasNext()) {
                    MenuIcon icon = icons.next();
                    for (int x = 0; x < inventory.getContents().length; x++) {
                        ItemStack i = inventory.getContents()[x];
                        if (i == null) continue;
                        if (x != icon.getSlot()) continue;
                        inventory.setItem(x, icon.getItem());
                    }
                }
                panels.add(this);
            } else panels.add(p);
        });

        paginator.setPanels(panels);
        List<HumanEntity> entities = inventory.getViewers();
        Iterator<HumanEntity> iter = entities.iterator();
        while(iter.hasNext()) {
            HumanEntity entity = iter.next();
            if (!(entity instanceof Player)) continue;
            Player p = (Player) entity;
            p.openInventory(inventory);
        }
    }

    @Override
    public String getSeed() {
        return seed;
    }

}
