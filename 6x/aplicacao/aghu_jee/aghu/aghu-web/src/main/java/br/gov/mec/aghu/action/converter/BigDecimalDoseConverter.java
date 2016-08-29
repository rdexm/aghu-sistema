package br.gov.mec.aghu.action.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "bigDecimalDoseConverter")
public class BigDecimalDoseConverter extends AbstractConverter {

	private static final long serialVersionUID = -1550434482496481226L;
	private final String DOSE_MASK="#########0.####";

	@Override
	public Object getAsObject(String valor) {
        if(StringUtils.isEmpty(valor)) {
			return null;
		}
        try {
            Object valorLng = getDecimalFormat().parseObject(valor);            
            if(valorLng instanceof Double) {
				return BigDecimal.valueOf((Double)valorLng);
			}
            if(valorLng instanceof Long) {
				return BigDecimal.valueOf((Long)valorLng);
			}
        }
        catch (ParseException ex) {
        	logInfo("Erro ao tentar converter valor: " + valor);
        }
		return null;
	}

	@Override
	public String getAsString(Object valor) {
        return (valor == null)?null:getDecimalFormat().format(valor);
	}
	
	private DecimalFormat getDecimalFormat(){
		Locale localeBR = new Locale("pt", "BR");//Brasil 
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(localeBR);
        dfSymbols.setDecimalSeparator(',');
        return new DecimalFormat(DOSE_MASK, dfSymbols);
	}
}
