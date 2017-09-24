package com.example.timetable;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Fields
    EditText tvInputSearchText;
    ImageButton btnSearch;
    ProgressBar progressBar;
    TextView dayOfTheWeak;
    TextView textError;
    TextView restText;
    TableLayout tableLayout;


    //Functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        initViews();
        restText.setText("");
    }

    //инициализация вьюшек
    private void initViews() {
        tvInputSearchText = (EditText) findViewById(R.id.tvInputSearchText);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        dayOfTheWeak = (TextView) findViewById(R.id.tvDayOfTheWeak);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        textError = (TextView) findViewById(R.id.tvError);
        restText = (TextView) findViewById(R.id.tvRestText);
    }

    //обработка нажатия кнопки
    @Override
    public void onClick(View v) {
        DownloadTimeTableThread dttt = new DownloadTimeTableThread(this);
        dttt.execute("http://91.232.173.137:8083/izmenenie/izmenenie.html", tvInputSearchText.getText().toString());
    }

    //проверка подключения к интернету
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) return true;
        return false;
    }
}
