package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.math.BigDecimal;
import java.util.Date;
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
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ON de Parâmetros Orçamentários de Verbas de Gestão de SC
 * 
 * @author mlcruz
 */
@Stateless
public class FsoVerbaGestaoScParamON extends BaseBusiness {

	// TODO migracao
	@EJB
	private FsoParametrosOrcamentoFilterON fsoParametrosOrcamentoFilterON;

	@EJB
	private FsoParametrosOrcamentoScFinderON fsoParametrosOrcamentoScFinderON;

	@EJB
	private FsoVerbaGestaoValidParamFilterON fsoVerbaGestaoValidParamFilterON;

	@EJB
	private FsoParametrosOrcamentoON fsoParametrosOrcamentoON;

	@EJB
	private FsoVerbaGestaoParamListFilterON fsoVerbaGestaoParamListFilterON;

	@EJB
	private FsoVerbaGestaoUniqueRequiredParamFilterON fsoVerbaGestaoUniqueRequiredParamFilterON;

	private static final Log LOG = LogFactory
			.getLog(FsoVerbaGestaoScParamON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = 2273076199217872075L;

	/**
	 * Obtem verba de gestão parâmetro conforme SC.
	 * 
	 * @param material
	 *            Material
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @param valor
	 *            Valor da SC.
	 * @return Centro de custo aplicação parâmetro.
	 */
	public FsoVerbaGestao getVerbaGestaoScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		criteria.setDataReferencia(new Date());

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		criteria.setMaxResults(2);

		FsoParametrosOrcamento param = getFsoParametrosOrcamentoScFinderON()
				.getResult(criteria, getFsoParametrosOrcamentoFilterON());

		return param != null ? param.getVerbaGestao() : null;
	}

	/**
	 * Indica se verba de gestão possui parâmetro obrigatório Ãºnico.
	 * 
	 * @param material
	 *            Material.
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @param valor
	 *            Valor da SC.
	 * @return Indicador.
	 */
	public Boolean hasUniqueRequiredVerbaGestaoScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valor) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		criteria.setDataReferencia(new Date());

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		criteria.setMaxResults(2);

		Boolean flag = getFsoParametrosOrcamentoScFinderON().getResult(
				criteria, getFsoVerbaGestaoUniqueRequiredParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Indica se a verba de gestão é válida.
	 * 
	 * @param material
	 *            Material.
	 * @param ccSolicitante
	 *            Centro de custo solicitante.
	 * @param valor
	 *            Valor da SC.
	 * @param verbaGestao
	 *            Verba de gestão.
	 * @return Indicador.
	 */
	public Boolean isVerbaGestaoValidScParam(ScoMaterial material,
			FccCentroCustos ccSolicitante, BigDecimal valor,
			FsoVerbaGestao verbaGestao) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(ccSolicitante);
		criteria.setVerbaGestao(verbaGestao);
		criteria.setVerbaGestaoShouldBeNull(verbaGestao == null);
		criteria.setDataReferencia(new Date());
		criteria.setMaxResults(1);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoScParam(material, ccSolicitante, valor,
						FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);

		if (acaoParam != null) {
			// Se existe apenas um parâmetro de sugestão, então qualquer valor é
			// válido.
			if (DominioAcaoParametrosOrcamento.S.equals(acaoParam.getAcaoVbg())) {
				return true;
			}

			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoVbg());
			criteria.setAcoes(acoes);
		}

		Boolean flag = getFsoParametrosOrcamentoScFinderON().getResult(
				criteria, getFsoVerbaGestaoValidParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Obtem verbas de gestão parametrizadas.
	 * 
	 * @param material
	 *            Material.
	 * @param centroCusto
	 *            Centro de custo solicitante.
	 * @param valorTotal
	 *            Valor da SC.
	 * @param filter
	 *            Filtro da verba de gestão por ID ou descrição.
	 * @return Verbas de gestão parametrizadas.
	 */
	public List<FsoVerbaGestao> listarVerbasGestaoScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valor, Object filter) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);
		criteria.setDataReferencia(new Date());
		criteria.setFiltro(filter);
		criteria.setMaxResults(100);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoScParam(material, centroCusto, valor,
						FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);

		if (acaoParam != null) {
			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoVbg());
			criteria.setAcoes(acoes);
		}

		criteria.setOrder(DominioAcaoParametrosOrcamento.O.equals(criteria
				.getAcao()) ? FsoParametrosOrcamento.Fields.VERBA_GESTAO
				.toString() : FsoVerbaGestao.Fields.SEQ.toString());

		return getFsoParametrosOrcamentoScFinderON().getResult(criteria,
				getFsoVerbaGestaoParamListFilterON());
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

	protected FsoVerbaGestaoUniqueRequiredParamFilterON getFsoVerbaGestaoUniqueRequiredParamFilterON() {
		return fsoVerbaGestaoUniqueRequiredParamFilterON;
	}

	protected FsoVerbaGestaoValidParamFilterON getFsoVerbaGestaoValidParamFilterON() {
		return fsoVerbaGestaoValidParamFilterON;
	}

	protected FsoVerbaGestaoParamListFilterON getFsoVerbaGestaoParamListFilterON() {
		return fsoVerbaGestaoParamListFilterON;
	}
}