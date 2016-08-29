package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.core.utils.DateUtil;
/**
 * Classe para carregar dados do relatório de Agenda de Consultas
 * #27521 #8236 #8233 C2
 * 
 * @author camila.barreto
 *
 */
public class ConsultasRelatorioAgendaConsultasVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3464230070027445316L;

	private Integer numeroConsulta;
	
	private Integer prontuario;
	
	private String nomePaciente;
	
	private DominioSimNao pacConfirmado;
	
	private String condicaoAtendimentoDescricao;
		
	private Date dataConsulta;
		
	private Integer codigoPaciente;
	
	private Short seqRetorno;
	
	private Date pacienteDataObito;

	private Long prontuarioFamilia;
	
	private DominioTipoDataObito pacienteTipoDataObito;
	
	private Short seqUnidadeFuncional;
	
	private Short seqEspecialidade;
	
	private Integer seqGrade;
	
	private boolean possuiUbs;
	
	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public String getProntuarioString() {
		if (prontuario == null) {
			return "";
		}
		String prontuarioString = String.valueOf(prontuario);
		return prontuarioString.substring(0, prontuarioString.length()-1) + 
				"/" + prontuarioString.charAt(prontuarioString.length()-1); 
	}
		
	public Integer getProntuario(){
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public DominioSimNao getPacConfirmado() {
		return pacConfirmado;
	}

	public void setPacConfirmado(DominioSimNao pacConfirmado) {
		this.pacConfirmado = pacConfirmado;
	}

	public String getPacConfirmadoString() {
		if (pacConfirmado != null && !pacConfirmado.isSim()){
			return "REC";
		} else{
			return "";
		}
	}

	public String getCondicaoAtendimentoDescricao() {
		return condicaoAtendimentoDescricao;
	}

	public void setCondicaoAtendimentoDescricao(String condicaoAtendimentoDescricao) {
		this.condicaoAtendimentoDescricao = condicaoAtendimentoDescricao;
	}

	public String getHoraConsulta() {
		return DateUtil.obterDataFormatada(dataConsulta, "HH:mm");
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Short getSeqRetorno() {
		return seqRetorno;
	}

	public void setSeqRetorno(Short seqRetorno) {
		this.seqRetorno = seqRetorno;
	}

	public Date getPacienteDataObito() {
		return pacienteDataObito;
	}

	public void setPacienteDataObito(Date pacienteDataObito) {
		this.pacienteDataObito = pacienteDataObito;
	}

	public DominioTipoDataObito getPacienteTipoDataObito() {
		return pacienteTipoDataObito;
	}

	public void setPacienteTipoDataObito(DominioTipoDataObito pacienteTipoDataObito) {
		this.pacienteTipoDataObito = pacienteTipoDataObito;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public Integer getSeqGrade() {
		return seqGrade;
	}

	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}

	public Long getProntuarioFamilia() {
		return prontuarioFamilia;
	}

	public void setProntuarioFamilia(Long prontuarioFamilia) {
		this.prontuarioFamilia = prontuarioFamilia;
	}

	public boolean isPossuiUbs() {
		return possuiUbs;
	}

	public void setPossuiUbs(boolean possuiUbs) {
		this.possuiUbs = possuiUbs;
	}
	
}
