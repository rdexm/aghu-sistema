package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MBC_PROC_DESCRICOES_JN", schema = "AGH")
@SequenceGenerator(name="mbcPodJnSeq", sequenceName="AGH.MBC_POD_JN_SEQ", allocationSize = 1)
@Immutable
public class MbcProcDescricaoJn extends BaseJournal {

	private static final long serialVersionUID = -8999002293104524150L;
	private Integer dcgCrgSeq;
	private Short dcgSeqp;
	private Integer seqp;
	private DominioIndContaminacao indContaminacao;
	private String complemento;
	private Integer pciSeq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public MbcProcDescricaoJn() {
	}

	public MbcProcDescricaoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer dcgCrgSeq, Short dcgSeqp, Integer seqp) {
		this.dcgCrgSeq = dcgCrgSeq;
		this.dcgSeqp = dcgSeqp;
		this.seqp = seqp;
	}

	public MbcProcDescricaoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer dcgCrgSeq, Short dcgSeqp, Integer seqp,
			DominioIndContaminacao indContaminacao, String complemento, Integer pciSeq, Date criadoEm, Integer serMatricula, Short serVinCodigo) {
		this.dcgCrgSeq = dcgCrgSeq;
		this.dcgSeqp = dcgSeqp;
		this.seqp = seqp;
		this.indContaminacao = indContaminacao;
		this.complemento = complemento;
		this.pciSeq = pciSeq;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcPodJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "DCG_CRG_SEQ", nullable = false)
	public Integer getDcgCrgSeq() {
		return this.dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	@Column(name = "DCG_SEQP", nullable = false)
	public Short getDcgSeqp() {
		return this.dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}

	@Column(name = "SEQP", nullable = false)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	@Column(name = "IND_CONTAMINACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndContaminacao getIndContaminacao() {
		return this.indContaminacao;
	}

	public void setIndContaminacao(DominioIndContaminacao indContaminacao) {
		this.indContaminacao = indContaminacao;
	}

	@Column(name = "COMPLEMENTO", length = 500)
	@Length(max = 500)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name = "PCI_SEQ")
	public Integer getPciSeq() {
		return this.pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public enum Fields {

		DCG_CRG_SEQ("dcgCrgSeq"),
		DCG_SEQP("dcgSeqp"),
		SEQP("seqp"),
		IND_CONTAMINACAO("indContaminacao"),
		COMPLEMENTO("complemento"),
		PCI_SEQ("pciSeq"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

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
