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

public class RemoveUser extends AppCompatActivity {
    Button submitButton;
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_user);
        submitButton = (Button) findViewById(R.id.remove_user_submit);
        username = (EditText) findViewById(R.id.username_holder);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String usernameString = username.getText().toString();
                if(username.length() <= 0)
                    Toast.makeText(RemoveUser.this, "Please enter username", Toast.LENGTH_SHORT).show();
                if(username.length() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("username", usernameString);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
