package com.RAHA.doorlock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class Connect
{
    //widgets
    Button btnPaired;
    ListView devicelist;
    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    public Connect() {
        Log.d("Connect", "Connect class Started");
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            System.out.println("Bluetooth Device Not Available");
        }
        else if(!myBluetooth.isEnabled())
        {
            System.out.println("Turn On Your Bluetooth!!!");
        }

        pairedDevicesList();


    }

    String pairedDevicesList()
    {
        Log.d("Connect", "PairedDeviceList() ran");
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                Log.d("Connect", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                Log.d("Connect", bt.getName());
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println(bt.getName());
                if (bt.getName().equals("DESKTOP-254608Q")) {
//                if (bt.getName().equals("AMIN")) {
//                if (bt.getName().equals("DESKTOP-O50CT6R")) {
                    return bt.getAddress();
                }
            }
        }
        else
        {
            System.out.println("No Paired Bluetooth Devices Found.");
            return "None";
        }
        return null;
    }
}