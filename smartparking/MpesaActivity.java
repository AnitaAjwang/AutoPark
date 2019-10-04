package com.example.smartparking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MpesaActivity extends AppCompatActivity implements View.OnClickListener, AppController {
    EditText tvAccountName, tvAccountNo, tvTotalCash, tvPhonenumber;
    TextView tvProceed;
    Button btMpesa, btProceed;
    Activity mActivity;
    SharedValues sharedValues = SharedValues.getInstance();
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa);

        mActivity = MpesaActivity.this;
        tvAccountName = (EditText) findViewById(R.id.tvAccountName);
        tvPhonenumber = (EditText) findViewById(R.id.tvPhonenumber);
        tvAccountNo = (EditText) findViewById(R.id.tvAccountnumber);
        tvTotalCash = (EditText) findViewById(R.id.tvTotalCash);
        tvProceed = (TextView) findViewById(R.id.tvProceed);
        btMpesa = (Button) findViewById(R.id.btMpesa);
        btProceed = (Button) findViewById(R.id.btProceed);

        btMpesa.setOnClickListener(this);
        btProceed.setOnClickListener(this);
        dialog = new ProgressDialog(mActivity);
        dialog.setMessage(getResources().getString(R.string.loading));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btMpesa:
                paymentViaMpesa();
                break;

            case R.id.btProceed:
                JSONObject postData = new JSONObject();
                try {
                    postData.put("mpesaid", sharedValues.getMpesaCheckoutID());
                    postData.put("business_number", tvAccountNo.getText().toString());
                    postData.put("typeofActivity", "confirmPayment");

                    String reg_url = "http://192.168.43.66/carent/mpesapayment/TestPHP.php";
                    new ConnectionClass(mActivity, "confirmPayment").execute(reg_url, postData.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void paymentViaMpesa() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("mpesaamount", tvTotalCash.getText().toString());
            postData.put("mpesanumber", tvPhonenumber.getText().toString());
            postData.put("account_ref", tvAccountName.getText().toString());
            postData.put("account_number", tvAccountNo.getText().toString());
            postData.put("typeofActivity", "sendCash");

            String reg_url = "http://192.168.43.66/carent/mpesapayment/TestPHP.php";
            new ConnectionClass(mActivity, "sendCash").execute(reg_url, postData.toString());
            btProceed.setVisibility(View.VISIBLE);
            tvProceed.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public ProgressDialog getProgressDialog() {
        return dialog;
    }

    @Override
    public void retrieveData(String result) throws JSONException {
        sharedValues.setMpesaCheckoutID(result);
        Toast.makeText(mActivity, result, Toast.LENGTH_LONG).show();
    }
}
