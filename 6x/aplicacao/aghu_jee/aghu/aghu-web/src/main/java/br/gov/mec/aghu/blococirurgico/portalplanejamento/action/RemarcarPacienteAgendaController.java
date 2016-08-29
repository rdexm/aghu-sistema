package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class RemarcarPacienteAgendaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -618867046987215423L;

	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	@Inject
	private PesquisaAgendaCirurgiaController pesquisaAgendaCirurgiaController;
	@Inject
	private AtualizarEscalaPortalAgendamentoController atualizarEscalaPortalAgendamentoController;
	@Inject
	private VisualizaListaEsperaAgendaMedicoController visualizaListaEsperaAgendaMedicoController;
	@Inject
	private CirurgiasCanceladasAgendaMedicoController cirurgiasCanceladasAgendaMedicoController;
	
	private Date dataSelecionada;
	private String justificativa;
	private String resumoDia;
	private MbcAgendas agenda;
	private String textoTooltip;
	private String tituloModal;
	private String cameFrom;
	private Integer agdSeq;
	private Boolean renderJustificativa = Boolean.TRUE;
	private List<MbcSalaCirurgica> salasCirurgicas;
	private MbcSalaCirurgica salaCirurgica;
	private DominioTipoAgendaJustificativa dominio;
	
	//Telas que poderão chamar esta estória
	private final String TELA_DETALHAMENTO = "cameFromDetalhamento";
	private final String TELA_CANCELADAS = "cameFromCanceladas";
	private final String TELA_LISTA_ESPERA = "cameFromListaEspera";
	private final String REMARCAR_AGENDA_PLANEJADA = "remarcarEscalaAgendaPlanejada";
	private final String REMARCAR_AGENDA_ESCALA = "remarcarEscalaAgendaEscala";
	
	public void inicio() {
		
		Integer seqAgenda = agdSeq;
		String cameFromAux = cameFrom;
		
		limparTela(); 
		this.agdSeq = seqAgenda;
		this.cameFrom = cameFromAux;
		
		if (!cameFrom.equals(TELA_DETALHAMENTO) && (!cameFrom.equals(REMARCAR_AGENDA_ESCALA) && !cameFrom.equals(REMARCAR_AGENDA_PLANEJADA))) {
			renderJustificativa = Boolean.FALSE;
			//não insere justificativa para remarcação
			justificativa = null;
		}
		
		//seta dominio para remarcar dependendo da lista selecionada 
		//na tela de atualização da escala - ou se modal foi chamada no detalhamento
		if (cameFrom.equals(REMARCAR_AGENDA_PLANEJADA) || cameFrom.equals(TELA_DETALHAMENTO)) {
			dominio = DominioTipoAgendaJustificativa.REA;
		} else if (cameFrom.equals(REMARCAR_AGENDA_ESCALA)) {
			dominio = DominioTipoAgendaJustificativa.REE;
		}
		
		agenda = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorAgdSeq(agdSeq, null, new Enum[] {MbcAgendas.Fields.PACIENTE, MbcAgendas.Fields.PROCEDIMENTO});
		dataSelecionada = new Date();
		obterResumoDia();
		obterTextoCabecalhoModal();	
	}
	
	public void gravar() {
		try {
			agenda = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaRemarcarPorSeq(agdSeq);
			blocoCirurgicoPortalPlanejamentoFacade.remarcarPacienteAgenda(dataSelecionada, agenda, justificativa, dominio, salaCirurgica);
			limparTela();
			
			if (cameFrom.equals(TELA_DETALHAMENTO)) {
				detalhamentoPortalAgendamentoController.buscarDetalhamento();
				apresentarMsgNegocio(Severity.INFO, "MESSAGE_REMARCAR_PACIENTE_AGENDA_SUCESSO");
			} else if (cameFrom.equals(TELA_CANCELADAS)) {
				cirurgiasCanceladasAgendaMedicoController.recebeParametros(
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerMatricula(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerVinCodigo(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getUnfSeq(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getIndFuncaoProf(), 
						pesquisaAgendaCirurgiaController.getEspecialidade().getSeq(), 
						pesquisaAgendaCirurgiaController.getUnidadeFuncional().getSeq(), 
						pesquisaAgendaCirurgiaController.getPacCodigo());
				cirurgiasCanceladasAgendaMedicoController.getDataModelCancelados().reiniciarPaginator();
				apresentarMsgNegocio(Severity.INFO, "MESSAGE_REAGENDAR_PACIENTE_PROCEDIMENTO_CANCELADO_SUCESSO");
			} else if (cameFrom.equals(TELA_LISTA_ESPERA)) {
				visualizaListaEsperaAgendaMedicoController.recebeParametros(
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerMatricula(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerVinCodigo(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getUnfSeq(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getIndFuncaoProf(), 
						pesquisaAgendaCirurgiaController.getEspecialidade().getSeq(), 
						pesquisaAgendaCirurgiaController.getUnidadeFuncional().getSeq(), 
						pesquisaAgendaCirurgiaController.getPacCodigo());
				visualizaListaEsperaAgendaMedicoController.getDataModelEspera().reiniciarPaginator();
				apresentarMsgNegocio(Severity.INFO, "MESSAGE_REAGENDAR_PACIENTE_LISTA_ESPERA_SUCESSO");
			} else if (cameFrom.equals(REMARCAR_AGENDA_PLANEJADA) || cameFrom.equals(REMARCAR_AGENDA_ESCALA)){
				atualizarEscalaPortalAgendamentoController.iniciar();
				apresentarMsgNegocio(Severity.INFO, "MESSAGE_REMARCAR_PACIENTE_AGENDA_SUCESSO");
			}
			this.closeDialog("remarcarPacienteAgendaModalWG");
			limparTela();
		} catch (BaseException e) {
			this.openDialog("remarcarPacienteAgendaModalWG");
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void obterTextoCabecalhoModal() {
		StringBuilder retorno = new StringBuilder();
		
		if (cameFrom.equals(TELA_DETALHAMENTO) || cameFrom.equals(REMARCAR_AGENDA_ESCALA) || cameFrom.equals(REMARCAR_AGENDA_PLANEJADA)) {
			retorno.append(getBundle().getString("LABEL_REMARCAR_PACIENTE_AGENDA_JUST_REMARC_DE"));
		} else if (cameFrom.equals(TELA_CANCELADAS)) {
			retorno.append(getBundle().getString("TITLE_REAGENDAR_PACIENTE_PROCEDIMENTO_CANCELADO"));
		} else if (cameFrom.equals(TELA_LISTA_ESPERA)) {
			retorno.append(getBundle().getString("TITLE_REAGENDAR_PACIENTE_LISTA_ESPERA"));
		}
		
		retorno.append(' ').append(WordUtils.capitalizeFully(agenda.getPaciente().getNome())).append(" - ")
		.append(CoreUtil.formataProntuario(agenda.getPaciente().getProntuario())).append(" - ")
		.append(CoreUtil.capitalizaTextoFormatoAghu(agenda.getProcedimentoCirurgico().getDescricao()));
		
		if (retorno.length() > 100) {
			textoTooltip = retorno.toString();
			tituloModal = StringUtils.abbreviate(retorno.toString(), 100);
			return;
		}
		textoTooltip = null;
		tituloModal = retorno.toString();
	}
	
	public void obterResumoDia() {
		resumoDia = blocoCirurgicoPortalPlanejamentoFacade.obterResumoAgendamento(dataSelecionada, agenda);
		listarSalasCirurgicas();
	}
	
	public void limparTela() {
		dataSelecionada = null;
		justificativa = null;
		resumoDia = null;
		agenda = null;
		textoTooltip = null;
		tituloModal = null;
		salasCirurgicas = null;
		salaCirurgica = null;
		renderJustificativa = Boolean.TRUE;
		dominio = null;
	}

	public void cancelar() {
		limparTela();
	}
	
	public void listarSalasCirurgicas() {
		salasCirurgicas = this.blocoCirurgicoPortalPlanejamentoFacade.pesquisarSalasCirurgicasParaReagendamentoPaciente(
			dataSelecionada, agenda.getProfAtuaUnidCirgs(), agenda.getEspecialidade().getSeq(), agenda.getUnidadeFuncional().getSeq());
	}

	public Date getDataSelecionada() {
		return dataSelecionada;
	}

	public void setDataSelecionada(Date dataSelecionada) {
		this.dataSelecionada = dataSelecionada;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getResumoDia() {
		return resumoDia;
	}

	public void setResumoDia(String resumoDia) {
		this.resumoDia = resumoDia;
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

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Integer getAgdSeq() {
		return agdSeq;
	}

	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}

	public void setRenderJustificativa(Boolean renderJustificativa) {
		this.renderJustificativa = renderJustificativa;
	}

	public Boolean getRenderJustificativa() {
		return renderJustificativa;
	}

	public void setSalasCirurgicas(List<MbcSalaCirurgica> salasCirurgicas) {
		this.salasCirurgicas = salasCirurgicas;
	}

	public List<MbcSalaCirurgica> getSalasCirurgicas() {
		return salasCirurgicas;
	}

	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}

	public MbcSalaCirurgica getSalaCirurgica() {
		return salaCirurgica;
	}

	public void setDominio(DominioTipoAgendaJustificativa dominio) {
		this.dominio = dominio;
	}

	public DominioTipoAgendaJustificativa getDominio() {
		return dominio;
	}
}
