package dotinc.attendancemanager2;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dotinc.attendancemanager2.Adapters.CustomSpinnerAdapter;
import dotinc.attendancemanager2.Objects.AttendanceList;
import dotinc.attendancemanager2.Objects.SubjectsList;
import dotinc.attendancemanager2.Utils.AttendanceDatabase;
import dotinc.attendancemanager2.Utils.SubjectDatabase;

public class DetailedAnalysisActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spinnerNav;
    private LinearLayout root;
    private Context context;

    private ArrayList<String> subjects;
    private ArrayList<SubjectsList> subjectsLists;
    private SubjectDatabase subjectDatabase;
    private ArrayList<AttendanceList> attendanceObject;
    private AttendanceDatabase attendancedb;
    private SimpleDateFormat formatter;

    private CaldroidFragment caldroidFragment;


    private void instantiate() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinnerNav = (Spinner) findViewById(R.id.spinner_nav);
        root = (LinearLayout) findViewById(R.id.display_root);

        context = DetailedAnalysisActivity.this;
        subjectDatabase = new SubjectDatabase(context);
        attendancedb = new AttendanceDatabase(context);
        subjectsLists = subjectDatabase.getAllSubjects();
        formatter = new SimpleDateFormat("dd-MM-yyyy");
        subjects = new ArrayList<>();

        for (int pos = 0; pos < subjectsLists.size(); pos++)
            subjects.add(subjectsLists.get(pos).getSubjectName());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_analysis);

        instantiate();
        addItemsToSpinner();

    }

    public void addItemsToSpinner() {

        spinnerNav.setAdapter(new CustomSpinnerAdapter(this, subjects));
        spinnerNav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                fetchFromDatabase(subjectsLists.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    private void setCustomResourceForDates() {
        Date greenDate = new Date();

        for (int i = 0; i < attendanceObject.size(); i++) {
            try {
                Log.d("option_action", String.valueOf(attendanceObject.get(i).getAction()));
                String date = attendanceObject.get(i).getDate();
                greenDate = formatter.parse(date);
                if (attendanceObject.get(i).getAction() == 1) {
                    caldroidFragment.setBackgroundResourceForDate(R.color.colorPrimary,
                            greenDate);
                    caldroidFragment.setTextColorForDate(R.color.backgroundColor, greenDate);
                } else if (attendanceObject.get(i).getAction() == 0) {
                    caldroidFragment.setBackgroundResourceForDate(R.color.absentColor,
                            greenDate);
                    caldroidFragment.setTextColorForDate(R.color.backgroundColor, greenDate);
                } else if (attendanceObject.get(i).getAction() == -1) {
                    caldroidFragment.setBackgroundResourceForDate(R.color.noClassColor, greenDate);
                    caldroidFragment.setTextColorForDate(R.color.white, greenDate);
                }
                Log.d("NoClass", String.valueOf(attendanceObject.get(i).getAction()));
            } catch (ParseException e) {

                e.printStackTrace();
                Log.d("option_da", e.toString());
            }
        }
    }

    private void fetchFromDatabase(int id) {
        attendanceObject = attendancedb.getAllDates(id);
        setUpCalendar();
    }

    private void setUpCalendar() {


        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
        caldroidFragment.setArguments(args);
        caldroidFragment.setMaxDate(new Date());
        setCustomResourceForDates();
        customize();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.display_root, caldroidFragment);
        t.commit();

    }

    private void customize() {
        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {

            }

            @Override
            public void onCaldroidViewCreated() {
                super.onCaldroidViewCreated();

                Button leftButton = caldroidFragment.getLeftArrowButton();
                Button rightButton = caldroidFragment.getRightArrowButton();
                TextView textView = caldroidFragment.getMonthTitleTextView();

                leftButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
                rightButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
                textView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                textView.setTypeface(null, Typeface.BOLD);
            }
        };
        caldroidFragment.setCaldroidListener(listener);
    }

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
}
