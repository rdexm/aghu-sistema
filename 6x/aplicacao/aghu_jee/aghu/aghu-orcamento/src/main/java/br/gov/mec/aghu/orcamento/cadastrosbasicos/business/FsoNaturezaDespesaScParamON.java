package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.math.BigDecimal;
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
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ON de Parâmetros Orçamentários de Naturezas de Despesa de SC
 * 
 * @author mlcruz
 */
@Stateless
public class FsoNaturezaDespesaScParamON extends BaseBusiness {

	@EJB
	private FsoParametrosOrcamentoScFinderON fsoParametrosOrcamentoScFinderON;

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
			.getLog(FsoNaturezaDespesaScParamON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = 7560261683059075366L;

	/**
	 * Obtem natureza parâmetro conforme SC.
	 * 
	 * @param material
	 *            Material.
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @oaran grupoNatureza Grupo de natureza de despesas.
	 * @param valor
	 *            Valor da SC.
	 * @return Natureza parâmetro.
	 */
	public FsoNaturezaDespesa getNaturezaScParam(ScoMaterial material,
			FccCentroCustos centroCusto, FsoGrupoNaturezaDespesa grupoNatureza,
			BigDecimal valor) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		criteria.setGrupoNatureza(grupoNatureza);

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);
		criteria.setMaxResults(2);
		
		return getFsoParametrosOrcamentoScFinderON()
				.getResult(criteria, getFsoNaturezaDespesaParamFilterON());
	}

	/**
	 * Indica se natureza possui parâmetro obrigatório Ãºnico.
	 * 
	 * @param material
	 *            Material.
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @param grupoNatureza
	 * @param valor
	 *            Valor da SC.
	 * @return Indicador.
	 */
	public boolean hasUniqueRequiredNaturezaScParam(ScoMaterial material,
			FccCentroCustos centroCusto, FsoGrupoNaturezaDespesa grupoNatureza,
			BigDecimal valor) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		criteria.setGrupoNatureza(grupoNatureza);

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		criteria.setMaxResults(2);

		Boolean flag = getFsoParametrosOrcamentoScFinderON().getResult(
				criteria, getFsoNaturezaDespesaUniqueRequiredParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Indica se a natureza de despesa é válida.
	 * 
	 * @param material
	 *            Material.
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @param valor
	 *            Valor da SC.
	 * @param grupoNatureza
	 *            Grupo.
	 * @param natureza
	 *            Natureza.
	 * @return Indicador.
	 */
	public boolean isNaturezaValidScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor,
			FsoNaturezaDespesa natureza) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		criteria.setNatureza(natureza);
		criteria.setNaturezaShouldBeNull(natureza == null);
		criteria.setMaxResults(1);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoScParam(material, centroCusto, valor,
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

		Boolean flag = getFsoParametrosOrcamentoScFinderON().getResult(
				criteria, getFsoNaturezaDespesaValidParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Obtem naturezas parametrizadas.
	 * 
	 * @param material
	 *            Material.
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @param valor
	 *            Valor da SC.
	 * @param filter
	 *            Filtro do grupo por ID ou descrição.
	 * @return Naturezas parametrizadas.
	 */
	public List<FsoNaturezaDespesa> listarNaturezaScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			FsoGrupoNaturezaDespesa grupoNatureza, BigDecimal valor,
			Object filter) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		criteria.setGrupoNatureza(grupoNatureza);
		criteria.setFiltro(filter);
		criteria.setMaxResults(100);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoScParam(material, centroCusto, valor,
						FsoParametrosOrcamentoCriteriaVO.Parametro.NATUREZA);

		if (acaoParam != null) {
			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoNtd());
			criteria.setAcoes(acoes);
		}

		criteria.setOrder(DominioAcaoParametrosOrcamento.O.equals(criteria
				.getAcao()) ? FsoParametrosOrcamento.Fields.NATUREZA_DESPESA
				.toString() : FsoNaturezaDespesa.Fields.ID.toString());

		return getFsoParametrosOrcamentoScFinderON().getResult(criteria,
				getFsoNaturezaDespesaParamListFilterON());
	}

	protected FsoParametrosOrcamentoON getFsoParametrosOrcamentoON() {
		return fsoParametrosOrcamentoON;
	}

	protected FsoParametrosOrcamentoScFinderON getFsoParametrosOrcamentoScFinderON() {
		return fsoParametrosOrcamentoScFinderON;
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