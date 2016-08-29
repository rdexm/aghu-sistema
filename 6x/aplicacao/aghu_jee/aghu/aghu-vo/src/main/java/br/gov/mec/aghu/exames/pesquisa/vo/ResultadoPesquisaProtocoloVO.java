package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;

public class ResultadoPesquisaProtocoloVO implements Serializable {
	
	private static final long serialVersionUID = 8660130775577170417L;
	private Integer codigoSoe;
	private Short iseSeq;
	private String situacaoCodigo;
	private Long protocolo;
	private String retiradoEm;
	private String retiradoPor;
	private String usuario;
	private boolean podeCancelar = false;
	private boolean podeEstornar = false;
	private boolean verResultado = false;
	private boolean existeDocAnexado = false;
	
	
	public Long getProtocolo() {
		return protocolo;
	}
	public void setProtocolo(Long protocolo) {
		this.protocolo = protocolo;
	}
	public String getRetiradoEm() {
		return retiradoEm;
	}
	public void setRetiradoEm(String retiradoEm) {
		this.retiradoEm = retiradoEm;
	}
	public String getRetiradoPor() {
		return retiradoPor;
	}
	public void setRetiradoPor(String retiradoPor) {
		this.retiradoPor = retiradoPor;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}	
	public boolean isPodeCancelar() {
		return podeCancelar;
	}
	public void setPodeCancelar(boolean podeCancelar) {
		this.podeCancelar = podeCancelar;
	}
	public boolean isPodeEstornar() {
		return podeEstornar;
	}
	public void setPodeEstornar(boolean podeEstornar) {
		this.podeEstornar = podeEstornar;
	}
	public boolean isVerResultado() {
		return verResultado;
	}
	public void setVerResultado(boolean verResultado) {
		this.verResultado = verResultado;
	}
	public boolean isExisteDocAnexado() {
		return existeDocAnexado;
	}
	public void setExisteDocAnexado(boolean existeDocAnexado) {
		this.existeDocAnexado = existeDocAnexado;
	}
	public Integer getCodigoSoe() {
		return codigoSoe;
	}
	public void setCodigoSoe(Integer codigoSoe) {
		this.codigoSoe = codigoSoe;
	}
	public Short getIseSeq() {
		return iseSeq;
	}
	public void setIseSeq(Short iseSeq) {
		this.iseSeq = iseSeq;
	}
	public String getSituacaoCodigo() {
		return situacaoCodigo;
	}
	public void setSituacaoCodigo(String situacaoCodigo) {
		this.situacaoCodigo = situacaoCodigo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((protocolo == null) ? 0 : protocolo.hashCode());
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
		ResultadoPesquisaProtocoloVO other = (ResultadoPesquisaProtocoloVO) obj;
		if (protocolo == null) {
			if (other.protocolo != null) {
				return false;
			}
		} else {
			if (!protocolo.equals(other.protocolo)) {
			    return false;
			}
		}
		return true;
	}
	
	

}
