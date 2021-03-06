package br.gov.mec.aghu.model;

// Generated 10/08/2010 17:33:16 by Hibernate Tools 3.3.0.GA

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import br.gov.mec.aghu.dominio.DominioSituacaFatura;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * SceItemRmps generated by hbm2java
 */
@Entity
@Table(name = "SCE_ITEM_RMPS")

public class SceItemRmps extends BaseEntityId<SceItemRmpsId> implements java.io.Serializable {

	// TODO Implementar coluna version quando utilizar essa entidade para
	// persistencia

	/**
	 * 
	 */
	private static final long serialVersionUID = -2820603291964216883L;
	private SceItemRmpsId id;
	private RapServidores servidor;
	private Integer quantidade;
	private Date data;
	private Integer notaFiscal;
	private String serie;
	private String lote;
	private String registroAnvisa;
	private Long cnpjRegistroAnvisa;
	private DominioSituacaFatura situacaoFatura;
	private Integer quantidadeReposicao;
	private String justificativa;
	private Integer quantidadeFaturada;
	private Date dataLiberacao;
	private String tamanho;
	private Integer quantidadeUso;
	private Integer quantidadeBloqueioHcpa;
	private Integer quantidadeBloqueioForn;
	private String numeroEtiqueta;
	private Set<FatItemContaHospitalar> itensContasHospitalar;

	private SceEstoqueAlmoxarifado estoqueAlmoxarifado;
	private SceItemRmr itemRmr;
	private SceRmrPaciente sceRmrPaciente;

	// private Integer version;

	public SceItemRmps() {
	}

	public SceItemRmps(final SceItemRmpsId id, final Integer quantidade) {
		this.id = id;
		this.quantidade = quantidade;
	}

	public SceItemRmps(final SceItemRmpsId id, final RapServidores servidor, final Integer quantidade, final Date data, final Integer notaFiscal,
			final String serie, final String lote, final String registroAnvisa, final Long cnpjRegistroAnvisa,
			final DominioSituacaFatura situacaoFatura, final Integer quantidadeReposicao, final String justificativa,
			final Integer quantidadeFaturada, final Date dataLiberacao, final String tamanho, final Integer quantidadeUso,
			final Integer quantidadeBloqueioHcpa, final Integer quantidadeBloqueioForn, final String numeroEtiqueta,
			final Set<FatItemContaHospitalar> itensContasHospitalar) {
		this.id = id;
		this.servidor = servidor;
		this.quantidade = quantidade;
		this.data = data;
		this.notaFiscal = notaFiscal;
		this.serie = serie;
		this.lote = lote;
		this.registroAnvisa = registroAnvisa;
		this.cnpjRegistroAnvisa = cnpjRegistroAnvisa;
		this.situacaoFatura = situacaoFatura;
		this.quantidadeReposicao = quantidadeReposicao;
		this.justificativa = justificativa;
		this.quantidadeFaturada = quantidadeFaturada;
		this.dataLiberacao = dataLiberacao;
		this.tamanho = tamanho;
		this.quantidadeUso = quantidadeUso;
		this.quantidadeBloqueioHcpa = quantidadeBloqueioHcpa;
		this.quantidadeBloqueioForn = quantidadeBloqueioForn;
		this.numeroEtiqueta = numeroEtiqueta;
		this.itensContasHospitalar = itensContasHospitalar;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "rmpSeq", column = @Column(name = "RMP_SEQ", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "numero", column = @Column(name = "NUMERO", nullable = false, precision = 3, scale = 0)) })
	public SceItemRmpsId getId() {
		return this.id;
	}

	public void setId(final SceItemRmpsId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_LIBERACAO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_LIBERACAO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "QUANTIDADE", nullable = false, precision = 7, scale = 0)
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(final Integer quantidade) {
		this.quantidade = quantidade;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA", length = 7)
	public Date getData() {
		return this.data;
	}

	public void setData(final Date data) {
		this.data = data;
	}

	@Column(name = "NOTA_FISCAL", precision = 7, scale = 0)
	public Integer getNotaFiscal() {
		return this.notaFiscal;
	}

	public void setNotaFiscal(final Integer notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	@Column(name = "SERIE", length = 20)
	public String getSerie() {
		return this.serie;
	}

	public void setSerie(final String serie) {
		this.serie = serie;
	}

	@Column(name = "LOTE", length = 20)
	public String getLote() {
		return this.lote;
	}

	public void setLote(final String lote) {
		this.lote = lote;
	}

	@Column(name = "REG_ANVISA", length = 20)
	public String getRegistroAnvisa() {
		return this.registroAnvisa;
	}

	public void setRegistroAnvisa(final String registroAnvisa) {
		this.registroAnvisa = registroAnvisa;
	}

	@Column(name = "CNPJ_REG_ANVISA", precision = 14, scale = 0)
	public Long getCnpjRegistroAnvisa() {
		return this.cnpjRegistroAnvisa;
	}

	public void setCnpjRegistroAnvisa(final Long cnpjRegistroAnvisa) {
		this.cnpjRegistroAnvisa = cnpjRegistroAnvisa;
	}

	@Column(name = "IND_SITUACAO_FATURA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaFatura getSituacaoFatura() {
		if(this.situacaoFatura == null){
			this.situacaoFatura = DominioSituacaFatura.G;
		}
		return this.situacaoFatura;
	}

	public void setSituacaoFatura(final DominioSituacaFatura situacaoFatura) {
		this.situacaoFatura = situacaoFatura;
	}

	@Column(name = "QTDE_REPOSICAO", precision = 7, scale = 0)
	public Integer getQuantidadeReposicao() {
		return this.quantidadeReposicao;
	}

	public void setQuantidadeReposicao(final Integer quantidadeReposicao) {
		this.quantidadeReposicao = quantidadeReposicao;
	}

	@Column(name = "JUSTIFICATIVA", length = 240)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(final String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "QTDE_FATURADA", precision = 7, scale = 0)
	public Integer getQuantidadeFaturada() {
		return this.quantidadeFaturada;
	}

	public void setQuantidadeFaturada(final Integer quantidadeFaturada) {
		this.quantidadeFaturada = quantidadeFaturada;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_LIBERACAO", length = 7)
	public Date getDataLiberacao() {
		return this.dataLiberacao;
	}

	public void setDataLiberacao(final Date dataLiberacao) {
		this.dataLiberacao = dataLiberacao;
	}

	@Column(name = "TAMANHO", length = 20)
	public String getTamanho() {
		return this.tamanho;
	}

	public void setTamanho(final String tamanho) {
		this.tamanho = tamanho;
	}

	@Column(name = "QTDE_USO", precision = 7, scale = 0)
	public Integer getQuantidadeUso() {
		return this.quantidadeUso;
	}

	public void setQuantidadeUso(final Integer quantidadeUso) {
		this.quantidadeUso = quantidadeUso;
	}

	@Column(name = "QTDE_BLOQ_HCPA", precision = 7, scale = 0)
	public Integer getQuantidadeBloqueioHcpa() {
		return this.quantidadeBloqueioHcpa;
	}

	public void setQuantidadeBloqueioHcpa(final Integer quantidadeBloqueioHcpa) {
		this.quantidadeBloqueioHcpa = quantidadeBloqueioHcpa;
	}

	@Column(name = "QTDE_BLOQ_FORN", precision = 7, scale = 0)
	public Integer getQuantidadeBloqueioForn() {
		return this.quantidadeBloqueioForn;
	}

	public void setQuantidadeBloqueioForn(final Integer quantidadeBloqueioForn) {
		this.quantidadeBloqueioForn = quantidadeBloqueioForn;
	}

	@Column(name = "NRO_ETIQUETA", length = 15)
	public String getNumeroEtiqueta() {
		return this.numeroEtiqueta;
	}

	public void setNumeroEtiqueta(final String numeroEtiqueta) {
		this.numeroEtiqueta = numeroEtiqueta;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemRmps")
	public Set<FatItemContaHospitalar> getItensContasHospitalar() {
		if(this.itensContasHospitalar == null){
			this.itensContasHospitalar = new HashSet<FatItemContaHospitalar>();
		}
		return this.itensContasHospitalar;
	}

	public void setItensContasHospitalar(final Set<FatItemContaHospitalar> itensContasHospitalar) {
		this.itensContasHospitalar = itensContasHospitalar;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EAL_SEQ")
	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifado() {
		return this.estoqueAlmoxarifado;
	}

	public void setEstoqueAlmoxarifado(final SceEstoqueAlmoxarifado estoqueAlmoxarifado) {
		this.estoqueAlmoxarifado = estoqueAlmoxarifado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "IRR_EAL_SEQ", referencedColumnName = "EAL_SEQ"),
			@JoinColumn(name = "IRR_RMR_SEQ", referencedColumnName = "RMR_SEQ") })
	public SceItemRmr getItemRmr() {
		return this.itemRmr;
	}

	public void setItemRmr(final SceItemRmr sceItemRmr) {
		this.itemRmr = sceItemRmr;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RMP_SEQ", nullable = false, insertable = false, updatable = false)
	public SceRmrPaciente getSceRmrPaciente() {
		return this.sceRmrPaciente;
	}

	public void setSceRmrPaciente(final SceRmrPaciente sceRmrPaciente) {
		this.sceRmrPaciente = sceRmrPaciente;
	}

	//
	// @Version
	// @Column(name = "VERSION", nullable = false)
	// public Integer getVersion() {
	// return this.version;
	// }
	//
	// public void setVersion(Integer version) {
	// this.version = version;
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SceItemRmps other = (SceItemRmps) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

//	private enum ItemRmpsExceptionCode implements BusinessExceptionCode {
//		SCE_IPS_CK1
//	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarConstraints() {
		final boolean condicao1 = this.itemRmr != null && this.estoqueAlmoxarifado == null;
		final boolean condicao2 = this.itemRmr == null && this.estoqueAlmoxarifado != null;
		//TODO: Desativado pq a tela não tem os campos.
		/*if (!(condicao1 || condicao2)) {
			throw new BaseRuntimeException(ItemRmpsExceptionCode.SCE_IPS_CK1);
		}*/
	}

	public enum Fields {

		NUMERO("id.numero"), 
		RMP_SEQ("id.rmpSeq"), 
		NOTA_FISCAL("notaFiscal"), 
		SCE_RMR_PACIENTE("sceRmrPaciente"), 
		SCE_ESTQ_ALMOX("estoqueAlmoxarifado"), 
		SCE_ESTQ_ALMOX_SEQ("estoqueAlmoxarifado.seq"),
		ITENS_CONTAS_HOSPITALAR("itensContasHospitalar"), 
		ITEM_RMR("itemRmr"),
		DATA("data"),
		QUANTIDADE("quantidade"),
		IRR_RMR_SEQ("itemRmr.id.rmrSeq"),
		IRR_EAL_SEQ("itemRmr.id.ealSeq"),
		NUMERO_ETIQUETA("numeroEtiqueta");
		
		private String field;

		private Fields(final String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}

	}

}
