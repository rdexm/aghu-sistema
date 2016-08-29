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
@Table(name = "V_RAP_SERV_VALIDA_PME", schema = "AGH")
@Immutable
public class VMedicoSolicitante extends BaseEntityId<VMedicoSolicitanteId> implements java.io.Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4861086814677837238L;

	private VMedicoSolicitanteId id;
	
	private Integer matricula;
	
	private Integer vinCodigo;
	
	private String nome;
	
	private String nomeUsual;
	
	private String sigla;
	
	private String nroRegConselho;
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "matricula", column = @Column(name = "MATRICULA")),
			@AttributeOverride(name = "vinCodigo", column = @Column(name = "VIN_CODIGO")),
			@AttributeOverride(name = "nome", column = @Column(name = "NOME")),
			@AttributeOverride(name = "nomeUsual", column = @Column(name = "NOME_USUAL")),
			@AttributeOverride(name = "sigla", column = @Column(name = "SIGLA")),
			@AttributeOverride(name = "nroRegConselho", column = @Column(name = "NRO_REG_CONSELHO")) })
	public VMedicoSolicitanteId getId() {
		return id;
	}

	public void setId(VMedicoSolicitanteId id) {
		this.id = id;		
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

	@Column(name = "NOME", insertable = false, updatable = false)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "NOME_USUAL", insertable = false, updatable = false)
	public String getNomeUsual() {
		return nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}
	
	@Column(name = "SIGLA", insertable = false, updatable = false)
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "NRO_REG_CONSELHO", insertable = false, updatable = false)
	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}
	
	public enum Fields {
		MATRICULAII("matricula"),
		VIN_CODIGO("vinCodigo"),
		NOMEII("nome"),
		NOME_USUAL("nomeUsual"),
		SIGLA("sigla"),
		NRO_REG_CONSELHO("nroRegConselho");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	
	// ##### GeradorEqualsHashCodeMain #####
			@Override
		    public int hashCode() {

		          HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		          umHashCodeBuilder.append(this.getMatricula());
		          umHashCodeBuilder.append(this.getNome());
		          

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

		          if (!(obj instanceof VMedicoSolicitante)) {
		                 return false;
		          }

		          VMedicoSolicitante other = (VMedicoSolicitante) obj;
		          EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		          umEqualsBuilder.append(this.getMatricula(), other.getMatricula());
		          umEqualsBuilder.append(this.getNome(), other.getNome());

		          return umEqualsBuilder.isEquals();
		    }

			// ##### GeradorEqualsHashCodeMain #####

}
