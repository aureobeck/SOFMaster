package com.example.aureo.sofmaster;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {

    // ******   Variables  *****
    Context ctx = this;
    private static Bundle extras;
    private static TextView textViewTitle;
    private static TextView textViewDescription;
    private static ListView listViewAnswers;
    private static ArrayAdapter<Answer_Line> adapterListViewAnswer;
    private static ArrayList<Answer_Line> arrayListAnswers = new ArrayList<>();


    // ******   Inicialization Rotines  *****
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // *****   Inicializa Interface   ******
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // *****   Barra Superior   *****
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_question_activity));

        // *****   Inicializa Controles  *****
        find_views();

        // *****   Inicializa Variáveis   ******
        extras = getIntent().getExtras();
        inicializeAnswersList();

        // *****   Configure Controls    *****
        configureControls();


    }

    private void inicializeAnswersList() {

        arrayListAnswers.clear();

        //openAlertDialog("",extras.getString("answers_current_question"));
        try {

            JSONObject jsonObject = new JSONObject(extras.getString("answers_current_question"));
            JSONArray jsonArray = jsonObject.getJSONArray("answers");

            for (int index = 0; index < jsonArray.length(); index++) {
                arrayListAnswers.add(new Answer_Line(
                        "",
                        jsonArray.getJSONObject(index).getJSONObject("owner").getString("display_name"),
                        jsonArray.getJSONObject(index).getJSONObject("owner").getString("profile_image"),
                        jsonArray.getJSONObject(index).getString("score"),
                        jsonArray.getJSONObject(index).getString("answer_id"),
                        jsonArray.getJSONObject(index).getString("body")
                ));
            }
        } catch (org.json.JSONException ex) {
            //openAlertDialog("",ex.getMessage());
            arrayListAnswers.add(new Answer_Line(
                    "",
                    "",
                    "",
                    "",
                    "",
                    getString(R.string.message_empty_answers)
            ));
        }
    }

    private void find_views() {

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewDescription = (TextView) findViewById(R.id.textViewDescriptionContent);
        listViewAnswers = (ListView) findViewById(R.id.listViewAnswers);

    }

    private void configureControls() {
        textViewTitle.setText(extras.getString("title_current_question"));
        textViewDescription.setText(Html.fromHtml(extras.getString("body_current_question")));

        // Configure ListView
        adapterListViewAnswer = new adapterAnswers();
        listViewAnswers.setAdapter(adapterListViewAnswer);
    }

    private class adapterAnswers extends ArrayAdapter<Answer_Line> {

        public adapterAnswers() {
            super(QuestionActivity.this, R.layout.line_answer, arrayListAnswers);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.line_answer, parent, false);
            }

            // Configure each line of listview
            final Answer_Line current_line = arrayListAnswers.get(position);

            ImageView imageViewQuestionUser = (ImageView) itemView.findViewById(R.id.question_user_image);
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(QuestionActivity.this).build();
            ImageLoader.getInstance().init(config);

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(current_line.getImageURL(), imageViewQuestionUser);

            //imageViewQuestionUser.setImageResource();

            TextView textViewQuestionTitle = (TextView) itemView.findViewById(R.id.title_question);
            textViewQuestionTitle.setText(Html.fromHtml("<b>"+current_line.getUser()+"</b>"+current_line.getBody()));

            return itemView;
        }
    }

    public void onBackPressed() {

        acaoVoltar();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                acaoVoltar();
                break;
        }
        return true;
    }

    public void acaoVoltar() {
        NavUtils.navigateUpFromSameTask(this);
    }

    // ******   General Rotines  *****

    public void openAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    private void showToast(String message, Integer length) {
        final Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, length);
    }


}