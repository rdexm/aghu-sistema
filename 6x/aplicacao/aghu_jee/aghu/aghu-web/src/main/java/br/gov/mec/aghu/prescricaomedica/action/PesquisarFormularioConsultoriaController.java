package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.SolicitacaoConsultoriaVO;
import br.gov.mec.aghu.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;

public class PesquisarFormularioConsultoriaController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -446428241048680825L;
	
	private static final String RELATORIO_LISTA_CONSULTORIAS = "prescricaomedica-relatorioFormularioConsultoriaPdf";
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private PrescricaoMedicaVO prescricaoMedicaVO;
	private MpmPrescricaoMedicaVO mpmPrescricaoMedicaVO;
	private List<SolicitacaoConsultoriaVO> listaSolicitacaoConsultoria;
	private String voltarPara;
	private Boolean validarIndImpSolicConsultoria = Boolean.FALSE;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
		pesquisar();
	}
	
	public String voltar() {		
		return voltarPara;
	}

	private void pesquisar() {
		mpmPrescricaoMedicaVO = this.prescricaoMedicaFacade
				.pesquisaPrescricaoMedicaPorAtendimentoESeq(
						prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getSeq(),
						prescricaoMedicaVO.getPrescricaoMedica().getId().getSeq());
		
		listaSolicitacaoConsultoria = this.prescricaoMedicaFacade.pesquisaSolicitacaoConsultoriaPorAtendimentoEDataInicioEDataFim(
						prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getSeq(), 
						mpmPrescricaoMedicaVO.getDthrInicio(), 
						mpmPrescricaoMedicaVO.getDthrFim());
	}
	
	public String visualizarRelatorio() {
		return RELATORIO_LISTA_CONSULTORIAS;
	}

	public MpmPrescricaoMedicaVO getMpmPrescricaoMedicaVO() {
		return mpmPrescricaoMedicaVO;
	}

	public void setMpmPrescricaoMedicaVO(
			MpmPrescricaoMedicaVO mpmPrescricaoMedicaVO) {
		this.mpmPrescricaoMedicaVO = mpmPrescricaoMedicaVO;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public List<SolicitacaoConsultoriaVO> getListaSolicitacaoConsultoria() {
		return listaSolicitacaoConsultoria;
	}

	public void setListaSolicitacaoConsultoria(
			List<SolicitacaoConsultoriaVO> listaSolicitacaoConsultoria) {
		this.listaSolicitacaoConsultoria = listaSolicitacaoConsultoria;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public Boolean getValidarIndImpSolicConsultoria() {
		if (validarIndImpSolicConsultoria == null) {
			validarIndImpSolicConsultoria = Boolean.FALSE;
		}
		return validarIndImpSolicConsultoria;
	}

	public void setValidarIndImpSolicConsultoria(Boolean validarIndImpSolicConsultoria) {
		this.validarIndImpSolicConsultoria = validarIndImpSolicConsultoria;
	}
}
