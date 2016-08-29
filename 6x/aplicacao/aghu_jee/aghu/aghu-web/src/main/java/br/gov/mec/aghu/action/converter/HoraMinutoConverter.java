package br.gov.mec.aghu.action.converter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "horaMinutoConverter")
public class HoraMinutoConverter extends AbstractConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8706657313154995891L;

	@Override
	public Object getAsObject(String valor) {

		try {
			if (StringUtils.isNotBlank(valor)) {
				String[] arrayHora = valor.split(":");
				if (arrayHora.length == 2) {
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal
							.set(Calendar.HOUR_OF_DAY, Integer
									.valueOf(arrayHora[0]));
					cal.set(Calendar.MINUTE, Integer.valueOf(arrayHora[1]));
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);

					return cal.getTime();
				}
			}
			return null;
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			return null;
		}
	}

	@Override
	public String getAsString(Object valor) {
		return (valor != null) ? (new SimpleDateFormat("HH:mm")).format(valor)
				: null;
	}
}