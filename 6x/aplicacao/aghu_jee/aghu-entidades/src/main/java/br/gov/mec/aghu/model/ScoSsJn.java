package br.gov.mec.aghu.model;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;


import br.gov.mec.aghu.core.model.BaseJournal;




/**
 * ScoSsJn 
 */

@Entity
@Table(name = "SCO_SS_JN", schema = "AGH")
@SequenceGenerator(name = "scoSlsJnSeq", sequenceName = "AGH.SCO_SLS_JN_SEQ", allocationSize = 1)
public class ScoSsJn extends BaseJournal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4793359987143264041L;
	
	//private Long seqJn;
	//private String jnUser;
	//private Date jnDateTime;
	//private String jnOperation;
	private Integer numero;
	private Short ppsCodigo;
	private Integer serMatriculaAutorizada;
	private Short serVinCodigoAutorizada;
	private Boolean indDevolucao;
	private String etapa;

	public ScoSsJn() {
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoSlsJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	/*public ScoSsJn(Long seqJn, String jnUser, Date jnDateTime,
			String jnOperation, Integer serMatricula, Short serVinCodigo) {
		this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Long getSeqJn() {
		return this.seqJn;
	}

	public void setSeqJn(Long seqJn) {
		this.seqJn = seqJn;
	}

	@Column(name = "JN_USER", nullable = false, length = 30)
	public String getJnUser() {
		return this.jnUser;
	}

	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME", nullable = false, length = 29)
	public Date getJnDateTime() {
		return this.jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}

	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	public String getJnOperation() {
		return this.jnOperation;
	}

	public void setJnOperation(String jnOperation) {
		this.jnOperation = jnOperation;
	}*/

	@Column(name = "SER_MATRICULA_AUTORIZADA")
	public Integer getSerMatriculaAutorizada() {
		return this.serMatriculaAutorizada;
	}

	public void setSerMatriculaAutorizada(Integer serMatriculaAutorizada) {
		this.serMatriculaAutorizada = serMatriculaAutorizada;
	}

	@Column(name = "SER_VIN_CODIGO_AUTORIZADA")
	public Short getSerVinCodigoAutorizada() {
		return this.serVinCodigoAutorizada;
	}

	public void setSerVinCodigoAutorizada(Short serVinCodigoAutorizada) {
		this.serVinCodigoAutorizada = serVinCodigoAutorizada;
	}

	@Column(name = "NUMERO", nullable=false)
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name = "PPS_CODIGO")
	public Short getPpsCodigo() {
		return ppsCodigo;
	}

	public void setPpsCodigo(Short ppsCodigo) {
		this.ppsCodigo = ppsCodigo;
	}

	@Column(name = "IND_DEVOLUCAO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDevolucao() {
		return indDevolucao;
	}

	public void setIndDevolucao(Boolean indDevolucao) {
		this.indDevolucao = indDevolucao;
		
	}
	
	@Transient
	public String getEtapa() {
		if ("Atual".equals(etapa)){
		   return etapa;
		}
		else {
			return "Anterior";
		}
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}
	
	public enum Fields {
		SEQ_JN("seqJn"),
		NUMERO("numero"),
		PPS_CODIGO("ppsCodigo"),
		SER_MATRICULA_AUTORIZADA("serMatriculaAutorizada"),
		SER_VIN_CODIGO_AUTORIZADA("serVinCodigoAutorizada"),
		IND_DEVOLUCAO("indDevolucao");

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
