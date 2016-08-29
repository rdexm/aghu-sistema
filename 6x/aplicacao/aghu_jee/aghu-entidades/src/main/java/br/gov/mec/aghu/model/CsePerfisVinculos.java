package br.gov.mec.aghu.model;
import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "CSE_PERFIS_VINCULOS", schema="AGH")
public class CsePerfisVinculos extends BaseEntityId<CsePerfisVinculosId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 318594727401751798L;
	private CsePerfisVinculosId id;
	private String perfil;
	private RapVinculos vinculo;	
	
	public CsePerfisVinculos(){
	}

	public CsePerfisVinculos(CsePerfisVinculosId id){ 
	this.id = id;
	}

	@EmbeddedId()
	@AttributeOverrides( {
		@AttributeOverride(name = "PER_NOME", column = @Column(name = "PER_NOME", nullable = false, length = 30)),
		@AttributeOverride(name = "VIN_CODIGO", column = @Column(name = "VIN_CODIGO", nullable = false, length = 3))
	})
	public CsePerfisVinculosId getId(){
		return this.id;
	}
	
	public void setId(CsePerfisVinculosId id){
		this.id = id;
	}
	
	@Column(name = "PER_NOME", nullable = false, insertable = false, updatable = false)
	public String getPerfil(){
		return perfil;
	}
	
	public void setPerfil(String perfil ){
		this.perfil = perfil;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VIN_CODIGO", nullable = false, insertable = false, updatable = false)
	public RapVinculos getVinculo(){
		return vinculo;
	}
	
	public void setVinculo(RapVinculos vinculo ){
		this.vinculo = vinculo;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CsePerfisVinculos)) {
			return false;
		}
		CsePerfisVinculos castOther = (CsePerfisVinculos) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {

		CSE_PERFIS("perfil"), RAP_VINCULOS("vinculo"), ID_PER_NOME("id.perNome");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
}