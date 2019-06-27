package com.example.ssp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private TableLayout mTableLayout;
    private SudokuTable mSudokuTable;
    private boolean ignoreNextText;

    final static int DEFAULT_BOARD_SIZE = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSudokuTable = new SudokuTable(DEFAULT_BOARD_SIZE);

        mTableLayout = (TableLayout) findViewById(R.id.board_table);
        createTable(DEFAULT_BOARD_SIZE);

    }

    private void createTable(final int size) {

        for (int i = 0; i < size; i++) {
            final TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)); //sets width and height
            tableRow.setId(i + 100); //sets id between 100 and 100 + boardSize

            for (int j = 0; j < size; j++) {
                final TableEntryPointEdit editText = new TableEntryPointEdit(this);
                editText.setId(i * size + j); //sets id between 0 and 80

                mTableLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        editText.setHeight(mTableLayout.getWidth() / 9);
                    }
                });
                //editText.setHeight(tableRow.getWidth());
                //http://stackoverflow.com/questions/3591784/getwidth-and-getheight-of-view-returns-0

                editText.addTextChangedListener(new TextWatcher() { //add custom textwatcher class that accesses edittext parent and such and changes background id's of all edititexts
                    //will need a way to check if certain set of edittexts background has been set and change it if changes

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (ignoreNextText)
                            return;

                        if (editText.length() > 1) {
                            String newInput = s.toString().substring(s.length() - 1); //grabs the last value entered
                            editText.setText(newInput); //sets the text to the newInput
                            editText.setSelection(editText.length()); //sets cursor to end of editText
                            checkEntry(editText.getId());
                        } else if (editText.length() == 1 && s.length() == 1) {
                            mSudokuTable.setData(editText.getId(), Integer.parseInt(s.toString())); //sets board data when something entered
                            checkEntry(editText.getId());
                        } else if (count == 0 && editText.length() == 0) {
                            mSudokuTable.deleteSingleData(editText.getId());
                            checkEntry(editText.getId());
                            Log.v("onTextChanged", "deleting data at index " + editText.getId());
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                tableRow.addView(editText);
            }

            mTableLayout.addView(tableRow); //add the row to the table
        }
    }



    private void checkEntry(int editTextId) {
        TableEntryPointEdit tempET;
        int startPos;

        switch (mSudokuTable.checkData(editTextId)) {
            case 1:
                //set col background for elements to red
                for (int i = mSudokuTable.computeX(editTextId); i < mSudokuTable.getSize() * mSudokuTable.getSize(); i += 9) {
                    tempET = (TableEntryPointEdit) findViewById(i);
                    if (tempET != null)
                        tempET.setInvalidEntry(true);
                    //tempET.setBackgroundResource(R.drawable.invalid_cell);
                }
                break;
            case 2:
                //set row background for elements to red
                startPos = mSudokuTable.computeY(editTextId) * mSudokuTable.getSize();
                for (int i = startPos; i < startPos + 9; i++) {
                    tempET = (TableEntryPointEdit) findViewById(i);
                    if (tempET != null)
                        tempET.setInvalidEntry(true);
                    //tempET.setBackgroundResource(R.drawable.invalid_cell);
                }
                break;
            case 3:
                //set box background for elements to red
                startPos = (editTextId - (editTextId % 3)) - ((editTextId / 9) % 3 * 9);
                for (int y = startPos; y < 27 + startPos; y += 9) {
                    for (int loc = y; loc < y + 3; loc++) {
                        tempET = (TableEntryPointEdit) findViewById(loc);
                        if (tempET != null)
                            tempET.setInvalidEntry(true);
                        //tempET.setBackgroundResource(R.drawable.invalid_cell);
                    }
                }

                break;
            case 0:
                //remove element from list and set background to normal
                break;
        }
    }

}
