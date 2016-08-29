package br.gov.mec.aghu.compras.solicitacaoservico.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoCaracteristicaUsuarioCentroCustoDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoModalidadeLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSsJnDAO;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoSsJn;
import br.gov.mec.aghu.orcamento.business.IOrcamentoFacade;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ScoSolicitacaoServicoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoSolicitacaoServicoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@Inject
	private ScoModalidadeLicitacaoDAO scoModalidadeLicitacaoDAO;

	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;

	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;

	@Inject
	private ScoServicoDAO scoServicoDAO;

	@Inject
	ScoCaracteristicaUsuarioCentroCustoDAO scoCaracteristicaUsuarioCentroCustoDAO;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IOrcamentoFacade orcamentoFacade;

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	@Inject
	private ScoSsJnDAO scoSsJnDAO;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1401862739567031485L;

	public enum SolicitacaoServicoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_SC_URGENTE_PRIORITARIO, SCO_00294, MENSAGEM_ERRO_SS_QTDE_SOLICITADA, MENSAGEM_ERRO_SC_VLR_UNITARIO_M20, MENSAGEM_ERRO_SS_ATIVA_LIC_M08, NATUREZA_NAO_PERMITIDA_PARA_SS, VERBA_GESTAO_NAO_PERMITIDA_PARA_SC, ERRO_CLONE_LICITACAO, URGENCIA_OBRIGATORIA, PRIORIDADE_OBRIGATORIA, EXCLUSIVIDADE_OBRIGATORIA;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	/**
	 * Retorna o ponto de parada Solicitante conforme cadastros de ponto de
	 * parada da solicitação
	 * 
	 * @return ScoPontoParadaSolicitacao
	 */
	public ScoPontoParadaSolicitacao getPpsSolicitante() {
		return this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.SL);
	}

	/**
	 * Retorna o ponto de parada Autorização conforme cadastros de ponto de
	 * parada da solicitação
	 * 
	 * @return ScoPontoParadaSolicitacao
	 */
	public ScoPontoParadaSolicitacao getPpsAutorizacao() {
		return this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.CH);
	}

	/**
	 * Retorna o ponto de parada Parecer Técnico conforme cadastros de ponto de
	 * parada da solicitação
	 * 
	 * @return ScoPontoParadaSolicitacao
	 */
	public ScoPontoParadaSolicitacao getPpsParecerTecnico() {
		return this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.PT);
	}

	public List<ScoServico> getPGrpoServEng() throws ApplicationBusinessException {

		return this.getScoServicoDAO().listarServicosEngenhariaPorGrupoEng();

	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	public void persistirSolicitacaoDeServico(ScoSolicitacaoServico solicitacaoServico, ScoSolicitacaoServico solicitacaoServicoClone)
			throws BaseException {
		this.validarJustificativas(solicitacaoServico);
		if (solicitacaoServico.getNumero() == null) {
			// INCLUIR
			inserirSolicitacaoServico(solicitacaoServico);
		} else {
			// EDITAR
			atualizarSolicitacaoServico(solicitacaoServico, solicitacaoServicoClone);
		}

	}

	private void validarJustificativas(final ScoSolicitacaoServico solicitacaoServico) throws BaseListException {
		if (solicitacaoServico != null) {
			final BaseListException codes = new BaseListException();
			if (solicitacaoServico.getIndUrgente() != null && solicitacaoServico.getIndUrgente()
					&& StringUtils.isEmpty(solicitacaoServico.getMotivoUrgencia())) {
				codes.add(new ApplicationBusinessException(SolicitacaoServicoRNExceptionCode.URGENCIA_OBRIGATORIA));
			}

			if (solicitacaoServico.getIndPrioridade() != null && solicitacaoServico.getIndPrioridade()
					&& StringUtils.isEmpty(solicitacaoServico.getMotivoPrioridade())) {
				codes.add(new ApplicationBusinessException(SolicitacaoServicoRNExceptionCode.PRIORIDADE_OBRIGATORIA));
			}

			if (solicitacaoServico.getIndExclusivo() != null && solicitacaoServico.getIndExclusivo()
					&& StringUtils.isEmpty(solicitacaoServico.getJustificativaExclusividade())) {
				codes.add(new ApplicationBusinessException(SolicitacaoServicoRNExceptionCode.EXCLUSIVIDADE_OBRIGATORIA));
			}

			if (codes.hasException()) {
				throw codes;
			}
		}
	}

	private void inserirSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) throws BaseException {
		this.inserirSolicitacaoServico(solicitacaoServico, true, false);
	}

	/***
	 * Metodo que insere uma solicitação de serviço , valida as regras da
	 * estória de manter uma SS e verifa se precisa do commit nesta camada.
	 * 
	 * @oradb SCOT_SLS_BRI
	 * @param solicitacaoServico
	 *            , servidor logado, se validar regras orçamentarias ou não, se
	 *            é para comitar ou não
	 * @throws BaseException
	 */
	public void inserirSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico, boolean flagFlush, boolean duplicacao)
			throws BaseException {

		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();

		this.validarJustificativas(solicitacaoServico);
		if (!duplicacao) {
			setaPontoParada(solicitacaoServico, servidorLogado);
		}
		validaDataSolicitacao(solicitacaoServico);
		validaQtdeSolicitada(solicitacaoServico);
		validaValorUnitarioPrevisto(solicitacaoServico);
		validarRegrasOrcamentarias(solicitacaoServico, null);
		solicitacaoServico.setServidor(servidorLogado);

		this.getScoSolicitacaoServicoDAO().persistir(solicitacaoServico);

		if (flagFlush) {
			this.getScoSolicitacaoServicoDAO().flush();
		}

		// inserir na journal
		this.inserirSSJournal(solicitacaoServico, DominioOperacoesJournal.INS, servidorLogado, solicitacaoServico.getIndDevolucao());

	}

	public void validarRegrasOrcamentarias(ScoSolicitacaoServico servico, ScoSolicitacaoServico clone) throws BaseException {
		validarNaturezaParam(servico, clone);
		validarVerbaGestaoParam(servico, clone);
	}

	/**
	 * Valida se natureza da SS está parametrizado.
	 * 
	 * @param novaSs
	 *            SC.
	 * @throws BaseException
	 */
	private void validarNaturezaParam(ScoSolicitacaoServico novaSs, ScoSolicitacaoServico ssAntiga) throws BaseException {
		if (ssAntiga != null && !CoreUtil.modificados(novaSs.getServico(), ssAntiga.getServico())
				&& !CoreUtil.modificados(novaSs.getNaturezaDespesa(), ssAntiga.getNaturezaDespesa())) {
			return;
		} else if (!getCadastrosBasicosOrcamentoFacade().isNaturezaValidSsParam(novaSs.getServico(), novaSs.getNaturezaDespesa())) {
			SolicitacaoServicoRNExceptionCode.NATUREZA_NAO_PERMITIDA_PARA_SS.throwException();
		}
	}

	/**
	 * Valida se verba de gestão da SC está parametrizado.
	 * 
	 * @param novaSs
	 *            SC.
	 * @throws BaseException
	 */
	private void validarVerbaGestaoParam(ScoSolicitacaoServico novaSs, ScoSolicitacaoServico ssAntiga) throws BaseException {
		if (ssAntiga != null && !CoreUtil.modificados(novaSs.getServico(), ssAntiga.getServico())
				&& !CoreUtil.modificados(novaSs.getVerbaGestao(), ssAntiga.getVerbaGestao())) {
			return;
		} else if (!getCadastrosBasicosOrcamentoFacade().isVerbaGestaoValidSsParam(novaSs.getServico(), novaSs.getVerbaGestao())) {
			SolicitacaoServicoRNExceptionCode.VERBA_GESTAO_NAO_PERMITIDA_PARA_SC.throwException();
		}
	}

	/***
	 * Metodo que valida Valor unitario que quando preenchido deve ser maior que
	 * zero
	 * 
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 * @throws ApplicationBusinessException
	 */
	public void validaValorUnitarioPrevisto(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {

		if (solicitacaoServico.getValorUnitPrevisto() != null) {
			// Valor unitário previsto deve ser maior que zero. 
			if (!(solicitacaoServico.getValorUnitPrevisto().compareTo(BigDecimal.ZERO) > 0)) {
				throw new ApplicationBusinessException(SolicitacaoServicoRNExceptionCode.MENSAGEM_ERRO_SC_VLR_UNITARIO_M20);
			}
		}

	}

	/**
	 * Atualiza a SS passando pelas validacoes necessarias da estória de manter
	 * SS.
	 * 
	 * @oradb SCOT_SLS_BRU
	 * @param solicitacaoServico
	 * @param solicitacaoServicoClone
	 * @throws BaseException
	 */
	public void atualizarSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico, ScoSolicitacaoServico solicitacaoServicoClone)
			throws BaseException {
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		this.validarJustificativas(solicitacaoServico);
		try {
			this.validaDataSolicitacao(solicitacaoServico);
			this.validaQtdeSolicitada(solicitacaoServico);
			this.validaValorUnitarioPrevisto(solicitacaoServico);
			validarRegrasOrcamentarias(solicitacaoServico, solicitacaoServicoClone);
		} catch (BaseException e) {
			getScoSolicitacaoServicoDAO().desatachar(solicitacaoServico);
			throw new BaseException(e);
		}

		if (solicitacaoServico.getIndDevolucao() == true
				|| !Objects.equals(solicitacaoServico.getPontoParada(), solicitacaoServicoClone.getPontoParada())) {
			this.inserirSSJournal(solicitacaoServico, DominioOperacoesJournal.UPD, servidorLogado, solicitacaoServico.getIndDevolucao());
		}
		//		// #29721  - Se devolvido setar  PPS_CODIGO = PPS_CODIGO_LOC_ATUAL, PPS_CODIGO_LOC_ATUAL = valor original PPS_CODIGO
		//		// #29965 : a inversão de PP não deve ser realizada neste método. Ela é realizada na ON pelo método devolverSolicitacoesServico
		//		// invocado somente quando esta sendo realizada uma devolução e não a cada atualização		
		//		if (solicitacaoServico.getIndDevolucao()){
		//			ScoPontoParadaSolicitacao pontoParada = solicitacaoServico.getPontoParada();			
		//			solicitacaoServico.setPontoParada(solicitacaoServico.getPontoParadaLocAtual());
		//			solicitacaoServico.setPontoParadaLocAtual(pontoParada);
		//		}

		if (solicitacaoServico.getIndExclusao() != null && solicitacaoServico.getIndExclusao()) {
			this.validaSsLiberadaExclusao(solicitacaoServico);
			solicitacaoServico.setServidorExcluidor(servidorLogado);
			solicitacaoServico.setDtExclusao(new Date());
		}

		if (solicitacaoServico.getVerbaGestao() != null && solicitacaoServico.getConvenioFinanceiro() == null) {

			FsoConveniosFinanceiro convFin = this.getOrcamentoFacade().obterFsoConveniosFinanceiroPorChavePrimaria(
					solicitacaoServico.getVerbaGestao().getSeq());

			if (convFin != null) {
				solicitacaoServico.setConvenioFinanceiro(convFin);
			}
		}

		solicitacaoServico.setServidorAlterador(servidorLogado);
		solicitacaoServico.setDtAlteracao(new Date());

		this.getScoSolicitacaoServicoDAO().atualizar(solicitacaoServico);
	}

	/**
	 * Insere alteracoes de ponto de parada na journal que nao é journal da SS
	 * 
	 * @param solicitacaoServico
	 * @param servidorLogado
	 */
	public void inserirSSJournal(ScoSolicitacaoServico solicitacaoServico, DominioOperacoesJournal operacao, RapServidores servidorLogado,
			Boolean isDevolucao) {
		// inserir na journal
		ScoSsJn scoSsJn = new ScoSsJn();
		scoSsJn.setOperacao(operacao);
		scoSsJn.setNomeUsuario(servidorLogado.getUsuario());
		scoSsJn.setNumero(solicitacaoServico.getNumero());
		scoSsJn.setPpsCodigo(solicitacaoServico.getPontoParada().getCodigo());
		if (isDevolucao) {
			scoSsJn.setIndDevolucao(isDevolucao);
		} else {
			scoSsJn.setIndDevolucao(solicitacaoServico.getIndDevolucao());
		}
		scoSsJn.setSerMatriculaAutorizada(servidorLogado.getId().getMatricula());
		scoSsJn.setSerVinCodigoAutorizada(servidorLogado.getId().getVinCodigo());
		this.getScoSsJnDAO().persistir(scoSsJn);
		this.getScoSsJnDAO().flush();
	}

	public void inserirSSJournal(ScoSsJn scoSsJn) {
		this.getScoSsJnDAO().persistir(scoSsJn);
		this.getScoSsJnDAO().flush();
	}

	/*********************************
	 * RN 25 - Verifica se SS está liberada para exclusão (Não pode estar em
	 * Lict ou AF não excluídas).
	 * 
	 * @oradb SCOP_ENFORCE_SLS_RULES
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void validaSsLiberadaExclusao(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {

		ScoFaseSolicitacao scoFaseSolicitacao = this.getScoFasesSolicitacaoDAO().obterSsAtivoFasesLicitacao(solicitacaoServico);

		if (scoFaseSolicitacao != null) {
			Integer lctNumero = scoFaseSolicitacao.getItemLicitacao().getId().getLctNumero();
			Short numero = scoFaseSolicitacao.getItemLicitacao().getId().getNumero();

			throw new ApplicationBusinessException(SolicitacaoServicoRNExceptionCode.MENSAGEM_ERRO_SS_ATIVA_LIC_M08, lctNumero, numero);
		}

	}

	/****
	 * Metodo que valida a data de solicitacao da Ss que não pode ser maior que
	 * hoje Adicionado na inclusão e alteração *
	 * 
	 * @param solicitacaoCompra
	 */
	public void validaDataSolicitacao(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {

		if (solicitacaoServico.getDtSolicitacao() != null && DateUtil.validaDataMaior(solicitacaoServico.getDtSolicitacao(), new Date())) {
			throw new ApplicationBusinessException(SolicitacaoServicoRNExceptionCode.SCO_00294);
		}
	}

	/*************
	 * Valida Quantidade Solicitada e Aprovada que deve ser maior que zero
	 * chamado na inserção
	 * 
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void validaQtdeSolicitada(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {

		if (solicitacaoServico.getQtdeSolicitada() <= 0) {
			solicitacaoServico.setQtdeSolicitada(null);
			throw new ApplicationBusinessException(SolicitacaoServicoRNExceptionCode.MENSAGEM_ERRO_SS_QTDE_SOLICITADA);
		}

	}

	/***
	 * Metodo que seta o ponto de parada para solicitante na inclusao da
	 * solicitação
	 * 
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 */
	public void setaPontoParada(ScoSolicitacaoServico solicitacaoServico, RapServidores servidorLogado) throws ApplicationBusinessException {

		// Validação Perfis, se comprador , setar os parametros de comprador.
		if (getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "cadastrarSSComprador")) {

			AghParametros ppsComprador = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_COMPRADOR);
			solicitacaoServico.setServidorComprador(servidorLogado);

			solicitacaoServico.setPontoParadaLocAtual(this.getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
					ppsComprador.getVlrNumerico().shortValue()));
			solicitacaoServico.setPontoParada(this.getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
					ppsComprador.getVlrNumerico().shortValue()));

			AghParametros matriculaChefePlanj = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATR_CHEFE_PLANEJ);
			AghParametros vincChefePlanj = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VIN_CHEFE_PLANEJ);

			solicitacaoServico.setServidorAutorizador(this.getRegistroColaboradorFacade().buscarServidor(
					vincChefePlanj.getVlrNumerico().shortValue(), matriculaChefePlanj.getVlrNumerico().intValue()));

			solicitacaoServico.setDtAutorizacao(new Date());

		} else if (getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "cadastrarSSPlanejamento")) {
			// perfil planejamento
			AghParametros ppsPlanejamento = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_PLANEJAMENTO);

			solicitacaoServico.setPontoParadaLocAtual(this.getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
					ppsPlanejamento.getVlrNumerico().shortValue()));
			solicitacaoServico.setPontoParada(this.getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
					ppsPlanejamento.getVlrNumerico().shortValue()));

			AghParametros matriculaChefePlanj = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATR_CHEFE_PLANEJ);
			AghParametros vincChefePlanj = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VIN_CHEFE_PLANEJ);

			solicitacaoServico.setServidorAutorizador(this.getRegistroColaboradorFacade().buscarServidor(
					vincChefePlanj.getVlrNumerico().shortValue(), matriculaChefePlanj.getVlrNumerico().intValue()));

			solicitacaoServico.setDtAutorizacao(new Date());
		} else if (getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "cadastrarSSChefias")) {

			AghParametros ppsComprador = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_AUTORIZACAO);
			ScoPontoParadaSolicitacao ppSolicitacao = this.getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
					ppsComprador.getVlrNumerico().shortValue());

			solicitacaoServico.setPontoParadaLocAtual(ppSolicitacao);
			solicitacaoServico.setPontoParada(ppSolicitacao);
		}

		else {
			ScoPontoParadaSolicitacao ppsSolicitante = this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(
					DominioTipoPontoParada.SL);

			solicitacaoServico.setPontoParada(ppsSolicitante);
			solicitacaoServico.setPontoParadaLocAtual(ppsSolicitante);
		}

	}

	public void clonarSolicitacaoServicoServidor(ScoSolicitacaoServico solicitacaoServico,
			ScoSolicitacaoServico solicitacaoServicoCloneLocal) throws ApplicationBusinessException {

		if (solicitacaoServico.getServidor() != null) {
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(
					solicitacaoServico.getServidor().getId().getMatricula(), solicitacaoServico.getServidor().getId().getVinCodigo());
			solicitacaoServicoCloneLocal.setServidor(servidor);
		}

		if (solicitacaoServico.getServidorAlterador() != null) {
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(
					solicitacaoServico.getServidorAlterador().getId().getMatricula(),
					solicitacaoServico.getServidorAlterador().getId().getVinCodigo());
			solicitacaoServicoCloneLocal.setServidorAlterador(servidor);
		}

		if (solicitacaoServico.getServidorAutorizador() != null) {
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(
					solicitacaoServico.getServidorAutorizador().getId().getMatricula(),
					solicitacaoServico.getServidorAutorizador().getId().getVinCodigo());
			solicitacaoServicoCloneLocal.setServidorAutorizador(servidor);
		}

		if (solicitacaoServico.getServidorComprador() != null) {
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(
					solicitacaoServico.getServidorComprador().getId().getMatricula(),
					solicitacaoServico.getServidorComprador().getId().getVinCodigo());
			solicitacaoServicoCloneLocal.setServidorComprador(servidor);
		}
		if (solicitacaoServico.getServidorExcluidor() != null) {
			RapServidores servidor = getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(
					solicitacaoServico.getServidorExcluidor().getId().getMatricula(),
					solicitacaoServico.getServidorExcluidor().getId().getVinCodigo());
			solicitacaoServicoCloneLocal.setServidorExcluidor(servidor);
		}
	}

	public void clonarSolicitacaoServicoCentroCusto(ScoSolicitacaoServico solicitacaoServico,
			ScoSolicitacaoServico solicitacaoServicoCloneLocal) throws ApplicationBusinessException {
		if (solicitacaoServico.getCentroCusto() != null) {
			FccCentroCustos centroCusto = this.getCentroCustoFacade().obterFccCentroCustos(solicitacaoServico.getCentroCusto().getCodigo());
			solicitacaoServicoCloneLocal.setCentroCusto(centroCusto);
		}

		if (solicitacaoServico.getCentroCustoAplicada() != null) {
			FccCentroCustos centroCusto = this.getCentroCustoFacade().obterFccCentroCustos(
					solicitacaoServico.getCentroCustoAplicada().getCodigo());
			solicitacaoServicoCloneLocal.setCentroCustoAplicada(centroCusto);
		}

		if (solicitacaoServico.getCentroCustoAutzTecnica() != null) {
			FccCentroCustos centroCusto = this.getCentroCustoFacade().obterFccCentroCustos(
					solicitacaoServico.getCentroCustoAutzTecnica().getCodigo());
			solicitacaoServicoCloneLocal.setCentroCustoAutzTecnica(centroCusto);
		}
	}

	public ScoSolicitacaoServico clonarSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {
		ScoSolicitacaoServico solicitacaoServicoCloneLocal = null;

		try {
			solicitacaoServicoCloneLocal = (ScoSolicitacaoServico) BeanUtils.cloneBean(solicitacaoServico);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			throw new ApplicationBusinessException(SolicitacaoServicoRNExceptionCode.ERRO_CLONE_LICITACAO);
		}

		this.clonarSolicitacaoServicoCentroCusto(solicitacaoServico, solicitacaoServicoCloneLocal);
		this.clonarSolicitacaoServicoServidor(solicitacaoServico, solicitacaoServicoCloneLocal);

		if (solicitacaoServico.getConvenioFinanceiro() != null) {
			FsoConveniosFinanceiro conveniosFinanceiro = this.getOrcamentoFacade().obterConvenioPorChave(
					solicitacaoServico.getConvenioFinanceiro().getCodigo());
			solicitacaoServicoCloneLocal.setConvenioFinanceiro(conveniosFinanceiro);
		}

		if (solicitacaoServico.getNaturezaDespesa() != null) {
			FsoNaturezaDespesa naturezaDespesa = getCadastrosBasicosOrcamentoFacade().obterNaturezaDespesa(
					new FsoNaturezaDespesaId(solicitacaoServico.getNaturezaDespesa().getId().getGndCodigo(), solicitacaoServico
							.getNaturezaDespesa().getId().getCodigo()));
			solicitacaoServicoCloneLocal.setNaturezaDespesa(naturezaDespesa);
		}

		if (solicitacaoServico.getPontoParada() != null) {
			ScoPontoParadaSolicitacao pontoParadaSolicitacao = comprasCadastrosBasicosFacade.obterPontoParada(solicitacaoServico
					.getPontoParada().getCodigo());
			solicitacaoServicoCloneLocal.setPontoParada(pontoParadaSolicitacao);
		}

		if (solicitacaoServico.getPontoParadaLocAtual() != null) {
			ScoPontoParadaSolicitacao pontoParadaSolicitacao = comprasCadastrosBasicosFacade.obterPontoParada(solicitacaoServico
					.getPontoParadaLocAtual().getCodigo());
			solicitacaoServicoCloneLocal.setPontoParadaLocAtual(pontoParadaSolicitacao);
		}

		if (solicitacaoServico.getModalidadeLicitacao() != null) {
			ScoModalidadeLicitacao modalidade = this.getScoModalidadeLicitacaoDAO().obterPorChavePrimaria(
					solicitacaoServico.getModalidadeLicitacao().getCodigo());
			solicitacaoServicoCloneLocal.setModalidadeLicitacao(modalidade);
		}

		if (solicitacaoServico.getServico() != null) {
			ScoServico servico = getScoServicoDAO().obterServicoPorId(solicitacaoServico.getServico().getCodigo());
			solicitacaoServicoCloneLocal.setServico(servico);
		}
		return solicitacaoServicoCloneLocal;
	}

	/*********************************
	 * 
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void validaUrgentePrioritario(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {

		if (!solicitacaoServico.getIndUrgente()) {
			solicitacaoServico.setMotivoUrgencia(null);

		}
		if (!solicitacaoServico.getIndPrioridade()) {
			solicitacaoServico.setMotivoPrioridade(null);
		}

		if (solicitacaoServico.getIndUrgente() && solicitacaoServico.getIndPrioridade()) {
			throw new ApplicationBusinessException(SolicitacaoServicoRNExceptionCode.MENSAGEM_ERRO_SC_URGENTE_PRIORITARIO);

		}
	}

	/*********************************
	 * RN22
	 * 
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public boolean isReadonlyEdicao(ScoSolicitacaoServico solicitacaoServico) {
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();

		if (solicitacaoServico.getPontoParada() == null
				|| getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "cadastrarSSComprador")
				|| getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "cadastrarSSPlanejamento")) {
			return false;
		}

		FccCentroCustos ccFipe = this.getScoCaracteristicaUsuarioCentroCustoDAO().obterCcAplicacaoGeracaoGppg(servidorLogado);
		Boolean possuiCaractGppg = false;
		if (ccFipe != null) {
			Set<Integer> listaHierarquica = getCentroCustoFacade().pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
			if (listaHierarquica.contains(solicitacaoServico.getCentroCustoAplicada().getCodigo())) {
				possuiCaractGppg = true;
			}
		}

		if (possuiCaractGppg
				&& (solicitacaoServico.getNumero() != null && !this.getScoFasesSolicitacaoDAO()
						.getSsEmFases(solicitacaoServico.getNumero()))) {
			return false;
		}

		return !(Objects.equals(solicitacaoServico.getPontoParada().getTipoPontoParada(), DominioTipoPontoParada.SL) || Objects
				.equals(solicitacaoServico.getPontoParada().getTipoPontoParada(), DominioTipoPontoParada.CH));
	}

	public ScoSolicitacaoServico duplicarSS(ScoSolicitacaoServico solicitacaoServico, boolean flagFlush, boolean isDuplicarPac, boolean manterCcOriginal)
			throws BaseException {

		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogadoSemCache();

		if (!isDuplicarPac) {
			solicitacaoServico.setServidorAutorizador(null);
			solicitacaoServico.setDtAutorizacao(null);
			setaPontoParada(solicitacaoServico, servidorLogado);
		}
		solicitacaoServico.setNumero(null);
		solicitacaoServico.setServidor(servidorLogado);
		if (!manterCcOriginal) {
			solicitacaoServico.setCentroCusto(servidorLogado.getCentroCustoLotacao());
			solicitacaoServico.setCentroCustoAplicada(servidorLogado.getCentroCustoLotacao());
		}
		solicitacaoServico.setServidorComprador(null);
		solicitacaoServico.setDtSolicitacao(new Date());
		solicitacaoServico.setDtDigitacao(new Date());
		solicitacaoServico.setIndExclusao(false);
		solicitacaoServico.setIndUrgente(false);
		solicitacaoServico.setIndDevolucao(false);
		solicitacaoServico.setMotivoExclusao(null);
		solicitacaoServico.setDtExclusao(null);
		solicitacaoServico.setDtAnalise(null);
		solicitacaoServico.setMotivoUrgencia(null);
		solicitacaoServico.setJustificativaDevolucao(null);
		solicitacaoServico.setServidorExcluidor(null);
		solicitacaoServico.setServidorAlterador(null);
		solicitacaoServico.setDtAlteracao(null);
		solicitacaoServico.setIndEfetivada(false);
		solicitacaoServico.setFasesSolicitacao(null);

		this.inserirSolicitacaoServico(solicitacaoServico, flagFlush, true);

		return solicitacaoServico;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	protected IOrcamentoFacade getOrcamentoFacade() {
		return this.orcamentoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return scoSolicitacaoServicoDAO;
	}

	protected ScoSsJnDAO getScoSsJnDAO() {
		return scoSsJnDAO;
	}

	protected ScoServicoDAO getScoServicoDAO() {
		return scoServicoDAO;
	}

	protected ScoFaseSolicitacaoDAO getScoFasesSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}

	protected ScoModalidadeLicitacaoDAO getScoModalidadeLicitacaoDAO() {
		return scoModalidadeLicitacaoDAO;
	}

	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return scoPontoParadaSolicitacaoDAO;
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return cadastrosBasicosOrcamentoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected ScoCaracteristicaUsuarioCentroCustoDAO getScoCaracteristicaUsuarioCentroCustoDAO() {
		return scoCaracteristicaUsuarioCentroCustoDAO;
	}

}