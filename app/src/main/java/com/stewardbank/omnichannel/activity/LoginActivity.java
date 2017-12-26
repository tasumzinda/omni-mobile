package com.stewardbank.omnichannel.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.stewardbank.omnichannel.R;
import com.stewardbank.omnichannel.business.domain.User;
import com.stewardbank.omnichannel.business.rest.LoginService;
import com.stewardbank.omnichannel.business.rest.ServiceGenerator;
import com.stewardbank.omnichannel.business.util.AppUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.username)
    EditText userName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.btn_login)
    Button login;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(getToolbar("Steward Bank Accounts:: Login"));
        login.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == login.getId()){
            login();
        }
    }

    private void login(){
        progressDialog.setTitle("Signing in to server. Please wait.....");
        progressDialog.setCancelable(true);
        progressDialog.show();
        final String user = userName.getText().toString();
        final String pass = password.getText().toString();
        LoginService service = ServiceGenerator.createService(LoginService.class, user, pass);
        Call<User> call = service.login(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User item = response.body();
                    if(User.findByServerId(item.serverId) == null){
                        response.body().save();
                    }

                    AppUtil.savePreferences(getApplicationContext(), AppUtil.LOGGED_IN, Boolean.TRUE);
                    AppUtil.savePreferences(getApplicationContext(), AppUtil.USERNAME, user);
                    AppUtil.savePreferences(getApplicationContext(), AppUtil.PASSWORD, pass);
                    progressDialog.hide();
                    AppUtil.createLongNotification(getApplicationContext(), "Successfully logged in!");
                    Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progressDialog.hide();
                AppUtil.createShortNotification(getApplicationContext(), "Error: " + t.getMessage());
            }
        });
    }
}
