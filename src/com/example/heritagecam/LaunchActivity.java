package com.example.heritagecam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;


public class LaunchActivity extends Activity {
	public static int lang_id = 1 ;//The id of the language (English - 1, Hindi - 0)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Launch","onCreate called");
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_launch);
		Button button = (Button) findViewById(R.id.btnNext);      
		//Adding Listener to button
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LaunchActivity.this,MainActivity.class);
				startActivityForResult(i,0);
			}});
	}
	 

	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     if(resultCode==0){
	    	 Log.d("Launch","Result received");
	         finish();
	     }
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

}