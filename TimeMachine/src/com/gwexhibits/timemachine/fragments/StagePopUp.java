package com.gwexhibits.timemachine.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.gwexhibits.timemachine.OrderDetailsActivity;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.Order;

public class StagePopUp extends DialogFragment {

    private Bundle bundle;

    public static final String LIST_OF_PHASES_KEY = "options";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        bundle = this.getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.stage_dialog_title)
                .setSingleChoiceItems(bundle.getStringArray(LIST_OF_PHASES_KEY), -1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent showOrderDetails = new Intent(getActivity(), OrderDetailsActivity.class);
                            showOrderDetails.putExtra(OrderDetailsActivity.ORDER_KEY,
                                    bundle.getSerializable(OrderDetailsActivity.ORDER_KEY));

                            showOrderDetails.putExtra(OrderDetailsActivity.PHASE_KEY,
                                    bundle.getStringArray(LIST_OF_PHASES_KEY)[id]);
                            getActivity().startActivity(showOrderDetails);
                            dialog.dismiss();
                        }
                    }
                );
        return builder.create();
    }
}
