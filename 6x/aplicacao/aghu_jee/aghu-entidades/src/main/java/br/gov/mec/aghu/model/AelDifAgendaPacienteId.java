package br.gov.mec.aghu.model;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * Created by renan_boni on 03/03/15.
 */
@Embeddable
public class AelDifAgendaPacienteId implements EntityCompositeId {

    private static final long serialVersionUID = -5580880772156394860L;

    private Integer codigoPaciente;
    private Date dataHoraAgenda;
    private Short unidadeFuncionalSeq;
    private Integer seqp;

    @Column(name = "PAC_CODIGO", nullable = false)
    public Integer getCodigoPaciente() {
        return codigoPaciente;
    }

    public void setCodigoPaciente(Integer codigoPaciente) {
        this.codigoPaciente = codigoPaciente;
    }

    @Column(name = "HED_DTHR_AGENDA", nullable = false)
    public Date getDataHoraAgenda() {
        return dataHoraAgenda;
    }

    public void setDataHoraAgenda(Date dataHoraAgenda) {
        this.dataHoraAgenda = dataHoraAgenda;
    }

    @Column(name = "HED_GAE_UNF_SEQ", nullable = false)
    public Short getUnidadeFuncionalSeq() {
        return unidadeFuncionalSeq;
    }

    public void setUnidadeFuncionalSeq(Short unidadeFuncionalSeq) {
        this.unidadeFuncionalSeq = unidadeFuncionalSeq;
    }

    @Column(name = "HED_GAE_SEQP", nullable = false)
    public Integer getSeqp() {
        return seqp;
    }

    public void setSeqp(Integer seqp) {
        this.seqp = seqp;
    }
    
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("codigoPaciente", this.codigoPaciente)
				.append("dataHoraAgenda", this.dataHoraAgenda)
				.append("unidadeFuncionalSeq", this.unidadeFuncionalSeq)
				.append("seqp", this.seqp).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AelDifAgendaPacienteId)) {
			return false;
		}
		AelDifAgendaPacienteId castOther = (AelDifAgendaPacienteId) other;
		return new EqualsBuilder()
				.append(this.codigoPaciente, castOther.getCodigoPaciente())
				.append(this.dataHoraAgenda, castOther.getDataHoraAgenda())
				.append(this.unidadeFuncionalSeq,
						castOther.getUnidadeFuncionalSeq())
				.append(this.seqp, castOther.getSeqp()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigoPaciente)
				.append(this.dataHoraAgenda).append(this.unidadeFuncionalSeq)
				.append(this.seqp).toHashCode();
	}

}
