package br.gov.mec.aghu.model;

// Generated 28/03/2012 15:17:44 by Hibernate Tools 3.4.0.CR1

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
@SequenceGenerator(name="mbcFotSq1", sequenceName="AGH.MBC_FOT_SQ1", allocationSize = 1)
@Table(name = "MBC_FICHA_ORGAO_TRANSPLANTES", schema = "AGH")
public class MbcFichaOrgaoTransplante extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -8083179387845466286L;
	private Integer seq;
	private Integer version;
	private MbcFichaAnestesias mbcFichaAnestesias;
	private MbcOrgaoTransplantado mbcOrgaoTransplantados;
	private Date dthrRetirada;
	private RapServidores servidor;
	private Date criadoEm;

	public MbcFichaOrgaoTransplante() {
	}

	public MbcFichaOrgaoTransplante(Integer seq, Integer version,
			MbcFichaAnestesias mbcFichaAnestesias,
			MbcOrgaoTransplantado mbcOrgaoTransplantados, Date dthrRetirada,
			RapServidores servidor, Date criadoEm) {
		super();
		this.seq = seq;
		this.version = version;
		this.mbcFichaAnestesias = mbcFichaAnestesias;
		this.mbcOrgaoTransplantados = mbcOrgaoTransplantados;
		this.dthrRetirada = dthrRetirada;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcFotSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIC_SEQ", nullable = false)
	public MbcFichaAnestesias getMbcFichaAnestesias() {
		return this.mbcFichaAnestesias;
	}

	public void setMbcFichaAnestesias(MbcFichaAnestesias mbcFichaAnestesias) {
		this.mbcFichaAnestesias = mbcFichaAnestesias;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OTR_SEQ", nullable = false)
	public MbcOrgaoTransplantado getMbcOrgaoTransplantados() {
		return this.mbcOrgaoTransplantados;
	}

	public void setMbcOrgaoTransplantados(
			MbcOrgaoTransplantado mbcOrgaoTransplantados) {
		this.mbcOrgaoTransplantados = mbcOrgaoTransplantados;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_RETIRADA", nullable = false, length = 29)
	public Date getDthrRetirada() {
		return this.dthrRetirada;
	}

	public void setDthrRetirada(Date dthrRetirada) {
		this.dthrRetirada = dthrRetirada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public enum Fields {
		
		SEQ("seq"),
		FICHA_ANESTESIA("mbcFichaAnestesias"),
		ORGAO_TRANSPLATADO("mbcOrgaoTransplantados"),
		DTHR_RETIRADA("dthrRetirada"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		OTR_SEQ("mbcOrgaoTransplantados.seq"),
		FIC_SEQ("mbcFichaAnestesias.seq");

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
		if (!(obj instanceof MbcFichaOrgaoTransplante)) {
			return false;
		}
		MbcFichaOrgaoTransplante other = (MbcFichaOrgaoTransplante) obj;
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

}
