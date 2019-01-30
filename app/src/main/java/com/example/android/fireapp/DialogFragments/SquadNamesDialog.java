package com.example.android.fireapp.DialogFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.android.fireapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.webianks.library.scroll_choice.ScrollChoice;
import java.util.ArrayList;
import java.util.List;
public class SquadNamesDialog extends DialogFragment  {


    public SquadNamesDialog(){

    }
    private ScrollChoice scrollChoice;
    private FirebaseFirestore mFirebaseFirestore;
    private List<String> squadNames = new ArrayList<>();

    public interface SquadNamesPickerDialogInput{
        void onSquadNamesSelected(String squadName);
    }

    public SquadNamesDialog.SquadNamesPickerDialogInput mSquadNamesPickerInput;

    public void setSquadNamesPickerDialogInput(SquadNamesDialog.SquadNamesPickerDialogInput squadNamesPicker){
        this.mSquadNamesPickerInput = squadNamesPicker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.training_type, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        scrollChoice =  view.findViewById(R.id.scroll_choice);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = mFirebaseFirestore.collection("Squad Names");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        squadNames.add(document.getString("name").toString());
                    }

                }

                scrollChoice.addItems(squadNames,1);


                scrollChoice.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                        getDialog().dismiss();
                        mSquadNamesPickerInput.onSquadNamesSelected(name); //Changed

                    }
                });
            }
        });


    /*    List<String> data = new ArrayList<>();
        data.add("Swim");
        data.add("Run");
        data.add("Hike");
        data.add("Sail");
        data.add("Walk");
        data.add("Gym");
        data.add("Jog");
*/





        //  for (String toyName : toyNames) {
        //       textViewTrainingType.append(toyName + "\n\n\n");
        //  }
    }

}


