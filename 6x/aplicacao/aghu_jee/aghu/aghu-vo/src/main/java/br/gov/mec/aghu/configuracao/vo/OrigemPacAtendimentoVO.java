package br.gov.mec.aghu.configuracao.vo;

import java.io.Serializable;

/**
 * 
 * @author frocha
 *
 */
public class OrigemPacAtendimentoVO  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1851705254865884239L;

	
	private String indPacAtendimento;
	private String origem;
	private Short espSeq;
	
	
	public String getIndPacAtendimento() {
		return indPacAtendimento;
	}
	public void setIndPacAtendimento(String indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
}
