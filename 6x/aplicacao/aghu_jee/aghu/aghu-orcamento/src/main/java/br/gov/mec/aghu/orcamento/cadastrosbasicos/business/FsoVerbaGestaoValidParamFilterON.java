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
 * Filtro de Grupo de Natureza de Despesa Parâmetro Válido
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoVerbaGestaoValidParamFilterON.class)
public class FsoVerbaGestaoValidParamFilterON
		extends BaseBusiness
		implements FsoParametrosOrcamentoFilterStrategy<Boolean> {

private static final Log LOG = LogFactory.getLog(FsoVerbaGestaoValidParamFilterON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;
	private static final long serialVersionUID = -3653661513149733840L;

	@Override
	public Boolean find(FsoParametrosOrcamentoCriteriaVO criteria) {
		Boolean isValid = null;
		
		if (criteria.getAcao() == null) {
			if (criteria.getVerbaGestao() != null) {
				isValid  = getCadastrosBasicosOrcamentoFacade()
						.existeVerbaGestaoComFonteVigente(
								criteria.getVerbaGestao().getSeq(),
								criteria.getDataReferencia());
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
				if (DominioAcaoParametrosOrcamento.R.equals(criteria.getAcao())) {
					if (criteria.getVerbaGestao() != null) {
						isValid = !getFsoParametrosOrcamentoDAO()
							.contarVerbasGestao(criteria).equals(0);
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