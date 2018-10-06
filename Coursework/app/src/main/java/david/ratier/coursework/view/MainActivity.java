package david.ratier.coursework.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import david.ratier.coursework.model.Course;
import david.ratier.coursework.R;
import david.ratier.coursework.model.InputQuestion;
import david.ratier.coursework.model.MultipleChoiceQuestion;
import david.ratier.coursework.model.Slide;
import david.ratier.coursework.model.Topic;
import david.ratier.coursework.model.slide.components.H1;
import david.ratier.coursework.model.slide.components.IMG;
import david.ratier.coursework.model.slide.components.P;
import david.ratier.coursework.model.Question;
import david.ratier.coursework.model.slide.components.SlideComponent;

/**
 * Created by David on 28/02/2017.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final class GsonInterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
        public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
            final JsonObject wrapper = new JsonObject();
            wrapper.addProperty("type", object.getClass().getName());
            wrapper.add("data", context.serialize(object));
            return wrapper;
        }

        public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject wrapper = (JsonObject) elem;
            final JsonElement typeName = get(wrapper, "type");
            final JsonElement data = get(wrapper, "data");
            final Type actualType = typeForName(typeName);
            return context.deserialize(data, actualType);
        }

        private Type typeForName(final JsonElement typeElem) {
            try {
                return Class.forName(typeElem.getAsString());
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        private JsonElement get(final JsonObject wrapper, String memberName) {
            final JsonElement elem = wrapper.get(memberName);
            if (elem == null) throw new JsonParseException("no '" + memberName + "' member found in what was expected to be an interface wrapper");
            return elem;
        }
    }

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SlideComponent.class, new GsonInterfaceAdapter<SlideComponent>())
            .registerTypeAdapter(Question.class, new GsonInterfaceAdapter<Question>())
            .create();
    public static Gson getGson(){
        return gson;
    }

    private static final String COURSES_KEY = "courses";

    @Nullable
    public static Intent getLaunchingIntent(Context ctx, @Nullable String coursesJSON) {
        DownloadCoursesFragment.loadCoursesAvailable();
        Intent i = new Intent(ctx, MainActivity.class);
        if(coursesJSON != null){
            i.putExtra(COURSES_KEY, coursesJSON);
        }
        return i;
    }

    static public Course getCourseByName(String courseName){
        for(Course c : MainActivity.courses){
            if(c.getName().equals(courseName)){
                return c;
            }
        }
        return null;
    }

    static public Topic getTopicByName(String topicName){
        for(Course c : MainActivity.courses){
            for (Topic t : c.getTopics()){
                if(t.getName().equals(topicName)){
                    return t;
                }
            }
        }
        return null;
    }

    static public List<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            String result = "[";
            File coursesDirectory = getApplicationContext().getDir("courses", Context.MODE_PRIVATE);
            for (File f : coursesDirectory.listFiles()) {
                if(f.isFile()){
                    FileInputStream fin = new FileInputStream(f);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    fin.close();
                    result += sb.toString() + ",";
                }
            }
            if(!result.equals("[")){
                result = result.substring(0, result.length() - 1);
            }
            result += "]";
            MainActivity.courses = gson.fromJson(result, new TypeToken<List<Course>>(){}.getType());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = MainFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_content_frame_layout, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        saveData(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData(getApplicationContext());
    }

    public static void saveData(Context context){
        File coursesDirectory = context.getDir("courses", Context.MODE_PRIVATE);
        for (Course c : courses) {
            String json = gson.toJson(c);
            try {
                File fileWithinMyDir = new File(coursesDirectory, c.getFormattedName() + ".json");
                FileOutputStream out = new FileOutputStream(fileWithinMyDir);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
                outputStreamWriter.write(json);
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_courses) {
            fragment = MainFragment.newInstance();
        } else if (id == R.id.nav_stats) {
            fragment = StatsFragment.newInstance();
        } else if (id == R.id.nav_download) {
            fragment = DownloadCoursesFragment.newInstance();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content_frame_layout, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
