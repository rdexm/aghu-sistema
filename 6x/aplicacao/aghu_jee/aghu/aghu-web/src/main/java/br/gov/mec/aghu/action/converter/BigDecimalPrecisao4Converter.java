package br.gov.mec.aghu.action.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "bigDecimalPrecisao4Converter")
public class BigDecimalPrecisao4Converter extends AbstractConverter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1550434482496481216L;

	@Override
	public Object getAsObject(String valor) {
		Locale localeBR = new Locale("pt", "BR");//Brasil 
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(localeBR);
        dfSymbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,###,###,###,##0.####", dfSymbols);
        if(StringUtils.isEmpty(valor)) {
			return null;
		}
        try {
            Object valorLng = format.parseObject(valor);            
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
		Locale locBR = new Locale("pt", "BR");//Brasil 
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
        dfSymbols.setDecimalSeparator(',');
        DecimalFormat format;
       	format = new DecimalFormat("#,###,###,###,##0.####", dfSymbols);
        return (valor == null)?null:format.format(valor);
	}
}
