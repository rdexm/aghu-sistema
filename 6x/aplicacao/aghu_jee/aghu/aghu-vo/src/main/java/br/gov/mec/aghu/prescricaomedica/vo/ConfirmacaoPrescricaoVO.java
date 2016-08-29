package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;

public class ConfirmacaoPrescricaoVO implements Serializable {
	
	private static final long serialVersionUID = -25669481937594694L;
	
	
	
	
	public ConfirmacaoPrescricaoVO(List<ItemPrescricaoMedica> itensConfirmados) {
		super();
		this.itensConfirmados = itensConfirmados;
	}

	public ConfirmacaoPrescricaoVO() {
	}

	public List<ItemPrescricaoMedica> getItensConfirmados() {
		return itensConfirmados;
	}
	
	public void setItensConfirmados(List<ItemPrescricaoMedica> itensConfirmados) {
		this.itensConfirmados = itensConfirmados;
	}
	public Date getDataMovimento() {
		return dataMovimento;
	}
	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}
	public RapServidores getServidorValido() {
		return servidorValido;
	}
	public void setServidorValido(RapServidores servidorValido) {
		this.servidorValido = servidorValido;
	}
	private List<ItemPrescricaoMedica> itensConfirmados;
	private Date dataMovimento;
	private RapServidores servidorValido;
	

}
