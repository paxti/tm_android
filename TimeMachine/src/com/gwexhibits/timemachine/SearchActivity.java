package com.gwexhibits.timemachine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;

import com.quinny898.library.persistentsearch.SearchBox;

public class SearchActivity extends AppCompatActivity {

    Boolean isSearch;
    private SearchBox search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search = (SearchBox) findViewById(R.id.searchbox);
        search.enableVoiceRecognition(this);
        search.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
}
