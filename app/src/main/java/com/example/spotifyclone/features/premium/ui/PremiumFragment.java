package com.example.spotifyclone.features.premium.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.spotifyclone.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PremiumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PremiumFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PremiumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PremiumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PremiumFragment newInstance(String param1, String param2) {
        PremiumFragment fragment = new PremiumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_premium, container, false);

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

        switch(plan) {
            case "mini":
                bundle.putString("plan", "mini");
                bundle.putInt("duration", 1); // 1 month
                break;
            case "individual":
                bundle.putString("plan", "individual");
                bundle.putInt("duration", 3); // 3 months
                break;
            case "student":
                bundle.putString("plan", "student");
                bundle.putInt("duration", 6); // 6 months
                break;
        }

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_nav_premiumfm_to_checkoutfm, bundle);
    }
}