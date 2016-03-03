package com.gwexhibits.timemachine.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gwexhibits.timemachine.OrderDetails;
import com.gwexhibits.timemachine.R;

public class StagePopUp extends DialogFragment {

    private Bundle bundle;
    private StagePopUp dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = this;
        bundle = this.getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.stage_dialog_title)
                .setSingleChoiceItems(bundle.getStringArray("options"), -1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent showOrderDetails = new Intent(getActivity(), OrderDetails.class);
                            showOrderDetails.putExtra("order", bundle.getString("order"));
                            getActivity().startActivity(showOrderDetails);
                            dialog.dismiss();

                        }
                    }
                );
        return builder.create();
    }
}
