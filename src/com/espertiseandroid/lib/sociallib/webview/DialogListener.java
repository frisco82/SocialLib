package com.espertiseandroid.lib.sociallib.webview;


public abstract interface DialogListener {

    /**
     * Called when a dialog completes.
     * 
     * Executed by the thread that initiated the dialog.
     * @param url 
     * 
     */
    public void onComplete(String url);

    /**
     * Called when a dialog has an error.
     * 
     * Executed by the thread that initiated the dialog.
     * 
     */        
    public void onError(String message, int errorCode, String failingUrl);
}
