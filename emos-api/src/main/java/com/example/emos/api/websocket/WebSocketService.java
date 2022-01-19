package com.example.emos.api.websocket;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket服务类
 * 这个类是多例的，每次有客户端用WebSocket连接这个类，Spring就会创建一个新的WebSocketService对象，所以不用担心线程安全的问题。
 */
@Component
@ServerEndpoint(value = "/socket")
@Slf4j
public class WebSocketService {

    // 用于保存WebSocket连接对象
    public static ConcurrentHashMap<String,Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        Map map = session.getUserProperties();
        if (map.containsKey("userId")) {
            String userId = MapUtil.getStr(map, "userId");
            sessionMap.remove(userId);
        }
    }

    /**
     * 接收消息
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        //把字符串转换成JSON
        JSONObject json = JSONUtil.parseObj(message);
        String opt = json.getStr("opt");
        // 避免websocket超市
        if("ping".equals(opt)){
            return;
        }
        //从JSON中取出Token
        String token = json.getStr("token");
        //从Token取出userId
        String userId = StpUtil.stpLogic.getLoginIdByToken(token).toString();

        //取出Session绑定的属性
        Map map = session.getUserProperties();
        //如果没有userId属性，就给Session绑定userId属性，关闭连接的时候会用到
        if (!map.containsKey("userId")) {
            map.put("userId", userId);
        }
        //把Session缓存起来
        if (sessionMap.containsKey(userId)) {
            //替换缓存中的Session
            sessionMap.replace(userId, session);
        } else {
            //向缓存添加Session
            sessionMap.put(userId, session);
        }
        sendInfo("ok",userId);

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误", error);
    }

    /**
     * 发送消息给客户端
     */
    public static void sendInfo(String message, String userId) {
        if (StrUtil.isNotBlank(userId) && sessionMap.containsKey(userId)) {
            //从缓存中查找到Session对象
            Session session = sessionMap.get(userId);
            //发送消息
            sendMessage(message, session);
        }
    }

    /**
     * 封装发送消息给客户端
     */
    private static void sendMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("执行异常", e);
        }
    }
}
