package com.example.timetable;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadTimeTableThread extends AsyncTask<String, Object, List<Teacher>> {
    //Fields
    String dayOfTheWeak;
    String error;
    MainActivity mainActivity;


    //Constructor
    public DownloadTimeTableThread(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        error = "";
        dayOfTheWeak = "";
    }


    //Functions
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mainActivity.progressBar.setVisibility(ProgressBar.VISIBLE);
        mainActivity.textError.setText(error);
        mainActivity.scrollHor.scrollTo(0, 0);
        mainActivity.scrollVer.scrollTo(0, 0);
    }

    @Override
    protected List<Teacher> doInBackground(String... uri) {
        Document doc;
        if (uri[1].isEmpty() || uri[1] == null) return null;

        if (!mainActivity.isOnline()) {
            error = "Отсутствует подключение к интернету!";
            return null;
        }

        try {
            doc = Jsoup.connect(uri[0]).get();

            dayOfTheWeak = doc.select("tr").get(1).text().split(" ")[0];//день недели
//            Log.d("myTags", doc.select("tr").get(1).text().split(" ")[0]);//день недели
            return parsingTable(doc, uri[1]);

        } catch (IOException e) {
//            error = "Отсутствует подключение к интернету или неверный URI!";
            error = "Неверный URI!";
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Teacher> teachers) {
        super.onPostExecute(teachers);
        mainActivity.dayOfTheWeak.setText("(" + dayOfTheWeak + ")");
        mainActivity.progressBar.setVisibility(ProgressBar.GONE);
        mainActivity.textError.setText(error);
        error = "";

        fillData(teachers);
    }

    private void fillData(List<Teacher> list) {
        mainActivity.tableLayout.removeAllViews();

        if (list == null) {
            return;
        }
        if (list.isEmpty()) {
            mainActivity.restText.setText("По ходу выходной");
        } else mainActivity.restText.setText("");

        int count = 0;

        for (Teacher t : list) {
            fillTable(t, count);
            count++;

            Log.d("myTags", String.valueOf(t.getLessonNumber()));
            Log.d("myTags", String.valueOf(t.getTimeLesson()));
            Log.d("myTags", String.valueOf(t.getSerNameTeacher()));
            Log.d("myTags", String.valueOf(t.getCabinetNumber()));
            Log.d("myTags", String.valueOf(t.getLesson()));
            Log.d("myTags", "");
        }
    }

    //заполнение TableRow данными
    private void fillTable(Teacher t, int count) {
        TableRow tr = new TableRow(mainActivity);

        if (count % 2 == 0)
            tr.setBackgroundResource(R.color.colorWhite);//установка фона на четные ряды

        createTextVievAndFillData(tr, t.getLessonNumber() + " урок");
        createTextVievAndFillData(tr, t.getTimeLesson());
        createTextVievAndFillData(tr, t.getSerNameTeacher());
        createTextVievAndFillData(tr, t.getCabinetNumber());
        createTextVievAndFillData(tr, t.getLesson());

        mainActivity.tableLayout.addView(tr);
    }

    private void createTextVievAndFillData(TableRow tr, String s) {
        TextView tv = new TextView(mainActivity);
        tv.setTextSize(20);

        if (s.contains(mainActivity.tvInputSearchText.getText())) {
            tv.setTextColor(Color.BLACK);
        }

        tv.setText("  " + s + "  ");
        tr.addView(tv);
    }

    //распарсивание аштиэмэльки
    private List<Teacher> parsingTable(Document doc, String inputText) {
        List<Teacher> listTeacher = new ArrayList<>();
        Teacher teacher;

        for (Element element1 : doc.select("tr")) {
            for (Element element2 : element1.getElementsMatchingText("\\d \\d\\d:\\d\\d - .* " + inputText + ".*")) {
                teacher = new Teacher();

                //время и номер урока
                for (Element elementTimeNumber : element2.select(".T2")) {

                    if (elementTimeNumber.text().matches("\\d\\d:\\d\\d - \\d\\d:\\d\\d")) {
//                            Log.d("myTags", elementTimeNumber.text() + " время");//время урока
                        teacher.setTimeLesson(elementTimeNumber.text());//время урока
                        continue;
                    }

                    try {
                        Integer.parseInt(elementTimeNumber.text());
                    } catch (Exception e1) {
                        continue;
                    }
//                        Log.d("myTags", elementTimeNumber.text() + " урок");//номер урока
                    teacher.setLessonNumber(elementTimeNumber.text());//номер урока
                }

                for (Element elementOfClass : element2.select(".T1")) {
                    for (Element elementTROfClass : elementOfClass.select("tr")) {

                        if (elementTROfClass.text().contains(inputText)) {

                            try {
                                String[] names = elementTROfClass.text().split("/");
                                if (names[0].contains(inputText)) {
                                    String[] nameSubject = names[0].split(" ");
                                    if (nameSubject.length > 3) {
//                                            Log.d("myTags", nameSubject[0] + " " + nameSubject[1]);//предмет
                                        teacher.setLesson(nameSubject[0] + " " + nameSubject[1]);//предмет
//                                            Log.d("myTags", nameSubject[2].trim() + " " + nameSubject[3]);//фамилия
                                        teacher.setSerNameTeacher(nameSubject[2].trim() + " " + nameSubject[3]);//фамилия
//                                            Log.d("myTags", elementTROfClass.siblingElements().text().split(" ")[0].trim());//номер кабинета
                                        teacher.setCabinetNumber(elementTROfClass.siblingElements().text().split(" ")[0].trim());//номер кабинета
                                    } else {
//                                            Log.d("myTags", nameSubject[0]);//предмет
                                        teacher.setLesson(nameSubject[0]);//предмет
//                                            Log.d("myTags", nameSubject[1].trim() + " " + nameSubject[2]);//фамилия
                                        teacher.setSerNameTeacher(nameSubject[1].trim() + " " + nameSubject[2]);//фамилия
//                                            Log.d("myTags", elementTROfClass.siblingElements().text().split(" ")[0].trim());//номер кабинета
                                        teacher.setCabinetNumber(elementTROfClass.siblingElements().text().split(" ")[0].trim());//номер кабинета
                                    }
                                } else {
//                                        Log.d("myTags", names[0].split(" ")[0]);//предмет
                                    teacher.setLesson(names[0].split(" ")[0]);//предмет
//                                        Log.d("myTags", names[1].trim());//фамилия
                                    teacher.setSerNameTeacher(names[1].trim());//фамилия
//                                        Log.d("myTags", elementTROfClass.siblingElements().text().split(" ")[1].trim());//номер кабинета
                                    teacher.setCabinetNumber(elementTROfClass.siblingElements().text().split(" ")[1].trim());//номер кабинета
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }
                }
                listTeacher.add(teacher);
            }
        }
        return listTeacher;
    }
}
