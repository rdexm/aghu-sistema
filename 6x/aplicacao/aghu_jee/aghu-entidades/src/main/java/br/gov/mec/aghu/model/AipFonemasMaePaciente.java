package br.gov.mec.aghu.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.annotations.Cascade;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AIP_FONEMAS_MAE_PACIENTE", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = {"PAC_CODIGO", "FON_FONEMA"}))
@SequenceGenerator(name="aipFmpSq1", sequenceName="AGH.AIP_FMP_SQ1", allocationSize = 1)

public class AipFonemasMaePaciente extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1918645912922472106L;
	private Integer seq;
	private AipFonemas aipFonemas;
	private AipPacientes aipPaciente;

	private Set<AipPosicaoFonemasMaePaciente> posicaoFonemas = new HashSet<AipPosicaoFonemasMaePaciente>(
			0);

	public AipFonemasMaePaciente() {
	}

	public AipFonemasMaePaciente(Integer seq, AipFonemas aipFonemas,
			AipPacientes aipPaciente) {
		this.seq = seq;
		this.aipFonemas = aipFonemas;
		this.aipPaciente = aipPaciente;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipFmpSq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FON_FONEMA", nullable = false)
	public AipFonemas getAipFonemas() {
		return this.aipFonemas;
	}

	public void setAipFonemas(AipFonemas aipFonemas) {
		this.aipFonemas = aipFonemas;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable = false)
	public AipPacientes getAipPaciente() {
		return this.aipPaciente;
	}

	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fonema", orphanRemoval=true)
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public Set<AipPosicaoFonemasMaePaciente> getPosicaoFonemas() {
		return posicaoFonemas;
	}

	public void setPosicaoFonemas(
			Set<AipPosicaoFonemasMaePaciente> posicaoFonemas) {
		this.posicaoFonemas = posicaoFonemas;
	}

	public enum Fields {
		SEQ("seq"), PACIENTE("aipPaciente"), COD_PACIENTE("aipPaciente.codigo"), FONEMA("aipFonemas"), FON_FONEMA(
				"aipFonemas.fonema");

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
		AipFonemasMaePaciente other = (AipFonemasMaePaciente) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}
