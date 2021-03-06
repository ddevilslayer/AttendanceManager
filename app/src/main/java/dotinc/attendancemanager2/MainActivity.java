package dotinc.attendancemanager2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dotinc.attendancemanager2.Adapters.MainPageAdapter;
import dotinc.attendancemanager2.Adapters.MainViewPagerAdapter;
import dotinc.attendancemanager2.Fragements.HeaderFragment;
import dotinc.attendancemanager2.Fragements.SecondFragment;
import dotinc.attendancemanager2.Fragements.TutorialFragment;
import dotinc.attendancemanager2.Objects.SubjectsList;
import dotinc.attendancemanager2.Objects.TimeTableList;
import dotinc.attendancemanager2.Utils.AttendanceDatabase;
import dotinc.attendancemanager2.Utils.Helper;
import dotinc.attendancemanager2.Utils.ProgressPageIndicator;
import dotinc.attendancemanager2.Utils.SubjectDatabase;
import dotinc.attendancemanager2.Utils.TimeTableDatabase;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private RecyclerView recyclerView, exclRecyclerView;
    private CoordinatorLayout root;
    private RelativeLayout extraClassLayout;
    private AppBarLayout appBarLayout;
    private TextView extraClassText, fullAttText;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ViewPager pager;
    private ArrayList<Fragment> pageList;
    private HeaderFragment headerFragment;
    private ProgressPageIndicator indicator;
    private FloatingActionButton fab;
    private TutorialFragment fragment;
    public FragmentTransaction transaction;

    private CardView rootEmptyView;
    private TextView rootEmptyTitle, rootEmptyFooter;


    private RelativeLayout extraEmptyView;
    private TextView extraEmptyTitle;


    private ArrayList<TimeTableList> allSubjectsArrayList;      //add
    private ArrayList<TimeTableList> arrayList;
    private ArrayList<SubjectsList> subjectsName;
    private ArrayList<String> subjects;
    private int dayCode;


    private AttendanceDatabase database;
    private TimeTableDatabase timeTableDatabase;
    private TimeTableList timeTableList;
    private SubjectDatabase subjectDatabase;                            //add
    private String day;
    private Date date;
    private String activityName;
    private RecyclerView.Adapter exadapter, mainadapter;
    private Boolean exclViewOpen = false, attAllViewOpen = false;


    void instantiate() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = MainActivity.this;

        extraClassText = (TextView) findViewById(R.id.extra_class_text);
        extraClassText.setTypeface(Typeface.createFromAsset(getAssets(), Helper.OXYGEN_BOLD));
        fullAttText = (TextView) findViewById(R.id.full_att_text);
        fullAttText.setTypeface(Typeface.createFromAsset(getAssets(), Helper.OXYGEN_BOLD));


        rootEmptyView = (CardView) findViewById(R.id.root_empty_view);
        rootEmptyTitle = (TextView) findViewById(R.id.root_empty_title);
        rootEmptyTitle.setTypeface(Typeface.createFromAsset(getAssets(), Helper.OXYGEN_BOLD));
        rootEmptyFooter = (TextView) findViewById(R.id.root_empty_footer);
        rootEmptyFooter.setTypeface(Typeface.createFromAsset(getAssets(), Helper.JOSEFIN_SANS_REGULAR));

        extraEmptyView = (RelativeLayout) findViewById(R.id.empty_view_extra);
        extraEmptyTitle = (TextView) findViewById(R.id.empty_text_extra);
        extraEmptyTitle.setTypeface(Typeface.createFromAsset(getAssets(), Helper.OXYGEN_BOLD));


        extraClassLayout = (RelativeLayout) findViewById(R.id.extra_class_layout);
        activityName = "MainActivity";

        date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("d-M-yyyy");
        day = format.format(date);


        root = (CoordinatorLayout) findViewById(R.id.root);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (ProgressPageIndicator) findViewById(R.id.pageIndicator);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        exclRecyclerView = (RecyclerView) findViewById(R.id.extra_class_recycler_view);
        exclRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exclRecyclerView.setHasFixedSize(true);

        subjectsName = new ArrayList<>();
        subjects = new ArrayList<>();
        pageList = new ArrayList<>();
        headerFragment = new HeaderFragment();
        pageList.add(headerFragment);
        pageList.add(new SecondFragment());
        allSubjectsArrayList = new ArrayList<>();

        timeTableList = new TimeTableList();
        database = new AttendanceDatabase(this);
        timeTableDatabase = new TimeTableDatabase(this);
        subjectDatabase = new SubjectDatabase(this);

        dayCode = getdaycode();
        timeTableList.setDayCode(dayCode);

        arrayList = timeTableDatabase.getSubjects(timeTableList);


        if (arrayList.isEmpty())
            rootEmptyView.setVisibility(View.VISIBLE);
        else
            rootEmptyView.setVisibility(View.INVISIBLE);


        mainadapter = new MainPageAdapter(this, arrayList, day, activityName);
        ((MainPageAdapter) mainadapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mainadapter);
        extraClass();
        if (allSubjectsArrayList.size() == 0)
            extraEmptyView.setVisibility(View.VISIBLE);
        else
            extraEmptyView.setVisibility(View.INVISIBLE);

    }

    public void showSnackbar(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }

    private void extraClass() {
        timeTableList.setDayCode(dayCode);
        arrayList = timeTableDatabase.getSubjects(timeTableList);
        allSubjectsArrayList = subjectDatabase.getAllSubjectsForExtra();
        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < allSubjectsArrayList.size(); j++) {
                if ((allSubjectsArrayList.get(j).getSubjectName().equals(arrayList.get(i).getSubjectName())))
                    allSubjectsArrayList.remove(j);
            }
        }
        exadapter = new MainPageAdapter(this, allSubjectsArrayList, day, activityName);
        ((MainPageAdapter) exadapter).setMode(Attributes.Mode.Single);
        exclRecyclerView.setAdapter(exadapter);
    }

    private void setTitle(String dayName) {
        getSupportActionBar().setTitle(dayName);
    }

    private int getdaycode() {
        int day_code = 1;
        Date date = new Date();
        String myDate;
        SimpleDateFormat format = new SimpleDateFormat("EEE");
        myDate = format.format(date.getTime());
        switch (myDate) {
            case "Mon":
                day_code = 1;
                setTitle(getResources().getString(R.string.monday));
                break;
            case "Tue":
                day_code = 2;
                setTitle(getResources().getString(R.string.tuesday));
                break;
            case "Wed":
                day_code = 3;
                setTitle(getResources().getString(R.string.wednesday));
                break;
            case "Thu":
                day_code = 4;
                setTitle(getResources().getString(R.string.thursday));
                break;
            case "Fri":
                setTitle(getResources().getString(R.string.friday));
                day_code = 5;
                break;
            case "Sat":
                day_code = 6;
                setTitle(getResources().getString(R.string.saturday));
                break;
            case "Sun":
                day_code = 7;
                setTitle(getResources().getString(R.string.sunday));
                break;
        }
        return day_code;
    }

    public void updateOverallPerc() {
        double totalPresent = 0, totalClasses = 0;
        subjectsName = subjectDatabase.getAllSubjects();

        for (int pos = 0; pos < subjectsName.size(); pos++) {
            int id = subjectsName.get(pos).getId();
            totalPresent += database.totalPresent(id);
            totalClasses += database.totalClasses(id);
        }
        headerFragment.setOverallPerc(totalPresent, totalClasses);
    }

    @Override
    protected void onResume() {
        super.onResume();
        instantiate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instantiate();

        if (Integer.parseInt(Helper.getFromPref(context, Helper.FIRST_TIME, String.valueOf(0))) == 0)
            showTutorial();

        pager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager(), pageList));

        pager.addOnPageChangeListener(new CustomOnPageChangeListener());
        indicator.setViewPager(pager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    showExtraClass();
                } else {
                    //------------code for pre-lolipop for extra class------------//
                    Intent intent = new Intent(MainActivity.this, ExtraClassActivity.class);
                    Date date = new Date();
                    String myDate;
                    SimpleDateFormat format = new SimpleDateFormat("EEE");
                    myDate = format.format(date.getTime());
                    intent.putExtra("day_selected", myDate);
                    intent.putExtra("date", day);
                    startActivity(intent);
                }
            }

        });


        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                if (!exclViewOpen) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (!attAllViewOpen && arrayList.size() != 0)
                            markAllClass();
                        else
                            showSnackbar("You don't have any classes today");
                    } else {
                        if (arrayList.size() == 0)
                            showSnackbar("You don't have any classes today");
                        else {
                            //------code for pre-lolipop-------//
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Attended all the subjects?");
                            builder.setPositiveButton("Attended all", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    database.addAllAttendance(arrayList, 1, day);
                                    mainadapter.notifyDataSetChanged();
                                }
                            });
                            builder.setNegativeButton("Bunked all", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    database.addAllAttendance(arrayList, 0, day);
                                    mainadapter.notifyDataSetChanged();
                                }
                            });
                            builder.setNeutralButton("No class", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    database.addAllAttendance(arrayList, -1, day);
                                    mainadapter.notifyDataSetChanged();
                                }
                            });
                            builder.create().show();
                        }

                    }
                }
                return true;
            }
        });
    }

    public void attendAll(View view) {
        //**************** Define the functionality here ***********//
        database.addAllAttendance(arrayList, 1, day);
        mainadapter.notifyDataSetChanged();
        markedAtt();
    }

    public void bunkedAll(View view) {
        //**************** Define the functionality here ***********//
        database.addAllAttendance(arrayList, 0, day);
        mainadapter.notifyDataSetChanged();
        markedAtt();
    }

    public void noClassAll(View view) {
        //**************** Define the functionality here ***********//
        database.addAllAttendance(arrayList, -1, day);
        mainadapter.notifyDataSetChanged();
        markedAtt();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void markAllClass() {

        final View fullAttView = findViewById(R.id.full_att_layout);
        Animator anim;
        attAllViewOpen = true;

        int cX = fullAttView.getWidth();
        int cY = 0;
        int finalRadius = Math.max(fullAttView.getWidth(), fullAttView.getHeight() + 1000);

        anim = ViewAnimationUtils.createCircularReveal(fullAttView, cX, cY, 0, finalRadius);
        anim.setDuration(500).setInterpolator(new DecelerateInterpolator(1));
        fullAttView.setVisibility(View.VISIBLE);
        anim.start();
        fab.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.setImageResource(R.mipmap.ic_clear_white_36dp);
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
                fab.show();
            }
        }, 300);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void markedAtt() {
        final View fullAttView = findViewById(R.id.full_att_layout);

        Animator anim;
        attAllViewOpen = false;

        int cX = fullAttView.getWidth();
        int cY = 0;
        int maxRadius = 0;

        anim = ViewAnimationUtils.createCircularReveal(fullAttView, cX, cY, fullAttView.getWidth(), maxRadius);
        anim.setDuration(500).setInterpolator(new DecelerateInterpolator(1));
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fullAttView.setVisibility(View.GONE);
            }
        });
        anim.start();
        fab.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
                fab.setImageResource(R.mipmap.ic_add_white_36dp);
                fab.show();
            }
        }, 300);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showExtraClass() {

        final View extraView = findViewById(R.id.extra_class_layout);
        Animator anim;
        int cx = extraView.getWidth();
        int cY = 0;
        if (!attAllViewOpen) {
            if (!exclViewOpen) {


                int finalRadius = Math.max(extraView.getWidth(), extraView.getHeight() + 1000);

                anim = ViewAnimationUtils.createCircularReveal(extraView, cx, cY, 0, finalRadius);
                anim.setDuration(1000).setInterpolator(new DecelerateInterpolator(1));
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        rootEmptyView.setVisibility(View.INVISIBLE);
                        if (allSubjectsArrayList.size() == 0)
                            extraEmptyView.setVisibility(View.VISIBLE);
                        else
                            extraEmptyView.setVisibility(View.INVISIBLE);
                    }


                });
                exclViewOpen = true;
                extraView.setVisibility(View.VISIBLE);
                anim.start();
                fab.hide();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fab.setImageResource(R.mipmap.ic_done_white_36dp);
                        fab.show();
                    }
                }, 300);

            } else {
                int finalRadius = 0;
                anim = ViewAnimationUtils.createCircularReveal(extraView,
                        cx, cY, extraView.getHeight() + 1000, finalRadius);
                anim.setDuration(500).setInterpolator(new DecelerateInterpolator(1));
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        extraView.setVisibility(View.INVISIBLE);
                        if (arrayList.size() == 0)
                            rootEmptyView.setVisibility(View.VISIBLE);

                    }
                });
                anim.start();
                fab.hide();
                exclViewOpen = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fab.setImageResource(R.mipmap.ic_add_white_36dp);
                        fab.show();
                    }
                }, 300);
            }
        } else {
            markedAtt();
        }
    }

    @Override
    public void onBackPressed() {
        if (exclViewOpen) {
            final View extraView = findViewById(R.id.extra_class_layout);
            Animator anim;
            int cx = extraView.getWidth();
            int cY = 0;
            int finalRadius = 0;
            anim = ViewAnimationUtils.createCircularReveal(extraView,
                    cx, cY, extraView.getHeight() + 1000, finalRadius);
            anim.setDuration(500).setInterpolator(new DecelerateInterpolator(1));
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    extraView.setVisibility(View.INVISIBLE);
                    if (arrayList.size() == 0)
                        rootEmptyView.setVisibility(View.VISIBLE);

                    extraEmptyView.setVisibility(View.INVISIBLE);

                }
            });
            anim.start();
            fab.hide();
            exclViewOpen = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.setImageResource(R.mipmap.ic_add_white_36dp);
                    fab.show();
                }
            }, 300);
        } else if (attAllViewOpen) {
            markedAtt();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                startActivity(new Intent(context, SettingsActivity.class));
                break;
            case R.id.help:
                startActivity(new Intent(context, HelpActivity.class));
                break;
            case R.id.about_us:
                startActivity(new Intent(context, AboutUsActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showTutorial() {
        fragment = new TutorialFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.root, fragment, "fragment").commit();
        appBarLayout.setVisibility(View.GONE);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        params.setAnchorId(View.NO_ID);
        fab.setLayoutParams(params);
        fab.setVisibility(View.GONE);
    }

    public void closeTutorial() {
        Helper.saveToPref(context, Helper.FIRST_TIME, String.valueOf(1));
        appBarLayout.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        params.setAnchorId(R.id.appbar_layout);
        fab.setLayoutParams(params);
        fab.setVisibility(View.VISIBLE);
        instantiate();
    }


    private class CustomOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    indicator.setViewPager(pager, 0);
                    indicator.setFillColor(getResources().getColor(android.R.color.white));
                    break;
                case 1:
                    indicator.setViewPager(pager, 1);
                    indicator.setFillColor(getResources().getColor(android.R.color.white));
                    break;
                default:
                    break;
            }
            super.onPageSelected(position);
        }
    }
}