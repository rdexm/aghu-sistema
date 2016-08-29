package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class SolicitacaoExameFilter implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4358498930324598132L;
	/**
	 * atendimento.paciente.prontuario<br>
	 * Digite o prontuário do paciente.
	 */
	private Integer prontuario;
	/**
	 * atendimento.consulta.numero<br>
	 * Digite o número da consulta.
	 */
	private Integer numero;
	/**
	 * atendimento.paciente.nome
	 * Digite o nome do paciente.
	 */
	private String nomePaciente;
	/**
	 * atendimento.paciente.nomeSocial
	 * Digite o nome social do paciente.
	 */
	private String nomeSocialPaciente;
	/**
	 * atendimento.origem
	 * Selecione o origem do atendimento.
	 */
	private DominioOrigemAtendimento origem;
	/**
	 * atendimento.leito.leitoID;
	 * Digite o leito.
	 */
	private String leito;
	/**
	 * atendimento.quarto.numero
	 * Digite o numero do quarto
	 */
	private String quarto;
	/**
	 * ComboBox
	 * atendimento.unidadeFuncional
	 * Selecione a unidade de internação.
	 */
	private AghUnidadesFuncionais unidade;
	/**
	 * internado;
	 * Verifica se internado.
	 */
	private Boolean internado;
	
	
	
	
	/**
	 * Estah preenchido se algum dos atributos tiver valor nao nulo e nao vazio.
	 * @return
	 */
	public boolean isPreenchido() {
		return (
			this.getProntuario() != null
			|| this.getNumero() != null
			|| StringUtils.isNotBlank(this.getNomePaciente())
			|| this.getOrigem() != null
			|| StringUtils.isNotBlank(this.getLeito())
			|| this.getQuarto() != null
			|| this.getUnidade() != null
		);
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isPreenchidoFiltroMinimo() {
		return (
				this.getProntuario() != null
				|| this.getNumero() != null
				|| StringUtils.isNotBlank(this.getNomePaciente())
				|| StringUtils.isNotBlank(this.getLeito())
				|| this.getQuarto() != null
				|| this.getUnidade() != null
			);
	}
	
	
	
	
	
	/**
	 * @return the prontuario
	 */
	public Integer getProntuario() {
		return prontuario;
	}
	/**
	 * @param prontuario the prontuario to set
	 */
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	/**
	 * @return the numero
	 */
	public Integer getNumero() {
		return numero;
	}
	/**
	 * @param numero the numero to set
	 */
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	/**
	 * @return the nomePaciente
	 */
	public String getNomePaciente() {
		return nomePaciente;
	}
	/**
	 * @param nomePaciente the nomePaciente to set
	 */
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	/**
	 * @return the nomeSocialPaciente
	 */
	public String getNomeSocialPaciente() {
		return nomeSocialPaciente;
	}
	/**
	 * @param nomeSocialPaciente the nomeSocialPaciente to set
	 */
	public void setNomeSocialPaciente(String nomeSocialPaciente) {
		this.nomeSocialPaciente = nomeSocialPaciente;
	}
	/**
	 * @return the origem
	 */
	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}
	/**
	 * @param origem the origem to set
	 */
	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}
	/**
	 * @return the leito
	 */
	public String getLeito() {
		return leito;
	}
	/**
	 * @param leito the leito to set
	 */
	public void setLeito(String leito) {
		this.leito = leito;
	}
	/**
	 * @return the quarto
	 */
	public String getQuarto() {
		return quarto;
	}
	/**
	 * @param quarto the quarto to set
	 */
	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}
	/**
	 * @return the unidade
	 */
	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}
	/**
	 * @param unidade the unidade to set
	 */
	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}
	/**
	 * @param internado to get
	 */
	public Boolean getInternado() {
		return internado;
	}
	/**
	 * @param internado to set
	 */
	public void setInternado(Boolean internado) {
		this.internado = internado;
	}
}
