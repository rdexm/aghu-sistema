package br.gov.mec.aghu.compras.vo;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

public class ParcelasAFVO {

	private Integer peaSeq;
	private Integer afnNumero;
	private Integer iafNumero;
	private Short itlNumero;
	private Integer peaParcela;
	private Date peaDtEntrega;
	private Integer codigo;
	private String nome;
	private String iafUnidadeMedidaCodigo;
	private Integer iafFatorConversao;
	private Integer peaQtde;
	private Integer peaQtdeEntregue;
	private Double peaValorTotal;
	private Double peaValorUnit;
	private Boolean peaIndPlanejamento;
	private Boolean peaIndAssinatura;
	private Boolean peaIndEntregaImediata;
	private Boolean peaIndTramiteInterno;
	private List<EntregaPorItemVO> entregasPorItem;
	private Boolean possuiEntregaPorItem;

	private DominioTipoFaseSolicitacao tipoParcela;

	public DominioTipoFaseSolicitacao getTipoParcela() {
		return tipoParcela;
	}

	public void setTipoParcela(DominioTipoFaseSolicitacao tipoParcela) {
		this.tipoParcela = tipoParcela;
	}

	public Integer getPeaSeq() {
		return peaSeq;
	}

	public void setPeaSeq(Integer peaSeq) {
		this.peaSeq = peaSeq;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Short getItlNumero() {
		return itlNumero;
	}

	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}

	public Integer getPeaParcela() {
		return peaParcela;
	}

	public void setPeaParcela(Integer peaParcela) {
		this.peaParcela = peaParcela;
	}

	public Date getPeaDtEntrega() {
		return peaDtEntrega;
	}

	public void setPeaDtEntrega(Date peaDtEntrega) {
		this.peaDtEntrega = peaDtEntrega;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getIafUnidadeMedidaCodigo() {
		return iafUnidadeMedidaCodigo;
	}

	public void setIafUnidadeMedidaCodigo(String iafUnidadeMedidaCodigo) {
		this.iafUnidadeMedidaCodigo = iafUnidadeMedidaCodigo;
	}

	public Integer getIafFatorConversao() {
		return iafFatorConversao;
	}

	public void setIafFatorConversao(Integer iafFatorConversao) {
		this.iafFatorConversao = iafFatorConversao;
	}

	public Integer getPeaQtde() {
		peaQtde = peaQtde == 0 ? 1 : peaQtde;
		return peaQtde;
	}

	public void setPeaQtde(Integer peaQtde) {
		this.peaQtde = peaQtde;
	}

	public Integer getPeaQtdeEntregue() {
		return peaQtdeEntregue;
	}

	public void setPeaQtdeEntregue(Integer peaQtdeEntregue) {
		this.peaQtdeEntregue = peaQtdeEntregue;
	}

	public Double getPeaValorTotal() {
		return peaValorTotal;
	}

	public void setPeaValorTotal(Double peaValorTotal) {
		this.peaValorTotal = peaValorTotal;
	}

	public Boolean getPeaIndPlanejamento() {
		return peaIndPlanejamento;
	}

	public void setPeaIndPlanejamento(Boolean peaIndPlanejamento) {
		this.peaIndPlanejamento = peaIndPlanejamento;
	}

	public Boolean getPeaIndAssinatura() {
		return peaIndAssinatura;
	}

	public void setPeaIndAssinatura(Boolean peaIndAssinatura) {
		this.peaIndAssinatura = peaIndAssinatura;
	}

	public Boolean getPeaIndEntregaImediata() {
		return peaIndEntregaImediata;
	}

	public void setPeaIndEntregaImediata(Boolean peaIndEntregaImediata) {
		this.peaIndEntregaImediata = peaIndEntregaImediata;
	}

	public Boolean getPeaIndTramiteInterno() {
		return peaIndTramiteInterno;
	}

	public void setPeaIndTramiteInterno(Boolean peaIndTramiteInterno) {
		this.peaIndTramiteInterno = peaIndTramiteInterno;
	}

	public List<EntregaPorItemVO> getEntregasPorItem() {
		return entregasPorItem;
	}

	public void setEntregasPorItem(List<EntregaPorItemVO> entregasPorItem) {
		this.entregasPorItem = entregasPorItem;
	}

	public String getCodigoNome() {
		String retorno = null;
		if (codigo != null && nome != null) {
			if(nome.length()>30){
				retorno = codigo + " - " + StringUtils.abbreviate(nome, 15);
			}else{
				retorno = codigo + " - " + nome;
			}
		}
		return retorno;
	}

	public String getPeaValorTotalFormatado(){
		String valorFormatado = AghuNumberFormat.formatarValor(peaValorTotal, "###,##0.00");
		if(valorFormatado.length()>10){
			valorFormatado ="*********";
		}
		
		return valorFormatado;
	}
	
	public String getPeaValorUnitFormatado(){
		String valorFormatado = AghuNumberFormat.formatarValor(peaValorUnit, "###,##0.00##");
		if(valorFormatado.length()>11){
			valorFormatado ="*********";
		}
		return valorFormatado;
	}
	
	public Double getPeaValorUnit() {
		return peaValorUnit;
	}

	public void setPeaValorUnit(Double peaValorUnit) {
		this.peaValorUnit = peaValorUnit;
	}
	
	public Boolean getPossuiEntregaPorItem() {
		return possuiEntregaPorItem;
	}

	public void setPossuiEntregaPorItem(Boolean possuiEntregaPorItem) {
		this.possuiEntregaPorItem = possuiEntregaPorItem;
	}



	public enum Fields {
		PEA_SEQ("peaSeq"), AFN_NUMERO("afnNumero"), IAF_NUMERO("iafNumero"), ITL_NUMERO(
				"itlNumero"), PEA_PARCELA("peaParcela"), PEA_DT_PREV_ENTREGA(
				"peaDtEntrega"), CODIGO("codigo"), NOME("nome"), IAF_UNID_CODIGO(
				"iafUnidadeMedidaCodigo"), IAF_FATOR_CONVERSAO(
				"iafFatorConversao"), PEA_QTDE("peaQtde"), PEA_QTDE_ENTREGUE(
				"peaQtdeEntregue"), PEA_VALOR_TOTAL("peaValorTotal"), PEA_IND_PLANEJAMENTO(
				"peaIndPlanejamento"), PEA_IND_ASSINATURA("peaIndAssinatura"), PEA_IND_ENTREGA_IMEDIATA(
				"peaIndEntregaImediata"), PEA_IND_TRAMITE_INTERNO(
				"peaIndTramiteInterno");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}