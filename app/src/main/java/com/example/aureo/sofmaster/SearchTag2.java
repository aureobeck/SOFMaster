package com.example.aureo.sofmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchTag2 extends AsyncTask<String,Void,String> {
    Context context;
    Boolean dialog_true;
    Dialog dialog;
    AlertDialog alertDialog;

    SearchTag2(Context ctx, Boolean dialog) {
        context = ctx;
        dialog_true = dialog;
    }
    //Dialog dialog = new Dialog(context);
    @Override
    protected String doInBackground(String... params) {

        String login_url = "https://api.stackexchange.com/2.2/search?";

        try {
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            String post_data =
                    URLEncoder.encode("pagesize", "UTF-8") + "=" + URLEncoder.encode("20", "UTF-8") + "&"
                            + URLEncoder.encode("order", "UTF-8") + "=" + URLEncoder.encode("desc", "UTF-8") + "&"
                            + URLEncoder.encode("sort", "UTF-8") + "=" + URLEncoder.encode("relevance", "UTF-8") + "&"
                            + URLEncoder.encode("tagged", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&"
                            + URLEncoder.encode("site", "UTF-8") + "=" + URLEncoder.encode("stackoverflow", "UTF-8")
                    ;

            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
    // you may separate this or combined to caller class.
    public interface AsyncResponse_Ler {
        //void processFinishLer(String output);
        void processFinishSearch(String result);
    }

    public AsyncResponse_Ler delegate = null;

    public SearchTag2(AsyncResponse_Ler delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(String result) {

        delegate.processFinishSearch(result);
        if (dialog_true) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public ArrayList<ArrayList<String>> jSONArray_ArrayList(String result) {

        ArrayList<ArrayList<String>> arrayList_final = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(result);

            for (int index = 0; index < jsonArray.length(); index++) {

                ArrayList<String> arrayList_01 = new ArrayList<>();
                JSONArray jsonArray_2 = new JSONArray(jsonArray.get(index).toString());

                for (int index_2 = 0; index_2 < jsonArray_2.length(); index_2++) {
                    arrayList_01.add(jsonArray_2.get(index_2).toString());
                }
                arrayList_final.add(arrayList_01);
            }
        } catch (JSONException | NullPointerException je) {
            //openAlertDialog("", "JSON Error");
            //openErrorDialog();
            //openErrorConexion();
            //openAlertDialog("","Vazio 01");
        }
        return arrayList_final;
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
