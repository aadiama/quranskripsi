package com.example.adiama.quranskripsi;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.SurahViewHolder>{
    private ArrayList<Surah> surahArrayList;
    private Context context;


    public SurahAdapter(ArrayList<Surah> surahArrayList, Context context) {
        this.surahArrayList = surahArrayList;
        this.context = context;

    }


    @Override
    public SurahAdapter.SurahViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_surah, parent, false);
        SurahAdapter.SurahViewHolder viewHolder = new SurahAdapter.SurahViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SurahAdapter.SurahViewHolder holder, int position) {

        Surah surah = surahArrayList.get(position);
        holder.surah_idTextView.setText(Long.toString(surah.getId()) + ".");
        holder.translateTextView.setText(surah.getNameTranslate());
        holder.arabicTextView.setText(surah.getNameArabic());

    }

    @Override
    public long getItemId(int position) {
        //  Surah surah = surahArrayList.get(position);

        return surahArrayList.get(position).getId();
    }

    public Object getItem(int position) {

        return surahArrayList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return surahArrayList.size();
    }


    public class SurahViewHolder extends RecyclerView.ViewHolder
    {
        public TextView translateTextView;

        public TextView surah_idTextView;

        public TextView arabicTextView;
        public RelativeLayout row_surah;

        public SurahViewHolder(View view) {
            super(view);
            translateTextView = view.findViewById(R.id.translate_textView);
            arabicTextView =  view.findViewById(R.id.arabic_textView);
            surah_idTextView = view.findViewById(R.id.surah_idTextView);
            row_surah =  view.findViewById(R.id.row_surah);

        }

    }

}

