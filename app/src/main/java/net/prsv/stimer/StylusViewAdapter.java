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

    private final EditStylusClickListener listener;
    private final ArrayList<Stylus> mStyli;
    private final HashMap<Integer, StylusProfile> mProfiles;

    public final double ONE_SIDE_DEFAULT = 0.35;
    public final double ONE_LP_DEFAULT = 0.7;

    private final STimerPreferences preferences = STimerPreferences.getInstance();

    public StylusViewAdapter(Context context, @NonNull ArrayList<Stylus> styli, @NonNull ArrayList<StylusProfile> stylusProfiles) {
        this.mContext = context;
        this.listener = (EditStylusClickListener) context;
        this.mStyli = styli;
        this.mProfiles = new HashMap<>();
        for (StylusProfile sp : stylusProfiles) {
            mProfiles.put(sp.getId(), sp);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stylus_list_item,
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

        holder.clParentLayout.setBackgroundColor(bgColor);

        Stylus stylus = mStyli.get(position);

        // display stylus name
        holder.tvStylusName.setText(stylus.getName());

        // display stylus profile
        StylusProfile profile = mProfiles.get(stylus.getProfileId());
        assert profile != null;
        holder.tvStylusProfile.setText(String.format(mContext.getString(R.string.tv_profile),
                profile.getName()));

        holder.tvVTF.setText(String.format(mContext.getString(R.string.tv_vtf),
                stylus.getTrackingForce()));

        // determine the wear threshold
        int threshold =
                stylus.getCustomThreshold() != 0 ? stylus.getCustomThreshold() : profile.getThreshold();

        // format and display stylus usage
        holder.tvStylusUsage.setText(String.format(mContext.getString(R.string.tv_usage),
                stylus.getHours(), threshold));

        // set max and progress for the progress bar
        holder.pbWearBar.setMax(threshold);
        holder.pbWearBar.setProgress((int) Math.round(stylus.getHours()));

        // calculate and display stylus wear and progress bar color
        holder.tvStylusWear.setText(wearString(threshold, stylus.getHours()));
        Drawable progressDrawable = holder.pbWearBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(progressBarColor(threshold, stylus.getHours()), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.pbWearBar.setProgressDrawable(progressDrawable);

    }

    private void setButtonListeners(@NonNull ViewHolder holder) {

        // listener for the image button
        holder.ibEdit.setOnClickListener(v -> {
            Stylus stylus = mStyli.get(holder.getAdapterPosition());
            listener.callEditStylusActivity(stylus.getId(), holder.getAdapterPosition());
        });

        // listener for the "+SIDE" button
        holder.btnAddSide.setOnClickListener(v -> {
            Stylus stylus = mStyli.get(holder.getAdapterPosition());
            double oneSide =
                    preferences.getCustomSide() != 0 ? (double) preferences.getCustomSide() / 60 : ONE_SIDE_DEFAULT;
            stylus.setHours(stylus.getHours() + oneSide);
            try(DataHelper helper = new DataHelper()) {
                helper.updateStylus(stylus);
            }
            notifyItemChanged(holder.getAdapterPosition());
        });

        // listener for the "+LP" button
        holder.btnAddLP.setOnClickListener(v -> {
            Stylus stylus = mStyli.get(holder.getAdapterPosition());
            double oneLP =
                    preferences.getCustomLP() != 0 ? (double) preferences.getCustomLP() / 60 : ONE_LP_DEFAULT;
            stylus.setHours(stylus.getHours() + oneLP);
            try(DataHelper helper = new DataHelper()) {
                helper.updateStylus(stylus);
            }
            notifyItemChanged(holder.getAdapterPosition());
        });

        // listener for the "+custom time" button
        holder.btnAddCustom.setOnClickListener(v -> {
            Stylus stylus = mStyli.get(holder.getAdapterPosition());

            // create a new pop-up dialog
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
            dialogBuilder.setTitle(R.string.enter_time_in_minutes);

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
            dialogBuilder.setPositiveButton(R.string.button_ok, (dialog, which) -> {
                int customTime = Integer.parseInt(input.getText().toString());
                double customTimeInHours = (double) customTime / 60;
                stylus.setHours(stylus.getHours() + customTimeInHours);
                try(DataHelper helper = new DataHelper()) {
                    helper.updateStylus(stylus);
                }
                notifyItemChanged(holder.getAdapterPosition());
            });

            // cancel button
            dialogBuilder.setNegativeButton(R.string.button_cancel, (dialog, which) -> dialog.cancel());

            // show the dialog
            dialogBuilder.show();

        });
    }

    @Override
    public int getItemCount() {
        return mStyli.size();
    }

    protected void clearDataSet() {
        mStyli.clear();
    }

    protected void addToDataSet(Stylus stylus) {
        mStyli.add(stylus);
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
        final TextView tvStylusName, tvStylusProfile, tvVTF, tvStylusUsage, tvStylusWear;

        final Button btnAddSide, btnAddLP, btnAddCustom;
        final ImageButton ibEdit;
        final ConstraintLayout clParentLayout;
        final ProgressBar pbWearBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStylusName = itemView.findViewById(R.id.tvStylusName);
            tvStylusProfile = itemView.findViewById(R.id.tvStylusProfile);
            tvVTF = itemView.findViewById(R.id.tvVTF);
            tvStylusUsage = itemView.findViewById(R.id.tvStylusUsage);
            tvStylusWear = itemView.findViewById(R.id.tvStylusWear);
            clParentLayout = itemView.findViewById(R.id.clParentLayout);
            ibEdit = itemView.findViewById(R.id.ibEdit);
            btnAddSide = itemView.findViewById(R.id.btnAddSide);
            btnAddLP = itemView.findViewById(R.id.btnAddLP);
            btnAddCustom = itemView.findViewById(R.id.btnAddCustom);
            pbWearBar = itemView.findViewById(R.id.pbWearBar);
        }
    }
}
