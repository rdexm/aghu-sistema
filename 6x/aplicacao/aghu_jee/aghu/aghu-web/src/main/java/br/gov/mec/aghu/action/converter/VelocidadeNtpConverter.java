package br.gov.mec.aghu.action.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.converter.AbstractConverter;

/**
 * Converter para os valores da fórmula em #990 - Prescrição de Nutrição Parenteral
 * 
 * @author aghu
 */
@FacesConverter(value = "velocidadeNtpConverter")
public class VelocidadeNtpConverter extends AbstractConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2175897596264821312L;

	private static final Locale LOCALE_BR = new Locale("pt", "BR");

	private static final String PATTERN = "###.##";

	@Override
	public Object getAsObject(String valor) {
		return formatAsObject(valor, 2);
	}

	@Override
	public String getAsString(Object valor) {
		return formatAsString(valor, PATTERN);
	}

	protected Object formatAsObject(String valor, final int scale) {
		if (StringUtils.isBlank(valor)) {
			return null;
		}
		try {
			valor = valor.replace(".", "").replace(',', '.');
			return new BigDecimal(valor).setScale(scale, BigDecimal.ROUND_DOWN).stripTrailingZeros();
		} catch (NumberFormatException ex) {
			logError("Erro ao tentar converter valor: " + valor);
		}
		return null;
	}

	protected String formatAsString(final Object valor, final String pattern) {
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(LOCALE_BR);
		dfSymbols.setDecimalSeparator(',');
		String retorno = null;
		if (valor != null) {
			try {
				Double.parseDouble(valor.toString());
			} catch (Exception e) {
				logDebug("Erro ao tentar converter valor: " + valor);
				return null;
			}
			DecimalFormat df = new DecimalFormat(pattern, dfSymbols);
			df.setRoundingMode(RoundingMode.DOWN);
			retorno = df.format(new BigDecimal(valor.toString()));
		}
		return retorno;
	}

}
