package br.gov.mec.aghu.paciente.vo;


/**
 * Os dados armazenados nesse objeto representam as solicitações de
 * desarquivamento de prontuário ulitizados na geração de relatório.
 * 
 * @author lalegre
 */
public class DesarquivamentoProntuarioVO { // implements JRDataSource
	
	private String solicitacao;
	private String prontuario;
	private String nome;
	private String local;
	private String observacao;
	private String volume;
	private String dataMvto;
	
	
	public String getSolicitacao() {
		return solicitacao;
	}
	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getDataMvto() {
		return dataMvto;
	}
	public void setDataMvto(String dataMvto) {
		this.dataMvto = dataMvto;
	}

}
