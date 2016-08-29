package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptTtsSq1", sequenceName="AGH.MPT_TTS_SQ1", allocationSize = 1)
@Table(name = "MPT_TURNO_TIPO_SESSAO", schema = "AGH")

public class MptTurnoTipoSessao extends BaseEntitySeq<Short> implements java.io.Serializable {
	
	private static final long serialVersionUID = 1474219604682991991L;
	
	private Short seq;
	private MptTipoSessao tipoSessao;
	private DominioTurno turno;
	private Date horaInicio;
	private Date horaFim;
	private Date criadoEm;
	private Integer version; 	
	
	public MptTurnoTipoSessao() {
		
	}

	public MptTurnoTipoSessao(Short seq, MptTipoSessao tipoSessao, DominioTurno turno,
			Date horaInicio, Date horaFim, Date criadoEm) {
		this.seq = seq;
		this.tipoSessao = tipoSessao;
		this.turno = turno;
		this.horaInicio = horaInicio;
		this.horaFim = horaFim;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptTtsSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPS_SEQ", nullable = false, referencedColumnName = "seq")
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	@Column(name = "TURNO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTurno getTurno() {
		return turno;
	}

	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HR_INICIO", nullable = false, length = 7)
	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HR_FIM", nullable = false, length = 7)
	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {
		
		SEQ("seq"),
		TIPO_SESSAO("tipoSessao"),
		TPS_SEQ("tipoSessao.seq"),
		TURNO("turno"),
		HR_INICIO("horaInicio"),
		HR_FIM("horaFim"),
		CRIADO_EM("criadoEm");
		
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
		MptTurnoTipoSessao other = (MptTurnoTipoSessao) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
