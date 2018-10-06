package david.ratier.coursework.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import david.ratier.coursework.R;
import david.ratier.coursework.model.slide.components.SlideComponent;

/**
 * Created by ratie on 12/03/2017.
 */

public class Slide {

    private String title;
    private List<SlideComponent> content;

    public Slide(){
        content = new ArrayList<>();
    }

    public Slide(String title, List<SlideComponent> content) {
        title = title;
        content = content;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public List<SlideComponent> getSlideComponents(){
        return content;
    }

    public void addComponents(SlideComponent ... components){
        for (SlideComponent c: components) {
            this.content.add(c);
        }
    }

}
