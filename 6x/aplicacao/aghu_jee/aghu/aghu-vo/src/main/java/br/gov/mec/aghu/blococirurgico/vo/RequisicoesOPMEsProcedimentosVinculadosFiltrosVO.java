package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;

public class RequisicoesOPMEsProcedimentosVinculadosFiltrosVO {
	private Date dataRequisicao;
	private RapServidores requerente;
	private RapServidores executor;
	private AghUnidadesFuncionais unidadeFuncional;
	private Date dataProcedimento;
	private AghEspecialidades especialidade;
	private RapServidores equipe;
	private Integer prontuario;
	private String etapaAtualRequisicao;
	private Boolean requisicoesConcluidas;
	private Date dataFim;
	private Integer numeroDias;
	private Date dataAtualMenosNumeroDias;
	private Boolean habilitarBotaoOrcamento;
	
	public Date getDataRequisicao() {
		return dataRequisicao;
	}
	public void setDataRequisicao(Date dataRequisicao) {
		this.dataRequisicao = dataRequisicao;
	}
	public RapServidores getRequerente() {
		return requerente;
	}
	public void setRequerente(RapServidores requerente) {
		this.requerente = requerente;
	}
	public RapServidores getExecutor() {
		return executor;
	}
	public void setExecutor(RapServidores executor) {
		this.executor = executor;
	}

	public Date getDataProcedimento() {
		return dataProcedimento;
	}
	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}
	
	public RapServidores getEquipe() {
		return equipe;
	}
	public void setEquipe(RapServidores equipe) {
		this.equipe = equipe;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getEtapaAtualRequisicao() {
		return etapaAtualRequisicao;
	}
	public void setEtapaAtualRequisicao(
			String etapaAtualRequisicao) {
		this.etapaAtualRequisicao = etapaAtualRequisicao;
	}
	public Boolean getRequisicoesConcluidas() {
		return requisicoesConcluidas;
	}
	public void setRequisicoesConcluidas(Boolean requisicoesConcluidas) {
		this.requisicoesConcluidas = requisicoesConcluidas;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
	public Integer getNumeroDias() {
		return numeroDias;
	}
	public void setNumeroDias(Integer numeroDias) {
		this.numeroDias = numeroDias;
	}
	public Date getDataAtualMenosNumeroDias() {
		return dataAtualMenosNumeroDias;
	}
	public void setDataAtualMenosNumeroDias(Date dataAtualMenosNumeroDias) {
		this.dataAtualMenosNumeroDias = dataAtualMenosNumeroDias;
	}
	public Boolean getHabilitarBotaoOrcamento() {
		return habilitarBotaoOrcamento;
	}
	public void setHabilitarBotaoOrcamento(Boolean habilitarBotaoOrcamento) {
		this.habilitarBotaoOrcamento = habilitarBotaoOrcamento;
	}
	
}
