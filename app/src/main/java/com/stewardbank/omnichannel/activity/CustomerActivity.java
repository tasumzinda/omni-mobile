package com.stewardbank.omnichannel.activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.stewardbank.omnichannel.R;
import com.stewardbank.omnichannel.business.domain.Customer;
import com.stewardbank.omnichannel.business.domain.User;
import com.stewardbank.omnichannel.business.util.AppUtil;
import com.stewardbank.omnichannel.business.util.DateUtil;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomerActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.firstName)
    EditText firstName;
    @BindView(R.id.lastName)
    EditText lastName;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.idNumber)
    EditText idNumber;
    @BindView(R.id.cardNumber)
    EditText cardNumber;
    @BindView(R.id.phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.btn_save)
    Button save;
    EditText [] fields;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        ButterKnife.bind(this);
        setSupportActionBar(getToolbar("Step 1:: Create New Account"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        save.setOnClickListener(this);
        fields = new EditText[]{firstName, lastName, address, idNumber, cardNumber, phoneNumber};
        if( ! Customer.findComplete().isEmpty()){
            syncAppData();
            Log.d("Result", AppUtil.createGson().toJson(Customer.findComplete()));
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == save.getId()){
            if(validate(fields)){
                save(getCustomer());
            }

        }
    }

    public void save(Customer customer){
        customer.save();
        AppUtil.createShortNotification(this, "Successfully saved customer");
        Intent intent = new Intent(this, SignatureActivity.class);
        intent.putExtra("idNumber", customer.idNumber);
        startActivity(intent);
        finish();

    }

    public Customer getCustomer(){
        Customer customer = new Customer();
        customer.firstName = firstName.getText().toString();
        customer.lastName = lastName.getText().toString();
        customer.address = address.getText().toString();
        customer.idNumber = idNumber.getText().toString();
        customer.cardNumber = Long.parseLong(cardNumber.getText().toString());
        customer.phoneNumber = Long.parseLong(phoneNumber.getText().toString());
        customer.date = DateUtil.formatDateRest(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        customer.timeCreated = sdf.format(System.currentTimeMillis());
        customer.createdBy = User.findByUserName(AppUtil.getUsername(this));
        return customer;
    }





    public boolean validate(EditText [] fields){
        boolean isValid = true;
        for(int i = 0; i < fields.length; i++){
            if(fields[i].getText().toString().isEmpty()){
                fields[i].setError(getResources().getString(R.string.required_field_error));
                isValid = false;
            }else{
                fields[i].setError(null);
            }
        }
        if(Customer.findByIdNumber(idNumber.getText().toString()) != null){
            idNumber.setError("Customer already exists");
            isValid = false;
        }else{
            idNumber.setError(null);
        }
        return isValid;
    }
}
