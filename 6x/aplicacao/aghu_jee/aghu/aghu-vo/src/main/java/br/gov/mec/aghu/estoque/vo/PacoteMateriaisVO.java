package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class PacoteMateriaisVO implements Serializable{

	private static final long serialVersionUID = 6199272730131095426L;
	private Integer codigoCentroCustoProprietario;
	private String descricaoCentroCustoProprietario;
	private Integer codigoCentroCustoAplicacao;
	private String descricaoCentroCustoAplicacao;
	private Integer numero;
	private Short seqAlmoxarifado;
	private String descricaoAlmoxarifado;
	private DominioSituacao situacao;
	private String descricao;
	

	public Integer getCodigoCentroCustoProprietario() {
		return codigoCentroCustoProprietario;
	}



	public void setCodigoCentroCustoProprietario(
			Integer codigoCentroCustoProprietario) {
		this.codigoCentroCustoProprietario = codigoCentroCustoProprietario;
	}



	public String getDescricaoCentroCustoProprietario() {
		return descricaoCentroCustoProprietario;
	}



	public void setDescricaoCentroCustoProprietario(
			String descricaoCentroCustoProprietario) {
		this.descricaoCentroCustoProprietario = descricaoCentroCustoProprietario;
	}



	public Integer getCodigoCentroCustoAplicacao() {
		return codigoCentroCustoAplicacao;
	}



	public void setCodigoCentroCustoAplicacao(Integer codigoCentroCustoAplicacao) {
		this.codigoCentroCustoAplicacao = codigoCentroCustoAplicacao;
	}



	public String getDescricaoCentroCustoAplicacao() {
		return descricaoCentroCustoAplicacao;
	}



	public void setDescricaoCentroCustoAplicacao(
			String descricaoCentroCustoAplicacao) {
		this.descricaoCentroCustoAplicacao = descricaoCentroCustoAplicacao;
	}



	public Short getSeqAlmoxarifado() {
		return seqAlmoxarifado;
	}



	public void setSeqAlmoxarifado(Short seqAlmoxarifado) {
		this.seqAlmoxarifado = seqAlmoxarifado;
	}



	public String getDescricaoAlmoxarifado() {
		return descricaoAlmoxarifado;
	}



	public void setDescricaoAlmoxarifado(String descricaoAlmoxarifado) {
		this.descricaoAlmoxarifado = descricaoAlmoxarifado;
	}



	public DominioSituacao getSituacao() {
		return situacao;
	}



	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}



	public String getDescricao() {
		return descricao;
	}



	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public void setNumero(Integer numero) {
		this.numero = numero;
	}



	public Integer getNumero() {
		return numero;
	}


	public enum Fields{
		
		CODIGO_CENTRO_CUSTO_PROPRIETARIO("codigoCentroCustoProprietario"),
		DESCRICAO_CENTRO_CUSTO_PROPRIETARIO("descricaoCentroCustoProprietario"),
		CODIGO_CENTRO_CUSTO_APLICACAO("codigoCentroCustoAplicacao"),
		DESCRICAO_CENTRO_CUSTO_APLICACAO("descricaoCentroCustoAplicacao"),
		CODIGO_ALMOXARIFADO("seqAlmoxarifado"),
		DESCRICAO_ALMOXARIFADO("descricaoAlmoxarifado"),
		SITUACAO("situacao"),
		DESCRICAO("descricao"),
		NUMERO("numero");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
}
