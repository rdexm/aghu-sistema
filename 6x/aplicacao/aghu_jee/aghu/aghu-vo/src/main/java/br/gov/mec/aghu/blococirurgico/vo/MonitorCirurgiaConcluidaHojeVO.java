package br.gov.mec.aghu.blococirurgico.vo;


/**
 * VO da estória #27171 – Painel para exibição do status do paciente durante o período em que esteve no centro cirúrgico - Monitor
 * 
 * @author aghu
 * 
 */
public class MonitorCirurgiaConcluidaHojeVO extends MonitorCirurgiaVO {

	private static final long serialVersionUID = -6398359929330362704L;

	private String destino; // Coluna DESCRICAO de MBC_DESTINO_PACIENTES
	private String local; // Retorno da FUNCTION MBCC_LOCAL_AIP_PAC
	private Integer codigoPaciente; // Código do paciente utilizado na chamada da FUNCTION MBCC_LOCAL_AIP_PAC

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}
	
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}
	
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

}
