package br.gov.mec.aghu.exames.patologia.vo;

import java.io.Serializable;
import java.util.List;

public class CidOTreeNodeVO  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 185665366737567567L;
	/**
	 * 
	 */
	private Object valor;
	private List<CidOTreeNodeVO> filhos;
	private boolean expandido = false;
	private boolean folha = false;
	private boolean raiz = false;
	private Long seqTopografiaGrupoCidOs;
	public boolean isRaiz() {
		return raiz;
	}

	public void setRaiz(boolean raiz) {
		this.raiz = raiz;
	}

	private String label;

	public boolean isExpandido() {
		return expandido;
	}

	public void setExpandido(boolean expandido) {
		this.expandido = expandido;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	public List<CidOTreeNodeVO> getFilhos() {
		return filhos;
	}

	public void setFilhos(List<CidOTreeNodeVO> filhos) {
		this.filhos = filhos;
	}

	public boolean isFolha() {
		return folha;
	}

	public void setFolha(boolean folha) {
		this.folha = folha;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Long getSeqTopografiaGrupoCidOs() {
		return seqTopografiaGrupoCidOs;
	}

	public void setSeqTopografiaGrupoCidOs(Long seqTopografiaGrupoCidOs) {
		this.seqTopografiaGrupoCidOs = seqTopografiaGrupoCidOs;
	}
	
}
