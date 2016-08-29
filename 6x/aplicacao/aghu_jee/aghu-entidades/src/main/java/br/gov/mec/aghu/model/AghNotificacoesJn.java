package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;
import br.gov.mec.aghu.dominio.DominioIndTerminoNotificacoes;

@Entity
@Table(name = "AGH_NOTIFICACOES_JN", schema = "AGH")
@SequenceGenerator(name="aghNtsSeqJn", sequenceName="AGH.agh_nts_seq_jn", allocationSize = 1)
@Immutable
public class AghNotificacoesJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7738305589151120541L;
	
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
	private Integer serMatricula;
	private Short serVinCodigo;
	
	public AghNotificacoesJn() {
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghNtsSeqJn")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "DESCRICAO", length=100)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "NOME_PROCESSO", length=100)
	public String getNomeProcesso() {
		return nomeProcesso;
	}

	public void setNomeProcesso(String nomeProcesso) {
		this.nomeProcesso = nomeProcesso;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INICIO_EM", length = 7)
	public Date getInicioEm() {
		return inicioEm;
	}

	public void setInicioEm(Date inicioEm) {
		this.inicioEm = inicioEm;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HORARIO_AGENDAMENTO")
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
		VERSION("version");
		
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
