package br.gov.mec.aghu.model;

// Generated 19/10/2011 15:37:43 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraints.Length;
import org.quartz.CronTrigger;
import org.quartz.Trigger;

import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.core.quartz.MECScheduler;

/**
 * AghJobDetail generated by hbm2java
 */
@Entity
@Table(name = "AGH_JOB_DETAIL", schema = "AGH")
@SequenceGenerator(name="aghJodSq1", sequenceName="AGH.AGH_JOD_SQ1", allocationSize = 1)
public class AghJobDetail extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	private static final Log LOG = LogFactory.getLog(AghJobDetail.class);

	private static final long serialVersionUID = 4948767270010742536L;
	
	
	private Integer seq;
	private String nomeProcesso;
	private Date agendado;
	private RapServidores servidor;
	private DominioSituacaoJobDetail indSituacao;
	private String log;
	//private byte[] quartzTriggerHandleArray;
	private Trigger trigger;
	
	
	public AghJobDetail(String nomeProcesso, Date agendado,
			RapServidores servidor, DominioSituacaoJobDetail indSituacao) {
		super();
		this.nomeProcesso = nomeProcesso;
		this.agendado = agendado;
		this.servidor = servidor;
		this.indSituacao = indSituacao;
	}

	public AghJobDetail() {
	}
	
	public AghJobDetail(String np, Trigger qth) {
		this.nomeProcesso = np;
		
		this.setTrigger(qth);
	}
	
	public AghJobDetail(String np, RapServidores serMatricula, Trigger qth) {
		this.nomeProcesso = np;
		
		this.servidor = serMatricula;
		
		if (qth != null) {
			this.setTrigger(qth);
		}
	}


	


	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghJodSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NOME_PROCESSO", nullable = false, length = 100)
	@Length(max = 100)
	public String getNomeProcesso() {
		return this.nomeProcesso;
	}

	public void setNomeProcesso(String nomeProcesso) {
		this.nomeProcesso = nomeProcesso;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "AGENDADO_PARA", nullable = false, length = 29)
	public Date getAgendado() {
		return this.agendado;
	}

	public void setAgendado(Date agendadoEm) {
		this.agendado = agendadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoJobDetail getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoJobDetail i) {
		this.indSituacao = i;
	}
	
	@Transient
	public Boolean getIsPauseState() {
		boolean isPause = false;
		
		try {
			if (this.getTrigger() != null) {
				MECScheduler agendadorAghu = MECScheduler.getInstance();
				isPause = agendadorAghu.isStatePause(this.getTrigger().getKey().getName());
			}
		} catch (Exception e) {
			LOG.warn("getIsPauseState", e);
			isPause = false;
		}
		
		return isPause;
	}
	
	
	@Transient
	public String getCronExpression() {
		String returnValue = null;
		if (this.getTrigger() != null && this.getTrigger() instanceof CronTrigger) {
			CronTrigger cronTrigger = (CronTrigger) this.getTrigger();
			returnValue = cronTrigger.getCronExpression();
		}		
		return returnValue;
	}
	
	
	//@Lob
	@Column(name = "LOG")
	public String getLog() {
		return this.log;
	}

	public void setLog(String alog) {
		this.log = alog;
	}
	
	//@Lob
	// Para a Arquitetura JEE6 do AGHU devido a serializacao do objeto a coluna que vale eh quartz
	// Todos os Jobs deverão ser recriados durante a implantacao da nova Arquitetura.
	//@Column(name = "QUARTZ_TRIGGER_HANDLE")
	@Column(name = "QUARTZ")
	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	
	/*
	@Lob
	@Column(name = "QUARTZ_TRIGGER_HANDLE")
	@Type(type = "org.hibernate.type.BinaryType")
	public byte[] getQuartzTriggerHandleArray() {
		return quartzTriggerHandleArray;
	}
	
	public void setQuartzTriggerHandleArray(byte[] quartzTriggerHandleArray) {
		this.quartzTriggerHandleArray = quartzTriggerHandleArray;
	}
	*/
	
	/*
	@Transient
	public void setTrigger(Trigger trg) {
		this.trigger = trg;
		this.quartzTriggerHandleArray = this.quartzTriggerHandleToArray(trg);
	}
	
	@Transient
	public Trigger getTrigger() {
		if (this.trigger == null && this.quartzTriggerHandleArray != null) {
			this.trigger = this.arrayToQuartzTriggerHandle(this.quartzTriggerHandleArray);
		}
		
		return this.trigger;
	}
	*/

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA" ),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Transient
	public Date getDataProximaExecucao() {
		Date returnValue = null;
		if (this.getTrigger() != null) {
			returnValue = this.getTrigger().getFireTimeAfter(new Date());
		}		
		return returnValue;
	}
		
	
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
		if (!(obj instanceof AghJobDetail)) {
			return false;
		}
		AghJobDetail other = (AghJobDetail) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "AghJobDetail [seq=" + seq + ", nomeProcesso=" + nomeProcesso
				+ ", indSituacao="	+ indSituacao 
				+ ", agendado=" + agendado
				+ "]";
	}
	/*
	private byte[] quartzTriggerHandleToArray(Trigger quartzTriggerHandle) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(bos);
		    out.writeObject(quartzTriggerHandle);
		    out.close();
		} catch (IOException e) {
			LOG.warn("quartzTriggerHandleToArray", e);
		}
		
		return bos.toByteArray();
	}
	
	private Trigger arrayToQuartzTriggerHandle(byte[] objectArray) {
		Trigger triggerHandle = null;
		
		try {
			// Deserialize a partir de uma matriz de bytes
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(objectArray));
			Object obj = in.readObject();
			
			if (obj instanceof Trigger) {
				triggerHandle = (Trigger) obj;	
			} 
		// comentando pois tem dependencia com o seam.
//			else if (obj instanceof QuartzTriggerHandle) {
//				QuartzTriggerHandle handle = (QuartzTriggerHandle) obj;
//				triggerHandle = handle.getTrigger();
//			}
			
			in.close();
		} catch (IOException e) {
			LOG.warn("arrayToQuartzTriggerHandle", e);
		} catch (ClassNotFoundException e) {
			LOG.warn("arrayToQuartzTriggerHandle", e);
		} 
//		catch (SchedulerException e) {
//			LOG.warn("arrayToQuartzTriggerHandle", e);
//		}
		
		return triggerHandle;
	}
	*/
	
	public enum Fields {
		SEQ("seq")
		, NOME_PROCESSO("nomeProcesso")
		, AGENDADO_EM("agendado")
		, SERVIDOR("servidor")
		, IND_SITUACAO("indSituacao")
		;


		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


}
