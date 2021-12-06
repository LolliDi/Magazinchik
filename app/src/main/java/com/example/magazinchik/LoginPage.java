package com.example.magazinchik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity implements View.OnClickListener {
    EditText LoginBox;
    EditText PasBox;
    EditText RoleBox;
    SQLiteDatabase db;
    DBHelper dbHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Button btnEntr = findViewById(R.id.btnEntr);
        btnEntr.setOnClickListener(this);

        Button btnReg = findViewById(R.id.btnReg);
        btnReg.setOnClickListener(this);

        LoginBox = findViewById(R.id.LoginBox);
        PasBox = findViewById(R.id.PassBox);
        RoleBox = findViewById(R.id.roleBox);
        dbHelp = new DBHelper(this);
        db = dbHelp.getWritableDatabase();

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnEntr:



                if(find(false))
                {

                    return;
                }
                else
                {
                    Toast.makeText(this,"Введенная комбинация логина и пароля не найдена",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnReg:
                if(find(true))
                {
                    Toast.makeText(this,"Такой логин уже зарегестрирован!",Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    int role;
                    try
                    {
                       role = Integer.parseInt(RoleBox.getText().toString());
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this,"Введите в третье поле номер роли\n(1 - админ, остальные - другие)",Toast.LENGTH_LONG).show();
                        return;
                    }
                    String log = LoginBox.getText().toString();
                    String pas = PasBox.getText().toString();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.login,log);
                    contentValues.put(DBHelper.password,pas);
                    contentValues.put(DBHelper.role,role);
                    db.insert(DBHelper.tbUsers,null,contentValues);
                    Toast.makeText(this,"Запись добавлена",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public boolean find( boolean searchPas)
    {
        Cursor logCursor = db.query(DBHelper.tbUsers,null,null,null,null,null,null);
        if(logCursor.moveToFirst())
        {
            int userNameId = logCursor.getColumnIndex(DBHelper.login);
            int passwordId = logCursor.getColumnIndex(DBHelper.password);
            int roleId = logCursor.getColumnIndex(DBHelper.role);
            do
            {
                if(LoginBox.getText().toString().equals(logCursor.getString(userNameId))&&(searchPas||PasBox.getText().toString().equals(logCursor.getString(passwordId))))
                {
                    if(logCursor.getInt(roleId)==1)
                    {
                        startActivity(new Intent(this,MainActivity.class));
                    }
                    else
                    {
                        Intent inte = new Intent(this,Magaz.class);

                        inte.putExtra("thisUser",true);
                        startActivity(inte);

                    }
                    logCursor.close();
                    return true;
                }
            }
            while(logCursor.moveToNext());
        }
        logCursor.close();
        return false;
    }
}