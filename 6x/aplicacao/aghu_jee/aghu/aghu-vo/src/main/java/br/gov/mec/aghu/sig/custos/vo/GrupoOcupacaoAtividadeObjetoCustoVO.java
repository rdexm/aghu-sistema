package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;

public class GrupoOcupacaoAtividadeObjetoCustoVO {
	
	private Integer tvdSeq;
    private Integer aisSeq;
    private Integer cmtSeq;
    private Integer cctCodigo;
    private Integer gocSeq;
    private Integer qtdeProfissionais;
    private BigDecimal qtdeTempo;
    private Integer dirSeqTempo;
    private Integer dirSeqAtividade;
    private Integer nroExecucoes;
    private Date procDataInicio;
    private Date procDataFim;
    private Long nroDiasProducao; 
    private BigDecimal qtde;
    
    
    public GrupoOcupacaoAtividadeObjetoCustoVO() {
	}
	
	public GrupoOcupacaoAtividadeObjetoCustoVO(Object[] obj, DominioTipoObjetoCusto tipoObjetoCusto) {
		
		if (obj[0] != null) {
			this.setTvdSeq((Integer) obj[0]);
		}
		
		if (obj[1] != null) {
			this.setAisSeq((Integer) obj[1]);	
		}
		
		if (obj[2] != null) {
			this.setCmtSeq((Integer) obj[2]);
		}
		
		if (obj[3] != null) {
			this.setCctCodigo((Integer) obj[3]);
		}
		
		if (obj[4] != null) {
			this.setGocSeq((Integer) obj[4]);
		}
		
		if (obj[5] != null) {
			this.setQtdeProfissionais((Integer) obj[5]);
		}
		
		if (obj[6] != null) {
			this.setQtdeTempo((BigDecimal) obj[6]);
		}
		
		if (obj[7] != null) {
			this.setDirSeqTempo((Integer) obj[7]);
		}
		
		if (obj[8] != null) {
			this.setDirSeqAtividade((Integer) obj[8]);		
		}
		
		if (obj[9] != null) {
			this.setNroExecucoes((Integer) obj[9]);
		}
		
		if (obj[10] != null) {
			this.setProcDataInicio((Date) obj[10]);
		}
		
		if (obj[11] != null) {
			this.setProcDataFim((Date) obj[11]);
		} 
		
		if(tipoObjetoCusto == DominioTipoObjetoCusto.AS ){
			
			if (obj[12] != null) {
				this.setNroDiasProducao((Long) obj[12]);		
			}
			
			if (obj[13] != null) {
				this.setQtde((BigDecimal) obj[13]);
			}
		}
	}
    
	public Integer getTvdSeq() {
		return tvdSeq;
	}
	public void setTvdSeq(Integer tvdSeq) {
		this.tvdSeq = tvdSeq;
	}
	public Integer getAisSeq() {
		return aisSeq;
	}
	public void setAisSeq(Integer aisSeq) {
		this.aisSeq = aisSeq;
	}
	public Integer getCmtSeq() {
		return cmtSeq;
	}
	public void setCmtSeq(Integer cmtSeq) {
		this.cmtSeq = cmtSeq;
	}
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	public Integer getGocSeq() {
		return gocSeq;
	}
	public void setGocSeq(Integer gocSeq) {
		this.gocSeq = gocSeq;
	}
	public Integer getQtdeProfissionais() {
		return qtdeProfissionais;
	}
	public void setQtdeProfissionais(Integer qtdeProfissionais) {
		this.qtdeProfissionais = qtdeProfissionais;
	}
	public BigDecimal getQtdeTempo() {
		return qtdeTempo;
	}
	public void setQtdeTempo(BigDecimal qtdeTempo) {
		this.qtdeTempo = qtdeTempo;
	}
	public Integer getDirSeqTempo() {
		return dirSeqTempo;
	}
	public void setDirSeqTempo(Integer dirSeqTempo) {
		this.dirSeqTempo = dirSeqTempo;
	}
	public Integer getDirSeqAtividade() {
		return dirSeqAtividade;
	}
	public void setDirSeqAtividade(Integer dirSeqAtividade) {
		this.dirSeqAtividade = dirSeqAtividade;
	}
	public Integer getNroExecucoes() {
		return nroExecucoes;
	}
	public void setNroExecucoes(Integer nroExecucoes) {
		this.nroExecucoes = nroExecucoes;
	}
	public Date getProcDataInicio() {
		return procDataInicio;
	}
	public void setProcDataInicio(Date procDataInicio) {
		this.procDataInicio = procDataInicio;
	}
	public Date getProcDataFim() {
		return procDataFim;
	}
	public void setProcDataFim(Date procDataFim) {
		this.procDataFim = procDataFim;
	}
	public Long getNroDiasProducao() {
		return nroDiasProducao;
	}
	public void setNroDiasProducao(Long nroDiasProducao) {
		this.nroDiasProducao = nroDiasProducao;
	}
	public BigDecimal getQtde() {
		return qtde;
	}
	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}    
}
