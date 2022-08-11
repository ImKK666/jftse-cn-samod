package com.jftse.emulator.server.game.core.packet.packets.guild;

import com.jftse.emulator.server.networking.packet.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SGuildChangeMasterRequestPacket extends Packet {
    private int playerId;

    public C2SGuildChangeMasterRequestPacket(Packet packet) {
        super(packet);

        this.playerId = this.readInt();
    }
}