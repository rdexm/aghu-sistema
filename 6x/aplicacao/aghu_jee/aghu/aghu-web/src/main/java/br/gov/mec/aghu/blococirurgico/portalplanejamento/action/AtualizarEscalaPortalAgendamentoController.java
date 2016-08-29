package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.action.AcompanharProcessoAutorizacaoOPMsController;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacadeBean;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.AgendamentoProcedimentoCirurgicoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicao;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


public class AtualizarEscalaPortalAgendamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(AtualizarEscalaPortalAgendamentoController.class);
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 4845594282470451774L;
		
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IBlocoCirurgicoFacadeBean blocoCirurgicoFacadeBean;	
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	

	@Inject
	private JustificativaTransferirAgendamentoController justificativaTransferirAgendamentoController;
	
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	
	@Inject
	private TrocarLocalEspEquipeListaEsperaController trocarLocalEspEquipeListaEsperaController;
	
	private final String DETALHAMENTO_PORTAL = "detalhamentoPortalAgendamento";
	private final String REMARCAR_AGENDA_PLANEJADA = "remarcarEscalaAgendaPlanejada";
	private final String REMARCAR_AGENDA_ESCALA = "remarcarEscalaAgendaEscala";
	private final String ACOMPANHAR_PROCESSO_AUT_OPME = "blococirurgico-acompanharProcessoAutorizacaoOPMs";
	private final String TROCAR_LOCAL_ESP_EQUIPE_LISTA_ESPERA = "trocarLocalEspEquipeListaEspera";
	private final String PLANEJAMENTO_PACIENTE_AGENDA_CRUD = "planejamentoPacienteAgendaCRUD";
	private final String ATUALIZAR_ESCALA_PORTAL_PLANEJAMENTO = "blococirurgico-atualizarEscalaPortalPlanejamento";
	
	
	private Date horaInicio;
	private Date tempoTotal;

	private MbcSalaCirurgica salaCirurgica;
	private List<MbcSalaCirurgica> salasCirurgicas;
	private Boolean readOnlyTransferir = Boolean.TRUE;
	private Boolean readOnlyAlterarEsp = Boolean.TRUE;
	private Boolean readOnlyExclusao = Boolean.TRUE;
	private Boolean readOnlyTransferirEscalaAgenda = Boolean.TRUE;
	private Boolean habilitarOrdenacao = Boolean.TRUE;
	private Boolean readOnlyTransferirAgendaEscala = Boolean.TRUE;
	private Boolean renderedTransferirAgendaEscala = Boolean.TRUE;
	private Boolean habilitarTransferirEscalaAgenda = Boolean.TRUE;
	private String cameFromPlanejadoEscala;
	
	
	//parametros tela detalhamento
	private Long dataEscala;
	private Short unfSeq;
	private Short espSeq;
	private Short salaSeqp;
	private Short unfSeqSala;
	private Integer matriculaEquipe;
	private Short vinCodigoEquipe;
	private Short unfSeqEquipe;
	private String indFuncaoProfEquipe;
	private Short seqUnidFuncionalCirugica;
	private List<EscalaPortalPlanejamentoCirurgiasVO> planejados;
	private List<EscalaPortalPlanejamentoCirurgiasVO> escala;
	private List<EscalaPortalPlanejamentoCirurgiasVO> canceladosTransferencia;
	private List<EscalaPortalPlanejamentoCirurgiasVO> listaParaTransferir;
	private List<EscalaPortalPlanejamentoCirurgiasVO> listaTransferencia;
	private EscalaPortalPlanejamentoCirurgiasVO planejamentoSelecionado;
	private MbcAgendas agendaSelecionada;
	private Integer agdSeqSelecionado;
	private EscalaPortalPlanejamentoCirurgiasVO escalaAux;
	private Integer indiceEscala;
	private Boolean regimeSusMenor;
	private String pergunta;

	private Boolean mostrarModalCancelamento;
	private Boolean semSala;
	
	//#31975
	@Inject
	private AcompanharProcessoAutorizacaoOPMsController acompanharProcessoRequisicao;

	@Inject
	private SecurityController securityController;
	
	@Inject
	private CadastroPlanejamentoPacienteAgendaController cadastroPlanejamentoPacienteAgendaController;
	
	private AgendamentoProcedimentoCirurgicoVO agendamProcCirVO;
	private MbcAgendas agendaValidacaoOPMEs;
	
	public void iniciar() {
		getSalas();
		getAgendas();
		agendaSelecionada = null;
		planejamentoSelecionado = null;
		agdSeqSelecionado = null;
		this.setMostrarModalCancelamento(Boolean.TRUE);
		desabilitaBotoes();
		setAgendamProcCirVO(new AgendamentoProcedimentoCirurgicoVO());
	
	}
	
	
	public void gravar(){
		try {
			AghEspecialidades aghEspecialidades = null;
			if(espSeq != null) {
				aghEspecialidades = aghuFacade.obterAghEspecialidadesPorChavePrimaria(espSeq);
			}
			blocoCirurgicoFacadeBean.salvarEscala(planejados, escala, unfSeqEquipe, dataEscala, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe), aghEspecialidades);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_ESCALA");
			getAgendasEscala();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void selecionaItemPlanejado() {
		for (EscalaPortalPlanejamentoCirurgiasVO escalaVO : planejados) {
			if(agdSeqSelecionado.equals(escalaVO.getAgdSeq())){
				agendaSelecionada = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorAgdSeq(agdSeqSelecionado);
				planejamentoSelecionado = escalaVO;
				habilitaBotoes();
				cameFromPlanejadoEscala = REMARCAR_AGENDA_PLANEJADA;
				return;
			}
		}
	}
	
	public void selecionaItemEscala() {
		for (EscalaPortalPlanejamentoCirurgiasVO escalaVO : escala) {
			if(agdSeqSelecionado.equals(escalaVO.getAgdSeq())){
				agendaSelecionada = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorAgdSeq(agdSeqSelecionado);
				habilitaBotoes();
				cameFromPlanejadoEscala = REMARCAR_AGENDA_ESCALA;
				return;
			}
		}
	}
	
	private void habilitaBotoes(){
		this.readOnlyTransferir = Boolean.FALSE;
		this.readOnlyExclusao = Boolean.FALSE;
		if (agendaSelecionada.getIndSituacao().equals(DominioSituacaoAgendas.ES)) {
			this.readOnlyTransferirEscalaAgenda = Boolean.FALSE;
			this.readOnlyAlterarEsp = Boolean.TRUE;
			this.readOnlyTransferirAgendaEscala = Boolean.TRUE;
		} else {
			this.readOnlyTransferirEscalaAgenda = Boolean.TRUE;
			this.readOnlyAlterarEsp = Boolean.FALSE;
			this.readOnlyTransferirAgendaEscala = Boolean.FALSE;
		}
		
		this.renderedTransferirAgendaEscala = securityController.usuarioTemPermissao("atualizarEscalaPortalAgendaBotaoTransferirAgendaEscala", "atualizarEscalaPortalAgendaBotaoTransferirAgendaEscala");
		this.habilitarOrdenacao = securityController.usuarioTemPermissao("atualizarEscalaPortalAgendaBotaoOrdenarEscala", "atualizarEscalaPortalAgendaBotaoOrdenarEscala");
		this.habilitarTransferirEscalaAgenda = securityController.usuarioTemPermissao("atualizarEscalaPortalAgendaBotaoTransferirEscalaAgenda", "atualizarEscalaPortalAgendaBotaoTransferirEscalaAgenda");
	}
	
	public void desabilitaBotoes(){
		this.readOnlyTransferir = Boolean.TRUE;
		this.readOnlyAlterarEsp = Boolean.TRUE;
		this.readOnlyExclusao = Boolean.TRUE;
		this.readOnlyTransferirEscalaAgenda = Boolean.TRUE;
		this.readOnlyTransferirAgendaEscala = Boolean.TRUE;
	}
	
	
	//RN1
	public void getAgendas() {
		planejados = blocoCirurgicoPortalPlanejamentoFacade.pesquisarAgendasPlanejadas(new Date(dataEscala), matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, 
				DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe), espSeq, unfSeq);	
		calculaTempoTotalSala();
		getAgendasEscala();
	}
	
	public void getSalas() {
		salasCirurgicas = blocoCirurgicoPortalPlanejamentoFacade.buscarSalasDisponiveisParaEscala(new Date(dataEscala), unfSeq, 
				espSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe));
		if (!salasCirurgicas.isEmpty()) {
			for(MbcSalaCirurgica sala : salasCirurgicas) {
				if(sala.getId().getSeqp().equals(salaSeqp)) {
					salaCirurgica = sala;
					break;
				}
			}
		} 
		
		if (salaCirurgica != null) {
			atualizaHoraEscala();
		}
	}
	
	public void mudarSalaCirurgica() {
		if (salaCirurgica != null) {
			getAgendasEscala();
			atualizaHoraEscala();
		} else {
			horaInicio = null;
		}
		agendaSelecionada = null;
		planejamentoSelecionado = null;
		agdSeqSelecionado = null;
		desabilitaBotoes();
	}

	public String redirectTrocarLocalEspEquipeListaEspera() {
		trocarLocalEspEquipeListaEsperaController.setCameFrom(ATUALIZAR_ESCALA_PORTAL_PLANEJAMENTO);
		trocarLocalEspEquipeListaEsperaController.setAgdSeq(agendaSelecionada.getSeq());
		return TROCAR_LOCAL_ESP_EQUIPE_LISTA_ESPERA;
	}
	
	public void calculaTempoTotalSala() {
		Calendar calendar = Calendar.getInstance();    
	    
		calendar.set(Calendar.HOUR_OF_DAY, 00);  
		calendar.set(Calendar.MINUTE, 00);  
		calendar.set(Calendar.SECOND, 00);  
		calendar.set(Calendar.MILLISECOND, 00);
		
		tempoTotal = calendar.getTime();
		
		for (EscalaPortalPlanejamentoCirurgiasVO vo: planejados) {
			tempoTotal = DateUtil.adicionaMinutos(tempoTotal, getTempoEmMinutos(vo.getTempoSala()));
		}
	} 
	//RN1
	private void getAgendasEscala() {		
		escala = blocoCirurgicoPortalPlanejamentoFacade.pesquisarAgendasEmEscala(new Date(dataEscala), 
				((salaCirurgica != null) ? salaCirurgica.getId().getUnfSeq() : null), 
				((salaCirurgica != null) ? salaCirurgica.getId().getSeqp() : null), unfSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, 
				DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe), espSeq);
	}
	
	private Integer getTempoEmMinutos(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
	}
	
	public void atualizaHoraEscala() {
		try {
			if (salaCirurgica != null) {
				horaInicio = blocoCirurgicoPortalPlanejamentoFacade.atualizaHoraInicioEscala(null, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, 
						DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe), espSeq, unfSeq, salaCirurgica.getId().getSeqp(), new Date(dataEscala));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}	
	}
	
	/**
	 * chama ON2 e inicializa a modal para transferir o agendamento
	 * passando como parametro o seg da agenda e o tipo da agenda
	 * 
	 */
	public void habilitarTransferirAgendamento(){
		DominioTipoAgendaJustificativa parametro;
		// mock de agenda 
		try {
			if (agendaSelecionada != null && agendaSelecionada.getSeq() != null) {
				parametro = blocoCirurgicoPortalPlanejamentoFacade.retornarParametroCirurgiasCanceladas(agendaSelecionada.getSeq());
				justificativaTransferirAgendamentoController.setComeFrom(ATUALIZAR_ESCALA_PORTAL_PLANEJAMENTO);

				if(DominioTipoAgendaJustificativa.TAE.equals(parametro)){
					this.setMostrarModalCancelamento(Boolean.FALSE); //Caso ejete uma exceção na chamada abaixo. Não irá apresentar a modal.
					blocoCirurgicoPortalPlanejamentoFacade.validaSituacaoAgenda(agendaSelecionada);
					this.setMostrarModalCancelamento(Boolean.TRUE);
				}
				
				justificativaTransferirAgendamentoController.iniciarModal(agendaSelecionada.getSeq(), parametro.toString());
				openDialog("modalJustificativaTransferirAgendamentoWG");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
		}
	}
	
	public String colorirTabela(Object obj) {
		StringBuffer retorno = new StringBuffer();
		if(obj instanceof EscalaPortalPlanejamentoCirurgiasVO) {
			EscalaPortalPlanejamentoCirurgiasVO item = (EscalaPortalPlanejamentoCirurgiasVO) obj;
			
			if(item.getOverbooking() != null && item.getOverbooking()) {
				retorno.append("agd-overbooking");
			} else if(item.getEscala() != null && item.getEscala()) {
				retorno.append("agd-escala");
			} else if(item.getPlanejado() != null && item.getPlanejado()) {
				retorno.append("agd-planejada");
			}
			if(item.getEditavel() != null && !item.getEditavel()) {
				retorno.append(" regDisabled");
			}
		}
		
		return retorno.toString();
			
	}	
	
	public boolean isEletivas(){
		if(escala!=null && ! escala.isEmpty() ){
			for (EscalaPortalPlanejamentoCirurgiasVO escalaPortal : escala) {
				if(escalaPortal.getEditavel()== null || escalaPortal.getEditavel()== Boolean.TRUE){
					return false;
				}
			}
		}
		return true;
	}
	
	public void ajustarHorarios() {
		try {
			MbcProfAtuaUnidCirgs atuaUnidCirgs = new MbcProfAtuaUnidCirgs();
			MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(matriculaEquipe, vinCodigoEquipe, unfSeq, DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe));
			atuaUnidCirgs.setId(id);
			
			blocoCirurgicoFacadeBean.ajustarHorariosAgendamentoEmEscala(new Date(dataEscala), 
					 salaCirurgica.getId().getSeqp(), atuaUnidCirgs, espSeq, unfSeq, false);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_AJUSTAR_HORARIOS");
			getAgendasEscala();
			atualizaHoraEscala();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void conferirTransferirTodosPlanejado(){
		getAgendas();
		listaParaTransferir = new ArrayList<EscalaPortalPlanejamentoCirurgiasVO>();
		listaParaTransferir.addAll(planejados);
		conferirIniciar();
		conferirRegimeSusMenor();
		verificaModalConsultaRequisicao();
	}

	public void conferirTransferirPlanejado(){
		getAgendas();
		listaParaTransferir = new ArrayList<EscalaPortalPlanejamentoCirurgiasVO>();
		listaParaTransferir.add(planejamentoSelecionado);
		conferirIniciar();
		conferirRegimeSusMenor();
		verificaModalConsultaRequisicao();
	}
	
	private void conferirRegimeSusMenor(){
		if(regimeSusMenor){
			openDialog("modalConfirmacaoRegimeWG");
		}
	}
	
	private void verificaModalConsultaRequisicao() {
		if(this.agendamProcCirVO.isMostrarModal()){
			openDialog("modalConsultarRequisicaoWG");
		}
	}
	
	private void conferirIniciar(){
		try {
			if(horaInicio == null) {
				atualizaHoraEscala();
			}
			blocoCirurgicoPortalPlanejamentoFacade.verificarHoraTurnoValido(new Date(dataEscala), salaCirurgica.getId().getUnfSeq(), 
					salaCirurgica.getId().getSeqp() , unfSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, 
					DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe), espSeq, horaInicio);
			listaTransferencia = new ArrayList<EscalaPortalPlanejamentoCirurgiasVO>();
			canceladosTransferencia = new ArrayList<EscalaPortalPlanejamentoCirurgiasVO>();
			indiceEscala =0;
			this.conferirRegimeSus();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public String conferirRegimeSus(){
		regimeSusMenor = false;
		while(indiceEscala<listaParaTransferir.size()){
			EscalaPortalPlanejamentoCirurgiasVO escalaAux = listaParaTransferir.get(indiceEscala);
			pergunta = blocoCirurgicoPortalPlanejamentoFacade.verificarRegimeMinimoSus(escalaAux.getAgdSeq());
			if(pergunta!=null){
				regimeSusMenor = true;
				indiceEscala++;
				return "regimeMinimo";
			}
			indiceEscala++;
		}
		//lista para transferir
		
		if(canceladosTransferencia.size()>0){
			for (EscalaPortalPlanejamentoCirurgiasVO escalaPlanejada : listaParaTransferir ) {
				if(!canceladosTransferencia.contains(escalaPlanejada)){
					listaTransferencia.add(escalaPlanejada);
				}
			}
		}else{
			listaTransferencia.addAll(listaParaTransferir);
		}
		if(listaTransferencia.size()>0){
			transferirSelecionadosEscala();
		}
		//this.desabilitaBotoes();
		return "regimeMinimo";
	}
	
	public String abreviar(String abreviar,int tam){
		if(abreviar.length()>tam){
			String abreviada = StringUtils.abbreviate(abreviar, tam);
			return abreviada;
		}else{
			return abreviar;
		}
	}
	
	
	public void atualizarRegimeSus(){
		int atualizaInd = indiceEscala-1;
		EscalaPortalPlanejamentoCirurgiasVO aux = listaParaTransferir.get(atualizaInd);
		Integer seq = aux.getAgdSeq();
		try {
			blocoCirurgicoFacadeBean.atualizaRegimeMinimoSus(seq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Excecao Capturada: ", e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Excecao Capturada: ", e);
		}
		this.conferirRegimeSus();
	}
	
	public void cancelaPlanejadoRegimeSus(){
		int remove = indiceEscala-1;
		canceladosTransferencia.add(listaParaTransferir.get(remove));
		this.conferirRegimeSus();
	}
	
	public void ordenarEscalaParaCima() {
		try {
			blocoCirurgicoFacadeBean.ordenarEscalaParaCima(agdSeqSelecionado, new Date(dataEscala), salaCirurgica.getId().getSeqp(),
					unfSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe),
					espSeq);
			getAgendasEscala();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
	}
	
	public void ordenarEscalaParaBaixo() {
		try {
			blocoCirurgicoFacadeBean.ordenarEscalaParaBaixo(agdSeqSelecionado, new Date(dataEscala), salaCirurgica.getId().getSeqp(),
					unfSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe),
					espSeq);
			getAgendasEscala();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	//#31975
	public String getConsultarRequisicao(Boolean consultar) {
		this.agendamProcCirVO.setMostrarModal(false);
		if(consultar) {
			if(agendaValidacaoOPMEs.getRequisicoesOpmes() != null){
				MbcRequisicaoOpmes requisicao = agendaValidacaoOPMEs.getRequisicoesOpmes().get(0);
				acompanharProcessoRequisicao.setRequisicaoSeq(requisicao.getSeq());
			}
			acompanharProcessoRequisicao.setItemEtapasRequisicao(DominioPesquisaWorkflowOPMEsCodigoTemplateEtapa.TODAS);
			acompanharProcessoRequisicao.setVoltarParaUrl(ATUALIZAR_ESCALA_PORTAL_PLANEJAMENTO);
			acompanharProcessoRequisicao.setExecutarIniciar(true);
			
			return ACOMPANHAR_PROCESSO_AUT_OPME;
		} 
		return null;
	}	
	
	public void transferirSelecionadosEscala() {
		boolean temOpme = false;
		try {
			Boolean gerouReservaHemo = false;
			for(EscalaPortalPlanejamentoCirurgiasVO planejado : listaTransferencia) {
				MbcAgendas agd = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorSeq(planejado.getAgdSeq());
				List<MbcRequisicaoOpmes> reqsOpme = blocoCirurgicoPortalPlanejamentoFacade.consultarListaRequisicoesPorAgenda(planejado.getAgdSeq());
				agd.setRequisicoesOpmes(reqsOpme);
				List<MbcGrupoAlcadaAvalOpms> alcadas = this.blocoCirurgicoFacade.listarGrupoAlcadaFiltro(null,
						agd.getEspecialidade(), null, null, null, DominioSituacao.A);
				
				 if(alcadas != null && alcadas.size() > 0){
					 if(reqsOpme != null){
							if(reqsOpme.size() > 0){
								if(DominioSituacaoRequisicao.CANCELADA.equals(reqsOpme.get(0).getSituacao())){
									temOpme = true;
									this.apresentarMsgNegocio(Severity.INFO, "MSG_ERRO_REQ_AGENDA_CANCELADA");
									continue;
								}
							}
					 	}
						agendaValidacaoOPMEs = agd;
						if(!Boolean.TRUE.equals(semSala)) {
							agendamProcCirVO = blocoCirurgicoFacadeBean.verificarRequisicaoOPMEs(agd);
							if (agendamProcCirVO.getParametro() == null) {
								temOpme = true;
								if(StringUtils.isNotBlank(agendamProcCirVO.getMensagem())){
									this.apresentarMsgNegocio(Severity.INFO, agendamProcCirVO.getMensagem());
								}
							} else {
								temOpme = true;
								this.apresentarMsgNegocio(Severity.INFO, agendamProcCirVO.getMensagem(), agendamProcCirVO.getParametro());
							}
							
							if(!agendamProcCirVO.isConfirmarEscalaMedica()) {
								continue;
							}
						}
				 }
				
				gerouReservaHemo = blocoCirurgicoFacadeBean.transferirAgendamentosEscala(planejado, new Date(dataEscala), salaCirurgica.getId().getUnfSeq(), 
				salaCirurgica.getId().getSeqp() , unfSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe,  DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe), espSeq, horaInicio);
				
				if(agd.getDthrPrevFim() != null) {
					horaInicio = DateUtil.adicionaMinutos(agd.getDthrPrevFim(), agd.getIntervaloEscala());
				}
			}
			getAgendas();
			if(gerouReservaHemo) {
				this.apresentarMsgNegocio(Severity.INFO, "MBC_01013");
			}
			if(!temOpme){
				if(listaTransferencia.size() > 1) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_VARIAS_TRANSFERENCIAS");
				} else if(listaTransferencia.size() == 1) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_UMA_TRANSFERENCIA", listaTransferencia.get(0).getNomePacienteCompleto());
				}
			}
			atualizaHoraEscala();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			agendaSelecionada = null;
			planejamentoSelecionado = null;
			agdSeqSelecionado = null;
			desabilitaBotoes();
		}
	}
	
	public void transferirAgendaEscalaParaPlanejamento() {
		try {
			
			blocoCirurgicoFacadeBean.transferirAgendasEmEscalaParaPlanejamento(new Date(dataEscala), ((salaCirurgica != null) ? salaCirurgica.getId().getUnfSeq() : null), 
					((salaCirurgica != null) ? salaCirurgica.getId().getSeqp() : null), unfSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, 
					DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe), espSeq, agdSeqSelecionado);			
			
			//OPME cancelamento removido
			
			//RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			//MbcRequisicaoOpmes requisicaoOpmes = blocoCirurgicoOpmesFacade.carregaRequisicao(agendaSelecionada);
			//if(requisicaoOpmes != null){
				//blocoCirurgicoOpmesFacade.cancelarFluxoAutorizacaoOPMEs(servidor, requisicaoOpmes.getFluxo(), "", requisicaoOpmes);
			//}
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TRANSFERIR_AGENDA_ESCALA_PLANEJAMENTO_SUCESSO", agendaSelecionada.getPaciente().getNome());
			getAgendas();
			atualizaHoraEscala();
			readOnlyTransferirEscalaAgenda = Boolean.TRUE;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			agendaSelecionada = null;
			planejamentoSelecionado = null;
			agdSeqSelecionado = null;
			desabilitaBotoes();
		}
	}
	
	public void transferirAgendasEscalaParaPlanejamento() {		
		try {
			//RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			blocoCirurgicoFacadeBean.transferirAgendasEmEscalaParaPlanejamento(new Date(dataEscala), ((salaCirurgica != null) ? salaCirurgica.getId().getUnfSeq() : null), 
					((salaCirurgica != null) ? salaCirurgica.getId().getSeqp() : null), unfSeq, matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, 
					DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe), espSeq, null);
			
			// OPME removido o cancelar
			
			//for (EscalaPortalPlanejamentoCirurgiasVO escalaAgenda : escala) {
				//MbcAgendas agenda = blocoCirurgicoFacadeBean.obterAgendaPorAgdSeq(escalaAgenda.getAgdSeq());
				//MbcRequisicaoOpmes requisicaoOpmes = blocoCirurgicoOpmesFacade.carregaRequisicao(agenda);
				//if (requisicaoOpmes != null) {
				//	blocoCirurgicoOpmesFacade.cancelarFluxoAutorizacaoOPMEs(servidor, requisicaoOpmes.getFluxo(), "", requisicaoOpmes);
				//}
			//}
			
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TRANSFERIR_AGENDAS_ESCALA_PLANEJAMENTO_SUCESSO");			
			getAgendas();
			atualizaHoraEscala();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			agendaSelecionada = null;
			planejamentoSelecionado = null;
			agdSeqSelecionado = null;
			desabilitaBotoes();
		}
	}
	
	public String voltar() {
		espSeq = null;
		detalhamentoPortalAgendamentoController.setUnfSeqSala(this.unfSeqSala);
		detalhamentoPortalAgendamentoController.setSalaSeqp(Boolean.TRUE.equals(semSala) ? Short.valueOf("-1") : this.salaSeqp);
		detalhamentoPortalAgendamentoController.iniciar();
		limparParametros();
		return DETALHAMENTO_PORTAL;
	}
	
	private void limparParametros() {
		this.horaInicio = null;
		this.tempoTotal = null;
		this.salaCirurgica = null;
		this.salasCirurgicas = null;
		this.readOnlyTransferir = Boolean.TRUE;
		this.readOnlyAlterarEsp = Boolean.TRUE;
		this.readOnlyExclusao = Boolean.TRUE;
		this.readOnlyTransferirEscalaAgenda = Boolean.TRUE;
		this.habilitarOrdenacao = Boolean.TRUE;
		this.readOnlyTransferirAgendaEscala = Boolean.TRUE;
		this.renderedTransferirAgendaEscala = Boolean.TRUE;
		this.habilitarTransferirEscalaAgenda = Boolean.TRUE;
		this.cameFromPlanejadoEscala = null;
		this.dataEscala = null;
		this.unfSeq = null;
		this.espSeq = null;
		this.salaSeqp = null;
		this.unfSeqSala = null;
		this.matriculaEquipe = null;
		this.vinCodigoEquipe = null;
		this.unfSeqEquipe = null;
		this.indFuncaoProfEquipe = null;
		this.seqUnidFuncionalCirugica = null;
		this.planejados = null;
		this.escala = null;
		this.canceladosTransferencia = null;
		this.listaParaTransferir = null;
		this.listaTransferencia = null;
		this.planejamentoSelecionado = null;
		this.agendaSelecionada = null;
		this.agdSeqSelecionado = null;
		this.escalaAux = null;
		this.indiceEscala = null;
		this.regimeSusMenor = null;
		this.pergunta = null;
		this.mostrarModalCancelamento = null;
		this.agendamProcCirVO = null;
		this.agendaValidacaoOPMEs = null;
	}
	
	public String incluirPaciente() {
	    cadastroPlanejamentoPacienteAgendaController.setSeqEspecialidade(espSeq);
	    cadastroPlanejamentoPacienteAgendaController.setMatriculaEquipe(matriculaEquipe);
	    cadastroPlanejamentoPacienteAgendaController.setVinCodigoEquipe(vinCodigoEquipe);
	    cadastroPlanejamentoPacienteAgendaController.setUnfSeqEquipe(unfSeqEquipe);
	    cadastroPlanejamentoPacienteAgendaController.setSeqUnidFuncionalCirugica(seqUnidFuncionalCirugica);
	    cadastroPlanejamentoPacienteAgendaController.setIndFuncaoProfEquipe(indFuncaoProfEquipe);
	    cadastroPlanejamentoPacienteAgendaController.setCameFrom(ATUALIZAR_ESCALA_PORTAL_PLANEJAMENTO);
	    cadastroPlanejamentoPacienteAgendaController.setDataAgenda(dataEscala);
	    cadastroPlanejamentoPacienteAgendaController.setSalaSeqp(salaSeqp);
	    cadastroPlanejamentoPacienteAgendaController.setSituacaoAgendaParam("AG");
	    
	    return PLANEJAMENTO_PACIENTE_AGENDA_CRUD;
	}
	
	public String editarPaciente() {
	    cadastroPlanejamentoPacienteAgendaController.setSeqEspecialidade(espSeq);
	    cadastroPlanejamentoPacienteAgendaController.setMatriculaEquipe(matriculaEquipe);
	    cadastroPlanejamentoPacienteAgendaController.setVinCodigoEquipe(vinCodigoEquipe);
	    cadastroPlanejamentoPacienteAgendaController.setUnfSeqEquipe(unfSeqEquipe);
	    cadastroPlanejamentoPacienteAgendaController.setSeqUnidFuncionalCirugica(seqUnidFuncionalCirugica);
	    cadastroPlanejamentoPacienteAgendaController.setIndFuncaoProfEquipe(indFuncaoProfEquipe);
	    cadastroPlanejamentoPacienteAgendaController.setCameFrom(ATUALIZAR_ESCALA_PORTAL_PLANEJAMENTO);
	    cadastroPlanejamentoPacienteAgendaController.setDataAgenda(dataEscala);
	    cadastroPlanejamentoPacienteAgendaController.setSalaSeqp(salaSeqp);
	    cadastroPlanejamentoPacienteAgendaController.setSituacaoAgendaParam(agendaSelecionada.getIndSituacao().toString());
	    cadastroPlanejamentoPacienteAgendaController.setAgdSeq(agdSeqSelecionado);
	    return PLANEJAMENTO_PACIENTE_AGENDA_CRUD;
	}
	
	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}
	
	public MbcSalaCirurgica getSalaCirurgica() {
		return salaCirurgica;
	}

	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}

	public List<MbcSalaCirurgica> getSalasCirurgicas() {
		return salasCirurgicas;
	}

	public void setSalasCirurgicas(List<MbcSalaCirurgica> salasCirurgicas) {
		this.salasCirurgicas = salasCirurgicas;
	}

	public void setTempoTotal(Date tempoTotal) {
		this.tempoTotal = tempoTotal;
	}

	public Date getTempoTotal() {
		return tempoTotal;
	}
	
	public void setDataEscala(Long dataEscala) {
		this.dataEscala = dataEscala;
	}

	public Long getDataEscala() {
		return dataEscala;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Short getSalaSeqp() {
		return salaSeqp;
	}

	public void setSalaSeqp(Short salaSeqp) {
		this.salaSeqp = salaSeqp;
	}

	public Integer getMatriculaEquipe() {
		return matriculaEquipe;
	}

	public void setMatriculaEquipe(Integer matriculaEquipe) {
		this.matriculaEquipe = matriculaEquipe;
	}

	public Short getVinCodigoEquipe() {
		return vinCodigoEquipe;
	}

	public void setVinCodigoEquipe(Short vinCodigoEquipe) {
		this.vinCodigoEquipe = vinCodigoEquipe;
	}

	public Short getUnfSeqEquipe() {
		return unfSeqEquipe;
	}

	public void setUnfSeqEquipe(Short unfSeqEquipe) {
		this.unfSeqEquipe = unfSeqEquipe;
	}

	public String getIndFuncaoProfEquipe() {
		return indFuncaoProfEquipe;
	}

	public void setIndFuncaoProfEquipe(String indFuncaoProfEquipe) {
		this.indFuncaoProfEquipe = indFuncaoProfEquipe;
	}

	public void setUnfSeqSala(Short unfSeqSala) {
		this.unfSeqSala = unfSeqSala;
	}

	public Short getUnfSeqSala() {
		return unfSeqSala;
	}
	
	public Short getSeqUnidFuncionalCirugica() {
		return seqUnidFuncionalCirugica;
	}

	public void setSeqUnidFuncionalCirugica(Short seqUnidFuncionalCirugica) {
		this.seqUnidFuncionalCirugica = seqUnidFuncionalCirugica;
	}
	

	public void setPlanejados(List<EscalaPortalPlanejamentoCirurgiasVO> planejados) {
		this.planejados = planejados;
	}

	public List<EscalaPortalPlanejamentoCirurgiasVO> getPlanejados() {
		return planejados;
	}

	public List<EscalaPortalPlanejamentoCirurgiasVO> getEscala() {
		return escala;
	}

	public void setEscala(List<EscalaPortalPlanejamentoCirurgiasVO> escala) {
		this.escala = escala;
	}
	
	public Boolean getReadOnlyTransferir() {
		return readOnlyTransferir;
	}

	public void setReadOnlyTransferir(Boolean readOnlyTransferir) {
		this.readOnlyTransferir = readOnlyTransferir;
	}

	public Boolean getReadOnlyAlterarEsp() {
		return readOnlyAlterarEsp;
	}

	public void setReadOnlyAlterarEsp(Boolean readOnlyAlterarEsp) {
		this.readOnlyAlterarEsp = readOnlyAlterarEsp;
	}

	public Boolean getReadOnlyExclusao() {
		return readOnlyExclusao;
	}

	public void setReadOnlyExclusao(Boolean readOnlyExclusao) {
		this.readOnlyExclusao = readOnlyExclusao;
	}
	public MbcAgendas getAgendaSelecionada() {
		return agendaSelecionada;
	}
	public void setAgendaSelecionada(MbcAgendas agendaSelecionada) {
		this.agendaSelecionada = agendaSelecionada;
	}
	public Integer getAgdSeqSelecionado() {
		return agdSeqSelecionado;
	}
	public void setAgdSeqSelecionado(Integer agdSeqSelecionado) {
		this.agdSeqSelecionado = agdSeqSelecionado;
	}
	
	public void setCameFromPlanejadoEscala(String cameFromPlanejadoEscala) {
		this.cameFromPlanejadoEscala = cameFromPlanejadoEscala;
	}

	public String getCameFromPlanejadoEscala() {
		return cameFromPlanejadoEscala;
	}

	public void setReadOnlyTransferirEscalaAgenda(
			Boolean readOnlyTransferirEscalaAgenda) {
		this.readOnlyTransferirEscalaAgenda = readOnlyTransferirEscalaAgenda;
	}
	
	public Boolean getReadOnlyTransferirEscalaAgenda() {
		return readOnlyTransferirEscalaAgenda;
	}


	public EscalaPortalPlanejamentoCirurgiasVO getEscalaAux() {
		return escalaAux;
	}


	public void setEscalaAux(EscalaPortalPlanejamentoCirurgiasVO escalaAux) {
		this.escalaAux = escalaAux;
	}


	public Integer getIndiceEscala() {
		return indiceEscala;
	}


	public void setIndiceEscala(Integer indiceEscala) {
		this.indiceEscala = indiceEscala;
	}


	public Boolean getRegimeSusMenor() {
		return regimeSusMenor;
	}


	public void setRegimeSusMenor(Boolean regimeSusMenor) {
		this.regimeSusMenor = regimeSusMenor;
	}


	public String getPergunta() {
		return pergunta;
	}


	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}


	public EscalaPortalPlanejamentoCirurgiasVO getPlanejamentoSelecionado() {
		return planejamentoSelecionado;
	}


	public void setPlanejamentoSelecionado(
			EscalaPortalPlanejamentoCirurgiasVO planejamentoSelecionado) {
		this.planejamentoSelecionado = planejamentoSelecionado;
	}


	public Boolean getReadOnlyTransferirAgendaEscala() {
		return readOnlyTransferirAgendaEscala;
	}


	public void setReadOnlyTransferirAgendaEscala(
			Boolean readOnlyTransferirAgendaEscala) {
		this.readOnlyTransferirAgendaEscala = readOnlyTransferirAgendaEscala;
	}

	public Boolean getRenderedTransferirAgendaEscala() {
		return renderedTransferirAgendaEscala;
	}

	public void setRenderedTransferirAgendaEscala(
			Boolean renderedTransferirAgendaEscala) {
		this.renderedTransferirAgendaEscala = renderedTransferirAgendaEscala;
	}

	public Boolean getHabilitarOrdenacao() {
		return habilitarOrdenacao;
	}

	public void setHabilitarOrdenacao(Boolean habilitarOrdenacao) {
		this.habilitarOrdenacao = habilitarOrdenacao;
	}

	public Boolean getMostrarModalCancelamento() {
		return mostrarModalCancelamento;
	}

	public void setMostrarModalCancelamento(Boolean mostrarModalCancelamento) {
		this.mostrarModalCancelamento = mostrarModalCancelamento;
	}

	public Boolean getHabilitarTransferirEscalaAgenda() {
		return habilitarTransferirEscalaAgenda;
	}

	public void setHabilitarTransferirEscalaAgenda(
			Boolean habilitarTransferirEscalaAgenda) {
		this.habilitarTransferirEscalaAgenda = habilitarTransferirEscalaAgenda;
	}

	public void setAgendamProcCirVO(AgendamentoProcedimentoCirurgicoVO agendamProcCirVO) {
		this.agendamProcCirVO = agendamProcCirVO;
	}

	public AgendamentoProcedimentoCirurgicoVO getAgendamProcCirVO() {
		return agendamProcCirVO;
	}

	public Boolean getSemSala() {
		return semSala;
	}

	public void setSemSala(Boolean semSala) {
		this.semSala = semSala;
	}
}
