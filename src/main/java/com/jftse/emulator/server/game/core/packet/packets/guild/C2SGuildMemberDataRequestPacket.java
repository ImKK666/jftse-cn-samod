package com.jftse.emulator.server.game.core.packet.packets.guild;

import com.jftse.emulator.server.networking.packet.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SGuildMemberDataRequestPacket extends Packet {

    public C2SGuildMemberDataRequestPacket(Packet packet) {
        super(packet);
    }
}