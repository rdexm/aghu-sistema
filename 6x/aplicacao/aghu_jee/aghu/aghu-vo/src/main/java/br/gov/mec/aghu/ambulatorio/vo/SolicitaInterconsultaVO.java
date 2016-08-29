package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;





public class SolicitaInterconsultaVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3744659660855302289L;
	
	private Short espSeq;
	private String espSigla;
	private String espNome;
	private Integer equipeSeq;
	private String equipeNome;
	private Short equipeVinCodigo;
	private Integer equipeMatricula;
	private String rpfNome;
	private Integer rpfCodigo;
	private Short rpfVinCodigo;
	private Integer rpfMatricula;
	private String observacao;
	private Date previsao;
	private String agendaFormatado;
	private String equipeFormatado;
	private String profissionalFormatado;
	private Integer codPaciente;
	private Boolean edita;
	private String traco = " - ";
	

	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	public String getEspSigla() {
		return espSigla;
	}
	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}
	public String getEspNome() {
		return espNome;
	}
	public void setEspNome(String espNome) {
		this.espNome = espNome;
	}
	public Integer getEquipeSeq() {
		return equipeSeq;
	}
	public void setEquipeSeq(Integer equipeSeq) {
		this.equipeSeq = equipeSeq;
	}
	public String getEquipeNome() {
		return equipeNome;
	}
	public void setEquipeNome(String equipeNome) {
		this.equipeNome = equipeNome;
	}
	public Short getEquipeVinCodigo() {
		return equipeVinCodigo;
	}
	public void setEquipeVinCodigo(Short equipeVinCodigo) {
		this.equipeVinCodigo = equipeVinCodigo;
	}
	public Integer getEquipeMatricula() {
		return equipeMatricula;
	}
	public void setEquipeMatricula(Integer equipeMatricula) {
		this.equipeMatricula = equipeMatricula;
	}
	public String getRpfNome() {
		return rpfNome;
	}
	public void setRpfNome(String rpfNome) {
		this.rpfNome = rpfNome;
	}
	public Integer getRpfCodigo() {
		return rpfCodigo;
	}
	public void setRpfCodigo(Integer rpfCodigo) {
		this.rpfCodigo = rpfCodigo;
	}
	public Short getRpfVinCodigo() {
		return rpfVinCodigo;
	}
	public void setRpfVinCodigo(Short rpfVinCodigo) {
		this.rpfVinCodigo = rpfVinCodigo;
	}
	public Integer getRpfMatricula() {
		return rpfMatricula;
	}
	public void setRpfMatricula(Integer rpfMatricula) {
		this.rpfMatricula = rpfMatricula;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public Date getPrevisao() {
		return previsao;
	}
	public void setPrevisao(Date previsao) {
		this.previsao = previsao;
	}
	public Integer getCodPaciente() {
		return codPaciente;
	}
	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}
	public String getAgendaFormatado() {
		if(this.getEspSigla() != null && this.getEspNome() != null){
			agendaFormatado = this.getEspSigla() + traco + this.getEspNome();
		}
		return agendaFormatado;
	}
	public void setAgendaFormatado(String agendaFormatado) {
		this.agendaFormatado = agendaFormatado;
	}
	public String getEquipeFormatado() {
		if(this.getEquipeSeq() != null && this.getEquipeNome() != null){
			equipeFormatado = this.getEquipeSeq() + traco + this.getEquipeNome();
		}
		return equipeFormatado;
	}
	public void setEquipeFormatado(String equipeFormatado) {
		this.equipeFormatado = equipeFormatado;
	}
	public String getProfissionalFormatado() {
		if(this.getRpfVinCodigo() != null &&  this.getRpfMatricula() != null &&  this.getRpfNome() != null){
			profissionalFormatado = this.getRpfVinCodigo() + traco + this.getRpfMatricula() + traco + this.getRpfNome();
		}
		return profissionalFormatado;
	}
	public void setProfissionalFormatado(String profissionalFormatado) {
		this.profissionalFormatado = profissionalFormatado;
	}
	public Boolean getEdita() {
		return edita;
		
	}
	public void setEdita(Boolean edita) {
		this.edita = edita;
	}
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getEspSeq());
		umHashCodeBuilder.append(this.getEspSigla());
		umHashCodeBuilder.append(this.getEspNome());
		umHashCodeBuilder.append(this.getEquipeSeq());
		umHashCodeBuilder.append(this.getEquipeNome());
		umHashCodeBuilder.append(this.getEquipeVinCodigo());
		umHashCodeBuilder.append(this.getEquipeMatricula());
		umHashCodeBuilder.append(this.getRpfNome());
		umHashCodeBuilder.append(this.getRpfCodigo());
		umHashCodeBuilder.append(this.getRpfVinCodigo());
		umHashCodeBuilder.append(this.getRpfMatricula());
		umHashCodeBuilder.append(this.getObservacao());
		umHashCodeBuilder.append(this.getPrevisao());
		umHashCodeBuilder.append(this.getCodPaciente());
		umHashCodeBuilder.append(this.getEquipeMatricula());
		umHashCodeBuilder.append(this.getAgendaFormatado());
		umHashCodeBuilder.append(this.getEquipeFormatado());
		umHashCodeBuilder.append(this.getProfissionalFormatado());

		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SolicitaInterconsultaVO other = (SolicitaInterconsultaVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();

		umEqualsBuilder.append(this.getEspSeq(), other.getEspSeq());
		umEqualsBuilder.append(this.getEspSigla(),other.getEspSigla());
		umEqualsBuilder.append(this.getEspNome(), other.getEspNome());
		umEqualsBuilder.append(this.getEquipeSeq(),other.getEquipeSeq());
		umEqualsBuilder.append(this.getEquipeNome(), other.getEquipeNome());
		umEqualsBuilder.append(this.getEquipeVinCodigo(), other.getEquipeVinCodigo());
		umEqualsBuilder.append(this.getEquipeMatricula(),other.getEquipeMatricula());
		umEqualsBuilder.append(this.getRpfNome(), other.getRpfNome());
		umEqualsBuilder.append(this.getRpfCodigo(), other.getRpfCodigo());
		umEqualsBuilder.append(this.getRpfVinCodigo(),other.getRpfVinCodigo());
		umEqualsBuilder.append(this.getRpfMatricula(), other.getRpfMatricula());
		umEqualsBuilder.append(this.getObservacao(),other.getObservacao());
		umEqualsBuilder.append(this.getPrevisao(), other.getPrevisao());
		umEqualsBuilder.append(this.getCodPaciente(), other.getCodPaciente());
		umEqualsBuilder.append(this.getEquipeMatricula(),other.getEquipeMatricula());
		umEqualsBuilder.append(this.getAgendaFormatado(), other.getAgendaFormatado());
		umEqualsBuilder.append(this.getEquipeFormatado(), other.getEquipeFormatado());
		umEqualsBuilder.append(this.getProfissionalFormatado(), other.getProfissionalFormatado());

		return umEqualsBuilder.isEquals();
	}
	
	
	
}
