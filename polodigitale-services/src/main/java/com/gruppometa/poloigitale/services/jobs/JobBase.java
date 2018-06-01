package com.gruppometa.poloigitale.services.jobs;

import com.gruppometa.poloigitale.services.objects.Message;

/**
 * Created by ingo on 16/12/16.
 */
public class JobBase {
    protected String message;
    protected String status;

    public Message getStatus(){
        Message messageObj = new Message();
        messageObj.setStatus(status);
        messageObj.setMessage(message);
        return messageObj;
    }
}
