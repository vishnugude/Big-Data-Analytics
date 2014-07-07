package com.vishnu.vishnulab;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class Main_Activity extends Activity{
	Button b1,b2,b3,b4;
	private TextView t1;
	private EditText edittext;
	String output="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//_activity = UnityPlayer.currentActivity;
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		b1 = (Button) findViewById(R.id.createBtn);
		b2 = (Button) findViewById(R.id.insertBtn);
		b3 = (Button) findViewById(R.id.retrieveBtn);
		b4 = (Button) findViewById(R.id.deleteBtn);
		t1 = (TextView) findViewById(R.id.resultView);
		edittext=(EditText)findViewById(R.id.editText1);
		b1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				
				output=runHbaseCreate(edittext.getText().toString());
				t1.setText(output);
			}
		});
		b2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				output=runHbaseInsert(edittext.getText().toString());
				t1.setText(output);
			}
		});
		b3.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				output=runHbaseRetrieveAll(edittext.getText().toString());
				t1.setText(output);
				
			}
		});
		b4.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				output=runHbaseDelete(edittext.getText().toString());
				t1.setText(output);
			}
		});
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	public String runHbaseCreate(String tname)
	{
		 try {
	         String furl="http://10.0.2.2:8080/HbaseWS/jaxrs/generic/hbaseCreate/"+tname+"/GeoLocation:Date:Accelerometer:Humidity:Temperature";
			 URL url = new URL(furl);
	            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	            String inputLine,result=""; 
	 
	            while ((inputLine = in.readLine()) != null) {
	                // Process each line.
	                //System.out.println(inputLine);
	            	result+=inputLine;
	            	
	            }
	            in.close(); 
	 return result.toString();
	        } catch (MalformedURLException me) {
	            System.out.println(me); 
	            return me.toString();
	 
	        } catch (IOException ioe) {
	            System.out.println(ioe);
	            return ioe.toString();
	        }
		 
	 }
	
	
	public String runHbaseInsert(String tname)
	{
		 try {
			   String furl= "http://10.0.2.2:8080/HbaseWS/jaxrs/generic/hbaseInsert/"+tname+"/C:-Users-vgz8b-Documents-sensor.txt/GeoLocation:Date:Accelerometer:Humidity:Temperature";
			 URL url = new URL(furl);
	            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	            String inputLine,result=""; 
	 
	            while ((inputLine = in.readLine()) != null) {
	                // Process each line.
	                System.out.println(inputLine);
	                result+=inputLine;
	            }
	            in.close(); 
	 return result.toString();
	        } catch (MalformedURLException me) {
	            System.out.println(me); 
	            return me.toString();
	 
	        } catch (IOException ioe) {
	            System.out.println(ioe);
	            return ioe.toString();
	        }
	}
	
	
	public String runHbaseRetrieveAll(String tname)
	{
		
		try {
	        String furl="http://10.0.2.2:8080/HbaseWS/jaxrs/generic/hbaseRetrieveAll/"+tname;
			URL url = new URL(furl);
	            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	            String inputLine="",result="";
	 
	            while ((inputLine = in.readLine()) != null) {
	                // Process each line.
	                System.out.println(inputLine);
	                result+=inputLine;
	                
	            }
	            
	            in.close(); 
	            return result.toString();
	 
	        } catch (MalformedURLException me) {
	            System.out.println(me);
	            
	            return me.toString();
	 
	        } catch (IOException ioe) {
	            System.out.println(ioe);
	            return ioe.toString();
	        }
		
	}
	
	
	public String runHbaseDelete(String tname)
	{
		 try {
			 String furl="http://10.0.2.2:8080/HbaseWS/jaxrs/generic/hbasedeletel/"+tname;
			 URL url = new URL(furl);
			 
	            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	            String inputLine,result=""; 
	 
	            while ((inputLine = in.readLine()) != null) {
	                // Process each line.
	                //System.out.println(inputLine);
	            	result+=inputLine;
	            }
	            in.close(); 
	 return result.toString();
	        } catch (MalformedURLException me) {
	            System.out.println(me); 
	            return me.toString();
	 
	        } catch (IOException ioe) {
	            System.out.println(ioe);
	            return ioe.toString();
	        }
	}

}
