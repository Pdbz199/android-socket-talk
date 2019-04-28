package com.example.sockets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    private ListView messages;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    private EditText message;
    private Button send;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mSocket = IO.socket("http://192.168.43.77:3000");
        } catch (URISyntaxException e) {}

        setContentView(R.layout.activity_main);

        mSocket.on("message", onNewMessage);
        mSocket.connect();

        messages = findViewById(R.id.messages);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        messages.setAdapter(adapter);
        message = findViewById(R.id.EditText01);
        send = findViewById(R.id.myButton);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = message.getText().toString().trim();
                message.setText("");

                JSONObject data = new JSONObject();
                try {
                    data.put("msg", msg);
                    data.put("id", 1);
                } catch (JSONException e) {
                    return;
                }

                mSocket.emit("message", data);
            }
        });
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;
                    int id;
                    try {
                        message = data.getString("msg");
                        id = data.getInt("id");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    addMessage(message, id);
                }
            });
        }
    };

    private void addMessage(String message, int id) {
        listItems.add(message);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off("message", onNewMessage);
    }
}