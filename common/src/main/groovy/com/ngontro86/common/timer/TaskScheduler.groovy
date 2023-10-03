package com.ngontro86.common.timer

import com.ngontro86.common.annotations.DBOEComponent

import javax.annotation.PostConstruct

@DBOEComponent
class TaskScheduler {

    @Delegate(includes = ['schedule', 'scheduleAtFixedRate'])
    private Timer timer

    @PostConstruct
    private void init() {
        timer = new Timer()
    }

}
