package com.louis.myudpclient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UDPClient {

    public void client() {
        try {
            String clientIp = "192.168.12.222";
            String serverIp = "192.168.12.222";
            int clientPort = 7774;
            int serverPort = 6666;
//            InetAddress clientInetAddress = InetAddress.getByName(clientIp);
            InetAddress clientInetAddress = InetAddress.getLocalHost();
//            InetAddress serverInetAddress = InetAddress.getByName(serverIp);
            InetAddress serverInetAddress = InetAddress.getLocalHost();
            DatagramSocket datagramSocket = new DatagramSocket(new InetSocketAddress(clientInetAddress, clientPort));
            //发送消息
            String send = String.format(Locale.CHINA, "这是客户端 %s:%d 发送的消息", clientInetAddress.getHostAddress(), clientPort);
            byte[] sendData = send.getBytes();
            DatagramPacket sendDatagramPacket = new DatagramPacket(sendData, sendData.length,
                    new InetSocketAddress(serverInetAddress, serverPort));
            datagramSocket.send(sendDatagramPacket);
            String log = String.format(Locale.CHINA, "1 客户端 %s:%d 发送消息到服务端 %s:%d 成功",
                    clientInetAddress.getHostAddress(), clientPort, serverInetAddress.getHostAddress(), serverPort);
            System.out.println(log);

            //发送消息后等待回复消息
            byte[] bytes = new byte[1024];
            DatagramPacket receiveDatagramPacket = new DatagramPacket(bytes, bytes.length);
            log = String.format(Locale.CHINA, "2 客户端 %s:%d 发送消息后等待服务端 %s:%d 回复",
                    clientInetAddress.getHostAddress(), clientPort, serverInetAddress.getHostAddress(), serverPort);
            System.out.println(log);

            datagramSocket.receive(receiveDatagramPacket);
            byte[] receiveData = receiveDatagramPacket.getData();
            String receiveIp = receiveDatagramPacket.getAddress().getHostAddress();
            int receivePort = receiveDatagramPacket.getPort();
            String receive = new String(receiveData, Charset.forName("UTF-8"));
            log = String.format(Locale.CHINA, "3 客户端 %s:%d 接收到服务端消息消息 %s:%d 内容：%s",
                    clientInetAddress.getHostAddress(), clientPort, receiveIp, receivePort, receive);
            System.out.println(log);

            //释放资源
            datagramSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
