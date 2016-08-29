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
@SequenceGenerator(name="mptFavSq1", sequenceName="AGH.MPT_FAV_SQ1", allocationSize = 1)
@Table(name = "MPT_FAVORITOS_SERVIDOR", schema = "AGH")
public class MptFavoritoServidor extends BaseEntitySeq<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1061698662554112341L;

	private Integer seq;
	private RapServidores servidor;
	private MptTipoSessao tipoSessao;
	private MptSalas sala;
	private Date criadoEm;
	private Integer version;

	private Integer serMatricula;
	private Short serVinCodigo;
	
	private Short seqTipoSessao;
	private Short seqSala;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptFavSq1")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPS_SEQ", nullable = false)
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAL_SEQ")
	public MptSalas getSala() {
		return sala;
	}

	public void setSala(MptSalas sala) {
		this.sala = sala;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
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
	
	public enum Fields {
		
		SEQ("seq"),
		SERVIDOR("servidor"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		TIPO_SESSAO("tipoSessao"),
		TPS_SEQ("tipoSessao.seq"),
		SALA("sala"),
		SAL_SEQ("sala.seq"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");
		
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
		MptFavoritoServidor other = (MptFavoritoServidor) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	
	@Column(name="SER_MATRICULA", insertable=false, updatable=false)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name="SER_VIN_CODIGO", insertable=false, updatable=false)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name="TPS_SEQ", insertable=false, updatable=false)
	public Short getSeqTipoSessao() {
		return seqTipoSessao;
	}

	public void setSeqTipoSessao(Short seqTipoSessao) {
		this.seqTipoSessao = seqTipoSessao;
	}

	@Column(name="SAL_SEQ", insertable=false, updatable=false)
	public Short getSeqSala() {
		return seqSala;
	}

	public void setSeqSala(Short seqSala) {
		this.seqSala = seqSala;
	}	
	
}
