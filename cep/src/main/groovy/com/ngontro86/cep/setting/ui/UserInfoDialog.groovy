package com.ngontro86.cep.setting.ui

import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JTextField
import javax.swing.SwingConstants
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class UserInfoDialog extends JDialog {
    private final JLabel jlblUsername = new JLabel("Username")
    private final JLabel jlblPassword = new JLabel("Password")

    private final JTextField jtfUsername = new JTextField(15)
    private final JPasswordField jpfPassword = new JPasswordField()

    private final JButton jbtOk = new JButton("Login")
    private final JButton jbtCancel = new JButton("Cancel")

    private final JLabel jlblStatus = new JLabel(" ")

    UserInfoDialog(final JFrame parent, boolean modal) {
        super(parent, modal)

        JPanel p3 = new JPanel(new GridLayout(2, 1))
        p3.add(jlblUsername)
        p3.add(jlblPassword)

        JPanel p4 = new JPanel(new GridLayout(2, 1))
        p4.add(jtfUsername)
        p4.add(jpfPassword)

        JPanel p1 = new JPanel()
        p1.add(p3)
        p1.add(p4)

        JPanel p2 = new JPanel()
        p2.add(jbtOk)
        p2.add(jbtCancel)

        JPanel p5 = new JPanel(new BorderLayout())
        p5.add(p2, BorderLayout.CENTER)
        p5.add(jlblStatus, BorderLayout.NORTH)
        jlblStatus.setForeground(Color.RED)
        jlblStatus.setHorizontalAlignment(SwingConstants.CENTER)

        setLayout(new BorderLayout())
        add(p1, BorderLayout.CENTER)
        add(p5, BorderLayout.SOUTH)
        pack()
        setLocationRelativeTo(null)
        setDefaultCloseOperation(DISPOSE_ON_CLOSE)

        addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent e) {
                super.windowClosing(e)
            }
        })

        jbtOk.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                if ("stackoverflow".equals(String.valueOf(jpfPassword.getPassword()))
                        && "stackoverflow".equals(jtfUsername.getText())) {
                    parent.setVisible(true)
                    setVisible(false)
                } else {
                    jlblStatus.setText("Invalid username or password")
                }
            }
        })
        jbtCancel.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                setVisible(false)
                parent.dispose()
                System.exit(0)
            }
        })
    }
}
