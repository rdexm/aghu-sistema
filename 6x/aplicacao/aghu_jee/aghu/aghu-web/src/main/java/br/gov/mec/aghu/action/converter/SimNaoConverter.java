package br.gov.mec.aghu.action.converter;

import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.converter.AbstractConverter;

/**
 * Converter sim/nao.
 */

@FacesConverter(value = "simNaoConverter")
public class SimNaoConverter extends AbstractConverter {

	private static final long serialVersionUID = 5232412427613337919L;

	@Override
	public String getAsString(Object valor) {
		return DominioSimNao
				.getInstance(Boolean.parseBoolean(valor.toString()))
				.getDescricao();
	}

	@Override
	public Object getAsObject(String valor) {
		return DominioSimNao.valueOf(valor).getCodigo();
	}

}
