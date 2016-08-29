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
@SequenceGenerator(name="mamTplSq1", sequenceName="AGH.MAM_TPL_SQ1", allocationSize = 1)
@Table(name = "MAM_TMP_PA_SISTOLICAS", schema = "AGH")
public class MamTmpPaSistolicas extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 766809454179817533L;
	private Long seq;
	private Long chave;
	private Integer qusQutSeq;
	private Short qusSeqp;
	private Short seqp;
	private String tipo;
	private Short sistolica;
	private String indRecuperado;
	private Date criadoEm;
	private Integer pacCodigo;

	public MamTmpPaSistolicas() {
	}

	public MamTmpPaSistolicas(Long seq, String tipo, Short sistolica,
			String indRecuperado, Date criadoEm, Integer pacCodigo) {
		this.seq = seq;
		this.tipo = tipo;
		this.sistolica = sistolica;
		this.indRecuperado = indRecuperado;
		this.criadoEm = criadoEm;
		this.pacCodigo = pacCodigo;
	}

	public MamTmpPaSistolicas(Long seq, Long chave, Integer qusQutSeq,
			Short qusSeqp, Short seqp, String tipo, Short sistolica,
			String indRecuperado, Date criadoEm, Integer pacCodigo) {
		this.seq = seq;
		this.chave = chave;
		this.qusQutSeq = qusQutSeq;
		this.qusSeqp = qusSeqp;
		this.seqp = seqp;
		this.tipo = tipo;
		this.sistolica = sistolica;
		this.indRecuperado = indRecuperado;
		this.criadoEm = criadoEm;
		this.pacCodigo = pacCodigo;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamTplSq1")
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

	@Column(name = "SISTOLICA", nullable = false, precision = 3, scale = 0)
	public Short getSistolica() {
		return this.sistolica;
	}

	public void setSistolica(Short sistolica) {
		this.sistolica = sistolica;
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
		TIPO("tipo"),
		QUS_QUT_SEQ("qusQutSeq"),
		QUS_SEQP("qusSeqp"),
		SEQP("seqp"),
		IND_RECUPERADO("indRecuperado");

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
		if (!(obj instanceof MamTmpPaSistolicas)) {
			return false;
		}
		MamTmpPaSistolicas other = (MamTmpPaSistolicas) obj;
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
