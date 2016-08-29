package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

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
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ON de Parâmetros Orçamentários de Verbas de Gestão de SS
 * 
 * @author mlcruz
 */
@Stateless
public class FsoVerbaGestaoSsParamON extends BaseBusiness {

	
	@EJB
	private FsoParametrosOrcamentoFilterON fsoParametrosOrcamentoFilterON;

	@EJB
	private FsoVerbaGestaoValidParamFilterON fsoVerbaGestaoValidParamFilterON;

	@EJB
	private FsoParametrosOrcamentoSsFinderON fsoParametrosOrcamentoSsFinderON;

	@EJB
	private FsoParametrosOrcamentoON fsoParametrosOrcamentoON;

	@EJB
	private FsoVerbaGestaoParamListFilterON fsoVerbaGestaoParamListFilterON;

	@EJB
	private FsoVerbaGestaoUniqueRequiredParamFilterON fsoVerbaGestaoUniqueRequiredParamFilterON;

	private static final Log LOG = LogFactory
			.getLog(FsoVerbaGestaoSsParamON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = -4755764062582190187L;

	/**
	 * Obtem verba de gestão parâmetro conforme SC.
	 * 
	 * @param servico
	 *            Serviço
	 * @return Verba de Gestão
	 */
	public FsoVerbaGestao getVerbaGestaoSsParam(ScoServico servico) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setDataReferencia(new Date());

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);
		criteria.setMaxResults(2);

		FsoParametrosOrcamento param = getFsoParametrosOrcamentoSsFinderON()
				.getResult(criteria, getFsoParametrosOrcamentoFilterON());

		return param != null ? param.getVerbaGestao() : null;
	}

	/**
	 * Indica se verba de gestão possui parâmetro obrigatório único.
	 * 
	 * @param servico
	 *            Serviço
	 * @return Flag
	 */
	public Boolean hasUniqueRequiredVerbaGestaoSsParam(ScoServico servico) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setDataReferencia(new Date());

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		criteria.setMaxResults(2);

		Boolean flag = getFsoParametrosOrcamentoSsFinderON().getResult(
				criteria, getFsoVerbaGestaoUniqueRequiredParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Indica se a verba de gestão é válida.
	 * 
	 * @param servico
	 *            Serviço
	 * @param verbaGestao
	 *            Verba de Gestão
	 * @return Flag
	 */
	public Boolean isVerbaGestaoValidSsParam(ScoServico servico,
			FsoVerbaGestao verbaGestao) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setVerbaGestao(verbaGestao);
		criteria.setVerbaGestaoShouldBeNull(verbaGestao == null);
		criteria.setDataReferencia(new Date());
		criteria.setMaxResults(1);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoSsParam(servico,
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

		Boolean flag = getFsoParametrosOrcamentoSsFinderON().getResult(
				criteria, getFsoVerbaGestaoValidParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Obtem verbas de gestão parametrizadas.
	 * 
	 * @param servico
	 *            Serviço
	 * @param filter
	 *            Filtro
	 * @return Verbas de Gestão
	 */
	public List<FsoVerbaGestao> listarVerbasGestaoScParams(ScoServico servico,
			Object filter) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setDataReferencia(new Date());
		criteria.setFiltro(filter);
		criteria.setMaxResults(100);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoSsParam(servico,
						FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO);

		if (acaoParam != null) {
			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoVbg());
			criteria.setAcoes(acoes);
		}

		criteria.setOrder(DominioAcaoParametrosOrcamento.O.equals(criteria
				.getAcao()) ? FsoParametrosOrcamento.Fields.VERBA_GESTAO
				.toString() : FsoVerbaGestao.Fields.SEQ.toString());

		return getFsoParametrosOrcamentoSsFinderON().getResult(criteria,
				getFsoVerbaGestaoParamListFilterON());
	}

	protected FsoParametrosOrcamentoON getFsoParametrosOrcamentoON() {
		return fsoParametrosOrcamentoON;
	}

	protected FsoParametrosOrcamentoSsFinderON getFsoParametrosOrcamentoSsFinderON() {
		return fsoParametrosOrcamentoSsFinderON;
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