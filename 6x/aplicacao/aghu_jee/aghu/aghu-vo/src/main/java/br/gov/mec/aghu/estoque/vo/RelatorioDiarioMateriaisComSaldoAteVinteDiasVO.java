/**
 * 
 */
package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.core.utils.DateFormatUtil;




public class RelatorioDiarioMateriaisComSaldoAteVinteDiasVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -820993126499534899L;

	public enum Fields{
		MAT_CODIGO("codigoMaterial"),
		MAT_NOME("nomeMaterial"),
		EGR_CLASSIF_ABC("classificacaoAbc"),
		EGR_SUB_CLASSIF_ABC("subClassificacaoAbc"),
		EAL_QTDE_DISPONIVEL("ealQtdeDisponivel"),
		EAL_QTDE_PONTO_PEDIDO("quantidadePontoPedido"),
		EAL_ALM_SEQ("almSeq"),
		EAL_TEMPO_REPOSICAO("tempoReposicao"),
		EGR_QTDE("egrQtde"),
		MAT_GMT_CODIGO("gmtCodigo"), 
		SOLICITACAO_COMPRA_NUMERO("numeroSolicitacaoCompra"),
		DATA_SOLICITACAO("dataSolicitacao"),
		NUMERO_LICITACAO("numeroLicitacao"),
		DATA_LICITACAO("dataLicitacao"),
		NUMERO_ITEM_LICITACAO("numeroItemLicitacao"),
		NUMERO_ITEM_AUTORIZACAO("numeroItemAutorizacao"),
		AFN_NUMERO_COMPLEMENTO("nroComplemento"),
		QTDE_RECEBIDA("quantidadeRecebida"),
		DATA_PREVISAO_ENTREGA("dataPrevisaoEntrega"),
		DATA_ALTERACAO("dtAlteracao"),
		IND_SITUACAO_AUTORIZACAO_FORN("indSituacao"),
		RAZAO_SOCIAL("razaoSocial"),
		IAF_QUANTIDADE_SOLICITADA("iafQuantidadeSolicitada");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	/*Campos do grupo principal (Cabeçalho do relatório)*/
	private String nomeMaterial;
	private Integer codigoMaterial;
	private DominioClassifABC classificacaoAbc;
	private DominioClassifABC subClassificacaoAbc;
	private Integer ealQtdeDisponivel;
	private Integer quantidadeDisponivel;
	private Integer quantidadePontoPedido;
	private Short almSeq;
	private Integer tempoReposicao;
	private Integer egrQtde;
	private Integer gmtCodigo;
	private Integer durQuantidadeDisponivel;
	private Integer quantidadeEstoque;
	private Integer durEstoque;
	private Integer numeroSolicitacaoCompra;
	private Date dataSolicitacao;
	private Date ultimaDataGeracaoMovMaterial;
	private Integer numeroLicitacao;
	private Date dataLicitacao;
	private Short numeroItemLicitacao;
	private Integer numeroItemAutorizacao;
	private Short nroComplemento;
	private String concatenaNumLicitacaoComNroComplemento;
	private Long quantidadeSolicitada;
	private Integer quantidadeRecebida;
	private Integer iafQuantidadeSolicitada;
	private Date dataPrevisaoEntrega;
	private Date dtAlteracao;
	private DominioSituacaoAutorizacaoFornecimento indSituacao;
	private String razaoSocial;
	
	// atributo utilizado como controle para report.
	// é setado true caso o objeto anterior da lista tenha o mesmo codigoMaterial do objeto avaliado.
	private Boolean materialRepetido;
	
	public Integer getEalQtdeDisponivel() {
		return ealQtdeDisponivel;
	}

	public void setEalQtdeDisponivel(Integer qtdeDisponivel) {
		this.ealQtdeDisponivel = qtdeDisponivel;
	}

	public Integer getQuantidadePontoPedido() {
		if(quantidadePontoPedido == null){
			return 0;
		}
		return quantidadePontoPedido;
	}

	public void setQuantidadePontoPedido(Integer qtdePontoPedido) {
		this.quantidadePontoPedido = qtdePontoPedido;
	}
	
	public Short getAlmSeq() {
		return almSeq;
	}

	public void setAlmSeq(Short almSeq) {
		this.almSeq = almSeq;
	}

	public Integer getTempoReposicao() {
		return tempoReposicao;
	}

	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}

	public Integer getGmtCodigo() {
		return gmtCodigo;
	}

	public void setGmtCodigo(Integer gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}

	public Integer getEgrQtde() {
		return egrQtde;
	}

	public void setEgrQtde(Integer egrQtde) {
		this.egrQtde = egrQtde;
	}
 
	public DominioClassifABC getClassificacaoAbc() {
		return classificacaoAbc;
	}

	public void setClassificacaoAbc(DominioClassifABC classificacaoAbc) {
		this.classificacaoAbc = classificacaoAbc;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer matCodigo) {
		this.codigoMaterial = matCodigo;
	}

	public DominioClassifABC getSubClassificacaoAbc() {
		return subClassificacaoAbc;
	}

	public void setSubClassificacaoAbc(DominioClassifABC subClassificacaoAbc) {
		this.subClassificacaoAbc = subClassificacaoAbc;
	}

 
	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
		this.quantidadeDisponivel = quantidadeDisponivel;
	}

	public Integer getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}
	public void setDurQuantidadeDisponivel(Integer durQtDisponivel) {
		this.durQuantidadeDisponivel = durQtDisponivel;
	}

	public Integer getDurQuantidadeDisponivel() {
		return durQuantidadeDisponivel;
	}

	public void setDurEstoque(Integer durEstoque) {
		this.durEstoque = durEstoque;
	}

	public Integer getDurEstoque() {
		return durEstoque;
	}

	public void setNumeroSolicitacaoCompra(Integer slcNumero) {
		this.numeroSolicitacaoCompra = slcNumero;
	}

	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dtSolicitacao) {
		this.dataSolicitacao = dtSolicitacao;
	}

	public Date getUltimaDataGeracaoMovMaterial() {		
		return ultimaDataGeracaoMovMaterial;
	}

	public void setUltimaDataGeracaoMovMaterial(Date ultimaDataGeracaoMovMaterial) {
		this.ultimaDataGeracaoMovMaterial = ultimaDataGeracaoMovMaterial;
	}

	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}

	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}

	public Date getDataLicitacao() {
		return dataLicitacao;
	}

	public void setDataLicitacao(Date lctDtAberturaProposta) {
		this.dataLicitacao = lctDtAberturaProposta;
	}
	public Short getNumeroItemLicitacao() {
		return numeroItemLicitacao;
	}

	public void setNumeroItemLicitacao(Short numeroItemLicitacao) {
		this.numeroItemLicitacao = numeroItemLicitacao;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public String getConcatenaNumLicitacaoComNroComplemento() {
		if(getNumeroLicitacao() != null && getNroComplemento() != null) {
			return getNumeroLicitacao() + "/" + getNroComplemento();
		}
		return concatenaNumLicitacaoComNroComplemento;
	}

	public void setConcatenaNumLicitacaoComNroComplemento(String concatenaNumLicitacaoComNroComplemento) {
		this.concatenaNumLicitacaoComNroComplemento = concatenaNumLicitacaoComNroComplemento;
	}
	public Long getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}

	public void setQuantidadeSolicitada(Long quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}
	public Integer getQuantidadeRecebida() {
		return quantidadeRecebida;
	}

	public void setQuantidadeRecebida(Integer quantidadeRecebida) {
		this.quantidadeRecebida = quantidadeRecebida;
	}
	
	
	public Integer getSaldo() {
		if(getIafQuantidadeSolicitada() != null && getQuantidadeRecebida() != null) {
			return getIafQuantidadeSolicitada() - getQuantidadeRecebida();
		} else if (getIafQuantidadeSolicitada() != null) {
			return getIafQuantidadeSolicitada();
		}
		return 0;
	}
	
	public Date getDataPrevisaoEntrega() {
		return dataPrevisaoEntrega;
	}

	public void setDataPrevisaoEntrega(Date dtPrevEntrega) {
		this.dataPrevisaoEntrega = dtPrevEntrega;
	}
	
	public Date getDtAlteracao() {
		return dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}
	public DominioSituacaoAutorizacaoFornecimento getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoAutorizacaoFornecimento indSituacao) {
		this.indSituacao = indSituacao;
	}

	
	public String getIndSituacaoString() {
		StringBuilder sb = new StringBuilder("");
		if (getIndSituacao() != null) {
			sb.append(getIndSituacao().toString());
		}
		return sb.toString();
	}
		
	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Integer getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(Integer qtEstoque) {
		this.quantidadeEstoque = qtEstoque;
	}

	public String getClassificacaoAbcString() {
		StringBuilder sb = new StringBuilder();
		if(getClassificacaoAbc() != null) {
			sb.append(getClassificacaoAbc().getDescricao());
		}
		if (getSubClassificacaoAbc() != null) {
			sb.append(' ').append(getSubClassificacaoAbc().getDescricao());
		}
		return sb.toString();
	}

	public String getDataLicitacaoFormatada() {
		if(getDataLicitacao() != null) {
			return DateFormatUtil.formataDiaMes(getDataLicitacao());
		}
		return "";
	}

	public String getUltimaDataGeracaoMovMaterialFormatada() {
		if(getUltimaDataGeracaoMovMaterial() != null) {
			return DateFormatUtil.formataDiaMes(getUltimaDataGeracaoMovMaterial());
		}
		return "";
	}
	public String getDataSolicitacaoFormatada() {
		if(getDataSolicitacao() != null) {
			return DateFormatUtil.formataDiaMesAnoDoisDigitos(getDataSolicitacao());
		}
		return "";
	}

	
	public String getDataPrevisaoEntregaFormatada() {
		if(getDataPrevisaoEntrega() != null) {
			return DateFormatUtil.formataDiaMes(getDataPrevisaoEntrega());
		}
		return "";
	}

 
	public Integer getIafQuantidadeSolicitada() {
		return iafQuantidadeSolicitada;
	}

	public void setIafQuantidadeSolicitada(Integer iafQuantidadeSolicitada) {
		this.iafQuantidadeSolicitada = iafQuantidadeSolicitada;
	}
	public Integer getNumeroItemAutorizacao() {
		return numeroItemAutorizacao;
	}

	public void setNumeroItemAutorizacao(Integer numeroItemAutorizacao) {
		this.numeroItemAutorizacao = numeroItemAutorizacao;
	}
	
	public Short getNumeroItem() {
		if (getNumeroItemLicitacao() != null) {
			return getNumeroItemLicitacao();
		} else if (getNumeroItemAutorizacao() != null) {
			return getNumeroItemAutorizacao().shortValue();
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gmtCodigo == null) ? 0 : gmtCodigo.hashCode());
		result = prime * result + ((almSeq == null) ? 0 : almSeq.hashCode());
		result = prime * result + ((codigoMaterial == null) ? 0 : codigoMaterial.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RelatorioDiarioMateriaisComSaldoAteVinteDiasVO other = (RelatorioDiarioMateriaisComSaldoAteVinteDiasVO) obj;
		if (gmtCodigo == null) {
			if (other.gmtCodigo != null) {
				return false;
			}
		} else if (!gmtCodigo.equals(other.gmtCodigo)) {
			return false;
		}
		if (almSeq == null) {
			if (other.almSeq != null) {
				return false;
			}
		} else if (!almSeq.equals(other.almSeq)) {
			return false;
		}
		if (codigoMaterial == null) {
			if (other.codigoMaterial != null) {
				return false;
			}
		} else if (!codigoMaterial.equals(other.codigoMaterial)) {
			return false;
		}
		return true;
	}

	public Boolean getMaterialRepetido() {
		return materialRepetido;
	}

	public void setMaterialRepetido(Boolean materialRepetido) {
		this.materialRepetido = materialRepetido;
	}
	
}