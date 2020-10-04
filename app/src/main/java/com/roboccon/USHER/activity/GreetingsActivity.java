package com.roboccon.USHER.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.roboccon.USHER.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import javax.net.ssl.SSLContext;

public class GreetingsActivity extends AppCompatActivity implements TextToSpeech.OnUtteranceCompletedListener {
    private final String UTTER_ID = "utterance";

    private TextToSpeech textToSpeech;
    private LottieAnimationView lottieAnimationView;
    HashMap<String, String> ttsOptions;
    boolean firstTime = false;
    String lastCall = "";
    HashMap<Integer, String> utteranceHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);
        lottieAnimationView = findViewById(R.id.animationView);

        utteranceHashMap.put(0, "When is the roman art exhibit");
        utteranceHashMap.put(1, "Where is the roman art exhibit");
        utteranceHashMap.put(3, "Can you repeat that?");
        utteranceHashMap.put(4, "hello");

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsOptions = new HashMap<String, String>();
                ttsOptions.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTER_ID);
                firstTime = true;

                if (status == TextToSpeech.SUCCESS) {
                    lottieAnimationView.playAnimation();
                    lottieAnimationView.loop(true);
                    int ttsLang = textToSpeech.setLanguage(Locale.US);
                    lastCall = "Welcome to the louvre museum , I will be your usher for today.";
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

    public void httpRequest(String utterance) {
        final HttpClient httpclient;
        final String[] entityString = {""};
        SSLContext sslContext = SSLContexts.createSystemDefault();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
        httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(null)
                .setSSLSocketFactory(sslsf)
                .build();
        try {
            String AppId = "59d4b94b-89dd-4f5c-9909-b5303548b912";
            String Key = "fd34cf08f6f545ca94aebcec04ba5919";
            String Endpoint = "https://usher.cognitiveservices.azure.com/";
            String Utterance = utterance;
            URIBuilder endpointURLbuilder = new URIBuilder(Endpoint + "luis/prediction/v3.0/apps/" + AppId + "/slots/production/predict?");
            endpointURLbuilder.setParameter("query", Utterance);
            endpointURLbuilder.setParameter("subscription-key", Key);
            endpointURLbuilder.setParameter("show-all-intents", "false");
            endpointURLbuilder.setParameter("verbose", "true");
            URI endpointURL = endpointURLbuilder.build();
            final HttpGet request = new HttpGet(endpointURL);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    HttpResponse response = null;
                    try {
                        response = httpclient.execute(request);
                    } catch (IOException e) {
                        System.out.println("error : " + e.getMessage());
                        e.printStackTrace();
                    }
                    // Get the response.
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        try {
                            entityString[0] = EntityUtils.toString(entity);
                            System.out.println(entityString[0]);

                            try {
                                JSONObject jsonObject = new JSONObject(entityString[0]);
                                JSONObject prediction = (JSONObject) jsonObject.get("prediction");
                                String intent = prediction.optString("topIntent");
                                if (intent.equalsIgnoreCase("findlocation")) {
                                    JSONObject entities = prediction.optJSONObject("entities");
                                    if (entities.optJSONArray("museum_events") != null) {
                                        nextPage("museum");
                                    }
                                } else if (intent.equalsIgnoreCase("find time")) {
                                    nextPage("time");
                                } else if (intent.equalsIgnoreCase("utilities.repeat")
                                        || intent.equalsIgnoreCase("utilities.confirm")) {
                                    nextPage("repeat");
                                }
                            } catch (JSONException err) {
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                }
            }.execute();
        }
        // Display errors if they occur.
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        if (utteranceId.equalsIgnoreCase(UTTER_ID)) {
            lottieAnimationView.loop(false);
            if (firstTime) {
                firstTime = false;
                Random random = new Random();
                final int index = random.nextInt(5);
                Looper.prepare();
                Handler handler = new Handler(Looper.getMainLooper());
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        //mock user interaction when asking a random question
                        httpRequest(utteranceHashMap.get(index));
                    }
                };
                handler.postDelayed(r, 3000);
            }
        }

    }

    public void nextPage(final String event) {
        Looper.prepare();
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                lottieAnimationView.playAnimation();
                lottieAnimationView.loop(true);
                switch (event) {
                    case "museum":
                        lastCall = "The Greek Antiques are in room 345. Please take  your left and then the stairs to reach it.";
                        textToSpeech.speak("The Greek Antiques are in room 345. Please take  your left and then the stairs to reach it.", TextToSpeech.QUEUE_FLUSH, ttsOptions);
                        Intent intent = new Intent(GreetingsActivity.this, MapActivity.class);
                        startActivity(intent);
                        break;
                    case "time":
                        lastCall = "The Roman exhibition will be on the fifth of october at 6 pm.";
                        textToSpeech.speak("The Roman exhibition will be on the fifth of october at 6 pm.", TextToSpeech.QUEUE_FLUSH, ttsOptions);
                        break;
                    case "repeat":
                        textToSpeech.speak(lastCall, TextToSpeech.QUEUE_FLUSH, ttsOptions);
                        break;
                }
            }
        };
        handler.postDelayed(r, 0);
    }

}