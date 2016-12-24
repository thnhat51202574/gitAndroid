package layout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.example.a51202_000.testbug.EditprofileAcitivity;
import com.example.a51202_000.testbug.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.Future;

import globalClass.GlobalUserClass;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    GlobalUserClass globalUser;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CircularImageView avatar;
    private TextView txtUserFullName,txtUserNameLogin, txtAddress, txtBrithday,txtPhone;
    private OnFragmentInteractionListener mListener;
    private CircularProgressButton chooseImgBtn;
    private int CHOOSE_FILE_IMAGE = 2;
    private String SERVER_PATH ="http://totnghiep.herokuapp.com";
    String path;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        avatar = (CircularImageView) rootView.findViewById(R.id.avatar);
        txtUserNameLogin = (TextView) rootView.findViewById(R.id.txtUserNameLoginFg);
        txtUserFullName = (TextView) rootView.findViewById(R.id.txtUserNameFg);
        txtAddress = (TextView) rootView.findViewById(R.id.txtAddressFg);
        txtBrithday = (TextView) rootView.findViewById(R.id.txtBirthdayFg);
        txtPhone = (TextView) rootView.findViewById(R.id.txtPhoneFg);


        globalUser = (GlobalUserClass) getActivity().getApplicationContext();

        setContentToView();
//        chooseImgBtn.setIndeterminateProgressMode(true);
//        avatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImgBtn.setVisibility(View.VISIBLE);
//            }
//        });
//        chooseImgBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImgBtn .setProgress(0);
//                pickImage(CHOOSE_FILE_IMAGE);
//            }
//        });

        return rootView;
    }

    public void setContentToView() {

        String Link = globalUser.getCur_user().getAvatarLink();
        Picasso.with(getActivity()).load(Link).into(avatar);
        txtUserNameLogin.setText(globalUser.getCur_user().getName());
        txtUserFullName.setText(globalUser.getCur_user().getFullName());
        txtBrithday.setText(android.text.format.DateFormat.format("dd/MM/yyyy", globalUser.getCur_user().getBirthday()));
        txtAddress.setText(globalUser.getCur_user().getAddress());
        txtPhone.setText(globalUser.getCur_user().getPhone());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode, data );

        if(resultCode != RESULT_OK) {
            return;
        }
        //       Toast.makeText(getActivity().getApplicationContext(),data.getExtras().getString("results").toString(),Toast.LENGTH_LONG).show();
        if(requestCode == 101){
            setContentToView();
        }

    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void startEditProfile() {
        Intent intent = new Intent(getActivity(), EditprofileAcitivity.class);
        startActivityForResult(intent, 101);
//        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
