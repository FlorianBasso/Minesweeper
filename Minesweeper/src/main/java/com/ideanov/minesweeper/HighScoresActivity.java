package com.ideanov.minesweeper;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class HighScoresActivity extends ListActivity {

    static String[] highScores = null; //= {"1er","2ème"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        if (highScores != null)
            setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, highScores));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.high_scores, menu);
        return true;
    }
}
