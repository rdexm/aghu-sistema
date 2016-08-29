package br.gov.mec.aghu.controlepaciente.vo;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;

/**
 * Representa um atendimento na lista de pacientes internados do controle do
 * paciente.
 * 
 * @author dcastro
 * 
 */
@SuppressWarnings("ucd")
public class PacienteInternadoVO {

	
	private Integer atdSeq;
	private String nome;
	private Integer pacCodigo;
	private Integer prontuario;
	private Date dataNascimento;
	private Date dataInicioAtendimento;
	private String especialidade;
	private StatusSinalizadorUP sinalizadorUlceraPressao;
	private Boolean sumarioAlta;
	private Boolean disableIconeChecagem;
	private Short unfSeq;
	private String leitoId;
	private Short nroQuarto;
	private String andarUnidade;
	private AghAla alaUnidade;
	private String descricaoUnidade;
	private boolean pacienteNotifGMR;
	private String descPacienteNotifGMR;

	// construtores

	public PacienteInternadoVO() {

	}

	/**
	 * Constroi o vo com os dados do atendimento fornecido.
	 * 
	 * @param atendimento
	 */
	public PacienteInternadoVO(AghAtendimentos atendimento) {
		this.atdSeq = atendimento.getSeq();
		this.pacCodigo = atendimento.getPaciente().getCodigo();
		this.nome = atendimento.getPaciente().getNome();
		this.dataInicioAtendimento = atendimento.getDthrInicio();
		this.dataNascimento = atendimento.getPaciente().getDtNascimento();
		this.prontuario = atendimento.getProntuario();
		this.especialidade = atendimento.getEspecialidade().getNomeReduzido();
	}

	// getters & setters

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

	/**
	 * Código do paciente.
	 * 
	 * @return
	 */
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
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
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
	 * Especialidade do atendimento
	 * 
	 * @return
	 */
	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	/**
	 * Status sinalizador de úlcera de pressão
	 * 
	 * @return
	 */
	public StatusSinalizadorUP getSinalizadorUlceraPressao() {
		return sinalizadorUlceraPressao;
	}

	public void setSinalizadorUlceraPressao(
			StatusSinalizadorUP sinalizadorUlceraPressao) {
		this.sinalizadorUlceraPressao = sinalizadorUlceraPressao;
	}

	/**
	 * Existe sumário alta médica validado
	 * 
	 * @return
	 */
	public Boolean getSumarioAlta() {
		return sumarioAlta;
	}

	public void setSumarioAlta(Boolean sumarioAlta) {
		this.sumarioAlta = sumarioAlta;
	}

	/**
	 * Controle para habilitar/desabilitar ícone da checagem
	 * 
	 * @return
	 */
	public Boolean getDisableIconeChecagem() {
		return disableIconeChecagem;
	}

	public void setDisableIconeChecagem(Boolean disableIconeChecagem) {
		this.disableIconeChecagem = disableIconeChecagem;
	}

	/**
	 * Identificador do leito - utilizado para montar o local do atendimento
	 * 
	 * @return
	 */
	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}

	/**
	 * Número do quarto - utilizado para montar o local do atendimento
	 * 
	 * @return
	 */
	public Short getNroQuarto() {
		return nroQuarto;
	}

	public void setNroQuarto(Short nroQuarto) {
		this.nroQuarto = nroQuarto;
	}

	/**
	 * Andar da unidade funcional - utilizado para montar o local do atendimento
	 * 
	 * @return
	 */
	public String getAndarUnidade() {
		return andarUnidade;
	}

	public void setAndarUnidade(String andarUnidade) {
		this.andarUnidade = andarUnidade;
	}

	/**
	 * Ala da unidade funcional - utilizado para montar o local do atendimento
	 * 
	 * @return
	 */
	public AghAla getAlaUnidade() {
		return alaUnidade;
	}

	public void setAlaUnidade(AghAla a) {
		this.alaUnidade = a;
	}

	/**
	 * Descrição da unidade funcional - utilizado para montar o local do
	 * atendimento
	 * 
	 * @return
	 */
	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}

	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}

	/**
	 * Identificador da unidade funcional
	 * 
	 * @return
	 */
	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
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
	 * Representa o status da prescrição.
	 * 
	 */
	public enum StatusSinalizadorUP {
		/**
		 * Flag verde.
		 */
		FLAG_VERDE,
		/**
		 * Flag amarelo.
		 */
		FLAG_AMARELO,
		/**
		 * Flag vermelho.
		 */
		FLAG_VERMELHO;

	}

	/**
	 * Retorna descrição para a localização.<br>
	 * Retorna uma descrição de acordo com os argumentos fornecidos.
	 * 
	 * @param leito
	 * @param quarto
	 * @param unidade
	 * @return
	 */
	public String getDescricaoLocalizacao() {

		if (this.leitoId == null && this.nroQuarto == null
				&& this.andarUnidade == null && this.alaUnidade == null
				&& this.descricaoUnidade == null) {
			return null;
		}

		if (leitoId != null) {
			return String.format("L:%s", leitoId);
		}
		if (nroQuarto != null) {
			return String.format("Q:%s", nroQuarto);
		}

		String string = String.format("U:%s %s - %s", andarUnidade, alaUnidade,
				descricaoUnidade);

		return StringUtils.substring(string, 0, 8);
	}

	// outros

	public boolean isPacienteNotifGMR() {
		return pacienteNotifGMR;
	}

	public void setPacienteNotifGMR(boolean pacienteNotifGMR) {
		this.pacienteNotifGMR = pacienteNotifGMR;
	}

	public String getDescPacienteNotifGMR() {
		return descPacienteNotifGMR;
	}

	public void setDescPacienteNotifGMR(String descPacienteNotifGMR) {
		this.descPacienteNotifGMR = descPacienteNotifGMR;
	}

	public String toString() {
		return new ToStringBuilder(this).append("atdSeq", this.atdSeq)
				.toString();
	}

	public boolean equals(Object other) {
		if (!(other instanceof PacienteInternadoVO)) {
			return false;
		}
		PacienteInternadoVO castOther = (PacienteInternadoVO) other;
		return new EqualsBuilder().append(this.atdSeq, castOther.getAtdSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.atdSeq).toHashCode();
	}

}
