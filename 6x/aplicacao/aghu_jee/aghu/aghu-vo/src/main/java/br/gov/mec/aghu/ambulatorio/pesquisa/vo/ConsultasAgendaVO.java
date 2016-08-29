package br.gov.mec.aghu.ambulatorio.pesquisa.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * @author rkempfer
 *
 */
public class ConsultasAgendaVO implements BaseBean{
	
	private static final long serialVersionUID = 70345027933587983L;
	
	private String especialidadeSigla;
	private String especialidadeNome;
	private Integer gradeAgendamentoSeq;
	private Date dataConsulta;
	private String condicaoAtendimentoDescricao;
	private String situcaoConsultaDescricao;
	private Integer pacienteProntuario;
	private String pacienteNome;
	private String pacienteEnderecoPadraoCidade;
	private String pacienteEnderecoPadraoUfSigla;
	private Integer retornoSeq;
	private String retornoDescricao;
	private String nomeCidade;
	private String cidadeUfSigla;
	
	public String getEspecialidadeSigla() {
		return especialidadeSigla;
	}
	public void setEspecialidadeSigla(String especialidadeSigla) {
		this.especialidadeSigla = especialidadeSigla;
	}
	public String getEspecialidadeNome() {
		return especialidadeNome;
	}
	public void setEspecialidadeNome(String especialidadeNome) {
		this.especialidadeNome = especialidadeNome;
	}
	public Integer getGradeAgendamentoSeq() {
		return gradeAgendamentoSeq;
	}
	public void setGradeAgendamentoSeq(Integer gradeAgendamentoSeq) {
		this.gradeAgendamentoSeq = gradeAgendamentoSeq;
	}
	public Date getDataConsulta() {
		return dataConsulta;
	}
	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}
	public String getCondicaoAtendimentoDescricao() {
		return condicaoAtendimentoDescricao;
	}
	public void setCondicaoAtendimentoDescricao(String condicaoAtendimentoDescricao) {
		this.condicaoAtendimentoDescricao = condicaoAtendimentoDescricao;
	}
	public String getSitucaoConsultaDescricao() {
		return situcaoConsultaDescricao;
	}
	public void setSitucaoConsultaDescricao(String situcaoConsultaDescricao) {
		this.situcaoConsultaDescricao = situcaoConsultaDescricao;
	}
	public Integer getPacienteProntuario() {
		return pacienteProntuario;
	}
	public void setPacienteProntuario(Integer pacienteProntuario) {
		this.pacienteProntuario = pacienteProntuario;
	}
	public String getPacienteNome() {
		return pacienteNome;
	}
	public void setPacienteNome(String pacienteNome) {
		this.pacienteNome = pacienteNome;
	}
	public String getPacienteEnderecoPadraoCidade() {		
		return pacienteEnderecoPadraoCidade == null ? nomeCidade : pacienteEnderecoPadraoCidade;
	}
	public void setPacienteEnderecoPadraoCidade(String pacienteEnderecoPadraoCidade) {
		this.pacienteEnderecoPadraoCidade = pacienteEnderecoPadraoCidade;
	}
	public String getPacienteEnderecoPadraoUfSigla() {
		return pacienteEnderecoPadraoUfSigla == null ? cidadeUfSigla : pacienteEnderecoPadraoCidade;
	}
	public void setPacienteEnderecoPadraoUfSigla(String pacienteEnderecoPadraoUfSigla) {
		this.pacienteEnderecoPadraoUfSigla = pacienteEnderecoPadraoUfSigla;
	}
	public Integer getRetornoSeq() {
		return retornoSeq;
	}
	public void setRetornoSeq(Integer retornoSeq) {
		this.retornoSeq = retornoSeq;
	}
	public String getRetornoDescricao() {
		return retornoDescricao;
	}
	public void setRetornoDescricao(String retornoDescricao) {
		this.retornoDescricao = retornoDescricao;
	}
	public String getNomeCidade() {
		return nomeCidade;
	}
	public void setNomeCidade(String nomeCidade) {
		this.nomeCidade = nomeCidade;
	}
	public String getCidadeUfSigla() {
		return cidadeUfSigla;
	}
	public void setCidadeUfSigla(String cidadeUfSigla) {
		this.cidadeUfSigla = cidadeUfSigla;
	}
	
	public String getCidadeUf(){
		StringBuilder cidadeUf = null;
		if(pacienteEnderecoPadraoCidade != null){
			cidadeUf = new StringBuilder(pacienteEnderecoPadraoCidade).append("/").append(pacienteEnderecoPadraoUfSigla);			
		}else if(nomeCidade != null){
			cidadeUf = new StringBuilder(nomeCidade).append("/").append(cidadeUfSigla);
		}
		return cidadeUf == null ? null : cidadeUf.toString();
	}
}
