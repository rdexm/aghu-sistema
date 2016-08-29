package br.gov.mec.aghu.core.validation;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.time.DateUtils;

public class IdadeValidator implements ConstraintValidator<Idade, Object> {
	
	private static final String[] DATE_PATTERNS = new String[] {"dd/mm/yyyy"};

	private Idade idade;
	
	public void initialize(Idade i) {
		idade = i;
	}
	
	public boolean isValid(Object obj, ConstraintValidatorContext constraintContext) {

		if (obj == null) {
			return true;
		}
		
		String 	valueStr = null;
		Date 	valueDat = null;
		if (obj instanceof Date) {
			valueDat = (Date)obj;
		} else {
			valueStr = obj.toString();
		}
	
		if (valueDat != null){
			return validateDate(valueDat);
		}
		
		// inteiro
		try
		{
			int v = Integer.parseInt(valueStr);
			return v >= idade.min() && v <= idade.max();
		}
		catch(NumberFormatException nfex)
		{
			// pode ser data
			try
			{
				return validateDate(DateUtils.parseDate(valueStr, DATE_PATTERNS));
			}
			catch(ParseException pex)
			{
				return false;
			}
		}
	}
	
	private boolean validateDate(Date d)
	{
		Calendar hoje = Calendar.getInstance();
		Calendar dn = Calendar.getInstance();
		dn.setTime(d);
		if (hoje.get(Calendar.YEAR)-dn.get(Calendar.YEAR) > idade.max()) {
			return false;
		}
		if (hoje.get(Calendar.YEAR)-dn.get(Calendar.YEAR) < idade.min()) {
			return false;
		}
		return true;
	}
}