package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO que representa os dados de exames significativos associados a unidades funcionais
 * 
 * @author luismoura
 * 
 */
public class ExameSignificativoVO implements BaseBean {
	private static final long serialVersionUID = -9186836221603142392L;

	private Short unfSeq;
	private Integer emaManSeq;
	private String emaExaSigla;
	private String unidadeFuncional;
	private String siglaExame;
	private String exame;
	private String materialAnalise;
	private Boolean indCargaExame;

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public String getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(String unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public String getSiglaExame() {
		return siglaExame;
	}

	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getMaterialAnalise() {
		return materialAnalise;
	}

	public void setMaterialAnalise(String materialAnalise) {
		this.materialAnalise = materialAnalise;
	}

	public Boolean getIndCargaExame() {
		return indCargaExame;
	}

	public void setIndCargaExame(Boolean indCargaExame) {
		this.indCargaExame = indCargaExame;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((emaExaSigla == null) ? 0 : emaExaSigla.hashCode());
		result = prime * result + ((emaManSeq == null) ? 0 : emaManSeq.hashCode());
		result = prime * result + ((unfSeq == null) ? 0 : unfSeq.hashCode());
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
		ExameSignificativoVO other = (ExameSignificativoVO) obj;
		if (emaExaSigla == null) {
			if (other.emaExaSigla != null) {
				return false;
			}
		} else if (!emaExaSigla.equals(other.emaExaSigla)) {
			return false;
		}
		if (emaManSeq == null) {
			if (other.emaManSeq != null) {
				return false;
			}
		} else if (!emaManSeq.equals(other.emaManSeq)) {
			return false;
		}
		if (unfSeq == null) {
			if (other.unfSeq != null) {
				return false;
			}
		} else if (!unfSeq.equals(other.unfSeq)) {
			return false;
		}
		return true;
	}
}