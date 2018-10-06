package david.ratier.coursework.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import david.ratier.coursework.R;
import david.ratier.coursework.model.QuizResult;
import david.ratier.coursework.model.Topic;
import david.ratier.coursework.utils.Triplet;

public class TopicStatsActivity extends AppCompatActivity {

    public static class QuizResultRecyclerViewAdaptor extends RecyclerView.Adapter<QuizResultRecyclerViewAdaptor.ViewHolder>{

        final private List<QuizResult> quizResultSet;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final public CardView mCardView;
            public ViewHolder(CardView v){
                super(v);
                mCardView = v;
            }
        }

        public QuizResultRecyclerViewAdaptor(List<QuizResult> dataSet) {
            quizResultSet = dataSet;
        }

        @Override
        public QuizResultRecyclerViewAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_result_stats, parent, false);
            return new QuizResultRecyclerViewAdaptor.ViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(final QuizResultRecyclerViewAdaptor.ViewHolder holder, final int position) {
            ((TextView)holder.mCardView.findViewById(R.id.quiz_result_date)).setText(quizResultSet.get(position).getDate());
            ((TextView)holder.mCardView.findViewById(R.id.quiz_result_score)).setText(String.valueOf((int)quizResultSet.get(position).getPercentage()) + "%");
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TopicStatsActivity)v.getContext()).showResultDialog(quizResultSet.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return quizResultSet.size();
        }
    }

    public void showResultDialog(QuizResult quizResult){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog);
        View layout = this.getLayoutInflater().inflate(R.layout.result_layout_topic, null);
        LinearLayout rootLayout = (LinearLayout) layout.findViewById(R.id.root_linear_layout);
        int index = 0;
        for (Triplet<String, String, String> r : quizResult.getResults()) {
            View child = this.getLayoutInflater().inflate(R.layout.result_item, null);
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

            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private static final String TOPIC_STATS_KEY = "topicName";

    public static Intent getLaunchingIntent(Context ctx, Topic topic) {
        Intent i = new Intent(ctx, TopicStatsActivity.class);
        i.putExtra(TOPIC_STATS_KEY, topic.getName());
        return i;
    }

    private Topic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_stats);

        if (this.topic == null){
            topic = MainActivity.getTopicByName(getIntent().getStringExtra(TOPIC_STATS_KEY));
        }

        List<QuizResult> quizResults = topic.getQuizResults();
        if(quizResults.size() == 0){
            TextView noResultTextView = (TextView) findViewById(R.id.no_result_textview);
            noResultTextView.setText(getResources().getString(R.string.no_quiz_result));
            BarChart graph = (BarChart) findViewById(R.id.resultGraph);
            graph.setVisibility(View.GONE);
        }
        else{
            BarChart graph = (BarChart) findViewById(R.id.resultGraph);
            graph.getLegend().setEnabled(false);
            graph.setDrawGridBackground(false);
            graph.getAxisLeft().setDrawAxisLine(false);
            graph.getAxisLeft().setDrawLabels(false);
            graph.getAxisLeft().setDrawZeroLine(false);
            graph.getAxisLeft().setAxisMinimum(0f);
            graph.getAxisLeft().setAxisMaximum(100f);
            graph.getAxisRight().setEnabled(false);
            graph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            graph.getXAxis().setCenterAxisLabels(true);
            graph.getXAxis().setDrawLimitLinesBehindData(false);
            graph.getXAxis().setDrawLabels(false);
            graph.setDrawMarkers(false);
            graph.getDescription().setEnabled(false);
            graph.setMinimumHeight(250);

            List<BarEntry> entries = new ArrayList<>();
            int cpt = 0;
            for(QuizResult qr : quizResults){
                entries.add(new BarEntry(cpt, qr.getPercentage()));
                cpt++;
            }
            BarDataSet dataset = new BarDataSet(entries, "Scores");
            List<Integer> colors = new ArrayList<>();
            for(QuizResult qr : quizResults){
                if(qr.getPercentage() >= 80){
                    colors.add(ResourcesCompat.getColor(getResources(), R.color.pieChartColorMastered, null));
                }
                else if (qr.getPercentage() >= 50){
                    colors.add(ResourcesCompat.getColor(getResources(), R.color.pieChartColorAcquired, null));
                }
                else{
                    colors.add(ResourcesCompat.getColor(getResources(), R.color.pieChartColorNotAcquired, null));
                }
            }
            dataset.setColors(colors);
            graph.setData(new BarData(dataset));

            RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.quiz_results_recycler_view);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);

            RecyclerView.Adapter adapter = new QuizResultRecyclerViewAdaptor(topic.getQuizResults());
            recyclerView.setAdapter(adapter);
        }

    }
}
