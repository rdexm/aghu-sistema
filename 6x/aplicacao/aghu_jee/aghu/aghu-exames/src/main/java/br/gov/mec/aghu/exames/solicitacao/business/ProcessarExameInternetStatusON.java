/**
 * 
 */
package br.gov.mec.aghu.exames.solicitacao.business;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.business.IExameInternetStatusBean;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelExameInternetGrupoDAO;
import br.gov.mec.aghu.exames.dao.AelExameInternetStatusDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.laudos.ResultadoLaudoVO;
import br.gov.mec.aghu.exames.solicitacao.integracao.business.IExameIntegracaoInternet;
import br.gov.mec.aghu.exames.solicitacao.vo.ConselhoProfissionalVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DadosEnvioExameInternetVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DadosRetornoExameInternetVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemLaudoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameGrupoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameGrupoVO.TipoFilaExameInternet;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelExameInternetGrupo;
import br.gov.mec.aghu.model.AelExameInternetStatus;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.criptografia.CriptografiaUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Classe responsável por realizar o processamento dos exames para envio ao
 * portal externo
 * 
 * @author ghernandez
 * 
 */
@Stateless
public class ProcessarExameInternetStatusON extends BaseBusiness {

	private static final String TAREFA = "Tarefa [";

	private static final String AGHU_EXAMES = "aghu-exames";

	private static final String TAREFA_ERRO_ATUALIZANDO_STATUS_INTERNET_E = "Tarefa:Erro Atualizando StatusInternet -> E ...";

	private static final String CAUSA = " causa ";

	private static final String PARAMETRO_NAO_INFORMADO = "Parâmetro não informado.";

	private static final Log LOG = LogFactory.getLog(ProcessarExameInternetStatusON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private Instance<IExamesFacade> examesFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	@EJB
	private ISchedulerFacade schedulerFacade;

	@Inject
	private AelExameInternetStatusDAO aelExameInternetStatusDAO;

	@Inject
	private AelExameInternetGrupoDAO aelExameInternetGrupoDAO;

	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@EJB
	private IExameIntegracaoInternet exameIntegracaoInternet;

	@Inject
	private ExameInternetPoolON exameInternetPoolON;

	@EJB
	private IExameInternetStatusBean exameInternetStatusBean;

	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

	public enum ExameInternetStatusONExceptionCode implements BusinessExceptionCode {
		ERRO_CONTEXTO_XML, ERRO_CRIACAO_XML, ERRO_ENVIO_XML, SOLICITACAO_EXAME_NAO_ENCONTRADO, ERRO_CONFIGURACAO_DATA, EXAME_INTERNET_GRUPO_NAO_ENCONTRADO, DATA_HORA_LIBERACAO_EXAME_NAO_ENCONTRADO, DATA_HORA_EXAME_NAO_ENCONTRADO, ERRO_NRO_CONSELHO_PROFISSIONAL, ERRO_CODIGO_PACIENTE, ERRO_CODIGO_GRUPO_EXAME, LOCALIZADOR_EXAME_NAO_PREENCHIDO, ERRO_GERACAO_TOKEN, ERRO_CRIACAO_PDF, ITEM_SOLICITACAO_NAO_ENCONTRADO, SOLICITACAO_EXAME_SEM_ATENDIMENTO_OU_SEM_ORIGEM_DE_ATENDIMENTO, ERRO_GERACAO_XML;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 111167461223782587L;
	private static final String SIGLA_CREMERS = "CREMERS";
	private static final String SIGLA_CRM = "CRM";
	private static final String UF_RS = "RS";
	private static final String SPACE = " ";

	private static final String ERRO_GENERICO_ZIP = "Erro genérico ao gerar o arquivo ZIP";
	private static final String ERRO_URL_NAO_DEFINIDA = "Erro URL do Webservice não definida";
	private static final String ERRO_SENHA_PORTAL = "Erro Senha Portal não definida";

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void processarExameInternet(AghJobDetail job) {
		if (job == null || job.getSeq() == null) {
			LOG.error("JOB do quartz sem SEQ. Abortando execução.");
			return;
		}

		MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO = exameInternetPoolON.getExameParaProcessar();
		if (mensagemSolicitacaoExameGrupoVO != null
				&& TipoFilaExameInternet.NOVO.equals(mensagemSolicitacaoExameGrupoVO.getTipoFila())) {

			MensagemLaudoExameVO mensagemEnvio = null;
			try {
				exameInternetStatusBean.atualizarStatusInternet(
						mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
						mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(), DominioStatusExameInternet.LI,
						DominioSituacaoExameInternet.R, DominioStatusExameInternet.FG, null);
				LOG.info(TAREFA + job.getSeq() + "] Gerando PDF Laudo ... SOE_SEQ:["
						+ mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame() + "]");
				mensagemEnvio = gerarPdfExame(mensagemSolicitacaoExameGrupoVO);

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				schedulerFacade.adicionarLog(job, TAREFA_ERRO_ATUALIZANDO_STATUS_INTERNET_E);
				ServiceLocator.getBean(IExameInternetStatusBean.class, AGHU_EXAMES).atualizarStatusInternet(
						mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
						mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(), null,
						DominioSituacaoExameInternet.E, null, e.getMessage());
				return;
			}
			try {
				LOG.info(TAREFA + job.getSeq() + "] Enviando PDF Laudo Para o Portal ... SOE_SEQ:["
						+ mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame() + "]");
				enviarPdfParaPortal(mensagemEnvio);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				schedulerFacade.adicionarLog(job, TAREFA_ERRO_ATUALIZANDO_STATUS_INTERNET_E);
				ServiceLocator.getBean(IExameInternetStatusBean.class, AGHU_EXAMES).atualizarStatusInternet(
						mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
						mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(), null,
						DominioSituacaoExameInternet.E, null, e.getMessage());
				return;
			}
		} else if (mensagemSolicitacaoExameGrupoVO != null
				&& TipoFilaExameInternet.REENVIO.equals(mensagemSolicitacaoExameGrupoVO.getTipoFila())) {
			LOG.info(TAREFA + job.getSeq() + "] Reenviando Exame do pool de erros ...");
			try {
				reenviarExameParaPortal(mensagemSolicitacaoExameGrupoVO, job.getSeq());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				schedulerFacade.adicionarLog(job, TAREFA_ERRO_ATUALIZANDO_STATUS_INTERNET_E);
				ServiceLocator.getBean(IExameInternetStatusBean.class, AGHU_EXAMES).atualizarStatusInternet(
						mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
						mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(), null,
						DominioSituacaoExameInternet.E, null, e.getMessage());
				return;
			}
		} else {
			LOG.warn(TAREFA + job.getSeq() + "]: Não encontrou exame na pool ...");
		}
	}

	public void reenviarExameParaPortal(MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO) {
		reenviarExameParaPortal(mensagemSolicitacaoExameGrupoVO, null);
	}

	private void reenviarExameParaPortal(MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO, Integer jobSeq) {

		if (mensagemSolicitacaoExameGrupoVO != null) {

			List<AelExameInternetStatus> buscarExameInternetStatus = buscarExameInternetStatusPorSoeSeqEGeiSeq(mensagemSolicitacaoExameGrupoVO);
			if (!buscarExameInternetStatus.isEmpty()) {
				AelExameInternetStatus exameInternetStatus = buscarExameInternetStatus.get(0);
				exameInternetStatusBean.inserirStatusInternet(exameInternetStatus.getSolicitacaoExames(),
						exameInternetStatus.getItemSolicitacaoExames(), new Date(), DominioSituacaoExameInternet.N,
						DominioStatusExameInternet.RE, null, null);
			} else {
				LOG.error("Não encontrada entrada na AelExameInternetStatus para os valores SOE_SEQ: "
						+ mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame() + " e GEI_SEQ: "
						+ mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo());
				return;
			}

			MensagemLaudoExameVO mensagemEnvio = null;
			try {
				exameInternetStatusBean.atualizarStatusInternet(
						mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
						mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(), DominioStatusExameInternet.RE,
						DominioSituacaoExameInternet.R, DominioStatusExameInternet.FG, null);
				LOG.info(TAREFA + jobSeq + "] Gerando PDF Laudo ... SOE_SEQ:["
						+ mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame() + "]");
				mensagemEnvio = gerarPdfExame(mensagemSolicitacaoExameGrupoVO);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				ServiceLocator.getBean(IExameInternetStatusBean.class, AGHU_EXAMES).atualizarStatusInternet(
						mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
						mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(), null,
						DominioSituacaoExameInternet.E, null, e.getMessage());
				return;
			}
			try {
				LOG.info(TAREFA + jobSeq + "] Enviando PDF Laudo Para o Portal ... SOE_SEQ:["
						+ mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame() + "]");
				enviarPdfParaPortal(mensagemEnvio);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				ServiceLocator.getBean(IExameInternetStatusBean.class, AGHU_EXAMES).atualizarStatusInternet(
						mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
						mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(), null,
						DominioSituacaoExameInternet.E, null, e.getMessage());
				return;
			}

		}

	}

	private synchronized List<AelExameInternetStatus> buscarExameInternetStatusPorSoeSeqEGeiSeq(
			MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO) {
		List<AelExameInternetStatus> buscarExameInternetStatus = aelExameInternetStatusDAO.buscarExameInternetStatus(
				mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
				mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(), null, null);
		return buscarExameInternetStatus;
	}

	private MensagemLaudoExameVO gerarPdfExame(MensagemSolicitacaoExameGrupoVO mensagem) {

		List<AelItemSolicitacaoExames> itensSolicitacao = new ArrayList<AelItemSolicitacaoExames>();
		MensagemLaudoExameVO mensagemEnvio = null;
		try {
			itensSolicitacao = buscaItensSolicitacao(mensagem);

			if (itensSolicitacao != null && !itensSolicitacao.isEmpty()) {
				List<IAelItemSolicitacaoExamesId> itemIds = new ArrayList<IAelItemSolicitacaoExamesId>();
				for (AelItemSolicitacaoExames item : itensSolicitacao) {
					itemIds.add(item.getId());
				}

				ExamesListaVO dadosLaudo = examesFacade.get().buscarDadosLaudo(itemIds);
				ResultadoLaudoVO outputStreamLaudo = examesFacade.get().executaLaudo(dadosLaudo,
						DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO);
				String xmlEnvio = null;
				try {
					xmlEnvio = gerarXmlEnvioExameInternet(itensSolicitacao, mensagem.getSeqExameInternetGrupo());
				} catch (Exception e) {
					throw new BaseException(ExameInternetStatusONExceptionCode.ERRO_GERACAO_XML, e);
				}

				exameInternetStatusBean.atualizarStatusInternet(mensagem.getSeqSolicitacaoExame(),
						mensagem.getSeqExameInternetGrupo(), DominioStatusExameInternet.FG,
						DominioSituacaoExameInternet.R, null, null);

				if (outputStreamLaudo != null && xmlEnvio != null) {
					AelSolicitacaoExames solic = obterSolicitacao(mensagem);

					mensagemEnvio = new MensagemLaudoExameVO();
					mensagemEnvio.setArquivoLaudo(outputStreamLaudo.getOutputStreamLaudo().toByteArray());
					mensagemEnvio.setLocalizador(solic.getLocalizador());
					mensagemEnvio.setSeqExameInternetGrupo(mensagem.getSeqExameInternetGrupo());
					mensagemEnvio.setSeqSolicitacaoExame(mensagem.getSeqSolicitacaoExame());
					mensagemEnvio.setXmlEnvio(xmlEnvio);

					for (AelItemSolicitacaoExames itemSolicitacaoExames : itensSolicitacao) {
						exameInternetStatusBean.inserirStatusInternet(solic, itemSolicitacaoExames, null,
								DominioSituacaoExameInternet.N, DominioStatusExameInternet.FE, null, null);
					}
				}
				return mensagemEnvio;
			} else {
				throw new BaseException(ExameInternetStatusONExceptionCode.ITEM_SOLICITACAO_NAO_ENCONTRADO);
			}
		} catch (Exception e) {
			LOG.error("Erro ao gerar arquivos para fila: ", e);
			exameInternetStatusBean.atualizarStatusInternet(mensagem.getSeqSolicitacaoExame(),
					mensagem.getSeqExameInternetGrupo(), DominioStatusExameInternet.FG, DominioSituacaoExameInternet.E,
					null, e.getMessage() + CAUSA + e.getCause());
			return null;
		}
	}

	private synchronized AelSolicitacaoExames obterSolicitacao(MensagemSolicitacaoExameGrupoVO mensagem) {
		return aelSolicitacaoExameDAO.obterPorChavePrimaria(mensagem.getSeqSolicitacaoExame());
	}

	private synchronized List<AelItemSolicitacaoExames> buscaItensSolicitacao(MensagemSolicitacaoExameGrupoVO mensagem) {
		List<AelItemSolicitacaoExames> listaSolics = aelItemSolicitacaoExameDAO.buscarItemExamesLiberadosPorGrupo(mensagem.getSeqSolicitacaoExame(),
				mensagem.getSeqExameInternetGrupo());

		examesLaudosFacade.verificaLaudoPatologia(listaSolics, false);
		
		return listaSolics;
	}

	private void enviarPdfParaPortal(MensagemLaudoExameVO mensagemEnvio) {
		try {
			if (mensagemEnvio != null) {
				File arquivoZip = exameIntegracaoInternet.gerarArquivoCompactado(mensagemEnvio.getArquivoLaudo(),
						mensagemEnvio.getXmlEnvio());
				if (arquivoZip == null) {
					LOG.error("Erro ao gerar o arquivo zip");
					exameInternetStatusBean.atualizarStatusInternet(mensagemEnvio.getSeqSolicitacaoExame(),
							mensagemEnvio.getSeqExameInternetGrupo(), DominioStatusExameInternet.FE,
							DominioSituacaoExameInternet.E, null, ERRO_GENERICO_ZIP);
					return;
				}

				AghParametros urlParametro = this.parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_URL_INTEGRACAO_PORTAL_LAUDO);
				if (urlParametro == null || StringUtils.isEmpty(urlParametro.getVlrTexto())) {
					LOG.error("Erro ao comunicar com o webservice do Portal");
					exameInternetStatusBean.atualizarStatusInternet(mensagemEnvio.getSeqSolicitacaoExame(),
							mensagemEnvio.getSeqExameInternetGrupo(), DominioStatusExameInternet.FE,
							DominioSituacaoExameInternet.E, null, ERRO_URL_NAO_DEFINIDA);
					return;
				}

				AghParametros senhaParametro = this.parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_AGHU_SENHA_PORTAL_EXAMES_INTERNET);
				if (senhaParametro == null || StringUtils.isEmpty(senhaParametro.getVlrTexto())) {
					LOG.error("Erro ao obter parametro da senha do portal de laudos da internet");
					exameInternetStatusBean.atualizarStatusInternet(mensagemEnvio.getSeqSolicitacaoExame(),
							mensagemEnvio.getSeqExameInternetGrupo(), DominioStatusExameInternet.FE,
							DominioSituacaoExameInternet.E, null, ERRO_SENHA_PORTAL);
					return;
				}
				DadosRetornoExameInternetVO retornoVO =

				exameIntegracaoInternet.enviarLaudoPortal(mensagemEnvio.getSeqSolicitacaoExame(),
						mensagemEnvio.getSeqExameInternetGrupo(), mensagemEnvio.getLocalizador(), arquivoZip,
						urlParametro.getVlrTexto(), CriptografiaUtil.descriptografar(senhaParametro.getVlrTexto()));

				if (retornoVO.getDescricaoErro() != null && !retornoVO.getDescricaoErro().isEmpty()) {
					exameInternetStatusBean.atualizarStatusInternet(mensagemEnvio.getSeqSolicitacaoExame(),
							mensagemEnvio.getSeqExameInternetGrupo(), DominioStatusExameInternet.FE,
							DominioSituacaoExameInternet.E, null,
							this.montarDescricaoErro(retornoVO.getDescricaoErro()));
					return;
				}
				exameInternetStatusBean.atualizarStatusInternet(mensagemEnvio.getSeqSolicitacaoExame(),
						mensagemEnvio.getSeqExameInternetGrupo(), DominioStatusExameInternet.FE,
						DominioSituacaoExameInternet.R, DominioStatusExameInternet.EC, null);
			}
		} catch (Exception e) {
			LOG.error("Erro ao obter mensagem: ", e);
			exameInternetStatusBean.atualizarStatusInternet(mensagemEnvio.getSeqSolicitacaoExame(),
					mensagemEnvio.getSeqExameInternetGrupo(), DominioStatusExameInternet.FE,
					DominioSituacaoExameInternet.E, null, e.getMessage() + CAUSA + e.getCause());
		}
	}

	/**
	 * Retornar a descrição do erro loocalizado no XML
	 * 
	 * @param listaErro
	 * @return
	 */
	private String montarDescricaoErro(List<String> listaErro) {

		StringBuilder descricaoErro = new StringBuilder();

		for (String erro : listaErro) {
			descricaoErro.append(erro);
			if (descricaoErro.length() > 4000) {
				break;
			}
		}

		return descricaoErro.length() <= 4000 ? descricaoErro.toString() : descricaoErro.substring(0, 4000);
	}

	/**
	 * Método responsável por gerar o xml de envio dos resultados
	 * 
	 * @param solicitacaoExame
	 * @param listaItensAgrupados
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String gerarXmlEnvioExameInternet(List<AelItemSolicitacaoExames> listaItensAgrupados,
			Integer seqExameInternetGrupo) throws BaseException {

		if (listaItensAgrupados == null || seqExameInternetGrupo == null || listaItensAgrupados.isEmpty()) {
			throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.SOLICITACAO_EXAME_NAO_ENCONTRADO);
		}

		DadosEnvioExameInternetVO dadosEnvioExameInternetVO = new DadosEnvioExameInternetVO();
		AelSolicitacaoExames solicitacaoExame = listaItensAgrupados.get(0).getSolicitacaoExame();

		this.atualizaGrupoExame(dadosEnvioExameInternetVO, seqExameInternetGrupo);

		dadosEnvioExameInternetVO.setSoeSeq(solicitacaoExame.getSeq().toString());
		dadosEnvioExameInternetVO.setLocalizador(solicitacaoExame.getLocalizador());

		atualizaDadosPaciente(solicitacaoExame, dadosEnvioExameInternetVO);

		if (solicitacaoExame.getAtendimento() == null || solicitacaoExame.getAtendimento().getOrigem() == null) {
			throw new ApplicationBusinessException(
					ExameInternetStatusONExceptionCode.SOLICITACAO_EXAME_SEM_ATENDIMENTO_OU_SEM_ORIGEM_DE_ATENDIMENTO);
		}

		if (DominioOrigemAtendimento.X.equals(solicitacaoExame.getAtendimento().getOrigem())) {
			dadosEnvioExameInternetVO.setConselhoProfissionalMedico(this.atualizaConselhoMedicoExterno(solicitacaoExame
					.getAtendimento()));
		} else {
			dadosEnvioExameInternetVO.setConselhoProfissionalMedico(this.atualizaDadosConselhoMedico(solicitacaoExame
					.getServidorResponsabilidade()));
		}

		// Quando origem de atendimento for 'I - INTERNACAO' informa o médico
		// responsável do atendimento
		if (DominioOrigemAtendimento.I.equals(solicitacaoExame.getAtendimento().getOrigem())) {
			dadosEnvioExameInternetVO.setConselhoProfissionalResponsavel(this
					.atualizaDadosConselhoMedico(solicitacaoExame.getAtendimento().getServidor()));
		}

		if (solicitacaoExame.getConvenioSaude() != null) {
			dadosEnvioExameInternetVO.setConvenio(solicitacaoExame.getConvenioSaude().getDescricao());
		}

		try {
			dadosEnvioExameInternetVO.setDataLiberacaoExame(obterDataFormatada(listaItensAgrupados.get(0)
					.getDthrLiberada(), true));
		} catch (DatatypeConfigurationException e) {
			throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.ERRO_CONFIGURACAO_DATA);
		}

		dadosEnvioExameInternetVO.setDataExame(this.buscaDataExame(solicitacaoExame));
		dadosEnvioExameInternetVO.setNotaAdicional(this.possuiNotaAdicional(listaItensAgrupados));

		if (solicitacaoExame.getAtendimento() != null
				&& solicitacaoExame.getAtendimento().getAtendimentoPacienteExterno() != null
				&& solicitacaoExame.getAtendimento().getAtendimentoPacienteExterno().getLaboratorioExterno() != null) {
			dadosEnvioExameInternetVO.setCnpjInstituicao(this.validaCnpj(solicitacaoExame.getAtendimento()
					.getAtendimentoPacienteExterno().getLaboratorioExterno().getCgc()));
		}

		return exameIntegracaoInternet.gerarXmlEnvio(dadosEnvioExameInternetVO);
	}

	/**
	 * Validar o CNPJ que está sendo enviado
	 * 
	 * @param cnpj
	 * @return
	 */
	private String validaCnpj(String cnpj) {
		// Acrescenta a consulta o critério do CGC/CNPJ do fornecedor
		if (StringUtils.isNotBlank(cnpj) && CoreUtil.validarCNPJ(cnpj)) {
			return cnpj;
		} else {
			return null;
		}
	}

	/**
	 * Atualizar o conselho médico quando o atendimento for externo
	 * 
	 * @param atendimento
	 * @return
	 */
	private ConselhoProfissionalVO atualizaConselhoMedicoExterno(AghAtendimentos atendimento) {

		if (atendimento == null) {
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		ConselhoProfissionalVO conselhoProfissional = new ConselhoProfissionalVO();
		if (atendimento.getAtendimentoPacienteExterno() != null
				&& atendimento.getAtendimentoPacienteExterno().getMedicoExterno() != null
				&& atendimento.getAtendimentoPacienteExterno().getMedicoExterno().getCrm() != null) {
			conselhoProfissional.setRegConselhoMedico(atendimento.getAtendimentoPacienteExterno().getMedicoExterno()
					.getCrm().toString());
		}
		conselhoProfissional.setSiglaConselhoMedico(SIGLA_CRM);
		conselhoProfissional.setUfConselhoMedico(UF_RS);

		return conselhoProfissional;
	}

	/**
	 * Retorna a data formatada para ser utilizada na geração do XML
	 * 
	 * @param data
	 * @param comHoraMinSeg
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	private XMLGregorianCalendar obterDataFormatada(Date data, boolean comHoraMinSeg)
			throws DatatypeConfigurationException {

		if (data == null) {
			return null;
		} else {
			DatatypeFactory df = DatatypeFactory.newInstance();
			DateTime dt = new DateTime(data);

			XMLGregorianCalendar xmlGregorianCalendar = df.newXMLGregorianCalendar();
			xmlGregorianCalendar.setDay(dt.getDayOfMonth());
			xmlGregorianCalendar.setMonth(dt.getMonthOfYear());
			xmlGregorianCalendar.setYear(dt.getYear());
			if (comHoraMinSeg) {
				xmlGregorianCalendar.setTime(dt.getHourOfDay(), dt.getMinuteOfHour(), dt.getSecondOfMinute());
			}

			return xmlGregorianCalendar;
		}

	}

	/**
	 * Atualizar os dados do Paciente do Exame
	 * 
	 * @param solicitacaoExame
	 * @param dadosEnvioExameInternetVO
	 * @throws ApplicationBusinessException
	 */
	private void atualizaDadosPaciente(AelSolicitacaoExames solicitacaoExame,
			DadosEnvioExameInternetVO dadosEnvioExameInternetVO) throws ApplicationBusinessException {

		if (solicitacaoExame == null || dadosEnvioExameInternetVO == null) {
			throw new IllegalArgumentException("Parâmetros não informados.");
		}

		if (solicitacaoExame.getAtendimento() != null && solicitacaoExame.getAtendimento().getPaciente() != null) {
			try {
				dadosEnvioExameInternetVO.setCodigoPaciente(BigInteger.valueOf(solicitacaoExame.getAtendimento()
						.getPaciente().getCodigo().longValue()));
			} catch (NumberFormatException e) {
				throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.ERRO_CODIGO_PACIENTE);
			}

			if (solicitacaoExame.getAtendimento().getPaciente().getDtNascimento() != null) {
				try {
					dadosEnvioExameInternetVO.setDataNascimentoPaciente(this.obterDataFormatada(solicitacaoExame
							.getAtendimento().getPaciente().getDtNascimento(), false));
				} catch (DatatypeConfigurationException e) {
					throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.ERRO_CONFIGURACAO_DATA);
				}
			}
			dadosEnvioExameInternetVO.setNomePaciente(solicitacaoExame.getAtendimento().getPaciente().getNome());
		}

	}

	/**
	 * Atualizar dados do conselho médico a partir do servidor informado
	 * 
	 * @param servidor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private ConselhoProfissionalVO atualizaDadosConselhoMedico(RapServidores servidor)
			throws ApplicationBusinessException {

		if (servidor == null) {
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		ConselhoProfissionalVO conselhoProfissionalMedicoVO = new ConselhoProfissionalVO();

		String nroRegConselhoProfissional = this.getNumeroConselhoProfissional(servidor.getId().getMatricula(),
				servidor.getId().getVinCodigo());
		if (!StringUtils.isBlank(nroRegConselhoProfissional)) {
			try {
				conselhoProfissionalMedicoVO.setRegConselhoMedico(nroRegConselhoProfissional);
			} catch (NumberFormatException e) {
				LOG.error("Erro ao tentar converter CRM " + nroRegConselhoProfissional + " para inteiro ");
				LOG.error(e.getMessage(), e);
			}

		}

		conselhoProfissionalMedicoVO.setSiglaConselhoMedico(this.getSiglaConselhoProfissional(servidor.getId()
				.getMatricula(), servidor.getId().getVinCodigo()));

		conselhoProfissionalMedicoVO.setUfConselhoMedico(UF_RS);

		return conselhoProfissionalMedicoVO;
	}

	/**
	 * Retornar o Número do Conselho Profissional
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	private String getNumeroConselhoProfissional(Integer matricula, Short vinCodigo) {

		return cadastrosBasicosFacade.obterRapQualificaoComNroRegConselho(matricula, vinCodigo, DominioSituacao.A);

	}

	/**
	 * Retornar a Sigla do Conselho Profissional
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	private String getSiglaConselhoProfissional(Integer matricula, Short vinCodigo) {
		List<RapConselhosProfissionais> rapConselhoProfissional = cadastrosBasicosFacade
				.listarConselhoProfissionalComNroRegConselho(matricula, vinCodigo, DominioSituacao.A);

		if (rapConselhoProfissional != null && rapConselhoProfissional.size() > 1) {
			ordenaPorConselhoMedicina(rapConselhoProfissional);
		}

		if (rapConselhoProfissional != null && !rapConselhoProfissional.isEmpty()
				&& !StringUtils.isBlank(rapConselhoProfissional.get(0).getSigla())) {
			return rapConselhoProfissional.get(0).getSigla().equals(SIGLA_CREMERS) ? SIGLA_CRM
					: rapConselhoProfissional.get(0).getSigla();
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	/**
	 * Ordena primeiro os conselhos regionais de medicina
	 * 
	 * @param listaConselhoProfissional
	 */
	private void ordenaPorConselhoMedicina(List<RapConselhosProfissionais> listaConselhoProfissional) {

		final List<AipUfs> listaUfs = cadastrosBasicosPacienteFacade.listarTodosUFs();
		final String CONS_CREME = "CREME";
		final String CONS_CRM = "CRM";

		Collections.sort(listaConselhoProfissional, new Comparator<RapConselhosProfissionais>() {

			public int compare(RapConselhosProfissionais cons1, RapConselhosProfissionais cons2) {
				RapConselhosProfissionais c1 = (RapConselhosProfissionais) cons1;
				// RapConselhosProfissionais c2 = (RapConselhosProfissionais)
				// cons2;

				if (!StringUtils.isBlank(c1.getSigla())) {

					for (AipUfs uf : listaUfs) {
						if ((c1.getSigla().equals(CONS_CREME + uf.getSigla()))
								|| (c1.getSigla().equals(CONS_CRM + uf.getSigla()))) {
							return -1;
						}
					}

				}

				return 1;
			}
		});

	}

	/**
	 * Montar a descrição do conselho profissional
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	private String montarDescricaoConselhoProfissional(RapServidores servidor) {
		String siglaConselho = this.getSiglaConselhoProfissional(servidor.getId().getMatricula(), servidor.getId()
				.getVinCodigo());
		String numeroConselho = this.getNumeroConselhoProfissional(servidor.getId().getMatricula(), servidor.getId()
				.getVinCodigo());
		StringBuilder descricaoConselho = new StringBuilder();

		if (siglaConselho != null) {
			descricaoConselho.append(siglaConselho).append(SPACE);
		}

		descricaoConselho.append(UF_RS).append(SPACE);

		if (numeroConselho != null) {
			descricaoConselho.append(numeroConselho);
		}

		return descricaoConselho.toString();

	}

	/**
	 * Montar a descrição do conselho profissional para atendimento externo
	 * 
	 * @param atendimento
	 * @return
	 */
	private String montarDescricaoConselhoProfissionalExterno(AghAtendimentos atendimento) {

		if (atendimento == null) {
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		StringBuilder descricaoConselho = new StringBuilder();
		descricaoConselho.append(SIGLA_CRM).append(SPACE).append(UF_RS).append(SPACE);

		if (atendimento.getAtendimentoPacienteExterno() != null
				&& atendimento.getAtendimentoPacienteExterno().getMedicoExterno() != null
				&& atendimento.getAtendimentoPacienteExterno().getMedicoExterno().getCrm() != null) {
			descricaoConselho.append((atendimento.getAtendimentoPacienteExterno().getMedicoExterno().getCrm())).append(
					SPACE);
		}

		return descricaoConselho.toString();

	}

	/**
	 * Retornar a descrição do solicitante do exame, com tratamento quando
	 * atendimento for externo
	 * 
	 * @param exameInternetStatus
	 * @return
	 */
	public String obterSolicitanteExame(AelExameInternetStatus exameInternetStatus) {

		if (exameInternetStatus != null
				&& exameInternetStatus.getSolicitacaoExames() != null
				&& exameInternetStatus.getSolicitacaoExames().getAtendimento() != null
				&& DominioOrigemAtendimento.X.equals(exameInternetStatus.getSolicitacaoExames().getAtendimento()
						.getOrigem())) {
			return this.montarDescricaoConselhoProfissionalExterno(exameInternetStatus.getSolicitacaoExames()
					.getAtendimento());
		} else {
			if (exameInternetStatus.getSolicitacaoExames().getServidorResponsabilidade() != null) {
				return this.montarDescricaoConselhoProfissional(exameInternetStatus.getSolicitacaoExames()
						.getServidorResponsabilidade());
			}
		}

		return null;
	}

	/**
	 * Buscar a data de efetivação do exame
	 * 
	 * @param solicitacaoExame
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private XMLGregorianCalendar buscaDataExame(AelSolicitacaoExames solicitacaoExame)
			throws ApplicationBusinessException {

		if (solicitacaoExame == null) {
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		XMLGregorianCalendar dtRetorno = null;
		Date dtExame = aelExtratoItemSolicitacaoDAO.obterMenorDataAreaExecutoraPorSolicitacaoExame(solicitacaoExame
				.getSeq());
		if (dtExame == null) {
			throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.DATA_HORA_EXAME_NAO_ENCONTRADO);
		} else {
			try {
				dtRetorno = this.obterDataFormatada(dtExame, true);
			} catch (DatatypeConfigurationException e) {
				throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.ERRO_CONFIGURACAO_DATA);
			}

		}

		return dtRetorno;
	}

	/**
	 * Verificar se o éxame possui nota adicional
	 * 
	 * @param listaItensAgrupados
	 * @return
	 */
	private boolean possuiNotaAdicional(List<AelItemSolicitacaoExames> listaItensAgrupados) {

		if (listaItensAgrupados == null) {
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		boolean possuiNotaAdicional = false;
		for (AelItemSolicitacaoExames item : listaItensAgrupados) {
			if (item.getNotaAdicional() != null && !item.getNotaAdicional().isEmpty()) {
				possuiNotaAdicional = true;
				break;
			}
		}

		return possuiNotaAdicional;
	}

	/**
	 * Atualizar os dados do grupo de exame
	 * 
	 * @param dadosEnvioExameInternetVO
	 * @param seqExameInternetGrupo
	 * @throws ApplicationBusinessException
	 */
	private void atualizaGrupoExame(DadosEnvioExameInternetVO dadosEnvioExameInternetVO, Integer seqExameInternetGrupo)
			throws ApplicationBusinessException {

		if (dadosEnvioExameInternetVO == null || seqExameInternetGrupo == null) {
			throw new IllegalArgumentException("Parâmetros não informados.");
		}

		AelExameInternetGrupo exameInternetGrupo = aelExameInternetGrupoDAO
				.buscarExameInterGrupo(seqExameInternetGrupo);

		if (exameInternetGrupo == null) {
			throw new ApplicationBusinessException(
					ExameInternetStatusONExceptionCode.EXAME_INTERNET_GRUPO_NAO_ENCONTRADO);
		}

		try {
			dadosEnvioExameInternetVO.setSeqGrupo(BigInteger.valueOf(exameInternetGrupo.getSeq().longValue()));
		} catch (NumberFormatException e) {
			throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.ERRO_CODIGO_GRUPO_EXAME);
		}

		if (!StringUtils.isBlank(exameInternetGrupo.getDescricao())) {
			dadosEnvioExameInternetVO
					.setDescricaoGrupo(exameInternetGrupo.getDescricao().length() <= 30 ? exameInternetGrupo
							.getDescricao() : exameInternetGrupo.getDescricao().substring(0, 30));
		}

	}

}