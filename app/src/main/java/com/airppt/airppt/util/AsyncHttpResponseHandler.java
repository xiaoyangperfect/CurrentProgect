package com.airppt.airppt.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.airppt.airppt.activity.LoginActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by user on 2015/8/3.
 */
public abstract class AsyncHttpResponseHandler implements ResponseHandlerInterface {
    private static final String LOG_TAG = "AsyncHttpResponseHandler";
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int PROGRESS_MESSAGE = 4;
    protected static final int RETRY_MESSAGE = 5;
    protected static final int CANCEL_MESSAGE = 6;
    protected static final int BUFFER_SIZE = 4096;
    public static final String DEFAULT_CHARSET = "UTF-8";
    private String responseCharset = "UTF-8";
    private Handler handler;
    private boolean useSynchronousMode;
    private URI requestURI = null;
    private Header[] requestHeaders = null;
    private Context context;

    public URI getRequestURI() {
        return this.requestURI;
    }

    public Header[] getRequestHeaders() {
        return this.requestHeaders;
    }

    public void setRequestURI(URI requestURI) {
        this.requestURI = requestURI;
    }

    public void setRequestHeaders(Header[] requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public boolean getUseSynchronousMode() {
        return this.useSynchronousMode;
    }

    public void setUseSynchronousMode(boolean value) {
        if(!value && Looper.myLooper() == null) {
            value = true;
            Log.w("AsyncHttpResponseHandler", "Current thread has not called Looper.prepare(). Forcing synchronous mode.");
        }

        if(!value && this.handler == null) {
            this.handler = new AsyncHttpResponseHandler.ResponderHandler(this);
        } else if(value && this.handler != null) {
            this.handler = null;
        }

        this.useSynchronousMode = value;
    }

    public void setCharset(String charset) {
        this.responseCharset = charset;
    }

    public String getCharset() {
        return this.responseCharset == null?"UTF-8":this.responseCharset;
    }

    public AsyncHttpResponseHandler() {
        this.setUseSynchronousMode(false);
    }

    public AsyncHttpResponseHandler(Context context) {
        this.setUseSynchronousMode(false);
        this.context = context;
    }

    public void onProgress(int bytesWritten, int totalSize) {
        Log.v("AsyncHttpResponseHandler", String.format("Progress %d from %d (%2.0f%%)", new Object[]{Integer.valueOf(bytesWritten), Integer.valueOf(totalSize), Double.valueOf(totalSize > 0?(double)bytesWritten * 1.0D / (double)totalSize * 100.0D:-1.0D)}));
    }

    public void onStart() {
    }

    public void onFinish() {
    }

    public void onSuccess(int var1, Header[] var2, byte[] var3){
        try {
            JSONObject object = new JSONObject(new String(var3));
            int errorCode = object.getInt("errorCode");
            if (errorCode == 70) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    };

    public abstract void onFailure(int var1, Header[] var2, byte[] var3, Throwable var4);

    public void onRetry(int retryNo) {
        Log.d("AsyncHttpResponseHandler", String.format("Request retry no. %d", new Object[]{Integer.valueOf(retryNo)}));
    }

    public void onCancel() {
        Log.d("AsyncHttpResponseHandler", "Request got cancelled");
    }

    public final void sendProgressMessage(int bytesWritten, int bytesTotal) {
        this.sendMessage(this.obtainMessage(4, new Object[]{Integer.valueOf(bytesWritten), Integer.valueOf(bytesTotal)}));
    }

    public final void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBytes) {
        this.sendMessage(this.obtainMessage(0, new Object[]{Integer.valueOf(statusCode), headers, responseBytes}));
    }

    public final void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable throwable) {
        this.sendMessage(this.obtainMessage(1, new Object[]{Integer.valueOf(statusCode), headers, responseBody, throwable}));
    }

    public final void sendStartMessage() {
        this.sendMessage(this.obtainMessage(2, (Object)null));
    }

    public final void sendFinishMessage() {
        this.sendMessage(this.obtainMessage(3, (Object)null));
    }

    public final void sendRetryMessage(int retryNo) {
        this.sendMessage(this.obtainMessage(5, new Object[]{Integer.valueOf(retryNo)}));
    }

    public final void sendCancelMessage() {
        this.sendMessage(this.obtainMessage(6, (Object)null));
    }

    protected void handleMessage(Message message) {
        Object[] response;
        switch(message.what) {
            case 0:
                response = (Object[])((Object[])message.obj);
                if(response != null && response.length >= 3) {
                    this.onSuccess(((Integer)response[0]).intValue(), (Header[])((Header[])response[1]), (byte[])((byte[])response[2]));
                } else {
                    Log.e("AsyncHttpResponseHandler", "SUCCESS_MESSAGE didn\'t got enough params");
                }
                break;
            case 1:
                response = (Object[])((Object[])message.obj);
                if(response != null && response.length >= 4) {
                    this.onFailure(((Integer)response[0]).intValue(), (Header[])((Header[])response[1]), (byte[])((byte[])response[2]), (Throwable)response[3]);
                } else {
                    Log.e("AsyncHttpResponseHandler", "FAILURE_MESSAGE didn\'t got enough params");
                }
                break;
            case 2:
                this.onStart();
                break;
            case 3:
                this.onFinish();
                break;
            case 4:
                response = (Object[])((Object[])message.obj);
                if(response != null && response.length >= 2) {
                    try {
                        this.onProgress(((Integer)response[0]).intValue(), ((Integer)response[1]).intValue());
                    } catch (Throwable var4) {
                        Log.e("AsyncHttpResponseHandler", "custom onProgress contains an error", var4);
                    }
                } else {
                    Log.e("AsyncHttpResponseHandler", "PROGRESS_MESSAGE didn\'t got enough params");
                }
                break;
            case 5:
                response = (Object[])((Object[])message.obj);
                if(response != null && response.length == 1) {
                    this.onRetry(((Integer)response[0]).intValue());
                } else {
                    Log.e("AsyncHttpResponseHandler", "RETRY_MESSAGE didn\'t get enough params");
                }
                break;
            case 6:
                this.onCancel();
        }

    }

    protected void sendMessage(Message msg) {
        if(!this.getUseSynchronousMode() && this.handler != null) {
            if(!Thread.currentThread().isInterrupted()) {
                this.handler.sendMessage(msg);
            }
        } else {
            this.handleMessage(msg);
        }

    }

    protected void postRunnable(Runnable runnable) {
        if(runnable != null) {
            if(!this.getUseSynchronousMode() && this.handler != null) {
                this.handler.post(runnable);
            } else {
                runnable.run();
            }
        }

    }

    protected Message obtainMessage(int responseMessageId, Object responseMessageData) {
        Message msg;
        if(this.handler == null) {
            msg = Message.obtain();
            if(msg != null) {
                msg.what = responseMessageId;
                msg.obj = responseMessageData;
            }
        } else {
            msg = Message.obtain(this.handler, responseMessageId, responseMessageData);
        }

        return msg;
    }

    public void sendResponseMessage(HttpResponse response) throws IOException {
        if(!Thread.currentThread().isInterrupted()) {
            StatusLine status = response.getStatusLine();
            byte[] responseBody = this.getResponseData(response.getEntity());
            if(!Thread.currentThread().isInterrupted()) {
                if(status.getStatusCode() >= 300) {
                    this.sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), responseBody, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
                } else {
                    this.sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), responseBody);
                }
            }
        }

    }

    byte[] getResponseData(HttpEntity entity) throws IOException {
        byte[] responseBody = null;
        if(entity != null) {
            InputStream instream = entity.getContent();
            if(instream != null) {
                long contentLength = entity.getContentLength();
                if(contentLength > 2147483647L) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }

                int buffersize = contentLength <= 0L?4096:(int)contentLength;

                try {
                    ByteArrayBuffer e = new ByteArrayBuffer(buffersize);

                    try {
                        byte[] tmp = new byte[4096];
                        int count = 0;

                        int l;
                        while((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                            count += l;
                            e.append(tmp, 0, l);
                            this.sendProgressMessage(count, (int)(contentLength <= 0L?1L:contentLength));
                        }
                    } finally {
                        AsyncHttpClient.silentCloseInputStream(instream);
                        AsyncHttpClient.endEntityViaReflection(entity);
                    }

                    responseBody = e.toByteArray();
                } catch (OutOfMemoryError var15) {
                    System.gc();
                    throw new IOException("File too large to fit into available memory");
                }
            }
        }

        return responseBody;
    }

    private static class ResponderHandler extends Handler {
        private final AsyncHttpResponseHandler mResponder;

        ResponderHandler(AsyncHttpResponseHandler mResponder) {
            this.mResponder = mResponder;
        }

        public void handleMessage(Message msg) {
            this.mResponder.handleMessage(msg);
        }
    }
}
