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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioTipoRarPrograma;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * RarPrograma generated by hbm2java
 */
@Entity
@SequenceGenerator(name="rarPgaSq1", sequenceName="AGH.RAR_PGA_SQ1", allocationSize = 1)
@Table(name = "RAR_PROGRAMAS", schema = "AGH")
public class RarPrograma extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1227250666797202805L;
	private Integer seq;
	private Integer version;
	private RarResolucao rarResolucao;
	private AghEspecialidades aghEspecialidades;
	private RarProjetoPrograma rarProjetoPrograma;
	private String nome;
	private String resumo;
	private Date dtInicio;
	private Date dtFim;
	private String titulo;
	private Short tempo;
	private Integer nroVagas;
	private DominioTipoRarPrograma tipo;
	private Set<RarProgramaDuracao> rarProgramaDuracaoes = new HashSet<RarProgramaDuracao>(0);
	private Set<RarBancaExaminadora> rarBancaExaminadoraes = new HashSet<RarBancaExaminadora>(0);
	private Set<RarCandidatoPrograma> rarCandidatosProgramasForPgaSeq = new HashSet<RarCandidatoPrograma>(0);
	private Set<RarCandidatoPrograma> rarCandidatosProgramasForPgaSuperior = new HashSet<RarCandidatoPrograma>(0);
	private Set<RarPreceptorResidente> rarPreceptorResidentees = new HashSet<RarPreceptorResidente>(0);
	private Set<RarAssociaPrograma> rarAssociaProgramasesForPgaSuperior = new HashSet<RarAssociaPrograma>(0);
	private Set<RarAssociaPrograma> rarAssociaProgramasesForPgaAreaAtuacao = new HashSet<RarAssociaPrograma>(0);
	private Set<RarChefiaPrograma> rarChefiaProgramas = new HashSet<RarChefiaPrograma>(0);
	private Set<RarRequisito> rarRequisitoes = new HashSet<RarRequisito>(0);
	private Set<RarSelecao> rarSelecaoes = new HashSet<RarSelecao>(0);
	private Set<RarAvaliacao> rarAvaliacaoes = new HashSet<RarAvaliacao>(0);

	public RarPrograma() {
	}

	public RarPrograma(Integer seq, RarResolucao rarResolucao, AghEspecialidades aghEspecialidades, String nome, Date dtInicio) {
		this.seq = seq;
		this.rarResolucao = rarResolucao;
		this.aghEspecialidades = aghEspecialidades;
		this.nome = nome;
		this.dtInicio = dtInicio;
	}

	public RarPrograma(Integer seq, RarResolucao rarResolucao, AghEspecialidades aghEspecialidades,
			RarProjetoPrograma rarProjetoPrograma, String nome, String resumo, Date dtInicio, Date dtFim, String titulo,
			Short tempo, Integer nroVagas, DominioTipoRarPrograma tipo, Set<RarProgramaDuracao> rarProgramaDuracaoes,
			Set<RarBancaExaminadora> rarBancaExaminadoraes, Set<RarCandidatoPrograma> rarCandidatosProgramasForPgaSeq,
			Set<RarCandidatoPrograma> rarCandidatosProgramasForPgaSuperior, Set<RarPreceptorResidente> rarPreceptorResidentees,
			Set<RarAssociaPrograma> rarAssociaProgramasesForPgaSuperior,
			Set<RarAssociaPrograma> rarAssociaProgramasesForPgaAreaAtuacao, Set<RarChefiaPrograma> rarChefiaProgramas,
			Set<RarRequisito> rarRequisitoes, Set<RarSelecao> rarSelecaoes, Set<RarAvaliacao> rarAvaliacaoes) {
		this.seq = seq;
		this.rarResolucao = rarResolucao;
		this.aghEspecialidades = aghEspecialidades;
		this.rarProjetoPrograma = rarProjetoPrograma;
		this.nome = nome;
		this.resumo = resumo;
		this.dtInicio = dtInicio;
		this.dtFim = dtFim;
		this.titulo = titulo;
		this.tempo = tempo;
		this.nroVagas = nroVagas;
		this.tipo = tipo;
		this.rarProgramaDuracaoes = rarProgramaDuracaoes;
		this.rarBancaExaminadoraes = rarBancaExaminadoraes;
		this.rarCandidatosProgramasForPgaSeq = rarCandidatosProgramasForPgaSeq;
		this.rarCandidatosProgramasForPgaSuperior = rarCandidatosProgramasForPgaSuperior;
		this.rarPreceptorResidentees = rarPreceptorResidentees;
		this.rarAssociaProgramasesForPgaSuperior = rarAssociaProgramasesForPgaSuperior;
		this.rarAssociaProgramasesForPgaAreaAtuacao = rarAssociaProgramasesForPgaAreaAtuacao;
		this.rarChefiaProgramas = rarChefiaProgramas;
		this.rarRequisitoes = rarRequisitoes;
		this.rarSelecaoes = rarSelecaoes;
		this.rarAvaliacaoes = rarAvaliacaoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rarPgaSq1")
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
	@JoinColumn(name = "RSL_SEQ", nullable = false)
	public RarResolucao getRarResolucao() {
		return this.rarResolucao;
	}

	public void setRarResolucao(RarResolucao rarResolucao) {
		this.rarResolucao = rarResolucao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ", nullable = false)
	public AghEspecialidades getAghEspecialidades() {
		return this.aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PPR_SEQ")
	public RarProjetoPrograma getRarProjetoPrograma() {
		return this.rarProjetoPrograma;
	}

	public void setRarProjetoPrograma(RarProjetoPrograma rarProjetoPrograma) {
		this.rarProjetoPrograma = rarProjetoPrograma;
	}

	@Column(name = "NOME", nullable = false, length = 90)
	@Length(max = 90)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "RESUMO", length = 20)
	@Length(max = 20)
	public String getResumo() {
		return this.resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO", nullable = false, length = 29)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM", length = 29)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@Column(name = "TITULO", length = 60)
	@Length(max = 60)
	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "TEMPO")
	public Short getTempo() {
		return this.tempo;
	}

	public void setTempo(Short tempo) {
		this.tempo = tempo;
	}

	@Column(name = "NRO_VAGAS")
	public Integer getNroVagas() {
		return this.nroVagas;
	}

	public void setNroVagas(Integer nroVagas) {
		this.nroVagas = nroVagas;
	}

	@Column(name = "TIPO", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoRarPrograma getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoRarPrograma tipo) {
		this.tipo = tipo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarPrograma")
	public Set<RarProgramaDuracao> getRarProgramaDuracaoes() {
		return this.rarProgramaDuracaoes;
	}

	public void setRarProgramaDuracaoes(Set<RarProgramaDuracao> rarProgramaDuracaoes) {
		this.rarProgramaDuracaoes = rarProgramaDuracaoes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarPrograma")
	public Set<RarBancaExaminadora> getRarBancaExaminadoraes() {
		return this.rarBancaExaminadoraes;
	}

	public void setRarBancaExaminadoraes(Set<RarBancaExaminadora> rarBancaExaminadoraes) {
		this.rarBancaExaminadoraes = rarBancaExaminadoraes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarProgramasByPgaSeq")
	public Set<RarCandidatoPrograma> getRarCandidatosProgramasForPgaSeq() {
		return this.rarCandidatosProgramasForPgaSeq;
	}

	public void setRarCandidatosProgramasForPgaSeq(Set<RarCandidatoPrograma> rarCandidatosProgramasForPgaSeq) {
		this.rarCandidatosProgramasForPgaSeq = rarCandidatosProgramasForPgaSeq;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarProgramasByPgaSuperior")
	public Set<RarCandidatoPrograma> getRarCandidatosProgramasForPgaSuperior() {
		return this.rarCandidatosProgramasForPgaSuperior;
	}

	public void setRarCandidatosProgramasForPgaSuperior(Set<RarCandidatoPrograma> rarCandidatosProgramasForPgaSuperior) {
		this.rarCandidatosProgramasForPgaSuperior = rarCandidatosProgramasForPgaSuperior;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarPrograma")
	public Set<RarPreceptorResidente> getRarPreceptorResidentees() {
		return this.rarPreceptorResidentees;
	}

	public void setRarPreceptorResidentees(Set<RarPreceptorResidente> rarPreceptorResidentees) {
		this.rarPreceptorResidentees = rarPreceptorResidentees;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarProgramasByPgaSuperior")
	public Set<RarAssociaPrograma> getRarAssociaProgramasesForPgaSuperior() {
		return this.rarAssociaProgramasesForPgaSuperior;
	}

	public void setRarAssociaProgramasesForPgaSuperior(Set<RarAssociaPrograma> rarAssociaProgramasesForPgaSuperior) {
		this.rarAssociaProgramasesForPgaSuperior = rarAssociaProgramasesForPgaSuperior;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarProgramasByPgaAreaAtuacao")
	public Set<RarAssociaPrograma> getRarAssociaProgramasesForPgaAreaAtuacao() {
		return this.rarAssociaProgramasesForPgaAreaAtuacao;
	}

	public void setRarAssociaProgramasesForPgaAreaAtuacao(Set<RarAssociaPrograma> rarAssociaProgramasesForPgaAreaAtuacao) {
		this.rarAssociaProgramasesForPgaAreaAtuacao = rarAssociaProgramasesForPgaAreaAtuacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarPrograma")
	public Set<RarChefiaPrograma> getRarChefiaProgramas() {
		return this.rarChefiaProgramas;
	}

	public void setRarChefiaProgramas(Set<RarChefiaPrograma> rarChefiaProgramas) {
		this.rarChefiaProgramas = rarChefiaProgramas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarPrograma")
	public Set<RarRequisito> getRarRequisitoes() {
		return this.rarRequisitoes;
	}

	public void setRarRequisitoes(Set<RarRequisito> rarRequisitoes) {
		this.rarRequisitoes = rarRequisitoes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarPrograma")
	public Set<RarSelecao> getRarSelecaoes() {
		return this.rarSelecaoes;
	}

	public void setRarSelecaoes(Set<RarSelecao> rarSelecaoes) {
		this.rarSelecaoes = rarSelecaoes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarPrograma")
	public Set<RarAvaliacao> getRarAvaliacaoes() {
		return this.rarAvaliacaoes;
	}

	public void setRarAvaliacaoes(Set<RarAvaliacao> rarAvaliacaoes) {
		this.rarAvaliacaoes = rarAvaliacaoes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAR_RESOLUCOES("rarResolucao"),
		AGH_ESPECIALIDADES("aghEspecialidades"),
		RAR_PROJETO_PROGRAMAS("rarProjetoPrograma"),
		NOME("nome"),
		RESUMO("resumo"),
		DT_INICIO("dtInicio"),
		DT_FIM("dtFim"),
		TITULO("titulo"),
		TEMPO("tempo"),
		NRO_VAGAS("nroVagas"),
		TIPO("tipo"),
		RAR_PROGRAMA_DURACAOES("rarProgramaDuracaoes"),
		RAR_BANCA_EXAMINADORAES("rarBancaExaminadoraes"),
		RAR_CANDIDATOS_PROGRAMAS_FOR_PGA_SEQ("rarCandidatosProgramasForPgaSeq"),
		RAR_CANDIDATOS_PROGRAMAS_FOR_PGA_SUPERIOR("rarCandidatosProgramasForPgaSuperior"),
		RAR_PRECEPTOR_RESIDENTEES("rarPreceptorResidentees"),
		RAR_ASSOCIA_PROGRAMASES_FOR_PGA_SUPERIOR("rarAssociaProgramasesForPgaSuperior"),
		RAR_ASSOCIA_PROGRAMASES_FOR_PGA_AREA_ATUACAO("rarAssociaProgramasesForPgaAreaAtuacao"),
		RAR_CHEFIA_PROGRAMAS("rarChefiaProgramas"),
		RAR_REQUISITOES("rarRequisitoes"),
		RAR_SELECAOES("rarSelecaoes"),
		RAR_AVALIACAOES("rarAvaliacaoes");

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
		if (!(obj instanceof RarPrograma)) {
			return false;
		}
		RarPrograma other = (RarPrograma) obj;
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
