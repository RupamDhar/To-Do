package com.example.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class frag2 extends Fragment {

    ArrayList<String> tasksComplete;
    private static final String SHARED_PREF_COMPLETE="sharedPrefComplete";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //load data from shared pref
        loadData();
        ListView listView = (ListView) view.findViewById(R.id.listviewComplete);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,tasksComplete);
        //adapting list to show any data (if)available from shared pref
        listView.setAdapter(arrayAdapter);

        //receiving data from fragment manager from frag1
        getParentFragmentManager().setFragmentResultListener("fromFrag1", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                tasksComplete.add(result.getString("tskCmplt"));
                listView.setAdapter(arrayAdapter);

                //to add to shared pref
                saveData();

            }
        });

        //deleting completed tasks
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                final AlertDialog.Builder builderDelCmpltTask = new AlertDialog.Builder(getContext());
                builderDelCmpltTask.setTitle("TASK COMPLETED");

                builderDelCmpltTask.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        tasksComplete.remove(index);
                        listView.setAdapter(arrayAdapter);

                        //to add to shared pref
                        saveData();

                    }
                });

                builderDelCmpltTask.setNegativeButton("DELETE ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        tasksComplete.clear();
                        listView.setAdapter(arrayAdapter);

                        //to add to shared pref
                        saveData();

                    }
                });
                builderDelCmpltTask.show();
            }
        });
    }

    //save data to shared pref
    public void saveData()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREF_COMPLETE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasksComplete);   //converting from gson to json to save to shared pref
        editor.putString("tasksComplete", json);
        editor.apply();
    }

    //load data from shared pref
    public void loadData()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREF_COMPLETE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("tasksComplete", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        tasksComplete = gson.fromJson(json, type);  //converting from json to gson to save to tasksComplete

        if(tasksComplete == null) tasksComplete = new ArrayList<>();
    }

}