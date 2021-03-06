package com.aurnhammer.imageeffects;


import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ActivityHome extends MyActivity{
	
	// UI Elements
	private static ImageView ivHome;
	
	// Variables
	public static final String PHOTO_PATH_TAG = "com.imageeffects.photochooser.path";
	private static final int CAMERA_CODE = 0;
	private static final int LIBRARY_CODE = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.activity_home);
		flurryEventName = "Home";
		setMetrics();
		ivHome = (ImageView)findViewById(R.id.ivHome);
		ivHome.getLayoutParams().height = metrics.widthPixels;
		ivHome.requestLayout();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  if (resultCode == RESULT_OK) {
		  switch (requestCode) {
		  	  case LIBRARY_CODE:{
				  // currImageURI is the global variable used to hold the content:// URI of the image
			      Uri currImageURI = data.getData();
			      String directPath = getDirectSystemPathFromURI(currImageURI);
			      if(directPath != null){
			    	  Intent intent = new Intent(this, ActivityPhotoCrop.class);
			    	  intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			    	  System.out.println("DIRECT PATH " + directPath);
			  		  intent.putExtra(PHOTO_PATH_TAG, directPath);
			    	  startActivity(intent);
			    	  
			      } else{
			    	  Toast.makeText(this, "Currently cannot accept file.", 
			    			  Toast.LENGTH_SHORT).show();
			      }
			      break;
		  }case CAMERA_CODE:{
			  File file = getFile(this);
			  Intent intent = new Intent(this, ActivityPhotoCrop.class);
			  intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			  System.out.println("DIRECT PATH " + file.getAbsolutePath());
	  		  intent.putExtra(PHOTO_PATH_TAG, file.getAbsolutePath());
	    	  startActivity(intent);
		  }default:{
			  	Log.w("ActivityHome", "got strange request code from activity: " +
  				  requestCode);
		  	  }	  
		  }
	  }
	}
	
	public void onCreateAction(View v){
		final CharSequence[] options = {"Camera", "Library"};
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Choose photo...");
	    
	    builder.setItems(options, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int option) {
	        	switch(option) {
		        	case 0:{
		        		LoadFromCamera();
		        		break;
		        	}case 1:{
		        		LoadFromLibrary();
		        		break;
		        	}default:{
		        		Log.w("ActivityHome", "got strange item when loading image: " + option);
		        	}
	        	}
	        }
	    });
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	private void LoadFromLibrary() {
	    Intent intent = new Intent();
	    intent.setType("image/*");
	    intent.setAction(Intent.ACTION_GET_CONTENT);
	    startActivityForResult(Intent.createChooser(intent, "Select Picture"),LIBRARY_CODE);
	}
	
	private void LoadFromCamera() {
	    final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFile(this)) );
	    startActivityForResult(intent, CAMERA_CODE);
	}
	
	private File getFile(Context context){
		// Get the directory for the user's public pictures 
	    File album = new File(Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES),getResources().getString(R.string.app_name));
	    if(!album.exists()){
	    	 if (!album.mkdirs()) {
	 	        Log.e("ActivityHome", "Album directory not created");
	 	    }
	    }
	    return new File(album, "image.tmp"); // create a temporary file
	}
	  
	// Convert the image URI to the direct file system path of the image file
	@SuppressLint("NewApi")
	public String getDirectSystemPathFromURI(Uri contentUri){
		  
		String filePath = null;
		String [] column = {MediaStore.Images.Media.DATA};
		
		// Convert to direct file system path based on current path
		if(contentUri.toString().contains("content://media/external/images/media/")){
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(contentUri,
			    column, // Which columns to return
			    null,   // WHERE clause; which rows to return (all rows)
			    null,   // WHERE clause selection arguments (none)
			    null);  // Order-by clause (ascending by name)
				
			int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if(cursor.moveToFirst()){
			    filePath = cursor.getString(columnIndex);
			}
		}
		else if(contentUri.toString().contains(
			"content://com.android.providers.media.documents/document/")
			&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			String wholeID = DocumentsContract.getDocumentId(contentUri);
			String id = wholeID.split(":")[1];          
			String sel = MediaStore.Images.Media._ID + "=?";
			Cursor cursor = getContentResolver().
			                          query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
			                          column, sel, new String[]{id}, null);
			int columnIndex = cursor.getColumnIndex(column[0]);
			if(cursor.moveToFirst()){
			    filePath = cursor.getString(columnIndex);
			}
		}
		return filePath;
	  }
}
