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
@Table(name = "CCE_AMOSTRAS_CONTAGEM", schema = "AGH")
public class CceAmostraContagem extends BaseEntityId<CceAmostraContagemId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9202297780711632972L;
	private CceAmostraContagemId id;
	private Integer version;
	private CcePaciente ccePaciente;
	private String exame;
	private String equipamento;
	private Date dataHora;
	private String profissional;
	private Integer celulasContadas;
	private Date dataLiberacao;
	private String situacao;
	private String indLista;
	private String origem;
	private String nroInterno;
	private String infoClinicas;
	private String flags;
	private String repeticao;
	private String indEnviado;
	private Set<CceAmostraObservacao> cceAmostraObservacaoes = new HashSet<CceAmostraObservacao>(0);
	private Set<CceResultadoExame> cceResultadoExamees = new HashSet<CceResultadoExame>(0);

	public CceAmostraContagem() {
	}

	public CceAmostraContagem(CceAmostraContagemId id, CcePaciente ccePaciente, String exame) {
		this.id = id;
		this.ccePaciente = ccePaciente;
		this.exame = exame;
	}

	public CceAmostraContagem(CceAmostraContagemId id, CcePaciente ccePaciente, String exame, String equipamento, Date dataHora,
			String profissional, Integer celulasContadas, Date dataLiberacao, String situacao, String indLista, String origem,
			String nroInterno, String infoClinicas, String flags, String repeticao, String indEnviado,
			Set<CceAmostraObservacao> cceAmostraObservacaoes, Set<CceResultadoExame> cceResultadoExamees) {
		this.id = id;
		this.ccePaciente = ccePaciente;
		this.exame = exame;
		this.equipamento = equipamento;
		this.dataHora = dataHora;
		this.profissional = profissional;
		this.celulasContadas = celulasContadas;
		this.dataLiberacao = dataLiberacao;
		this.situacao = situacao;
		this.indLista = indLista;
		this.origem = origem;
		this.nroInterno = nroInterno;
		this.infoClinicas = infoClinicas;
		this.flags = flags;
		this.repeticao = repeticao;
		this.indEnviado = indEnviado;
		this.cceAmostraObservacaoes = cceAmostraObservacaoes;
		this.cceResultadoExamees = cceResultadoExamees;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "numero", column = @Column(name = "NUMERO", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public CceAmostraContagemId getId() {
		return this.id;
	}

	public void setId(CceAmostraContagemId id) {
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
	@JoinColumn(name = "CPA_CODIGO", nullable = false)
	public CcePaciente getCcePaciente() {
		return this.ccePaciente;
	}

	public void setCcePaciente(CcePaciente ccePaciente) {
		this.ccePaciente = ccePaciente;
	}

	@Column(name = "EXAME", nullable = false, length = 60)
	@Length(max = 60)
	public String getExame() {
		return this.exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	@Column(name = "EQUIPAMENTO", length = 5)
	@Length(max = 5)
	public String getEquipamento() {
		return this.equipamento;
	}

	public void setEquipamento(String equipamento) {
		this.equipamento = equipamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_HORA", length = 29)
	public Date getDataHora() {
		return this.dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	@Column(name = "PROFISSIONAL", length = 20)
	@Length(max = 20)
	public String getProfissional() {
		return this.profissional;
	}

	public void setProfissional(String profissional) {
		this.profissional = profissional;
	}

	@Column(name = "CELULAS_CONTADAS")
	public Integer getCelulasContadas() {
		return this.celulasContadas;
	}

	public void setCelulasContadas(Integer celulasContadas) {
		this.celulasContadas = celulasContadas;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_LIBERACAO", length = 29)
	public Date getDataLiberacao() {
		return this.dataLiberacao;
	}

	public void setDataLiberacao(Date dataLiberacao) {
		this.dataLiberacao = dataLiberacao;
	}

	@Column(name = "SITUACAO", length = 1)
	@Length(max = 1)
	public String getSituacao() {
		return this.situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	@Column(name = "IND_LISTA", length = 1)
	@Length(max = 1)
	public String getIndLista() {
		return this.indLista;
	}

	public void setIndLista(String indLista) {
		this.indLista = indLista;
	}

	@Column(name = "ORIGEM", length = 1)
	@Length(max = 1)
	public String getOrigem() {
		return this.origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	@Column(name = "NRO_INTERNO", length = 20)
	@Length(max = 20)
	public String getNroInterno() {
		return this.nroInterno;
	}

	public void setNroInterno(String nroInterno) {
		this.nroInterno = nroInterno;
	}

	@Column(name = "INFO_CLINICAS", length = 500)
	@Length(max = 500)
	public String getInfoClinicas() {
		return this.infoClinicas;
	}

	public void setInfoClinicas(String infoClinicas) {
		this.infoClinicas = infoClinicas;
	}

	@Column(name = "FLAGS", length = 500)
	@Length(max = 500)
	public String getFlags() {
		return this.flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	@Column(name = "REPETICAO", length = 3)
	@Length(max = 3)
	public String getRepeticao() {
		return this.repeticao;
	}

	public void setRepeticao(String repeticao) {
		this.repeticao = repeticao;
	}

	@Column(name = "IND_ENVIADO", length = 1)
	@Length(max = 1)
	public String getIndEnviado() {
		return this.indEnviado;
	}

	public void setIndEnviado(String indEnviado) {
		this.indEnviado = indEnviado;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cceAmostraContagem")
	public Set<CceAmostraObservacao> getCceAmostraObservacaoes() {
		return this.cceAmostraObservacaoes;
	}

	public void setCceAmostraObservacaoes(Set<CceAmostraObservacao> cceAmostraObservacaoes) {
		this.cceAmostraObservacaoes = cceAmostraObservacaoes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cceAmostraContagem")
	public Set<CceResultadoExame> getCceResultadoExamees() {
		return this.cceResultadoExamees;
	}

	public void setCceResultadoExamees(Set<CceResultadoExame> cceResultadoExamees) {
		this.cceResultadoExamees = cceResultadoExamees;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		CCE_PACIENTES("ccePaciente"),
		EXAME("exame"),
		EQUIPAMENTO("equipamento"),
		DATA_HORA("dataHora"),
		PROFISSIONAL("profissional"),
		CELULAS_CONTADAS("celulasContadas"),
		DATA_LIBERACAO("dataLiberacao"),
		SITUACAO("situacao"),
		IND_LISTA("indLista"),
		ORIGEM("origem"),
		NRO_INTERNO("nroInterno"),
		INFO_CLINICAS("infoClinicas"),
		FLAGS("flags"),
		REPETICAO("repeticao"),
		IND_ENVIADO("indEnviado"),
		CCE_AMOSTRA_OBSERVACAOES("cceAmostraObservacaoes"),
		CCE_RESULTADO_EXAMEES("cceResultadoExamees");

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
		if (!(obj instanceof CceAmostraContagem)) {
			return false;
		}
		CceAmostraContagem other = (CceAmostraContagem) obj;
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
