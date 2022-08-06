package com.example.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class frag1 extends Fragment {

    //string list for tasks
    ArrayList<String> tasksPending;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //loading data from shared pref
        loadData();
        ListView listView = (ListView) view.findViewById(R.id.listviewPending);
        Button buttonTask = (Button) view.findViewById(R.id.btnTask);

        //array adapter to show list of tasks
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,tasksPending);
        //adapting list to show any data (if)available from shared pref
        listView.setAdapter(arrayAdapter);

        //add task or delete all task
        buttonTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //alertDialogue popup for task creation
                final AlertDialog.Builder builderAddItem = new AlertDialog.Builder(getContext());
                builderAddItem.setTitle("ENTER TASK");

                //edit text to input task text
                final EditText taskInput = new EditText(getContext());
                taskInput.setInputType(InputType.TYPE_CLASS_TEXT);
                taskInput.setHint("Enter task here...");
                builderAddItem.setView(taskInput);

                //add task
                builderAddItem.setPositiveButton("ADD", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //storing task input in a variable then adding it to tasks(LIST)
                        String task = " "+taskInput.getText().toString();
                        tasksPending.add(task);
                        listView.setAdapter(arrayAdapter);  //"re"adapt the task list into listView

                        //to add to shared pref
                        saveData();
                    }
                });

                //delete all task
                builderAddItem.setNegativeButton("DELETE ALL", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        tasksPending.clear();
                        listView.setAdapter(arrayAdapter);

                        //to add to shared pref
                        saveData();

                    }
                });
                builderAddItem.show();


            }
        });

        //edit or complete task on item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                final AlertDialog.Builder builderListItem = new AlertDialog.Builder(getContext());
                builderListItem.setTitle("Task "+ (index+1));

                //edit task
                builderListItem.setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //nested alert dialogue for task edit
                        final AlertDialog.Builder builderEditTask = new AlertDialog.Builder(getContext());
                        builderEditTask.setTitle("EDIT TASK");

                        final EditText editTask = new EditText(getContext());
                        editTask.setInputType(InputType.TYPE_CLASS_TEXT);
                        editTask.setHint("Edit task here...");
                        builderEditTask.setView(editTask);

                        builderEditTask.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int indexEdit) {

                                String task = " " + editTask.getText().toString();
                                tasksPending.set(index, task);  //using index of actual list item
                                listView.setAdapter(arrayAdapter);

                                //to add to shared pref
                                saveData();

                            }
                        });

                        builderEditTask.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //NOTHING HAPPENS
                            }
                        });
                        builderEditTask.show();
                    }
                });

                //passing completed task to frag2
                builderListItem.setPositiveButton("COMPLETED", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                            //passing data between fragments
                            //passing completed task to fragment manager with a key
                            Bundle task = new Bundle();
                            task.putString("tskCmplt", tasksPending.get(index));
                            getParentFragmentManager().setFragmentResult("fromFrag1", task);

                            //removing completed task from pending list
                            tasksPending.remove(index);
                            listView.setAdapter(arrayAdapter);

                            //to add to shared pref
                            saveData();
                    }
                });
                builderListItem.show();
            }
        });
    }

    public void saveData()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPrefPending", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasksPending);
        editor.putString("tasksPending", json);
        editor.apply();
    }

    public void loadData()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPrefPending", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("tasksPending", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        tasksPending = gson.fromJson(json, type);

        if(tasksPending == null) tasksPending = new ArrayList<>();
    }

}