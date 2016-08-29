package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;

public class RetornoCirurgiaEmLotePesquisaVO {
	
	private Boolean digitouNotaSala;
	private Integer cirurgiaSeq;
	private Short sala;
	private DominioSituacaoCirurgia situacao;
	private Integer prontuario;
	private String nomePaciente;
	private Short convenio;
	private Date dataInicio;
	private Date dataFim;
	private FatConvenioSaudePlano plano;
	private Boolean foiAlterado;
	private String corLinhaTable;
	
	public Boolean getDigitouNotaSala() {
		return digitouNotaSala;
	}
	public void setDigitouNotaSala(Boolean digitouNotaSala) {
		this.digitouNotaSala = digitouNotaSala;
	}
	public Short getSala() {
		return sala;
	}
	public void setSala(Short sala) {
		this.sala = sala;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Short getConvenio() {
		return convenio;
	}
	public void setConvenio(Short convenio) {
		this.convenio = convenio;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public Integer getCirurgiaSeq() {
		return cirurgiaSeq;
	}
	public void setCirurgiaSeq(Integer cirurgiaSeq) {
		this.cirurgiaSeq = cirurgiaSeq;
	}
	public DominioSituacaoCirurgia getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoCirurgia situacao) {
		this.situacao = situacao;
	}
	public FatConvenioSaudePlano getPlano() {
		return plano;
	}
	public void setPlano(FatConvenioSaudePlano plano) {
		this.plano = plano;
	}
	public Boolean getFoiAlterado() {
		return foiAlterado;
	}
	public void setFoiAlterado(Boolean foiAlterado) {
		this.foiAlterado = foiAlterado;
	}
	public String getCorLinhaTable() {
		return corLinhaTable;
	}
	public void setCorLinhaTable(String corLinhaTable) {
		this.corLinhaTable = corLinhaTable;
	}
	public Byte getPlanoId() {
		return plano != null ? plano.getId().getSeq(): null;
	}

}
