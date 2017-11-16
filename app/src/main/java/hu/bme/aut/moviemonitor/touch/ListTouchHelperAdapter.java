package hu.bme.aut.moviemonitor.touch;


public interface ListTouchHelperAdapter
{
    void onItemDismiss(int position);

    void onItemMove(int fromPosition, int toPosition);
}
