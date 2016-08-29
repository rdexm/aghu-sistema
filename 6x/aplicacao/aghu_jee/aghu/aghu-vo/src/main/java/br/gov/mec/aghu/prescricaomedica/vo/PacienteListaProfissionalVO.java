package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.view.VMpmListaPacInternados;

/**
 * Representa um atendimento na lista de pacientes internados do profissional.
 * 
 * @author cvagheti
 * 
 */

public class PacienteListaProfissionalVO implements Serializable {
	
	private static final long serialVersionUID = -652711289609470162L;
	private Integer atdSeq;
	private String local;
	private String nome;
	private String nomeSocial;
	private Integer pacCodigo;
	private String prontuario;
	private Date dataNascimento;
	private String nomeResponsavel;
	private Date dataInicioAtendimento;
	private Date dataFimAtendimento;

	private StatusPrescricao statusPrescricao;
	private StatusSumarioAlta statusSumarioAlta;
	private StatusPendenciaDocumento statusPendenciaDocumento;
	private StatusCertificaoDigital statusCertificacaoDigital;
	private StatusExamesNaoVistos statusExamesNaoVistos;
	private StatusPacientePesquisa statusPacientePesquisa;
	private StatusEvolucao statusEvolucao;
	private StatusAnamneseEvolucao statusAnamneseEvolucao;
	// TODO: trocar o tipo para enum e usar como chave no resource bundle para
	// icone, descrição, tooltip, etc.
	private String iconExames;
	private String iconPendencias;
	private String iconProjetoPesquisa;
	private String iconEvolucao;
	private String iconCertificacao;

	private StatusAltaObito labelAlta;
	private StatusAltaObito labelObito;
	private boolean disableButtonAltaObito;
	private boolean disableButtonPrescrever;
	private boolean possuiPlanoAltas;
	private boolean enableButtonAnamneseEvolucao;	
	private Boolean indGmr;
	
	// construtores

	public PacienteListaProfissionalVO() {

	}

	/**
	 * Constroi o vo com os dados do atendimento fornecido.
	 * 
	 * @param atendimento
	 */
	public PacienteListaProfissionalVO(AghAtendimentos atendimento) {
		this.atdSeq = atendimento.getSeq();
		this.pacCodigo = atendimento.getPaciente().getCodigo();
		this.nome = atendimento.getPaciente().getNome();
		if(StringUtils.isNotBlank(atendimento.getPaciente().getNomeSocial())){
			this.nomeSocial = atendimento.getPaciente().getNomeSocial();
		}else{
			this.nomeSocial = null;
		}
		this.dataInicioAtendimento = atendimento.getDthrInicio();
		this.dataFimAtendimento = atendimento.getDthrFim();
		this.dataNascimento = atendimento.getPaciente().getDtNascimento();
		if (atendimento.getProntuario() != null) {
			this.prontuario = atendimento.getProntuario().toString();
		}
		if (atendimento.getServidor() != null
				&& atendimento.getServidor().getPessoaFisica() != null) {
			if (StringUtils.isNotBlank(atendimento.getServidor()
					.getPessoaFisica().getNomeUsual())) {
				this.nomeResponsavel = atendimento.getServidor()
						.getPessoaFisica().getNomeUsual();
			} else {
				this.nomeResponsavel = atendimento.getServidor()
						.getPessoaFisica().getNome();
			}
		}
	}

	// getters & setters
	//@SuppressWarnings("PMD")
	public PacienteListaProfissionalVO(VMpmListaPacInternados pacInternado) {
        	this.atdSeq = pacInternado.getAtdSeq();
        	this.pacCodigo = pacInternado.getPacCodigo();
        	this.nome = pacInternado.getNome();
    		if (StringUtils.isNotBlank(pacInternado.getNomeSocial())) {
    			this.nomeSocial = pacInternado.getNomeSocial();
    		} else {
    			this.nomeSocial = null;
    		}
        	this.dataInicioAtendimento = pacInternado.getDataInicioAtendimento();
        	this.dataFimAtendimento =pacInternado.getDataFimAtendimento();
        	this.dataNascimento = pacInternado.getDataNascimento();
        	this.prontuario = pacInternado.getProntuario();
        	this.nomeResponsavel = pacInternado.getNomeResponsavel();
        	
        	if (StringUtils.isNotBlank(pacInternado.getPossuiPlanoAltas())){
        	    this.possuiPlanoAltas = Boolean.valueOf(pacInternado.getPossuiPlanoAltas());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getLocal())){
        	    this.local = StringUtils.substring(pacInternado.getLocal(), 0, 8);
        	}
        	if (StringUtils.isNotBlank(pacInternado.getStatusPrescricao())){
        	    this.statusPrescricao = StatusPrescricao.valueOf(pacInternado.getStatusPrescricao());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getStatusSumarioAlta())){
        	    this.statusSumarioAlta = StatusSumarioAlta.valueOf(pacInternado.getStatusSumarioAlta());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getStatusExamesNaoVistos())){
        	    this.statusExamesNaoVistos = StatusExamesNaoVistos.valueOf(pacInternado.getStatusExamesNaoVistos());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getStatusPendenciaDocumento())){
        	    this.statusPendenciaDocumento = StatusPendenciaDocumento.valueOf(pacInternado.getStatusPendenciaDocumento());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getStatusPacientePesquisa())){
        	    this.statusPacientePesquisa = StatusPacientePesquisa.valueOf(pacInternado.getStatusPacientePesquisa());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getStatusEvolucao())){
        	    this.statusEvolucao = StatusEvolucao.valueOf(pacInternado.getStatusEvolucao());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getStatusCertificacaoDigital())){
        	    this.statusCertificacaoDigital = StatusCertificaoDigital.valueOf(pacInternado.getStatusCertificacaoDigital());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getDisableButtonAltaObito())){
        	    this.disableButtonAltaObito = Boolean.valueOf(pacInternado.getDisableButtonAltaObito());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getDisableButtonPrescrever())){
        	    this.disableButtonPrescrever = Boolean.valueOf(pacInternado.getDisableButtonPrescrever());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getLabelAlta())){
        	    this.labelAlta = StatusAltaObito.valueOf(pacInternado.getLabelAlta());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getLabelObito())){
        	    this.labelObito =  StatusAltaObito.valueOf(pacInternado.getLabelObito());
        	}
        	if (StringUtils.isNotBlank(pacInternado.getIndGmr())){
        	    this.indGmr = Boolean.valueOf(pacInternado.getIndGmr());
        	}
        	
	}

	/**
	 * Id do atendimento do paciente.
	 * 
	 * @return
	 */
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	
	public Boolean getIndGmr() {
		return indGmr;
	}

	public void setIndGmr(Boolean indGmr) {
		this.indGmr = indGmr;
	}

	/**
	 * Localização do paciente.
	 * 
	 * @return
	 */
	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	/**
	 * Nome do paciente.
	 * 
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	/**
	 * Prontuário do paciente.
	 * 
	 * @return
	 */
	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	/**
	 * Data de nascimento do paciente.
	 * 
	 * @return
	 */
	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/**
	 * Nome do profissional responsável pelo atendimento.
	 * 
	 * @return
	 */
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	/**
	 * Data e hora de inicio do atendimento.
	 * 
	 * @return
	 */
	public Date getDataInicioAtendimento() {
		return dataInicioAtendimento;
	}

	public void setDataInicioAtendimento(Date dataInicioAtendimento) {
		this.dataInicioAtendimento = dataInicioAtendimento;
	}

	/**
	 * Data e hora de fim do atendimento.
	 * 
	 * @return
	 */
	public Date getDataFimAtendimento() {
		return dataFimAtendimento;
	}

	public void setDataFimAtendimento(Date dataFimAtendimento) {
		this.dataFimAtendimento = dataFimAtendimento;
	}

	/**
	 * Retorna o status da prescrição.
	 * 
	 * @return
	 */
	public StatusPrescricao getStatusPrescricao() {
		return statusPrescricao;
	}

	public void setStatusPrescricao(StatusPrescricao statusPrescricao) {
		this.statusPrescricao = statusPrescricao;
	}

	/**
	 * Retorna o status do sumário de alta.
	 * 
	 * @return
	 */
	public StatusSumarioAlta getStatusSumarioAlta() {
		return statusSumarioAlta;
	}

	public void setStatusSumarioAlta(StatusSumarioAlta statusSumarioAlta) {
		this.statusSumarioAlta = statusSumarioAlta;
	}

	public StatusExamesNaoVistos getStatusExamesNaoVistos() {
		return statusExamesNaoVistos;
	}

	public void setStatusExamesNaoVistos(StatusExamesNaoVistos statusExamesNaoVistos) {
		this.statusExamesNaoVistos = statusExamesNaoVistos;
	}

	/**
	 * Ícones informativos para exames.
	 * 
	 * @return
	 */
	public String getIconExames() {
		return iconExames;
	}

	public void setIconExames(String iconExames) {
		this.iconExames = iconExames;
	}

	/**
	 * Ícones informativos para pendências de preenchimento de documentos.
	 * 
	 * @return
	 */
	public String getIconPendencias() {
		return iconPendencias;
	}

	public void setIconPendencias(String iconPendencias) {
		this.iconPendencias = iconPendencias;
	}

	/**
	 * Ícones informativos para paciente em projeto de pesquisa.
	 * 
	 * @return
	 */
	public String getIconProjetoPesquisa() {
		return iconProjetoPesquisa;
	}

	public void setIconProjetoPesquisa(String iconProjetoPesquisa) {
		this.iconProjetoPesquisa = iconProjetoPesquisa;
	}

	/**
	 * Ícones informativos sobre evolução.
	 * 
	 * @return
	 */
	public String getIconEvolucao() {
		return iconEvolucao;
	}

	public void setIconEvolucao(String iconEvolucao) {
		this.iconEvolucao = iconEvolucao;
	}

	/**
	 * Ícones informativos para certificação digital.
	 * 
	 * @return
	 */
	public String getIconCertificacao() {
		return iconCertificacao;
	}

	public void setIconCertificacao(String iconCertificacao) {
		this.iconCertificacao = iconCertificacao;
	}

	public StatusAltaObito getLabelAlta() {
		return labelAlta;
	}

	public void setLabelAlta(StatusAltaObito labelAlta) {
		this.labelAlta = labelAlta;
	}

	public StatusAltaObito getLabelObito() {
		return labelObito;
	}

	public void setLabelObito(StatusAltaObito labelObito) {
		this.labelObito = labelObito;
	}

	public boolean isDisableButtonAltaObito() {
		return disableButtonAltaObito;
	}

	public void setDisableButtonAltaObito(boolean disableButtonAltaObito) {
		this.disableButtonAltaObito = disableButtonAltaObito;
	}

	public boolean isDisableButtonPrescrever() {
		return disableButtonPrescrever;
	}

	public void setDisableButtonPrescrever(boolean disableButtonPrescrever) {
		this.disableButtonPrescrever = disableButtonPrescrever;
	}

	public StatusPendenciaDocumento getStatusPendenciaDocumento() {
		return statusPendenciaDocumento;
	}

	public void setStatusPendenciaDocumento(
			StatusPendenciaDocumento statusPendenciaDocumento) {
		this.statusPendenciaDocumento = statusPendenciaDocumento;
	}

	public void setStatusCertificacaoDigital(StatusCertificaoDigital statusCertificacaoDigital) {
		this.statusCertificacaoDigital = statusCertificacaoDigital;
	}

	public StatusCertificaoDigital getStatusCertificacaoDigital() {
		return statusCertificacaoDigital;
	}
	
	public StatusAnamneseEvolucao getStatusAnamneseEvolucao() {
		return statusAnamneseEvolucao;
	}

	public void setStatusAnamneseEvolucao(StatusAnamneseEvolucao statusAnamneseEvolucao) {
		this.statusAnamneseEvolucao = statusAnamneseEvolucao;
	}
	

	public StatusPacientePesquisa getStatusPacientePesquisa() {
		return statusPacientePesquisa;
	}

	public void setStatusPacientePesquisa(
			StatusPacientePesquisa statusPacientePesquisa) {
		this.statusPacientePesquisa = statusPacientePesquisa;
	}
	
	

	public StatusEvolucao getStatusEvolucao() {
		return statusEvolucao;
	}

	public void setStatusEvolucao(StatusEvolucao statusEvolucao) {
		this.statusEvolucao = statusEvolucao;
	}

	/**
	 * Retorna o tempo de permanência em dias.<br>
	 * Calculado pela diferença entre data final do atendimento e a data de
	 * inicio do atendimento. Se a data final do atendimento for null calcula
	 * com a data atual.
	 * 
	 * @return null se {@link #getDataInicioAtendimento()} == null e -1 se houve
	 *         inconsistência de datas
	 */
	public Integer getPermanencia() {
		if (this.getDataInicioAtendimento() == null) {
			return null;
		}

		DateTime dataInicio = new DateTime(this.getDataInicioAtendimento());
		DateTime dataFim = new DateTime(DateUtil.hojeSeNull(this
				.getDataFimAtendimento()));
		Interval interval = null;
		if (dataInicio.isBefore(dataFim)) {
			interval = new Interval(dataInicio, dataFim);
		} else {
			//TODOMIGRACAO
			// para evitar erro irrecuperavel na aplicação o problema foi logado
//			LOG.warn(String.format(
//							"houve problemas ao calcular "
//									+ "o tempo de permanência do atendimento %s."
//									+ " o calculo é (dataFim==null?hoje:dataFimAtendimento) - dataInicio"
//									+ " a data de inicio é maior que a data de fim."
//									+ " as datas são inicio %s e fim %s e dataFimAtendimento %s",
//							atdSeq, dataInicio, dataFim, this
//									.getDataFimAtendimento()));
			return -1;
		}
		int dias = interval.toPeriod(PeriodType.days()).getDays();

		return dias;

	}

	/**
	 * Retorna a idade do paciente na data corrente.
	 * 
	 * @return null se {@link #getDataNascimento()} == null
	 */
	public Integer getIdade() {
		if (this.getDataNascimento() == null) {
			return null;
		}
		Period period = new Period(this.getDataNascimento().getTime(), Calendar
				.getInstance().getTimeInMillis(), PeriodType.years());

		return period.getYears();
	}

	/**
	 * Método utilizado para realizar a ordenação da lista
	 * 
	 * @return
	 */
	public Integer getProntuarioLista() {
		if (this.getProntuario() == null) {
			return null;
		}
		return Integer.parseInt(this.getProntuario());

	}

	
	public Integer getOrdemIconeAnamneseEvolucao() {
		Integer retorno = 0;
		if(this.enableButtonAnamneseEvolucao && this.statusAnamneseEvolucao != null 
				&& this.statusAnamneseEvolucao == StatusAnamneseEvolucao.ANAMNESE_NAO_REALIZADA) {
			retorno = 1;
		} else if(this.enableButtonAnamneseEvolucao && this.statusAnamneseEvolucao != null 
				&& this.statusAnamneseEvolucao == StatusAnamneseEvolucao.ANAMNESE_PENDENTE) {
			retorno = 2;
		} else if(this.enableButtonAnamneseEvolucao && this.statusAnamneseEvolucao != null 
				&& this.statusAnamneseEvolucao == StatusAnamneseEvolucao.EVOLUCAO_DO_DIA_NAO_REALIZADA) {
			retorno = 3;
		} else if(this.enableButtonAnamneseEvolucao && this.statusAnamneseEvolucao != null 
				&& this.statusAnamneseEvolucao == StatusAnamneseEvolucao.EVOLUCAO_DO_DIA_PENDENTE) {
			retorno = 4;
		} else if(this.enableButtonAnamneseEvolucao && this.statusAnamneseEvolucao != null 
				&& this.statusAnamneseEvolucao == StatusAnamneseEvolucao.EVOLUCAO_VENCE_NO_DIA_SEGUINTE) {
			retorno = 5;
		} 
		return retorno;
	}
	
	/**
	 * Método utilizado para ordenar os ícones da prescrição
	 * 
	 * @return
	 */
	public Integer getOrdemIconePrescricao() {

		Integer retorno = 4;

		if (this.getStatusPrescricao() == null) {
			return retorno;
		}

		if (StatusPrescricao.PRESCRICAO_NAO_REALIZADA.equals(this
				.getStatusPrescricao())) {
			retorno = 1;
		} else {
			if (StatusPrescricao.PRESCRICAO_VENCE_NO_DIA.equals(this
					.getStatusPrescricao())) {
				retorno = 2;
			} else {
				if (StatusPrescricao.PRESCRICAO_VENCE_NO_DIA_SEGUINTE
						.equals(this.getStatusPrescricao())) {
					retorno = 3;
				}
			}
		}
		return retorno;
	}

	/**
	 * Método utilizado para ordenar o ícone do sumário
	 * 
	 * @return
	 */
	public Integer getOrdemIconeSumario() {

		Integer retorno = 3;

		if (this.getStatusSumarioAlta() == null) {
			return retorno;
		} else {
			if (StatusSumarioAlta.SUMARIO_ALTA_NAO_REALIZADO.equals(this
					.getStatusSumarioAlta())) {
				retorno = 1;
			} else {
				if (StatusSumarioAlta.SUMARIO_ALTA_NAO_ENTREGUE_SAMIS
						.equals(this.getStatusSumarioAlta())) {
					retorno = 2;
				}
			}
		}
		return retorno;
	}

	/**
	 * Método utilizado para ordenar o ícone de Pendência de Documento
	 * 
	 * @return
	 */
	public Integer getOrdemIconePendenciaDocumento() {

		Integer retorno = 2;

		if (this.getStatusPendenciaDocumento() == null) {
			return retorno;
		} else {
			if (StatusPendenciaDocumento.PENDENCIA_LAUDO_UTI.equals(this
					.getStatusPendenciaDocumento())) {
				retorno = 1;
			}
		}
		return retorno;
	}

	public enum StatusAnamneseEvolucao {
		/**
		 * Mensagem "Anamnese não realizada."
		 */
		ANAMNESE_NAO_REALIZADA,
		/**
		 * Mensagem "Anamnese pendente."
		 */
		ANAMNESE_PENDENTE,
		/**
		 * Mensagem "Evolução do dia não realizada."
		 */		
		EVOLUCAO_DO_DIA_NAO_REALIZADA,
		/**
		 * Mensagem "Evolução do dia pendente."
		 */
		EVOLUCAO_DO_DIA_PENDENTE,
		/**
		 * Para o caso de adiantamento de uma evolução
		 * Mensagem "Paciente com Evolução que vence no dia seguinte."
		 */
		EVOLUCAO_VENCE_NO_DIA_SEGUINTE;	
	}
	
	
	/**
	 * Método utilizado para ordenar o ícone de Pendência de Documento
	 * 
	 * @return
	 */
	public Integer getOrdemIconeCertificacaoDigital() {

		Integer retorno = 2;
		
		if (this.getStatusCertificacaoDigital() == null) {
			return retorno;
		} else {
			if (StatusCertificaoDigital.PENDENTE.equals(this
					.getStatusCertificacaoDigital())) {
				retorno = 1;
			}
		}

		return retorno;
	}
	
	/**
	 * Método utilizado para ordenar o ícone de Paciente Pesquisa
	 * 
	 * @return
	 */
	public Integer getOrdemIconePacientePesquisa(){
		
		Integer retorno = 2;
		
		if (this.getStatusPacientePesquisa() == null) {
			return retorno;
		} else {
			if (StatusPacientePesquisa.PACIENTE_PESQUISA.equals(this
					.getStatusPacientePesquisa())) {
				retorno = 1;
			}
		}

		return retorno;
	}
	
	/**
	 * Método utilizado para ordenar o ícone de evolucao
	 * 
	 * @return
	 */
	public Integer getOrdemIconeEvolucao(){
		
		Integer retorno = 2;
		
		if (this.getStatusPacientePesquisa() == null) {
			return retorno;
		} else {
			if (StatusPacientePesquisa.PACIENTE_PESQUISA.equals(this
					.getStatusPacientePesquisa())) {
				retorno = 1;
			}
		}

		return retorno;
	
	}
	

	
	/**
	 * Representa o status de exames não vistos.
	 * 
	 * @author mlcneto
	 * 
	 */
	public enum StatusExamesNaoVistos {
		/**
		 * Resultados não visualizados.
		 */
		RESULTADOS_NAO_VISUALIZADOS,
		/**
		 * Resultados visualizados por outro médico.
		 */
		RESULTADOS_VISUALIZADOS_OUTRO_MEDICO,
	}
	
	/**
	 * Representa o status da prescrição.
	 * 
	 * @author cvagheti
	 * 
	 */
	public enum StatusPrescricao {
		/**
		 * Prescrição do dia não realizada.
		 */
		PRESCRICAO_NAO_REALIZADA,
		/**
		 * Prescrição vence no dia.
		 */
		PRESCRICAO_VENCE_NO_DIA,
		/**
		 * Prescrição vence no dia seguinte.
		 */
		PRESCRICAO_VENCE_NO_DIA_SEGUINTE;

	}

	/**
	 * Representa o status do sumário de alta.
	 * 
	 * @author cvagheti
	 * 
	 */
	public enum StatusSumarioAlta {
		/**
		 * Sumário de alta não entregue no SAMIS.
		 */
		SUMARIO_ALTA_NAO_ENTREGUE_SAMIS,
		/**
		 * Sumário de alta não realizado.
		 */
		SUMARIO_ALTA_NAO_REALIZADO;
	}

	public enum StatusPendenciaDocumento {

		/**
		 * Pendência de Laudo de UTI, de acompanhante para o paciente e de
		 * justificativa de tempo de permanência
		 */
		PENDENCIA_LAUDO_UTI,

		/**
		 * Pendência da Ficha Apache
		 */
		PENDENCIA_FICHA_APACHE,

		/**
		 * Pendência dp PIM2
		 */
		PENDENCIA_PIM2

	}

	public enum StatusAltaObito {
		ALTA, OBITO, SUMARIO_ALTA, SUMARIO_OBITO
	}

	public enum StatusCertificaoDigital {
		PENDENTE, ASSINADO
	}
	
	public enum StatusPacientePesquisa {
		PACIENTE_PESQUISA, PACIENTE_NAO_PESQUISA
	}
	
	public enum StatusEvolucao {
		EVOLUCAO, SEM_EVOLUCAO
	}
	
	public boolean isEnableButtonAnamneseEvolucao() {
		return enableButtonAnamneseEvolucao;
	}

	public void setEnableButtonAnamneseEvolucao(boolean enableButtonAnamneseEvolucao) {
		this.enableButtonAnamneseEvolucao = enableButtonAnamneseEvolucao;
	}
	
	// outros

	public String toString() {
		return new ToStringBuilder(this).append("atdSeq", this.atdSeq)
				.toString();
	}

	public boolean equals(Object other) {
		if (!(other instanceof PacienteListaProfissionalVO)) {
			return false;
		}
		PacienteListaProfissionalVO castOther = (PacienteListaProfissionalVO) other;
		return new EqualsBuilder().append(this.atdSeq, castOther.getAtdSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.atdSeq).toHashCode();
	}

	public boolean isPossuiPlanoAltas() {
		return possuiPlanoAltas;
	}

	public void setPossuiPlanoAltas(boolean possuiPlanoAltas) {
		this.possuiPlanoAltas = possuiPlanoAltas;
	}

}
