package com.ideanov.minesweeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends Activity {

    public static final String KEY_CONTINUE = "com.ideanov.minesweeper.continue";
    public static final String KEY_DIFFICULTY = "com.ideanov.minesweeper.difficulty";

    private TextView txtMineCount;
    private TextView txtTimer;
    private ImageButton btnSmile;

    private TableLayout mineField; // table layout to add mines to

    private Block blocks[][]; // blocks for mine field
    private int blockDimension = 48; // width of each block
    private int blockPadding = 2; // padding between blocks

    private int numberOfRowsInMineField;
    private int numberOfColumnsInMineField;
    private int totalNumberOfMines;

    private int difficulty;

    // timer to keep track of time elapsed
    private Handler timer = new Handler();
    private int secondsPassed = 0;

    private boolean isTimerStarted; // check if timer already started or not
    private boolean areMinesSet; // check if mines are planted in blocks
    private boolean isGameOver;
    private int minesToFind; // number of mines yet to be discovered


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //Get the difficulty chosen by the user and set it to easy if there is no answer
        difficulty = getIntent().getIntExtra(KEY_DIFFICULTY, 0);
        switch (difficulty)
        {
            case 0:
                numberOfRowsInMineField = 9;
                numberOfColumnsInMineField = 9;
                totalNumberOfMines = 10;
                break;
            case 1:
                numberOfRowsInMineField = 11;
                numberOfColumnsInMineField = 9;
                totalNumberOfMines = 20;
                break;
            case 2:
                numberOfRowsInMineField = 15;
                numberOfColumnsInMineField = 9;
                totalNumberOfMines = 30;
                break;
        }
        //Define if the user is continuing a game and set it to false by default
        boolean continueOrNot = getIntent().getBooleanExtra(KEY_CONTINUE, false);

        txtMineCount = (TextView) findViewById(R.id.MineCount);
        txtTimer = (TextView) findViewById(R.id.Timer);

        btnSmile = (ImageButton) findViewById(R.id.Smiley);
        btnSmile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                endExistingGame();
                startNewGame();
            }
        });
        mineField = (TableLayout)findViewById(R.id.MineField);
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

    private void startNewGame()
    {
        // plant mines and do rest of the calculations
        createMineField();
        // display all blocks in UI
        showMineField();

        minesToFind = totalNumberOfMines;
        isGameOver = false;
        secondsPassed = 0;
        updateMineCountDisplay();
    }

    private void showMineField()
    {
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams((blockDimension + 2 * blockPadding) * numberOfColumnsInMineField, blockDimension + 2 * blockPadding));

            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                blocks[row][column].setLayoutParams(new TableRow.LayoutParams(
                        blockDimension + 2 * blockPadding,
                        blockDimension + 2 * blockPadding));
                blocks[row][column].setPadding(blockPadding, blockPadding, blockPadding, blockPadding);
                tableRow.addView(blocks[row][column]);
            }
            mineField.addView(tableRow,new TableLayout.LayoutParams(
                    (blockDimension + 2 * blockPadding) * numberOfColumnsInMineField, blockDimension + 2 * blockPadding));
        }
    }

    private void endExistingGame()
    {
        stopTimer(); // stop if timer is running
        txtTimer.setText("000"); // revert all text
        txtMineCount.setText("000"); // revert mines count
        btnSmile.setBackgroundResource(R.drawable.smile);

        // remove all rows from mineField TableLayout
        mineField.removeAllViews();

        // set all variables to support end of game
        isTimerStarted = false;
        areMinesSet = false;
        isGameOver = false;
        minesToFind = 0;
    }

    private void createMineField()
    {
        blocks = new Block[numberOfRowsInMineField + 2][numberOfColumnsInMineField + 2];

        for (int row = 0; row < numberOfRowsInMineField + 2; row++)
        {
            for (int column = 0; column < numberOfColumnsInMineField + 2; column++)
            {
                blocks[row][column] = new Block(this);
                blocks[row][column].setDefaults();

                final int currentRow = row;
                final int currentColumn = column;

                // add Click Listener
                blocks[row][column].setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // start timer on first click
                        if (!isTimerStarted)
                        {
                            startTimer();
                            isTimerStarted = true;
                        }

                        // set mines on first click
                        if (!areMinesSet)
                        {
                            areMinesSet = true;
                            setMines(currentRow, currentColumn);
                        }

                        // check if current block is flagged if flagged the don't do anything
                        // if block is not flagged then uncover nearby blocks
                        if (!blocks[currentRow][currentColumn].isFlagged())
                        {
                            // open nearby blocks till we get numbered blocks
                            rippleUncover(currentRow, currentColumn);

                            // click on mine
                            if (blocks[currentRow][currentColumn].hasMine())
                            {
                                finishGame(currentRow,currentColumn);
                            }

                            // check if we win the game
                            if (checkGameWin())
                            {
                                winGame();
                            }
                        }
                    }
                });

                // add Long Click listener
                blocks[row][column].setOnLongClickListener(new View.OnLongClickListener()
                {
                    public boolean onLongClick(View view)
                    {
                        // open all surrounding blocks
                        if (!blocks[currentRow][currentColumn].isCovered() && (blocks[currentRow][currentColumn].getNumberOfMinesInSorrounding() > 0) && !isGameOver)
                        {
                            int nearbyFlaggedBlocks = 0;
                            for (int previousRow = -1; previousRow < 2; previousRow++)
                            {
                                for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                {
                                    if (blocks[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                    {
                                        nearbyFlaggedBlocks++;
                                    }
                                }
                            }

                            // if flagged block count is equal to nearby mine count then open nearby blocks
                            if (nearbyFlaggedBlocks == blocks[currentRow][currentColumn].getNumberOfMinesInSorrounding())
                            {
                                for (int previousRow = -1; previousRow < 2; previousRow++)
                                {
                                    for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                                    {
                                        // don't open flagged blocks
                                        if (!blocks[currentRow + previousRow][currentColumn + previousColumn].isFlagged())
                                        {
                                            // open blocks till we get numbered block
                                            rippleUncover(currentRow + previousRow, currentColumn + previousColumn);

                                            // click on a mine
                                            if (blocks[currentRow + previousRow][currentColumn + previousColumn].hasMine())
                                            {
                                                finishGame(currentRow + previousRow, currentColumn + previousColumn);
                                            }

                                            // check if we won the game
                                            if (checkGameWin())
                                            {
                                                winGame();
                                            }
                                        }
                                    }
                                }
                            }

                            return true;
                        }

                        // if clicked block is enabled, clickable or flagged
                        if (blocks[currentRow][currentColumn].isClickable() &&
                                (blocks[currentRow][currentColumn].isEnabled() || blocks[currentRow][currentColumn].isFlagged()))
                        {
                            // set blank block to flagged
                            if (!blocks[currentRow][currentColumn].isFlagged() && !blocks[currentRow][currentColumn].isQuestionMarked())
                            {
                                blocks[currentRow][currentColumn].setBlockAsDisabled(false);
                                blocks[currentRow][currentColumn].setFlagIcon(true);
                                blocks[currentRow][currentColumn].setFlagged(true);
                                minesToFind--; //reduce mine count
                                updateMineCountDisplay();
                            }
                            // set flagged to question mark
                            else if (!blocks[currentRow][currentColumn].isQuestionMarked())
                            {
                                blocks[currentRow][currentColumn].setBlockAsDisabled(true);
                                blocks[currentRow][currentColumn].setQuestionMarkIcon(true);
                                blocks[currentRow][currentColumn].setFlagged(false);
                                blocks[currentRow][currentColumn].setQuestionMarked(true);
                                minesToFind++; // increase mine count
                                updateMineCountDisplay();
                            }
                            // change to blank square
                            else
                            {
                                blocks[currentRow][currentColumn].setBlockAsDisabled(true);
                                blocks[currentRow][currentColumn].clearAllIcons();
                                blocks[currentRow][currentColumn].setQuestionMarked(false);
                                // if it is flagged then increment mine count
                                if (blocks[currentRow][currentColumn].isFlagged())
                                {
                                    minesToFind++; // increase mine count
                                    updateMineCountDisplay();
                                }
                                // remove flagged status
                                blocks[currentRow][currentColumn].setFlagged(false);
                            }

                            updateMineCountDisplay(); // update mine display
                        }

                        return true;
                    }
                });
            }
        }
    }

    private boolean checkGameWin()
    {
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                if (!blocks[row][column].hasMine() && blocks[row][column].isCovered())
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateMineCountDisplay()
    {
        if (minesToFind < 0)
        {
            txtMineCount.setText(Integer.toString(minesToFind));
        }
        else if (minesToFind < 10)
        {
            txtMineCount.setText("00" + Integer.toString(minesToFind));
        }
        else if (minesToFind < 100)
        {
            txtMineCount.setText("0" + Integer.toString(minesToFind));
        }
        else
        {
            txtMineCount.setText(Integer.toString(minesToFind));
        }
    }

    private void winGame()
    {
        stopTimer();
        isTimerStarted = false;
        isGameOver = true;
        minesToFind = 0; //set mine count to 0

        //set icon to cool dude
        btnSmile.setBackgroundResource(R.drawable.cool);

        updateMineCountDisplay(); // update mine count

        // disable all buttons
        // set flagged all un-flagged blocks
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                blocks[row][column].setClickable(false);
                if (blocks[row][column].hasMine())
                {
                    blocks[row][column].setBlockAsDisabled(false);
                    blocks[row][column].setFlagIcon(true);
                }
            }
        }

        // show message
        showDialog("You won in " + Integer.toString(secondsPassed) + " seconds!", 1000, false, true);
        enterUsername();
    }

    private void finishGame(int currentRow, int currentColumn)
    {
        isGameOver = true; // mark game as over
        stopTimer(); // stop timer
        isTimerStarted = false;
        btnSmile.setBackgroundResource(R.drawable.sad);

        // show all mines
        // disable all blocks
        for (int row = 1; row < numberOfRowsInMineField + 1; row++)
        {
            for (int column = 1; column < numberOfColumnsInMineField + 1; column++)
            {
                // disable block
                blocks[row][column].setBlockAsDisabled(false);

                // block has mine and is not flagged
                if (blocks[row][column].hasMine() && !blocks[row][column].isFlagged())
                {
                    // set mine icon
                    blocks[row][column].setMineIcon(false);
                }

                // block is flagged and doesn't not have mine
                if (!blocks[row][column].hasMine() && blocks[row][column].isFlagged())
                {
                    // set flag icon
                    blocks[row][column].setFlagIcon(false);
                }

                // block is flagged
                if (blocks[row][column].isFlagged())
                {
                    // disable the block
                    blocks[row][column].setClickable(false);
                }
            }
        }

        // trigger mine
        blocks[currentRow][currentColumn].triggerMine();

        // show message
        showDialog("You tried for " + Integer.toString(secondsPassed) + " seconds!", 1000, false, false);
    }


    private void setMines(int currentRow, int currentColumn)
    {
        // set mines excluding the location where user clicked
        Random rand = new Random();
        int mineRow, mineColumn;

        for (int row = 0; row < totalNumberOfMines; row++)
        {
            mineRow = rand.nextInt(numberOfColumnsInMineField);
            mineColumn = rand.nextInt(numberOfRowsInMineField);
            if ((mineRow + 1 != currentColumn) || (mineColumn + 1 != currentRow))
            {
                // if mine is already there, don't repeat for same block
                if (blocks[mineColumn + 1][mineRow + 1].hasMine())
                {
                    row--;
                }
                // plant mine at this location
                blocks[mineColumn + 1][mineRow + 1].plantMine();
            }
            // exclude the user clicked location
            else
            {
                row--;
            }
        }

        int nearByMineCount;

        // count number of mines in surrounding blocks
        for (int row = 0; row < numberOfRowsInMineField + 2; row++)
        {
            for (int column = 0; column < numberOfColumnsInMineField + 2; column++)
            {
                // for each block find nearby mine count
                nearByMineCount = 0;
                if ((row != 0) && (row != (numberOfRowsInMineField + 1)) && (column != 0) && (column != (numberOfColumnsInMineField + 1)))
                {
                    // check in all nearby blocks
                    for (int previousRow = -1; previousRow < 2; previousRow++)
                    {
                        for (int previousColumn = -1; previousColumn < 2; previousColumn++)
                        {
                            if (blocks[row + previousRow][column + previousColumn].hasMine())
                            {
                                // a mine was found so increment the counter
                                nearByMineCount++;
                            }
                        }
                    }

                    blocks[row][column].setNumberOfMinesInSurrounding(nearByMineCount);
                }
                // for side rows (0th and last row/column)
                // set count as 9 and mark it as opened
                else
                {
                    blocks[row][column].setNumberOfMinesInSurrounding(9);
                    blocks[row][column].OpenBlock();
                }
            }
        }
    }

    private void rippleUncover(int rowClicked, int columnClicked)
    {
        // don't open flagged or mined rows
        if (blocks[rowClicked][columnClicked].hasMine() || blocks[rowClicked][columnClicked].isFlagged())
        {
            return;
        }

        // open clicked block
        blocks[rowClicked][columnClicked].OpenBlock();

        // if clicked block have nearby mines then don't open further
        if (blocks[rowClicked][columnClicked].getNumberOfMinesInSorrounding() != 0 )
        {
            return;
        }

        // open next 3 rows and 3 columns recursively
        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 3; column++)
            {
                // check all the above checked conditions if met then open subsequent blocks
                if (blocks[rowClicked + row - 1][columnClicked + column - 1].isCovered()
                        && (rowClicked + row - 1 > 0) && (columnClicked + column - 1 > 0)
                        && (rowClicked + row - 1 < numberOfRowsInMineField + 1) && (columnClicked + column - 1 < numberOfColumnsInMineField + 1))
                {
                    rippleUncover(rowClicked + row - 1, columnClicked + column - 1 );
                }
            }
        }
        return;
    }

    public void startTimer()
    {
        if (secondsPassed == 0)
        {
            timer.removeCallbacks(updateTimeElasped);
            // tell timer to run call back after 1 second
            timer.postDelayed(updateTimeElasped, 1000);
        }
    }

    public void stopTimer()
    {
        // disable call backs
        timer.removeCallbacks(updateTimeElasped);
    }

    // timer call back when timer is ticked
    private Runnable updateTimeElasped = new Runnable()
    {
        public void run()
        {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;

            if (secondsPassed < 10)
            {
                txtTimer.setText("00" + Integer.toString(secondsPassed));
            }
            else if (secondsPassed < 100)
            {
                txtTimer.setText("0" + Integer.toString(secondsPassed));
            }
            else
            {
                txtTimer.setText(Integer.toString(secondsPassed));
            }

            // add notification
            timer.postAtTime(this, currentMilliseconds);
            // notify to call back after 1 seconds
            timer.postDelayed(updateTimeElasped, 1000);
        }
    };

    private void showDialog(String message, int milliseconds, boolean useSmileImage, boolean useCoolImage)
    {
        // show message
        Toast dialog = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG);

        dialog.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout dialogView = (LinearLayout) dialog.getView();
        ImageView coolImage = new ImageView(getApplicationContext());
        if (useSmileImage)
        {
            coolImage.setImageResource(R.drawable.smile);
        }
        else if (useCoolImage)
        {
            coolImage.setImageResource(R.drawable.cool);
        }
        else
        {
            coolImage.setImageResource(R.drawable.sad);
        }
        dialogView.addView(coolImage, 0);
        dialog.setDuration(milliseconds);
        dialog.show();
    }
}
