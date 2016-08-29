package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;

public class ConsultaRespostasConsultoriaController extends ActionController{

	private static final long serialVersionUID = 9160660847529450383L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private AghEspecialidades especialidade;
	private AipPacientes paciente;
	private String resposta;
	private MpmSolicitacaoConsultoria solicitacaoConsultoria;
	private MpmPrescricaoMedica prescricaoMedica;
	
	private Integer atdSeq;
	private Integer scnSeq;
	private Boolean listaConsultaRet = Boolean.FALSE;
	private String voltarPara;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio() {
		solicitacaoConsultoria = prescricaoMedicaFacade.obterMpmSolicitacaoConsultoriaPorIdComPaciente(atdSeq, scnSeq);
		
		if (listaConsultaRet && solicitacaoConsultoria.getDthrConhecimentoResposta() == null) {
			prescricaoMedica = prescricaoMedicaFacade.obterPrescricaoMedicaPorAtendimento(atdSeq);
			solicitacaoConsultoria.setDthrConhecimentoResposta(new Date());
			try {
				prescricaoMedicaFacade.persistirSolicitacaoConsultoria(solicitacaoConsultoria, atdSeq, prescricaoMedica.getId().getSeq());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		pesquisar();
	}

	public void pesquisar() {
		especialidade = solicitacaoConsultoria.getEspecialidade();
		paciente = solicitacaoConsultoria.getPrescricaoMedica().getAtendimento().getPaciente();
		resposta = prescricaoMedicaFacade.pesquisarRespostasConsultoriaPorAtdSeqConsultoria(atdSeq, scnSeq, Integer.valueOf("1"));
	}
	
	public String voltar() {
		atdSeq = null;
		scnSeq = null;
		listaConsultaRet = false;
		especialidade = null;
		paciente = null;
		solicitacaoConsultoria = null;
		return voltarPara;
	}
	
	public String obterProntuarioFormatado(){
		return CoreUtil.formataProntuario(paciente.getProntuario());
	}

	// getters & setters
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public MpmSolicitacaoConsultoria getSolicitacaoConsultoria() {
		return solicitacaoConsultoria;
	}

	public void setSolicitacaoConsultoria(MpmSolicitacaoConsultoria solicitacaoConsultoria) {
		this.solicitacaoConsultoria = solicitacaoConsultoria;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getScnSeq() {
		return scnSeq;
	}

	public void setScnSeq(Integer scnSeq) {
		this.scnSeq = scnSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getListaConsultaRet() {
		return listaConsultaRet;
	}

	public void setListaConsultaRet(Boolean listaConsultaRet) {
		this.listaConsultaRet = listaConsultaRet;
	}
}
