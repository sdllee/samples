package me.leon.socket.bio.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BioMessageServer3 {

    private static Logger logger = LoggerFactory.getLogger(BioMessageServer3.class);

    private static int server_port = 18383;

    public static void main(String[] args) {
        ServerSocket s = null;
        try {
            int i = 1; // 线程计数器
            // 通过配置文件取端口号
            s = new ServerSocket(server_port, 100);
            logger.debug("报文通讯服务端启动");
            while (true) {
                Socket incoming = s.accept(); // 服务器监听等待链接，将阻塞线程！
                Runnable r = new MessageHanlder3(incoming); // 链接成功， 实例化线程接口
                Thread t = new Thread(r); // 实例化线程
                t.start(); // 启动线程
                i++; // 计数器加1
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                s = null;
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

