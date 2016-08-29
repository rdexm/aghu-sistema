package br.gov.mec.aghu.model;

import java.math.BigDecimal;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioIndicadorParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioLimiteValorPatrimonio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * Parâmetros de Regras Orçamentárias
 */
@Entity
@org.hibernate.annotations.Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
@SequenceGenerator(name = "fsoPmcSq1", sequenceName = "AGH.FSO_PMC_SQ1", allocationSize = 1)
@Table(name = "FSO_PARAMETROS_ORCAMENTO", schema = "AGH")
public class FsoParametrosOrcamento extends BaseEntitySeq<Integer> implements java.io.Serializable {
	private static final long serialVersionUID = 3245120459266684305L;

	private Integer seq;
	private DominioTipoSolicitacao tpProcesso;
	private DominioIndicadorParametrosOrcamento indGrupo;
	private ScoGrupoMaterial grupoMaterial;
	private ScoMaterial material;
	private ScoGrupoServico grupoServico;
	private ScoServico servico;
	private DominioLimiteValorPatrimonio tpLimite;
	private BigDecimal vlrLimitePatrimonio;
	private FccCentroCustos centroCusto;
	private DominioSituacao indSituacao;
	private DominioAcaoParametrosOrcamento acaoGnd;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;
	private DominioAcaoParametrosOrcamento acaoNtd;
	private FsoNaturezaDespesa naturezaDespesa;
	private Boolean isCadastradaGrupo;
	private DominioAcaoParametrosOrcamento acaoVbg;
	private FsoVerbaGestao verbaGestao;
	private DominioAcaoParametrosOrcamento acaoCct;
	private FccCentroCustos centroCustoReferencia;
	private Integer version;
	private RapServidores servidorInclusao;
	private RapServidores servidorAlteracao;
	private String regra;
	private String descricao;
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fsoPmcSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 6, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "TP_PROCESSO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoSolicitacao getTpProcesso() {
		return tpProcesso;
	}

	public void setTpProcesso(DominioTipoSolicitacao tpProcesso) {
		this.tpProcesso = tpProcesso;
	}

	@Column(name = "IND_GRUPO", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndicadorParametrosOrcamento getIndGrupo() {
		return indGrupo;
	}

	public void setIndGrupo(DominioIndicadorParametrosOrcamento indGrupo) {
		this.indGrupo = indGrupo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GMT_CODIGO", referencedColumnName = "CODIGO")
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName = "CODIGO")
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@ManyToOne(fetch  = FetchType.LAZY)
	@JoinColumn(name = "GSV_CODIGO", referencedColumnName = "CODIGO")
	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	@ManyToOne(fetch  = FetchType.LAZY)
	@JoinColumn(name = "SRV_CODIGO", referencedColumnName = "CODIGO")
	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	@Column(name = "TP_LIMITE", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioLimiteValorPatrimonio getTpLimite() {
		return tpLimite;
	}

	public void setTpLimite(DominioLimiteValorPatrimonio tpLimite) {
		this.tpLimite = tpLimite;
	}

	@Column(name = "VLR_LIMITE_PATRIMONIO", nullable = true, precision = 18, scale = 2)
	public BigDecimal getVlrLimitePatrimonio() {
		return vlrLimitePatrimonio;
	}

	public void setVlrLimitePatrimonio(BigDecimal vlrLimitePatrimonio) {
		this.vlrLimitePatrimonio = vlrLimitePatrimonio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_COD_APLICACAO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	@Column(name = "IND_SITUACAO", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "ACAO_GND", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioAcaoParametrosOrcamento getAcaoGnd() {
		return acaoGnd;
	}

	public void setAcaoGnd(DominioAcaoParametrosOrcamento acaoGnd) {
		this.acaoGnd = acaoGnd;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GND_CODIGO", referencedColumnName = "CODIGO")
	public FsoGrupoNaturezaDespesa getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}

	public void setGrupoNaturezaDespesa(
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}

	@Column(name = "ACAO_NTD", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioAcaoParametrosOrcamento getAcaoNtd() {
		return acaoNtd;
	}

	public void setAcaoNtd(DominioAcaoParametrosOrcamento acaoNtd) {
		this.acaoNtd = acaoNtd;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "GND_CODIGO", referencedColumnName = "GND_CODIGO", insertable = false, updatable = false),
			@JoinColumn(name = "NTD_CODIGO", referencedColumnName = "CODIGO", insertable = false, updatable = false) })
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}
	
	// FIXME a coluna GND_CODIGO precisa ser insertable/updatable = false no
	// método getNaturezaDespesa pois GND_CODIGO já é mapeada no método
	// getGrupoNaturezaDespesa. Portanto, para poder persistir o valor de
	// NTD_CODIGO foi criado este getter mapeado somente com esta coluna.
	@Column(name = "NTD_CODIGO", nullable = true)
	public Byte getNtdCodigo() {
		return naturezaDespesa != null ? naturezaDespesa.getId().getCodigo()
				: null;
	}
	
	public void setNtdCodigo(Byte ntdCodigo) {
		// FIXME criado para fins de compatibilidade com o ORM. 
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	@Column(name = "IND_USO_NAT_GRUPO", nullable = true, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIsCadastradaGrupo() {
		return isCadastradaGrupo;
	}

	public void setIsCadastradaGrupo(Boolean isCadastradaGrupo) {
		this.isCadastradaGrupo = isCadastradaGrupo;
	}

	@Column(name = "ACAO_VBG", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioAcaoParametrosOrcamento getAcaoVbg() {
		return acaoVbg;
	}

	public void setAcaoVbg(DominioAcaoParametrosOrcamento acaoVbg) {
		this.acaoVbg = acaoVbg;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VBG_SEQ", referencedColumnName = "SEQ")
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	@Column(name = "ACAO_CCT", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioAcaoParametrosOrcamento getAcaoCct() {
		return acaoCct;
	}

	public void setAcaoCct(DominioAcaoParametrosOrcamento acaoCct) {
		this.acaoCct = acaoCct;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_COD_REFERENCIA", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCustoReferencia() {
		return centroCustoReferencia;
	}

	public void setCentroCustoReferencia(FccCentroCustos centroCustoReferencia) {
		this.centroCustoReferencia = centroCustoReferencia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", nullable = false, referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", nullable = false, referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorInclusao() {
		return servidorInclusao;
	}

	public void setServidorInclusao(RapServidores servidorInclusao) {
		this.servidorInclusao = servidorInclusao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERACAO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERACAO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlteracao() {
		return servidorAlteracao;
	}

	public void setServidorAlteracao(RapServidores servidorAlteracao) {
		this.servidorAlteracao = servidorAlteracao;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "REGRA", length = 50)	
	public String getRegra() {
		return regra;
	}

	public void setRegra(String regra) {
		this.regra = regra;
	}

	@Column(name = "DESCRICAO", length = 500)	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder().append(getSeq(),
				((FsoParametrosOrcamento) obj).getSeq()).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getSeq()).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), TP_PROCESSO("tpProcesso"), IND_GRUPO("indGrupo"), GRUPO_MATERIAL(
				"grupoMaterial"), MATERIAL("material"), TP_LIMITE("tpLimite"), VLR_LIMITE_PATRIMONIO(
				"vlrLimitePatrimonio"), CENTRO_CUSTO("centroCusto"), IND_SITUACAO(
				"indSituacao"), GRUPO_SERVICO("grupoServico"), SERVICO(
				"servico"), GRUPO_NATUREZA_DESPESA("grupoNaturezaDespesa"), NATUREZA_DESPESA(
				"naturezaDespesa"), CENTRO_CUSTO_REFERENCIA(
				"centroCustoReferencia"), VERBA_GESTAO("verbaGestao"), ACAO_CCT(
				"acaoCct"), ACAO_GND("acaoGnd"), ACAO_NTD("acaoNtd"), ACAO_VBG("acaoVbg"),
				IS_CADASTRADA_GRUPO("isCadastradaGrupo"), NTD_CODIGO("ntdCodigo"),
				REGRA("regra"), DESCRICAO("descricao");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}
