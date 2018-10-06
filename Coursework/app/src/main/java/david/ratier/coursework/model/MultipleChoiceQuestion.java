package david.ratier.coursework.model;

import java.util.List;

/**
 * Created by David on 29/04/2017.
 */

public class MultipleChoiceQuestion extends Question {

    private List<String> propositions;

    public MultipleChoiceQuestion(String question, String answer, List<String> propositions) {
        super(question, answer);
        this.propositions = propositions;
    }

    public List<String> getPropositions(){
        return propositions;
    }

}
