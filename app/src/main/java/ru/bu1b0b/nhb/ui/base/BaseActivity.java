package ru.bu1b0b.nhb.ui.base;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import ru.bu1b0b.nhb.R;

public abstract class BaseActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
