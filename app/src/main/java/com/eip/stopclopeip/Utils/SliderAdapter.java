package com.eip.stopclopeip.Utils;

import com.eip.stopclopeip.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
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

    public int[] slide_backgrounds = {
            R.drawable.tutorial_1,
            R.drawable.tutorial_2,
            R.drawable.tutorial_3,
            R.drawable.tutorial_4
    };

    public String[] slide_titles = {
            "Accueil",
            "Statistique",
            "Conseil",
            "Carte"
    };

    public String[] slide_descs = {
            "L’état de Cactos le cactus représentera votre évolution",
            "Suivez vos statistiques dans l’arrêt de la cigarette en temps réel",
            "Consultez les conseils des autres utilisateurs",
            "La carte proposera des pharmacies ainsi que vos zones à risques à proximité"
    };

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (ConstraintLayout) o;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ConstraintLayout slideLayout = view.findViewById(R.id.slide_layout);
        TextView slideTitle = view.findViewById(R.id.tutorial_title);
        TextView slideDescription = view.findViewById(R.id.tutorial_description);

        slideLayout.setBackground(ContextCompat.getDrawable(context, slide_backgrounds[position]));
        slideTitle.setText(slide_titles[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
