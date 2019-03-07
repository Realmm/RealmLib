package net.jamesandrew.realmlib.bungeecord;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Documentation copied from https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/
 *
 * Code heavily inspired by another authors BungeeChannelApi
 *
 * @author Realmm (James Andrew)
 * @see <a href="https://github.com/leonardosnt/BungeeChannelApi/blob/master/src/main/java/io/github/leonardosnt/bungeechannelapi/BungeeChannelApi.java">BungeeChannelApi GitHub</a>
 * @see <a href="https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel">https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel</a>
 */
public class BungeeChannel {

    private static final WeakHashMap<Plugin, BungeeChannel> registeredInstances = new WeakHashMap<>();

    private final Map<String, Queue<BungeeFuture<?>>> callback = new HashMap<>();
    private final Map<String, ForwardConsumer> forwardListeners = new HashMap<>();
    private Plugin plugin;
    private PluginMessageListener listener;
    private ForwardConsumer globalForwardListener;

    /**
     * Get or create new {@link BungeeChannel} instance
     *
     * @param plugin the plugin instance.
     * @return the {@link BungeeChannel} instance for the {@link Plugin}
     * @throws NullPointerException if the {@link Plugin} is {@code null}
     */
    public synchronized static BungeeChannel of(Plugin plugin) {
        return registeredInstances.compute(plugin, (k, v) -> {
            if (v == null) v = new BungeeChannel(plugin);
            return v;
        });
    }

    /**
     * Register your {@link Plugin} with this {@link PluginMessageListener}
     * @param plugin the plugin to register
     */

    public BungeeChannel(Plugin plugin) {
        Validate.isTrue(this.plugin == null, "Plugin has already been set on this BungeeChannel instance");
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");

        synchronized (registeredInstances) {
            registeredInstances.compute(plugin, (k, oldInstance) -> {
                if (oldInstance != null) oldInstance.unregister();
                return this;
            });
        }

        listener = this::onPluginMessageReceived;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, Channel.BUNGEE_CORD);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, Channel.BUNGEE_CORD, listener);
    }

    /**
     * Registers a global forward listener, so that other servers can receive messages via the BungeeCord proxy
     * A global listener is a listener that listens to messages received on all channels
     *
     * @param globalForwardListener the listener to register
     * @see <a href="https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/#forward">https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/#forward</a>
     */
    public void registerForwardListener(ForwardConsumer globalForwardListener) {
        this.globalForwardListener = globalForwardListener;
    }

    /**
     * Registers a forward listener under a specific channel name, so that other servers can receive messages via the BungeeCord proxy
     * A regular forward listener is a listener that listens to messages received on a specific channel
     *
     * @param channelName the channel to listen on
     * @param forwardListener the listener to register
     * @see <a href="https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/#forward">https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/#forward</a>
     */
    public void registerForwardListener(String channelName, ForwardConsumer forwardListener) {
        synchronized (forwardListeners) {
            forwardListeners.put(channelName, forwardListener);
        }
    }

    /**
     * Get the amount of players on a certain server, or on ALL the servers.
     *
     * @param server the server name of the server to get the player count of, or ALL to get the global player count
     * @return A {@link BungeeFuture} that, when completed, will return
     *         the amount of players on a certain server, or on ALL the servers.
     * @throws IllegalArgumentException if there is no players online.
     */
    public BungeeFuture<Integer> getPlayerCount(String server) {
        return sendUTFThenReturnFuture(Channel.PLAYER_COUNT, server, Channel.PLAYER_COUNT, server);
    }

    /**
     * Get a list of players connected on a certain server, or on ALL the servers.
     *
     * @param server the name of the server to get the list of connected players, or ALL for global online player list
     * @return A {@link BungeeFuture} that, when completed, will return a
     *         list of players connected on a certain server, or on ALL the servers.
     * @throws IllegalArgumentException if there is no players online.
     */
    public BungeeFuture<String[]> getPlayerList(String server) {
        return sendUTFThenReturnFuture(Channel.PLAYER_LIST, server, Channel.PLAYER_LIST, server);
    }

    /**
     * Get a list of server name strings, as defined in BungeeCord's config.yml.
     *
     * @return A {@link BungeeFuture} that, when completed, will return a
     *         list of server name strings, as defined in BungeeCord's config.yml.
     * @throws IllegalArgumentException if there is no players online.
     */
    public BungeeFuture<String[]> getServers() {
        return sendNoValUTFThenReturnFuture(Channel.GET_SERVERS, Channel.GET_SERVERS);
    }

    /**
     * Connects a player to said subserver.
     *
     * @param p the player you want to teleport.
     * @param server the name of server to connect to, as defined in BungeeCord config.yml.
     */
    public void connect(Player p, String server) {
        sendPlayerUTF(p, Channel.CONNECT, server);
    }

    /**
     * Connect a named player to said subserver.
     *
     * @param playerName name of the player to teleport.
     * @param server name of server to connect to, as defined in BungeeCord config.yml.
     * @throws IllegalArgumentException if there is no players online.
     */
    public void connectOther(String playerName, String server) {
        sendUTF(Channel.CONNECT_OTHER, playerName, server);
    }

    /**
     * Get the (real) IP of a player.
     *
     * @param p The player you wish to get the IP of.
     * @return A {@link BungeeFuture} that, when completed, will return the (real) IP of {@code player}.
     */
    public BungeeFuture<InetSocketAddress> getIP(Player p) {
        return sendPlayerNoValUTFThenReturnFuture(p, Channel.IP, Channel.IP);
    }

    /**
     * Send a message (as in, a chat message) to the specified player.
     *
     * @param playerName the name of the player to send the chat message.
     * @param message the message to send to the player.
     * @throws IllegalArgumentException if there is no players online.
     */
    public void sendMessage(String playerName, String message) {
        sendUTF(Channel.MESSAGE, playerName, message);
    }

    /**
     * Get this server's name, as defined in BungeeCord's config.yml
     *
     * @return A {@link BungeeFuture} that, when completed, will return
     *         the {@code server's} name, as defined in BungeeCord's config.yml.
     * @throws IllegalArgumentException if there is no players online.
     */
    public BungeeFuture<String> getServer() {
        return sendNoValUTFThenReturnFuture(Channel.GET_SERVER, Channel.GET_SERVER);
    }

    /**
     * Request the UUID of this player.
     *
     * @param p The player whose UUID you requested.
     * @return A {@link BungeeFuture} that, when completed, will return the UUID of {@code player}.
     */
    public BungeeFuture<String> getUUID(Player p) {
        return sendPlayerNoValUTFThenReturnFuture(p, Channel.UUID, Channel.UUID);
    }

    /**
     * Request the UUID of any player connected to the BungeeCord proxy.
     *
     * @param playerName the name of the player whose UUID you would like.
     * @return A {@link BungeeFuture} that, when completed, will return the UUID of {@code playerName}.
     * @throws IllegalArgumentException if there is no players online.
     */
    public BungeeFuture<String> getUUID(String playerName) {
        return sendUTFThenReturnFuture(Channel.UUID_OTHER, playerName, Channel.UUID_OTHER, playerName);
    }

    /**
     * Request the IP of any server on this proxy.
     *
     * @param server the name of the server.
     * @return A {@link BungeeFuture} that, when completed, will return the requested ip.
     * @throws IllegalArgumentException if there is no players online.
     */
    public BungeeFuture<InetSocketAddress> getServerIP(String server) {
        return sendUTFThenReturnFuture(Channel.SERVER_IP, server, Channel.SERVER_IP, server);
    }

    /**
     * Kick any player on this proxy.
     *
     * @param playerName the name of the player.
     * @param message the reason the player is kicked with.
     * @throws IllegalArgumentException if there is no players online.
     */
    public void kickPlayer(String playerName, String message) {
        sendUTF(Channel.KICK_PLAYER, Channel.KICK_PLAYER, playerName, message);
    }

    /**
     * Send a custom plugin message to said server. This is one of the most useful channels ever.
     * <b>Remember, the sending and receiving server(s) need to have a player online.</b>
     *
     * @param server the name of the server to send to,
     *        ALL to send to every server (except the one sending the plugin message),
     *        or ONLINE to send to every server that's online (except the one sending the plugin message).
     *
     * @param channelName Subchannel for plugin usage.
     * @param data data to send.
     * @throws IllegalArgumentException if there is no players online.
     */
    public void forward(String server, String channelName, byte[] data) {
        Player p = confirmPlayer(null);
        ByteArrayDataOutput o = ByteStreams.newDataOutput();

        o.writeUTF(Channel.FORWARD);
        o.writeUTF(server);
        o.writeUTF(channelName);
        o.writeShort(data.length);
        o.write(data);
        p.sendPluginMessage(plugin, Channel.BUNGEE_CORD, o.toByteArray());
    }

    /**
     * Send a custom plugin message to specific player.
     *
     * @param playerName the name of the player to send to.
     * @param channelName Subchannel for plugin usage.
     * @param data data to send.
     * @throws IllegalArgumentException if there is no players online.
     */
    public void forwardToPlayer(String playerName, String channelName, byte[] data) {
        Player p = confirmPlayer(null);
        ByteArrayDataOutput o = ByteStreams.newDataOutput();

        o.writeUTF(Channel.FORWARD_TO_PLAYER);
        o.writeUTF(playerName);
        o.writeUTF(channelName);
        o.writeShort(data.length);
        o.write(data);
        p.sendPluginMessage(plugin, Channel.BUNGEE_CORD, o.toByteArray());
    }

    /**
     * Unregister message channels
     */
    public void unregister() {
        Messenger messenger = Bukkit.getServer().getMessenger();
        messenger.unregisterIncomingPluginChannel(plugin, Channel.BUNGEE_CORD, listener);
        messenger.unregisterOutgoingPluginChannel(plugin);
        callback.clear();
    }

    private void onPluginMessageReceived(String s, Player p, byte[] bytes) {
        if (!s.equals(Channel.BUNGEE_CORD)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();

        synchronized (callback) {
            Queue<BungeeFuture<?>> cbQueue;

            String[] identifier = {Channel.PLAYER_COUNT, Channel.PLAYER_LIST, Channel.UUID_OTHER, Channel.SERVER_IP};

            if (Arrays.stream(identifier).anyMatch(st -> st.equals(subChannel))) {
                String i = in.readUTF();
                cbQueue = callback.get(subChannel + "-" + i);

                if (cbQueue == null || cbQueue.isEmpty()) return;
                final BungeeFuture<?> cb = cbQueue.poll();

                switch(subChannel) {
                    case Channel.PLAYER_LIST:
                        complete(cb, in.readUTF().split(", "));
                        break;
                    case Channel.PLAYER_COUNT:
                        complete(cb, in.readInt());
                        break;
                    case Channel.SERVER_IP:
                        String ip = in.readUTF();
                        int port = in.readUnsignedShort();
                        complete(cb, new InetSocketAddress(ip, port));
                        break;
                    case Channel.UUID_OTHER:
                        complete(cb, in.readUTF());
                        break;

                }
                return;
            }

            cbQueue = callback.get(subChannel);

            if (cbQueue == null) {
                short dataLength = in.readShort();
                byte[] data = new byte[dataLength];
                in.readFully(data);

                if (globalForwardListener != null) {
                    globalForwardListener.accept(subChannel, p, data);
                }

                synchronized (forwardListeners) {
                    ForwardConsumer listener = forwardListeners.get(subChannel);
                    if (listener != null) {
                        listener.accept(subChannel, p, data);
                    }
                }

                return;
            }

            if (cbQueue.isEmpty()) return;
            final BungeeFuture<?> cb = cbQueue.poll();

            switch(subChannel) {
                case Channel.GET_SERVERS:
                    complete(cb, in.readUTF().split(", "));
                    break;
                case Channel.GET_SERVER:
                    complete(cb, in.readUTF());
                    break;
                case Channel.IP:
                    String ip = in.readUTF();
                    int port = in.readInt();
                    complete(cb, new InetSocketAddress(ip, port));
                    break;
                case Channel.UUID:
                    complete(cb, in.readUTF());
                    break;
            }


        }
    }

    private void sendPlayerUTF(Player p, String... args) {
        Player player = confirmPlayer(p);
        ByteArrayDataOutput o = ByteStreams.newDataOutput();
        Arrays.stream(args).forEach(o::writeUTF);
        player.sendPluginMessage(plugin, Channel.BUNGEE_CORD, o.toByteArray());
    }

    private void sendUTF(String... args) {
        sendPlayerUTF(null, args);
    }

    private String format(String key, String val) {
        return key + "-" + val;
    }

    private <T> BungeeFuture<T> sendPlayerUTFThenReturnFuture(Player p, String key, String val, String... args) {
        BungeeFuture<T> f = new BungeeFuture<>();
        synchronized (callback) {
            if (val == null) {
                callback.compute(key, queueVal(f));
            } else callback.compute(format(key, val), queueVal(f));
        }
        sendPlayerUTF(p, args);
        return f;
    }

    private <T> BungeeFuture<T> sendUTFThenReturnFuture(String key, String val, String... args) {
        return sendPlayerUTFThenReturnFuture(null, key, val, args);
    }

    private <T> BungeeFuture<T> sendNoValUTFThenReturnFuture(String key, String... args) {
        return sendPlayerUTFThenReturnFuture(null, key, null, args);
    }

    private <T> BungeeFuture<T> sendPlayerNoValUTFThenReturnFuture(Player p, String key, String... args) {
        return sendPlayerUTFThenReturnFuture(p, key, null, args);
    }

    private Player confirmPlayer(Player p) {
        Player player = p == null ? Iterables.getFirst(Bukkit.getOnlinePlayers(), null) : p;
        if (player == null) throw new IllegalArgumentException("BungeeCord requires at least one player to be online to receive calls");
        return player;
    }

    @SuppressWarnings("unchecked")
    private <T> void complete(BungeeFuture<?> f, T t) {
        try {
            ((BungeeFuture<T>) f).complete(t);
        } catch (Exception e) {
            f.completeExceptionally(e);
        }
    }

    private static BiFunction<String, Queue<BungeeFuture<?>>, Queue<BungeeFuture<?>>> queueVal(BungeeFuture<?> queueValue) {
        return (key, value) -> {
            if (value == null) value = new ArrayDeque<>();
            value.add(queueValue);
            return value;
        };
    }

    private final class Channel {
        private Channel(){}
        private static final String CONNECT = "Connect";
        private static final String CONNECT_OTHER = "ConnectOther";
        private static final String IP = "IP";
        private static final String PLAYER_COUNT = "PlayerCount";
        private static final String PLAYER_LIST = "PlayerList";
        private static final String GET_SERVERS = "GetServers";
        private static final String MESSAGE = "Message";
        private static final String GET_SERVER = "GetServer";
        private static final String FORWARD = "Forward";
        private static final String FORWARD_TO_PLAYER = "ForwardToPlayer";
        private static final String UUID = "UUID";
        private static final String UUID_OTHER = "UUIDOther";
        private static final String SERVER_IP = "ServerIP";
        private static final String KICK_PLAYER = "KickPlayer";
        private static final String BUNGEE_CORD = "BungeeCord";
    }

    /**
     * A consumer that accepts the following arguments to adhere to the forwarded message format
     * detailed on the Spigot plugin messaging channel page
     *
     * @see <a href="https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/#forward">https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/#forward</a>
     */
    @FunctionalInterface
    public interface ForwardConsumer {
        void accept(String channel, Player p, byte[] data);
    }
}