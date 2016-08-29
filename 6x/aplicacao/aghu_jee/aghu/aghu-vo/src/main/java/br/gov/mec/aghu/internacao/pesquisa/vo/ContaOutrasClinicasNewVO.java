package br.gov.mec.aghu.internacao.pesquisa.vo;

/**
 * VO utilizado para armazenar os valores de retorno de
 * AINK_PES_REF_PRO.conta_outras_clinicas_new.
 * 
 * @author Marcelo Tocchetto
 */
public class ContaOutrasClinicasNewVO {

	private Integer outrasClinicas;
	private Integer adm;
	private Integer ast;

	public ContaOutrasClinicasNewVO() {
	}

	public ContaOutrasClinicasNewVO(Integer outrasClinicas, Integer adm,
			Integer ast) {
		super();
		this.outrasClinicas = outrasClinicas;
		this.adm = adm;
		this.ast = ast;
	}

	public Integer getOutrasClinicas() {
		return this.outrasClinicas;
	}

	public void setOutrasClinicas(Integer outrasClinicas) {
		this.outrasClinicas = outrasClinicas;
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