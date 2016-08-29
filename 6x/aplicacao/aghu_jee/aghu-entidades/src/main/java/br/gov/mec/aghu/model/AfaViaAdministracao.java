package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.view.VMpmViaAdm;
import br.gov.mec.aghu.core.persistence.BaseEntity;


@Entity
@Table(name = "AFA_VIA_ADMINISTRACOES", schema = "AGH")
public class AfaViaAdministracao implements BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2144147055728831620L;

	private String sigla;

	private String descricao;
	private Boolean indUsoNutricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Boolean indUsoQuimioterapia;
	private Boolean indAplicaQuimioCca;
	private Boolean indPermiteBi;
	private Date tempo;
	private RapServidores servidor;
	private Set<VMpmViaAdm> viewMpmViasAdm = new HashSet<VMpmViaAdm>(0);
	private Set<AfaViaAdmUnf> viasAdmUnf = new HashSet<AfaViaAdmUnf>(0);
	private Set<AfaViaAdministracaoMedicamento> viaAdministracaoMedicamento = new HashSet<AfaViaAdministracaoMedicamento>();
	
	// construtores
	public AfaViaAdministracao() {
	}

	// getters & setters
	@Id
	@Column(name = "SIGLA", length = 2, nullable = false)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_USO_NUTRICAO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoNutricao() {
		return indUsoNutricao;
	}

	public void setIndUsoNutricao(Boolean indUsoNutricao) {
		this.indUsoNutricao = indUsoNutricao;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO")
	public Date getTempo() {
		return this.tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	@Column(name = "IND_USO_QUIMIOTERAPIA", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoQuimioterapia() {
		return indUsoQuimioterapia;
	}

	public void setIndUsoQuimioterapia(Boolean indUsoQuimioterapia) {
		this.indUsoQuimioterapia = indUsoQuimioterapia;
	}

	@Column(name = "IND_APLICA_QUIMIO_CCA", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAplicaQuimioCca() {
		return indAplicaQuimioCca;
	}

	public void setIndAplicaQuimioCca(Boolean indAplicaQuimioCca) {
		this.indAplicaQuimioCca = indAplicaQuimioCca;
	}

	@Column(name = "IND_PERMITE_BI", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPermiteBi() {
		return indPermiteBi;
	}

	public void setIndPermiteBi(Boolean indPermiteBi) {
		this.indPermiteBi = indPermiteBi;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "viaAdministracao")
	public Set<VMpmViaAdm> getViewMpmViasAdm() {
		return viewMpmViasAdm;
	}

	public void setViewMpmViasAdm(Set<VMpmViaAdm> viewMpmViasAdm) {
		this.viewMpmViasAdm = viewMpmViasAdm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "viaAdministracao")
	public Set<AfaViaAdmUnf> getViasAdmUnf() {
		return viasAdmUnf;
	}

	public void setViasAdmUnf(Set<AfaViaAdmUnf> viasAdmUnf) {
		this.viasAdmUnf = viasAdmUnf;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "viaAdministracao")
	public Set<AfaViaAdministracaoMedicamento> getViaAdministracaoMedicamento() {
		return viaAdministracaoMedicamento;
	}
	
	public void setViaAdministracaoMedicamento(Set<AfaViaAdministracaoMedicamento> viaAdministracaoMedicamento) {
		this.viaAdministracaoMedicamento = viaAdministracaoMedicamento;
	}

	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("sigla", this.sigla).toString();
	}


	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AfaViaAdministracao)) {
			return false;
		}
		AfaViaAdministracao castOther = (AfaViaAdministracao) other;
		return new EqualsBuilder().append(this.sigla, castOther.getSigla())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.sigla).toHashCode();
	}
	
	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validacoes() {
		if (this.indUsoNutricao == null) {
			this.indUsoNutricao = Boolean.FALSE;
		}
		
		if (this.indSituacao == null) {
			this.indSituacao = DominioSituacao.A;
		}
		
		if (this.indUsoQuimioterapia == null) {
			this.indUsoQuimioterapia = Boolean.FALSE;
		}
		
		if (this.indAplicaQuimioCca == null) {
			this.indAplicaQuimioCca = Boolean.FALSE;
		}
		
		if (this.indPermiteBi == null) {
			this.indPermiteBi = Boolean.FALSE;
		}
	}
	
	public enum Fields {
		SIGLA("sigla"), DESCRICAO("descricao"), IND_USO_NUTRICAO(
				"indUsoNutricao"), IND_SITUACAO("indSituacao"), CRIADO_EM(
				"criadoEm"), IND_USO_QUIMIOTERAPIA("indUsoQuimioterapia"), IND_APLICA_QUIMIO_CCA(
				"indAplicaQuimioCca"), IND_PERMITE_BI("indPermiteBi"), TEMPO(
				"tempo"), SERVIDOR("servidor"), 
				VIEW_MPM_VIAS_ADM("viewMpmViasAdm"),
				VIAS_ADM_UNF("viasAdmUnf"),
				VIA_ADM_MEDICAMENTOS("viaAdministracaoMedicamento");

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
