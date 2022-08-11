package com.jftse.emulator.server.game.core.packet.packets.player;

import com.jftse.emulator.server.database.model.account.Account;
import com.jftse.emulator.server.database.model.player.ClothEquipment;
import com.jftse.emulator.server.database.model.player.Player;
import com.jftse.emulator.server.database.model.pocket.PlayerPocket;
import com.jftse.emulator.server.game.core.packet.PacketID;
import com.jftse.emulator.server.networking.packet.Packet;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class S2CPlayerListPacket extends Packet {
    public S2CPlayerListPacket(Account account, List<Player> playerList) {
        super(PacketID.S2CPlayerList);

        this.write(0);
        this.write(0);
        this.write(Math.toIntExact(account.getId()));
        this.write((byte) 0);
        this.write(account.getGameMaster()); // GM

        if (playerList != null) {
            this.write((byte) playerList.size());

            for (Player player : playerList) {

                this.write(Math.toIntExact(player.getId()));
                this.write(player.getName());
                this.write(player.getLevel());
                this.write(player.getAlreadyCreated());
                this.write(!player.getFirstPlayer()); // forCharacter delete: true/false
                this.write(player.getGold());
                this.write(player.getPlayerType());
                this.write(player.getStrength());
                this.write(player.getStamina());
                this.write(player.getDexterity());
                this.write(player.getWillpower());
                this.write(player.getStatusPoints());
                this.write(player.getNameChangeAllowed()); // old, "Change Nickname"
                Boolean t=player.haveNameChangeItem();
                this.write(t); // new, change nickname item/icon
                log.debug("Have name change item="+t);


                ClothEquipment clothEquipment = player.getClothEquipment();
                this.write(clothEquipment.getHair());
                this.write(clothEquipment.getFace());
                this.write(clothEquipment.getDress());
                this.write(clothEquipment.getPants());
                this.write(clothEquipment.getSocks());
                this.write(clothEquipment.getShoes());
                this.write(clothEquipment.getGloves());
                this.write(clothEquipment.getRacket());
                this.write(clothEquipment.getGlasses());
                this.write(clothEquipment.getBag());
                this.write(clothEquipment.getHat());
                this.write(clothEquipment.getDye());
            }
        }
    }
}
