package br.gov.mec.aghu.perinatologia.vo;

import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Criado para poder lidar com lista de {@link McoNascIndicacoes} em memória.
 * 
 * @author luismoura
 * 
 */
public class IndicacaoPartoVO implements BaseBean {
	private static final long serialVersionUID = 325158661890426884L;

	private Integer id;
	private McoNascIndicacoes mcoNascIndicacoes;
	
	public IndicacaoPartoVO() {

	}

	public IndicacaoPartoVO(Integer id, McoNascIndicacoes mcoNascIndicacoes) {
		this.id = id;
		this.mcoNascIndicacoes = mcoNascIndicacoes;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public McoNascIndicacoes getMcoNascIndicacoes() {
		return mcoNascIndicacoes;
	}

	public void setMcoNascIndicacoes(McoNascIndicacoes mcoNascIndicacoes) {
		this.mcoNascIndicacoes = mcoNascIndicacoes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		IndicacaoPartoVO other = (IndicacaoPartoVO) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
