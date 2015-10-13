package xyz.victor97.wifiswitch.wifiswitch3;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Fragment> mTabs = new ArrayList<>();
    private int[] mIDs = new int[]{0, 1, 2, 3};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
    }

    private void InitView() {
        if (getSupportActionBar() != null) getSupportActionBar().setElevation(0);
        TabLayout mTablayout = (TabLayout) findViewById(R.id.tablayout);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        for (int ID: mIDs) {
            TabFragment tagFragment = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(TabFragment.IDNUMBER, ID);
            tagFragment.setArguments(bundle);
            mTabs.add(tagFragment);
        }

        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "开关" + String.valueOf(position + 1);
            }
        };

        mTablayout.setTabsFromPagerAdapter(mPagerAdapter);
        mViewPager.setAdapter(mPagerAdapter);
        mTablayout.setupWithViewPager(mViewPager);
    }
}
