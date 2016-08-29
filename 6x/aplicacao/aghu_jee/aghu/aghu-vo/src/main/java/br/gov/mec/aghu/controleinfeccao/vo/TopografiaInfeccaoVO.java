package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class TopografiaInfeccaoVO implements BaseBean {

	private static final long serialVersionUID = 8449010552707373610L;

	private Short seq;
	private String descricao;
	private Boolean supervisao;
	private DominioSituacao situacao;
	private Boolean pacienteInfectado;
	private Boolean contaInfecadoMensal;
	private Date criadoEm;
	private Date alteradoEm;
	
	private Integer matricula;
	private Short vinCodigo;
	private Integer matriculaMovi;
	private Short vinCodigoMovi;
	
	// formatados na ON e serao usados na tela de pesquisa.
	private Boolean situacaoBoolean;
	private Date criadoAlteradoEm;

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getSupervisao() {
		return supervisao;
	}

	public void setSupervisao(Boolean supervisao) {
		this.supervisao = supervisao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Boolean getPacienteInfectado() {
		return pacienteInfectado;
	}

	public void setPacienteInfectado(Boolean pacienteInfectado) {
		this.pacienteInfectado = pacienteInfectado;
	}

	public Boolean getContaInfecadoMensal() {
		return contaInfecadoMensal;
	}

	public void setContaInfecadoMensal(Boolean contaInfecadoMensal) {
		this.contaInfecadoMensal = contaInfecadoMensal;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	
	public Integer getMatriculaMovi() {
		return matriculaMovi;
	}

	public void setMatriculaMovi(Integer matriculaMovi) {
		this.matriculaMovi = matriculaMovi;
	}

	public Short getVinCodigoMovi() {
		return vinCodigoMovi;
	}

	public void setVinCodigoMovi(Short vinCodigoMovi) {
		this.vinCodigoMovi = vinCodigoMovi;
	}
	
	public Boolean getSituacaoBoolean() {
		return situacaoBoolean;
	}

	public void setSituacaoBoolean(Boolean situacaoBoolean) {
		this.situacaoBoolean = situacaoBoolean;
	}

	public Date getCriadoAlteradoEm() {
		return criadoAlteradoEm;
	}

	public void setCriadoAlteradoEm(Date criadoAlteradoEm) {
		this.criadoAlteradoEm = criadoAlteradoEm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
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

		TopografiaInfeccaoVO other = (TopografiaInfeccaoVO) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}	
		} else if (!seq.equals(other.seq)){
			return false;
		}	
		return true;
	}

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SUPERVISAO("supervisao"),
		IND_SITUACAO("situacao"),
		PACIENTE_INFECTADO("pacienteInfectado"),
		IND_PAC_INFECTADO("pacienteInfectado"),
		IND_CONTA_INFEC_MENSAL("contaInfecadoMensal"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		MATRICULA("matricula"), 
		CODIGO_VINCULO("vinCodigo"),
		MATRICULA_MOVI("matriculaMovi"), 
		CODIGO_VINCULO_MOVI("vinCodigoMovi")
		;


		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public String toString() {
		return "TopografiaInfeccaoVO [seq=" + seq + ", descricao=" + descricao
				+ ", supervisao=" + supervisao + ", situacao=" + situacao
				+ ", pacienteInfectado=" + pacienteInfectado
				+ ", contaInfecadoMensal=" + contaInfecadoMensal
				+ ", criadoEm=" + criadoEm + ", alteradoEm=" + alteradoEm
				+ ", matricula=" + matricula + ", vinCodigo=" + vinCodigo
				+ ", matriculaMovi=" + matriculaMovi + ", vinCodigoMovi="
				+ vinCodigoMovi + ", situacaoBoolean=" + situacaoBoolean
				+ ", criadoAlteradoEm=" + criadoAlteradoEm + "]";
	}

}
