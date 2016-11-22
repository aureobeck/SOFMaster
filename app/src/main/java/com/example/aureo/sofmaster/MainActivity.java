package com.example.aureo.sofmaster;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Main Activity - performs StackOverFlow request in online and offline mode
 */

public class MainActivity extends AppCompatActivity implements

        StackOverFlowRequestQuestions.AsyncResponseStackOverFlowQuestions {

    // ******   Variables  *****
    Context ctx = this;
    private static Bundle extras;
    private static Toolbar toolbar;
    private static Spinner spinnerTags;
    private static ListView listViewQuestions;
    private static ArrayAdapter<CharSequence> adapterSpinnerTags;
    private static ArrayAdapter<Question_Line> adapterListViewQuestions;
    private static ImageView dropdown_spinner;
    private static ArrayList<Question_Line> arrayListQuestions = new ArrayList<>();
    private static boolean sync_mode = true;
    private static boolean first_access = true;

    // ******   Inicialization Rotines  *****
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

        // *****   Configure Controls    *****
        configureControls();

        // *****   Events   *****
        onQuestionClick();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Sync Icon Action: change to "online" and "offline" mode
            case R.id.icon_sync:
                if (sync_mode) {
                    sync_mode = false;
                    item.setIcon(R.mipmap.ic_sync_disabled_white_24dp);
                    openSyncWarningOffline();
                } else {
                    sync_mode = true;
                    item.setIcon(R.mipmap.ic_sync_white_24dp);
                    openSyncWarningOnline();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Configure Menu - Toolbar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_activity, menu);

        // *****   Inicialize Variables   ******
        inicializeVariables();

        return super.onCreateOptionsMenu(menu);
    }

    private void find_views() {
        spinnerTags = (Spinner) findViewById(R.id.spinner_tags);
        listViewQuestions = (ListView) findViewById(R.id.lista_view_questions);
        dropdown_spinner = (ImageView) findViewById(R.id.image_view_dropdown_spinner);
    }

    private void inicializeVariables() {
        arrayListQuestions.clear();
        try {
            // Inicialize variables if this inicialization is from Question_Activity
            extras = getIntent().getExtras();
            if (extras.getString("sync_mode_info").equals("false")) {
                sync_mode = false;
                toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_sync_disabled_white_24dp);
                showToast(getString(R.string.title_offline_mode),2000);
            }else{
                showToast(getString(R.string.title_online_mode),2000);
            }
            spinnerTags.setSelection(Integer.parseInt(extras.getString("tag_info")));
        }catch (Exception exp){
            // First Inicialization - not good practice
        }

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

        // Refresh the listview with the questions from StackOverFlow API if in "Online Mode"
        // Or refresh with questions from SQLite Database

        if (sync_mode) {
            // Read from API
            StackOverFlowRequestQuestions stackOverFlowRequest = new StackOverFlowRequestQuestions(ctx, true);
            stackOverFlowRequest.delegate = MainActivity.this;
            stackOverFlowRequest.execute(spinnerTags.getSelectedItem().toString().replaceAll(" ", "-"));
            // Async Task - continues in "processFinishStackOverFlowQuestions"
        } else {
            // Read from DataBase
            getDataBase();
        }
    }

    @Override
    public void processFinishStackOverFlowQuestions(String questions) {

        if (questions == null) {
            // No results
            openAlertDialog(getString(R.string.word_search), getString(R.string.message_empty_result));
        } else {
            // Separates the JSON info in the Question_Line object
            JSON_Questions(questions);
            // Refresh the Database
            refreshCurrentDatabase(spinnerTags.getSelectedItem().toString().replaceAll(" ", "_").toLowerCase());
        }
    }

    private void refreshCurrentDatabase(String tag) {
        // Refresh the Database
        DataBaseQuestions dataBaseQuestions = new DataBaseQuestions(this, tag);
        dataBaseQuestions.deletePreviosTable();

        ArrayList<Boolean> responses = new ArrayList<>();
        for (int index = 0; index < arrayListQuestions.size(); index++) {
            responses.add(dataBaseQuestions.insertQuestionLine(
                    arrayListQuestions.get(index).getTitle(),
                    arrayListQuestions.get(index).getUser(),
                    arrayListQuestions.get(index).getImageURL(),
                    arrayListQuestions.get(index).getClassification(),
                    arrayListQuestions.get(index).getId(),
                    arrayListQuestions.get(index).getBody(),
                    arrayListQuestions.get(index).getAnswerJSON())
            );
        }
    }

    private void getDataBase() {
        // Read Data from SQLite Database
        DataBaseQuestions dataBaseQuestions = new DataBaseQuestions(this, spinnerTags.getSelectedItem().toString().replaceAll(" ", "_").toLowerCase());
        Cursor current_database_cursor = dataBaseQuestions.getAllData();
        arrayListQuestions.clear();
        if (current_database_cursor.getCount()==0) {
            openSyncWarningEmptyResultsOffline();
        } else {
            while (current_database_cursor.moveToNext()) {
                arrayListQuestions.add(new Question_Line(
                        current_database_cursor.getString(1),       // Title
                        current_database_cursor.getString(2),       //  User
                        current_database_cursor.getString(3),       // Image_URL
                        current_database_cursor.getString(4),       // Classification
                        current_database_cursor.getString(5),       // Id
                        current_database_cursor.getString(6),       // Body
                        current_database_cursor.getString(7)        // Answer JSON
                ));
            }
        }
        adapterListViewQuestions.notifyDataSetChanged();
    }

    private void onQuestionClick() {
        // Open QuestionActivity
        listViewQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent("com.example.aureo.sofmaster.QuestionActivity");
                intent.putExtra("id_current_question", arrayListQuestions.get(position).getId());
                intent.putExtra("title_current_question", arrayListQuestions.get(position).getTitle());
                intent.putExtra("body_current_question", arrayListQuestions.get(position).getBody());
                intent.putExtra("answers_current_question", arrayListQuestions.get(position).getAnswerJSON());
                if(sync_mode){
                    intent.putExtra("sync_mode_info", "true");
                }else{
                    intent.putExtra("sync_mode_info", "false");
                }
                Integer position_spinner = spinnerTags.getSelectedItemPosition();
                intent.putExtra("tag_info", position_spinner.toString());
                startActivity(intent);
            }
        });
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
                            "{\"answers\": " + jsonArray.getJSONObject(index).getJSONArray("answers").toString() + "}"
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

    private void openSyncWarningOnline(){

        final Dialog dialog_two_buttons = new Dialog(ctx);
        dialog_two_buttons.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_two_buttons.setContentView(R.layout.dialog_two_buttons);
        dialog_two_buttons.show();
        dialog_two_buttons.setCancelable(false);

        TextView text_view_title = (TextView) dialog_two_buttons.findViewById(R.id.text_view_title);
        text_view_title.setText(getString(R.string.title_online_mode));

        TextView text_view_message = (TextView) dialog_two_buttons.findViewById(R.id.text_view_message);
        text_view_message.setText(getString(R.string.message_online_mode));

        Button button_go = (Button) dialog_two_buttons.findViewById(R.id.button_go);
        button_go.setText(getString(R.string.word_continue));
        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_two_buttons.cancel();
                sync_mode = true;
                toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_sync_white_24dp);
                try {
                    refreshTag();
                } catch (IOException | net.sf.stackwrap4j.json.JSONException e) {
                    //openAlertDialog("Error", e.toString());
                }
            }
        });
        Button button_cancel = (Button) dialog_two_buttons.findViewById(R.id.button_cancel);
        button_cancel.setText(getString(R.string.word_cancel));
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_two_buttons.cancel();
                sync_mode = false;
                toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_sync_disabled_white_24dp);
            }
        });
    }

    private void openSyncWarningOffline(){

        final Dialog dialog_two_buttons = new Dialog(ctx);
        dialog_two_buttons.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_two_buttons.setContentView(R.layout.dialog_two_buttons);
        dialog_two_buttons.show();
        dialog_two_buttons.setCancelable(false);

        TextView text_view_title = (TextView) dialog_two_buttons.findViewById(R.id.text_view_title);
        text_view_title.setText(getString(R.string.title_offline_mode));

        TextView text_view_message = (TextView) dialog_two_buttons.findViewById(R.id.text_view_message);
        text_view_message.setText(getString(R.string.message_offline_mode));

        Button button_go = (Button) dialog_two_buttons.findViewById(R.id.button_go);
        button_go.setText(getString(R.string.word_continue));
        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_two_buttons.cancel();
                sync_mode = false;
                toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_sync_disabled_white_24dp);
                showToast(getString(R.string.title_offline_mode),2000);
            }
        });
        Button button_cancel = (Button) dialog_two_buttons.findViewById(R.id.button_cancel);
        button_cancel.setText(getString(R.string.word_cancel));
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_two_buttons.cancel();
                sync_mode = true;
                toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_sync_white_24dp);
                showToast(getString(R.string.title_online_mode),2000);
            }
        });
    }

    private void openSyncWarningEmptyResultsOffline(){

        final Dialog dialog_two_buttons = new Dialog(ctx);
        dialog_two_buttons.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_two_buttons.setContentView(R.layout.dialog_two_buttons);
        dialog_two_buttons.show();
        dialog_two_buttons.setCancelable(false);

        TextView text_view_title = (TextView) dialog_two_buttons.findViewById(R.id.text_view_title);
        text_view_title.setText(getString(R.string.message_empty_result));

        TextView text_view_message = (TextView) dialog_two_buttons.findViewById(R.id.text_view_message);
        text_view_message.setText(getString(R.string.message_empty_result_offline));

        Button button_go = (Button) dialog_two_buttons.findViewById(R.id.button_go);
        button_go.setText(getString(R.string.word_change));
        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_two_buttons.cancel();
                sync_mode = true;
                toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_sync_white_24dp);
                try {
                    refreshTag();
                } catch (IOException | net.sf.stackwrap4j.json.JSONException e) {
                    //openAlertDialog("Error", e.toString());
                }

            }
        });
        Button button_cancel = (Button) dialog_two_buttons.findViewById(R.id.button_cancel);
        button_cancel.setText(getString(R.string.word_cancel));
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_two_buttons.cancel();
            }
        });
    }

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
