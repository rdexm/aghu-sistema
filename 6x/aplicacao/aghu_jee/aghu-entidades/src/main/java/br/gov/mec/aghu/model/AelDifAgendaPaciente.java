package br.gov.mec.aghu.model;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

import javax.persistence.*;

/**
 * Created by renanribeiro on 03/03/15.
 */
@Entity
@Table(name = "V_AEL_DIF_AGEND_PAC", schema = "AGH")
public class AelDifAgendaPaciente extends BaseEntityId<AelDifAgendaPacienteId> implements java.io.Serializable {

    private static final long serialVersionUID = -6363673119279029368L;

    private AelDifAgendaPacienteId id;

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "codigoPaciente", column = @Column(name = "PAC_CODIGO", nullable = false)),
            @AttributeOverride(name = "dataHoraAgenda", column = @Column(name = "HED_DTHR_AGENDA", nullable = false)),
            @AttributeOverride(name = "unidadeFuncionalSeq", column = @Column(name = "HED_GAE_UNF_SEQ", nullable = false)),
            @AttributeOverride(name = "seqp", column = @Column(name = "HED_GAE_SEQP", nullable = false))})
    public AelDifAgendaPacienteId getId() {
        return id;
    }

    public void setId(AelDifAgendaPacienteId id) {
        this.id = id;
    }
}
