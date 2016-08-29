package br.gov.mec.aghu.sig.custos.vo;


public class ItensDePreparoVO extends SigProcessamentoContagemVO{
	
	private SigVO vo;
	private SigObjetoCustoPhisVO objetoCustoPhis;
	
	public SigVO getVo() {
		return vo;
	}
	public void setVo(SigVO vo) {
		this.vo = vo;
	}
	public SigObjetoCustoPhisVO getObjetoCustoPhis() {
		return objetoCustoPhis;
	}
	public void setObjetoCustoPhis(SigObjetoCustoPhisVO objetoCustoPhis) {
		this.objetoCustoPhis = objetoCustoPhis;
	}
}
