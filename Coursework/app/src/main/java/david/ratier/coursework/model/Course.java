package david.ratier.coursework.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ratie on 22/02/2017.
 */

public class Course {

    private String name;
    private List<Topic> topics;

    public Course(){
        this.topics = new ArrayList<Topic>();
    }

    public Course(String name, List<Topic> topics){
        name = name;
        topics = topics;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getFormattedName(){
        return name.replace(' ', '_').toLowerCase().trim();
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void addTopics(Topic ... topics){
        for (Topic t: topics) {
            this.topics.add(t);
        }
    }

}
