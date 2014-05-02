package com.frostbittmedia.Hiccups.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.frostbittmedia.Hiccups.Activities.LogActivity;
import com.frostbittmedia.Hiccups.R;


public class NoteFragment extends Fragment {

    private EditText noteText;
    private Button noteSaveBtn;
    private InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notefragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        noteText = (EditText)getActivity().findViewById(R.id.noteEditText);
        noteText.setText("");
        noteText.requestFocus();

        imm.showSoftInput(noteText, InputMethodManager.SHOW_IMPLICIT); // Showing keyboard

        noteSaveBtn = (Button)getActivity().findViewById(R.id.noteSaveBtn);
        noteSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = noteText.getText().toString().trim();

                if (note.length() < 255)
                    if (!note.isEmpty()){
                        LogActivity.saveNote(note);

                        imm.hideSoftInputFromWindow(noteText.getWindowToken(), 0); // Hiding keyboard

                        LogActivity.fragmentContainer.setVisibility(View.GONE);
                        LogActivity.listContainer.setVisibility(View.VISIBLE);
                        LogActivity.buttonPanel.setVisibility(View.VISIBLE);

                        try{
                            LogActivity.fragmentManager.getBackStackEntryAt(0);
                            LogActivity.fragmentManager.popBackStack(LogActivity.fragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }catch (IndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                    }
                    else
                        Toast.makeText(getActivity(), "Empty note", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Max 255 characters, current: " + note.length(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
