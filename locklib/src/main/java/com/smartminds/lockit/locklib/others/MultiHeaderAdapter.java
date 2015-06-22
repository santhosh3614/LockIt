package com.smartminds.lockit.locklib.others;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by santhosh on 18/6/15.
 */
public abstract class MultiHeaderAdapter<T> extends BaseAdapter {

    private static final int HEADER_IDX = 0;
    private static final int NORMAL_IDX = 1;

    public abstract int getHeaderCount();

    public abstract List<T> getHeaderItems(int headerIdx);

    public abstract void bindHeaderView(int headerIdx, View convertView, ViewGroup parent);

    public abstract View getHeaderView(int headerIdx, View convertView, ViewGroup parent);

    public abstract void bindNormalView(T t, View convertView, ViewGroup parent);

    public abstract View getNormalView(T t, View convertView, ViewGroup parent);

    @Override
    public final int getCount() {
        int count = 0;
        long headerCount = getHeaderCount();
        for (int i = 0; i < headerCount; i++) {
            int size = getHeaderItems(i).size();
            count += size > 0 ? ++size : size;
        }
        return count;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != HEADER_IDX;
    }

    @Override
    public final T getItem(int position) {
        int totCount=0,prevCount=0;
        int headerCount = getHeaderCount();
        for (int i = 0; i < headerCount; i++) {
            List<T> headerItems=getHeaderItems(i);
            int size = headerItems.size();
            prevCount=totCount;
            totCount += size > 0 ? size + 1 : size;
            if(position>totCount){
                continue;
            }
            return headerItems.get(position-prevCount-1);
        }
        return null;
    }

    @Override
    public final int getItemViewType(int position) {
        int count = 0;
        int headerCount = getHeaderCount();
        for (int i = 0; i < headerCount; i++) {
            int size = getHeaderItems(i).size();
            count += size > 0 ? size + 1 : size;
            if (position == count - size - 1) {
                return HEADER_IDX;
            }
            if (position < count) {
                break;
            }
        }
        return NORMAL_IDX;
    }

    @Override
    public final int getViewTypeCount() {
        return 2;
    }

    @Override
    public final long getItemId(int position) {
        int prevItemsCount = 0;
        int headerCount = getHeaderCount();
        for (int i = 0; i < headerCount; i++) {
            List<T> tList = getHeaderItems(i);
            int currentHeaderCount = i + 1;
            prevItemsCount += tList.size();
            if (position < prevItemsCount + currentHeaderCount) {
                return currentHeaderCount - 1;
            }
        }
        return -1;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getItemType:"+getItemViewType(position)+" Position:"+ position);
        if (getItemViewType(position) == HEADER_IDX) {
            int headerId = (int) getItemId(position);
            if (convertView == null) {
                convertView = getHeaderView(headerId, convertView, parent);
            }
            bindHeaderView(headerId, convertView, parent);
        } else {
            T t = getItem(position);
            if (convertView == null) {
                convertView = getNormalView(t, convertView, parent);
            }
            bindNormalView(t, convertView, parent);
        }
        return convertView;
    }
}