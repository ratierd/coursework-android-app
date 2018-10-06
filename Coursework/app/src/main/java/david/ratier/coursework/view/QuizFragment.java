package david.ratier.coursework.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.test.espresso.core.deps.guava.reflect.TypeToken;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import david.ratier.coursework.R;
import david.ratier.coursework.model.Question;
import david.ratier.coursework.model.QuizResult;
import david.ratier.coursework.utils.Triplet;

/**
 * Created by ratie on 14/03/2017.
 */

public class QuizFragment extends Fragment {

    public static class QuizViewPager extends TopicActivity.TopicViewPager {

        public QuizViewPager(Context context) {
            super(context);
        }

        public QuizViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            return super.onInterceptTouchEvent(event);
        }

        public void setAdapter(QuizPagerAdapter adapter) {
            super.setAdapter(adapter);
        }

        public QuizPagerAdapter getAdapter(){
            return (QuizPagerAdapter) super.getAdapter();
        }

        public void nextItem(){
            setCurrentItem(getAdapter().nextPosition(), true);
        }

    }

    public static class QuizPagerAdapter extends FragmentPagerAdapter {

        final private List<Question> questions;
        private int currentPosition;

        public QuizPagerAdapter(FragmentManager fm, List<Question> questions) {
            super(fm);
            this.questions = questions;
            currentPosition = 0;
        }

        public int getCurrentPosition(){
            return currentPosition;
        }

        public int nextPosition(){
            currentPosition++;
            return currentPosition;
        }

        @Override
        public Fragment getItem(int position) {
            return QuestionFragment.newInstance(questions.get(position), position == getCount() - 1);
        }

        @Override
        public int getCount() {
            return questions.size();
        }

    }

    private static final String QUESTIONS_KEY = "questions";

    public static QuizFragment newInstance(List<Question> questions) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString(QUESTIONS_KEY, MainActivity.getGson().toJson(questions, new TypeToken<List<Question>>(){}.getType()));
        fragment.setArguments(args);
        return fragment;
    }

    private QuizViewPager viewPager;
    private QuizPagerAdapter pagerAdapter;

    private List<Question> questions;
    private QuizResult quizResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(questions == null){
            questions = MainActivity.getGson().fromJson(getArguments().getString(QUESTIONS_KEY), new TypeToken<List<Question>>(){}.getType());
        }
        if(quizResult == null){
            quizResult = new QuizResult();
            ((TopicActivity)this.getActivity()).getTopic().addQuizResult(quizResult);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewPager = (QuizViewPager) inflater.inflate(R.layout.quiz_layout_topic, container, false);
        pagerAdapter = new QuizPagerAdapter(getChildFragmentManager(), questions);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPagingEnabled(false);
        return viewPager;
    }

    public void nextQuestion(){
        viewPager.nextItem();
    }

    public QuizResult getQuizResult(){
        return quizResult;
    }

    public void showResultsDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog);
        View layout = getActivity().getLayoutInflater().inflate(R.layout.result_layout_topic, null);
        LinearLayout rootLayout = (LinearLayout) layout.findViewById(R.id.root_linear_layout);
        int index = 0;
        for (Triplet<String, String, String> r : quizResult.getResults()) {
            View child = getActivity().getLayoutInflater().inflate(R.layout.result_item, null);
            TextView questionLabel = (TextView) child.findViewById(R.id.question_label);
            TextView question = (TextView) child.findViewById(R.id.question_text_view);
            TextView answer = (TextView) child.findViewById(R.id.answer_text_view);
            TextView userAnswer = (TextView) child.findViewById(R.id.user_answer_text_view);
            questionLabel.setText(getString(R.string.question, index));
            question.setText(r.getValue0());
            answer.setText(getResources().getString(R.string.answer,r.getValue1()));
            userAnswer.setText(getResources().getString(R.string.your_answer, r.getValue2()));

            RelativeLayout result = (RelativeLayout) child.findViewById(R.id.result_relative_layout);
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout resultDetails = (RelativeLayout) ((View) v.getParent()).findViewById(R.id.result_details_relative_layout);
                    if(resultDetails.getVisibility() == View.GONE){
                        resultDetails.setVisibility(View.VISIBLE);
                    }
                    else{
                        resultDetails.setVisibility(View.GONE);
                    }
                }
            });

            rootLayout.addView(child, index);
            index++;
        }

        dialogBuilder.setView(layout);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = MainActivity.getLaunchingIntent(getContext(), null);
                startActivity(i);
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

}
