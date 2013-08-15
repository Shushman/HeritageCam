package com.example.heritagecam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

public class MainActivity extends Activity implements SensorEventListener {

		public static Camera mCamera;
		private static CameraPreview mCameraPreview;
		private FrameLayout preview;
		private SensorManager mSensorManager;
		private Sensor mSensor;
		private static int limit = 24; //The number of cycles for which user must hold the phone still
		private static int count=limit;
		private float oldvals[] = new float[3];
		private float gravity[] = new float[3];
		private float lin_acc[] = new float[3];
		private final float LOW_THRESH = 1.6f;
	    private static boolean flag = false;
	    public static File pictureFile;
		public static String fpath;
	    public final static String ANNOTATED = "com.example.heritagecam.ISANNOTATED";
	    public final static String BYTEARRAY = "com.example.heritagecam.BYTEARRAY";
	    public static boolean pictaken;
	    private MenuItem English;
	    private MenuItem Hindi;
	    
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        Log.d("Main","Oncreate called");
	       
	        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
		    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    }
		/**
	     * Helper method to access the camera returns null if
	     * it cannot get the camera or does not exist
	     * @return
	     */
		
	    public static Camera getCameraInstance() {
	        Camera camera = null;
	        try {
	            camera = Camera.open();
	        } catch (Exception e) {
	            Log.d("Main","Get Camera gives exception");
	        }
	        return camera;
	    }
	 
	    protected void onResume() {
		    super.onResume();
		    mCamera = getCameraInstance();
	        Camera.Parameters cp = mCamera.getParameters();
	        cp.setPictureSize(1280,960);
	        mCamera.setParameters(cp);
	        mCameraPreview = new CameraPreview(this, mCamera); 
	        preview = (FrameLayout) findViewById(R.id.camera_preview);
	        preview.addView(mCameraPreview);
		    count = 0;
			flag=false;
			Log.d("Main","Resume called");
		    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		    //mCamera.startPreview();
		  }
	
		  @Override
	    protected void onPause() {
		    super.onPause();
		    mSensorManager.unregisterListener(this);
		 }
		  
		  
		private boolean checkCameraHardware() {
			if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
				// this device has a camera
				return true;
			} else {
				// no camera on this device
				return false;
			}
		 }
	    
		AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				mCamera.takePicture(null,null,mPicture); 
			}
		};
		
		
		PictureCallback mPicture = new PictureCallback() {
	    	 
	        @Override
	        public void onPictureTaken(byte[] data, Camera camera) {
            	count++;
            	fpath = getOverwrittenFile(data);
            	String res="";
            	//res = search(fpath) - native function with imgpath
            	if(res.equals("No Annotation Found"))
            		goToNewActivity(data,false);
            	else
            		goToNewActivity(data,true);
	        }
	         
	    };
	    
	    private void goToNewActivity(byte[] data, boolean isAnnotated){
	    	Intent intent = new Intent(this, MenuActivity.class);
	    	Log.d("Main","Intent created");
	    	intent.putExtra(BYTEARRAY, data);
	    	intent.putExtra(ANNOTATED, isAnnotated);
	    	Log.d("Main","Extra put");
	    	startActivity(intent);
	    }
	    
	    private String getOverwrittenFile(byte[] data){

			//Obtains the file path where the taken image will be saved
	    	File testimgfile = getBaseContext().getFileStreamPath("TestImage.jpg");

			// Create a media file name
			File mediaFile = new File(testimgfile.getAbsolutePath());
			try {
				FileOutputStream fos = new FileOutputStream(mediaFile);
				fos.write(data);
				fos.close();
				Log.d("Main","File overwritten");
			}catch (FileNotFoundException e) {
				finish();
			}catch (IOException e) {
				finish();
			}
			
			return testimgfile.getAbsolutePath();
		}

	    
		@Override
		public void onSensorChanged(SensorEvent event){

			  final float alpha = 0.8f;

			  // Isolate the fore of gravity with the low-pass filter.
			  gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			  gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			  gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

			  // Remove the gravity contribution with the high-pass filter.
			  lin_acc[0] = event.values[0] - gravity[0];
			  lin_acc[1] = event.values[1] - gravity[1];
			  lin_acc[2] = event.values[2] - gravity[2];

			  double d = Math.abs(lin_acc[0]-oldvals[0])+Math.abs(lin_acc[1]-oldvals[1])+Math.abs(lin_acc[2]-oldvals[2]);
				if(flag==false&&d<LOW_THRESH){
					count++;
					if(count==limit){
						mCamera.autoFocus(autoFocusCallback);
						flag=true;
					}
				}
				else{
					count=0;
				}
			
				for(int i=0;i<3;i++){
					oldvals[i] = lin_acc[i];
				} 
			}
		
		
		@Override
		  public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		    // Do something here if sensor accuracy changes.
		  }
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			English = menu.add("English");
	        Hindi = menu.add("Hindi");
			return true;
		}
		
		public boolean onOptionsItemSelected(MenuItem item) {
			
			if(item==English){
				LaunchActivity.lang_id = 1;
			}
			else if (item==Hindi){
				LaunchActivity.lang_id = 0;
			}
			
			return true;
		}
	}
