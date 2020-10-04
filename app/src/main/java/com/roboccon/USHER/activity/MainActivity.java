package com.roboccon.USHER.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;


import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.roboccon.USHER.R;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    ImageView language;
    HashMap<Integer, String> utteranceHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoView) findViewById(R.id.video_view);
        language = findViewById(R.id.language);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        //Video Loop
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        Uri uri = null;
        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.louvre3);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGreetingPage();
            }
        });
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
                            goToGreetingPage();
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

    public void goToGreetingPage() {
        Intent intent = new Intent(this, GreetingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = null;
        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.louvre3);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
        generateRequest();
    }

    public void generateRequest() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //mock user greetings
                httpRequest("hello");
            }
        };
        handler.postDelayed(r, 9000);

    }

}