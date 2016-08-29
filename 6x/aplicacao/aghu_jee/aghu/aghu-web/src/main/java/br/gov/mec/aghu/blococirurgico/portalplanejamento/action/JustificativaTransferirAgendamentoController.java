package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacadeBean;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.model.MbcAgendaJustificativa;
import br.gov.mec.aghu.model.MbcAgendaJustificativaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class JustificativaTransferirAgendamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 2180692121705212906L;
	
	@EJB
	private IBlocoCirurgicoFacadeBean blocoCirurgicoFacadeBean;
	
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	
	@Inject
	private AtualizarEscalaPortalAgendamentoController atualizarEscalaPortalAgendamentoController;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;

	private MbcAgendas agendaSelecionada;
	private DominioTipoAgendaJustificativa tipoAgenda;
	private String justificativaTransferirAgendamento;
	private final String CANCELAR_TRANSFERIR = "cancelarTransferirAgendamento";
	private final String DETALHAMENTO_PORTAL = "blococirurgico-detalhamentoPortalAgendamento";
	private final String ATUALIZAR_ESCALA = "blococirurgico-atualizarEscalaPortalPlanejamento";
	private String tituloTransferirAgendamento;
	private String labelTransferirAgendamento;
	private String titleTransferirAgendamento;
	private Boolean comErro;
	private String comeFrom;
	
	public void iniciarModal(Integer agdSeq, String tipo) {
		if (agdSeq != null) {
			agendaSelecionada = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorAgdSeq(agdSeq, null, new Enum[] {MbcAgendas.Fields.PACIENTE, MbcAgendas.Fields.PROCEDIMENTO});
			
			if(tipo != null && !tipo.isEmpty()) {
				tipoAgenda = DominioTipoAgendaJustificativa.valueOf(tipo);
			}
			
			this.setTituloTransferirAgendamento(obterTituloTransferirAgendamento());
			this.setLabelTransferirAgendamento(obterLabelTransferirAgendamento());
			this.setTitleTransferirAgendamento(obterTitleTransferirAgendamento());
			this.setComErro(Boolean.TRUE);
		}
	}
	
	public String obterTituloTransferirAgendamento(){
		if(agendaSelecionada == null){
			return "";
		}
		
		StringBuilder tituloTransferirAgendamento = new StringBuilder();
		tituloTransferirAgendamento.append(getBundle().getString("LABEL_TRANSFERIR_AGENDAMENTO_TITULO_JUSTIFICATIVA_DE")).append(' ')
		.append(WordUtils.capitalizeFully(agendaSelecionada.getPaciente().getNome()) ).append( " - ")
		.append(CoreUtil.formataProntuario(agendaSelecionada.getPaciente().getProntuario()) ).append( " - ")
		.append(CoreUtil.capitalizaTextoFormatoAghu(agendaSelecionada.getProcedimentoCirurgico().getDescricao()));
		return tituloTransferirAgendamento.toString();
	}
	
	private String obterLabelTransferirAgendamento() {
		if(tipoAgenda == null) {
			return "";
		}
		
		StringBuilder tituloTransferirAgendamento = new StringBuilder();
		if(tipoAgenda.equals(DominioTipoAgendaJustificativa.TAC)) {
			tituloTransferirAgendamento.append(getBundle().getString("LABEL_TRANSFERIR_AGENDAMENTO_JUSTIFICATIVA_LISTA_CANCELADOS"));
		} else if(tipoAgenda.equals(DominioTipoAgendaJustificativa.TAE)) {
			tituloTransferirAgendamento.append(getBundle().getString("LABEL_TRANSFERIR_AGENDAMENTO_JUSTIFICATIVA_LISTA_ESPERA"));
		}
		
		return tituloTransferirAgendamento.toString();
	}
	
	private String obterTitleTransferirAgendamento(){
		if(tipoAgenda == null){
			return "";
		}
		StringBuilder tituloTransferirAgendamento = new StringBuilder();
		if(tipoAgenda.equals(DominioTipoAgendaJustificativa.TAC)){
			tituloTransferirAgendamento.append(getBundle().getString("TITLE_TRANSFERIR_AGENDAMENTO_JUSTIFICATIVA_LISTA_CANCELADOS"));
		}
		if(tipoAgenda.equals(DominioTipoAgendaJustificativa.TAE)){
			tituloTransferirAgendamento.append(getBundle().getString("TITLE_TRANSFERIR_AGENDAMENTO_JUSTIFICATIVA_LISTA_ESPERA"));
		}
		return tituloTransferirAgendamento.toString();
	}
	
	public String abreviar(String str, int maxWidth) {
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	private void limparTela(){
		agendaSelecionada = null;
		tipoAgenda = null;
		comErro = Boolean.TRUE;
		tituloTransferirAgendamento = null;
		labelTransferirAgendamento = null;
		titleTransferirAgendamento = null;
		justificativaTransferirAgendamento = null;
		
	}
	
	public void cancelar() {
		limparTela();
	}
	
	public void gravarJustificativaTransferirAgendamento() throws BaseException {
		
		try{
			MbcAgendaJustificativa agendaJustificativa = criarJustificativa();
			blocoCirurgicoFacadeBean.transferirAgendamento(agendaJustificativa, comeFrom);
			//verificar tela come from
			mostrarMensagemRecarregarTelaAnterior();
		} catch (final BaseException e) {
			justificativaTransferirAgendamento = null;
			apresentarExcecaoNegocio(e);
		}
	}
	
	/*  criar a agenda justiticativa de acordo com a justificativa informada na tela
	 *  e com a agenda selecionada na tela de detalhamento
	 *  
	 */
	private MbcAgendaJustificativa criarJustificativa(){
		MbcAgendaJustificativa agendaJustificativa = new MbcAgendaJustificativa();
		MbcAgendaJustificativaId agendaJustificativaId = new MbcAgendaJustificativaId();
		agendaJustificativaId.setAgdSeq(agendaSelecionada.getSeq());
		agendaJustificativa.setId(agendaJustificativaId);
		agendaJustificativa.setMbcAgendas(agendaSelecionada);			
		agendaJustificativa.setTipo(tipoAgenda);	
		agendaJustificativa.setJustificativa(justificativaTransferirAgendamento != null && justificativaTransferirAgendamento.length() > 500 ? justificativaTransferirAgendamento.replaceAll("\\r\\n", "\n") : justificativaTransferirAgendamento);
		return agendaJustificativa;
	}
	
	/*
	 *  mostra a mensagem de sucesso na tela sem erro 
	 *  e recarrega a tela de detalhamento
	 *  
	 */
	private void mostrarMensagemRecarregarTelaAnterior(){
		justificativaTransferirAgendamento = null;
		this.setComErro(Boolean.FALSE);
		if(tipoAgenda.equals(DominioTipoAgendaJustificativa.TAC)){
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_TRANSFERIR_LISTA_CANCELADOS_PACIENTE");
		} else if(tipoAgenda.equals(DominioTipoAgendaJustificativa.TAE)) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_TRANSFERIR_LISTA_ESPERA_PACIENTE");
		}
		if(DETALHAMENTO_PORTAL.equals(comeFrom)){
			detalhamentoPortalAgendamentoController.buscarDetalhamento();
			detalhamentoPortalAgendamentoController.setReadOnlyTransferir(Boolean.TRUE);
		}
		if(ATUALIZAR_ESCALA.equals(comeFrom)){
			atualizarEscalaPortalAgendamentoController.setReadOnlyTransferir(Boolean.TRUE);
			//RN1
			atualizarEscalaPortalAgendamentoController.iniciar();
			// p_atualiza_hora_inicio_escala
			atualizarEscalaPortalAgendamentoController.atualizaHoraEscala();
		}
		
	}
	
	public String cancelarTransferirAgendamento(){
		return CANCELAR_TRANSFERIR;
	}

	public MbcAgendas getAgendaSelecionada() {
		return agendaSelecionada;
	}

	public void setAgendaSelecionada(MbcAgendas agendaSelecionada) {
		this.agendaSelecionada = agendaSelecionada;
	}

	public DominioTipoAgendaJustificativa getTipoAgenda() {
		return tipoAgenda;
	}

	public void setTipoAgenda(DominioTipoAgendaJustificativa tipoAgenda) {
		this.tipoAgenda = tipoAgenda;
	}

	public String getJustificativaTransferirAgendamento() {
		return justificativaTransferirAgendamento;
	}

	public void setJustificativaTransferirAgendamento(
			String justificativaTransferirAgendamento) {
		this.justificativaTransferirAgendamento = justificativaTransferirAgendamento;
	}

	public String getTituloTransferirAgendamento() {
		return tituloTransferirAgendamento;
	}

	public void setTituloTransferirAgendamento(String tituloTransferirAgendamento) {
		this.tituloTransferirAgendamento = tituloTransferirAgendamento;
	}

	public String getTitleTransferirAgendamento() {
		return titleTransferirAgendamento;
	}

	public void setTitleTransferirAgendamento(String titleTransferirAgendamento) {
		this.titleTransferirAgendamento = titleTransferirAgendamento;
	}


	public String getLabelTransferirAgendamento() {
		return labelTransferirAgendamento;
	}


	public void setLabelTransferirAgendamento(String labelTransferirAgendamento) {
		this.labelTransferirAgendamento = labelTransferirAgendamento;
	}


	public Boolean getComErro() {
		return comErro;
	}


	public void setComErro(Boolean comErro) {
		this.comErro = comErro;
	}

	public String getComeFrom() {
		return comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}
	
	
}
