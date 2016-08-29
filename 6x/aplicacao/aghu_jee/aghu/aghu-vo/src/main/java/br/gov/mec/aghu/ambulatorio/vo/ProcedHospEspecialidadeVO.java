package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class ProcedHospEspecialidadeVO implements Serializable {
	
	private static final long serialVersionUID = -6682281877258270100L;
	private Integer phiSeq;
	private String descricao;
	private DominioSituacao situacao;
	
	public Integer getPhiSeq() {
		return phiSeq;
	}
	
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(phiSeq).append(descricao)
				.append(situacao)
				.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		ProcedHospEspecialidadeVO other = (ProcedHospEspecialidadeVO) obj;
		return new EqualsBuilder().append(phiSeq, other.phiSeq)
				.append(descricao, other.descricao)
				.append(situacao, other.situacao)
				.isEquals();
	}
	
}
