package br.gov.mec.aghu.estoque.vo;

/**
 * Classe responsável pela totalização de valores de ajuste,
 * para cada motivo
 * @author clayton.bras
 *
 */
public 	class TotalizadorMotivoAjusteVO{
	
	private String descricaoMotivoAjuste;
	
	private Double valorTotalMotivoAjuste;
	
	public TotalizadorMotivoAjusteVO(){
		
	}
	
	public TotalizadorMotivoAjusteVO(String descricaoMotivoAjuste, Double valorMotivoAjuste) {
		super();
		this.descricaoMotivoAjuste = descricaoMotivoAjuste;
		this.valorTotalMotivoAjuste = valorMotivoAjuste;
	}
	
	public String getDescricaoMotivoAjuste() {
		return descricaoMotivoAjuste;
	}
	
	public void setDescricaoMotivoAjuste(String descricaoMotivoAjuste) {
		this.descricaoMotivoAjuste = descricaoMotivoAjuste;
	}
	
	public Double getValorTotalMotivoAjuste() {
		return valorTotalMotivoAjuste;
	}
	
	public void setValorTotalMotivoAjuste(Double valorTotalMotivoAjuste) {
		this.valorTotalMotivoAjuste = valorTotalMotivoAjuste;
	}

	public void contabilizarMotivoAjuste(Double valorMotivoAjuste) {
		this.valorTotalMotivoAjuste += valorMotivoAjuste;
	}
}
