package com.example.aureo.sofmaster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.sf.stackwrap4j.StackOverflow;
import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.json.JSONException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements

        StackOverFlowRequestQuestions.AsyncResponseStackOverFlowQuestions {

    // ******   Variables  *****
    Context ctx = this;
    private static Toolbar toolbar;
    private static Spinner spinnerTags;
    private static ListView listViewQuestions;
    private static ArrayAdapter<CharSequence> adapterSpinnerTags;
    private static ArrayAdapter<Question_Line> adapterListViewQuestions;
    private static ImageView dropdown_spinner;
    private static ArrayList<Question_Line> arrayListQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // *****   Inicialize Interface   ******
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // *****   Action Bar    *****
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name_long));

        // *****   Inicialize Controls  *****
        find_views();

        // *****   Inicialize Variables   ******
        inicializeVariables();

        // *****   Configure Controls    *****
        configureControls();

        // *****   Events   *****
        onQuestionClick();

    }

    private void find_views() {
        spinnerTags = (Spinner) findViewById(R.id.spinner_tags);
        listViewQuestions = (ListView) findViewById(R.id.lista_view_questions);
        dropdown_spinner = (ImageView) findViewById(R.id.image_view_dropdown_spinner);
    }

    private void inicializeVariables() {

        arrayListQuestions.clear();
    }

    private void configureControls() {

        // Configure Spinner Tag
        adapterSpinnerTags = ArrayAdapter.createFromResource(this, R.array.array_tags, R.layout.text_view_01);
        adapterSpinnerTags.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTags.setAdapter(adapterSpinnerTags);
        spinnerTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    refreshTag();
                } catch (IOException | net.sf.stackwrap4j.json.JSONException e) {
                    openAlertDialog("Error", e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Dropdown Image Click Event
        dropdown_spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerTags.performClick();
            }
        });

        // Configure ListView
        adapterListViewQuestions = new adapterQuestions();
        listViewQuestions.setAdapter(adapterListViewQuestions);

    }

    // ******   Action Rotines  *****

    private void refreshTag() throws IOException, net.sf.stackwrap4j.json.JSONException {

        StackOverFlowRequestQuestions stackOverFlowRequest = new StackOverFlowRequestQuestions(ctx, true);
        stackOverFlowRequest.delegate = MainActivity.this;
        stackOverFlowRequest.execute(spinnerTags.getSelectedItem().toString().replaceAll(" ", "-"));

    }

    private void onQuestionClick() {
        listViewQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent("com.example.aureo.sofmaster.QuestionActivity");
                intent.putExtra("id_current_question", arrayListQuestions.get(position).getId());
                intent.putExtra("title_current_question", arrayListQuestions.get(position).getTitle());
                intent.putExtra("body_current_question", arrayListQuestions.get(position).getBody());
                intent.putExtra("answers_current_question", arrayListQuestions.get(position).getAnswerJSON());
                startActivity(intent);
            }
        });
    }

    private ArrayList<Answer_Line> jsonToArrayListAnswers(JSONArray jsonArray) {
        ArrayList<Answer_Line> arrayListAnswers = new ArrayList<>();
        try {
            for (int index = 0; index < jsonArray.length(); index++) {
                arrayListAnswers.add(new Answer_Line(
                        jsonArray.getJSONObject(index).getString("title"),
                        jsonArray.getJSONObject(index).getJSONObject("owner").getString("display_name"),
                        jsonArray.getJSONObject(index).getJSONObject("owner").getString("profile_image"),
                        jsonArray.getJSONObject(index).getString("score"),
                        jsonArray.getJSONObject(index).getString("answer_id"),
                        jsonArray.getJSONObject(index).getString("body")
                        ));
            }
        } catch (org.json.JSONException ex) {
            openAlertDialog("", ex.getMessage());
        }
        return arrayListAnswers;
    }

    private void testAPI() {
        String displayText = null;
        try {
            StackWrapper stackWrap = new StackOverflow(getString(R.string.api_key));

            openAlertDialog("", stackWrap.getStats().toString());

            /*Stats stats = stackWrap.getStats();
            displayText = "Stack Overflow Statistics";
            displayText += "\nTotal Questions: " + stats.getTotalQuestions();
            displayText += "\nTotal Unanswered: " + stats.getTotalUnanswered();
            displayText += "\nTotal Answers: " + stats.getTotalAnswers();
            displayText += "\nTotal Comments: " + stats.getTotalComments();
            displayText += "\nTotal Votes: " + stats.getTotalVotes();
            displayText += "\nTotal Users: " + stats.getTotalUsers();*/

            //openAlertDialog("","Result: "+displayText);
        } catch (JSONException | IOException e) {

            displayText = e.getMessage();
            openAlertDialog("", "Error: " + e.getMessage());
        }
    }

    @Override
    public void processFinishStackOverFlowQuestions(String questions) {

        if (questions == null) {
            openAlertDialog(getString(R.string.word_search), getString(R.string.message_empty_result));
        } else {
            JSON_Questions(questions);
        }
    }
    private void JSON_Questions(String json_string) {

        try {
            JSONObject jsonObject = new JSONObject(json_string);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            arrayListQuestions.clear();
            for (int index = 0; index < jsonArray.length(); index++) {
                try {
                arrayListQuestions.add(new Question_Line(
                        jsonArray.getJSONObject(index).getString("title"),
                        jsonArray.getJSONObject(index).getJSONObject("owner").getString("display_name"),
                        jsonArray.getJSONObject(index).getJSONObject("owner").getString("profile_image"),
                        jsonArray.getJSONObject(index).getString("score"),
                        jsonArray.getJSONObject(index).getString("question_id"),
                        jsonArray.getJSONObject(index).getString("body"),
                        "{\"answers\": "+jsonArray.getJSONObject(index).getJSONArray("answers").toString()+"}"
                ));
                } catch (org.json.JSONException ex) {

                    arrayListQuestions.add(new Question_Line(
                            jsonArray.getJSONObject(index).getString("title"),
                            jsonArray.getJSONObject(index).getJSONObject("owner").getString("display_name"),
                            jsonArray.getJSONObject(index).getJSONObject("owner").getString("profile_image"),
                            jsonArray.getJSONObject(index).getString("score"),
                            jsonArray.getJSONObject(index).getString("question_id"),
                            jsonArray.getJSONObject(index).getString("body"),
                            ""
                    ));
                }

            }
            adapterListViewQuestions.notifyDataSetChanged();


            //openAlertDialog("",jsonArray.toString());
        } catch (org.json.JSONException ex) {
            openAlertDialog("", ex.getMessage());
        }

    }


    private class adapterQuestions extends ArrayAdapter<Question_Line> {

        public adapterQuestions() {
            super(MainActivity.this, R.layout.line_tag_result, arrayListQuestions);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.line_tag_result, parent, false);
            }

            // Configure each line of listview
            final Question_Line current_line = arrayListQuestions.get(position);

            ImageView imageViewQuestionUser = (ImageView) itemView.findViewById(R.id.question_user_image);
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MainActivity.this).build();
            ImageLoader.getInstance().init(config);

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(current_line.getImageURL(), imageViewQuestionUser);

            //imageViewQuestionUser.setImageResource();

            TextView textViewQuestionTitle = (TextView) itemView.findViewById(R.id.title_question);
            textViewQuestionTitle.setText(current_line.getTitle());

            TextView textViewQuestionInfo = (TextView) itemView.findViewById(R.id.question_info);
            textViewQuestionInfo.setText(getString(R.string.word_user) + ": " + current_line.getUser() + " - " + getString(R.string.word_classification) + ": " + current_line.getClassification());

            return itemView;
        }
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
