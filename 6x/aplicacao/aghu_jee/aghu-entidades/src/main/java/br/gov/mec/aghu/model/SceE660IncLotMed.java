package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "SCE_E660INC_LOTMED")
@SequenceGenerator(allocationSize = 1, name = "sequence", sequenceName = "SCE_EIL_SQ1")
public class SceE660IncLotMed extends BaseEntitySeq<Long> implements java.io.Serializable, Cloneable {

	
	private static final long serialVersionUID = -3618690978156458069L;
	
	private Long seq;
	private Long eifSeqInc;
	private String numLote;
	private Double qtdLote;
	private Date dtFabricacao;
	private Date dtValidade;
	private SceE660IncForn sceE660IncForn;
	

	public SceE660IncLotMed() {
		
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
	@Column(name = "SEQ", nullable = false)
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}


	@Column(name = "EIF_SEQ_INC", nullable = false, insertable=false, updatable=false)
	public Long getEifSeqInc() {
		return eifSeqInc;
	}

	public void setEifSeqInc(Long eifSeqInc) {
		this.eifSeqInc = eifSeqInc;
	}

	@Column(name = "NUM_LOTE", length = 20, nullable = false)
	public String getNumLote() {
		return numLote;
	}

	public void setNumLote(String numLote) {
		this.numLote = numLote;
	}

	@Column(name = "QTD_LOTE", precision = 11, scale = 3)
	public Double getQtdLote() {
		return qtdLote;
	}

	public void setQtdLote(Double qtdLote) {
		this.qtdLote = qtdLote;
	}

	@Column(name = "DT_FABRICACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtFabricacao() {
		return dtFabricacao;
	}

	public void setDtFabricacao(Date dtFabricacao) {
		this.dtFabricacao = dtFabricacao;
	}

	@Column(name = "DT_VALIDADE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtValidade() {
		return dtValidade;
	}

	public void setDtValidade(Date dtValidade) {
		this.dtValidade = dtValidade;
	}
	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="EIF_SEQ_INC", referencedColumnName="SEQ_INC")
	public SceE660IncForn getSceE660IncForn() {
		return sceE660IncForn;
	}

	public void setSceE660IncForn(SceE660IncForn sceE660IncForn) {
		this.sceE660IncForn = sceE660IncForn;
	}
	
	// outros
	


	@Override
    public int hashCode() {
        HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getSeq());
        return umHashCodeBuilder.toHashCode();
    }
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SceE660IncLotMed)) {
            return false;
        }
        SceE660IncLotMed other = (SceE660IncLotMed) obj;
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getSeq(), other.getSeq());
        return umEqualsBuilder.isEquals();
    }
	
	public enum Fields {
		EIF_SEQ_INC("eifSeqInc"),
		EIF_SEQ_INC2("sceE660IncForn"),
		QUANTIDADE_LOTE("qtdLote"),
		DATA_VALIDADE("dtValidade"),
		NUMERO_LOTE("numLote");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}	
	}
}
