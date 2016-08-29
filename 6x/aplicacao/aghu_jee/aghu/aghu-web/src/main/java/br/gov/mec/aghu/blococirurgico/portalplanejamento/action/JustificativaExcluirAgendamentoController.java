package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

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


public class JustificativaExcluirAgendamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -1293859173026419738L;
	
	private static final String ID_MODAL_JUSTIFICATIVA_CIRURGIAS = "modalJustificativaCirurgiasWG";

	@EJB
	private IBlocoCirurgicoFacadeBean blocoCirurgicoFacadeBean;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;

	@Inject
	private PesquisaAgendaCirurgiaController pesquisaAgendaCirurgiaController;
	
	@Inject
	private VisualizaListaEsperaAgendaMedicoController visualizaListaEsperaAgendaMedicoController;
	
	@Inject
	private CirurgiasCanceladasAgendaMedicoController cirurgiasCanceladasAgendaMedicoController;

	private String cameFrom;
	private MbcAgendas agenda;
	private String tituloModal;
	private String justificativa;
	private String titulo;
	private Integer seq;
	private String tituloJustificativa;
	private boolean gravouJustificativa;
	
	private final String LISTA_ESPERA = "listaEspera";
	private final String LISTA_CANCELADOS = "listaCirurgiasCanceladas";
	
	public void gravarJustificativaExclusaoCirurgia() {
		
		if(StringUtils.isBlank(this.justificativa)){
			apresentarMsgNegocio("inputJustificativa",Severity.ERROR, "CAMPO_OBRIGATORIO", super.getBundle().getString("LABEL_JUSTIFICATIVA"));
			return;
		} else if (this.justificativa.length() > 500){
			this.justificativa = this.justificativa.replaceAll("\\r\\n", "\n"); 
		}
		
		
		try {
			MbcAgendaJustificativa agendaJustificativa = new MbcAgendaJustificativa();
			MbcAgendaJustificativaId agendaJustificativaId = new MbcAgendaJustificativaId();
			agendaJustificativaId.setAgdSeq(agenda.getSeq());
			agendaJustificativa.setId(agendaJustificativaId);
			agendaJustificativa.setMbcAgendas(agenda);
			
			if(cameFrom.equals("listaEspera")) {
				agendaJustificativa.setTipo(DominioTipoAgendaJustificativa.ELE);
			} else if (cameFrom.equals("listaCirurgiasCanceladas")) {
				agendaJustificativa.setTipo(DominioTipoAgendaJustificativa.ELC);
			}
			
			agendaJustificativa.setJustificativa(getJustificativa());
			
			this.blocoCirurgicoFacadeBean.excluirAgenda(agendaJustificativa);
			
			if(cameFrom.equals(LISTA_ESPERA)) {
				this.gravouJustificativa = true;
				visualizaListaEsperaAgendaMedicoController.recebeParametros(
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerMatricula(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerVinCodigo(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getUnfSeq(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getIndFuncaoProf(), 
						pesquisaAgendaCirurgiaController.getEspecialidade().getSeq(), 
						pesquisaAgendaCirurgiaController.getUnidadeFuncional().getSeq(), 
						pesquisaAgendaCirurgiaController.getPacCodigo());
				
				this.visualizaListaEsperaAgendaMedicoController.getDataModelEspera().reiniciarPaginator();
			} else if (cameFrom.equals(LISTA_CANCELADOS)) {
				this.gravouJustificativa = true;
				cirurgiasCanceladasAgendaMedicoController.recebeParametros(
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerMatricula(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerVinCodigo(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getUnfSeq(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getIndFuncaoProf(), 
						pesquisaAgendaCirurgiaController.getEspecialidade().getSeq(), 
						pesquisaAgendaCirurgiaController.getUnidadeFuncional().getSeq(), 
						pesquisaAgendaCirurgiaController.getPacCodigo());
				
				this.cirurgiasCanceladasAgendaMedicoController.getDataModelCancelados().reiniciarPaginator();
			}				
			
			this.justificativa = null;
			closeDialog(ID_MODAL_JUSTIFICATIVA_CIRURGIAS);
		} catch (final BaseException e) {
			this.justificativa = null;
			this.apresentarExcecaoNegocio(e);			
		}
	}
	
	public String obterTituloExcluirCirurgia(final Integer seq) {
		if(seq != null) {
			agenda = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorAgdSeq(seq, null, new Enum[] {MbcAgendas.Fields.PACIENTE, MbcAgendas.Fields.PROCEDIMENTO});

			if (agenda != null) {
				StringBuilder titulo = new StringBuilder();
				if(cameFrom.equals(LISTA_ESPERA)){
					titulo.append(getBundle().getString("TITLE_JUSTIFICATIVA_CIRURGIA_LISTA_ESPERA") ).append(' ');
					tituloJustificativa = getBundle().getString("TITLE_JUSTIFICATIVA_EXCLUSAO_PACIENTE_EM_LISTA_ESPERA");
				}
				if(cameFrom.equals(LISTA_CANCELADOS)){
					titulo.append(getBundle().getString("TITLE_JUSTIFICATIVA_CIRURGIA_LISTA_CANCELADOS") ).append(' ');
					tituloJustificativa = getBundle().getString("TITLE_JUSTIFICATIVA_EXCLUSAO_PACIENTE_EM_LISTA_CANCELADOS");
				}	
				
				titulo.append(agenda.getPaciente().getNome() ).append( " - ")
				.append(CoreUtil.formataProntuario(agenda.getPaciente().getProntuario()) ).append( " - ")
				.append(agenda.getProcedimentoCirurgico().getDescricao());

				this.setTitulo(titulo.toString());				
			}
		} 		
		
		return titulo;
	}
	
	public Integer buscarListaCirurgiasCanceladasSeqId(Integer seq){
		this.setCameFrom(LISTA_CANCELADOS);
		return seq;
	}
	
	public Integer buscarListaEsperaSeqId(Integer seq){
		this.setCameFrom(LISTA_ESPERA);
		return seq;
	}

	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}

	public void limparParametros(){
		if(this.gravouJustificativa){ // Gravação com sucesso
			if(LISTA_ESPERA.equalsIgnoreCase(this.cameFrom)) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_PACIENTE_LISTA_ESPERA");
			} else if (LISTA_CANCELADOS.equalsIgnoreCase(this.cameFrom)) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_PACIENTE_LISTA_CANCELADOS");
			}
		}
		this.gravouJustificativa = false;
		this.agenda = new MbcAgendas();
		this.seq = null;
		this.justificativa = null;
		this.tituloModal = null;
		this.cameFrom = null;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public MbcAgendas getAgenda() {
		return agenda;
	}

	public void setAgenda(MbcAgendas agenda) {
		this.agenda = agenda;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getListaEspera() {
		return LISTA_ESPERA;
	}

	public String getListaCancelados() {
		return LISTA_CANCELADOS;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setTituloJustificativa(String tituloJustificativa) {
		this.tituloJustificativa = tituloJustificativa;
	}

	public String getTituloJustificativa() {
		return tituloJustificativa;
	}
	
	public String getTituloModal() {
		return tituloModal;
	}
	
	public void setTituloModal(String tituloModal) {
		this.tituloModal = tituloModal;
	}
	
	public boolean isGravouJustificativa() {
		return gravouJustificativa;
	}
	
	public void setGravouJustificativa(boolean gravouJustificativa) {
		this.gravouJustificativa = gravouJustificativa;
	}
	
}
