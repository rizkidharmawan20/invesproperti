package com.selada.invesproperti.presentation.portofolio.withdrawal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.selada.invesproperti.R;
import com.selada.invesproperti.presentation.profile.bantuan.BantuanActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawalActivity extends AppCompatActivity {

    @OnClick(R.id.btn_back)
    void onClickBack(){
        onBackPressed();
    }

    @OnClick(R.id.btn_tarik)
    void onClickTarik(){
        Intent intent = new Intent(this, KonfirmasiWithdrawalActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.btn_cs)
    void onClickCs(){
        Intent intent = new Intent(this, BantuanActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        ButterKnife.bind(this);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}