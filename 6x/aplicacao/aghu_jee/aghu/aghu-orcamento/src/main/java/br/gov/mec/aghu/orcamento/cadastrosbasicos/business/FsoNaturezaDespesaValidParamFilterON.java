package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import javax.ejb.EJB;
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
 * Filtro de Natureza de Despesa Parâmetro Válido
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoNaturezaDespesaValidParamFilterON.class)
public class FsoNaturezaDespesaValidParamFilterON
		extends BaseBusiness
		implements FsoParametrosOrcamentoFilterStrategy<Boolean> {

private static final Log LOG = LogFactory.getLog(FsoNaturezaDespesaValidParamFilterON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;
	private static final long serialVersionUID = 7021790848608507357L;

	@Override
	public Boolean find(FsoParametrosOrcamentoCriteriaVO criteria) {
		Boolean isValid = null;
		
		if (criteria.getAcao() == null) {
			if (criteria.getNatureza() != null) {
				isValid = getCadastrosBasicosOrcamentoFacade()
						.existeNaturezaDespesaAtiva(
								criteria.getNatureza().getId());
			} else {
				isValid = true;
			}
		} else {
			FsoParametrosOrcamentoCriteriaVO clone = null;
			
			try {
				clone = criteria.cloneBasico();
			} catch (CloneNotSupportedException e) {
				logError(e);
			}
			
			if (getFsoParametrosOrcamentoDAO().contarParametrosOrcamento(clone) > 0) {
				if (DominioAcaoParametrosOrcamento.R.equals(criteria
						.getAcao())) {
					if (criteria.getNatureza() != null) {
						isValid = !getFsoParametrosOrcamentoDAO()
								.contarNaturezas(criteria).equals(0);
					} else {
						isValid = true;
					}
				} else {
					isValid = !getFsoParametrosOrcamentoDAO()
							.contarParametrosOrcamento(criteria)
							.equals(0);
				}
			}
		}
		
		return isValid;
	}

	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}
	
	protected ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return cadastrosBasicosOrcamentoFacade;
	}
}