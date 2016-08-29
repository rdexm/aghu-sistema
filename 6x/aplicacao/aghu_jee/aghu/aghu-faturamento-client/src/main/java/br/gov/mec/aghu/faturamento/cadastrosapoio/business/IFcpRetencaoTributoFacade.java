package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.model.FcpRetencaoTributo;

public interface IFcpRetencaoTributoFacade extends Serializable {
	
	List<FcpRetencaoTributo> pesquisa(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, DominioTipoTributo situacao);
	
	Long pesquisaCount(Integer codigo,
			DominioTipoTributo situacao);
	
}
