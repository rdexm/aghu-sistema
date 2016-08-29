package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioBaseReposicao;
import br.gov.mec.aghu.dominio.DominioTipoConsumo;

public class CriterioReposicaoMaterialVO {

	private DominioBaseReposicao baseReposicao;
	private Date dataInicioReposicao;
	private Date dataFimReposicao;
	private DominioTipoConsumo tipoConsumo;
	private BigDecimal fatorSeguranca;
	private String nomeLote;

	public DominioBaseReposicao getBaseReposicao() {
		return baseReposicao;
	}

	public void setBaseReposicao(DominioBaseReposicao baseReposicao) {
		this.baseReposicao = baseReposicao;
	}

	public Date getDataInicioReposicao() {
		return dataInicioReposicao;
	}

	public void setDataInicioReposicao(Date dataInicioReposicao) {
		this.dataInicioReposicao = dataInicioReposicao;
	}

	public Date getDataFimReposicao() {
		return dataFimReposicao;
	}

	public void setDataFimReposicao(Date dataFimReposicao) {
		this.dataFimReposicao = dataFimReposicao;
	}

	public DominioTipoConsumo getTipoConsumo() {
		return tipoConsumo;
	}

	public void setTipoConsumo(DominioTipoConsumo tipoConsumo) {
		this.tipoConsumo = tipoConsumo;
	}

	public BigDecimal getFatorSeguranca() {
		return fatorSeguranca;
	}

	public void setFatorSeguranca(BigDecimal fatorSeguranca) {
		this.fatorSeguranca = fatorSeguranca;
	}

	public String getNomeLote() {
		return nomeLote;
	}

	public void setNomeLote(String nomeLote) {
		this.nomeLote = nomeLote;
	}
}
