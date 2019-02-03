package com.example.android.fireapp.testTab.redundant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.fireapp.R;
import com.example.android.fireapp.testTab.MySimpleArrayAdapter;


public class BlankFragment extends Fragment  {

    private ActionMode mActionMode;
    public int selectedItem = -1;

    private ListView listView;
    private Button button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete_layout_test, container, false);



        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2","Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };
        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getActivity(), values);

        listView = view.findViewById(R.id.deleteTestListView);
        button = view.findViewById(R.id.button3test);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "long clicked", Toast.LENGTH_SHORT).show();
                if (mActionMode != null)
                {
                    //if action mode active don t activate it again
                    return false;
                }
                ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

                mActionMode = getActivity().startActionMode(mActionModeCallback1);



                return true;
            }
        });

        listView.setAdapter(adapter);

        return view;

    }


    private ActionMode.Callback mActionModeCallback1 = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
            mode.setTitle("Choose your option");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.option2:
                    Toast.makeText(getActivity(), "Option 2 selected", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        }
    };

    public BlankFragment() {
        // Required empty public constructor
    }



}
