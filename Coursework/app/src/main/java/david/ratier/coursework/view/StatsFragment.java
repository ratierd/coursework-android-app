package david.ratier.coursework.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import david.ratier.coursework.R;
import david.ratier.coursework.model.Course;
import david.ratier.coursework.model.QuizResult;
import david.ratier.coursework.model.Topic;

public class StatsFragment extends Fragment {

    public static class CourseStatsRecyclerViewAdaptor extends RecyclerView.Adapter<CourseStatsRecyclerViewAdaptor.ViewHolder>{

        final private List<Course> courseSet;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            final public CardView mCardView;

            public ViewHolder(CardView v){
                super(v);
                mCardView = v;
            }
        }

        public CourseStatsRecyclerViewAdaptor(List<Course> dataSet) {
            courseSet = dataSet;
        }

        @Override
        public CourseStatsRecyclerViewAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_course_stats, parent, false);
            RelativeLayout statsCard = (RelativeLayout) card.findViewById(R.id.stats_relative_layout);
            statsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout statsDetails = (LinearLayout) ((View) v.getParent()).findViewById(R.id.stats_details_relative_layout);
                    if(statsDetails.getVisibility() == View.GONE){
                        statsDetails.setVisibility(View.VISIBLE);
                    }
                    else{
                        statsDetails.setVisibility(View.GONE);
                    }
                }
            });
            CourseStatsRecyclerViewAdaptor.ViewHolder vh = new CourseStatsRecyclerViewAdaptor.ViewHolder(card);
            return vh;
        }

        @Override
        public void onBindViewHolder(final CourseStatsRecyclerViewAdaptor.ViewHolder holder, int position) {
            ((TextView)holder.mCardView.findViewById(R.id.course_text)).setText(courseSet.get(position).getName());

            PieChart chart = (PieChart) holder.mCardView.findViewById(R.id.course_average_score_chart);

            chart.getLegend().setEnabled(false);
            chart.getDescription().setEnabled(false);
            chart.setDrawEntryLabels(false);
            chart.setHoleRadius(60f);
            chart.setTransparentCircleRadius(65f);
            chart.setMinimumWidth(200);
            chart.setMinimumHeight(200);
            chart.setCenterTextColor(ResourcesCompat.getColor(holder.mCardView.getResources(), R.color.pieChartCenterTextColor, null));
            chart.setCenterTextSize(16f);

            List<PieEntry> entries = new ArrayList<>();
            int notFinished = 0;
            int notAcquired = 0;
            int acquired = 0;
            int mastered = 0;
            for(Topic t : courseSet.get(position).getTopics()){
                if(t.getQuizResults().size() > 0){
                    float average = t.getAverageResult();
                    if(average >= 80){
                        mastered++;
                    }
                    else if(average >= 50){
                        acquired++;
                    }
                    else{
                        notAcquired++;
                    }
                }
                else{
                    notFinished++;
                }
            }
            entries.add(new PieEntry(notFinished, "Not finished"));
            entries.add(new PieEntry(notAcquired, "Not aquired"));
            entries.add(new PieEntry(acquired, "Acquired"));
            entries.add(new PieEntry(mastered, "Mastered"));
            chart.setCenterText((acquired + notAcquired) + "/" + courseSet.get(position).getTopics().size());
            PieDataSet dataset = new PieDataSet(entries, "Topics completed");
            List<Integer> colors = new ArrayList<>();
            colors.add(ResourcesCompat.getColor(holder.mCardView.getResources(), R.color.pieChartColorNotFinished, null));
            colors.add(ResourcesCompat.getColor(holder.mCardView.getResources(), R.color.pieChartColorNotAcquired, null));
            colors.add(ResourcesCompat.getColor(holder.mCardView.getResources(), R.color.pieChartColorAcquired, null));
            colors.add(ResourcesCompat.getColor(holder.mCardView.getResources(), R.color.pieChartColorMastered, null));
            dataset.setColors(colors);
            dataset.setDrawValues(false);
            PieData data = new PieData(dataset);
            chart.setData(data);
            chart.invalidate();

            RecyclerView recyclerView = (RecyclerView) holder.mCardView.findViewById(R.id.topics_recycler_view);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(holder.mCardView.getContext());
            recyclerView.setLayoutManager(layoutManager);

            RecyclerView.Adapter adapter = new TopicStatsRecyclerViewAdaptor(courseSet.get(position).getTopics());
            recyclerView.setAdapter(adapter);
        }

        @Override
        public int getItemCount() {
            return courseSet.size();
        }
    }

    public static class TopicStatsRecyclerViewAdaptor extends RecyclerView.Adapter<TopicStatsRecyclerViewAdaptor.ViewHolder>{

        final private List<Topic> topicSet;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final public CardView mCardView;
            public ViewHolder(CardView v){
                super(v);
                mCardView = v;
            }
        }

        public TopicStatsRecyclerViewAdaptor(List<Topic> dataSet) {
            topicSet = dataSet;
        }

        @Override
        public TopicStatsRecyclerViewAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_topic_stats, parent, false);
            TopicStatsRecyclerViewAdaptor.ViewHolder vh = new TopicStatsRecyclerViewAdaptor.ViewHolder(cardView);
            return vh;
        }

        @Override
        public void onBindViewHolder(final TopicStatsRecyclerViewAdaptor.ViewHolder holder, int position) {
            ((TextView)holder.mCardView.findViewById(R.id.topic_name)).setText(topicSet.get(position).getName());
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity host = (Activity) v.getContext();
                    Intent intent = TopicStatsActivity.getLaunchingIntent(host, topicSet.get(holder.getAdapterPosition()));
                    host.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return topicSet.size();
        }
    }

    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_stats, container, false);

        RecyclerView recyclerView = (RecyclerView) fragmentView.findViewById(R.id.courses_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new CourseStatsRecyclerViewAdaptor(MainActivity.courses);
        recyclerView.setAdapter(adapter);

        return fragmentView;
    }

}
