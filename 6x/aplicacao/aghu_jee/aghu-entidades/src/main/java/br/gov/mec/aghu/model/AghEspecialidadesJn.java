package br.gov.mec.aghu.model;

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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioEspecialidadeInterna;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "AGH_ESPECIALIDADES_JN", schema = "AGH")
@SequenceGenerator(name = "aghEspJnSeq", sequenceName = "AGH.AGH_ESP_JN_SEQ", allocationSize = 1)

@Immutable
public class AghEspecialidadesJn extends BaseJournal implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1372393599783728603L;
	private Short seq;
	private String sigla;
	private String nomeEspecialidade;
	private String nomeReduzido;
	private Short idadeMinPacAmbulatorio;
	private Short idadeMaxPacAmbulatorio;
	private Short idadeMinPacInternacao;
	private Short idadeMaxPacInternacao;
	private DominioEspecialidadeInterna indEspInterna;
	private DominioSimNao indConsultoria;
	private AghClinicas clinica;
	private Byte greSeq;
	private RapServidores servidor;
	private AghEspecialidades especialidade;
	private FccCentroCustos centroCusto;
	private RapServidores servidorChefe;
	private DominioSituacao indSituacao;
	private Short capacReferencial;
	private DominioSimNao indAtendHospDia;
	private AghEspecialidades especialidadeAgrupaLoteExame;
	private DominioSimNao indSugereProfInternacao;
	private DominioSimNao indHoraDispFeriado;
	private Short epcSeq;
	private DominioSimNao indImprimeAgenda;
	private DominioSimNao indEmiteBoletimAtendimento;
	private DominioSimNao indEmiteTicket;
	private DominioSimNao indEnviaMensagem;
	private DominioSimNao indAcompPosTransplante;
	private Integer mediaPermanencia;
	private DominioSimNao indProntoAtendimento;
	private DominioSimNao indImpSoServico;
	private Byte maxQuantFaltas;

	public AghEspecialidadesJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghEspJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "SIGLA", length = 3)
	@Length(max = 3)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "NOME_ESPECIALIDADE", length = 45)
	@Length(max = 45)
	public String getNomeEspecialidade() {
		return this.nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	@Column(name = "NOME_REDUZIDO", length = 14)
	@Length(max = 14)
	public String getNomeReduzido() {
		return this.nomeReduzido;
	}

	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}

	@Column(name = "IDADE_MIN_PAC_AMBULATORIO", precision = 3, scale = 0)
	public Short getIdadeMinPacAmbulatorio() {
		return this.idadeMinPacAmbulatorio;
	}

	public void setIdadeMinPacAmbulatorio(Short idadeMinPacAmbulatorio) {
		this.idadeMinPacAmbulatorio = idadeMinPacAmbulatorio;
	}

	@Column(name = "IDADE_MAX_PAC_AMBULATORIO", precision = 3, scale = 0)
	public Short getIdadeMaxPacAmbulatorio() {
		return this.idadeMaxPacAmbulatorio;
	}

	public void setIdadeMaxPacAmbulatorio(Short idadeMaxPacAmbulatorio) {
		this.idadeMaxPacAmbulatorio = idadeMaxPacAmbulatorio;
	}

	@Column(name = "IDADE_MIN_PAC_INTERNACAO", precision = 3, scale = 0)
	public Short getIdadeMinPacInternacao() {
		return this.idadeMinPacInternacao;
	}

	public void setIdadeMinPacInternacao(Short idadeMinPacInternacao) {
		this.idadeMinPacInternacao = idadeMinPacInternacao;
	}

	@Column(name = "IDADE_MAX_PAC_INTERNACAO", precision = 3, scale = 0)
	public Short getIdadeMaxPacInternacao() {
		return this.idadeMaxPacInternacao;
	}

	public void setIdadeMaxPacInternacao(Short idadeMaxPacInternacao) {
		this.idadeMaxPacInternacao = idadeMaxPacInternacao;
	}

	@Column(name = "IND_ESP_INTERNA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioEspecialidadeInterna getIndEspInterna() {
		return this.indEspInterna;
	}

	public void setIndEspInterna(DominioEspecialidadeInterna indEspInterna) {
		this.indEspInterna = indEspInterna;
	}

	@Column(name = "IND_CONSULTORIA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndConsultoria() {
		return this.indConsultoria;
	}

	public void setIndConsultoria(DominioSimNao indConsultoria) {
		this.indConsultoria = indConsultoria;
	}

	/**
	 * @return the clinica
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLC_CODIGO")
	public AghClinicas getClinica() {
		return clinica;
	}

	/**
	 * @param clinica
	 *            the clinica to set
	 */
	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	@Column(name = "GRE_SEQ", precision = 2, scale = 0)
	public Byte getGreSeq() {
		return greSeq;
	}

	public void setGreSeq(Byte greSeq) {
		this.greSeq = greSeq;
	}

	/**
	 * @return the servidorResponsavel
	 */
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	/**
	 * @param servidorResponsavel
	 *            the servidorResponsavel to set
	 */
	public void setServidor(RapServidores servidorResponsavel) {
		this.servidor = servidorResponsavel;
	}

	/**
	 * @return the especialidade
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ")
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	/**
	 * @param especialidade
	 *            the especialidade to set
	 */
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	/**
	 * @return the centroCusto
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO")
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	/**
	 * @param centroCusto
	 *            the centroCusto to set
	 */
	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	/**
	 * @return the servidorChefe
	 */
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_CHEFE", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CHEFE", referencedColumnName = "VIN_CODIGO") })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getServidorChefe() {
		return servidorChefe;
	}

	/**
	 * @param servidorChefe
	 *            the servidorChefe to set
	 */
	public void setServidorChefe(RapServidores servidorChefe) {
		this.servidorChefe = servidorChefe;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "CAPAC_REFERENCIAL", precision = 3, scale = 0)
	public Short getCapacReferencial() {
		return this.capacReferencial;
	}

	public void setCapacReferencial(Short capacReferencial) {
		this.capacReferencial = capacReferencial;
	}

	@Column(name = "IND_ATEND_HOSP_DIA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndAtendHospDia() {
		return this.indAtendHospDia;
	}

	public void setIndAtendHospDia(DominioSimNao indAtendHospDia) {
		this.indAtendHospDia = indAtendHospDia;
	}

	/**
	 * @return the especialidadeAgrupaLoteExame
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ_AGRUPA_LOTE_EXAME")
	public AghEspecialidades getEspecialidadeAgrupaLoteExame() {
		return especialidadeAgrupaLoteExame;
	}

	/**
	 * @param especialidadeAgrupaLoteExame
	 *            the especialidadeAgrupaLoteExame to set
	 */
	public void setEspecialidadeAgrupaLoteExame(
			AghEspecialidades especialidadeAgrupaLoteExame) {
		this.especialidadeAgrupaLoteExame = especialidadeAgrupaLoteExame;
	}

	@Column(name = "IND_SUGERE_PROF_INTERNACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndSugereProfInternacao() {
		return this.indSugereProfInternacao;
	}

	public void setIndSugereProfInternacao(DominioSimNao indSugereProfInternacao) {
		this.indSugereProfInternacao = indSugereProfInternacao;
	}

	@Column(name = "IND_HORA_DISP_FERIADO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndHoraDispFeriado() {
		return this.indHoraDispFeriado;
	}

	public void setIndHoraDispFeriado(DominioSimNao indHoraDispFeriado) {
		this.indHoraDispFeriado = indHoraDispFeriado;
	}

	@Column(name = "EPC_SEQ", precision = 3, scale = 0)
	public Short getEpcSeq() {
		return this.epcSeq;
	}

	public void setEpcSeq(Short epcSeq) {
		this.epcSeq = epcSeq;
	}

	@Column(name = "IND_IMPRIME_AGENDA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndImprimeAgenda() {
		return this.indImprimeAgenda;
	}

	public void setIndImprimeAgenda(DominioSimNao indImprimeAgenda) {
		this.indImprimeAgenda = indImprimeAgenda;
	}

	@Column(name = "IND_EMITE_BA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndEmiteBoletimAtendimento() {
		return this.indEmiteBoletimAtendimento;
	}

	public void setIndEmiteBoletimAtendimento(DominioSimNao indEmiteBa) {
		this.indEmiteBoletimAtendimento = indEmiteBa;
	}

	@Column(name = "IND_EMITE_TICKET", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndEmiteTicket() {
		return this.indEmiteTicket;
	}

	public void setIndEmiteTicket(DominioSimNao indEmiteTicket) {
		this.indEmiteTicket = indEmiteTicket;
	}

	@Column(name = "IND_ENVIA_MSG", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndEnviaMensagem() {
		return this.indEnviaMensagem;
	}

	public void setIndEnviaMensagem(DominioSimNao indEnviaMsg) {
		this.indEnviaMensagem = indEnviaMsg;
	}

	@Column(name = "IND_ACOMP_POS_TRANSPLANTE", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndAcompPosTransplante() {
		return this.indAcompPosTransplante;
	}

	public void setIndAcompPosTransplante(DominioSimNao indAcompPosTransplante) {
		this.indAcompPosTransplante = indAcompPosTransplante;
	}

	@Column(name = "MEDIA_PERMANENCIA", precision = 5, scale = 0)
	public Integer getMediaPermanencia() {
		return this.mediaPermanencia;
	}

	public void setMediaPermanencia(Integer mediaPermanencia) {
		this.mediaPermanencia = mediaPermanencia;
	}

	@Column(name = "IND_PRONTO_ATENDIMENTO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndProntoAtendimento() {
		return this.indProntoAtendimento;
	}

	public void setIndProntoAtendimento(DominioSimNao indProntoAtendimento) {
		this.indProntoAtendimento = indProntoAtendimento;
	}

	@Column(name = "IND_IMP_SO_SERVICO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndImpSoServico() {
		return this.indImpSoServico;
	}

	public void setIndImpSoServico(DominioSimNao indImpSoServico) {
		this.indImpSoServico = indImpSoServico;
	}

	@Column(name = "MAX_QUANT_FALTAS", precision = 2, scale = 0)
	public Byte getMaxQuantFaltas() {
		return this.maxQuantFaltas;
	}

	public void setMaxQuantFaltas(Byte maxQuantFaltas) {
		this.maxQuantFaltas = maxQuantFaltas;
	}

	public enum Fields {

		SEQ("seq"),
		SIGLA("sigla"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		NOME_REDUZIDO("nomeReduzido"),
		IDADE_MIN_PAC_AMBULATORIO("idadeMinPacAmbulatorio"),
		IDADE_MAX_PAC_AMBULATORIO("idadeMaxPacAmbulatorio"),
		IDADE_MIN_PAC_INTERNACAO("idadeMinPacInternacao"),
		IDADE_MAX_PAC_INTERNACAO("idadeMaxPacInternacao"),
		IND_ESP_INTERNA("indEspInterna"),
		IND_CONSULTORIA("indConsultoria"),
		CLINICA("clinica"),
		GRE_SEQ("greSeq"),
		SERVIDOR("servidor"),
		ESPECIALIDADE("especialidade"),
		CENTRO_CUSTO("centroCusto"),
		SERVIDOR_CHEFE("servidorChefe"),
		IND_SITUACAO("indSituacao"),
		CAPAC_REFERENCIAL("capacReferencial"),
		IND_ATEND_HOSP_DIA("indAtendHospDia"),
		ESPECIALIDADE_AGRUPA_LOTE_EXAME("especialidadeAgrupaLoteExame"),
		IND_SUGERE_PROF_INTERNACAO("indSugereProfInternacao"),
		IND_HORA_DISP_FERIADO("indHoraDispFeriado"),
		EPC_SEQ("epcSeq"),
		IND_IMPRIME_AGENDA("indImprimeAgenda"),
		IND_EMITE_BOLETIM_ATENDIMENTO("indEmiteBoletimAtendimento"),
		IND_EMITE_TICKET("indEmiteTicket"),
		IND_ENVIA_MENSAGEM("indEnviaMensagem"),
		IND_ACOMP_POS_TRANSPLANTE("indAcompPosTransplante"),
		MEDIA_PERMANENCIA("mediaPermanencia"),
		IND_PRONTO_ATENDIMENTO("indProntoAtendimento"),
		IND_IMP_SO_SERVICO("indImpSoServico"),
		MAX_QUANT_FALTAS("maxQuantFaltas");

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
