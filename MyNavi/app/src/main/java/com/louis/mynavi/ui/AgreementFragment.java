package com.louis.mynavi.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.louis.mynavi.databinding.FragmentAgreementBinding;
import com.louis.mynavi.navi.FlowManager;
import com.louis.mynavi.navi.PageNavigator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgreementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgreementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentAgreementBinding binding;

    public AgreementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgreementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgreementFragment newInstance(String param1, String param2) {
        AgreementFragment fragment = new AgreementFragment();
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
        binding = FragmentAgreementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnBack.setOnClickListener(v -> {
            PageNavigator.getInstance().navigateBack();
        });
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                PageNavigator.getInstance().markNodeCompleted("AgreementFragment");
//                PageNavigator.getInstance().navigateToNext(true);
                PreferenceManager.getDefaultSharedPreferences(binding.getRoot().getContext()).edit().putBoolean(("isAgreed"), true).apply();
//                PageNavigator.getInstance().navigateToNext(true);
                NavManager.getInstance().navToNext(getParentFragmentManager());
            }
        });

        binding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                PageNavigator.getInstance().markNodeCompleted("AgreementFragment");
//                PageNavigator.getInstance().navigateToNext(true);
                NavManager.getInstance().agreementCompleted = true;
//                PageNavigator.getInstance().navigateToNext(true);
                NavManager.getInstance().navToNext(getParentFragmentManager());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}