package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.model.ScoSolicitacaoServico;



public class SolicitacaoServicoVO {
	
	private ScoSolicitacaoServico solicitacaoServico;
	private String mensagemAnexos;
	private Boolean possuiAnexo;
	
	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}
	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}
	public String getMensagemAnexos() {
		return mensagemAnexos;
	}
	public void setMensagemAnexos(String mensagemAnexos) {
		this.mensagemAnexos = mensagemAnexos;
	}
	public Boolean getPossuiAnexo() {
		return possuiAnexo;
	}
	public void setPossuiAnexo(Boolean possuiAnexo) {
		this.possuiAnexo = possuiAnexo;
	}
	

	
	
	/*private String numeroSolicitacaoServico;
	private String dataDigitalizacao;
	private String dataSolicitacao;
	private String nroInvestimento;
	private String codigoCentroCusto;
	private String codigoSolicitanteCentroCusto;
	private String descricaoSolicitanteCentroCusto;
	private String codigoAplicada;
	private String descricaoAplicada;
	private String codigoAutTecnica;
	private String codigo;
	private String codigoServico;
	private String nomeServico;
	private String qtdeSolicitada;
	private Double valorUnitPrevisto;
	private String codConvFinanciamento;
	private String descricaoConvFinanciamento;
	private String digitoCodNatDespesa;
	private String codNaturezaDespesa;
	private String descricaoSolicitacao;
	private String aplicacao;
	private String justificativa;
	private String motivoUrgencia;
	private String nomeSolicitante;
	private String nomeAutorizacao;
	private String matriculaAutorizada;
	private String ramalSolicitante;
	private String ramalAutorizacao;
	private String serVinCodAut;
	private String serVinCod;
	private String serMatricula;
	private Double totalValor;
	
	public Double getTotalValor() {
		return totalValor;
	}
	public void setTotalValor(Double totalValor) {
		this.totalValor = totalValor;
	}
	public String getNumeroSolicitacaoServico() {
		return numeroSolicitacaoServico;
	}
	public void setNumeroSolicitacaoServico(String numeroSolicitacaoServico) {
		this.numeroSolicitacaoServico = numeroSolicitacaoServico;
	}
	public String getDataDigitalizacao() {
		return dataDigitalizacao;
	}
	public void setDataDigitalizacao(String dataDigitalizacao) {
		this.dataDigitalizacao = dataDigitalizacao;
	}
	public String getDataSolicitacao() {
		return dataSolicitacao;
	}
	public void setDataSolicitacao(String dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}
	public String getNroInvestimento() {
		return nroInvestimento;
	}
	public void setNroInvestimento(String nroInvestimento) {
		this.nroInvestimento = nroInvestimento;
	}
	public String getCodigoCentroCusto() {
		return codigoCentroCusto;
	}
	public void setCodigoCentroCusto(String codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}
	public String getCodigoSolicitanteCentroCusto() {
		return codigoSolicitanteCentroCusto;
	}
	public void setCodigoSolicitanteCentroCusto(String codigoSolicitanteCentroCusto) {
		this.codigoSolicitanteCentroCusto = codigoSolicitanteCentroCusto;
	}
	public String getDescricaoSolicitanteCentroCusto() {
		return descricaoSolicitanteCentroCusto;
	}
	public void setDescricaoSolicitanteCentroCusto(
			String descricaoSolicitanteCentroCusto) {
		this.descricaoSolicitanteCentroCusto = descricaoSolicitanteCentroCusto;
	}
	public String getCodigoAplicada() {
		return codigoAplicada;
	}
	public void setCodigoAplicada(String codigoAplicada) {
		this.codigoAplicada = codigoAplicada;
	}
	public String getDescricaoAplicada() {
		return descricaoAplicada;
	}
	public void setDescricaoAplicada(String descricaoAplicada) {
		this.descricaoAplicada = descricaoAplicada;
	}
	public String getCodigoAutTecnica() {
		return codigoAutTecnica;
	}
	public void setCodigoAutTecnica(String codigoAutTecnica) {
		this.codigoAutTecnica = codigoAutTecnica;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCodigoServico() {
		return codigoServico;
	}
	public void setCodigoServico(String codigoServico) {
		this.codigoServico = codigoServico;
	}
	public String getNomeServico() {
		return nomeServico;
	}
	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}
	public String getQtdeSolicitada() {
		return qtdeSolicitada;
	}
	public void setQtdeSolicitada(String qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}
	public Double getValorUnitPrevisto() {
		return valorUnitPrevisto;
	}
	public void setValorUnitPrevisto(Double valorUnitPrevisto) {
		this.valorUnitPrevisto = valorUnitPrevisto;
	}
	public String getCodConvFinanciamento() {
		return codConvFinanciamento;
	}
	public void setCodConvFinanciamento(String codConvFinanciamento) {
		this.codConvFinanciamento = codConvFinanciamento;
	}
	public String getDescricaoConvFinanciamento() {
		return descricaoConvFinanciamento;
	}
	public void setDescricaoConvFinanciamento(String descricaoConvFinanciamento) {
		this.descricaoConvFinanciamento = descricaoConvFinanciamento;
	}
	public String getDigitoCodNatDespesa() {
		return digitoCodNatDespesa;
	}
	public void setDigitoCodNatDespesa(String digitoCodNatDespesa) {
		this.digitoCodNatDespesa = digitoCodNatDespesa;
	}
	public String getCodNaturezaDespesa() {
		return codNaturezaDespesa;
	}
	public void setCodNaturezaDespesa(String codNaturezaDespesa) {
		this.codNaturezaDespesa = codNaturezaDespesa;
	}
	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}
	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}
	public String getAplicacao() {
		return aplicacao;
	}
	public void setAplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
	}
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	public String getMotivoUrgencia() {
		return motivoUrgencia;
	}
	public void setMotivoUrgencia(String motivoUrgencia) {
		this.motivoUrgencia = motivoUrgencia;
	}
	public String getNomeSolicitante() {
		return nomeSolicitante;
	}
	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}
	public String getNomeAutorizacao() {
		return nomeAutorizacao;
	}
	public void setNomeAutorizacao(String nomeAutorizacao) {
		this.nomeAutorizacao = nomeAutorizacao;
	}
	public String getMatriculaAutorizada() {
		return matriculaAutorizada;
	}
	public void setMatriculaAutorizada(String matriculaAutorizada) {
		this.matriculaAutorizada = matriculaAutorizada;
	}
	public String getRamalSolicitante() {
		return ramalSolicitante;
	}
	public void setRamalSolicitante(String ramalSolicitante) {
		this.ramalSolicitante = ramalSolicitante;
	}
	public String getRamalAutorizacao() {
		return ramalAutorizacao;
	}
	public void setRamalAutorizacao(String ramalAutorizacao) {
		this.ramalAutorizacao = ramalAutorizacao;
	}
	public String getSerVinCodAut() {
		return serVinCodAut;
	}
	public void setSerVinCodAut(String serVinCodAut) {
		this.serVinCodAut = serVinCodAut;
	}
	public String getSerVinCod() {
		return serVinCod;
	}
	public void setSerVinCod(String serVinCod) {
		this.serVinCod = serVinCod;
	}
	public String getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(String serMatricula) {
		this.serMatricula = serMatricula;
	}*/
	
	
		
}
