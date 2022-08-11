package com.jftse.emulator;

import com.jftse.emulator.common.GlobalSettings;
import com.jftse.emulator.common.discord.DiscordWebhook;
import com.jftse.emulator.server.game.core.anticheat.AntiCheatHeartBeatNetworkListener;
import com.jftse.emulator.server.game.core.auth.AuthenticationServerNetworkListener;
import com.jftse.emulator.server.game.core.game.GameServerNetworkListener;
import com.jftse.emulator.server.game.core.game.RelayServerNetworkListener;
import com.jftse.emulator.server.networking.Server;
import com.jftse.emulator.server.shared.module.checker.AuthServerChecker;
import com.jftse.emulator.server.shared.module.checker.GameServerChecker;
import com.jftse.emulator.server.shared.module.checker.RelayServerChecker;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Log4j2
public class StartApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(StartApplication.class, args);

        log.info("认证服务器 初始化中...");
        AuthenticationServerNetworkListener authenticationNetworkListener = new AuthenticationServerNetworkListener();
        // post dependency injection for this class
        ctx.getBeanFactory().autowireBean(authenticationNetworkListener);

        Server authenticationServer = new Server();
        authenticationServer.addListener(authenticationNetworkListener);
        // Reset login status for all accounts
        // 重置所有账号的登录状态
        authenticationNetworkListener.resetAllLoginStatus();

        try {
            authenticationServer.bind(5894);
        }
        catch (IOException ioe) {
            log.error("认证服务器 初始化失败!");
            ioe.printStackTrace();
            ctx.close();
            System.exit(1);
        }
        authenticationServer.start("auth server");

        log.info("初始化成功!");
        log.info("--------------------------------------");

        log.info("游戏服务器 初始化中...");
        GameServerNetworkListener gameServerNetworkListener = new GameServerNetworkListener();
        // post dependency injection for this class
        ctx.getBeanFactory().autowireBean(gameServerNetworkListener);

        Server gameServer = new Server();
        gameServer.addListener(gameServerNetworkListener);

        try {
            gameServer.bind(5895);
        }
        catch (IOException ioe) {
            log.error("游戏服务器 初始化失败!");
            ioe.printStackTrace();
            ctx.close();
            System.exit(1);
        }
        gameServer.start("game server");

        log.info("初始化成功!");
        log.info("--------------------------------------");

        log.info("中继服务器 初始化中...");
        RelayServerNetworkListener relayServerNetworkListener = new RelayServerNetworkListener();
        // post dependency injection for this class
        ctx.getBeanFactory().autowireBean(relayServerNetworkListener);

        Server relayServer = new Server();
        relayServer.addListener(relayServerNetworkListener);

        try {
            relayServer.bind(5896);
        } catch (IOException ioe) {
            log.error("中继服务器 初始化失败!");
            ioe.printStackTrace();
            ctx.close();
            System.exit(1);
        }
        relayServer.start("relay server");

        log.info("初始化成功!");
        log.info("--------------------------------------");

        log.info("由于功能性问题，临时禁用了反作弊服务器!");
        log.info("--------------------------------------");
        // 由于运行不正常，临时禁用了反作弊
        /*
        AntiCheatHeartBeatNetworkListener antiCheatHeartBeatNetworkListener;
        Server antiCheatServer;
        if (GlobalSettings.IsAntiCheatEnabled) {
            log.info("Initializing anti cheat heartbeat server...");
            antiCheatHeartBeatNetworkListener = new AntiCheatHeartBeatNetworkListener();

            // post dependency injection for this class
            ctx.getBeanFactory().autowireBean(antiCheatHeartBeatNetworkListener);

            antiCheatServer = new Server();
            antiCheatServer.addListener(antiCheatHeartBeatNetworkListener);

            try {
                antiCheatServer.bind(1337); // adjustable
            }
            catch (IOException ioe) {
                log.error("Failed to start anti cheat heartbeat server!");
                ioe.printStackTrace();
                ctx.close();
                System.exit(1);
            }
            antiCheatServer.start("anti cheat server");

            log.info("Successfully initialized!");
            log.info("--------------------------------------");
        }
        */

        log.info("服务端启动成功!");

        DiscordWebhook discordWebhook = new DiscordWebhook(""); // empty till global config table created
        discordWebhook.setContent("Login Server is online.\nGame Server is online.\nMatchmaking Server is online.");
        try {
            discordWebhook.execute();
        } catch (IOException e) {
            log.error("DiscordWebhook error: ", e);
        }

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        executor.scheduleWithFixedDelay(new AuthServerChecker(authenticationServer), 0, 5, TimeUnit.MINUTES);
        executor.scheduleWithFixedDelay(new GameServerChecker(gameServer, relayServer, gameServerNetworkListener, relayServerNetworkListener), 0, 2, TimeUnit.MINUTES);
        executor.scheduleWithFixedDelay(new RelayServerChecker(relayServer, relayServerNetworkListener), 0, 5, TimeUnit.MINUTES);

        Scanner scan = new Scanner(System.in);
        String input;
        while (true) {
            input = scan.next();

            if (input.equals("exit"))
                break;
        }

        log.info("正在关闭服务端...");

        gameServerNetworkListener.cleanUp();
        relayServerNetworkListener.cleanUp();

//        if (GlobalSettings.IsAntiCheatEnabled) {
//            antiCheatHeartBeatNetworkListener.cleanUp();
//        }

        executor.shutdown();

        try {
            authenticationServer.dispose();
            gameServer.dispose();
            relayServer.dispose();

//            if (GlobalSettings.IsAntiCheatEnabled) {
//                antiCheatServer.dispose();
//            }
        }
        catch (IOException ioe) {
            log.error(ioe.getMessage());
            ctx.close();
            System.exit(1);
        }
        ctx.close();
        System.exit(1);
    }
}
