package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Version;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioBaseAnaliseReposicao;
import br.gov.mec.aghu.dominio.DominioBaseReposicao;
import br.gov.mec.aghu.dominio.DominioSituacaoLoteReposicao;
import br.gov.mec.aghu.dominio.DominioTipoConsumo;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name="SCO_LOTES_REPOSICAO", schema="AGH")
@SequenceGenerator(name = "scoLoteReposicaoSq1", sequenceName = "AGH.SCO_LTR_SQ1", allocationSize = 1)
public class ScoLoteReposicao extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9186472712607761155L;
	private Integer seq;
	private String descricao;
	private DominioSituacaoLoteReposicao situacao;
	private DominioTipoMaterial tipoMaterial;
	private ScoGrupoMaterial grupoMaterial;
	private String classificacaoAbc;
	private Boolean indEmSc;
	private Boolean indEmOutraSc;
	private Boolean indEmLicitacao;
	private Boolean indEmAf;
	private Boolean indContrato;
	private Boolean indProducaoInterna;
	private Boolean indComLicitacao;
	private Boolean indPontoPedido;
	private Boolean indAfContrato;
	private Boolean indAfVencida;
	private Boolean indItemContrato;
	private Integer cobertura;
	private Date dtVigenciaContrato;
	private Long cn5Numero;
	private FccCentroCustos centroCustoAplicacao;
	private ScoModalidadeLicitacao modalidade;
	private DominioBaseAnaliseReposicao baseAnalise;
	private Date dtInicioBaseAnalise;
	private Date dtFimBaseAnalise;
	private Integer frequenciaBaseAnalise;
	private BigDecimal vlrInicioBaseAnalise;
	private BigDecimal vlrFinalBaseAnalise;
	private DominioBaseReposicao baseReposicao;
	private Date dataInicioReposicao;
	private Date dataFimReposicao;
	private DominioTipoConsumo tipoConsumo;
	private BigDecimal fatorSeguranca;
	private RapServidores servidorGeracao;
	private Date dtGeracao;
	private RapServidores servidorAlteracao;
	private Date dtAlteracao;
	private RapServidores servidorExclusao;
	private Date dtExclusao;
	private List<ScoItemLoteReposicao> itensLote;
	private Integer version;
		
    public ScoLoteReposicao() {
    }
	
	@Id
	@Column(name = "SEQ")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoLoteReposicaoSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DT_GERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}
	
	@Column(name = "DT_ALTERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAlteracao() {
		return dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao= dtAlteracao;
	}

	@Column(name = "DT_EXCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtExclusao() {
		return dtExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}
	
	@Column(name = "DT_VIG_CONTRATO")
	@Temporal(TemporalType.TIMESTAMP)	
	public Date getDtVigenciaContrato() {
		return dtVigenciaContrato;
	}

	public void setDtVigenciaContrato(Date dtVigenciaContrato) {
		this.dtVigenciaContrato = dtVigenciaContrato;
	}
	
	@Column(name = "DT_INI_BASE_GERACAO")
	@Temporal(TemporalType.TIMESTAMP)	
	public Date getDtInicioBaseAnalise() {
		return dtInicioBaseAnalise;
	}

	public void setDtInicioBaseAnalise(Date dtInicioBaseAnalise) {
		this.dtInicioBaseAnalise = dtInicioBaseAnalise;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "loteReposicao")
	public List<ScoItemLoteReposicao> getItensLote() {
		return itensLote;
	}

	public void setItensLote(List<ScoItemLoteReposicao> itensLote) {
		this.itensLote = itensLote;
	}

	@Column(name = "DT_FIM_BASE_GERCAO")
	@Temporal(TemporalType.TIMESTAMP)	
	public Date getDtFimBaseAnalise() {
		return dtFimBaseAnalise;
	}

	public void setDtFimBaseAnalise(Date dtFimBaseAnalise) {
		this.dtFimBaseAnalise = dtFimBaseAnalise;
	}

	@Column(name = "DT_INI_BASE_REPOSICAO")
	@Temporal(TemporalType.TIMESTAMP)	
	public Date getDataInicioReposicao() {
		return dataInicioReposicao;
	}

	public void setDataInicioReposicao(Date dataInicioReposicao) {
		this.dataInicioReposicao = dataInicioReposicao;
	}
	
	@Column(name = "DT_FIM_BASE_REPOSICAO")
	@Temporal(TemporalType.TIMESTAMP)	
	public Date getDataFimReposicao() {
		return dataFimReposicao;
	}

	public void setDataFimReposicao(Date dataFimReposicao) {
		this.dataFimReposicao = dataFimReposicao;
	}
	
	@Column(name = "VERSION")
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "COBERTURA", length= 7)
	public Integer getCobertura() {
		return cobertura;
	}

	public void setCobertura(Integer cobertura) {
		this.cobertura = cobertura;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorGeracao() {
		return servidorGeracao;
	}

	public void setServidorGeracao(RapServidores servidorGeracao) {
		this.servidorGeracao = servidorGeracao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALT", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALT", referencedColumnName = "VIN_CODIGO") })	
	public RapServidores getServidorAlteracao() {
		return servidorAlteracao;
	}

	public void setServidorAlteracao(RapServidores servidorAlteracao) {
		this.servidorAlteracao= servidorAlteracao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_EXC", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_EXC", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorExclusao() {
		return servidorExclusao;
	}

	public void setServidorExclusao(RapServidores servidorExclusao) {
		this.servidorExclusao = servidorExclusao;
	}	
	
	@ManyToOne
	@JoinColumn(name = "GMT_CODIGO")
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO_APLICACAO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	@Column(name = "DESCRICAO", length = 40)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "CLASSIF_ABC", length = 3)
	public String getClassificacaoAbc() {
		return this.classificacaoAbc;
	}

	public void setClassificacaoAbc(String classificacaoAbc) {
		this.classificacaoAbc = classificacaoAbc;
	}
	
	@Column(name = "CN5_NUMERO")
	public Long getCn5Numero() {
		return this.cn5Numero;
	}

	public void setCn5Numero(Long cn5Numero) {
		this.cn5Numero = cn5Numero;
	}	
	
	@Column(name = "VALOR_INI_BASE_GERACAO", precision = 17, scale = 2)
	@Digits(integer=16, fraction=2)
	public BigDecimal getVlrInicioBaseAnalise() {
		return this.vlrInicioBaseAnalise;
	}

	public void setVlrInicioBaseAnalise(BigDecimal vlrInicioBaseAnalise) {
		this.vlrInicioBaseAnalise = vlrInicioBaseAnalise;
	}

	@Column(name = "VALOR_FIM_BASE_GERACAO", precision = 17, scale = 2)
	@Digits(integer=16, fraction=2)
	public BigDecimal getVlrFinalBaseAnalise() {
		return this.vlrFinalBaseAnalise;
	}

	public void setVlrFinalBaseAnalise(BigDecimal vlrFinalBaseAnalise) {
		this.vlrFinalBaseAnalise = vlrFinalBaseAnalise;
	}	
	
	@Column(name = "FATOR_REPOSICAO", precision = 5, scale = 2)
	@Digits(integer=5, fraction=2)
	public BigDecimal getFatorSeguranca() {
		return this.fatorSeguranca;
	}

	public void setFatorSeguranca(BigDecimal fatorSeguranca) {
		this.fatorSeguranca = fatorSeguranca;
	}	
	
	@Column(name = "IND_OUTRA_SC")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEmOutraSc() {
		return indEmOutraSc;
	}

	public void setIndEmOutraSc(Boolean indEmOutraSc) {
		this.indEmOutraSc = indEmOutraSc;
	}

	@Column(name = "IND_EM_SC")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEmSc() {
		return indEmSc;
	}

	public void setIndEmSc(Boolean indEmSc) {
		this.indEmSc = indEmSc;
	}

	@Column(name = "IND_EM_AF")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEmAf() {
		return indEmAf;
	}

	public void setIndEmAf(Boolean indEmAf) {
		this.indEmAf = indEmAf;
	}

	@Column(name = "IND_PRODUCAO_INTERNA", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndProducaoInterna() {
		return indProducaoInterna;
	}

	public void setIndProducaoInterna(Boolean indProducaoInterna) {
		this.indProducaoInterna = indProducaoInterna;
	}

	@Column(name = "IND_COM_LICITACAO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndComLicitacao() {
		return indComLicitacao;
	}

	public void setIndComLicitacao(Boolean indComLicitacao) {
		this.indComLicitacao = indComLicitacao;
	}

	@Column(name = "IND_PONTO_PEDIDO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPontoPedido() {
		return indPontoPedido;
	}

	public void setIndPontoPedido(Boolean indPontoPedido) {
		this.indPontoPedido = indPontoPedido;
	}

	@Column(name = "IND_AF_CONTRATO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAfContrato() {
		return indAfContrato;
	}

	public void setIndAfContrato(Boolean indAfContrato) {
		this.indAfContrato= indAfContrato;
	}

	@Column(name = "IND_AF_VENCIDA", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAfVencida() {
		return indAfVencida;
	}

	public void setIndAfVencida(Boolean indAfVencida) {
		this.indAfVencida = indAfVencida;
	}

	@Column(name = "IND_ITEM_CONTRATO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndItemContrato() {
		return indItemContrato;
	}

	public void setIndItemContrato(Boolean indItemContrato) {
		this.indItemContrato = indItemContrato;
	}
	
	@Column(name = "IND_LICITACAO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEmLicitacao() {
		return indEmLicitacao;
	}

	public void setIndEmLicitacao(Boolean indEmLicitacao) {
		this.indEmLicitacao = indEmLicitacao;
	}

	@Column(name = "IND_CONTRATO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndContrato() {
		return indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato= indContrato;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoLoteReposicao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoLoteReposicao situacao) {
		this.situacao = situacao;
	}
	
	@Column(name = "TIPO_MATERIAL")
	@Enumerated(EnumType.STRING)
	public DominioTipoMaterial getTipoMaterial() {
		return this.tipoMaterial;
	}

	public void setTipoMaterial(DominioTipoMaterial tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MLC_CODIGO", referencedColumnName = "CODIGO")
	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}

	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
	}

	@Column(name = "FREQ_BASE_GERACAO", precision = 7, scale = 0)
	public Integer getFrequenciaBaseAnalise() {
		return this.frequenciaBaseAnalise;
	}

	public void setFrequenciaBaseAnalise(Integer frequenciaBaseAnalise) {
		this.frequenciaBaseAnalise = frequenciaBaseAnalise;
	}

	@Column(name = "BASE_REPOSICAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioBaseReposicao getBaseReposicao() {
		return this.baseReposicao;
	}

	public void setBaseReposicao(DominioBaseReposicao baseReposicao) {
		this.baseReposicao = baseReposicao;
	}

	@Column(name = "BASE_GERACAO")
	@Enumerated(EnumType.STRING)
	public DominioBaseAnaliseReposicao getBaseAnalise() {
		return this.baseAnalise;
	}

	public void setBaseAnalise(DominioBaseAnaliseReposicao baseAnalise) {
		this.baseAnalise = baseAnalise;
	}

	@Column(name = "BASE_CONSUMO_REPOSICAO")
	@Enumerated(EnumType.STRING)
	public DominioTipoConsumo getTipoConsumo() {
		return this.tipoConsumo;
	}

	public void setTipoConsumo(DominioTipoConsumo tipoConsumo) {
		this.tipoConsumo = tipoConsumo;
	}

	public enum Fields {
		SEQ("seq"),
		DT_GERACAO("dtGeracao"),
		DESCRICAO("descricao"),
		SITUACAO("situacao"),
		TIPO_MATERIAL("tipoMaterial"),
		GRUPO_MATERIAL("grupoMaterial"),
		CLASS_ABC("classificacaoAbc"),
		IND_EM_SC("indEmSc"),
		IND_EM_OUTRA_SC("indEmOutraSc"),
		IND_EM_LICITACAO("indEmLicitacao"),
		IND_EM_AF("indEmAf"),
		IND_CONTRATO("indContrato"),
		DT_VIGENCIA_CONTRATO("dtVigenciaContrato"),
		CN5_NUMERO("cn5Numero"),
		CC_APLICACAO("centroCustoAplicacao"),
		MODALIDADE("modalidade"),
		BASE_ANALISE("baseAnalise"),
		DT_INICIO_BASE_ANALISE("dtInicioBaseAnalise"),
		DT_FIM_BASE_ANALISE("dtFimBaseAnalise"),
		FREQUENCIA_BASE_ANALISE("frequenciaBaseAnalise"),
		VLR_INICIO_BASE_ANALISE("vlrInicioBaseAnalise"),
		VLR_FINAL_BASE_ANALISE("vlrFinalBaseAnalise"),
		BASE_REPOSICAO("baseReposicao"),
		DT_INI_REPOSICAO("dataInicioReposicao"),
		DT_FIM_REPOSICAO("dataFimReposicao"),
		TIPO_CONSUMO("tipoConsumo"),
		FATOR_SEGURANCA("fatorSeguranca"),
		SERVIDOR_GERACAO("servidorGeracao"),
		SERVIDOR_ALTERACAO("servidorAlteracao"),
		DT_ALTERACAO("dtAlteracao"),
		SERVIDOR_EXCLUSAO("servidorExclusao"),
		DT_EXCLUSAO("dtExclusao"),
		ITENS_LOTE_REPOSICAO("itensLote")
		 
		;
		
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		public String toString() {
			return this.field;
		}

	}
}