package com.endimuceku.groupup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

// Activity for creating new events
public class CreateEventActivity extends AppCompatActivity {

    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private TextInputEditText date;
    private TextInputEditText time;
    private AutoCompleteTextView type;

    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference eventsRef;
    private DatabaseReference groupsRef;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialise context, authentication and user
        context = getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Set up database references
        eventsRef = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("events");
        groupsRef = FirebaseDatabase.getInstance("https://groupup-115e1-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("groups");

        // Code implementation for date picker
        date = (TextInputEditText) findViewById(R.id.event_date_ed);
        date.setFocusable(false);
        date.setClickable(true);
        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String text = dayOfMonth + "/" + (month + 1) + "/" + year;

                        date.setText(text);

                    }

                }, year, month, day);

                datePicker.show();

            }

        });

        // Code implementation for time picker
        time = (TextInputEditText) findViewById(R.id.event_time_ed);
        time.setFocusable(false);
        time.setClickable(true);
        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);

                timePicker = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String text;

                        if (hourOfDay < 10 && minute < 10){

                            text = "0" + hourOfDay + ":0" + minute;

                        } else if (hourOfDay < 10) {

                            text = "0" + hourOfDay + ":" + minute;

                        } else if (minute < 10){

                            text = hourOfDay + ":0" + minute;

                        } else {

                            text = hourOfDay + ":" + minute;

                        }

                        time.setText(text);

                    }

                }, hour, minutes, true);

                timePicker.show();

            }

        });

        // Code for event type drop down picker
        type = (AutoCompleteTextView) findViewById(R.id.event_type_ed);

        String[] dropDownOptions =
                {"Food & Drink", "Exercise", "Games", "Hangout", "Cinema", "Concert", "Sports", "Party", "Shopping", "Other"};

        ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, dropDownOptions);

        type.setAdapter(adapter);
        type.setThreshold(1);
    }

    // Method which runs when the create event button is clicked, responsible for creating a new event
    public void createEventButtonClicked(View view) {

        // Get data from the input forms
        TextInputLayout mEventTitle = (TextInputLayout) findViewById(R.id.event_title);
        TextInputLayout mEventDescription = (TextInputLayout) findViewById(R.id.event_description);
        TextInputLayout mEventDate = (TextInputLayout) findViewById(R.id.event_date);
        TextInputLayout mEventTime = (TextInputLayout) findViewById(R.id.event_time);
        TextInputLayout mAddressLine1 = (TextInputLayout) findViewById(R.id.address_line_1);
        TextInputLayout mAddressLine2 = (TextInputLayout) findViewById(R.id.address_line_2);
        TextInputLayout mAddressLine3 = (TextInputLayout) findViewById(R.id.address_line_3);
        TextInputLayout mPostcode = (TextInputLayout) findViewById(R.id.postcode);
        TextInputLayout mLocation = (TextInputLayout) findViewById(R.id.location);
        TextInputLayout mEventType = (TextInputLayout) findViewById(R.id.event_type);

        String eventTitle = mEventTitle.getEditText().getText().toString();
        String eventDescription = mEventDescription.getEditText().getText().toString();
        String eventDate = mEventDate.getEditText().getText().toString();
        String eventTime = mEventTime.getEditText().getText().toString();
        String addressLine1 = mAddressLine1.getEditText().getText().toString();
        String addressLine2 = mAddressLine2.getEditText().getText().toString();
        String addressLine3 = mAddressLine3.getEditText().getText().toString();
        String postcode = mPostcode.getEditText().getText().toString();
        String location = mLocation.getEditText().getText().toString();
        String eventType = mEventType.getEditText().getText().toString();

        // Display error messages if input data is not valid
        if (eventTitle.isEmpty()) {

            mEventTitle.setError("You need to enter an event title.");

        } else if (eventDate.isEmpty()) {

            mEventDate.setError("You need to set a date.");

        } else if (eventTime.isEmpty()) {

            mEventTime.setError("You need to set a time.");

        } else if (addressLine1.isEmpty() || addressLine2.isEmpty()) {

            mAddressLine1.setError("Invalid address.");

        } else if (postcode.isEmpty()) {

            mPostcode.setError("You need to enter a postcode.");

        } else if (eventType.isEmpty()) {

            mEventType.setError("You need to choose an event type.");

        } else {

            // Create new event in the database
            EventGroup eventGroup = new EventGroup(eventTitle, eventDescription, eventDate, eventTime, addressLine1, addressLine2,
                    addressLine3, postcode, location, eventType, user.getUid());

            eventGroup.addUser(user.getUid(), user.getEmail());

            DatabaseReference newEventsRef = eventsRef.push();
            newEventsRef.setValue(eventGroup);
            newEventsRef.push();

            // Display a message informing the user the new event was successfully created
            Toast.makeText(context, "Event successfully created: " + eventGroup.getEventTitle(), Toast.LENGTH_LONG).show();
            finish();

        }

    }

}