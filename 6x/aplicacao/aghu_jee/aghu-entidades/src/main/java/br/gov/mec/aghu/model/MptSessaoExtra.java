package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptExtSq1", sequenceName="AGH.MPT_EXT_SQ1", allocationSize = 1)
@Table(name = "MPT_SESSAO_EXTRA", schema = "AGH")

public class MptSessaoExtra extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = -8119437772014668251L;
	
	private Integer seq;
	private MptHorarioSessao mptHorarioSessao;
	private MptJustificativa mptJustificativa;
	private RapServidores rapServidores;
	private RapServidores rapServidoresSolicitado;
	private Date criadoEm;	
	private Integer version; 	
	private String justificativa;
		
	public MptSessaoExtra() {
		
	}

	public enum Fields {
		
		SEQ("seq"),
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
	
	
//	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MptSessaoExtra)) {
			return false;
		}
		MptSessaoExtra other = (MptSessaoExtra) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptExtSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HRS_SEQ", referencedColumnName = "SEQ", nullable=false)
	public MptHorarioSessao getMptHorarioSessao() {
		return mptHorarioSessao;
	}

	public void setMptHorarioSessao(MptHorarioSessao mptHorarioSessao) {
		this.mptHorarioSessao = mptHorarioSessao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "JUS_SEQ", referencedColumnName = "SEQ", nullable=false)
	public MptJustificativa getMptJustificativa() {
		return mptJustificativa;
	}

	public void setMptJustificativa(MptJustificativa mptJustificativa) {
		this.mptJustificativa = mptJustificativa;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_AUTORIZADOR", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_AUTORIZADOR", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_SOLICITADO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_SOLICITADO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresSolicitado() {
		return rapServidoresSolicitado;
	}

	public void setRapServidoresSolicitado(RapServidores rapServidoresSolicitado) {
		this.rapServidoresSolicitado = rapServidoresSolicitado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "JUSTIFICATIVA", length = 200)
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

}
