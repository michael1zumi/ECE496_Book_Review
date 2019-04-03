package com.tess.scarlett.tess_v5;

import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;
import static com.tess.scarlett.tess_v5.R.color.color_grey;

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
    private Button purchase_button;
    private Button favourite_button;
    private Button share_button;
    private SearchView.OnQueryTextListener queryTextListener;
    private String userInput;

    private TessBaseAPI tessBaseAPI;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/tess_v5";



    private static final String Purchase_file = "purchase.ser";
    private static final String Favourite_file = "favourite.ser";

    private String key;
    private String value;
    private File purchase_file;
    private File favourite_file;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchview = view.findViewById(R.id.searchView);
        purchase_button = view.findViewById(R.id.purchase_button);
        favourite_button = view.findViewById(R.id.favourite_button);
        share_button = view.findViewById(R.id.share_button);
        key = ((MainActivity)getActivity()).getBookname();
        value = ((MainActivity)getActivity()).getProductLink()[0];

        purchase_file = new File(getContext().getFilesDir() + "/map.ser");
        favourite_file = new File(getContext().getFilesDir() + "/map2.ser");
//
//        ((MainActivity)getActivity()).color_helper("purchase",purchase_file,key);
//        ((MainActivity)getActivity()).color_helper("favourite",favourite_file,key);

        purchase_button.setOnClickListener(new Button.OnClickListener() { // Then you should add add click listener for your button.
            @Override
            public void onClick(View v) {
                onClick_helper("purchase",purchase_file);
            }
        });

        favourite_button.setOnClickListener(new Button.OnClickListener() { // Then you should add add click listener for your button.
            @Override
            public void onClick(View v) {
                onClick_helper("favourite",favourite_file);
            }
        });

        share_button.setOnClickListener(new Button.OnClickListener() { // Then you should add add click listener for your button.
            @Override
            public void onClick(View v) {
                // share button code here
            }
        });
        queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                //searchview.setQuery("button pressed",false);
                searchview.clearFocus();
                userInput = searchview.getQuery().toString();
                ((MainActivity)getActivity()).showResult(userInput);
                return true;
            }
        };
        searchview.setOnQueryTextListener(queryTextListener);

        return view;
    }

    public void onClick_helper(String filename, File file){
        Map map = new HashMap();
        Map existing_map;
        boolean alreadyExist = false;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        Drawable drawable;

        key = ((MainActivity)getActivity()).getBookname();
        value = ((MainActivity)getActivity()).getProductLink()[0];
        //wirte to file
        try {
            if (!file.exists()){
                map.put(key,value);
                FileOutputStream fos = new FileOutputStream(file);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(map);
                oos.close();
                fos.close();
                //System.out.println("purchase file did not exist before, but now created!\n");
            }
            else{
                //System.out.println("purchase file already exists\n");
                FileInputStream fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                existing_map = (Map) ois.readObject();
                ois.close();
                fis.close();

                FileOutputStream fos = new FileOutputStream(file);
                if (existing_map.containsKey(key)){
                    alreadyExist = true;
                    existing_map.remove(key);
                    fos.close(); //emptying out the file
                    fos = new FileOutputStream(file);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(existing_map);
                    oos.close();
                    fos.close();
                    System.out.println("product name exists!\n");
                }
                else{
                    alreadyExist = false;
                    existing_map.put(key,value);
                    fos.close(); //emptying out the file
                    fos = new FileOutputStream(file);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(existing_map);
                    oos.close();
                    fos.close();
                    System.out.println("product name does not exist!\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        switch(filename){
            case "purchase":
                drawable = getResources().getDrawable(R.drawable.ic_cart).mutate();
                drawable = DrawableCompat.wrap(drawable);
                if (alreadyExist){
                    drawable.setColorFilter(getResources().getColor(R.color.color_grey), PorterDuff.Mode.SRC_ATOP);
                }
                else{
                    drawable.setColorFilter(getResources().getColor(R.color.colorNavi), PorterDuff.Mode.SRC_ATOP);
                }
                purchase_button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                break;
            case "favourite":
                drawable = getResources().getDrawable(R.drawable.ic_favourite).mutate();
                drawable = DrawableCompat.wrap(drawable);
                if (alreadyExist){
                    drawable.setColorFilter(getResources().getColor(R.color.color_grey), PorterDuff.Mode.SRC_ATOP);
                }
                else{
                    drawable.setColorFilter(getResources().getColor(R.color.colorNavi), PorterDuff.Mode.SRC_ATOP);
                }
                favourite_button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                break;
            default:
        }

    }
//    public void color_helper(String filename, File file, String key){
//        Drawable drawable;
//        if (file.exists()){
//            Map existing_map =  new HashMap();
//            try {
//                ObjectInputStream ois;
//                FileInputStream fis = new FileInputStream(file);
//                ois = new ObjectInputStream(fis);
//                existing_map = (Map) ois.readObject();
//                ois.close();
//                fis.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            if (existing_map.containsKey(key)){
//                switch (filename){
//                    case "purchase":
//                        drawable = getResources().getDrawable(R.drawable.ic_cart).mutate();
//                        drawable = DrawableCompat.wrap(drawable);
//                        drawable.setColorFilter(getResources().getColor(R.color.colorNavi), PorterDuff.Mode.SRC_ATOP);
//                        purchase_button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
//                        break;
//                    case "favourite":
//                        drawable = getResources().getDrawable(R.drawable.ic_favourite).mutate();
//                        drawable = DrawableCompat.wrap(drawable);
//                        drawable.setColorFilter(getResources().getColor(R.color.colorNavi), PorterDuff.Mode.SRC_ATOP);
//                        favourite_button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
//                        break;
//                    default:
//
//                }
//            }
//
//        }
//    }

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
