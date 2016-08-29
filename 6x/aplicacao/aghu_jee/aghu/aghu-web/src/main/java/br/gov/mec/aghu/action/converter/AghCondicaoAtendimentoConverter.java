package br.gov.mec.aghu.action.converter;

import javax.ejb.EJB;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "aghCondicaoAtendimentoConverter")
public class AghCondicaoAtendimentoConverter extends AbstractConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8131739400200701810L;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Override
	public Object getAsObject(String valor) {
		if (valor==null || valor.isEmpty()){
			return null;
		}		
		return  ambulatorioFacade.obterCondicaoAtendimentoPorCodigo(Short.valueOf(valor));
	}

	@Override
	public String getAsString(Object valor) {
		if (valor==null) {
			return null;
		}
		return ((AacCondicaoAtendimento) valor).getSeq().toString();
	}

}
