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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptPseSq1", sequenceName="AGH.MPT_PSE_SQ1", allocationSize = 1)
@Table(name = "MPT_PROTOCOLO_SESSAO", schema = "AGH")

public class MptProtocoloSessao extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = 7136877534967025517L;
	
	private Integer seq;
	private String titulo;
	private Integer qtdCiclo;
	private MptTipoSessao tipoSessao;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	private Short tpsSeq;
			
	public MptProtocoloSessao() {
		
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPseSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "TITULO", nullable = false, length = 120)
	@Length(max = 120)
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "QTD_CICLOS", nullable = false, length = 3)
	public Integer getQtdCiclo() {
		return qtdCiclo;
	}

	public void setQtdCiclo(Integer qtdCiclo) {
		this.qtdCiclo = qtdCiclo;
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
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(
			RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "TPS_SEQ", insertable = false, updatable = false)
	public Short getTpsSeq() {
		return tpsSeq;
	}

	public void setTpsSeq(Short tpsSeq) {
		this.tpsSeq = tpsSeq;
	}

	public enum Fields {
		
		SEQ("seq"),
		TITULO("titulo"),
		TIPO_SESSAO("tipoSessao"),
		TIPO_SESSAO_SEQ("tipoSessao.seq"),
		TPS_SEQ("tpsSeq"),
		CRIADO_EM("criadoEm"),
		QTD_CICLO("qtdCiclo");
		
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
		MptProtocoloSessao other = (MptProtocoloSessao) obj;
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
