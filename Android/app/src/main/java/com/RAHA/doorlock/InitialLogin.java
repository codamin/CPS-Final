package com.RAHA.doorlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InitialLogin extends AppCompatActivity {
    Button submitButton;
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_login);
        submitButton = (Button) findViewById(R.id.button_submit_login);
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText)findViewById(R.id.login_password);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();

                if(usernameString.length() <= 0)
                    Toast.makeText(InitialLogin.this, "Your username must contain at least one character!", Toast.LENGTH_SHORT).show();

                if(passwordString.length() <= 0)
                    Toast.makeText(InitialLogin.this, "Your password must contain at least one character!", Toast.LENGTH_SHORT).show();

                if(usernameString.length() > 0 & passwordString.length() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("username", usernameString);
                    intent.putExtra("password", passwordString);
                    Log.d("InitialLogin", ">>>>>>>>>>>>>>>>>>username " + usernameString);
                    Log.d("InitialLogin", ">>>>>>>>>>>>>>>>>>password " + passwordString);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
