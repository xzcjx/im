package com.xzccc.netty_server.session;

import com.xzccc.netty.model.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Data
public final class SessionMap {
    private static SessionMap singleInstance = new SessionMap();
    // 会话集合
    private ConcurrentHashMap<String, ServerSession> map =
            new ConcurrentHashMap<String, ServerSession>();

    private SessionMap() {
    }

    public static SessionMap inst() {
        return singleInstance;
    }

    /**
     * 增加session对象
     */
    public void addSession(ServerSession s) {
        map.put(s.getSessionId(), s);
        log.info("用户登录:id= " + s.getUser().getId() + "   在线总数: " + map.size());
    }

    /**
     * 获取session对象
     */
    public ServerSession getSession(String sessionId) {
        if (map.containsKey(sessionId)) {
            return map.get(sessionId);
        } else {
            return null;
        }
    }

    /**
     * 根据用户id，获取session对象
     */
    public List<ServerSession> getSessionsBy(String userId) {

        List<ServerSession> list =
                map.values().stream()
                        .filter(s -> s.getUser().getId().equals(userId))
                        .collect(Collectors.toList());
        return list;
    }

    /**
     * 删除session
     */
    public void removeSession(String sessionId) {
        if (!map.containsKey(sessionId)) {
            return;
        }
        ServerSession s = map.get(sessionId);
        map.remove(sessionId);
        if (s.getUser() != null) {
            log.info("用户下线:id= " + s.getUser().getId() + "   在线总数: " + map.size());
        } else {
            log.info("登录失败！！！ " + "   在线总数: " + map.size());
        }
    }

    public boolean hasLogin(User user) {
        Iterator<Map.Entry<String, ServerSession>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ServerSession> next = it.next();
            User u = next.getValue().getUser();
            if (u.getId().equals(user.getId())) {
                return true;
            }
        }

        return false;
    }
}
