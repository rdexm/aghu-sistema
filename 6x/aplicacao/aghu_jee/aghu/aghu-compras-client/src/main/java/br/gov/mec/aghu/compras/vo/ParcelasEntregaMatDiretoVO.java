package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

public class ParcelasEntregaMatDiretoVO implements Serializable {

	private static final long serialVersionUID = -62571880948778766L;

	// IAF.AFN_NUMERO
	private Integer iafAfnNumero;
	// IAF.NUMERO
	private Integer iafNumero;
	// (IAF.QTDE_SOLICITADA - COALESCE(IAF.QTDE_RECEBIDA,0)) QTDE_SALDO_ITEM
	private Integer qtdSaldoItem;
	// IAF.VALOR_UNITARIO
	private Double iafValorUnitario;
	// MAT.CODIGO MAT_CODIGO
	private Integer matCodigo;
	// SLC.NUMERO SLC_NUMERO
	private Integer sclNumero;
	// IAF.UMD_CODIGO
	private String iafUmdCod;
	// IAF.QTDE_SOLICITADA
	private Integer iafQtdSolicitada;
	// MAT.GMT_CODIGO
	private Integer codGrupoMaterial;
	// IAF.IND_CONTRATO,
	private Boolean iafIndContrato;
	// IAF.IND_CONSIGNADO
	private Boolean iafIndConsignado;
	
	private Integer qtdParcelaZero;
	private Integer numeroParcela;
	private Integer seq;
	private Integer qtdSaldo;
	private Integer qtdParcela;
	
	

	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Integer getQtdSaldoItem() {
		return qtdSaldoItem;
	}

	public void setQtdSaldoItem(Integer qtdSaldoItem) {
		this.qtdSaldoItem = qtdSaldoItem;
	}

	public Double getIafValorUnitario() {
		return iafValorUnitario;
	}

	public void setIafValorUnitario(Double iafValorUnitario) {
		this.iafValorUnitario = iafValorUnitario;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public Integer getSclNumero() {
		return sclNumero;
	}

	public void setSclNumero(Integer sclNumero) {
		this.sclNumero = sclNumero;
	}

	public String getIafUmdCod() {
		return iafUmdCod;
	}

	public void setIafUmdCod(String iafUmdCod) {
		this.iafUmdCod = iafUmdCod;
	}

	public Integer getIafQtdSolicitada() {
		return iafQtdSolicitada;
	}

	public void setIafQtdSolicitada(Integer iafQtdSolicitada) {
		this.iafQtdSolicitada = iafQtdSolicitada;
	}

	public Integer getCodGrupoMaterial() {
		return codGrupoMaterial;
	}

	public void setCodGrupoMaterial(Integer codGrupoMaterial) {
		this.codGrupoMaterial = codGrupoMaterial;
	}

	public Boolean getIafIndContrato() {
		return iafIndContrato;
	}

	public void setIafIndContrato(Boolean iafIndContrato) {
		this.iafIndContrato = iafIndContrato;
	}

	public Boolean getIafIndConsignado() {
		return iafIndConsignado;
	}

	public void setIafIndConsignado(Boolean iafIndConsignado) {
		this.iafIndConsignado = iafIndConsignado;
	}

	public void setQtdParcelaZero(Integer qtdParcelaZero) {
		this.qtdParcelaZero = qtdParcelaZero;
	}

	public Integer getQtdParcelaZero() {
		return qtdParcelaZero;
	}

	public void setNumeroParcela(Integer numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public Integer getNumeroParcela() {
		return numeroParcela;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setQtdSaldo(Integer qtdSaldo) {
		this.qtdSaldo = qtdSaldo;
	}

	public Integer getQtdSaldo() {
		return qtdSaldo;
	}

	public void setQtdParcela(Integer qtdParcela) {
		this.qtdParcela = qtdParcela;
	}

	public Integer getQtdParcela() {
		return qtdParcela;
	}

}
