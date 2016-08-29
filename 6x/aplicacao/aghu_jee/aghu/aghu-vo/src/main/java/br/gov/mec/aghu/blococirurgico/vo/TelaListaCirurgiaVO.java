package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.RapServidores;

public class TelaListaCirurgiaVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -610735063307130238L;
	private Date dtProcedimento;
	private AghUnidadesFuncionais unidade;
	private MbcSalaCirurgica sala;
	private SuggestionListaCirurgiaVO especialidade;
	private SuggestionListaCirurgiaVO equipe;
	private SuggestionListaCirurgiaVO procedimento;
	private DominioNaturezaFichaAnestesia natureza;
	private boolean checkSalaRecuperacao;
	private boolean pesquisaAtiva;
	private String filtrosPesquisa;
	private boolean pesquisaLiberada;
	private Integer matricula;
	private Short vinculo;

//	private boolean vPermiteAnestesia;

	private Short desmarcar;
	private Short desmarcarAdm;
	private Integer pDiasNotaAdicional;
	private Short convenioSusPadrao;
	
	private String loginUsuarioLogado;
	private RapServidores servidorLogado;

	public Date getDtProcedimento() {
		return dtProcedimento;
	}

	public void setDtProcedimento(Date dtProcedimento) {
		this.dtProcedimento = dtProcedimento;
	}

	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}

	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public MbcSalaCirurgica getSala() {
		return sala;
	}

	public void setSala(MbcSalaCirurgica sala) {
		this.sala = sala;
	}

	public SuggestionListaCirurgiaVO getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(SuggestionListaCirurgiaVO especialidade) {
		this.especialidade = especialidade;
	}

	public SuggestionListaCirurgiaVO getEquipe() {
		return equipe;
	}

	public void setEquipe(SuggestionListaCirurgiaVO equipe) {
		this.equipe = equipe;
	}

	public SuggestionListaCirurgiaVO getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(SuggestionListaCirurgiaVO procedimento) {
		this.procedimento = procedimento;
	}

	public DominioNaturezaFichaAnestesia getNatureza() {
		return natureza;
	}

	public void setNatureza(DominioNaturezaFichaAnestesia natureza) {
		this.natureza = natureza;
	}

	public boolean isCheckSalaRecuperacao() {
		return checkSalaRecuperacao;
	}

	public void setCheckSalaRecuperacao(boolean checkSalaRecuperacao) {
		this.checkSalaRecuperacao = checkSalaRecuperacao;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public String getFiltrosPesquisa() {
		return filtrosPesquisa;
	}

	public void setFiltrosPesquisa(String filtrosPesquisa) {
		this.filtrosPesquisa = filtrosPesquisa;
	}

	public boolean isPesquisaLiberada() {
		return pesquisaLiberada;
	}

	public void setPesquisaLiberada(boolean pesquisaLiberada) {
		this.pesquisaLiberada = pesquisaLiberada;
	}

	public Integer getpDiasNotaAdicional() {
		return pDiasNotaAdicional;
	}

	public void setpDiasNotaAdicional(Integer pDiasNotaAdicional) {
		this.pDiasNotaAdicional = pDiasNotaAdicional;
	}

	public Short getConvenioSusPadrao() {
		return convenioSusPadrao;
	}

	public void setConvenioSusPadrao(Short convenioSusPadrao) {
		this.convenioSusPadrao = convenioSusPadrao;
	}

	public String getLoginUsuarioLogado() {
		return loginUsuarioLogado;
	}

	public void setLoginUsuarioLogado(String loginUsuarioLogado) {
		this.loginUsuarioLogado = loginUsuarioLogado;
	}

	public Short getDesmarcar() {
		return desmarcar;
	}

	public void setDesmarcar(Short desmarcar) {
		this.desmarcar = desmarcar;
	}

	public Short getDesmarcarAdm() {
		return desmarcarAdm;
	}

	public void setDesmarcarAdm(Short desmarcarAdm) {
		this.desmarcarAdm = desmarcarAdm;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
}
