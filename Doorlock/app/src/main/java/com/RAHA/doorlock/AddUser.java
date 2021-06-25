package com.RAHA.doorlock;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddUser extends AppCompatActivity {
    Button submitButton;
    EditText macAddress, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);
        submitButton = (Button) findViewById(R.id.button);
        macAddress = (EditText) findViewById(R.id.Mac_address_holder);
        password = (EditText)findViewById(R.id.password_holder);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String macAddressString = macAddress.getText().toString();
                String passwordString = password.getText().toString();

                if(macAddressString.length() <= 0)
                    Toast.makeText(AddUser.this, "Please enter MAC address", Toast.LENGTH_SHORT).show();

                if(passwordString.length() <= 0)
                    Toast.makeText(AddUser.this, "Please enter password", Toast.LENGTH_SHORT).show();
                System.out.println(String.format("mac : %s, password : %s", macAddressString, passwordString));
            }
        });

    }
}
