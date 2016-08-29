package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by renanribeiro on 02/03/15.
 */
public class ExameAgendamentoMesmoHorarioVO implements Serializable {

    private static final long serialVersionUID = 7483266744414543172L;

    private Integer hedGaeSeqp;

    private Date hedDthrAgenda;

    private Integer pacienteCod;

    private Short unfSeq;

    public ExameAgendamentoMesmoHorarioVO(Integer hedGaeSeqp, Date hedDthrAgenda) {
        this.hedGaeSeqp = hedGaeSeqp;
        this.hedDthrAgenda = hedDthrAgenda;
    }

    public ExameAgendamentoMesmoHorarioVO(Integer hedGaeSeqp, Date hedDthrAgenda, Integer pacienteCod, Short unfSeq) {
        this.hedGaeSeqp = hedGaeSeqp;
        this.hedDthrAgenda = hedDthrAgenda;
        this.pacienteCod = pacienteCod;
        this.unfSeq = unfSeq;
    }

    public Integer getHedGaeSeqp() {
        return hedGaeSeqp;
    }

    public void setHedGaeSeqp(Integer hedGaeSeqp) {
        this.hedGaeSeqp = hedGaeSeqp;
    }

    public Date getHedDthrAgenda() {
        return hedDthrAgenda;
    }

    public void setHedDthrAgenda(Date hedDthrAgenda) {
        this.hedDthrAgenda = hedDthrAgenda;
    }

    public Integer getPacienteCod() {
        return pacienteCod;
    }

    public void setPacienteCod(Integer pacienteCod) {
        this.pacienteCod = pacienteCod;
    }

    public Short getUnfSeq() {
        return unfSeq;
    }

    public void setUnfSeq(Short unfSeq) {
        this.unfSeq = unfSeq;
    }
}
