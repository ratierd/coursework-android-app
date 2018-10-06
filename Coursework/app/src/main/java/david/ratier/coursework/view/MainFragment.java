package david.ratier.coursework.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import david.ratier.coursework.model.Course;
import david.ratier.coursework.R;

/**
 * Created by David on 28/02/2017.
 */

public class MainFragment extends Fragment {

    public static class MainRecyclerViewAdaptorForCourses extends RecyclerView.Adapter<MainRecyclerViewAdaptorForCourses.ViewHolder>{

        final private List<Course> courseSet;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final public CardView mCardView;
            public ViewHolder(CardView v){
                super(v);
                mCardView = v;
            }
        }

        public MainRecyclerViewAdaptorForCourses(List<Course> dataSet) {
            courseSet = dataSet;
        }

        @Override
        public MainRecyclerViewAdaptorForCourses.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_main, parent, false);
            MainRecyclerViewAdaptorForCourses.ViewHolder vh = new MainRecyclerViewAdaptorForCourses.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final MainRecyclerViewAdaptorForCourses.ViewHolder holder, int position) {
            ((TextView)holder.mCardView.findViewById(R.id.course_text)).setText(courseSet.get(position).getName());
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity host = (Activity) v.getContext();
                    Intent intent = CourseActivity.getLaunchingIntent(host, courseSet.get(holder.getAdapterPosition()));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(host,
                            (View)v.findViewById(R.id.card_image), "course_card_to_topics_image");
                    host.startActivity(intent, options.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return courseSet.size();
        }

    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        if(MainActivity.courses == null || MainActivity.courses.size() == 0){
            TextView noCoursesTextView = (TextView) fragmentView.findViewById(R.id.no_courses_textview);
            noCoursesTextView.setText(getResources().getString(R.string.no_courses));
        }
        else{
            RecyclerView recyclerView = (RecyclerView) fragmentView.findViewById(R.id.courses_recycler_view);
            recyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
            recyclerView.setLayoutManager(layoutManager);

            RecyclerView.Adapter adapter = new MainRecyclerViewAdaptorForCourses(MainActivity.courses);
            recyclerView.setAdapter(adapter);
        }

        return fragmentView;
    }

}
