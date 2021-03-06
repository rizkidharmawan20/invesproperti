package com.selada.invesproperti.presentation.portofolio.withdrawal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.selada.invesproperti.R;
import com.selada.invesproperti.presentation.profile.cs.CallCenterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawalConfirmationPassActivity extends AppCompatActivity {

    @BindView(R.id.etKataSandi)
    in.anshul.libray.PasswordEditText etKataSandi;

    @OnClick(R.id.btn_back)
    void onClickBack(){
        onBackPressed();
    }

    @OnClick(R.id.btn_tarik)
    void onClickTarik(){
        if (!etKataSandi.getText().toString().equals("")){
            Intent intent = new Intent(this, CompleteWithdrawalActivity.class);
            startActivity(intent);
            this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else {
            etKataSandi.setError("Silahkan masukan kata sandi");
        }
    }

    @OnClick(R.id.btn_cs)
    void onClickBtnCs(){
        Intent intent = new Intent(this, CallCenterActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_withdrawal_pass);
        ButterKnife.bind(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}