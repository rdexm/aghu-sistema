package br.gov.mec.aghu.compras.vo;

import java.util.Date;

public class ProgramacaoParcelaItemVO {
	
	private Integer numeroAf; // sco_autorizacoes_forn - pfr_lct_numero
	private Short nroComplemento; // sco_autorizacoes_forn - nro_complemento
	private Short numeroItem; // sco_fases_solicitacoes - itl_numero
	private String unidFornecimento; // sco_itens_autorizacao_forn - umd_codigo
	private Integer fatorConversao; // sco_itens_autorizacao_forn - fator_conversao
	private String unidEstoque; // sco_materiais - umd_codigo
	private Integer codigoMaterial;
	private Integer numeroFornecedor;
	private Date dtVencContrato;

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Short getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Short numeroItem) {
		this.numeroItem = numeroItem;
	}

	public String getUnidFornecimento() {
		return unidFornecimento;
	}

	public void setUnidFornecimento(String unidFornecimento) {
		this.unidFornecimento = unidFornecimento;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public String getUnidEstoque() {
		return unidEstoque;
	}

	public void setUnidEstoque(String unidEstoque) {
		this.unidEstoque = unidEstoque;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Date getDtVencContrato() {
		return dtVencContrato;
	}

	public void setDtVencContrato(Date dtVencContrato) {
		this.dtVencContrato = dtVencContrato;
	}
}
