package com.example.caroclient.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caroclient.R;
import com.example.caroclient.callback.OnItemClickListener;
import com.example.caroclient.model.Room;

import java.util.List;
import java.util.zip.Inflater;

public class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.ViewHolder> {

    private Context context;
    private List<Room> rooms;
    private OnItemClickListener listener;

    public LobbyAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvPort.setText(rooms.get(position).getPort()+"");
        holder.tvNumber.setText(rooms.get(position).getNumberPlayers()+"/2");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPort,tvNumber;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPort=itemView.findViewById(R.id.tv_port);
            tvNumber=itemView.findViewById(R.id.tv_number_players);
            cardView=itemView.findViewById(R.id.cardview);

        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
