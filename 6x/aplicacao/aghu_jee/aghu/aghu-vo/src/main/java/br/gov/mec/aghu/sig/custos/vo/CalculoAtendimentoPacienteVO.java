package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CalculoAtendimentoPacienteVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5927655917562263571L;

	private BigDecimal quantidade;
	private Short ordemVisualizacao;
	private String descricao;
	private String agrupador;
	private Integer codCentroCusto;
	private String descricaoCentroCusto;
	private BigDecimal custoUnitario;
	private BigDecimal custoTotal;
	private BigDecimal receitaTotal;
	private String objNome;
	private String phiDescricao;
	private String especialidade;
	private Short espSeq;
	private String nomeEquipe;
	private Integer matriculaRespEquipe;
	private Short vinCodigoRespEquipe;
	private String diagnostico;
	private Boolean principal;
	private Date dataInicio;
	private Date dataFim;
	private Integer ctcSeq;
	private String nomeReduzido; 
	
	public String getNomeReduzido() {
		return nomeReduzido;
	}

	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}

	public CalculoAtendimentoPacienteVO() {}
	
	public BigDecimal getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}
	
	public Short getOrdemVisualizacao() {
		return ordemVisualizacao;
	}
	
	public void setOrdemVisualizacao(Short ordemVisualizacao) {
		this.ordemVisualizacao = ordemVisualizacao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getAgrupador() {
		return agrupador;
	}

	public void setAgrupador(String agrupador) {
		this.agrupador = agrupador;
	}

	public BigDecimal getCustoUnitario() {
		return custoUnitario;
	}
	
	public void setCustoUnitario(BigDecimal custoUnitario) {
		this.custoUnitario = custoUnitario;
	}
	
	public BigDecimal getCustoTotal() {
		return custoTotal;
	}
	
	public void setCustoTotal(BigDecimal custoTotal) {
		this.custoTotal = custoTotal;
	}	

	public Integer getCodCentroCusto() {
		return codCentroCusto;
	}

	public String getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
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

	public void setCodCentroCusto(Integer codCentroCusto) {
		this.codCentroCusto = codCentroCusto;
	}

	public String getDescricaoCentroCusto() {
		return descricaoCentroCusto;
	}

	public void setDescricaoCentroCusto(String descricaoCentroCusto) {
		this.descricaoCentroCusto = descricaoCentroCusto;
	}
	
	public String getObjNome() {
		return objNome;
	}

	public void setObjNome(String objNome) {
		this.objNome = objNome;
	}

	public String getPhiDescricao() {
		return phiDescricao;
	}

	public void setPhiDescricao(String phiDescricao) {
		this.phiDescricao = phiDescricao;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public Integer getMatriculaRespEquipe() {
		return matriculaRespEquipe;
	}

	public void setMatriculaRespEquipe(Integer matriculaRespEquipe) {
		this.matriculaRespEquipe = matriculaRespEquipe;
	}

	public Short getVinCodigoRespEquipe() {
		return vinCodigoRespEquipe;
	}

	public void setVinCodigoRespEquipe(Short vinCodigoRespEquipe) {
		this.vinCodigoRespEquipe = vinCodigoRespEquipe;
	}

	public String getDescricaoFormatada() {
		if(agrupador != null){
			return agrupador + " - "+ descricao;
		}
		else{
			return descricao;
		}
	}
	
	public String getDescricaoCentroCustoFormatada() {
		return codCentroCusto + " - " + descricaoCentroCusto;
	}
	
	public BigDecimal getReceitaTotal() {
		return receitaTotal;
	}

	public void setReceitaTotal(BigDecimal receitaTotal) {
		this.receitaTotal = receitaTotal;
	}
	
	public Integer getCtcSeq() {
		return ctcSeq;
	}

	public void setCtcSeq(Integer ctcSeq) {
		this.ctcSeq = ctcSeq;
	}

	public String getCentroProducaoFormatado() {
		
		if(objNome == null) {
			if(phiDescricao != null) {
				return phiDescricao;
			}
		} else {
			return objNome;
		}
		
		return "NENHUM";
	}
}
