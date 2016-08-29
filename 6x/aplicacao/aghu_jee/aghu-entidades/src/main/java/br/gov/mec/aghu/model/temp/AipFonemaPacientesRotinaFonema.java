package br.gov.mec.aghu.model.temp;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.annotations.Cascade;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AIP_FONEMA_PACIENTES", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = {"PAC_CODIGO", "FON_FONEMA"}))
public class AipFonemaPacientesRotinaFonema extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -8677157352177275411L;
	
	private Integer seq;
	private AipFonemasRotinaFonema aipFonemas;
	private AipPacientesRotinaFonema aipPaciente;

	private Set<AipPosicaoFonemasPacientesRotinaFonema> posicaoFonemas = new HashSet<AipPosicaoFonemasPacientesRotinaFonema>(
			0);

	public AipFonemaPacientesRotinaFonema() {
	}

	public AipFonemaPacientesRotinaFonema(Integer seq, AipFonemasRotinaFonema aipFonemas,
			AipPacientesRotinaFonema aipPaciente) {
		this.seq = seq;
		this.aipFonemas = aipFonemas;
		this.aipPaciente = aipPaciente;
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FON_FONEMA", nullable = false)
	public AipFonemasRotinaFonema getAipFonemas() {
		return this.aipFonemas;
	}

	public void setAipFonemas(AipFonemasRotinaFonema aipFonemas) {
		this.aipFonemas = aipFonemas;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable = false)
	public AipPacientesRotinaFonema getAipPaciente() {
		return this.aipPaciente;
	}

	public void setAipPaciente(AipPacientesRotinaFonema aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fonema", orphanRemoval=true)
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public Set<AipPosicaoFonemasPacientesRotinaFonema> getPosicaoFonemas() {
		return posicaoFonemas;
	}

	public void setPosicaoFonemas(Set<AipPosicaoFonemasPacientesRotinaFonema> posicaoFonemas) {
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
		AipFonemaPacientesRotinaFonema other = (AipFonemaPacientesRotinaFonema) obj;
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
