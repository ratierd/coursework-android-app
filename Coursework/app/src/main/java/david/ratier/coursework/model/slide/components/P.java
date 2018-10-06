package david.ratier.coursework.model.slide.components;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import david.ratier.coursework.R;

/**
 * Created by ratie on 12/03/2017.
 */

public class P implements SlideComponent {

    private String content;

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public View getView(Context context) {
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());

        TextView v = new TextView(context);
        v.setText(content);
        v.setTextSize(textSize);
        v.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.textColorPrimary, null));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,marginBottom);
        v.setLayoutParams(params);

        return v;
    }

}
