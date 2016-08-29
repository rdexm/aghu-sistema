package br.gov.mec.aghu.internacao.pesquisa.vo;

/**
 * VO utilizado para armazenar os valores de retorno de
 * AINK_PES_REF_PRO.conta_outras_unidades_new.
 * 
 * @author Marcelo Tocchetto
 */
public class ContaOutrasUnidadesNewVO {

	private Integer outrasUnidades;
	private Integer adm;
	private Integer ast;

	public ContaOutrasUnidadesNewVO() {
	}

	public ContaOutrasUnidadesNewVO(Integer outrasUnidades, Integer adm,
			Integer ast) {
		super();
		this.outrasUnidades = outrasUnidades;
		this.adm = adm;
		this.ast = ast;
	}

	public Integer getOutrasUnidades() {
		return this.outrasUnidades;
	}

	public void setOutrasUnidades(Integer outrasUnidades) {
		this.outrasUnidades = outrasUnidades;
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