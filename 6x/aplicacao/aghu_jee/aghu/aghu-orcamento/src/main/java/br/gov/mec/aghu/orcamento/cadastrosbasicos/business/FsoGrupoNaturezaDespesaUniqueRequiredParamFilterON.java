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
 * Filtro de Grupo de Natureza Despesa Parâmetro Único e Obrigatório
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoGrupoNaturezaDespesaUniqueRequiredParamFilterON.class)
public class FsoGrupoNaturezaDespesaUniqueRequiredParamFilterON
		extends BaseBusiness
		implements FsoParametrosOrcamentoFilterStrategy<Boolean> {

private static final Log LOG = LogFactory.getLog(FsoGrupoNaturezaDespesaUniqueRequiredParamFilterON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;
	private static final long serialVersionUID = 1664547753981405104L;

	@Override
	public Boolean find(FsoParametrosOrcamentoCriteriaVO criteria) {
		Long count = getFsoParametrosOrcamentoDAO()
				.contarParametrosOrcamento(criteria);
		
		Boolean isUnique = null;
		
		if (count > 0) {
			if (count.equals(1)) {
				isUnique = getFsoParametrosOrcamentoDAO()
						.pesquisarParametrosOrcamento(criteria).get(0)
						.getAcaoGnd()
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