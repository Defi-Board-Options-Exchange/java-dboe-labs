package com.ngontro86.cep.setting.ui

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.utils.StringUtils
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.swing.*
import java.awt.*

@DBOEComponent
class UserInfoSetup {
    @Logging
    protected Logger logger

    @ConfigValue(config = "useremail")
    private String email

    @ConfigValue(config = "allowUserToSetUp")
    private Boolean allowUserToSetUp = true

    @PostConstruct
    void init() {
        if (allowUserToSetUp) {
            if (StringUtils.isEmpty(email)) {
                EventQueue.invokeAndWait(new Runnable() {
                    @Override
                    void run() {
                        initGUI()
                    }
                })
            }
        }
    }

    void initGUI() {
        logger.info("Need to set user info...")
        def dBfrm = new JFrame("HedgeLah User Initial Setup")
        dBfrm.setVisible(true)
        dBfrm.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)
        dBfrm.setBounds(100, 100, 600, 150)
        dBfrm.setLayout(new BoxLayout(dBfrm.getContentPane(), BoxLayout.X_AXIS))
        dBfrm.setLocationRelativeTo(null)
        def userInfoDialog = new UserInfoDialog(dBfrm, true)
        userInfoDialog.setVisible(true)
    }

}
