package com.jftse.emulator.server.game.core.packet.packets.guild;

import com.jftse.emulator.server.networking.packet.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SGuildReserveMemberDataRequestPacket extends Packet {

    public C2SGuildReserveMemberDataRequestPacket(Packet packet) {
        super(packet);
    }
}