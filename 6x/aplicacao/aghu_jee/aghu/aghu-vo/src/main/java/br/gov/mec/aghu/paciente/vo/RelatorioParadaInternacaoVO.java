package br.gov.mec.aghu.paciente.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RelatorioParadaInternacaoVO {

	private String descricaoItemOrdem1; //descricao_item_ordem1 de Q_1
	private List<RelatorioParadaInternacaoExamesVO> exames = new ArrayList<RelatorioParadaInternacaoExamesVO>();
	private List<RelatorioParadaInternacaoDetalhesExamesVO> detalhesExames = new ArrayList<RelatorioParadaInternacaoDetalhesExamesVO>();
	private List<RelatorioParadaInternacaoControleVO> controles = new ArrayList<RelatorioParadaInternacaoControleVO>();
	private String descricaoItemOrdem2;
	private String nomePaciente;
	private Boolean recemNascido; //recem_nascido de Q_SUE
	private Integer pacCodigo; //pac_codigo de Q_SUE
	private String agenda;
	private String prontuario;
	private Boolean exibirExamesDetalhesExames; //será utilizado no jasper
	
	//parametros para relatorio
	private Integer atdSeq;
	private Date dthrCriacao;
	
	public String getDescricaoItemOrdem1() {
		return descricaoItemOrdem1;
	}
	public void setDescricaoItemOrdem1(String descricaoItemOrdem1) {
		this.descricaoItemOrdem1 = descricaoItemOrdem1;
	}
	public List<RelatorioParadaInternacaoExamesVO> getExames() {
		return exames;
	}
	public void setExames(List<RelatorioParadaInternacaoExamesVO> exames) {
		this.exames = exames;
	}
	public List<RelatorioParadaInternacaoDetalhesExamesVO> getDetalhesExames() {
		return detalhesExames;
	}
	public void setDetalhesExames(List<RelatorioParadaInternacaoDetalhesExamesVO> detalhesExames) {
		this.detalhesExames = detalhesExames;
	}
	public List<RelatorioParadaInternacaoControleVO> getControles() {
		return controles;
	}
	public void setControles(List<RelatorioParadaInternacaoControleVO> controles) {
		this.controles = controles;
	}
	public String getDescricaoItemOrdem2() {
		return descricaoItemOrdem2;
	}
	public void setDescricaoItemOrdem2(String descricaoItemOrdem2) {
		this.descricaoItemOrdem2 = descricaoItemOrdem2;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getAgenda() {
		return agenda;
	}
	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public void setExibirExamesDetalhesExames(Boolean exibirExamesDetalhesExames) {
		this.exibirExamesDetalhesExames = exibirExamesDetalhesExames;
	}
	public Boolean getExibirExamesDetalhesExames() {
		return exibirExamesDetalhesExames;
	}
	public Boolean getRecemNascido() {
		return recemNascido;
	}
	public void setRecemNascido(Boolean recemNascido) {
		this.recemNascido = recemNascido;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Date getDthrCriacao() {
		return dthrCriacao;
	}
	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}
}
