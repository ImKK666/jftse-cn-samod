package com.jftse.emulator.server.game.core.packet.packets.authserver;

import com.jftse.emulator.server.game.core.packet.PacketID;
import com.jftse.emulator.server.networking.packet.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S2CPlayerRenameEndPacket extends Packet {

    public S2CPlayerRenameEndPacket(char result,byte unk0){
        super(PacketID.S2CPlayerRenameEnd);
        this.write(result);
        this.write(unk0);
    }
}
