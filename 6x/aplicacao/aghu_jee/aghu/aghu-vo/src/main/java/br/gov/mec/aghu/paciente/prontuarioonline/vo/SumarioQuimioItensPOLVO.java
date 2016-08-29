package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoPrescricaoQuimioterapia;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class SumarioQuimioItensPOLVO  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7629433709683809562L;
	
	// atributos diretos do objeto
	private Date diqData;
	//
	private DominioTipoPrescricaoQuimioterapia itqTipo;
	private Integer itqTipoInteger;
	//
	private Integer itqSeq;
	private String itqDescricao;
	private String diqValor;
	private Integer diqApaAtdSeq;
	private Integer diqApaSeq;
	private Integer diqseqp;;
	// atributos processados 
	private Short diqDataN;
	private String descrTipo;
	private String ordem;
	private String valorOrderBy;
	
	private String diqDataFormatada;
	
	public enum Fields {
		DIQ_DATA("diqData"),
		ITQ_TIPO_INTEGER("itqTipoInteger"),
		ITQ_SEQ("itqSeq"),
		ITQ_DESCRICAO("itqDescricao"),
		DIQ_VALOR("diqValor"),
		DIQ_APA_ATD_SEQ("diqApaAtdSeq"),
		DIQ_APA_SEQ("diqApaSeq"),
		DIQ_SEQP("diqseqp"), 
		ORDEM("ordem"),
		VALOR_ORDER_BY("valorOrderBy"),
		DIQ_DATA_FORMATADA("diqDataFormatada"),;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Date getDiqData() {
		return diqData;
	}

	public void setDiqData(Date diqData) {
		this.diqData = diqData;
	}

	public DominioTipoPrescricaoQuimioterapia getItqTipo() {
		return itqTipo;
	}

	public void setItqTipo(DominioTipoPrescricaoQuimioterapia itqTipo) {
		this.itqTipo = itqTipo;
	}

	public Integer getItqSeq() {
		return itqSeq;
	}

	public void setItqSeq(Integer itqSeq) {
		this.itqSeq = itqSeq;
	}

	public String getItqDescricao() {
		return itqDescricao;
	}

	public void setItqDescricao(String itqDescricao) {
		this.itqDescricao = itqDescricao;
	}

	public String getDiqValor() {
		return diqValor;
	}

	public void setDiqValor(String diqValor) {
		this.diqValor = diqValor;
	}

	public Integer getDiqApaAtdSeq() {
		return diqApaAtdSeq;
	}

	public void setDiqApaAtdSeq(Integer diqApaAtdSeq) {
		this.diqApaAtdSeq = diqApaAtdSeq;
	}

	public Integer getDiqApaSeq() {
		return diqApaSeq;
	}

	public void setDiqApaSeq(Integer diqApaSeq) {
		this.diqApaSeq = diqApaSeq;
	}

	public Integer getDiqseqp() {
		return diqseqp;
	}

	public void setDiqseqp(Integer diqseqp) {
		this.diqseqp = diqseqp;
	}

	public Short getDiqDataN() {
		return diqDataN;
	}

	public void setDiqDataN(Short diqDataN) {
		this.diqDataN = diqDataN;
	}

	public String getDescrTipo() {
		return descrTipo;
	}

	public void setDescrTipo(String descrTipo) {
		this.descrTipo = descrTipo;
	}

	public String getOrdem() {
		return ordem;
	}

	public void setOrdem(String ordem) {
		this.ordem = ordem;
	}

	public void setValorOrderBy(String valorOrderBy) {
		this.valorOrderBy = valorOrderBy;
	}
	
	public String getValorOrderBy() {
		return valorOrderBy;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descrTipo == null) ? 0 : descrTipo.hashCode());
		result = prime * result
				+ ((diqApaAtdSeq == null) ? 0 : diqApaAtdSeq.hashCode());
		result = prime * result
				+ ((diqApaSeq == null) ? 0 : diqApaSeq.hashCode());
		result = prime * result + ((diqData == null) ? 0 : diqData.hashCode());
		result = prime * result
				+ ((diqDataN == null) ? 0 : diqDataN.hashCode());
		result = prime * result
				+ ((diqValor == null) ? 0 : diqValor.hashCode());
		result = prime * result + ((diqseqp == null) ? 0 : diqseqp.hashCode());
		result = prime * result
				+ ((itqDescricao == null) ? 0 : itqDescricao.hashCode());
		result = prime * result + ((itqSeq == null) ? 0 : itqSeq.hashCode());
		result = prime * result + ((itqTipo == null) ? 0 : itqTipo.hashCode());
		result = prime * result + ((ordem == null) ? 0 : ordem.hashCode());
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
		SumarioQuimioItensPOLVO other = (SumarioQuimioItensPOLVO) obj;
		if (descrTipo == null) {
			if (other.descrTipo != null) {
				return false;
			}
		} else if (!descrTipo.equals(other.descrTipo)) {
			return false;
		}
		if (diqApaAtdSeq == null) {
			if (other.diqApaAtdSeq != null) {
				return false;
			}
		} else if (!diqApaAtdSeq.equals(other.diqApaAtdSeq)) {
			return false;
		}
		if (diqApaSeq == null) {
			if (other.diqApaSeq != null) {
				return false;
			}
		} else if (!diqApaSeq.equals(other.diqApaSeq)) {
			return false;
		}
		if (diqData == null) {
			if (other.diqData != null) {
				return false;
			}
		} else if (!diqData.equals(other.diqData)) {
			return false;
		}
		if (diqDataN == null) {
			if (other.diqDataN != null) {
				return false;
			}
		} else if (!diqDataN.equals(other.diqDataN)) {
			return false;
		}
		if (diqValor == null) {
			if (other.diqValor != null) {
				return false;
			}
		} else if (!diqValor.equals(other.diqValor)) {
			return false;
		}
		if (diqseqp == null) {
			if (other.diqseqp != null) {
				return false;
			}
		} else if (!diqseqp.equals(other.diqseqp)) {
			return false;
		}
		if (itqDescricao == null) {
			if (other.itqDescricao != null) {
				return false;
			}
		} else if (!itqDescricao.equals(other.itqDescricao)) {
			return false;
		}
		if (itqSeq == null) {
			if (other.itqSeq != null) {
				return false;
			}
		} else if (!itqSeq.equals(other.itqSeq)) {
			return false;
		}
		if (itqTipo == null) {
			if (other.itqTipo != null) {
				return false;
			}
		} else if (!itqTipo.equals(other.itqTipo)) {
			return false;
		}
		if (ordem == null) {
			if (other.ordem != null) {
				return false;
			}
		} else if (!ordem.equals(other.ordem)) {
			return false;
		}
		return true;
	}

	public Integer getItqTipoInteger() {
		return itqTipoInteger;
	}

	public void setItqTipoInteger(Integer itqTipoInteger) {
		this.itqTipoInteger = itqTipoInteger;
	}

	public String getDiqDataFormatada() {
		return diqDataFormatada;
	}

	public void setDiqDataFormatada(String diqDataFormatada) {
		this.diqDataFormatada = diqDataFormatada;
	}
}
	
