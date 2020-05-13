package com.example.sih;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class DashboardFragment extends Fragment implements AdapterView.OnItemSelectedListener,EntriesFragment.OnFragmentInteractionListener, ExitsFragment.OnFragmentInteractionListener{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);



        // Creating spinner for gates
        Spinner spinner = view.findViewById(R.id.spinner_gates);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.gates, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Spinner for showing data of that time
        Spinner time_s = view.findViewById(R.id.spinner_time);
        ArrayAdapter<CharSequence> adapter_t = ArrayAdapter.createFromResource(this.getActivity(), R.array.time, android.R.layout.simple_spinner_item);
        adapter_t.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_s.setAdapter(adapter_t);
        time_s.setOnItemSelectedListener(this);




        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = view.findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        PagerAdapter adapter_p = new PagerAdapter(getChildFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter_p);


        // Give the TabLayout the ViewPager
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);


        return view;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
