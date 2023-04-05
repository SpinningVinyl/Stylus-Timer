package net.prsv.stimer;

/**
 * Implementing this interface allows an activity to listen to events for items in a RecyclerView
 */
public interface EditStylusClickListener {

    void callEditStylusActivity(int id, int position);

}
