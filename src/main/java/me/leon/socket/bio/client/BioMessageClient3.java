package me.leon.socket.bio.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * DataInputStream 和 DataOutputStream 有自己固定的格式，该client暂时还不可用
 */
public class BioMessageClient3 {

    private static Logger logger = LoggerFactory.getLogger(BioMessageClient3.class);

    public static final String IP_ADDR = "localhost";//服务器地址
    public static final int PORT = 18383;//服务器端口号

    public static void main(String[] args) {
        Socket socket = null;
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            socket = new Socket(IP_ADDR, PORT);

            //读取服务器端数据
            DataInputStream input = new DataInputStream(socket.getInputStream());
            //向服务器端发送数据
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            File file = new File("/home/yanlg/test.xml");

            if (!file.exists()) {
                System.out.println("文件不存在！");
            }
            FileReader fr = new FileReader(file);
            BufferedReader bfr = new BufferedReader(fr);
            String s = null;
            StringBuilder sb = new StringBuilder();
            while ((s = bfr.readLine()) != null) {
                sb.append(s).append(System.lineSeparator());
            }

            out.writeUTF(sb.toString());
            String ret = input.readUTF();
            System.out.println("服务器端返回过来的是: " + ret);
            // 如接收到 "OK" 则断开连接
            if ("OK".equals(ret)) {
                System.out.println("客户端将关闭连接");
                Thread.sleep(500);
            }

            out.close();
            input.close();
        } catch (Exception e) {
            logger.error("客户端异常:", e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("客户端 finally 异常:", e);
                }
            }
        }
    }
}
