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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

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
@Table(name = "ABS_SITUACOES_APRAZAMENTOS", schema = "AGH")
public class AbsSituacaoAprazamento extends BaseEntityCodigo<String> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2780633255290552350L;
	private String codigo;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private String indComponente;
	private String indProcedimento;
	private String indEnfermagem;
	private String indPendBancoSangue;
	private String indSituacao;
	private Date criadoEm;

	public AbsSituacaoAprazamento() {
	}

	public AbsSituacaoAprazamento(String codigo, String descricao, String indComponente, String indProcedimento,
			String indEnfermagem, String indPendBancoSangue, String indSituacao) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.indComponente = indComponente;
		this.indProcedimento = indProcedimento;
		this.indEnfermagem = indEnfermagem;
		this.indPendBancoSangue = indPendBancoSangue;
		this.indSituacao = indSituacao;
	}

	public AbsSituacaoAprazamento(String codigo, RapServidores rapServidores, String descricao, String indComponente,
			String indProcedimento, String indEnfermagem, String indPendBancoSangue, String indSituacao, Date criadoEm) {
		this.codigo = codigo;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.indComponente = indComponente;
		this.indProcedimento = indProcedimento;
		this.indEnfermagem = indEnfermagem;
		this.indPendBancoSangue = indPendBancoSangue;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false, length = 2)
	@Length(max = 2)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_COMPONENTE", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndComponente() {
		return this.indComponente;
	}

	public void setIndComponente(String indComponente) {
		this.indComponente = indComponente;
	}

	@Column(name = "IND_PROCEDIMENTO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndProcedimento() {
		return this.indProcedimento;
	}

	public void setIndProcedimento(String indProcedimento) {
		this.indProcedimento = indProcedimento;
	}

	@Column(name = "IND_ENFERMAGEM", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndEnfermagem() {
		return this.indEnfermagem;
	}

	public void setIndEnfermagem(String indEnfermagem) {
		this.indEnfermagem = indEnfermagem;
	}

	@Column(name = "IND_PEND_BANCO_SANGUE", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndPendBancoSangue() {
		return this.indPendBancoSangue;
	}

	public void setIndPendBancoSangue(String indPendBancoSangue) {
		this.indPendBancoSangue = indPendBancoSangue;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		CODIGO("codigo"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		IND_COMPONENTE("indComponente"),
		IND_PROCEDIMENTO("indProcedimento"),
		IND_ENFERMAGEM("indEnfermagem"),
		IND_PEND_BANCO_SANGUE("indPendBancoSangue"),
		IND_SITUACAO("indSituacao"),
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
		result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof AbsSituacaoAprazamento)) {
			return false;
		}
		AbsSituacaoAprazamento other = (AbsSituacaoAprazamento) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
