package br.gov.mec.aghu.action.converter;

import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.converter.AbstractConverter;

/**
 * Retorna a situação com valor Sim(Ativa) ou Não(Inativa)
 *
 */
@FacesConverter(value = "situacaoAtivoSimNaoConverter")
public class SituacaoAtivoSimNaoConverter extends AbstractConverter {
	private static final long serialVersionUID = -5472887346884309505L;

	@Override
	public String getAsString(Object valor) {
		switch (DominioSituacao.valueOf(DominioSituacao.class, valor.toString())){
			case A:
				return "Sim";
			case I:
				return "Não";
			default:
				return "";
		}
		
	}

	@Override
	public Object getAsObject(String valor) {
		return DominioSituacao.valueOf(valor).getCodigo();
	}
}
