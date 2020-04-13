package fr.groupe4.clientprojet.display.dialog.projectcreationdialog.controller;

import fr.groupe4.clientprojet.display.dialog.exitdialog.view.ExitDialog;
import fr.groupe4.clientprojet.display.dialog.projectcreationdialog.view.ProjectCreationDialog;
import fr.groupe4.clientprojet.display.mainwindow.view.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EventExitCreationDialog implements ActionListener {

    private static ProjectCreationDialog owner;

    public EventExitCreationDialog(ProjectCreationDialog source) {
        this.owner = source;
    }

    public static void exit() {

    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        owner.dispose();
        //new ExitDialog(owner);
    }


}
