package com.example.spotifyclone.features.authentication.ui.signup_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.ui.SignupActivity;
import com.example.spotifyclone.features.authentication.viewmodel.SignupVMFactory;
import com.example.spotifyclone.features.authentication.viewmodel.SignupViewModel;

import java.util.Calendar;

public class BirthdayFragment extends Fragment {
    private NumberPicker dayPicker, monthPicker, yearPicker;

    public BirthdayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_birthday, container, false);

        dayPicker = view.findViewById(R.id.dayPicker);
        monthPicker = view.findViewById(R.id.monthPicker);
        yearPicker = view.findViewById(R.id.yearPicker);
        Button continueBtn = view.findViewById(R.id.btnContinue);
        ImageView backBtn = view.findViewById(R.id.btnBack);
        ImageView closeBtn = view.findViewById(R.id.btnClose);

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(2021);

        dayPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            handleDatePickerValuesChanged(dayPicker.getValue(), monthPicker.getValue(), yearPicker.getValue());
        });

        monthPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            handleDatePickerValuesChanged(dayPicker.getValue(), monthPicker.getValue(), yearPicker.getValue());
        });

        yearPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            handleDatePickerValuesChanged(dayPicker.getValue(), monthPicker.getValue(), yearPicker.getValue());
        });

        backBtn.setOnClickListener(v -> {
            ((SignupActivity) requireActivity()).previousStep();
        });

        closeBtn.setOnClickListener(v -> {
            ((SignupActivity) requireActivity()).closeSignup();
        });

        continueBtn.setOnClickListener(v -> {
            int day = dayPicker.getValue();
            int month = monthPicker.getValue();
            int year = yearPicker.getValue();

            if (!isValidAge(year, month, day)) {
                Toast.makeText(requireContext(), "You must be at least 13 years old to use Spotify", Toast.LENGTH_SHORT).show();
                return;
            }

            ((SignupActivity) requireActivity()).nextStep();
        });

        return view;
    }

    private void handleDatePickerValuesChanged(int dayVal, int mothVal, int yearVal) {
        String day = String.valueOf(dayVal);
        String month = String.valueOf(mothVal);
        String year = String.valueOf(yearVal);

        if (day.length() == 1) {
            day = "0" + day;
        }

        if (month.length() == 1) {
            month = "0" + month;
        }

        String dateOfBirth = day + "/" + month + "/" + year;
        ((SignupActivity) requireActivity()).signupViewModel.setDataOfBirth(dateOfBirth);
    }

    private boolean isValidAge(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.set(year, month, day);

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age >= 13;
    }
}
