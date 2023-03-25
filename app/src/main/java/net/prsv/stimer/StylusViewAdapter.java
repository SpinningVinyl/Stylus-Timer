package net.prsv.stimer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;


public class StylusViewAdapter extends RecyclerView.Adapter<StylusViewAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Stylus> mStyli;
    private final HashMap<Integer, StylusProfile> mProfiles;

    public final double ONE_SIDE_DEFAULT = 0.35;
    public final double ONE_LP_DEFAULT = 0.7;

    private final STimerPreferences preferences = STimerPreferences.getInstance();

    public StylusViewAdapter(Context mContext, @NonNull ArrayList<Stylus> mStyli, @NonNull ArrayList<StylusProfile> stylusProfiles) {
        this.mContext = mContext;
        this.mStyli = mStyli;
        this.mProfiles = new HashMap<>();
        for (StylusProfile sp : stylusProfiles) {
            mProfiles.put(sp.getId(), sp);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_list_item,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // set alternating background colour
        int bgColor = mContext.getColor(R.color.white);
        if (position % 2 != 0) {
            bgColor = mContext.getColor(R.color.gray);
        }

        setButtonListeners(holder);

        holder.mParentLayout.setBackgroundColor(bgColor);

        Stylus stylus = mStyli.get(position);

        // display stylus name
        holder.mStylusName.setText(stylus.getName());

        // display stylus profile
        StylusProfile profile = mProfiles.get(stylus.getProfileId());
        assert profile != null;
        holder.mStylusProfile.setText(String.format(mContext.getString(R.string.tv_profile),
                profile.getName()));

        holder.mVTF.setText(String.format(mContext.getString(R.string.tv_vtf),
                stylus.getTrackingForce()));

        // determine the wear threshold
        int threshold =
                stylus.getCustomThreshold() != 0 ? stylus.getCustomThreshold() : profile.getThreshold();

        // format and display stylus usage
        holder.mStylusUsage.setText(String.format(mContext.getString(R.string.tv_usage),
                stylus.getHours(), threshold));

        // set max and progress for the progress bar
        holder.mWearBar.setMax(threshold);
        holder.mWearBar.setProgress((int) Math.round(stylus.getHours()));

        // calculate and display stylus wear and progress bar color
        holder.mStylusWear.setText(wearString(threshold, stylus.getHours()));
        Drawable progressDrawable = holder.mWearBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(progressBarColor(threshold, stylus.getHours()), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.mWearBar.setProgressDrawable(progressDrawable);



    }

    private void setButtonListeners(@NonNull ViewHolder holder) {

        holder.mEdit.setOnClickListener(v -> {

        });

        // listener for the "+SIDE" button
        holder.mAddSide.setOnClickListener(v -> {
            Stylus stylus = mStyli.get(holder.getAdapterPosition());
            double oneSide =
                    preferences.getCustomSide() != 0 ? (double) preferences.getCustomSide() / 60 : ONE_SIDE_DEFAULT;
            stylus.setHours(stylus.getHours() + oneSide);
            notifyItemChanged(holder.getAdapterPosition());
        });

        // listener for the "+LP" button
        holder.mAddLP.setOnClickListener(v -> {
            Stylus stylus = mStyli.get(holder.getAdapterPosition());
            double oneLP =
                    preferences.getCustomLP() != 0 ? (double) preferences.getCustomLP() / 60 : ONE_LP_DEFAULT;
            stylus.setHours(stylus.getHours() + oneLP);
            notifyItemChanged(holder.getAdapterPosition());
        });

        // listener for the "+custom time" button
        holder.mAddCustom.setOnClickListener(v -> {
            Stylus stylus = mStyli.get(holder.getAdapterPosition());

            // create a new pop-up dialog
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
            dialogBuilder.setTitle("Enter custom time (in minutes)");

            // create an edit field
            final EditText input = new EditText(mContext);
            input.setPadding(15,15,15,15);
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            // disable any characters except 0123456789
            InputFilter filter = (source, start, end, dest, dstart, dend) -> {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            };
            input.setFilters(new InputFilter[]{filter});

            // put the edit field inside the dialog
            dialogBuilder.setView(input);

            // OK button + event handler
            dialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                int customTime = Integer.parseInt(input.getText().toString());
                double customTimeInHours = (double) customTime / 60;
                stylus.setHours(stylus.getHours() + customTimeInHours);
                notifyItemChanged(holder.getAdapterPosition());
            });

            // cancel button
            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            // show the dialog
            dialogBuilder.show();

        });
    }

    @Override
    public int getItemCount() {
        return mStyli.size();
    }

    private int progressBarColor(int threshold, double hours) {
        double wear = hours / threshold;
        int color;
        if (wear < 0.5) {
            color = mContext.getColor(R.color.pb_wear_low);
        } else if (wear < 0.8) {
            color = mContext.getColor(R.color.pb_wear_medium);
        } else if (wear < 1.0) {
            color = mContext.getColor(R.color.pb_wear_high);
        } else {
            color = mContext.getColor(R.color.pb_wear_replace);
        }
        return color;
    }

    private String wearString(int threshold, double hours) {
        double wear = hours / threshold;
        String wearLevel;
        if (wear < 0.5) {
            wearLevel = mContext.getString(R.string.tv_wear_low);
        } else if (wear < 0.8) {
            wearLevel = mContext.getString(R.string.tv_wear_medium);
        } else if (wear < 1.0) {
            wearLevel = mContext.getString(R.string.tv_wear_high);
        } else {
            wearLevel = mContext.getString(R.string.tv_wear_replace);
        }
        return String.format(mContext.getString(R.string.tv_wear_template), wearLevel);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mStylusName, mStylusProfile, mVTF, mStylusUsage, mStylusWear;

        Button mAddSide, mAddLP, mAddCustom;
        ImageButton mEdit;
        ConstraintLayout mParentLayout;
        ProgressBar mWearBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mStylusName = itemView.findViewById(R.id.tvStylusName);
            mStylusProfile = itemView.findViewById(R.id.tvStylusProfile);
            mVTF = itemView.findViewById(R.id.tvVTF);
            mStylusUsage = itemView.findViewById(R.id.tvStylusUsage);
            mStylusWear = itemView.findViewById(R.id.tvStylusWear);
            mParentLayout = itemView.findViewById(R.id.stylusItemLayout);
            mEdit = itemView.findViewById(R.id.btnEdit);
            mAddSide = itemView.findViewById(R.id.btnAddSide);
            mAddLP = itemView.findViewById(R.id.btnAddLP);
            mAddCustom = itemView.findViewById(R.id.btnAddCustom);
            mWearBar = itemView.findViewById(R.id.wearBar);
        }
    }
}
