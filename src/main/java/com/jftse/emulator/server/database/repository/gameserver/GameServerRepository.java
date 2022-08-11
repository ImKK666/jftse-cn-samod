package com.jftse.emulator.server.database.repository.gameserver;

import com.jftse.emulator.server.database.model.gameserver.GameServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameServerRepository extends JpaRepository<GameServer, Long> {
    // 新定义 获取服务器列表时忽略转发服务
    @Query(value = "FROM GameServer gs LEFT JOIN FETCH gs.gameServerType gst WHERE gst.type IS NOT NULL AND gst.type != 4")
    List<GameServer> findAllFetchedExcludeRelayServer();

    @Query(value = "FROM GameServer gs LEFT JOIN FETCH gs.gameServerType gst WHERE gst.type IS NOT NULL")
    List<GameServer> findAllFetched();

    @Query(value = "FROM GameServer gs LEFT JOIN FETCH gs.gameServerType gst WHERE gs.port = :port")
    Optional<GameServer> findGameServerByPort(@Param("port") Integer port);
}
