package david.ratier.coursework.view;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import david.ratier.coursework.model.Topic;
import david.ratier.coursework.R;

/**
 * Created by David on 14/03/2017.
 */

public class TopicActivity extends AppCompatActivity {

    public static class TopicTabLayout extends TabLayout {

        private boolean isPagingEnabled = true;

        public TopicTabLayout(Context context) {
            super(context);
        }

        public TopicTabLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TopicTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public boolean isPagingEnabled() {
            return isPagingEnabled;
        }

        public void setPagingEnabled(boolean b) {
            if(b != isPagingEnabled){
                LinearLayout tabStrip = (LinearLayout) getChildAt(0);
                tabStrip.setEnabled(b);
                for (int i = 0; i < tabStrip.getChildCount() - 1; i++){
                    ViewGroup tab = (ViewGroup) tabStrip.getChildAt(i);
                    tab.setClickable(b);
                    ((TextView) tab.getChildAt(1)).setTextColor(getResources().getColor(R.color.textColorSecondary));
                }
                tabStrip.getChildAt(tabStrip.getChildCount() - 1).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                this.isPagingEnabled = b;
            }
        }

    }

    public static class TopicViewPager extends ViewPager {

        private boolean isPagingEnabled = true;

        public TopicViewPager(Context context) {
            super(context);
        }

        public TopicViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return this.isPagingEnabled && super.onTouchEvent(event);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            return this.isPagingEnabled && super.onInterceptTouchEvent(event);
        }

        public boolean isPagingEnabled() {
            return isPagingEnabled;
        }

        public void setPagingEnabled(boolean b) {
            this.isPagingEnabled = b;
        }

    }

    public static class TopicPagerAdapter extends FragmentPagerAdapter {

        final static int NBR_QUESTION = 5;

        final private Topic topic;

        public TopicPagerAdapter(FragmentManager fm, Topic topic) {
            super(fm);
            this.topic = topic;
        }

        @Override
        public Fragment getItem(int position) {
            if(position == topic.getSlides().size()){
                return QuizFragment.newInstance(topic.getRandomQuestions(NBR_QUESTION));
            }
            else{
                return SlideFragment.newInstance(topic.getSlides().get(position).getSlideComponents());
            }
        }

        @Override
        public int getCount() {
            return topic.getSlides().size() + 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == topic.getSlides().size()){
                return "QUIZ";
            }
            return topic.getSlides().get(position).getTitle();
        }

    }

    private static final String TOPIC_KEY = "topicName";

    public static Intent getLaunchingIntent(Context ctx, Topic topic) {
        Intent i = new Intent(ctx, TopicActivity.class);
        i.putExtra(TOPIC_KEY, topic.getName());
        return i;
    }

    private TopicTabLayout tabLayout;
    private TopicViewPager viewPager;
    private TopicPagerAdapter pagerAdapter;

    private Topic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        if(this.topic == null){
            topic = MainActivity.getTopicByName(getIntent().getStringExtra(TOPIC_KEY));
        }

        tabLayout = (TopicTabLayout) findViewById(R.id.topic_tab_layout);
        viewPager = (TopicViewPager) findViewById(R.id.topic_view_pager);
        pagerAdapter = new TopicPagerAdapter(getSupportFragmentManager(), topic);

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                if (position >= topic.getSlides().size()){
                    viewPager.setPagingEnabled(false);
                    tabLayout.setPagingEnabled(false);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    public Topic getTopic(){
        return topic;
    }

}
