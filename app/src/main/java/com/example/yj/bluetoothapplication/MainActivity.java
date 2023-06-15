package com.example.yj.bluetoothapplication;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    private static final String TAG = "bluetooth2";

    ToggleButton btnLed1, btnLed2, btnLed3;
    EditText editText1,editText2,editText3,editText4,editText5,editText6,editText7,editText8,editText9,editText10,editTextLog;
    RelativeLayout rlayout;
    Handler h;

    final int RECIEVE_MESSAGE = 1;  // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    private static int flag = 0;

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    // private static String address = "FC:A8:9A:00:06:4F";
    private static String address = "00:19:10:00:93:92";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnLed1 = (ToggleButton) findViewById(R.id.button1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btnLed1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.OFF));
        }
        btnLed2 = (ToggleButton) findViewById(R.id.button2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btnLed2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.OFF));
        }
        btnLed3 = (ToggleButton) findViewById(R.id.button3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btnLed3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.OFF));
        }

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);
        editText5 = (EditText) findViewById(R.id.editText5);
        editText6 = (EditText) findViewById(R.id.editText6);
        editText7 = (EditText) findViewById(R.id.editText7);
        editText8 = (EditText) findViewById(R.id.editText8);
        editText9 = (EditText) findViewById(R.id.editText9);
        editText10 = (EditText) findViewById(R.id.editText10);
        editTextLog = (EditText) findViewById(R.id.logEditText);

        rlayout = (RelativeLayout) findViewById(R.id.layout);
        h = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        try{
                            byte[] readBuf = (byte[]) msg.obj;
                            String strIncom = new String(readBuf, 0, msg.arg1);
                            String cleanString;
                            double parsedValue;
                            String formattedValue;
                            sb.append(strIncom);
                            int endOfLineIndex = sb.indexOf("\r\n");
                            if (endOfLineIndex >= 0) {
                                String completeMessage = sb.substring(0, endOfLineIndex);
                                sb.delete(0, endOfLineIndex + 2); // Xóa chuỗi đã được xử lý
                                editTextLog.setText(completeMessage);

                                String[] values = completeMessage.split("_");
                                if (values.length == 7) {
                                    cleanString = (values[0].replace(".", ""));
                                    parsedValue = Double.parseDouble(cleanString) / 100.0;;
                                    formattedValue = String.format("%05.2f", parsedValue);
                                    editText1.setText(formattedValue);

                                    cleanString = (values[1].replace(".", ""));
                                    parsedValue = Double.parseDouble(cleanString) / 100.0;;
                                    formattedValue = String.format("%05.2f", parsedValue);
                                    editText2.setText(formattedValue);

                                    cleanString = (values[2].replace(".", ""));
                                    parsedValue = Double.parseDouble(cleanString) / 100.0;;
                                    formattedValue = String.format("%05.2f", parsedValue);
                                    editText3.setText(formattedValue);

                                    cleanString = (values[3].replace(".", ""));
                                    parsedValue = Double.parseDouble(cleanString) / 100.0;;
                                    formattedValue = String.format("%05.2f", parsedValue);
                                    editText4.setText(formattedValue);

                                    cleanString = (values[4].replace(".", ""));
                                    parsedValue = Double.parseDouble(cleanString) / 100.0;;
                                    formattedValue = String.format("%05.2f", parsedValue);
                                    editText5.setText(formattedValue);

                                    cleanString = (values[5].replace(".", ""));
                                    parsedValue = Double.parseDouble(cleanString) / 100.0;;
                                    formattedValue = String.format("%05.2f", parsedValue);
                                    editText6.setText(formattedValue);

                                    cleanString = (values[6].replace(".", ""));
                                    parsedValue = Double.parseDouble(cleanString) / 100.0;;
                                    formattedValue = String.format("%05.2f", parsedValue);
                                    editText7.setText(formattedValue);
                                }

                            }
                        } catch ( Exception e)
                        {
                            // Do nothing
                        }

                        break;
                }
            };
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        btnLed1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Xử lý khi toggle button được chọn
                    mConnectedThread.write("1_ON");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        buttonView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.ON)); // Đổi màu nền thành màu xanh
                    }
                } else {
                    // Xử lý khi toggle button không được chọn
                    mConnectedThread.write("1_OFF");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        buttonView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.OFF)); // Đổi màu nền thành màu xanh
                    }
                }
            }
        });

        btnLed2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Xử lý khi toggle button được chọn
                    mConnectedThread.write("2_ON");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        buttonView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.ON)); // Đổi màu nền thành màu xanh
                    }
                } else {
                    // Xử lý khi toggle button không được chọn
                    mConnectedThread.write("2_OFF");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        buttonView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.OFF)); // Đổi màu nền thành màu xanh
                    }
                }
            }
        });

        btnLed3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Xử lý khi toggle button được chọn
                    mConnectedThread.write("3_ON");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        buttonView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.ON)); // Đổi màu nền thành màu xanh
                    }
                } else {
                    // Xử lý khi toggle button không được chọn
                    mConnectedThread.write("3_OFF");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        buttonView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.OFF)); // Đổi màu nền thành màu xanh
                    }
                }
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using its address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Create a separate thread for the connection process
        Thread connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Establish the connection. This will block until it connects.
                Log.d(TAG, "...Connecting...");
                try {
                    btSocket.connect();
                    Log.d(TAG, "....Connection ok...");

                    // Create a data stream so we can talk to the server.
                    Log.d(TAG, "...Create Socket...");
                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();
                } catch (IOException e) {
                    try {
                        btSocket.close();
                    } catch (IOException e2) {
                        errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                    }
                }
            }
        });

        // Start the connection thread
        connectThread.start();
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()
            byte i = 0;
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (Exception e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}