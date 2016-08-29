package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.List;

import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class FormaAgendamentoVO {

	private List<AacPagador> pagadorList;
	private AacPagador pagador;
	private List<AacTipoAgendamento> tipoAgendamentoList;
	private AacTipoAgendamento tipoAgendamento;
	private List<AacCondicaoAtendimento> condicaoAtendimentoList;
	private AacCondicaoAtendimento condicaoAtendimento;
	private Boolean indProjetoPesquisa=false;
	
	public enum FormaAgendamentoVOExceptionCode implements
	BusinessExceptionCode {
		AAC_00613;
	}

	public void executaValidacao() throws BaseRuntimeException{
		boolean valido=false;
		if (pagador!=null && tipoAgendamento!=null && condicaoAtendimento!=null){
			valido=true;
		}else if (pagador==null && tipoAgendamento==null && condicaoAtendimento==null){
			valido=true;
		}
		if (!valido){
			throw new BaseRuntimeException(FormaAgendamentoVOExceptionCode.AAC_00613);
		}
	}
	
	
	public void limpaSelecionados(){
		pagador=null;
		tipoAgendamento=null;
		condicaoAtendimento=null;
		indProjetoPesquisa=false;
	}
	
	public void clonaSelecionados(FormaAgendamentoVO formaAgendamento){
		this.pagador=formaAgendamento.getPagador();
		this.tipoAgendamento=formaAgendamento.getTipoAgendamento();
		this.condicaoAtendimento=formaAgendamento.getCondicaoAtendimento();
	}

	
	public void insereNaGrade(AacGradeAgendamenConsultas grade){
		grade.setPagador(pagador);
		grade.setTipoAgendamento(tipoAgendamento);
		grade.setCondicaoAtendimento(condicaoAtendimento);
	}
	
	
	public FormaAgendamentoVO(){		
	}
	
	public void set(List<AacPagador> pagadorList,
			List<AacTipoAgendamento> tipoAgendamentoList,
			List<AacCondicaoAtendimento> condicaoAtendimentoList) {
		this.pagadorList = pagadorList;
		this.tipoAgendamentoList = tipoAgendamentoList;
		this.condicaoAtendimentoList = condicaoAtendimentoList;
	}


	public List<AacPagador> getPagadorList() {
		return pagadorList;
	}
	public void setPagadorList(List<AacPagador> pagadorList) {
		this.pagadorList = pagadorList;
	}
	public AacPagador getPagador() {
		return pagador;
	}
	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}
	public List<AacTipoAgendamento> getTipoAgendamentoList() {
		return tipoAgendamentoList;
	}
	public void setTipoAgendamentoList(List<AacTipoAgendamento> tipoAgendamentoList) {
		this.tipoAgendamentoList = tipoAgendamentoList;
	}
	public AacTipoAgendamento getTipoAgendamento() {
		return tipoAgendamento;
	}
	public void setTipoAgendamento(AacTipoAgendamento tipoAgendamento) {
		this.tipoAgendamento = tipoAgendamento;
	}
	public List<AacCondicaoAtendimento> getCondicaoAtendimentoList() {
		return condicaoAtendimentoList;
	}
	public void setCondicaoAtendimentoList(
			List<AacCondicaoAtendimento> condicaoAtendimentoList) {
		this.condicaoAtendimentoList = condicaoAtendimentoList;
	}
	public AacCondicaoAtendimento getCondicaoAtendimento() {
		return condicaoAtendimento;
	}
	public void setCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) {
		this.condicaoAtendimento = condicaoAtendimento;
	}
	public Boolean getIndProjetoPesquisa() {
		return indProjetoPesquisa;
	}
	public void setIndProjetoPesquisa(Boolean indProjetoPesquisa) {
		this.indProjetoPesquisa = indProjetoPesquisa;
	}
}