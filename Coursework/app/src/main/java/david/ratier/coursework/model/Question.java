package david.ratier.coursework.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by ratie on 28/02/2017.
 */

public abstract class Question {

    private String base64Picture;
    private String question;
    private String answer;
    private String userAnswer;

    public Question(String question, String answer){
        this.question = question;
        this.answer = answer;
        userAnswer = "";
        base64Picture = "";
    }

    public void addBase64Picture(String base64Picture){
        this.base64Picture = base64Picture;
    }

    public String getBase64Picture(){
        return base64Picture;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

}
