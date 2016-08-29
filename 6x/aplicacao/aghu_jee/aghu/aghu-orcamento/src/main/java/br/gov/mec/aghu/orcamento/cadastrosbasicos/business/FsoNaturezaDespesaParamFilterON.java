package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Filtro de Grupo de Natureza de Despesa Parâmetro
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoNaturezaDespesaParamFilterON.class)
public class FsoNaturezaDespesaParamFilterON
		extends BaseBusiness
		implements FsoParametrosOrcamentoFilterStrategy<FsoNaturezaDespesa> {

private static final Log LOG = LogFactory.getLog(FsoNaturezaDespesaParamFilterON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ISolicitacaoServicoFacade solicitacaoServicoFacade;

@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;

@EJB
private ISolicitacaoComprasFacade solicitacaoComprasFacade;	
	private static final long serialVersionUID = -1717107988103175405L;

	@Override
	public FsoNaturezaDespesa find(FsoParametrosOrcamentoCriteriaVO criteria) {		
		if (!getFsoParametrosOrcamentoDAO().contarParametrosOrcamento(criteria).equals(1L)) {
			return null;
		}
	
		FsoParametrosOrcamento param = getFsoParametrosOrcamentoDAO()
				.pesquisarParametrosOrcamento(criteria).get(0);
		
		logInfo(String
				.format("Encontrada natureza a partir do parâmetro %d.",
						param.getSeq()));
		
		if (Boolean.TRUE.equals(param.getIsCadastradaGrupo())) {
			switch (criteria.getAplicacao()) {
			case SC:
				logInfo(String.format(
						"Obtendo natureza cadastrada no grupo do material %d, "
								+ "conforme parâmetro %d", criteria
								.getMaterial().getCodigo(), param
								.getSeq()));
				
				return getSolicitacaoComprasFacade()
						.obterNaturezaDespesa(
								criteria.getGrupoNatureza(),
								criteria.getMaterial());
				
			case SS:
				logInfo(String.format(
						"Obtendo natureza cadastrada no serviço %d, "
								+ "conforme parâmetro %d", criteria
								.getServico().getCodigo(), param
								.getSeq()));
				
				return criteria.getServico().getNaturezaDespesa();
				
			default: throw new IllegalArgumentException(criteria.getAplicacao().toString());
			}
		} else {
			return param.getNaturezaDespesa();
		}
	}

	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}
	
	protected ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return solicitacaoServicoFacade;
	}
}