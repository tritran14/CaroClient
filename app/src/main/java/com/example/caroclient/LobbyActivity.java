package com.example.caroclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.caroclient.adapters.LobbyAdapter;
import com.example.caroclient.application.MyApplication;
import com.example.caroclient.callback.OnItemClickListener;
import com.example.caroclient.model.Room;
import com.example.caroclient.preform.LoginActivity;
import com.example.caroclient.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    private static final String TAG="AAA1";
    private static final int MAIN_PORT=6666;
    private EditText edtMainIP;
    private Button button;
    private List<Room> rooms=new ArrayList<>();
    private RecyclerView rv;
    private LobbyAdapter adapter;
    private int ports[]={7777,8888,6969};
    private MyApplication app;
    private int role= Constants.DEFAULT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        app=(MyApplication)getApplication();
        edtMainIP=findViewById(R.id.edt_main_ip);
        button=findViewById(R.id.button);
        rv=findViewById(R.id.rv);
        adapter=new LobbyAdapter(this,rooms);
        for(int i=0;i<ports.length;++i){
            rooms.add(new Room(ports[i],0));
            adapter.notifyDataSetChanged();
        }
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip=edtMainIP.getText().toString();
                if(TextUtils.isEmpty(ip)){
                    Toast.makeText(LobbyActivity.this, "ip is empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    connectToServer(ip,MAIN_PORT);
                }
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                String ip=app.getIP();
                int port=ports[position];
                if(TextUtils.isEmpty(ip)){
                    Toast.makeText(LobbyActivity.this, "please connect with server", Toast.LENGTH_SHORT).show();
                }
                else{
                    LobbyActivity.RequestServer request=new LobbyActivity.RequestServer(ip,port,"tri");
                    new Thread(request).start();
                }
            }
        });
    }

    public void connectToServer(String ip,int port){
        new Thread(new Runnable() {
            @Override
            public void run() {
                app= (MyApplication) getApplication();
                try {
                    app.setNewSocketLobby(ip,port);
                    Log.e("AAA1", "connected");
                    toast("connected");
                    while(true){
                        try {
                            String msg=app.getInLobby().readUTF();
//                            Log.e("AAA1", "serverLobby ---> "+msg);
                            String[] strs=msg.split(",");
                            rooms.clear();
                            for(int i=0;i< strs.length;++i){
                                rooms.add(new Room(ports[i],Integer.parseInt(strs[i])));
                                notifyDataSetChanged();
                            }
                            Thread.sleep(1000);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("AAA1", "error in lobby :  "+e.getMessage());
                            break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    void notifyDataSetChanged(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
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

    void toast(String msg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LobbyActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
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
                Intent intent=new Intent(LobbyActivity.this, CaroClientActivity.class);
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

            Context context = LobbyActivity.this;

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