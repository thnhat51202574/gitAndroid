package com.example.a51202_000.testbug;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import globalClass.GlobalUserClass;
import layout.EventFragment;
import layout.FriendFrament;
import layout.HomeFragment;
import layout.ProfileFragment;

public class MainTabActivity extends AppCompatActivity implements HomeFragment.comuticateParent {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    FloatingActionButton fab;
    HomeFragment homeFragment;
    EventFragment eventFragment;
    ProfileFragment profileFragment;
    FriendFrament friendFragment;
    GlobalUserClass globalUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        globalUser = (GlobalUserClass) getApplicationContext();
//        screen holdon
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        end holdon
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        mSectionsPagerAdapter.addPage(new HomeFragment());
        homeFragment = new HomeFragment();
        eventFragment = new EventFragment();
        profileFragment = new ProfileFragment();
        friendFragment = new FriendFrament();
        //set frament to each tab
        mSectionsPagerAdapter.addPage(homeFragment);
        mSectionsPagerAdapter.addPage(eventFragment);
        mSectionsPagerAdapter.addPage(friendFragment);
        mSectionsPagerAdapter.addPage(profileFragment);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_event_note_black_24dp);
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_message_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_people_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_person_black_24dp);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Snackbar.make(view,Integer.toString(mViewPager.getCurrentItem()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        homeFragment.receiveMess("ĐÂY LA TIN NHẮN TỪ HOME ACTIVITY");
                        break;
                    case 1:
                        Intent intent_add_event = new Intent(MainTabActivity.this, AddEventActivity.class);
                        intent_add_event.putExtra("userid",globalUser.get_id());
                        startActivity(intent_add_event);
                        break;
                    case 2:
                        break;
                    case 3:
                        Intent intent = new Intent(MainTabActivity.this, EditprofileAcitivity.class);
                        startActivity(intent);
                        break;

                }
            }
        });

    }

    protected void animateFab(final int position) {
        final int[] colorIntArray = {R.color.hindfocus,R.color.btnbackground,R.color.orange,R.color.orange};
        final int[] iconIntArray = {R.drawable.ic_home_black_24dp, R.drawable.fab_ic_add, R.drawable.fab_ic_add,R.drawable.fab_ic_edit};

            fab.clearAnimation();
            // Scale down animation
            ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            shrink.setDuration(150);     // animation duration in milliseconds
            shrink.setInterpolator(new DecelerateInterpolator());
            shrink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Change FAB color and icon
                    fab.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), colorIntArray[position]));
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), iconIntArray[position]));

                    // Scale up animation
                    ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    expand.setDuration(100);     // animation duration in milliseconds
                    expand.setInterpolator(new AccelerateInterpolator());
                    fab.startAnimation(expand);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fab.startAnimation(shrink);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendMess(String text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_tab, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Đây là fragment 1");
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter{
        ArrayList<Fragment> fragments = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
                case 4:
                    return "";
            }
            return null;
        }
        public void addPage(Fragment f) {
            fragments.add(f);
        }
    }
}
