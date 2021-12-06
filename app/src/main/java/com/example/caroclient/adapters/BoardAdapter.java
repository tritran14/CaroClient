package com.example.caroclient.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.caroclient.R;
import com.example.caroclient.model.Type;

import java.util.List;

public class BoardAdapter extends BaseAdapter {
    private Context context;
    private List<Type> list;
    public BoardAdapter(Context context, List<Type> list){
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v=LayoutInflater.from(context).inflate(R.layout.item,viewGroup,false);
        int type=list.get(i).getType();
        ImageView imageView=v.findViewById(R.id.item);
        if(type==0){
            imageView.setImageResource(R.drawable.o);
        }else if(type==1){
            imageView.setImageResource(R.drawable.x);
        }else{
            imageView.setImageResource(R.drawable.blank);
        }
        Resources r = context.getResources();
        int px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 35,r.getDisplayMetrics()));
        v.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,px));
        return v;
    }
}
