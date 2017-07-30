package com.example.tcpserver1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Aleksandr Levenko on 18.04.2017.
 */

//start on PC after Android app is launched
public class TcpClient {
    static int firstServerPort = 56001;
    static String address ="192.168.0.100"; // IP of my phone
    public static void main(String[] args) {
        for (int i = 0; i < 48; i++) {
            final int i1 = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InetAddress ip = InetAddress.getByName(address);
                        Socket socket = new Socket(ip, firstServerPort);
                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(new byte[254]); // send start package
                        outputStream.flush();
                        while (true) {
                            byte[] bufferOut = new byte[254];
                            bufferOut[0] = (byte) i1;
                            byte[] bufferIn = new byte[254];
                            inputStream.read(bufferIn);
                            outputStream.write(bufferOut);
                            outputStream.flush();
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }
}
