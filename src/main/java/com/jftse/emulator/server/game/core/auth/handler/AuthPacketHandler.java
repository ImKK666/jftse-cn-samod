package com.jftse.emulator.server.game.core.auth.handler;

import com.jftse.emulator.common.GlobalSettings;
import com.jftse.emulator.server.database.model.account.Account;
import com.jftse.emulator.server.database.model.anticheat.ClientWhitelist;
import com.jftse.emulator.server.database.model.home.AccountHome;
import com.jftse.emulator.server.database.model.item.ItemChar;
import com.jftse.emulator.server.database.model.player.*;
import com.jftse.emulator.server.database.model.pocket.Pocket;
import com.jftse.emulator.server.game.core.packet.packets.S2CDisconnectAnswerPacket;
import com.jftse.emulator.server.game.core.packet.packets.S2CWelcomePacket;
import com.jftse.emulator.server.networking.Connection;
import com.jftse.emulator.server.networking.packet.Packet;
import com.jftse.emulator.server.shared.module.Client;
import com.jftse.emulator.server.game.core.packet.packets.authserver.*;
import com.jftse.emulator.server.game.core.packet.packets.player.*;
import com.jftse.emulator.server.game.core.service.*;
import com.sun.xml.bind.v2.runtime.reflect.Lister;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthPacketHandler {
    private final AuthenticationService authenticationService;
    private final PlayerService playerService;
    private final PocketService pocketService;
    private final HomeService homeService;
    private final ClothEquipmentService clothEquipmentService;
    private final QuickSlotEquipmentService quickSlotEquipmentService;
    private final SpecialSlotEquipmentService specialSlotEquipmentService;
    private final ToolSlotEquipmentService toolSlotEquipmentService;
    private final CardSlotEquipmentService cardSlotEquipmentService;
    private final PlayerStatisticService playerStatisticService;
    private final ItemCharService itemCharService;
    private final ClientWhitelistService clientWhitelistService;

    public void sendWelcomePacket(Connection connection) {
        S2CWelcomePacket welcomePacket = new S2CWelcomePacket(0, 0, 0, 0);
	/*
	log.debug("Sleep 2000 for new connection");
	try {
	  Thread.sleep(2000);
	}
	catch (InterruptedException ex) {
	  ex.printStackTrace();
	}
	*/
        connection.sendTCP(welcomePacket);
    }

    public void handleLoginPacket(Connection connection, Packet packet) {
        C2SLoginPacket loginPacket = new C2SLoginPacket(packet);

        if (GlobalSettings.IsAntiCheatEnabled && !isClientValid(connection.getRemoteAddressTCP(), loginPacket.getHwid())) {
            S2CLoginAnswerPacket loginAnswerPacket = new S2CLoginAnswerPacket(S2CLoginAnswerPacket.INVAILD_VERSION);
            connection.sendTCP(loginAnswerPacket);

            S2CDisconnectAnswerPacket disconnectAnswerPacket = new S2CDisconnectAnswerPacket();
            connection.sendTCP(disconnectAnswerPacket);
            return;
        }

        // 移除客户端版本检查
        /**
         if (loginPacket.getVersion() != 21108180) {
         S2CLoginAnswerPacket loginAnswerPacket = new S2CLoginAnswerPacket(S2CLoginAnswerPacket.INVAILD_VERSION);
         connection.sendTCP(loginAnswerPacket);

         S2CDisconnectAnswerPacket disconnectAnswerPacket = new S2CDisconnectAnswerPacket();
         connection.sendTCP(disconnectAnswerPacket);
         return;
         }
         */
//        Account account = authenticationService.login(loginPacket.getUsername(), loginPacket.getPassword());
        Account account = authenticationService.loginBCrypt(loginPacket.getUsername(), loginPacket.getPassword());

        if (account == null) {
            S2CLoginAnswerPacket loginAnswerPacket = new S2CLoginAnswerPacket(S2CLoginAnswerPacket.ACCOUNT_INVALID_USER_ID);
            connection.sendTCP(loginAnswerPacket);
        } else {
            Integer accountStatus = account.getStatus();
            if (!accountStatus.equals((int) S2CLoginAnswerPacket.SUCCESS)) {
                S2CLoginAnswerPacket loginAnswerPacket = new S2CLoginAnswerPacket(accountStatus.shortValue());
                connection.sendTCP(loginAnswerPacket);
            } else {
                // set last login date
                account.setLastLogin(new Date());
                // mark as logged in
                account.setStatus((int) S2CLoginAnswerPacket.ACCOUNT_ALREADY_LOGGED_IN);
                account = authenticationService.updateAccount(account);

                connection.getClient().setAccount(account);

                S2CLoginAnswerPacket loginAnswerPacket = new S2CLoginAnswerPacket(S2CLoginAnswerPacket.SUCCESS);
                connection.sendTCP(loginAnswerPacket);

                S2CPlayerListPacket PlayerListPacket = new S2CPlayerListPacket(account, account.getPlayerList());
                connection.sendTCP(PlayerListPacket);

                S2CGameServerListPacket gameServerListPacket = new S2CGameServerListPacket(authenticationService.getGameServerList());
                connection.sendTCP(gameServerListPacket);
            }
        }
    }

    public void handleFirstPlayerPacket(Connection connection, Packet packet) {
        C2SFirstPlayerPacket firstPlayerPacket = new C2SFirstPlayerPacket(packet);
        Client client = connection.getClient();

        if (client.getAccount().getPlayerList().isEmpty()) {
            Player player = new Player();
            player.setAccount(client.getAccount());
            player.setPlayerType(firstPlayerPacket.getPlayerType());
            player.setFirstPlayer(true);

            ClothEquipment clothEquipment = new ClothEquipment();
            clothEquipment = clothEquipmentService.save(clothEquipment);
            player.setClothEquipment(clothEquipment);

            QuickSlotEquipment quickSlotEquipment = new QuickSlotEquipment();
            quickSlotEquipment = quickSlotEquipmentService.save(quickSlotEquipment);
            player.setQuickSlotEquipment(quickSlotEquipment);

            SpecialSlotEquipment specialSlotEquipment = new SpecialSlotEquipment();
            specialSlotEquipment = specialSlotEquipmentService.save(specialSlotEquipment);
            player.setSpecialSlotEquipment(specialSlotEquipment);

            ToolSlotEquipment toolSlotEquipment = new ToolSlotEquipment();
            toolSlotEquipment = toolSlotEquipmentService.save(toolSlotEquipment);
            player.setToolSlotEquipment(toolSlotEquipment);

            CardSlotEquipment cardSlotEquipment = new CardSlotEquipment();
            cardSlotEquipment = cardSlotEquipmentService.save(cardSlotEquipment);
            player.setCardSlotEquipment(cardSlotEquipment);

            Pocket pocket = new Pocket();
            pocket = pocketService.save(pocket);
            player.setPocket(pocket);

            PlayerStatistic playerStatistic = new PlayerStatistic();
            playerStatistic = playerStatisticService.save(playerStatistic);
            player.setPlayerStatistic(playerStatistic);

            player = playerService.save(player);

            AccountHome accountHome = new AccountHome();
            accountHome.setAccount(client.getAccount());
            accountHome = homeService.save(accountHome);

            S2CFirstPlayerAnswerPacket firstPlayerAnswerPacket = new S2CFirstPlayerAnswerPacket((char) 0, player.getId(), player.getPlayerType());
            connection.sendTCP(firstPlayerAnswerPacket);
        } else {
            S2CFirstPlayerAnswerPacket firstPlayerAnswerPacket = new S2CFirstPlayerAnswerPacket((char) -1, 0L, (byte) 0);
            connection.sendTCP(firstPlayerAnswerPacket);
        }
    }

    // 使用正则表达式匹配不安全的字符，避免客户端的检查机制被恶意绕过
    public Boolean isPlayerNameValid(String nickname) {
        String regex = "[.\\\"\\'\\[\\]{}$%^&*()!@`#\\\\\\/]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nickname);
        if (!matcher.matches() && playerService.findByName(nickname) == null) {
            return true;
        } else {
            return false;
        }
    }

    public void handlePlayerNameCheckPacket(Connection connection, Packet packet) {
        C2SPlayerNameCheckPacket playerNameCheckPacket = new C2SPlayerNameCheckPacket(packet);
        String nickname = playerNameCheckPacket.getNickname();
        if (isPlayerNameValid(nickname)) {
            S2CPlayerNameCheckAnswerPacket playerNameCheckAnswerPacket = new S2CPlayerNameCheckAnswerPacket((char) 0);
            connection.sendTCP(playerNameCheckAnswerPacket);
        } else {
            S2CPlayerNameCheckAnswerPacket playerNameCheckAnswerPacket = new S2CPlayerNameCheckAnswerPacket((char) -1);
            connection.sendTCP(playerNameCheckAnswerPacket);
        }
    }

    public void handlePlayerCreatePacket(Connection connection, Packet packet) {

        C2SPlayerCreatePacket playerCreatePacket = new C2SPlayerCreatePacket(packet);
        if (!isPlayerNameValid(playerCreatePacket.getNickname())) {
            S2CPlayerCreateAnswerPacket playerCreateAnswerPacket = new S2CPlayerCreateAnswerPacket((char) -1);
            connection.sendTCP(playerCreateAnswerPacket);
        } else {
            Player player = playerService.findByIdFetched((long) playerCreatePacket.getPlayerId());
            if (player == null) {
                S2CPlayerCreateAnswerPacket playerCreateAnswerPacket = new S2CPlayerCreateAnswerPacket((char) -1);
                connection.sendTCP(playerCreateAnswerPacket);
            } else {
                player.setName(playerCreatePacket.getNickname());
                player.setAlreadyCreated(true);

                if (playerService.isStatusPointHack(playerCreatePacket, player)) {
                    ItemChar itemChar = itemCharService.findByPlayerType(player.getPlayerType());

                    player.setStrength(itemChar.getStrength());
                    player.setStamina(itemChar.getStamina());
                    player.setDexterity(itemChar.getDexterity());
                    player.setWillpower(itemChar.getWillpower());

                    player.setStatusPoints((byte) (player.getStatusPoints() + 20));
                } else {
                    player.setStrength(playerCreatePacket.getStrength());
                    player.setStamina(playerCreatePacket.getStamina());
                    player.setDexterity(playerCreatePacket.getDexterity());
                    player.setWillpower(playerCreatePacket.getWillpower());

                    player.setStatusPoints((byte) (playerCreatePacket.getStatusPoints() + 20));
                }

                // 新角色初始等级、经验、金钱
                player.setStatusPoints((byte) (playerCreatePacket.getStatusPoints() + 64));
                player.setLevel((byte) 60);
                player.setExpPoints(259937);
                player.setGold(20000);

                player = playerService.save(player);

                if (homeService.findAccountHomeByAccountId(player.getAccount().getId()) == null) {
                    AccountHome accountHome = new AccountHome();
                    accountHome.setAccount(player.getAccount());

                    accountHome = homeService.save(accountHome);
                }

                S2CPlayerCreateAnswerPacket playerCreateAnswerPacket = new S2CPlayerCreateAnswerPacket((char) 0);
                connection.sendTCP(playerCreateAnswerPacket);

                StatusPointsAddedDto statusPointsAddedDto = clothEquipmentService.getStatusPointsFromCloths(player);

                S2CPlayerStatusPointChangePacket playerStatusPointChangePacket = new S2CPlayerStatusPointChangePacket(player, statusPointsAddedDto);
                connection.sendTCP(playerStatusPointChangePacket);
            }
        }
    }

    public void handlePlayerDeletePacket(Connection connection, Packet packet) {
        C2SPlayerDeletePacket playerDeletePacket = new C2SPlayerDeletePacket(packet);

        Player player = playerService.findById((long) playerDeletePacket.getPlayerId());
        if (player != null) {

            connection.getClient().getAccount().getPlayerList().removeIf(pl -> pl.getId().equals(player.getId()));
            playerService.remove(player.getId());

            S2CPlayerDeleteAnswerPacket playerDeleteAnswerPacket = new S2CPlayerDeleteAnswerPacket((char) 0);
            connection.sendTCP(playerDeleteAnswerPacket);

            S2CPlayerListPacket playerListPacket = new S2CPlayerListPacket(connection.getClient().getAccount(), connection.getClient().getAccount().getPlayerList());
            connection.sendTCP(playerListPacket);
        } else {
            S2CPlayerDeleteAnswerPacket playerDeleteAnswerPacket = new S2CPlayerDeleteAnswerPacket((char) -1);
            connection.sendTCP(playerDeleteAnswerPacket);
        }
    }

    public void handleAuthServerLoginPacket(Connection connection, Packet packet) {

        C2SAuthLoginPacket authLoginPacket = new C2SAuthLoginPacket(packet);

        Account account = authenticationService.findAccountByUsername(authLoginPacket.getUsername());
        if (account != null) {
            account.setStatus((int) S2CLoginAnswerPacket.ACCOUNT_ALREADY_LOGGED_IN);
            account = authenticationService.updateAccount(account);

            connection.getClient().setAccount(account);

            S2CAuthLoginPacket authLoginAnswerPacket = new S2CAuthLoginPacket((char) 0, (byte) 1);
            connection.sendTCP(authLoginAnswerPacket);

            S2CPlayerListPacket PlayerListPacket = new S2CPlayerListPacket(account, account.getPlayerList());
            connection.sendTCP(PlayerListPacket);
        } else {
            S2CAuthLoginPacket authLoginAnswerPacket = new S2CAuthLoginPacket((char) -1, (byte) 0);
            connection.sendTCP(authLoginAnswerPacket);
        }
    }

    // 目前还不能正常工作
    public void handlePlayerRenamePacket(Connection connection, Packet packet) {
        C2SPlayerRenamePacket renamePacket = new C2SPlayerRenamePacket(packet);
        log.debug(String.format("Rename id[%d] old[%s] new[%s]",
                renamePacket.getPlayerId(), renamePacket.getCurrentPlayerName(), renamePacket.getNewPlayerName()));
            connection.sendTCP(new S2CPlayerRenameAnswerPacket((char) 1, renamePacket.getPlayerId(), renamePacket.getNewPlayerName()));
            connection.sendTCP(new S2CPlayerRenameEndPacket((char) 0,(byte) 1));
    }

    public void handleDisconnectPacket(Connection connection, Packet packet) {
        if (connection.getClient().getAccount() != null) {
            // reset status
            Account account = authenticationService.findAccountById(connection.getClient().getAccount().getId());
            account.setStatus((int) S2CLoginAnswerPacket.SUCCESS);
            authenticationService.updateAccount(account);
        }

        S2CDisconnectAnswerPacket disconnectAnswerPacket = new S2CDisconnectAnswerPacket();
        connection.sendTCP(disconnectAnswerPacket);
    }

    public void handleDisconnected(Connection connection) {
        if (connection.getClient().getAccount() != null) {
            // reset status
            Account account = authenticationService.findAccountById(connection.getClient().getAccount().getId());
            account.setStatus((int) S2CLoginAnswerPacket.SUCCESS);
            authenticationService.updateAccount(account);
        }

        connection.setClient(null);
        connection.close();
    }

    public void handleUnknown(Connection connection, Packet packet) {
        Packet unknownAnswer = new Packet((char) (packet.getPacketId() + 1));
        unknownAnswer.write((short) 0);
        connection.sendTCP(unknownAnswer);
    }

    private boolean isClientValid(InetSocketAddress inetSocketAddress, String hwid) {
        if (inetSocketAddress == null)
            return false;
        String hostAddress = inetSocketAddress.getAddress().getHostAddress();
        ClientWhitelist clientWhitelist = clientWhitelistService.findByIpAndHwid(hostAddress, hwid);
        return clientWhitelist != null;
    }
}
