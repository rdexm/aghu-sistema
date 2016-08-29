package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO.Parametro;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoResultVO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.orcamento.dao.FsoVerbaGestaoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe ON responsável pelas regras de negócio de parâmetros de regras orçamentárias.
 */
@Stateless
public class FsoParametrosOrcamentoON extends BaseBusiness {

	@EJB
	private FsoParametrosOrcamentoScFinderON fsoParametrosOrcamentoScFinderON;

	@EJB
	private FsoParametrosOrcamentoSsFinderON fsoParametrosOrcamentoSsFinderON;

	@EJB
	private FsoParametrosOrcamentoAcaoFilterON fsoParametrosOrcamentoAcaoFilterON;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	
	@Inject
	private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private FsoVerbaGestaoDAO fsoVerbaGestaoDAO;

	private static final long serialVersionUID = 8183699985798028757L;
	
	private static final Log LOG = LogFactory.getLog(FsoParametrosOrcamentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Pesquisa por parâmetros de regras orçamentárias.
	 */
	public List<FsoParametrosOrcamentoResultVO> pesquisarParametrosOrcamento(FsoParametrosOrcamentoCriteriaVO criteria, int first, int max, String orderField, Boolean orderAsc) {
		return getFsoParametrosOrcamentoDAO().pesquisarParametrosOrcamentoVO(criteria, first, max, orderField, orderAsc);
	}

	/**
	 * Conta parâmetros de regras orçamentárias.
	 */
	public Long contarParametrosOrcamento(FsoParametrosOrcamentoCriteriaVO criteria) {
		return getFsoParametrosOrcamentoDAO().contarParametrosOrcamentoVO(criteria);
	}

	/**
	 * Obtem parâmetros de regras orçamentárias pelo ID.
	 */
	public FsoParametrosOrcamento obterParametrosOrcamento(Integer id) {
		return getFsoParametrosOrcamentoDAO().obterPorChavePrimaria(id);
	}
	
	public FsoParametrosOrcamento obterParametrosOrcamento(Integer id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getFsoParametrosOrcamentoDAO().obterPorChavePrimaria(id, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	/**
	 * Inclui parâmetros de orçamento.
	 */
	public void incluir(FsoParametrosOrcamento param) throws ApplicationBusinessException {

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		param.setServidorInclusao(servidorLogado);
		validar(param);
		getFsoParametrosOrcamentoDAO().persistir(param);
	}

	/**
	 * Altera parâmetros de orçamento.
	 */
	public void alterar(FsoParametrosOrcamento param) throws ApplicationBusinessException {

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		param.setServidorAlteracao(servidorLogado);
		
		validar(param);
		getFsoParametrosOrcamentoDAO().merge(param);
	}

	public void excluir(Integer seq) throws ApplicationBusinessException {
		getFsoParametrosOrcamentoDAO().removerPorId(seq);
	}

	public FsoParametrosOrcamento clonarParametroOrcamento(FsoParametrosOrcamento parametroOriginal) throws ApplicationBusinessException {
		FsoParametrosOrcamento copiaParametro;
		try {
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			copiaParametro = (FsoParametrosOrcamento) BeanUtils.cloneBean(parametroOriginal); 
			copiaParametro.setSeq(null);
			copiaParametro.setVersion(0);
			copiaParametro.setServidorInclusao(servidorLogado);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ExceptionCode.MENSAGEM_FALHA_CLONE_ORCAMENTO);
		}

		return copiaParametro;
	}

	/**
	 * Obtem parâmetro orçamentário de SC a fim de identificar sua ação.
	 * 
	 * @param material Material
	 * @param centroCusto Centro de Custo
	 * @param valor Valor da SC
	 * @param param Tipo de Parâmetro Orçamentário (Grupo de Natureza, Natureza, etc)
	 * @return Parâmetro Orçamentário
	 */
	public FsoParametrosOrcamento getAcaoScParam(ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valor, FsoParametrosOrcamentoCriteriaVO.Parametro param) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SC);
		criteria.setParametro(param);
		criteria.setMaterial(material);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setValor(valor);
		criteria.setCentroCusto(centroCusto);

		if (FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO.equals(param)) {
			criteria.setDataReferencia(new Date());
		}

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.R);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		criteria.setMaxResults(2);

		return getFsoParametrosOrcamentoScFinderON().getResult(criteria, getFsoParametrosOrcamentoAcaoFilterON());
	}

	/**
	 * Obtem parâmetro orçamentário de SS a fim de identificar sua ação.
	 * 
	 * @param servico Serviço
	 * @param param Tipo de Parâmetro
	 * @return Parâmetro Orçamentário
	 */
	public FsoParametrosOrcamento getAcaoSsParam(ScoServico servico, Parametro param) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(param);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);

		if (FsoParametrosOrcamentoCriteriaVO.Parametro.VERBA_GESTAO.equals(param)) {
			criteria.setDataReferencia(new Date());
		}

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.R);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		criteria.setMaxResults(2);

		return getFsoParametrosOrcamentoSsFinderON().getResult(criteria, getFsoParametrosOrcamentoAcaoFilterON());
	}

	/**
	 * Valida parâmetros de orçamento.
	 * 
	 * @param param Parâmetros de orçamento.
	 */
	private void validar(FsoParametrosOrcamento param) throws ApplicationBusinessException {

		if (param.getTpProcesso() == DominioTipoSolicitacao.SC
				&& param.getAcaoCct() == null && param.getAcaoGnd() == null
				&& param.getAcaoNtd() == null && param.getAcaoVbg() == null) {
			throw new ApplicationBusinessException(ExceptionCode.ERRO_ACAO_PARAMETRO_ORCAMENTO_OBRIGATORIA_SC);
		}

		if (param.getTpProcesso() == DominioTipoSolicitacao.SS
				&& param.getAcaoGnd() == null && param.getAcaoNtd() == null
				&& param.getAcaoVbg() == null) {
			throw new ApplicationBusinessException(ExceptionCode.ERRO_ACAO_PARAMETRO_ORCAMENTO_OBRIGATORIA_SS);
		}

		if (DominioSituacao.A.equals(param.getIndSituacao())) {
			List<FsoParametrosOrcamento> params = getFsoParametrosOrcamentoDAO().pesquisarParametrosOrcamento(param);

			for (FsoParametrosOrcamento item : params) {
				validarConflito(param.getAcaoGnd(), param.getGrupoNaturezaDespesa(), item.getAcaoGnd(), item.getGrupoNaturezaDespesa(), item.getSeq());
				validarConflito(param.getAcaoNtd(), param.getNaturezaDespesa(), item.getAcaoNtd(), item.getNaturezaDespesa(), item.getSeq());
				validarConflito(param.getAcaoVbg(), param.getVerbaGestao(), item.getAcaoVbg(), item.getVerbaGestao(), item.getSeq());
				validarConflito(param.getAcaoCct(), param.getCentroCustoReferencia(), item.getAcaoCct(), item.getCentroCustoReferencia(), item.getSeq());
			}
		}
	}

	/**
	 * Valida conflito de regras entre parâmetros.
	 * 
	 * @param a Ação A.
	 * @param oa Objeto da ação A.
	 * @param b  Ação B.
	 * @param ob Objeto da ação B.
	 * @param seq Código da regra conflitante.
	 */
	private void validarConflito(DominioAcaoParametrosOrcamento a, Object oa,
			DominioAcaoParametrosOrcamento b, Object ob, Integer seq) throws ApplicationBusinessException {
		Boolean isConflitante = false;

		if (DominioAcaoParametrosOrcamento.O.equals(a)) {
			if (DominioAcaoParametrosOrcamento.S.equals(b) || 
					DominioAcaoParametrosOrcamento.R.equals(b)) {
				isConflitante = true;
			} else if (isConflitante(a, oa, b, ob)) {
				isConflitante = true;
			}
			
		} else if (DominioAcaoParametrosOrcamento.S.equals(a)) {
			if (DominioAcaoParametrosOrcamento.O.equals(b) || 
					DominioAcaoParametrosOrcamento.S.equals(b)) {
				isConflitante = true;
			}
			
		} else if (DominioAcaoParametrosOrcamento.R.equals(a)) {
			if (DominioAcaoParametrosOrcamento.O.equals(b)) {
				isConflitante = true;
			} else if (isConflitante(a, oa, b, ob)) {
				isConflitante = true;
			}
		}

		if (isConflitante) {
			throw new ApplicationBusinessException(ExceptionCode.ERRO_REGRA_ORCAMENTARIA_JA_EXISTENTE, seq);
		}
	}

	private Boolean isConflitante(DominioAcaoParametrosOrcamento a, Object oa, DominioAcaoParametrosOrcamento b, Object ob) {
		if (a != null && a.equals(b)) {
			if (oa == null && ob == null) {
				return true;
			} else if (oa != null && oa.equals(ob)) {
				return true;
			} else if (ob != null && ob.equals(oa)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// a estrutura atual de regras orcamentarias nao permite programar a regra
	// para o centro de custo aplicacao = Fipe. desta forma, isso vai ter que ser feito por fora
	// (mudar o que existe eh muito trabalhoso, pois a mudanca eh estrutural e a estrutura foi feita pelo professor) 
	// amenegotto em 23/06/2014
	public FsoVerbaGestao obterVerbaGestaoProjetoFipe(FccCentroCustos ccAplic) {
		FsoVerbaGestao verbaFipe = null;
		Integer cctCodigo = null;
		
		if (ccAplic != null) {
			try {
				cctCodigo = this.getParametroFacade().
						buscarAghParametro(AghuParametrosEnum.P_CCUSTO_GPPG).getVlrNumerico().intValue();
			} catch (ApplicationBusinessException e) {
				LOG.error("Erro",e);
			}
			
			if (cctCodigo != null) {
				Set<Integer> listaHierarquica = getCentroCustoFacade().pesquisarCentroCustoComHierarquia(cctCodigo);
				if (listaHierarquica.contains(ccAplic.getCodigo())) {
					try {
						verbaFipe = this.getFsoVerbaGestaoDAO().obterPorChavePrimaria(this.getParametroFacade().
								buscarAghParametro(AghuParametrosEnum.P_CONV_PROJ_FIPE).getVlrNumerico().intValue());
					} catch (ApplicationBusinessException e) {
						LOG.error("Erro",e);
					}
				}
			}
		}
		
		return verbaFipe;
	}
	
	/**
	 * Pesquisa por parâmetros de regras orçamentárias.
	 * 
	 * @param criteria
	 * @param first
	 * @param max
	 * @return Parâmetros de regras orçamentárias.
	 */
	public FsoParametrosOrcamento pesquisarRegraOrcamentaria(
			FsoParametrosOrcamentoCriteriaVO criteria) {
		return getFsoParametrosOrcamentoDAO().pesquisarRegraOrcamentaria(criteria);
	}
	
	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}

	protected FsoVerbaGestaoDAO getFsoVerbaGestaoDAO() {
		return fsoVerbaGestaoDAO;
	}
	
	protected FsoParametrosOrcamentoScFinderON getFsoParametrosOrcamentoScFinderON() {
		return fsoParametrosOrcamentoScFinderON;
	}

	protected FsoParametrosOrcamentoSsFinderON getFsoParametrosOrcamentoSsFinderON() {
		return fsoParametrosOrcamentoSsFinderON;
	}

	protected FsoParametrosOrcamentoAcaoFilterON getFsoParametrosOrcamentoAcaoFilterON() {
		return fsoParametrosOrcamentoAcaoFilterON;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public enum ExceptionCode implements BusinessExceptionCode {
		ERRO_ACAO_PARAMETRO_ORCAMENTO_OBRIGATORIA_SC, ERRO_REGRA_ORCAMENTARIA_JA_EXISTENTE, ERRO_ACAO_PARAMETRO_ORCAMENTO_OBRIGATORIA_SS, MENSAGEM_FALHA_CLONE_ORCAMENTO;
	}
	
	public FsoNaturezaDespesa pesquisarNaturezaScGrupoMaterial(ScoMaterial material, BigDecimal paramVlrNumerico) {
		return getFsoParametrosOrcamentoDAO().pesquisarNaturezaScGrupoMaterial(material, paramVlrNumerico);
	}
}