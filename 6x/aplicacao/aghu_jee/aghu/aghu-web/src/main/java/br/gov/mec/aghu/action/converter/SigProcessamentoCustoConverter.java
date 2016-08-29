package br.gov.mec.aghu.action.converter;

import javax.ejb.EJB;
import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.converter.AbstractConverter;

@FacesConverter(value = "sigProcessamentoCustoConverter")
public class SigProcessamentoCustoConverter  extends AbstractConverter {

	private static final long serialVersionUID = 2064788299274032478L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@Override
	public Object getAsObject(String arg0) {
		return custosSigFacade.obterSigProcessamentoCustoPorChavePrimaria(Integer.valueOf(arg0));
	}
	
	@Override
	public String getAsString(Object valor) {
		return valor instanceof SigProcessamentoCusto ? ((SigProcessamentoCusto) valor).getSeq().toString() : "";
	}
}
