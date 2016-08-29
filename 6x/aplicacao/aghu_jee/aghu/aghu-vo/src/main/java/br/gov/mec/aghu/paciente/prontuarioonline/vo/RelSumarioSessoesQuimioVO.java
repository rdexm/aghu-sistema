package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;
import java.util.List;


public class RelSumarioSessoesQuimioVO {
	//01
	private String nomePaciente;		//F_PACIENTE			F_NOME_RODAPE
	private String prontuario;			//F_PRONTUARIO			F_PONTUARIO_RODAPE
	private String nomeEquipe;			//F_EQUIPE
	private Date dataInicioTratamento;	//F_DTHR_INICIO_TRAT
	private Date dataFimTratamento;		//F_DTHR_FIM_TRAT
	//02
	private String textoProtocolo;
	private String protocolo;			//F_PROTOCOLO
	private Date dataInicial;			//F_DATA_INICIAL
	private Date dataFim;				//F_DATA_FINAL
	//03
	private List<Date> listaDias;
	//04
	private String tipo;				//F_TIPO
	private String descricao;			//F_DESCRICAO
	private List<String> status;
	
	//05
	private String unidade;				//F_1
	private Date dataAtual;				// F_current_date3_SEC2	F_DATA_ATLZ
	private String tipoEmissao;
	

	//Getters and Setters
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNomeEquipe() {
		return nomeEquipe;
	}
	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}
	public Date getDataInicioTratamento() {
		return dataInicioTratamento;
	}
	public void setDataInicioTratamento(Date dataInicioTratamento) {
		this.dataInicioTratamento = dataInicioTratamento;
	}
	public Date getDataFimTratamento() {
		return dataFimTratamento;
	}
	public void setDataFimTratamento(Date dataFimTratamento) {
		this.dataFimTratamento = dataFimTratamento;
	}
	public String getProtocolo() {
		return protocolo;
	}
	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}
	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public List<Date> getListaDias() {
		return listaDias;
	}
	public void setListaDias(List<Date> listaDias) {
		this.listaDias = listaDias;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public Date getDataAtual() {
		return dataAtual;
	}
	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}
	public void setTextoProtocolo(String textoProtocolo) {
		this.textoProtocolo = textoProtocolo;
	}
	public String getTextoProtocolo() {
		return textoProtocolo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public void setStatus(List<String> status) {
		this.status = status;
	}
	public List<String> getStatus() {
		return status;
	}
	public void setTipoEmissao(String tipoEmissao) {
		this.tipoEmissao = tipoEmissao;
	}
	public String getTipoEmissao() {
		return tipoEmissao;
	}
}