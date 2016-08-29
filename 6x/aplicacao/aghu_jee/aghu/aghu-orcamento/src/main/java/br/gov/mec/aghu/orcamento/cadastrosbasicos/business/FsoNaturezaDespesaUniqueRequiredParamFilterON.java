package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Filtro de Natureza Despesa Parâmetro Único e Obrigatório
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoNaturezaDespesaUniqueRequiredParamFilterON.class)
public class FsoNaturezaDespesaUniqueRequiredParamFilterON
		extends BaseBusiness
		implements FsoParametrosOrcamentoFilterStrategy<Boolean> {

private static final Log LOG = LogFactory.getLog(FsoNaturezaDespesaUniqueRequiredParamFilterON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;	
	private static final long serialVersionUID = -1883033408450951272L;

	@Override
	public Boolean find(FsoParametrosOrcamentoCriteriaVO criteria) {
		Boolean isUnique = null;
		Long count = getFsoParametrosOrcamentoDAO()
				.contarParametrosOrcamento(criteria);
		
		if (count > 0) {
			if (count.equals(1)) {
				isUnique = getFsoParametrosOrcamentoDAO()
						.pesquisarParametrosOrcamento(criteria).get(0)
						.getAcaoNtd()
						.equals(DominioAcaoParametrosOrcamento.O);
			} else {
				isUnique = false;
			}
		}
		
		return isUnique;
	}

	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}
}