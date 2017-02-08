package ru.bu1b0b.nhb.ui.base;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import ru.bu1b0b.nhb.R;


public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }


}
