package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PEDIDO")
@XmlAccessorType(XmlAccessType.FIELD)
public class PedidoPapelariaVO implements java.io.Serializable {

	private static final long serialVersionUID = -869047834824138329L;

	public PedidoPapelariaVO() {
		
	}
	
	@XmlElement(name="NumPedido")
	private Integer numPedido;
	
	@XmlElement(name="DataPedido")
	private Date dataPedido;
	
	@XmlElement(name="HoraPedido")
	private Date horaPedido;
	
	@XmlElement(name="Usuario")
	private String usuario;
	
	@XmlElement(name="CentroCusto")
	private Integer centroCusto;
	
	@XmlElement(name="Observacoes")
	private String observacoes;
	
	@XmlElement(name="NumeroNotaFiscal")
	private Integer numeroNotaFiscal;
	
	@XmlElement(name="DataFaturamento")
	private Date dataFaturamento;
	
	@XmlElement(name="NumeroPedidoFaturado")
	private Integer numeroPedidoFaturado;
	
	@XmlElement(name="ValorFaturado")
	private BigDecimal valorFaturado;
	
	@XmlElement(name="ITENSPEDIDO")
	private ItensPedidoPapelaria itensPedidoPapelaria;
	
	
	public Integer getNumPedido() {
		return numPedido;
	}


	public void setNumPedido(Integer numPedido) {
		this.numPedido = numPedido;
	}


	public Date getDataPedido() {
		return dataPedido;
	}


	public void setDataPedido(Date dataPedido) {
		this.dataPedido = dataPedido;
	}


	public Date getHoraPedido() {
		return horaPedido;
	}


	public void setHoraPedido(Date horaPedido) {
		this.horaPedido = horaPedido;
	}


	public String getUsuario() {
		return usuario;
	}


	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}


	public Integer getCentroCusto() {
		if(centroCusto != null) {
			return centroCusto ==  0 ? null : centroCusto;
		} else {
			return centroCusto;
		}
	}


	public void setCentroCusto(Integer centroCusto) {
		this.centroCusto = centroCusto;
	}


	public String getObservacoes() {
		return observacoes;
	}


	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}


	public Integer getNumeroNotaFiscal() {
		if(numeroNotaFiscal != null) {
			return numeroNotaFiscal ==  0 ? null : numeroNotaFiscal;
		} else {
			return numeroNotaFiscal;
		}
	}


	public void setNumeroNotaFiscal(Integer numeroNotaFiscal) {
		this.numeroNotaFiscal = numeroNotaFiscal;
	}


	public Date getDataFaturamento() {
		return dataFaturamento;
	}


	public void setDataFaturamento(Date dataFaturamento) {
		this.dataFaturamento = dataFaturamento;
	}


	public Integer getNumeroPedidoFaturado() {
		return numeroPedidoFaturado;
	}


	public void setNumeroPedidoFaturado(Integer numeroPedidoFaturado) {
		this.numeroPedidoFaturado = numeroPedidoFaturado;
	}


	public BigDecimal getValorFaturado() {
		return valorFaturado;
	}


	public void setValorFaturado(BigDecimal valorFaturado) {
		this.valorFaturado = valorFaturado;
	}


	public ItensPedidoPapelaria getItensPedidoPapelaria() {
		return itensPedidoPapelaria;
	}


	public void setItensPedidoPapelaria(ItensPedidoPapelaria itensPedidoPapelaria) {
		this.itensPedidoPapelaria = itensPedidoPapelaria;
	}


	public enum Fields {
		RETORNO("BuscaPedidosResponse.BuscaPedidosResult");		

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