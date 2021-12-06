package com.example.magazinchik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Magaz extends AppCompatActivity implements View.OnClickListener{

    TextView TVPrice;
    SQLiteDatabase database;
    DBHelper dbHelper;
    public List kol = new ArrayList();

    boolean z = true; // первый проход по записям
    float price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magaz);

        Bundle arg = getIntent().getExtras();
        boolean thisUser = arg.getBoolean("thisUser",false);

        Button BtnZak = findViewById(R.id.btnZak);
        BtnZak.setOnClickListener(this);

        Button btndd = findViewById(R.id.btnbd);
        if(thisUser)
        {
            btndd.setVisibility(View.INVISIBLE);
        }
        else
        {
            btndd.setOnClickListener(this);
        }
        TVPrice = findViewById(R.id.TVPrice);
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        UpdateTable();


    }

    @Override
    public void onClick(View v)
    {
        Button b = (Button) v;
        String s = b.getText().toString();
        if(s=="В корзину")
        {
            Cursor cursor = database.query(DBHelper.tb_contacts, null, null, null, null, null, null);
            int gid = v.getId();
            cursor.moveToPosition(gid-1);
            int id = cursor.getColumnIndex(DBHelper.sprice);
            try{
                price+=Float.valueOf(cursor.getString(id));
                kol.set(gid-1,((int)kol.get(gid-1)+1));
                TVPrice.setText(price + "₽");
            }
            catch(Exception ee)
            {
                Toast ttt = Toast.makeText(getApplicationContext(), "Ошибка!\n"+ee, Toast.LENGTH_LONG);
                ttt.show();
            }
            cursor.close();
        }
        else {
            switch (v.getId()) {
                case R.id.btnZak:
                    Toast toast = Toast.makeText(getApplicationContext(), "Сумма заказа: " + price + "₽", Toast.LENGTH_LONG);
                    toast.show();
                    for (int ii = 0; ii < kol.size(); ii++) {
                        kol.remove(0);
                        ii--;
                    }
                    z = true;
                    price = 0;
                    TVPrice.setText(price + "₽");
                    break;
                case R.id.btnbd:
                    Intent intent1 = new Intent(this,MainActivity.class);
                    startActivity(intent1);
                    break;
                default:
                    break;
            }
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
                BtnDelete.setText("В корзину");
                BtnDelete.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(BtnDelete);

                dbOutput.addView(dbOutputRow);
                if(z)
                    kol.add(0);

            } while(cursor.moveToNext());
            z=false;
        }
        cursor.close();
    }
}