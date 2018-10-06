package david.ratier.coursework.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import david.ratier.coursework.R;
import david.ratier.coursework.model.Course;
import david.ratier.coursework.utils.Pair;
import david.ratier.coursework.utils.Triplet;
import david.ratier.coursework.utils.Tuple;

public class DownloadCoursesFragment extends Fragment {

    public static class DownloadsViewAdaptor extends RecyclerView.Adapter<DownloadsViewAdaptor.ViewHolder>{

        final private List<Pair<String, String>> files;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final public CardView mCardView;
            public ViewHolder(CardView v){
                super(v);
                mCardView = v;
            }
        }

        public DownloadsViewAdaptor(List<Pair<String, String>> files) {
            this.files = files;
        }

        @Override
        public DownloadsViewAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_download_item_courses, parent, false);
            DownloadsViewAdaptor.ViewHolder vh = new DownloadsViewAdaptor.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final DownloadsViewAdaptor.ViewHolder holder, final int position) {
            String courseTitle = files.get(position).getValue0();
            ((TextView)holder.mCardView.findViewById(R.id.course_text)).setText(courseTitle);
            final Course course = MainActivity.getCourseByName(courseTitle);
            if(course == null){
                ((Button)holder.mCardView.findViewById(R.id.download_course_button)).setText(holder.mCardView.getResources().getString(R.string.download));
                ((Button)holder.mCardView.findViewById(R.id.download_course_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectivityManager connectivityManager = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
                            DownloadCoursesFragment.DownloadCourse asyncTask = new DownloadCoursesFragment.DownloadCourse((Button) v, files.get(position).getValue1());
                            try {
                                asyncTask.execute(new URL(fileServerURL + files.get(position).getValue1()));
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(v.getContext(), "No network available !", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 20);
                            toast.show();
                        }
                    }
                });
            }
            else{
                ((Button)holder.mCardView.findViewById(R.id.download_course_button)).setText(holder.mCardView.getResources().getString(R.string.open));
                ((Button)holder.mCardView.findViewById(R.id.download_course_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity host = (Activity) v.getContext();
                        Intent intent = CourseActivity.getLaunchingIntent(host, course);
                        host.startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return files.size();
        }

    }


    private static String fileServerURL = "https://coursework-c345a.firebaseapp.com/";
    private static List<Pair<String, String>> files = new ArrayList<>();

    private static class DownloadCourseList extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            try {
                String tDir = System.getProperty("java.io.tmpdir");
                String path = tDir + "tmp" + ".ini";
                File file = new File(path);
                file.deleteOnExit();
                FileUtils.copyURLToFile(urls[0], file);

                FileInputStream fin = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
                String line = null;
                ArrayList<Pair<String, String>> files = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    files.add(new Pair<String, String>(line, reader.readLine()));
                }
                reader.close();
                fin.close();
                DownloadCoursesFragment.files = files;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0l;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
        }
    }

    public static void loadCoursesAvailable(){
        DownloadCourseList asyncTask = new DownloadCourseList();
        try {
            asyncTask.execute(new URL(fileServerURL + "index.ini"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static DownloadCoursesFragment newInstance() {
        DownloadCoursesFragment fragment = new DownloadCoursesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private static class DownloadCourse extends AsyncTask<URL, Integer, Long> {

        private Button button;
        private File courseFile;
        private String filename;

        public DownloadCourse(Button button, String filename){
            super();
            this.button = button;
            this.filename = filename;
        }

        protected Long doInBackground(URL... urls) {
            try {
                String tDir = System.getProperty("java.io.tmpdir");
                String path = tDir + "tmp" + ".json";
                File file = new File(path);
                file.deleteOnExit();
                FileUtils.copyURLToFile(urls[0], file);
                courseFile = file;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0l;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            File coursesDirectory = button.getContext().getDir("courses", Context.MODE_PRIVATE);
            try {
                File fileWithinMyDir = new File(coursesDirectory, filename);
                FileUtils.copyFile(courseFile, fileWithinMyDir);
                button.setText(button.getResources().getString(R.string.open));
                String jsonCourse = "";
                jsonCourse = FileUtils.readFileToString(fileWithinMyDir, Charset.defaultCharset());
                final Course course = MainActivity.getGson().fromJson(jsonCourse, Course.class);
                MainActivity.courses.add(course);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity host = (Activity) v.getContext();
                        Intent intent = CourseActivity.getLaunchingIntent(host, course);
                        host.startActivity(intent);
                    }
                });
                MainActivity.saveData(button.getContext());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_download_courses, container, false);

        Log.d("PasBien", files.toString());

        RecyclerView recyclerView = (RecyclerView) fragmentView.findViewById(R.id.courses_download_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new DownloadCoursesFragment.DownloadsViewAdaptor(files);
        recyclerView.setAdapter(adapter);

        return fragmentView;
    }

}
