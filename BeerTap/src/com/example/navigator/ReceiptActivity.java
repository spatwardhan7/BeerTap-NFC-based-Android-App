package com.example.navigator;

import java.util.ArrayList;
import java.util.Set;



import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ReceiptActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        
        ArrayAdapter<String> adapter;
        
        final ListView cartListView = (ListView) findViewById(R.id.listView1);
        Set<MenuItemDetails> menuItems = ViewItemActivity.cartDetails.keySet();
        
        ArrayList<String> menuItemList = new ArrayList<String>();
        
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
    	
    //	 String orderNo  = getIntent().getStringExtra("orderNo");
    	// TextView source = (TextView) findViewById(R.id.textView2);
    	// source.setText(orderNo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_receipt, menu);
        return true;
    }
}
