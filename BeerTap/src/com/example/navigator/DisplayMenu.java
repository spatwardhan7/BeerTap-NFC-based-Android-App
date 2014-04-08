package com.example.navigator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


class ImageViewOnClickListener implements OnClickListener {

	private MenuItemDetails mid;
	
	@Override
	public void onClick(View arg0) {
		
		Log.d("ImageViewOnClickListener", "OnClickFunction Called with: " + this.mid);
		Intent viewItemIntent = new Intent(arg0.getContext(), ViewItemActivity.class);
		viewItemIntent.putExtra("itemName", this.mid.getName());
		viewItemIntent.putExtra("desc", this.mid.getDescription());
		viewItemIntent.putExtra("price", this.mid.getPrice());
		viewItemIntent.putExtra("spice",this.mid.getSpiceLevel());
		viewItemIntent.putExtra("rootPath", DisplayMenu.rootURL);
		
		
		arg0.getContext().startActivity(viewItemIntent);
	}
	
	public ImageViewOnClickListener(MenuItemDetails mid) {
		this.mid = mid;
	}
	
}


public class DisplayMenu extends Activity {

	//private String rootURL = "";
	public static String rootURL = null;
	
	public static Map<String, MenuItemDetails> menuItems = new LinkedHashMap<String, MenuItemDetails>(); // Menu item and details
	public static Map<String,ArrayList<String>> categoryNamesAndItems = new LinkedHashMap<String,ArrayList<String>>();

	private int currentViewId = 0 ;
	
	/* Remove Later*/
	Bitmap resizedbitmap;
	
	
	public void onCreate(Bundle savedInstanceState) {
		Intent intent_fromMain = getIntent();
		rootURL = intent_fromMain.getStringExtra("MenuURL");
	
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy); 
    	Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        int width=200;
        int height=200;
        resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
        
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        parseNamesFile();
        getDescriptions();
        InitializeUI();
    }
	
	private Map<String,String> parseInputStream(InputStream is) {
		LinkedHashMap<String,String> ret = new LinkedHashMap<String,String>();
		String inputLine = "";
		 BufferedReader in = new BufferedReader(new InputStreamReader(is));
		try {
			while ((inputLine = in.readLine()) != null) {
				StringTokenizer tok = new StringTokenizer(inputLine,"=");
				while(tok.hasMoreTokens()) {
					String key = tok.nextToken();
					String value = tok.nextToken();
					ret.put(key, value);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	
	private void InitializeUI() {
		/**
		 * For Network Capability, Also add <uses-permission android:name="android.permission.INTERNET"/>
		 * to mainfest
		 */
		Log.d("Main", "Before Creating Layouts");
        createLayout();
        
        Log.d("Main", "After Creating Layouts");
        
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_test);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy); 
    	Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        int width=200;
        int height=200;
        resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
        
		InitializeUI();
	}
	
	
	public void onClickViewCart(View view)
	{					
			Intent intent_ShowCart = new Intent(this, ViewCart.class);
			startActivity(intent_ShowCart);		
	}

    private void getDescriptions() {
    	URL desc;
        Properties prop = new Properties();
        try {
			desc = new URL(rootURL +"desc.txt") ;
			InputStream is = desc.openStream();
			prop.load(is);
			Set<String> itemNames = prop.stringPropertyNames();
			for (String item: itemNames) {
				MenuItemDetails mid = parseItemDescription(item, prop.getProperty(item));
				menuItems.put(item, mid);
			}
			is.close();
		} catch (MalformedURLException e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	private MenuItemDetails parseItemDescription(String item, String desc) {
		MenuItemDetails mid;
		StringTokenizer tok = new StringTokenizer(desc,"|");
		double price = Double.parseDouble(tok.nextToken());
		int spiceLevel = Integer.parseInt(tok.nextToken());
		String description = tok.nextToken();
		mid = new MenuItemDetails(item, description, spiceLevel, price);
		return mid;
	}

	private void createLayout() {
		View linearLayout = findViewById(R.id.testActivityLinearLayout);
		View imageView = (ImageView) findViewById(R.id.cart);
		imageView.getLayoutParams().height=70;
		imageView.getLayoutParams().width=70;
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)imageView.getLayoutParams();
		params.gravity = Gravity.RIGHT;
		
		Iterator itr = categoryNamesAndItems.keySet().iterator();
		while(itr.hasNext()) {
			String category = (String) itr.next();
			TextView categoryTextView = (TextView)createCategoryTextView(category);
			((LinearLayout) linearLayout).addView(categoryTextView);
			
			HorizontalScrollView hsView =  createHorizontalScrollView(category);
			((LinearLayout) linearLayout).addView(hsView);
		}

    	
	}

	private HorizontalScrollView createHorizontalScrollView(String category) {
		
		HorizontalScrollView hsView = new HorizontalScrollView(this);
        hsView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        LinearLayout ll = createInnerLinearLayout(category);
        hsView.addView(ll);
		return hsView;
		
	}

	private LinearLayout createInnerLinearLayout(String category) {
		  LinearLayout ll = new LinearLayout(this);
	      ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	      ll.setOrientation(LinearLayout.HORIZONTAL);
	        
	      ArrayList<String> itemsInCategory = categoryNamesAndItems.get(category);
	      
	      for(String item: itemsInCategory) {
	    	  FrameLayout imageView = createFrameLayout(item);
	    	  ll.addView(imageView);
	      }
	      
	      return ll;
	}
	
	

	private FrameLayout createFrameLayout(String item) {
		
		FrameLayout fl = new FrameLayout(this);
		ImageView imageView = createImageView(item);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		TextView tv = new TextView(this);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		tv.setText(item.replaceAll("_", "\n"));tv.setTextColor(Color.BLACK);
		
		fl.addView(imageView);fl.addView(tv);
		return fl;
	}

	private ImageView createImageView(String item) {
		URL picURL = null  ;
		ImageView imageView = new ImageView(this);
    	MenuItemDetails mid = menuItems.get(item);
    	imageView.setId(++currentViewId);
    	OnClickListener listener = new ImageViewOnClickListener(mid);
    	imageView.setOnClickListener(listener);
	    try {
	    	picURL = new URL(rootURL +"images/small/" + item + "_sm.jpg");
		    InputStream is = picURL.openStream();
		    BufferedInputStream bis = new BufferedInputStream(is); 
		    int width=200;
	        int height=200;
	        Bitmap bmp = BitmapFactory.decodeStream(bis);
	        resizedbitmap=Bitmap.createScaledBitmap(bmp, width, height, true);
		    
		    bis.close(); 
		    is.close(); 

		    } catch (Exception e) {
		    	Log.e("BitmapImageCreation", "Remote Image Exception: " + picURL.toString(), e);
		    } finally {
		    	
		    }
	    imageView.setImageBitmap(resizedbitmap);
		return imageView;
	}

	private View createCategoryTextView(String category) {
		TextView textView = new TextView(this);
		textView.setText(category.replaceAll("_", " "));
		textView.setId(++currentViewId);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(20);
		textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		return textView;
	}

	private void parseNamesFile() {
    	URL root;
        Properties prop = new Properties();
        try {
			root = new URL(rootURL +"names.txt") ;
			InputStream is = root.openStream();
			//prop.load(is);
			LinkedHashMap<String, String> names = (LinkedHashMap<String, String>) parseInputStream(is);
			Collection c = names.keySet();
			Iterator itr = c.iterator();
			while(itr.hasNext()) {
				String key = (String)itr.next();
				ArrayList<String> values = parseCategoryValues(names.get(key));
				categoryNamesAndItems.put(key,values);
				for(String v : values) {
					menuItems.put(v, new MenuItemDetails(v));
				}
			}
			is.close();
		} catch (MalformedURLException e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
        
        
	}

	private ArrayList<String> parseCategoryValues(String values) {
		ArrayList<String> ret = new ArrayList<String>();
		
		StringTokenizer tok = new StringTokenizer(values, ",");
		while(tok.hasMoreElements()) {
			ret.add(tok.nextToken());
		}
		
		return ret;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_test, menu);
        return true;
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
        rootURL = new String(msg.getRecords()[0].getPayload());
        Toast.makeText(getApplicationContext(), rootURL, Toast.LENGTH_LONG).show();
        
    }
}
