package com.example.caroclient.preform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.caroclient.CaroClientActivity;
import com.example.caroclient.R;
import com.example.caroclient.application.MyApplication;
import com.example.caroclient.util.Constants;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.Format;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG="AAA1";
    private TextInputEditText edtPort,edtName,edtIp;
    private Button btnGo;
    MyApplication app;
    private int role= Constants.DEFAULT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtPort=findViewById(R.id.edt_port);
        edtIp=findViewById(R.id.edt_ip);
        edtName=findViewById(R.id.edt_name);
        btnGo=findViewById(R.id.btn_go);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app=(MyApplication)getApplication();
                String ip=edtIp.getText().toString().trim();
                String port=edtPort.getText().toString().trim();
                String name=edtName.getText().toString().trim();
                Log.e(TAG, "onClick: click !");
                Toast.makeText(LoginActivity.this, "Click click", Toast.LENGTH_SHORT).show();
                if(!ip.isEmpty()&&!port.isEmpty()&&!name.isEmpty()){
                    RequestServer request=new RequestServer(ip,Integer.parseInt(port),name);
                    new Thread(request).start();
                }
                else{
                    Toast.makeText(LoginActivity.this, "something is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    boolean checkIp(String Ip){
        String[] num=Ip.split("\\.");
        if(num.length!=4) return false;
        for(int i=0;i<num.length;++i){
            int number=Integer.parseInt(num[i]);
            Log.e(TAG, "checkIp: "+number);
            if(number<0&&number>255) return false;
        }
        return true;
    }

    class RequestServer implements Runnable{
        String ip,name;
        int port;

        public RequestServer(String ip, int port,String name) {
            this.ip = ip;
            this.port = port;
            this.name = name;
        }

        @Override
        public void run() {
            if(checkValid(ip,port,name)){
                Log.e(TAG, "run: ok");
                Intent intent=new Intent(LoginActivity.this, CaroClientActivity.class);
                intent.putExtra("role",role);
                startActivity(intent);
//                finish();
            }
            else{
                Log.e(TAG,"run: wrong");
            }
        }
    }

    boolean checkValid(String ip,int port,String name){
        Log.e(TAG, "checkValid: ip : "+checkIp(ip));
        if(!checkIp(ip)) return false;
        try {
            Log.e(TAG, "checkValid: ip : "+ip+" port : "+port);
            app.setNewSocket(ip,port);

            Context context = LoginActivity.this;

            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String myIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//            out.writeUTF(myIp+";"+name);
            app.getOut().writeUTF(myIp+";"+name);
            Log.e(TAG, "client -> "+myIp+";"+name);
            Log.e(TAG, "checkValid: "+myIp); // thay hem no bi cai j ay
            Log.e(TAG, "checkValid: waiting...");
//            String response=in.readUTF();
            String response=app.getIn().readUTF();
            Log.e(TAG, "server response -> : "+response);
//            in.close();
//            out.close();
            if(response.substring(0,2).equals("OK")){
                role=Integer.parseInt(response.substring(2,3));
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "error: "+e.getMessage());
        }
        return false;
    }


}