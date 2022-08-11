package com.jftse.emulator.server.game.core.service;

import com.jftse.emulator.server.database.model.account.Account;
import com.jftse.emulator.server.database.model.gameserver.GameServer;
import com.jftse.emulator.server.database.repository.account.AccountRepository;
import com.jftse.emulator.server.database.repository.gameserver.GameServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE)
public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final GameServerRepository gameServerRepository;

    public Account login(String username, String password) {
        List<Account> accountList = accountRepository.findByUsernameAndPassword(username, password);

        return (accountList == null || accountList.isEmpty()) ? null : accountList.get(0);
    }

    // 以BCrypt hash方式校验密码
    public Account loginBCrypt(String username, String password) {
        try {
            List<Account> accountList = accountRepository.findByUsername(username);
            if (accountList == null || accountList.isEmpty()) {
                return null;
            } else {
                String hashedPassword = accountList.get(0).getPassword();
                hashedPassword = "$2a" + hashedPassword.substring(3);
                Boolean result = BCrypt.checkpw(password, hashedPassword);
                if(result)
		    return accountList.get(0);
		else
		    return null;
            }
        } catch (Exception exception) {
            log.error(exception.toString() + exception.getStackTrace().toString());
            return null;
        }
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account findAccountByUsername(String username) {
        Optional<Account> account = accountRepository.findAccountByUsername(username);
        return account.orElse(null);
    }

    public Account findAccountById(Long id) {
        Optional<Account> account = accountRepository.findAccountById(id);
        return account.orElse(null);
    }

    public List<GameServer> getGameServerList() {
        // 获取服务器列表时忽略转发服务
        return gameServerRepository.findAllFetchedExcludeRelayServer();
    }

    public GameServer getGameServerByPort(Integer port) {
        Optional<GameServer> gameServer = gameServerRepository.findGameServerByPort(port);
        return gameServer.orElse(null);
    }
}
