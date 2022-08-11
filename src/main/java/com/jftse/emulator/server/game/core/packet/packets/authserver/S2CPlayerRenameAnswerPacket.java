package com.jftse.emulator.server.game.core.packet.packets.authserver;

import com.jftse.emulator.server.game.core.packet.PacketID;
import com.jftse.emulator.server.networking.packet.Packet;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S2CPlayerRenameAnswerPacket extends Packet {

    public S2CPlayerRenameAnswerPacket(char result,int playerID,String nickName){
        super(PacketID.S2CPlayerRenameAnswer);
        this.write(result);
        this.write(playerID);
        this.write(nickName);
    }
}
