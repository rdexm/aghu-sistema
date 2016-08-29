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
@Table(name = "ANU_DIETA_NUTRICOES", schema = "AGH")
public class AnuDietaNutricao extends BaseEntityId<AnuDietaNutricaoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8786703506714982459L;
	private AnuDietaNutricaoId id;
	private Integer version;
	private RapServidores rapServidoresByAnuDnuSerFk1;
	private RapServidores rapServidoresByAnuDnuSerFk2;
	private RapServidores rapServidoresByAnuDnuSerFk3;
	private AnuEsqEspecialDietaUsual anuEsqEspecialDietaUsual;
	private AghAtendimentos aghAtendimentos;
	private AfaViaAdministracao afaViaAdministracao;
	private String tipoClassificacao;
	private String indSuspenso;
	private Date criadoEm;
	private Date dthrInicio;
	private Date dthrFim;
	private String dietaAplicada;
	private String observacao;
	private Date alteradoEm;
	private Set<AnuAlimentacaoComplementar> anuAlimentacaoComplementar = new HashSet<AnuAlimentacaoComplementar>(0);
	private Set<AnuHidratanteAplicado> anuHidratanteAplicadoes = new HashSet<AnuHidratanteAplicado>(0);

	public AnuDietaNutricao() {
	}

	public AnuDietaNutricao(AnuDietaNutricaoId id, RapServidores rapServidoresByAnuDnuSerFk1, AghAtendimentos aghAtendimentos,
			AfaViaAdministracao afaViaAdministracao, String tipoClassificacao, Date criadoEm, Date dthrInicio) {
		this.id = id;
		this.rapServidoresByAnuDnuSerFk1 = rapServidoresByAnuDnuSerFk1;
		this.aghAtendimentos = aghAtendimentos;
		this.afaViaAdministracao = afaViaAdministracao;
		this.tipoClassificacao = tipoClassificacao;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
	}

	public AnuDietaNutricao(AnuDietaNutricaoId id, RapServidores rapServidoresByAnuDnuSerFk1,
			RapServidores rapServidoresByAnuDnuSerFk2, RapServidores rapServidoresByAnuDnuSerFk3,
			AnuEsqEspecialDietaUsual anuEsqEspecialDietaUsual, AghAtendimentos aghAtendimentos,
			AfaViaAdministracao afaViaAdministracao, String tipoClassificacao, String indSuspenso, Date criadoEm, Date dthrInicio,
			Date dthrFim, String dietaAplicada, String observacao, Date alteradoEm,
			Set<AnuAlimentacaoComplementar> anuAlimentacaoComplementar, Set<AnuHidratanteAplicado> anuHidratanteAplicadoes) {
		this.id = id;
		this.rapServidoresByAnuDnuSerFk1 = rapServidoresByAnuDnuSerFk1;
		this.rapServidoresByAnuDnuSerFk2 = rapServidoresByAnuDnuSerFk2;
		this.rapServidoresByAnuDnuSerFk3 = rapServidoresByAnuDnuSerFk3;
		this.anuEsqEspecialDietaUsual = anuEsqEspecialDietaUsual;
		this.aghAtendimentos = aghAtendimentos;
		this.afaViaAdministracao = afaViaAdministracao;
		this.tipoClassificacao = tipoClassificacao;
		this.indSuspenso = indSuspenso;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.dietaAplicada = dietaAplicada;
		this.observacao = observacao;
		this.alteradoEm = alteradoEm;
		this.anuAlimentacaoComplementar = anuAlimentacaoComplementar;
		this.anuHidratanteAplicadoes = anuHidratanteAplicadoes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuDietaNutricaoId getId() {
		return this.id;
	}

	public void setId(AnuDietaNutricaoId id) {
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
	public RapServidores getRapServidoresByAnuDnuSerFk1() {
		return this.rapServidoresByAnuDnuSerFk1;
	}

	public void setRapServidoresByAnuDnuSerFk1(RapServidores rapServidoresByAnuDnuSerFk1) {
		this.rapServidoresByAnuDnuSerFk1 = rapServidoresByAnuDnuSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuDnuSerFk2() {
		return this.rapServidoresByAnuDnuSerFk2;
	}

	public void setRapServidoresByAnuDnuSerFk2(RapServidores rapServidoresByAnuDnuSerFk2) {
		this.rapServidoresByAnuDnuSerFk2 = rapServidoresByAnuDnuSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuDnuSerFk3() {
		return this.rapServidoresByAnuDnuSerFk3;
	}

	public void setRapServidoresByAnuDnuSerFk3(RapServidores rapServidoresByAnuDnuSerFk3) {
		this.rapServidoresByAnuDnuSerFk3 = rapServidoresByAnuDnuSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EDU_SEQ")
	public AnuEsqEspecialDietaUsual getAnuEsqEspecialDietaUsual() {
		return this.anuEsqEspecialDietaUsual;
	}

	public void setAnuEsqEspecialDietaUsual(AnuEsqEspecialDietaUsual anuEsqEspecialDietaUsual) {
		this.anuEsqEspecialDietaUsual = anuEsqEspecialDietaUsual;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ", nullable = false, insertable = false, updatable = false)
	public AghAtendimentos getAghAtendimentos() {
		return this.aghAtendimentos;
	}

	public void setAghAtendimentos(AghAtendimentos aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VAD_SIGLA", nullable = false)
	public AfaViaAdministracao getAfaViaAdministracao() {
		return this.afaViaAdministracao;
	}

	public void setAfaViaAdministracao(AfaViaAdministracao afaViaAdministracao) {
		this.afaViaAdministracao = afaViaAdministracao;
	}

	@Column(name = "TIPO_CLASSIFICACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getTipoClassificacao() {
		return this.tipoClassificacao;
	}

	public void setTipoClassificacao(String tipoClassificacao) {
		this.tipoClassificacao = tipoClassificacao;
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

	@Column(name = "DIETA_APLICADA", length = 60)
	@Length(max = 60)
	public String getDietaAplicada() {
		return this.dietaAplicada;
	}

	public void setDietaAplicada(String dietaAplicada) {
		this.dietaAplicada = dietaAplicada;
	}

	@Column(name = "OBSERVACAO", length = 240)
	@Length(max = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuDietaNutricao")
	public Set<AnuAlimentacaoComplementar> getAnuAlimentacaoComplementar() {
		return this.anuAlimentacaoComplementar;
	}

	public void setAnuAlimentacaoComplementar(Set<AnuAlimentacaoComplementar> anuAlimentacaoComplementar) {
		this.anuAlimentacaoComplementar = anuAlimentacaoComplementar;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuDietaNutricao")
	public Set<AnuHidratanteAplicado> getAnuHidratanteAplicadoes() {
		return this.anuHidratanteAplicadoes;
	}

	public void setAnuHidratanteAplicadoes(Set<AnuHidratanteAplicado> anuHidratanteAplicadoes) {
		this.anuHidratanteAplicadoes = anuHidratanteAplicadoes;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES_BY_ANU_DNU_SER_FK1("rapServidoresByAnuDnuSerFk1"),
		RAP_SERVIDORES_BY_ANU_DNU_SER_FK2("rapServidoresByAnuDnuSerFk2"),
		RAP_SERVIDORES_BY_ANU_DNU_SER_FK3("rapServidoresByAnuDnuSerFk3"),
		ANU_ESQ_ESPECIAL_DIETA_USUAIS("anuEsqEspecialDietaUsual"),
		AGH_ATENDIMENTOS("aghAtendimentos"),
		AFA_VIA_ADMINISTRACAO("afaViaAdministracao"),
		TIPO_CLASSIFICACAO("tipoClassificacao"),
		IND_SUSPENSO("indSuspenso"),
		CRIADO_EM("criadoEm"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		DIETA_APLICADA("dietaAplicada"),
		OBSERVACAO("observacao"),
		ALTERADO_EM("alteradoEm"),
		ANU_ALIMENTACAO_COMPLEMENTARES("anuAlimentacaoComplementar"),
		ANU_HIDRATANTE_APLICADOES("anuHidratanteAplicadoes");

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
		if (!(obj instanceof AnuDietaNutricao)) {
			return false;
		}
		AnuDietaNutricao other = (AnuDietaNutricao) obj;
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
