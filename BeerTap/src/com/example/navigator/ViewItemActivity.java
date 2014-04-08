package com.example.navigator;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


class QuantitySpinnerSetOnItemSelectedListener implements OnItemSelectedListener {

	public int count = 0;
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		count = arg2+1;
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}

public class ViewItemActivity extends Activity {

	public static HashMap<MenuItemDetails, Integer> cartDetails = 
			new HashMap<MenuItemDetails, Integer>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        
        final String itemName = getIntent().getStringExtra("itemName");
        final String desc = getIntent().getStringExtra("desc");
        final int spiceLevel = getIntent().getIntExtra("spice", 0);
        final double price = getIntent().getDoubleExtra("price", 0.0);
        String rootPath = getIntent().getStringExtra("rootPath");
        TextView itemNameView = (TextView) findViewById(R.id.itemName);
        itemNameView.setText(itemName.replaceAll("_", " "));
        ImageView large = (ImageView)findViewById(R.id.foodItemImageView);
        Bitmap bmp = null;
        try {
	    	URL picURL = new URL(rootPath +"images/large/" + itemName + "_lg.jpg");
		    InputStream is = picURL.openStream();
		    BufferedInputStream bis = new BufferedInputStream(is); 
	        bmp = BitmapFactory.decodeStream(bis);
		    
		    bis.close(); 
		    is.close(); 

		    } catch (Exception e) {
		    	Log.e("BitmapImageCreation", "ViewItemActivity: Remote Image Exception", e);
		    } finally {
		    	
		    }
	    large.setImageBitmap(bmp);
	   
	    EditText tv = (EditText)findViewById(R.id.descText);
	    tv.setKeyListener(null);
	    tv.setText(desc);
	   
	    // tv.setTextColor(Color.WHITE);
	    
        TextView priceView = (TextView) findViewById(R.id.price);
        priceView.setText("$"+price);
      //  priceView.setTextColor(Color.WHITE);
        
        final String[] quants = {"1","2","3","4","5","6","7","8","9","10"};
        final Spinner quantitySpinner = (Spinner)findViewById(R.id.quantitySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        								android.R.layout.simple_list_item_1,quants);
        
        quantitySpinner.setAdapter(adapter);
    //    quantitySpinner.setBackgroundColor(Color.WHITE);
        
        final MenuItemDetails mid = new MenuItemDetails(itemName, desc, spiceLevel, price);
        final QuantitySpinnerSetOnItemSelectedListener listener = new QuantitySpinnerSetOnItemSelectedListener();
        quantitySpinner.setOnItemSelectedListener(listener);
        
        
        Button addToCart = (Button) findViewById(R.id.AddtoCartbutton);
        addToCart.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(cartDetails.containsKey(mid)) {
					int previous = cartDetails.get(mid);
					cartDetails.put(mid, listener.count + previous);
				}
				else 
					cartDetails.put(mid, listener.count);
				//Intent returnToTestActivity = new Intent(getApplicationContext(),TestActivity.class);
				finish();
			}
		});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_place_order, menu);
        return true;
    }
}
