package david.ratier.coursework.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import david.ratier.coursework.model.Course;
import david.ratier.coursework.R;
import david.ratier.coursework.model.Topic;

/**
 * Created by David on 28/02/2017.
 */

public class CourseActivity extends AppCompatActivity {

    public static class CourseRecyclerViewAdaptorForTopics extends RecyclerView.Adapter<CourseRecyclerViewAdaptorForTopics.ViewHolder> {

        final private List<Topic> topicSet;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final public CardView mCardView;
            public ViewHolder(CardView v){
                super(v);
                mCardView = v;
            }
        }

        public CourseRecyclerViewAdaptorForTopics(List<Topic> dataSet) {
            topicSet = dataSet;
        }

        @Override
        public CourseRecyclerViewAdaptorForTopics.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
            return new CourseRecyclerViewAdaptorForTopics.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final CourseRecyclerViewAdaptorForTopics.ViewHolder holder, int position) {
            ((TextView)holder.mCardView.findViewById(R.id.topic_name)).setText(topicSet.get(position).getName());
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity host = (Activity) v.getContext();
                    Intent intent = TopicActivity.getLaunchingIntent(host, topicSet.get(holder.getAdapterPosition()));
                    host.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return topicSet.size();
        }

    }

    private static final String COURSE_KEY = "courseName";

    public static Intent getLaunchingIntent(Context ctx, Course course) {
        Intent i = new Intent(ctx, CourseActivity.class);
        i.putExtra(COURSE_KEY, course.getName());
        return i;
    }

    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        if (this.course == null){
            course = MainActivity.getCourseByName(getIntent().getStringExtra(COURSE_KEY));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(course.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.topics_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new CourseRecyclerViewAdaptorForTopics(course.getTopics());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
