package com.selada.invesproperti.presentation.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.selada.invesproperti.R;
import com.selada.invesproperti.SplashScreen;
import com.selada.invesproperti.api.ApiCore;
import com.selada.invesproperti.model.response.ResponseUserProfile;
import com.selada.invesproperti.presentation.profile.bank.AkunBankActivity;
import com.selada.invesproperti.presentation.profile.cs.CallCenterActivity;
import com.selada.invesproperti.presentation.profile.detail.DetailProfileActivity;
import com.selada.invesproperti.presentation.profile.disclaimer.DisclaimerActivity;
import com.selada.invesproperti.presentation.profile.kebijakan.KebijakanPrivasiActivity;
import com.selada.invesproperti.presentation.profile.ketentuan.SyaratKetentuanActivity;
import com.selada.invesproperti.presentation.profile.mitigasi.MitigasiResikoActivity;
import com.selada.invesproperti.presentation.questioner.QuestionerActivity;
import com.selada.invesproperti.presentation.verification.VerificationActivity;
import com.selada.invesproperti.util.Constant;
import com.selada.invesproperti.util.Loading;
import com.selada.invesproperti.util.MethodUtil;
import com.selada.invesproperti.util.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    @BindView(R.id.tv_profile_name)
    TextView tv_profile_name;
    @BindView(R.id.tv_profile_email)
    TextView tv_profile_email;
    @BindView(R.id.tv_profile_type)
    TextView tv_profile_type;
    @BindView(R.id.btn_profile)
    LinearLayout btn_profile;
    @BindView(R.id.btn_akun_bank)
    LinearLayout btn_akun_bank;
    @BindView(R.id.line_profile)
    ImageView line_profile;
    @BindView(R.id.line_akun_bank)
    ImageView line_akun_bank;
    @BindView(R.id.frameVerifikasi)
    FrameLayout frameVerifikasi;
    @BindView(R.id.tv_title_verification_notif)
    TextView tv_title_verification_notif;
    @BindView(R.id.img_verified)
    ImageView img_verified;
    @BindView(R.id.frame_profile_type)
    FrameLayout frame_profile_type;
    @BindView(R.id.img_profile)
    CircleImageView img_profile;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int IMAGE_PICK_REQUEST = 12345;

    @OnClick(R.id.btn_signout)
    void onClickbtnSignOut(){
        signOut();
    }

    @OnClick(R.id.btn_cs)
    void onClickCs(){
        Intent intent = new Intent(requireActivity(), CallCenterActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.btn_profile)
    void onClickbtn_profile(){
        Intent intent = new Intent(requireActivity(), DetailProfileActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.btn_disclaimer)
    void onClickbtn_disclaimer(){
        Intent intent = new Intent(requireActivity(), DisclaimerActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.btn_kebijakan)
    void onClickbtn_kebijakan(){
        Intent intent = new Intent(requireActivity(), KebijakanPrivasiActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.btn_syarat_ketentuan)
    void onClickbtn_syarat_ketentuan(){
        Intent intent = new Intent(requireActivity(), SyaratKetentuanActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.btn_mitigasi)
    void onClickbtn_mitigasi(){
        Intent intent = new Intent(requireActivity(), MitigasiResikoActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.btn_bantuan)
    void onClickBtnBantuan(){
        Intent intent = new Intent(requireActivity(), CallCenterActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.btn_akun_bank)
    void onClickBtnBank(){
        Intent intent = new Intent(requireActivity(), AkunBankActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.frame_profile_pic)
    void onClickFramePic(){
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        displayChoiceDialog();
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        configureGoogleSignIn();
        setContentHome();
    }

    @SuppressLint("SetTextI18n")
    private void setContentHome() {
        if(PreferenceManager.getUserProfile()!=null){
            ResponseUserProfile userProfile = PreferenceManager.getUserProfile();
            if (userProfile.getProfilePicture()!=null) {
                try {
                    byte[] decodedString = Base64.decode(Objects.requireNonNull(userProfile).getProfilePicture(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    img_profile.setImageBitmap(decodedByte);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        tv_profile_name.setText(PreferenceManager.getFullname());
        tv_profile_email.setText(PreferenceManager.getEmail());

        switch (PreferenceManager.getUserStatus()){
            case Constant.GUEST:
                btn_profile.setVisibility(View.GONE);
                btn_akun_bank.setVisibility(View.GONE);
                line_profile.setVisibility(View.GONE);
                line_akun_bank.setVisibility(View.GONE);
                img_verified.setVisibility(View.GONE);
                frame_profile_type.setBackground(getResources().getDrawable(R.drawable.bg_round_profile_type));
                tv_profile_type.setText("Guest");
                tv_profile_type.setTextColor(getResources().getColor(R.color.black_primary));
                frameVerifikasi.setVisibility(View.VISIBLE);
                tv_title_verification_notif.setText("Verifikasi akun anda sekarang!");
                frameVerifikasi.setOnClickListener(view -> {
                    Intent intent = new Intent(requireActivity(), VerificationActivity.class);
                    startActivity(intent);
                });
                break;
            case Constant.ON_VERIFICATION:
                btn_profile.setVisibility(View.GONE);
                btn_akun_bank.setVisibility(View.GONE);
                line_profile.setVisibility(View.GONE);
                line_akun_bank.setVisibility(View.GONE);
                img_verified.setVisibility(View.GONE);
                frame_profile_type.setBackground(getResources().getDrawable(R.drawable.bg_round_profile_type));
                tv_profile_type.setText("Guest");
                tv_profile_type.setTextColor(getResources().getColor(R.color.black_primary));
                frameVerifikasi.setVisibility(View.VISIBLE);
                tv_title_verification_notif.setText("Akun sedang dalam proses verifikasi data");
                frameVerifikasi.setOnClickListener(view -> {});
                break;
            case Constant.INVESTOR:
                btn_profile.setVisibility(View.VISIBLE);
                btn_akun_bank.setVisibility(View.VISIBLE);
                line_profile.setVisibility(View.VISIBLE);
                line_akun_bank.setVisibility(View.VISIBLE);
                img_verified.setVisibility(View.VISIBLE);
                frame_profile_type.setBackground(getResources().getDrawable(R.drawable.bg_round_profile_type_green));
                tv_profile_type.setText("Investor");
                tv_profile_type.setTextColor(getResources().getColor(R.color.white));
                if (PreferenceManager.isAlreadyQuesioner()){
                    frameVerifikasi.setVisibility(View.GONE);
                } else {
                    tv_title_verification_notif.setText("Akun anda terverifikasi. Isi quesioner.");
                    frameVerifikasi.setVisibility(View.VISIBLE);
                    frameVerifikasi.setOnClickListener(view -> {
                        Intent intent = new Intent(requireActivity(), QuestionerActivity.class);
                        startActivity(intent);
                    });
                }
                break;
            case Constant.PRODUCT_OWNER:
                btn_profile.setVisibility(View.VISIBLE);
                btn_akun_bank.setVisibility(View.VISIBLE);
                line_profile.setVisibility(View.VISIBLE);
                line_akun_bank.setVisibility(View.VISIBLE);
                img_verified.setVisibility(View.VISIBLE);
                frame_profile_type.setBackground(getResources().getDrawable(R.drawable.bg_round_profile_type_green));
                tv_profile_type.setText("Product Owner");
                tv_profile_type.setTextColor(getResources().getColor(R.color.white));
                frameVerifikasi.setVisibility(View.GONE);
                break;
        }
    }

    private void signOut() {
        Dialog dialog = MethodUtil.getDialogCart(R.layout.dialog_logout, requireContext());
        CardView btnLogout = dialog.findViewById(R.id.btnLogout);
        ImageView btnClose = dialog.findViewById(R.id.imgClose);

        btnLogout.setOnClickListener(view -> {

            switch (PreferenceManager.getLoginFrom()){
                case Constant.LOGIN_FROM_GOOGLE:
                    mGoogleSignInClient.signOut()
                            .addOnCompleteListener(requireActivity(), task -> PreferenceManager.logOut());
                    startActivity(new Intent(requireContext(), SplashScreen.class));
                    requireActivity().finish();
                    break;
                case Constant.LOGIN_FROM_FACEBOOK:
                    FirebaseAuth.getInstance().signOut();
                    PreferenceManager.logOut();
                    startActivity(new Intent(requireContext(), SplashScreen.class));
                    requireActivity().finish();
                    break;
                case Constant.LOGIN_FROM_EMAIL:
                    PreferenceManager.logOut();
                    startActivity(new Intent(requireContext(), SplashScreen.class));
                    requireActivity().finish();
                    break;
            }

        });

        btnClose.setOnClickListener(view -> dialog.dismiss());
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_PICK_REQUEST) {
            if(resultCode == RESULT_OK&&data!=null) {
                Uri selectedImageUri = data.getData();
                if(selectedImageUri!=null){
                    img_profile.setImageURI(selectedImageUri);
                    String imagePath = getRealPathFromURI(requireContext(), selectedImageUri);
                    if(imagePath!=null){
                        File file = new File(imagePath);
                        Log.i("Register","Nombre del archivo "+file.getName());
                        RequestBody requestFile =
                                RequestBody.create(MediaType.parse("multipart/form-data"), file);

                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("ProfilePicture", file.getName(), requestFile)
                                .build();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        updateProfilePicture(requestBody, bitmap);
                    }
                } else {
                    Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    Objects.requireNonNull(bitmap).compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("ProfilePicture", "photo.jpeg", RequestBody.create(MediaType.parse("image/jpeg"), byteArrayOutputStream.toByteArray()))
                            .build();

                    updateProfilePicture(requestBody, bitmap);
                }
            } else {
                Log.d("==>","Operation canceled!");
            }
        }
        else if (requestCode==0){
            if(resultCode == RESULT_OK){
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(
                        Intent.createChooser(intent, "Select profile picture"), IMAGE_PICK_REQUEST);
            }
        }
    }

    private void updateProfilePicture(RequestBody requestBody, Bitmap bitmap) {
        Loading.show(requireActivity());
        ApiCore.apiInterface().updateProfilePicture(requestBody, PreferenceManager.getSessionToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Loading.hide(requireActivity());
                try {
                    if (response.isSuccessful()){
                        Toast.makeText(requireActivity(), "Berhasil mengganti avatar", Toast.LENGTH_SHORT).show();
                        img_profile.setImageBitmap(bitmap);
                    } else {
                        MethodUtil.getErrorMessage(response.errorBody(), requireActivity());
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    MethodUtil.showSnackBar(requireActivity(), "Terjadi kesalahan, Silahkan coba beberapa saat lagi");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Loading.hide(requireActivity());
                t.printStackTrace();
            }
        });
    }

    private void displayChoiceDialog() {
        String choiceString[] = new String[] {"Gallery" ,"Camera"};
        AlertDialog.Builder dialog= new AlertDialog.Builder(requireActivity());
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Select image from");
        dialog.setItems(choiceString,
                (dialog1, which) -> {
                    Intent intent = null;
                    if (which == 0) {
                        intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                Intent.createChooser(intent, "Select profile picture"), IMAGE_PICK_REQUEST);
                    } else {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.CAMERA}, 0);
                        } else {
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select profile picture"), IMAGE_PICK_REQUEST);
                        }
                    }

                }).show();
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
