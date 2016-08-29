package br.gov.mec.aghu.compras.pac.vo;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;

public class PreItemPacVO {

	private Integer numero;
	private Integer codMatOuServ;
	private String nomeMatOuServ;
	private boolean indUrgente;
	private Boolean estocavel;
	private Long qtdSC;
	private Integer qtdSS;
	private BigDecimal valorUnitPrevisto;
	private FccCentroCustos ccSolicitante;
	private FccCentroCustos ccAplicacao;
	private ScoPontoParadaSolicitacao caixa;
	
	private Integer ccSolicitanteCod;
	private String ccSolicitanteDesc;
	
	private Integer ccAplicacaoCod;
	private String ccAplicacaoDesc;
	
	private Short caixaCod;
	private String caixaDesc;
	
	private boolean marked;
	private Integer solAssociada;
	private String msgExcluidas;
	private String msgNAutorizadas;
	private String msgAssociadas;
	private String msgTituloAvulso;
	
	
	
	public String getMsgTituloAvulso() {
		return msgTituloAvulso;
	}

	public void setMsgTituloAvulso(String msgTituloAvulso) {
		this.msgTituloAvulso = msgTituloAvulso;
	}

	public PreItemPacVO() {
	}

	public Integer getNumero() {
		return numero;
	}

	public Integer getCodMatOuServ() {
		return codMatOuServ;
	}

	public String getNomeMatOuServ() {
		return nomeMatOuServ;
	}

	public boolean isIndUrgente() {
		return indUrgente;
	}

	public DominioSimNao isIndEstocavel() {
		return DominioSimNao.getInstance(estocavel);
	}

	public BigDecimal getValorUnitPrevisto() {
		return valorUnitPrevisto;
	}

	public FccCentroCustos getCcSolicitante() {
		return ccSolicitante;
	}

	public FccCentroCustos getCcAplicacao() {
		return ccAplicacao;
	}

	public ScoPontoParadaSolicitacao getCaixa() {
		return caixa;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public void setCodMatOuServ(Integer codMatOuServ) {
		this.codMatOuServ = codMatOuServ;
	}

	public void setNomeMatOuServ(String nomeMatOuServ) {
		this.nomeMatOuServ = nomeMatOuServ;
	}

	public void setIndUrgente(boolean indUrgente) {
		this.indUrgente = indUrgente;
	}

	public void setValorUnitPrevisto(BigDecimal valorUnitPrevisto) {
		this.valorUnitPrevisto = valorUnitPrevisto;
	}

	public void setCcSolicitante(FccCentroCustos ccSolicitante) {
		this.ccSolicitante = ccSolicitante;
	}

	public void setCcAplicacao(FccCentroCustos ccAplicacao) {
		this.ccAplicacao = ccAplicacao;
	}

	public void setCaixa(ScoPontoParadaSolicitacao caixa) {
		this.caixa = caixa;
	}

	
	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public Boolean getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(Boolean estocavel) {
		this.estocavel = estocavel;
	}

	public enum Fields {
		
		NUMERO("numero"),
		COD_MAT_SERV("codMatOuServ"),
		NOME_MAT_SERV("nomeMatOuServ"), 
		IND_URGENTE("indUrgente"),
		IND_ESTOCAVEL("estocavel"),
		QTD_SC("qtdSC"),
		QTD_SS("qtdSS"),
		VALOR_UNIT_PREVISTO("valorUnitPrevisto"),
		CC_SOLICITANTE("ccSolicitante"),
		CC_APLICACAO("ccAplicacao"),
		CAIXA("caixa"),
		ASSOCIADA("solAssociada"), 
		CC_SOLICITANTE_COD("ccSolicitanteCod"),
		CC_SOLICITANTE_DESC("ccSolicitanteDesc"),
		CC_APLICACAO_COD("ccAplicacaoCod"),
		CC_APLICACAO_DESC("ccAplicacaoDesc"),
		CAIXA_COD("caixaCod"),
		CAIXA_DESC("caixaDesc");		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof PreItemPacVO){
			PreItemPacVO castOther = (PreItemPacVO) other;
			return new EqualsBuilder()
				.append(this.numero, castOther.getNumero())
			.isEquals();
		}
		else {
			return false;
		}	
	}
	
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.numero).toHashCode();
	}


		public DominioSimNao getIndEstocavel() {
			return isIndEstocavel();
		}
		
		public Long getQtdSC() {
			return qtdSC;
		}
		
		public Integer getQtdSS() {
			return qtdSS;
		}
		
		public void setQtdSC(Long qtdSC) {
			this.qtdSC = qtdSC;
		}
		
		public void setQtdSS(Integer qtdSS) {
			this.qtdSS = qtdSS;
		}
		
		public Integer getSolAssociada() {
			return solAssociada;
		}
		
		public void setSolAssociada(Integer solAssociada) {
			this.solAssociada = solAssociada;
		}

		public String getMsgExcluidas() {
			return msgExcluidas;
		}

		public String getMsgNAutorizadas() {
			return msgNAutorizadas;
		}

		public String getMsgAssociadas() {
			return msgAssociadas;
		}

		public void setMsgExcluidas(String msgExcluidas) {
			this.msgExcluidas = msgExcluidas;
		}

		public void setMsgNAutorizadas(String msgNAutorizadas) {
			this.msgNAutorizadas = msgNAutorizadas;
		}

		public void setMsgAssociadas(String msgAssociadas) {
			this.msgAssociadas = msgAssociadas;
		}

		public Integer getCcSolicitanteCod() {
			return ccSolicitanteCod;
		}

		public String getCcSolicitanteDesc() {
			return ccSolicitanteDesc;
		}

		public Integer getCcAplicacaoCod() {
			return ccAplicacaoCod;
		}

		public String getCcAplicacaoDesc() {
			return ccAplicacaoDesc;
		}

		public Short getCaixaCod() {
			return caixaCod;
		}

		public String getCaixaDesc() {
			return caixaDesc;
		}

		public void setCcSolicitanteCod(Integer ccSolicitanteCod) {
			this.ccSolicitanteCod = ccSolicitanteCod;
		}

		public void setCcSolicitanteDesc(String ccSolicitanteDesc) {
			this.ccSolicitanteDesc = ccSolicitanteDesc;
		}

		public void setCcAplicacaoCod(Integer ccAplicacaoCod) {
			this.ccAplicacaoCod = ccAplicacaoCod;
		}

		public void setCcAplicacaoDesc(String ccAplicacaoDesc) {
			this.ccAplicacaoDesc = ccAplicacaoDesc;
		}

		public void setCaixaCod(Short caixaCod) {
			this.caixaCod = caixaCod;
		}

		public void setCaixaDesc(String caixaDesc) {
			this.caixaDesc = caixaDesc;
		}

		
		
	
	
}
