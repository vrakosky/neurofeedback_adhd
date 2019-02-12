package utp.esirem.vincent.realtimegraph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {
            R.drawable.hot1000_device,
            R.drawable.icon_read,
            R.drawable.icon_write,
            R.drawable.sleep_icon,
            R.drawable.code_icon
    };

    public String[] slide_headings = {
            "WEAR",
            "READ",
            "PLAY",
            "REST",
            "ANALYSE"
    };

    public String[] slide_descs = {
            "HOT1000 Sensor is a fNIRS portable measurement device. Put the device on the front of your head. You can now see your brain data in real-time",
            "Read the following passage to answer the given questions based on it. Some words/phrases are printed in bold to help you locate them while answering some of the questions. ",
            "The game is a quiz, you will see questions and answer coming at every round, based to your reading answer it",
            "You need rest to increase your attention and to be effective to the next task, rest for 2 minutes",
            "Analyse your score and your collegues score, with and without automatic adaptative brightness"
    };


    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
