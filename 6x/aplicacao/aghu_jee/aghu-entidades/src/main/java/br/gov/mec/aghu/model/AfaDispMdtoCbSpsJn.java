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


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AFA_DISP_MDTO_CB_SPS_JN", schema = "AGH")
@SequenceGenerator(name = "afaDispMdtoCbSpsJnSeq", sequenceName = "AGH.AFA_SDC_jn_seq", allocationSize = 1)
@Immutable
public class AfaDispMdtoCbSpsJn extends BaseJournal implements
	java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5991854540513649450L;
	private Long seq;
	private Date criadoEm;
	private String nroEtiqueta;
	private Long dsmSeq;
	private Long ldiSeq;
	private String micNome;
	private RapServidores servidor;
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "afaDispMdtoCbSpsJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	//Constructors
	public AfaDispMdtoCbSpsJn() {
	}
	
	public AfaDispMdtoCbSpsJn(Long seq, Date criadoEm, String nroEtiqueta, 
			Long dsmSeq, Long ldiSeq, String micNome, 
			RapServidores servidor) {
		
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.nroEtiqueta = nroEtiqueta;
		this.dsmSeq = dsmSeq;
		this.ldiSeq = ldiSeq;
		this.micNome = micNome;
		this.servidor = servidor;
	}
	
	@Column(name = "SEQ", nullable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "NRO_ETIQUETA", nullable = false, length = 15)
	public String getNroEtiqueta() {
		return this.nroEtiqueta;
	}

	public void setNroEtiqueta(String nroEtiqueta) {
		this.nroEtiqueta = nroEtiqueta;
	}
	
	@Column(name = "DSM_SEQ", nullable = false)
	public Long getDsmSeq() {
		return this.dsmSeq;
	}
	
	public void setDsmSeq(Long dsmSeq) {
		this.dsmSeq = dsmSeq;
	}
	
	@Column(name = "LDI_SEQ", nullable = false)
	public Long getLdiSeq() {
		return this.ldiSeq;
	}
	
	public void setLdiSeq(Long ldiSeq) {
		this.ldiSeq = ldiSeq;
	}
	
	@Column(name = "MIC_NOME", nullable = false, length = 50)
	public String getMicNome() {
		return this.micNome;
	}
	
	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		NRO_ETIQUETA("nroEtiqueta"),
		DSM_SEQ("dsmSeq"),
		LDI_SEQ("ldiSeq"),
		MIC_NOME("micNome"),
		SERVIDOR("servidor");

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
