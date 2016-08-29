package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Immutable
@Table(name = "ael_doc_resultado_exames", schema = "hist")
public class AelDocResultadoExamesHist extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -3041490948024607356L;
	private Integer seq;
	private Integer version;
	private Date criadoEm;
	private AelItemSolicExameHist itemSolicitacaoExame;
//	private Blob documento;
	private byte[] documento;
//	private RapServidoresHist servidor;
	private Boolean indAnulacaoDoc;
	private Date dthrAnulacaoDoc;
//	private RapServidoresHist servidorAnulacao;

	public AelDocResultadoExamesHist() {
	}

	public AelDocResultadoExamesHist(Integer seq, Integer version, Date criadoEm,
			AelItemSolicExameHist itemSolicitacaoExame, byte[] documento,
			Boolean indAnulacaoDoc, Date dthrAnulacaoDoc) {
		super();
		this.seq = seq;
		this.version = version;
		this.criadoEm = criadoEm;
		this.itemSolicitacaoExame = itemSolicitacaoExame;
		this.documento = documento;
		this.indAnulacaoDoc = indAnulacaoDoc;
		this.dthrAnulacaoDoc = dthrAnulacaoDoc;
	}

	@Id
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ", nullable = false),
			@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP", nullable = false) })
	public AelItemSolicExameHist getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}
	
	public void setItemSolicitacaoExame(AelItemSolicExameHist itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}
	
	/*@Type(type="org.hibernate.type.BinaryType") 
	@Column(name = "DOCUMENTO", nullable = false)
	public Blob getDocumento() {
		return documento;
	}
	public void setDocumento(Blob documento) {
		this.documento = documento;
	}

	/*public Blob getDocumento() {
		return this.documento;
	}

	public void setDocumento(Blob documento) {
		this.documento = documento;
	}*/

	/**
	 * Retorna uma cópia do documento.
	 * 
	 * @return
	 */
	@Type(type="org.hibernate.type.BinaryType") 
	@Column(name = "DOCUMENTO", nullable = false)
	public byte[] getDocumento() {
		return ArrayUtils.clone(documento);
	}
	
	public void setDocumento(byte[] documento) {
		this.documento = documento;
	}

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumns( {
//			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
//			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
//	public RapServidoresHist getServidor() {
//		return servidor;
//	}
//
//	public void setServidor(RapServidoresHist servidor) {
//		this.servidor = servidor;
//	}

	@Column(name = "IND_ANULACAO_DOC", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAnulacaoDoc() {
		return this.indAnulacaoDoc;
	}

	public void setIndAnulacaoDoc(Boolean indAnulacaoDoc) {
		this.indAnulacaoDoc = indAnulacaoDoc;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ANULACAO_DOC")
	public Date getDthrAnulacaoDoc() {
		return this.dthrAnulacaoDoc;
	}

	public void setDthrAnulacaoDoc(Date dthrAnulacaoDoc) {
		this.dthrAnulacaoDoc = dthrAnulacaoDoc;
	}

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumns( {
//			@JoinColumn(name = "SER_MATRICULA_ANULACAO", referencedColumnName = "MATRICULA"),
//			@JoinColumn(name = "SER_VIN_CODIGO_ANULACAO", referencedColumnName = "VIN_CODIGO") })
//	public RapServidoresHist getServidorAnulacao() {
//		return servidorAnulacao;
//	}
//	
//	public void setServidorAnulacao(RapServidoresHist servidorAnulacao) {
//		this.servidorAnulacao = servidorAnulacao;
//	}
	
	public enum Fields {
		ID("seq"),
		ISE_SOE_SEQ("itemSolicitacaoExame.id.soeSeq"),
		ISE_SEQP("itemSolicitacaoExame.id.seqp"),
		CRIADO_EM("criadoEm"),
		DOCUMENTO("documento"),
		IND_ANULACAO_DOC("indAnulacaoDoc"),
		DATA_HORA_ANULACAO_DOC("dthrAnulacaoDoc");

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
		if (!(obj instanceof AelDocResultadoExame)) {
			return false;
		}
		AelDocResultadoExame other = (AelDocResultadoExame) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

}
