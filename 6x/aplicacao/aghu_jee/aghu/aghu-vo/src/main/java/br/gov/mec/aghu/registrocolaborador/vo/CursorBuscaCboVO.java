package br.gov.mec.aghu.registrocolaborador.vo;

public class CursorBuscaCboVO {
	
	private Short tiiSeq;
	private String valor;

	public Short getTiiSeq() {
		return tiiSeq;
	}
	
	public void setTiiSeq(Short tiiSeq) {
		this.tiiSeq = tiiSeq;
	}
	
	public String getValor() {
		return valor;
	}
	
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * Método criado para casos onde deve-se efetuar um substring no valor
	 * 
	 * @param qtCaracteres posição final do substring (geralmente -1 pois oracle inicia em 1 e java em 0)
	 * @return valor.substring(0,qtCaracteres)
	 */
	public String getValorSubs(int qtCaracteres) {
		if (valor != null && valor.length() >= qtCaracteres) {
			return valor.substring(0,qtCaracteres);
			
		} else {
			return valor;
		}
	}
}
