package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.core.persistence.EntityCompositeId;

public class VMamMedicamentosId implements EntityCompositeId{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6168652877240452025L;
	private String descricaoMat;
	private	DominioSituacaoMedicamento	indSituacao;
	private	Integer	matCodigo;
	private String descricao;
	
	@Column(name = "DESCRICAO_MAT", nullable = false, length = 60)
	public String getDescricaoMat() {
		return descricaoMat;
	}

	public void setDescricaoMat(String descricaoMat) {
		this.descricaoMat = descricaoMat;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false,length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoMedicamento getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoMedicamento indSituacao) {
		this.indSituacao = indSituacao;
	}
	@Column(name = "MAT_CODIGO",nullable = false, precision = 6)
	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getDescricaoMat());
		umHashCodeBuilder.append(this.getIndSituacao());
		umHashCodeBuilder.append(this.getMatCodigo());
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
		if (!(obj instanceof VMamMedicamentosId)) {
			return false;
		}
		VMamMedicamentosId other = (VMamMedicamentosId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getDescricaoMat(), other.getDescricaoMat());
		umEqualsBuilder.append(this.getIndSituacao(), other.getIndSituacao());
		umEqualsBuilder.append(this.getMatCodigo(), other.getMatCodigo());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
}
