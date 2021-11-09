package com.example.magazinchik;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int LONG_DELAY = 7000;
    EditText etNazv, etPrice;
    SQLiteDatabase database;
    ContentValues contentValues;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button BtnTovar = findViewById(R.id.BtnNewTovar);
        BtnTovar.setOnClickListener(this);

        Button btnMagaz = findViewById(R.id.btnMag);
        btnMagaz.setOnClickListener(this);


        etNazv = findViewById(R.id.etNazv);
        etPrice=findViewById(R.id.etPrice);
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        UpdateTable();

    }

    @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.BtnNewTovar:
                    try {
                        String naz = etNazv.getText().toString();
                        float spr = Float.valueOf(etPrice.getText().toString());
                        contentValues = new ContentValues();
                        contentValues.put(DBHelper.nazv, naz);
                        contentValues.put(DBHelper.sprice, spr);
                        database.insert(DBHelper.tb_contacts, null, contentValues);
                    } catch (Exception ee) {
                        Toast tt = Toast.makeText(getApplicationContext(), "Ошибка!", Toast.LENGTH_LONG);
                        tt.show();
                    }
                    break;
                case R.id.btnMag:
                    Intent inten = new Intent(this,Magaz.class);
                    startActivity(inten);
                    break;
                default:
                    try{
                        contentValues = new ContentValues();
                    View outputDBRow = (View) v.getParent();
                    ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                    outputDB.removeView(outputDBRow);
                    outputDB.invalidate();
                    int gid = v.getId();
                    database.delete(DBHelper.tb_contacts, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf(gid)});
                    Cursor cursorUpdater = database.query(DBHelper.tb_contacts, null, null, null, null, null, null);
                    int realID = 1;
                    if (cursorUpdater.getCount() > gid-1) {
                        if (cursorUpdater.moveToFirst()) {
                            int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                            int nazvIndex = cursorUpdater.getColumnIndex(DBHelper.nazv);
                            int priceIndex = cursorUpdater.getColumnIndex(DBHelper.sprice);
                            do { if (cursorUpdater.getInt(idIndex) != realID) {
                                    contentValues.put(DBHelper.KEY_ID, realID);
                                    contentValues.put(dbHelper.nazv, cursorUpdater.getString(nazvIndex));
                                    contentValues.put(dbHelper.sprice, cursorUpdater.getString(priceIndex));
                                    database.replace(dbHelper.tb_contacts, null, contentValues);
                                }
                                realID++;
                            } while (cursorUpdater.moveToNext());
                            if (cursorUpdater.moveToLast()) {
                                database.delete(dbHelper.tb_contacts, DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                            }
                        }
                        cursorUpdater.close();
                    }
                    } catch (Exception ee) {
                        Toast tt = Toast.makeText(getApplicationContext(), "Ошибка!"+ee, Toast.LENGTH_LONG);
                        tt.show();}
                    break;
            }
        UpdateTable();
    }
    public void UpdateTable()
    {
        Cursor cursor = database.query(DBHelper.tb_contacts, null, null, null, null, null, null);

        TableLayout dbOutput = findViewById(R.id.dbOutput);
        dbOutput.removeAllViews();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nazvIndex = cursor.getColumnIndex(DBHelper.nazv);
            int priceIndex = cursor.getColumnIndex(DBHelper.sprice);

            do{
                TableRow dbOutputRow = new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)) ;

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputName = new TextView(this);
                params.weight = 3.0f;
                params.width=300;
                outputName.setLayoutParams(params);
                outputName.setText(cursor.getString(nazvIndex));
                dbOutputRow.addView(outputName);

                TextView outputMail = new TextView(this);
                params.weight = 3.0f;
                params.width=300;
                outputMail.setLayoutParams(params);
                outputMail.setText(cursor.getString(priceIndex));
                dbOutputRow.addView(outputMail);

                Button BtnDelete = new Button(this);
                BtnDelete.setOnClickListener(this);
                params.weight = 1.0f;
                BtnDelete.setWidth(100);
                BtnDelete.setLayoutParams(params);
                BtnDelete.setText("Удалить запись");
                BtnDelete.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(BtnDelete);

                dbOutput.addView(dbOutputRow);

            } while(cursor.moveToNext());
        }
        cursor.close();
    }
}