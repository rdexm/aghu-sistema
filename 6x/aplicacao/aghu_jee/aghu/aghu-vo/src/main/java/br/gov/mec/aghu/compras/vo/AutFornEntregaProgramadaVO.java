package br.gov.mec.aghu.compras.vo;


public class AutFornEntregaProgramadaVO extends ProgramacaoEntregaGlobalVO {

	private Integer lctNumero;
	private Short nroComplemento;
	private Integer afnNumero;
	private Integer iafNumero;

	public enum Fields {
		LCT_NUMERO("lctNumero"), NRO_COMPLEMENTO("nroComplemento"), 
		SALDO_PROGRAMADO("saldoProgramado"), VALOR_LIBERAR("valorALiberar"), 
		VALOR_LIBERADO("valorLiberado"), VALOR_ATRASO("valorEmAtraso"), AFN_NUMERO("afnNumero"), IAF_NUMERO("iafNumero");
		
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

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}
	
	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lctNumero == null) ? 0 : lctNumero.hashCode());
		result = prime * result
				+ ((nroComplemento == null) ? 0 : nroComplemento.hashCode());
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
		AutFornEntregaProgramadaVO other = (AutFornEntregaProgramadaVO) obj;
		if (lctNumero == null) {
			if (other.lctNumero != null) {
				return false;
			}
		} else if (!lctNumero.equals(other.lctNumero)) {
			return false;
		}
		if (nroComplemento == null) {
			if (other.nroComplemento != null) {
				return false;
			}
		} else if (!nroComplemento.equals(other.nroComplemento)) {
			return false;
		}
		return true;
	}
	
	
}