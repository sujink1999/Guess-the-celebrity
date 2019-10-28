package com.sujin.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button cel1, cel2, cel3, cel4;
    ImageView image;
    String result;
    ArrayList<String> names;
    ArrayList<String> links;
    ArrayList<String> namesinbutton;
    int key;


    public void choose(View view)
    {
        Log.i("key",Integer.toString(key));
        Log.i("integer",view.getTag().toString());
        if(key == Integer.parseInt(view.getTag().toString()))
        {
            Toast.makeText(this, "Correct!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Wrong!!", Toast.LENGTH_SHORT).show();
        }
        generate();
    }

    public class GetImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream(in);
                return mybitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    public void generate()
    {
        Random rand = new Random();
        int celebrityno = rand.nextInt(81);
        key = rand.nextInt(4);
        GetImage getImage = new GetImage();
        namesinbutton = new ArrayList<String>();
        Bitmap map;
        try {
            map = getImage.execute(links.get(celebrityno)).get();
            image.setImageBitmap(map);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for( int i=0;i<4; i++)
        {
            int guess = rand.nextInt(81);
            if(key == i)
            {
                namesinbutton.add(names.get(celebrityno));
            }
            else if(guess == celebrityno)
            {
                i--;
            }
            else{

                namesinbutton.add(names.get(guess));
            }
        }
        cel1.setText(namesinbutton.get(0));
        cel2.setText(namesinbutton.get(1));
        cel3.setText(namesinbutton.get(2));
        cel4.setText(namesinbutton.get(3));
        Log.i("key",Integer.toString(key));


    }

    public class GetString extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            try {
                String result = "";
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1)
                {
                    char character = (char) data;
                    if (character == '"')
                    {

                    }
                    result+=character;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cel1 = (Button) findViewById(R.id.button);
        cel2 = (Button) findViewById(R.id.button2);
        cel3 = (Button) findViewById(R.id.button3);
        cel4 = (Button) findViewById(R.id.button4);
        image = (ImageView) findViewById(R.id.imageView);



        result = null;
        GetString get = new GetString();
        try {
            result = get.execute("http://www.posh24.se/kandisar").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Pattern pattern = Pattern.compile("img src=\"(.*?)\" alt");
        Matcher m = pattern.matcher(result);
        links = new ArrayList<String>();


        while(m.find())
        {
            Log.i("Link",m.group(1));
            links.add(m.group(1));
        }

        Log.i("links ",Integer.toString(links.size()));

        Pattern pattern2 = Pattern.compile(" alt=\"(.*?)\"/>");
        Matcher m2 = pattern2.matcher(result);
        names = new ArrayList<String>();


        while(m2.find())
        {
            Log.i("Name",m2.group(1));
            names.add(m2.group(1));
        }
        Log.i("names ",Integer.toString(names.size()));
        generate();
    }
}