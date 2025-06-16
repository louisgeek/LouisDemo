package com.louis.myudpserver;

import java.net.*;
import java.nio.charset.Charset;
import java.util.Locale;

public class UDPServer {
    /**
     * 一次监听
     */
    public void server() {
        try {
            String serverIp = "192.168.12.222";
            int serverPort = 6666;
//            InetAddress serverInetAddress = InetAddress.getByName(serverIp);
            InetAddress serverInetAddress = InetAddress.getLocalHost();
            DatagramSocket datagramSocket = new DatagramSocket(new InetSocketAddress(serverInetAddress, serverPort));
            //接收消息
            byte[] bytes = new byte[1024];
            DatagramPacket receiveDatagramPacket = new DatagramPacket(bytes, bytes.length);
            String log = String.format(Locale.CHINA, "1 服务端 %s:%d 开始接收未知客户端消息", serverInetAddress.getHostAddress(), serverPort);
            System.out.println(log);
            datagramSocket.receive(receiveDatagramPacket);
            byte[] receiveData = receiveDatagramPacket.getData();
            String receiveIp = receiveDatagramPacket.getAddress().getHostAddress();
            int receivePort = receiveDatagramPacket.getPort();
            String receive = new String(receiveData, Charset.forName("UTF-8"));
            log = String.format(Locale.CHINA, "2 服务端 %s:%d 接收客户端消息 %s:%d 内容：%s",
                    serverInetAddress.getHostAddress(), serverPort, receiveIp, receivePort, receive);
            System.out.println(log);
            //接收后回复消息
            SocketAddress clientSocketAddress = receiveDatagramPacket.getSocketAddress();
            String send = String.format(Locale.CHINA, "这是服务端 %s:%d 接收后回复的消息", serverInetAddress.getHostAddress(), serverPort);
            byte[] sendData = send.getBytes();
            DatagramPacket sendDatagramPacket = new DatagramPacket(sendData, sendData.length, clientSocketAddress);
            datagramSocket.send(sendDatagramPacket);
            log = String.format(Locale.CHINA, "3 服务端 %s:%d 接收后回复消息 %s:%d 成功",
                    serverInetAddress.getHostAddress(), serverPort, receiveIp, receivePort);
            System.out.println(log);
            //释放资源
            datagramSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int countMax = 3;
    private static int count = 0;

    /**
     * 有条件退出监听
     */
    public void serverCount() {
        try {
            String serverIp = "192.168.12.222";
            int serverPort = 6666;
//            InetAddress serverInetAddress = InetAddress.getByName(serverIp);
            InetAddress serverInetAddress = InetAddress.getLocalHost();
            DatagramSocket datagramSocket = new DatagramSocket(new InetSocketAddress(serverInetAddress, serverPort));
            //
//            while (true) {
            while (count < countMax) {
                //接收消息
                byte[] bytes = new byte[1024];
                DatagramPacket receiveDatagramPacket = new DatagramPacket(bytes, bytes.length);
                String log = String.format(Locale.CHINA, "1 服务端 %s:%d 开始接收未知客户端消息", serverInetAddress.getHostAddress(), serverPort);
                System.out.println(log);
                datagramSocket.receive(receiveDatagramPacket);
                byte[] receiveData = receiveDatagramPacket.getData();
                String receiveIp = receiveDatagramPacket.getAddress().getHostAddress();
                int receivePort = receiveDatagramPacket.getPort();
                String receive = new String(receiveData, Charset.forName("UTF-8"));
                log = String.format(Locale.CHINA, "2 服务端 %s:%d 接收客户端消息 %s:%d 内容：%s",
                        serverInetAddress.getHostAddress(), serverPort, receiveIp, receivePort, receive);
                System.out.println(log);
                //接收后回复消息
                SocketAddress clientSocketAddress = receiveDatagramPacket.getSocketAddress();
                String send = String.format(Locale.CHINA, "这是服务端 %s:%d 接收后回复的消息", serverInetAddress.getHostAddress(), serverPort);
                byte[] sendData = send.getBytes();
                DatagramPacket sendDatagramPacket = new DatagramPacket(sendData, sendData.length, clientSocketAddress);
                datagramSocket.send(sendDatagramPacket);
                log = String.format(Locale.CHINA, "3 服务端 %s:%d 接收后回复消息 %s:%d 成功",
                        serverInetAddress.getHostAddress(), serverPort, receiveIp, receivePort);
                System.out.println(log);
                //
                count++;
                System.out.println("flag " + count);
                if (count == countMax) {
                    //释放资源
                    datagramSocket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 一直监听
     */
    public void serverListing() {
        try {
            String serverIp = "192.168.12.222";
            int serverPort = 6666;
//            InetAddress serverInetAddress = InetAddress.getByName(serverIp);
            InetAddress serverInetAddress = InetAddress.getLocalHost();
            DatagramSocket datagramSocket = new DatagramSocket(new InetSocketAddress(serverInetAddress, serverPort));
            //
            while (true) {
                //接收消息
                byte[] bytes = new byte[1024];
                DatagramPacket receiveDatagramPacket = new DatagramPacket(bytes, bytes.length);
                String log = String.format(Locale.CHINA, "1 服务端 %s:%d 开始接收未知客户端消息", serverInetAddress.getHostAddress(), serverPort);
                System.out.println(log);
                datagramSocket.receive(receiveDatagramPacket);
                byte[] receiveData = receiveDatagramPacket.getData();
                String receiveIp = receiveDatagramPacket.getAddress().getHostAddress();
                int receivePort = receiveDatagramPacket.getPort();
                String receive = new String(receiveData, Charset.forName("UTF-8"));
                log = String.format(Locale.CHINA, "2 服务端 %s:%d 接收客户端消息 %s:%d 内容：%s",
                        serverInetAddress.getHostAddress(), serverPort, receiveIp, receivePort, receive);
                System.out.println(log);
                //接收后回复消息
                SocketAddress clientSocketAddress = receiveDatagramPacket.getSocketAddress();
                String send = String.format(Locale.CHINA, "这是服务端 %s:%d 接收后回复的消息", serverInetAddress.getHostAddress(), serverPort);
                byte[] sendData = send.getBytes();
                DatagramPacket sendDatagramPacket = new DatagramPacket(sendData, sendData.length, clientSocketAddress);
                datagramSocket.send(sendDatagramPacket);
                log = String.format(Locale.CHINA, "3 服务端 %s:%d 接收后回复消息 %s:%d 成功",
                        serverInetAddress.getHostAddress(), serverPort, receiveIp, receivePort);
                System.out.println(log);
                //释放资源
//                datagramSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
