package com.example.majorchatbot;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.paralleldots.paralleldots.App;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
//import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.paralleldots.paralleldots.App;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//import org.json.JSONObject;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.StringWriter;
import java.io.PrintWriter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MainActivity extends AppCompatActivity {

    private RecyclerView chatsRV;
    private EditText userMsgEdt;
    private FloatingActionButton sendMsgFAB;

    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";

    private ArrayList<ChatsModal> chatsModalArrayList;

    private ChatRVAdapter chatRVAdapter;

    storedData myData = new storedData();

    boolean isThreadRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//
//        StrictMode.setThreadPolicy(policy);



        chatsRV = findViewById(R.id.idRVChats);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        sendMsgFAB = findViewById(R.id.idFABSend);

        chatsModalArrayList = new ArrayList<>();

        chatRVAdapter = new ChatRVAdapter(chatsModalArrayList, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(chatRVAdapter);


//        try {
//            getMood("i am lonely.");
//        } catch (Exception e) {
//            Log.d("mood", "failed mood getting");
//
//
//
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//            String sStackTrace = sw.toString(); // stack trace as a string
//            Log.d("mood", sStackTrace);
//            e.printStackTrace();
//        }

        NetworkThread myThread = new NetworkThread(this);
        Thread thread = new Thread(myThread);
        thread.start();






        sendMsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userMsgEdt.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                    return;
                }

                storedData.paragraph += userMsgEdt.getText().toString() + ". ";
                storedData.messageCount++;

                //if(storedData.messageCount >= 3 ){
                    //thread.start();

                myThread.addToQueue("myData.messageCount changed");
//                    try {
//
//
//                        getMood(userMsgEdt.getText().toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    //isThreadRunning = true;

               // }
                //thread.getClass().getMethod("addToQueue").invoke("myData.messageCount changed");



                getResponse(userMsgEdt.getText().toString());
                userMsgEdt.setText("");

            }
        });
    }

    public void showMessage(String message) {
        Log.d("mood", "showMessage called, message is :" + storedData.BotReply);

        chatsModalArrayList.add(new ChatsModal(storedData.BotReply, BOT_KEY));
        chatRVAdapter.notifyDataSetChanged();
    }

    private void getResponse(String message) {

        Log.d("mood", "response method called");

        chatsModalArrayList.add(new ChatsModal(message, USER_KEY));
        chatRVAdapter.notifyDataSetChanged();

        String url = "http://api.brainshop.ai/get?bid=174793&key=v1vXnmui42aLf6jh&uid=uid&msg=" + message;
        String BASE_URL = "http://api.brainshop.ai";

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        Call<MsgModal> call = retrofitAPI.getMessage(url);

        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if (response.isSuccessful()) {
                    MsgModal modal = response.body();
                    Log.d("mood", response.body().getCnt());
                    chatsModalArrayList.add(new ChatsModal(modal.getCnt(), BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {

                chatsModalArrayList.add(new ChatsModal("Sorry! I am unable to get that.", BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();

            }
        });
        Log.d("mood", "response method terminated");



//        OkHttpClient client = new OkHttpClient();
//
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url("https://paphus-botlibre.p.rapidapi.com/form-get-all-instances?token=123456&sort=dailyConnects&password=password&user=myuserid&application=myapp&tag=fun")
//                .get()
//                .addHeader("content-type", "application/octet-stream")
//                .addHeader("X-RapidAPI-Key", "9f3d0fccb1msh3c2410b5bc67557p12c3d2jsn2f077c38921a")
//                .addHeader("X-RapidAPI-Host", "paphus-botlibre.p.rapidapi.com")
//                .build();
//
//        try {
//            okhttp3.Response response = client.newCall(request).execute();
//            Log.d("botapi", response.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    private void getMood(String message) throws Exception {


        Log.d("getmood", "mood function called");
        App pd = new App("nd4EirrBZwb5F4ELaxbaZvCyGgHmXIyLlQFZ1mcLdgA");

        Log.d("getmood", "mood function called one line over");


        // for single sentences

        Log.d("mood", storedData.paragraph);

        String emotion = "";
        try {
            String em = pd.emotion("hello check");
            Log.d("moodCheck", em);
            emotion = pd.intent(storedData.paragraph);Log.d("mood", emotion);
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

        if(highestEmotionValue >= 0.4){

            getSong(highestEmotion);
        }
        else{
            chatsModalArrayList.add(new ChatsModal("Hey you looks " + highestEmotion + "How was your day?", BOT_KEY));
            chatRVAdapter.notifyDataSetChanged();
            //thread.stop();

            myData.messageCount = 0;

        }


    }



    private void getSong(String mood) {

        Log.d("mood", "getSong method called");

        String url = "http://ws.audioscrobbler.com/2.0/?format=json&method=tag.gettoptracks&tag="+mood+"&api_key=6ed91ca6ed76970017e3ca97c0b3a587";


        RequestQueue mRequestQueue;
        StringRequest mStringRequest;

        mRequestQueue = Volley.newRequestQueue(this);

        // String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("song", response);

                org.json.JSONObject responseJSON = null;

                try {


                    JSONParser parser = new JSONParser();
                    responseJSON = (org.json.JSONObject) parser.parse(response);
                }

                 catch (ParseException e) {
                    e.printStackTrace();
                }

                // convert the JSON string to a JSONObject
                //JSONObject responseJSON = new JSONObject(response);

                String trackName = "";
                try {


                    org.json.JSONObject tracks = responseJSON.getJSONObject("tracks");
                    org.json.JSONArray trackArray = tracks.getJSONArray("track");
                    int randomIndex = (int) (Math.random() * trackArray.length());
                    org.json.JSONObject randomTrack = trackArray.getJSONObject(randomIndex);
                    trackName = randomTrack.getString("name");
                    //System.out.println("Selected track name: " + trackName);
                }

                catch (JSONException e) {
                    //some exception handler code.
                }

                //In this code, we first get the tracks object and the track array from the JSON object. Then, we generate a random index to select a random track from the array. We get the random track as a JSONObject, and then extract the name property using the getString method. Finally, we print the selected track name to the console.


                chatsModalArrayList.add(new ChatsModal("Listen the song:." + trackName, BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
                //Toast.makeText(getApplicationContext(), "Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("song", "Error :" + error.toString());
            }

            //Log.d("songs", )
        }
        );

        Log.d("mood", "response method ended");


    }


    Thread thread = new Thread(new Runnable() {

        private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        public void addToQueue(String message) {
            queue.offer(message);
        }

        @Override
        public void run() {


            while (true) {
                try {
                    String message = queue.take();
                    if (message.equals("myData.messageCount changed") && myData.messageCount >= 4) {
                        try {
                            getMood(myData.paragraph);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }




                //Your code goes here

        }
    });


}