package com.example.android.beam;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceOrder extends Activity implements CreateNdefMessageCallback,
OnNdefPushCompleteCallback{

	NfcAdapter mNfcAdapter;
	String items;
	String price;
	String tableNo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectNetwork() // or .detectAll() for all detectable problems
        .penaltyDialog()  //show a dialog
        .permitNetwork() //permit Network access 
        .build());
        
        
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
       /*     mInfoText = (TextView) findViewById(R.id.textView);
            mInfoText.setText("NFC is not available on this device.");
       */ } else {
            // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
       
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_place_order, menu);
        return true;
    }

	public void onNdefPushComplete(NfcEvent event) {
		// TODO Auto-generated method stub
		
	}

	public NdefMessage createNdefMessage(NfcEvent event) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onResume() {
	        super.onResume();
	        // Check to see that the Activity started due to an Android Beam
	        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
	            processIntent(getIntent());
	        }
	}
	
	void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        /*byte[] image = msg.getRecords()[0].getPayload();
        InputStream is = new ByteArrayInputStream(image);
        Bitmap bmp = BitmapFactory.decodeStream(is);
        
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bmp);
        mInfoText.setText(""+ bmp.getByteCount());*/
        String print = new String(msg.getRecords()[0].getPayload());
        System.out.println("My NDef Reciever SOP: "+print);
        
        StringTokenizer st = new StringTokenizer(print, "|");
        if(st.hasMoreTokens()){	
        	items=st.nextToken();
        	price=st.nextToken();
        	tableNo=st.nextToken();
        }
        String ordernumber = httpPostOrder(items,price,tableNo);
        Toast.makeText(getApplicationContext(), "My NDef Reciever: "+print, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "My Order Number: "+ordernumber, Toast.LENGTH_LONG).show();
        /*final Dialog dialog = new Dialog(getApplicationContext());
        dialog.addContentView(imageView, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));*/
        //textView.ad
    }
	public String httpPostOrder(String items, String price, String tableNo){
		 String orderNo=null;
	        try{
	        String result = "";
			String line = null;
			
	        HttpPost httppost = new HttpPost("http://m2.cip.gatech.edu/developer/foodies/widget/base_widget/content/api/orders");
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = null;
			
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("items", items));
			nvps.add(new BasicNameValuePair("tableNo", tableNo));
			nvps.add(new BasicNameValuePair("price", price));
			
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			response = httpclient.execute(httppost);
			
	        HttpEntity entity = response.getEntity();
	        
	        InputStream is = entity.getContent();
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	        StringBuilder sb = new StringBuilder();
	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }
	        is.close();
	        result=sb.toString();
	        System.out.println("my Result==="+result);
	        orderNo = result.substring(4,result.length()-1);
	        Toast.makeText(getApplicationContext(), "Order has been placed. Your Order No. is: "+orderNo, Toast.LENGTH_LONG).show();
	        }catch(Exception e){
	        	System.out.println("Exception Place Order");
	        }
	        TextView text = (TextView) findViewById(R.id.textView1);
	        text.setText("Order Placed with Number: "+orderNo);
	        return orderNo;
	}
}
