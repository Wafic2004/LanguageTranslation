package com.ahmed.languagetranslation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //Variables for UI
    Button convert_btn;
    EditText multilineTextFrom;
    EditText multilineTextTo;
    Spinner fromLangSpinner;
    Spinner toLangSpinner;
    ImageButton speechToTextButton;
    ImageButton clearButton;
    ImageButton copyButton;

    //Variables for Translator
    Translator translator;
    TranslatorOptions translatorOptions;
    DownloadConditions downloadConditions;

    //A variable to store the object that contains the name and the code of the each languages in it
    ArrayList<NotDownloadedLanguageCodeSeparation> notDownloadedLanguageNameAndCode;
    ArrayList<DownloadedLanguageCodeSeparation> downloadedLanguageNameAndCode;
    //ArrayList<String> downloadedLanguageCodesAL;
    List<String> notDownloadedLanguageCodes;

    //Log tags
    final String activityTest = "ActivityTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Log.d(activityTest, "OnCreate");

        //Interactable UI elements(button and 2 editText) will ge get and stored in the variable
        convert_btn = findViewById(R.id.convert_btn);
        multilineTextFrom = findViewById(R.id.editTextTextMultiLine);
        multilineTextTo = findViewById(R.id.editTextTextMultiLine2);
        fromLangSpinner = findViewById(R.id.fromSpinner);
        toLangSpinner = findViewById(R.id.toSpinner);
        speechToTextButton = findViewById(R.id.micButton);
        clearButton = findViewById(R.id.clearButton);
        copyButton = findViewById(R.id.copyButton);
        Log.d(activityTest, "UI Variables are ready");

        //Calling the settingAllLanguage function to set all languages
        settingAllLanguages();
        Log.d(activityTest, "Setting All Languages");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Translating on button click
        convert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!downloadedLanguageNameAndCode.isEmpty()) {
                    //Variable to get the codes of all languages using LanguageCodeSeparation
                    List<String> languageCode = new ArrayList<>();
                    for (int i = 0; i < downloadedLanguageNameAndCode.size(); i++) {
                        languageCode.add(downloadedLanguageNameAndCode.get(i).getLanguageCode());
                    }

                    //Setting the Translator
                    int fls_SelectedPosition = fromLangSpinner.getSelectedItemPosition();
                    int tls_SelectedPosition = toLangSpinner.getSelectedItemPosition();
                    translatorOptions = new TranslatorOptions.Builder()
                            .setSourceLanguage(languageCode.get(fls_SelectedPosition))
                            .setTargetLanguage(languageCode.get(tls_SelectedPosition))
                            .build();
                    translator = Translation.getClient(translatorOptions);
                    downloadConditions = new DownloadConditions.Builder()
                            .requireWifi()
                            .build();

                    //To translate the text
                    translator.downloadModelIfNeeded(downloadConditions)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //Toast.makeText(this, "Download for languages are completed", Toast.LENGTH_SHORT).show();
                                    if (multilineTextFrom.getText().toString().isEmpty()) {
                                        //no text
                                        Toast.makeText(getBaseContext(), "Please Enter Text in From TextEdit", Toast.LENGTH_SHORT).show();
                                    } else {
                                        translator.translate(String.valueOf(multilineTextFrom.getText()))
                                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        multilineTextTo.setText(s);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        });
        //button to turn on mic system
        speechToTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TestLog", "Getting in");
                Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                //startActivity(recognizerIntent);
                startSpeechToText.launch(recognizerIntent);
            }
        });
        //button to clear text
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multilineTextFrom.getText().clear();
            }
        });
        //button to copy the converted text to clipboard
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Converted Sentence", multilineTextTo.getText());
                clipboardManager.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ActivityResultLauncher<Intent> startSpeechToText = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Log.d("Speech", "Enters the Result");
            if (result != null && result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    ArrayList<String> speechText = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
                    //Log.d("Speech", speechText.isEmpty());
                    String txt_in_editText = multilineTextFrom.getText().toString();
                    if(speechText != null && !speechText.isEmpty()) {
                        if(!txt_in_editText.endsWith(" ")) {
                            String txtSeq = txt_in_editText + " " + speechText.get(0).toString();
                            multilineTextFrom.setText(txtSeq);
                        } else if (txt_in_editText.endsWith(" ")) {
                            String txtSeq = txt_in_editText + speechText.get(0).toString();
                            multilineTextFrom.setText(txtSeq);
                        }
                        Log.d("Speech", "Does worked");
                    } else {
                        Log.d("Speech", "Doesn't worked");
                    }
                }
            }

            if(!downloadedLanguageNameAndCode.isEmpty()) {
                //Variable to get the codes of all languages using LanguageCodeSeparation
                List<String> languageCode = new ArrayList<>();
                for (int i = 0; i < downloadedLanguageNameAndCode.size(); i++) {
                    languageCode.add(downloadedLanguageNameAndCode.get(i).getLanguageCode());
                }

                //Setting the Translator
                int fls_SelectedPosition = fromLangSpinner.getSelectedItemPosition();
                int tls_SelectedPosition = toLangSpinner.getSelectedItemPosition();
                translatorOptions = new TranslatorOptions.Builder()
                        .setSourceLanguage(languageCode.get(fls_SelectedPosition))
                        .setTargetLanguage(languageCode.get(tls_SelectedPosition))
                        .build();
                translator = Translation.getClient(translatorOptions);
                downloadConditions = new DownloadConditions.Builder()
                        .requireWifi()
                        .build();

                //To translate the text
                translator.downloadModelIfNeeded(downloadConditions)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Toast.makeText(this, "Download for languages are completed", Toast.LENGTH_SHORT).show();
                                if (multilineTextFrom.getText().toString().isEmpty()) {
                                    //no text
                                    Toast.makeText(getBaseContext(), "Please Enter Text in From TextEdit", Toast.LENGTH_SHORT).show();
                                } else {
                                    translator.translate(String.valueOf(multilineTextFrom.getText()))
                                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String s) {
                                                    multilineTextTo.setText(s);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        }
    });

    void settingAllLanguages() {
        notDownloadedLanguageNameAndCode = new ArrayList<>();
        downloadedLanguageNameAndCode = new ArrayList<>();
        //downloadedLanguageCodesAL = new ArrayList<>();
        Log.d(activityTest, "Enter the SettingAllLanguages function");
        //Variable to store all language codes and anther List to store the Downloaded Languages
        notDownloadedLanguageCodes = new LinkedList<String>(TranslateLanguage.getAllLanguages());
        //In this RemoteModelManager we separated the downloaded languages and not downloaded languages
        RemoteModelManager.getInstance().getDownloadedModels(TranslateRemoteModel.class).addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
            @Override
            public void onSuccess(Set<TranslateRemoteModel> translateRemoteModels) {
                for(TranslateRemoteModel languageModel : translateRemoteModels) {
                    Log.d("TestLog", "DownloadedLanguage - "+new Locale(languageModel.getLanguage()).getDisplayName());
                    notDownloadedLanguageCodes.remove(languageModel.getLanguage());
                    Log.d("TestLog", "After removal");
                    String languageName = new Locale(languageModel.getLanguage()).getDisplayName();
                    DownloadedLanguageCodeSeparation downloadedLanguageCodeSeparation = new DownloadedLanguageCodeSeparation(languageModel.getLanguage(), languageName);
                    Log.d("TestLog", "Language Name In Downloaded Class - "+downloadedLanguageCodeSeparation.getLanguageName());
                    Log.d("TestLog", "Language Code In Downloaded Class - "+downloadedLanguageCodeSeparation.getLanguageCode());
                    downloadedLanguageNameAndCode.add(downloadedLanguageCodeSeparation);
                }

                //For each loop it stores the code as well as the language name in the notDownloadedLanguageCodeSeparation
                for(String languageCode : notDownloadedLanguageCodes) {
                    Log.d("AllModels", languageCode);
                    Log.d("TestLog", new Locale(languageCode).getDisplayName());
                    //To get the display name of the language
                    String languageName = new Locale(languageCode).getDisplayName();
                    //Sending to the LanguageCodeSeparation class and storing the Object in the ArrayList
                    NotDownloadedLanguageCodeSeparation notDownloadedLanguageCodeSeparation = new NotDownloadedLanguageCodeSeparation(languageCode, languageName);
                    notDownloadedLanguageNameAndCode.add(notDownloadedLanguageCodeSeparation);
                }
                //Setting the Spinners and Updating the Menu inside the RemoteModelManager
                invalidateOptionsMenu();
                settingSpinners();
            }
        });
    }

    //On menu Created, we set the not downloaded languages in the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
//        MenuInflater inflater = new MenuInflater(this);
//        inflater.inflate(R.menu.home_menu, menu);
        menu.add(0,0,0,"DownloadLanguages");
        int keyIndex = 1;
        for(int i = 0; i<notDownloadedLanguageNameAndCode.size(); i++){
            menu.add(0, keyIndex, 0, notDownloadedLanguageNameAndCode.get(i).languageName);
            keyIndex++;
        }
        return super.onCreateOptionsMenu(menu);
    }

    //On options selected, we get the language and download it
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() > 0) {
            String languageCode = notDownloadedLanguageCodes.get(item.getItemId() - 1);
            String languageName = new Locale(languageCode).getDisplayName();

            //To download the selected Language and to update the menu and spinner
            translatorOptions = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(languageCode)
                    .build();
            translator = Translation.getClient(translatorOptions);
//            downloadConditions = new DownloadConditions.Builder()
//                    .requireWifi()
//                    .build();
            translator.downloadModelIfNeeded()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "Downloading the Language", Toast.LENGTH_SHORT).show();
                            Log.d(activityTest, "DownloadCompleted");
                            DownloadedLanguageCodeSeparation downloadedLanguageCodeSeparation = new DownloadedLanguageCodeSeparation(languageCode, languageName);
                            downloadedLanguageNameAndCode.add(downloadedLanguageCodeSeparation);
                            notDownloadedLanguageCodes.remove(item.getItemId() - 1);
                            notDownloadedLanguageNameAndCode.remove(item.getItemId() - 1);
                            invalidateOptionsMenu();
                            settingSpinners();
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }



    void settingSpinners() {
        Log.d(activityTest, "Enter the SettingSpinners function");
        //Creating variable to get the name of the languages from the LanguageCodeSeparation class
        List<String> languageNames = new ArrayList<>();
        for(int i = 0; i < downloadedLanguageNameAndCode.size(); i++) {
            Log.d("TestLog", downloadedLanguageNameAndCode.get(i).getLanguageName());
            languageNames.add(downloadedLanguageNameAndCode.get(i).getLanguageName());
        }
        //Setting up the arrayAdapter to set in the spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languageNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromLangSpinner.setAdapter(arrayAdapter);
        toLangSpinner.setAdapter(arrayAdapter);
    }
}