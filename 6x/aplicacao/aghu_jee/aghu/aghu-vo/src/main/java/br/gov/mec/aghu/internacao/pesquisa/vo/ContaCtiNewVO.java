package br.gov.mec.aghu.internacao.pesquisa.vo;

/**
 * VO utilizado para armazenar os valores de retorno de AINK_PES_REF_PRO.conta_cti_new.
 * 
 * @author Marcelo Tocchetto
 */
public class ContaCtiNewVO {

	private Integer cti;
	private Integer adm;
	private Integer ast;

	public ContaCtiNewVO() {
	}

	public ContaCtiNewVO(Integer cti, Integer adm, Integer ast) {
		super();
		this.cti = cti;
		this.adm = adm;
		this.ast = ast;
	}

	public Integer getCti() {
		return this.cti;
	}

	public void setCti(Integer cti) {
		this.cti = cti;
	}

	public Integer getAdm() {
		return this.adm;
	}

	public void setAdm(Integer adm) {
		this.adm = adm;
	}

	public Integer getAst() {
		return this.ast;
	}

	public void setAst(Integer ast) {
		this.ast = ast;
	}

}