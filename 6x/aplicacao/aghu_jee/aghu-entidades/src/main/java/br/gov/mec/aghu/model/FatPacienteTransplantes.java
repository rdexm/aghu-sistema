package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "FAT_PACIENTE_TRANSPLANTES", schema = "AGH")
public class FatPacienteTransplantes extends BaseEntityId<FatPacienteTransplantesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7728703123639950908L;
	private FatPacienteTransplantesId id;
	private Date dtTransplante;
	private Long nroAihTransplante;
	private Short iphPhoSeq;
	private Integer iphSeq;
	private String indSituacao;
	
	private FatTipoTratamentos fatTipoTratamentos;
	private AipPacientes paciente;

	public FatPacienteTransplantes() {
	}

	public FatPacienteTransplantes(FatPacienteTransplantesId id,
			String indSituacao) {
		this.id = id;
		this.indSituacao = indSituacao;
	}

	public FatPacienteTransplantes(FatPacienteTransplantesId id,
			Date dtTransplante, Long nroAihTransplante, Short iphPhoSeq,
			Integer iphSeq, String indSituacao) {
		this.id = id;
		this.dtTransplante = dtTransplante;
		this.nroAihTransplante = nroAihTransplante;
		this.iphPhoSeq = iphPhoSeq;
		this.iphSeq = iphSeq;
		this.indSituacao = indSituacao;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "ttrCodigo", column = @Column(name = "TTR_CODIGO", nullable = false, length = 10)),
			@AttributeOverride(name = "dtInscricaoTransplante", column = @Column(name = "DT_INSCRICAO_TRANSPLANTE", nullable = false, length = 7)) })
	public FatPacienteTransplantesId getId() {
		return this.id;
	}

	public void setId(FatPacienteTransplantesId id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_TRANSPLANTE", length = 7)
	public Date getDtTransplante() {
		return this.dtTransplante;
	}

	public void setDtTransplante(Date dtTransplante) {
		this.dtTransplante = dtTransplante;
	}

	@Column(name = "NRO_AIH_TRANSPLANTE", precision = 13, scale = 0)
	public Long getNroAihTransplante() {
		return this.nroAihTransplante;
	}

	public void setNroAihTransplante(Long nroAihTransplante) {
		this.nroAihTransplante = nroAihTransplante;
	}

	@Column(name = "IPH_PHO_SEQ", precision = 4, scale = 0)
	public Short getIphPhoSeq() {
		return this.iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	@Column(name = "IPH_SEQ", precision = 8, scale = 0)
	public Integer getIphSeq() {
		return this.iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TTR_SEQ", insertable = false, updatable = false)
	public FatTipoTratamentos getFatTipoTratamentos() {
		return fatTipoTratamentos;
	}

	public void setFatTipoTratamentos(FatTipoTratamentos fatTipoTratamentos) {
		this.fatTipoTratamentos = fatTipoTratamentos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", insertable = false, updatable = false)
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	public enum Fields {
		PAC_CODIGO("id.pacCodigo"),
		TTR_CODIGO("id.ttrCodigo"),
		DT_INSCRICAO_TRANSPLANTE("id.dtInscricaoTransplante"),
		DT_TRANSPLANTE("dtTransplante"),
		TIPO_TRATAMENTO("fatTipoTratamentos"),
		TTR_SEQ("fatTipoTratamentos.seq"), 
		AIP_PACIENTE("paciente");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof FatPacienteTransplantes)) {
			return false;
		}
		FatPacienteTransplantes other = (FatPacienteTransplantes) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
