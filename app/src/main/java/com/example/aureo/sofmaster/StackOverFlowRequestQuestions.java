package com.example.aureo.sofmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import net.sf.stackwrap4j.http.HttpClient;
import net.sf.stackwrap4j.query.SearchQuery;

import java.io.IOException;


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

    public StackOverFlowRequestQuestions(AsyncResponseStackOverFlowQuestions delegate){
        this.delegate = delegate;
    }

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

    public void openErrorConexion() {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getString(R.string.title_no_internet));
        alertDialog.setMessage(context.getString(R.string.message_no_internet));
        alertDialog.show();
    }

    public void openAlertDialog(String title, String message) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    private void openErrorDialog() {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(context.getString(R.string.general_error));
        alertDialog.show();
    }

}