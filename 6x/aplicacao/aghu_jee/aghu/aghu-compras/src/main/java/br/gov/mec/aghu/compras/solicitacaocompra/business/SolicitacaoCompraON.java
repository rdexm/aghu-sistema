package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoCaracteristicaUsuarioCentroCustoDAO;
import br.gov.mec.aghu.compras.dao.ScoMateriaisClassificacoesDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.SolicitacaoCompraRN.SolicitacaoCompraRNExceptionCode;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioTipoDespesa;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.suprimentos.vo.PesqLoteSolCompVO;

@Stateless
public class SolicitacaoCompraON extends BaseBusiness {

	private static final String PERMISSAO_CADASTRAR_SC_GERAL = "cadastrarSCGeral";

	private static final String PERMISSAO_CADASTRAR_SC_COMPRADOR = "cadastrarSCComprador";

	@EJB
	private SolicitacaoCompraRN solicitacaoCompraRN;
	
	@EJB
	private ListaMateriaisON listaMateriaisON;
	
	private static final Log LOG = LogFactory.getLog(SolicitacaoCompraON.class);
	
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
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoMateriaisClassificacoesDAO scoMateriaisClassificacoesDAO;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private ScoMaterialDAO scoMaterialDAO;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoCaracteristicaUsuarioCentroCustoDAO scoCaracteristicaUsuarioCentroCustoDAO;
	
	private static final long serialVersionUID = -5922834917785524834L;

	public enum SolicitacaoCompraONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_PPS_LICITACAO_NAO_ENCONTRADO, ERRO_DEVOLUCAO_NAO_PERMITIDA, ERRO_MATERIAL_ESTOCAVEL_PERFIL, ERRO_MATERIAL_COMPRAS_WEB;
	}
	
	public void persistirSolicitacaoDeCompra(
			ScoSolicitacaoDeCompra solicitacaoCompra,
			ScoSolicitacaoDeCompra solicitacaoCompraClone)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		this.validaCentroCusto(solicitacaoCompra);
		this.validaMaterialGenerico(solicitacaoCompra);
		this.validaTipoDespesaAceitaMaterial(solicitacaoCompra);
		this.validaCentroCustoExigeProjeto(solicitacaoCompra, servidorLogado);

		if (solicitacaoCompra.getNumero() == null) {
			// INCLUIR
			this.setaPontoParada(solicitacaoCompra);
			this.setaParametrosSCUsuarioAreasEspecificas(solicitacaoCompra,
					servidorLogado.getUsuario());
			this.setaParametrosSCUsuarioPlanejamento(solicitacaoCompra,
					servidorLogado.getUsuario());

			solicitacaoCompraRN.inserirSolicitacaoCompra(
					solicitacaoCompra, true);
		} else {
			// EDITAR
			this.validaDevolucaoUrgenteExclusao(solicitacaoCompra,
					solicitacaoCompraClone);

			solicitacaoCompraRN.atualizarSolicitacaoCompra(
					solicitacaoCompra, solicitacaoCompraClone);
		}
		// getEvents().raiseEvent("br.gov.mec.aghu.compras.solicitacaoCompraPersistida");
		// TODO MIGRAÇÃO Adicionar a chamada do método diretamente na controller
		// após o gravar
	}
	
	
	public void validaDevolucaoUrgenteExclusao(
			ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraOriginal)
			throws ApplicationBusinessException {
		if (!((!solicitacaoDeCompra.getDevolucao() && solicitacaoDeCompra
				.getJustificativaDevolucao() == null) || (solicitacaoDeCompra
				.getDevolucao() && solicitacaoDeCompra
				.getJustificativaDevolucao() != null))) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_SC_IND_DEVOLUCAO_E_JUSTIFICATIVA_DEVOLUCAO_INVALIDA);
		}

		if (!((!solicitacaoDeCompra.getUrgente() && solicitacaoDeCompra
				.getMotivoUrgencia() == null) || (solicitacaoDeCompra
				.getUrgente() && solicitacaoDeCompra.getMotivoUrgencia() != null))) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_SC_IND_URGENTE_E_MOTIVO_URGENCIA_INVALIDA);
		}

		if (!((!solicitacaoDeCompra.getExclusao() && solicitacaoDeCompra
				.getMotivoExclusao() == null) || (solicitacaoDeCompra
				.getExclusao() && solicitacaoDeCompra.getMotivoExclusao() != null))) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_SC_IND_EXCLUSAO_E_MOTIVO_EXCLUSAO_INVALIDA);
		}

		// #29707 - Se devolvido, setar (pps_codigo_loc_proxima = pps_codigo).
		if (solicitacaoDeCompra.getDevolucao()
				&& solicitacaoDeCompra.getDevolucao() != solicitacaoDeCompraOriginal
						.getDevolucao()) {
			solicitacaoDeCompra.setPontoParadaProxima(solicitacaoDeCompra
					.getPontoParada());
		}
	}
	
	/*********************************
	 * RN26
	 * 
	 * @param solicitacaoDeCompra	 
	 * @throws ApplicationBusinessException
	 */
	
	public boolean isRequiredCcProjeto(FccCentroCustos centroCusto)
			throws ApplicationBusinessException {
		return centroCustoFacade.centroCustoAceitaProjeto(centroCusto);

	}
	
	public void validaCentroCustoExigeProjeto(
			ScoSolicitacaoDeCompra solicitacaoDeCompra, RapServidores servidor)
			throws ApplicationBusinessException {

		if (this.isRequiredCcProjeto(solicitacaoDeCompra
				.getCentroCustoAplicada())
				&& solicitacaoDeCompra.getNroProjeto() == null) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_CC_OBRA_PROJETO_M19);
		}
	}
	
	public List<ScoMaterial> listarMateriaisAtivos(Object param, String servidorLogado) {
		return listaMateriaisON.listarMaterial(param, servidorLogado);
	}
		
	public Long listarMateriaisAtivosCount(Object param, String servidor) {
		return listaMateriaisON.listarMaterialCount(param, servidor);
	}
		
	public List<ScoMaterial> listarMateriaisSC(Object param) {
		return listaMateriaisON.listarMaterialSC(param, null);
	}
		
	public List<ScoMaterial> listarMateriaisSC(Object param, Integer gmtCodigo, Short almCodigo) {
		return listaMateriaisON.listarMaterialSC(param, gmtCodigo, almCodigo);
	}
		
	public Integer listarMateriaisSCCount(Object param, Integer gmtCodigo) {
		return listaMateriaisON.listarMaterialSCCount(param,gmtCodigo);
	}
		
	/**** FIM RN 26 ***/	
	
	/***********************
	 * RN 27 -- Para perfil areas especificas seta campos da Sc 
	 * vindos da tela de parametros da SC utilizado apenas na inserção
	 */
	
	public void setaParametrosSCUsuarioAreasEspecificas(
			ScoSolicitacaoDeCompra solicitacaoDeCompra, String login)
			throws ApplicationBusinessException {

		ScoParamAutorizacaoSc paramAutorizacaoSC = new ScoParamAutorizacaoSc();

		if (cascaFacade.usuarioTemPermissao(login,
				"cadastrarSCAreasEspecificas")) {

			paramAutorizacaoSC = comprasCadastrosBasicosFacade.obterParametrosAutorizacaoSCPrioridade(
							solicitacaoDeCompra.getCentroCusto(),
							solicitacaoDeCompra.getCentroCustoAplicada(),
							solicitacaoDeCompra.getServidor());

			if (paramAutorizacaoSC == null) {
				// Correção defeito em Homologação #28247
				// buscar por centro de custo superior
				ScoParamAutorizacaoSc paramAutorizacaoSCSuperior = comprasCadastrosBasicosFacade
						.obterParametrosAutorizacaoSCPrioridade(
								solicitacaoDeCompra.getCentroCusto()
										.getCentroCusto(),
								solicitacaoDeCompra.getCentroCustoAplicada(),
								solicitacaoDeCompra.getServidor());

				if (paramAutorizacaoSCSuperior != null
						&& paramAutorizacaoSCSuperior.getIndHierarquiaCCusto() != null
						&& paramAutorizacaoSCSuperior.getIndHierarquiaCCusto()) {
					paramAutorizacaoSC = paramAutorizacaoSCSuperior;
				}
			}

		}
		
		if (paramAutorizacaoSC != null && paramAutorizacaoSC.getSeq() != null) {

			if (paramAutorizacaoSC.getPontoParada() != null) {
				solicitacaoDeCompra.setPontoParada(paramAutorizacaoSC
						.getPontoParada());
			}

			if (paramAutorizacaoSC.getPontoParadaProxima() != null) {
				solicitacaoDeCompra.setPontoParadaProxima(paramAutorizacaoSC
						.getPontoParadaProxima());
			}

			if (paramAutorizacaoSC.getServidorAutoriza() != null) {
				solicitacaoDeCompra.setServidorAutorizacao(paramAutorizacaoSC
						.getServidorAutoriza());
				solicitacaoDeCompra.setDtAutorizacao(new Date());
			}

			if (paramAutorizacaoSC.getServidorCompra() != null) {
				solicitacaoDeCompra.setServidorCompra(paramAutorizacaoSC
						.getServidorCompra());
			}
		}
	}

	  /***********************
	   * RN 28 -- Para perfil areas especificas seta campos da Sc 
	   * vindos da tela de parametros da SC utilizado apenas na inserção
	  */
	  
	public void setaParametrosSCUsuarioPlanejamento(
			ScoSolicitacaoDeCompra solicitacaoDeCompra, String login)
			throws ApplicationBusinessException {

		if (cascaFacade.usuarioTemPermissao(login,
				"cadastrarSCPlanejamento")) {

			AghParametros matriculaPlanejamentoParametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_MATR_CHEFE_PLANEJ);

			AghParametros vinculoPlanejamentoParametro = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_VIN_CHEFE_PLANEJ);

			AghParametros pontoParadaPlanejamentoParametro = this
					.parametroFacade.buscarAghParametro(
							AghuParametrosEnum.PPS_PLANEJAMENTO);

			Integer matriculaChefePlanejamento = null;
			if (pontoParadaPlanejamentoParametro != null){
				matriculaChefePlanejamento = matriculaPlanejamentoParametro
					.getVlrNumerico().intValue();
			}
			
			Short vinculoChefePlanejamento = null;
			if (vinculoPlanejamentoParametro != null && vinculoPlanejamentoParametro.getVlrNumerico() != null){
				vinculoChefePlanejamento = vinculoPlanejamentoParametro.getVlrNumerico().shortValue();
			}
			Short pontoParadaPlanejamento = pontoParadaPlanejamentoParametro
					.getVlrNumerico().shortValue();

			RapServidores servidorPlanejamento = null;
			if(vinculoChefePlanejamento != null && matriculaChefePlanejamento != null){
				servidorPlanejamento = registroColaboradorFacade
						.obterRapServidorPorVinculoMatricula(matriculaChefePlanejamento, vinculoChefePlanejamento);
			}
			
			ScoPontoParadaSolicitacao pontoParadaSolicitacaoPlanejamento = comprasCadastrosBasicosFacade
					.obterPontoParada(pontoParadaPlanejamento);

			solicitacaoDeCompra.setServidorAutorizacao(servidorPlanejamento);
			solicitacaoDeCompra.setDtAutorizacao(new Date());
			solicitacaoDeCompra.setPontoParada(pontoParadaSolicitacaoPlanejamento);
			solicitacaoDeCompra.setPontoParadaProxima(pontoParadaSolicitacaoPlanejamento);

		}
	} 
	
	/**
	 * Verifica se o Tipo de Despesa do Ccusto aceita o material
	 */
	
	public void validaTipoDespesaAceitaMaterial(
			ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {
		FccCentroCustos centroCusto = solicitacaoDeCompra
				.getCentroCustoAplicada();
		if (centroCusto == null) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.CENTRO_CUSTO_NAO_ENCONTRADO);
		}

		if (solicitacaoDeCompra.getMaterial() == null) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_MATERIAL_M13);
		}

		// Verifica se o Centro de Custo foi criado para Serviço. Nesse caso não
		// aceita SC de material.
		if (DominioTipoDespesa.S.equals(centroCusto.getIndTipoDespesa())) { // ServiÃ§o
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_CC_SERV_MAT_M10);
		}

		if (DominioTipoDespesa.O.equals(centroCusto.getIndTipoDespesa())) { // Obra
			if (!solicitacaoDeCompra.getMaterial().getGrupoMaterial()
					.getEngenhari()) {
				throw new ApplicationBusinessException(
						SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_CC_OBRA_MAT_ENG_M11);
			}
		}

	}
	
	public void setaPontoParada(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade
				.obterServidorLogado();

		if (cascaFacade.usuarioTemPermissao(servidorLogado.getUsuario(),
				PERMISSAO_CADASTRAR_SC_COMPRADOR)) {

			// #29625 - Antes de autorizar, verificar se o usuario logado está
			// no mesmo centro de custo, caso não autorizar.
			// Se sim, não setar os parametros para autorização
			if (!this.registroColaboradorFacade
					.isServidorVinculadoCentroCusto(
							servidorLogado.getId().getVinCodigo(),
							servidorLogado.getId().getMatricula(),
							solicitacaoDeCompra.getCentroCustoAplicada()
									.getCodigo())) {

				AghParametros ppsComprador = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PPS_COMPRADOR);

				solicitacaoDeCompra.setServidorCompra(servidorLogado);

				solicitacaoDeCompra.setPontoParadaProxima(scoPontoParadaSolicitacaoDAO
						.obterPorChavePrimaria(
								ppsComprador.getVlrNumerico().shortValue()));
				solicitacaoDeCompra.setPontoParada(scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(
								ppsComprador.getVlrNumerico().shortValue()));

				AghParametros matriculaChefeCompras = parametroFacade.buscarAghParametro( AghuParametrosEnum.P_MATR_CHEFE_CPRAS);
				AghParametros vincChefeCompras = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_VIN_CHEFE_CPRAS);
				solicitacaoDeCompra.setServidorAutorizacao(registroColaboradorFacade.buscarServidor(
						vincChefeCompras.getVlrNumerico().shortValue(),
								matriculaChefeCompras.getVlrNumerico().intValue()));

				solicitacaoDeCompra.setDtAutorizacao(new Date());

			}

		} else if (cascaFacade.usuarioTemPermissao(
				servidorLogado.getUsuario(), PERMISSAO_CADASTRAR_SC_GERAL)) {

			AghParametros ppsSolicitante = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_PPS_SOLICITANTE);
			solicitacaoDeCompra.setServidor(servidorLogado);
			solicitacaoDeCompra.setPontoParadaProxima(scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(
							ppsSolicitante.getVlrNumerico().shortValue()));
			solicitacaoDeCompra.setPontoParada(scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(
							ppsSolicitante.getVlrNumerico().shortValue()));
		}

		else if (cascaFacade.usuarioTemPermissao(
				servidorLogado.getUsuario(), "cadastrarSCChefias")) {

			AghParametros ppsSolicitante = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_PPS_AUTORIZACAO);
			solicitacaoDeCompra.setPontoParadaProxima(scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(
							ppsSolicitante.getVlrNumerico().shortValue()));
			solicitacaoDeCompra.setPontoParada(scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(
							ppsSolicitante.getVlrNumerico().shortValue()));
		}

	}
	
	
	/****
	 * RN 08 - Metodo que valida o centro de custo acionado na inclusão e
	 * alteracao caso centro de custo nao esteja na lista
	 * 
	 * @param solicitacaoCompra
	 * @throws ApplicationBusinessException
	 */
	
	public void validaCentroCusto(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade
				.obterServidorLogado();

		if (!cascaFacade.usuarioTemPermissao(servidorLogado.getUsuario(),
				"cadastrarSCPlanejamento")
				&& !cascaFacade.usuarioTemPermissao(
						servidorLogado.getUsuario(),
						PERMISSAO_CADASTRAR_SC_COMPRADOR)
				&& !cascaFacade.usuarioTemPermissao(
						servidorLogado.getUsuario(),
						PERMISSAO_CADASTRAR_SC_GERAL)) {

			if (!centroCustoFacade
					.pesquisarCentroCustoUsuarioGerarSC()
					.contains(solicitacaoDeCompra.getCentroCusto())) {

				FccCentroCustos ccFipe = this
						.scoCaracteristicaUsuarioCentroCustoDAO
						.obterCcAplicacaoGeracaoGppg(servidorLogado);
				Boolean possuiCaractGppg = false;
				if (ccFipe != null) {
					Set<Integer> listaHierarquica = centroCustoFacade
							.pesquisarCentroCustoComHierarquia(
									ccFipe.getCodigo());
					if (listaHierarquica.contains(solicitacaoDeCompra
							.getCentroCustoAplicada().getCodigo())) {
						possuiCaractGppg = true;
					}
				}

				if (!possuiCaractGppg) {

					throw new ApplicationBusinessException(
							SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_CC_PERMISSAO);

				}
			}
		}
	}
		
	/*****
	 * Material generico deve obriga a SC a ter descrição
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 */
	
	public void validaMaterialGenerico(
			ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		boolean flagQtdeSol = false;
		if (solicitacaoDeCompra.getDescricao() != null) {
			solicitacaoDeCompra.setDescricao(solicitacaoDeCompra.getDescricao().trim());
			flagQtdeSol = (solicitacaoDeCompra.getDescricao().length() > 0 ? false : true);
		} else {
			flagQtdeSol = true;
		}

		if (solicitacaoDeCompra.getMaterial() == null) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_MATERIAL_M13);
		}

		if (this.isRequeridDescricaoCompra(solicitacaoDeCompra.getMaterial())
				&& (flagQtdeSol)) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_MATERIAL_DESCRICAO_OBR_M06);

		}
	}



	public boolean isRequeridDescricaoCompra(ScoMaterial scoMaterial)
			throws ApplicationBusinessException {
		return solicitacaoCompraRN.isRequeridDescricaoCompra(scoMaterial);
	}

	public Double getUltimoValorCompra(ScoMaterial scoMaterial)
			throws ApplicationBusinessException {
		return solicitacaoCompraRN.getUltimoValorCompra(scoMaterial);
	}

	/**
	 * Retorna a quantidade de solicitações de compras conforme preenchimento do VO filtroPesquisa.
	 * Utilizado na tela de encaminhar solicitações de compras
	 * @param filtroPesquisa
	 * @param servidor
	 * @return Integer
	 */
	public Long countLoteSolicitacaoCompras(PesqLoteSolCompVO filtroPesquisa) {
		RapServidores servidor = this.servidorLogadoFacade
				.obterServidorLogado();
		return scoSolicitacoesDeComprasDAO.countLoteSolicitacaoCompras(
				filtroPesquisa, servidor);
	}

	
	/*************
	 * Valida Quantidade Solicitada e Aprovada que deve ser maior que zero
	 * chamado na inserção
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void validaQtdeSolicitadaAprovada(
			ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		solicitacaoCompraRN.validaQtdeSolicitadaAprovada(
				solicitacaoDeCompra);

	}

	/****
	 * RN 11 - Metodo Acionado na acao gravar caso a quantidade solicitada Não
	 * esteja em branca seta o valor dela para o da quantidade aprovada *
	 * 
	 * @param solicitacaoCompra
	 */
	public void alteraQuantidadeAprovada(
			ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		solicitacaoCompraRN.alteraQuantidadeAprovada(solicitacaoDeCompra);
	}

	

	/*********************************
	 * RN23
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void validaUrgentePrioritario(
			ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		solicitacaoCompraRN.validaUrgentePrioritario(solicitacaoDeCompra);

	}

	public ScoSolicitacaoDeCompra clonarSolicitacaoDeCompra(
			ScoSolicitacaoDeCompra solicitacaoCompra)
			throws ApplicationBusinessException {
		return this.solicitacaoCompraRN.clonarSolicitacaoDeCompra(
				solicitacaoCompra);
	}

	public void duplicarSC(ScoSolicitacaoDeCompra solicitacaoDeCompra,
			RapServidores usuarioLogado, Boolean mantemCcOriginal)
			throws ApplicationBusinessException {

		RapServidores servidorLogado = servidorLogadoFacade
				.obterServidorLogado();
		solicitacaoDeCompra.setNumero(null);
		solicitacaoDeCompra.setServidor(servidorLogado);
		if (!mantemCcOriginal) {
			solicitacaoDeCompra.setCentroCusto(usuarioLogado.getCentroCustoLotacao());
			solicitacaoDeCompra.setCentroCustoAplicada(usuarioLogado.getCentroCustoLotacao());
		}
		solicitacaoDeCompra.setServidorAutorizacao(null);
		solicitacaoDeCompra.setDtAutorizacao(null);
		solicitacaoDeCompra.setServidorCompra(null);
		solicitacaoDeCompra.setDtSolicitacao(new Date());
		solicitacaoDeCompra.setDtDigitacao(new Date());
		solicitacaoDeCompra.setExclusao(false);
		solicitacaoDeCompra.setDevolucao(false);
		solicitacaoDeCompra.setMotivoExclusao(null);
		solicitacaoDeCompra.setDtExclusao(null);
		solicitacaoDeCompra.setDtAnalise(null);
		solicitacaoDeCompra.setJustificativaDevolucao(null);
		solicitacaoDeCompra.setServidorExclusao(null);
		solicitacaoDeCompra.setServidorAlteracao(null);
		solicitacaoDeCompra.setDtAlteracao(null);
		solicitacaoDeCompra.setQtdeEntregue(null);
		solicitacaoDeCompra.setEfetivada(false);
		solicitacaoDeCompra.setPaciente(null);
		solicitacaoDeCompra.setSlcNumeroVinculado(null);
		solicitacaoDeCompra.setFases(null);
		solicitacaoDeCompra.setPontoParada(null);
		solicitacaoDeCompra.setPontoParadaProxima(null);
		this.persistirSolicitacaoDeCompra(solicitacaoDeCompra, null);
	}
	
	public List<ScoMaterial> listarMaterial(String param, String login) {

		if (cascaFacade.usuarioTemPermissao(login,
				PERMISSAO_CADASTRAR_SC_COMPRADOR)
				|| cascaFacade.usuarioTemPermissao(login,
						"cadastrarSolicitacaoCompras")
				|| cascaFacade.usuarioTemPermissao(login,
						"cadastrarSCChefias")
				|| cascaFacade.usuarioTemPermissao(login,
						"cadastrarSCAreasEspecificas")
				|| cascaFacade.usuarioTemPermissao(login,
						PERMISSAO_CADASTRAR_SC_GERAL)) {
			return scoMaterialDAO.listarMaterialAtivo(param, false, null);
		}

		else {
			return scoMaterialDAO.listarMaterialAtivo(param, null, null);
		}

	}

	public Long listarMaterialCount(String param, String login) {

		if (cascaFacade.usuarioTemPermissao(login,
				PERMISSAO_CADASTRAR_SC_COMPRADOR)
				|| cascaFacade.usuarioTemPermissao(login,
						"cadastrarSolicitacaoCompras")
				|| cascaFacade.usuarioTemPermissao(login,
						"cadastrarSCChefias")
				|| cascaFacade.usuarioTemPermissao(login,
						"cadastrarSCAreasEspecificas")
				|| cascaFacade.usuarioTemPermissao(login,
						PERMISSAO_CADASTRAR_SC_GERAL)) {
			return scoMaterialDAO.listarMaterialAtivoCount(param, false);
		}

		else {
			return scoMaterialDAO.listarMaterialAtivoCount(param, null);
		}

	}

	public List<ScoMaterial> listarMaterialSC(Object param, Integer gmtCodigo) {
		return listarMaterialSC(param, gmtCodigo, null);
	}
	
	public List<ScoMaterial> listarMaterialSC(Object param, Integer gmtCodigo,
			Short almCodigo) {
		try {
			AghParametros parametroFiltroAlmox = parametroFacade
					.buscarAghParametro(
							AghuParametrosEnum.P_AGHU_FILTRO_ALMOXARIFADO_RM);
			if (parametroFiltroAlmox == null
					|| parametroFiltroAlmox.getVlrTexto().equals("N")) {
				almCodigo = null;
			}
		} catch (ApplicationBusinessException e) {
			almCodigo = null;
		}

		List<ScoMaterial> listaMaterialAtivo = scoMaterialDAO.listarMaterialAtivo(param, null, gmtCodigo, almCodigo);
		String strPesquisa = (String) param;
		List<ScoMaterial> listaMaterialAtivoAux = new ArrayList<ScoMaterial>();
		if (!CoreUtil.isNumeroInteger(strPesquisa)
				&& StringUtils.isNotBlank(strPesquisa)) {
			for (ScoMaterial scoMaterial : listaMaterialAtivo) {
				if (scoMaterial.getIndAtivo()
						&& (gmtCodigo == null || (gmtCodigo != null && gmtCodigo
								.equals(scoMaterial.getGmtCodigo())))
						&& (almCodigo == null || (almCodigo != null && almCodigo
								.equals(scoMaterial.getAlmoxarifado().getSeq())))) {
					listaMaterialAtivoAux.add(scoMaterial);
				}
			}
			if (gmtCodigo != null) {
				for (ScoMaterial scoMaterial : listaMaterialAtivoAux) {
					if (!scoMaterial.getGmtCodigo().equals(gmtCodigo)) {
						listaMaterialAtivoAux.remove(scoMaterial);
					}
				}
			}

			if (almCodigo != null) {
				for (ScoMaterial scoMaterial : listaMaterialAtivoAux) {
					if (!scoMaterial.getAlmoxarifado().getSeq().equals(almCodigo)) {
						listaMaterialAtivoAux.remove(scoMaterial);
					}
				}
			}
			return listaMaterialAtivoAux;
		}
		return listaMaterialAtivo;
	}

	public Integer listarMaterialSCCount(String param, Integer gmtCodigo) {
		return this.listarMaterialSC(param, gmtCodigo).size();
	}


	/**
	 * 
	 * @param param
	 * @param login
	 * @throws ApplicationBusinessException
	 */
	public void verificarMaterialSelecionado(ScoMaterial material, Boolean temPermissaoCadastrar, Boolean temPermissaoChefia,
			Boolean temPermissaoAreasEspecificas, Boolean temPermissaoGeral, Boolean temPermissaoPlanejamento) throws ApplicationBusinessException {

		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		if (!cascaFacade.usuarioTemPermissao(servidorLogado.getUsuario(),"permiteSolicitarWeb")) {
			AghParametros paramClassificacaoComprasWeb = null;
			try {
				paramClassificacaoComprasWeb = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRUPO_CLASSIF_COMPRAS_WEB);
			} catch (ApplicationBusinessException e) {
				LOG.error("parametro compras web nao cadastrado.");
			}

			if (comprasFacade.verificarComprasWeb(paramClassificacaoComprasWeb, material)) {
				throw new ApplicationBusinessException(SolicitacaoCompraONExceptionCode.ERRO_MATERIAL_COMPRAS_WEB);
			}
		}
		if (bloqueiaSolicitacaoComprasMatEstocavel(material, temPermissaoCadastrar, temPermissaoChefia,
				temPermissaoAreasEspecificas, temPermissaoGeral, temPermissaoPlanejamento)) {
			throw new ApplicationBusinessException(SolicitacaoCompraONExceptionCode.ERRO_MATERIAL_ESTOCAVEL_PERFIL,material.getCodigo());
		}
	}
	
	public Boolean bloqueiaSolicitacaoComprasMatEstocavel(ScoMaterial material,
			Boolean temPermissaoCadastrar, Boolean temPermissaoChefia,
			Boolean temPermissaoAreasEspecificas, Boolean temPermissaoGeral,
			Boolean temPermissaoPlanejamento) {
		return (material != null && material.getEstocavel()
				&& !temPermissaoPlanejamento && (temPermissaoCadastrar
				|| temPermissaoChefia || temPermissaoAreasEspecificas || temPermissaoGeral));
	}
	
	public Boolean habilitarAutorizarSC(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException {
		if ((solicitacaoDeCompra.getExclusao() != null && solicitacaoDeCompra.getExclusao())
				|| (solicitacaoDeCompra.getDevolucao() != null && solicitacaoDeCompra.getDevolucao())
				|| solicitacaoDeCompra.getDtAutorizacao() != null) {
			return false;
		}
		AghParametros ppsAutorizacao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PPS_AUTORIZACAO);
		ScoPontoParadaSolicitacao pontoParadaPpsAutorizacao = scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(
						ppsAutorizacao.getVlrNumerico().shortValue());
		if (!pontoParadaPpsAutorizacao.equals(solicitacaoDeCompra.getPontoParadaProxima())) {
			return false;
		}

		List<FccCentroCustos> listaCentroCustos = centroCustoFacade.pesquisarCentroCustoUsuarioAutorizaSC();

		if (!listaCentroCustos.contains(solicitacaoDeCompra.getCentroCusto())) {
			return false;
		}

		List<ScoFaseSolicitacao> listFases = comprasFacade.pesquisarFaseSolicitacaoPorNumeroSolCompraComAutForn(
						solicitacaoDeCompra.getNumero());
		if (listFases != null && listFases.size() > 0) {
			return false;
		}

		return true;
	}

	public List<Integer> obterListaNumeroSc(List<ScoSolicitacaoDeCompra> listaSc) {
		List<Integer> listaResultado = new ArrayList<Integer>();

		if (listaSc != null) {
			for (ScoSolicitacaoDeCompra sc : listaSc) {
				listaResultado.add(sc.getNumero());
			}
		}

		return listaResultado;
	}
	
	public Boolean habilitarEncaminharSC(ScoSolicitacaoDeCompra solicitacaoDeCompra,
			Boolean temPermissaoComprador, Boolean temPermissaoPlanejamento,
			Boolean temPermissaoEncaminhar, List<FccCentroCustos> listaCentroCustosUsuario) {

		if (solicitacaoDeCompra.getExclusao() != null && solicitacaoDeCompra.getExclusao() || !temPermissaoEncaminhar) {
			return false;
		}

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		FccCentroCustos ccFipe = this.scoCaracteristicaUsuarioCentroCustoDAO.obterCcAplicacaoGeracaoGppg(servidorLogado);
		Boolean possuiCaractGppg = false;
		if (ccFipe != null) {
			Set<Integer> listaHierarquica = centroCustoFacade.pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
			if (listaHierarquica.contains(solicitacaoDeCompra.getCentroCustoAplicada().getCodigo())) {
				possuiCaractGppg = true;
			}
		}
		// Valida se o servidor pode mover a solicitação no ponto de parada
		// atual.
		if (!possuiCaractGppg && !this.validarAcessoPontoParada(solicitacaoDeCompra.getPontoParadaProxima())) {
			return false;
		}
		// Para as permissões cadastrarSCPlanejamento e cadastrarSCComprador
		// podem encaminhar solicitação para qualquer centro de custos
		if (!temPermissaoPlanejamento && !temPermissaoComprador
				&& !possuiCaractGppg) {
			if (listaCentroCustosUsuario == null || !listaCentroCustosUsuario.contains(solicitacaoDeCompra.getCentroCusto())) {
				return false;
			}
		}

		if (this.comprasFacade.getScEmFases(solicitacaoDeCompra.getNumero())) {
			return false;
		}

		return true;
	}

	private boolean validarAcessoPontoParada(final ScoPontoParadaSolicitacao pontoParada) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (this.solicitacaoComprasFacade.pesquisarScoPontoServidorCount(pontoParada, servidorLogado, null) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Devolve solicitações de compras.
	 * 
	 * @param nroScos ID's das SC's a serem devolvidas.
	 * @param justificativa Justificativa.
	 */
	public void devolverSolicitacoesCompras(List<Integer> nroScos, String justificativa) throws BaseException {
		for (Integer scId : nroScos) {
			ScoSolicitacaoDeCompra sc = scoSolicitacoesDeComprasDAO.obterPorChavePrimaria(scId);
			ScoSolicitacaoDeCompra scClone = solicitacaoCompraRN.clonarSolicitacaoDeCompra(sc);
			if (ObjectUtils.equals(sc.getPontoParadaProxima().getTipoPontoParada(), DominioTipoPontoParada.CH)
					&& ObjectUtils.equals(sc.getPontoParada().getTipoPontoParada(), DominioTipoPontoParada.PL)
					|| ObjectUtils.equals(sc.getPontoParada().getTipoPontoParada(), DominioTipoPontoParada.CP)) {
				throw new ApplicationBusinessException(SolicitacaoCompraONExceptionCode.ERRO_DEVOLUCAO_NAO_PERMITIDA, sc.getNumero());
			}
			sc.setDevolucao(true);
			sc.setJustificativaDevolucao(justificativa);
			ScoPontoParadaSolicitacao pontoParada = sc.getPontoParada();
			sc.setPontoParada(sc.getPontoParadaProxima());
			sc.setPontoParadaProxima(pontoParada);
			solicitacaoCompraRN.atualizarSolicitacaoCompra(sc, scClone);
		}
	}

	public ScoSolicitacaoDeCompra obterSolicitacaoDeCompra(Integer numero) {
		ScoSolicitacaoDeCompra solicitacaoDeCompra = this.scoSolicitacoesDeComprasDAO.obterPorChavePrimaria(
						numero,
						true,
						ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA,
						ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO,
						ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA,
						ScoSolicitacaoDeCompra.Fields.PACIENTE,
						ScoSolicitacaoDeCompra.Fields.SERVIDOR_COMPRA,
						ScoSolicitacaoDeCompra.Fields.MODALIDADE_LICITACAO,
						ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO,
						ScoSolicitacaoDeCompra.Fields.PONTO_PARADA_SOLICITACAO,
						ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL,
						ScoSolicitacaoDeCompra.Fields.MATERIAL,
						ScoSolicitacaoDeCompra.Fields.GRUPO_NATUREZA_DESPESA);
		if (solicitacaoDeCompra != null) {
			if (solicitacaoDeCompra.getNaturezaDespesa() != null && solicitacaoDeCompra.getNaturezaDespesa().getGrupoNaturezaDespesa() != null) {
				solicitacaoDeCompra.getNaturezaDespesa().getGrupoNaturezaDespesa().getDescricao();
			}
			if (solicitacaoDeCompra.getServidorCompra() != null	&& solicitacaoDeCompra.getServidorCompra().getPessoaFisica() != null) {
				solicitacaoDeCompra.getServidorCompra().getPessoaFisica().getNome();
			}
			if (solicitacaoDeCompra.getMaterial() != null && solicitacaoDeCompra.getMaterial().getUnidadeMedida() != null) {
				solicitacaoDeCompra.getMaterial().getUnidadeMedida().getDescricao();
			}
		}
		return solicitacaoDeCompra;
	}
	
	public Boolean verificarComprasWeb(AghParametros param, ScoMaterial material) {
		return scoMateriaisClassificacoesDAO.verificarComprasWeb(param, material);
	}

	public boolean verificaPemissaoUsuario(String permissao, String login)
			throws ApplicationBusinessException {
		return cascaFacade.usuarioTemPermissao(login, permissao);
	}
	
}