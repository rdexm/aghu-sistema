package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;

public class VRapServidorConselhoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1927000295519742179L;
	

	private Long luxSeq;
	
	private Integer luiSeq;
	
	private String luiNomeParaLaudo;
	
	private DominioFuncaoPatologista luiFuncao;
	
	private String vcsNroRegConselho;
	
	private Integer luiMatricula;
	
	private Short luiVinCodigo;
	
	public VRapServidorConselhoVO() {
		super();
	}

	//@SuppressWarnings("ucd")
	public VRapServidorConselhoVO(Long luxSeq, Integer luiSeq,
			String luiNomeParaLaudo, DominioFuncaoPatologista luiFuncao,
			String vcsNroRegConselho, Integer luiMatricula, Short luiVinCodigo) {
		super();
		this.luxSeq = luxSeq;
		this.luiSeq = luiSeq;
		this.luiNomeParaLaudo = luiNomeParaLaudo;
		this.luiFuncao = luiFuncao;
		this.vcsNroRegConselho = vcsNroRegConselho;
		this.luiMatricula = luiMatricula;
		this.luiVinCodigo = luiVinCodigo;
	}

	public Long getLuxSeq() {
		return luxSeq;
	}

	public Integer getLuiSeq() {
		return luiSeq;
	}

	public String getLuiNomeParaLaudo() {
		return luiNomeParaLaudo;
	}

	public DominioFuncaoPatologista getLuiFuncao() {
		return luiFuncao;
	}

	public String getVcsNroRegConselho() {
		return vcsNroRegConselho;
	}

	public Integer getLuiMatricula() {
		return luiMatricula;
	}

	public Short getLuiVinCodigo() {
		return luiVinCodigo;
	}

	public void setLuxSeq(Long luxSeq) {
		this.luxSeq = luxSeq;
	}

	public void setLuiSeq(Integer luiSeq) {
		this.luiSeq = luiSeq;
	}

	public void setLuiNomeParaLaudo(String luiNomeParaLaudo) {
		this.luiNomeParaLaudo = luiNomeParaLaudo;
	}

	public void setLuiFuncao(DominioFuncaoPatologista luiFuncao) {
		this.luiFuncao = luiFuncao;
	}

	public void setVcsNroRegConselho(String vcsNroRegConselho) {
		this.vcsNroRegConselho = vcsNroRegConselho;
	}

	public void setLuiMatricula(Integer luiMatricula) {
		this.luiMatricula = luiMatricula;
	}

	public void setLuiVinCodigo(Short luiVinCodigo) {
		this.luiVinCodigo = luiVinCodigo;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((luiFuncao == null) ? 0 : luiFuncao.hashCode());
		result = prime * result
				+ ((luiMatricula == null) ? 0 : luiMatricula.hashCode());
		result = prime
				* result
				+ ((luiNomeParaLaudo == null) ? 0 : luiNomeParaLaudo.hashCode());
		result = prime * result + ((luiSeq == null) ? 0 : luiSeq.hashCode());
		result = prime * result
				+ ((luiVinCodigo == null) ? 0 : luiVinCodigo.hashCode());
		result = prime * result + ((luxSeq == null) ? 0 : luxSeq.hashCode());
		result = prime
				* result
				+ ((vcsNroRegConselho == null) ? 0 : vcsNroRegConselho
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
		if (!(obj instanceof VRapServidorConselhoVO)) {
			return false;
		}
		VRapServidorConselhoVO other = (VRapServidorConselhoVO) obj;
		if (luiFuncao != other.luiFuncao) {
			return false;
		}
		if (luiMatricula == null) {
			if (other.luiMatricula != null) {
				return false;
			}
		} else if (!luiMatricula.equals(other.luiMatricula)) {
			return false;
		}
		if (luiNomeParaLaudo == null) {
			if (other.luiNomeParaLaudo != null) {
				return false;
			}
		} else if (!luiNomeParaLaudo.equals(other.luiNomeParaLaudo)) {
			return false;
		}
		if (luiSeq == null) {
			if (other.luiSeq != null) {
				return false;
			}
		} else if (!luiSeq.equals(other.luiSeq)) {
			return false;
		}
		if (luiVinCodigo == null) {
			if (other.luiVinCodigo != null) {
				return false;
			}
		} else if (!luiVinCodigo.equals(other.luiVinCodigo)) {
			return false;
		}
		if (luxSeq == null) {
			if (other.luxSeq != null) {
				return false;
			}
		} else if (!luxSeq.equals(other.luxSeq)) {
			return false;
		}
		if (vcsNroRegConselho == null) {
			if (other.vcsNroRegConselho != null) {
				return false;
			}
		} else if (!vcsNroRegConselho.equals(other.vcsNroRegConselho)) {
			return false;
		}
		return true;
	}

}
