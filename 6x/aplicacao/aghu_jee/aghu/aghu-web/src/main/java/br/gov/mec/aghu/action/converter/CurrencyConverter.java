package br.gov.mec.aghu.action.converter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "currencyConverter")
public class CurrencyConverter extends AbstractConverter 
{

	private static final long serialVersionUID = 804002971833806168L;

	@Override
	public Object getAsObject(String valor) {
		Locale localeBR = new Locale("pt", "BR");//Brasil 
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(localeBR);
        dfSymbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,###,###,###,##0.00", dfSymbols);
        if(StringUtils.isEmpty(valor)) {
			return null;
		}
        try {
            Object valorLng = format.parseObject(valor.trim());            
            if(valorLng instanceof Double) {
				return BigDecimal.valueOf((Double)valorLng);
			}
            if(valorLng instanceof Long) {
				return BigDecimal.valueOf((Long)valorLng);
			}
        }
        catch (ParseException ex) {
        	logDebug("Erro ao tentar converter valor: " + valor);
        }
		return null;
	}

	@Override
	public String getAsString(Object valor) {
		Locale locBR = new Locale("pt", "BR");//Brasil 
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
        dfSymbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,###,###,###,##0.00", dfSymbols);
        return (valor == null)?null:format.format(valor);
	}

}
