package com.example.majorchatbot;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.paralleldots.paralleldots.App;

import org.json.JSONException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkThread implements Runnable  {


    private MainActivity mainActivity;
    storedData myData = new storedData();

    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public NetworkThread(MainActivity mainActivity) {

        this.mainActivity = mainActivity;

    }

    public void addToQueue(String message) {
        queue.offer(message);
    }

    @Override
    public void run() {
        // The thread waits for any queue updates
        while (true) {
            try {
                String message = queue.take();
                if (message.equals("myData.messageCount changed") && storedData.messageCount >= 4) {
                    try {
                        getMood(storedData.paragraph);


//                        mainActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mainActivity.showMessage(storedData.BotReply);
//                            }
//                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getMood(String message) throws Exception {

        //mainActivity.showMessage(storedData.paragraph);

        String Mlurl = "https://text-emotion-classifier-api--anas16d.repl.co/api/emotion_classifier/predict?input="+message;

        RequestQueue mRequestQueue;
        StringRequest mStringRequest;

        mRequestQueue = Volley.newRequestQueue(this.mainActivity.getApplicationContext());

        mStringRequest = new StringRequest(Request.Method.GET, Mlurl, new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("moodM", response);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("moodM", "Error :" + error.toString());
            }

        }
        );

        mRequestQueue.add(mStringRequest);  // Add the StringRequest to the RequestQueue




        Log.d("getmood", "mood function called");
        App pd = new App("nd4EirrBZwb5F4ELaxbaZvCyGgHmXIyLlQFZ1mcLdgA");

        Log.d("getmood", "mood function called one line over");


        // for single sentences

        Log.d("mood", storedData.paragraph);

        String emotion = "";
        try {
            String em = pd.emotion("hello check");
            //Log.d("moodCheck", em);
            //emotion = pd.intent(storedData.paragraph);Log.d("mood", emotion);
            emotion = pd.emotion(storedData.paragraph);Log.d("mood", emotion);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //String emotions = pd.emotion("I had expectations from my new phone but it is average.");

        Log.d("mood", emotion);


        JSONParser parser = new JSONParser();


        Type mapType = new TypeToken<Map<String, Map>>(){}.getType();
        Map<String, double[]> son = new Gson().fromJson(emotion, mapType);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(emotion, JsonObject.class);
        JsonElement emotionElement = jsonObject.get("emotion");
        JsonObject emotionObject = emotionElement.getAsJsonObject();
        Map<String, Double> emotions = new HashMap<>();
        Log.d("mood", "here1");
        for (Map.Entry<String, JsonElement> entry : emotionObject.entrySet()) {
            String emot = entry.getKey();
            Double value = entry.getValue().getAsDouble();
            emotions.put(emot, value);
        }

        // Sorting the map in descending order by value
        List<Map.Entry<String, Double>> list = new ArrayList<>(emotions.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<String, Double> sortedEmotions = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedEmotions.put(entry.getKey(), entry.getValue());
        }

        String highestEmotion = null;
        Double highestEmotionValue = null;
        Map.Entry<String, Double> firstEntry = sortedEmotions.entrySet().iterator().next();
        highestEmotion = firstEntry.getKey();
        highestEmotionValue = firstEntry.getValue();


        Log.d("moodMap", "fear = " + sortedEmotions.get("Fear") );

        if(highestEmotionValue >= 0.35){

            getSong(highestEmotion);
        }
        else{

            storedData.showMessage = true;
            storedData.mood = highestEmotion;
            storedData.BotReply = "Hey you looks " + storedData.mood + "How was your day?";

            mainActivity.runOnUiThread(new Runnable() {

                            public void run() {
                                mainActivity.showMessage("Hey you looks " + storedData.mood + "How was your day?");
                            }
                        });

            //mainActivity.showMessage("Hey you looks " + highestEmotion + "How was your day?");
//            chatsModalArrayList.add(new ChatsModal("Hey you looks " + highestEmotion + "How was your day?", BOT_KEY));
//            chatRVAdapter.notifyDataSetChanged();
            //thread.stop();

            storedData.messageCount = 0;

        }


    }

    private void getSong(String mood) {

        Log.d("mood", "getSong method called");

        String url = "http://ws.audioscrobbler.com/2.0/?format=json&method=tag.gettoptracks&tag="+mood+"&api_key=6ed91ca6ed76970017e3ca97c0b3a587";


        RequestQueue mRequestQueue;
        StringRequest mStringRequest;

        mRequestQueue = Volley.newRequestQueue(this.mainActivity.getApplicationContext());

        Log.d("mood", "getSong method called line 3");

        // String Request initialized
        //**api_key**xxxxxxxx**method**auth.getMobileSession**password**xxxxxxx**username**xxxxxxxx
        // https://text-emotion-classifier-api--anas16d.repl.co/api/emotion_classifier/predict?input=i%20am%20happy

        mStringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {

            @Override

            public void onResponse(String response) {

                Log.d("mood", response);
                Log.d("mood", "getSong method called line 4");

                //org.json.JSONObject responseJSON = null;

//                try {
//                    Log.d("mood", "getSong method called line 5");
//
//                    JSONParser parser = new JSONParser();
//                    responseJSON = (org.json.JSONObject) parser.parse(response);
//                }
//
//                catch (ParseException e) {
//                    e.printStackTrace();
//                    Log.d("mood", "getSong method called line 5e");
//                }

                // convert the JSON string to a JSONObject
                //JSONObject responseJSON = new JSONObject(response);

                String trackName = "";
                try {

                    org.json.JSONObject responseJSON = new org.json.JSONObject(response);
                    Log.d("mood", "getSong method called line 6");
                    org.json.JSONObject tracks = responseJSON.getJSONObject("tracks");
                    org.json.JSONArray trackArray = tracks.getJSONArray("track");
                    int randomIndex = (int) (Math.random() * trackArray.length());
                    org.json.JSONObject randomTrack = trackArray.getJSONObject(randomIndex);
                    trackName = randomTrack.getString("name");
                    //System.out.println("Selected track name: " + trackName);
                    Log.d("mood", trackName);
                }

                catch (JSONException e) {
                    //some exception handler code.
                    Log.d("mood", e.toString());
                    Log.d("mood", "getSong method called line 6e");
                }

                Log.d("mood", "getSong method called line 7");
                storedData.trackName = trackName;
                //In this code, we first get the tracks object and the track array from the JSON object. Then, we generate a random index to select a random track from the array. We get the random track as a JSONObject, and then extract the name property using the getString method. Finally, we print the selected track name to the console.
                storedData.BotReply ="Listen the song: " + storedData.trackName;

                Log.d("moodSong", trackName);

                mainActivity.runOnUiThread(new Runnable() {

                    public void run() {
                        mainActivity.showMessage("Listen the song: " + storedData.trackName);
                    }
                });


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("song", "Error :" + error.toString());
            }


        }
        );

        Log.d("mood", "getSong method ended");

        mRequestQueue.add(mStringRequest);  // Add the StringRequest to the RequestQueue


    }


}