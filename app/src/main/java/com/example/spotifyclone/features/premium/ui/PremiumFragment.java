package com.example.spotifyclone.features.premium.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.premium.model.Plan;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class PremiumFragment extends Fragment {
    ArrayList<Plan> plans = new ArrayList<>();

    public PremiumFragment() {
    }

    public static PremiumFragment newInstance(String param1, String param2) {
        PremiumFragment fragment = new PremiumFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_premium, container, false);

        // Create a list of plans
        plans.add(new Plan("Premium Mini", "mini", getString(R.string.mini_plan_description), "", 10500, 10500, 1));
        plans.add(new Plan("Premium Individual","individual", getString(R.string.individual_plan_description), getString(R.string.individual_plan_term), 0, 59000, 3));
        plans.add(new Plan("Premium Student","student", getString(R.string.student_plan_description), getString(R.string.student_plan_term), 29500, 29500, 2));

        Button btnPremiumMini = view.findViewById(R.id.btn_get_premium_mini);
        Button btnPremiumIndividual = view.findViewById(R.id.btn_try_individual);
        Button btnPremiumStudent = view.findViewById(R.id.btn_try_student);

        btnPremiumMini.setOnClickListener(v -> navigateToCheckoutPage("mini"));
        btnPremiumIndividual.setOnClickListener(v -> navigateToCheckoutPage("individual"));
        btnPremiumStudent.setOnClickListener(v -> navigateToCheckoutPage("student"));

        return view;
    }

    private void navigateToCheckoutPage(String plan) {
        // Navigate to checkout page
        Bundle bundle = new Bundle();

        switch (plan) {
            case "mini":
                String miniPlanJson = new Gson().toJson(plans.get(0));
                bundle.putString("planJson", miniPlanJson);
                break;
            case "individual":
                String individualPlanJson = new Gson().toJson(plans.get(1));
                bundle.putString("planJson", individualPlanJson);
                break;
            case "student":
                String studentPlanJson = new Gson().toJson(plans.get(2));
                bundle.putString("planJson", studentPlanJson);
                break;
        }

        NavDirections action = PremiumFragmentDirections.actionNavPremiumfmToCheckoutfm(
                bundle.getString("planJson")
        );
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(action);
    }
}