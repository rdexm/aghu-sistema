package br.gov.mec.aghu.exames.ejb;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioLwsCodigoResposta;
import br.gov.mec.aghu.dominio.DominioLwsTipoComunicacao;
import br.gov.mec.aghu.dominio.DominioLwsTipoLiberacao;
import br.gov.mec.aghu.dominio.DominioLwsTipoResultado;
import br.gov.mec.aghu.dominio.DominioLwsTipoStatusTransacao;
import br.gov.mec.aghu.estoque.vo.VoltarProtocoloUnicoVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoRelacionadoDAO;
import br.gov.mec.aghu.exames.dao.AelEquipamentosDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelServidorUnidAssinaEletDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.dao.LwsComAmostraDAO;
import br.gov.mec.aghu.exames.dao.LwsComAntibiogramaDAO;
import br.gov.mec.aghu.exames.dao.LwsComResultadoDAO;
import br.gov.mec.aghu.exames.dao.LwsComSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.LwsComunicacaoDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelCampoLaudoRelacionado;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoExameId;
import br.gov.mec.aghu.model.AelServidorUnidAssinaElet;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.LwsComAmostra;
import br.gov.mec.aghu.model.LwsComAntibiograma;
import br.gov.mec.aghu.model.LwsComResultado;
import br.gov.mec.aghu.model.LwsComSolicitacaoExame;
import br.gov.mec.aghu.model.LwsComunicacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.PackagePrivateBaseBusiness", "PMD.AtributoEmSeamContextManager" })
public class VoltarProtocoloUnicoBean extends BaseBusiness implements VoltarProtocoloUnicoBeanLocal {

	private static final String COMMA_COMUNICACAO = ", comunicaÃ§Ã£o ";

	private static final String COMMA_SOLICITACAO = ", solicitaÃ§Ã£o ";

	private static final String COMMA_EXAME = ", exame ";

	private static final String _DA_COMUNICACAO_ = " da comunicaÃ§Ã£o ";

	private static final String _DA_SOLICITACAO_ = " da solicitaÃ§Ã£o ";

	private static final String EXAME_ = "Exame ";

	private static final String _NAO_EXISTE_NO_AGHU = " nÃ£o existe no AGHU.";

	private static final String _EXAME_ = " exame ";

	private static final String COMUNIC = " comunic. ";

	private static final String SOLICIT = " solicit. ";

	/**
	 * 
	 */
	private static final long serialVersionUID = 7035158440992413254L;

	private static final Log LOG = LogFactory.getLog(VoltarProtocoloUnicoBean.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private LwsComSolicitacaoExameDAO lwsComSolicitacaoExameDAO;

	@Inject
	private LwsComunicacaoDAO lwsComunicacaoDAO;

	@Inject
	private LwsComAmostraDAO lwsComAmostraDAO;

	@Inject
	private AelAmostrasDAO aelAmostrasDAO;

	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

	@Inject
	private AelExamesDAO aelExamesDAO;

	@Inject
	private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;

	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@Inject
	private LwsComResultadoDAO lwsComResultadoDAO;

	@Inject
	private AelEquipamentosDAO aelEquipamentosDAO;

	@Inject
	private AelCampoLaudoDAO aelCampoLaudoDAO;

	@Inject
	private AelServidorUnidAssinaEletDAO aelServidorUnidAssinaEletDAO;

	@Inject
	private LwsComAntibiogramaDAO lwsComAntibiogramaDAO;

	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;

	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;

	@Inject
	private AelCampoLaudoRelacionadoDAO aelCampoLaudoRelacionadoDAO;

	public enum VoltarProtocoloUnicoBeanRNExceptionCode implements BusinessExceptionCode {
		AEL_03025, AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK, ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK, ERRO_AO_TENTAR_VOLTAR_AMOSTRA_GESTAM;
	}

	@Resource
	protected SessionContext ctx;

	/*
	 * MÃ‰TODOS DO SCHEDULER
	 */

	/**
	 * MÃ©todo chamado pelo Scheduler para o processamento de amostras no mÃ³dulo GESTAM
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void processarComunicacaoModuloGestaoAmostra(String nomeMicrocomputador) throws BaseException {

		/*
		 * O ciclo de vida da tarefa de processamento de amostas Ã© INTERROMPIDO quando o parÃ¢metro P_MODO_INTERFACEAMENTO estÃ¡ desativado (diferente de H ou N)
		 */
		AghParametros parametroModoInterfaceamento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MODO_INTERFACEAMENTO);
		String modoInterfaceamento = parametroModoInterfaceamento.getVlrTexto();
		if (!("H".equalsIgnoreCase(modoInterfaceamento) || "N".equalsIgnoreCase(modoInterfaceamento))) {
			// Exibe log de inicializaÃ§Ã£o
			LOG.info("Volta do Interfaceamento NÃƒO EXECUTOU! O P_MODO_INTERFACEAMENTO estÃ¡ desativado (diferente de H ou N)");
			return; // Interfaceamento INATIVO ou DIFERENTE de H ou N
		}

		// Mensagens de LOG somente. NÃ£o Ã© utilizado nas Regras
		boolean logarErros = false;
		boolean logarRestricoes = false;

		try {

			// Exibe log de inicializaÃ§Ã£o
			LOG.info("Volta do Interfaceamento INICIADA...");

			final VoltarProtocoloUnicoBeanLocal ejbPesquisaRecepcaoResultaosGestam = this.ctx.getBusinessObject(VoltarProtocoloUnicoBeanLocal.class);

			// Pesquisa a recepÃ§Ã£o de resultados do mÃ³dulo GESTAM
			final LwsComunicacao resultadoGestam = ejbPesquisaRecepcaoResultaosGestam.obterRecepcaoResultadoGestaoAmostra();

			// ContÃ©m atributos anÃ¡logos as variÃ¡veis do pacote do PL-SQL
			final VoltarProtocoloUnicoVO voVariaveisPacote = new VoltarProtocoloUnicoVO();

			if (resultadoGestam != null) {

				VoltarProtocoloUnicoBeanLocal ejbFinalProcessamentoErro = this.ctx.getBusinessObject(VoltarProtocoloUnicoBeanLocal.class);

				// ObtÃ©m ID DA COMUNICAÃ‡ÃƒO e que serÃ¡ utilizado como chave (identificador) de um erro
				final Integer idComunicacao = resultadoGestam.getIdComunicacao();

				try {

					// // Quando @InjetarLogin falhar
					// if(servidorLogado != null && StringUtils.isEmpty(servidorLogado.getUsuario())){
					// AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_USUARIO_AGENDADOR);
					// servidorLogado = getRapServidoresDAO().obterServidorPorUsuario(aghParametro.getVlrTexto());
					// }

					VoltarProtocoloUnicoBeanLocal ejbVoltarProtocoloUnico = this.ctx.getBusinessObject(VoltarProtocoloUnicoBeanLocal.class);

					/*
					 * Chamada da PROCEDURE AELP_VOLTA_PROT_UNIC da RN VoltarProtocoloUnicoBean
					 */
					ejbVoltarProtocoloUnico.voltarProtocoloUnico(resultadoGestam, voVariaveisPacote, nomeMicrocomputador);

				} catch (Exception e) {

					LOG.error(e.getMessage(),e);

					// Mensagens de LOG somente. NÃ£o Ã© utilizado nas Regras
					logarErros = true;

					// ROLLBACK DO CONTEXTO DA SESSÃƒO
					this.ctx.setRollbackOnly();

					// ObtÃ©m a observaÃ§Ã£o do erro
					String observacao = this.obterObservacaoFormatadaErro(idComunicacao, e);

					// Insere erro de comunicaÃ§Ã£o
					ejbFinalProcessamentoErro.inserirComErro(idComunicacao, observacao);

				} finally {

					VoltarProtocoloUnicoBeanLocal ejbFinalProcessamento = this.ctx.getBusinessObject(VoltarProtocoloUnicoBeanLocal.class);

					/**
					 * Atualiza o status do resultado do GESTAM para "conunicaÃ§Ã£o processada" Obs. Ocorre sempre e independente de erros, pois assim evita que a amostra fique
					 * trancada no GESTAM
					 */
					ejbFinalProcessamento.atualizarResultadoGestaoAmostra(idComunicacao);

					// Verifica se a VARIÃ�VEL DE PACOTE "podeGravar" foi alterada
					if (voVariaveisPacote.isPodeGravar()) {

						// Mensagens de LOG somente. NÃ£o Ã© utilizado nas Regras
						voVariaveisPacote.setMarcaProcessada(1);

						// Verifica se hÃ¡ mensagens de ALERTA ou ERRO
						if (voVariaveisPacote.getMensagemAlerta() == null && voVariaveisPacote.getLocalizacaoAlerta() == null) {

							// Insere uma nova comunicaÃ§Ã£o LWS processada com SUCESSO (CÃ“DIGO A00)
							ejbFinalProcessamento.inserirLwsComunicacaoProcessadaSucesso(idComunicacao);

						} else {

							// Mensagens de LOG somente. NÃ£o Ã© utilizado nas Regras
							logarRestricoes = false;

							// Insere uma nova comunicaÃ§Ã£o LWS processada mas COM RESTRIÃ‡Ã•ES (CÃ“DIGO A01)
							ejbFinalProcessamento.inserirLwsComunicacaoProcessadaRestricoes(idComunicacao, voVariaveisPacote);
						}
					}
				}

			}

		} finally {

			// CompÃµe o complemento na mensagem de log na finalizaÃ§Ã£o. NÃ£o Ã© utilizado nas Regras
			String msgLogErros = logarErros ? "ERROS " : "";
			String msgLogRestricoes = logarRestricoes ? "RESTRIÃ‡Ã•ES" : "";
			String msgLogErrosRestricoes = (StringUtils.isNotEmpty(msgLogErros) || StringUtils.isNotEmpty(msgLogRestricoes)) ? " COM " + msgLogErros + msgLogRestricoes : "";

			// Exibe log de finalizaÃ§Ã£o
			LOG.info("Volta do Interfaceamento FINALIZADA" + msgLogErrosRestricoes);
		}

	}

	/*
	 * PESQUISA RESULTADOS GESTAM
	 */

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public LwsComunicacao obterRecepcaoResultadoGestaoAmostra() {
		return this.getLwsComunicacaoDAO().obterRecepcaoResultadoGestaoAmostra();
	}

	/*
	 * ATUALIZA RESULTADO GESTAM
	 */

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void atualizarResultadoGestaoAmostra(Integer idComunicacao) throws BaseException {
		this.getLwsComunicacaoDAO().atualizarResultadoGestaoAmostraStatusProcessada(idComunicacao);
		this.getLwsComunicacaoDAO().flush();
	}

	/**
	 * Insere uma nova comunicaÃ§Ã£o LWS processada com sucesso
	 * 
	 * @param seqComunicacao
	 * @throws BaseException
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void inserirLwsComunicacaoProcessadaSucesso(Integer seqComunicacao) throws BaseException {

		final LwsComunicacao lwsComunicacaoProcessadaComSucesso = new LwsComunicacao();

		lwsComunicacaoProcessadaComSucesso.setIdOrigem(this.getExamesFacade().obterIdModuloLisHis());
		lwsComunicacaoProcessadaComSucesso.setIdDestino(this.getExamesFacade().obterIdModuloGestaoAmostra());
		lwsComunicacaoProcessadaComSucesso.setTipoComunicacao(DominioLwsTipoComunicacao.PROPOSTA_PEDIDO_CARGA_EXAMES); // 4
		lwsComunicacaoProcessadaComSucesso.setSeqComunicacao(seqComunicacao); // ComunicaÃ§Ã£o atual
		lwsComunicacaoProcessadaComSucesso.setDataHora(new Date());
		lwsComunicacaoProcessadaComSucesso.setCodResposta(DominioLwsCodigoResposta.A00);
		lwsComunicacaoProcessadaComSucesso.setStatus(DominioLwsTipoStatusTransacao.NAO_PROCESSADA); // 0

		// Insere uma nova comunicaÃ§Ã£o LWS processada com SUCESSO
		this.getExamesFacade().inserirLwsComunicacao(lwsComunicacaoProcessadaComSucesso);
		this.getLwsComunicacaoDAO().flush();

	}

	/**
	 * Insere uma nova comunicaÃ§Ã£o LWS processada mas com restriÃ§Ãµes
	 * 
	 * @param seqComunicacao
	 * @param voVariaveisPacote
	 * @throws BaseException
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void inserirLwsComunicacaoProcessadaRestricoes(Integer seqComunicacao, VoltarProtocoloUnicoVO voVariaveisPacote) throws BaseException {

		final LwsComunicacao lwsComunicacaoProcessadaComRestricoes = new LwsComunicacao();

		lwsComunicacaoProcessadaComRestricoes.setIdOrigem(this.getExamesFacade().obterIdModuloLisHis()); // 90
		lwsComunicacaoProcessadaComRestricoes.setIdDestino(this.getExamesFacade().obterIdModuloGestaoAmostra()); // 99
		lwsComunicacaoProcessadaComRestricoes.setTipoComunicacao(DominioLwsTipoComunicacao.PROPOSTA_PEDIDO_CARGA_EXAMES); // 4
		lwsComunicacaoProcessadaComRestricoes.setSeqComunicacao(seqComunicacao); // ComunicaÃ§Ã£o atual
		lwsComunicacaoProcessadaComRestricoes.setDataHora(new Date());
		lwsComunicacaoProcessadaComRestricoes.setCodResposta(DominioLwsCodigoResposta.A01);

		// ObservaÃ§Ã£o com ALERTAS
		String observacao = voVariaveisPacote.getMensagemAlerta() + voVariaveisPacote.getLocalizacaoAlerta();
		lwsComunicacaoProcessadaComRestricoes.setObservacao(observacao);

		lwsComunicacaoProcessadaComRestricoes.setStatus(DominioLwsTipoStatusTransacao.NAO_PROCESSADA); // 0

		// Insere uma nova comunicaÃ§Ã£o LWS processada mas com RESTRIÃ‡Ã•ES
		this.getExamesFacade().inserirLwsComunicacao(lwsComunicacaoProcessadaComRestricoes);
		this.getLwsComunicacaoDAO().flush();

	}

	/*
	 * PROCEDURES
	 */

	/**
	 * ORADB PROCEDURE AELP_VOLTA_PROT_UNIC Atualiza o AGHU com as informaÃ§Ãµes da GestÃ£o da Amostra (GESTAM) Obs. A assinatura do mÃ©todo foi modificada para supir as necessidades
	 * do scheduler e simulaÃ§Ã£o das variÃ¡veis de pacote do PL-SQL
	 * 
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void voltarProtocoloUnico(LwsComunicacao lwsComunicacao, VoltarProtocoloUnicoVO voVariaveisPacote, String nomeMicrocomputador)
	throws BaseException {

		try {

			final Integer pIdComunicacao = lwsComunicacao.getIdComunicacao();

			// ObtÃ©m parÃ¢metro final ParametrosVoltarProtocoloUnicoVO parametrosVo = new ParametrosVoltarProtocoloUnicoVO();
			this.obterParametro(voVariaveisPacote, lwsComunicacao);

			/*
			 * ObtÃ©m seq da comunicaÃ§Ã£o final LwsComunicacao lwsComunicacaoSeqComunicacao =
			 * this.getLwsComunicacaoDAO().obterComunicacaoPorIdOrigemDestino(lwsComunicacao.getIdComunicacao(), lwsComunicacao.getIdOrigem(), lwsComunicacao.getIdDestino());
			 * if(lwsComunicacaoSeqComunicacao == null){ throw new ApplicationBusinessException(VoltarProtocoloUnicoBeanRNExceptionCode.AEL_03122); }
			 */

			// ObtÃ©m o contador de amostras
			final Integer contadorAmostras = this.getLwsComAmostraDAO().pesquisarLwsComAmostraPorIdComunicacaoCount(pIdComunicacao);

			// Amostras NÃƒO encontradas
			if (contadorAmostras == 0) {

				lwsComunicacao.setStatus(DominioLwsTipoStatusTransacao.PROCESSADA);
				/*
				 * UPDATE LWS_COMUNICACAO com situaÃ§Ã£o PROCESSADA
				 */
				this.getExamesFacade().atualizarLwsComunicacao(lwsComunicacao);
				this.getLwsComunicacaoDAO().flush();

				voVariaveisPacote.setPodeGravar(false);

				String observacao = "AEL-03126 - ComunicaÃ§Ã£o " + pIdComunicacao + " sem amostras.";

				throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

			} else {

				lwsComunicacao.setStatus(DominioLwsTipoStatusTransacao.PROCESSADA);
				/*
				 * UPDATE LWS_COMUNICACAO com situaÃ§Ã£o PROCESSADA
				 */
				this.getExamesFacade().persistirLwsComunicacao(lwsComunicacao);
				this.getLwsComunicacaoDAO().flush();

				voVariaveisPacote.setMensagemAlerta(null);
				voVariaveisPacote.setLocalizacaoAlerta(null);

				/*
				 * Chamada PROCEDURE AELP_PROC_AMOSTRAS
				 */
				this.processarAmostras(lwsComunicacao, voVariaveisPacote, nomeMicrocomputador);

				/*
				 * A migraÃ§Ã£o do cÃ³digo neste ponto no AGHU foi adaptada no mÃ©todo principal do Scheduler Tal trecho no AGH diz respeito a lÃ³gica de agendamento e mensagens de erro
				 * na finalizaÃ§Ã£o do processamento de resultados
				 */
			}

		} catch (VoltarProtocoloUnicoErroLocalizado e) {
			this.ctx.setRollbackOnly(); // ROLLBACK
			throw e;
		} catch (VoltarProtocoloUnicoErroVerificado e) {
			this.ctx.setRollbackOnly(); // ROLLBACK
			throw e;
		} catch (Exception e) {
			voVariaveisPacote.setPodeGravar(false);
			this.ctx.setRollbackOnly(); // ROLLBACK
			throw new VoltarProtocoloUnicoErroVerificado(e, VoltarProtocoloUnicoBeanRNExceptionCode.ERRO_AO_TENTAR_VOLTAR_AMOSTRA_GESTAM);
		}

		// Grava todas informaÃ§Ãµes processadas
		this.flush();

	}

	/**
	 * ORADB PROCEDURE AELP_OBTEM_PARAMETRO ObtÃ©m parÃ¢metros do sistema para o processamento GESTAM
	 * 
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	protected void obterParametro(VoltarProtocoloUnicoVO parametrosVo, LwsComunicacao lwsComunicacao) throws BaseException {

		CoreUtil.validaParametrosObrigatorios(parametrosVo);

		try {

			AghParametros parametroSituacaoExecutando = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);
			if (parametroSituacaoExecutando != null) {
				parametrosVo.setSituacaoExecutando(parametroSituacaoExecutando.getVlrTexto());
			}

			AghParametros parametroAreaExecutora = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
			if (parametroAreaExecutora != null) {
				parametrosVo.setAreaExecutora(parametroAreaExecutora.getVlrTexto());
			}

			AghParametros parametroSituacaoLiberado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
			if (parametroSituacaoLiberado != null) {
				parametrosVo.setSituacaoLiberado(parametroSituacaoLiberado.getVlrTexto());
			}

			AghParametros parametroSituacaoCancelado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
			if (parametroSituacaoCancelado != null) {
				parametrosVo.setSituacaoCancelado(parametroSituacaoCancelado.getVlrTexto());
			}

			AghParametros parametroMocCancelaDept = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOC_CANCELA_DEPT);
			if (parametroSituacaoCancelado != null) {
				parametrosVo.setMocCancelaDept(parametroMocCancelaDept.getVlrTexto());
			}

		} catch (Exception e) {

			String observacao = "AEL-03025 - Erro verificado: " + e.getMessage();
			throw new VoltarProtocoloUnicoErroVerificado(observacao, e, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_03025);
		}

	}

	/**
	 * ORADB PROCEDURE AELP_PROC_AMOSTRAS
	 * 
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	protected void processarAmostras(LwsComunicacao lwsComunicacao, final VoltarProtocoloUnicoVO voVariaveisPacote, String nomeMicrocomputador)
	throws BaseException {

		/**
		 * ATENÃ‡ÃƒO: Neste ponto o AGH considera o SAVEPOINT salva_comunicacao;
		 */

		Integer idComunicacao = lwsComunicacao.getIdComunicacao();
		// Integer seqComunicacao = lwsComunicacao.getSeqComunicacao();

		voVariaveisPacote.setExameSemAmostra(false);

		voVariaveisPacote.setPodeGravar(true);

		List<LwsComAmostra> listLwsComAmostra = this.getLwsComAmostraDAO().listarComAmostraPorIdComunicacao(idComunicacao);

		Integer aghSolicitacao = null; // v_agh_solicitacao
		Integer aghAmostra = null;
		AelAmostras contaAmostra = null;
		Long contadorExamesAgh;

		List<LwsComSolicitacaoExame> listLwsComSolicitacaoExame = null;

		for (LwsComAmostra regAmostrasComunicacao : listLwsComAmostra) {

			try {

				String codAmostra = regAmostrasComunicacao.getCodigoAmostra();
				if (codAmostra.length() >= 3) {
					aghAmostra = obterAmostraUtil(codAmostra);
					aghSolicitacao = obterSolicitacaoUtil(codAmostra);

				}

				contaAmostra = this.getAelAmostrasDAO().buscarAmostrasPorId(aghSolicitacao, aghAmostra.shortValue());

				if (contaAmostra != null) {

					List<String> listSitCodigo = new ArrayList<String>();

					listSitCodigo.add(voVariaveisPacote.getSituacaoExecutando());
					listSitCodigo.add(voVariaveisPacote.getAreaExecutora());

					contadorExamesAgh = this.getAelAmostraItemExamesDAO().obterCountIse(aghSolicitacao, aghAmostra, listSitCodigo);

					if (contadorExamesAgh == 0) {

						listLwsComSolicitacaoExame = this.getLwsComSolicitacaoExameDAO().pesquisarLwsComSolicitacaoExame(idComunicacao, regAmostrasComunicacao.getId());
						AelMateriaisAnalises materialAnalise = null;

						for (LwsComSolicitacaoExame LwsComSolic : listLwsComSolicitacaoExame) {
							Integer lwsExameLis = Integer.valueOf(LwsComSolic.getLwsComAmostra().getMaterialLis());// NotNull
							materialAnalise = this.getLwsComSolicitacaoExameDAO().obterAelMateriaisAnalisesPorLwsExameLis(lwsExameLis);

							// v_contador_exames_sem_amostra
							if (materialAnalise == null) {

								if (voVariaveisPacote.isPodeGravar()) {
									// ROLLBACK TO salva_comunicacao;
									voVariaveisPacote.setPodeGravar(false);
								}

								String observacao = "AEL-03117 - SolicitaÃ§Ã£o-Amostra: " + aghSolicitacao + "-" + aghAmostra + _DA_COMUNICACAO_ + idComunicacao
								+ " - nÃ£o existem exames em execuÃ§Ã£o.";

								throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

							} else {

								voVariaveisPacote.setExameSemAmostra(true);

							}
						}

					}// FIM IF contadorExamesAgh

				} else {

					if (voVariaveisPacote.isPodeGravar()) {
						// ROLLBACK TO salva_comunicacao;
						voVariaveisPacote.setPodeGravar(false);
					}

					String observacao = "AEL-03118 - SolicitaÃ§Ã£o-Amostra: " + aghSolicitacao + "-" + aghAmostra + _DA_COMUNICACAO_ + idComunicacao
					+ " - NÃ£o existe esta amostra no AGHU.";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				}

				listLwsComSolicitacaoExame = this.getLwsComSolicitacaoExameDAO().pesquisarLwsComSolicitacaoExame(idComunicacao, regAmostrasComunicacao.getId());

				// CURSOR c_contador_exames_lws
				if (listLwsComSolicitacaoExame != null && !listLwsComSolicitacaoExame.isEmpty()) {

					/*
					 * Chamada PROCEDURE AELP_PROC_EXAMES
					 */
					this.processarExames(lwsComunicacao, regAmostrasComunicacao, aghSolicitacao, aghAmostra.shortValue(), voVariaveisPacote, nomeMicrocomputador);

				} else {

					if (voVariaveisPacote.isPodeGravar()) {
						// ROLLBACK TO salva_comunicacao;
						voVariaveisPacote.setPodeGravar(false);
					}

					String observacao = "AEL-03120 - SolicitaÃ§Ã£o-Amostra: " + aghSolicitacao + "-" + aghAmostra + _DA_COMUNICACAO_ + idComunicacao + "- chegou sem exames.";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				}

			} catch (Exception e) { // Somente erros VERIFICADOS

				if (e instanceof VoltarProtocoloUnicoErroLocalizado) {
					throw (VoltarProtocoloUnicoErroLocalizado) e;
				} else if (e instanceof VoltarProtocoloUnicoErroVerificado) {
					throw (VoltarProtocoloUnicoErroVerificado) e;
				}

				// Insere erro de comunicaÃ§Ã£o
				String observacaoErro = this.obterObservacaoFormatadaErro(regAmostrasComunicacao, idComunicacao, e);

				// Exibe no LOG
				LOG.error(e.getMessage(), e);

				/*
				 * ForÃ§a o lanÃ§amento de uma BaseException para um erro verificado e delega o tratamento a classe que fez a chamada! Obs. Simula o comportamento da chamada:
				 * ROLLBACK TO salva_comunicacao;
				 */
				throw new VoltarProtocoloUnicoErroVerificado(observacaoErro, e, VoltarProtocoloUnicoBeanRNExceptionCode.ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK);
			}

		} // FIM FOR/LOOP

	}

	/**
	 * ORADB PROCEDURE AELP_PROC_EXAMES
	 * 
	 * @param lwsComunicacao
	 * @param lwsComAmostra
	 * @param pAghSolicitacao
	 * @param pAghmostra
	 * @param voVariaveisPacote
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	protected void processarExames(LwsComunicacao lwsComunicacao, LwsComAmostra lwsComAmostra, final Integer pAghSolicitacao, final Short pAghmostra,
			final VoltarProtocoloUnicoVO voVariaveisPacote, String nomeMicrocomputador) throws BaseException {

		final Integer pIdComunicacao = lwsComAmostra.getLwsComunicacao().getIdComunicacao();
		final Integer pIdAmostraLws = lwsComAmostra.getId();
		final Integer pSeqComunicacao = lwsComunicacao.getSeqComunicacao();

		// VariÃ¡veis locais
		Short vUnidadeExecutora = null;
		Long vContadorUnidadeExec = null;
		Short vUnidadeExecutoraAgh = null;
		Short vVinculo = null;
		Integer vMatricula = null;
		Short vAghItemSol = null;
		Integer vContadorResultados = 0;
		AelSitItemSolicitacoes vSituacaoSolicitacao = null;

		// Pesquisa LWS com solicitaÃ§Ã£o de exame
		List<LwsComSolicitacaoExame> listaLwsComSolicitacaoExame = this.getLwsComSolicitacaoExameDAO().pesquisarLwsComSolicitacaoExame(pIdComunicacao, pIdAmostraLws);

		for (LwsComSolicitacaoExame regExamesAmostra : listaLwsComSolicitacaoExame) {

			try {

				final String exameLis = regExamesAmostra.getExameLis();
				voVariaveisPacote.setSiglaExame(this.substr(exameLis, 0, exameLis.length() - 9));
				voVariaveisPacote.setMaterialAnalise(this.toNumber(this.substr(exameLis, exameLis.length() - 9, (exameLis.length() - 8) + 4)));
				vUnidadeExecutora = this.toNumber(this.substr(exameLis, exameLis.length() - 4, exameLis.length())).shortValue();

				// Verifica Sigla do Exame, Material de AnÃ¡lise e Unidade Executora
				if (voVariaveisPacote.getSiglaExame() == null || voVariaveisPacote.getMaterialAnalise() == null || vUnidadeExecutora == null) {

					String observacao = EXAME_ + regExamesAmostra.getExameLis() + _DA_SOLICITACAO_ + pAghSolicitacao + _DA_COMUNICACAO_ + pIdComunicacao
					+ " erro em exame_lis.";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				}

				// Verifica Sigla do Exame
				if (this.getAelExamesDAO().obterPeloId(voVariaveisPacote.getSiglaExame()) == null) {

					String observacao = "Sigla " + voVariaveisPacote.getSiglaExame() + _EXAME_ + exameLis + SOLICIT + pAghSolicitacao + COMUNIC + pIdComunicacao
					+ _NAO_EXISTE_NO_AGHU;

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
				}

				// Verifica Material de AnÃ¡lise
				if (this.getAelMaterialAnaliseDAO().obterPeloId(voVariaveisPacote.getMaterialAnalise()) == null) {

					String observacao = "Mat. Analise " + voVariaveisPacote.getMaterialAnalise() + _EXAME_ + exameLis + SOLICIT + pAghSolicitacao + COMUNIC
					+ pIdComunicacao + _NAO_EXISTE_NO_AGHU;

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				}

				// ObtÃ©m a quantidade de unidades executoras de exame
				vContadorUnidadeExec = this.getAelUnfExecutaExamesDAO().obterAelUnfExecutaExamesCount(voVariaveisPacote.getSiglaExame(), voVariaveisPacote.getMaterialAnalise(),
						vUnidadeExecutora);

				// Verifica Unidade Executora de Exames
				if (vContadorUnidadeExec == 0) {

					String observacao = "Unid. Executora " + vUnidadeExecutora + _EXAME_ + exameLis + SOLICIT + pAghSolicitacao + COMUNIC + pIdComunicacao
					+ _NAO_EXISTE_NO_AGHU;

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				} else if (vContadorUnidadeExec > 1) {

					String observacao = "Mais de uma Unid. Executora no AHUU" + vUnidadeExecutora + _EXAME_ + exameLis + SOLICIT + pAghSolicitacao + COMUNIC
					+ pIdComunicacao + ".";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				} else {

					// ObtÃ©m a unidade executora de exame AGHU
					AelUnfExecutaExames unfExecutaExames = this.getAelUnfExecutaExamesDAO().obterAelUnfExecutaExames(voVariaveisPacote.getSiglaExame(),
							voVariaveisPacote.getMaterialAnalise(), vUnidadeExecutora);
					vUnidadeExecutoraAgh = unfExecutaExames.getUnidadeFuncional().getSeq();

				}

				// ObtÃ©m vÃ­nculo e matrÃ­cula do profissional responsÃ¡vel pela liberaÃ§Ã£o
				final String profissionalLiberacao = regExamesAmostra.getProfissionalLiberacao();

				if (profissionalLiberacao != null) {
					vVinculo = this.toNumber(this.substr(profissionalLiberacao, 0, 3)).shortValue();
					vMatricula = this.toNumber(this.substr(profissionalLiberacao, 3, profissionalLiberacao.length()));
				}

				// Verifica servidor responsÃ¡vel pela liberaÃ§Ã£o
				if (profissionalLiberacao == null || this.getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(vMatricula, vVinculo) == null) {

					String observacao = "Prof. LiberaÃ§Ã£o " + vVinculo + "-" + vMatricula + _EXAME_ + exameLis + SOLICIT + pAghSolicitacao + COMUNIC + pIdComunicacao
					+ _NAO_EXISTE_NO_AGHU;

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
				}

				// ObtÃ©m o seq da solicitaÃ§Ã£o e seqp da amostra item exame
				final String solicitacao = regExamesAmostra.getSolicitacao();
				final Integer soeSeq = this.toNumber(this.substr(solicitacao, 0, 8));
				final Short seqp = this.toNumber(substr(solicitacao, 8, solicitacao.length())).shortValue();

				// CURSOR c_contador_item_solicitacao
				final AelItemSolicitacaoExames itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExameVoltarProtocoloUnicoLws(soeSeq, seqp,
						voVariaveisPacote.getSiglaExame(), voVariaveisPacote.getMaterialAnalise(), vUnidadeExecutora);

				// Caso o item de solicitaÃ§Ã£o exista
				if (itemSolicitacaoExame != null) {

					// ObtÃ©m os valores do seqp do e situaÃ§Ã£o do item de solicitaÃ§Ã£o
					vAghItemSol = seqp;
					// CURSOR c_busca_situacao_solicitacao
					vSituacaoSolicitacao = itemSolicitacaoExame.getSituacaoItemSolicitacao();

					// CURSOR c_contador_item_exame
					// ObtÃ©m amostra item exame
					AelAmostraItemExames amostraItemExames = this.getAelAmostraItemExamesDAO().obterAmostraItemExamesVoltarProtocoloUnicoLws(pAghSolicitacao, vAghItemSol,
							pAghmostra.intValue());

					// Verifica amostra item exame
					if (amostraItemExames == null) {
						String observacao = "Item " + vAghItemSol + _EXAME_ + exameLis + SOLICIT + pAghSolicitacao + COMUNIC + pIdComunicacao + _NAO_EXISTE_NO_AGHU;

						throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
					}

				} else {

					// CURSOR c_busca_item_solicitacao
					final AelItemSolicitacaoExames itemSolicitacaoExamePai = this.getAelItemSolicitacaoExameDAO()
					.obterItemSolicitacaoExameVoltarProtocoloUnicoLwsPorItemSolicitacaoExamePai(soeSeq, seqp, voVariaveisPacote.getSiglaExame(),
							voVariaveisPacote.getMaterialAnalise(), vUnidadeExecutora);

					if (itemSolicitacaoExamePai != null) {
						vAghItemSol = itemSolicitacaoExamePai.getId().getSeqp();
						vSituacaoSolicitacao = itemSolicitacaoExamePai.getSituacaoItemSolicitacao();
					}

				}

				// Valida item de solicitaÃ§Ã£o
				if (vAghItemSol == null) {
					String observacao = EXAME_ + exameLis + SOLICIT + pAghSolicitacao + COMUNIC + pIdComunicacao + " tem erro em solicitaÃ§Ã£o.";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
				}

				List<String> condicoes = Arrays.asList(voVariaveisPacote.getSituacaoExecutando(), voVariaveisPacote.getAreaExecutora());

				if (vSituacaoSolicitacao != null && condicoes.contains(vSituacaoSolicitacao.getCodigo())) {

					List<LwsComResultado> listRegResultados = this.getLwsComResultadoDAO().pesquisarLwsComResultadoPorIdComunicIdExame(pIdComunicacao, regExamesAmostra.getId());

					if (listRegResultados != null && !listRegResultados.isEmpty()) {
						vContadorResultados = listRegResultados.size();
					}

					if (vContadorResultados > 0) {

						voVariaveisPacote.setPrimeiroResultado(true);
						voVariaveisPacote.setPrimeiroResultadoOutros(true);
						voVariaveisPacote.setPrimeiroResultadoAntibio(true);

						/*
						 * Chamada PROCEDURE AELP_PROC_RESULT
						 */
						this.processarResultados(lwsComAmostra, regExamesAmostra, voVariaveisPacote, vUnidadeExecutoraAgh, vAghItemSol, nomeMicrocomputador);

					} else {
						String observacao = EXAME_ + exameLis + _DA_SOLICITACAO_ + pAghSolicitacao + _DA_COMUNICACAO_ + pIdComunicacao + " nÃ£o tem resultados.";

						throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
					}

					if (voVariaveisPacote.isPodeGravar() != false) {
						voVariaveisPacote.setPodeGravar(true);
					}

					if (voVariaveisPacote.isPodeGravar()) {
						/*
						 * Chamada PROCEDURE AELP_AJUSTES_FINAIS
						 */
						this.processarAjustesFinais(pIdComunicacao, pSeqComunicacao, pIdAmostraLws, pAghSolicitacao, pAghmostra, regExamesAmostra, vUnidadeExecutoraAgh,
								vAghItemSol, vVinculo, vMatricula, voVariaveisPacote, nomeMicrocomputador);
					}

				} else if (vSituacaoSolicitacao != null && vSituacaoSolicitacao.equals(voVariaveisPacote.getSituacaoLiberado())) {

					voVariaveisPacote.setMensagemAlerta("AEL-03399 - ");
					voVariaveisPacote.setLocalizacaoAlerta("Comunicacao " + pIdComunicacao + " enviou Exames jÃ¡ liberados no AGHU para a solicitaÃ§Ã£o - amostra " + pAghSolicitacao
							+ " - " + pAghmostra);

				}

			} catch (Exception e) { // Somente erros VERIFICADOS

				if (e instanceof VoltarProtocoloUnicoErroLocalizado) {
					throw (VoltarProtocoloUnicoErroLocalizado) e;
				} else if (e instanceof VoltarProtocoloUnicoErroVerificado) {
					throw (VoltarProtocoloUnicoErroVerificado) e;
				}

				// Insere erro de comunicaÃ§Ã£o
				String observacao = this.obterObservacaoFormatadaErro(regExamesAmostra, lwsComunicacao.getIdComunicacao(), e);

				LOG.error(e.getMessage(), e);

				/*
				 * ForÃ§a o lanÃ§amento de uma BaseException para um erro verificado e delega o tratamento a classe que fez a chamada! Obs. Simula o comportamento da chamada:
				 * ROLLBACK TO salva_comunicacao;
				 */
				throw new VoltarProtocoloUnicoErroVerificado(observacao, e, VoltarProtocoloUnicoBeanRNExceptionCode.ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK);
			}

		}

	}

	/**
	 * ORADB PROCEDURE AELP_AJUSTES_FINAIS
	 * 
	 * @param pIdComunicacao
	 * @param pSeqComunicacao
	 * @param pIdAmostraLws
	 * @param pAghSolicitacao
	 * @param pAghAmostra
	 * @param regExamesAmostra
	 * @param v_unidade_executora_agh
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength"})
	protected void processarAjustesFinais(final Integer pIdComunicacao, final Integer pSeqComunicacao, final Integer pIdAmostraLws, final Integer pAghSolicitacao,
			final Short pAghAmostra, final LwsComSolicitacaoExame regExamesAmostra, final Short pUnidadeExecutoraAgh, final Short pAghItemSolicitacao, final Short pVinculo,
			final Integer pMatricula, final VoltarProtocoloUnicoVO voVariaveisPacote, String nomeMicrocomputador) throws BaseException {

		// VariÃ¡veis AGH
		Integer vMatAssinaturaEletronica = null;
		Short vVincAssinaturaEletronica = null;
		final String pExameLis = regExamesAmostra.getExameLis().trim();

		try {

			final Long vContAssinaturaEletronica = getAelServidorUnidAssinaEletDAO().pesquisarServidorUnidAssinaEletPorUnidadeFuncionalCount(pUnidadeExecutoraAgh);

			if (vContAssinaturaEletronica > 1) {

				String observacao = "Mais de uma assin. eletr. para exame " + pExameLis + _DA_SOLICITACAO_ + pAghSolicitacao + _DA_COMUNICACAO_ + pIdComunicacao + ".";

				throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

			} else if (vContAssinaturaEletronica == 1) {

				List<AelServidorUnidAssinaElet> listaServidorUnidAssinaElet = getAelServidorUnidAssinaEletDAO().pesquisarServidorUnidAssinaEletPorUnidadeFuncional(
						pUnidadeExecutoraAgh);
				final AelServidorUnidAssinaElet servidorUnidAssinaElet = listaServidorUnidAssinaElet.get(0);

				RapServidores servidor = servidorUnidAssinaElet.getServidor();
				vMatAssinaturaEletronica = servidor.getId().getMatricula();
				vVincAssinaturaEletronica = servidor.getId().getVinCodigo();

			}

			// Caso nenhum erro ocorra autoriza a gravaÃ§Ã£o
			if (voVariaveisPacote != null && Boolean.TRUE.equals(voVariaveisPacote.isPodeGravar())) {

				if ((vMatAssinaturaEletronica == null || vVincAssinaturaEletronica == null) && Boolean.FALSE.equals(regExamesAmostra.getListaContagem())
						&& !DominioLwsTipoLiberacao.M.equals(regExamesAmostra.getTipoLiberacao())) {

					AelItemSolicitacaoExames itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(pAghSolicitacao, pAghItemSolicitacao);

					// Atualiza item de solicitaÃ§Ã£o de exame com situaÃ§Ã£o executando
					AelSitItemSolicitacoes aelSitItemSolicitacoes = new AelSitItemSolicitacoes();
					aelSitItemSolicitacoes.setCodigo(voVariaveisPacote.getSituacaoExecutando());

					itemSolicitacaoExame.setSituacaoItemSolicitacao(aelSitItemSolicitacoes);

					/*
					 * UPDATE AEL_ITEM_SOLICITACAO_EXAMES com a situaÃ§Ã£o QUE ESTÃ� EXECUTANDO
					 */
					this.getSolicitacaoExameFacade().atualizar(itemSolicitacaoExame, nomeMicrocomputador);

				} else {

					if (Boolean.TRUE.equals(regExamesAmostra.getListaContagem()) && DominioLwsTipoLiberacao.M.equals(regExamesAmostra.getTipoLiberacao())) {
						RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
						
						AelItemSolicitacaoExames itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(pAghSolicitacao, pAghItemSolicitacao);

						AelSitItemSolicitacoes aelSitItemSolic = new AelSitItemSolicitacoes();
						aelSitItemSolic.setCodigo(voVariaveisPacote.getSituacaoLiberado());

						itemSolicitacaoExame.setSituacaoItemSolicitacao(aelSitItemSolic);

						/*
						 * Atualiza item de solicitaÃ§Ã£o de exame com o servidor responsÃ¡vel Obs. O usuÃ¡rio LWSCOMM nÃ£o estÃ¡ sendo utilizado no AGHU, assim a responsabilidade da
						 * liberaÃ§Ã£o do exame Ã© do superusuÃ¡rio AGHU (injetado na facade)
						 */
						itemSolicitacaoExame.setServidorResponsabilidade(servidorLogado);

						/*
						 * UPDATE AEL_ITEM_SOLICITACAO_EXAMES com situaÃ§Ã£o LIBERADO
						 */
						this.getSolicitacaoExameFacade().atualizar(itemSolicitacaoExame, nomeMicrocomputador);

					} else {

						AelItemSolicitacaoExames itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(pAghSolicitacao, pAghItemSolicitacao);

						AelSitItemSolicitacoes aelSitItemSolic = new AelSitItemSolicitacoes();
						aelSitItemSolic.setCodigo(voVariaveisPacote.getSituacaoLiberado());

						itemSolicitacaoExame.setSituacaoItemSolicitacao(aelSitItemSolic);

						// Atualiza item de solicitaÃ§Ã£o de exame com o servidor responsÃ¡vel com assinatura eletrÃ´nica
						RapServidores servidorResponsabilidade = this.getRegistroColaboradorFacade().obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
								vMatAssinaturaEletronica, vVincAssinaturaEletronica);
						itemSolicitacaoExame.setServidorResponsabilidade(servidorResponsabilidade);

						/*
						 * UPDATE AEL_ITEM_SOLICITACAO_EXAMES com situaÃ§Ã£o LIBERADO
						 */
						this.getSolicitacaoExameFacade().atualizar(itemSolicitacaoExame, nomeMicrocomputador);
					}

				}

			}

			// Verifica se o exame NÃƒO tem amosta
			if (Boolean.FALSE.equals(voVariaveisPacote.isExameSemAmostra())) {

				AelAmostraItemExames amostraItemExame = this.getAelAmostraItemExamesDAO().obterPorChavePrimaria(pAghSolicitacao, pAghItemSolicitacao, pAghSolicitacao,
						pAghAmostra.intValue());

				if (amostraItemExame != null) {
					// Atualiza amostra item exame com o equipamento
					amostraItemExame.setAelEquipamentosExecutado(voVariaveisPacote.getEquipamento());
					this.getExamesFacade().atualizarAelAmostraItemExames(amostraItemExame, true, true, nomeMicrocomputador);
				}
			}

		} catch (Exception e) { // Somente erros VERIFICADOS

			if (e instanceof VoltarProtocoloUnicoErroLocalizado) {
				throw (VoltarProtocoloUnicoErroLocalizado) e;
			} else if (e instanceof VoltarProtocoloUnicoErroVerificado) {
				throw (VoltarProtocoloUnicoErroVerificado) e;
			}

			// Insere erro de comunicaÃ§Ã£o
			String observacaoErro = this.obterObservacaoFormatadaErro(regExamesAmostra, pIdComunicacao, e);

			LOG.error(e.getMessage(), e);

			/*
			 * ForÃ§a o lanÃ§amento de uma BaseException para um erro verificado e delega o tratamento a classe que fez a chamada! Obs. Simula o comportamento da chamada: ROLLBACK
			 * TO salva_comunicacao;
			 */
			throw new VoltarProtocoloUnicoErroVerificado(observacaoErro, e, VoltarProtocoloUnicoBeanRNExceptionCode.ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK);
		}

	}

	/**
	 * ORADB PROCEDURE AELP_PROC_RESULT Processa os resultados de exames
	 * 
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	protected void processarResultados(LwsComAmostra lwsComAmostra, LwsComSolicitacaoExame lwsComSolicitacaoExame, final VoltarProtocoloUnicoVO voVariaveisPacote,
			Short pUnidadeExecutoraAgh, Short aghItemSolic, String nomeMicrocomputador) throws BaseException {

		final Integer idComunicacao = lwsComAmostra.getLwsComunicacao().getIdComunicacao(); // notNull
		// final Integer seqComunicacao = lwsComAmostra.getLwsComunicacao().getSeqComunicacao();
		final Integer idExame = lwsComSolicitacaoExame.getId();
		final String setorLis = lwsComSolicitacaoExame.getSetorLis();
		final String exameLis = lwsComSolicitacaoExame.getExameLis();
		// final String aghAmostra = lwsComAmostra.getCodigoAmostra();
		final String aghSolicitacao = lwsComAmostra.getSolicitacao();
		// final Short unidadeExecutora = unfSeq;

		// VariÃ¡veis
		Boolean cancelaItemDependente = null;
		List<LwsComAntibiograma> contadorAntibio = null;

		List<LwsComResultado> listRegResultados = this.getLwsComResultadoDAO().pesquisarLwsComResultadoPorIdComunicIdExame(idComunicacao, idExame);

		for (LwsComResultado regResultados : listRegResultados) {

			try {

				if (voVariaveisPacote.isPrimeiroResultado()) {

					voVariaveisPacote.setPrimeiroResultado(false);
					voVariaveisPacote.setPrimeiroResultadoOutros(true);
					voVariaveisPacote.setPrimeiroResultadoAntibio(true);

					String driverId = regResultados.getDriverid();

					List<AelEquipamentos> contadorEquipamentos = this.getAelEquipamentosDAO().pesquisarEquipamentosProcResult(Short.valueOf(setorLis), driverId);

					final String detalhamentoMensagemErroEquipamento = this.obterDetalhamentoMensagemErroEquipamento(setorLis, driverId);

					if (contadorEquipamentos == null) {

						String observacao = "Equipamento " + detalhamentoMensagemErroEquipamento + " do resultado " + regResultados.getId() + COMMA_EXAME + exameLis
						+ COMMA_SOLICITACAO + aghSolicitacao + COMMA_COMUNICACAO + idComunicacao + ", nÃ£o existe ou estÃ¡ inativo no AGHU.";

						throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

					} else if (contadorEquipamentos != null && contadorEquipamentos.size() > 1) {

						String observacao = "Mais de um equipamento " + detalhamentoMensagemErroEquipamento + " do resultado " + regResultados.getId() + COMMA_EXAME + exameLis
						+ COMMA_SOLICITACAO + aghSolicitacao + COMMA_COMUNICACAO + idComunicacao + " ativo.";

						throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
					}// else{
					//

					// List<AelEquipamentos> listEquipamentos= this.getAelEquipamentosDAO().pesquisaEquipamentosProcResult(setorLis, driverId);
					// AelEquipamentos v_equipamento = listEquipamentos!=null?listEquipamentos.get(0):null;

					// }

				}

				AelCampoLaudo contadorDependente = this.getAelCampoLaudoDAO().obterCampoLaudoPorSeq(Integer.valueOf(regResultados.getParametroLis()));

				if (contadorDependente != null) {

					cancelaItemDependente = contadorDependente.getCancelaItemDept();

					if (cancelaItemDependente && DominioLwsTipoResultado.QUALITATIVO.equals(regResultados.getTipoResultado()) && "S".equalsIgnoreCase(regResultados.getValor1())) {

						cancelaItemDependente = null;

						List<AelItemSolicitacaoExames> listItemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().pesquisarItensDeSolicitacaoProcResult(
								Integer.valueOf(aghSolicitacao), aghItemSolic.shortValue(), voVariaveisPacote.getMocCancelaDept(), voVariaveisPacote.getSituacaoLiberado());

						for (AelItemSolicitacaoExames item : listItemSolicitacaoExame) {

							AelSitItemSolicitacoes sitItemSolicitacoes = item.getSituacaoItemSolicitacao();
							sitItemSolicitacoes.setDescricao(voVariaveisPacote.getSituacaoCancelado());
							item.setSituacaoItemSolicitacao(sitItemSolicitacoes);
							AelMotivoCancelaExames motivoCancelarExames = item.getAelMotivoCancelaExames();
							motivoCancelarExames.setDescricao(voVariaveisPacote.getMocCancelaDept());
							item.setAelMotivoCancelaExames(motivoCancelarExames);

							/*
							 * UPDATE AEL_ITEM_SOLICITACAO_EXAMES
							 */
							this.getAelItemSolicitacaoExameDAO().atualizar(item);
							this.getAelItemSolicitacaoExameDAO().flush();
						}

					}

				} // FIM IF contadorDependente

				Boolean resultadoTemAntibiograma = "S".equalsIgnoreCase(regResultados.getAntibiograma());
				if (resultadoTemAntibiograma) {

					contadorAntibio = this.getLwsComAntibiogramaDAO().pesquisarLwsComAntibiogramaPorIdResultado(regResultados.getId());

					if (contadorAntibio != null && !contadorAntibio.isEmpty()) {

						voVariaveisPacote.setPrimeiroResultadoAntibio(true);

						/*
						 * Chamada PROCEDURE AELP_PROC_RES_ANTIB
						 */
						this.processarResultadosAntibiograma(regResultados, lwsComAmostra, lwsComSolicitacaoExame, voVariaveisPacote, pUnidadeExecutoraAgh, aghItemSolic,
								nomeMicrocomputador);

					} else {

						String observacao = "Resultado " + regResultados.getId() + COMMA_EXAME + exameLis + COMMA_SOLICITACAO + aghSolicitacao + COMMA_COMUNICACAO + idComunicacao
						+ ": Antibiograma sem registros em LWS_COM_ANTIBIOGRAMA.";

						throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
					}

				} else {

					/*
					 * Chamada PROCEDURE AELP_PROC_RES_OUTR
					 */
					this.processarResultadoOutros(regResultados, lwsComSolicitacaoExame, voVariaveisPacote, pUnidadeExecutoraAgh, regResultados.getTipoResultado(), aghItemSolic,
							nomeMicrocomputador);

				}

			} catch (Exception e) { // Somente erros VERIFICADOS

				if (e instanceof VoltarProtocoloUnicoErroLocalizado) {
					throw (VoltarProtocoloUnicoErroLocalizado) e;
				} else if (e instanceof VoltarProtocoloUnicoErroVerificado) {
					throw (VoltarProtocoloUnicoErroVerificado) e;
				}

				// Insere erro de comunicaÃ§Ã£o
				String observacaoErro = this.obterObservacaoFormatadaErro(regResultados, idComunicacao, e);

				LOG.error(e.getMessage(), e);

				/*
				 * ForÃ§a o lanÃ§amento de uma BaseException para um erro verificado e delega o tratamento a classe que fez a chamada! Obs. Simula o comportamento da chamada:
				 * ROLLBACK TO salva_comunicacao;
				 */
				throw new VoltarProtocoloUnicoErroVerificado(observacaoErro, e, VoltarProtocoloUnicoBeanRNExceptionCode.ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK);
			}

		} // FIM FOR/LOOP

	}

	/**
	 * ORADB PROCEDURE AELP_PROC_RES_ANTIB Processa resultados de um antibiograma
	 * 
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	protected void processarResultadosAntibiograma(LwsComResultado lwsComResultado, LwsComAmostra lwsComAmostra, LwsComSolicitacaoExame lwsComSolicitacaoExame,
			final VoltarProtocoloUnicoVO voVariaveisPacote, Short unfSeq, Short aghItemSolic, String nomeMicrocomputador) throws BaseException {

		// VariÃ¡veis do pacote
		final String v_sigla_exame = voVariaveisPacote.getSiglaExame();
		final Integer v_material_analise = voVariaveisPacote.getMaterialAnalise();

		// ParÃ¢metros da procedure
		final Integer p_agh_solicitacao = Integer.valueOf(lwsComAmostra.getSolicitacao());
		final Integer p_id_comunicacao = lwsComResultado.getLwsComunicacao().getIdComunicacao();
		final String p_exame_lis = lwsComSolicitacaoExame.getExameLis();
		final Short p_agh_item_solicitacao = aghItemSolic;

		// VariÃ¡veis da procedure
		Integer v_ordemgerme_germe = 0;
		Integer v_ordemgerme = null;
		Integer v_versao_ativa_antibio = null;
		Integer v_seqp_resultados_exames = null;
		Integer v_ordemdroga = null;
		Integer v_grupocodificadodroga = null;
		Integer v_codificadodroga = null;

		// CURSOR C_ANTIBIO
		final Long p_id_resultado = lwsComResultado.getId();
		List<LwsComAntibiograma> c_antibio = this.getLwsComAntibiogramaDAO().pesquisarLwsComAntibiogramaPorIdResultado(p_id_resultado);

		for (LwsComAntibiograma regAntibio : c_antibio) {
			try {

				// v_primeiro_resultado_antibio
				if (voVariaveisPacote.isPrimeiroResultadoAntibio()) {

					voVariaveisPacote.setPrimeiroResultadoAntibio(false); // v_primeiro_resultado_antibio := 'N'

					// MigraÃ§Ã£o de SUBSTR(reg_antibio.cod_germe_lis,1,7) utilizado nas consultas
					final Integer substr_cod_germe_lis = this.toNumber(this.substr(regAntibio.getCodGermeLis(), 0, 7));

					// CURSOR C_CONTADOR_VERSAO_ATIVA
					List<AelParametroCamposLaudo> c_contador_versao_ativa = this.getAelParametroCamposLaudoDAO().pesquisarParametroCamposLaudoComVersaoLaudoAtiva(
							voVariaveisPacote.getSiglaExame(), voVariaveisPacote.getMaterialAnalise(), substr_cod_germe_lis);

					final Integer v_contador_versao_ativa = c_contador_versao_ativa.isEmpty() ? 0 : c_contador_versao_ativa.size();

					if (v_contador_versao_ativa == 0) {

						// DETALHAMENTO DA OBSERVAÃ‡ÃƒO. NÃƒO EXISTE NA PROCEDURE
						final String detalhamentoMensagemErroCampoLaudo = this.obterDetalhamentoMensagemErroCampoLaudo(voVariaveisPacote.getSiglaExame(),
								voVariaveisPacote.getMaterialAnalise(), substr_cod_germe_lis);

						String observacao = "Antibiograma" + regAntibio.getId() + ", resultado " + regAntibio.getLwsComResultado().getId() + COMMA_EXAME + p_exame_lis
						+ detalhamentoMensagemErroCampoLaudo + ",  solicitaÃ§Ã£o " + p_agh_solicitacao + " , comunicacao" + p_id_comunicacao + " nÃ£o tem versÃ£o ativa.";

						throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
					} else {

						// CURSOR C_VERSAO_ATIVA_ANTIBIO: Obtem o SEQP da versÃ£o ativa da versÃ£o do campo laudo para antibiograma.
						v_versao_ativa_antibio = getAelParametroCamposLaudoDAO().obterSeqpVersaoAtivaAntibiogramaInterfaceamento(voVariaveisPacote.getSiglaExame(),
								voVariaveisPacote.getMaterialAnalise(), substr_cod_germe_lis);

					}

					// CURSOR C_CONTADOR_NAO_ANULADOS
					List<AelResultadoExame> c_contador_nao_anulados = this.getAelResultadoExameDAO().pesquisarResultadosExamesAnuladosAntibiograma(p_agh_solicitacao, aghItemSolic,
							v_sigla_exame, v_material_analise, v_versao_ativa_antibio, substr_cod_germe_lis);

					final Integer v_contador_nao_anulados = c_contador_nao_anulados.isEmpty() ? 0 : c_contador_nao_anulados.size();

					// Se nÃ£o tiver resultados nÃ£o anulados
					if (v_contador_nao_anulados == 0 && voVariaveisPacote.isPodeGravar()) {

						// EntÃ£o verifica se tem resultados nÃ£o anulados independente de versÃ£o ativa

						// CURSOR C_CONTADOR_NAO_ANULADOS_SV: NÃ£o passa versÃ£o ativa do antibiograma = v_versao_ativa_antibio
						List<AelResultadoExame> c_contador_nao_anulados_sv = this.getAelResultadoExameDAO().pesquisarResultadosExamesAnuladosAntibiograma(p_agh_solicitacao,
								aghItemSolic, v_sigla_exame, v_material_analise, null, substr_cod_germe_lis);

						final Integer v_contador_nao_anulados_sv = c_contador_nao_anulados_sv.isEmpty() ? 0 : c_contador_nao_anulados_sv.size();

						if (v_contador_nao_anulados_sv > 0) {

							final List<AelResultadoExame> listaAtualizarAnulacaoLaudo = this.getAelResultadoExameDAO().pesquisarResultadosExameAtualizarAnuladosAntibiograma(
									p_agh_solicitacao, p_agh_item_solicitacao, v_sigla_exame, v_material_analise);

							for (AelResultadoExame resultadoAnulacaoLaudo : listaAtualizarAnulacaoLaudo) {
								// UPDATE AEL_RESULTADOS_EXAMES COM ANULAÃ‡ÃƒO DE LAUDO
								resultadoAnulacaoLaudo.setAnulacaoLaudo(Boolean.TRUE);
								this.getExamesFacade().atualizarAelResultadoExame(resultadoAnulacaoLaudo, nomeMicrocomputador);
							}

						}

					} else if (v_contador_nao_anulados > 0 && voVariaveisPacote.isPodeGravar()) {

						final List<AelResultadoExame> listaAtualizarAnulacaoLaudo = this.getAelResultadoExameDAO().pesquisarResultadosExameAtualizarAnuladosAntibiograma(
								p_agh_solicitacao, p_agh_item_solicitacao, v_sigla_exame, v_material_analise);

						for (AelResultadoExame resultadoAnulacaoLaudo : listaAtualizarAnulacaoLaudo) {
							// UPDATE AEL_RESULTADOS_EXAMES COM ANULAÃ‡ÃƒO DE LAUDO
							resultadoAnulacaoLaudo.setAnulacaoLaudo(Boolean.TRUE);
							this.getExamesFacade().atualizarAelResultadoExame(resultadoAnulacaoLaudo, nomeMicrocomputador);
						}

					}

					// CURSOR C_SEQP_RESULTADOS_EXAMES
					v_seqp_resultados_exames = this.getAelResultadoExameDAO().obterMaxSeqpResultadoExame(p_agh_solicitacao, aghItemSolic, voVariaveisPacote,
							v_versao_ativa_antibio, substr_cod_germe_lis);

					if (v_seqp_resultados_exames == null || v_seqp_resultados_exames == 0) {
						v_seqp_resultados_exames = 1;
					}

				} // FIM do teste de v_primeiro_resultado_antibio

				Integer v_campolaudogerme = null;
				Integer v_grupocodificadogerme = null;
				Integer v_codificadogerme = null;
				if (regAntibio.getCodGermeLis() != null) {
					v_campolaudogerme = this.toNumber(this.substr(regAntibio.getCodGermeLis(), 0, 7));
					v_grupocodificadogerme = this.toNumber(this.substr(regAntibio.getCodGermeLis(), 7, 14));
					v_codificadogerme = this.toNumber(this.substr(regAntibio.getCodGermeLis(), 14, 19));
				}

				// CURSOR C_CONTA_ORDEMGERME
				List<AelResultadoExame> c_conta_ordemgerme = this.getAelResultadoExameDAO().pesquisarResultadosExameOrdemGerme(p_agh_solicitacao, aghItemSolic, voVariaveisPacote,
						v_versao_ativa_antibio, v_campolaudogerme, v_grupocodificadogerme, v_codificadogerme);

				final Integer v_conta_ordemgerme = c_conta_ordemgerme.isEmpty() ? 0 : c_conta_ordemgerme.size();

				if (v_conta_ordemgerme > 1) {

					String observacao = "Mais de um reg. ordemGerme no antibiograma" + regAntibio.getId() + ", resultado " + regAntibio.getLwsComResultado().getId() + COMMA_EXAME
					+ p_exame_lis + ",  solicitaÃ§Ã£o " + p_agh_solicitacao + " , comunicacao" + p_id_comunicacao + ".";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				}

				if (v_conta_ordemgerme == 0) {

					v_ordemgerme_germe = v_ordemgerme_germe + 1;
					v_ordemgerme = v_ordemgerme_germe;

					if (voVariaveisPacote.isPodeGravar()) {

						// AEL_PARAMETRO_CAMPOS_LAUDO
						AelParametroCampoLaudoId paramCamposlaudoId = new AelParametroCampoLaudoId();
						paramCamposlaudoId.setVelEmaExaSigla(v_sigla_exame);
						paramCamposlaudoId.setVelEmaManSeq(v_material_analise);
						paramCamposlaudoId.setVelSeqp(v_versao_ativa_antibio);
						paramCamposlaudoId.setCalSeq(v_campolaudogerme);
						paramCamposlaudoId.setSeqp(v_ordemgerme);
						AelParametroCamposLaudo parametroCamposLaudo = this.getAelParametroCamposLaudoDAO().obterPorChavePrimaria(paramCamposlaudoId);

						if (parametroCamposLaudo != null) {

							// INSTANCIA AEL_RESULTADOS_EXAMES
							AelResultadoExame resultadoExame = new AelResultadoExame();

							// ID DE AEL_RESULTADOS_EXAMES
							AelResultadoExameId resultadoExameId = new AelResultadoExameId();
							resultadoExameId.setIseSoeSeq(p_agh_solicitacao);
							resultadoExameId.setIseSeqp(p_agh_item_solicitacao);
							resultadoExameId.setPclVelEmaExaSigla(v_sigla_exame);
							resultadoExameId.setPclVelEmaManSeq(v_material_analise);
							resultadoExameId.setPclVelSeqp(v_versao_ativa_antibio);
							resultadoExameId.setPclCalSeq(v_campolaudogerme);
							resultadoExameId.setPclSeqp(v_ordemgerme);

							resultadoExameId.setSeqp(v_seqp_resultados_exames);

							resultadoExame.setId(resultadoExameId); // SETA ID

							// AEL_ITEM_SOLICITACAO_EXAMES
							final AelItemSolicitacaoExames itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().obterPorId(p_agh_solicitacao, p_agh_item_solicitacao);
							resultadoExame.setItemSolicitacaoExame(itemSolicitacaoExame);

							resultadoExame.setParametroCampoLaudo(parametroCamposLaudo);

							// AEL_RESULTADOS_CODIFICADOS
							AelResultadoCodificadoId resultadoCodificadoId = new AelResultadoCodificadoId();
							resultadoCodificadoId.setGtcSeq(v_grupocodificadogerme);
							resultadoCodificadoId.setSeqp(v_codificadogerme);
							final AelResultadoCodificado resultadoCodificado = this.getAelResultadoCodificadoDAO().obterPorChavePrimaria(resultadoCodificadoId);
							resultadoExame.setResultadoCodificado(resultadoCodificado);

							resultadoExame.setValor(null);
							resultadoExame.setAnulacaoLaudo(Boolean.FALSE);

							// INSERIR RESULTADO 1
							this.getExamesFacade().inserirAelResultadoExame(resultadoExame);
							this.flush();

							v_seqp_resultados_exames = v_seqp_resultados_exames + 1;

						}

					}

				} else {

					// CURSOR C_ORDEMGERME
					List<AelResultadoExame> c_ordemgerme = this.getAelResultadoExameDAO().pesquisarResultadosExameOrdemGerme(p_agh_solicitacao, aghItemSolic, voVariaveisPacote,
							v_versao_ativa_antibio, v_campolaudogerme, v_grupocodificadogerme, v_codificadogerme);

					if (!c_ordemgerme.isEmpty()) {
						v_ordemgerme = c_ordemgerme.get(0).getId().getPclSeqp();
					} else {
						v_ordemgerme = null; // NULL AQUI!
					}

				}
				
				Integer v_conta_droga = 0;
				final boolean isRisValido = StringUtils.isNotBlank(regAntibio.getRis());
				
				if(isRisValido){
					
					// CURSOR C_CONTA_DROGA: ATENÃ‡ÃƒO, CAMPO LAUDO Ã‰ FEITO POR FORA EM gtc_seq
					AelCampoLaudo campoLaudo1 = this.getAelCampoLaudoDAO().obterCampoLaudoPorSeq(Integer.valueOf(regAntibio.getCodDrogaLis()));
					final Integer gtc_seq1 = campoLaudo1.getGrupoResultadoCodificado().getSeq();
					List<AelResultadoCodificado> c_conta_droga = this.getAelResultadoCodificadoDAO().pesquisarResulCodificadosPorGtcSeqDescricao(regAntibio.getRis(), gtc_seq1);
					
					v_conta_droga = c_conta_droga.isEmpty() ? 0 : c_conta_droga.size();
				}
			
				if (isRisValido && v_conta_droga > 1) {

					String observacao = "Mais de um registro droga. Antibiograma" + regAntibio.getId() + ", resultado " + regAntibio.getLwsComResultado().getId() + COMMA_EXAME
					+ p_exame_lis + ",  solicitaÃ§Ã£o " + p_agh_solicitacao + " , comunicacao" + p_id_comunicacao + ".";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
				}

				if (isRisValido && v_conta_droga == 0) {

					String observacao = "Nenhum registro droga. Exame" + p_exame_lis + _DA_SOLICITACAO_ + p_agh_solicitacao + "  da comunicacao" + p_id_comunicacao + ".";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
				} else {

					// CURSOR C_GRUPOCODIFICADODROGA: ATENÃ‡ÃƒO, CAMPO LAUDO Ã‰ FEITO POR FORA EM gtc_seq
					AelCampoLaudo campoLaudo2 = this.getAelCampoLaudoDAO().obterCampoLaudoPorSeq(Integer.valueOf(regAntibio.getCodDrogaLis()));
					final Integer gtc_seq2 = campoLaudo2.getGrupoResultadoCodificado().getSeq();
					List<AelResultadoCodificado> c_grupocodificadodroga = this.getAelResultadoCodificadoDAO().pesquisarResulCodificadosPorGtcSeqDescricao(regAntibio.getRis(),
							gtc_seq2);

					if (c_grupocodificadodroga != null && !c_grupocodificadodroga.isEmpty()) {
						v_grupocodificadodroga = c_grupocodificadodroga.get(0).getId().getGtcSeq();
						v_codificadodroga = c_grupocodificadodroga.get(0).getId().getSeqp();
					} else {
						v_grupocodificadodroga = null;
						v_codificadodroga = null;
					}

				}

				// CURSOR C_DEFINE_ORDEMDROGA
				List<AelCampoLaudoRelacionado> c_define_ordemdroga = this.getAelCampoLaudoRelacionadoDAO().pesquisarAelCampoLaudoRelacionadoDefineOrdemDroga(v_sigla_exame,
						v_material_analise, v_versao_ativa_antibio, v_campolaudogerme, v_ordemgerme, regAntibio.getCodDrogaLis());

				if (c_define_ordemdroga != null && !c_define_ordemdroga.isEmpty()) {
					v_ordemdroga = c_define_ordemdroga.get(0).getId().getPclSeqp();
				} else {
					v_ordemdroga = null; // NULL AQUI!
				}

				if (voVariaveisPacote.isPodeGravar()) {

					// AEL_PARAMETRO_CAMPOS_LAUDO
					AelParametroCampoLaudoId paramCamposlaudoId = new AelParametroCampoLaudoId();
					paramCamposlaudoId.setVelEmaExaSigla(v_sigla_exame);
					paramCamposlaudoId.setVelEmaManSeq(v_material_analise);
					paramCamposlaudoId.setVelSeqp(v_versao_ativa_antibio);
					paramCamposlaudoId.setCalSeq(Integer.valueOf(regAntibio.getCodDrogaLis()));
					paramCamposlaudoId.setSeqp(v_ordemdroga);
					AelParametroCamposLaudo parametroCamposLaudo = this.getAelParametroCamposLaudoDAO().obterPorChavePrimaria(paramCamposlaudoId);

					if (parametroCamposLaudo != null) {

						// INSTANCIA AEL_RESULTADOS_EXAMES
						AelResultadoExame resultadoExame = new AelResultadoExame();

						// ID DE AEL_RESULTADOS_EXAMES
						AelResultadoExameId resultadoExameId = new AelResultadoExameId();
						resultadoExameId.setIseSoeSeq(p_agh_solicitacao);
						resultadoExameId.setIseSeqp(p_agh_item_solicitacao);
						resultadoExameId.setPclVelEmaExaSigla(v_sigla_exame);
						resultadoExameId.setPclVelEmaManSeq(v_material_analise);
						resultadoExameId.setPclVelSeqp(v_versao_ativa_antibio);
						resultadoExameId.setPclCalSeq(Integer.valueOf(regAntibio.getCodDrogaLis()));
						resultadoExameId.setPclSeqp(v_ordemdroga);

						resultadoExameId.setSeqp(v_seqp_resultados_exames);

						resultadoExame.setId(resultadoExameId); // SETA ID

						// AEL_ITEM_SOLICITACAO_EXAMES
						final AelItemSolicitacaoExames itemSolicitacaoExame = this.getAelItemSolicitacaoExameDAO().obterPorId(p_agh_solicitacao, p_agh_item_solicitacao);
						resultadoExame.setItemSolicitacaoExame(itemSolicitacaoExame);

						resultadoExame.setParametroCampoLaudo(parametroCamposLaudo);

						// AEL_RESULTADOS_CODIFICADOS
						if (v_grupocodificadodroga != null && v_codificadodroga != null) {
							AelResultadoCodificadoId resultadoCodificadoId = new AelResultadoCodificadoId();
							resultadoCodificadoId.setGtcSeq(v_grupocodificadodroga);
							resultadoCodificadoId.setSeqp(v_codificadodroga);
							final AelResultadoCodificado resultadoCodificado = this.getAelResultadoCodificadoDAO().obterPorChavePrimaria(resultadoCodificadoId);
							resultadoExame.setResultadoCodificado(resultadoCodificado);
						}

						resultadoExame.setValor(null);
						resultadoExame.setAnulacaoLaudo(Boolean.FALSE);

						// INSERIR RESULTADO 2
						this.getExamesFacade().inserirAelResultadoExame(resultadoExame);
						this.flush();

						v_seqp_resultados_exames = v_seqp_resultados_exames + 1;

					}

				}

			} catch (Exception e) { // Somente erros VERIFICADOS

				if (e instanceof VoltarProtocoloUnicoErroLocalizado) {
					throw (VoltarProtocoloUnicoErroLocalizado) e;
				} else if (e instanceof VoltarProtocoloUnicoErroVerificado) {
					throw (VoltarProtocoloUnicoErroVerificado) e;
				}

				// Insere erro de comunicaÃ§Ã£o
				String observacaoErro = this.obterObservacaoFormatadaErro(regAntibio, p_id_comunicacao, e);

				LOG.error(e.getMessage(), e);

				/*
				 * ForÃ§a o lanÃ§amento de uma BaseException para um erro verificado e delega o tratamento a classe que fez a chamada! Obs. Simula o comportamento da chamada:
				 * ROLLBACK TO salva_comunicacao;
				 */
				throw new VoltarProtocoloUnicoErroVerificado(observacaoErro, e, VoltarProtocoloUnicoBeanRNExceptionCode.ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK);
			}
		}

	}

	/**
	 * ORADB PROCEDURE AELP_PROC_RES_OUTR Processa outros resultados
	 * 
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	protected void processarResultadoOutros(LwsComResultado lwsComResultado, LwsComSolicitacaoExame lwsComSolicitacaoExame, final VoltarProtocoloUnicoVO voVariaveisPacote,
			Short pUnidadeExecutoraAgh, DominioLwsTipoResultado pTipoResultado, Short pAghItemSolicitacao, String nomeMicrocomputador)
	throws BaseException {

		final LwsComSolicitacaoExame regExamesAmostra = lwsComResultado.getLwsComSolicitacaoExame();

		// Parametros
		final Integer pIdComunicacao = lwsComResultado.getLwsComunicacao().getIdComunicacao();
		// final Integer pSeqComunicacao = lwsComResultado.getLwsComunicacao().getSeqComunicacao();
		Integer pIdAmostraLws = lwsComSolicitacaoExame.getLwsComAmostra().getId();
		Integer pAghSolicitacao = lwsComSolicitacaoExame.getId();
		// Integer pAghAmostra = 0;
		// Integer pIdExame = 0;
		// Integer pSetorLis = 0;
		// Integer pIdAmostra = 0;
		// Short pUnidadeExecutoraAgh = 0;
		// Integer pEquipamento = 0;
		final Integer pParametroLis = Integer.valueOf(lwsComResultado.getParametroLis());
		// Integer pTipoResultado = null;
		// String pValor1 = null;
		String pExameLis = lwsComSolicitacaoExame.getExameLis().trim();
		final Integer pGrupoCodificadoLis = lwsComResultado.getGrupoCodificadoLis() != null ? Integer.valueOf(lwsComResultado.getGrupoCodificadoLis()) : null;
		final Integer pCodificadoLis = lwsComResultado.getCodificadoLis() != null ? Integer.valueOf(lwsComResultado.getCodificadoLis()) : null;
		// Short pAghItemSolicitacao = 0;

		// VariÃ¡veis
		final DominioLwsTipoResultado tipoResultado = lwsComResultado.getTipoResultado();
		// Integer vContadorEquipamentos = 0;
		// Integer vContadorVersao_ativa = 0;
		// Integer vContadorCodificados = 0;
		List<AelParametroCamposLaudo> listaVersaoAtiva = null;

		try {

			if (voVariaveisPacote.isPrimeiroResultadoOutros()) {

				// voVariaveisPacote.setPrimeiroResultadoOutros(false);

				/*
				 * NÃ£o Ã© necessÃ¡rio setar primeiros resultados outros
				 */

				listaVersaoAtiva = this.getAelParametroCamposLaudoDAO().pesquisarParametroCamposLaudoComVersaoLaudoAtiva(voVariaveisPacote.getSiglaExame(),
						voVariaveisPacote.getMaterialAnalise(), pParametroLis);

				final String detalhamentoMensagemErroCampoLaudo = this.obterDetalhamentoMensagemErroCampoLaudo(voVariaveisPacote.getSiglaExame(),
						voVariaveisPacote.getMaterialAnalise(), pParametroLis);

				// Verifica se o exame tem versÃµes ativas
				if (listaVersaoAtiva.isEmpty()) {

					String observacao = EXAME_ + pExameLis + detalhamentoMensagemErroCampoLaudo + _DA_SOLICITACAO_ + pAghSolicitacao + _DA_COMUNICACAO_ + pIdComunicacao
					+ " nÃ£o tem versÃ£o ativa.";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				} else if (listaVersaoAtiva.size() > 1) { // Verifica se o exame tem mais de uma versÃ£o ativa

					String observacao = EXAME_ + pExameLis + detalhamentoMensagemErroCampoLaudo + _DA_SOLICITACAO_ + pAghSolicitacao + _DA_COMUNICACAO_ + pIdComunicacao
					+ " mais de uma versÃ£o ativa ou campo laudo repetido.";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				} else {

					// Obtem a versÃ£o ativa. Vide: CURSOR c_versao_ativa
					final Integer versaoAtiva = this.getAelParametroCamposLaudoDAO().obterSeqpVersaoAtivaInterfaceamento(voVariaveisPacote.getSiglaExame(),
							voVariaveisPacote.getMaterialAnalise(), pParametroLis);

					// Seta SEQP da versÃ£o ativa
					voVariaveisPacote.setVersaoAtiva(versaoAtiva);

					// Obtem ocorrÃªncia do campo laudo. Vide: CURSOR c_ocorrencia_campo_laudo
					final Integer parametroCampoSeqp = this.getAelParametroCamposLaudoDAO().obterSeqpOcorrenciaCampoLaudoInterfaceamento(voVariaveisPacote.getSiglaExame(),
							voVariaveisPacote.getMaterialAnalise(), pParametroLis, versaoAtiva);

					// Seta SEQP parÃ¢metro campo laudo
					voVariaveisPacote.setParametroCampoSeqp(parametroCampoSeqp);
				}
			}

			if (pGrupoCodificadoLis == null && pCodificadoLis == null) {

				// Verifica se o tipo de resultado Ã© vÃ¡lido
				if (!DominioLwsTipoResultado.QUANTITATIVO.equals(tipoResultado) && !DominioLwsTipoResultado.QUALITATIVO.equals(tipoResultado)) {

					String observacao = "Tipo de resultado do exame " + pExameLis + _DA_SOLICITACAO_ + pAghSolicitacao + _DA_COMUNICACAO_ + pIdComunicacao
					+ " nÃ£o Ã© 1 (Quantitativo) nem 2 (Qualitativo).";

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

				}

			} else {

				if (pGrupoCodificadoLis != null && pCodificadoLis != null) {

					List<AelResultadoCodificado> listaResultadoCodificados = this.getAelResultadoCodificadoDAO().pesquisarResultadoCodificadoPorGtcSeqSeqp(pGrupoCodificadoLis,
							pCodificadoLis);

					if (listaResultadoCodificados.isEmpty()) {

						String observacao = "Codificacao de " + pExameLis + " da solicit. " + pAghSolicitacao + " da comunic. " + pIdComunicacao + _NAO_EXISTE_NO_AGHU;

						throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

					} else if (listaResultadoCodificados.size() > 1) {

						String observacao = "Mais de uma codificacao para " + pExameLis + " da solicit. " + pAghSolicitacao + " da comunic. " + pIdComunicacao + " no AGHU.";

						throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

					}

					/*
					 * else { // TIPO RESULTADO 3 NÃƒO ESTÃ� MAPEADO NO MANUAL, SOMENTE OS TIPOS 1 (Quantitativo) e 2 (Qualitativo) SÃƒO ACEITOS! tipo_resultado = 3; }
					 */

				} else {

					String observacao = "Campo nulo no exame " + pExameLis + " da amostra " + pIdAmostraLws + _DA_COMUNICACAO_ + pIdComunicacao;

					throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);
				}

			}

			if (voVariaveisPacote.isPodeGravar()) {

				/*
				 * Chamada PROCEDURE AELP_GRAVA_RES_OUTR
				 */
				this.gravarResultadoOutros(lwsComResultado, regExamesAmostra.getLwsComAmostra(), lwsComSolicitacaoExame, voVariaveisPacote, pUnidadeExecutoraAgh,
						pAghItemSolicitacao, pParametroLis, pTipoResultado, pGrupoCodificadoLis, pCodificadoLis, nomeMicrocomputador);

			}

		} catch (Exception e) { // Somente erros VERIFICADOS

			if (e instanceof VoltarProtocoloUnicoErroLocalizado) {
				throw (VoltarProtocoloUnicoErroLocalizado) e;
			} else if (e instanceof VoltarProtocoloUnicoErroVerificado) {
				throw (VoltarProtocoloUnicoErroVerificado) e;
			}

			// Insere erro de comunicaÃ§Ã£o
			String observacaoErro = this.obterObservacaoFormatadaErro(regExamesAmostra, pIdComunicacao, e);

			LOG.error(e.getMessage(), e);

			/*
			 * ForÃ§a o lanÃ§amento de uma BaseException para um erro verificado e delega o tratamento a classe que fez a chamada! Obs. Simula o comportamento da chamada: ROLLBACK
			 * TO salva_comunicacao;
			 */
			throw new VoltarProtocoloUnicoErroVerificado(observacaoErro, e, VoltarProtocoloUnicoBeanRNExceptionCode.ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK);
		}

	}

	/**
	 * ORADB PROCEDURE AELP_GRAVA_RES_OUTR Grava outros resultados
	 * 
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength" })
	protected void gravarResultadoOutros(LwsComResultado lwsComResultado, LwsComAmostra lwsComAmostra, LwsComSolicitacaoExame lwsComSolicitacaoExame,
			final VoltarProtocoloUnicoVO voVariaveisPacote, Short unfSeq, Short aghItemSolic, Integer pParametroLis, DominioLwsTipoResultado pTipoResultado,
			Integer pGrupoCodificadoLis, Integer pCodificadoLis, String nomeMicrocomputador) throws BaseException {

		Integer idComunicacao = lwsComResultado.getLwsComunicacao().getIdComunicacao(); // notNull
		// Integer seqComunicacao = lwsComResultado.getLwsComunicacao().getSeqComunicacao();
		// Integer idExame = lwsComResultado.getLwsComSolicitacaoExame().getId();
		// String setorLis = lwsComSolicitacaoExame.getSetorLis();
		String exameLis = lwsComSolicitacaoExame.getExameLis();
		// String aghAmostra = lwsComAmostra.getCodigoAmostra();
		Integer aghSolicitacao = Integer.valueOf(lwsComAmostra.getSolicitacao()); // soeSeq
		// Short unidadeExecutora = unfSeq;
		// Long idResultado = lwsComResultado.getId();

		// Integer quantResultadosValidos = 0; //v_quant_resultados_validos
		// Integer quantResultadosValidosSv = 0; //v_quant_resultados_validos_sv
		Integer seqpResultadosExames = null; // /v_seqp_resultados_exames
		Short qtdeCasasDecimais = 0; // v_qtde_casas_decimais

		AelParametroCamposLaudo parametroCamposLaudo = null;
		AelItemSolicitacaoExames itemSolicExames = null;

		try {

			/*
			 * Buscar AelParametroCamposLaudo ( para ser usado no insert ael_resultados_exames )
			 */
			AelParametroCampoLaudoId paramCamposlaudoId = new AelParametroCampoLaudoId();
			paramCamposlaudoId.setCalSeq(pParametroLis);
			paramCamposlaudoId.setVelSeqp(voVariaveisPacote.getVersaoAtiva());
			paramCamposlaudoId.setVelEmaManSeq(voVariaveisPacote.getMaterialAnalise());
			paramCamposlaudoId.setVelEmaExaSigla(voVariaveisPacote.getSiglaExame());
			paramCamposlaudoId.setSeqp(voVariaveisPacote.getParametroCampoSeqp());
			parametroCamposLaudo = this.getAelParametroCamposLaudoDAO().obterPorChavePrimaria(paramCamposlaudoId);

			Long quantidadeAmostras = this.getAelAmostraItemExamesDAO().pesquisarAmostraItemExamesPorSoeSeqSeqp(aghSolicitacao, aghItemSolic);

			List<AelResultadoExame> resultadosValidos = this.getAelResultadoExameDAO().pesquisarResultadosExameNaoAnulados(aghSolicitacao, aghItemSolic, voVariaveisPacote,
					voVariaveisPacote.getVersaoAtiva(), pParametroLis);

			/*
			 * Buscar AelItemSolicitacaoExames
			 */
			itemSolicExames = this.getAelItemSolicitacaoExameDAO().obterPorId(aghSolicitacao, aghItemSolic);

			/*
			 * Se nÃ£o tiver resultados vÃ¡lidos, EntÃ£o verificar se tem resultados validos independente de versÃ£o
			 */
			if (resultadosValidos == null || resultadosValidos.isEmpty()) {
				// v_quant_resultados_validos_sv
				List<AelResultadoExame> resultadosValidosSv = this.getAelResultadoExameDAO().pesquisarResultadosExameNaoAnulados(aghSolicitacao, aghItemSolic, voVariaveisPacote,
						null, pParametroLis);

				if (resultadosValidosSv != null && !resultadosValidosSv.isEmpty()) {

					for (AelResultadoExame resultadoExameSv : resultadosValidosSv) {

						resultadoExameSv.setAnulacaoLaudo(Boolean.TRUE);

						/*
						 * UPDATE AEL_RESULTADOS_EXAMES com ANULAÃ‡ÃƒO de laudo
						 */
						this.getExamesFacade().atualizarAelResultadoExame(resultadoExameSv, nomeMicrocomputador);
					}
				}

			} else if (resultadosValidos.size() >= quantidadeAmostras) {

				for (AelResultadoExame resultadoExame : resultadosValidos) {

					resultadoExame.setAnulacaoLaudo(Boolean.TRUE);

					/*
					 * UPDATE AEL_RESULTADOS_EXAMES com ANULAÃ‡ÃƒO de laudo
					 */
					this.getExamesFacade().atualizarAelResultadoExame(resultadoExame, nomeMicrocomputador);

				}

			}

			seqpResultadosExames = this.getAelResultadoExameDAO().obterMaxSeqpResultadoExame(aghSolicitacao, aghItemSolic, voVariaveisPacote, voVariaveisPacote.getVersaoAtiva(),
					pParametroLis);

			if (seqpResultadosExames == null) {
				seqpResultadosExames = 1;
			}

			// if(p_tipo_resultado!=3) // ATENÃ‡ÃƒO: NÃ£o existe domÃ­nio de tipo de resultado = 3
			if (pTipoResultado != null) {

				if (DominioLwsTipoResultado.QUALITATIVO.equals(pTipoResultado)) {

					/*
					 * INSERT AEL_RESULTADOS_EXAMES
					 */
					AelResultadoExameId resultadoExameId = popularResultadoExamesId(voVariaveisPacote, aghItemSolic, pParametroLis, aghSolicitacao, seqpResultadosExames);

					AelResultadoExame resultadoExame = new AelResultadoExame();
					resultadoExame.setId(resultadoExameId);
					resultadoExame.setItemSolicitacaoExame(itemSolicExames);
					// resultadoExame.setParametroCampoLaudo(parametroCamposLaudo);
					resultadoExame.setValor(null);
					resultadoExame.setAnulacaoLaudo(Boolean.FALSE);
					
					if(pGrupoCodificadoLis != null && pCodificadoLis != null)	{
						AelResultadoCodificadoId resultadoCodificadoId = new AelResultadoCodificadoId();
						resultadoCodificadoId.setGtcSeq(pGrupoCodificadoLis);
						resultadoCodificadoId.setSeqp(pCodificadoLis);
						AelResultadoCodificado resultadoCodificado = this.getAelResultadoCodificadoDAO().obterPorChavePrimaria(resultadoCodificadoId);
						 /*
						 * INSERT AEL_RESULTADOS_EXAMES	
						 */
						 resultadoExame.setResultadoCodificado(resultadoCodificado);
					}
					
					if (voVariaveisPacote.isPodeGravar()) {
						resultadoExame.setDescricao(lwsComResultado.getValor1());
					}
					this.getExamesFacade().inserirAelResultadoExame(resultadoExame);

				} else {

					/*
					 * v_conta_casas_decimais
					 */
					if (parametroCamposLaudo == null) {

						String observacao = "NÃ£o ha registro em 'ael_parametros_campos_laudos' : Exame " + exameLis + COMMA_SOLICITACAO + aghSolicitacao + COMMA_COMUNICACAO
						+ idComunicacao + ".";

						throw new VoltarProtocoloUnicoErroLocalizado(observacao, VoltarProtocoloUnicoBeanRNExceptionCode.AEL_ERRO_PROCESSAMENTO_GESTAM_ROLLBACK);

					} else {

						qtdeCasasDecimais = parametroCamposLaudo.getQuantidadeCasasDecimais();

						/*
						 * INSERT AEL_RESULTADOS_EXAMES
						 */
						AelResultadoExameId Id = popularResultadoExamesId(voVariaveisPacote, aghItemSolic, pParametroLis, aghSolicitacao, seqpResultadosExames);

						AelResultadoExame resultadoExame = new AelResultadoExame();
						resultadoExame.setId(Id);
						resultadoExame.setItemSolicitacaoExame(itemSolicExames);
						resultadoExame.setParametroCampoLaudo(parametroCamposLaudo);

						if (lwsComResultado.getValor1() != null) {
							NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
							Double valor1 = nf.parse(lwsComResultado.getValor1()).doubleValue();
							long valor = (long) (valor1 * Math.pow(10, qtdeCasasDecimais)); // power(10,v_qtde_casas_decimais)
							resultadoExame.setValor(valor);
						}

						resultadoExame.setAnulacaoLaudo(Boolean.FALSE);

						this.getExamesFacade().inserirAelResultadoExame(resultadoExame);

					}

				}

			}// else{ // ATENÃ‡ÃƒO: NÃ£o existe domÃ­nio de tipo de resultado = 3
			//
			// //Resultado codificado
			// /*
			// *Buscar AelResultadoCodificado
			// */
			// AelResultadoCodificadoId resultadoCodificadoId = new AelResultadoCodificadoId();
			//
			// resultadoCodificadoId.setGtcSeq(Integer.valueOf(p_grupo_codificado_lis));
			// resultadoCodificadoId.setSeqp(p_codificado_lis);
			// AelResultadoCodificado resultadoCodificado = this.getAelResultadoCodificadoDAO().obterPorChavePrimaria(resultadoCodificadoId);
			//
			// /*
			// * INSERT AEL_RESULTADOS_EXAMES
			// */
			// resultadoExameId = popularResultadoExamesId(voVariaveisPacote,aghItemSolic, p_parametro_lis, aghSolicitacao, seqpResultadosExames);
			//
			// AelResultadoExame resultadoExame = new AelResultadoExame();
			// resultadoExame.setId(resultadoExameId);
			// resultadoExame.setItemSolicitacaoExame(itemSolicExames);
			// resultadoExame.setParametroCampoLaudo(parametroCamposLaudo);
			// resultadoExame.setValor(null);
			// resultadoExame.setAnulacaoLaudo(Boolean.FALSE);
			// resultadoExame.setResultadoCodificado(resultadoCodificado);
			//
			//
			// this.getAelResultadoExameRN().inserir(resultadoExame);
			// }

		} catch (Exception e) { // Somente erros VERIFICADOS

			if (e instanceof VoltarProtocoloUnicoErroLocalizado) {
				throw (VoltarProtocoloUnicoErroLocalizado) e;
			} else if (e instanceof VoltarProtocoloUnicoErroVerificado) {
				throw (VoltarProtocoloUnicoErroVerificado) e;
			}

			// Insere erro de comunicaÃ§Ã£o
			String observacaoErro = this.obterObservacaoFormatadaErro(lwsComSolicitacaoExame, idComunicacao, e);

			LOG.error(e.getMessage(), e);

			/*
			 * ForÃ§a o lanÃ§amento de uma BaseException para um erro verificado e delega o tratamento a classe que fez a chamada! Obs. Simula o comportamento da chamada: ROLLBACK
			 * TO salva_comunicacao;
			 */
			throw new VoltarProtocoloUnicoErroVerificado(observacaoErro, e, VoltarProtocoloUnicoBeanRNExceptionCode.ERRO_VERIFICADO_PROCESSAMENTO_GESTAM_ROLLBACK);
		}

	}

	/**
	 * Popula um id de AelResultadoExame
	 * 
	 * @param voVariaveisPacote
	 * @param aghItemSolic
	 * @param p_parametro_lis
	 * @param aghSolicitacao
	 * @param seqpResultadosExames
	 * @return
	 */
	private AelResultadoExameId popularResultadoExamesId(final VoltarProtocoloUnicoVO voVariaveisPacote, Short aghItemSolic, Integer p_parametro_lis, Integer aghSolicitacao,
			Integer seqpResultadosExames) {

		AelResultadoExameId resultadoExameId;

		resultadoExameId = new AelResultadoExameId();
		resultadoExameId.setIseSoeSeq(aghSolicitacao);
		resultadoExameId.setIseSeqp(aghItemSolic);
		resultadoExameId.setPclVelEmaExaSigla(voVariaveisPacote.getSiglaExame());
		resultadoExameId.setPclVelEmaManSeq(voVariaveisPacote.getMaterialAnalise());
		resultadoExameId.setPclVelSeqp(voVariaveisPacote.getVersaoAtiva());
		resultadoExameId.setPclCalSeq(p_parametro_lis);
		resultadoExameId.setPclSeqp(voVariaveisPacote.getParametroCampoSeqp());
		resultadoExameId.setSeqp(seqpResultadosExames);

		return resultadoExameId;
	}

	/*
	 * PERSISTÃŠNCIA DO LOG DE ERROS GESTAM
	 */

	/**
	 * ORADB PROCEDURE AELP_INS_COM_ERRO
	 * 
	 * @param seqComunicacao
	 * @param observacao
	 * @return
	 * @throws BaseException
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public LwsComunicacao inserirComErro(final Integer seqComunicacao, final String observacao) throws BaseException {

		LwsComunicacao lwsComunicacaoErro = new LwsComunicacao();

		lwsComunicacaoErro.setIdOrigem(this.getExamesFacade().obterIdModuloGestaoAmostra());
		lwsComunicacaoErro.setIdDestino(this.getExamesFacade().obterIdModuloLisHis());
		lwsComunicacaoErro.setTipoComunicacao(DominioLwsTipoComunicacao.PROPOSTA_PEDIDO_CARGA_EXAMES); // 4
		lwsComunicacaoErro.setSeqComunicacao(seqComunicacao); // ComunicaÃ§Ã£o atual
		lwsComunicacaoErro.setDataHora(new Date());
		lwsComunicacaoErro.setCodResposta(DominioLwsCodigoResposta.E99); // Erro Desconhecido
		lwsComunicacaoErro.setObservacao(observacao);
		lwsComunicacaoErro.setStatus(DominioLwsTipoStatusTransacao.NAO_PROCESSADA); // 0

		this.getExamesFacade().persistirLwsComunicacao(lwsComunicacaoErro);
		this.getLwsComunicacaoDAO().flush();

		return lwsComunicacaoErro;
	}

	/*
	 * MÃ‰TODOS PARA FORMATAÃ‡ÃƒO DA OBSERVAÃ‡ÃƒO NO LOG DE ERROS GESTAM
	 */

	/**
	 * ObtÃ©m a observaÃ§Ã£o formatada de um erro verificado ou genÃ©rico
	 * 
	 * @param idComunicacao
	 * @param e
	 * @return
	 */
	private String obterObservacaoFormatadaErro(Integer idComunicacao, Throwable e) {

		// Quando o erro Ã© LOCALIZADO
		if (e instanceof VoltarProtocoloUnicoErroLocalizado) {

			// Retorna mensagem de ERRO LOCALIZADO
			return ((VoltarProtocoloUnicoErroLocalizado) e).getObservacao();

		} else if (e instanceof VoltarProtocoloUnicoErroVerificado) {

			// Quando o erro Ã© VERIFICADO
			VoltarProtocoloUnicoErroVerificado erroVerificado = (VoltarProtocoloUnicoErroVerificado) e;

			String observacao = "Comunicacao " + idComunicacao;
			if (erroVerificado.getObservacao() != null) {
				observacao = erroVerificado.getObservacao();
			}

			// ATENÃ‡ÃƒO: Sempre passar a ExceptionOriginal do erro verificado
			String observacaoErroVerificado = this.obterObservacaoErroVerificado(erroVerificado.getExceptionOriginal());

			// Retorna mensagem de ERRO VERIFICADO
			return observacao + observacaoErroVerificado;

		} else {

			// ERRO GENÃ‰RICO!
			String observacao = "ERRO GENERICO na comunicaÃ§Ã£o " + idComunicacao;
			String observacaoErroVerificado = this.obterObservacaoErroVerificado(e);

			// Retorna mensagem de ERRO GENÃ‰RICO
			return observacao + observacaoErroVerificado;
		}

	}

	/**
	 * ObtÃ©m a observaÃ§Ã£o formatada de um erro verificado ou genÃ©rico atravÃ©s de uma instÃ¢ncia de LwsComAmostra
	 * 
	 * @param exame
	 * @param regAmostrasComunicacao
	 * @param idComunicacao
	 * @param e
	 * @return
	 */
	private String obterObservacaoFormatadaErro(LwsComAmostra regAmostrasComunicacao, Integer idComunicacao, Exception e) {

		String solicitacao = regAmostrasComunicacao.getSolicitacao();
		String amostra = regAmostrasComunicacao.getCodigoAmostra();

		String observacao = "SolicitaÃ§Ã£o-Amostra: " + solicitacao + "-" + amostra + _DA_COMUNICACAO_ + idComunicacao;
		String observacaoErroVerificado = this.obterObservacaoErroVerificado(e);

		return observacao + observacaoErroVerificado;
	}

	/**
	 * ObtÃ©m a observaÃ§Ã£o formatada de um erro verificado ou genÃ©rico atravÃ©s de uma instÃ¢ncia de LwsComSolicitacaoExame
	 * 
	 * @param regExamesAmostra
	 * @param idComunicacao
	 * @param e
	 * @return
	 */
	private String obterObservacaoFormatadaErro(LwsComSolicitacaoExame regExamesAmostra, Integer idComunicacao, Exception e) {

		String exame = regExamesAmostra.getExameLis().trim();
		String solicitacao = regExamesAmostra.getSolicitacao();

		String observacao = EXAME_ + exame + _DA_SOLICITACAO_ + solicitacao + _DA_COMUNICACAO_ + idComunicacao;
		String observacaoErroVerificado = this.obterObservacaoErroVerificado(e);

		return observacao + observacaoErroVerificado;

	}

	/**
	 * ObtÃ©m a observaÃ§Ã£o formatada de um erro verificado ou genÃ©rico atravÃ©s de uma instÃ¢ncia de LwsComResultado
	 * 
	 * @param regExamesAmostra
	 * @param idComunicacao
	 * @param e
	 * @return
	 */
	private String obterObservacaoFormatadaErro(LwsComResultado regResultados, Integer idComunicacao, Exception e) {

		Long resultadoId = regResultados.getId();
		String exame = regResultados.getLwsComSolicitacaoExame().getExameLis();
		String solicitacao = regResultados.getLwsComSolicitacaoExame().getSolicitacao();

		String observacao = "Resultado " + resultadoId + " do exame " + exame + _DA_SOLICITACAO_ + solicitacao + _DA_COMUNICACAO_ + idComunicacao;
		String observacaoErroVerificado = this.obterObservacaoErroVerificado(e);

		return observacao + observacaoErroVerificado;
	}

	/**
	 * ObtÃ©m a observaÃ§Ã£o formatada de um erro verificado ou genÃ©rico atravÃ©s de uma instÃ¢ncia de LwsComResultado
	 * 
	 * @param regExamesAmostra
	 * @param idComunicacao
	 * @param e
	 * @return
	 */
	private String obterObservacaoFormatadaErro(LwsComAntibiograma regAntibio, Integer idComunicacao, Exception e) {

		Long antibioId = regAntibio.getId();
		Long resultadoId = regAntibio.getLwsComResultado().getId();
		String exame = regAntibio.getLwsComResultado().getLwsComSolicitacaoExame().getExameLis();
		String solicitacao = regAntibio.getLwsComResultado().getLwsComSolicitacaoExame().getSolicitacao();

		String observacao = "Antibio. " + antibioId + " resultado " + resultadoId + _EXAME_ + exame + SOLICIT + solicitacao + COMUNIC + idComunicacao;

		String observacaoErroVerificado = this.obterObservacaoErroVerificado(e);

		return observacao + observacaoErroVerificado;
	}

	/**
	 * ObtÃ©m a descriÃ§Ã£o de um erro verificado atravÃ©s de uma Exception
	 * 
	 * @param e
	 * @return
	 */
	private String obterObservacaoErroVerificado(Throwable e) {

		StackTraceElement ste = e.getStackTrace()[0];

		String metodo = ste.getMethodName();
		int linha = ste.getLineNumber();
		String classe = ste.getFileName().replace(".java", "");
		String mensagem = e.getMessage();

		return " - RASTREAMENTO ERRO VERIF.: " + classe + "." + metodo + "@" + linha + " = " + StringUtils.upperCase(mensagem);
	}

	/*
	 * MÃ©todos utilitÃ¡rios
	 */

	/**
	 * MÃ©todo reutilizÃ¡vel e anÃ¡logo a FUNCTION SUBSTR do PL-SQL
	 * 
	 * @param valor
	 * @param indiceInicial
	 * @param IndiceFinal
	 * @return
	 */
	private final String substr(String valor, int indiceInicial, int IndiceFinal) {
		String retorno = null;
		if (StringUtils.isNotEmpty(valor)) {
			retorno = valor.substring(indiceInicial, IndiceFinal);
			retorno = retorno.trim();
		}
		return retorno;
	}

	/**
	 * MÃ©todo reutilizÃ¡vel e anÃ¡logo a FUNCTION TO_NUMBER do PL-SQL
	 * 
	 * @param valor
	 * @return
	 */
	private final Integer toNumber(String valor) {
		Integer retorno = null;
		if (StringUtils.isNotEmpty(valor)) {
			retorno = Integer.valueOf(valor);
		}
		return retorno;
	}

	/**
	 * ObtÃ©m o nÃºmero da solicitaÃ§Ã£o da amostra
	 * 
	 * @param codAmostra
	 * @return
	 */
	private Integer obterSolicitacaoUtil(String codAmostra) {
		String aghSolicitacao;
		aghSolicitacao = codAmostra.substring(0, codAmostra.length() - 3);
		return Integer.valueOf(aghSolicitacao);
	}

	/**
	 * ObtÃ©m o cÃ³digo da amostra
	 * 
	 * @param codAmostra
	 * @return
	 */
	private Integer obterAmostraUtil(String codAmostra) {
		String aghAmostra;
		aghAmostra = codAmostra.substring(codAmostra.length() - 3, codAmostra.length());
		return Integer.valueOf(aghAmostra);
	}

	/*
	 * MÃ©todos para o detalhamento das mensagens de erro no AGHU
	 */

	/**
	 * ObtÃ©m o detalhamento dos erros de campo laudo
	 * 
	 * @param exaSigla
	 * @param manSeq
	 * @param calSeq
	 * @return
	 */
	private String obterDetalhamentoMensagemErroCampoLaudo(String exaSigla, Integer manSeq, Integer calSeq) {
		return " (EXA_SIGLA: " + exaSigla + ", MAN_SEQ: " + manSeq + ", CAL_SEQ: " + calSeq + ") ";
	}

	/**
	 * ObtÃ©m o detalhamento dos erros de equipamento
	 * 
	 * @param exaSigla
	 * @param manSeq
	 * @param calSeq
	 * @return
	 */
	private String obterDetalhamentoMensagemErroEquipamento(String setorLis, String driverId) {
		return " (SETOR_LIS: " + setorLis + ", DRIVER_ID: " + driverId + ") ";
	}

	/*
	 * DependÃªncias
	 */

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected LwsComSolicitacaoExameDAO getLwsComSolicitacaoExameDAO() {
		return lwsComSolicitacaoExameDAO;
	}

	protected LwsComunicacaoDAO getLwsComunicacaoDAO() {
		return lwsComunicacaoDAO;
	}

	protected LwsComAmostraDAO getLwsComAmostraDAO() {
		return lwsComAmostraDAO;
	}

	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}

	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}

	protected AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}

	protected AelMaterialAnaliseDAO getAelMaterialAnaliseDAO() {
		return aelMaterialAnaliseDAO;
	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected LwsComResultadoDAO getLwsComResultadoDAO() {
		return lwsComResultadoDAO;
	}

	protected AelEquipamentosDAO getAelEquipamentosDAO() {
		return aelEquipamentosDAO;
	}

	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}

	protected AelServidorUnidAssinaEletDAO getAelServidorUnidAssinaEletDAO() {
		return aelServidorUnidAssinaEletDAO;
	}

	protected LwsComAntibiogramaDAO getLwsComAntibiogramaDAO() {
		return lwsComAntibiogramaDAO;
	}

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}

	protected AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}

	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return aelResultadoCodificadoDAO;
	}

	protected AelCampoLaudoRelacionadoDAO getAelCampoLaudoRelacionadoDAO() {
		return aelCampoLaudoRelacionadoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}