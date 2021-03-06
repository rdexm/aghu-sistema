package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * AnuPrescricaoDietaIg generated by hbm2java
 */
@Entity
@Table(name = "ANU_PRESCRICAO_DIETA_IG", schema = "AGH")
public class AnuPrescricaoDietaIg extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7353999172000591215L;
	private Integer seq;
	private Integer version;
	private RapServidores rapServidores;
	private AnuGrupoQuadroDieta anuGrupoQuadroDieta;
	private AghUnidadesFuncionais aghUnidadesFuncionais;
	private AnuHabitoAlimUsual anuHabitoAlimUsual;
	private Date dtCompetencia;
	private Date criadoEm;
	private Date alteradoEm;
	private String perfilAtendimento;
	private String tipoPrescricao;
	private Integer quantidade;
	private String sus;

	public AnuPrescricaoDietaIg() {
	}

	public AnuPrescricaoDietaIg(Integer seq, RapServidores rapServidores, AnuGrupoQuadroDieta anuGrupoQuadroDieta,
			AghUnidadesFuncionais aghUnidadesFuncionais, Date dtCompetencia, String perfilAtendimento, String tipoPrescricao,
			Integer quantidade) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.anuGrupoQuadroDieta = anuGrupoQuadroDieta;
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
		this.dtCompetencia = dtCompetencia;
		this.perfilAtendimento = perfilAtendimento;
		this.tipoPrescricao = tipoPrescricao;
		this.quantidade = quantidade;
	}

	public AnuPrescricaoDietaIg(Integer seq, RapServidores rapServidores, AnuGrupoQuadroDieta anuGrupoQuadroDieta,
			AghUnidadesFuncionais aghUnidadesFuncionais, AnuHabitoAlimUsual anuHabitoAlimUsual, Date dtCompetencia, Date criadoEm,
			Date alteradoEm, String perfilAtendimento, String tipoPrescricao, Integer quantidade, String sus) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.anuGrupoQuadroDieta = anuGrupoQuadroDieta;
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
		this.anuHabitoAlimUsual = anuHabitoAlimUsual;
		this.dtCompetencia = dtCompetencia;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.perfilAtendimento = perfilAtendimento;
		this.tipoPrescricao = tipoPrescricao;
		this.quantidade = quantidade;
		this.sus = sus;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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
	@JoinColumn(name = "GQD_SEQ", nullable = false)
	public AnuGrupoQuadroDieta getAnuGrupoQuadroDieta() {
		return this.anuGrupoQuadroDieta;
	}

	public void setAnuGrupoQuadroDieta(AnuGrupoQuadroDieta anuGrupoQuadroDieta) {
		this.anuGrupoQuadroDieta = anuGrupoQuadroDieta;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", nullable = false)
	public AghUnidadesFuncionais getAghUnidadesFuncionais() {
		return this.aghUnidadesFuncionais;
	}

	public void setAghUnidadesFuncionais(AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HAU_SEQ")
	public AnuHabitoAlimUsual getAnuHabitoAlimUsual() {
		return this.anuHabitoAlimUsual;
	}

	public void setAnuHabitoAlimUsual(AnuHabitoAlimUsual anuHabitoAlimUsual) {
		this.anuHabitoAlimUsual = anuHabitoAlimUsual;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_COMPETENCIA", nullable = false, length = 29)
	public Date getDtCompetencia() {
		return this.dtCompetencia;
	}

	public void setDtCompetencia(Date dtCompetencia) {
		this.dtCompetencia = dtCompetencia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "PERFIL_ATENDIMENTO", nullable = false, length = 20)
	@Length(max = 20)
	public String getPerfilAtendimento() {
		return this.perfilAtendimento;
	}

	public void setPerfilAtendimento(String perfilAtendimento) {
		this.perfilAtendimento = perfilAtendimento;
	}

	@Column(name = "TIPO_PRESCRICAO", nullable = false, length = 20)
	@Length(max = 20)
	public String getTipoPrescricao() {
		return this.tipoPrescricao;
	}

	public void setTipoPrescricao(String tipoPrescricao) {
		this.tipoPrescricao = tipoPrescricao;
	}

	@Column(name = "QUANTIDADE", nullable = false)
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "SUS", length = 1)
	@Length(max = 1)
	public String getSus() {
		return this.sus;
	}

	public void setSus(String sus) {
		this.sus = sus;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		ANU_GRUPO_QUADRO_DIETAS("anuGrupoQuadroDieta"),
		AGH_UNIDADES_FUNCIONAIS("aghUnidadesFuncionais"),
		ANU_HABITO_ALIM_USUAIS("anuHabitoAlimUsual"),
		DT_COMPETENCIA("dtCompetencia"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		PERFIL_ATENDIMENTO("perfilAtendimento"),
		TIPO_PRESCRICAO("tipoPrescricao"),
		QUANTIDADE("quantidade"),
		SUS("sus");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof AnuPrescricaoDietaIg)) {
			return false;
		}
		AnuPrescricaoDietaIg other = (AnuPrescricaoDietaIg) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
