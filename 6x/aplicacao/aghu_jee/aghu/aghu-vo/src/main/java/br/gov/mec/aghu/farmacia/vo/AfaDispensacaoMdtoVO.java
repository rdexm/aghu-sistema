package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioTipoOperacaoConversao;

public class AfaDispensacaoMdtoVO implements Serializable {

	//#####################################################################
	//foi necessario nao utilizar camelCase nos nomes dos atributos
	//pois este VO eh usado em uma query nativa, que nao funcionou sem isso
	//#####################################################################

	/**
	 * 
	 */
	private static final long serialVersionUID = -8848832265337998746L;
	//private Date criadoem;
	private Integer medmatcod;
	private Integer phiseq;
	private BigDecimal fatorconversao;
	private DominioTipoOperacaoConversao tipooperconversao;
	private BigDecimal quantidade;

	public AfaDispensacaoMdtoVO() {

	}
	
	

	public AfaDispensacaoMdtoVO(Integer medmatcod, Integer phiseq,
			BigDecimal fatorconversao, DominioTipoOperacaoConversao tipooperconversao,
			BigDecimal quantidade) {
		this.medmatcod = medmatcod;
		this.phiseq = phiseq;
		this.fatorconversao = fatorconversao;
		this.tipooperconversao = tipooperconversao;
		this.quantidade = quantidade;
	}




	public Integer getMedmatcod() {
		return medmatcod;
	}



	public void setMedmatcod(Integer medmatcod) {
		this.medmatcod = medmatcod;
	}



	public Integer getPhiseq() {
		return phiseq;
	}



	public void setPhiseq(Integer phiseq) {
		this.phiseq = phiseq;
	}



	public BigDecimal getFatorconversao() {
		return fatorconversao;
	}



	public void setFatorconversao(BigDecimal fatorconversao) {
		this.fatorconversao = fatorconversao;
	}



	public DominioTipoOperacaoConversao getTipooperconversao() {
		return tipooperconversao;
	}



	public void setTipooperconversao(DominioTipoOperacaoConversao tipooperconversao) {
		this.tipooperconversao = tipooperconversao;
	}



	public BigDecimal getQuantidade() {
		return quantidade;
	}



	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}



	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fatorconversao == null) ? 0 : fatorconversao.hashCode());
		result = prime * result
				+ ((medmatcod == null) ? 0 : medmatcod.hashCode());
		result = prime * result + ((phiseq == null) ? 0 : phiseq.hashCode());
		result = prime * result
				+ ((quantidade == null) ? 0 : quantidade.hashCode());
		result = prime
				* result
				+ ((tipooperconversao == null) ? 0 : tipooperconversao
						.hashCode());
		return result;
	}



	@Override
	@SuppressWarnings("PMD.NPathComplexity")
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
		AfaDispensacaoMdtoVO other = (AfaDispensacaoMdtoVO) obj;
		if (fatorconversao == null) {
			if (other.fatorconversao != null) {
				return false;
			}
		} else if (!fatorconversao.equals(other.fatorconversao)) {
			return false;
		}
		if (medmatcod == null) {
			if (other.medmatcod != null) {
				return false;
			}
		} else if (!medmatcod.equals(other.medmatcod)) {
			return false;
		}
		if (phiseq == null) {
			if (other.phiseq != null) {
				return false;
			}
		} else if (!phiseq.equals(other.phiseq)) {
			return false;
		}
		if (quantidade == null) {
			if (other.quantidade != null) {
				return false;
			}
		} else if (!quantidade.equals(other.quantidade)) {
			return false;
		}
		if (tipooperconversao == null) {
			if (other.tipooperconversao != null) {
				return false;
			}
		} else if (!tipooperconversao.equals(other.tipooperconversao)) {
			return false;
		}
		return true;
	}



	/*public Date getCriadoem() {
		return criadoem;
	}



	public void setCriadoem(Date criadoem) {
		this.criadoem = criadoem;
	}*/



	/**
	 * FIELDS
	 * 
	 */
	public enum Fields {
		MED_MAT_COD("medmatcod"),
		PHI_SEQ("phiseq"),
		TIPO_OPER_CONVERSAO("tipooperconversao"),
		FATOR_CONVERSAO("fatorconversao"),
		QUANTIDADE("quantidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
