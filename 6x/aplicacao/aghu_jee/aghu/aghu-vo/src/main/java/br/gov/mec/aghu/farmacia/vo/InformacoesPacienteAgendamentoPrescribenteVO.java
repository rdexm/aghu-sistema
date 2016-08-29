package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;

public class InformacoesPacienteAgendamentoPrescribenteVO implements Serializable {

	private static final long serialVersionUID = -8153500080107548062L;
	
	private Integer ifpSeq;
	private Integer pmeAtdSeq;
	private Integer pmeSeq;
	private Short unfSeq;
	private Integer atdSeq;
	private String descricao;
	private Integer pacCodigo;
	private String pacNome;
	private Integer prontuario;
	private Boolean indInfVerificada;
	
	public enum Fields {
		IFP_SEQ("ifpSeq"),
		PME_ATD_SEQ("pmeAtdSeq"),
		PME_SEQ("pmeSeq"),
		UNF_SEQ("unfSeq"),
		ATD_SEQ("atdSeq"),
		DESCRICAO("descricao"),
		PAC_CODIGO("pacCodigo"),
		PAC_NOME("pacNome"),
		PRONTUARIO("prontuario"),
		IND_INF_VERIFICA("indInfVerificada"),
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public Boolean getIndInfVerificada() {
		return indInfVerificada;
	}
	public void setIndInfVerificada(Boolean indInfVerificada) {
		this.indInfVerificada = indInfVerificada;
	}
	
	public Integer getIfpSeq() {
		return ifpSeq;
	}
	public void setIfpSeq(Integer ifpSeq) {
		this.ifpSeq = ifpSeq;
	}
	public Integer getPmeAtdSeq() {
		return pmeAtdSeq;
	}
	public void setPmeAtdSeq(Integer pmeAtdSeq) {
		this.pmeAtdSeq = pmeAtdSeq;
	}
	public Integer getPmeSeq() {
		return pmeSeq;
	}
	public void setPmeSeq(Integer pmeSeq) {
		this.pmeSeq = pmeSeq;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public String getPacNome() {
		return pacNome;
	}
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
}
