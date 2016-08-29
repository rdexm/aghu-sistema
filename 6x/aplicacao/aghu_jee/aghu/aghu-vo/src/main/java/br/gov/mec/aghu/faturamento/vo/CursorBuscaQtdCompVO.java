package br.gov.mec.aghu.faturamento.vo;


/**
 * VO utilizado no cursor c_busca_qtd_por_comp
 */
public class CursorBuscaQtdCompVO implements java.io.Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3392583219013303095L;
	private Integer anomes;
	private Short qtd;
	
	public Integer getAnomes() {
		return anomes;
	}
	public void setAnomes(Integer anomes) {
		this.anomes = anomes;
	}
	public Short getQtd() {
		return qtd;
	}
	public void setQtd(Short qtd) {
		this.qtd = qtd;
	}

	
	
}