package david.ratier.coursework.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import david.ratier.coursework.R;
import david.ratier.coursework.model.slide.components.H1;
import david.ratier.coursework.model.slide.components.H2;
import david.ratier.coursework.model.slide.components.P;
import david.ratier.coursework.model.slide.components.SlideComponent;

/**
 * Created by ratie on 14/03/2017.
 */

public class SlideFragment extends Fragment {

    private static final String CONTENT_KEY = "content";

    public static SlideFragment newInstance(List<SlideComponent> components) {
        SlideFragment fragment = new SlideFragment();
        Bundle args = new Bundle();
        args.putString(CONTENT_KEY, MainActivity.getGson().toJson(components, new TypeToken<List<SlideComponent>>(){}.getType()));
        fragment.setArguments(args);
        return fragment;
    }

    private List<SlideComponent> components;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(components == null){
            components = MainActivity.getGson().fromJson(getArguments().getString(CONTENT_KEY), new TypeToken<List<SlideComponent>>(){}.getType());
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NestedScrollView view = (NestedScrollView) inflater.inflate(R.layout.slide_layout_topic, container, false);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.slide_layout);
        for (SlideComponent c : components) {
            layout.addView(c.getView(getContext()));
        }
        return view;
    }

}
