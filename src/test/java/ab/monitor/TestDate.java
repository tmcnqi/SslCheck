package ab.monitor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestDate {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		 Date date1 = new Date(118,4,27);
		 Date date2 = new Date();
		 System.out.println("date1:" + sdf.format(date1));
		 System.out.println("date2:" + sdf.format(date2));
		 if(date1.after(date2)) { 
			 System.out.println("date1大于date2");
		 }else{
			 System.out.println("date1小于date2");
		 }
		 
		 Calendar calendar = Calendar.getInstance();
		 System.out.println("calendar: " + sdf.format( calendar.getTime() ));
		 calendar.set(Calendar.YEAR, 2018);
		 calendar.set(Calendar.MONTH, 4);
		 calendar.set(Calendar.DAY_OF_MONTH, 27);
		 System.out.println("calendar: " + sdf.format( calendar.getTime() ));
		 
		 System.out.println("date2:" + sdf.format(date2));
		 System.out.println(calendar.getTimeInMillis() );
		 System.out.println( date2.getTime());

		 
		 if( calendar.getTimeInMillis() > date2.getTime() ) { 
			 System.out.println("calendar大于date2");
		 }else{
			 System.out.println("calendar小于date2");
		 }
		 

		 

	}

}
