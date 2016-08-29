package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

public class RelatorioReceitaCuidadoVO implements Serializable {

	private static final long serialVersionUID = 8459574296812321792L;
	
	// Identificação
	private String nome; 
	private String descricao; 
	private String infoConsulta;
	private String enderecoHosp;
	private String cep;
	private String cidade;
	private String uf;
	private String recCuidado;
	private String telefone;
	
	
	private String titulo;
		
	//Data
	private Date data;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nomePaciente) {
		this.nome = nomePaciente;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String prontuario) {
		this.descricao = prontuario;
	}
	public String getInfoConsulta() {
		return infoConsulta;
	}
	public void setInfoConsulta(String infoConsulta) {
		this.infoConsulta = infoConsulta;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}	
	public String getEnderecoHosp() {
		return enderecoHosp;
	}
	public void setEnderecoHosp(String enderecoHosp) {
		this.enderecoHosp = enderecoHosp;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getRecCuidado() {
		return recCuidado;
	}
	public void setRecCuidado(String recCuidado) {
		this.recCuidado = recCuidado;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	

}
