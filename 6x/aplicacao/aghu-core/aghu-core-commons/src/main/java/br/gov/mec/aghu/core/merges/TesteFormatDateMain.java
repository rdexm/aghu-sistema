package br.gov.mec.aghu.core.merges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TesteFormatDateMain {

	public static void main(String[] args) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			String strDate = "2014-07-09 08:39:31";
			
			Date date = format.parse(strDate);
			System.out.println(date);//NOPMD
			
		} catch (ParseException e) {
			e.printStackTrace();//NOPMD
		}


	}

}
