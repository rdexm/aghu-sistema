package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "AGH_MICRO_EQUIPAMENTOS", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = "patrimonio"))
public class AghMicroEquipamento extends BaseEntityId<AghMicroEquipamentoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7921259424240680950L;
	private AghMicroEquipamentoId id;
	private Integer version;
	private RapServidores rapServidores;
	private AghMicrocomputador aghMicrocomputador;
	private Integer codEqu;
	private Integer codLeq;
	private Integer codAss;
	private String equipamento;
	private String tipo;
	private String modelo;
	private String fabricante;
	private String serie;
	private String status;
	private Date dataAquisicao;
	private String notaFiscal;
	private String fornecedor;
	private Date dataGarantia;
	private String obs;
	private String motivo;
	private String retornar;
	private String patrimonio;
	private Float tamanhoHd;
	private Integer tamanhoMemoria;
	private String numeroComodato;
	private String proprietarioComodato;
	private String ativo;
	private Date criadoEm;

	public AghMicroEquipamento() {
	}

	public AghMicroEquipamento(AghMicroEquipamentoId id, RapServidores rapServidores, AghMicrocomputador aghMicrocomputador,
			Integer codEqu, String equipamento, String status, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.aghMicrocomputador = aghMicrocomputador;
		this.codEqu = codEqu;
		this.equipamento = equipamento;
		this.status = status;
		this.criadoEm = criadoEm;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public AghMicroEquipamento(AghMicroEquipamentoId id, RapServidores rapServidores, AghMicrocomputador aghMicrocomputador,
			Integer codEqu, Integer codLeq, Integer codAss, String equipamento, String tipo, String modelo, String fabricante,
			String serie, String status, Date dataAquisicao, String notaFiscal, String fornecedor, Date dataGarantia, String obs,
			String motivo, String retornar, String patrimonio, Float tamanhoHd, Integer tamanhoMemoria, String numeroComodato,
			String proprietarioComodato, String ativo, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.aghMicrocomputador = aghMicrocomputador;
		this.codEqu = codEqu;
		this.codLeq = codLeq;
		this.codAss = codAss;
		this.equipamento = equipamento;
		this.tipo = tipo;
		this.modelo = modelo;
		this.fabricante = fabricante;
		this.serie = serie;
		this.status = status;
		this.dataAquisicao = dataAquisicao;
		this.notaFiscal = notaFiscal;
		this.fornecedor = fornecedor;
		this.dataGarantia = dataGarantia;
		this.obs = obs;
		this.motivo = motivo;
		this.retornar = retornar;
		this.patrimonio = patrimonio;
		this.tamanhoHd = tamanhoHd;
		this.tamanhoMemoria = tamanhoMemoria;
		this.numeroComodato = numeroComodato;
		this.proprietarioComodato = proprietarioComodato;
		this.ativo = ativo;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "micNome", column = @Column(name = "MIC_NOME", nullable = false, length = 50)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AghMicroEquipamentoId getId() {
		return this.id;
	}

	public void setId(AghMicroEquipamentoId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MIC_NOME", nullable = false, insertable = false, updatable = false)
	public AghMicrocomputador getAghMicrocomputador() {
		return this.aghMicrocomputador;
	}

	public void setAghMicrocomputador(AghMicrocomputador aghMicrocomputador) {
		this.aghMicrocomputador = aghMicrocomputador;
	}

	@Column(name = "COD_EQU", nullable = false)
	public Integer getCodEqu() {
		return this.codEqu;
	}

	public void setCodEqu(Integer codEqu) {
		this.codEqu = codEqu;
	}

	@Column(name = "COD_LEQ")
	public Integer getCodLeq() {
		return this.codLeq;
	}

	public void setCodLeq(Integer codLeq) {
		this.codLeq = codLeq;
	}

	@Column(name = "COD_ASS")
	public Integer getCodAss() {
		return this.codAss;
	}

	public void setCodAss(Integer codAss) {
		this.codAss = codAss;
	}

	@Column(name = "EQUIPAMENTO", nullable = false, length = 30)
	@Length(max = 30)
	public String getEquipamento() {
		return this.equipamento;
	}

	public void setEquipamento(String equipamento) {
		this.equipamento = equipamento;
	}

	@Column(name = "TIPO", length = 30)
	@Length(max = 30)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Column(name = "MODELO", length = 30)
	@Length(max = 30)
	public String getModelo() {
		return this.modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@Column(name = "FABRICANTE", length = 30)
	@Length(max = 30)
	public String getFabricante() {
		return this.fabricante;
	}

	public void setFabricante(String fabricante) {
		this.fabricante = fabricante;
	}

	@Column(name = "SERIE", length = 30)
	@Length(max = 30)
	public String getSerie() {
		return this.serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	@Column(name = "STATUS", nullable = false, length = 1)
	@Length(max = 1)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_AQUISICAO", length = 29)
	public Date getDataAquisicao() {
		return this.dataAquisicao;
	}

	public void setDataAquisicao(Date dataAquisicao) {
		this.dataAquisicao = dataAquisicao;
	}

	@Column(name = "NOTA_FISCAL", length = 20)
	@Length(max = 20)
	public String getNotaFiscal() {
		return this.notaFiscal;
	}

	public void setNotaFiscal(String notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	@Column(name = "FORNECEDOR", length = 30)
	@Length(max = 30)
	public String getFornecedor() {
		return this.fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_GARANTIA", length = 29)
	public Date getDataGarantia() {
		return this.dataGarantia;
	}

	public void setDataGarantia(Date dataGarantia) {
		this.dataGarantia = dataGarantia;
	}

	@Column(name = "OBS", length = 220)
	@Length(max = 220)
	public String getObs() {
		return this.obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	@Column(name = "MOTIVO", length = 220)
	@Length(max = 220)
	public String getMotivo() {
		return this.motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	@Column(name = "RETORNAR", length = 1)
	@Length(max = 1)
	public String getRetornar() {
		return this.retornar;
	}

	public void setRetornar(String retornar) {
		this.retornar = retornar;
	}

	@Column(name = "PATRIMONIO", unique = true, length = 30)
	@Length(max = 30)
	public String getPatrimonio() {
		return this.patrimonio;
	}

	public void setPatrimonio(String patrimonio) {
		this.patrimonio = patrimonio;
	}

	@Column(name = "TAMANHO_HD", precision = 8, scale = 8)
	public Float getTamanhoHd() {
		return this.tamanhoHd;
	}

	public void setTamanhoHd(Float tamanhoHd) {
		this.tamanhoHd = tamanhoHd;
	}

	@Column(name = "TAMANHO_MEMORIA")
	public Integer getTamanhoMemoria() {
		return this.tamanhoMemoria;
	}

	public void setTamanhoMemoria(Integer tamanhoMemoria) {
		this.tamanhoMemoria = tamanhoMemoria;
	}

	@Column(name = "NUMERO_COMODATO", length = 60)
	@Length(max = 60)
	public String getNumeroComodato() {
		return this.numeroComodato;
	}

	public void setNumeroComodato(String numeroComodato) {
		this.numeroComodato = numeroComodato;
	}

	@Column(name = "PROPRIETARIO_COMODATO", length = 60)
	@Length(max = 60)
	public String getProprietarioComodato() {
		return this.proprietarioComodato;
	}

	public void setProprietarioComodato(String proprietarioComodato) {
		this.proprietarioComodato = proprietarioComodato;
	}

	@Column(name = "ATIVO", length = 1)
	@Length(max = 1)
	public String getAtivo() {
		return this.ativo;
	}

	public void setAtivo(String ativo) {
		this.ativo = ativo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		AGH_MICROCOMPUTADOR("aghMicrocomputador"),
		COD_EQU("codEqu"),
		COD_LEQ("codLeq"),
		COD_ASS("codAss"),
		EQUIPAMENTO("equipamento"),
		TIPO("tipo"),
		MODELO("modelo"),
		FABRICANTE("fabricante"),
		SERIE("serie"),
		STATUS("status"),
		DATA_AQUISICAO("dataAquisicao"),
		NOTA_FISCAL("notaFiscal"),
		FORNECEDOR("fornecedor"),
		DATA_GARANTIA("dataGarantia"),
		OBS("obs"),
		MOTIVO("motivo"),
		RETORNAR("retornar"),
		PATRIMONIO("patrimonio"),
		TAMANHO_HD("tamanhoHd"),
		TAMANHO_MEMORIA("tamanhoMemoria"),
		NUMERO_COMODATO("numeroComodato"),
		PROPRIETARIO_COMODATO("proprietarioComodato"),
		ATIVO("ativo"),
		CRIADO_EM("criadoEm");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof AghMicroEquipamento)) {
			return false;
		}
		AghMicroEquipamento other = (AghMicroEquipamento) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
