package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Filtro de Verbas de Gestão Parâmetros
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoVerbaGestaoParamListFilterON.class)
public class FsoVerbaGestaoParamListFilterON extends BaseBusiness implements
		FsoParametrosOrcamentoFilterStrategy<List<FsoVerbaGestao>> {

	private static final Log LOG = LogFactory
			.getLog(FsoVerbaGestaoParamListFilterON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	@Inject
	private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;
	private static final long serialVersionUID = 8055253850322250040L;

	@Override
	public List<FsoVerbaGestao> find(FsoParametrosOrcamentoCriteriaVO criteria) {
		List<FsoVerbaGestao> result = null;

		if (criteria.getAcao() == null
				|| DominioAcaoParametrosOrcamento.S.equals(criteria.getAcao())) {
			logInfo("Não foram encontradas verbas de gestão parametrizadas.");

			result = getCadastrosBasicosOrcamentoFacade().pesquisarVerbaGestao(
					criteria.getFiltro(), criteria.getDataReferencia(),
					criteria.getMaxResults());
		} else {
			FsoParametrosOrcamentoCriteriaVO clone = null;

			try {
				clone = criteria.cloneBasico();
			} catch (CloneNotSupportedException e) {
				logError(e.getMessage());
			}

			if (getFsoParametrosOrcamentoDAO().contarParametrosOrcamento(clone) > 0) {
				if (DominioAcaoParametrosOrcamento.O.equals(criteria.getAcao())) {
					result = new ArrayList<FsoVerbaGestao>();

					for (FsoParametrosOrcamento cct : getFsoParametrosOrcamentoDAO()
							.pesquisarParametrosOrcamento(criteria)) {
						logInfo(String
								.format("Encontrada verba de gestão a partir do parâmetro %d.",
										cct.getSeq()));

						result.add(cct.getVerbaGestao());
					}
				} else {
					logInfo("Obtendo verbas de gestão não restringidas.");

					result = getFsoParametrosOrcamentoDAO()
							.pesquisarVerbasGestao(criteria);
				}
			}
		}

		return result != null && !result.isEmpty() ? result : null;
	}

	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}

	protected ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return cadastrosBasicosOrcamentoFacade;
	}
}