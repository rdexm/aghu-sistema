package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAndamentoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoConsultaAssinarAF;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoFiltroAutorizacaoFornecimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;

public class PesquisaAutorizacaoFornecimentoVO implements Serializable {

	private static final long serialVersionUID = -101869066447880475L;
	
	private Integer afnNumero;
	private Integer lctNumero;
	private Short numeroComplemento;
	private DominioSituacaoAutorizacaoFornecimento situacaoAf;
	private DominioAndamentoAutorizacaoFornecimento andamentoAf;
	private ScoFornecedor fornecedor;
	private ScoPropostaFornecedor propostaFornecedor;
	private ScoModalidadeLicitacao modalidadeCompra;
	private RapServidores servidorGestor;
	private DominioTipoFiltroAutorizacaoFornecimento tipoFiltroAf;
	private Integer codigoFiltroAf;
	private Integer numeroContrato;
	private Date dataInicioContrato;
	private Date dataFimContrato;
	private DominioSimNao pendente;
	private DominioSimNao vencida;
	
	private DominioTipoConsultaAssinarAF tipoConsultaAssinarAF;
	private String observacao;
	private Short sequenciaAlteracao;
	private Date dtAlteracao;
	private String descricaoMotivoAlteracao; // ScoAutorizacaoForn.ScoMotivoAlteracaoAf.descricao
	private Integer numeroAFP;
	private DominioTipoFaseSolicitacao dominioTipoFaseSolicitacao;
	// DominioTipoFaseSolicitacao.S -> "S"; DominioTipoFaseSolicitacao.C -> "M";
	// Senão "CUM"
	private String origem;
	// DominioTipoFaseSolicitacao.S -> "Serviço"; DominioTipoFaseSolicitacao.C
	// -> "Material"; Senão "Comunicação Uso Material"
	private String hintOrigem;
	private Date dtGeracao;
	private Date dtPrevEntrega;
	private Date dtVenctoContrato;
	private Double valorReforco;
	private Double valorTotal;
	private Double saldoOrcamento;
	private Integer codigoGrupoNatureza;
	private ScoCondicaoPagamentoPropos condicaoPagamentoPropos;
	
	private Boolean selecionado;
	private Boolean objContrato;

	private Integer qtdCum;
	private String razaoSocial;
	private String cnpjCpfFornecedor;
	private Integer matriculaServidorGestor;
	private Short vinculoServidorGestor;
	private String nomeServidorGestor;
	private String loginServidorGestor;
	private String dtAlteracaoFormatada;
	private String descricaoSituacaoAf;
	private String tipoSolicitacao;
	private BigDecimal vlrOrcamento;
	private BigDecimal vlrComprometido;
	private Long seqAfJn;
	private Integer maxNumeroAfp;
	private BigDecimal vlrTotalAfp;
	
	private Short sequenciaAlteracaoAf;
	private DominioSituacaoAutorizacaoFornecimento situacaoAfjnAf;
	private Integer cdpNumero;
	private Integer existeContratoSicon;
	
	private Boolean publicaCUM = false;
	
	public enum Fields {
		SEQ_AF_JN("seqAfJn"),
		MAX_NUMERO_AFP("maxNumeroAfp"),
		VINCULO_SERVIDOR_GESTOR("vinculoServidorGestor"),
		RAZAO_SOCIAL("razaoSocial"),
		CNPJ_CPF_FORN("cnpjCpfFornecedor"),
		VALOR_TOTAL_AFP("vlrTotalAfp"),
		MATRICULA_GESTOR("matriculaServidorGestor"),
		NOME_GESTOR("nomeServidorGestor"),
		LOGIN_GESTOR("loginServidorGestor"),
		VALOR_TOTAL("valorTotal"),
		VALOR_REFORCO("valorReforco"),
		SALDO_ORCAMENTO("saldoOrcamento"),
		DT_PREV_ENTREGA("dtPrevEntrega"),
		HINT_ORIGEM("hintOrigem"),
		AFN_NUMERO("afnNumero"),
		LCT_NUMERO("lctNumero"),
		NUMERO_COMPLEMENTO("numeroComplemento"),
		DT_VENCIMENTO_CONTRATO("dtVenctoContrato"),
		NUMERO_CONTRATO("numeroContrato"),
		OBSERVACAO("observacao"),
		SEQUENCIA_ALTERACAO("sequenciaAlteracao"),
		NUMERO_AFP("numeroAFP"),
		DATA_ALTERACAO_FORMATADA("dtAlteracaoFormatada"),
		DESCRICAO_MOTIVO_ALTERACAO("descricaoMotivoAlteracao"),
		SITUACAO_AF("situacaoAf"),
		DESCRICAO_SITUACAO_AF("descricaoSituacaoAf"),
		DT_GERACAO("dtGeracao"),
		EXISTE_CONTRATO_SICON("existeContratoSicon"),
		CODIGO_GRUPO_NATUREZA("codigoGrupoNatureza"),
		TIPO_CONSULTAR_ASSINAR_AF("tipoConsultaAssinarAF"),
		QTD_CUM("qtdCum"),
		TIPO_SOLICITACAO("tipoSolicitacao"),
		VLR_ORCAMENTO("vlrOrcamento"),
		DT_ALTERACAO("dtAlteracao"),
		VLR_COMPROMETIDO("vlrComprometido"),
		SEQUENCIA_ALTERACAO_AF("sequenciaAlteracaoAf"),
		SITUACAO_AFJN_AF("situacaoAfjnAf"),
		CDP_NUMERO("cdpNumero"),
		FORNECEDOR("fornecedor"),
		PROPOSTA_FORNECEDOR("propostaFornecedor");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	
	public PesquisaAutorizacaoFornecimentoVO() {
		
	}
	
	public boolean isAfp() {
		return DominioTipoConsultaAssinarAF.AFP.equals(this.getTipoConsultaAssinarAF());
	}

	public boolean valorMaiorSaldoOrcamento() {
		if(this.getValorTotal() != null && this.getSaldoOrcamento() != null && 
				this.getValorTotal()>this.getSaldoOrcamento()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasDataContrato() {
		return dtVenctoContrato != null && dtVenctoContrato.before(new Date());
	}
	
	public Short getNumeroComplemento() {
		return numeroComplemento;
	}
	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}
	public DominioSituacaoAutorizacaoFornecimento getSituacaoAf() {
		return situacaoAf;
	}
	public void setSituacaoAf(DominioSituacaoAutorizacaoFornecimento situacaoAf) {
		this.situacaoAf = situacaoAf;
	}
	public DominioAndamentoAutorizacaoFornecimento getAndamentoAf() {
		return andamentoAf;
	}
	public void setAndamentoAf(DominioAndamentoAutorizacaoFornecimento andamentoAf) {
		this.andamentoAf = andamentoAf;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}
	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}
	public RapServidores getServidorGestor() {
		return servidorGestor;
	}
	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}
	public DominioTipoFiltroAutorizacaoFornecimento getTipoFiltroAf() {
		return tipoFiltroAf;
	}
	public void setTipoFiltroAf(
			DominioTipoFiltroAutorizacaoFornecimento tipoFiltroAf) {
		this.tipoFiltroAf = tipoFiltroAf;
	}
	public Integer getCodigoFiltroAf() {
		return codigoFiltroAf;
	}
	public void setCodigoFiltroAf(Integer codigoFiltroAf) {
		this.codigoFiltroAf = codigoFiltroAf;
	}
	public Integer getNumeroContrato() {
		return numeroContrato;
	}
	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
	public Date getDataInicioContrato() {
		return dataInicioContrato;
	}
	public void setDataInicioContrato(Date dataInicioContrato) {
		this.dataInicioContrato = dataInicioContrato;
	}
	public Date getDataFimContrato() {
		return dataFimContrato;
	}
	public void setDataFimContrato(Date dataFimContrato) {
		this.dataFimContrato = dataFimContrato;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Short getSequenciaAlteracao() {
		return sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Short sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public Date getDtAlteracao() {
		return dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	public String getDescricaoMotivoAlteracao() {
		return descricaoMotivoAlteracao;
	}

	public void setDescricaoMotivoAlteracao(String descricaoMotivoAlteracao) {
		this.descricaoMotivoAlteracao = descricaoMotivoAlteracao;
	}

	public Integer getNumeroAFP() {
		return numeroAFP;
	}

	public void setNumeroAFP(Integer numeroAFP) {
		this.numeroAFP = numeroAFP;
	}

	public DominioTipoFaseSolicitacao getDominioTipoFaseSolicitacao() {
		return dominioTipoFaseSolicitacao;
	}

	public void setDominioTipoFaseSolicitacao(DominioTipoFaseSolicitacao dominioTipoFaseSolicitacao) {
		this.dominioTipoFaseSolicitacao = dominioTipoFaseSolicitacao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getHintOrigem() {
		return hintOrigem;
	}

	public void setHintOrigem(String hintOrigem) {
		this.hintOrigem = hintOrigem;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}

	public Date getDtVenctoContrato() {
		return dtVenctoContrato;
	}

	public void setDtVenctoContrato(Date dtVenctoContrato) {
		this.dtVenctoContrato = dtVenctoContrato;
	}

	public Double getValorReforco() {
		return valorReforco;
	}

	public void setValorReforco(Double valorReforco) {
		this.valorReforco = valorReforco;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Double getSaldoOrcamento() {
		return saldoOrcamento;
	}

	public void setSaldoOrcamento(Double saldoOrcamento) {
		this.saldoOrcamento = saldoOrcamento;
	}

	public Integer getCodigoGrupoNatureza() {
		return codigoGrupoNatureza;
	}

	public void setCodigoGrupoNatureza(Integer codigoGrupoNatureza) {
		this.codigoGrupoNatureza = codigoGrupoNatureza;
	}

	public void setTipoConsultaAssinarAF(DominioTipoConsultaAssinarAF tipoConsultaAssinarAF) {
		this.tipoConsultaAssinarAF = tipoConsultaAssinarAF;
	}

	public DominioTipoConsultaAssinarAF getTipoConsultaAssinarAF() {
		return tipoConsultaAssinarAF;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	public ScoCondicaoPagamentoPropos getCondicaoPagamentoPropos() {
		return condicaoPagamentoPropos;
	}

	public void setCondicaoPagamentoPropos(
			ScoCondicaoPagamentoPropos condicaoPagamentoPropos) {
		this.condicaoPagamentoPropos = condicaoPagamentoPropos;
	}

	public ScoPropostaFornecedor getPropostaFornecedor() {
		return propostaFornecedor;
	}

	public void setPropostaFornecedor(ScoPropostaFornecedor propostaFornecedor) {
		this.propostaFornecedor = propostaFornecedor;
	}
	
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lctNumero == null) ? 0 : lctNumero.hashCode());
		result = prime
				* result
				+ ((numeroComplemento == null) ? 0 : numeroComplemento
						.hashCode());
		result = prime
				* result
				+ ((sequenciaAlteracao == null) ? 0 : sequenciaAlteracao
						.hashCode());
		result = prime
				* result
				+ ((numeroAFP == null) ? 0 : numeroAFP
						.hashCode());
		
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
		PesquisaAutorizacaoFornecimentoVO other = (PesquisaAutorizacaoFornecimentoVO) obj;
		if (lctNumero == null) {
			if (other.lctNumero != null){
				return false;
			}
		} else if (!lctNumero.equals(other.lctNumero)){
			return false;
		}
		if (numeroComplemento == null) {
			if (other.numeroComplemento != null){
				return false;
			}
		} else if (!numeroComplemento.equals(other.numeroComplemento)){
			return false;
		}
		if (sequenciaAlteracao == null) {
			if (other.sequenciaAlteracao != null){
				return false;
			}
		} else if (!sequenciaAlteracao.equals(other.sequenciaAlteracao)){
			return false;
		}
				
		return this.equalsNumeroAFP(other);
		
	}

	public boolean equalsNumeroAFP(PesquisaAutorizacaoFornecimentoVO other){
		if (numeroAFP == null) {
			if (other.numeroAFP != null){
				return false;
			}
		} else if (!numeroAFP.equals(other.numeroAFP)){
			return false;
		}
		return true;
	}
	
	public Boolean getObjContrato() {
			return objContrato;
	}

	
	public void setObjContrato(Boolean objContrato) {
			this.objContrato = objContrato;
	}

	public DominioSimNao getPendente() {
		return pendente;
	}

	public void setPendente(DominioSimNao pendente) {
		this.pendente = pendente;
	}

	public DominioSimNao getVencida() {
		return vencida;
	}

	public void setVencida(DominioSimNao vencida) {
		this.vencida = vencida;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getCnpjCpfFornecedor() {
		return cnpjCpfFornecedor;
	}

	public void setCnpjCpfFornecedor(String cnpjCpfFornecedor) {
		this.cnpjCpfFornecedor = cnpjCpfFornecedor;
	}

	public Integer getMatriculaServidorGestor() {
		return matriculaServidorGestor;
	}

	public void setMatriculaServidorGestor(Integer matriculaServidorGestor) {
		this.matriculaServidorGestor = matriculaServidorGestor;
	}

	public String getNomeServidorGestor() {
		return nomeServidorGestor;
	}

	public void setNomeServidorGestor(String nomeServidorGestor) {
		this.nomeServidorGestor = nomeServidorGestor;
	}

	public String getLoginServidorGestor() {
		return loginServidorGestor;
	}

	public void setLoginServidorGestor(String loginServidorGestor) {
		this.loginServidorGestor = loginServidorGestor;
	}

	

	public String getDescricaoSituacaoAf() {
		return descricaoSituacaoAf;
	}

	public void setDescricaoSituacaoAf(String descricaoSituacaoAf) {
		this.descricaoSituacaoAf = descricaoSituacaoAf;
	}

	public String getDtAlteracaoFormatada() {
		return dtAlteracaoFormatada;
	}

	public void setDtAlteracaoFormatada(String dtAlteracaoFormatada) {
		this.dtAlteracaoFormatada = dtAlteracaoFormatada;
	}

	public String getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(String tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public Integer getQtdCum() {
		return qtdCum;
	}

	public void setQtdCum(Integer qtdCum) {
		this.qtdCum = qtdCum;
	}

	public BigDecimal getVlrOrcamento() {
		return vlrOrcamento;
	}

	public void setVlrOrcamento(BigDecimal vlrOrcamento) {
		this.vlrOrcamento = vlrOrcamento;
	}

	public BigDecimal getVlrComprometido() {
		return vlrComprometido;
	}

	public void setVlrComprometido(BigDecimal vlrComprometido) {
		this.vlrComprometido = vlrComprometido;
	}

	public Long getSeqAfJn() {
		return seqAfJn;
	}

	public void setSeqAfJn(Long seqAfJn) {
		this.seqAfJn = seqAfJn;
	}

	public Integer getMaxNumeroAfp() {
		return maxNumeroAfp;
	}

	public void setMaxNumeroAfp(Integer maxNumeroAfp) {
		this.maxNumeroAfp = maxNumeroAfp;
	}

	public BigDecimal getVlrTotalAfp() {
		return vlrTotalAfp;
	}

	public void setVlrTotalAfp(BigDecimal vlrTotalAfp) {
		this.vlrTotalAfp = vlrTotalAfp;
	}

	public Short getVinculoServidorGestor() {
		return vinculoServidorGestor;
	}

	public void setVinculoServidorGestor(Short vinculoServidorGestor) {
		this.vinculoServidorGestor = vinculoServidorGestor;
	}

	public Short getSequenciaAlteracaoAf() {
		return sequenciaAlteracaoAf;
	}

	public void setSequenciaAlteracaoAf(Short sequenciaAlteracaoAf) {
		this.sequenciaAlteracaoAf = sequenciaAlteracaoAf;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacaoAfjnAf() {
		return situacaoAfjnAf;
	}

	public void setSituacaoAfjnAf(DominioSituacaoAutorizacaoFornecimento situacaoAfjnAf) {
		this.situacaoAfjnAf = situacaoAfjnAf;
	}

	public Integer getCdpNumero() {
		return cdpNumero;
	}

	public void setCdpNumero(Integer cdpNumero) {
		this.cdpNumero = cdpNumero;
	}

	public Integer getExisteContratoSicon() {
		return existeContratoSicon;
	}

	public void setExisteContratoSicon(Integer existeContratoSicon) {
		this.existeContratoSicon = existeContratoSicon;
	}

	public Boolean getPublicaCUM() {
		return publicaCUM;
	}

	public void setPublicaCUM(Boolean publicaCUM) {
		this.publicaCUM = publicaCUM;
	}	
}