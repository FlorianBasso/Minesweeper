package com.ideanov.minesweeper;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HighScoresActivity extends ExpandableListActivity
{
    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        if(listDataChild != null && listDataHeader != null)
            setListAdapter(listAdapter);
    }

    /*
    * Preparing the list data
    */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding header data
        listDataHeader.add(getString(R.string.easy_label));
        listDataHeader.add(getString(R.string.medium_label));
        listDataHeader.add(getString(R.string.hard_label));

        // Adding child data
        List<String> easy = new ArrayList<String>();
        easy.add("0");
        easy.add("1");
        easy.add("2");
        easy.add("3");
        easy.add("4");
        easy.add("5");
        easy.add("6");

        List<String> medium = new ArrayList<String>();
        medium.add("0");
        medium.add("1");
        medium.add("2");
        medium.add("3");
        medium.add("4");
        medium.add("5");
        medium.add("6");

        List<String> hard = new ArrayList<String>();
        hard.add("0");
        hard.add("1");
        hard.add("2");
        hard.add("3");
        hard.add("4");
        hard.add("5");
        hard.add("6");

        listDataChild.put(listDataHeader.get(0), easy); // Header, Child data
        listDataChild.put(listDataHeader.get(1), medium);
        listDataChild.put(listDataHeader.get(2), hard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.high_scores, menu);
        return true;
    }
}
