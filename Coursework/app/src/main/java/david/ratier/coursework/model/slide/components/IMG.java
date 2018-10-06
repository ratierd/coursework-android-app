package david.ratier.coursework.model.slide.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by David on 02/05/2017.
 */

public class IMG implements SlideComponent {

    private String base64Picture;

    public IMG(String base64Picture){
        this.base64Picture = base64Picture;
    }

    @Override
    public View getView(Context context) {
        byte[] decodedString = Base64.decode(base64Picture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ImageView v = new ImageView(context);
        v.setImageBitmap(decodedByte);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(params);
        return v;
    }

}
