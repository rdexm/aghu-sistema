package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RelatorioProgramacaoGradeVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 838036131803078871L;
	
	/*Campos retornados pela consulta C2 (#6810) */
	private String siglaEspecialidade;
	private String nomeEspecialidade;
	private Integer grdSeq;
	private String tpsTipo;
	private Short seqPagador;
	private String descricaoPagador;
	private Byte sala;
	private String siglaUfSala;
	private Integer seqEquipe;
	private String nomeEquipe;
	private Integer perSerMatricula;
	private Short perSerVinculo;
	
	/*Campos concatenados*/
	private String especialidade;
	private String pagador;
	private String setorSala;
	private String equipe;
	private String profissional;
	
	private List<RelatorioProgramacaoGradeHorarioVO> horarios;
	private List<String> listaHora = new ArrayList<String>();
	
	
	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}
	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public Integer getGrdSeq() {
		return grdSeq;
	}
	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}
	public Short getSeqPagador() {
		return seqPagador;
	}
	public void setSeqPagador(Short seqPagador) {
		this.seqPagador = seqPagador;
	}
	public String getDescricaoPagador() {
		return descricaoPagador;
	}
	public void setDescricaoPagador(String descricaoPagador) {
		this.descricaoPagador = descricaoPagador;
	}
	public Byte getSala() {
		return sala;
	}
	public void setSala(Byte sala) {
		this.sala = sala;
	}
	public String getSiglaUfSala() {
		return siglaUfSala;
	}
	public void setSiglaUfSala(String siglaUfSala) {
		this.siglaUfSala = siglaUfSala;
	}
	public Integer getSeqEquipe() {
		return seqEquipe;
	}
	public void setSeqEquipe(Integer seqEquipe) {
		this.seqEquipe = seqEquipe;
	}
	public String getNomeEquipe() {
		return nomeEquipe;
	}
	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}
	public Integer getPerSerMatricula() {
		return perSerMatricula;
	}
	public void setPerSerMatricula(Integer perSerMatricula) {
		this.perSerMatricula = perSerMatricula;
	}
	public Short getPerSerVinculo() {
		return perSerVinculo;
	}
	public void setPerSerVinculo(Short perSerVinculo) {
		this.perSerVinculo = perSerVinculo;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getPagador() {
		return pagador;
	}
	public void setPagador(String pagador) {
		this.pagador = pagador;
	}
	public String getSetorSala() {
		return setorSala;
	}
	public void setSetorSala(String setorSala) {
		this.setorSala = setorSala;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getProfissional() {
		return profissional;
	}
	public void setProfissional(String profissional) {
		this.profissional = profissional;
	}
	public List<RelatorioProgramacaoGradeHorarioVO> getHorarios() {
		return horarios;
	}
	public void setHorarios(List<RelatorioProgramacaoGradeHorarioVO> horarios) {
		this.horarios = horarios;
	}
	public String getTpsTipo() {
		return tpsTipo;
	}
	public void setTpsTipo(String tpsTipo) {
		this.tpsTipo = tpsTipo;
	}
	public List<String> getListaHora() {
		return listaHora;
	}
	public void setListaHora(List<String> listaHora) {
		this.listaHora = listaHora;
	}
	
}
