package org.abhijith;

import org.abhijith.ui.HomeUI;
public class Main {
    public static void main( String[] args ) {
        // You can send an argument simply to test whether main is working
        boolean isTesting = args.length != 0;

        // Creating a new HomeUI object
        HomeUI homeUI = new HomeUI();

        // If not testing, invoke the object
        if ( !isTesting ) homeUI.mainInterface();
    }
}