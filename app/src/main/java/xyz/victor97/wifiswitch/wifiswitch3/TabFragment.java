package xyz.victor97.wifiswitch.wifiswitch3;

import android.graphics.ColorFilter;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabFragment extends Fragment {
    private FloatingActionButton mFAB;
    private int ID = -1;
    public static final String IDNUMBER = "IDNUMBER";
    private TextView mTextView;
    private ShapeDrawable mShape = new ShapeDrawable();
    private View view = null;
    private boolean light = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            if (getArguments() != null) {
                ID = getArguments().getInt(IDNUMBER);
            }
            view = inflater.inflate(R.layout.tablayout, container, false);
            mTextView = (TextView) view.findViewById(R.id.textview);
            mTextView.setText(String.valueOf(ID + 1));
            mFAB = (FloatingActionButton) view.findViewById(R.id.fab);
            mFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    light = !light;
                    if (light)
                        mFAB.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    else
                        mFAB.setBackgroundColor(getResources().getColor(R.color.colorBccent));
                }
            });
        }
//        mFAB = (FloatingActionButton) view.findViewById(R.id.fab);
//        if (light)
//            mFAB.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
//        else
//            mFAB.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent2));

        return view;
    }
}
