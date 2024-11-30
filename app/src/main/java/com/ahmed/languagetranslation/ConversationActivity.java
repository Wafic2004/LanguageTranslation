package com.ahmed.languagetranslation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConversationActivity extends AppCompatActivity {
    //UI elements
    RecyclerView conversationList;
    ImageButton backButton;
    Button clearButton;

    //Global variable
    String nameOfSharedPref = "LanguageTranslationConversation";
    SharedPreferences sharedPreferences;
    List<SharedPreferenceDataValues> listOfConversationObject;
    int fromLangKey = 0;
    int toLangKey = 1;
    int firstConversationFromKey = 2;
    int firstConversationToKey = 3;
    int secondConversationFromKey = 4;
    int secondConversationToKey = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conversation);
        Log.d("TestLog", "Gets in the conversation Activity");
        conversationList = findViewById(R.id.conversationList);
        backButton = findViewById(R.id.backButton);
        clearButton = findViewById(R.id.clearConversationButton);
        sharedPreferences = getSharedPreferences(nameOfSharedPref, Context.MODE_PRIVATE);

        settingTheRecyclerList();
        LanguageRecyclerAdapter languageRecyclerAdapter = new LanguageRecyclerAdapter(listOfConversationObject);
        conversationList.setAdapter(languageRecyclerAdapter);
        conversationList.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(ConversationActivity.this, DividerItemDecoration.VERTICAL);
        conversationList.addItemDecoration(decoration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //button to go back to the main activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(ConversationActivity.this, MainActivity.class));
                        finish();
                    }
                }, 150);
            }
        });
        //button to reset the shared preference
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, ?> keyCollection = sharedPreferences.getAll();
                for (Map.Entry<String, ?> keys : keyCollection.entrySet()){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(keys.getKey()).apply();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(ConversationActivity.this, MainActivity.class));
                        finish();
                    }
                }, 150);
            }
        });
    }

    void settingTheRecyclerList() {
        listOfConversationObject = new ArrayList<>();
        Map<String, ?> keyCollection = sharedPreferences.getAll();
        for (Map.Entry<String, ?> keys : keyCollection.entrySet()){
            JSONArray showDataTest;
            try {
                showDataTest = new JSONArray(sharedPreferences.getString(keys.getKey(), "[]"));
                String fromLang = showDataTest.getString(fromLangKey);
                String toLang = showDataTest.toString(toLangKey);
                String firstConversationFrom = showDataTest.getString(firstConversationFromKey);
                String firstConversationTo = showDataTest.getString(firstConversationToKey);
                String secondConversationFrom = showDataTest.getString(secondConversationFromKey);
                String secondConversationTo = showDataTest.getString(secondConversationToKey);

                //Adding to the SharedPreferenceDataValues class
                SharedPreferenceDataValues sharedPreferenceDataValues = new SharedPreferenceDataValues(fromLang, toLang, firstConversationFrom, firstConversationTo, secondConversationFrom, secondConversationTo);
                listOfConversationObject.add(sharedPreferenceDataValues);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}