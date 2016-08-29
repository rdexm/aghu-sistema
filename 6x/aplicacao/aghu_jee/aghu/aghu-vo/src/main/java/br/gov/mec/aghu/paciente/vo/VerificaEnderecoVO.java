package br.gov.mec.aghu.paciente.vo;

public class VerificaEnderecoVO {

	private Integer ind;
	private Short vSeqp;
	
	private Integer vEndDestinoOk;
	private Character vDestinoOk;
	private Integer vEndDestinoNcadOk;
	
	private Integer vEndOrigemOk; 
	private Character vOrigemOk;  
	private Integer vEndOrigemNcadOk;	
	
	public VerificaEnderecoVO() {
		this.ind = 0;
		this.vSeqp = 0;
		
		// Flags do endereço destino
		this.vEndDestinoOk = 0;
		this.vDestinoOk = 'N';
		this.vEndDestinoNcadOk = 0;

		// Flags do endereço origem
		this.vEndOrigemOk = 0;
		this.vOrigemOk = 'N';
		this.vEndOrigemNcadOk = 0;
	}
		
	public Integer getInd() {
		return this.ind;
	}

	public void setInd(Integer ind) {
		this.ind = ind;
	}

	public Short getvSeqp() {
		return this.vSeqp;
	}

	public void setvSeqp(Short vSeqp) {
		this.vSeqp = vSeqp;
	}

	public Integer getvEndDestinoOk() {
		return this.vEndDestinoOk;
	}

	public void setvEndDestinoOk(Integer vEndDestinoOk) {
		this.vEndDestinoOk = vEndDestinoOk;
	}

	public Character getvDestinoOk() {
		return this.vDestinoOk;
	}

	public void setvDestinoOk(Character vDestinoOk) {
		this.vDestinoOk = vDestinoOk;
	}

	public Integer getvEndDestinoNcadOk() {
		return this.vEndDestinoNcadOk;
	}

	public void setvEndDestinoNcadOk(Integer vEndDestinoNcadOk) {
		this.vEndDestinoNcadOk = vEndDestinoNcadOk;
	}
	
	public Integer getvEndOrigemOk() {
		return this.vEndOrigemOk;
	}

	public void setvEndOrigemOk(Integer vEndOrigemOk) {
		this.vEndOrigemOk = vEndOrigemOk;
	}

	public Character getvOrigemOk() {
		return this.vOrigemOk;
	}

	public void setvOrigemOk(Character vOrigemOk) {
		this.vOrigemOk = vOrigemOk;
	}

	public Integer getvEndOrigemNcadOk() {
		return this.vEndOrigemNcadOk;
	}

	public void setvEndOrigemNcadOk(Integer vEndOrigemNcadOk) {
		this.vEndOrigemNcadOk = vEndOrigemNcadOk;
	}
	
}
