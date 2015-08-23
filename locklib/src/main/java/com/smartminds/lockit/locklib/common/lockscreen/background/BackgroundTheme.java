package com.smartminds.lockit.locklib.common.lockscreen.background;

import android.net.Uri;

/**
 * Created by santhoshkumar on 8/5/15.
 */
public class BackgroundTheme {

    private long id;
    private String name;
    private Uri uri;

    protected BackgroundTheme(long id,String name,String uriPath) {
        this.id=id;
        this.name=name;
        this.uri=Uri.parse(uriPath);
    }

    void setId(long id) {
        this.id = id;
    }

    long getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public String getImagePath(){
        return uri.getPath();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof BackgroundTheme){
            return id==((BackgroundTheme)o).id;
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return id+"";
    }
}
