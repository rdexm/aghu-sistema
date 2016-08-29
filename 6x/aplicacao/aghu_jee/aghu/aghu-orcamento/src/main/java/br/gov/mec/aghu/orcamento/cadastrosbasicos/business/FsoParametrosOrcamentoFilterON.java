package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Filtro de Parâmetro Orçamentário
 * 
 * Responsável por obter um único parâmetro orçamentário.
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoParametrosOrcamentoFilterON.class)
public class FsoParametrosOrcamentoFilterON
		extends BaseBusiness
		implements FsoParametrosOrcamentoFilterStrategy<FsoParametrosOrcamento> {

private static final Log LOG = LogFactory.getLog(FsoParametrosOrcamentoFilterON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;
	private static final long serialVersionUID = 7005504770560027160L;

	@Override
	public FsoParametrosOrcamento find(FsoParametrosOrcamentoCriteriaVO criteria) {
		if (getFsoParametrosOrcamentoDAO().contarParametrosOrcamento(
				criteria).equals(1L)) {
			List<FsoParametrosOrcamento> result = getFsoParametrosOrcamentoDAO()
					.pesquisarParametrosOrcamento(criteria);
			
			if (!result.isEmpty()) {
				FsoParametrosOrcamento item = result.get(0);
				
				logInfo(String.format(
						"Encontrado parâmetro %d do tipo %s.", 
							item.getSeq(), criteria.getParametro().name()));
				
				return item;
			}
		}
		
		return null;
	}

	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}
}