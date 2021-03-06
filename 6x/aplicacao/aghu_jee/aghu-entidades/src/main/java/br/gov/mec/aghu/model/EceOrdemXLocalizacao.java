package br.gov.mec.aghu.model;

// Generated 27/12/2010 20:08:36 by Hibernate Tools 3.3.0.GA

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * EceOrdemXLocalizacao generated by hbm2java
 */
@Entity
@SequenceGenerator(name="eceOxlSq1", sequenceName="AGH.ECE_OXL_SQ1", allocationSize = 1)
@Table(name = "ECE_ORDEM_X_LOCALIZACAO", schema = "AGH")

public class EceOrdemXLocalizacao extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7186244822672067331L;
	private Long seq;
	private EceOrdemDeAdministracao ordemAdministracao;
	private AghUnidadesFuncionais unidadesFuncionais;
	private AinQuartos quarto;
	private AinLeitos leito;  
	private DominioSituacao situacao;
	private Date criadoEm;

	public EceOrdemXLocalizacao() {
	}

	public EceOrdemXLocalizacao(Long seq,
			EceOrdemDeAdministracao ordemAdministracao, AghUnidadesFuncionais unidadesFuncionais,
			DominioSituacao situacao, Date criadoEm) {
		this.seq = seq;
		this.ordemAdministracao = ordemAdministracao;
		this.unidadesFuncionais = unidadesFuncionais;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
	}

	public EceOrdemXLocalizacao(Long seq,
			EceOrdemDeAdministracao ordemAdministracao, AghUnidadesFuncionais unidadesFuncionais,
			AinQuartos quarto, AinLeitos ainLeito, DominioSituacao situacao, Date criadoEm) {
		this.seq = seq;
		this.ordemAdministracao = ordemAdministracao;
		this.unidadesFuncionais = unidadesFuncionais;
		this.quarto = quarto;
		this.leito = ainLeito;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "eceOxlSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 12, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ODA_SEQ", nullable = false)
	public EceOrdemDeAdministracao getOrdemAdministracao() {
		return this.ordemAdministracao;
	}

	public void setOrdemAdministracao(
			EceOrdemDeAdministracao ordemAdministracao) {
		this.ordemAdministracao = ordemAdministracao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ")
	public AghUnidadesFuncionais getUnidadesFuncionais() {
		return unidadesFuncionais;
	}

	public void setUnidadesFuncionais(AghUnidadesFuncionais unidadesFuncionais) {
		this.unidadesFuncionais = unidadesFuncionais;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QRT_NUMERO")
	public AinQuartos  getQuarto() {
		return this.quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LTO_LTO_ID")
	public AinLeitos getLeito() {
		return this.leito;
	}

	public void setLeito(AinLeitos Leito) {
		this.leito = Leito;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		EceOrdemXLocalizacao other = (EceOrdemXLocalizacao) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	public enum Fields {

		SEQ("seq"),
		ORDEM_ADMINISTRACAO("ordemAdministracao"),
		ORDEM_ADMINISTRACAO_ID("ordemAdministracao.seq"),
		UNF_SEQ("unidadesFuncionais.seq"),
		SITUACAO("situacao"),
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

}
