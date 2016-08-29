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

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptCtsSq1", sequenceName="AGH.MPT_CTS_SQ1", allocationSize = 1)
@Table(name = "MPT_CARACTERISTICA_TIPO_SESSAO", schema = "AGH")
public class MptCaracteristicaTipoSessao  extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7000791252358299137L;
	
	private Short seq;
	private MptCaracteristica mptCaracteristica;
	private MptTipoSessao mptTipoSessao;
	private Date criadoEm;
	private RapServidores servidor;
	
	private Short tpsSeq;
	
	public MptCaracteristicaTipoSessao() {
		
	}

	public MptCaracteristicaTipoSessao(Short seq, String descricao, DominioSituacao indSituacao,
			String sigla, Date criadoEm, RapServidores servidor) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptCtsSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return seq;
	}
	
	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAR_SEQ", nullable = false)
	public MptCaracteristica getMptCaracteristica() {
		return mptCaracteristica;
	}

	public void setMptCaracteristica(MptCaracteristica mptCaracteristica) {
		this.mptCaracteristica = mptCaracteristica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPS_SEQ", nullable = false)
	public MptTipoSessao getMptTipoSessao() {
		return mptTipoSessao;
	}

	public void setMptTipoSessao(MptTipoSessao mptTipoSessao) {
		this.mptTipoSessao = mptTipoSessao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name="TPS_SEQ", insertable=false, updatable=false)
	public Short getTpsSeq() {
		return tpsSeq;
	}

	public void setTpsSeq(Short tpsSeq) {
		this.tpsSeq = tpsSeq;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		CAR("mptCaracteristica"),
		TPS("mptTipoSessao"),
		CAR_SEQ("mptCaracteristica.seq"),
		TPS_SEQ("mptTipoSessao.seq"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		SERVIDOR("servidor"),
		TPS_SEQ_S("tpsSeq");
		
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
		MptCaracteristicaTipoSessao other = (MptCaracteristicaTipoSessao) obj;
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
