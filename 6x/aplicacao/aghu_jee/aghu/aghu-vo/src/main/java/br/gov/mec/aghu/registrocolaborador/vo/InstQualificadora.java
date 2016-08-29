package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

/**
 * Classe utilizada para o retorno dos registros de Instituições Qualificadoras
 * Obtida através de web service
 * 
 * @author agerling
 * 
 */
public class InstQualificadora  implements Serializable  {

	private static final long serialVersionUID = 7568215153012951637L;
	
	private Integer codigo;
	private String descricao;
	private String indInterno;

	public InstQualificadora() {
	}

	public InstQualificadora(Integer codigo, String descricao, String indInterno) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.indInterno = indInterno;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getIndInterno() {
		return indInterno;
	}

	public void setIndInterno(String indInterno) {
		this.indInterno = indInterno;
	}

}
