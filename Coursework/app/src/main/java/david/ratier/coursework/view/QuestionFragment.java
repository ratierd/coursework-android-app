package david.ratier.coursework.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import david.ratier.coursework.R;
import david.ratier.coursework.model.MultipleChoiceQuestion;
import david.ratier.coursework.model.Question;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by David on 22/03/2017.
 */

public class QuestionFragment extends Fragment {

    private static final String QUESTION_KEY = "question";
    private static final String LAST_KEY = "last";

    public static QuestionFragment newInstance(Question question, boolean lastQuestion) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_KEY, MainActivity.getGson().toJson(question, Question.class));
        args.putBoolean(LAST_KEY, lastQuestion);
        fragment.setArguments(args);
        return fragment;
    }

    private Question question;
    private boolean isLast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(question == null){
            question = MainActivity.getGson().fromJson(getArguments().getString(QUESTION_KEY), Question.class);
        }
        isLast = getArguments().getBoolean(LAST_KEY);
        super.onCreate(savedInstanceState);
    }

    private OnClickListener getAnswerOnClickListener(final String answer){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(null);
                question.setUserAnswer(answer);
                ((QuizFragment) getParentFragment()).getQuizResult().addAnsweredQuestion(question);
                if(isLast){
                    ((QuizFragment) getParentFragment()).showResultsDialog();
                }
                else{
                    ((QuizFragment) getParentFragment()).nextQuestion();
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        LinearLayout layout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);

        if(!question.getBase64Picture().isEmpty()){
            ImageView imageView = new ImageView(getContext());
            byte[] decodedString = Base64.decode(question.getBase64Picture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
            LinearLayout.LayoutParams paramsImageView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(paramsImageView);
            layout.addView(imageView);
        }

        TextView questionView = new TextView(getContext());
        int questionSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getContext().getResources().getDisplayMetrics());
        int questionMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics());
        LinearLayout.LayoutParams paramsQuestionView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsQuestionView.setMargins(questionMargin, questionMargin, questionMargin, questionMargin);
        questionView.setLayoutParams(paramsQuestionView);
        questionView.setText(question.getQuestion());
        questionView.setTextSize(questionSize);
        questionView.setTextColor(ResourcesCompat.getColor(getContext().getResources(), R.color.textColorSecondary, null));
        layout.addView(questionView);

        switch(question.getClass().getSimpleName()){
            case "MultipleChoiceQuestion":
                NestedScrollView scrollView = (NestedScrollView) inflater.inflate(R.layout.question_layout_topic, container, false);
                LinearLayout questionBox = (LinearLayout) scrollView.findViewById(R.id.questionBox);
                for (String s : ((MultipleChoiceQuestion)question).getPropositions()){
                    CardView c = (CardView) inflater.inflate(R.layout.question_item, questionBox, false);
                    ((TextView)c.findViewById(R.id.question_text)).setText(s);
                    c.setOnClickListener(getAnswerOnClickListener(s));
                    questionBox.addView(c);
                }
                layout.addView(scrollView);
                break;
            case "InputQuestion":
                final EditText editText = new EditText(getContext());
                LinearLayout.LayoutParams paramsEditText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                editText.setLayoutParams(paramsEditText);
                layout.addView(editText);
                Button button = new Button(getContext());
                LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                button.setLayoutParams(paramsButton);
                button.setGravity(Gravity.CENTER_HORIZONTAL);
                button.setText(getString(R.string.submit));
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editText.getText().toString().isEmpty()){
                            Toast toast = Toast.makeText(getContext(), "You must enter an answer", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 20);
                            toast.show();
                        }
                        else{
                            v.setOnClickListener(null);
                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                            question.setUserAnswer(editText.getText().toString());
                            ((QuizFragment) getParentFragment()).getQuizResult().addAnsweredQuestion(question);
                            if(isLast){
                                ((QuizFragment) getParentFragment()).showResultsDialog();
                            }
                            else{
                                ((QuizFragment) getParentFragment()).nextQuestion();
                            }
                        }
                    }
                });
                layout.addView(button);
        }
        return layout;
    }

}
