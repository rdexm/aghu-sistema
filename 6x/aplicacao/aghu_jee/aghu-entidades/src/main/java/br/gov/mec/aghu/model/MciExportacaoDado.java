package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

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
@SequenceGenerator(name="mciEdaSq1", sequenceName="AGH.MCI_EDA_SQ1", allocationSize = 1)
@Table(name = "MCI_EXPORTACAO_DADOS", schema = "AGH")
public class MciExportacaoDado extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6680585135074070900L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private String procedureExecucao;
	private String nomeRelatorio;
	private String nomeArquivo;
	private Short nroCopiasRelatorio;
	private Set<MciParamReportGrupo> mciParamReportGrupoes = new HashSet<MciParamReportGrupo>(0);
	private Set<MciLinhaExportacaoDado> mciLinhaExportacaoDadoes = new HashSet<MciLinhaExportacaoDado>(0);
	private Set<MciProcessaExportacao> mciProcessaExportacaoes = new HashSet<MciProcessaExportacao>(0);

	public MciExportacaoDado() {
	}

	public MciExportacaoDado(Short seq, RapServidores rapServidores, String descricao, Date criadoEm, DominioSituacao indSituacao,
			String procedureExecucao, String nomeRelatorio, String nomeArquivo, Short nroCopiasRelatorio) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.procedureExecucao = procedureExecucao;
		this.nomeRelatorio = nomeRelatorio;
		this.nomeArquivo = nomeArquivo;
		this.nroCopiasRelatorio = nroCopiasRelatorio;
	}

	public MciExportacaoDado(Short seq, RapServidores rapServidores, String descricao, Date criadoEm, DominioSituacao indSituacao,
			String procedureExecucao, String nomeRelatorio, String nomeArquivo, Short nroCopiasRelatorio,
			Set<MciParamReportGrupo> mciParamReportGrupoes, Set<MciLinhaExportacaoDado> mciLinhaExportacaoDadoes,
			Set<MciProcessaExportacao> mciProcessaExportacaoes) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.procedureExecucao = procedureExecucao;
		this.nomeRelatorio = nomeRelatorio;
		this.nomeArquivo = nomeArquivo;
		this.nroCopiasRelatorio = nroCopiasRelatorio;
		this.mciParamReportGrupoes = mciParamReportGrupoes;
		this.mciLinhaExportacaoDadoes = mciLinhaExportacaoDadoes;
		this.mciProcessaExportacaoes = mciProcessaExportacaoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciEdaSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
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

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "PROCEDURE_EXECUCAO", nullable = false, length = 20)
	@Length(max = 20)
	public String getProcedureExecucao() {
		return this.procedureExecucao;
	}

	public void setProcedureExecucao(String procedureExecucao) {
		this.procedureExecucao = procedureExecucao;
	}

	@Column(name = "NOME_RELATORIO", nullable = false, length = 20)
	@Length(max = 20)
	public String getNomeRelatorio() {
		return this.nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	@Column(name = "NOME_ARQUIVO", nullable = false, length = 20)
	@Length(max = 20)
	public String getNomeArquivo() {
		return this.nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	@Column(name = "NRO_COPIAS_RELATORIO", nullable = false)
	public Short getNroCopiasRelatorio() {
		return this.nroCopiasRelatorio;
	}

	public void setNroCopiasRelatorio(Short nroCopiasRelatorio) {
		this.nroCopiasRelatorio = nroCopiasRelatorio;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mciExportacaoDado")
	public Set<MciParamReportGrupo> getMciParamReportGrupoes() {
		return this.mciParamReportGrupoes;
	}

	public void setMciParamReportGrupoes(Set<MciParamReportGrupo> mciParamReportGrupoes) {
		this.mciParamReportGrupoes = mciParamReportGrupoes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mciExportacaoDado")
	public Set<MciLinhaExportacaoDado> getMciLinhaExportacaoDadoes() {
		return this.mciLinhaExportacaoDadoes;
	}

	public void setMciLinhaExportacaoDadoes(Set<MciLinhaExportacaoDado> mciLinhaExportacaoDadoes) {
		this.mciLinhaExportacaoDadoes = mciLinhaExportacaoDadoes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mciExportacaoDado")
	public Set<MciProcessaExportacao> getMciProcessaExportacaoes() {
		return this.mciProcessaExportacaoes;
	}

	public void setMciProcessaExportacaoes(Set<MciProcessaExportacao> mciProcessaExportacaoes) {
		this.mciProcessaExportacaoes = mciProcessaExportacaoes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		PROCEDURE_EXECUCAO("procedureExecucao"),
		NOME_RELATORIO("nomeRelatorio"),
		NOME_ARQUIVO("nomeArquivo"),
		NRO_COPIAS_RELATORIO("nroCopiasRelatorio"),
		MCI_PARAM_REPORT_GRUPOES("mciParamReportGrupoes"),
		MCI_LINHA_EXPORTACAO_DADOES("mciLinhaExportacaoDadoes"),
		MCI_PROCESSA_EXPORTACAOES("mciProcessaExportacaoes");

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
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.seq);
		umHashCodeBuilder.append(this.rapServidores);
		umHashCodeBuilder.append(this.descricao);
		umHashCodeBuilder.append(this.criadoEm);
		umHashCodeBuilder.append(this.indSituacao);
		umHashCodeBuilder.append(this.procedureExecucao);
		umHashCodeBuilder.append(this.nomeRelatorio);
		umHashCodeBuilder.append(this.nomeArquivo);
		umHashCodeBuilder.append(this.nroCopiasRelatorio);
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MciExportacaoDado)) {
			return false;
		}
		MciExportacaoDado other = (MciExportacaoDado) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getDescricao(), other.getDescricao());
		equalsBuilder.append(this.seq, other.getSeq());
		equalsBuilder.append(this.descricao, other.getDescricao());
		equalsBuilder.append(this.criadoEm, other.getCriadoEm());
		equalsBuilder.append(this.indSituacao, other.getIndSituacao());
		equalsBuilder.append(this.procedureExecucao, other.getProcedureExecucao());
		equalsBuilder.append(this.nomeRelatorio, other.getNomeRelatorio());
		equalsBuilder.append(this.nomeArquivo, other.getNomeArquivo());
		equalsBuilder.append(this.nroCopiasRelatorio, other.getNroCopiasRelatorio());
		return equalsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####

}
