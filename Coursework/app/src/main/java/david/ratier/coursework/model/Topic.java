package david.ratier.coursework.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ratie on 07/03/2017.
 */

public class Topic {

    private String name;
    private List<Slide> slides;
    private List<Question> quiz;
    private List<QuizResult> quizResults;

    public Topic(){
        this.slides = new ArrayList<>();
        this.quiz = new ArrayList<>();
        this.quizResults = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addQuestions(Question ... questions){
        for (Question q: questions) {
            this.quiz.add(q);
        }
    }

    public List<Question> getRandomQuestions(int nbrQuestion){
        if(nbrQuestion > quiz.size()){
            nbrQuestion = quiz.size();
        }
        List<Integer> indexList = new ArrayList<Integer>();
        for(int i = 0; i < quiz.size(); i++){
            indexList.add(i);
        }
        List<Question> result = new ArrayList<Question>();
        Random r = new Random();
        for(int i = 0; i < nbrQuestion; i++){
            int index = r.nextInt(indexList.size());
            result.add(quiz.get(indexList.remove(index)));
        }
        return result;
    }

    public void addSlides(Slide ... slides){
        for (Slide s: slides) {
            this.slides.add(s);
        }
    }

    public List<Slide> getSlides(){
        return this.slides;
    }

    public void addQuizResult(QuizResult r){
        quizResults.add(r);
    }

    public List<QuizResult> getQuizResults(){
        return quizResults;
    }

    public float getAverageResult(){
        float result = 0;
        for(QuizResult r : quizResults){
            result += r.getPercentage();
        }
        return result / quizResults.size();
    }

}
