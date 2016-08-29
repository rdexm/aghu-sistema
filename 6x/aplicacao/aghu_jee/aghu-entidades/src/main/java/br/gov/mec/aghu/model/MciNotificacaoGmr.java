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
@Table(name = "MCI_NOTIFICACAO_GMR", schema = "AGH")
@SequenceGenerator(name="mciNgmSq1", sequenceName="AGH.MCI_NGM_SQ1", allocationSize = 1)
public class MciNotificacaoGmr extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4321084884999745280L;
	private Integer seq;
	private Integer version;
	private AelResultadoExame resultadoExame;
	private AipPacientes paciente;
	private MciAntimicrobianos mciAntimicrobianos;
	private MciBacteriaMultir mciBacteriaMultir;
	private Boolean notificacaoAtiva;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Date alteradoEm;
	private RapServidores servidorAltera;
	private Integer bmrSeq;

	public MciNotificacaoGmr() {
	}

	public MciNotificacaoGmr(Integer seq, AelResultadoExame resultadoExame, AipPacientes paciente, MciAntimicrobianos mciAntimicrobianos,
			MciBacteriaMultir mciBacteriaMultir, Boolean notificacaoAtiva, Date criadoEm, RapServidores rapServidores, Date alteradoEm,
			RapServidores servidorAltera) {
		this.seq = seq;
		this.resultadoExame = resultadoExame;
		this.paciente = paciente;
		this.mciAntimicrobianos = mciAntimicrobianos;
		this.mciBacteriaMultir = mciBacteriaMultir;
		this.notificacaoAtiva = notificacaoAtiva;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.alteradoEm = alteradoEm;
		this.servidorAltera = servidorAltera;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciNgmSq1")
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
	@JoinColumns( {
			@JoinColumn(name = "REE_ISE_SOE_SEQ", referencedColumnName = "ISE_SOE_SEQ", insertable = false, updatable = false),
			@JoinColumn(name = "REE_ISE_SEQP", referencedColumnName = "ISE_SEQP", insertable = false, updatable = false),
			@JoinColumn(name = "REE_PCL_VEL_EMA_EXA_SIGLA", referencedColumnName = "PCL_VEL_EMA_EXA_SIGLA", insertable = false, updatable = false),
			@JoinColumn(name = "REE_PCL_VEL_EMA_MAN_SEQ", referencedColumnName = "PCL_VEL_EMA_MAN_SEQ", insertable = false, updatable = false),
			@JoinColumn(name = "REE_PCL_VEL_SEQP", referencedColumnName = "PCL_VEL_SEQP", insertable = false, updatable = false),
			@JoinColumn(name = "REE_PCL_CAL_SEQ", referencedColumnName = "PCL_CAL_SEQ", insertable = false, updatable = false),
			@JoinColumn(name = "REE_PCL_SEQP", referencedColumnName = "PCL_SEQP", insertable = false, updatable = false),
			@JoinColumn(name = "REE_SEQP", referencedColumnName = "SEQP", insertable = false, updatable = false) })
	public AelResultadoExame getResultadoExame() {
		return resultadoExame;
	}

	public void setResultadoExame(AelResultadoExame resultadoExame) {
		this.resultadoExame = resultadoExame;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable = false)
	public AipPacientes getPaciente() {
		return this.paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AMB_SEQ")
	public MciAntimicrobianos getMciAntimicrobianos() {
		return mciAntimicrobianos;
	}

	public void setMciAntimicrobianos(MciAntimicrobianos mciAntimicrobianos) {
		this.mciAntimicrobianos = mciAntimicrobianos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BMR_SEQ", nullable = false)
	public MciBacteriaMultir getMciBacteriaMultir() {
		return mciBacteriaMultir;
	}

	public void setMciBacteriaMultir(MciBacteriaMultir mciBacteriaMultir) {
		this.mciBacteriaMultir = mciBacteriaMultir;
	}

	@Column(name="BMR_SEQ", insertable=false, updatable=false)
	public Integer getBmrSeq() {
		return bmrSeq;
	}

	public void setBmrSeq(Integer bmrSeq) {
		this.bmrSeq = bmrSeq;
	}

	@Column(name = "IND_NOTIFICACAO_ATIVA", nullable = false, length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getNotificacaoAtiva() {
		return notificacaoAtiva;
	}

	public void setNotificacaoAtiva(Boolean notificacaoAtiva) {
		this.notificacaoAtiva = notificacaoAtiva;
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_ALTERA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAltera() {
		return servidorAltera;
	}

	public void setServidorAltera(RapServidores servidorAltera) {
		this.servidorAltera = servidorAltera;
	}


	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		PACIENTE("paciente"),
		PAC_CODIGO("paciente.codigo"),
		IND_NOTIFICACAO_ATIVA("notificacaoAtiva"),
		MCI_ANTIMICROBIANOS("mciAntimicrobianos"),
		AMB_SEQ("mciAntimicrobianos.seq"),
		MCI_BACTERIA_MULTIR("mciBacteriaMultir"),
		BMR_SEQ("mciBacteriaMultir.seq"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		RESULTADO_EXAME("resultadoExame"),
		BMRSEQ("bmrSeq");

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
		if (!(obj instanceof MciNotificacaoGmr)) {
			return false;
		}
		MciNotificacaoGmr other = (MciNotificacaoGmr) obj;
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
