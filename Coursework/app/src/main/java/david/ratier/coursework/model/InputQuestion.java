package david.ratier.coursework.model;

/**
 * Created by David on 03/05/2017.
 */

public class InputQuestion extends Question{

    public InputQuestion(String question, String answer) {
        super(question, answer.toLowerCase());
    }

    @Override
    public String getUserAnswer() {
        return super.getUserAnswer().toLowerCase();
    }

}
