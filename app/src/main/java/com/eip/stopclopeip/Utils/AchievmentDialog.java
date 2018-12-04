package com.eip.stopclopeip.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eip.stopclopeip.R;

public class AchievmentDialog extends AppCompatDialogFragment {
    private ImageView image;
    private TextView desc;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_achievment, null);

        builder.setView(view)
                .setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

        image = view.findViewById(R.id.achievment_logo);
        desc = view.findViewById(R.id.achievment_desc);

        image.setImageDrawable(ContextCompat.getDrawable(getActivity(), getArguments().getInt("image")));
        desc.setText(getArguments().getString("desc"));

        return builder.create();
    }
}
