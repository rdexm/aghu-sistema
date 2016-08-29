package br.gov.mec.aghu.compras.vo;


public class ItemAutFornEntregaProgramadaVO extends ProgramacaoEntregaGlobalVO{

	private Integer lctNumero;
	private Short nroComplemento;
	private Short itlNumero;
	private Integer matCodigo;
	private String matNome;
	private String umdCodigo;
	private String umdDescricao;
	private Integer iafNumero;

	public enum Fields {
		LCT_NUMERO("lctNumero"), NRO_COMPLEMENTO("nroComplemento"), 
		SALDO_PROGRAMADO("saldoProgramado"), VALOR_LIBERAR("valorALiberar"), 
		VALOR_LIBERADO("valorLiberado"), VALOR_ATRASO("valorEmAtraso"), AFN_NUMERO("afnNumero"), 
		ITL_NUMERO("itlNumero"), NOME_MATERIAL("matNome"), CODIGO_MATERIAL("matCodigo"), 
		DESCRICAO_UNIDADE_MEDIDA("umdDescricao"), CODIGO_UNIDADE_MEDIDA("umdCodigo"), IAF_NUMERO("iafNumero");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Short getItlNumero() {
		return itlNumero;
	}

	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getMatNome() {
		return matNome;
	}

	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}

	public String getUmdCodigo() {
		return umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	public String getUmdDescricao() {
		return umdDescricao;
	}

	public void setUmdDescricao(String umdDescricao) {
		this.umdDescricao = umdDescricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = hashCodeNumero(prime, result);
		result = hashCodeMaterial(prime, result);
		result = prime * result
				+ ((nroComplemento == null) ? 0 : nroComplemento.hashCode());
		result = prime * result
				+ ((umdCodigo == null) ? 0 : umdCodigo.hashCode());
		result = prime * result
				+ ((umdDescricao == null) ? 0 : umdDescricao.hashCode());
		return result;
	}

	private int hashCodeMaterial(final int prime, int result) {
		result = prime * result
				+ ((matCodigo == null) ? 0 : matCodigo.hashCode());
		result = prime * result + ((matNome == null) ? 0 : matNome.hashCode());
		return result;
	}

	private int hashCodeNumero(final int prime, int result) {
		result = prime * result
				+ ((itlNumero == null) ? 0 : itlNumero.hashCode());
		result = prime * result
				+ ((lctNumero == null) ? 0 : lctNumero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean b = equalsObj(obj);
		if (!b) {
			return false;
		}
		ItemAutFornEntregaProgramadaVO other = (ItemAutFornEntregaProgramadaVO) obj;
		b = equalsNumeros(other);
		
		if (!b) {
			return false;
		}
		b = equalsMaterial(other);
		
		if (!b) {
			return false;
		}
		if (nroComplemento == null) {
			if (other.nroComplemento != null) {
				return false;
			}
		} else if (!nroComplemento.equals(other.nroComplemento)) {
			return false;
		}
		if (umdCodigo == null) {
			if (other.umdCodigo != null) {
				return false;
			}
		} else if (!umdCodigo.equals(other.umdCodigo)) {
			return false;
		}
		if (umdDescricao == null) {
			if (other.umdDescricao != null) {
				return false;
			}
		} else if (!umdDescricao.equals(other.umdDescricao)) {
			return false;
		}
		return true;
	}

	private boolean equalsMaterial(ItemAutFornEntregaProgramadaVO other) {
		if (matCodigo == null) {
			if (other.matCodigo != null) {
				return false;
			}
		} else if (!matCodigo.equals(other.matCodigo)) {
			return false;
		}
		if (matNome == null) {
			if (other.matNome != null) {
				return false;
			}
		} else if (!matNome.equals(other.matNome)) {
			return false;
		}
		return true;
	}

	private boolean equalsNumeros(ItemAutFornEntregaProgramadaVO other) {
		if (itlNumero == null) {
			if (other.itlNumero != null) {
				return false;
			}
		} else if (!itlNumero.equals(other.itlNumero)) {
			return false;
		}
		if (lctNumero == null) { 
			if (other.lctNumero != null) {
				return false;
			}
		} else if (!lctNumero.equals(other.lctNumero)) {
			return false;
		}
		return true;
	}

	private boolean equalsObj(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return true;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}
	
}