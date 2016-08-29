package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioOrcamentoPrevio;
import br.gov.mec.aghu.core.persistence.BaseEntityNumero;


/**
 * The persistent class for the sco_solicitacoes_servico database table.
 * 
 */
@Entity
@SequenceGenerator(name="scoSlsSq1", sequenceName="AGH.SCO_SLS_SQ1", allocationSize = 1)
@Table(name="SCO_SOLICITACOES_SERVICO", schema = "AGH")
public class ScoSolicitacaoServico extends BaseEntityNumero<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6467864245666589672L;
	
	private Integer numero;
	private String aplicacao;
	private FccCentroCustos centroCusto;
	private FccCentroCustos centroCustoAplicada;
	private FccCentroCustos centroCustoAutzTecnica;
	private Integer cctCodigoAutzTecnica;
	private FsoConveniosFinanceiro convenioFinanceiro;
	private String descricao;
	private Date dtAlteracao;
	private Date dtAnalise;
	private Date dtAutorizacao;
	private Date dtDigitacao;
	private Date dtEncerramento;
	private Date dtExclusao;
	private Date dtSolicitacao;
	private Boolean indDevolucao;
	private Boolean indEfetivada;
	private Boolean indExclusao;
	private Boolean indUrgente;
	private Boolean indExclusivo;
	private Boolean indPrioridade;
	private String justificativaDevolucao;
	private String justificativaUso;
	private String justificativaExclusividade;
	//private String mlcCodigo;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private String motivoExclusao;
	private String motivoUrgencia;
	private String motivoPrioridade;
	private Integer nroInvestimento;
	private Integer nroProjeto;
	private FsoNaturezaDespesa naturezaDespesa;
	private DominioOrcamentoPrevio orcamentoPrevio;
	private Integer qtdeSolicitada;
	private RapServidores servidor;
	private RapServidores servidorAlterador;
	private RapServidores servidorAutorizador;
	private RapServidores servidorComprador;
	private RapServidores servidorExcluidor;
	private BigDecimal valorUnitPrevisto;
	private Integer version;

	private Set<ScoFaseSolicitacao> fasesSolicitacao;
	private ScoServico servico;
	private ScoPontoParadaSolicitacao pontoParada;
	private ScoPontoParadaSolicitacao pontoParadaLocAtual;
	private FsoVerbaGestao verbaGestao;
	private Date dtMaxAtendimento;
	
	private Integer numeroOrdemItemLicitacao;

    public ScoSolicitacaoServico() {
    }

    @Id
    @Column(name = "NUMERO", nullable = false, precision = 7, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoSlsSq1")
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name = "APLICACAO", length = 500)
	@Length(max = 500)
	public String getAplicacao() {
		return this.aplicacao;
	}

	public void setAplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO_APLICADA", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCustoAplicada() {
		return centroCustoAplicada;
	}

	public void setCentroCustoAplicada(FccCentroCustos centroCustoAplicada) {
		this.centroCustoAplicada = centroCustoAplicada;
	}	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "CCT_CODIGO_AUTZ_TECNICA", referencedColumnName = "CODIGO", insertable=false,updatable=false)
	public FccCentroCustos getCentroCustoAutzTecnica() {
		return centroCustoAutzTecnica;
	}

	public void setCentroCustoAutzTecnica(FccCentroCustos centroCustoAutzTecnica) {
		this.centroCustoAutzTecnica = centroCustoAutzTecnica;
	}

	@Column(name="CCT_CODIGO_AUTZ_TECNICA")
	public Integer getCctCodigoAutzTecnica() {
		return this.cctCodigoAutzTecnica;
	}

	public void setCctCodigoAutzTecnica(Integer cctCodigoAutzTecnica) {
		this.cctCodigoAutzTecnica = cctCodigoAutzTecnica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CVF_CODIGO", referencedColumnName = "CODIGO")
	public FsoConveniosFinanceiro getConvenioFinanceiro() {
		return convenioFinanceiro;
	}

	public void setConvenioFinanceiro(FsoConveniosFinanceiro convenioFinanceiro) {
		this.convenioFinanceiro = convenioFinanceiro;
	}

	@Column(name = "DESCRICAO", length = 500)
	@Length(max = 500)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name="DT_ALTERACAO")
	public Date getDtAlteracao() {
		return this.dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	@Column(name="DT_ANALISE")
	public Date getDtAnalise() {
		return this.dtAnalise;
	}

	public void setDtAnalise(Date dtAnalise) {
		this.dtAnalise = dtAnalise;
	}

	@Column(name="DT_AUTORIZACAO")
	public Date getDtAutorizacao() {
		return this.dtAutorizacao;
	}

	public void setDtAutorizacao(Date dtAutorizacao) {
		this.dtAutorizacao = dtAutorizacao;
	}

	@Column(name="DT_DIGITACAO")
	public Date getDtDigitacao() {
		return this.dtDigitacao;
	}

	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	@Column(name="DT_ENCERRAMENTO")
	public Date getDtEncerramento() {
		return this.dtEncerramento;
	}

	public void setDtEncerramento(Date dtEncerramento) {
		this.dtEncerramento = dtEncerramento;
	}

	@Column(name="DT_EXCLUSAO")
	public Date getDtExclusao() {
		return this.dtExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}

	@Column(name="DT_SOLICITACAO")
	public Date getDtSolicitacao() {
		return this.dtSolicitacao;
	}

	public void setDtSolicitacao(Date dtSolicitacao) {
		this.dtSolicitacao = dtSolicitacao;
	}

	@Column(name="IND_DEVOLUCAO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDevolucao() {
		return this.indDevolucao;
	}

	public void setIndDevolucao(Boolean indDevolucao) {
		this.indDevolucao = indDevolucao;
	}

	@Column(name="IND_EFETIVADA")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEfetivada() {
		return this.indEfetivada;
	}

	public void setIndEfetivada(Boolean indEfetivada) {
		this.indEfetivada = indEfetivada;
	}

	@Column(name="IND_EXCLUSAO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExclusao() {
		return this.indExclusao;
	}

	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}

	@Column(name="IND_URGENTE")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUrgente() {
		return this.indUrgente;
	}

	public void setIndUrgente(Boolean indUrgente) {
		this.indUrgente = indUrgente;
	}
	
	@Column(name="IND_SER_EXCLUSIVO") 
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExclusivo() {
		return this.indExclusivo;
	}

	public void setIndExclusivo(Boolean indExclusivo) {
		this.indExclusivo = indExclusivo;
	}

	@Column(name="JUSTIFICATIVA_DEVOLUCAO")
	public String getJustificativaDevolucao() {
		return this.justificativaDevolucao;
	}

	public void setJustificativaDevolucao(String justificativaDevolucao) {
		this.justificativaDevolucao = justificativaDevolucao;
	}

	@Column(name="JUSTIFICATIVA_USO")
	public String getJustificativaUso() {
		return this.justificativaUso;
	}

	public void setJustificativaUso(String justificativaUso) {
		this.justificativaUso = justificativaUso;
	}

	@Column(name = "JUSTIFICATIVA_EXCLUSIVIDADE", length = 240)
	@Length(max = 240)
	public String getJustificativaExclusividade() {
		return this.justificativaExclusividade;
	}

	public void setJustificativaExclusividade(String justificativaExclusividade) {
		this.justificativaExclusividade = justificativaExclusividade;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MLC_CODIGO", referencedColumnName = "CODIGO")
	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	@Column(name="MOTIVO_EXCLUSAO")
	public String getMotivoExclusao() {
		return this.motivoExclusao;
	}

	public void setMotivoExclusao(String motivoExclusao) {
		this.motivoExclusao = motivoExclusao;
	}

	@Column(name="MOTIVO_URGENCIA")
	public String getMotivoUrgencia() {
		return this.motivoUrgencia;
	}

	public void setMotivoUrgencia(String motivoUrgencia) {
		this.motivoUrgencia = motivoUrgencia;
	}

	@Column(name="NRO_INVESTIMENTO")
	public Integer getNroInvestimento() {
		return this.nroInvestimento;
	}

	public void setNroInvestimento(Integer nroInvestimento) {
		this.nroInvestimento = nroInvestimento;
	}

	@Column(name="NRO_PROJETO")
	public Integer getNroProjeto() {
		return this.nroProjeto;
	}

	public void setNroProjeto(Integer nroProjeto) {
		this.nroProjeto = nroProjeto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "NTD_CODIGO", referencedColumnName = "CODIGO"),
			@JoinColumn(name = "NTD_GND_CODIGO", referencedColumnName = "GND_CODIGO") })
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	@Column(name = "ORCAMENTO_PREVIO", nullable = true, length = 1)	
	@Enumerated(EnumType.STRING)
	public DominioOrcamentoPrevio getOrcamentoPrevio() {
		return this.orcamentoPrevio;
	}

	public void setOrcamentoPrevio(DominioOrcamentoPrevio orcamentoPrevio) {
		this.orcamentoPrevio = orcamentoPrevio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn (name="PPS_CODIGO",referencedColumnName="CODIGO")
	public ScoPontoParadaSolicitacao getPontoParada() {
		return this.pontoParada;
	}

	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn (name="PPS_CODIGO_LOC_ATUAL")
	public ScoPontoParadaSolicitacao getPontoParadaLocAtual() {
		return pontoParadaLocAtual;
	}

	public void setPontoParadaLocAtual(
			ScoPontoParadaSolicitacao pontoParadaLocAtual) {
		this.pontoParadaLocAtual = pontoParadaLocAtual;
	}
	
	@Column(name="QTDE_SOLICITADA")
	public Integer getQtdeSolicitada() {
		return this.qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}


    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_AUTORIZADA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_AUTORIZADA", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorAutorizador() {
		return this.servidorAutorizador;
	}

	public void setServidorAutorizador(RapServidores servidorAutorizador) {
		this.servidorAutorizador = servidorAutorizador;
	}


    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_EXCLUIDA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_EXCLUIDA", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorExcluidor() {
		return this.servidorExcluidor;
	}

	public void setServidorExcluidor(RapServidores servidorExcluidor) {
		this.servidorExcluidor = servidorExcluidor;
	}


    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_ALTERADA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_ALTERADA", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorAlterador() {
		return this.servidorAlterador;
	}

	public void setServidorAlterador(RapServidores servidorAlterador) {
		this.servidorAlterador = servidorAlterador;
	}


    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_COMPRA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_COMPRA", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorComprador() {
		return this.servidorComprador;
	}

	public void setServidorComprador(RapServidores servidorComprador) {
		this.servidorComprador = servidorComprador;
	}

	@Column(name="VALOR_UNIT_PREVISTO", precision = 18, scale = 2)
	public BigDecimal getValorUnitPrevisto() {
		return this.valorUnitPrevisto;
	}

	public void setValorUnitPrevisto(BigDecimal valorUnitPrevisto) {
		this.valorUnitPrevisto = valorUnitPrevisto;
	}

	@Column(name = "IND_PRIORIDADE", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPrioridade() {
		return indPrioridade;
	}

	public void setIndPrioridade(Boolean indPrioridade) {
		this.indPrioridade = indPrioridade;
	}
	
	@Column(name = "MOTIVO_PRIORIDADE", length = 500)
	@Length(max = 500)
	public String getMotivoPrioridade() {
		return motivoPrioridade;
	}

	public void setMotivoPrioridade(String motivoPrioridade) {
		this.motivoPrioridade = motivoPrioridade;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_MAX_ATENDIMENTO", length = 7)
	public Date getDtMaxAtendimento() {
		return dtMaxAtendimento;
	}

	public void setDtMaxAtendimento(Date dtMaxAtendimento) {
		this.dtMaxAtendimento = dtMaxAtendimento;
	}
	
	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy="solicitacaoServico")
	public Set<ScoFaseSolicitacao> getFasesSolicitacao() {
		return fasesSolicitacao;
	}

	public void setFasesSolicitacao(Set<ScoFaseSolicitacao> fasesSolicitacao) {
		this.fasesSolicitacao = fasesSolicitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn (name="SRV_CODIGO")
	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VBG_SEQ", referencedColumnName = "SEQ")
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}
	
	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}
	
	@Transient
	public Integer getNumeroOrdemItemLicitacao() {
		return numeroOrdemItemLicitacao;
	}

	public void setNumeroOrdemItemLicitacao(Integer numeroOrdemItemLicitacao) {
		this.numeroOrdemItemLicitacao = numeroOrdemItemLicitacao;
	}

	@Transient
	public BigDecimal getValorTotal() {
		if (getValorUnitPrevisto() != null && getQtdeSolicitada() != null) {
			return BigDecimal.ZERO.add(getValorUnitPrevisto().multiply(
					new BigDecimal(getQtdeSolicitada())));
		} else {
			return null;
		}
	}
	
	public enum Fields{
		NUMERO("numero"),
		DESCRICAO("descricao"),
		QUANTIDADE_SOLICITADA("qtdeSolicitada"),
		SERVICO("servico"),
		SRV_CODIGO("servico"),
		SERVICO_CODIGO("servico.codigo"),
		PONTO_PARADA("pontoParada"),
		PONTO_PARADA_ATUAL("pontoParadaLocAtual"),
		IND_EXCLUSAO("indExclusao"),
		IND_EXCLUSIVO("indExclusivo"),
		IND_EFETIVADA("indEfetivada"),
		NATUREZA_DESPESA("naturezaDespesa"),
		CONVENIO_FINANCEIRO("convenioFinanceiro"),
		CC_APLICADA("centroCustoAplicada"),
		CC_APLICADA_CODIGO("centroCustoAplicada.codigo"),
		IND_URGENTE("indUrgente"),
		VALOR_UNITARIO_PREVISTO("valorUnitPrevisto"),
		SERVIDOR_COMPRADOR("servidorComprador"),
		DT_AUTORIZACAO("dtAutorizacao"),
		DT_SOLICITACAO("dtSolicitacao"),
		DT_DIGITACAO("dtDigitacao"),
		NRO_INVESTIMENTO("nroInvestimento"),
		QTDE_SOLICITADA("qtdeSolicitada"),
		VALOR_UNIT_PREVISTO("valorUnitPrevisto"),
		MOTIVO_URGENCIA("motivoUrgencia"),
		JUSTIFICATIVA_USO("justificativaUso"),
		APLICACAO("aplicacao"),
		FASES_SOLICITACAO("fasesSolicitacao"),
		CCT_CODIGO_AUTZ_TECNICA("cctCodigoAutzTecnica"),
		CCT_CODIGO_APLICADA("centroCustoAplicada.codigo"),
		CVF_CODIGO("convenioFinanceiro.codigo"),
		NTD_GND_CODIGO("naturezaDespesa.id.gndCodigo"),
		NTD_CODIGO("naturezaDespesa.id.codigo"),
		SER_VIN_CODIGO_AUTORIZADA("servidorAutorizador.id.vinCodigo"),
		SERVIDOR("servidor"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		SER_MATRICULA("servidor.id.matricula"),
		SERVIDOR_AUTORIZADOR("servidorAutorizador"),
		SERVIDOR_EXCLUIDOR("servidorExcluidor"),		
		DT_EXCLUSAO("dtExclusao"),
		SER_MATRICULA_AUTORIZADA("servidorAutorizador.id.matricula"),
		MATRICULA_SERVIDOR_COMPRADOR("servidorComprador.id.matricula"),
		CENTRO_CUSTO("centroCusto"),
		CCT_CODIGO("centroCusto.codigo"),
		CENTRO_CUSTO_AUT_TEC("centroCustoAutzTecnica"),
		IND_PRIORIDADE("indPrioridade"),
		IND_DEVOLUCAO("indDevolucao"),
		PPS_CODIGO("pontoParada.codigo"),
		PPS_CODIGO_LOC_ATUAL("pontoParadaLocAtual.codigo"),
		MODALIDADE_LICITACAO("modalidadeLicitacao"),
		MOD_LICITACAO("modalidadeLicitacao.codigo"),
		SRV_GRUPO_ENGENHARIA("servico.grupoServico.indEngenharia"),
		DT_ANALISE("dtAnalise"),
		VERBA_GESTAO("verbaGestao"),
		NRO_PROJETO("nroProjeto"),
		VBG_SEQ("verbaGestao.seq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getNumero() == null) ? 0 : getNumero().hashCode());
		return result;
	}
	
	/*@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ScoSolicitacaoServico)) {
			return false;
		}
		ScoSolicitacaoServico other = (ScoSolicitacaoServico) obj;
		if (getNumero() == null) {
			if (other.getNumero() != null) {
				return false;
			}
		} else if (!getNumero().equals(other.getNumero())) {
			return false;
		}
		return true;
	}*/
	@Override
	public boolean equals(Object other) {
		if (other instanceof ScoSolicitacaoServico){
			ScoSolicitacaoServico castOther = (ScoSolicitacaoServico) other;
			return new EqualsBuilder()
				.append(this.numero, castOther.numero)
			.isEquals();
		}
		else {
			return false;
		}	
	}
	// ##### GeradorEqualsHashCodeMain #####



}