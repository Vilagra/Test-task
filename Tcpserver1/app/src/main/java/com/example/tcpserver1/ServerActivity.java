package com.example.tcpserver1;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ServerActivity extends AppCompatActivity {
    int firstPort = 56001;
    public final static int numberOfServers = 48;
    TextView textViewPackages;
    TextView textViewServers;
    public int countOfPackages;
    public int countOfServers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewServers = (TextView) findViewById(R.id.numberOfServers);
        textViewPackages = (TextView) findViewById(R.id.numberOfPackage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runServers();
    }

    private void runServers() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(firstPort));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    while (true) {
                        Log.d("beforeaccept","dddd");
                        final Socket socket = serverSocket.accept();
                        Log.d("afteraccept","dddd");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                InputStream sin = null;
                                OutputStream sout = null;
                                try {
                                    sin = socket.getInputStream();
                                    sout = socket.getOutputStream();
                                    sin.read(new byte[254]); //get start package
                                    while (!Thread.currentThread().isInterrupted()) {
                                        TimeUnit.MILLISECONDS.sleep(500);
                                        byte[] bufferOut = new byte[254];
                                        byte[] bufferIn = new byte[254];
                                        sout.write(bufferOut);
                                        sout.flush();
                                        sin.read(bufferIn);
                                        Log.d("read", Arrays.toString(bufferIn));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        sin.close();
                                        sout.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }


}
