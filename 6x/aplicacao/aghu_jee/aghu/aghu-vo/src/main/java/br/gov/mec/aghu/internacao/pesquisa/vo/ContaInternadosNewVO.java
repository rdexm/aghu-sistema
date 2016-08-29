package br.gov.mec.aghu.internacao.pesquisa.vo;

/**
 * VO utilizado para armazenar os valores de retorno de AINK_PES_REF_PRO.conta_internados_new.
 * 
 * @author Marcelo Tocchetto
 */
public class ContaInternadosNewVO {

	private Integer pac;
	private Integer ast;
	private Integer adm;
	
	public ContaInternadosNewVO() {
	}

	public ContaInternadosNewVO(Integer pac, Integer ast, Integer adm) {
		super();
		this.pac = pac;
		this.ast = ast;
		this.adm = adm;
	}

	public Integer getPac() {
		return this.pac;
	}

	public void setPac(Integer pac) {
		this.pac = pac;
	}

	public Integer getAst() {
		return this.ast;
	}

	public void setAst(Integer ast) {
		this.ast = ast;
	}

	public Integer getAdm() {
		return this.adm;
	}

	public void setAdm(Integer adm) {
		this.adm = adm;
	}

}