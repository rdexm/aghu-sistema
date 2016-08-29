package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioTipoImpressaoMapa;

public class ImprimeEtiquetaRedomeVO implements Serializable{

	private DominioTipoImpressaoMapa tipoImpressao;
	private Integer numeroVezesImpressao;
	private String sigla;
	private String codigoDoador;
	private String dataColetaDoador;
	private String numeroSolicitacao;
	private String numeroAmostra;
	private String codBarras;

	public DominioTipoImpressaoMapa getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(DominioTipoImpressaoMapa tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getCodigoDoador() {
		return codigoDoador;
	}

	public void setCodigoDoador(String codigoDoador) {
		this.codigoDoador = codigoDoador;
	}

	public String getDataColetaDoador() {
		return dataColetaDoador;
	}

	public void setDataColetaDoador(String dataColetaDoador) {
		this.dataColetaDoador = dataColetaDoador;
	}

	public String getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(String numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public String getNumeroAmostra() {
		return numeroAmostra;
	}

	public void setNumeroAmostra(String numeroAmostra) {
		this.numeroAmostra = numeroAmostra;
	}

	public String getCodBarras() {
		return codBarras;
	}

	public void setCodBarras(String codBarras) {
		this.codBarras = codBarras;
	}

	public Integer getNumeroVezesImpressao() {
		return numeroVezesImpressao;
	}

	public void setNumeroVezesImpressao(Integer numeroVezesImpressao) {
		this.numeroVezesImpressao = numeroVezesImpressao;
	}
}