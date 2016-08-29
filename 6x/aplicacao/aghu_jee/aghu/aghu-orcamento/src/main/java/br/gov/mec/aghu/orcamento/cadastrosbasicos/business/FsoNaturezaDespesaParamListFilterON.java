package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Filtro de Natureza de Despesa Parâmetros
 * 
 * @author mlcruz
 */
@Stateless
@Local(FsoNaturezaDespesaParamListFilterON.class)
public class FsoNaturezaDespesaParamListFilterON
		extends BaseBusiness
		implements FsoParametrosOrcamentoFilterStrategy<List<FsoNaturezaDespesa>> {

private static final Log LOG = LogFactory.getLog(FsoNaturezaDespesaParamListFilterON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ISolicitacaoServicoFacade solicitacaoServicoFacade;

@EJB
private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

@Inject
private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;

@EJB
private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	private static final long serialVersionUID = -8841768282302321253L;

	@Override
	public List<FsoNaturezaDespesa> find(FsoParametrosOrcamentoCriteriaVO criteria) {
		List<FsoNaturezaDespesa> result = null;
		
		if (criteria.getAcao() == null
				|| DominioAcaoParametrosOrcamento.S.equals(criteria
						.getAcao())) {
			logInfo("Não foram encontrados naturezas parametrizadas.");
			
			result = getCadastrosBasicosOrcamentoFacade()
					.pesquisarNaturezasDespesaAtivas(
							criteria.getGrupoNatureza(),
							criteria.getFiltro());
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
					result = getNaturezaList(criteria);
				} else {
					logInfo("Obtendo naturezas não restringidas.");
					
					result = getFsoParametrosOrcamentoDAO()
							.pesquisarNaturezas(criteria);
				}
			}
		}
		
		return result;
	}
	
	private List<FsoNaturezaDespesa> getNaturezaList(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		List<FsoParametrosOrcamento> params = getFsoParametrosOrcamentoDAO()
				.pesquisarParametrosOrcamento(criteria);
		List<FsoNaturezaDespesa> tmpResult = new ArrayList<FsoNaturezaDespesa>();

		for (FsoParametrosOrcamento param : params) {
			if (Boolean.TRUE.equals(param.getIsCadastradaGrupo())) {
				switch (criteria.getAplicacao()) {
				case SC:
					logInfo(String.format(
							"Obtendo natureza cadastrada no grupo do material %d, "
									+ "conforme parâmetro %d", criteria
									.getMaterial().getCodigo(), param.getSeq()));

					tmpResult.add(getSolicitacaoComprasFacade()
							.obterNaturezaDespesa(criteria.getGrupoNatureza(),
									criteria.getMaterial()));
					
					break;
				case SS:
					logInfo(String.format(
							"Obtendo natureza cadastrada no serviço %d, "
									+ "conforme parâmetro %d", criteria
									.getServico().getCodigo(), param.getSeq()));

					tmpResult.add(criteria.getServico().getNaturezaDespesa());					
					break;
				}
			} else {
				logInfo(String.format(
						"Encontrada natureza a partir do parâmetro %d.", param.getSeq()));
				
				tmpResult.add(param.getNaturezaDespesa());
			}
		}

		Object[] resultArr = tmpResult.toArray();

		// É preciso fazer novo ordenamento em função de algumas
		// naturezas virem dos parâmetros e outras virem do
		// grupo de
		// material.
		Arrays.sort(resultArr, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				FsoNaturezaDespesa n1 = (FsoNaturezaDespesa) o1;
				FsoNaturezaDespesa n2 = (FsoNaturezaDespesa) o2;

				int a = n1.getGrupoNaturezaDespesa().getCodigo()
						.compareTo(n2.getGrupoNaturezaDespesa().getCodigo());

				if (a == 0) {
					return n1.getId().getCodigo()
							.compareTo(n2.getId().getCodigo());
				} else {
					return a;
				}
			}
		});

		List<FsoNaturezaDespesa> result = new ArrayList<FsoNaturezaDespesa>();
		for (Object o : resultArr) {
			result.add((FsoNaturezaDespesa) o);
		}
		return result;
	}

	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}
	
	protected ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return cadastrosBasicosOrcamentoFacade;
	}
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}
	
	protected ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return solicitacaoServicoFacade;
	}
}