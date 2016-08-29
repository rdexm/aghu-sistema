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


public class ExcluirPacienteAgendaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 8765453201077804523L;

	private static final String ID_MODAL_JUSTIFICATIVA_CIRURGIAS = "excluirPacienteAgendaModalWG";
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	@EJB
	private IBlocoCirurgicoFacadeBean blocoCirurgicoFacadeBean;
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	@Inject
	private AtualizarEscalaPortalAgendamentoController atualizarEscalaPortalAgendamentoController;

	private final String DETALHAMENTO_PORTAL = "detalhamentoPortalAgendamento";
	private final String ATUALIZAR_ESCALA = "atualizarEscalaPortalPlanejamento";
	
	private String justificativa;
	private Integer agdSeq;
	private MbcAgendas agenda;
	private String textoTooltip;
	private String tituloModal;
	private String comeFrom;

	public void inicio(Integer seqAgenda, String comeFrom) {
		limparTela();
		this.agdSeq = seqAgenda;
		agenda = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorSeq(agdSeq);
		obterTextoCabecalhoModal();
		this.comeFrom = comeFrom;
	}

	public void gravarJustificativaExclusaoPacienteEmAgenda() {	
		
		if(StringUtils.isBlank(this.justificativa)) {
			apresentarMsgNegocio("inputJustificativa",Severity.ERROR, "CAMPO_OBRIGATORIO", super.getBundle().getString("LABEL_JUSTIFICATIVA"));
			return;
		} else if (this.justificativa.length() > 500) {
			this.justificativa = this.justificativa.replaceAll("\\r\\n", "\n"); 
		}
		
		try {
			MbcAgendaJustificativa agendaJustificativa = new MbcAgendaJustificativa();
			MbcAgendaJustificativaId agendaJustificativaId = new MbcAgendaJustificativaId();
			agendaJustificativaId.setAgdSeq(agenda.getSeq());
			agendaJustificativa.setId(agendaJustificativaId);
			agendaJustificativa.setMbcAgendas(agenda);	
			
			if(DETALHAMENTO_PORTAL.equals(comeFrom)){
				agendaJustificativa.setTipo(DominioTipoAgendaJustificativa.EEA);
			}
			if(ATUALIZAR_ESCALA.equals(comeFrom)){
				agendaJustificativa.setTipo(DominioTipoAgendaJustificativa.EES);
			}
			
			agendaJustificativa.setJustificativa(this.justificativa);
			
			blocoCirurgicoFacadeBean.excluirPacienteEmAgenda(agendaJustificativa, comeFrom);
		
			if(DETALHAMENTO_PORTAL.equals(comeFrom)){
				detalhamentoPortalAgendamentoController.buscarDetalhamento();
			}
			
			if(ATUALIZAR_ESCALA.equals(comeFrom)){
				atualizarEscalaPortalAgendamentoController.desabilitaBotoes();
				//RN1
				atualizarEscalaPortalAgendamentoController.iniciar();
				// p_atualiza_hora_inicio_escala
				atualizarEscalaPortalAgendamentoController.atualizaHoraEscala();
			}
			
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_AGENDAMENTO_PACIENTE");
			closeDialog(ID_MODAL_JUSTIFICATIVA_CIRURGIAS);
		} catch (BaseException e) {
			this.justificativa = null;
			apresentarExcecaoNegocio(e);
		}
		
		limparTela();
	}

	private void obterTextoCabecalhoModal() {
		StringBuilder retorno = new StringBuilder(100);

		retorno.append(this.getBundle().getString("TITLE_JUSTIFICATIVA_EXCLUSAO_PACIENTE_AGENDAMENTO")).append(' ')
		.append(WordUtils.capitalizeFully(agenda.getPaciente().getNome())).append(" - ")
		.append(CoreUtil.formataProntuario(agenda.getPaciente().getProntuario())).append(" - ")
		.append(CoreUtil.capitalizaTextoFormatoAghu(agenda.getEspProcCirgs().getMbcProcedimentoCirurgicos().getDescricao()));

		if (retorno.length() > 80) {
			textoTooltip = retorno.toString();
			tituloModal = StringUtils.abbreviate(retorno.toString(), 80);
			return;
		}
		
		textoTooltip = null;
		tituloModal = retorno.toString();
	}

	public void limparTela() {
		justificativa = null;
		agenda = null;
		textoTooltip = null;
		tituloModal = null;
		agdSeq = null;
	}

	public void cancelar() {
		limparTela();
	}
	
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public MbcAgendas getAgenda() {
		return agenda;
	}

	public void setAgenda(MbcAgendas agenda) {
		this.agenda = agenda;
	}

	public String getTextoTooltip() {
		return textoTooltip;
	}

	public void setTextoTooltip(String textoTooltip) {
		this.textoTooltip = textoTooltip;
	}

	public String getTituloModal() {
		return tituloModal;
	}

	public void setTituloModal(String tituloModal) {
		this.tituloModal = tituloModal;
	}

	public Integer getAgdSeq() {
		return agdSeq;
	}

	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}

	public String getComeFrom() {
		return comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}
	
	
}
