package br.gov.mec.aghu.exames.vo;




public class RelatorioAgendamentoProfissionalVO {

	//Campos do Relatorio
	private String unidadeFuncional;
	private Integer totalAgendados;
	private Integer totalSolicitacoes;

	//Campos da query para montar o relatório
	private Short unfSeq;
	private Integer solicitacoes ;
	private Short vinCodigo;
	private Integer matricula;
	private Short vinResp;
	private Integer matResp;
	private Integer solicitot;

	public RelatorioAgendamentoProfissionalVO(String unidadeFuncional, Integer totalAgendados, Integer totalSolicitacoes){

		this.unidadeFuncional = unidadeFuncional;
		this.totalAgendados = totalAgendados;
		this.totalSolicitacoes = totalSolicitacoes;

	}

	public RelatorioAgendamentoProfissionalVO(Short unfSeq, Integer solicitacoes, Short vinCodigo, Integer matricula, Short vinResp ,
			Integer matResp, Integer solicitot ){

		this.unfSeq = unfSeq;
		this.solicitacoes = solicitacoes;
		this.vinCodigo = vinCodigo;
		this.matricula = matricula;
		this.vinResp = vinResp;
		this.matResp = matResp;
		this.solicitot = solicitot;

	}

	public RelatorioAgendamentoProfissionalVO(Short unfSeq, Integer solicitacoes, Integer vinCodigo, Integer matricula, Integer vinResp ,
			Integer matResp, Integer solicitot ){

		this.unfSeq = unfSeq;
		this.solicitacoes = solicitacoes;
		if(vinCodigo!=null){
			this.vinCodigo = vinCodigo.shortValue();
		}
		this.matricula = matricula;
		if(vinResp !=null){
			this.vinResp = vinResp.shortValue();
		}
		this.matResp = matResp;
		this.solicitot = solicitot;

	}

	public String getUnidadeFuncional() {
		return unidadeFuncional;
	}
	public void setUnidadeFuncional(String unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	public Integer getTotalAgendados() {
		return totalAgendados;
	}
	public void setTotalAgendados(Integer totalAgendados) {
		this.totalAgendados = totalAgendados;
	}
	public Integer getTotalSolicitacoes() {
		return totalSolicitacoes;
	}
	public void setTotalSolicitacoes(Integer totalSolicitacoes) {
		this.totalSolicitacoes = totalSolicitacoes;
	}


	public Short getUnfSeq() {
		return unfSeq;
	}


	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}


	public Integer getSolicitacoes() {
		return solicitacoes;
	}


	public void setSolicitacoes(Integer solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinResp() {
		return vinResp;
	}


	public void setVinResp(Short vinResp) {
		this.vinResp = vinResp;
	}


	public Integer getMatResp() {
		return matResp;
	}


	public void setMatResp(Integer matResp) {
		this.matResp = matResp;
	}


	public Integer getSolicitot() {
		return solicitot;
	}


	public void setSolicitot(Integer solicitot) {
		this.solicitot = solicitot;
	}


}
