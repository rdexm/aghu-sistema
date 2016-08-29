package br.gov.mec.aghu.paciente.vo;
import java.io.Serializable;

/**
 * VO utilizado na estoria #17321 - POL: emitir relatório de antendimentos na emergência obstétrica
 */
public class RelatorioAtendEmergObstetricaCondutaVO implements Serializable {

	private static final long serialVersionUID = -1603533859853794271L;
	private String descrConduta; //DESCR_CONDUTA de QCDT
	private String codConduta; //COD_CONDUTA de QCDT
	private String complementoConduta; //COMPLEMENTO_CONDUTA de QPLI
	
	public String getDescrConduta() {
		return descrConduta;
	}
	
	public void setDescrConduta(String descrConduta) {
		this.descrConduta = descrConduta;
	}
	
	public String getCodConduta() {
		return codConduta;
	}
	
	public void setCodConduta(String codConduta) {
		this.codConduta = codConduta;
	}
	
	public String getComplementoConduta() {
		return complementoConduta;
	}
	
	public void setComplementoConduta(String complementoConduta) {
		this.complementoConduta = complementoConduta;
	}
}
