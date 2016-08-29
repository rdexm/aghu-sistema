package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.core.dominio.Dominio;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class CompSanguineoProcedHemoterapicoVO {

	private String codigo;
	private String codigoComposto;
	private String descricao;
	private Boolean indIrradiado;
	private Boolean indFiltrado;
	private Boolean indLavado;
	private Boolean indAferese;
	private Boolean indJustificativa;
	private DominioTipo tipo;
	private String avisoPrescricao;
	private String avisoAferese;
	
	public enum DominioTipo implements Dominio{
		P,C;
		
		@Override
		public int getCodigo() {
			return this.ordinal();
		}
		
		@Override
		public String getDescricao() {
			switch (this) {
			case P:
				return "Procedimento Hemoterapico";
			case C:
				return "Componente Sanguineo";

			default:
				return "";
			}
		}
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public void setCodigoComposto(String codigoComposto) {
		this.codigoComposto = codigoComposto;
	}

	public String getCodigoComposto() {
		return codigoComposto;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Boolean getIndIrradiado() {
		return indIrradiado;
	}
	
	public void setIndIrradiado(Boolean indIrradiado) {
		this.indIrradiado = indIrradiado;
	}
	
	public Boolean getIndFiltrado() {
		return indFiltrado;
	}
	
	public void setIndFiltrado(Boolean indFiltrado) {
		this.indFiltrado = indFiltrado;
	}
	
	public Boolean getIndLavado() {
		return indLavado;
	}
	
	public void setIndLavado(Boolean indLavado) {
		this.indLavado = indLavado;
	}
	
	public Boolean getIndAferese() {
		return indAferese;
	}
	
	public void setIndAferese(Boolean indAferese) {
		this.indAferese = indAferese;
	}
	
	public Boolean getIndJustificativa() {
		return indJustificativa;
	}
	
	public void setIndJustificativa(Boolean indJustificativa) {
		this.indJustificativa = indJustificativa;
	}

	public void setTipo(DominioTipo tipo) {
		this.tipo = tipo;
	}

	public DominioTipo getTipo() {
		return tipo;
	}

	public void setAvisoPrescricao(String avisoPrescricao) {
		this.avisoPrescricao = avisoPrescricao;
	}

	public String getAvisoPrescricao() {
		return avisoPrescricao;
	}
	
	public void setAvisoAferese(String avisoAferese) {
		this.avisoAferese = avisoAferese;
	}

	public String getAvisoAferese() {
		return avisoAferese;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((avisoAferese == null) ? 0 : avisoAferese.hashCode());
		result = prime * result
				+ ((avisoPrescricao == null) ? 0 : avisoPrescricao.hashCode());
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result
				+ ((codigoComposto == null) ? 0 : codigoComposto.hashCode());
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result
				+ ((indAferese == null) ? 0 : indAferese.hashCode());
		result = prime * result
				+ ((indFiltrado == null) ? 0 : indFiltrado.hashCode());
		result = prime * result
				+ ((indIrradiado == null) ? 0 : indIrradiado.hashCode());
		result = prime
				* result
				+ ((indJustificativa == null) ? 0 : indJustificativa.hashCode());
		result = prime * result
				+ ((indLavado == null) ? 0 : indLavado.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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
		CompSanguineoProcedHemoterapicoVO other = (CompSanguineoProcedHemoterapicoVO) obj;
		if (avisoAferese == null) {
			if (other.avisoAferese != null) {
				return false;
			}
		} else if (!avisoAferese.equals(other.avisoAferese)) {
			return false;
		}
		if (avisoPrescricao == null) {
			if (other.avisoPrescricao != null) {
				return false;
			}
		} else if (!avisoPrescricao.equals(other.avisoPrescricao)) {
			return false;
		}
		if (codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!codigo.equals(other.codigo)) {
			return false;
		}
		if (codigoComposto == null) {
			if (other.codigoComposto != null) {
				return false;
			}
		} else if (!codigoComposto.equals(other.codigoComposto)) {
			return false;
		}
		if (descricao == null) {
			if (other.descricao != null) {
				return false;
			}
		} else if (!descricao.equals(other.descricao)) {
			return false;
		}
		if (indAferese == null) {
			if (other.indAferese != null) {
				return false;
			}
		} else if (!indAferese.equals(other.indAferese)) {
			return false;
		}
		if (indFiltrado == null) {
			if (other.indFiltrado != null) {
				return false;
			}
		} else if (!indFiltrado.equals(other.indFiltrado)) {
			return false;
		}
		if (indIrradiado == null) {
			if (other.indIrradiado != null) {
				return false;
			}
		} else if (!indIrradiado.equals(other.indIrradiado)) {
			return false;
		}
		if (indJustificativa == null) {
			if (other.indJustificativa != null) {
				return false;
			}
		} else if (!indJustificativa.equals(other.indJustificativa)) {
			return false;
		}
		if (indLavado == null) {
			if (other.indLavado != null) {
				return false;
			}
		} else if (!indLavado.equals(other.indLavado)) {
			return false;
		}
		if (tipo == null) {
			if (other.tipo != null) {
				return false;
			}
		} else if (!tipo.equals(other.tipo)) {
			return false;
		}
		return true;
	}

}
