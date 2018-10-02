package com.example.adiama.quranskripsi;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SurahFragment extends Fragment {


    static String lang;
    private ArrayList<Surah> surahArrayList;
    private RecyclerView mRecyclerView;
    private SurahAdapter surahAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public SurahFragment() {
        // Required empty public constructor
    }

    public static SurahFragment newInstance() {
        SurahFragment surahFragment = new SurahFragment();
        return surahFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        surahArrayList = getSurahArrayList();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bar, menu);
        super.onCreateOptionsMenu(menu,inflater);

        MenuItem searchIem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchIem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<Surah> surahArrayListByName = new ArrayList<Surah>();
                surahArrayListByName = getSurahArrayListByName(query);
                surahArrayList.clear();
                surahArrayList.addAll(surahArrayListByName);
                surahAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                ArrayList<Surah> surahArrayListByName = new ArrayList<Surah>();
                surahArrayListByName = getSurahArrayListByName(query);
                surahArrayList.clear();
                surahArrayList.addAll(surahArrayListByName);
                surahAdapter.notifyDataSetChanged();

                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surah, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_surah_view);
        surahAdapter = new SurahAdapter(surahArrayList, getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setAdapter(surahAdapter);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        surahAdapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Surah surah = (Surah) surahAdapter.getItem(position);

                long surah_id = surah.getId(); //mRecyclerView.getAdapter().getItemId(position);
                long ayah_number = surah.getAyahNumber();
                String surah_name = surah.getNameTranslate();

                Log.d("SurahFragment", "ID: " + surah_id + " Surah Name: " + surah_name);

                Bundle dataBundle = new Bundle();
                dataBundle.putLong(SurahDataSource.SURAH_ID_TAG, surah_id);
                dataBundle.putLong(SurahDataSource.SURAH_AYAH_NUMBER, ayah_number);
                dataBundle.putString(SurahDataSource.SURAH_NAME_TRANSLATE, surah_name);

                Intent intent = new Intent(getActivity(), AyahViewController.class);
                intent.putExtras(dataBundle);
                intent.putExtra("surah_id",surah_id);
                intent.putExtra("surah_name",surah_name);
                startActivity(intent);
            }
        });
    }

    private ArrayList<Surah> getSurahArrayList() {
        ArrayList<Surah> surahArrayList = new ArrayList<Surah>();
        SurahDataSource surahDataSource = new SurahDataSource(getActivity());
                surahArrayList = surahDataSource.getEnglishSurahArrayList();
        return surahArrayList;
    }

    private ArrayList<Surah> getSurahArrayListByName(String query) {
        ArrayList<Surah> surahArrayList = new ArrayList<Surah>();
        SurahDataSource surahDataSource = new SurahDataSource(getActivity());
        surahArrayList = surahDataSource.getEnglishSurahArrayListByName(query);
        return surahArrayList;
    }
}
