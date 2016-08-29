package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;


public class SolCompraVO implements BaseBean {

	private static final long serialVersionUID = 6458323172920114348L;
	private Integer numero;
	private Short pontoParadaAtual;
	private Short pontoParada;
	private Boolean urgente;
	private Boolean prioridade;
	private Boolean exclusao;
	private Boolean devolucao;
	private Date dataSolicitacao;
	private Date dataAutorizacao;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private Long qtdSolicitada;
	private Long qtdAprovada;
	private String codigoUnidadeMedida;
	private BigDecimal valorUnitPrevisto;
	private String descricaoPontoParada;
	private Integer codigoCc;
	private String descricaoCc;
	private Integer codigoCcAplicacao;
	private String descricaoCcAplicacao;
	private Integer qtdAnexo;
	private String nomeSolicitante;
	private String descricaoMaterial;
	private String descricaoSc;
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
		SolCompraVO other = (SolCompraVO) obj;
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
		DT_AUTORIZACAO("dataAutorizacao"),
		MAT_CODIGO("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		QTD_SOLICITADA("qtdSolicitada"),
		QTD_APROVADA("qtdAprovada"),
		COD_UNIDADE_MEDIDA("codigoUnidadeMedida"),
		VLR_UNITARIO_PREVISTO("valorUnitPrevisto"),
		DESCRICAO_PONTO_PARADA("descricaoPontoParada"),
		CODIGO_CC("codigoCc"),
		DESCRICAO_CC("descricaoCc"),
		CODIGO_CC_APLICACAO("codigoCcAplicacao"),
		QTDE_ANEXO("qtdAnexo"),
		DEVOLVIDA("devolvida"),
		DESCRICAO_CC_APLICACAO("descricaoCcAplicacao"),
		NOME_SOLICITANTE("nomeSolicitante"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		DESCRICAO_SC("descricaoSc"),
		RAMAL("ramal");

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

	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}

	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public Long getQtdSolicitada() {
		return qtdSolicitada;
	}

	public void setQtdSolicitada(Long qtdSolicitada) {
		this.qtdSolicitada = qtdSolicitada;
	}

	public Long getQtdAprovada() {
		return qtdAprovada;
	}

	public void setQtdAprovada(Long qtdAprovada) {
		this.qtdAprovada = qtdAprovada;
	}

	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}

	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}

	public BigDecimal getValorUnitPrevisto() {
		return valorUnitPrevisto;
	}

	public void setValorUnitPrevisto(BigDecimal valorUnitPrevisto) {
		this.valorUnitPrevisto = valorUnitPrevisto;
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

	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	public String getDescricaoSc() {
		return descricaoSc;
	}

	public void setDescricaoSc(String descricaoSc) {
		this.descricaoSc = descricaoSc;
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