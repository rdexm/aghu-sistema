package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;


public class SolServicoVO implements BaseBean {

	private static final long serialVersionUID = 5814589235635382536L;
	private Integer numero;
	private Short pontoParadaAtual;
	private Short pontoParada;
	private Boolean urgente;
	private Boolean prioridade;
	private Boolean exclusao;
	private Boolean devolucao;
	private Date dataSolicitacao;
	private Integer codigoServico;
	private String nomeServico;
	private Integer qtdSolicitada;
	private String descricaoPontoParada;
	private Integer codigoCc;
	private String descricaoCc;
	private Integer codigoCcAplicacao;
	private String descricaoCcAplicacao;
	private Short serVinCodigoComprador;
	private Integer serVinMatriculaComprador;
	private String nomeUsualComprador;
	private String nomeComprador;
	private String nomeSolicitante;
	private Integer qtdAnexo;
	private String descricaoServico;
	private String descricaoSs;
	private String ramal;
	private Integer devolvida;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		SolServicoVO other = (SolServicoVO) obj;
		if (numero == null) {
			if (other.numero != null){
				return false;
			}
		} else if (!numero.equals(other.numero)){
			return false;
		}
		return true;
	}
	
	public enum Fields {
		
		NUMERO("numero"),
		PONTO_PARADA_ATUAL("pontoParadaAtual"),
		PONTO_PARADA("pontoParada"),
		URGENTE("urgente"),
		PRIORIDADE("prioridade"),
		EXCLUSAO("exclusao"),
		DEVOLUCAO("devolucao"),
		DT_SOLICITACAO("dataSolicitacao"),
		SRV_CODIGO("codigoServico"),
		NOME_SERVICO("nomeServico"),
		QTD_SOLICITADA("qtdSolicitada"),
		DESCRICAO_PONTO_PARADA("descricaoPontoParada"),
		CODIGO_CC("codigoCc"),
		DESCRICAO_CC("descricaoCc"),
		CODIGO_CC_APLICACAO("codigoCcAplicacao"),
		QTDE_ANEXO("qtdAnexo"),
		DESCRICAO_CC_APLICACAO("descricaoCcAplicacao"),
		VINCULO_COMPRADOR("serVinCodigoComprador"),
		MATRICULA_COMPRADOR("serVinMatriculaComprador"),
		NOME_USUAL_COMPRADOR("nomeUsualComprador"),
		NOME_SOLICITANTE("nomeSolicitante"),
		NOME_COMPRADOR("nomeComprador"),
		DESCRICAO_SERVICO("descricaoServico"),
		DESCRICAO_SS("descricaoSs"),
		RAMAL("ramal"),
		DEVOLVIDA("devolvida");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Short getPontoParadaAtual() {
		return pontoParadaAtual;
	}

	public void setPontoParadaAtual(Short pontoParadaAtual) {
		this.pontoParadaAtual = pontoParadaAtual;
	}

	public Short getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(Short pontoParada) {
		this.pontoParada = pontoParada;
	}

	public Boolean getUrgente() {
		return urgente;
	}

	public void setUrgente(Boolean urgente) {
		this.urgente = urgente;
	}

	public Boolean getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Boolean prioridade) {
		this.prioridade = prioridade;
	}

	public Boolean getExclusao() {
		return exclusao;
	}

	public void setExclusao(Boolean exclusao) {
		this.exclusao = exclusao;
	}

	public Boolean getDevolucao() {
		return devolucao;
	}

	public void setDevolucao(Boolean devolucao) {
		this.devolucao = devolucao;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Integer getQtdSolicitada() {
		return qtdSolicitada;
	}

	public void setQtdSolicitada(Integer qtdSolicitada) {
		this.qtdSolicitada = qtdSolicitada;
	}

	public String getDescricaoPontoParada() {
		return descricaoPontoParada;
	}

	public void setDescricaoPontoParada(String descricaoPontoParada) {
		this.descricaoPontoParada = descricaoPontoParada;
	}

	public Integer getCodigoCc() {
		return codigoCc;
	}

	public void setCodigoCc(Integer codigoCc) {
		this.codigoCc = codigoCc;
	}

	public String getDescricaoCc() {
		return descricaoCc;
	}

	public void setDescricaoCc(String descricaoCc) {
		this.descricaoCc = descricaoCc;
	}

	public Integer getCodigoCcAplicacao() {
		return codigoCcAplicacao;
	}

	public void setCodigoCcAplicacao(Integer codigoCcAplicacao) {
		this.codigoCcAplicacao = codigoCcAplicacao;
	}

	public String getDescricaoCcAplicacao() {
		return descricaoCcAplicacao;
	}

	public void setDescricaoCcAplicacao(String descricaoCcAplicacao) {
		this.descricaoCcAplicacao = descricaoCcAplicacao;
	}

	public Integer getQtdAnexo() {
		return qtdAnexo;
	}

	public void setQtdAnexo(Integer qtdAnexo) {
		this.qtdAnexo = qtdAnexo;
	}

	public Integer getCodigoServico() {
		return codigoServico;
	}

	public void setCodigoServico(Integer codigoServico) {
		this.codigoServico = codigoServico;
	}

	public String getNomeServico() {
		return nomeServico;
	}

	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}

	public Short getSerVinCodigoComprador() {
		return serVinCodigoComprador;
	}

	public void setSerVinCodigoComprador(Short serVinCodigoComprador) {
		this.serVinCodigoComprador = serVinCodigoComprador;
	}

	public Integer getSerVinMatriculaComprador() {
		return serVinMatriculaComprador;
	}

	public void setSerVinMatriculaComprador(Integer serVinMatriculaComprador) {
		this.serVinMatriculaComprador = serVinMatriculaComprador;
	}

	public String getNomeUsualComprador() {
		return nomeUsualComprador;
	}

	public void setNomeUsualComprador(String nomeUsualComprador) {
		this.nomeUsualComprador = nomeUsualComprador;
	}

	public String getNomeComprador() {
		return nomeComprador;
	}

	public void setNomeComprador(String nomeComprador) {
		this.nomeComprador = nomeComprador;
	}

	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	public String getDescricaoServico() {
		return descricaoServico;
	}

	public void setDescricaoServico(String descricaoServico) {
		this.descricaoServico = descricaoServico;
	}

	public String getDescricaoSs() {
		return descricaoSs;
	}

	public void setDescricaoSs(String descricaoSs) {
		this.descricaoSs = descricaoSs;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}

	public Integer getDevolvida() {
		return devolvida;
	}

	public void setDevolvida(Integer devolvida) {
		this.devolvida = devolvida;
	}
}
