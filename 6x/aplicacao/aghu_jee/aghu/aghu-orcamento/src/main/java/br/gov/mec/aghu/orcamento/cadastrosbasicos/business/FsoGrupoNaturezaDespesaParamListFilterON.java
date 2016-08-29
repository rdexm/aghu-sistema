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
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoGrupoNaturezaDespesaCriteriaVO;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Filtro de Grupos de Natureza de Despesa Parâmetros
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoGrupoNaturezaDespesaParamListFilterON.class)
public class FsoGrupoNaturezaDespesaParamListFilterON
		extends BaseBusiness
		implements FsoParametrosOrcamentoFilterStrategy<List<FsoGrupoNaturezaDespesa>> {

private static final Log LOG = LogFactory.getLog(FsoGrupoNaturezaDespesaParamListFilterON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;
	private static final long serialVersionUID = 3639737407342125107L;

	@Override
	public List<FsoGrupoNaturezaDespesa> find(FsoParametrosOrcamentoCriteriaVO criteria) {
		List<FsoGrupoNaturezaDespesa> result = null;
		
		if (criteria.getAcao() == null
				|| DominioAcaoParametrosOrcamento.S.equals(criteria
						.getAcao())) {
			logInfo("Não foram encontrados grupos de natureza parametrizados.");
			
			FsoGrupoNaturezaDespesaCriteriaVO cri = new FsoGrupoNaturezaDespesaCriteriaVO();
			cri.setFiltro(criteria.getFiltro());
			cri.setIndSituacao(DominioSituacao.A);
			
			result = getCadastrosBasicosOrcamentoFacade()
					.pesquisarGruposNaturezaDespesa(cri, 0, 100, null, true);
		} else {
			FsoParametrosOrcamentoCriteriaVO clone = null;

			try {
				clone = criteria.cloneBasico();
			} catch (CloneNotSupportedException e) {
				logError(e.getMessage());
			}

			if (getFsoParametrosOrcamentoDAO()
					.contarParametrosOrcamento(clone) > 0) {
				if (DominioAcaoParametrosOrcamento.O.equals(criteria
						.getAcao())) {
					result = new ArrayList<FsoGrupoNaturezaDespesa>();

					for (FsoParametrosOrcamento po : getFsoParametrosOrcamentoDAO()
							.pesquisarParametrosOrcamento(criteria)) {
						logInfo(String.format(
								"Encontrado grupo de natureza a partir do parâmetro %d.", 
								po.getSeq()));
						
						if (DominioTipoSolicitacao.SS.equals(po.getTpProcesso())
								&& Boolean.TRUE.equals(po.getIsCadastradaGrupo())) {
							FsoNaturezaDespesa natureza = criteria.getServico().getNaturezaDespesa();
							
							if (natureza != null && natureza.getGrupoNaturezaDespesa() != null) {
								result.add(natureza.getGrupoNaturezaDespesa());
							}
						} else if (po.getGrupoNaturezaDespesa() != null) {
							result.add(po.getGrupoNaturezaDespesa());
						}
					}
				} else {
					logInfo("Obtendo grupos de natureza não restringidos.");
					
					result = getFsoParametrosOrcamentoDAO()
							.pesquisarGruposNatureza(criteria);
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