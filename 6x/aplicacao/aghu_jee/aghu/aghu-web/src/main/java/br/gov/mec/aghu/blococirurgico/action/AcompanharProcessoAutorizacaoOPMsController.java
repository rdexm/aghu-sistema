package br.gov.mec.aghu.blococirurgico.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.picketbox.util.StringUtil;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.action.DetalhamentoPortalAgendamentoController;
import br.gov.mec.aghu.blococirurgico.vo.ConsultarHistoricoProcessoOpmeVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.EspecialidadeVO;
import br.gov.mec.aghu.blococirurgico.vo.ExecutorEtapaAtualVO;
import br.gov.mec.aghu.blococirurgico.vo.RequerenteVO;
import br.gov.mec.aghu.blococirurgico.vo.RequisicoesOPMEsProcedimentosVinculadosVO;
import br.gov.mec.aghu.blococirurgico.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.blococirurgico.workflow.business.IWorkflowFacade;
import br.gov.mec.aghu.dominio.DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.dominio.DominioWorkflowOPMEsCodigoTemplateEtapa;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AcompanharProcessoAutorizacaoOPMsController extends ActionController {

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	private static final long serialVersionUID = 4296098383197650940L;
	
	private static final String PAGE_GERENCIAR_REQUISICAO_AUTORIZACAO_OPMES = "blococirurgico-requisicaoAutorizacaoOPMs";
	private static final String PAGE_SOLICITAR_RECEBER_ORCAMENTO_MATERIAIS = "blococirurgico-solicitarReceberOrcMatNaoLicitados";
	private static final String PAGE_AGENDA_PROCEDIMENTOS = "blococirurgico-detalhamentoAgendaProcedimentos";
	private static final String PAGE_DETALHAMENTO_PORTAL_AGENDAMENTO = "blococirurgico-detalhamentoPortalAgendamento";
	private static final String PAGE_CONSULTA_PREPARA_OPME = "blococirurgico-consultaPreparaOPME";
	
	private List<RequisicoesOPMEsProcedimentosVinculadosVO> listaProcessoAutorizacaoOPMEs;
	private RequisicoesOPMEsProcedimentosVinculadosVO itemListaAutorizacao;
	private List<ConsultarHistoricoProcessoOpmeVO> listaHistoricoVO;
	private RequisicoesOPMEsProcedimentosVinculadosVO requisicoesOPMEsProcedimentosVinculadosSelecionado;
	private List<ConsultarHistoricoProcessoOpmeVO> consultarHistoricoProcessoOpmeSelecionado;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;
	
	@EJB
	private IWorkflowFacade workFlowFacade;
		
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private Date dataRequisicao;
	private Date dataProcedimento;
	private RequerenteVO requerente;
	private ExecutorEtapaAtualVO executorEtapaAtual;
	private UnidadeFuncionalVO unidadeFuncional;
	private EspecialidadeVO especialidade;
	private EquipeVO equipe;
	private Integer prontuario;
	private Boolean pesquisarRequisicao = Boolean.FALSE;
	private Integer nrDias;
	private Integer etapa;
	private Short requisicaoSeqSelecionada;
	private Short requisicaoSeq;

	private DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa itemEtapasRequisicao;
	private Boolean executarIniciar = Boolean.FALSE;
	private String voltarParaUrl;
	
	private Integer executorSeq; 
	private Integer etapaSeq;
	
	public void iniciar() throws ApplicationBusinessException {
		
		if(!StringUtil.isNullOrEmpty(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("etapa"))){
			this.etapaSeq = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("etapa"));
		}
		
		if(!StringUtil.isNullOrEmpty(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("executor"))){
			this.executorSeq = Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("executor"));
		}
		
		if(!StringUtil.isNullOrEmpty(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("executarIniciar"))){
			this.executarIniciar = Boolean.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("executarIniciar").toString());
		}
		
		if(!StringUtil.isNullOrEmpty(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("seq"))){
			this.requisicaoSeq = Short.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("seq").toString());
		}
		
		if(executarIniciar) {
			this.listaProcessoAutorizacaoOPMEs = listar();
			conferirPermissaoBotaoOrcamento(listaProcessoAutorizacaoOPMEs);
			//this.requisicaoSeq = null;
			executorSeq = null;
			etapaSeq = null;
		}
	
	}
	
	private RapServidores getServidorLogado() throws ApplicationBusinessException{
		return this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
	}
	
	private void conferirPermissaoBotaoOrcamento( List<RequisicoesOPMEsProcedimentosVinculadosVO> itens) throws ApplicationBusinessException {
		RapServidores servidorLogado = this.getServidorLogado();
		for (RequisicoesOPMEsProcedimentosVinculadosVO requisicaoDetalhe : itens) {
			
			ExecutorEtapaAtualVO executorEtapa = this.blocoCirurgicoOpmesFacade.pesquisarExecutorEtapaAtualProcesso(requisicaoDetalhe.getRequisicaoSeq(), servidorLogado);
			
			if ((DominioSituacaoRequisicao.EM_AUTORIZACAO_02.equals(requisicaoDetalhe.getSituacao())
			   || DominioSituacaoRequisicao.EM_ORCAMENTO_01.equals(requisicaoDetalhe.getSituacao())) && executorEtapa != null) {
				requisicaoDetalhe.setHabilitarBotaoOrcamento(Boolean.TRUE);
			}
		}
	}
	
	public void pesquisar() throws ApplicationBusinessException {
		this.requisicaoSeq = null;
		this.listaProcessoAutorizacaoOPMEs = listar();
		
		if(this.listaProcessoAutorizacaoOPMEs.isEmpty()){
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_CONSULTA_SEM_REGISTROS_01");
		}
		
		conferirPermissaoBotaoOrcamento(listaProcessoAutorizacaoOPMEs);
	}
	
	public List<RequisicoesOPMEsProcedimentosVinculadosVO> listar(){
		return this.blocoCirurgicoFacade.pesquisarRequisicaoOpmes(this.requisicaoSeq, this.dataRequisicao,
				this.requerente,
				this.itemEtapasRequisicao,
				this.executorEtapaAtual,
				this.dataProcedimento,
				this.unidadeFuncional,
				this.especialidade,
				this.equipe,
				this.prontuario,
				this.pesquisarRequisicao,
				this.nrDias,
				this.executorSeq,
				this.etapaSeq);
	}
	
	public void limparPesquisa(){
		this.setExecutorEtapaAtual(null);
		this.setDataRequisicao(null);
		this.setDataProcedimento(null);
		this.setRequerente(null);
		this.setEquipe(null);
		this.setUnidadeFuncional(null);
		this.setEspecialidade(null);
		this.setEquipe(null);
		this.setProntuario(null);
		this.setPesquisarRequisicao(Boolean.FALSE);
		this.setNrDias(null);
		this.setItemEtapasRequisicao(null);
		this.setListaProcessoAutorizacaoOPMEs(null);
		this.executorSeq = null;
		this.etapaSeq = null;
		this.requisicaoSeq = null;
	}
	
	public void iniciaProcessosOPME() {
		try {
			for (Integer seq : buscaDadosProcessoAutorizacaoOpme()) {
				try {
					blocoCirurgicoOpmesFacade.iniciarFluxoAutorizacaoOPMEs(this.getServidorLogado(),obterRequisicaoOpme(seq.shortValue()));
				} catch (BaseException e) {
					this.apresentarExcecaoNegocio(e);
				}
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	private List<Integer> buscaDadosProcessoAutorizacaoOpme() throws ApplicationBusinessException {
		return this.blocoCirurgicoOpmesFacade.buscaDadosProcessoAutorizacaoOpme();
	}
	
	private MbcRequisicaoOpmes obterRequisicaoOpme(Short seq) {
		return this.blocoCirurgicoOpmesFacade.obterRequisicaoOpme(seq);
	}
	
	public String gerenciarRequisicaoAutorizacao(RequisicoesOPMEsProcedimentosVinculadosVO gerenciarItem){
		this.setEtapa(gerenciarItem.getEtapaSeq());
		this.setItemListaAutorizacao(gerenciarItem);
		return PAGE_GERENCIAR_REQUISICAO_AUTORIZACAO_OPMES;
	}
	
	public String consultarAjustarProcedimento(RequisicoesOPMEsProcedimentosVinculadosVO consAjusProcItem){
		MbcAgendas agenda = null;
		try {
//			String page = this.blocoCirurgicoFacade.validarUsuarioSituacaoAjustarProcedimento(consAjusProcItem, obterLoginUsuarioLogado());
			
			agenda = this.blocoCirurgicoFacade.validarUsuarioSituacaoAjustarProcedimento(consAjusProcItem, obterLoginUsuarioLogado());
			
			//if(consAjusProcItem.getRequerenteMatricula().intValue() != this.getServidorLogado().getId().getMatricula().intValue() &&
			//		consAjusProcItem.getRequerenteVinCodigo().shortValue() != this.getServidorLogado().getId().getVinCodigo().shortValue()){
			//	apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_USUARIO_SEM_PERMISSAO_01");
			
			//} //else if(!DominioSituacaoRequisicao.NAO_AUTORIZADA.equals(consAjusProcItem.getSituacao())){
				//apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_USUARIO_SEM_PERMISSAO_02");
			//}
			
			if (agenda != null) {
				this.detalhamentoPortalAgendamentoController.setSalaCirurgica(agenda.getSalaCirurgica());
				this.detalhamentoPortalAgendamentoController.setUnidadeFuncional(agenda.getUnidadeFuncional());
				this.detalhamentoPortalAgendamentoController.setDataInicio(agenda.getDtAgenda());
				
				this.detalhamentoPortalAgendamentoController.setSalasCirurgicas(buscarSalasPorAgenda(agenda));
				this.detalhamentoPortalAgendamentoController.setCameFrom("blococirurgico-acompanharProcessoAutorizacaoOPMs");
				
			}
			
//			if(page.equalsIgnoreCase(PAGE_AGENDA_PROCEDIMENTOS)){
//				return PAGE_AGENDA_PROCEDIMENTOS;
//			} else if (page.equalsIgnoreCase(PAGE_DETALHAMENTO_PORTAL_AGENDAMENTO)){
//				return PAGE_DETALHAMENTO_PORTAL_AGENDAMENTO;
//			}
			
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
//			this.apresentarExcecaoNegocio(e);
			return null;
		} 
		
		if(agenda != null){
			if(agenda.getIndGeradoSistema()){
				return PAGE_AGENDA_PROCEDIMENTOS;
			} else {
				return PAGE_DETALHAMENTO_PORTAL_AGENDAMENTO;
			}
		}	
		
		return "";
	}
	
	public List<MbcSalaCirurgica> buscarSalasPorAgenda(MbcAgendas agenda){
		
		return this.blocoCirurgicoFacade.buscarSalasPorAgenda(agenda);
		
	}
	
	public String solicitarReceberOrcMat(RequisicoesOPMEsProcedimentosVinculadosVO item) {
		this.setItemListaAutorizacao(item);
		this.requisicaoSeqSelecionada = item.getRequisicaoSeq();
		this.etapa = item.getEtapaSeq();
		return PAGE_SOLICITAR_RECEBER_ORCAMENTO_MATERIAIS;
	}
	
	public void consultarHistorico(Integer seqFluxo){
		this.listaHistoricoVO =	this.blocoCirurgicoOpmesFacade.consultarHistorico(seqFluxo);
		
		if(this.listaHistoricoVO.isEmpty()){
			this.apresentarMsgNegocio(Severity.ERROR, "M01_SEM_REGISTROS");
		}else{
			openDialog("modalConsultarHistoricoProcessoWG");
		}
	
	}
	
	public String prepararOpmes(RequisicoesOPMEsProcedimentosVinculadosVO prepararOpme){
//		this.consultaPreparaOPMEController.setPrepararOpme(prepararOpme);
//		this.consultaPreparaOPMEController.setRequisicaoSeqSelecionada(prepararOpme.getRequisicaoSeq());
//		this.consultaPreparaOPMEController.setVoltarParaUrl(VOLTAR_TELA_ACOMPANHAMENTO);
		return PAGE_CONSULTA_PREPARA_OPME;
	}
	
	public String voltar() {
		return voltarParaUrl;
	}
	
	public DominioWorkflowOPMEsCodigoTemplateEtapa[] listaItensEtapasRequisicao(){
		return DominioWorkflowOPMEsCodigoTemplateEtapa.values();
	}
	
	public List<RequerenteVO> consultarRequerentes(String requerente) {
		return this.blocoCirurgicoFacade.consultarRequerentes(requerente);
	}
	
	public List<ExecutorEtapaAtualVO> consultarExecutoresEtapaAtual(String executor) {
		return this.blocoCirurgicoFacade.consultarExecutoresEtapaAtual(executor);
	}
	
	public List<UnidadeFuncionalVO> pesquisarUnidadeFuncional(String unidade){
		return this.blocoCirurgicoFacade.pesquisarUnidadeFuncional(unidade);
	}
	
	public List<EspecialidadeVO> pesquisarEspecialidade(String especialidade){
		return this.blocoCirurgicoFacade.pesquisarEspecialidade(especialidade);
	}
	
	public List<EquipeVO> pesquisarEquipe(String equipe){
		return this.blocoCirurgicoFacade.pesquisarEquipe(equipe);
	}
	
	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IWorkflowFacade getWorkFlowFacade() {
		return workFlowFacade;
	}

	public void setWorkFlowFacade(IWorkflowFacade workFlowFacade) {
		this.workFlowFacade = workFlowFacade;
	}

	public List<RequisicoesOPMEsProcedimentosVinculadosVO> getListaProcessoAutorizacaoOPMEs() {
		return listaProcessoAutorizacaoOPMEs;
	}

	public void setListaProcessoAutorizacaoOPMEs(List<RequisicoesOPMEsProcedimentosVinculadosVO> listaProcessoAutorizacaoOPMEs) {
		this.listaProcessoAutorizacaoOPMEs = listaProcessoAutorizacaoOPMEs;
	}

	public RequerenteVO getRequerente() {
		return requerente;
	}

	public void setRequerente(RequerenteVO requerente) {
		this.requerente = requerente;
	}

	public ExecutorEtapaAtualVO getExecutorEtapaAtual() {
		return executorEtapaAtual;
	}

	public void setExecutorEtapaAtual(ExecutorEtapaAtualVO executorEtapaAtual) {
		this.executorEtapaAtual = executorEtapaAtual;
	}
	
	public UnidadeFuncionalVO getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(UnidadeFuncionalVO unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public EspecialidadeVO getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(EspecialidadeVO especialidade) {
		this.especialidade = especialidade;
	}

	public EquipeVO getEquipe() {
		return equipe;
	}

	public void setEquipe(EquipeVO equipe) {
		this.equipe = equipe;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Boolean getPesquisarRequisicao() {
		return pesquisarRequisicao;
	}

	public void setPesquisarRequisicao(Boolean pesquisarRequisicao) {
		this.pesquisarRequisicao = pesquisarRequisicao;
	}

	public Integer getNrDias() {
		return nrDias;
	}

	public void setNrDias(Integer nrDias) {
		this.nrDias = nrDias;
	}

	public Date getDataRequisicao() {
		return dataRequisicao;
	}

	public void setDataRequisicao(Date dataRequisicao) {
		this.dataRequisicao = dataRequisicao;
	}

	public Date getDataProcedimento() {
		return dataProcedimento;
	}

	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}

	public DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa getItemEtapasRequisicao() {
		return itemEtapasRequisicao;
	}

	public void setItemEtapasRequisicao(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa itemEtapasRequisicao) {
		this.itemEtapasRequisicao = itemEtapasRequisicao;
	}

	public RequisicoesOPMEsProcedimentosVinculadosVO getItemListaAutorizacao() {
		return itemListaAutorizacao;
	}

	public void setItemListaAutorizacao(RequisicoesOPMEsProcedimentosVinculadosVO itemListaAutorizacao) {
		this.itemListaAutorizacao = itemListaAutorizacao;
	}

	public List<ConsultarHistoricoProcessoOpmeVO> getListaHistoricoVO() {
		return listaHistoricoVO;
	}

	public void setListaHistoricoVO(List<ConsultarHistoricoProcessoOpmeVO> listaHistoricoVO) {
		this.listaHistoricoVO = listaHistoricoVO;
	}

	public Integer getEtapa() {
		return etapa;
	}

	public void setEtapa(Integer etapa) {
		this.etapa = etapa;
	}

	public Short getRequisicaoSeqSelecionada() {
		return requisicaoSeqSelecionada;
	}

	public void setRequisicaoSeqSelecionada(Short requisicaoSeqSelecionada) {
		this.requisicaoSeqSelecionada = requisicaoSeqSelecionada;
	}

	public void setExecutarIniciar(Boolean executarIniciar) {
		this.executarIniciar = executarIniciar;
	}

	public Boolean getExecutarIniciar() {
		return executarIniciar;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setRequisicaoSeq(Short requisicaoSeq) {
		this.requisicaoSeq = requisicaoSeq;
	}

	public Short getRequisicaoSeq() {
		return requisicaoSeq;
	}

	public RequisicoesOPMEsProcedimentosVinculadosVO getRequisicoesOPMEsProcedimentosVinculadosSelecionado() {
		return requisicoesOPMEsProcedimentosVinculadosSelecionado;
	}

	public void setRequisicoesOPMEsProcedimentosVinculadosSelecionado(
			RequisicoesOPMEsProcedimentosVinculadosVO requisicoesOPMEsProcedimentosVinculadosSelecionado) {
		this.requisicoesOPMEsProcedimentosVinculadosSelecionado = requisicoesOPMEsProcedimentosVinculadosSelecionado;
	}

	public List<ConsultarHistoricoProcessoOpmeVO> getConsultarHistoricoProcessoOpmeSelecionado() {
		return consultarHistoricoProcessoOpmeSelecionado;
	}

	public void setConsultarHistoricoProcessoOpmeSelecionado(
			List<ConsultarHistoricoProcessoOpmeVO> consultarHistoricoProcessoOpmeSelecionado) {
		this.consultarHistoricoProcessoOpmeSelecionado = consultarHistoricoProcessoOpmeSelecionado;
	}

}
