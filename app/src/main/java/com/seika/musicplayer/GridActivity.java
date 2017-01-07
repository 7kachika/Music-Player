package com.seika.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Chou Seika on 1/8/2017.
 */

public class GridActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> musicList;
    private ArrayAdapter<String> listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        Intent intent = getIntent();
        musicList = intent.getStringArrayListExtra("musicList");

        listView = (ListView)findViewById(R.id.list_view);
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, musicList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra("indexOrder", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
