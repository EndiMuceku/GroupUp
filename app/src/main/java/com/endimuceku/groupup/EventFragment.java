package com.endimuceku.groupup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class EventFragment extends Fragment {

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();

        Intent startCreateEventActivityIntent = new Intent(activity, CreateEventActivity.class);

        View view = inflater.inflate(R.layout.fragment_event, container, false);
        ImageView button = (ImageView) view.findViewById(R.id.createEventImageView);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(startCreateEventActivityIntent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}