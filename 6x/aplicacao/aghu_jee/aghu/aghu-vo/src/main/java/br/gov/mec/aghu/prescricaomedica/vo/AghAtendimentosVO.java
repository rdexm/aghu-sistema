package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioControleSumarioAltaPendente;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioTipoLaminaLaringo;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Classe utilizado para recuperar dados da Base.<br>
 * Utilizada para verificacoes apenas.<br>
 *  
 * @author rcorvalao
 *
 */

public class AghAtendimentosVO implements BaseBean{
	
	private static final long serialVersionUID = 7841635293528554075L;
	
	private Integer seq;
	private RapServidores servidor;
	private RapServidores servidorMovimento;
	private Date dthrInicio;
	private Boolean indPacPediatrico;
	private Boolean indPacPrematuro;
	private DominioPacAtendimento indPacAtendimento;
	private AhdHospitaisDia hospitalDia;
	private AinInternacao internacao;
	private AinAtendimentosUrgencia atendimentoUrgencia;
	private Date dthrFim;
	private Date dthrUltImprSumrPrescr;
	private DominioTipoLaminaLaringo tipoLaminaLaringo;
	private AinLeitos leito;
	private AinQuartos quarto;
	private AghUnidadesFuncionais unidadeFuncional;
	private AghEspecialidades especialidade;
	private DominioOrigemAtendimento origem;
	private Date dthrIngressoUnidade;
	private DominioTipoTratamentoAtendimento indTipoTratamento;
	private DominioSituacaoSumarioAlta indSitSumarioAlta;
	private DominioControleSumarioAltaPendente ctrlSumrAltaPendente;	
	private AghAtendimentos atendimentoMae;
	private McoGestacoes mcoGestacoes;
	private AipPacientes paciente;
	private AacConsultas consulta;
	
	// Criado para solucionar as origens não mapeadas no DominioOrigemAtendimento
	private String origemAtendimento;
	
	
	
	private String estadoSaude;
	private Short unfSeq;
	private Integer qtDiariasAutorizadas;
	private String ltoId;
	
	private Integer codigoPaciente;
	private String nomePaciente;
	private Integer idadePaciente;
	private Date dtNascimentoPaciente;

	private Integer conNumero;
	private Integer prontuario;
	
	private String siglaEspecialidade;
	private Integer intSeq;

	private String senhaAutorizada;
	
	public enum Fields {

		SEQ("seq"),
		ESTADO_SAUDE("estadoSaude"),
		UNF_SEQ("unfSeq"),
		QT_DIARIAS_AUTORIZADAS("qtDiariasAutorizadas"),
		LTO_ID("ltoId"),
		
		CODIGO_PACIENTE("codigoPaciente"),
		NOME_PACIENTE("nomePaciente"),
		DT_NASCIMENTO_PACIENTE("dtNascimentoPaciente"),
		
		CON_NUMERO("conNumero"),
		PRONTUARIO("prontuario"),
		DATA_HORA_INICIO("dthrInicio"),
		
		SIGLA_ESPECIALIDADE("siglaEspecialidade"),
		
		SENHA_AUTORIZADA("senhaAutorizada"),
		
		INT_SEQ("intSeq")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	public RapServidores getServidorMovimento() {
		return servidorMovimento;
	}
	public void setServidorMovimento(RapServidores servidorMovimento) {
		this.servidorMovimento = servidorMovimento;
	}
	public Date getDthrInicio() {
		return dthrInicio;
	}
	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
	public Boolean getIndPacPediatrico() {
		return indPacPediatrico;
	}
	public void setIndPacPediatrico(Boolean indPacPediatrico) {
		this.indPacPediatrico = indPacPediatrico;
	}
	public Boolean getIndPacPrematuro() {
		return indPacPrematuro;
	}
	public void setIndPacPrematuro(Boolean indPacPrematuro) {
		this.indPacPrematuro = indPacPrematuro;
	}
	public DominioPacAtendimento getIndPacAtendimento() {
		return indPacAtendimento;
	}
	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}
	public AhdHospitaisDia getHospitalDia() {
		return hospitalDia;
	}
	public void setHospitalDia(AhdHospitaisDia hospitalDia) {
		this.hospitalDia = hospitalDia;
	}
	public AinInternacao getInternacao() {
		return internacao;
	}
	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}
	public AinAtendimentosUrgencia getAtendimentoUrgencia() {
		return atendimentoUrgencia;
	}
	public void setAtendimentoUrgencia(AinAtendimentosUrgencia atendimentoUrgencia) {
		this.atendimentoUrgencia = atendimentoUrgencia;
	}
	public Date getDthrFim() {
		return dthrFim;
	}
	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
	public Date getDthrUltImprSumrPrescr() {
		return dthrUltImprSumrPrescr;
	}
	public void setDthrUltImprSumrPrescr(Date dthrUltImprSumrPrescr) {
		this.dthrUltImprSumrPrescr = dthrUltImprSumrPrescr;
	}
	public DominioTipoLaminaLaringo getTipoLaminaLaringo() {
		return tipoLaminaLaringo;
	}
	public void setTipoLaminaLaringo(DominioTipoLaminaLaringo tipoLaminaLaringo) {
		this.tipoLaminaLaringo = tipoLaminaLaringo;
	}
	public AinLeitos getLeito() {
		return leito;
	}
	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}
	public AinQuartos getQuarto() {
		return quarto;
	}
	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}
	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}
	public Date getDthrIngressoUnidade() {
		return dthrIngressoUnidade;
	}
	public void setDthrIngressoUnidade(Date dthrIngressoUnidade) {
		this.dthrIngressoUnidade = dthrIngressoUnidade;
	}
	public DominioTipoTratamentoAtendimento getIndTipoTratamento() {
		return indTipoTratamento;
	}
	public void setIndTipoTratamento(
			DominioTipoTratamentoAtendimento indTipoTratamento) {
		this.indTipoTratamento = indTipoTratamento;
	}
	public DominioSituacaoSumarioAlta getIndSitSumarioAlta() {
		return indSitSumarioAlta;
	}
	public void setIndSitSumarioAlta(DominioSituacaoSumarioAlta indSitSumarioAlta) {
		this.indSitSumarioAlta = indSitSumarioAlta;
	}
	public DominioControleSumarioAltaPendente getCtrlSumrAltaPendente() {
		return ctrlSumrAltaPendente;
	}
	public void setCtrlSumrAltaPendente(
			DominioControleSumarioAltaPendente ctrlSumrAltaPendente) {
		this.ctrlSumrAltaPendente = ctrlSumrAltaPendente;
	}
	public AghAtendimentos getAtendimentoMae() {
		return atendimentoMae;
	}
	public void setAtendimentoMae(AghAtendimentos atendimentoMae) {
		this.atendimentoMae = atendimentoMae;
	}
	public McoGestacoes getMcoGestacoes() {
		return mcoGestacoes;
	}
	public void setMcoGestacoes(McoGestacoes mcoGestacoes) {
		this.mcoGestacoes = mcoGestacoes;
	}
	public AipPacientes getPaciente() {
		return paciente;
	}
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	public AacConsultas getConsulta() {
		return consulta;
	}
	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}
	public void setOrigemAtendimento(String origemAtendimento) {
		this.origemAtendimento = origemAtendimento;
	}
	public String getOrigemAtendimento() {
		return origemAtendimento;
	}
	public String getEstadoSaude() {
		return estadoSaude;
	}
	public void setEstadoSaude(String estadoSaude) {
		this.estadoSaude = estadoSaude;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public Integer getQtDiariasAutorizadas() {
		return qtDiariasAutorizadas;
	}
	public void setQtDiariasAutorizadas(Integer qtDiariasAutorizadas) {
		this.qtDiariasAutorizadas = qtDiariasAutorizadas;
	}
	public String getLtoId() {
		return ltoId;
	}
	public void setLtoId(String ltoId) {
		this.ltoId = ltoId;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Integer getIdadePaciente() {
		return idadePaciente;
	}
	public void setIdadePaciente(Integer idadePaciente) {
		this.idadePaciente = idadePaciente;
	}
	public Date getDtNascimentoPaciente() {
		return dtNascimentoPaciente;
	}
	public void setDtNascimentoPaciente(Date dtNascimentoPaciente) {
		this.dtNascimentoPaciente = dtNascimentoPaciente;
	}
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
	public Integer getConNumero() {
		return conNumero;
	}
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}
	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	public String getSenhaAutorizada() {
		return senhaAutorizada;
	}
	public void setSenhaAutorizada(String senhaAutorizada) {
		this.senhaAutorizada = senhaAutorizada;
	}
	public Integer getIntSeq() {
		return intSeq;
	}
	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}
	
	
	
	
}
