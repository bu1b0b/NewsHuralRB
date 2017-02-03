package ru.bu1b0b.nhb.ui.base;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import ru.bu1b0b.nhb.R;


/**
 * Created by bu1b0b on 01.02.2017.
 */

public class BaseFragment  extends Fragment {

    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
