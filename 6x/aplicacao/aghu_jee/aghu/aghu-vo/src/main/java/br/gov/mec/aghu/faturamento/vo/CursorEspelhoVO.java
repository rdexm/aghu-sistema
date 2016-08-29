package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioBoletimAmbulatorio;



public class CursorEspelhoVO implements Serializable {

	private static final long serialVersionUID = 798642843278088923L;
	
	private Long procedimentoHosp;
	private String codAtvProf;
	private Short idade;
	private Integer competencia;
	private Short iphPhoSeq;
	private Integer iphSeq;
	private Integer fccSeq;
	private Integer fcfSeq;
	private DominioBoletimAmbulatorio origemInf;
	private String classificacao;
	private String servico;
	private Short unidadeFuncional;

	private Long quantidade;
	private BigDecimal vlrAnestes;
	private BigDecimal vlrProc;
	private BigDecimal vlrSadt;
	private BigDecimal vlrServHosp;
	private BigDecimal vlrServProf;
	private String servClass;
	
	public Long getProcedimentoHosp() {
		return procedimentoHosp;
	}
	public void setProcedimentoHosp(Long procedimentoHosp) {
		this.procedimentoHosp = procedimentoHosp;
	}
	public String getCodAtvProf() {
		return codAtvProf;
	}
	public void setCodAtvProf(String codAtvProf) {
		this.codAtvProf = codAtvProf;
	}
	public Short getIdade() {
		return idade;
	}
	public void setIdade(Short idade) {
		this.idade = idade;
	}
	public Integer getCompetencia() {
		return competencia;
	}
	public void setCompetencia(Integer competencia) {
		this.competencia = competencia;
	}
	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}
	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}
	public Integer getIphSeq() {
		return iphSeq;
	}
	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	public Integer getFccSeq() {
		return fccSeq;
	}
	public void setFccSeq(Integer fccSeq) {
		this.fccSeq = fccSeq;
	}
	public Integer getFcfSeq() {
		return fcfSeq;
	}
	public void setFcfSeq(Integer fcfSeq) {
		this.fcfSeq = fcfSeq;
	}
	public DominioBoletimAmbulatorio getOrigemInf() {
		return origemInf;
	}
	public void setOrigemInf(DominioBoletimAmbulatorio origemInf) {
		this.origemInf = origemInf;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public BigDecimal getVlrAnestes() {
		return vlrAnestes;
	}
	public void setVlrAnestes(BigDecimal vlrAnestes) {
		this.vlrAnestes = vlrAnestes;
	}
	public BigDecimal getVlrProc() {
		return vlrProc;
	}
	public void setVlrProc(BigDecimal vlrProc) {
		this.vlrProc = vlrProc;
	}
	public BigDecimal getVlrSadt() {
		return vlrSadt;
	}
	public void setVlrSadt(BigDecimal vlrSadt) {
		this.vlrSadt = vlrSadt;
	}
	public BigDecimal getVlrServHosp() {
		return vlrServHosp;
	}
	public void setVlrServHosp(BigDecimal vlrServHosp) {
		this.vlrServHosp = vlrServHosp;
	}
	public BigDecimal getVlrServProf() {
		return vlrServProf;
	}
	public void setVlrServProf(BigDecimal vlrServProf) {
		this.vlrServProf = vlrServProf;
	}
	public String getClassificacao() {
		return classificacao;
	}
	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}
	public String getServico() {
		return servico;
	}
	public void setServico(String servico) {
		this.servico = servico;
	}
	public Short getUnidadeFuncional() {
		return unidadeFuncional;
	}
	public void setUnidadeFuncional(Short unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	/**
	 * eSchweigert 12/08/2013
	 * Não alterar, esta aplicando campos para agrupamento
	 */
	@Override
	public int hashCode() {

		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		
		umHashCodeBuilder.append(procedimentoHosp);
		umHashCodeBuilder.append(codAtvProf);
		umHashCodeBuilder.append(idade);
		umHashCodeBuilder.append(competencia);
		umHashCodeBuilder.append(iphPhoSeq);
		umHashCodeBuilder.append(iphSeq);
		umHashCodeBuilder.append(fccSeq);
		umHashCodeBuilder.append(fcfSeq);
		umHashCodeBuilder.append(origemInf);
		umHashCodeBuilder.append(servico);
		umHashCodeBuilder.append(classificacao);
		umHashCodeBuilder.append(servClass);

		return umHashCodeBuilder.toHashCode();
	}


	/**
	 * eSchweigert 12/08/2013
	 * Não alterar, esta aplicando campos para agrupamento
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CursorEspelhoVO)) {
			return false;
		}
		CursorEspelhoVO other = (CursorEspelhoVO) obj;

		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(procedimentoHosp, other.procedimentoHosp);
		umEqualsBuilder.append(codAtvProf, other.codAtvProf);
		umEqualsBuilder.append(idade, other.idade);
		umEqualsBuilder.append(competencia, other.competencia);
		umEqualsBuilder.append(iphPhoSeq, other.iphPhoSeq);
		umEqualsBuilder.append(iphSeq, other.iphSeq);
		umEqualsBuilder.append(fccSeq, other.fccSeq);
		umEqualsBuilder.append(fcfSeq, other.fcfSeq);
		umEqualsBuilder.append(origemInf, other.origemInf);
		umEqualsBuilder.append(servico, other.servico);
		umEqualsBuilder.append(classificacao, other.classificacao);
		umEqualsBuilder.append(servClass, other.servClass);

		return umEqualsBuilder.isEquals();
	}
	public String getServClass() {
		return servClass;
	}
	public void setServClass(String servClass) {
		this.servClass = servClass;
	}
	
	@Override
	public String toString() {
		return "CursorEspelhoVO [procedimentoHosp=" + procedimentoHosp
				+ ", codAtvProf=" + codAtvProf + ", idade=" + idade
				+ ", competencia=" + competencia + ", iphPhoSeq=" + iphPhoSeq
				+ ", iphSeq=" + iphSeq + ", fccSeq=" + fccSeq + ", fcfSeq="
				+ fcfSeq + ", origemInf=" + origemInf + ", classificacao="
				+ classificacao + ", servico=" + servico
				+ ", unidadeFuncional=" + unidadeFuncional + ", quantidade="
				+ quantidade + ", vlrAnestes=" + vlrAnestes + ", vlrProc="
				+ vlrProc + ", vlrSadt=" + vlrSadt + ", vlrServHosp="
				+ vlrServHosp + ", vlrServProf=" + vlrServProf + ", servClass="
				+ servClass + "]";
	}
	
	
	
}