package com.tess.scarlett.tess_v5;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecentScansFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecentScansFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentScansFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RecentScansFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentScansFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentScansFragment newInstance(String param1, String param2) {
        RecentScansFragment fragment = new RecentScansFragment();
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
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.addToBackStack(null);
        ((MainActivity)getActivity()).showUpButton();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recent_scans, container, false);

        File history_file = new File(getContext().getFilesDir() + "/map3.ser");

        try {
            FileInputStream fis = new FileInputStream(history_file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Map existing_map = (Map) ois.readObject();
            if (existing_map.size()==0){
                RelativeLayout layout = view.findViewById(R.id.recentScans);
                layout.setVisibility(View.VISIBLE);
                layout = view.findViewById(R.id.recentScansResults);
                layout.setVisibility(View.GONE);
            }
            else{
                RelativeLayout layout = view.findViewById(R.id.recentScans);
                layout.setVisibility(View.GONE);
                layout = view.findViewById(R.id.recentScansResults);
                layout.setVisibility(View.VISIBLE);
                TextView text = view.findViewById(R.id.history_field);
                Iterator it = existing_map.keySet().iterator();
                int i =0;
                while (it.hasNext())
                {
                    i += 1;
                    Object key = it.next();
                    text.append(i+". "+String.valueOf(key).replaceAll("\\+", " ").toUpperCase()+"\n");

                }
                text.append("\n\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
