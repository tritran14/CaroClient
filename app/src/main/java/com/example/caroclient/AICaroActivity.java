package com.example.caroclient;

import static com.example.caroclient.util.Constants.INF;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.caroclient.ai.Minimax;
import com.example.caroclient.application.MyApplication;
import com.example.caroclient.model.Caro;
import com.example.caroclient.model.Point;
import com.example.caroclient.model.Type;
import com.example.caroclient.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class AICaroActivity extends AppCompatActivity {
    private static final int N=10;
    private static final String YOUR_TURN="Your turn";
    private static final String CPU_TURN="CPU turn";

    private boolean running=true;
    private GridView board;
    private BoardAdapter adapter;
    private boolean isDraw=false;
    private TextView tvTurn;
    private List<Type> list=new ArrayList<>();
    private int turn=0;
    private int role=0;
    private ImageView YourTurn,CPUTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aicaro);
        YourTurn=findViewById(R.id.border_your_turn);
        CPUTurn=findViewById(R.id.border_cpu_turn);
        tvTurn=findViewById(R.id.tv_turn);
        for(int i=0;i<N*N;++i){
            list.add(new Type(Constants.DEFAULT));
        }

        Caro caro=new Caro();
        Minimax minimax=new Minimax();

        int O=100;
        int X=101;

        final int[] turn = {0};
        setTurn(turn[0]);

        final int[] curX = {-1};
        final int[] curY = {-1};
        final int[] cpuX = {-1};
        final int[] cpuY = {-1};

        board=findViewById(R.id.board);
        adapter=new BoardAdapter(this,list);
        board.setAdapter(adapter);
        board.setNumColumns(N);
        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(role== turn[0]&&running){
                    int x=i/N;
                    int y=i%N;
                    curX[0] =x;
                    curY[0] =y;
                    if(caro.checkCoordinates(x,y)){
                        caro.setBoard(pointToInt(x,y),100+ turn[0]);
                        list.set(i,toType(turn[0]));
                        adapter.notifyDataSetChanged();
                        turn[0] ^=1;
                        setTurn(turn[0]);
                    }
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
//                    Log.e("AAA1", "run in thread: ");
                    if(turn[0]==1?caro.checkWin(curX[0], curY[0]):caro.checkWin(cpuX[0], cpuY[0])){
                        break;
                    }
                    if(caro.checkFull()){
                        isDraw=true;
                        break;
                    }
                    if(turn[0]==(role^1)){
                        Log.e("AAA1", "run in minimax: ");
                        Point p=minimax.CPUMinimaxTurn(caro.getBoard(),true,-INF,INF,100+turn[0]);
                        System.out.print("AI : ");
                        System.out.println(p);
                        cpuX[0] =p.getX();
                        cpuY[0] =p.getY();
                        caro.setBoard(pointToInt(p.getX(),p.getY()),100+turn[0]);
                        list.set(pointToInt(new Point(cpuX[0],cpuY[0])),toType(turn[0]));
                        notifyDataSetChanged();
                        turn[0]^=1;
                        setTurn(turn[0]);
                    }
                }
                running=false;
                if(isDraw){
                    toast("Draw");
                }
                else{
                    if((turn[0]^1)==role){
                        toast("Player is winner");
                    }
                    else{
                        toast("AI is winner");
                    }
                }
            }
        }).start();
    }

    private void setTurn(int turn){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(turn==0){
                    setText(YOUR_TURN);
                    YourTurn.setVisibility(View.VISIBLE);
                    CPUTurn.setVisibility(View.INVISIBLE);
                }
                else{
                    setText(CPU_TURN);
                    CPUTurn.setVisibility(View.VISIBLE);
                    YourTurn.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setText(String text){
        tvTurn.setText(text);
    }

    int pointToInt(int x,int y){
        return x*Caro.N+y%Caro.N;
    }

    int pointToInt(Point point){
        return point.getX()*Caro.N+point.getY()%Caro.N;
    }

    Type toType(int x){
        return new Type(x);
    }

    void notifyDataSetChanged(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }
    void toast(String msg){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AICaroActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}