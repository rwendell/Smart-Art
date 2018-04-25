package com.example.austinsehnert.smartart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
       //Intent signIn = new Intent(this, SignInActivity.class);
       //startActivity(signIn);
        Intent global = new Intent(this, GlobalActivity.class);
        startActivity(global);

        //Intent g = new Intent(this, GlobalActivity.class);
        //startActivity(g);


       /* Intent socket = new Intent(this, WebSocket.class);
        startActivity(socket);*/

        //WebSocket w = new WebSocket();
       // w.connectWebSocket();

        //Intent socketTest = new Intent(this, WebSocket.class);

        Intent socket = new Intent(this, SocketService.class);
        startService(socket);


        Intent g = new Intent(this, GlobalActivity.class);
        startActivity(g);

    }
}












