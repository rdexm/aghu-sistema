package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class GrupoOcupacaoAlocadoAtividadeVO implements java.io.Serializable{

	private static final long serialVersionUID = -4874639802322882087L;

	private Integer seqAtividade;
	private Integer seqAtividadePessoa;
	private Integer seqCalculoComponente;
	private Integer seqCalculoAtividadePessoa;
	private Integer seqCentroCusto;
	private Integer seqGrupoOcupacao;
	private Integer seqDirecionador;
	private BigDecimal somaPesoOC;
	
	public GrupoOcupacaoAlocadoAtividadeVO(){
		
	}
	
	public GrupoOcupacaoAlocadoAtividadeVO(Object[] obj){
		
		if(obj[0] != null){
			this.setSeqAtividade((Integer)obj[0]);
		}
		if(obj[1] != null){
			this.setSeqAtividadePessoa((Integer)obj[1]);
		}
		if(obj[2] != null){
			this.setSeqCalculoComponente((Integer)obj[2]);
		}
		if(obj[3] != null){
			this.setSeqCalculoAtividadePessoa((Integer)obj[3]);
		}
		if(obj[4] != null){
			this.setSeqCentroCusto((Integer)obj[4]);
		}
		if(obj[5] != null){
			this.setSeqGrupoOcupacao((Integer)obj[5]);
		}
		if(obj[6] != null){
			this.setSeqDirecionador((Integer)obj[6]);
		}
		if(obj[7] != null){
			this.setSomaPesoOC((BigDecimal)obj[7]);
		}
	}

	public Integer getSeqAtividade() {
		return seqAtividade;
	}

	public void setSeqAtividade(Integer seqAtividade) {
		this.seqAtividade = seqAtividade;
	}

	public Integer getSeqAtividadePessoa() {
		return seqAtividadePessoa;
	}

	public void setSeqAtividadePessoa(Integer seqAtividadePessoa) {
		this.seqAtividadePessoa = seqAtividadePessoa;
	}

	public Integer getSeqCalculoComponente() {
		return seqCalculoComponente;
	}

	public void setSeqCalculoComponente(Integer seqCalculoComponente) {
		this.seqCalculoComponente = seqCalculoComponente;
	}

	public Integer getSeqCalculoAtividadePessoa() {
		return seqCalculoAtividadePessoa;
	}

	public void setSeqCalculoAtividadePessoa(Integer seqCalculoAtividadePessoa) {
		this.seqCalculoAtividadePessoa = seqCalculoAtividadePessoa;
	}

	public Integer getSeqCentroCusto() {
		return seqCentroCusto;
	}

	public void setSeqCentroCusto(Integer seqCentroCusto) {
		this.seqCentroCusto = seqCentroCusto;
	}

	public Integer getSeqGrupoOcupacao() {
		return seqGrupoOcupacao;
	}

	public void setSeqGrupoOcupacao(Integer seqGrupoOcupacao) {
		this.seqGrupoOcupacao = seqGrupoOcupacao;
	}

	public Integer getSeqDirecionador() {
		return seqDirecionador;
	}

	public void setSeqDirecionador(Integer seqDirecionador) {
		this.seqDirecionador = seqDirecionador;
	}

	public BigDecimal getSomaPesoOC() {
		return somaPesoOC;
	}

	public void setSomaPesoOC(BigDecimal somaPesoOC) {
		this.somaPesoOC = somaPesoOC;
	}


}
