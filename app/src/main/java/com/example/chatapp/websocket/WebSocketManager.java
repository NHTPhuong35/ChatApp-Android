package com.example.chatapp.websocket;

import android.util.Log;

import com.example.chatapp.models.Message;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {

    private static WebSocketManager instance;
    private WebSocket webSocket;
    private AppWebSocketListener appListener; // đổi tên biến
    private final Gson gson = new Gson();

    private static final String TAG = "WebSocketManager";
    private static final String SERVER_URL = "wss://s14542.blr1.piesocket.com/v3/1?api_key=GmxTZSOLQTJeV1GGH0kV5Z0vkXHVxo1ciDlviCxe&notify_self=1";

    private WebSocketManager() {
        connect();
    }

    public static WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    private void connect() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "WebSocket Received: " + text);
                if (appListener != null) {
                    try {
                        Message message = gson.fromJson(text, Message.class);
                        appListener.onWebSocketMessageReceived(message);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse message: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closing: " + reason);
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Error: " + t.getMessage());
            }
        });
    }

    public void setListener(AppWebSocketListener listener) {
        this.appListener = listener;
    }

    public void sendMessage(Message message) {
        if (webSocket != null) {
            String json = gson.toJson(message);
            webSocket.send(json);
        } else {
            Log.e(TAG, "WebSocket is not connected.");
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closed manually");
            webSocket = null;
        }
    }

    // Interface app dùng (đổi tên tránh conflict)
    public interface AppWebSocketListener {
        void onWebSocketMessageReceived(Message message);
    }
}
