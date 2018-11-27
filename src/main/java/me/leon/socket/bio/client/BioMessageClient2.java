package me.leon.socket.bio.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class BioMessageClient2 {

    private static Logger logger = LoggerFactory.getLogger(BioMessageClient2.class);

    public static final String IP_ADDR = "localhost";//服务器地址
    public static final int PORT = 18383;//服务器端口号

    public static void main(String[] args) {
        Socket socket = null;
        InputStream in = null;
        DataInputStream dis = null;
        OutputStream out = null;
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            socket = new Socket(IP_ADDR, PORT);

            //读取服务器端数据
            in = socket.getInputStream();
            dis = new DataInputStream(in);
            //向服务器端发送数据
            out = socket.getOutputStream();

            File file = new File("/home/yanlg/test.xml");

            if (!file.exists()) {
                System.out.println("文件不存在！");
            }
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int b ;
            while((b = fis.read(buf,0,buf.length))!=-1){
                out.write(buf,0,b);
                out.flush();
            }
            //发送完毕

            //
            byte[] receive = new byte[1024];
            int length = 0;
            while((b = dis.read(buf,0,buf.length))!=-1){
                if (b > receive.length - length) {
                    byte[] orig = receive;
                    receive = new byte[receive.length * 2];
                    System.arraycopy(orig, 0, receive, 0, length);
                }
                System.arraycopy(buf, 0, receive, length, b);
                length += b;
            }
            String ret = new String(receive, 0, length, "UTF-8");
            System.out.println("服务器端返回过来的是: " + ret);
            // 如接收到 "OK" 则断开连接
            if ("OK".equals(ret)) {
                System.out.println("客户端将关闭连接");
                Thread.sleep(500);
            }

            out.close();
            in.close();
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
