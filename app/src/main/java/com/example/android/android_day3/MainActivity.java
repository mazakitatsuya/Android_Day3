package com.example.android.android_day3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ListView mListview;
    private ArrayList<JsonObject> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListview = (ListView) findViewById(R.id.List);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JsonObject jo = mList.get(position);
                String url = jo.get("url").getAsString();

                if (!url.endsWith(".jpg")) {
                    url = url + ".jpg";
                }

                //ログを表示する
                Log.e("test", url);


                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);

            }
        });

        Ion.with(this)
                .load("http://www.reddit.com/r/cats/.json")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray array = result.get("data").getAsJsonObject()
                                .get("children").getAsJsonArray();
                        final int size = array.size();
                        mList = new ArrayList<JsonObject>(size);
                        for (int i = 0; i < size; i++) {
                            mList.add(array.get(i).getAsJsonObject().get("data").getAsJsonObject());
                        }
                        MyAdapter adapter = new MyAdapter();
                        mListview.setAdapter(adapter);

                    }
                });

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


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return mList.size();
        }

        @Override
        public JsonObject getItem(int position) {

            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.list_item, parent, false);

            TextView tv = (TextView) view.findViewById(R.id.textView);

            JsonObject jo = getItem(position);
            tv.setText(jo.get("title").getAsString());

            final ImageView iv = (ImageView) view.findViewById(R.id.imageView);

            Ion.with(iv)
                    .load(jo.get("thumbnail").getAsString());
            return view;
        }
    }





}
