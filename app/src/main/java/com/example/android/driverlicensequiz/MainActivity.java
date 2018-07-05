package com.example.android.driverlicensequiz;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    public static String[] arTypeQuestions = {"radio", "radio", "edit", "check"};
    public static int[] arMarkQuestions = {0, 0, 0, 0};
    public static int[] arScoreAnswers = {0, 0, 0, 0};
    int nQuestion;
    LinearLayout myLayoutPrimary;

    String[][] arAnswers = new String[4][1];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myLayoutPrimary = findViewById(R.id.LinearLayoutPrimary);

        //Get the array string with the answers.
        Resources res = getResources();
        for (int r = 0; r < arAnswers.length; r++) {
            int arrayID = res.getIdentifier("answer" + (r + 1), "array", getPackageName());
            for (int c = 0; c < arAnswers[r].length; c++) {
                arAnswers[r] = res.getStringArray(arrayID);//your value
            }
        }
    }

    /*
    *  Grading Button function
    */
    public void submitResults(View View) {
        // Check if the field of name is fill. Otherwise show toast message.
        EditText mEdit = findViewById(R.id.name_field);
        String nameUser = mEdit.getText().toString();
        if (nameUser.equals("")) {
            String msgString = getString(R.string.toast_messageNoName);
            Toast.makeText(getApplicationContext(), msgString, Toast.LENGTH_LONG).show();
            return;
        }

        // Check every type field of answers.
        for (int i = 0; i < arTypeQuestions.length; i++) {
            switch (arTypeQuestions[i]) {
                case "radio":
                    checkRadioButtonAnswer(i);
                    break;
                case "edit":
                    checkEditAnswer(i);
                    break;
                default:
                    checkCheckboxAnswer(i);
                    break;
            }
        }

        // Now show results in custom toast.
        showToastResults(View, nameUser);
    }

    /*
    *  Show the custom toast with results.
    */
    public void showToastResults(View view, String name) {
        final View v = view;
        int totalQuestionAnswered = 0;
        int totalRightAnswered = 0;

        // Before show toast set background button and disable all child of parent linearLayout.
        v.getBackground().setAlpha(128);
        disableViewChild(myLayoutPrimary, false);

        // Sum Total question answered and prepare the string message
        for (int x : arMarkQuestions) {
            totalQuestionAnswered += x;
        }
        int checkString = getResources().getIdentifier("toast_messageQuestions", "string", getPackageName());
        String msgTotalQuestions = getString(checkString, String.valueOf(totalQuestionAnswered), arMarkQuestions.length);

        // Sum Total right answer and prepare the string message
        for (int x : arScoreAnswers) {
            totalRightAnswered += x;
        }
        //Set how many percentage it is.
        double percentageScore = ((double) totalRightAnswered / arMarkQuestions.length) * 100;
        int c = (int) ((percentageScore) + 0.5f);
        checkString = getResources().getIdentifier("toast_rightAnswers", "string", getPackageName());
        String msgRightAnswers = getString(checkString, String.valueOf(totalRightAnswered), c + "%");

        //Set a custom message for user.
        if (c <= 25) {
            checkString = getResources().getIdentifier("toast_message1", "string", getPackageName());
        } else if (c >= 75) {
            checkString = getResources().getIdentifier("toast_message3", "string", getPackageName());
        } else {
            checkString = getResources().getIdentifier("toast_message2", "string", getPackageName());
        }
        String msgToUser = getString(checkString, name);

        /* Start build custom toast */
        // Set parent LinearLayout
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        layout.setBackgroundResource(R.color.colorAccent);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set TextView header
        TextView viewHeader = new TextView(this);
        //TextView viewHeader=new TextView(this, null, 0, R.style.ToastHeader);
        viewHeader.setTextColor(Color.WHITE);
        viewHeader.setTextSize(36);
        viewHeader.setBackgroundResource(R.color.colorPrimary);
        viewHeader.setGravity(Gravity.CENTER);
        viewHeader.setText(R.string.results_head);
        layout.addView(viewHeader, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // Set TextView show Total Questions
        TextView viewTotalQuestions = new TextView(this);
        viewTotalQuestions.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        viewTotalQuestions.setTextSize(24);
        viewTotalQuestions.setGravity(Gravity.CENTER);
        viewTotalQuestions.setText(msgTotalQuestions);
        layout.addView(viewTotalQuestions);

        // Set TextView show Right Answers
        TextView viewRightAnswers = new TextView(this);
        viewRightAnswers.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        viewRightAnswers.setTextSize(24);
        viewRightAnswers.setGravity(Gravity.CENTER);
        viewRightAnswers.setText(msgRightAnswers);
        layout.addView(viewRightAnswers);

        // Set TextView show Message to User
        TextView viewMessageToUser = new TextView(this);
        viewMessageToUser.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        viewMessageToUser.setTextSize(24);
        viewMessageToUser.setGravity(Gravity.CENTER);
        viewMessageToUser.setText(Html.fromHtml(msgToUser));
        viewMessageToUser.setPadding(0, 0, 0, 20);
        layout.addView(viewMessageToUser);

        // Set Toast and Show
        Toast toast = new Toast(this);
        toast.setView(layout);
        toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

        // Start Countdown similar to timer of toast. In the end, it sets everything as before.
        new CountDownTimer(3800, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                v.getBackground().setAlpha(255);
                disableViewChild(myLayoutPrimary, true);
            }
        }.start();
    }

    /*
    *  Check the Edit field answers
    */
    public void checkEditAnswer(int nQ) {
        nQuestion = nQ;
        arMarkQuestions[nQuestion] = 0;
        String idCheck = "Question_" + String.valueOf(nQuestion + 1) + "_answer_" + String.valueOf(arAnswers[nQuestion].length) + "_E";
        int resID = getResources().getIdentifier(idCheck, "id", getPackageName());
        EditText mEdit = findViewById(resID);
        String sEdit = mEdit.getText().toString();
        sEdit = sEdit.trim().toLowerCase();

        if (!sEdit.matches("")) {
            arMarkQuestions[nQuestion] = 1;
        }
        if (sEdit.equals(arAnswers[nQuestion][0].toLowerCase())) {
            arScoreAnswers[nQuestion] = 1;
        } else {
            arScoreAnswers[nQuestion] = 0;
        }
    }

    /*
    *  Check the CheckBox field answers
    */
    public void checkCheckboxAnswer(int nQ) {
        nQuestion = nQ;
        arMarkQuestions[nQuestion] = 0;
        boolean[] checkAnswer = new boolean[nQuestion];
        boolean[] arrayAnswer = new boolean[nQuestion];

        for (int i = 0; i < arAnswers[nQuestion].length; i++) {
            String idCheck = "Question_" + String.valueOf(nQuestion + 1) + "_answer_" + String.valueOf(i + 1) + "_C";
            int resID = getResources().getIdentifier(idCheck, "id", getPackageName());
            CheckBox elemCheck = findViewById(resID);
            checkAnswer[i] = elemCheck.isChecked();
            if (checkAnswer[i]) {
                arMarkQuestions[nQuestion] = 1;
            }
            arrayAnswer[i] = Boolean.parseBoolean(arAnswers[nQuestion][i]);
        }
        boolean matchIsPerfect = Arrays.equals(checkAnswer, arrayAnswer);
        if (matchIsPerfect) {
            arScoreAnswers[nQuestion] = 1;
        } else {
            arScoreAnswers[nQuestion] = 0;
        }
    }

    /*
    *  Check the RadioButton field answers
    */
    public void checkRadioButtonAnswer(int nQ) {
        nQuestion = nQ;
        arMarkQuestions[nQuestion] = 0;

        for (int i = 0; i < arAnswers[nQuestion].length; i++) {
            String idCheck = "Question_" + String.valueOf(nQuestion + 1) + "_answer_" + String.valueOf(i + 1) + "_R";
            int resID = getResources().getIdentifier(idCheck, "id", getPackageName());
            RadioButton elemCheck = findViewById(resID);
            Boolean boolCheck = Boolean.parseBoolean(arAnswers[nQuestion][i]);
            if (elemCheck.isChecked()) {
                arMarkQuestions[nQuestion] = 1;
            }
            if (elemCheck.isChecked() && boolCheck) {
                arScoreAnswers[nQuestion] = 1;
                return;
            } else {
                arScoreAnswers[nQuestion] = 0;
            }
        }
    }

    /*
    *  Disable all field
    */
    public void disableViewChild(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                disableViewChild(child, enabled);
            }
        }
    }


}
