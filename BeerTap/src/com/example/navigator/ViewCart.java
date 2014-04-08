package com.example.navigator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewCart extends Activity implements CreateNdefMessageCallback,
OnNdefPushCompleteCallback{

	ArrayList<String> menuItemList;
	ArrayAdapter<String> adapter;
	private static final int MESSAGE_SENT = 1;
	 NfcAdapter mNfcAdapter;
	   // TextView mInfoText;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);
        
        //Permissions to access the net. 
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectNetwork() // or .detectAll() for all detectable problems
        .penaltyDialog()  //show a dialog
        .permitNetwork() //permit Network access 
        .build());
        
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
        /*    mInfoText = (TextView) findViewById(R.id.textView);
            mInfoText.setText("NFC is not available on this device.");
      */  } else {
            // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            // Register callback to listen for message-sent success
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        
        final ListView cartListView = (ListView) findViewById(R.id.CartlistView);
        Set<MenuItemDetails> menuItems = ViewItemActivity.cartDetails.keySet();
        
        menuItemList = new ArrayList<String>();
        
        for(MenuItemDetails mid: menuItems) {
        	int count = ViewItemActivity.cartDetails.get(mid);
        	String item = mid.getName() + "\t" + count + "\t"+  mid.getPrice() * count;
        	menuItemList.add(item);
        }
        
		//RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)cartListView.getLayoutParams();
	//	params.gravity = Gravity.RIGHT;
	//	params.WRAP_CONTENT;
        
        adapter=new ArrayAdapter<String>(this,
    			android.R.layout.simple_list_item_1,menuItemList);
      //  cartListView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        cartListView.setAdapter(adapter);
    	cartListView.setItemsCanFocus(false);
    	
  
		// For Delete function 
    	registerForContextMenu(cartListView);
	
    }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
				
			
			String keyString=menuItemList.get(menuInfo.position);
			keyString = keyString.substring(0,keyString.indexOf("\t"));
			MenuItemDetails key = new MenuItemDetails(keyString);
		switch (item.getItemId()) 
		{
			case R.id.DeleteItem:
				System.out.println("Inside Delete Item");
				/*
							Insert logic to remove item from map 
					
				*/	
				menuItemList.remove(menuInfo.position);
				ViewItemActivity.cartDetails.remove(key);
				adapter.notifyDataSetChanged();
				break;	
		}
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
	
	
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_view_cart, menu);
        return true;
    }
    
    
    
    public void onClickCheckout(View view)
    {
    	
    	try{
        	
        	String result = "";
    		String line = null;
    		String items = "";
    		double price = 0;
    		Random generator = new Random();
    		int tableNo = generator.nextInt(20)+1;
    		Set<MenuItemDetails> menuItems = ViewItemActivity.cartDetails.keySet();
    		 for(MenuItemDetails mid: menuItems) {
    	        	int count = ViewItemActivity.cartDetails.get(mid);
    	        	items = items+mid.getName() + "-" + count+",";
    	        	
    	        	price = price + count*mid.getPrice();
    	        }
    		
		HttpPost httppost = new HttpPost("http://m2.cip.gatech.edu/developer/foodies/widget/base_widget/content/api/orders");
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("items", items.substring(0, items.length()-1)));
		nvps.add(new BasicNameValuePair("tableNo", Integer.toString(tableNo)));
		nvps.add(new BasicNameValuePair("price", Double.toString(price)));
		
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
        String orderNo = result.substring(4,result.length()-1);
        Toast.makeText(getApplicationContext(), "Order has been placed. Your Order No. is: "+orderNo, Toast.LENGTH_LONG).show();
		
        
        
    	}catch(Exception e){
			e.printStackTrace();
		}
    }

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		// TODO Auto-generated method stub
		 mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
               // Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
            	Intent intent_receipt = new Intent(getApplicationContext(), ReceiptActivity.class);
               // intent_receipt.putExtra("orderNo", orderNo);
                startActivity(intent_receipt);
                break;
            }
        }
    };
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		// TODO Auto-generated method stub
		String items = "";
		double price = 0;
		Random generator = new Random();
		int tableNo = generator.nextInt(20)+1;
		Set<MenuItemDetails> menuItems = ViewItemActivity.cartDetails.keySet();
		 for(MenuItemDetails mid: menuItems) {
	        	int count = ViewItemActivity.cartDetails.get(mid);
	        	items = items+mid.getName() + "-" + count+",";
	        	
	        	price = price + count*mid.getPrice();
	        }
		
		items = items.substring(0, items.length()-1);
		String text = items+"|"+price+"|"+tableNo;	 
		
		NdefMessage msg = new NdefMessage(NdefRecord.createMime(
	                "application/com.example.android.beam2", text.getBytes()));
		
		return msg;
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
	      //  mInfoText.setText(print);
	        System.out.println("Process Intent: "+print);
	        /*final Dialog dialog = new Dialog(getApplicationContext());
	        dialog.addContentView(imageView, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));*/
	        //textView.ad
	    }
}
