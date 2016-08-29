package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacaoConsulta;

public class EspecialidadeDisponivelVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3048858459813705403L;
	private String sigla;
	private String especialidade;
	private String condicaoAtendimento;
	private String pagador;
	private String autorizacao;
	private Long quantidade;
	private Long quantidadeTotal;
	private DominioSituacaoConsulta situacao;
	
	public enum Fields {
		SIGLA("sigla"),
		ESPECIALIDADE("especialidade"),
		CONDICAO_ATENDIMENTO("condicaoAtendimento"),
		PAGADOR("pagador"),
		AUTORIZACAO("autorizacao"),
		QUANTIDADE("quantidade"),
		QUANTIDADE_TOTAL("quantidadeTotal"),
		SITUACAO("situacao");
		
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getCondicaoAtendimento() {
		return condicaoAtendimento;
	}
	public void setCondicaoAtendimento(String condicaoAtendimento) {
		this.condicaoAtendimento = condicaoAtendimento;
	}
	public String getPagador() {
		return pagador;
	}
	public void setPagador(String pagador) {
		this.pagador = pagador;
	}
	public String getAutorizacao() {
		return autorizacao;
	}
	public void setAutorizacao(String autorizacao) {
		this.autorizacao = autorizacao;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public DominioSituacaoConsulta getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoConsulta situacao) {
		this.situacao = situacao;
	}
	public Long getQuantidadeTotal() {
		return quantidadeTotal;
	}
	public void setQuantidadeTotal(Long quantidadeTotal) {
		this.quantidadeTotal = quantidadeTotal;
	}

}
