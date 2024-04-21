package com.ashstudios.safana.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.ashstudios.safana.R;
import com.ashstudios.safana.activities.SupervisorDashboard;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;


/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetSortFragment extends BottomSheetDialogFragment {

    Button button;


    public BottomSheetSortFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_bottom_sheet_sort, container, false);
        final Chip chip_name = view.findViewById(R.id.chip_sort_name);
        final Chip chip_male = view.findViewById(R.id.chip_filter_male);
        final Chip chip_female = view.findViewById(R.id.chip_filter_female);

        // for remembering the sorting. Otherwise default sorting is always displayed not the selected one
        Bundle sorting = getArguments();
        if( !sorting.isEmpty() )
        {
            chip_name.setChecked(sorting.getBoolean("nameChip"));
            chip_male.setChecked(sorting.getBoolean("maleChip"));
            chip_female.setChecked(sorting.getBoolean("femaleChip"));

            if(chip_name.isChecked())
                chip_name.setChipIcon(getResources().getDrawable(R.drawable.ic_sort_by_attributes));
            else
                chip_name.setChipIcon(getResources().getDrawable(R.drawable.ic_sort_by_attributes_interface_button_option));
        }


        chip_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    chip_name.setChipIcon(getResources().getDrawable(R.drawable.ic_sort_by_attributes));
                }
                else
                {
                    chip_name.setChipIcon(getResources().getDrawable(R.drawable.ic_sort_by_attributes_interface_button_option));
                }
            }
        });


        button = view.findViewById(R.id.apply_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting sorting data;
                boolean nameChip = chip_name.isChecked();
                boolean maleChip = (chip_male).isChecked();
                boolean femaleChip = (chip_female).isChecked();

                Bundle b = new Bundle();
                b.putBoolean("nameChip",nameChip);
                b.putBoolean("maleChip",maleChip);
                b.putBoolean("femaleChip",femaleChip);

                ((SupervisorDashboard)getActivity()).onWorkerDetailsSortingChanged(b);
                dismiss();
            }
        });

        return view;
    }

}