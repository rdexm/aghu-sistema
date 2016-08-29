package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioIndTerminoNotificacoes;

@Entity
@Table(name = "AGH_NOTIFICACOES", schema = "AGH")
@SequenceGenerator(name="aghNtsSeq1", sequenceName="AGH.agh_nts_seq1", allocationSize = 1)
public class AghNotificacoes extends BaseEntitySeq<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -471151792231988413L;
	
	private Integer seq;
	private Date criadoEm;
	private Date alteradoEm;
	private String descricao;
	private String nomeProcesso;
	private Date inicioEm;
	private Date horarioAgendamento;
	private DominioIndTerminoNotificacoes indTerminoNotificacoes;
	private Date terminaEm;
	private Integer terminaApos;
	private Integer version;
	private RapServidores servidor;
	private List<AghNotificacaoDestinos> destinos = new LinkedList<AghNotificacaoDestinos>();
	
	public AghNotificacoes() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghNtsSeq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "DESCRICAO", length=100, nullable = false, unique=true)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "NOME_PROCESSO", length=100, nullable = false)
	public String getNomeProcesso() {
		return nomeProcesso;
	}

	public void setNomeProcesso(String nomeProcesso) {
		this.nomeProcesso = nomeProcesso;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INICIO_EM", nullable = false, length = 7)
	public Date getInicioEm() {
		return inicioEm;
	}

	public void setInicioEm(Date inicioEm) {
		this.inicioEm = inicioEm;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HORARIO_AGENDAMENTO", nullable = false)
	public Date getHorarioAgendamento() {
		return horarioAgendamento;
	}

	public void setHorarioAgendamento(Date horarioAgendamento) {
		this.horarioAgendamento = horarioAgendamento;
	}
	
	@Column(name = "IND_TERMINO_NOTIFICACOES", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioIndTerminoNotificacoes getIndTerminoNotificacoes() {
		return indTerminoNotificacoes;
	}

	public void setIndTerminoNotificacoes(
			DominioIndTerminoNotificacoes indTerminoNotificacoes) {
		this.indTerminoNotificacoes = indTerminoNotificacoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TERMINA_EM", length = 7)
	public Date getTerminaEm() {
		return terminaEm;
	}

	public void setTerminaEm(Date terminaEm) {
		this.terminaEm = terminaEm;
	}

	@Column(name = "TERMINA_APOS")
	public Integer getTerminaApos() {
		return terminaApos;
	}

	public void setTerminaApos(Integer terminaApos) {
		this.terminaApos = terminaApos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "notificacao")
	public List<AghNotificacaoDestinos> getDestinos() {
		return destinos;
	}

	public void setDestinos(List<AghNotificacaoDestinos> destinos) {
		this.destinos = destinos;
	}
	public enum Fields {
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		DESCRICAO("descricao"),
		NOME_PROCESSO("nomeProcesso"),
		INICIO_EM("inicioEm"),
		HORARIO_AGENDAMENTO("horarioAgendamento"),
		IND_TERMINO_NOTIFICACOES("indTerminoNotificacoes"),
		TERMINA_EM("terminaEm"),
		TERMINA_APOS("terminaApos"),
		VERSION("version"),
		SERVIDOR("servidor"),
		DESTINOS("destinos");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}
		
	}

}
