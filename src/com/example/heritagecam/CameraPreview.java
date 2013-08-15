package com.example.heritagecam;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	 
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    public static boolean released = false;
    //Constructor that obtains context and camera
    public CameraPreview(Context context, Camera camera) {
        super(context);
        Log.d("CamPreview","Cam constructor called");
        this.mCamera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this); // we get notified when underlying surface is created and destroyed
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //this is a deprecated method, is not requierd after 3.0
    }
 
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
        	Log.d("CameraPreview","In surface created");
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            //mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
        	
        }
 
    }
     
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        Log.d("CameraPreview","preview stopped");
        mCamera.release();
        Log.d("CameraPreview","preview released");
    }
 
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
            int width, int height) {
        // start preview with new settings
    	Log.d("CameraPreview","In surface changed");
        try {
        	mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        	   
        	} catch (Exception e) {
        }
    }
    
    AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
    	  @Override
    	  public void onAutoFocus(boolean success, Camera camera) {
    	   ;
    	  }
    	};
     
}