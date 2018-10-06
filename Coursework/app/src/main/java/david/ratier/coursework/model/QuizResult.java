package david.ratier.coursework.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import david.ratier.coursework.utils.Pair;
import david.ratier.coursework.utils.Triplet;

/**
 * Created by David on 24/03/2017.
 */

public class QuizResult {

    private List<Question> questions;
    private String date;

    public QuizResult(){
        this.questions = new ArrayList<>();
        date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    public void addAnsweredQuestion(Question question){
        if(!question.getUserAnswer().equals("")){
            questions.add(question);
        }
    }

    public List<Triplet<String, String, String>> getResults(){
        ArrayList<Triplet<String, String, String>> results = new ArrayList<>();
        for (Question q : questions) {
            Triplet<String, String, String> row = new Triplet<>(q.getQuestion(), q.getAnswer(), q.getUserAnswer());
            results.add(row);
        }
        return results;
    }

    public Pair<Float, Float> getScore() {
        List<Triplet<String, String, String>> results = getResults();
        int cpt = 0;
        for (Triplet<String, String, String> s : results) {
            if(s.getValue1().equals(s.getValue2())){
                cpt++;
            }
        }
        return new Pair<Float, Float>((float)cpt, (float)results.size());
    }

    public float getPercentage() {
        Pair<Float, Float> score = getScore();
        return (score.getValue0() / score.getValue1()) * 100;
    }

    public String getDate(){
        return date;
    }

}
