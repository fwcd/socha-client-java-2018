package com.thedroide.clienttester;

import com.thedroide.clienttester.ui.ClientTesterApp;

/**
 * <u>This tester is STILL IN ALPHA and thus has a lot of bugs.</u><br><br>
 * 
 * The "client tester" is a seperate application that
 * may be used to test clients against each other.
 */
public class ClientTesterMain {
	public static void main(String[] args) {
		new ClientTesterApp("Software Challenge Client Tester", 640, 480);
	}
}
