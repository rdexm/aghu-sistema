package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.model.AinLeitos;


public class DetalharItemSolicitacaoPacienteVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1791148825016559599L;
	private String prontuarioPaciente;
	private String codigoPaciente;
	private String nomePaciente;
	//private AipPacientes aipPacientes;
	private String origem;
	private String local;
	private String consulta;
	private AinLeitos leito;
	private String usuarioLogado;
	private DominioSexo sexoBiologico;
	private Date dtNascimento;
	private String idadeAnoMesFormat;
	
	public String getProntuarioPaciente() {
		return prontuarioPaciente;
	}
	public void setProntuarioPaciente(String prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}
	public String getCodigoPaciente() {
		return codigoPaciente;
	}
	public void setCodigoPaciente(String codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getConsulta() {
		return consulta;
	}
	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}
	public AinLeitos getLeito() {
		return leito;
	}
	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}
	public String getUsuarioLogado() {
		return usuarioLogado;
	}
	public void setUsuarioLogado(String usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}
	public void setSexoBiologico(DominioSexo sexoBiologico) {
		this.sexoBiologico = sexoBiologico;
	}
	public DominioSexo getSexoBiologico() {
		return sexoBiologico;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setIdadeAnoMesFormat(String idadeAnoMesFormat) {
		this.idadeAnoMesFormat = idadeAnoMesFormat;
	}
	public String getIdadeAnoMesFormat() {
		return idadeAnoMesFormat;
	}
	
}
