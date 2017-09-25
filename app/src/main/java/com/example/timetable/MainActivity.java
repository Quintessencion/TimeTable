package com.example.timetable;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Fields
    EditText tvInputSearchText;
    private ImageButton btnSearch;
    ProgressBar progressBar;
    TextView dayOfTheWeak;
    TextView textError;
    TextView restText;
    TableLayout tableLayout;
    HorizontalScrollView scrollHor;
    ScrollView scrollVer;

    private String URI = "http://91.232.173.137:8083/izmenenie/izmenenie.html";
    private String error;
    private DownloadTimeTableThread dttt;
    private MyConnect mc;
    private Document doc;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;


    //Functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        initViews();
        restText.setText("");
        error = "";

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        mc = new MyConnect();
        mc.execute(URI);
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
        scrollHor = (HorizontalScrollView) findViewById(R.id.scrollHor);
        scrollVer = (ScrollView) findViewById(R.id.scrollVer);
    }

    //обработка нажатия кнопки
    @Override
    public void onClick(View v) {
        if (doc == null) return;
        //поиск введенного текста в doc
        dttt = new DownloadTimeTableThread(this);
        dttt.execute(tvInputSearchText.getText().toString(), doc);
    }

    //проверка подключения к интернету
    public boolean isOnline() {
        return (netInfo != null && netInfo.isConnectedOrConnecting()) ? true : false;
    }


    //inner class connect
    class MyConnect extends AsyncTask<String, Void, Document> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textError.setText(error);
        }

        @Override
        protected Document doInBackground(String... params) {
            if (!isOnline()) {
                error = "Отсутствует подключение к интернету!";
                return null;
            }

            try {
                return Jsoup.connect(params[0]).get();
            } catch (IOException e) {
                error = "Неверный URI!";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            textError.setText(error);
            error = "";
            doc = document;
        }
    }
}
