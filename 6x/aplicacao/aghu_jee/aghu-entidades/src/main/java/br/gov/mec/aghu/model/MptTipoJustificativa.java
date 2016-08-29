package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptJusSeq", sequenceName="AGH.MPT_JUS_SEQ", allocationSize = 1)
@Table(name = "MPT_TIPO_JUSTIFICATIVA", schema = "AGH")
public class MptTipoJustificativa extends BaseEntitySeq<Short> implements Serializable{


	private static final long serialVersionUID = 3065992973957490948L;

	private Short seq;
	private String descricao;
	private String sigla;
	
	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		SIGLA("sigla"),
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
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
        if (!(obj instanceof MptTipoJustificativa)) {
            return false;
        }
        MptTipoJustificativa other = (MptTipoJustificativa) obj;
        
        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getSeq(), other.getSeq());
        
        return umEqualsBuilder.isEquals();
    }
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptJusSeq")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable=false, length = 3)
	@Length(max = 3)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "SIGLA", nullable=false, length = 3)
	@Length(max = 3)
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
}
