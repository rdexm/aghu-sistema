package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.DisponibilidadeHorariosVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Controller da tela de pesquisa de disponibilidade de horarios de emergência por grade/consulta
 */
public class DisponibilidadeHorariosEmergenciaPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<DisponibilidadeHorariosVO> dataModel;

	private static final Log LOG = LogFactory.getLog(DisponibilidadeHorariosEmergenciaPaginatorController.class);
	
	private static final long serialVersionUID = -7608004206009149053L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	private AacGradeAgendamenConsultas gradeAgendamenConsultas;
	

	private static final String LISTAR_CONSULTAS_POR_GRADE= "ambulatorio-listarConsultasPorGrade";
	
	// resultado da pesquisa na lista de valores
	private List<EspCrmVO> listaEspCrmVO = new ArrayList<EspCrmVO>();
	
	// FILTRO
	private Integer seq;
	
	private AacPagador pagador;
	private AacTipoAgendamento autorizacao;
	private AacCondicaoAtendimento condicao;
	
	/* CRM PROFESSOR */
	private ProfessorCrmInternacaoVO professorPesq;
	private VAacSiglaUnfSalaVO siglaUnfSalaVO;
	
	// SELEÇÃO
	private Integer seqSelected;
	private String labelZonaSala;
	private String labelZona;

	private Boolean disponibilidade;
	private Boolean primeiraVez = true;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		try {
			labelZona = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LABEL_ZONA);
			labelZonaSala = labelZona + "/" +  parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LABEL_SALA);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro ao buscar parâmetro", e);
		}
	}

	/**
	 * Método executado ao iniciar a controller
	 */
	public void iniciar() {
	 

	 

		if (this.primeiraVez){
			dataModel.reiniciarPaginator();
		}
		this.primeiraVez = false;
	
	}
	
	
	@Override
	public Long recuperarCount() {
		return ambulatorioFacade.listarDisponibilidadeHorariosEmergenciaCount();
	}
	
	@Override
	public List<DisponibilidadeHorariosVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<DisponibilidadeHorariosVO> lista = new ArrayList<DisponibilidadeHorariosVO>();
		try {
			lista = ambulatorioFacade.listarDisponibilidadeHorariosEmergencia(orderProperty, asc);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		if (lista == null) {
			return new ArrayList<DisponibilidadeHorariosVO>();
		} else if(lista.size()>(firstResult+maxResult)){
			lista = lista.subList(firstResult, firstResult+maxResult);
		} else {
			lista = lista.subList(firstResult, lista.size());
		}
		return lista;
	}	
	
	
	/**
	 * Verifica botão de excluir, só aparecerá se não tiver agendamento para a grade.
	 */
	public boolean verificarConsultas(AacGradeAgendamenConsultas gradeAgendamenConsulta){
		return ambulatorioFacade.verificarGradeConsulta(gradeAgendamenConsulta.getSeq());
	}

	public String listarConsultasPorGrade(){
		return LISTAR_CONSULTAS_POR_GRADE;
	}
	
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeqSelected() {
		return seqSelected;
	}

	public void setSeqSelected(Integer seqSelected) {
		this.seqSelected = seqSelected;
	}

	public AacGradeAgendamenConsultas getGradeAgendamenConsultas() {
		return gradeAgendamenConsultas;
	}

	public void setGradeAgendamenConsultas(
			AacGradeAgendamenConsultas gradeAgendamenConsultas) {
		this.gradeAgendamenConsultas = gradeAgendamenConsultas;
	}

	public List<EspCrmVO> getListaEspCrmVO() {
		return listaEspCrmVO;
	}

	public void setListaEspCrmVO(List<EspCrmVO> listaEspCrmVO) {
		this.listaEspCrmVO = listaEspCrmVO;
	}

	public ProfessorCrmInternacaoVO getProfessorPesq() {
		return professorPesq;
	}

	public void setProfessorPesq(ProfessorCrmInternacaoVO professorPesq) {
		this.professorPesq = professorPesq;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public VAacSiglaUnfSalaVO getSiglaUnfSalaVO() {
		return siglaUnfSalaVO;
	}

	public void setSiglaUnfSalaVO(VAacSiglaUnfSalaVO siglaUnfSalaVO) {
		this.siglaUnfSalaVO = siglaUnfSalaVO;
	}
	
	public String getLabelZonaSala() {
		return labelZonaSala;
	}

	public void setLabelZonaSala(String labelZonaSala) {
		this.labelZonaSala = labelZonaSala;
	}

	public AacPagador getPagador() {
		return pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}

	public AacTipoAgendamento getAutorizacao() {
		return autorizacao;
	}

	public void setAutorizacao(AacTipoAgendamento autorizacao) {
		this.autorizacao = autorizacao;
	}

	public AacCondicaoAtendimento getCondicao() {
		return condicao;
	}

	public void setCondicao(AacCondicaoAtendimento condicao) {
		this.condicao = condicao;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public Boolean getDisponibilidade() {
		return disponibilidade;
	}

	public void setDisponibilidade(Boolean disponibilidade) {
		this.disponibilidade = disponibilidade;
	}
	public DynamicDataModel<DisponibilidadeHorariosVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<DisponibilidadeHorariosVO> dataModel) {
	 this.dataModel = dataModel;
	}
}
