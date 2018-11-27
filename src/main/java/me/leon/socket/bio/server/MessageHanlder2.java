package me.leon.socket.bio.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class MessageHanlder2 implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(MessageHanlder2.class);

    private Socket incoming; // 收到的链接

    public MessageHanlder2(Socket incoming) {
        this.incoming = incoming;
    }

    @Override
    public void run() {
        OutputStream outStream = null;
        PrintStream out = null;
        InputStream in = null;
        BufferedInputStream bis = null;
        String message = null;
        String returnMessage = null;
        try {
            outStream = incoming.getOutputStream(); // 获取Socket输出流，用于写出响应信息
            out = new PrintStream(outStream, false, "UTF-8"); // 以字节流打印服务器信息到输出流
            in = incoming.getInputStream();
            bis = new BufferedInputStream(in);
            byte[] buff = new byte[10240];//申请10k
            int totalLength = 0;
            int length = 0;
            byte[] subBuf = new byte[256];
            while (true) {
                length = bis.read(subBuf);
                if (length != -1) {
                    if (buff.length < totalLength + length) {//扩容
                        byte[] orig = buff;
                        buff = new byte[totalLength * 2];
                        System.arraycopy(orig, 0, buff, 0, totalLength);
                    }
                    System.arraycopy(subBuf, 0, buff, totalLength, length);
                    totalLength += length;
                    message = new String(buff, 0, totalLength, "utf-8");
                    if (message.indexOf("</root>") != -1) {
                        break;
                    }
                } else {
                    break;
                }
            }
            if (totalLength == 0) {
                logger.error("接收到的报文长度为0，没有任何报文");
            } else {
                logger.info("接收到的报文长度：" + totalLength);
            }
            logger.info("收到请求报文[{}]", message);
            returnMessage = "success";
        } catch (IOException e) {
            returnMessage = "error";
            e.printStackTrace();
        } finally {
            logger.info("交易调用结束  返回报文;[{}]", returnMessage);
            out.print(returnMessage);
            SocketAddress sd = incoming.getRemoteSocketAddress();
            logger.info("第三方客户端：{} 已断开！", sd);
            if (out != null) {
                out.close();
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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