package com.example.awakedust_merge;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<Data> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_recyclerview, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(Data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private ImageView imageView;
        private TextView textView3;



        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView1);
            imageView = itemView.findViewById(R.id.image);
            textView3 = itemView.findViewById(R.id.textView3);



        }

        void onBind(Data data) {
            Log.d("problem",data.getWhattime());
            textView1.setText(data.getWhattime());
            if(data.getWhatcloud().equals("1"))
                imageView.setImageResource(R.drawable.sunny);
            else if(data.getWhatcloud().equals("3"))
                imageView.setImageResource(R.drawable.cloudy);
            else if(data.getWhatcloud().equals("4"))
                imageView.setImageResource(R.drawable.morecloudy);
            else if(data.getWhatcloud().equals("6"))
                imageView.setImageResource(R.drawable.rainy);
            else if(data.getWhatcloud().equals("8"))
                imageView.setImageResource(R.drawable.snowy);

            textView3.setText(data.getWhattemper());

        }
    }
}