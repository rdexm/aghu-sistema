package br.gov.mec.aghu.configuracao.vo;

import java.io.Serializable;
/**
 * VO que retorna os CIDs
 * @author felipe
 *
 */
public class CidVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1727825356718573422L;
	private Integer seq;
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	private String codigo;
	private String descricao;
	
	

}
