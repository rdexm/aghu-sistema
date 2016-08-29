package br.gov.mec.aghu.action.converter;

import java.util.List;

import javax.faces.convert.FacesConverter;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.core.converter.AbstractConverter;
import br.gov.mec.aghu.core.locator.ServiceLocator;

@FacesConverter(value = "aelSitItemSolicitacoesConverter")
public class AelSitItemSolicitacoesConverter extends AbstractConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -665897076448166102L;
	
	
	private IExamesFacade examesFacade = ServiceLocator.getBean(IExamesFacade.class, "aghu-exames");
	

	@Override
	public String getAsString(Object valor) {
		return ((AelSitItemSolicitacoes) valor).getCodigo();
	}
	
	@Override
	public Object getAsObject(String valor) {
		List<AelSitItemSolicitacoes> list = examesFacade.buscarListaAelSitItemSolicitacoesPorParametro(valor);
		return list.isEmpty() ? null : list.get(0);
	}

}
