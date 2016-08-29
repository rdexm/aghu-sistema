package br.gov.mec.aghu.paciente.vo;

/**
 * Os dados armazenados nesse objeto representam as movimentações ulitizados na
 * tela de 'Relatório de Movimentação por Situação'.
 * 
 * @author Ricardo Costa
 */
public class RelatorioMovimentacaoVO {

	private String codigo; //1
	private String descricao; //2
	private String prontuario; //7
	private String nome;
	private String ltoLtoId;
	private String codigo2;
	private String dataMovimento;
	private String dataRtDv;
	private String dataLocal;
	private String volumes;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public String getCodigo2() {
		return codigo2;
	}

	public void setCodigo2(String codigo2) {
		this.codigo2 = codigo2;
	}

	public String getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(String dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public String getDataRtDv() {
		return dataRtDv;
	}

	public void setDataRtDv(String dataRtDv) {
		this.dataRtDv = dataRtDv;
	}

	public String getDataLocal() {
		return dataLocal;
	}

	public void setDataLocal(String dataLocal) {
		this.dataLocal = dataLocal;
	}

	public String getVolumes() {
		return volumes;
	}

	public void setVolumes(String volumes) {
		this.volumes = volumes;
	}

}
