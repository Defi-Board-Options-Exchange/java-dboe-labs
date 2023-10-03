package com.ngontro86.common.net

import com.ngontro86.common.Handler

// A marker
interface PublisherHandler<SEND> extends Handler<SEND>{
    public void stop()
}