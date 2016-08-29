package br.gov.mec.aghu.parametrosistema.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class AghParametroVO implements BaseBean {
	
	private static final long serialVersionUID = 1839156916657546685L;
	
	private String modulo;
	private String nomeParametro;
	private Object valor;
	
	private Integer seq;
	private String nome;
	private String vlrTexto;
	private BigDecimal vlrNumerico;
	private Date vlrData;
	private String nomeSistema;
	
	/**
	 * 
	 */
	public AghParametroVO() {
		super();
	}


	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getModulo() {
		return modulo;
	}

	public void setNomeParametro(String nomeParametro) {
		this.nomeParametro = nomeParametro;
	}

	public String getNomeParametro() {
		return nomeParametro;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	public Object getValor() {
		return valor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof AghParametroVO)) {
			return false;
		}
		AghParametroVO other = (AghParametroVO) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getVlrTexto() {
		return vlrTexto;
	}

	public void setVlrTexto(String vlrTexto) {
		this.vlrTexto = vlrTexto;
	}

	public BigDecimal getVlrNumerico() {
		return vlrNumerico;
	}

	public void setVlrNumerico(BigDecimal vlrNumerico) {
		this.vlrNumerico = vlrNumerico;
	}

	public Date getVlrData() {
		return vlrData;
	}

	public void setVlrData(Date vlrData) {
		this.vlrData = vlrData;
	}

	public String getNomeSistema() {
		return nomeSistema;
	}

	public void setNomeSistema(String nomeSistema) {
		this.nomeSistema = nomeSistema;
	}
	
}
