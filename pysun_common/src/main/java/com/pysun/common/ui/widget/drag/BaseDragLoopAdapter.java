package com.pysun.common.ui.widget.drag;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseDragLoopAdapter {

    public abstract int getCount();

    public abstract Object getItem(int position);


    public abstract View onCreateView(int position, ViewGroup parent);

    public abstract void onBindView(int position, View convertView, ViewGroup parent);

    boolean isEmpty() {
        return getCount() == 0;
    }

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public boolean hasStableIds() {
        return false;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterAll();
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterAll();
//        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached View that the underlying data has been changed
     * and it should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

}
