package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class FatTabRegistroVO {

	private String codigo;
	
	private String descricao;

	private DominioSituacao situacao;

	private TipoProcessado processado;
	
	public FatTabRegistroVO() {}
	
	public FatTabRegistroVO(String codigo,String descricao, DominioSituacao situacao, TipoProcessado processado) {
		super();
		this.codigo = codigo;
		this.descricao = descricao;
		this.situacao = situacao;
		this.processado = processado;
	}

	public enum TipoProcessado {

		INCLUI("I"),
		ALTERA("A"),
		PROCESSADO("S"),
		NAO_PROCESSADO("N"),
		DESPREZADO("D");
		
		private String fields;

		private TipoProcessado(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public TipoProcessado getProcessado() {
		return processado;
	}

	public void setProcessado(TipoProcessado processado) {
		this.processado = processado;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}