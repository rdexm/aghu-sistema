package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "ANU_ALIMENTACAO_COMPLEMENTARES", schema = "AGH")
public class AnuAlimentacaoComplementar extends BaseEntityId<AnuAlimentacaoComplementarId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4073155888093774826L;
	private AnuAlimentacaoComplementarId id;
	private Integer version;
	private RapServidores rapServidoresByAnuAmiSerFk1;
	private RapServidores rapServidoresByAnuAmiSerFk2;
	private AnuDietaNutricao anuDietaNutricao;
	private RapServidores rapServidoresByAnuAmiSerFk3;
	private MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica;
	private AnuTipoAlimentacao anuTipoAlimentacao;
	private Date criadoEm;
	private Date dthrInicio;
	private Date dthrFim;
	private Short quantidade;
	private Short qtdeAdicional;
	private String observacao;
	private String indSuspenso;
	private Date alteradoEm;
	private Set<AnuHorarioRefeicao> anuHorarioRefeicaoes = new HashSet<AnuHorarioRefeicao>(0);

	public AnuAlimentacaoComplementar() {
	}

	public AnuAlimentacaoComplementar(AnuAlimentacaoComplementarId id, RapServidores rapServidoresByAnuAmiSerFk1,
			AnuDietaNutricao anuDietaNutricao, AnuTipoAlimentacao anuTipoAlimentacao, Date criadoEm, Date dthrInicio) {
		this.id = id;
		this.rapServidoresByAnuAmiSerFk1 = rapServidoresByAnuAmiSerFk1;
		this.anuDietaNutricao = anuDietaNutricao;
		this.anuTipoAlimentacao = anuTipoAlimentacao;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
	}

	public AnuAlimentacaoComplementar(AnuAlimentacaoComplementarId id, RapServidores rapServidoresByAnuAmiSerFk1,
			RapServidores rapServidoresByAnuAmiSerFk2, AnuDietaNutricao anuDietaNutricao, RapServidores rapServidoresByAnuAmiSerFk3,
			MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica, AnuTipoAlimentacao anuTipoAlimentacao, Date criadoEm, Date dthrInicio,
			Date dthrFim, Short quantidade, Short qtdeAdicional, String observacao, String indSuspenso, Date alteradoEm,
			Set<AnuHorarioRefeicao> anuHorarioRefeicaoes) {
		this.id = id;
		this.rapServidoresByAnuAmiSerFk1 = rapServidoresByAnuAmiSerFk1;
		this.rapServidoresByAnuAmiSerFk2 = rapServidoresByAnuAmiSerFk2;
		this.anuDietaNutricao = anuDietaNutricao;
		this.rapServidoresByAnuAmiSerFk3 = rapServidoresByAnuAmiSerFk3;
		this.mpmUnidadeMedidaMedica = mpmUnidadeMedidaMedica;
		this.anuTipoAlimentacao = anuTipoAlimentacao;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.quantidade = quantidade;
		this.qtdeAdicional = qtdeAdicional;
		this.observacao = observacao;
		this.indSuspenso = indSuspenso;
		this.alteradoEm = alteradoEm;
		this.anuHorarioRefeicaoes = anuHorarioRefeicaoes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "dnuAtdSeq", column = @Column(name = "DNU_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "dnuSeq", column = @Column(name = "DNU_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuAlimentacaoComplementarId getId() {
		return this.id;
	}

	public void setId(AnuAlimentacaoComplementarId id) {
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
	public RapServidores getRapServidoresByAnuAmiSerFk1() {
		return this.rapServidoresByAnuAmiSerFk1;
	}

	public void setRapServidoresByAnuAmiSerFk1(RapServidores rapServidoresByAnuAmiSerFk1) {
		this.rapServidoresByAnuAmiSerFk1 = rapServidoresByAnuAmiSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuAmiSerFk2() {
		return this.rapServidoresByAnuAmiSerFk2;
	}

	public void setRapServidoresByAnuAmiSerFk2(RapServidores rapServidoresByAnuAmiSerFk2) {
		this.rapServidoresByAnuAmiSerFk2 = rapServidoresByAnuAmiSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "DNU_ATD_SEQ", referencedColumnName = "ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "DNU_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public AnuDietaNutricao getAnuDietaNutricao() {
		return this.anuDietaNutricao;
	}

	public void setAnuDietaNutricao(AnuDietaNutricao anuDietaNutricao) {
		this.anuDietaNutricao = anuDietaNutricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuAmiSerFk3() {
		return this.rapServidoresByAnuAmiSerFk3;
	}

	public void setRapServidoresByAnuAmiSerFk3(RapServidores rapServidoresByAnuAmiSerFk3) {
		this.rapServidoresByAnuAmiSerFk3 = rapServidoresByAnuAmiSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMM_SEQ")
	public MpmUnidadeMedidaMedica getMpmUnidadeMedidaMedica() {
		return this.mpmUnidadeMedidaMedica;
	}

	public void setMpmUnidadeMedidaMedica(MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica) {
		this.mpmUnidadeMedidaMedica = mpmUnidadeMedidaMedica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TAL_SEQ", nullable = false)
	public AnuTipoAlimentacao getAnuTipoAlimentacao() {
		return this.anuTipoAlimentacao;
	}

	public void setAnuTipoAlimentacao(AnuTipoAlimentacao anuTipoAlimentacao) {
		this.anuTipoAlimentacao = anuTipoAlimentacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", nullable = false, length = 29)
	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", length = 29)
	public Date getDthrFim() {
		return this.dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@Column(name = "QUANTIDADE")
	public Short getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "QTDE_ADICIONAL")
	public Short getQtdeAdicional() {
		return this.qtdeAdicional;
	}

	public void setQtdeAdicional(Short qtdeAdicional) {
		this.qtdeAdicional = qtdeAdicional;
	}

	@Column(name = "OBSERVACAO", length = 240)
	@Length(max = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "IND_SUSPENSO", length = 1)
	@Length(max = 1)
	public String getIndSuspenso() {
		return this.indSuspenso;
	}

	public void setIndSuspenso(String indSuspenso) {
		this.indSuspenso = indSuspenso;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuAlimentacaoComplementar")
	public Set<AnuHorarioRefeicao> getAnuHorarioRefeicaoes() {
		return this.anuHorarioRefeicaoes;
	}

	public void setAnuHorarioRefeicaoes(Set<AnuHorarioRefeicao> anuHorarioRefeicaoes) {
		this.anuHorarioRefeicaoes = anuHorarioRefeicaoes;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES_BY_ANU_AMI_SER_FK1("rapServidoresByAnuAmiSerFk1"),
		RAP_SERVIDORES_BY_ANU_AMI_SER_FK2("rapServidoresByAnuAmiSerFk2"),
		ANU_DIETA_NUTRICOES("anuDietaNutricao"),
		RAP_SERVIDORES_BY_ANU_AMI_SER_FK3("rapServidoresByAnuAmiSerFk3"),
		MPM_UNIDADE_MEDIDA_MEDICA("mpmUnidadeMedidaMedica"),
		ANU_TIPO_ALIMENTACOES("anuTipoAlimentacao"),
		CRIADO_EM("criadoEm"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		QUANTIDADE("quantidade"),
		QTDE_ADICIONAL("qtdeAdicional"),
		OBSERVACAO("observacao"),
		IND_SUSPENSO("indSuspenso"),
		ALTERADO_EM("alteradoEm"),
		ANU_HORARIO_REFEICAOES("anuHorarioRefeicaoes");

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
		if (!(obj instanceof AnuAlimentacaoComplementar)) {
			return false;
		}
		AnuAlimentacaoComplementar other = (AnuAlimentacaoComplementar) obj;
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
