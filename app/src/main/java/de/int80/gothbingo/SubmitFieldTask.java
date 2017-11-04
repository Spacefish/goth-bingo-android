package de.int80.gothbingo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jens on 04.11.17.
 */

public class SubmitFieldTask extends AsyncTask<String, Void, Boolean> {

    private static String TAG = SubmitFieldTask.class.getSimpleName();

    private ProgressDialog dialog;
    private Context parentActivity;

    public SubmitFieldTask(Context activity) {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.submitting_suggestion_message));

        parentActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
        bodyBuilder.setType(MultipartBody.FORM);
        bodyBuilder.addFormDataPart("field", strings[0]);
        RequestBody body = bodyBuilder.build();

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("https://int80.de/bingo/collector.php");
        requestBuilder.post(body);

        Response response;

        try {
            response = new OkHttpClient().newCall(requestBuilder.build()).execute();
        } catch (IOException e) {
            Log.e(TAG, "Submit failed", e);
            return false;
        }

        return response.isSuccessful();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        if (dialog.isShowing())
            dialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        if (success) {
            builder.setTitle(R.string.submit_suggestion_success_title);
            builder.setMessage(R.string.submit_suggestion_success_message);
        } else {
            builder.setTitle(R.string.submit_suggestion_failed_title);
            builder.setMessage(R.string.submit_suggestion_failed_message);
        }

        builder.setPositiveButton(parentActivity.getResources().getString(R.string.ok_action_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }
}
