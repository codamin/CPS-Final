package com.RAHA.doorlock;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.an.biometric.BiometricCallback;
import com.an.biometric.BiometricManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity implements BiometricCallback {
    private ProgressDialog progress;
    String address = null;
    String myAddress = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket myBtSocket = null;
    private Boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BiometricManager mBiometricManager;
    boolean onButtonSelect = false;
    boolean offButtonSelect = false;

    SharedPreferences userInfo = null;
    SharedPreferences.Editor userInfoEditor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userInfo = getApplicationContext().getSharedPreferences("DoorLockInfo", MODE_PRIVATE);
        userInfoEditor = userInfo.edit();
        Log.d("Main", ">>>>>>>>>>>>>>> shit created");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Connect myConnect = new Connect();
        address = myConnect.pairedDevicesList();

        Button btnOn = (Button) findViewById(R.id.button_on);
        Button btnOff = (Button) findViewById(R.id.button_off);
        Button addUser = (Button) findViewById(R.id.button_add_user);

        new ConnectBT().execute();

        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {
                if (!userInfo.contains("username") | !userInfo.contains("password")) {
                    Intent addUserIntent = new Intent(MainActivity.this,
                            AddUser.class);
                    startActivityForResult(addUserIntent, 2);
                }
                else if (v == btnOn) {
                    onButtonSelect = true;
                    offButtonSelect = false;
                }
                else if (v == btnOff) {
                    onButtonSelect = false;
                    offButtonSelect = true;
                }
                else if (v == addUser) {
                    Intent addUserIntent = new Intent(MainActivity.this,
                            AddUser.class);
                    startActivityForResult(addUserIntent, 1);
                }

                if ((userInfo.contains("username") & userInfo.contains("password")) & (v == btnOn || v == btnOff || v == addUser)) {
                    mBiometricManager = new BiometricManager.BiometricBuilder(MainActivity.this)
                            .setTitle(getString(R.string.biometric_title))
                            .setSubtitle(getString(R.string.biometric_subtitle))
                            .setDescription(getString(R.string.biometric_description))
                            .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                            .build();
                    mBiometricManager.authenticate(MainActivity.this);
                }
            }
        };

        btnOn.setOnClickListener(handler);
        btnOff.setOnClickListener(handler);
        addUser.setOnClickListener(handler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String username = data.getStringExtra("username");
                String password = data.getStringExtra("password");
                Log.d("Main", ">>>>>>>>>>>>>>> usernameــ"+username);
                Log.d("Main", ">>>>>>>>>>>>>> passwordــ"+password);
                sendAddUser(username, password);
            }
        }
        else if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                String username = data.getStringExtra("username");
                String password = data.getStringExtra("password");
                Log.d("Main", ">>>>>>>>>>>>>>> for unlock username"+username);
                Log.d("Main", ">>>>>>>>>>>>>>> for unlock password"+password);
                userInfoEditor.putString("username", username);
                userInfoEditor.putString("password", password);
                userInfoEditor.apply();
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (myBtSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    myAddress = myBluetooth.getAddress();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    myBtSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    myBtSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }

    private void closeLock()
    {
        String userName = userInfo.getString("username", "nu");
        String userPass = userInfo.getString("password", "ll");
        if (myBtSocket != null) {
            try {
                byte[] message = Security.mergeByteString(userName+"#", Security.run("open#"+userName+"#0000000000000000", userPass));
                myBtSocket.getOutputStream().write(Security.AddNewline(message));
            } catch (IOException e) { msg("Error"); }
        }
    }

    private void openLock()
    {
        String userName = userInfo.getString("username", "");
        String userPass = userInfo.getString("password", "");
        if (myBtSocket!=null) {
            try {
                byte[] message = Security.mergeByteString(userName+"#", Security.run("open#"+userName, userPass));
                Log.d("Main", ">>>> encrypting text >> "+"open#"+userName);
                Log.d("Main", ">>>>>>>>>>>> "+ new String(message));
                byte[] tmpf = Security.run("open#"+userName, userPass);
                int i = 0;
                for (byte b: tmpf){
                    Log.i("myactivity", String.format("0x%20x", b)+ "--" + String.valueOf(i));
                    i += 1;
                }
                Log.d("Main", ">>>>>>>>>>>>>>>>>>>>>>> " + userName + " -- " + userPass);
//                myBtSocket.getOutputStream().write((userName+"#").getBytes());
//                myBtSocket.getOutputStream().write(Security.AddNewline(Security.run("open#", userPass)));
//                myBtSocket.getOutputStream().write(Security.AddNewline(message));
                myBtSocket.getOutputStream().write(Security.AddNewline(message));
                Log.d("Main", "Trying sending Open");
            }
            catch (IOException e) { msg("Error"); }
        }
    }

    public void sendAddUser(String username, String password) {
        String userName = userInfo.getString("username", "");
        String userPass = userInfo.getString("password", "");
        if (myBtSocket != null) {
            try {
                byte[] message = Security.mergeByteString(userName+"#", Security.run("add#"+username+"#"+password+"#0000000000000000", userPass));
                myBtSocket.getOutputStream().write(Security.AddNewline(message));
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void onSdkVersionNotSupported() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_sdk_not_supported), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationNotSupported() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_hardware_not_supported), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationNotAvailable() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_fingerprint_not_available), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_permission_not_granted), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationInternalError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
//        Toast.makeText(getApplicationContext(), getString(R.string.biometric_failure), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationCancelled() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_cancelled), Toast.LENGTH_LONG).show();
        mBiometricManager.cancelAuthentication();
    }

    @Override
    public void onAuthenticationSuccessful() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_success), Toast.LENGTH_LONG).show();
        if(onButtonSelect)
            openLock();
        else if(offButtonSelect)
            closeLock();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//        Toast.makeText(getApplicationContext(), helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
//        Toast.makeText(getApplicationContext(), errString, Toast.LENGTH_LONG).show();
    }
}