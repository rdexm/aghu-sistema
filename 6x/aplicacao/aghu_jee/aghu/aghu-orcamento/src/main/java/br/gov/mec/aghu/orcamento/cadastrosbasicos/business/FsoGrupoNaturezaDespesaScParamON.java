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
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ON de Parâmetros Orçamentários de Grupos de Natureza de SC
 * 
 * @author mlcruz
 */
@Stateless
public class FsoGrupoNaturezaDespesaScParamON extends BaseBusiness {

	// TODO migracao
	@EJB
	private FsoParametrosOrcamentoFilterON fsoParametrosOrcamentoFilterON;

	@EJB
	private FsoParametrosOrcamentoScFinderON fsoParametrosOrcamentoScFinderON;

	@EJB
	private FsoParametrosOrcamentoON fsoParametrosOrcamentoON;

	
	@EJB
	private FsoGrupoNaturezaDespesaUniqueRequiredParamFilterON fsoGrupoNaturezaDespesaUniqueRequiredParamFilterON;

	@EJB
	private FsoGrupoNaturezaDespesaParamListFilterON fsoGrupoNaturezaDespesaParamListFilterON;

	private static final Log LOG = LogFactory
			.getLog(FsoGrupoNaturezaDespesaScParamON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = 4035490415767399748L;

	/**
	 * Obtem grupo de natureza parâmetro conforme SC.
	 * 
	 * @param material
	 *            Material.
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @param valor
	 *            Valor da SC.
	 * @return Grupo de natureza parâmetro.
	 */
	public FsoGrupoNaturezaDespesa getGrupoNaturezaScParam(
			ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valor) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.GRUPO_NATUREZA);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);
		criteria.setMaxResults(2);

		FsoParametrosOrcamento param = getFsoParametrosOrcamentoScFinderON()
				.getResult(criteria, getFsoParametrosOrcamentoFilterON());

		return param != null ? param.getGrupoNaturezaDespesa() : null;
	}

	/**
	 * Indica se grupo de natureza possui parâmetro obrigatório Ãºnico.
	 * 
	 * @param material
	 *            Material.
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @param valor
	 *            Valor da SC.
	 * @return Indicador.
	 */
	public boolean hasUniqueRequiredGrupoNaturezaScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.GRUPO_NATUREZA);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		criteria.setMaxResults(2);

		Boolean flag = getFsoParametrosOrcamentoScFinderON().getResult(
				criteria,
				getFsoGrupoNaturezaDespesaUniqueRequiredParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Obtem grupos de natureza parametrizados.
	 * 
	 * @param material
	 *            Material.
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @param valorTotal
	 *            Valor da SC.
	 * @param filter
	 *            Filtro do grupo por ID ou descrição.
	 * @return Grupos de natureza parametrizados.
	 */
	public List<FsoGrupoNaturezaDespesa> listarGruposNaturezaScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valor, Object filter) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.GRUPO_NATUREZA);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		criteria.setFiltro(filter);
		criteria.setMaxResults(100);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoScParam(
						material,
						centroCusto,
						valor,
						FsoParametrosOrcamentoCriteriaVO.Parametro.GRUPO_NATUREZA);

		if (acaoParam != null) {
			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoGnd());
			criteria.setAcoes(acoes);
		}

		criteria.setOrder(DominioAcaoParametrosOrcamento.O.equals(criteria
				.getAcao()) ? FsoParametrosOrcamento.Fields.GRUPO_NATUREZA_DESPESA
				.toString() : FsoGrupoNaturezaDespesa.Fields.CODIGO.toString());

		return getFsoParametrosOrcamentoScFinderON().getResult(criteria,
				getFsoGrupoNaturezaDespesaParamListFilterON());
	}

	protected FsoParametrosOrcamentoON getFsoParametrosOrcamentoON() {
		return fsoParametrosOrcamentoON;
	}

	protected FsoParametrosOrcamentoScFinderON getFsoParametrosOrcamentoScFinderON() {
		return fsoParametrosOrcamentoScFinderON;
	}

	protected FsoParametrosOrcamentoFilterON getFsoParametrosOrcamentoFilterON() {
		return fsoParametrosOrcamentoFilterON;
	}

	protected FsoGrupoNaturezaDespesaUniqueRequiredParamFilterON getFsoGrupoNaturezaDespesaUniqueRequiredParamFilterON() {
		return fsoGrupoNaturezaDespesaUniqueRequiredParamFilterON;
	}

	protected FsoGrupoNaturezaDespesaParamListFilterON getFsoGrupoNaturezaDespesaParamListFilterON() {
		return fsoGrupoNaturezaDespesaParamListFilterON;
	}
}