package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxCptSq1", sequenceName="AGH.MTX_CPT_SQ1", allocationSize = 1)
@Table(name = "MTX_CONTATO_PACIENTES", schema = "AGH")
public class MtxContatoPacientes extends BaseEntitySeq<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3344224632027820283L;

	private Integer seq;
	private AipPacientes paciente;
	private String nome;
	private Long telefone;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxCptSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable = false)
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@Column(name = "NOME", length = 200, nullable = false)
	@Length(max = 200)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "TELEFONE", precision = 12, scale = 0, nullable = false)
	public Long getTelefone() {
		return telefone;
	}

	public void setTelefone(Long telefone) {
		this.telefone = telefone;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		PACIENTE("paciente"),
		PAC_CODIGO("paciente.codigo"),
		NOME("nome"),
		TELEFONE("telefone");

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
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
        umHashCodeBuilder.append(this.getNome());
        umHashCodeBuilder.append(this.getTelefone());
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
		MtxContatoPacientes other = (MtxContatoPacientes) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
        umEqualsBuilder.append(this.getNome(), other.getNome());
        umEqualsBuilder.append(this.getTelefone(), other.getTelefone());
        return umEqualsBuilder.isEquals();
	}
}
