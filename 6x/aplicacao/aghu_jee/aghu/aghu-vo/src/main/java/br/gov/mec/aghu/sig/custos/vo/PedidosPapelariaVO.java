package br.gov.mec.aghu.sig.custos.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PEDIDOS")
@XmlAccessorType(XmlAccessType.FIELD)
public class PedidosPapelariaVO {
	
	@XmlElement(name="PEDIDO")
	private List<PedidoPapelariaVO> pedidosPapelaria;

	public List<PedidoPapelariaVO> getPedidosPapelaria() {
		return pedidosPapelaria;
	}

	public void setPedidosPapelaria(List<PedidoPapelariaVO> pedidosPapelaria) {
		this.pedidosPapelaria = pedidosPapelaria;
	}
}
