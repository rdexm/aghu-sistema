package br.gov.mec.aghu.action.converter;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "fatConvenioSaudePlanoInternacaoConverter")
public class FatConvenioSaudePlanoInternacaoConverter extends AbstractConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3463200918617507092L;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@Override
	public Object getAsObject(String valor) {
		if (valor==null || valor.isEmpty()){
			return null;
		}
	
		List<FatConvenioSaudePlano> result  = faturamentoApoioFacade.pesquisarConvenioSaudePlanosInternacao(valor);
		
		if (result == null || result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	
	}
	
	@Override
	public String getAsString(Object valor) {
	
		FatConvenioSaudePlano plano = (FatConvenioSaudePlano) valor;
		
		return plano.getDescricao();
	
	}

}
