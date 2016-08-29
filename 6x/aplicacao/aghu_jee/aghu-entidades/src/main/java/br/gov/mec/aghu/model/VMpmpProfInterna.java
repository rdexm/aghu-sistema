package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "V_MPM_PROF_INTERNA", schema = "AGH")
@Immutable
public class VMpmpProfInterna extends BaseEntityId<VMpmpProfInternaId> implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4826325800453351775L;
	
	private VMpmpProfInternaId id;
	
	private Integer matricula;
	
	private Integer vinCodigo;
	
	private String indSituacao;
	
	private String responsavel;

	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "matricula", column = @Column(name = "MATRICULA")),
			@AttributeOverride(name = "vinCodigo", column = @Column(name = "VIN_CODIGO")),
			@AttributeOverride(name = "indSituacao", column = @Column(name = "IND_SITUACAO")),
			@AttributeOverride(name = "responsavel", column = @Column(name = "RESPONSAVEL")) })
	public VMpmpProfInternaId getId() {
		return id;
	}

	public void setId(VMpmpProfInternaId id) {
		this.id = id;		
	}
	
	public enum Fields {
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		IND_SITUACAO("indSituacao"),
		RESPONSAVEL("responsavel");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Column(name = "MATRICULA", insertable = false, updatable = false)
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	@Column(name = "VIN_CODIGO", insertable = false, updatable = false)
	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	@Column(name = "IND_SITUACAO", insertable = false, updatable = false)
	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "RESPONSAVEL", insertable = false, updatable = false)
	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}
	
	// ##### GeradorEqualsHashCodeMain #####
		@Override
	    public int hashCode() {

	          HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
	          umHashCodeBuilder.append(this.getMatricula());
	          umHashCodeBuilder.append(this.getResponsavel());
	          

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

	          if (!(obj instanceof VMpmpProfInterna)) {
	                 return false;
	          }

	          VMpmpProfInterna other = (VMpmpProfInterna) obj;
	          EqualsBuilder umEqualsBuilder = new EqualsBuilder();
	          umEqualsBuilder.append(this.getMatricula(), other.getMatricula());
	          umEqualsBuilder.append(this.getResponsavel(), other.getResponsavel());

	          return umEqualsBuilder.isEquals();
	    }

		// ##### GeradorEqualsHashCodeMain #####

}
