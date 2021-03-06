package com.selada.invesproperti.presentation.verification;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.selada.invesproperti.R;
import com.selada.invesproperti.api.ApiCore;
import com.selada.invesproperti.model.UserVerification;
import com.selada.invesproperti.model.request.SubmitProductRequest;
import com.selada.invesproperti.model.response.Education;
import com.selada.invesproperti.model.response.Occupation;
import com.selada.invesproperti.util.Loading;
import com.selada.invesproperti.util.MethodUtil;
import com.selada.invesproperti.util.PreferenceManager;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationData1Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.rb_laki)
    RadioButton rb_laki;
    @BindView(R.id.rb_perempuan)
    RadioButton rb_perempuan;
    @BindView(R.id.et_nama_lengkap)
    EditText et_nama_lengkap;
    @BindView(R.id.et_tempat_lahir)
    EditText et_tempat_lahir;
    @BindView(R.id.et_deskripsi_pekerjaan)
    EditText et_deskripsi_pekerjaan;
    @BindView(R.id.et_nama_pasangan)
    EditText et_nama_pasangan;
    @BindView(R.id.spinner_pendidikan)
    Spinner spinner_pendidikan;
    @BindView(R.id.spinner_pekerjaan)
    Spinner spinner_pekerjaan;
    @BindView(R.id.spinner_status)
    Spinner spinner_status;
    @BindView(R.id.tv_tgl_lahir)
    TextView tv_tgl_lahir;

    @BindView(R.id.label_nama_pasangan)
    TextView label_nama_pasangan;
    @BindView(R.id.layout_nama_pasangan)
    FrameLayout layout_nama_pasangan;

    private boolean isInvestor, isProjectOwner;
    private byte[] photoKtp, photoSelfie;
    private List<String> educationIdList, occupationIdList;
    private String educationSelectedItemId, occupationSelectedItemId, statusSelectedItem;
    private String gender = "";
    private Calendar newCalendar;
    private DatePickerDialog datePickerDialog;
    private Activity appActivity;
    private boolean isUnauthorized = false;
    private int minYear = 2004;
    private String educationNameSaved;
    private String occupationNameSaved;
    private String[] statusId = {" ", "MARRIED", "SINGLE", "DIVORCE", "WIDOW"};
    String[] status = {"Pilih status pernikahan", "Menikah", "Lajang", "Cerai", "Janda"};

    @OnClick(R.id.rb_laki)
    void onClickRbLaki(){
        gender = "MALE";
        rb_laki.setChecked(true);
        rb_perempuan.setChecked(false);
    }

    @OnClick(R.id.rb_perempuan)
    void onClickRbPerempuan(){
        gender = "FEMALE";
        rb_laki.setChecked(false);
        rb_perempuan.setChecked(true);
    }

    @OnClick(R.id.frame_save)
    void onClickSave(){
        //onSave
        PreferenceManager.setIsSaveVerificationData(true);
        setUserVerificationModel();
        MethodUtil.showSnackBar(findViewById(android.R.id.content), "Data berhasil disimpan");
    }

    @OnClick(R.id.tv_tgl_lahir)
    void onClickTgl(){
        datePickerDialog.show();
    }

    @OnClick(R.id.btn_back)
    void onClickBtnBack(){
        onBackPressed();
    }

    @OnClick(R.id.btn_lanjut)
    void onClickBtnLanjut() {
        //send all data to verif data 2
        if (TextUtils.isEmpty(et_nama_lengkap.getText().toString()))  {
            et_nama_lengkap.setError("Nama lengkap tidak boleh kosong");
            MethodUtil.showSnackBar(findViewById(android.R.id.content), "Nama Lengkap tidak boleh kosong");
            return;
        }

        if (TextUtils.isEmpty(et_tempat_lahir.getText().toString())) {
            et_tempat_lahir.setError("Tempat Lahir tidak boleh kosong");
            MethodUtil.showSnackBar(findViewById(android.R.id.content), "Tempat Lahir tidak boleh kosong");
            return;
        }

        if (TextUtils.isEmpty(gender)){
            MethodUtil.showSnackBar(findViewById(android.R.id.content), "Silahkan pilih jenis kelamin");
            return;
        }

        if (TextUtils.isEmpty(tv_tgl_lahir.getText().toString())){
            MethodUtil.showSnackBar(findViewById(android.R.id.content), "Tanggal lahir tidak boleh kosong");
            return;
        }

        if (TextUtils.isEmpty(educationSelectedItemId)) {
            MethodUtil.showSnackBar(findViewById(android.R.id.content), "Pendidikan tidak boleh kosong");
            return;
        }

        if (TextUtils.isEmpty(occupationSelectedItemId)) {
            MethodUtil.showSnackBar(findViewById(android.R.id.content), "Pekerjaan tidak boleh kosong");
            return;
        }

        if (TextUtils.isEmpty(statusSelectedItem)) {
            MethodUtil.showSnackBar(findViewById(android.R.id.content), "Status tidak boleh kosong");
            return;
        }

        if (layout_nama_pasangan.getVisibility() == View.VISIBLE){
            if (TextUtils.isEmpty(et_nama_pasangan.getText().toString())){
                MethodUtil.showSnackBar(findViewById(android.R.id.content), "Nama pasangan tidak boleh kosong");
                return;
            }
        }

        setUserVerificationModel();

        Intent intent = new Intent(this, VerificationData2Activity.class);
        intent.putExtra("is_investor", isInvestor);
        intent.putExtra("is_project_owner", isProjectOwner);
        intent.putExtra("photo_ktp", photoKtp);
        intent.putExtra("photo_selfie", photoSelfie);
        intent.putExtra("fullname", et_nama_lengkap.getText().toString());
        intent.putExtra("gender", gender);
        intent.putExtra("birth_place", et_tempat_lahir.getText().toString());
        intent.putExtra("birth_date", tv_tgl_lahir.getText().toString());
        intent.putExtra("education_id", educationSelectedItemId);
        intent.putExtra("occupation_id", occupationSelectedItemId);
        intent.putExtra("marital_status", statusSelectedItem);
        intent.putExtra("spouse_name", et_nama_pasangan.getText().toString().equals("")?"-":et_nama_pasangan.getText().toString());
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_verifikasi_data_pribadi);
        ButterKnife.bind(this);
        new PreferenceManager(this);
        initComponent();
    }

    private void initComponent() {
        appActivity = this;
        newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(
                this, VerificationData1Activity.this, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        int mYear = newCalendar.get(Calendar.YEAR);
        minYear = mYear - 17;

        if (getIntent() != null){
            isInvestor = getIntent().getBooleanExtra("is_investor", false);
            isProjectOwner = getIntent().getBooleanExtra("is_project_owner", false);
            photoKtp = getIntent().getByteArrayExtra("photo_ktp");
            photoSelfie = getIntent().getByteArrayExtra("photo_selfie");
        }

        et_nama_lengkap.setText(PreferenceManager.getFullname());
        getListEducation();
        getListOccupation();
        setSpinnerData();
        checkVerificationDataIsAvailable();
        if (PreferenceManager.getIsUnauthorized()){
            MethodUtil.refreshToken(this);
            initComponent();
        }
    }

    private void checkVerificationDataIsAvailable() {
        if (PreferenceManager.getIsSaveVerificationData()){
            UserVerification userVerification = PreferenceManager.getUserVerification();
            et_nama_lengkap.setText(userVerification.getName());
            et_tempat_lahir.setText(userVerification.getBirthplace());
            tv_tgl_lahir.setText(userVerification.getBirthDate());
            et_nama_pasangan.setText(userVerification.getSpouseName());
            if (userVerification.getGender().equals("MALE")) {
                rb_laki.setChecked(true);
                gender = "MALE";
            }
            if (userVerification.getGender().equals("FEMALE")) {
                rb_perempuan.setChecked(true);
                gender = "FEMALE";
            }

            int i = 0;
            for (String s : statusId) {
                if (s.equals(userVerification.getMaritalStatus())) {
                    spinner_status.setSelection(MethodUtil.getIndex(spinner_status, status[i]));
                }
                i++;
            }
        }
    }

    private void setUserVerificationModel() {
        if (PreferenceManager.getIsSaveVerificationData()) {
            UserVerification userVerification = PreferenceManager.getUserVerification();
            userVerification.setInvestor(isInvestor);
            userVerification.setProjectOwner(isProjectOwner);
            userVerification.setPhotoKtp(photoKtp);
            userVerification.setPhotoSelfie(photoSelfie);
            userVerification.setName(et_nama_lengkap.getText().toString());
            userVerification.setGender(gender);
            userVerification.setBirthplace(et_tempat_lahir.getText().toString());
            userVerification.setBirthDate(tv_tgl_lahir.getText().toString());
            userVerification.setEducationId(educationSelectedItemId);
            userVerification.setOccupationId(occupationSelectedItemId);
            userVerification.setMaritalStatus(statusSelectedItem);
            userVerification.setSpouseName(et_nama_pasangan.getText().toString().equals("")?"-":et_nama_pasangan.getText().toString());
            PreferenceManager.setUserVerification(userVerification);
            System.out.println("UserVerification : " + new Gson().toJson(PreferenceManager.getUserVerification()));
        } else {
            UserVerification userVerification = new UserVerification();
            userVerification.setInvestor(isInvestor);
            userVerification.setProjectOwner(isProjectOwner);
            userVerification.setPhotoKtp(photoKtp);
            userVerification.setPhotoSelfie(photoSelfie);
            userVerification.setName(et_nama_lengkap.getText().toString());
            userVerification.setGender(gender);
            userVerification.setBirthplace(et_tempat_lahir.getText().toString());
            userVerification.setBirthDate(tv_tgl_lahir.getText().toString());
            userVerification.setEducationId(educationSelectedItemId);
            userVerification.setOccupationId(occupationSelectedItemId);
            userVerification.setMaritalStatus(statusSelectedItem);
            userVerification.setSpouseName(et_nama_pasangan.getText().toString().equals("")?"-":et_nama_pasangan.getText().toString());
            PreferenceManager.setUserVerification(userVerification);
            System.out.println("UserVerification : " + new Gson().toJson(PreferenceManager.getUserVerification()));
        }

    }

    private void setSpinnerData() {
        ArrayAdapter aa_3 = new ArrayAdapter(this, R.layout.custom_simple_spinner_item, status){
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(getResources().getColor(R.color.grey_text));
                } else {
                    textview.setTextColor(getResources().getColor(R.color.black_primary));
                }
                return view;
            }
        };

        aa_3.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner_status.setAdapter(aa_3);
        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0){
                    statusSelectedItem = statusId[i];
                    if (statusId[i].equals("MARRIED")){
                        label_nama_pasangan.setVisibility(View.VISIBLE);
                        layout_nama_pasangan.setVisibility(View.VISIBLE);
                    } else {
                        label_nama_pasangan.setVisibility(View.GONE);
                        layout_nama_pasangan.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getListEducation() {
        Loading.show(this);

        educationIdList = new ArrayList<>();
        List<String> educationList = new ArrayList<>();
        ApiCore.apiInterface().getListEducation(PreferenceManager.getSessionToken()).enqueue(new Callback<List<Education>>() {
            @Override
            public void onResponse(Call<List<Education>> call, Response<List<Education>> response) {
                Loading.hide(appActivity);
                try {
                    if (response.isSuccessful()){
                        for (int i = 0; i < response.body().size(); i++){
                            Education education = response.body().get(i);
                            educationList.add(education.getName());
                            educationIdList.add(education.getId());

                            if (PreferenceManager.getIsSaveVerificationData()) {
                                UserVerification userVerification = PreferenceManager.getUserVerification();
                                if (education.getId().equals(userVerification.getEducationId())){
                                    educationNameSaved = education.getName();
                                    Log.d("educationNameSaved", educationNameSaved);
                                }
                            }
                        }

                        ArrayAdapter aa = new ArrayAdapter(appActivity, R.layout.custom_simple_spinner_item, educationList);
                        aa.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                        spinner_pendidikan.setAdapter(aa);
                        spinner_pendidikan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                educationSelectedItemId = educationIdList.get(i);
                                Log.d("educationSelectedItemId", educationSelectedItemId + " | " + adapterView.getSelectedItem());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        if (PreferenceManager.getIsSaveVerificationData()) {
                            spinner_pendidikan.setSelection(MethodUtil.getIndex(spinner_pendidikan, educationNameSaved));
                        }

                    } else {
                        if (response.message().toLowerCase().contains("unauthorized")){
                            PreferenceManager.setIsUnauthorized(true);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Education>> call, Throwable t) {
                Loading.hide(appActivity);
                t.printStackTrace();
            }
        });
    }

    private void getListOccupation(){
        Loading.show(this);

        occupationIdList = new ArrayList<>();
        List<String> occupationList = new ArrayList<>();
        ApiCore.apiInterface().getListOccupation(PreferenceManager.getSessionToken()).enqueue(new Callback<List<Occupation>>() {
            @Override
            public void onResponse(Call<List<Occupation>> call, Response<List<Occupation>> response) {
                Loading.hide(appActivity);
                try {
                    if (response.isSuccessful()){
                        for (int i = 0; i < response.body().size(); i++){
                            Occupation occupation = response.body().get(i);
                            occupationList.add(occupation.getName());
                            occupationIdList.add(occupation.getId());

                            if (PreferenceManager.getIsSaveVerificationData()) {
                                UserVerification userVerification = PreferenceManager.getUserVerification();
                                if (occupation.getId().equals(userVerification.getOccupationId())){
                                    occupationNameSaved = occupation.getName();
                                    Log.d("occupationNameSaved", occupationNameSaved);
                                }
                            }
                        }

                        ArrayAdapter aa = new ArrayAdapter(appActivity, R.layout.custom_simple_spinner_item, occupationList);
                        aa.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                        spinner_pekerjaan.setAdapter(aa);
                        spinner_pekerjaan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                occupationSelectedItemId = occupationIdList.get(i);
                                Log.d("occupationItemId", occupationSelectedItemId + " | " + adapterView.getSelectedItem());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        if (PreferenceManager.getIsSaveVerificationData()) {
                            spinner_pekerjaan.setSelection(MethodUtil.getIndex(spinner_pekerjaan, occupationNameSaved));
                        }

                    } else {
                        if (response.message().toLowerCase().contains("unauthorized")){
                            PreferenceManager.setIsUnauthorized(true);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Occupation>> call, Throwable t) {
                Loading.hide(appActivity);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar newDate = Calendar.getInstance();
        newDate.set(i, i1, i2);

        int mYear = newDate.get(Calendar.YEAR);
        if (mYear <= minYear) {
            tv_tgl_lahir.setText(dateFormatter.format(newDate.getTime()));
        } else {
            MethodUtil.showSnackBar(VerificationData1Activity.this, "Usia minimal adalah 17 tahun");
        }
    }
}