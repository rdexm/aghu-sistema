package br.gov.mec.aghu.sig.custos.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ITENSPEDIDO")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItensPedidoPapelaria {
	
	@XmlElement(name="itemPedido")
	private List<ItemPedidoPapelariaVO> itensPedidoPapelaria;

	public List<ItemPedidoPapelariaVO> getItensPedidoPapelaria() {
		return itensPedidoPapelaria;
	}

	public void setItensPedidoPapelaria(
			List<ItemPedidoPapelariaVO> itensPedidoPapelaria) {
		this.itensPedidoPapelaria = itensPedidoPapelaria;
	}
}
