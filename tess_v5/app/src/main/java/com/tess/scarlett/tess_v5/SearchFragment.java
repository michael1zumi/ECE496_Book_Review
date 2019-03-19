package com.tess.scarlett.tess_v5;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.tess.scarlett.tess_v5.MainActivity;

import java.io.FileNotFoundException;
import java.io.IOException;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SearchView searchview;
    private SearchView.OnQueryTextListener queryTextListener;
    private String userInput;

    private TessBaseAPI tessBaseAPI;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/tess_v5";



    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        //((MainActivity)getActivity()).hideUpButton();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchview = view.findViewById(R.id.searchView);

        queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                //searchview.setQuery("button pressed",false);
                ((MainActivity)getActivity()).showResult();
                searchview.clearFocus();
                userInput = searchview.getQuery().toString();
                return true;
            }
        };
        // Inflate the layout for this fragment

        /*
        //button for the old gallery access; now using tabs in mainactivity
        Button button;
        button = (Button) view.findViewById(R.id.ViewGallery);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();

            }
        });
        */
        searchview.setOnQueryTextListener(queryTextListener);

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        /*
        //requestCode = 1 is the old gallery access button; now mvoed to mainactivity
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){

            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    //String path = saveImage(bitmap);
                    Toast.makeText(getActivity().getApplicationContext(),"Image Saved Sueecssfully!", Toast.LENGTH_SHORT).show();
                    //imageview.setImageBitmap(bitmap);
                    //String result = this.getText(bitmap);

                    String result = ((MainActivity)getActivity()).getText(bitmap);
                    System.out.println("return string is "+result +"\n");


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),"Failed!", Toast.LENGTH_SHORT).show();
                }
                System.out.println("\n????????????request 1 fragment???????????????");
            }
        }
        else{
            System.out.println("\n???????????????????else fragment???????????????????~~");
            Toast.makeText(getActivity().getApplicationContext(),"Image problem", Toast.LENGTH_SHORT).show();
        }
        */

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
