/**
 * 
 */
package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

/**
 * 
 * @author gmneto
 * 
 */
public class RelatorioConfirmacaoItensPrescricaoVO implements Serializable,
		Comparable<RelatorioConfirmacaoItensPrescricaoVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2180065965215079475L;

	public RelatorioConfirmacaoItensPrescricaoVO(
			String descricaoItemPrescricaoMedica) {
		this.setDescricao(descricaoItemPrescricaoMedica);

	}

	private Integer numero;

	private String tipo;

	private String aprazamento;

	private String descricao;

	private String operacao;
	
	private Integer ordem;

	Boolean indAntiMicrobiano;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getAprazamento() {
		return aprazamento;
	}

	public void setAprazamento(String aprazamento) {
		this.aprazamento = aprazamento;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public Boolean getIndAntiMicrobiano() {
		return indAntiMicrobiano;
	}

	public void setIndAntiMicrobiano(Boolean indAntiMicrobiano) {
		this.indAntiMicrobiano = indAntiMicrobiano;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@Override
	public int compareTo(RelatorioConfirmacaoItensPrescricaoVO o) {

		if (this.getIndAntiMicrobiano() != null
				&& o.getIndAntiMicrobiano() != null) {
			if (this.getIndAntiMicrobiano() == true
					&& o.getIndAntiMicrobiano() == false) {
				return -1;
			}
			if (o.getIndAntiMicrobiano() == true
					&& this.getIndAntiMicrobiano() == false) {
				return 1;
			}
		}
		return this.getDescricao().compareTo(o.getDescricao());

	}
}