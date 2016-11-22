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

import com.dd.CircularProgressButton;
import com.example.a51202_000.testbug.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;

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
    private ImageView avatar;
    private TextView txtUserFullName,txtUserNameLogin, txtAddress, txtBrithday,txtPhone;
    private OnFragmentInteractionListener mListener;
    private CircularProgressButton chooseImgBtn;
    private int CHOOSE_FILE_IMAGE = 1;
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
        avatar = (ImageView) rootView.findViewById(R.id.avatar);
        txtUserNameLogin = (TextView) rootView.findViewById(R.id.txtUserNameLoginFg);
        txtUserFullName = (TextView) rootView.findViewById(R.id.txtUserNameFg);
        txtAddress = (TextView) rootView.findViewById(R.id.txtAddressFg);
        txtBrithday = (TextView) rootView.findViewById(R.id.txtBirthdayFg);
        txtPhone = (TextView) rootView.findViewById(R.id.txtPhoneFg);


        globalUser = (GlobalUserClass) getActivity().getApplicationContext();
        chooseImgBtn  = (CircularProgressButton) rootView.findViewById(R.id.circularButton1);

        setContentToView();
        chooseImgBtn.setIndeterminateProgressMode(true);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImgBtn.setVisibility(View.VISIBLE);
            }
        });
        chooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImgBtn .setProgress(0);
                pickImage(CHOOSE_FILE_IMAGE);
            }
        });

        return rootView;
    }

    private void setContentToView() {
        Ion.with(avatar)
//                .placeholder(R.drawable.placeholder_image)
//                .error(R.drawable.error_image)
//                .animateLoad(spinAnimation)
//                .animateIn(fadeInAnimation)
                .centerCrop()
                .load(SERVER_PATH + globalUser.getCur_user().getAvatarLink());
        txtUserNameLogin.setText(globalUser.getCur_user().getName());
        txtUserFullName.setText(globalUser.getCur_user().getFullName());
        txtBrithday.setText(globalUser.getCur_user().getBirthday().toString());
        txtAddress.setText(globalUser.getCur_user().getAddress());
        txtPhone.setText(globalUser.getCur_user().getPhone());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CHOOSE_FILE_IMAGE) {
                final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image

                final Bitmap newProfilePic = extras.getParcelable("data");
                Uri imageURI = getImageUri(getActivity().getApplicationContext(),newProfilePic);
                path = getPathFromURI(imageURI);
                final File f = new File(path);
                Future uploading = Ion.with(getActivity().getApplicationContext())
                        .load("http://totnghiep.herokuapp.com/upload")
                        .progress(new ProgressCallback() {@Override
                            public void onProgress(long downloaded, long total) {
                                int percent =(int)(downloaded*100/total);
                                Log.d("TAG","" + percent);
                                chooseImgBtn .setProgress(percent-1);
                            }
                        })
                        .setMultipartParameter("_id", globalUser.getCur_user().get_id())
                        .setMultipartFile("image", f)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                    avatar.setImageBitmap(newProfilePic);
                                    chooseImgBtn .setProgress(100);
                                    deleteImage(f);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            chooseImgBtn.setVisibility(View.GONE);
                                            chooseImgBtn .setProgress(0);
                                        }
                                    }, 1000);
                            }
                        });

            }
        }
    }
    public void deleteImage(File fdelete) {
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.e("-->", "file Deleted :");
            } else {
                Log.e("-->", "file not Deleted :");
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    public void pickImage(int req_code) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 512);
        intent.putExtra("outputY", 512);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, req_code);
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
