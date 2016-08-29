package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mamTpiSq1", sequenceName="AGH.MAM_TPI_SQ1", allocationSize = 1)
@Table(name = "MAM_TMP_PA_DIASTOLICAS", schema = "AGH")
public class MamTmpPaDiastolicas extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 749444807016242332L;
	private Long seq;
	private Long chave;
	private Integer qusQutSeq;
	private Short qusSeqp;
	private Short seqp;
	private String tipo;
	private Short diastolica;
	private String indRecuperado;
	private Date criadoEm;
	private Integer pacCodigo;

	public MamTmpPaDiastolicas() {
	}

	public MamTmpPaDiastolicas(Long seq, String tipo, Short diastolica,
			String indRecuperado, Date criadoEm, Integer pacCodigo) {
		this.seq = seq;
		this.tipo = tipo;
		this.diastolica = diastolica;
		this.indRecuperado = indRecuperado;
		this.criadoEm = criadoEm;
		this.pacCodigo = pacCodigo;
	}

	public MamTmpPaDiastolicas(Long seq, Long chave, Integer qusQutSeq,
			Short qusSeqp, Short seqp, String tipo, Short diastolica,
			String indRecuperado, Date criadoEm, Integer pacCodigo) {
		this.seq = seq;
		this.chave = chave;
		this.qusQutSeq = qusQutSeq;
		this.qusSeqp = qusSeqp;
		this.seqp = seqp;
		this.tipo = tipo;
		this.diastolica = diastolica;
		this.indRecuperado = indRecuperado;
		this.criadoEm = criadoEm;
		this.pacCodigo = pacCodigo;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamTpiSq1")
	@Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "CHAVE", precision = 14, scale = 0)
	public Long getChave() {
		return this.chave;
	}

	public void setChave(Long chave) {
		this.chave = chave;
	}

	@Column(name = "QUS_QUT_SEQ", precision = 6, scale = 0)
	public Integer getQusQutSeq() {
		return this.qusQutSeq;
	}

	public void setQusQutSeq(Integer qusQutSeq) {
		this.qusQutSeq = qusQutSeq;
	}

	@Column(name = "QUS_SEQP", precision = 3, scale = 0)
	public Short getQusSeqp() {
		return this.qusSeqp;
	}

	public void setQusSeqp(Short qusSeqp) {
		this.qusSeqp = qusSeqp;
	}

	@Column(name = "SEQP", precision = 3, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Column(name = "TIPO", nullable = false, length = 1)
	@Length(max = 1)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Column(name = "DIASTOLICA", nullable = false, precision = 3, scale = 0)
	public Short getDiastolica() {
		return this.diastolica;
	}

	public void setDiastolica(Short diastolica) {
		this.diastolica = diastolica;
	}

	@Column(name = "IND_RECUPERADO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndRecuperado() {
		return this.indRecuperado;
	}

	public void setIndRecuperado(String indRecuperado) {
		this.indRecuperado = indRecuperado;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	public enum Fields {
		PAC_CODIGO("pacCodigo"),
		CHAVE("chave"),
		IND_RECUPERADO("indRecuperado"),
		QUS_QUT_SEQ("qusqutseq"),
		QUS_SEQP("qusSeqp"),
		SEQP("seqp"),
		TIPO("tipo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof MamTmpPaDiastolicas)) {
			return false;
		}
		MamTmpPaDiastolicas other = (MamTmpPaDiastolicas) obj;
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
