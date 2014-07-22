package me.codeand.ahahpah_en;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationSequencesReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationVectorReader;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;
public class DataService extends IntentService implements BluetoothAdapter.LeScanCallback{
	public DataService() {
		super("DataService");
		// TODO Auto-generated constructor stub
	}
	private static final String TAG = "BluetoothGattActivity";

    private static final String DEVICE_NAME = "SensorTag";

    /* Humidity Service */
    private static final UUID HUMIDITY_SERVICE = UUID.fromString("f000aa20-0451-4000-b000-000000000000");
    private static final UUID HUMIDITY_DATA_CHAR = UUID.fromString("f000aa21-0451-4000-b000-000000000000");
    private static final UUID HUMIDITY_CONFIG_CHAR = UUID.fromString("f000aa22-0451-4000-b000-000000000000");
    /* Barometric Pressure Service */
    private static final UUID PRESSURE_SERVICE = UUID.fromString("f000aa40-0451-4000-b000-000000000000");
    private static final UUID PRESSURE_DATA_CHAR = UUID.fromString("f000aa41-0451-4000-b000-000000000000");
    private static final UUID PRESSURE_CONFIG_CHAR = UUID.fromString("f000aa42-0451-4000-b000-000000000000");
    private static final UUID PRESSURE_CAL_CHAR = UUID.fromString("f000aa43-0451-4000-b000-000000000000");
    /* Acceleromter configuration servcie */
    private static final UUID ACCELEROMETER_SERVICE = UUID.fromString("f000aa10-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_DATA_CHAR = UUID.fromString("f000aa11-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_CONFIG_CHAR = UUID.fromString("f000aa12-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_PERIOD_CHAR = UUID.fromString("f000aa13-0451-4000-b000-000000000000");

    /* Gyroscope Configuration service 
    private static final UUID GYROSCOPE_SERVICE = UUID.fromString("f000aa50-0451-4000-b000-000000000000");
    private static final UUID GYROSCOPE_DATA_CHAR = UUID.fromString("f000aa51-0451-4000-b000-000000000000");
    private static final UUID GYROSCOPE_CONFIG_CHAR = UUID.fromString("f000aa52-0451-4000-b000-000000000000");
    */
    /* Client Configuration Descriptor */
    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private final Handler handler = new Handler();
    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;
    
    Hmm<ObservationVector> initHmmPunch=null;
	Hmm<ObservationVector> learntHmmPunch=null;
	Hmm<ObservationVector> learntHmmScrolldown=null;
	Hmm<ObservationVector> learntHmmSend=null;

    
    
    static Hmm<ObservationVector> learn0=null;
    static Hmm<ObservationVector> learn1=null;
	static Hmm<ObservationVector> learn2=null;
	static Hmm<ObservationVector> learn3=null;
    static Hmm<ObservationVector> learn4=null;
	static Hmm<ObservationVector> learn5=null;
	static Hmm<ObservationVector> learn6=null;
    static Hmm<ObservationVector> learn7=null;
	static Hmm<ObservationVector> learn8=null;
	static Hmm<ObservationVector> learn9=null;
	private static int testCounter =0;
	 private Boolean testStart=false;
	 static Map<Hmm<ObservationVector>,String> HMMMap = new HashMap<Hmm<ObservationVector>,String>();
	static String result="";

    private BluetoothGatt mConnectedGatt;
    public String TemperatureData,HumidityData,AccelerometerData,PressureData="";
    
    String[] Items;
   // private TextView mTemperature, mHumidity, mPressure,mAccelerometer;
	public boolean CheckBluetooth()
	{
		
		 if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
	            //Bluetooth is disabled
	            //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            //startActivity(enableBtIntent);
	            //finish();
			 Toast.makeText(this, "Bluetooth is Turned off.Please Turn On and Start The Game.", Toast.LENGTH_LONG).show();
	            return false;
	        }else
	        {
	        	
	        	return true;
	        }
		
	}
	public static void HMMTraining(String[] datas,String Action) {
        String line="";
        //String Trainfilename="HmmPunch.seq::HmmStomp.seq::HmmCircle.seq";
        //String TestFile="combo.seq";
        String Actionname=Action;
        
    	 //Trainfilename = Trainfilename.replace("-","/");
    	// TestFile = TestFile.replace("-", "/");

    	
    	 
    	 //String TrainfileArr[]=Trainfilename.split("::");
    	
    	 
    	
    	 List<ObservationVector> Sequence1=new ArrayList<ObservationVector>();
    	 List<ObservationVector> Sequence2=new ArrayList<ObservationVector>();
    	 List<ObservationVector> Sequence3=new ArrayList<ObservationVector>();
    	 List<ObservationVector> Sequence4=new ArrayList<ObservationVector>();
    	 List<ObservationVector> Sequence5=new ArrayList<ObservationVector>();
    	 List<ObservationVector> Sequence6=new ArrayList<ObservationVector>();
    	 List<ObservationVector> Sequence7=new ArrayList<ObservationVector>();
    	 
    	
    	 String[] Getdata=datas;
    	 for(int k=0;k < 7;k++){
    	 String dataString[]=Getdata[k].split(":");
    	 for(int j=0;j<dataString.length;j++)
    	 {
    		
    		 String axis[]=dataString[j].split(",");
    		 double[] vec=new double[]{Double.valueOf(axis[0]),Double.valueOf(axis[1]),Double.valueOf(axis[2])};
    		 ObservationVector ov=new ObservationVector(vec);
    		 switch(k)
    		 {
    		 case 0:
    			 Sequence1.add(ov);
    			 break;
    		 case 1:
    			 Sequence2.add(ov);
    			 break;
    		 case 2:
    			 Sequence3.add(ov);
    			 break;
    		 case 3:
    			 Sequence4.add(ov);
    			 break;
    		 case 4:
    			 Sequence5.add(ov);
    			 break;
    		 case 5:
    			 Sequence6.add(ov);
    			 break;
    		 case 6:
    			 Sequence7.add(ov);
    			 break;
    		default:
    			break;
    			
    			 
    			 
    		 }
    		 
    	 }
    	 
    	 }
    	 
    	 
    	 Boolean exception =false;
    	
    	 
    
    		
    			exception =false;
    	    
    			 int x=10;
    			 
    	    	while(!exception){
    	    		
    	  	  
    	        OpdfMultiGaussianFactory initFactory = new OpdfMultiGaussianFactory(3);

    	       
    			try {
    				/*learnReader = new FileReader(new File (myDir,TrainfileArr[i]));
    			
    	        List<List<ObservationVector>> learnSequences = ObservationSequencesReader
    	                .readSequences(new ObservationVectorReader(), learnReader);
    	        learnReader.close();*/
    	        List<List<ObservationVector>> learnSequences=Arrays.asList(Sequence1,Sequence2,Sequence3,Sequence4,Sequence5,Sequence6,Sequence7);
//Log.i("Punch Data", learnSequences.toString());
    	        KMeansLearner<ObservationVector> kMeansLearner = new KMeansLearner<ObservationVector>(
    	                x, initFactory, learnSequences);
    	        
    	        // Create an estimation of the HMM (initHmm) using one iteration of the
    	        // k-Means algorithm
    	        Hmm<ObservationVector> initHmm = kMeansLearner.iterate();

 
    	        // Use BaumWelchLearner to create the HMM (learntHmm) from initHmm
    	        
    	       
    	        	 BaumWelchLearner baumWelchLearner0 = new BaumWelchLearner();
    	        learn0 = baumWelchLearner0.learn(
    	                initHmm, learnSequences);
    	         exception=true;
    	         HMMMap.put(learn0,Actionname);
    	        
    	            
    	        
    	       
    			} catch(Exception e){
    				  x--;
    				  
    			  }
    	    }     	         
    	 	
    	 
    	 
    	 line="training success for:"+Actionname+"\n";
         line = line + "Start Test....\n";
    	 
    	 
    	 line = line + "HMMMap size: " +HMMMap.size();
    	
    	 line = line + "\n" + HMMMap.get(learn0);
    	 
    	

    	    ///////////////////////// Start Testing  ///////////////////////////
          
     
          	
		
    	 
         /*
         if (gesture == 1) {
         	line=line+"This is a punch gesture\n";
         } else if (gesture == 2) {
         	line=line+"This is a right-left gesture\n";
         } else if (gesture == 3) {
         	line=line+"This is a left to right gesture\n";
         }*/
		
	Log.i("Training Gestures",line);
    }
	public void TestGesture(ArrayList sequence)
	{
		
		 String line = "";
		try {  
				/* Reader testReader = new FileReader(TestFile);
			        List<List<ObservationVector>> testSequences = ObservationSequencesReader
			                .readSequences(new ObservationVectorReader(), testReader);
			        testReader.close();*/
				
				 List<ArrayList> testSequences=Arrays.asList(sequence);
				

			       // System.out.println(testSequences.get(0));
			        short gesture; 
			        double Probability=0;
			        Map<Double,String> motionmap = new HashMap<Double,String>();
			        
			        for (int i = 0; i < testSequences.size(); i++) {
			        	
			        	  Iterator<Hmm<ObservationVector>> HMMIte2 = HMMMap.keySet().iterator();
			        	    while (HMMIte2.hasNext()) {
			        	        
			        	    	Hmm<ObservationVector> learnModel = HMMIte2.next();
			        	    	String motion = HMMMap.get(learnModel);
			        	    	Probability=learnModel.probability(testSequences.get(i));
			        	    	motionmap.put(Probability,motion);
			        	    }
			        	    
			        	    double comp = 0;
			        	     
			        	     Iterator<Double> motionIte2 = motionmap.keySet().iterator();
				        	    while (motionIte2.hasNext()) {
				        	    	Double prob = motionIte2.next();
				        	    	if(prob>comp)
				        	    	{
				        	    		comp=prob;
				        	    	}
				        	    	
				        	    }
			        	     
				        	    String  finaldecision= motionmap.get(comp);
				        	    Intent intent = new Intent("myproject");
				        	       intent.putExtra("data", finaldecision);
				        	       sendBroadcast(intent);
				       
				        	    line = line + "\n" + "highest probability is: "+comp + ", gesture is: "+finaldecision;
				        	    Log.i("Test Gesture", line);
			        	     
			        	   
			        	   
			        	    
			       
			        	    
			        	    
			 
			        } 
			    
	   
			            
			        
					} catch (Exception e) {
						// TODO Auto-generated catch block
						line=e.toString();
						e.printStackTrace();
					}
					
	}
	public boolean CheckBLE()
	{
		
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            //finish();
            return false;
        }else
        {
        	
        	return true;
        }
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		
		BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();

        mDevices = new SparseArray<BluetoothDevice>();
        if(CheckBLE() == true && CheckBluetooth()==true)
        {
        	Log.i(TAG, "Bluetooth check and BLE check Passes");
        	startScan();
        	
        }
        
        if(testStart==false)
		{
			String[] punchdata=new String[]{"-0.171875,-1.0,-0.390625:0.203125,-1.03125,-0.390625:-0.4375,-1.296875,-0.390625:-0.890625,-0.921875,-0.390625:0.015625,1.765625,-0.390625:1.328125,-2.0,-0.390625:-0.21875,0.453125,-0.390625:-0.484375,-0.609375,-0.390625:-0.453125,-1.265625,0.5","-0.359375,-1.359375,0.5:-1.0,-0.78125,0.5:0.75,1.90625,0.5:-0.078125,-2.0,0.5:-0.421875,0.578125,0.5:-0.59375,-0.53125,0.5:-0.640625,-1.21875,0.5:-0.3125,-1.234375,0.5:0.109375,-1.28125,0.5:0.171875,-1.046875,0.5:0.0625,-0.96875,0.5:0.078125,-1.078125,0.5:0.0,-1.171875,0.5:-1.15625,-0.59375,1.109375","0.03125,1.984375,1.109375:0.5,-0.953125,1.109375:-0.15625,0.625,1.109375:-0.546875,-0.609375,1.109375:-0.796875,-1.4375,1.109375:0.078125,-1.203125,1.109375:0.203125,-1.15625,1.109375:0.125,-0.96875,1.109375:0.015625,-1.078125,1.109375:0.015625,-1.203125,1.109375:-0.34375,-1.328125,1.109375:-1.25,-0.75,1.109375:0.453125,1.984375,1.109375:0.34375,-2.0,1.109375:0.21875,0.75,1.109375:-0.546875,-0.25,1.109375:-0.453125,-0.921875,1.109375:0.234375,-1.234375,0.265625","-0.890625,-0.859375,0.265625:-0.859375,-0.109375,0.265625:-1.859375,0.1875,0.265625:-1.0,0.5,0.265625:-0.046875,-0.375,0.265625:0.015625,-0.59375,0.265625:-0.59375,-0.9375,0.265625:-0.484375,-1.53125,0.265625:-0.015625,-1.15625,0.265625:0.015625,-1.03125,0.265625","-0.5,-1.171875,0.265625:-0.859375,-0.90625,0.265625:-0.5625,0.5,0.265625:-0.375,-2.0,0.265625:-0.53125,0.34375,0.265625:-0.40625,-0.40625,0.265625:-0.1875,-0.765625,0.265625:-0.75,-0.984375,0.265625:-0.25,-1.484375,0.265625:0.03125,-1.25,0.265625:0.140625,-0.765625,0.265625:-0.03125,-1.203125,0.265625:-0.1875,-1.390625,0.265625:-0.453125,-1.046875,0.265625:-1.03125,-0.4375,0.265625:0.203125,1.546875,0.265625:0.296875,-1.765625,0.265625:-0.25,-0.046875,0.265625:-0.234375,-0.578125,0.265625:-1.234375,-0.890625,0.953125","-0.484375,-1.234375,0.953125:-0.875,-0.921875,0.953125:-0.515625,0.328125,0.953125:-1.25,-0.5,0.953125:-0.75,0.234375,0.953125:-0.265625,-0.203125,0.953125:-0.21875,-0.609375,0.953125:-0.765625,-1.078125,0.953125:-0.625,-1.125,0.953125:0.0625,-1.25,0.953125:0.125,-0.9375,0.953125:-0.03125,-1.015625,0.953125:-0.234375,-1.125,0.421875:-0.234375,-1.125,0.78125:-0.625,-0.921875,0.78125:-1.265625,-0.5625,0.78125:0.484375,1.21875,0.78125:-0.0625,-1.046875,0.78125:-0.46875,-0.0625,0.78125:-0.375,-0.5,0.78125:-0.359375,-0.75,0.78125:-1.03125,-1.15625,0.3125:-0.0625,-1.328125,0.3125:-0.15625,-1.0,0.3125","-0.234375,-1.390625,-0.953125:0.0,-1.28125,-0.953125:0.09375,-0.875,-0.953125:0.046875,-1.078125,-0.953125:-0.203125,-1.265625,-0.953125:-0.4375,-1.046875,-0.953125:-0.8125,-0.5,-0.953125:-0.234375,0.703125,-0.953125:-0.828125,-1.234375,-0.953125:-0.953125,0.5,-0.953125:-0.359375,-0.40625,-0.953125:0.296875,-0.484375,-0.953125:-0.140625,-1.15625,-0.953125:-0.75,-1.25,-0.953125:0.03125,-1.234375,-0.953125:0.21875,-1.078125,-0.953125"};
			//String[] stompdata=new String[]{"-2.0,1.984375,0.625:-2.0,-2.0,0.625:-0.609375,-1.671875,0.625:-0.390625,0.96875,0.625:0.265625,-0.484375,0.625:0.4375,-1.03125,0.625:0.375,-1.234375,0.625:0.34375,-1.125,0.625:0.21875,-1.078125,0.625:-0.0625,-0.171875,0.625:-1.578125,1.984375,0.625:-2.0,-2.0,0.625:-1.6875,-1.609375,0.625:-0.359375,-0.1875,0.625:0.09375,0.328125,0.625:0.28125,-0.53125,0.625:0.46875,-1.046875,0.625:0.484375,-0.890625,0.375","-0.6875,-1.171875,0.375:-0.203125,0.265625,0.375:0.15625,-0.28125,0.375:0.296875,-0.90625,0.375:0.375,-1.265625,0.375:0.359375,-1.078125,0.375:0.25,-0.984375,0.375:-0.015625,-0.015625,0.375:-1.4375,1.984375,0.375:-2.0,-2.0,0.375:-1.921875,-1.109375,0.375:-0.4375,-0.359375,0.375:-0.03125,0.0625,0.375:0.453125,-1.015625,0.234375","0.03125,0.421875,-0.875:0.28125,-0.765625,-0.875:0.40625,-1.1875,-0.875:0.359375,-1.09375,-0.875:0.28125,-0.96875,-0.875:0.125,-0.53125,-0.875:-0.765625,1.984375,-0.875:-2.0,-1.03125,-0.875:-2.0,-2.0,-0.875:-1.078125,-1.734375,-0.875:-0.6875,0.84375,-0.875:0.09375,-0.3125,-0.875:0.25,-0.96875,-0.875:0.3125,-1.234375,-0.875:0.25,-0.953125,-0.875:0.109375,-0.640625,-0.875:-0.296875,0.796875,-0.875:-2.0,-2.0,-1.15625","0.015625,-0.265625,-1.15625:-0.84375,1.984375,-1.15625:-2.0,-2.0,-1.15625:-1.390625,-2.0,-1.15625:-0.9375,-0.328125,-1.15625:-0.203125,0.40625,-1.15625:0.34375,-0.921875,-1.15625:0.484375,-1.1875,-1.15625:0.375,-0.9375,-1.15625","-0.5,-0.90625,-1.984375:-0.515625,0.90625,-1.984375:0.265625,-0.796875,-1.984375:0.359375,-1.265625,-1.984375:0.265625,-0.96875,-1.984375:0.21875,-0.84375,-1.984375:-0.0625,-0.21875,-1.984375:-1.515625,1.984375,-1.984375:-2.0,-2.0,-1.984375:-1.140625,-1.953125,-1.984375:-1.015625,0.421875,-1.984375:-0.046875,0.28125,-1.984375:0.359375,-0.78125,-1.984375:0.46875,-1.140625,-1.984375:0.453125,-0.84375,-1.984375","-0.109375,0.65625,0.59375:-2.0,1.984375,0.59375:-2.0,-2.0,0.59375:-0.546875,-1.78125,0.59375:-1.25,0.625,0.59375:0.21875,-0.5625,0.59375:0.453125,-0.953125,0.59375","-0.265625,0.703125,-1.984375:-2.0,0.296875,-1.984375:-2.0,-2.0,-1.984375:-1.140625,-1.546875,-1.984375:-0.734375,-0.265625,-1.984375:-0.265625,0.53125,-1.984375:0.171875,-0.65625,-1.984375:0.15625,-0.703125,0.578125","-0.109375,0.078125,0.5:-1.234375,1.859375,0.5:-2.0,-2.0,0.5:-1.28125,-1.59375,0.5:-0.390625,-1.15625,0.5:-0.3125,-0.40625,0.5:-0.34375,0.296875,0.5:0.03125,-0.6875,0.5:0.28125,-1.140625,0.5:0.203125,-0.78125,0.5:-0.0625,-0.28125,0.5:-0.96875,1.671875,0.5:-2.0,-2.0,0.5:-0.9375,-1.953125,0.5:-0.015625,-0.609375,0.5:0.03125,-0.71875,0.5:0.21875,-0.734375,0.5:0.28125,-0.828125,0.5:0.234375,-0.96875,0.5"};
			String[] circledata=new String[]{"0.015625,-1.015625,-0.40625:0.046875,-0.90625,-0.40625:-1.0625,-1.390625,-0.40625:0.25,-2.0,-0.40625:1.375,-1.765625,-0.40625:0.953125,-0.09375,-0.40625:0.21875,0.90625,-0.40625:-0.453125,0.46875,-0.40625:-0.09375,-2.0,-1.0625","0.5625,-1.46875,-1.0625:0.0,-0.21875,-1.0625:0.25,-1.25,-1.0625:0.15625,-1.390625,-1.0625:-0.296875,-0.09375,-1.0625:-0.859375,-0.28125,-1.0625:-0.0625,-2.0,-1.0625:1.203125,-2.0,-1.0625:1.390625,-0.25,-1.0625:0.40625,0.390625,-1.0625:-0.546875,0.375,-1.0625:-0.84375,-0.234375,-1.0625:-0.890625,-2.0,-1.0625:0.8125,-2.0,-1.0625:0.625,-0.40625,-1.0625:-0.078125,-0.765625,-1.0625:0.453125,-0.390625,-0.234375:-0.21875,-0.390625,-0.234375:-0.75,0.03125,-0.234375:-0.59375,-2.0,-0.234375:1.25,-2.0,-0.234375:1.53125,-0.390625,-0.234375:0.390625,0.390625,-0.234375:-0.59375,0.234375,-0.234375:-0.828125,-0.09375,-0.234375:-1.03125,-0.96875,-0.234375:-0.09375,-2.0,-0.234375:1.625,-1.265625,-0.234375:-0.125,-0.640625,-0.234375:0.359375,-1.25,-0.234375:0.109375,-1.1875,-0.234375:-0.21875,-0.0625,-0.234375:-0.796875,-0.640625,-0.234375:0.1875,-2.0,-0.234375:1.203125,-1.953125,-0.234375:0.234375,0.484375,-0.328125","-0.65625,0.140625,-0.328125:-0.71875,-0.15625,-0.328125:-1.078125,-2.0,-0.328125:0.796875,-2.0,-0.328125:0.78125,-1.0,-0.328125:-0.234375,-0.5,-0.328125:0.421875,-1.96875,-0.328125:0.0,-0.484375,-0.328125:-0.515625,0.203125,-0.328125:-0.953125,-2.0,-0.328125:1.03125,-2.0,-0.328125:1.46875,-0.953125,-0.328125:0.78125,0.21875,-0.328125:-0.21875,0.578125,-0.328125:-0.640625,0.21875,-0.328125:-1.203125,-1.0625,-0.328125:-0.609375,-2.0,-0.625:1.953125,-2.0,-0.625:-0.25,-0.734375,-0.625:0.015625,-0.96875,-0.625:0.21875,-1.0625,-0.625:-0.46875,0.09375,-0.625:-0.703125,-1.734375,-0.625:0.296875,-2.0,-0.625:0.9375,-1.6875,-0.625:0.984375,-0.59375,-0.625:0.296875,0.25,-0.625:-0.28125,0.28125,-0.625:-1.0625,-0.140625,-0.625:-0.96875,-1.75,-0.625:0.15625,-2.0,-0.625:1.234375,-0.84375,-0.625:0.015625,-0.78125,-0.625:0.109375,-1.234375,-0.625:-0.140625,-0.546875,-0.625:-0.390625,-2.0,-0.890625","0.890625,-2.0,-0.890625:1.328125,-0.5,-0.890625:0.59375,0.5,-0.890625:-0.484375,0.25,-0.890625:-1.3125,-0.28125,-0.890625:-1.3125,-1.53125,-0.890625:0.46875,-2.0,-0.890625:0.875,-1.234375,-0.890625:-0.234375,-0.390625,-0.890625:0.328125,-1.796875,-0.890625:-0.078125,-0.8125,-0.890625:-0.328125,0.0,-0.890625:-0.765625,-1.546875,-0.890625:0.40625,-2.0,-0.890625:0.921875,-1.75,-0.5:0.890625,-0.296875,-0.265625:-0.765625,0.078125,-0.21875","-1.234375,-0.484375,-0.21875:-1.0,-1.46875,-0.21875:1.453125,-2.0,0.078125:0.125,-0.171875,-0.328125:0.015625,-0.96875,-0.328125:0.21875,-1.234375,-0.328125:-0.4375,-0.140625,-0.671875:-0.765625,-1.484375,-0.890625:0.515625,-2.0,-0.53125:0.859375,-1.546875,-0.53125:1.109375,-0.3125,-0.25:-0.078125,0.234375,-0.234375:-0.625,0.046875,-0.21875:-0.953125,-0.171875,-0.21875:-1.15625,-1.234375,-0.21875:0.40625,-2.0,-0.21875:1.046875,-1.09375,-0.21875:0.421875,-1.53125,-0.5","-0.171875,-0.59375,-0.5:-0.46875,0.015625,-0.5:-0.953125,-1.9375,-0.5:0.90625,-2.0,-0.5:1.25,-1.109375,-0.5:0.765625,0.234375,-0.5:-0.4375,0.484375,-0.5:-0.6875,0.015625,-0.5:-1.234375,-1.09375,-0.5:-0.40625,-2.0,-0.5:1.40625,-1.28125,-0.5:-0.296875,-0.625,-0.5:0.078125,-1.09375,-0.5:0.046875,-1.328125,-0.5:-0.125,-0.46875,-0.5:-0.515625,-0.140625,-0.5:-0.53125,-2.0,-0.765625:0.953125,-1.1875,-0.546875","0.953125,0.234375,-0.546875:-0.28125,0.125,-0.546875:-0.921875,-0.171875,-0.546875:-0.9375,-0.90625,-0.546875:0.015625,-2.0,-0.546875:1.484375,-1.5,-0.546875:-0.484375,-0.46875,-0.546875:0.4375,-1.59375,-0.546875:-0.0625,-0.796875,-0.546875:-0.46875,0.015625,-0.546875:-0.796875,-1.59375,-0.546875:0.5,-2.0,-0.546875:1.4375,-1.359375,-0.546875:1.109375,0.03125,-0.546875:-0.078125,0.71875,-0.546875:-0.8125,0.0625,-0.546875:-1.265625,-0.8125,-0.546875:1.6875,-2.0,-0.828125"};
			//Log.i("Sizes", String.valueOf(stompdata.length)+String.valueOf(circledata.length));
			HMMTraining(punchdata,"punch");
			//HMMTraining(stompdata,"stomp");
			HMMTraining(circledata,"circle");
		}
		testStart=true;
        return START_NOT_STICKY;
	}
	@Override
	public void onDestroy()
	{
		
		if (mConnectedGatt != null) {
            mConnectedGatt.disconnect();
            mConnectedGatt = null;
        }
	}
	
	public void Pause()
	{
		 	mHandler.removeCallbacks(mStopRunnable);
	        mHandler.removeCallbacks(mStartRunnable);
	        mBluetoothAdapter.stopLeScan(this);
	}
	public void Stop()
	{
		if (mConnectedGatt != null) {
            mConnectedGatt.disconnect();
            mConnectedGatt = null;
        }
		
	}
	public String[] ListScannedItems()
	{
		
		for (int i=0; i < mDevices.size(); i++) {
            BluetoothDevice device = mDevices.valueAt(i);
           Items[i]=device.getName();
        }
		return Items;
	}
	public void CreateConnection(int i)
	{
		if(mDevices.size() >0)
		{
		BluetoothDevice device = mDevices.get(mDevices.keyAt(i));
		//BluetoothDevice device = mDevices.get();
        Log.i(TAG, "Connecting to "+device.getName());
        /*
         * Make a connection with the device using the special LE-specific
         * connectGatt() method, passing in a callback for GATT events
         */
        mConnectedGatt = device.connectGatt(this, false, mGattCallback);
		}
		
	}
	private Runnable mStopRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };
    private Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    private void startScan() {
        mBluetoothAdapter.startLeScan(this);
        //setProgressBarIndeterminateVisibility(true);

        mHandler.postDelayed(mStopRunnable, 2500);
    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(this);
        //setProgressBarIndeterminateVisibility(false);
        if(mDevices.size() >0)
        {
        	CreateConnection(0);
        	
        }
    }

    /* BluetoothAdapter.LeScanCallback */

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i(TAG, "New LE Device: " + device.getName() + " @ " + rssi);
        /*
         * We are looking for SensorTag devices only, so validate the name
         * that each device reports before adding it to our collection
         */
        if (DEVICE_NAME.equals(device.getName())) {
            mDevices.put(device.hashCode(), device);
            //Update the overflow menu
            //invalidateOptionsMenu();
        }
        
    }

    /*
     * In this callback, we've created a bit of a state machine to enforce that only
     * one characteristic be read or written at a time until all of our sensors
     * are enabled and we are registered to get notifications.
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /* State Machine Tracking */
        private int mState = 0;

        private void reset() { mState = 0; }

        private void advance() { mState++; }

        /*
         * Send an enable command to each sensor by writing a configuration
         * characteristic.  This is specific to the SensorTag to keep power
         * low by disabling sensors you aren't using.
         */
        private void enableNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.d(TAG, "Enabling pressure cal");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x02});
                    break;
                case 1:
                    Log.d(TAG, "Enabling pressure");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;
                case 2:
                    Log.d(TAG, "Enabling humidity");
                    characteristic = gatt.getService(HUMIDITY_SERVICE)
                            .getCharacteristic(HUMIDITY_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;
                case 3:
                    Log.d(TAG, "Enabling Accelerometer");
                    characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;
                case 4:
                    Log.d(TAG,"Enabling accelerometer");
                    characteristic= gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_PERIOD_CHAR);
                    characteristic.setValue(new byte[]{(byte)10});
                    break;
                default:
                    //mHandler.sendEmptyMessage(MSG_DISMISS);
                    Log.i(TAG, "All Sensors Enabled 1");
                    return;
            }

            gatt.writeCharacteristic(characteristic);
        }

        /*
         * Read the data characteristic's value for each sensor explicitly
         */
        private void readNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.d(TAG, "Reading pressure cal");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CAL_CHAR);
                    break;
                case 1:
                    Log.d(TAG, "Reading pressure");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_DATA_CHAR);
                    break;
                case 2:
                    Log.d(TAG, "Reading humidity");
                    characteristic = gatt.getService(HUMIDITY_SERVICE)
                            .getCharacteristic(HUMIDITY_DATA_CHAR);
                    break;
                case 3:
                    Log.d(TAG, "Reading Accelerometer");
                    characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_DATA_CHAR);
                    break;
                default:
                    //mHandler.sendEmptyMessage(MSG_DISMISS);
                    Log.i(TAG, "All Sensors Enabled 2");
                    return;
            }

            gatt.readCharacteristic(characteristic);
        }

        /*
         * Enable notification of changes on the data characteristic for each sensor
         * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
         * configuration descriptor.
         */
        private void setNotifyNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.d(TAG, "Set notify pressure cal");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CAL_CHAR);
                    break;
                case 1:
                    Log.d(TAG, "Set notify pressure");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_DATA_CHAR);
                    break;
                case 2:
                    Log.d(TAG, "Set notify humidity");
                    characteristic = gatt.getService(HUMIDITY_SERVICE)
                            .getCharacteristic(HUMIDITY_DATA_CHAR);
                    break;
                case 3:
                    Log.d(TAG, "Set notify accelerometer");
                    characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_DATA_CHAR);
                    break;
                default:
                    //mHandler.sendEmptyMessage(MSG_DISMISS);
                    Log.i(TAG, "All Sensors Enabled 3");
                    return;
            }

            //Enable local notifications
            gatt.setCharacteristicNotification(characteristic, true);
            //Enabled remote notifications
            BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(desc);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "Connection State Change: "+status+" -> "+connectionState(newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
                 * Once successfully connected, we must next discover all the services on the
                 * device before we can read and write their characteristics.
                 */
                gatt.discoverServices();
                //mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Discovering Services..."));
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                /*
                 * If at any point we disconnect, send a message to clear the weather values
                 * out of the UI
                 */
               // mHandler.sendEmptyMessage(MSG_CLEAR);
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                /*
                 * If there is a failure at any stage, simply disconnect
                 */
                gatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "Services Discovered: "+status);
           // mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Enabling Sensors..."));
            /*
             * With services discovered, we are going to reset our state machine and start
             * working through the sensors we need to enable
             */
            reset();
            enableNextSensor(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //For each read, pass the data up to the UI thread to update the display
            if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
            }
            if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
            }
            if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
            }
            if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_ACCELEROMETER, characteristic));
            }

            //After reading the initial value, next we enable notifications
            setNotifyNextSensor(gatt);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //After writing the enable flag, next we read the initial value
            readNextSensor(gatt);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /*
             * After notifications are enabled, all updates from the device on characteristic
             * value changes will be posted here.  Similar to read, we hand these up to the
             * UI thread to update the display.
             */
            if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
            }
            if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
            }
            if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
            }
            if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
                mHandler.sendMessage(Message.obtain(null, MSG_ACCELEROMETER, characteristic));
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //Once notifications are enabled, we move to the next sensor and start over with enable
            advance();
            enableNextSensor(gatt);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.d(TAG, "Remote RSSI: "+rssi);
        }

        private String connectionState(int status) {
            switch (status) {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }
    };

	 /*
     * We have a Handler to process event results on the main thread
     */
    private static final int MSG_HUMIDITY = 101;
    private static final int MSG_PRESSURE = 102;
    private static final int MSG_PRESSURE_CAL = 103;
    private static final int MSG_ACCELEROMETER = 104;
    //private static final int MSG_PROGRESS = 201;
    //private static final int MSG_DISMISS = 202;
    //private static final int MSG_CLEAR = 301;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            BluetoothGattCharacteristic characteristic;
            switch (msg.what) {
                case MSG_HUMIDITY:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                        Log.w(TAG, "Error obtaining humidity value");
                        return;
                    }
                    updateHumidityValues(characteristic);
                    break;
                case MSG_PRESSURE:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                        Log.w(TAG, "Error obtaining pressure value");
                        return;
                    }
                    updatePressureValue(characteristic);
                    break;
                case MSG_PRESSURE_CAL:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                        Log.w(TAG, "Error obtaining cal value");
                        return;
                    }
                    updatePressureCals(characteristic);
                    break;
                case MSG_ACCELEROMETER:
                    characteristic = (BluetoothGattCharacteristic) msg.obj;
                    if (characteristic.getValue() == null) {
                        Log.w(TAG, "Error obtaining Accelerometer value");
                        return;
                    }
                    updateAccelerometerValue(characteristic);
                    break;
                
            }
        }

        private void updateHumidityValues(BluetoothGattCharacteristic characteristic) {
            double humidity = SensorTagData.extractHumidity(characteristic);

           HumidityData=String.format("%.0f%%", humidity);
        }
        DetectMotion motion = new DetectMotion();
        Float isStomp=-2.0f;
        double x1,y1,z1,d=0.0f,norm;
        Boolean trigger=false;
        ArrayList<String> dataPoints = new ArrayList<String>();
        ArrayList sequence = new ArrayList();
       double[] vec;
        private void updateAccelerometerValue(BluetoothGattCharacteristic characteristic) {
        	
        	Float[] values = SensorTagData.extractAccelerometerReading(characteristic, 0);
            double x,y,z;
            x=values[0];
            y=values[1];
            z=values[2];
            
            //String formatdata=data[0].toString()+","+data[1].toString()+","+data[2].toString();
            if(testStart){
            	d= Math.sqrt( Math.pow((x-x1),2 )  + Math.pow((y-y1),2 ) + Math.pow((z-z1),2 ));
                if(d>=0.3 && !trigger){
             	   Log.i("start","start");
             	 
             	   trigger=true;
                }
                else if(d<=0.1 && trigger){
              	   Log.i("end", "end");
              	   trigger=false;
              	  
              	   try{
              		   if(sequence.size()>6){
              			   
              		   //whichGesture(dataPoints);
              			TestGesture(sequence);
              		   testCounter++;
              		 Log.i("train counter", String.valueOf(testCounter));
                 	  
              		   }
              	   //dataPoints.clear(); 
              	   sequence.clear();
              	 
              	   }catch(Exception e){
              		   e.printStackTrace();
              	   }
                }
                if(trigger){
               	  dataPoints.add("[ "+x + " " + y + " " + z+" ] ;");
               	vec=new double[]{ x, y ,z};
               	ObservationVector ov=new ObservationVector(vec);
               	sequence.add(ov);
               	
                  }
                
                x1=x;y1=y;z1=z;
            }
          //SaveData(formatdata,"rtolTest");
           
         //MainActivity ma=new MainActivity();
         //ma.setAccelerometerData(formatdata);
        }

        private int[] mPressureCals;
        private void updatePressureCals(BluetoothGattCharacteristic characteristic) {
            mPressureCals = SensorTagData.extractCalibrationCoefficients(characteristic);
        }

        private void updatePressureValue(BluetoothGattCharacteristic characteristic) {
            if (mPressureCals == null) return;
            double pressure = SensorTagData.extractBarometer(characteristic, mPressureCals);
            double temp = SensorTagData.extractBarTemperature(characteristic, mPressureCals);

           TemperatureData=String.format("%.1f\u00B0C", temp);
            PressureData=String.format("%.2f", pressure);
        }
    };

    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getStompData()
	{
		return AccelerometerData;
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}
	
	

}
