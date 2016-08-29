package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioStatusEnvio;

/**
 * Classe do tipo VO, criada para auxiliar o retorno de envio dos itens do
 * contrato.
 * 
 * @author agerling
 */

public class EnvioItemVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1496191842425645330L;
	// Dados Retorno Envio
	private DominioStatusEnvio statusEnvio;
	private String codigoErro;
	private String descricaoErro;

	// Dados do Item (Serviço/Material)
	private Integer numItem;
	private String codigoMatServ;
	private String codigoInterno;
	private String indicadorMatServ;
	private String descricao;

	public DominioStatusEnvio getStatusEnvio() {
		return statusEnvio;
	}

	public void setStatusEnvio(DominioStatusEnvio statusEnvio) {
		this.statusEnvio = statusEnvio;
	}

	public Integer getNumItem() {
		return numItem;
	}

	public void setNumItem(Integer numItem) {
		this.numItem = numItem;
	}

	public String getCodigoErro() {
		return codigoErro;
	}

	public void setCodigoErro(String codigoErro) {
		this.codigoErro = codigoErro;
	}

	public String getDescricaoErro() {
		return descricaoErro;
	}

	public void setDescricaoErro(String descricaoErro) {
		this.descricaoErro = descricaoErro;
	}

	public String getCodigoMatServ() {
		return codigoMatServ;
	}

	public void setCodigoMatServ(String codigoMatServ) {
		this.codigoMatServ = codigoMatServ;
	}

	public String getCodigoInterno() {
		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {
		this.codigoInterno = codigoInterno;
	}

	public String getIndicadorMatServ() {
		return indicadorMatServ;
	}

	public void setIndicadorMatServ(String indicadorMatServ) {
		this.indicadorMatServ = indicadorMatServ;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
