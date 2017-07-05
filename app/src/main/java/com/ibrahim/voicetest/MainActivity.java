package com.ibrahim.voicetest;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView speechText;
    // Used for speech to text
    FloatingActionButton fabmic;
    private final static int REQ_CODE_SPEECH_INPUT = 100;
    // Used for text to speech
    private final static int MY_DATA_CHECK_CODE = 0; // status check code
    private TextToSpeech myTTS; // TTS object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("app", "onCreate called");
        fabmic = (FloatingActionButton) findViewById(R.id.fabmic);
        speechText = (TextView) findViewById(R.id.speechText);

        // check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

    }

    /** On fab click record voice and get string result */
    public void fabClick(View view) {
        Log.d("app", "fabClick called");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.micAct)); // use getresources().getstring(R.string.id))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException ex) {
            Log.d("app", "fabClick Exception caught");
            ex.printStackTrace();
        }
    }

    /** On textview click text to speech, say what is written there */
    public void textClick(View view) {
        Log.d("app", "textClick called");
        try {
            speakWords(speechText.getText().toString());
        } catch(Exception ex) {
            speechText.setError(getResources().getString(R.string.noText));
            Log.d("app", "textClick Exception caught");
            ex.printStackTrace();
        }
    }

    /** text to speech */
    private void speakWords(String speech) {
        Log.d("app", "speakWords called");

        myTTS.speak(speech, TextToSpeech.QUEUE_ADD, null); // add speech text to queue, to speak straight away make it QUEUE_FLUSH
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("app", "onActivityResult called");

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                Log.d("app", "recorded voice");
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechText.setText(result.get(0));
                }
                break;
            }
            case MY_DATA_CHECK_CODE: {
                Log.d("app", "TTS");
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    // the user has the necessary data - create the TTS
                    myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int initStatus) {
                            // check for successful instantiation
                            if (initStatus == TextToSpeech.SUCCESS) {
                                myTTS.setLanguage(Locale.US);
                            }
                            else if (initStatus == TextToSpeech.ERROR) {
                                speechText.setError(getResources().getString(R.string.noText));
                                Log.d("app", "speechText.setError");
                            }
                        }
                    }); // error for this, this
                }
                else {
                    // no data - install it now
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                    Log.d("app", "no data, installTTSIntent");
                }
            }

        }
    }
}
