package me.leon.socket.bio.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class MessageHanlder3 implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(MessageHanlder3.class);

    private Socket incoming; // 收到的链接

    public MessageHanlder3(Socket incoming) {
        this.incoming = incoming;
    }

    @Override
    public void run() {
        DataOutputStream out = null;
        DataInputStream in = null;
        String message = null;
        String returnMessage = null;
        try {
            out = new DataOutputStream(incoming.getOutputStream()); // 获取Socket输出流，用于写出响应信息
            in = new DataInputStream(incoming.getInputStream());
            String s = null;
            message = in.readUTF();

            if (message == null || "".equals(message)) {
                logger.error("接收到的报文长度为0，没有任何报文");
            } else {
                logger.info("接收到的报文：" + message);
            }
            returnMessage = "success";
        } catch (Exception e) {
            returnMessage = "error";
            e.printStackTrace();
        } finally {
            logger.info("交易调用结束  返回报文;[{}]", returnMessage);
            try {
                out.writeUTF(returnMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SocketAddress sd = incoming.getRemoteSocketAddress();
            logger.info("第三方客户端：{} 已断开！", sd);

            try {
                if (incoming != null) {
                    incoming.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}