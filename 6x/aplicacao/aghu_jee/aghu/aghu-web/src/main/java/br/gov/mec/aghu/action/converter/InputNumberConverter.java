package br.gov.mec.aghu.action.converter;

import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import br.gov.mec.aghu.core.converter.AbstractConverter;

/**
 * Converter para forcar a conversao do valor inserido no componente
 * <mec:inputNumero> para um tipo Inteiro (Short,Integer,Long,BigInteger)
 * 
 * @author ghernandez
 *
 */
@FacesConverter(value = "inputNumberConverter")
public class InputNumberConverter extends AbstractConverter {

    private static final long serialVersionUID = 3945147177403759906L;

    @Override
    public Object getAsObject(String valor) {
	if (StringUtils.isNumeric(valor)) {
	    try {
		return Short.valueOf(valor);
	    } catch (NumberFormatException e) {
		try {
		    return NumberUtils.createNumber(valor);
		} catch (NumberFormatException e1) {
		    logError("Erro ao tentar converter valor: " + valor);
		}
	    }
	}
	return null;
    }

    @Override
    public String getAsString(Object valor) {
	return (valor == null) ? null : valor.toString();
    }
}