package com.hbase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseStart
{
	static public void main(String args[]) throws IOException {
		
	//createTable();
		//insertTable();
retrieveTable();
		//deleteTable();
		}
	
	
	public static void createTable() throws IOException
	{
		Configuration config = HBaseConfiguration.create();		 
		config.clear();
         config.set("hbase.zookeeper.quorum", "134.193.136.127");
         config.set("hbase.zookeeper.property.clientPort","2181");
         config.set("hbase.master", "134.193.136.127:60010");
		
		HBaseAdmin admin = new HBaseAdmin(config);
		
		try {
			 HBaseConfiguration hc = new HBaseConfiguration(config);
			
			  HTableDescriptor ht = new HTableDescriptor("vishnu_Lab2"); 
			  
			  ht.addFamily( new HColumnDescriptor("Location"));

			 // ht.addFamily( new HColumnDescriptor("longitude"));
			  
			  ht.addFamily( new HColumnDescriptor("Date"));
			  
			  ht.addFamily( new HColumnDescriptor("AxisData"));
			  ht.addFamily( new HColumnDescriptor("Humidity"));
			  ht.addFamily( new HColumnDescriptor("Temperature"));
			  
			  //ht.addFamily( new HColumnDescriptor("y"));
			  
			  //ht.addFamily( new HColumnDescriptor("z"));
			  
			  System.out.println( "connecting" );

			  HBaseAdmin hba = new HBaseAdmin( hc );

			  System.out.println( "Creating Table" );

			  hba.createTable( ht );

			  System.out.println("Done......");
			  
			  	
        } finally {
            admin.close();
        }
		
		
	}
	
	
	public static void insertTable() throws IOException{
	
		Configuration config = HBaseConfiguration.create();		 
		config.clear();
         config.set("hbase.zookeeper.quorum", "134.193.136.127");
         config.set("hbase.zookeeper.property.clientPort","2181");
         config.set("hbase.master", "134.193.136.127:60010");
         
         
         String latitude="",longitude="",Date="",x="",y="",z="",Humid="",Temp="";
         

		  HTable table = new HTable(config, "vishnu_Lab2");
	
		  Put p = new Put(Bytes.toBytes("Sensor"));
		  
		  int count=1;
         
        BufferedReader br = null;
        BufferedReader hr=null;
         
 		try {
  
 			String sCurrentLine,hCurrentLine;
  
 			br = new BufferedReader(new FileReader("C:\\Users\\vgz8b\\Documents\\sensor.txt"));
 			hr=new BufferedReader(new FileReader("C:\\Users\\vgz8b\\Documents\\humtemp.txt"));
  
 			while ((sCurrentLine = br.readLine()) != null && (hCurrentLine = hr.readLine())!=null ) {
 				
 				if(sCurrentLine.equals("") && hCurrentLine.equals(""))
 				{
 					continue;
 				}
 				
 				String[] array = sCurrentLine.split("\t");
 				String[] harray=hCurrentLine.split("\t");
 				latitude = array[0];
 				longitude=array[1];
 				Date=array[2];
 				x=array[3];
 				y=array[4];
 				z=array[5];
 				Humid=harray[0];
 				Temp=harray[1];
 				
 				  p.add(Bytes.toBytes("Location"), Bytes.toBytes("Latitude"),Bytes.toBytes(latitude));
 				  
 				 p.add(Bytes.toBytes("Location"), Bytes.toBytes("Longitude"),Bytes.toBytes(longitude));
 				 
 				 p.add(Bytes.toBytes("Date"), Bytes.toBytes("Timestamp"),Bytes.toBytes(Date));
 				 
 				 p.add(Bytes.toBytes("AxisData"), Bytes.toBytes("X"),Bytes.toBytes(x));
 				 
 				p.add(Bytes.toBytes("AxisData"), Bytes.toBytes("Y"),Bytes.toBytes(y));
 				
 				p.add(Bytes.toBytes("AxisData"), Bytes.toBytes("Z"),Bytes.toBytes(z));
 				p.add(Bytes.toBytes("Humidity"), Bytes.toBytes("humidity"),Bytes.toBytes(Humid));
 				p.add(Bytes.toBytes("Temperature"), Bytes.toBytes("temperature"),Bytes.toBytes(Temp));

 			      table.put(p);
 			      //table.flushCommits();
 			      //table.close();
 			      
 			      
 			      count=count+1;
 				
 			}
  
 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			try {
 				if (br != null && hr!=null)
 					{
 					br.close();
 					hr.close();
 					}
 			} catch (IOException ex) {
 				ex.printStackTrace();
 			}
 		}
         
         
		
		
	  
	    
	}
	
	
	public static void retrieveTable() throws IOException{
		
		Configuration config = HBaseConfiguration.create();		 
		config.clear();
         config.set("hbase.zookeeper.quorum", "134.193.136.127");
         config.set("hbase.zookeeper.property.clientPort","2181");
         config.set("hbase.master", "134.193.136.127:60010");
		
		
		  HTable table = new HTable(config, "vishnu_Lab2");
		
		 Get g = new Get(Bytes.toBytes("Sensor"));
g.addFamily(Bytes.toBytes("Location"));
g.addFamily(Bytes.toBytes("Date"));
g.addFamily(Bytes.toBytes("AxisData"));
g.addFamily(Bytes.toBytes("Humidity"));
g.addFamily(Bytes.toBytes("Temperature"));
		  Result r = table.get(g);

		  byte[] value = r.getValue(Bytes.toBytes("Location"),Bytes.toBytes("Latitude"));

		  byte[] value1 = r.getValue(Bytes.toBytes("Location"),Bytes.toBytes("Longitude"));

		  byte[] value2 = r.getValue(Bytes.toBytes("Date"),Bytes.toBytes("Timestamp"));
		  
		  byte[] value3 = r.getValue(Bytes.toBytes("AxisData"),Bytes.toBytes("X"));
		  
		  byte[] value4 = r.getValue(Bytes.toBytes("AxisData"),Bytes.toBytes("Y"));
		  
		  byte[] value5 = r.getValue(Bytes.toBytes("AxisData"),Bytes.toBytes("Z"));
		  byte[] value6 = r.getValue(Bytes.toBytes("Humidity"),Bytes.toBytes("humidity"));
		  byte[] value7 = r.getValue(Bytes.toBytes("Temperature"),Bytes.toBytes("temperature"));
		  
		  String valueStr = Bytes.toString(value);

		  String valueStr1 = Bytes.toString(value1);
		  
		  String valueStr2 = Bytes.toString(value2);
		  
		  String valueStr3 = Bytes.toString(value3);
		  
		  String valueStr4 = Bytes.toString(value4);
		  
		  String valueStr5 = Bytes.toString(value5);
		  
		  String valueStr6 = Bytes.toString(value6);
		  
		  String valueStr7 = Bytes.toString(value7);

		  System.out.println("GET: " +"latitude: "+ valueStr+"longitude: "+valueStr1);
		  System.out.println("GET: " +"Date: "+ valueStr2);
		  System.out.println("GET: " +"x: "+ valueStr3);
		  System.out.println("GET: " +"y: "+ valueStr4);
		  System.out.println("GET: " +"z: "+ valueStr5);
		  System.out.println("GET: " +"Humidity: "+ valueStr6);
		  System.out.println("GET: " +"Temperature: "+ valueStr7);
		  
		  

		  

		  Scan s = new Scan();

		  s.addColumn(Bytes.toBytes("Location"), Bytes.toBytes("Latitude"));

		  s.addColumn(Bytes.toBytes("Location"), Bytes.toBytes("Longitude"));

		  ResultScanner scanner = table.getScanner(s);

		  try
		  {
		   for (Result rr = scanner.next(); rr != null; rr = scanner.next())
		   {
		    System.out.println("Found row : " +rr);
		   }
		  } finally
		  {
		   // Make sure you close your scanners when you are done!
		   scanner.close();
		  }
		
	}
	
	
	public static void deleteTable() throws IOException{
		
		Configuration config = HBaseConfiguration.create();		 
		config.clear();
         config.set("hbase.zookeeper.quorum", "134.193.136.127");
         config.set("hbase.zookeeper.property.clientPort","2181");
         config.set("hbase.master", "134.193.136.127:60010");
         
         HBaseAdmin admin = new HBaseAdmin(config);
         admin.disableTable("vishnu_Lab2");
         admin.deleteTable("vishnu_Lab2");

	}
}

