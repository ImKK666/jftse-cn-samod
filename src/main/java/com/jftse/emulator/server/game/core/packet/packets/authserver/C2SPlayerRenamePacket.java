package com.jftse.emulator.server.game.core.packet.packets.authserver;

import com.jftse.emulator.server.networking.packet.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class C2SPlayerRenamePacket extends Packet {
    private int playerId;
    private String currentPlayerName;
    private String newPlayerName;

    public C2SPlayerRenamePacket(Packet packet){
        super(packet);

        this.playerId=this.readInt();
        this.currentPlayerName=this.readUnicodeString();
        this.newPlayerName=this.readUnicodeString();
    }
}
