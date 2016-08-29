package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class VisualizaListaEsperaAgendaMedicoController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcAgendas> dataModelEspera;

	private static final long serialVersionUID = -7102047377830610820L;
	
//	private static final String VISUALIZA_LISTA_ESPERA_AGENDA_MEDICO = "blococirurgico-visualizaListaEsperaAgendaMedico";
	private static final String TROCAR_LOCAL_ESPERA_EQUIPE = "trocarLocalEspEquipeListaEspera";
	private static final String PESQUISA_AGENDA_CIRURGIA  = "blococirurgico-pesquisaAgendaCirurgia";
	private static final String PLANEJAMENTO_PACIENTE_AGENDA_CRUD = "planejamentoPacienteAgendaCRUD";
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private CadastroPlanejamentoPacienteAgendaController cadastroPlanejamentoPacienteAgendaController;
	
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Short pucUnfSeq;
	private DominioFuncaoProfissional pucIndFuncaoProf;
	private Short espSeq;
	private Short unfSeq;
	private Integer pacCodigo;
	
	private final String situacaoAgenda = DominioSituacaoAgendas.LE.toString();
	private Integer agdSeq;
	
	private MbcAgendas itemSelecionado;
	
	
	public void recebeParametros(Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf,
			Short espSeq, Short unfSeq, Integer pacCodigo) {
		this.dataModelEspera.limparPesquisa();
		this.pucSerMatricula = pucSerMatricula;
		this.pucSerVinCodigo = pucSerVinCodigo;
		this.pucUnfSeq = pucUnfSeq;
		this.pucIndFuncaoProf = pucIndFuncaoProf;
		this.espSeq = espSeq;
		this.unfSeq = unfSeq;
		this.pacCodigo = pacCodigo;
		//reiniciarPaginator(); #32127
	}
	
	public String incluirNaListaEspera() {
		cadastroPlanejamentoPacienteAgendaController.setSeqEspecialidade(espSeq);
		cadastroPlanejamentoPacienteAgendaController.setMatriculaEquipe(pucSerMatricula);
		cadastroPlanejamentoPacienteAgendaController.setVinCodigoEquipe(pucSerVinCodigo);
		cadastroPlanejamentoPacienteAgendaController.setUnfSeqEquipe(pucUnfSeq);
		cadastroPlanejamentoPacienteAgendaController.setIndFuncaoProfEquipe(pucIndFuncaoProf.getCodigo());
		cadastroPlanejamentoPacienteAgendaController.setSeqUnidFuncionalCirugica(unfSeq);
		cadastroPlanejamentoPacienteAgendaController.setSituacaoAgendaParam(situacaoAgenda);
		cadastroPlanejamentoPacienteAgendaController.setCameFrom(PESQUISA_AGENDA_CIRURGIA);
		cadastroPlanejamentoPacienteAgendaController.setAgdSeq(null);
		cadastroPlanejamentoPacienteAgendaController.setCameFromPesqPacOuListaEspera(true);
		return PLANEJAMENTO_PACIENTE_AGENDA_CRUD;
	}
	
	public String editarListaEspera() {
		cadastroPlanejamentoPacienteAgendaController.setSeqEspecialidade(espSeq);
		cadastroPlanejamentoPacienteAgendaController.setMatriculaEquipe(pucSerMatricula);
		cadastroPlanejamentoPacienteAgendaController.setVinCodigoEquipe(pucSerVinCodigo);
		cadastroPlanejamentoPacienteAgendaController.setUnfSeqEquipe(pucUnfSeq);
		cadastroPlanejamentoPacienteAgendaController.setIndFuncaoProfEquipe(pucIndFuncaoProf.getCodigo());
		cadastroPlanejamentoPacienteAgendaController.setSeqUnidFuncionalCirugica(unfSeq);
		cadastroPlanejamentoPacienteAgendaController.setSituacaoAgendaParam(situacaoAgenda);
		cadastroPlanejamentoPacienteAgendaController.setCameFrom(PESQUISA_AGENDA_CIRURGIA);
		cadastroPlanejamentoPacienteAgendaController.setAgdSeq(agdSeq);
	
		return PLANEJAMENTO_PACIENTE_AGENDA_CRUD;
	}
	
	@Override
	public Long recuperarCount() {		
		return blocoCirurgicoPortalPlanejamentoFacade.listarAgendaPorUnidadeEspecialidadeEquipePacienteCount(pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq, pacCodigo);
	}
	
	@Override
	public List<MbcAgendas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<MbcAgendas> listMbcAgendas = blocoCirurgicoPortalPlanejamentoFacade.listarAgendaPorUnidadeEspecialidadeEquipePaciente(firstResult, maxResult, orderProperty, asc, pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucIndFuncaoProf, espSeq, unfSeq, pacCodigo);
		for (MbcAgendas mbcAgenda : listMbcAgendas) {
			blocoCirurgicoPortalPlanejamentoFacade.desatacharMbcAgendas(mbcAgenda);
			mbcAgenda.getPaciente().setNome(ambulatorioFacade.mpmcMinusculo(mbcAgenda.getPaciente().getNome(), 2));
			mbcAgenda.getProcedimentoCirurgico().setDescricao(ambulatorioFacade.mpmcMinusculo(mbcAgenda.getProcedimentoCirurgico().getDescricao(), 1));
		}
		
		return listMbcAgendas;
	}

	public String redirectTrocarLocalEspEquipeListaEspera() {
		return TROCAR_LOCAL_ESPERA_EQUIPE;
	}
	
	public String obterProntuarioFormatado(Integer prontuario) {
		return CoreUtil.formataProntuario(prontuario);
	}

	// Getters and Setters
	public IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		return blocoCirurgicoPortalPlanejamentoFacade;
	}

	public void setBlocoCirurgicoPortalPlanejamentoFacade(IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade) {
		this.blocoCirurgicoPortalPlanejamentoFacade = blocoCirurgicoPortalPlanejamentoFacade;
	}	

	public Short getPucUnfSeq() {
		return pucUnfSeq;
	}

	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}	

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getPucSerMatricula() {
		return pucSerMatricula;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}

	public Short getPucSerVinCodigo() {
		return pucSerVinCodigo;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}

	public DominioFuncaoProfissional getPucIndFuncaoProf() {
		return pucIndFuncaoProf;
	}

	public void setPucIndFuncaoProf(DominioFuncaoProfissional pucIndFuncaoProf) {
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public String getSituacaoAgenda() {
		return situacaoAgenda;
	}

	public Integer getAgdSeq() {
		return agdSeq;
	}

	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}

	public DynamicDataModel<MbcAgendas> getDataModelEspera() {
	 return dataModelEspera;
	}

	public void setDataModelEspera(DynamicDataModel<MbcAgendas> dataModelEspera) {
	 this.dataModelEspera = dataModelEspera;
	}

	public MbcAgendas getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MbcAgendas itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}