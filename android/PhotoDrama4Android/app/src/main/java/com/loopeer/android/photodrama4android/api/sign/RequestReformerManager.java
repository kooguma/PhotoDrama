package com.loopeer.android.photodrama4android.api.sign;

import okhttp3.Request;

public class RequestReformerManager {

    private Request mPreRequest;
    private String md5key;
    private RequestReformer mRequestReformer;

    public RequestReformerManager(Request preRequest, String md5key) {
        mPreRequest = preRequest;
        this.md5key = md5key;
    }

    public Request createNewRequest() {
        switch (mPreRequest.method()) {
            case "GET":
                mRequestReformer = new GetReformer(mPreRequest, md5key);
                break;
            case "POST":
                mRequestReformer = new PostReformer(mPreRequest, md5key);
                break;
            case "PUT":
                mRequestReformer = new PutReformer(mPreRequest, md5key);
                break;
            default:
                return mPreRequest;
        }
        return mRequestReformer.createNewRequest();
    }

    public String getSign() {
        return mRequestReformer.getSign();
    }

}
