package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.view.VListaEpePrescricaoEnfermagem;


public class PacienteEnfermagemVO implements Serializable{
	
	private static final long serialVersionUID = 7496865803572100612L;
	private Integer atdSeq;
	private String local;
	private String nome;
	private Integer idade;
	private String prontuario;
	private Date dataAtendimento;
	private StatusPrescricaoEnfermagemPaciente statusPrescEnfPaciente;
	private DominioIndConcluido indAltaSumario;
	private Boolean desabilitaBotaoPrescrever;
	private Boolean desabilitaBotaoControles;
	private Boolean desabilitaBotaoEvolucao;
	private Integer pacCodigo;
	private Boolean indPartProjPesquisa;
	private Boolean indSolicConsultoria;
	private Boolean indSolicConsultoriaResp;
	private Boolean indEvolucao;
	private Boolean indGmr;
	private Boolean indConsultoriaEnf;
	private String  colorGmr;
	private Boolean indPrevAlta;
	private String  colorPrevAlta;
	private StatusSinalizadorUP sinalizadorUlceraPressao;
	
	public PacienteEnfermagemVO(VListaEpePrescricaoEnfermagem atendimento) {
		this.atdSeq = atendimento.getAtdSeq();
		this.nome = atendimento.getNome();
		this.idade = atendimento.getIdade();
		this.dataAtendimento = atendimento.getDtAtendimento();
		this.prontuario = atendimento.getProntuario().toString();
		this.local = atendimento.getLocal();
		this.pacCodigo = atendimento.getPacCodigo();
	}
	
	public String getDescricaoLocalizacao(AinLeitos leito) {
		if (leito != null) {
			return String.format(leito.getLeitoID());
		}
		return "";
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}
	
	/**
	 * Representa o status da prescrição de enfermagem para o paciente.
	 * 
	 * @author diego.pacheco
	 * 
	 */
	public enum StatusPrescricaoEnfermagemPaciente {

		/**
		 * Prescrição do dia não realizada ou pendente 
		 */
		PRESCRICAO_ENFERMAGEM_NAO_REALIZADA_OU_PENDENTE,
		
		/**
		 * Paciente com prescrição que vence no dia
		 */
		PRESCRICAO_ENFERMAGEM_VENCE_NO_DIA, 
		
		/**
		 * Paciente com prescrição pendente que vence no dia
		 */
		PRESCRICAO_ENFERMAGEM_PENDENTE_VENCE_NO_DIA, 
		
		/**
		 * Paciente com prescrição pendente que vence no dia seguinte
		 */
		PRESCRICAO_ENFERMAGEM_PENDENTE_VENCE_NO_DIA_SEGUINTE,
		
		/**
		 * Paciente com prescrições do dia e dia seguinte ambas pendentes
		 */
		PRESCRICAO_ENFERMAGEM_DO_DIA_E_DIA_SEGUINTE_PENDENTES,
		
		/**
		 * Paciente com prescrição que vence no dia seguinte
		 */
		PRESCRICAO_ENFERMAGEM_VENCE_NO_DIA_SEGUINTE;

	}

	public StatusPrescricaoEnfermagemPaciente getStatusPrescEnfPaciente() {
		return statusPrescEnfPaciente;
	}

	public void setStatusPrescEnfPaciente(
			StatusPrescricaoEnfermagemPaciente statusPrescEnfPaciente) {
		this.statusPrescEnfPaciente = statusPrescEnfPaciente;
	}

	public DominioIndConcluido getIndAltaSumario() {
		return indAltaSumario;
	}

	public void setIndAltaSumario(DominioIndConcluido indAltaSumario) {
		this.indAltaSumario = indAltaSumario;
	}
	
	/**
	 * Método utilizado para ordenar os ícones da prescrição
	 * 
	 * @return
	 */
	public Integer getOrdemIconePrescricao() {

		final Integer retornoPadrao = 7;

		if (this.getStatusPrescEnfPaciente() == null) {
			return retornoPadrao;
		} else if (StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_NAO_REALIZADA_OU_PENDENTE == this
				.getStatusPrescEnfPaciente()) {
			return 1;
		} else if (StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_VENCE_NO_DIA == this
				.getStatusPrescEnfPaciente()) {
			return 2;
		} else if (StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_PENDENTE_VENCE_NO_DIA == this
				.getStatusPrescEnfPaciente()) {
			return 3;
		} else if (StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_PENDENTE_VENCE_NO_DIA_SEGUINTE == this
				.getStatusPrescEnfPaciente()) {
			return 4;
		} else if (StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_DO_DIA_E_DIA_SEGUINTE_PENDENTES == this
				.getStatusPrescEnfPaciente()) {
			return 5;
		} else if (StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_VENCE_NO_DIA_SEGUINTE == this
				.getStatusPrescEnfPaciente()) {
			return 6;
		}

		return retornoPadrao;
	}
	
	/**
	 * Método utilizado para ordenar o ícone de sumário de alta
	 * 
	 * @return
	 */
	public Integer getOrdemIconeSumario() {

		final Integer retornoPadrao = 4;

		if (this.getIndAltaSumario() == null) {
			return retornoPadrao;
		} else if (DominioIndConcluido.S.equals(this.getIndAltaSumario())) {
			return 1;
		} else if (DominioIndConcluido.N.equals(this.getIndAltaSumario())) {
			return 2;
		} else if (DominioIndConcluido.R.equals(this.getIndAltaSumario())) {
			return 3;
		}
		
		return retornoPadrao;
	}
	
	
	/**
	 * Método utilizado para ordenar o ícone de projeto de pesquisa
	 * 
	 * @return
	 */
	public Integer getOrdemIconeProjPesquisa() {

		final Integer retornoPadrao = 3;

		if (this.getIndPartProjPesquisa() == null){
			return retornoPadrao;
		}else if (this.getIndPartProjPesquisa() == true){
			return 1;
		}else if (this.getIndPartProjPesquisa() == false){
			return 2;
		}
		
		return retornoPadrao;
	}
	

	/**
	 * Método utilizado para ordenar os ícones de solicitação de consultoria
	 * 
	 * @return
	 */
	public Integer getOrdemIconeSolicConsultoria(){
		
		final Integer retornoPadrao = 3;
		
		if(this.getIndSolicConsultoria() == null & this.getIndSolicConsultoriaResp() == null){
			return retornoPadrao;
		}else if(this.getIndSolicConsultoria() == true){
			return 1;
		}else if(this.getIndSolicConsultoriaResp() == true){
			return 2;
		}
		
		return retornoPadrao;
	}
	
	public Integer getOrdemIconeEvolucao(){
		
		final Integer retornoPadrao = 3;

		if (this.getIndEvolucao() == null){
			return retornoPadrao;
		}else if (this.getIndEvolucao() == true){
			return 1;
		}else if (this.getIndEvolucao() == false){
			return 2;
		}
		
		return retornoPadrao;
	}
	
	public String getColorLine(){
		if(this.getIndGmr() != null && this.getIndGmr() == true && this.getIndPrevAlta() != null &&  this.getIndPrevAlta() == true){
			return this.getColorPrevAlta();
		}else{
			if(this.getIndPrevAlta() != null && this.getIndPrevAlta() == true){
				return this.getColorPrevAlta();
			}else{
				if(this.getIndGmr() != null && this.getIndGmr() == true){
					return this.getColorGmr();
				}
			}
		}
		return "";
	}
	
	public String getColorNome(){
		if(this.getIndGmr() != null && this.getIndGmr() == true && this.getIndPrevAlta() != null &&  this.getIndPrevAlta() == true){
			return this.getColorGmr();
			
		}else{
			if(this.getIndPrevAlta() != null && this.getIndPrevAlta() == true){
				return this.getColorPrevAlta();
			}else{
				if(this.getIndGmr() != null && this.getIndGmr() == true){
					return this.getColorGmr();
				}
			}
		}
		return "";
	}
	
	/**
	 * Representa o status de sinalização de Úlcera por Pressão
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
	
	public Boolean getDesabilitaBotaoPrescrever() {
		return desabilitaBotaoPrescrever;
	}

	public void setDesabilitaBotaoPrescrever(Boolean desabilitaBotaoPrescrever) {
		this.desabilitaBotaoPrescrever = desabilitaBotaoPrescrever;
	}

	public Boolean getDesabilitaBotaoControles() {
		return desabilitaBotaoControles;
	}

	public void setDesabilitaBotaoControles(Boolean desabilitaBotaoControles) {
		this.desabilitaBotaoControles = desabilitaBotaoControles;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Boolean getIndPartProjPesquisa() {
		return indPartProjPesquisa;
	}

	public void setIndPartProjPesquisa(Boolean indPartProjPesquisa) {
		this.indPartProjPesquisa = indPartProjPesquisa;
	}

	public Boolean getIndSolicConsultoria() {
		return indSolicConsultoria;
	}

	public void setIndSolicConsultoria(Boolean indSolicConsultoria) {
		this.indSolicConsultoria = indSolicConsultoria;
	}

	public Boolean getIndSolicConsultoriaResp() {
		return indSolicConsultoriaResp;
	}

	public void setIndSolicConsultoriaResp(Boolean indSolicConsultoriaResp) {
		this.indSolicConsultoriaResp = indSolicConsultoriaResp;
	}

	public Boolean getIndEvolucao() {
		return indEvolucao;
	}

	public void setIndEvolucao(Boolean indEvolucao) {
		this.indEvolucao = indEvolucao;
	}

	public String getColorGmr() {
		return colorGmr;
	}

	public void setColorGmr(String colorGmr) {
		this.colorGmr = colorGmr;
	}

	public String getColorPrevAlta() {
		return colorPrevAlta;
	}

	public void setColorPrevAlta(String colorPrevAlta) {
		this.colorPrevAlta = colorPrevAlta;
	}

	public Boolean getIndGmr() {
		return indGmr;
	}

	public void setIndGmr(Boolean indGmr) {
		this.indGmr = indGmr;
	}

	public Boolean getIndPrevAlta() {
		return indPrevAlta;
	}

	public void setIndPrevAlta(Boolean indPrevAlta) {
		this.indPrevAlta = indPrevAlta;
	}

	public StatusSinalizadorUP getSinalizadorUlceraPressao() {
		return sinalizadorUlceraPressao;
	}

	public void setSinalizadorUlceraPressao(
			StatusSinalizadorUP sinalizadorUlceraPressao) {
		this.sinalizadorUlceraPressao = sinalizadorUlceraPressao;
	}

	public Boolean getDesabilitaBotaoEvolucao() {
		return desabilitaBotaoEvolucao;
	}

	public void setDesabilitaBotaoEvolucao(Boolean desabilitaBotaoEvolucao) {
		this.desabilitaBotaoEvolucao = desabilitaBotaoEvolucao;
	}

	public Boolean getIndConsultoriaEnf() {
		return indConsultoriaEnf;
	}

	public void setIndConsultoriaEnf(Boolean indConsultoriaEnf) {
		this.indConsultoriaEnf = indConsultoriaEnf;
	}
	
	
	public boolean equals(Object other) {
		if (!(other instanceof PacienteEnfermagemVO)){
			return false;
		}
		PacienteEnfermagemVO castOther = (PacienteEnfermagemVO) other;
		return new EqualsBuilder().append(this.atdSeq, castOther.getAtdSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.atdSeq).toHashCode();
	}
}
