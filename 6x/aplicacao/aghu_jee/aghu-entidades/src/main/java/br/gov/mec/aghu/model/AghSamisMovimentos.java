package br.gov.mec.aghu.model;

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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;

@Entity
@SequenceGenerator(name = "aghSamisMovimentosSq", sequenceName = "AGH.AGH_SAMIS_MOVIMENTOS_SQ1")
@Table(name = "AGH_SAMIS_MOVIMENTOS", schema = "AGH")
public class AghSamisMovimentos extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 3360054175803924505L;

	private Integer seq;
	private AipPacientes paciente;
	private Date dataMovimentacao;
	private Date dataCadastroOrigemProntuario;
	private AghSamis samisOrigem;
	private String localAtual;
	private RapServidores servidor;
	private DominioSituacaoMovimentoProntuario situacao;
	private Integer version;
	
	public AghSamisMovimentos() {

	}

	public AghSamisMovimentos(Integer seq, AipPacientes paciente,
			AghSamis samisOrigem, RapServidores servidor) {
		this.seq = seq;
		this.paciente = paciente;
		this.samisOrigem = samisOrigem;
		this.servidor = servidor;
	}

	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghSamisMovimentosSq")
	@Id
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", referencedColumnName = "CODIGO", nullable = false)
	@NotNull
	public AipPacientes getPaciente() {
		return this.paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_MOVIMENTACAO", nullable = true, length = 7)
	public Date getDataMovimentacao() {
		return this.dataMovimentacao;
	}

	public void setDataMovimentacao(Date dataMovimentacao) {
		this.dataMovimentacao = dataMovimentacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_CADASTRO_ORIGEM", nullable = true, length = 7)
	public Date getDataCadastroOrigemProntuario() {
		return this.dataCadastroOrigemProntuario;
	}

	public void setDataCadastroOrigemProntuario(Date dataCadastroOrigemProntuario) {
		this.dataCadastroOrigemProntuario = dataCadastroOrigemProntuario;
	}

	// get e set samisOrigem
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAMIS", referencedColumnName = "SEQ", nullable = false)
	public AghSamis getSamisOrigem() {
		return samisOrigem;
	}

	public void setSamisOrigem(AghSamis samisOrigem) {
		this.samisOrigem = samisOrigem;
	}

	@Column(name = "LOCAL_ATUAL", nullable = false, length = 60)
	@Length(max = 60, message = "Local atual pode ter no mÃ¡ximo 60 caracteres.")
	public String getLocalAtual() {
		return localAtual;
	}

	public void setLocalAtual(String localAtual) {
		this.localAtual = localAtual;
	}

	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@NotNull
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores sevidor) {
		this.servidor = sevidor;
	}

	@Column(name = "SITUACAO", nullable = true, length=1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoMovimentoProntuario getSituacao() {
		return this.situacao;
	}
	
	public void setSituacao(DominioSituacaoMovimentoProntuario situacao) {
		this.situacao = situacao;
	}
	
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		SEQ("codigo"), PACIENTE("paciente"), PAC_CODIGO("paciente.codigo"), DT_MOVIMENTACAO(
				"dataMovimentacao"), SAMIS("samisOrigem"), LOCAL_ATUAL(
						"localAtual"), SERVIDOR("servidor"), DT_CADASTRO_ORIGEM_PRONTUARIO(
								"dataCadastroOrigemProntuario"),SITUACAO("situacao");
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
