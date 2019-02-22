package com.example.proyectofincurso;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.proyectofincurso.R;

import org.w3c.dom.Text;

public class GalleryActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showGallery();





    }

    private void showGallery() {
        String name = getIntent().getExtras().getString("name");

        TextView textViewG = (TextView)findViewById(R.id.textViewG);
        textViewG.setText(name);
    }

}
