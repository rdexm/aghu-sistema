package br.gov.mec.aghu.compras.solicitacaoservico.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoArquivoAnexoDAO;
import br.gov.mec.aghu.compras.dao.ScoCaracteristicaUsuarioCentroCustoDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSsJnDAO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ScoSolicitacaoServicoRN.SolicitacaoServicoRNExceptionCode;
import br.gov.mec.aghu.compras.vo.LoteSolicitacaoServicoVO;
import br.gov.mec.aghu.compras.vo.SolicitacaoServicoVO;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoSolicitacaoServicoON extends BaseBusiness {

	private static final String GRAVAR = "gravar";

	@EJB
	private ScoSolicitacaoServicoRN scoSolicitacaoServicoRN;

	private static final Log LOG = LogFactory.getLog(ScoSolicitacaoServicoON.class);

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
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;

	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private ScoServicoDAO scoServicoDAO;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private ScoCaracteristicaUsuarioCentroCustoDAO scoCaracteristicaUsuarioCentroCustoDAO;
	
	@Inject
	private ScoSsJnDAO scoSsJnDAO;

	@Inject
	private ScoArquivoAnexoDAO scoArquivoAnexoDAO;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5922834917785524834L;

	public enum ScoSolicitacaoServicoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ENCAMINHAR_SCO_RN_01, MENSAGEM_ENCAMINHAR_SCO_RN_02, MENSAGEM_PARAMETRO_PPS_PADRAO_NAO_ENCONTRADO, MENSAGEM_PARAMETRO_PPS_PADRAO_INEXISTENTE, SOLICITACAO_SERVICO_INEXISTENTE;
	}

	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoAutorizarSs(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, LoteSolicitacaoServicoVO filtroPesquisa) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		return this.getScoSolicitacaoServicoDAO().pesquisarSolicitacaoServicoAutorizarSs(firstResult, maxResults, orderProperty, asc,
				filtroPesquisa, servidorLogado);
	}

	public Long countSolicitacaoServicoAutorizarSs(LoteSolicitacaoServicoVO filtroPesquisa) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		return this.getScoSolicitacaoServicoDAO().countSolicitacaoServicoAutorizarSs(filtroPesquisa, servidorLogado);
	}

	public Long countSolicitacaoServicoEncaminharSs(LoteSolicitacaoServicoVO filtroPesquisa, RapServidores servidorLogado) {
		return this.getScoSolicitacaoServicoDAO().countSolicitacaoServicoEncaminharSs(filtroPesquisa, servidorLogado);
	}

	public Long countSolicitacaoServicoEncaminharSs(LoteSolicitacaoServicoVO filtroPesquisa) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		return this.getScoSolicitacaoServicoDAO().countSolicitacaoServicoEncaminharSs(filtroPesquisa, servidorLogado);
	}

	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoEncaminharSs(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, LoteSolicitacaoServicoVO filtroPesquisa) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		return this.getScoSolicitacaoServicoDAO().pesquisarSolicitacaoServicoEncaminharSs(firstResult, maxResults, orderProperty, asc,
				filtroPesquisa, servidorLogado);
	}

	/**
	 * Inativa uma lista de solicitações de serviço
	 * 
	 * @param listaSolicitacoes
	 * @param motivoExclusao
	 * @throws BaseException
	 */
	public void inativarListaSolicitacaoServico(List<ScoSolicitacaoServico> listaSolicitacoes, String motivoExclusao) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		for (ScoSolicitacaoServico item : listaSolicitacoes) {
			ScoSolicitacaoServico solicitacao = getScoSolicitacaoServicoDAO().obterPorChavePrimaria(item.getNumero());
			//ScoSolicitacaoServico solicitacao = item;
			ScoSolicitacaoServico solicitacaoOld = getScoSolicitacaoServicoDAO().obterOriginal(item.getNumero());
			solicitacao.setIndExclusao(true);
			solicitacao.setMotivoExclusao(motivoExclusao);
			solicitacao.setDtExclusao(new Date());
			solicitacao.setServidorExcluidor(servidorLogado);

			this.getScoSolicitacaoServicoRN().atualizarSolicitacaoServico(solicitacao, solicitacaoOld);
		}
	}

	/**
	 * Encaminha uma lista de solicitacoes de servico para o proximo ponto de
	 * parada
	 * 
	 * @param listaSolicitacoes
	 * @param pontoParadaAtual
	 * @param proximoPontoParada
	 * @param funcionarioComprador
	 * @param autorizacaoChefia
	 * @throws BaseException
	 */
	public void encaminharListaSolicitacaoServico(List<ScoSolicitacaoServico> listaSolicitacaoServico, ScoPontoParadaSolicitacao pontoParadaAtual,
			ScoPontoParadaSolicitacao proximoPontoParada, RapServidores funcionarioComprador, Boolean autorizacaoChefia)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// não permite encaminhar para um ponto de parada "comprador" sem o
		// comprador definido
		if (this.getComprasCadastrosBasicosFacade().verificarPontoParadaComprador(proximoPontoParada) && funcionarioComprador == null) {
			throw new ApplicationBusinessException(ScoSolicitacaoServicoONExceptionCode.MENSAGEM_ENCAMINHAR_SCO_RN_01);
		}

		RapServidores comprador = null;

		for (ScoSolicitacaoServico item : listaSolicitacaoServico) {

			if (funcionarioComprador != null) {
				comprador = this.getRegistroColaboradorFacade().obterServidor(funcionarioComprador);
			}

			//ScoSolicitacaoServico solicitacao = getScoSolicitacaoServicoDAO().obterPorChavePrimaria(item);
			ScoSolicitacaoServico solicitacao = item;
			ScoSolicitacaoServico solicitacaoOld = getScoSolicitacaoServicoDAO().obterOriginal(item);

			if (!autorizacaoChefia) {
				if ((Objects.equals(proximoPontoParada.getTipoPontoParada(), DominioTipoPontoParada.SL) || Objects.equals(
						proximoPontoParada.getTipoPontoParada(), DominioTipoPontoParada.CH))
						&& (solicitacao.getDtAutorizacao() != null || solicitacao.getServidorAutorizador() != null)) {
					solicitacao.setDtAutorizacao(null);
					solicitacao.setServidorAutorizador(null);
				}
				if ((Objects.equals(proximoPontoParada.getTipoPontoParada(), DominioTipoPontoParada.CP) || Objects.equals(
						proximoPontoParada.getTipoPontoParada(), DominioTipoPontoParada.LI))
						&& (solicitacao.getDtAutorizacao() == null || solicitacao.getServidorAutorizador() == null)) {
					throw new ApplicationBusinessException(ScoSolicitacaoServicoONExceptionCode.MENSAGEM_ENCAMINHAR_SCO_RN_02);
				}
			} else {
				solicitacao.setDtAutorizacao(new Date());
				solicitacao.setServidorAutorizador(servidorLogado);
			}

			solicitacao.setPontoParadaLocAtual(solicitacao.getPontoParada());
			solicitacao.setPontoParada(proximoPontoParada);

			if (funcionarioComprador != null) {
				solicitacao.setServidorComprador(comprador);
			}

			solicitacao.setIndDevolucao(false);
			solicitacao.setJustificativaDevolucao(null);

			this.getScoSolicitacaoServicoRN().atualizarSolicitacaoServico(solicitacao, solicitacaoOld);
		}
	}

	public List<Integer> obterListaNumeroSs(List<ScoSolicitacaoServico> listaSs) {
		List<Integer> listaResultado = new ArrayList<Integer>();
		
		if (listaSs != null) {
			for(ScoSolicitacaoServico ss : listaSs) {
				listaResultado.add(ss.getNumero());
			}
		}
		
		return listaResultado;
	}
	
	/**
	 * Autoriza uma lista de solicitacoes de servico
	 * 
	 * @param listaSolicitacoes
	 * @throws BaseException
	 */
	public void autorizarListaSolicitacaoServico(List<ScoSolicitacaoServico> listaSolicitacoes) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AghParametros parametroPpsPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_PADRAO_AUTORIZA_SS);

		if (parametroPpsPadrao == null) {
			throw new ApplicationBusinessException(ScoSolicitacaoServicoONExceptionCode.MENSAGEM_PARAMETRO_PPS_PADRAO_INEXISTENTE);
		}

		for (ScoSolicitacaoServico solicitacao : listaSolicitacoes) {

			//ScoSolicitacaoServico solicitacao = getScoSolicitacaoServicoDAO().obterPorChavePrimaria(item);
			ScoSolicitacaoServico solicitacaoOld = getScoSolicitacaoServicoDAO().obterOriginal(solicitacao);

			ScoPontoParadaSolicitacao ppsPadrao = this.getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
					parametroPpsPadrao.getVlrNumerico().shortValue());

			if (ppsPadrao == null) {
				throw new ApplicationBusinessException(ScoSolicitacaoServicoONExceptionCode.MENSAGEM_PARAMETRO_PPS_PADRAO_NAO_ENCONTRADO);
			}

			solicitacao.setServidorAutorizador(servidorLogado);
			solicitacao.setPontoParadaLocAtual(solicitacao.getPontoParada());
			solicitacao.setPontoParada(ppsPadrao);
			solicitacao.setDtAutorizacao(new Date());

			this.getScoSolicitacaoServicoRN().atualizarSolicitacaoServico(solicitacao, solicitacaoOld);
		}
	}

	public List<ScoServico> listarServicosAtivos(Object param) {
		return getScoServicoDAO().listarServicosAtivos(param);
	}

	public Long listarServicosCount(Object param) {
		return getScoServicoDAO().listarServicosAtivosCount(param);

	}

	public List<ScoServico> listarServicos(Object param) {
		return getScoServicoDAO().listarServicos(param);
	}

	public ScoSolicitacaoServico clonarSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {
		return this.getScoSolicitacaoServicoRN().clonarSolicitacaoServico(solicitacaoServico);
	}

	/*********************************
	 * 
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void validaUrgentePrioritario(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {

		getScoSolicitacaoServicoRN().validaUrgentePrioritario(solicitacaoServico);

	}

	public List<SolicitacaoServicoVO> buscarSolicitacaoServico(List<Integer> listaCodSS) throws ApplicationBusinessException {
		//IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();

		List<SolicitacaoServicoVO> lista = getScoSolicitacaoServicoDAO().obterRelatorioSolicitacaoServico(listaCodSS);

		for (SolicitacaoServicoVO vo : lista) {
			vo.setPossuiAnexo(getScoArquivoAnexoDAO().verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento.SS, vo.getSolicitacaoServico().getNumero()));
		}
		
		if (lista.size() == 0) {
			throw new ApplicationBusinessException(ScoSolicitacaoServicoONExceptionCode.SOLICITACAO_SERVICO_INEXISTENTE);
		}

		return lista;
	}

	/**
	 * Verifica se o usuario possui permissão ao componente/método associado
	 * 
	 * @param login
	 * @param componente
	 * @param metodo
	 * @return Boolean
	 */
	private Boolean verificarPermissoes(String login, String componente, String metodo) {
		return this.getICascaFacade().usuarioTemPermissao(login, componente, metodo);
	}

	/**
	 * Verifica se o usuario logado possui alguma permissão associada à
	 * solicitação de serviço
	 * 
	 * @param login
	 * @param gravar
	 * @return Boolean
	 */
	public Boolean verificarPermissoesSolicitacaoServico(String login, Boolean gravar) {
		Boolean ret = Boolean.FALSE;

		if (gravar) {
			ret = (this.verificarPermissoes(login, "cadastrarSolicitacaoServico", GRAVAR)
					|| this.verificarPermissoes(login, "cadastrarSSPlanejamento", GRAVAR)
					|| this.verificarPermissoes(login, "cadastrarSSComprador", GRAVAR)
					|| this.verificarPermissoes(login, "cadastrarSSEngenharia", GRAVAR) || this.verificarPermissoes(login,
					"cadastrarSSChefias", GRAVAR));
		} else {
			ret = this.verificarPermissoes(login, "cadastrarSolicitacaoServico", "visualizar");
		}
		return ret;
	}

	public boolean verificaPemissaoUsuario(String permissao, String login) throws ApplicationBusinessException {
		return getICascaFacade().usuarioTemPermissao(login, permissao);
	}

	public Boolean habilitarEncaminharSS(ScoSolicitacaoServico solicitacaoServico, Boolean temPermissaoComprador,
			Boolean temPermissaoPlanejamento, Boolean temPermissaoEncaminhar, List<FccCentroCustos> listaCentroCustosUsuario) {
		if ((solicitacaoServico.getIndExclusao() != null && solicitacaoServico.getIndExclusao()) || !temPermissaoEncaminhar) {
			return false;
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		FccCentroCustos ccFipe = this.getScoCaracteristicaUsuarioCentroCustoDAO().obterCcAplicacaoGeracaoGppg(servidorLogado);
		Boolean possuiCaractGppg = false;
		if (ccFipe != null) {
			Set<Integer> listaHierarquica = getCentroCustoFacade().pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
			if (listaHierarquica.contains(solicitacaoServico.getCentroCustoAplicada().getCodigo())) {
				possuiCaractGppg = true;
			}
		}
		
		// Valida se o servidor pode mover a solicitação no ponto de parada
		// atual.
		if (!possuiCaractGppg && !this.validarAcessoPontoParada(solicitacaoServico.getPontoParada())) {
			return false;
		}

		// Para as permissões cadastrarSSPlanejamento e cadastrarSSCompradoR
		// podem encaminhar solicitação para qualquer centro de custos. Se não
		// tiver essas permissões, deve validar o centro de custos
		if (!temPermissaoPlanejamento && !temPermissaoComprador && !possuiCaractGppg) {
			if (listaCentroCustosUsuario == null || !listaCentroCustosUsuario.contains(solicitacaoServico.getCentroCusto())) {
				return false;
			}
		}

		if (this.getComprasFacade().getSsEmFases(solicitacaoServico.getNumero())) {
			return false;
		}

		return true;
	}

	public boolean validarAcessoPontoParada(final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (this.getSolicitacaoComprasFacade().pesquisarScoPontoServidorCount(scoPontoParadaSolicitacao, servidorLogado, null) > 0) {
			return true;
		}
		return false;
	}

	public Boolean habilitarAutorizarSS(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {

		if ((solicitacaoServico.getIndExclusao() != null && solicitacaoServico.getIndExclusao())
				|| (solicitacaoServico.getIndDevolucao() != null && solicitacaoServico.getIndDevolucao())
				|| solicitacaoServico.getDtAutorizacao() != null) {
			return false;
		}
		AghParametros ppsAutorizacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_AUTORIZACAO);
		ScoPontoParadaSolicitacao pontoParadaPpsAutorizacao = this.getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
				ppsAutorizacao.getVlrNumerico().shortValue());

		if (!pontoParadaPpsAutorizacao.equals(solicitacaoServico.getPontoParada())) {
			return false;
		}

		List<FccCentroCustos> listaCentroCustos = this.getCentroCustoFacade().pesquisarCentroCustoUsuarioAutorizaSs();

		if (!listaCentroCustos.contains(solicitacaoServico.getCentroCusto())) {
			return false;
		}

		List<ScoFaseSolicitacao> listFases = this.getComprasFacade().obterFaseSolicitacao(solicitacaoServico.getNumero(), true,
				DominioTipoSolicitacao.SS);
		if (listFases != null && listFases.size() > 0) {
			return false;
		}

		return true;
	}

	public Boolean verificarPontoParadaChefia(List<ScoSolicitacaoServico> solicitacoes) {
		Boolean todasSolicitacoesPPChefia= true;
		for (ScoSolicitacaoServico ss : solicitacoes) {
			if (!DominioTipoPontoParada.CH.equals(ss.getPontoParada().getTipoPontoParada())) {
				todasSolicitacoesPPChefia = false;
			}
		}
		return todasSolicitacoesPPChefia;
	}

	/**
	 * Devolve solicitações de Serviço. Chamado pela tela
	 * encaminhaSolicitacaoServico
	 * 
	 * @param nroScos
	 *            ID's das SS's a serem devolvidas.
	 * @param justificativa
	 *            Justificativa.
	 * @param servidor
	 *            Servidor.
	 */
	public void devolverSolicitacoesServico(List<ScoSolicitacaoServico> listaSolicitacaoServico, String justificativa) throws BaseException {
		for (ScoSolicitacaoServico solicitacaoServico : listaSolicitacaoServico) {
			//ScoSolicitacaoServico ss = getScoSolicitacaoServicoDAO().obterPorChavePrimaria(ssId);
			ScoSolicitacaoServico ss = solicitacaoServico;
			ScoSolicitacaoServico ssClone = getScoSolicitacaoServicoRN().clonarSolicitacaoServico(ss);

			// Inversão dos pontos de parada atual e anterior
			ScoPontoParadaSolicitacao pontoParada = ss.getPontoParada();
			ss.setPontoParada(ss.getPontoParadaLocAtual());
			ss.setPontoParadaLocAtual(pontoParada);
			// Set indDevolução
			ss.setIndDevolucao(true);
			// Set justificativa da Devolução
			ss.setJustificativaDevolucao(justificativa);

			getScoSolicitacaoServicoRN().atualizarSolicitacaoServico(ss, ssClone);
		}
	}

	public void persistirSolicitacaoDeServico(ScoSolicitacaoServico solicitacaoServico, ScoSolicitacaoServico solicitacaoServicoClone)
			throws BaseException {

		this.validaQtdeSolicitada(solicitacaoServico);
		this.validaValorUnitarioPrevisto(solicitacaoServico);

		if (solicitacaoServico.getNumero() == null) {
			// INCLUIR
			this.setaPontoParada(solicitacaoServico);
			getScoSolicitacaoServicoRN().inserirSolicitacaoServico(solicitacaoServico, true, false);
		} else {
			// EDITAR
			if (solicitacaoServico.getIndDevolucao()) {
				//Inverte os pontos de parada, chamado pela tela de solicitacaoServicoCRUD
				ScoPontoParadaSolicitacao pontoParada = solicitacaoServico.getPontoParada();
				solicitacaoServico.setPontoParada(solicitacaoServico.getPontoParadaLocAtual());
				solicitacaoServico.setPontoParadaLocAtual(pontoParada);
			}
			getScoSolicitacaoServicoRN().atualizarSolicitacaoServico(solicitacaoServico,solicitacaoServicoClone);
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

	/***
	 * Metodo que seta o ponto de parada para solicitante na inclusao da
	 * solicitação, apenas para os perfis: de comprador , planejamento e chefias
	 * 
	 * @param solicitacaoServico
	 * @throws ApplicationBusinessException
	 */
	public void setaPontoParada(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

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

	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	protected ScoSsJnDAO getScoSsJnDAO() {
		return scoSsJnDAO;
	}

	private ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return scoSolicitacaoServicoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return scoPontoParadaSolicitacaoDAO;
	}

	protected ScoSolicitacaoServicoRN getScoSolicitacaoServicoRN() {
		return scoSolicitacaoServicoRN;
	}

	protected ScoServicoDAO getScoServicoDAO() {
		return scoServicoDAO;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	private ScoCaracteristicaUsuarioCentroCustoDAO getScoCaracteristicaUsuarioCentroCustoDAO() {
		return scoCaracteristicaUsuarioCentroCustoDAO;
	}
	
	private ScoArquivoAnexoDAO getScoArquivoAnexoDAO() {
		return scoArquivoAnexoDAO;
	}
}
