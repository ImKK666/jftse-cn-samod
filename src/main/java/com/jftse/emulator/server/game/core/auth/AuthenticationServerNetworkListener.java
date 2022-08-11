package com.jftse.emulator.server.game.core.auth;

import com.jftse.emulator.server.game.core.auth.handler.AuthPacketHandler;
import com.jftse.emulator.server.game.core.packet.PacketID;
import com.jftse.emulator.server.database.model.account.Account;
import com.jftse.emulator.server.database.repository.account.AccountRepository;
import com.jftse.emulator.server.game.core.auth.handler.AuthPacketHandler;
import com.jftse.emulator.server.game.core.packet.PacketID;
import com.jftse.emulator.server.game.core.packet.packets.authserver.S2CLoginAnswerPacket;
import com.jftse.emulator.server.networking.Connection;
import com.jftse.emulator.server.networking.ConnectionListener;
import com.jftse.emulator.server.networking.packet.Packet;
import com.jftse.emulator.server.shared.module.Client;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
public class AuthenticationServerNetworkListener implements ConnectionListener {
    @Autowired
    private AuthPacketHandler authPacketHandler;
    @Autowired
    private AccountRepository accountRepository;

    public void cleanUp() {
        // empty..
    }

    public void connected(Connection connection) {
        Client client = new Client();
        client.setConnection(connection);

        connection.setClient(client);
        authPacketHandler.sendWelcomePacket(connection);
    }

    public void disconnected(Connection connection) {
        authPacketHandler.handleDisconnected(connection);
    }

    public void received(Connection connection, Packet packet) {
        switch (packet.getPacketId()) {
            case PacketID.C2SLoginRequest:
                authPacketHandler.handleLoginPacket(connection, packet);
                break;

            case PacketID.C2SDisconnectRequest:
                authPacketHandler.handleDisconnectPacket(connection, packet);
                break;

            case PacketID.C2SLoginFirstPlayerRequest:
                authPacketHandler.handleFirstPlayerPacket(connection, packet);
                break;

            case PacketID.C2SPlayerNameCheck:
                authPacketHandler.handlePlayerNameCheckPacket(connection, packet);
                break;

            case PacketID.C2SPlayerCreate:
                authPacketHandler.handlePlayerCreatePacket(connection, packet);
                break;

            case PacketID.C2SPlayerDelete:
                authPacketHandler.handlePlayerDeletePacket(connection, packet);
                break;

            case PacketID.C2SAuthLoginData:
                authPacketHandler.handleAuthServerLoginPacket(connection, packet);
                break;

            case PacketID.C2SPlayerRenameRequest:
                authPacketHandler.handlePlayerRenamePacket(connection, packet);
                break;

            case PacketID.C2SHeartbeat:
            case PacketID.C2SLoginAliveClient:
            case 0xE00E:
                break;

            default:
                authPacketHandler.handleUnknown(connection, packet);
                break;
        }
    }

    public void idle(Connection connection) {
        // empty..
    }

    public void onException(Connection connection, Exception exception) {
        switch ("" + exception.getMessage()) {
            case "Connection is closed.":
            case "Connection reset by peer":
            case "Broken pipe":
                break;
            default:
                log.error(exception.getMessage(), exception);
        }
    }

    // Set all login accounts free
    // 把所有具有已登录状态的账号重置到未登录
    public void resetAllLoginStatus() {
        log.debug("检查账号状态...");
        List<Account> accounts = accountRepository.findByStatus(S2CLoginAnswerPacket.ACCOUNT_ALREADY_LOGGED_IN);
        log.info("已登录账号数量 [{}]", accounts.size());
        for (Account account : accounts) {
            account.setStatus(0);
            accountRepository.save(account);
            log.info("已将账号 [{}] 的登录状态重置为 [0]", account.getUsername());
        }
    }

    public void onTimeout(Connection connection) {
        connection.close();
    }
}
