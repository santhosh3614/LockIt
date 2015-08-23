package com.smartminds.lockit.locklib.common.lockscreen.background;

import android.database.Cursor;

import com.appsforbb.common.sqlitelib.Column;
import com.appsforbb.common.sqlitelib.ColumnTypes;
import com.appsforbb.common.sqlitelib.ObjectTable;

import java.util.List;

/**
 * Created by santhoshkumar on 8/5/15.
 */
public class BackgroundThemeTable extends ObjectTable<BackgroundTheme> {

    private static final String TABLE_NAME = BackgroundTheme.class.getSimpleName();

    ColumnTypes.StringColumn NAME = new ColumnTypes.StringColumn("name");
    ColumnTypes.StringColumn IMAGEPATH = new ColumnTypes.StringColumn("imgpath",
            Column.Constraint.ALLOW_NULL);

    public BackgroundThemeTable() {
        super(TABLE_NAME);
        addColumns(NAME, IMAGEPATH);
    }

    @Override
    protected BackgroundTheme readRecordRow(long l, Cursor cursor) {
        return new BackgroundTheme(l, NAME.getValue(cursor), IMAGEPATH.getValue(cursor));
    }

    @Override
    protected void fillRecordRow(SQLiteRow sqLiteRow, BackgroundTheme backgroundTheme) {
        sqLiteRow.setColumnValue(NAME, backgroundTheme.getName());
        sqLiteRow.setColumnValue(IMAGEPATH,backgroundTheme.getImagePath());
    }

    protected List<BackgroundTheme> getBackgroundThemes() {
        return super.getAllRecords();
    }

    protected BackgroundTheme getBackgroundTheme(long rowId) {
        return super.getRecordById(rowId);
    }

    protected long insertBackground(BackgroundTheme backgroundTheme) {
        return super.insertNewRecord(backgroundTheme);
    }

    protected void deleteBackgroundTheme(BackgroundTheme backgroundTheme) {
        deleteByRowId(backgroundTheme.getId());
    }

}
