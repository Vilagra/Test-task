package com.example.tcpserver1;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
        for (int i = 0; i < numberOfServers; i++) {
            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        ServerSocket serverSocket = null;
                        try {
                            serverSocket = new ServerSocket(firstPort + finalI);
                            Socket socket = serverSocket.accept();
                            textViewServers.post(new Runnable() {
                                @Override
                                public void run() {
                                    countOfServers++;
                                    textViewServers.setText(String.valueOf(countOfServers));
                                }
                            });
                            InputStream sin = socket.getInputStream();
                            OutputStream sout = socket.getOutputStream();
                            sin.read(new byte[254]); //get start package
                            while (!Thread.currentThread().isInterrupted()) {
                                TimeUnit.MILLISECONDS.sleep(500);
                                byte[] bufferOut = new byte[254];
                                byte[] bufferIn = new byte[254];
                                sout.write(bufferOut);
                                sout.flush();
                                sin.read(bufferIn);
                                textViewServers.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        countOfPackages++;
                                        textViewPackages.setText(String.valueOf(countOfPackages));
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finally {
                            textViewServers.post(new Runnable() {
                                @Override
                                public void run() {
                                    countOfServers--;
                                    textViewServers.setText(String.valueOf(countOfServers));
                                }
                            });
                            try {
                                serverSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            t.start();
        }
    }


}
