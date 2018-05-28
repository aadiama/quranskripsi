package com.example.adiama.quranskripsi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.RecyclerVH>{
    Context c;
    String[] spacecrafts;

    public MyAdapter(Context c, String[] spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.model,parent,false);
        return new RecyclerVH(v);
    }

    @Override
    public void onBindViewHolder(RecyclerVH holder, int position) {
        holder.nameTxt.setText(spacecrafts[position]);
    }

    @Override
    public int getItemCount() {
        return spacecrafts.length;
    }

    /*
    VIEWHOLDER CLASS
     */
    public class RecyclerVH extends RecyclerView.ViewHolder
    {
        TextView nameTxt;

        public RecyclerVH(View itemView) {
            super(itemView);

            nameTxt= itemView.findViewById(R.id.nameTxt);
        }
    }
}
