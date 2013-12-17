package com.ideanov.minesweeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    //BUTTON MANAGEMENT
    public void displayDifficultyDialog(View target)
    {
        //Display a dialog to choose the difficulty
        new AlertDialog.Builder(this)
                .setTitle(R.string.difficulty_title)
                .setItems(R.array.difficulty,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialoginterface, int i)
                            {
                                startNewGame(i);
                            }
                        }).show();
    }

    private void startNewGame(int i)
    {
        //Intent intent = new Intent(this, GameActivity.class);
        //intent.putExtra(GameActivity.KEY_DIFFICULTY, i);
        //startActivity(intent);
    }

    public void continueGame(View target) {
    }

    public void displayHighScores(View target)
    {
        //Create an Intent to display the HighScores view
        Intent i = new Intent(this, HighScoresActivity.class);
        startActivity(i);
    }

    public void displayRules(View target)
    {
        //Create an Intent to display the Rules view
        Intent i = new Intent(this, RulesActivity.class);
        startActivity(i);
    }

    public void exitApplication(View target)
    {
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    
}
