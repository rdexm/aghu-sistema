package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class PacientesEmListaDeProcedimentosCanceladosVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6572106920446637738L;

	private String dataCancelamento;				//6
	private String nomePaciente;					//7
	private String prontuario;						//8
	private String procedimento;	    			//9
	private String comentario;						//10
	
	public PacientesEmListaDeProcedimentosCanceladosVO() {
		super();
	}
	
	public String getDataCancelamento() {
		return dataCancelamento;
	}
	public void setDataCancelamento(String dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
}
