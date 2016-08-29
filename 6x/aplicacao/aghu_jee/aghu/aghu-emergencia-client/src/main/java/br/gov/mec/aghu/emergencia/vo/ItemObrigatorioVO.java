package br.gov.mec.aghu.emergencia.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Representa os itens obrigatorios
 * 
 * @author luismoura
 * 
 */
public class ItemObrigatorioVO implements BaseBean, Comparable<ItemObrigatorioVO> {
	private static final long serialVersionUID = -3109355351836808829L;

	private Integer seqObr;
	private Boolean indSituacaoObr;

	private Integer seqItem;
	private String descricao;
	private String sigla;

	public ItemObrigatorioVO() {
	}

	public ItemObrigatorioVO(Integer seqItem) {
		this.seqItem = seqItem;
	}

	public ItemObrigatorioVO(Integer seqObr, Boolean indSituacaoObr) {
		this.seqObr = seqObr;
		this.indSituacaoObr = indSituacaoObr;
	}

	public Integer getSeqObr() {
		return seqObr;
	}

	public void setSeqObr(Integer seqObr) {
		this.seqObr = seqObr;
	}

	public Boolean getIndSituacaoObr() {
		return indSituacaoObr;
	}

	public void setIndSituacaoObr(Boolean indSituacaoObr) {
		this.indSituacaoObr = indSituacaoObr;
	}

	public Integer getSeqItem() {
		return seqItem;
	}

	public void setSeqItem(Integer seqItem) {
		this.seqItem = seqItem;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seqItem == null) ? 0 : seqItem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ItemObrigatorioVO other = (ItemObrigatorioVO) obj;
		if (seqItem == null) {
			if (other.seqItem != null) {
				return false;
			}
		} else if (!seqItem.equals(other.seqItem)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(ItemObrigatorioVO other) {
		int retorno = this.descricao.compareTo(other.descricao);
		if (retorno == 0) {
			retorno = this.seqItem.compareTo(other.seqItem);
		}
		return retorno;
	}

}
