package com.ideanov.minesweeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;

public class GameActivity extends Activity {

    public static final String KEY_CONTINUE = "com.ideanov.minesweeper.continue";
    public static final String KEY_DIFFICULTY = "com.ideanov.minesweeper.difficulty";
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;
    public static int totalMines = 0;
    public static int totalRows = 0;
    public static int totalCols = 0;
    public static final int easyRows = 9;
    public static final int easyColumns = 9;
    public static final int easyMines = 10;
    public static final int mediumRows = 16;
    public static final int mediumColumns = 16;
    public static final int mediumMines = 40;
    public static final int hardRows = 30;
    public static final int hardColumns = 16;
    public static final int hardMines = 99;
    public static boolean timerStarted = false;
    public static boolean minesSet = false;
    public int tileWH = 10;
    public int tilePadding = 5;
    public Handler timer = new Handler();
    public int secondsPassed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //Get the difficulty chosen by the user and set it to easy if there is no answer
        int difficulty = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
        //Define if the user is continuing a game and set it to false by default
        boolean continueOrNot = getIntent().getBooleanExtra(KEY_CONTINUE, false);
        enterUsername();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    public void enterUsername()
    {
        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.high_scores);

        // Setting Dialog Message
        alertDialog.setMessage(R.string.username);
        final EditText input = new EditText(GameActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialoginterface, int i)
                    {
                        //Store the user's score
                        Log.v(input.getText().toString(), "data from the edit text");

                    }
                }).show();
    }
    
}
