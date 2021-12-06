package com.example.caroclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caroclient.adapters.BoardAdapter;
import com.example.caroclient.application.MyApplication;
import com.example.caroclient.model.Point;
import com.example.caroclient.model.Type;
import com.example.caroclient.preform.LoginActivity;
import com.example.caroclient.util.Constants;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//            0 1 2
//            3 X 4
//            5 6 7


public class CaroClientActivity extends AppCompatActivity {
    private static final String YOUR_TURN="Your turn";
    private static final String CPU_TURN="Opponent turn";

    private TextView tvTurn;
    private ImageView ivYourSign,ivCPUSign,yourTurnBorder,CPUTurnBorder;
    private GridView board;
    private BoardAdapter adapter;
    MyApplication app;
    private int role=Constants.DEFAULT;
    private static final int N=10;
    private static final int TARGET=5;
    private static List<Type> list=new ArrayList<>();
    private static Point[][][] points=new Point[N][N][8];
    private static boolean waiting=false;
    private static int turn=-1;
    private boolean exit=false;
    private boolean onBack=false;

    private void setTurn(int turn){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(turn==role){
                    setText(YOUR_TURN);
                    yourTurnBorder.setVisibility(View.VISIBLE);
                    CPUTurnBorder.setVisibility(View.INVISIBLE);
                }
                else{
                    setText(CPU_TURN);
                    CPUTurnBorder.setVisibility(View.VISIBLE);
                    yourTurnBorder.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setText(String text){
        tvTurn.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caro_client);
        tvTurn=findViewById(R.id.tv_turn);
        ivYourSign=findViewById(R.id.iv_your_sign);
        ivCPUSign=findViewById(R.id.iv_cpu_sign);
        yourTurnBorder=findViewById(R.id.border_your_turn);
        CPUTurnBorder=findViewById(R.id.border_cpu_turn);
        Log.e("AAA1", "onCreate of ClientActivity ");
        Intent intent=getIntent();
        list=new ArrayList<>();
        role=intent.getIntExtra("role",Constants.DEFAULT);

        if(role==0){
            ivYourSign.setImageResource(R.drawable.o);
            ivCPUSign.setImageResource(R.drawable.x);
        }
        else{
            ivYourSign.setImageResource(R.drawable.x);
            ivCPUSign.setImageResource(R.drawable.o);
        }

        Log.e("AAA1", "onCreate: role : "+role);
        app=(MyApplication)this.getApplication();
        for(int i=0;i<N*N;++i){
            list.add(new Type(Constants.DEFAULT));
            for(int j=0;j<8;++j){
                points[i/N][i%N][j]=new Point(-1,-1);
            }
        }


        Log.e("AAA1","ok : "+(points[0][0].equals(Point.INVALID)));
        board=findViewById(R.id.board);
        adapter=new BoardAdapter(this,list);
        board.setAdapter(adapter);
        board.setNumColumns(N);
//        final int[] turn = {0};
        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("AAA1", "onItemClick: role : "+role+" a& turn : "+turn);
                if(!waiting&&role==turn){
                    Log.e("AAA1", "onItemClick: sent");
                    int x=i/N;
                    int y=i%N;
                    SendRequest request=new SendRequest(role+ask(x,y));
                    new Thread(request).start();
                    waiting=true;
                }
                else{
                    Log.e("AAA1", "onItemClick: waiting : "+waiting+"/ role : "+role+" turn : "+turn);
                }
//                Type type;
//                if(turn[0]==1) type=new Type(Constants.X);
//                else type=new Type(Constants.O);
//                if(isValid(i)){
//                    list.set(i,type);
//                    if(update(i/N,i%N,type)){
//                        adapter.notifyDataSetChanged();
//                        Toast.makeText(CaroClientActivity.this,type.getName()+" win !!!", Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        turn[0] ^=1;
//                        adapter.notifyDataSetChanged();
////                        Toast.makeText(CaroClientActivity.this,x+","+y, Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else{
//                    Toast.makeText(CaroClientActivity.this, "invalid", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        // check win -> check longest point that from this point to another point are same
            new Thread(new RunningTask()).start();
        }



    class RunningTask implements Runnable{

        @Override
        public void run() {
            // ready
            try {
                app.getOut().writeUTF(role+"ready");
                Log.e("AAA1", "client -> ready");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String res=app.getIn().readUTF();
                Log.e("AAA1", "run/res (read): "+res);
                // nen in ra stateconcak vd 0concak hoac 1concak
                turn=Integer.parseInt(res.charAt(0)+"");
                setTurn(turn);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("AAA1", "role : "+role+" turn : "+turn);
            Log.e("AAA1", "waiting: "+waiting);
            while (true){
                try {
                    if(turn==1-role&&!waiting){
                        Log.e("AAA1", "client -> : -1 -1");
                        app.getOut().writeUTF(role+ask(-1,-1));
                        waiting=true;
                    }
                    if(turn>1){
//                        app.getOut().writeUTF("stop");
                        break;
                    }
//                    Log.e("AAA1", "waiting for while(true): ");
                    // waiting for waiting equal true
                    while(waiting!=true){
                        Log.e("AAA1","waiting for action... ");
                    }
                    String res;
                    if(!onBack) res=app.getIn().readUTF();
                    else res="exit";
                    Log.e("AAA1", "while: res(server) -----> "+res);
                    waiting=false;
                    if(res.equals("waiting_for_another_player")){
                        continue;
                    }
                    if(res.equals("exit")) {
                        exit=true;
                        break;
                    }
                    update(res.substring(1));
                    turn=Integer.parseInt(res.charAt(0)+"");
                    setTurn(turn);
                    Log.e("AAA1", "TURN change: "+turn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String result="Loser";
            if(turn-2!=role&&!exit){
                result="Winner";
            }
            toast("You are "+result);
            if(!onBack){
                try {
                    Log.e("AAA1","client ----> stop ");
                    app.getOut().writeUTF("stop");
                    Log.e("AAA1","waiting for server confirm ");
                    String exitConfirm=app.getIn().readUTF();
                    Log.e("AAA1","server ------> "+exitConfirm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            startActivity(new Intent(CaroClientActivity.this,LoginActivity.class));
            finish();
        }
    }


    private void toast(String msg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CaroClientActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void update(String res){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String[] resplit=res.split(", ");
                for(int i=0;i<100;++i){
                    int cur=Integer.parseInt(resplit[i]);
                    list.set(i,converToType(cur));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private static Type converToType(int i){
        if(i==100) return new Type(0);
        if(i==101) return new Type(1);
        return new Type(2);
    }

    private static String ask(int x,int y){
        return (x*N+y%N)+"";
    }

    class SendRequest implements Runnable{
        private String msg;
        public SendRequest(String msg){
            this.msg=msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                app.getOut().writeUTF(msg);
                Log.e("AAA1", "client -> "+msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static boolean isValid(int pos){
        return list.get(pos).getType()==Constants.DEFAULT;
    }

    static boolean inSide(int x,int y){
        return x>=0&&x<N&&y>=0&&y<N;
    }

    static int pointToInt(int x,int y){
        return x*N+y%N;
    }

//    static boolean update(int x,int y,Type type){
////            0 1 2
////            3 X 4
////            5 6 7
//        boolean found=false;
//
//        Point point1=new Point(x,y);
//        Point point3=new Point(x,y);
//        Point point4=new Point(x,y);
//        Point point6=new Point(x,y);
//
//        Point cross0=new Point(x,y);
//        Point cross2=new Point(x,y);
//        Point cross5=new Point(x,y);
//        Point cross7=new Point(x,y);
////       update for horizontal
//        if(inSide(x-1,y)){
//            if(list.get(pointToInt(x-1,y)).getType()==type.getType()){
//                point1=points[x-1][y][1].copy();
//            }
//        }
//        if(inSide(x+1,y)){
//            if(list.get(pointToInt(x+1,y)).getType()==type.getType()){
//                point6=points[x+1][y][6].copy();
//            }
//        }
////         update
//        points[point1.getX()][point1.getY()][6]=point6.copy();
//        points[point6.getX()][point6.getY()][1]=point1.copy();
//
//        if(point1.caroDistance(point6)>=TARGET){
//            found=true;
//        }
////        update for vertical
//        if(inSide(x,y-1)){
//            if(list.get(pointToInt(x,y-1)).getType()==type.getType()){
//                point3=points[x][y-1][3];
//            }
//        }
//        if(inSide(x,y+1)){
//            if(list.get(pointToInt(x,y+1)).getType()==type.getType()){
//                point4=points[x][y+1][4];
//            }
//        }
////        update
//        points[point3.getX()][point3.getY()][4]=point4.copy();
//        points[point4.getX()][point4.getY()][3]=point3.copy();
//
//        if(point3.caroDistance(point4)>=TARGET){
//            found=true;
//        }
//
////        update for cross from left-top to right-bottom
//        if(inSide(x-1,y-1)){
//            if(list.get(pointToInt(x-1,y-1)).getType()==type.getType()){
//                cross0=points[x-1][y-1][0].copy();
//            }
//        }
//        if(inSide(x+1,y+1)){
//            if(list.get(pointToInt(x+1,y+1)).getType()==type.getType()){
//                cross7=points[x+1][y+1][7].copy();
//            }
//        }
////         update
//        points[cross0.getX()][cross0.getY()][7]=cross7.copy();
//        points[cross7.getX()][cross7.getY()][0]=cross0.copy();
//
//        if(cross0.caroDistance(cross7)>=TARGET){
//            found=true;
//        }
//
////        update for cross from right-top to left-bottom
//        if(inSide(x-1,y+1)){
//            if(list.get(pointToInt(x-1,y+1)).getType()==type.getType()){
//                cross2=points[x-1][y+1][2].copy();
//            }
//        }
//        if(inSide(x+1,y-1)){
//            if(list.get(pointToInt(x+1,y-1)).getType()==type.getType()){
//                cross5=points[x+1][y-1][5].copy();
//            }
//        }
////         update
//        points[cross2.getX()][cross2.getY()][5]=cross5.copy();
//        points[cross5.getX()][cross5.getY()][2]=cross2.copy();
//
//        if(cross2.caroDistance(cross5)>=TARGET){
//            found=true;
//        }
//
//        return found;
//    }


//    @Override
//    public void onBackPressed() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    app.getOut().writeUTF("stop");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    app.closeSocket();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        finish();
//    }

    @Override
    protected void onDestroy() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    app.getOut().writeUTF("stop");
//                    Log.e("AAA1", "client ----> stop");
//                    String exitPermission=app.getIn().readUTF();
//                    if(exitPermission.equals("exit")){
//                        Log.e("AAA1", "server ----> exit");
//                    }
////                    app.closeSocket();
//                    Log.e("AAA1", "close socket ");
//                    toast("onDestroy");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                try {
////                    app.closeSocket();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//            }
//        }).start();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    app.getOut().writeUTF("stop");
                    waiting=true;
                    onBack=true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("AAA1", "client ----> stop");
            }
        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    app.closeSocket();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        Toast.makeText(CaroClientActivity.this, "onBack", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
//        startActivity(new Intent(CaroClientActivity.this,LoginActivity.class));
//        finish();
    }
}