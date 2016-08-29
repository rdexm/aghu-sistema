package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;


@SuppressWarnings({"PMD.HierarquiaControllerIncorreta"})
public class ModalJustificativaLaudosController extends ActionController {//ManterJustificativaLaudosController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2970655089954913345L;

    private static final String PAGE_PRESCRICAOMEDICA_MANTER_DIAGNOSTICOS_PACIENTE = "prescricaomedica-manterDiagnosticosPaciente";

	private Integer apaSeq; 
	private Short seqp;
	
	
	/**
	 * Tipo Sumario: ALTA, OBITO, ANTECIPACAO.
	 */
	private String altaTipoOrigem;
	
	private MpmAltaSumario altaSumario;
	
	private String voltarPara;
	
	private Integer atendimentoSeq;
	
	@Inject
	private ManterJustificativaLaudosController manterJustificativaLaudosController;
	
	@Inject
	private ManterSumarioAltaConclusaoController manterSumarioAltaConclusaoController;
	
	/**
	 * 
	 */
	//@Override
	public void inicio() {
	 

		manterJustificativaLaudosController.inicio(true);
		
		MpmAltaSumarioId id = new MpmAltaSumarioId();
		id.setApaAtdSeq(manterJustificativaLaudosController.getAtendimentoSeqAux());
		id.setApaSeq(this.getApaSeq());
		id.setSeqp(this.getSeqp()); 
		this.altaSumario = manterJustificativaLaudosController.getPrescricaoMedicaFacade().obterAltaSumario(id);
		
	
	}
	
	public String continuarConclusaoSumario() throws JRException, SystemException, IOException {
		String returnValue = PAGE_PRESCRICAOMEDICA_MANTER_DIAGNOSTICOS_PACIENTE;
		
		try {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
			} catch (UnknownHostException e) {
				nomeMicrocomputador = null;
			}
			manterJustificativaLaudosController.getPrescricaoMedicaFacade().continuarConclusaoSumarioAlta(this.getAltaSumario(), nomeMicrocomputador);
			returnValue = manterSumarioAltaConclusaoController.concluirSumarioAlta();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			returnValue = null;
		}
		
		return returnValue;
	}
	
	public String voltar() {
		String returnValue = "manterSumarioAlta";
		
		try {
			manterJustificativaLaudosController.getPrescricaoMedicaFacade().cancelarConclusaoSumarioAlta(this.getAltaSumario());
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			returnValue = null;
		}
		
		return returnValue;
	}


	/**
	 * @param apaSeq the apaSeq to set
	 */
	public void setApaSeq(Integer apaSeq) {
		this.apaSeq = apaSeq;
	}


	/**
	 * @return the apaSeq
	 */
	public Integer getApaSeq() {
		return apaSeq;
	}

	/**
	 * @param seqp the seqp to set
	 */
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	/**
	 * @return the seqp
	 */
	public Short getSeqp() {
		return seqp;
	}

	/**
	 * @param altaSumario the altaSumario to set
	 */
	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	/**
	 * @return the altaSumario
	 */
	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	/**
	 * @param altaTipoOrigem the altaTipoOrigem to set
	 */
	public void setAltaTipoOrigem(String altaTipoOrigem) {
		this.altaTipoOrigem = altaTipoOrigem;
	}

	/**
	 * @return the altaTipoOrigem
	 */
	public String getAltaTipoOrigem() {
		return altaTipoOrigem;
	}

	public ManterJustificativaLaudosController getManterJustificativaLaudosController() {
		return manterJustificativaLaudosController;
	}

	public void setManterJustificativaLaudosController(
			ManterJustificativaLaudosController manterJustificativaLaudosController) {
		this.manterJustificativaLaudosController = manterJustificativaLaudosController;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

}
