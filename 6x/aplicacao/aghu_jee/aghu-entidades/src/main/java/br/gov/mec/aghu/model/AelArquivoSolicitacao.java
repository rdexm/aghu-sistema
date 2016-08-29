package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@SequenceGenerator(name="aelAqsSq1", sequenceName="AEL_AQS_SQ1", allocationSize = 1)
@Table(name = "AEL_ARQUIVO_SOLICITACOES", schema = "AGH")
public class AelArquivoSolicitacao extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5310811510855669270L;

	private Integer seq;

	private Integer crmMedico;
	private String nomeMedico;
	private String nomePaciente;
	private String nomeMaePaciente;
	private Date dataNascimentoPaciente;
	private Date dataHoraColeta;
	private Long numeroCartaoSaude;
	private DominioSimNao semAgenda;
	private DominioSexo sexoPaciente;
	private String motivo;
	private Date criadoEm;
	private Integer version;

	private AelArquivoIntegracao aelArquivoIntegracao;
	private AelSolicitacaoExames aelSolicitacaoExames;
	private RapServidores servidor;

	// getters & setters
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelAqsSq1")
	@Column(name = "SEQ", length = 9, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "CRM_MEDICO", length = 6)
	public Integer getCrmMedico() {
		return this.crmMedico;
	}

	public void setCrmMedico(Integer crmMedico) {
		this.crmMedico = crmMedico;
	}

	@Column(name = "NOME_MEDICO", length = 60)
	public String getNomeMedico() {
		return this.nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	@Column(name = "NOME_PACIENTE", length = 50)
	public String getNomePaciente() {
		return this.nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	@Column(name = "NOME_MAE_PACIENTE", length = 50)
	public String getNomeMaePaciente() {
		return this.nomeMaePaciente;
	}

	public void setNomeMaePaciente(String nomeMaePaciente) {
		this.nomeMaePaciente = nomeMaePaciente;
	}

	@Column(name = "DATA_NASCIMENTO_PACIENTE")
	public Date getDataNascimentoPaciente() {
		return this.dataNascimentoPaciente;
	}

	public void setDataNascimentoPaciente(Date dataNascimentoPaciente) {
		this.dataNascimentoPaciente = dataNascimentoPaciente;
	}

	@Column(name = "DATA_HORA_COLETA")
	public Date getDataHoraColeta() {
		return this.dataHoraColeta;
	}

	public void setDataHoraColeta(Date dataHoraColeta) {
		this.dataHoraColeta = dataHoraColeta;
	}

	@Column(name = "NUMERO_CARTAO_SAUDE", length = 22)
	public Long getNumeroCartaoSaude() {
		return this.numeroCartaoSaude;
	}

	public void setNumeroCartaoSaude(Long numeroCartaoSaude) {
		this.numeroCartaoSaude = numeroCartaoSaude;
	}

	@Column(name = "SEM_AGENDA", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getSemAgenda() {
		return this.semAgenda;
	}
	
	public void setSemAgenda(DominioSimNao semAgenda) {
		this.semAgenda = semAgenda;
	}


	@Column(name = "SEXO_PACIENTE", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSexo getSexoPaciente() {
		return this.sexoPaciente;
	}

	public void setSexoPaciente(DominioSexo sexoPaciente) {
		this.sexoPaciente = sexoPaciente;
	}

	@Column(name = "MOTIVO")
	public String getMotivo() {
		return this.motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Version
	@Column(name = "VERSION", length = 9, nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AQI_SEQ", referencedColumnName = "SEQ")
	public AelArquivoIntegracao getAelArquivoIntegracao() {
		return aelArquivoIntegracao;
	}

	public void setAelArquivoIntegracao(
			AelArquivoIntegracao aelArquivoIntegracao) {
		this.aelArquivoIntegracao = aelArquivoIntegracao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOE_SEQ", referencedColumnName = "SEQ")
	public AelSolicitacaoExames getAelSolicitacaoExames() {
		return aelSolicitacaoExames;
	}

	public void setAelSolicitacaoExames(
			AelSolicitacaoExames aelSolicitacaoExames) {
		this.aelSolicitacaoExames = aelSolicitacaoExames;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	// outros

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean equals(Object other) {
		if (!(other instanceof AelArquivoSolicitacao)){
			return false;
		}
		AelArquivoSolicitacao castOther = (AelArquivoSolicitacao) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}


	public enum Fields {
		SEQ("seq"), CRM_MEDICO("crmMedico"), NOME_MEDICO("nomeMedico"), NOME_PACIENTE(
				"nomePaciente"), NOME_MAE_PACIENTE("nomeMaePaciente"), DATA_NASCIMENTO_PACIENTE(
				"dataNascimentoPaciente"), DATA_HORA_COLETA("dataHoraColeta"), NUMERO_CARTAO_SAUDE(
				"numeroCartaoSaude"), SEM_AGENDA("semAgenda"), SEXO_PACIENTE(
				"sexoPaciente"), MOTIVO("motivo"), CRIADO_EM("criadoEm"), VERSION(
				"version"),

		AEL_ARQUIVO_INTEGRACOES("aelArquivoIntegracao"), AEL_SOLICITACAO_EXAMES(
				"aelSolicitacaoExames"), AEL_SOLICITACAO_EXAMES_SEQ(
						"aelSolicitacaoExames.seq"), RAP_SERVIDORES("servidor"), ;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		public String toString() {
			return this.field;
		}
	}

}