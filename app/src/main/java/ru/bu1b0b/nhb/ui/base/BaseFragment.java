package ru.bu1b0b.nhb.ui.base;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import ru.bu1b0b.nhb.R;


/**
 * Created by bu1b0b on 01.02.2017.
 */

public abstract class BaseFragment extends Fragment {

    public ProgressDialog progressDialog;

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
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
