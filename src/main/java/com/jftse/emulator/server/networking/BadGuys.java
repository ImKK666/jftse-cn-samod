package com.jftse.emulator.server.networking;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
public class BadGuys {
    private HashMap<String, Long> connectTimeMillisMap = new HashMap<>();
    private HashMap<String, Integer> suspectMap = new HashMap<>();
    private HashMap<String, Long> badGuysTimeMillisMap = new HashMap<>();
    private final int banDuration = 2*60*1000;

    private Runnable badGuysReview = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    if (badGuysTimeMillisMap.size() > 0) {
                        log.debug("目前有 {} 个IP处于封禁状态", badGuysTimeMillisMap.size());
                        Long currentTime = System.currentTimeMillis();
                        List<String> unbans = new ArrayList<>();
                        badGuysTimeMillisMap.forEach((k, v) -> {
                            long bannedTime = currentTime - v;
                            if (bannedTime > banDuration)
                                unbans.add(k);
                            else
                                log.debug("BAN " + k + " (" + (int) ((banDuration - bannedTime) / 1000) + " 秒后解封)");
                        });
                        unbans.forEach(k -> {
                            try {
                                badGuysTimeMillisMap.remove(k);
                                AliyunSecurity.revokeSecurityGroup(k);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                            log.info(k + " 已解除封禁");
                        });
                    } else {
                        //log.debug("目前没有被封禁的IP");
                    }
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void runBadGuysReview(String threadName) {
        Thread badGuysReviewThread = new Thread(badGuysReview);
        badGuysReviewThread.setName(threadName);
        badGuysReviewThread.start();
    }

    public Boolean isBadGuy(String ip) {
        if (ip != null && !ip.equals("127.0.0.1")) {
            long currentTime = System.currentTimeMillis();
            //log.debug("检查来自 " + ip + " 的连接");
            if (checkBadGuy(ip)) {
                return true;
            } else {
                if (connectTimeMillisMap.containsKey(ip)) {
                    //log.debug("检查 " + ip + " 的连接记录");
                    long lastTime = connectTimeMillisMap.get(ip);
                    int interval = 300;
                    int maxSuspectTimes = 10;
                    if (currentTime - lastTime < interval) {
                        //log.debug(ip + " CURR " + currentTime + " | LAST " + lastTime);
                        //log.debug(ip + " 连接请求间隔不足" + interval + "ms");
                        if (checkSuspect(ip) >= maxSuspectTimes) {
                            setBadGuy(ip, currentTime);
                            return true;
                        }
                    } else {
                        //log.debug(ip + " 的连接间隔大于" + interval + "ms,解除嫌疑");
                        clearSuspect(ip);
                    }
                    connectTimeMillisMap.replace(ip, currentTime);
                } else {
                    //log.debug(ip + " 没有连接记录");
                    connectTimeMillisMap.put(ip, currentTime);
                }
            }
        }
        return false;
    }

    public Boolean isBadGuy(Connection connection) {
        return isBadGuy(getRemoteIP(connection));
    }

    private int checkSuspect(String ip) {
        int offendTimes = 1;
        if (suspectMap.containsKey(ip)) {
            offendTimes = suspectMap.get(ip) + 1;
            suspectMap.replace(ip, offendTimes);
        } else {
            suspectMap.put(ip, offendTimes);
        }
        log.debug(ip + " 已连续触发嫌疑检测 " + offendTimes + " 次");
        return offendTimes;
    }

    private void clearSuspect(String ip) {
        if (suspectMap.containsKey(ip)) {
            suspectMap.remove(ip);
            log.debug(ip + " 的嫌疑已解除");
        }
    }

    private void setBadGuy(String ip, long time) {
        if (badGuysTimeMillisMap.containsKey(ip)) {
            badGuysTimeMillisMap.replace(ip, time);
            //log.debug(ip + "已延长封禁时间");
        } else {
            try {
                badGuysTimeMillisMap.put(ip, time);
                AliyunSecurity.authorizeSecurityGroup(ip);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            log.info(ip + " 已列入封禁表");
        }
    }

    private Boolean checkBadGuy(String ip) {
        if (badGuysTimeMillisMap.containsKey(ip)) {
            badGuysTimeMillisMap.replace(ip, System.currentTimeMillis());
            return true;
        } else {
            return false;
        }
    }

    private String getRemoteIP(Connection connection) {
        return connection.getRemoteAddressTCP().getAddress().getHostAddress();
    }

}
