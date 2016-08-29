package br.gov.mec.aghu.action.converter;

import javax.faces.convert.FacesConverter;

/**
 * Converter para os valores da fórmula em #990 - Prescrição de Nutrição Parenteral
 * 
 * @author aghu
 */
@FacesConverter(value = "quantidadeNtpConverter")
public class QuantidadeNtpConverter extends VelocidadeNtpConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4195267863104299303L;

	private static final String PATTERN = "##########.####";

	@Override
	public Object getAsObject(String valor) {
		return formatAsObject(valor, 4);
	}

	@Override
	public String getAsString(Object valor) {
		return formatAsString(valor, PATTERN);
	}

}
