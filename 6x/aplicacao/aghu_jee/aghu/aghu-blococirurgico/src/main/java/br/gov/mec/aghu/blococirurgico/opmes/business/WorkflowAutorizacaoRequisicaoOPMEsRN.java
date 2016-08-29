package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import br.gov.mec.aghu.model.AghWFHistoricoExecucao;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.AghWFEtapaDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFExecutorDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFFluxoDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFHistoricoExecucaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAlcadaAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcServidorAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.vo.CancelamentoOpmeVO;
import br.gov.mec.aghu.blococirurgico.workflow.business.IWorkflowFacade;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.dominio.DominioTipoResponsabilidadeOPMS;
import br.gov.mec.aghu.dominio.DominioWorkflowOPMEsCodigoCriterio;
import br.gov.mec.aghu.dominio.DominioWorkflowOPMEsCodigoTemplateEtapa;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFExecutor;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFRota;
import br.gov.mec.aghu.model.AghWFRotaCriterio;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.AghWFTemplateFluxo;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmesJn;
import br.gov.mec.aghu.model.MbcServidorAvalOpms;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;
import java.util.ArrayList;


@Stateless
public class WorkflowAutorizacaoRequisicaoOPMEsRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(WorkflowAutorizacaoRequisicaoOPMEsRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcServidorAvalOpmsDAO mbcServidorAvalOpmsDAO;

	@Inject
	private MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO;

	@Inject
	private MbcAlcadaAvalOpmsDAO mbcAlcadaAvalOpmsDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@Inject
	private AghWFFluxoDAO aghWFFluxoDAO;
	
	@Inject
	private AghWFExecutorDAO aghWFExecutorDAO;
	
	@Inject
	private MbcGrupoAlcadaDAO mbcGrupoAlcadaDAO; 
	
	@Inject
	private MbcRequisicaoOpmesJnDAO mbcRequisicaoOpmesJnDAO;
	
	@Inject
	private AghWFEtapaDAO aghWFEtapaDAO;
	
	@Inject
	private AghWFHistoricoExecucaoDAO aghWFHistoricoExecucaoDAO;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IWorkflowFacade iWorkflowFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;

	private static final long serialVersionUID = -1329871349769356507L;
	
	private static final String QUEBRA_LINHA = "<br>";
	
	private enum WorkflowAutorizacaoRequisicaoOPMEExceptionCode implements BusinessExceptionCode{
		MENSAGEM_EXECUTOR_NAO_AUTORIZADO;
	}
	
	protected IWorkflowFacade getWorkflowFacade() {
		return iWorkflowFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
	
	protected MbcServidorAvalOpmsDAO getMbcServidorAvalOpmsDAO() {
		return mbcServidorAvalOpmsDAO;
	}
	
	protected MbcRequisicaoOpmesDAO getMbcRequisicaoOpmesDAO() {
		return mbcRequisicaoOpmesDAO;		
	}
	
	protected MbcAlcadaAvalOpmsDAO getMbcAlcadaAvalOpmsDAO() {
		return mbcAlcadaAvalOpmsDAO;
	}
	
	
	protected MbcItensRequisicaoOpmesDAO getMbcItensRequisicaoOpmesDAO() {
		return mbcItensRequisicaoOpmesDAO;
	}
	
	protected AghWFFluxoDAO getAghWFFluxoDAO() {
		return aghWFFluxoDAO;
	}
	
	protected AghWFExecutorDAO getAghWFExecutorDAO() {
		return aghWFExecutorDAO;
	}
	
	public AghWFFluxo iniciarFluxoAutorizacaoOPMEs(RapServidores servidor, MbcRequisicaoOpmes requisicao)
			throws BaseException {

		AghWFFluxo fluxo = null;
		// verifica valor minimo, se valor incompativel menor que valor minimo da primeira alcada, nao roda fluxo de autorizacao
		requisicao = this.mbcRequisicaoOpmesDAO.obterPorChavePrimaria(requisicao.getSeq());
		Boolean valorValido = Boolean.FALSE;
		BigDecimal totalExcedidoRequisicao = calcularTotalExcedenteRequisicao(requisicao.getItensRequisicao());

		MbcGrupoAlcadaAvalOpms grupoAlcada = getMbcGrupoAlcadaDAO().buscaGrupoAlcadaAtivo(DominioTipoConvenioOpms.SUS, requisicao.getAgendas().getEspecialidade());

		if(grupoAlcada != null){
			for (MbcAlcadaAvalOpms alcada : grupoAlcada.getAlcadas()) {
				if (alcada.getNivelAlcada() == 1
						&& totalExcedidoRequisicao.doubleValue() > alcada.getValorMinimo().doubleValue()) {
					valorValido = Boolean.TRUE;
					break;
				}
			}
		}
		
		//#48271
		if(!valorValido){
			for (MbcItensRequisicaoOpmes item : requisicao.getItensRequisicao()) {
				if(DominioRequeridoItemRequisicao.ADC.equals(item.getRequerido()) || DominioRequeridoItemRequisicao.NOV.equals(item.getRequerido()) || item.getIndCompativel() == false){
					valorValido = Boolean.TRUE;
				}
			}
		}
		
		
		if (valorValido) {

			// executar a etapa
			String codigoModulo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_OPME_WF_TEMPLATE)
					.getVlrTexto();
			AghWFTemplateFluxo templateFluxo = getWorkflowFacade().recuperarTemplate(codigoModulo);
			if (templateFluxo != null) {
				fluxo = getWorkflowFacade().criarFluxo(templateFluxo,requisicao);
				fluxo.setObservacoes(getObservacoesFluxoOpme(requisicao.getAgendas().getSeq()));
				AghWFEtapa etapa = fluxo.getEtapas().get(0);
				AghWFExecutor executor = criarExecutorPrimeiraEtapa(servidor, fluxo, templateFluxo, etapa,requisicao);
				associarFluxoRequisicao(requisicao, fluxo);
				// Nao grava histório na primeira etapa
				executarEtapaFluxoAutorizacaoOPMEs(executor, etapa, "Execução primeira etapa", requisicao, Boolean.FALSE, servidor.getUsuario());
			}
		}else{
			//altera situacao da requisicao para compativel
			requisicao.setSituacao(DominioSituacaoRequisicao.COMPATIVEL);
			requisicao.setIndCompativel(Boolean.TRUE);
			getMbcRequisicaoOpmesDAO().atualizar(requisicao);
			getMbcRequisicaoOpmesDAO().flush();
		}
		return fluxo;
	}
	
		private String getObservacoesFluxoOpme(Integer agendaSeq) {
				
				String obsFormato = "(Unidade:%s | Data:%s | Especialidade: %s | Equipe: %s | Sala: %s | Prontuário: %s)";
				
				Object[] obs = getMbcRequisicaoOpmesDAO().obterObservacoesFluxoOpme(agendaSeq);
				
				String dataFormatada = "";
				Date dataAgenda = (Date) obs[1];
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				dataFormatada = df.format(dataAgenda);
		
				return String.format(obsFormato, obs[0], dataFormatada, obs[2], obs[3], obs[4], obs[5]);
		}

	private AghWFExecutor criarExecutorPrimeiraEtapa(RapServidores servidor,
			AghWFFluxo fluxo, AghWFTemplateFluxo templateFluxo, AghWFEtapa etapa,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		AghWFExecutor executor = new AghWFExecutor();
		executor.setEtapa(etapa);
		executor.setFluxo(fluxo);
		executor.setIndRecebeNotificacao(true);
		executor.setTemplateEtapa(etapa.getTemplateEtapa());
		executor.setRapServidor(servidor);
		executor.setTemplateFluxo(templateFluxo);
		executor.setAutorizadoExecutar(true);
		executor = getWorkflowFacade().adicionarExecutor(executor,requisicao);
		return executor;
	}
	
	public void rejeitarEtapaFluxoAutorizacaoOPMEs(AghWFEtapa etapa, String justificativa) throws BaseException {
		
		AghWFEtapa entityEtapa = this.aghWFEtapaDAO.obterPorChavePrimaria(etapa.getSeq());
		AghWFExecutor executor = obterExecutorEtapa(entityEtapa);
		MbcRequisicaoOpmes requisicao = getMbcRequisicaoOpmesDAO().obterMbcRequisicaoOpmesPorFluxo(entityEtapa.getFluxo().getSeq());
		// TODO ALTERACAO 
		if(DominioWorkflowOPMEsCodigoTemplateEtapa.EM_AUTORIZACAO_01.toString().equals(entityEtapa.getTemplateEtapa().getCodigo())
			|| DominioWorkflowOPMEsCodigoTemplateEtapa.EM_AUTORIZACAO_02.toString().equals(entityEtapa.getTemplateEtapa().getCodigo())
			|| DominioWorkflowOPMEsCodigoTemplateEtapa.EM_AUTORIZACAO_03.toString().equals(entityEtapa.getTemplateEtapa().getCodigo())
			|| DominioWorkflowOPMEsCodigoTemplateEtapa.EM_AUTORIZACAO_04.toString().equals(entityEtapa.getTemplateEtapa().getCodigo())) {
			// adicionar etapa NAO_AUTORIZADA ao fluxo e já executar ela e deixar o executor que está rejeitando a etapa atual
			// como executor da etapa NAO_AUTORIZADA, no momento em que o usuário for rejeitar uma etapa em_autorizacao 1 2 ou 3
			naoAutorizarFluxo(entityEtapa, justificativa, executor, requisicao);
		} else {
			voltarParaEtapaAnterior(entityEtapa, justificativa, executor, requisicao);
		}
	}

	private void voltarParaEtapaAnterior(AghWFEtapa etapa,
			String justificativa, AghWFExecutor executor,
			MbcRequisicaoOpmes requisicao) throws BaseException {
		getWorkflowFacade().rejeitarEtapa(etapa, executor, justificativa, requisicao);
		AghWFEtapa etapaAnterior = etapa.getEtapaAnterior();
		String codigoEtapaSituacao= etapaAnterior.getTemplateEtapa().getCodigo();
		atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.getInstance(codigoEtapaSituacao));
	}

	private void naoAutorizarFluxo(AghWFEtapa etapa, String justificativa,
			AghWFExecutor executor, MbcRequisicaoOpmes requisicao) throws BaseException {
		getWorkflowFacade().rejeitarEtapa(etapa, executor, justificativa, requisicao);
		AghWFTemplateFluxo templateFluxo = etapa.getFluxo().getTemplateFluxo();
		AghWFTemplateEtapa templateEtapa = obterTemplateEtapaPorCodigo(DominioSituacaoRequisicao.NAO_AUTORIZADA.toString(), templateFluxo);
		getWorkflowFacade().adicionarEtapa(etapa, etapa.getFluxo(), templateEtapa, executor);
		adicionarEtapaTodosExecutoresEnvolvidosNaoAutorizadas(etapa, requisicao, templateEtapa, executor);
		getWorkflowFacade().executarEtapa(executor, etapa, "Executando Etapa: Não Autorizada", Boolean.FALSE,requisicao);
		getWorkflowFacade().concluirFluxo(executor, etapa.getFluxo(), justificativa);
		requisicao.setDataFim(new Date());
		atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.NAO_AUTORIZADA);
	}
	
	private AghWFTemplateEtapa obterTemplateEtapaPorCodigo(String codigo, AghWFTemplateFluxo templateFluxo) {
		for(AghWFTemplateEtapa templateEtapa: templateFluxo.getEtapas()) {
			if(templateEtapa.getCodigo().equals(codigo)){
				return templateEtapa;
			}
		}
		return null;
	}
	
	public AghWFExecutor obterExecutorEtapa(AghWFEtapa etapa){
		for (AghWFExecutor aghWFExecutor : etapa.getExecutores()) {
			if(aghWFExecutor.getRapServidor().equals(servidorLogadoFacade.obterServidorLogado())) {
				return aghWFExecutor;
			}
		}
		return null;
	}
	
	public void cancelarFluxoAutorizacaoOPMEs(RapServidores servidor, AghWFFluxo fluxo, String justificativa, MbcRequisicaoOpmes requisicao) throws BaseException {
		AghWFExecutor executorCancelamento = new AghWFExecutor();
		if(fluxo != null){
			AghWFExecutor executorCriadorReq = obterExecutorPorFluxo(requisicao.getRapServidores(), fluxo);

			fluxo = aghWFFluxoDAO.obterPorChavePrimaria(fluxo.getSeq());
			
			AghWFTemplateFluxo templateFluxo = fluxo.getTemplateFluxo();
			
			AghWFTemplateEtapa templateEtapa = obterTemplateEtapaPorCodigo(DominioSituacaoRequisicao.CANCELADA.toString(), templateFluxo);
			AghWFEtapa etapa = fluxo.getEtapas().get(fluxo.getEtapas().size() - 1);
			
			AghWFEtapa etapaCancelamento = getWorkflowFacade().adicionarEtapa(etapa, etapa.getFluxo(), templateEtapa, executorCriadorReq);
			
			
			executorCancelamento.setEtapa(etapaCancelamento);
			executorCancelamento.setTemplateEtapa(etapaCancelamento.getTemplateEtapa());
			executorCancelamento.setTemplateFluxo(fluxo.getTemplateFluxo());
			executorCancelamento.setFluxo(fluxo);
			executorCancelamento.setIndRecebeNotificacao(Boolean.TRUE);
			executorCancelamento.setRapServidor(servidor);
			
			getWorkflowFacade().adicionarExecutor(executorCancelamento,requisicao);
			
			getWorkflowFacade().executarEtapaCancelamentoFluxo(executorCancelamento, etapaCancelamento, "",requisicao);
			getWorkflowFacade().cancelarFluxo(executorCancelamento, etapaCancelamento.getFluxo(), "");
		}
		
		
		requisicao.setDataFim(new Date());
		atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.CANCELADA);
		
		List<String> emails = getAghWFExecutorDAO().obterEmailsExecutores(requisicao.getSeq());
		CancelamentoOpmeVO info = getMbcRequisicaoOpmesDAO().obterInformacoesEmail(requisicao.getSeq());
		
		if(fluxo != null){
			getEmailUtil().enviaEmail(executorCancelamento.getRapServidor().getEmail(), emails, null, getAssuntoEmailCancelamento(info), getMessageEmailCancelamento(info));
		}else{
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			if(servidorLogado.getEmail() != null && info != null){
				getEmailUtil().enviaEmail(servidorLogado.getEmail(), emails, null, getAssuntoEmailCancelamento(info), getMessageEmailCancelamento(info));
			}
		}
	}
	
	public void cancelarOpmeSemFluxo(MbcRequisicaoOpmes requisicao){
		requisicao.setDataFim(new Date());
		atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.CANCELADA);
	}
	/**
	 * @return the emailUtil
	 */
	public EmailUtil getEmailUtil() {
		return new EmailUtil();
	}
	
	private String getAssuntoEmailCancelamento(CancelamentoOpmeVO cancelInfo) {
		return "Requisição de OPME Cancelada, Requerente " + cancelInfo.getRequerente();
	}

	private String getMessageEmailCancelamento(CancelamentoOpmeVO cancelInfo) {
		StringBuffer message = new StringBuffer(200);
		message.append("Requisição de OPME Cancelada"
				+ QUEBRA_LINHA
				+ QUEBRA_LINHA
				+ "Requerente: " +cancelInfo.getRequerente()
				+ QUEBRA_LINHA
				+ QUEBRA_LINHA
				+ "Maiores detalhes podem ser visualizados através da tela de acompanhamento de requisições de materiais especiais.");
		return message.toString();
	}

	private AghWFExecutor obterExecutorPorFluxo(RapServidores servidor,
			AghWFFluxo fluxo) {
		List<AghWFExecutor> executores = getWorkflowFacade().obterExecutoresPorFluxo(servidor, fluxo.getSeq());
		AghWFExecutor executor = null;
		if (!executores.isEmpty()) {
			int index = executores.size() - 1;
			executor =   executores.get(index);
		}
		return executor;
	}
	
	private void atualizarSituacaoRequisicao(MbcRequisicaoOpmes requisicao, DominioSituacaoRequisicao situacao) {
		if(requisicao.getSeq() != null){
			requisicao.setSituacao(situacao);
			requisicao = getMbcRequisicaoOpmesDAO().merge(requisicao);
			getMbcRequisicaoOpmesDAO().atualizar(requisicao);
			this.executarAcaoJournal(requisicao, DominioOperacoesJournal.UPD);
			getMbcRequisicaoOpmesDAO().flush();
		}
	}
	
	private void atualizarItensRequisicao(List <MbcItensRequisicaoOpmes> itensRequisicao) {
		for (MbcItensRequisicaoOpmes item : itensRequisicao) {
			item.setIndAutorizado(Boolean.TRUE);
			item.setQuantidadeAutorizadaHospital(item.getQuantidadeSolicitada().intValue());

			getMbcItensRequisicaoOpmesDAO().atualizar(item);
			blocoCirurgicoOpmesFacade.insereMbcItensRequisicaoOpmesJn(item, DominioOperacoesJournal.UPD);
		}
		getMbcItensRequisicaoOpmesDAO().flush();
		
	}
	public void executarEtapaFluxoAutorizacaoOPMEs(RapServidores servidor, AghWFEtapa etapa, String observacao) throws BaseException {
		
		AghWFEtapa etapaEntity = this.aghWFEtapaDAO.obterPorChavePrimaria(etapa.getSeq());
		
		AghWFExecutor executor = getWorkflowFacade().obterExecutorPorRapServidor(servidor, etapaEntity.getSeq());
		if (executor.getAutorizadoExecutar()) {
			// Obtem a requisicao relacionada ao fluxo
			MbcRequisicaoOpmes requisicao = getMbcRequisicaoOpmesDAO().obterMbcRequisicaoOpmesPorFluxo(
					etapaEntity.getFluxo().getSeq());
			
			getWorkflowFacade().executarEtapa(executor, etapaEntity, observacao, Boolean.TRUE,requisicao);
			
			
			avaliarRotasCriterios(etapaEntity, executor, requisicao, servidor.getUsuario());
		}else{
			throw new ApplicationBusinessException(WorkflowAutorizacaoRequisicaoOPMEExceptionCode.MENSAGEM_EXECUTOR_NAO_AUTORIZADO);
		}
	}
	
	private void executarEtapaFluxoAutorizacaoOPMEs(AghWFExecutor executor, AghWFEtapa etapa, String observacao, MbcRequisicaoOpmes requisicao, Boolean salvaHistorico, String usuario) throws BaseException {
		getWorkflowFacade().executarEtapa(executor, etapa, observacao, salvaHistorico,requisicao);
		avaliarRotasCriterios(etapa, executor, requisicao, usuario);
	}


	private void avaliarRotasCriterios(AghWFEtapa etapaOrigem, AghWFExecutor executor, MbcRequisicaoOpmes requisicao, String usuario) throws ApplicationBusinessException {
		//Busca as rotas para etapa origem
		List<AghWFRota> rotas =  getWorkflowFacade().recuperarRotas(etapaOrigem);
		BigDecimal totalExcedidoRequisicao = calcularTotalExcedenteRequisicao(requisicao.getItensRequisicao());
		//Para cada rota
		for (AghWFRota aghWFRota : rotas) {
			//Valida os criterios para as etapas destino, após encontrar o primeiro critério verdadeiro, 
			//escolhe a sua etapa destino pra adicionar no fluxo, junto com seus executores
			AghWFRotaCriterio criterio =  aghWFRota.getRotaCriterio();
			//Obtem o template da etapa destino
			AghWFTemplateEtapa templateEtapaDestino = aghWFRota.getTemplateEtapaDestino();
			if (DominioWorkflowOPMEsCodigoCriterio.MAT_NOVO.toString().equals(criterio.getCodigo())) {
				//Se criterio for MAT_NOVO
				//Verifica se existe algum material ainda não licitado
				if(!avaliarCriterio(etapaOrigem, templateEtapaDestino, null, requisicao, executor, totalExcedidoRequisicao)){
					cadastrarExecutoresDeAcordoComEtapaDestino(etapaOrigem, requisicao, templateEtapaDestino, executor);
					break;
				}
			} else if (DominioWorkflowOPMEsCodigoCriterio.NAO_HA.toString().equals(criterio.getCodigo())) {
				//Se nao ha criterio para etapa destino, cadastra os executores
				cadastrarExecutoresDeAcordoComEtapaDestino(etapaOrigem, requisicao,templateEtapaDestino, executor);
				break;
			} else if(DominioWorkflowOPMEsCodigoCriterio.ALCADA_1.toString().equals(criterio.getCodigo())) {
				//Se criterio e alcada_1 passa o valor esperado 1
				if(avaliarCriterio(etapaOrigem, templateEtapaDestino, 1, requisicao, executor, totalExcedidoRequisicao)) {
					break;
				}
			} else if(DominioWorkflowOPMEsCodigoCriterio.ALCADA_2.toString().equals(criterio.getCodigo())) {
				//Se criterio e alcada_2 passa o valor esperado 2
				if(avaliarCriterio(etapaOrigem, templateEtapaDestino, 2, requisicao, executor, totalExcedidoRequisicao)) {
					break;
				}
			} else if(DominioWorkflowOPMEsCodigoCriterio.ALCADA_3.toString().equals(criterio.getCodigo())) {
				//Se criterio e alcada_2 passa o valor esperado 2
				if(avaliarCriterio(etapaOrigem, templateEtapaDestino, 3, requisicao, executor, totalExcedidoRequisicao)) {
					break;
				}
			} else if(DominioWorkflowOPMEsCodigoCriterio.REQ_AUT.toString().equals(criterio.getCodigo())) {
				//Se criterio e REQ_AUT passa o valor esperado 6
				if(avaliarCriterio(etapaOrigem, templateEtapaDestino, 6, requisicao, executor, totalExcedidoRequisicao)) {
					break;
				}
			}
		}
	}

	private boolean avaliarCriterio(AghWFEtapa etapa, AghWFTemplateEtapa templateEtapaDestino, Integer alcadaEsperado, MbcRequisicaoOpmes requisicao, AghWFExecutor executor, BigDecimal totalExcedidoRequisicao) throws ApplicationBusinessException {
		boolean alcadaUmEMatNovo = false;
		Integer DOIS = 2;
		Integer UM = 1;
		
		Integer nivelAlcada = getMbcAlcadaAvalOpmsDAO().buscarNivelAlcadaPorEspecialidadeValorMaterial(requisicao.getAgendas().getEspecialidade().getSeq(), totalExcedidoRequisicao);
		
		//Verifica material novo
		Boolean contemMaterialNovo = Boolean.FALSE;
		for(MbcItensRequisicaoOpmes item : requisicao.getItensRequisicao()){
	
			if(item.getRequerido().equals(DominioRequeridoItemRequisicao.NOV)){
				if(item.getValorNovoMaterial() == null){
					contemMaterialNovo = Boolean.TRUE;
				}else if(DOIS.equals(alcadaEsperado) && UM.equals(nivelAlcada)){
					alcadaUmEMatNovo = true;
				}
			}
		}		
		if(contemMaterialNovo){
			return false;
		}
		
		if (totalExcedidoRequisicao == null || totalExcedidoRequisicao.doubleValue() == 0.00) {
			return false;
		}
		
		
		if((nivelAlcada != null && nivelAlcada.intValue() == alcadaEsperado.intValue()) || alcadaUmEMatNovo) {
		//if((nivelAlcada != null && nivelAlcada.intValue() == alcadaEsperado.intValue())) {	
			cadastrarExecutoresDeAcordoComEtapaDestino(etapa, requisicao, templateEtapaDestino, executor);
			return true;
		}
		return false;
	}
	
	private void verificarAutorizacaoUnanime(AghWFEtapa etapa, MbcRequisicaoOpmes requisicao, AghWFTemplateEtapa templateEtapaDestino, AghWFExecutor executor) throws ApplicationBusinessException{
		List<RapServidores> servidoresAutorizados = new ArrayList<RapServidores>();
		List<RapServidores> servidoresExecutores = new ArrayList<RapServidores>();
		
		List<AghWFExecutor> executores = aghWFExecutorDAO.buscarExecutoresUnanimes(etapa);
		if(executores != null){
			if(executores.size() == 1){
				AghWFEtapa etapaDestino = adicionarEtapaExecutorPorRequisicao(etapa, requisicao, templateEtapaDestino, executor);
				autorizarRequisicao(etapa, requisicao, templateEtapaDestino, executor, etapaDestino);
			}else if(executores.size() > 1){
				List<AghWFHistoricoExecucao> autorizados = aghWFHistoricoExecucaoDAO.buscarHistExecutoresAutorizacao(etapa);
				if(autorizados != null){
					if(autorizados.size() > 0){
						for (AghWFHistoricoExecucao item : autorizados) {
							servidoresAutorizados.add(item.getExecutor().getRapServidor());
						}
						for (AghWFExecutor item : executores) {
							servidoresExecutores.add(item.getRapServidor());
						}
						for (RapServidores item : servidoresAutorizados) {
							if(servidoresExecutores.contains(item)){
								servidoresExecutores.remove(item);
							}
						}
						if(servidoresExecutores.size() == 0){
							AghWFEtapa etapaDestino = adicionarEtapaExecutorPorRequisicao(etapa, requisicao, templateEtapaDestino, executor);
							autorizarRequisicao(etapa, requisicao, templateEtapaDestino, executor, etapaDestino);
						}
					}
				}
			}
		}
	}
	
	
	private void cadastrarExecutoresDeAcordoComEtapaDestino(AghWFEtapa etapa, MbcRequisicaoOpmes requisicao, AghWFTemplateEtapa templateEtapaDestino, AghWFExecutor executor) throws ApplicationBusinessException {
		Boolean orcamento = false;
		requisicao.getItensRequisicao();
		for (MbcItensRequisicaoOpmes item : requisicao.getItensRequisicao()) {
			if(DominioRequeridoItemRequisicao.NOV.equals(item.getRequerido())){
				 orcamento = true;
			}
		}
		if (DominioWorkflowOPMEsCodigoTemplateEtapa.EM_AUTORIZACAO_01.toString().equals(templateEtapaDestino.getCodigo())) {
			//C01_ALCADA_1
			adicionarEtapaExecutores(etapa, requisicao,	templateEtapaDestino, 1, executor);
			atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.EM_AUTORIZACAO_01);
		} else if (DominioWorkflowOPMEsCodigoTemplateEtapa.EM_AUTORIZACAO_02.toString().equals(templateEtapaDestino.getCodigo()))  {
			//C01_ALCADA_2
			adicionarEtapaExecutores(etapa, requisicao,	templateEtapaDestino, 2, executor);
			atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.EM_AUTORIZACAO_02);
		} else if (DominioWorkflowOPMEsCodigoTemplateEtapa.EM_ORCAMENTO_01.toString().equals(templateEtapaDestino.getCodigo()) && orcamento)  {
			//C04_ORCAMENTO
			getWorkflowFacade().adicionarEtapaExecutores(etapa, etapa.getFluxo(), templateEtapaDestino, executor, requisicao);
			atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.EM_ORCAMENTO_01);
		} else if (DominioWorkflowOPMEsCodigoTemplateEtapa.EM_AUTORIZACAO_03.toString().equals(templateEtapaDestino.getCodigo()))  {
			//C02_ALCADA_2
			adicionarEtapaExecutores(etapa, requisicao,	templateEtapaDestino, 2,executor);
			atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.EM_AUTORIZACAO_03);
		}  else if (DominioWorkflowOPMEsCodigoTemplateEtapa.EM_AUTORIZACAO_04.toString().equals(templateEtapaDestino.getCodigo()))  {
			//C02_ALCADA_3
			adicionarEtapaExecutores(etapa, requisicao,	templateEtapaDestino, 3, executor);
			atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.EM_AUTORIZACAO_04);
		} else if (DominioWorkflowOPMEsCodigoTemplateEtapa.AUTORIZADA.toString().equals(templateEtapaDestino.getCodigo())) {
			if(etapa.getIndUnanime()){
				verificarAutorizacaoUnanime(etapa,requisicao,templateEtapaDestino,executor);
			}else{
				AghWFEtapa etapaDestino = adicionarEtapaExecutorPorRequisicao(etapa, requisicao, templateEtapaDestino, executor);
				autorizarRequisicao(etapa, requisicao, templateEtapaDestino, executor,etapaDestino);
			}
		// #31958	
		}  else if (DominioWorkflowOPMEsCodigoTemplateEtapa.PREPARAR_OPM.toString().equals(templateEtapaDestino.getCodigo()))  {
			//PREPARAR_OPM
			adicionarEtapaExecutores(etapa, requisicao,	templateEtapaDestino, 6, executor);
			atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.PREPARAR_OPM);
			
		} else if (DominioWorkflowOPMEsCodigoTemplateEtapa.NAO_AUTORIZADA.toString().equals(templateEtapaDestino.getCodigo())) {
			adicionarEtapaExecutorPorRequisicao(etapa, requisicao, templateEtapaDestino, executor);
			atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.NAO_AUTORIZADA);
		} else if (DominioWorkflowOPMEsCodigoTemplateEtapa.CONCLUIDA.toString().equals(templateEtapaDestino.getCodigo())) {
			adicionarEtapaExecutorPorRequisicao(etapa, requisicao, templateEtapaDestino, executor);
			atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.CONCLUIDA);
		} else if (DominioWorkflowOPMEsCodigoTemplateEtapa.CANCELADA.toString().equals(templateEtapaDestino.getCodigo())) {
			adicionarEtapaExecutorPorRequisicao(etapa, requisicao, templateEtapaDestino, executor);
			atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.CANCELADA);
		}
	}

	private void autorizarRequisicao(AghWFEtapa etapa, MbcRequisicaoOpmes requisicao, AghWFTemplateEtapa templateEtapaDestino, AghWFExecutor executor,AghWFEtapa etapaDestino) throws ApplicationBusinessException {
		getWorkflowFacade().adicionarPendenciaAutorizacao(etapa, requisicao, templateEtapaDestino, executor, etapaDestino);
		requisicao.setIndAutorizado(Boolean.TRUE);
		// TODO ALTERACAO
		requisicao.setDataFim(new Date());
		atualizarSituacaoRequisicao(requisicao, DominioSituacaoRequisicao.AUTORIZADA);
		atualizarItensRequisicao(requisicao.getItensRequisicao());
	}

	
	private AghWFEtapa adicionarEtapaExecutorPorRequisicao(AghWFEtapa etapa, MbcRequisicaoOpmes requisicao, AghWFTemplateEtapa templateEtapaDestino, AghWFExecutor executor) throws ApplicationBusinessException {
		AghWFEtapa etapaDestino = getWorkflowFacade().adicionarEtapa(etapa, etapa.getFluxo(), templateEtapaDestino, executor);
		AghWFExecutor executorNovo = new AghWFExecutor();
		executorNovo.setEtapa(etapaDestino);
		executorNovo.setFluxo(etapaDestino.getFluxo());
		executorNovo.setIndRecebeNotificacao(Boolean.TRUE);
		executorNovo.setRapServidor(requisicao.getRapServidores());
		executorNovo.setTemplateEtapa(templateEtapaDestino);
		executorNovo.setTemplateFluxo(etapaDestino.getTemplateFluxo());
		getWorkflowFacade().adicionarExecutor(executorNovo,requisicao);
		return etapaDestino;
	}
	
//	private void adicionarEtapaTodosExecutoresEnvolvidos(AghWFEtapa etapa,	MbcRequisicaoOpmes requisicao, AghWFTemplateEtapa templateEtapaDestino, AghWFExecutor executor) throws ApplicationBusinessException {
//		AghWFEtapa etapaDestino = getWorkflowFacade().adicionarEtapa(etapa, etapa.getFluxo(), templateEtapaDestino, executor);
//
//		AghWFExecutor executorNovo = new AghWFExecutor();
//		executorNovo.setEtapa(etapaDestino);
//		executorNovo.setFluxo(etapaDestino.getFluxo());
//		executorNovo.setIndRecebeNotificacao(Boolean.TRUE);
//		executorNovo.setRapServidor(requisicao.getRapServidores());
//		executorNovo.setTemplateEtapa(templateEtapaDestino);
//		executorNovo.setTemplateFluxo(etapaDestino.getTemplateFluxo());
//		
//		getWorkflowFacade().adicionarExecutor(executorNovo,requisicao);
//	}
	
	private void adicionarEtapaTodosExecutoresEnvolvidosNaoAutorizadas(AghWFEtapa etapa,	MbcRequisicaoOpmes requisicao, AghWFTemplateEtapa templateEtapaDestino, AghWFExecutor executor) throws ApplicationBusinessException {
		AghWFEtapa etapaDestino = getWorkflowFacade().adicionarEtapa(etapa, etapa.getFluxo(), templateEtapaDestino, executor);
		etapaDestino.getTemplateEtapa().setDescricao("Requisição Não Autorizada"); 
		AghWFExecutor executorNovo = new AghWFExecutor();
		executorNovo.setEtapa(etapaDestino);
		executorNovo.setFluxo(etapaDestino.getFluxo());
		executorNovo.setIndRecebeNotificacao(Boolean.TRUE);
		executorNovo.setRapServidor(requisicao.getRapServidores());
		executorNovo.setTemplateEtapa(templateEtapaDestino);
		executorNovo.setTemplateFluxo(etapaDestino.getTemplateFluxo());
		
		getWorkflowFacade().adicionarExecutor(executorNovo,requisicao);
	}
	
	private void adicionarEtapaExecutores(AghWFEtapa etapa,	MbcRequisicaoOpmes requisicao, AghWFTemplateEtapa templateEtapaDestino, Integer alcada, AghWFExecutor executor) throws ApplicationBusinessException {
		AghWFEtapa etapaDestino = getWorkflowFacade().adicionarEtapa(etapa, etapa.getFluxo(), templateEtapaDestino, executor);
		List<MbcServidorAvalOpms> servidores = getMbcServidorAvalOpmsDAO().buscarServidoresPorEspConvenio(requisicao.getAgendas().getEspecialidade().getSeq(), alcada);
		MbcGrupoAlcadaAvalOpms grupoAlcada = null;  
		if (servidores != null) {
			if (requisicao.getGrupoAlcadaAvalOpms() == null) {
				if(servidores.size() > 0){
					if(servidores.get(0) != null){
						grupoAlcada = servidores.get(0).getAlcada().getGrupoAlcada();
						requisicao.setGrupoAlcadaAvalOpms(grupoAlcada);
					}
				}
			}
		}
		List<AghWFExecutor> executores = new LinkedList<AghWFExecutor>();
		for (MbcServidorAvalOpms mbcServidorAvalOpms : servidores) {
			if(mbcServidorAvalOpms != null){
				AghWFExecutor novoExecutor = new AghWFExecutor();
				novoExecutor.setEtapa(etapaDestino);
				novoExecutor.setFluxo(etapaDestino.getFluxo());
				novoExecutor.setIndRecebeNotificacao(Boolean.TRUE);
				novoExecutor.setRapServidor(mbcServidorAvalOpms.getRapServidores());
				novoExecutor.setTemplateEtapa(templateEtapaDestino);
				novoExecutor.setTemplateFluxo(etapaDestino.getTemplateFluxo());
				//observador não está autorizado a executar
				novoExecutor.setAutorizadoExecutar(mbcServidorAvalOpms.getResponsabilidade() != DominioTipoResponsabilidadeOPMS.OBSERVADOR);
				executores.add(novoExecutor);
			}
		}
		if(executores.size() > 0){
			getWorkflowFacade().adicionarExecutores(executores,requisicao);
		}
	}
	

	private BigDecimal calcularTotalExcedenteRequisicao(List<MbcItensRequisicaoOpmes>  itensRequisicao) {
		BigDecimal totalSolicitado = BigDecimal.ZERO;
		BigDecimal totalAutorizadoSUS = BigDecimal.ZERO;
		BigDecimal resultado = null;
		for (MbcItensRequisicaoOpmes item : itensRequisicao) {
			BigDecimal valorUnitIph = item.getValorUnitarioIph();
			Integer qtdSolicIph = item.getQuantidadeSolicitada();
			Short qtdAutSusIph = item.getQuantidadeAutorizadaSus();
			
			if(!item.getIndCompativel()) {
				if (valorUnitIph == null) {
					return null;
				}				
				if(valorUnitIph!= null && qtdSolicIph !=null) {
					totalSolicitado = totalSolicitado.add(valorUnitIph.multiply(new BigDecimal(qtdSolicIph)));
				}
				if(qtdSolicIph > qtdAutSusIph){
					if(valorUnitIph!= null && qtdAutSusIph !=null) {
						totalAutorizadoSUS = totalAutorizadoSUS.add(valorUnitIph.multiply(new BigDecimal(qtdAutSusIph)));
					}
				}
			}
		}
		resultado = totalSolicitado.subtract(totalAutorizadoSUS);
		return resultado;
	}
	
	
	private void associarFluxoRequisicao(MbcRequisicaoOpmes requisicao,	AghWFFluxo fluxo) {
		requisicao.setFluxo(fluxo);
		getMbcRequisicaoOpmesDAO().atualizar(requisicao);
		this.executarAcaoJournal(requisicao, DominioOperacoesJournal.UPD);
		getMbcRequisicaoOpmesDAO().flush();
	}
	
	private MbcRequisicaoOpmesJnDAO getMbcRequisicaoOpmesJnDAO(){
		return mbcRequisicaoOpmesJnDAO;
	}
	
	public void executarAcaoJournal(MbcRequisicaoOpmes mbcRequisicaoOpmes, DominioOperacoesJournal tipoAcao){
		mbcRequisicaoOpmes = mbcRequisicaoOpmesDAO.obterPorChavePrimaria(mbcRequisicaoOpmes.getSeq());
		MbcRequisicaoOpmesJn journal = this.bindJournal(mbcRequisicaoOpmes);
		journal.setOperacao(tipoAcao);
		journal.setNomeUsuario(obterLoginUsuarioLogado());
		this.getMbcRequisicaoOpmesJnDAO().persistir(journal);
	}
	
	private MbcRequisicaoOpmesJn bindJournal(MbcRequisicaoOpmes opme) {
		
		return new MbcRequisicaoOpmesJn(opme.getSeq()
				, opme.getAgendas()
				, opme.getCirurgia()
				, opme.getSituacao().toString()
				, opme.getObservacaoOpme(),
				opme.getJustificativaRequisicaoOpme(),
				opme.getJustificativaConsumoOpme(), 
				opme.getCriadoEm(), 
				opme.getModificadoEm(),
				opme.getRapServidores(),
				opme.getRapServidoresModificacao(), 
				opme.getIndCompativel(),
				opme.getIndAutorizado(), 
				opme.getIndConsAprovacao(), 
				opme.getDataFim(),
				opme.getFluxo());
	}
	
	private MbcGrupoAlcadaDAO getMbcGrupoAlcadaDAO(){
		return mbcGrupoAlcadaDAO;
	}
	
}
