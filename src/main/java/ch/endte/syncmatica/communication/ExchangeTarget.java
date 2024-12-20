package ch.endte.syncmatica.communication;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.communication.exchange.Exchange;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// since Client/Server PlayNetworkHandler are 2 different classes, but I want to use exchanges
// on both without having to recode them individually I have an adapter class here

public class ExchangeTarget {
    public final ClientPlayNetworkHandler clientPlayNetworkHandler;
    public final ServerPlayNetworkHandler serverPlayNetworkHandler;
    private final String persistentName;

    private FeatureSet features;
    private final List<Exchange> ongoingExchanges = new ArrayList<>(); // implicitly relies on priority

    public ExchangeTarget(ClientPlayNetworkHandler clientPlayNetworkHandler) {
        this.clientPlayNetworkHandler = clientPlayNetworkHandler;
        this.serverPlayNetworkHandler = null;
        this.persistentName = StringUtils.getWorldOrServerName();
    }

    public ExchangeTarget(ServerPlayNetworkHandler serverPlayNetworkHandler) {
        this.clientPlayNetworkHandler = null;
        this.serverPlayNetworkHandler = serverPlayNetworkHandler;
        this.persistentName = serverPlayNetworkHandler.player.getUuidAsString();
    }

    // this application exclusively communicates in CustomPayLoad packets
    // this class handles the sending of either S2C or C2S packets
    public void sendPacket(final Identifier id, final PacketByteBuf packetBuf, final Context context) {
        if (clientPlayNetworkHandler != null) {
        PacketByteBuf packetBufCopy = new PacketByteBuf(packetBuf.copy()); // 假设有一个接受 ByteBuf 的构造器
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(id, packetBufCopy);
        clientPlayNetworkHandler.sendPacket(packet);
    }

    if (serverPlayNetworkHandler != null) {
        PacketByteBuf packetBufCopy = new PacketByteBuf(packetBuf.copy()); // 假设有一个接受 ByteBuf 的构造器
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(id, packetBufCopy);
        serverPlayNetworkHandler.sendPacket(packet);
    }
}

    // removed equals code due to issues with Collection.contains

    public FeatureSet getFeatureSet() {
        return features;
    }

    public void setFeatureSet(final FeatureSet f) {
        features = f;
    }

    public Collection<Exchange> getExchanges() {
        return ongoingExchanges;
    }

    public String getPersistentName() {
        return persistentName;
    }

    public boolean isServer() {
        return serverPlayNetworkHandler != null;
    }

    public boolean isClient() {
        return clientPlayNetworkHandler != null;
    }
}
