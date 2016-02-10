package dotinc.attendancemanager2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import dotinc.attendancemanager2.Adapters.AttendanceAdapter;
import dotinc.attendancemanager2.Objects.SubjectsList;
import dotinc.attendancemanager2.Objects.TimeTableList;
import dotinc.attendancemanager2.Utils.AttendanceDatabase;
import dotinc.attendancemanager2.Utils.SubjectDatabase;
import dotinc.attendancemanager2.Utils.TimeTableDatabase;

public class GoToDateActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView, exclRecyclerView;
    private CoordinatorLayout root;
    private RelativeLayout extraClassLayout;

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private FloatingActionButton fab;
    private Button attendAll;
    private Button bunkedAll;
    private Button noClassAll;

    private ArrayList<SubjectsList> subjectsName;
    private ArrayList<String> subjects;
    private int dayCode;
    private String day_name;
    private ArrayList<TimeTableList> allSubjectsArrayList;      //add
    private ArrayList<TimeTableList> arrayList;

    AttendanceDatabase database;
    TimeTableDatabase timeTableDatabase;
    TimeTableList timeTableList;
    SubjectDatabase subjectDatabase;                            //add
    String activityName;
    String date;
    private Boolean isViewopened = false;


    void instantiate() {

        Intent intent = getIntent();
        day_name = intent.getStringExtra("day_name");
        date = intent.getStringExtra("date");
        activityName = "GoToDateActivity";

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        extraClassLayout = (RelativeLayout) findViewById(R.id.extra_class_layout);
        root = (CoordinatorLayout) findViewById(R.id.root);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        attendAll = (Button) findViewById(R.id.attend_all);
        bunkedAll = (Button) findViewById(R.id.bunked_all);
        noClassAll = (Button) findViewById(R.id.noclass_all);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        exclRecyclerView = (RecyclerView) findViewById(R.id.extra_class_recycler_view);
        exclRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exclRecyclerView.setHasFixedSize(true);

        subjectsName = new ArrayList<>();
        subjects = new ArrayList<>();
        allSubjectsArrayList = new ArrayList<>();

        timeTableList = new TimeTableList();
        database = new AttendanceDatabase(this);
        timeTableDatabase = new TimeTableDatabase(this);
        subjectDatabase = new SubjectDatabase(this);

        dayCode = getdaycode(day_name);
        timeTableList.setDayCode(dayCode);
        arrayList = timeTableDatabase.getSubjects(timeTableList);

    }

    public void showSnackbar(String meesage) {
        Snackbar.make(root, meesage, Snackbar.LENGTH_SHORT).show();
    }

    private void extraClass() {
        timeTableList.setDayCode(dayCode);                //daycode
        arrayList = timeTableDatabase.getSubjects(timeTableList);
        allSubjectsArrayList = subjectDatabase.getAllSubjectsForExtra();
        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < allSubjectsArrayList.size(); j++) {
                if ((allSubjectsArrayList.get(j).getSubjectName().equals(arrayList.get(i).getSubjectName())))
                    allSubjectsArrayList.remove(j);
            }
        }
        exclRecyclerView.setAdapter(new AttendanceAdapter(this, allSubjectsArrayList, date, activityName));
    }

//    private void setTitle(String dayName) {
//        getSupportActionBar().setTitle(dayName);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.go_to_date_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }

    private int getdaycode(String myDate) {
        int day_code = 0;
        switch (myDate) {
            case "Mon":
                day_code = 1;
                setTitle("Monday");
                break;
            case "Tue":
                day_code = 2;
                setTitle("Tuesday");
                break;
            case "Wed":
                day_code = 3;
                setTitle("Wednesday");
                break;
            case "Thu":
                day_code = 4;
                setTitle("Thursday");
                break;
            case "Fri":
                setTitle("Friday");
                day_code = 5;
                break;
            case "Sat":
                day_code = 6;
                setTitle("Saturday");
                break;
            case "Sun":
                setTitle("Sunday");
                break;
        }
        return day_code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to_date);
        instantiate();
        extraClass();

        recyclerView.setAdapter(new AttendanceAdapter(this, arrayList, date, activityName));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    final View extraView = findViewById(R.id.extra_class_layout);
                    Animator anim = null;
                    int cx = extraView.getWidth();
                    int cY = 0;

                    if (!isViewopened) {
                        int finalRadius = Math.max(extraView.getWidth(), extraView.getHeight() + 1000);

                        anim = ViewAnimationUtils.createCircularReveal(extraView, cx, cY, 0, finalRadius);
                        anim.setDuration(1000).setInterpolator(new DecelerateInterpolator(1));
                        isViewopened = true;
                        extraView.setVisibility(View.VISIBLE);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                extraClassLayout.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                            }
                        });
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

                            }
                        });
                        anim.start();
                        fab.hide();
                        isViewopened = false;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fab.setImageResource(R.mipmap.ic_add_white_36dp);
                                fab.show();
                            }
                        }, 300);
                    }
                }
            }

        });


        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fullAttendance();
                }
                return true;
            }
        });


        attendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markedAtt();


            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void fullAttendance() {

        final View fullAttView = findViewById(R.id.full_att_layout);
        Animator anim = null;

        int cX = fullAttView.getWidth();
        int cY = 0;

        int finalRadius = Math.max(fullAttView.getWidth(), fullAttView.getHeight() + 1000);

        anim = ViewAnimationUtils.createCircularReveal(fullAttView, cX, cY, 0, finalRadius);
        anim.setDuration(500).setInterpolator(new DecelerateInterpolator(1));
        fullAttView.setVisibility(View.VISIBLE);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                params.setAnchorId(View.NO_ID);
                fab.setLayoutParams(params);
                fab.setVisibility(View.GONE);
            }
        });
        anim.start();

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void markedAtt() {
        final View fullAttView = findViewById(R.id.full_att_layout);

        Animator anim = null;

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
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                layoutParams.setAnchorId(R.id.appbar_layout);
                fab.setLayoutParams(layoutParams);
                fab.show();
            }
        });
        anim.start();
    }

}
