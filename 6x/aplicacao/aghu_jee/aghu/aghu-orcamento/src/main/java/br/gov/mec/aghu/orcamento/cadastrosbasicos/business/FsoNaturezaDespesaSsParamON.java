package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ON de Parâmetros Orçamentários de Naturezas de Despesa de SS
 * 
 * @author mlcruz
 */
@Stateless
public class FsoNaturezaDespesaSsParamON extends BaseBusiness {

	@EJB
	private FsoParametrosOrcamentoSsFinderON fsoParametrosOrcamentoSsFinderON;

	@EJB
	private FsoParametrosOrcamentoON fsoParametrosOrcamentoON;

	@EJB
	private FsoNaturezaDespesaParamFilterON fsoNaturezaDespesaParamFilterON;

	@EJB
	private FsoNaturezaDespesaValidParamFilterON fsoNaturezaDespesaValidParamFilterON;

	
	@EJB
	private FsoNaturezaDespesaUniqueRequiredParamFilterON fsoNaturezaDespesaUniqueRequiredParamFilterON;

	@EJB
	private FsoNaturezaDespesaParamListFilterON fsoNaturezaDespesaParamListFilterON;

	private static final Log LOG = LogFactory
			.getLog(FsoNaturezaDespesaSsParamON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = -2861540955604132354L;

	/**
	 * Obtem natureza parâmetro conforme SC.
	 * 
	 * @param servico
	 *            Serviço
	 * @param grupoNatureza
	 *            Grupo de Natureza
	 * @return Natureza de Despesa
	 */
	public FsoNaturezaDespesa getNaturezaSsParam(ScoServico servico,
			FsoGrupoNaturezaDespesa grupoNatureza) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setServico(servico);
		criteria.setGrupoNatureza(grupoNatureza);
		criteria.setSituacao(DominioSituacao.A);

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		// Traz no máximo dois para saber se existe um ou mais.
		criteria.setMaxResults(2);
		
		return getFsoParametrosOrcamentoSsFinderON().getResult(criteria, getFsoNaturezaDespesaParamFilterON());
	}

	/**
	 * Indica se natureza possui parâmetro obrigatório Ãºnico.
	 * 
	 * @param servico
	 *            Serviço
	 * @param grupoNatureza
	 *            Grupo de Natureza
	 * @return Flag
	 */
	public boolean hasUniqueRequiredNaturezaSsParam(ScoServico servico,
			FsoGrupoNaturezaDespesa grupoNatureza) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setServico(servico);
		criteria.setGrupoNatureza(grupoNatureza);
		criteria.setSituacao(DominioSituacao.A);

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		criteria.setMaxResults(2);

		Boolean flag = getFsoParametrosOrcamentoSsFinderON().getResult(
				criteria, getFsoNaturezaDespesaUniqueRequiredParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Indica se a natureza de despesa é válida.
	 * 
	 * @param servico
	 *            Serviço
	 * @param grupoNatureza
	 *            Grupo de Natureza de Despesa
	 * @param natureza
	 *            Natureza de Despesa
	 * @return Flag
	 */
	public boolean isNaturezaValidSsParam(ScoServico servico,
			FsoNaturezaDespesa natureza) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setNatureza(natureza);
		criteria.setNaturezaShouldBeNull(natureza == null);
		criteria.setMaxResults(1);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoSsParam(servico,
						FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);

		if (acaoParam != null) {
			// Se existe apenas um parâmetro de sugestão, então qualquer valor é
			// válido.
			if (DominioAcaoParametrosOrcamento.S.equals(acaoParam.getAcaoNtd())) {
				return true;
			}

			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoNtd());
			criteria.setAcoes(acoes);
		}

		Boolean flag = getFsoParametrosOrcamentoSsFinderON().getResult(
				criteria, getFsoNaturezaDespesaValidParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Obtem naturezas parametrizadas.
	 * 
	 * @param servico
	 *            Serviço
	 * @param grupoNatureza
	 *            Grupo de Natureza de Despesa
	 * @param filter
	 *            Filtro
	 * @return Parâmetros
	 */
	public List<FsoNaturezaDespesa> listarNaturezaSsParams(ScoServico servico,
			FsoGrupoNaturezaDespesa grupoNatureza, Object filter) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setGrupoNatureza(grupoNatureza);
		criteria.setFiltro(filter);
		criteria.setMaxResults(100);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoSsParam(servico,
						FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);

		if (acaoParam != null) {
			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoNtd());
			criteria.setAcoes(acoes);
		}

		criteria.setOrder(DominioAcaoParametrosOrcamento.O.equals(criteria
				.getAcao()) ? FsoParametrosOrcamento.Fields.NATUREZA_DESPESA
				.toString() : FsoNaturezaDespesa.Fields.ID.toString());

		return getFsoParametrosOrcamentoSsFinderON().getResult(criteria,
				getFsoNaturezaDespesaParamListFilterON());
	}

	protected FsoParametrosOrcamentoON getFsoParametrosOrcamentoON() {
		return fsoParametrosOrcamentoON;
	}

	protected FsoParametrosOrcamentoSsFinderON getFsoParametrosOrcamentoSsFinderON() {
		return fsoParametrosOrcamentoSsFinderON;
	}

	protected FsoNaturezaDespesaParamFilterON getFsoNaturezaDespesaParamFilterON() {
		return fsoNaturezaDespesaParamFilterON;
	}

	protected FsoNaturezaDespesaUniqueRequiredParamFilterON getFsoNaturezaDespesaUniqueRequiredParamFilterON() {
		return fsoNaturezaDespesaUniqueRequiredParamFilterON;
	}

	protected FsoNaturezaDespesaValidParamFilterON getFsoNaturezaDespesaValidParamFilterON() {
		return fsoNaturezaDespesaValidParamFilterON;
	}

	protected FsoNaturezaDespesaParamListFilterON getFsoNaturezaDespesaParamListFilterON() {
		return fsoNaturezaDespesaParamListFilterON;
	}
}