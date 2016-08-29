package br.gov.mec.aghu.ambulatorio.vo;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Transient;

import br.gov.mec.aghu.dominio.DominioCorCondicaoAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemConsulta;
import br.gov.mec.aghu.dominio.DominioSinalizacaoControleFrequencia;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.model.AacConsultas.StatusCertificaoDigital;
import br.gov.mec.aghu.core.persistence.BaseEntity;


public class ConsultaAmbulatorioVO implements BaseEntity {
	
	private static final long serialVersionUID = -1154707541135103149L;
	
	private Integer numero;
	private Date dtConsulta;
	private DominioOrigemConsulta origem;
	private Boolean excedeProgramacao;
	private Long codCentral;
	private String nomeEspecialidade;
	private String nomeProfissional;
	private String gradeUnidadeSigla;
	private Short caaSeq;
	private Short tagSeq;
	private Short pgdSeq;
	
	private Boolean chegou;
	private Boolean atender;
	private Boolean reaberto;
	
	private StatusCertificaoDigital statusCertificacaoDigital;	
	
	//paciente
	private String pacienteNome;
	private String nomeSocial;
	private BigInteger pacienteNroCartaoSaude;
	private Integer prontuario;
	private String mascaraProntuario;
	private Integer pacienteCodigo;
	private Date pacienteDtNasc;
	private Date pacienteDtRecadastro;
	private Boolean pacienteNotifGMR;
	
	//gradeAgendamenConsulta
	private Integer gradeSeq;
	private String gradeEquipeNome;
	private Integer gradeProfmatricula;
	private Short gradeProfvinCodigo;
	private Short gradeEspSeq;
	private String gradeEspSigla;
	private String gradeEspNome;
	private Short gradeUnidSeq;
	
	//gradeAgendamenConsulta.aacUnidFuncionalSala.id.sala
	private Byte gradeUnidIdsala;
	
	//gradeAgendamenConsulta.siglaUnfSala.id.sala
	private Byte gradeSiglaUnfIdsala;
	
	//convenioSaudePlano
	private String convenioDescricao;
	private String convenioSaudeDescricao;
	
	//condicaoAtendimento
	private DominioCorCondicaoAtendimento condicaoCorExibica;
	private String condicaoDescricao;
	
	//controle
	private Date controledtHrInicio;
	private Date controledtHrFim;
	private Date controledthrChegada;
	private DominioSituacaoControle controleSituacao;
	private String microDoAtendimento;
	
	//controle.mamSituacaoAtendimento
	private Short controleSituacaoAtendimentoSeq;
	private Boolean controleSituacaoAtendimentoAgendado;
	private Boolean controleSituacaoAtendimentoPacAtend;
	
	//controle.servidorResponsavel;
	private Integer controleSerMatricula;
	private Short controleServinCodigo;
	private String controleServNome;
	
	//CONTROLE DE APAC
	private DominioSinalizacaoControleFrequencia controleFrequencia;
	private String tipoSinalizacao;
	//CARACTER QUE INDICA IMPRESSORA = 'I / CIRCULO = 'C' / NULO = 'N'
	private String indicaImagem;
	//USADO PARA MODIFICAR O TITLE DEPEDENDO DO DOMINIO SINALIZAÇÃO
	private String title;
	
	//Setado na controller
	private Byte salaAtendimento;    
	
	//AacSituacaoConsultas.situacao
	private String situacaoConsulta;
	
	//retorno
	private Integer retornoSeq;
	private String retornoDescricao;
	private Integer idade;
	private DominioCorCondicaoAtendimento condiaoCorExibica;
	
	
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public Date getDtConsulta() {
		return dtConsulta;
	}
	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}
	public Boolean getChegou() {
		if (chegou==null){
			if (getControleSituacaoAtendimentoAgendado()==null || getControleSituacaoAtendimentoAgendado()){
				chegou=false;
			}else{
				chegou=true;
			}
		}
		return chegou;
	}
	public void setChegou(Boolean chegou) {
		this.chegou = chegou;
	}
	public Boolean getAtender() {
		if (atender==null){
			if (Boolean.TRUE.equals(controleSituacaoAtendimentoPacAtend)) {
				atender=true;
			}else{
				atender=false;
			}
		}
		return atender;
	}
	public void setAtender(Boolean atender) {
		this.atender = atender;
	}
	public Boolean getReaberto() {
		return reaberto;
	}
	public void setReaberto(Boolean reaberto) {
		this.reaberto = reaberto;
	}
	public String getPacienteNome() {
		return pacienteNome;
	}
	public void setPacienteNome(String pacienteNome) {
		this.pacienteNome = pacienteNome;
	}
	public String getNomeSocial() {
		return nomeSocial;
	}
	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}
	public BigInteger getPacienteNroCartaoSaude() {
		return pacienteNroCartaoSaude;
	}
	public void setPacienteNroCartaoSaude(BigInteger pacienteNroCartaoSaude) {
		this.pacienteNroCartaoSaude = pacienteNroCartaoSaude;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Integer getGradeSeq() {
		return gradeSeq;
	}
	public void setGradeSeq(Integer gradeSeq) {
		this.gradeSeq = gradeSeq;
	}
	public String getGradeEquipeNome() {
		return gradeEquipeNome;
	}
	public void setGradeEquipeNome(String gradeEquipeNome) {
		this.gradeEquipeNome = gradeEquipeNome;
	}
	public Integer getGradeProfmatricula() {
		return gradeProfmatricula;
	}
	public void setGradeProfmatricula(Integer gradeProfmatricula) {
		this.gradeProfmatricula = gradeProfmatricula;
	}
	public Short getGradeProfvinCodigo() {
		return gradeProfvinCodigo;
	}
	public void setGradeProfvinCodigo(Short gradeProfvinCodigo) {
		this.gradeProfvinCodigo = gradeProfvinCodigo;
	}
	public String getGradeUnidadeSigla() {
		return gradeUnidadeSigla;
	}
	public void setGradeUnidadeSigla(String gradeUnidadeSigla) {
		this.gradeUnidadeSigla = gradeUnidadeSigla;
	}
	public String getGradeEspSigla() {
		return gradeEspSigla;
	}
	public void setGradeEspSigla(String gradeEspSigla) {
		this.gradeEspSigla = gradeEspSigla;
	}
	public String getGradeEspNome() {
		return gradeEspNome;
	}
	public void setGradeEspNome(String gradeEspNome) {
		this.gradeEspNome = gradeEspNome;
	}
	public Byte getGradeUnidIdsala() {
		return gradeUnidIdsala;
	}
	public void setGradeUnidIdsala(Byte gradeUnidIdsala) {
		this.gradeUnidIdsala = gradeUnidIdsala;
	}
	public Byte getGradeSiglaUnfIdsala() {
		return gradeSiglaUnfIdsala;
	}
	public void setGradeSiglaUnfIdsala(Byte gradeSiglaUnfIdsala) {
		this.gradeSiglaUnfIdsala = gradeSiglaUnfIdsala;
	}
	public String getConvenioDescricao() {
		return convenioDescricao;
	}
	public void setConvenioDescricao(String convenioDescricao) {
		this.convenioDescricao = convenioDescricao;
	}
	public String getCondicaoDescricao() {
		return condicaoDescricao;
	}
	public void setCondicaoDescricao(String condicaoDescricao) {
		this.condicaoDescricao = condicaoDescricao;
	}
	public Date getControledtHrInicio() {
		return controledtHrInicio;
	}
	public void setControledtHrInicio(Date controledtHrInicio) {
		this.controledtHrInicio = controledtHrInicio;
	}
	public Date getControledtHrFim() {
		return controledtHrFim;
	}
	public void setControledtHrFim(Date controledtHrFim) {
		this.controledtHrFim = controledtHrFim;
	}
	public Date getControledthrChegada() {
		return controledthrChegada;
	}
	public void setControledthrChegada(Date controledthrChegada) {
		this.controledthrChegada = controledthrChegada;
	}
	public Integer getRetornoSeq() {
		return retornoSeq;
	}
	public void setRetornoSeq(Integer retornoSeq) {
		this.retornoSeq = retornoSeq;
	}
	
	public String getSituacaoConsulta() {
		return situacaoConsulta;
	}
	public void setSituacaoConsulta(String situacaoConsulta) {
		this.situacaoConsulta = situacaoConsulta;
	}
	public DominioSituacaoControle getControleSituacao() {
		return controleSituacao;
	}
	
	public void setControleSituacao(DominioSituacaoControle controleSituacao) {
		this.controleSituacao = controleSituacao;
	}
	public Short getControleSituacaoAtendimentoSeq() {
		return controleSituacaoAtendimentoSeq;
	}
	public void setControleSituacaoAtendimentoSeq(
			Short controleSituacaoAtendimentoSeq) {
		this.controleSituacaoAtendimentoSeq = controleSituacaoAtendimentoSeq;
	}

	/**
	 * Campo sintético criado para calcular a idade do paciente em uma
	 * determinada data, conforme sua data de nascimento
	 * 
	 * @return
	 */
	@Transient
	public Integer getIdade(Date data) {
		if (this.getPacienteDtNasc() != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(this.getPacienteDtNasc());
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(data);
			// ObtÃ©m a idade baseado no ano
			this.idade = dataCalendario.get(Calendar.YEAR)
					- dataNascimento.get(Calendar.YEAR);
			dataNascimento.add(Calendar.YEAR, this.idade);
			// se a data de hoje Ã© antes da data de Nascimento, entÃ£o diminui
			// 1(um)
			if (dataCalendario.before(dataNascimento)) {
				this.idade--;
			}
		}

		return this.idade;
	}
	
	public Integer getIdade() {
		idade=null;
		if (this.getPacienteDtNasc() != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(this.getPacienteDtNasc());
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(new Date());
			// ObtÃ©m a idade baseado no ano
			idade = dataCalendario.get(Calendar.YEAR)
					- dataNascimento.get(Calendar.YEAR);
			dataNascimento.add(Calendar.YEAR,idade);
			// se a data de hoje Ã© antes da data de Nascimento, entÃ£o diminui
			// 1(um)
			if (dataCalendario.before(dataNascimento)) {
				idade--;
			}
		}
		return idade;
	}
	public Date getPacienteDtNasc() {
		return pacienteDtNasc;
	}
	public void setPacienteDtNasc(Date pacienteDtNasc) {
		this.pacienteDtNasc = pacienteDtNasc;
	}
	public DominioCorCondicaoAtendimento getCondicaoCorExibica() {
		return condicaoCorExibica;
	}
	public void setCondicaoCorExibica(DominioCorCondicaoAtendimento condicaoCorExibica) {
		this.condicaoCorExibica = condicaoCorExibica;
	}
	public String getRetornoDescricao() {
		return retornoDescricao;
	}
	public void setRetornoDescricao(String retornoDescricao) {
		this.retornoDescricao = retornoDescricao;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof ConsultaAmbulatorioVO)){
			return false;
		}	
		ConsultaAmbulatorioVO other = (ConsultaAmbulatorioVO) obj;
		if (numero == null) {
			if (other.numero != null){
				return false;
			}	
		} else if (!numero.equals(other.numero)){
			return false;
		}	
		return true;
	}
	public DominioOrigemConsulta getOrigem() {
		return origem;
	}
	public void setOrigem(DominioOrigemConsulta origem) {
		this.origem = origem;
	}
	public String getConvenioSaudeDescricao() {
		return convenioSaudeDescricao;
	}
	public void setConvenioSaudeDescricao(String convenioSaudeDescricao) {
		this.convenioSaudeDescricao = convenioSaudeDescricao;
	}
	public Boolean getExcedeProgramacao() {
		return excedeProgramacao;
	}
	public void setExcedeProgramacao(Boolean excedeProgramacao) {
		this.excedeProgramacao = excedeProgramacao;
	}
	public StatusCertificaoDigital getStatusCertificacaoDigital() {
		return statusCertificacaoDigital;
	}
	public void setStatusCertificacaoDigital(
			StatusCertificaoDigital statusCertificacaoDigital) {
		this.statusCertificacaoDigital = statusCertificacaoDigital;
	}
	public Integer getPacienteCodigo() {
		return pacienteCodigo;
	}
	public void setPacienteCodigo(Integer pacienteCodigo) {
		this.pacienteCodigo = pacienteCodigo;
	}
	public Short getGradeEspSeq() {
		return gradeEspSeq;
	}
	public void setGradeEspSeq(Short gradeEspSeq) {
		this.gradeEspSeq = gradeEspSeq;
	}
	public Long getCodCentral() {
		return codCentral;
	}
	public void setCodCentral(Long codCentral) {
		this.codCentral = codCentral;
	}
	public Boolean getControleSituacaoAtendimentoAgendado() {
		return controleSituacaoAtendimentoAgendado;
	}
	public void setControleSituacaoAtendimentoAgendado(
			Boolean controleSituacaoAtendimentoAgendado) {
		this.controleSituacaoAtendimentoAgendado = controleSituacaoAtendimentoAgendado;
	}
	public Boolean getControleSituacaoAtendimentoPacAtend() {
		return controleSituacaoAtendimentoPacAtend;
	}
	public void setControleSituacaoAtendimentoPacAtend(
			Boolean controleSituacaoAtendimentoPacAtend) {
		this.controleSituacaoAtendimentoPacAtend = controleSituacaoAtendimentoPacAtend;
	}
	public String getMicroDoAtendimento() {
		return microDoAtendimento;
	}
	public void setMicroDoAtendimento(String microDoAtendimento) {
		this.microDoAtendimento = microDoAtendimento;
	}
	public Date getPacienteDtRecadastro() {
		return pacienteDtRecadastro;
	}
	public void setPacienteDtRecadastro(Date pacienteDtRecadastro) {
		this.pacienteDtRecadastro = pacienteDtRecadastro;
	}
	
	public Boolean getPacienteNotifGMR() {
		return pacienteNotifGMR;
	}
	public void setPacienteNotifGMR(Boolean pacienteNotifGMR) {
		this.pacienteNotifGMR = pacienteNotifGMR;
	}
	public Byte getSalaAtendimento() {
		return salaAtendimento;
	}
	public void setSalaAtendimento(Byte salaAtendimento) {
		this.salaAtendimento = salaAtendimento;
	}
	public Integer getControleSerMatricula() {
		return controleSerMatricula;
	}
	public void setControleSerMatricula(Integer controleSerMatricula) {
		this.controleSerMatricula = controleSerMatricula;
	}
	public Short getControleServinCodigo() {
		return controleServinCodigo;
	}
	public void setControleServinCodigo(Short controleServinCodigo) {
		this.controleServinCodigo = controleServinCodigo;
	}
	public String getControleServNome() {
		return controleServNome;
	}
	public void setControleServNome(String controleServNome) {
		this.controleServNome = controleServNome;
	}
	public Short getCaaSeq() {
		return caaSeq;
	}
	public void setCaaSeq(Short caaSeq) {
		this.caaSeq = caaSeq;
	}
	public Short getTagSeq() {
		return tagSeq;
	}
	public void setTagSeq(Short tagSeq) {
		this.tagSeq = tagSeq;
	}
	public Short getPgdSeq() {
		return pgdSeq;
	}
	public void setPgdSeq(Short pgdSeq) {
		this.pgdSeq = pgdSeq;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public DominioSinalizacaoControleFrequencia getControleFrequencia() {
		return controleFrequencia;
	}
	public void setControleFrequencia(DominioSinalizacaoControleFrequencia controleFrequencia) {
		this.controleFrequencia = controleFrequencia;
	}
	public String getTipoSinalizacao() {
		return tipoSinalizacao;
	}
	public void setTipoSinalizacao(String tipoSinalizacao) {
		this.tipoSinalizacao = tipoSinalizacao;
	}
	public String getIndicaImagem() {
		return indicaImagem;
	}
	public void setIndicaImagem(String indicaImagem) {
		this.indicaImagem = indicaImagem;
	}
	public String getMascaraProntuario() {
		return mascaraProntuario;
	}
	public void setMascaraProntuario(String mascaraProntuario) {
		this.mascaraProntuario = mascaraProntuario;
	}
	public String getNomeProfissional() {
		return nomeProfissional;
	}
	public void setNomeProfissional(String nomeProfissional) {
		this.nomeProfissional = nomeProfissional;
	}	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public DominioCorCondicaoAtendimento getCondiaoCorExibica() {
		return condiaoCorExibica;
	}
	public void setCondiaoCorExibica(DominioCorCondicaoAtendimento condiaoCorExibica) {
		this.condiaoCorExibica = condiaoCorExibica;
	}
	public Short getGradeUnidSeq() {
		return gradeUnidSeq;
	}
	public void setGradeUnidSeq(Short gradeUnidSeq) {
		this.gradeUnidSeq = gradeUnidSeq;
	}
}