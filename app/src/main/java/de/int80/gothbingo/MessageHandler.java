package de.int80.gothbingo;

import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by jens on 31.10.17.
 */

public class MessageHandler extends WebSocketListener {
    private static final String TAG = MessageHandler.class.getSimpleName();
    private WebSocketService parentService;

    public MessageHandler(WebSocketService context) {
        parentService = context;
    }

    private void handleUnknownMessage(String message) {
        Log.e(TAG, "Unkown message received: " + message);
    }

    private void handleSignin(String params) {
        String[] tokens = params.split(";");
        int gameNumber = Integer.valueOf(tokens[0]);
        String winner = tokens[1];

        if (parentService.getCurrentGameNumber() == 0) {
            parentService.setCurrentGameNumber(gameNumber);
            return;
        }

        if (parentService.getCurrentGameNumber() != gameNumber) {
            parentService.handleLoss(gameNumber, winner);
            return;
        }

        parentService.setCurrentGameNumber(gameNumber);
    }

    private void handleWin(String params) {
        String[] tokens = params.split(";");
        int gameNumber = Integer.valueOf(tokens[0]);
        String winner = tokens[1];

        parentService.handleLoss(gameNumber, winner);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        if (!text.contains(";")) {
            handleUnknownMessage(text);
            return;
        }

        String[] tokens = text.split(";", 2);

        switch (tokens[0]) {
            case "SIGNIN":
                handleSignin(tokens[1]);
                break;
            case "WIN":
                handleWin(tokens[1]);
                break;
            default:
                handleUnknownMessage(text);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("SIGNIN;" + parentService.getGameID());
    }
}