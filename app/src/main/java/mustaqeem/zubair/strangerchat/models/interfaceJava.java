package mustaqeem.zubair.strangerchat.models;

import android.webkit.JavascriptInterface;

import mustaqeem.zubair.strangerchat.activities.CallActivity;

public class interfaceJava {
    CallActivity callActivity;
    public interfaceJava(CallActivity callActivity){
        this.callActivity = callActivity;

    }

@JavascriptInterface
    public void onPeerConnected(){
       callActivity.onPeerConnected();
    }
}
