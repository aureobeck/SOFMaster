package com.example.aureo.sofmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import net.sf.stackwrap4j.http.HttpClient;
import net.sf.stackwrap4j.query.SearchQuery;

import java.io.IOException;

/**
 * StackOverFlowRequestQuestions - handles communication with StackOverFlow API
 */

public class StackOverFlowRequestQuestions extends AsyncTask<String,Void,String> {

    Context context;
    Boolean dialog_true;
    Dialog dialog;
    AlertDialog alertDialog;

    StackOverFlowRequestQuestions(Context ctx, Boolean dialog) {
        context = ctx;
        dialog_true = dialog;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            SearchQuery query = new SearchQuery();
            query.addTag(params[0]);
            return new HttpClient().sendGetRequest("http://api.stackexchange.com/", "2.2/", "search", context.getString(R.string.api_key), query.getUrlParams());

        }catch (IOException exp){
            //openAlertDialog("",exp.toString());
            return exp.toString();
        }
    }

    @Override
    protected void onPreExecute() {
        if (dialog_true){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_wait_data);
        dialog.setTitle("Processando...");
        dialog.setCancelable(false);
        dialog.show();
        }
        //alertDialog = new AlertDialog.Builder(context).create();
    }

    public interface AsyncResponseStackOverFlowQuestions {
        //void processFinishStackOverFlowQuestions(String output);
        void processFinishStackOverFlowQuestions(String result);
    }

    public AsyncResponseStackOverFlowQuestions delegate = null;


    @Override
    protected void onPostExecute(String result) {
        delegate.processFinishStackOverFlowQuestions(result);
        if (dialog_true) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}