package com.example.ToDoList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Privacy extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bu fragment için layout'u inflate et
        View view = inflater.inflate(R.layout.fragment_privacy, container, false);

        return view;
    }
}
