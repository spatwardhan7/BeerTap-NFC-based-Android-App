package com.example.navigator;


import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements CreateNdefMessageCallback,
OnNdefPushCompleteCallback{

	NfcAdapter mNfcAdapter;
	private String menuInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) 
        {
        	Toast.makeText(getApplicationContext(), "NFC not available on this Device", Toast.LENGTH_LONG).show();
        } 
        else 
        {
        	// Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    

	public void onNdefPushComplete(NfcEvent event) {
		// TODO Auto-generated method stub
		
	}

	public NdefMessage createNdefMessage(NfcEvent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        menuInfo = new String(msg.getRecords()[0].getPayload());
        
        Toast.makeText(getApplicationContext(), menuInfo, Toast.LENGTH_LONG).show();
    }
    
    public void onClickContinue(View view)
    {
		System.out.println("Inside onclick");
    	Intent intent_DisplayMenu = new Intent(this, DisplayMenu.class);
    	if(menuInfo == null)
    		menuInfo="http://dl.dropbox.com/u/41621778/BeerTap/Tindrum/";
		intent_DisplayMenu.putExtra("MenuURL", menuInfo);
		System.out.println("Menu URL = "+menuInfo);
		// Call next screen to display Menu for Restaurant 
		startActivity(intent_DisplayMenu);   	
    }
    
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }    
}
