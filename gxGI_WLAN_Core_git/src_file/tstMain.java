import com.wlan.comm.netSshComm;

public class tstMain {
	public static void main(String[] args){

		 netSshComm netobject;
		 try{
			 netobject = new netSshComm("42.120.52.246","root","8^%fP[fo^$e_4_sDSEgs",22,"J:/id_dsa");
			 if(netobject.login()){
				 System.out.println("OK");
				 
	             //做出文件传输
	              try{
	            	  String smainfile = "J:/imss_2013_08_14.out";
		              netobject.SetFile(smainfile);
					  netobject.pushfile("/root/tst/");
					  netobject.chmodFile();
					  
	              }catch(Exception e){
	            	  e.printStackTrace();
	              }				 
			 }
			 netobject.disconnect();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 
		
	}
}
