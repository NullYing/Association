package com.lastfighting.association.association;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.lastfighting.association.association.Http.Http;
import com.lastfighting.association.association.Http.RetData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity {

    private UserRegisterTask mRegisterTask = null;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordViewR;
    private RadioGroup mSexView;
    private EditText mNameView;
    private EditText mPhoneView;
    private EditText mSchoolView;
    private RadioGroup mIdentifyView;

    private View mRegisterFormView;
    private View mProgressView;
    private Spinner mAssociationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (EditText) findViewById(R.id.rEmail);
        mPasswordView = (EditText) findViewById(R.id.rPassword);
        mPasswordViewR = (EditText) findViewById(R.id.rPasswordR);
        mSexView = (RadioGroup) findViewById(R.id.rradioSex);
        mNameView = (EditText) findViewById(R.id.rName);
        mPhoneView = (EditText) findViewById(R.id.rPhone);
        mSchoolView = (EditText) findViewById(R.id.rSchool);
        mIdentifyView = (RadioGroup) findViewById(R.id.rradioIdentity);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        mAssociationListView = (Spinner) findViewById(R.id.association_list);

        Button mRegisterButton = (Button) findViewById(R.id.rRegister);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        final List<String> list = new ArrayList<String>();
        final Runnable updateThread = new Runnable() {

            @Override
            public void run() {
                // 建立数据源
                String[] mItems = list.toArray(new String[list.size()]);
                // 建立Adapter并且绑定数据源
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, mItems);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //绑定 Adapter到控件
                mAssociationListView.setAdapter(adapter);
            }

        };
        Runnable oneRunnable = new Runnable() {
            @Override
            public void run() {
                RetData ret = new RetData();
                String result = Http.Get(Config.GetListUrl, ret);
                try {
                    JSONObject redata = new JSONObject(new String(result));
                    JSONArray list_array = redata.getJSONArray("errmsg");

                    for (int i = 0; i < list_array.length(); i++) {
                        list.add(list_array.getString(i));
                    }
                    RegisterActivity.this.runOnUiThread(updateThread);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(oneRunnable).start();
    }

    private void attemptLogin() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordR = mPasswordViewR.getText().toString();

        int sex = 0;
        int sexid = mSexView.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(sexid);
        if (rb.getText().equals("男"))
            sex = 1;
        else
            sex = 2;

        String name = mNameView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String school = mSchoolView.getText().toString();

        int identify = 0;
        int identifyid = mIdentifyView.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(identifyid);
        if (rb.getText().equals("我是社长"))
            identify = 1;
        else
            identify = 2;

        String association = mAssociationListView.getSelectedItem().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!password.equals(passwordR)) {
            mPasswordView.setError("两次输入的密码密码不一致");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mRegisterTask = new UserRegisterTask(email, password, sex, name, phone, school, identify, association, this);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final int mSex;
        private final String mName;
        private final String mPhone;
        private final String mSchool;
        private final int mIdentify;
        private final String mAssociation;

        private int errorCode = 0;
        private Context mContext;

        UserRegisterTask(String email, String password, int sex, String name, String phone, String school, int identify, String association, Context context) {
            mEmail = email;
            mPassword = password;
            mSex = sex;
            mName = name;
            mPhone = phone;
            mSchool = school;
            mIdentify = identify;
            mAssociation = association;

            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mEmail);
                jsonObject.put("password", mPassword);
                jsonObject.put("sex", mSex);
                jsonObject.put("name", mName);
                jsonObject.put("phone", mPhone);
                jsonObject.put("school", mSchool);
                jsonObject.put("identify", mIdentify);
                jsonObject.put("association", mAssociation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RetData ret = new RetData();
            StringBuffer result = Http.Post(Config.RegisterUrl, URLEncoder.encode(jsonObject.toString()), ret);
            if (ret.retcode == 200) {
                try {
                    JSONObject redata = new JSONObject(new String(result));
                    String msg = redata.getString("errmsg");
                    if (msg.equals("ok")) {
                        return true;
                    } else if (msg.equals("error")) {
                        errorCode = 1;
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(mContext, "注册成功",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (errorCode == 1) {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                    new AlertDialog.Builder(mContext)
                            .setMessage("已存在该账号")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            //按钮事件
                                        }
                                    })
                            .show();
                }
               else {
                    new AlertDialog.Builder(mContext)
                            .setMessage("网络连接超时，请稍后再试")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            //按钮事件
                                        }
                                    })
                            .show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            showProgress(false);
        }
    }
}
