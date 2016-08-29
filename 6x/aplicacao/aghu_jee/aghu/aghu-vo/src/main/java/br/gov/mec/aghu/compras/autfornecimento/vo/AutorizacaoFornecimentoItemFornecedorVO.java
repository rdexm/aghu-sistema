package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.util.Date;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AutorizacaoFornecimentoItemFornecedorVO implements BaseBean {
	
	private static final long serialVersionUID = 5853262266668455566L;
	
	private Integer proprostaFornecedorNumero;
	private Integer propostaFornecedorLicitacaoNumero;
	private Short numeroComplemento;
	private Integer afp;
	private Short item;
	private Integer parc;
	private Date prevEntrega;
	private Integer diasAtraso;
	private Integer qtd;
	private Integer qtdEntregue;
	private Integer saldoEntrega;
	private String unid;
	private Integer codigoMaterial;
	private String material;
	private RapServidores servidor;
	private RapServidores servidorGestor;
	private String gestor;
	private Integer telefone;

	public Integer getAfp() {
		return afp;
	}

	public void setAfp(Integer afp) {
		this.afp = afp;
	}

	public Short getItem() {
		return item;
	}

	public void setItem(Short item) {
		this.item = item;
	}

	public Integer getParc() {
		return parc;
	}

	public void setParc(Integer parc) {
		this.parc = parc;
	}

	public Date getPrevEntrega() {
		return prevEntrega;
	}

	public void setPrevEntrega(Date prevEntrega) {
		this.prevEntrega = prevEntrega;
	}

	public Integer getDiasAtraso() {
		return diasAtraso;
	}

	public void setDiasAtraso(Integer diasAtraso) {
		this.diasAtraso = diasAtraso;
	}

	public Integer getQtd() {
		return qtd == null ? Integer.valueOf("0") : qtd;
	}

	public void setQtd(Integer qtd) {
		this.qtd = qtd;
	}

	public Integer getQtdEntregue() {
		return qtdEntregue == null ? Integer.valueOf("0") : qtdEntregue;
	}

	public void setQtdEntregue(Integer qtdEntregue) {
		this.qtdEntregue = qtdEntregue;
	}

	public Integer getSaldoEntrega() {
		return saldoEntrega;
	}

	public void setSaldoEntrega(Integer saldoEntrega) {
		this.saldoEntrega = saldoEntrega;
	}

	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getGestor() {
		return gestor;
	}

	public void setGestor(String gestor) {
		this.gestor = gestor;
	}

	public Integer getTelefone() {
		return telefone;
	}

	public void setTelefone(Integer telefone) {
		this.telefone = telefone;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidorGestor() {
		return servidorGestor;
	}

	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}

	public Integer getProprostaFornecedorNumero() {
		return proprostaFornecedorNumero;
	}

	public void setProprostaFornecedorNumero(Integer proprostaFornecedorNumero) {
		this.proprostaFornecedorNumero = proprostaFornecedorNumero;
	}

	public Integer getPropostaFornecedorLicitacaoNumero() {
		return propostaFornecedorLicitacaoNumero;
	}

	public void setPropostaFornecedorLicitacaoNumero(
			Integer propostaFornecedorLicitacaoNumero) {
		this.propostaFornecedorLicitacaoNumero = propostaFornecedorLicitacaoNumero;
	}
}