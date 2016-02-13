package com.sagarnileshshah.nytsearchapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import com.sagarnileshshah.nytsearchapp.R;
import com.sagarnileshshah.nytsearchapp.models.Filter;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FilterDialogFragment extends DialogFragment {

    @Bind(R.id.datePicker)
    DatePicker datePicker;

    @Bind(R.id.cbArts)
    CheckBox cbArts;

    @Bind(R.id.cbFashion)
    CheckBox cbFashion;

    @Bind(R.id.cbSports)
    CheckBox cbSports;

    @Bind(R.id.spinnerSortBy)
    Spinner spinnerSortBy;

    @Bind(R.id.btnClose)
    Button btnClose;

    @Bind(R.id.btnApplyFilter)
    Button btnApplyFilter;

    @Bind(R.id.switchStartDate)
    Switch switchStartDate;

    @Bind(R.id.switchNewsDesk)
    Switch switchNewsDesk;

    @Bind(R.id.switchSortBy)
    Switch switchSortBy;

    Filter mFilter;

    private OnFilterDialogFragmentInteractionListener mActivityListener;

    public interface OnFilterDialogFragmentInteractionListener {
        void applyFilter(Filter filter);
    }

    public FilterDialogFragment() {
        // Required empty public constructor
    }

    public static FilterDialogFragment newInstance(Filter filter) {
        FilterDialogFragment fragment = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("filter", Parcels.wrap(filter));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilter = (Filter) Parcels.unwrap(getArguments().getParcelable("filter"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().setTitle("Filters");
        Calendar currDate = Calendar.getInstance();
        datePicker.init(currDate.get(Calendar.YEAR), currDate.get(Calendar.MONTH), currDate.get(Calendar.DATE), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilter();
                dismiss();
            }
        });

        switchStartDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    datePicker.setVisibility(View.VISIBLE);
                else
                    datePicker.setVisibility(View.GONE);
            }
        });

        switchNewsDesk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbArts.setVisibility(View.VISIBLE);
                    cbFashion.setVisibility(View.VISIBLE);
                    cbSports.setVisibility(View.VISIBLE);
                } else {
                    cbArts.setVisibility(View.GONE);
                    cbFashion.setVisibility(View.GONE);
                    cbSports.setVisibility(View.GONE);
                }
            }
        });

        switchSortBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinnerSortBy.setVisibility(View.VISIBLE);
                } else {
                    spinnerSortBy.setVisibility(View.GONE);
                }
            }
        });

        renderFilter();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFilterDialogFragmentInteractionListener) {
            mActivityListener = (OnFilterDialogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFilterDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityListener = null;
    }

    private void renderFilter() {
        if (!mFilter.getStartDate().equals("")) {
            try {
                int[] dateArray = mFilter.getStartDateArray();
                datePicker.updateDate(dateArray[0], dateArray[1], dateArray[2]);
                switchStartDate.setChecked(true);
                datePicker.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                Calendar currDate = Calendar.getInstance();
                datePicker.updateDate(currDate.get(Calendar.YEAR), currDate.get(Calendar.MONTH), currDate.get(Calendar.DAY_OF_MONTH));
                e.printStackTrace();
            }
        } else {
            switchStartDate.setChecked(false);
            datePicker.setVisibility(View.GONE);
        }

        ArrayList<String> newsDesk = mFilter.getNewsDesk();
        if (newsDesk.size() > 0) {
            switchNewsDesk.setChecked(true);
            cbArts.setVisibility(View.VISIBLE);
            cbFashion.setVisibility(View.VISIBLE);
            cbSports.setVisibility(View.VISIBLE);
            cbArts.setChecked(false);
            cbFashion.setChecked(false);
            cbSports.setChecked(false);
            for (String choice : newsDesk) {
                switch (choice) {
                    case "Arts":
                        cbArts.setChecked(true);
                        break;
                    case "Fashion & Style":
                        cbFashion.setChecked(true);
                        break;
                    case "Sports":
                        cbSports.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        } else {
            switchNewsDesk.setChecked(false);
            cbArts.setVisibility(View.GONE);
            cbFashion.setVisibility(View.GONE);
            cbSports.setVisibility(View.GONE);
        }
        if (!mFilter.getSortBy().equals("")) {
            switchSortBy.setChecked(true);
            spinnerSortBy.setVisibility(View.VISIBLE);
            int index = 0;
            SpinnerAdapter spinnerAdapter = spinnerSortBy.getAdapter();
            for (int i = 0; i < spinnerAdapter.getCount(); i++) {
                if (spinnerAdapter.getItem(i).equals(mFilter.getSortBy())) {
                    index = i;
                    break;
                }
            }
            spinnerSortBy.setSelection(index);
        } else {
            switchSortBy.setChecked(false);
            spinnerSortBy.setVisibility(View.GONE);
        }
    }

    private void updateFilter() {
        if (switchStartDate.isChecked())
            mFilter.setStartDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        else
            mFilter.setStartDate("");

        if (switchNewsDesk.isChecked()) {
            mFilter.getNewsDesk().clear();
            if (cbArts.isChecked())
                mFilter.getNewsDesk().add(cbArts.getText().toString());
            if (cbFashion.isChecked())
                mFilter.getNewsDesk().add(cbFashion.getText().toString());
            if (cbSports.isChecked())
                mFilter.getNewsDesk().add(cbSports.getText().toString());
        } else {
            mFilter.getNewsDesk().clear();
        }

        if (switchSortBy.isChecked())
            mFilter.setSortBy(spinnerSortBy.getSelectedItem().toString());
        else
            mFilter.setSortBy("");

        mActivityListener.applyFilter(mFilter);
    }

}
