package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;

public class ItemSolicitacaoExameAtendimentoVO implements Serializable {

    private static final long serialVersionUID = 8476578880208533255L;
    
    private Integer soeSeq;
    private Short seqp;
    private String ufeEmaExaSigla;
    private Integer ufeEmaManSeq;
    private DominioOrigemAtendimento origemAtendimento;
    private Integer atdSeq;
    private Short cspCnvCodigo;
    private Short cspSeq;
    
    public enum Fields {
          SOE_SEQ("soeSeq"), SEQP("seqp"), UFE_EMA_EXA_SIGLA("ufeEmaExaSigla"), UFE_EMA_MAN_SEQ("ufeEmaManSeq"),
          ORIGEM_ATENDIMENTO("origemAtendimento"), ATD_SEQ("atdSeq"), CSP_CNV_CODIGO("cspCnvCodigo"), CSP_SEQ("cspSeq");
          
          private String fields;

          private Fields(String fields) {
                 this.fields = fields;
          }

          @Override
          public String toString() {
                 return this.fields;
          }
    }
          
    public Integer getSoeSeq() {
          return soeSeq;
    }
    public void setSoeSeq(Integer soeSeq) {
          this.soeSeq = soeSeq;
    }
    public Short getSeqp() {
          return seqp;
    }
    public void setSeqp(Short seqp) {
          this.seqp = seqp;
    }
    public String getUfeEmaExaSigla() {
          return ufeEmaExaSigla;
    }
    public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
          this.ufeEmaExaSigla = ufeEmaExaSigla;
    }
    public Integer getUfeEmaManSeq() {
          return ufeEmaManSeq;
    }
    public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
          this.ufeEmaManSeq = ufeEmaManSeq;
    }
    public DominioOrigemAtendimento getOrigemAtendimento() {
          return origemAtendimento;
    }
    public void setOrigemAtendimento(DominioOrigemAtendimento origemAtendimento) {
          this.origemAtendimento = origemAtendimento;
    }
    public Integer getAtdSeq() {
          return atdSeq;
    }
    public void setAtdSeq(Integer atdSeq) {
          this.atdSeq = atdSeq;
    }
    public Short getCspCnvCodigo() {
          return cspCnvCodigo;
    }
    public void setCspCnvCodigo(Short cspCnvCodigo) {
          this.cspCnvCodigo = cspCnvCodigo;
    }
    public Short getCspSeq() {
          return cspSeq;
    }
    public void setCspSeq(Short cspSeq) {
          this.cspSeq = cspSeq;
    }
}