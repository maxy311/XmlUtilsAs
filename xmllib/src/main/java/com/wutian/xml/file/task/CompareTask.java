package com.wutian.xml.file.task;

import com.wutian.xml.file.TranslateHelper;

import java.io.File;

public class CompareTask extends Task{
    public File mOriginFile;
    public File mTargetFile;
    public File mSaveFile;
    public boolean mIsCompareAr;

    public CompareTask(File originFile, File targetFile, File saveFile, boolean isCompareAr) {
        mOriginFile = originFile;
        mTargetFile = targetFile;
        mSaveFile = saveFile;
        mIsCompareAr = isCompareAr;
    }

    @Override
    public void run() {
        TranslateHelper.compareFile(mOriginFile, mTargetFile, mSaveFile, mIsCompareAr);
        mIsDone = true;
    }

    @Override
    public boolean isDone() {
        return mIsDone;
    }
}
