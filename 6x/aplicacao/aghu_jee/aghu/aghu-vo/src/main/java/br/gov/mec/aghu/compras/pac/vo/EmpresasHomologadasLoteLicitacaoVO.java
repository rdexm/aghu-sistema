package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class EmpresasHomologadasLoteLicitacaoVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String loteTipo3;
	private String valorLote;
	private String tempoLance;
	private String tempoDisputa;
	private String cnpjTipo3;
	private String nomeFornecedorTipo3;
	
		
	public String getLoteTipo3() {
		return loteTipo3;
	}
	public void setLoteTipo3(String loteTipo3) {
		this.loteTipo3 = loteTipo3;
	}
	public String getValorLote() {
		return valorLote;
	}
	public void setValorLote(String valorLote) {
		this.valorLote = valorLote;
	}
	public String getTempoLance() {
		return tempoLance;
	}
	public void setTempoLance(String tempoLance) {
		this.tempoLance = tempoLance;
	}
	public String getTempoDisputa() {
		return tempoDisputa;
	}
	public void setTempoDisputa(String tempoDisputa) {
		this.tempoDisputa = tempoDisputa;
	}
	public String getCnpjTipo3() {
		return cnpjTipo3;
	}
	public void setCnpjTipo3(String cnpjTipo3) {
		this.cnpjTipo3 = cnpjTipo3;
	}
	public String getNomeFornecedorTipo3() {
		return nomeFornecedorTipo3;
	}
	public void setNomeFornecedorTipo3(String nomeFornecedorTipo3) {
		this.nomeFornecedorTipo3 = nomeFornecedorTipo3;
	}
}
