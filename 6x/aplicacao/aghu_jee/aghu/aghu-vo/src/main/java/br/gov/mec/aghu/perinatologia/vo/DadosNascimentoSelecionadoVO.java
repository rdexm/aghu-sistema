package br.gov.mec.aghu.perinatologia.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.model.McoNascimentos;

public class DadosNascimentoSelecionadoVO implements Serializable {
	
	private static final long serialVersionUID = 6787122324230664309L;
	
	private McoNascimentos mcoNascimento;
	private McoCesarianas mcoCesariana;
	private McoForcipes mcoForcipe;
	private String hrDuracaoFormatado;
	// Fieldset Parto
	private String duracaoTotalParto;
	// Fieldset Agendamento
	private Date dthrInicioProcedimento;
	private String tempoProcedimento;
	
	// Fieldset Cesariana
	private List<IndicacaoPartoVO> listaNascIndicacoesCesariana = new ArrayList<IndicacaoPartoVO>();
	private McoCesarianas mcoCesarianaExcluir;
	private List<McoNascIndicacoes> listaNascIndicacoesCesarianaRemover = new ArrayList<McoNascIndicacoes>();
	private IndicacaoPartoVO indicacaoPartoVOCesarianaRemover;
	
	// Fieldset Instrumentado (Forcipe)
	private List<IndicacaoPartoVO> listaNascIndicacoesForcipe = new ArrayList<IndicacaoPartoVO>();
	private McoForcipes mcoForcipesExcluir;
	private List<McoNascIndicacoes> listaNascIndicacoesForcipeRemover = new ArrayList<McoNascIndicacoes>();
	private IndicacaoPartoVO indicacaoPartoVOForcipeRemover;
	
	public McoNascimentos getMcoNascimento() {
		return mcoNascimento;
	}
	public void setMcoNascimento(McoNascimentos mcoNascimento) {
		this.mcoNascimento = mcoNascimento;
	}
	public McoCesarianas getMcoCesariana() {
		return mcoCesariana;
	}
	public void setMcoCesariana(McoCesarianas mcoCesariana) {
		this.mcoCesariana = mcoCesariana;
	}
	public McoForcipes getMcoForcipe() {
		return mcoForcipe;
	}
	public void setMcoForcipe(McoForcipes mcoForcipe) {
		this.mcoForcipe = mcoForcipe;
	}
	public String getHrDuracaoFormatado() {
		return hrDuracaoFormatado;
	}
	public void setHrDuracaoFormatado(String hrDuracaoFormatado) {
		this.hrDuracaoFormatado = hrDuracaoFormatado;
	}
	public String getDuracaoTotalParto() {
		return duracaoTotalParto;
	}
	public void setDuracaoTotalParto(String duracaoTotalParto) {
		this.duracaoTotalParto = duracaoTotalParto;
	}
	public Date getDthrInicioProcedimento() {
		return dthrInicioProcedimento;
	}
	public void setDthrInicioProcedimento(Date dthrInicioProcedimento) {
		this.dthrInicioProcedimento = dthrInicioProcedimento;
	}
	public String getTempoProcedimento() {
		return tempoProcedimento;
	}
	public void setTempoProcedimento(String tempoProcedimento) {
		this.tempoProcedimento = tempoProcedimento;
	}
	public List<IndicacaoPartoVO> getListaNascIndicacoesCesariana() {
		return listaNascIndicacoesCesariana;
	}
	public void setListaNascIndicacoesCesariana(
			List<IndicacaoPartoVO> listaNascIndicacoesCesariana) {
		this.listaNascIndicacoesCesariana = listaNascIndicacoesCesariana;
	}
	public McoCesarianas getMcoCesarianaExcluir() {
		return mcoCesarianaExcluir;
	}
	public void setMcoCesarianaExcluir(McoCesarianas mcoCesarianaExcluir) {
		this.mcoCesarianaExcluir = mcoCesarianaExcluir;
	}
	public List<McoNascIndicacoes> getListaNascIndicacoesCesarianaRemover() {
		return listaNascIndicacoesCesarianaRemover;
	}
	public void setListaNascIndicacoesCesarianaRemover(
			List<McoNascIndicacoes> listaNascIndicacoesCesarianaRemover) {
		this.listaNascIndicacoesCesarianaRemover = listaNascIndicacoesCesarianaRemover;
	}
	public IndicacaoPartoVO getIndicacaoPartoVOCesarianaRemover() {
		return indicacaoPartoVOCesarianaRemover;
	}
	public void setIndicacaoPartoVOCesarianaRemover(
			IndicacaoPartoVO indicacaoPartoVOCesarianaRemover) {
		this.indicacaoPartoVOCesarianaRemover = indicacaoPartoVOCesarianaRemover;
	}
	public List<IndicacaoPartoVO> getListaNascIndicacoesForcipe() {
		return listaNascIndicacoesForcipe;
	}
	public void setListaNascIndicacoesForcipe(
			List<IndicacaoPartoVO> listaNascIndicacoesForcipe) {
		this.listaNascIndicacoesForcipe = listaNascIndicacoesForcipe;
	}
	public McoForcipes getMcoForcipesExcluir() {
		return mcoForcipesExcluir;
	}
	public void setMcoForcipesExcluir(McoForcipes mcoForcipesExcluir) {
		this.mcoForcipesExcluir = mcoForcipesExcluir;
	}
	public List<McoNascIndicacoes> getListaNascIndicacoesForcipeRemover() {
		return listaNascIndicacoesForcipeRemover;
	}
	public void setListaNascIndicacoesForcipeRemover(
			List<McoNascIndicacoes> listaNascIndicacoesForcipeRemover) {
		this.listaNascIndicacoesForcipeRemover = listaNascIndicacoesForcipeRemover;
	}
	public IndicacaoPartoVO getIndicacaoPartoVOForcipeRemover() {
		return indicacaoPartoVOForcipeRemover;
	}
	public void setIndicacaoPartoVOForcipeRemover(
			IndicacaoPartoVO indicacaoPartoVOForcipeRemover) {
		this.indicacaoPartoVOForcipeRemover = indicacaoPartoVOForcipeRemover;
	}
	
}
