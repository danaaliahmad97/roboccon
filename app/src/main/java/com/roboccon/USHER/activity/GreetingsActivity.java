package com.roboccon.USHER.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.roboccon.USHER.R;

import java.util.HashMap;
import java.util.Locale;

public class GreetingsActivity extends AppCompatActivity implements TextToSpeech.OnUtteranceCompletedListener {
    private final String UTTER_ID = "utterance";

    private TextToSpeech textToSpeech;
    private LottieAnimationView lottieAnimationView;
    HashMap<String, String> ttsOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        lottieAnimationView = findViewById(R.id.animationView);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsOptions = new HashMap<String, String>();
                ttsOptions.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTER_ID);

                if (status == TextToSpeech.SUCCESS) {
                    lottieAnimationView.playAnimation();
                    lottieAnimationView.loop(true);
                    int ttsLang = textToSpeech.setLanguage(Locale.US);
                    textToSpeech.speak("Welcome to the louvre museum , I will be your usher for today.", TextToSpeech.QUEUE_FLUSH, ttsOptions);
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textToSpeech.setOnUtteranceCompletedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        if (utteranceId.equalsIgnoreCase(UTTER_ID)) {
            lottieAnimationView.loop(false);
        }
    }
}