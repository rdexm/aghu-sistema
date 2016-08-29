package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;


public class VAelSolicAtendsVO {
	
	private Integer numero;  //soeSeq
	private String prioridade;
	private String programada;
	private Date dataSolicitacao;
	private Integer prontuario;
	private String quarto;
	private String leito;
	private String pacienteDiversos;
	private String convenio;
	private String ur;
	private String local;
	private Integer numConsulta;
	private DominioOrigemAtendimento origem;
	private String informacoesClinicas;
	private Integer matriculaSolic;
	private Short vinCodigoSolic;
	private String solicitante;
	private String responsavel;
	private String imprimiu;
	private String unfDescricao;
	private Integer codPaciente;
	private Integer atdSeq;
	private Short unfSeq;	
	private Short cspSeq;
	private Short cspCnvCodigo;
	private String tipo;
	private Integer pacCodigo;
	private String descricaoOrigem;
	private boolean origemSolicitacaoExames;
	private boolean pacienteGMR;
	
	
	public VAelSolicAtendsVO(){
	}
	
	public VAelSolicAtendsVO(Integer numero, Short cspCnvCodigo, Short cspSeq, String descricaoOrigem, Integer pacCodigo,
			Integer prontuario, String tipo, String unfDescricao, String quarto, String leito, String informacoesClinicas, boolean origemSolicitacaoExames){
		this.numero = numero;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.descricaoOrigem = descricaoOrigem;
		this.pacCodigo = pacCodigo;
		this.prontuario = prontuario;
		this.tipo = tipo;
		this.unfDescricao = unfDescricao;
		this.quarto = quarto;
		this.leito = leito;
		this.informacoesClinicas = informacoesClinicas;
		this.origemSolicitacaoExames = origemSolicitacaoExames;
	}
	
	public VAelSolicAtendsVO(Integer numero, Short cspCnvCodigo, Short cspSeq, String descricaoOrigem, Integer pacCodigo,
			Integer prontuario, String tipo, String unfDescricao, String quarto, String leito, String informacoesClinicas){
		this.numero = numero;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.descricaoOrigem = descricaoOrigem;
		this.pacCodigo = pacCodigo;
		this.codPaciente = pacCodigo;
		this.prontuario = prontuario;
		this.tipo = tipo;
		this.unfDescricao = unfDescricao;
		this.quarto = quarto;
		this.leito = leito;
		this.informacoesClinicas = informacoesClinicas;
	}
	
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}
	public String getSolicitante() {
		return solicitante;
	}
	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}
	public Integer getMatriculaSolic() {
		return matriculaSolic;
	}
	public void setMatriculaSolic(Integer matriculaSolic) {
		this.matriculaSolic = matriculaSolic;
	}
	public Short getVinCodigoSolic() {
		return vinCodigoSolic;
	}
	public void setVinCodigoSolic(Short vinCodigoSolic) {
		this.vinCodigoSolic = vinCodigoSolic;
	}
	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}
	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}
	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}
	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}
	public Integer getNumConsulta() {
		return numConsulta;
	}
	public void setNumConsulta(Integer numConsulta) {
		this.numConsulta = numConsulta;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public String getPrioridade() {
		return prioridade;
	}
	public void setPrioridade(String prioridade) {
		this.prioridade = prioridade;
	}
	public String getProgramada() {
		return programada;
	}
	public void setProgramada(String programada) {
		this.programada = programada;
	}
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}
	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getQuarto() {
		return quarto;
	}
	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public String getPacienteDiversos() {
		return pacienteDiversos;
	}
	public void setPacienteDiversos(String pacienteDiversos) {
		this.pacienteDiversos = pacienteDiversos;
	}
	public String getConvenio() {
		return convenio;
	}
	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}
	public String getUr() {
		return ur;
	}
	public void setUr(String ur) {
		this.ur = ur;
	}
	public String getImprimiu() {
		return imprimiu;
	}
	public void setImprimiu(String imprimiu) {
		this.imprimiu = imprimiu;
	}
	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}
	public String getUnfDescricao() {
		return unfDescricao;
	}
	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}
	public Integer getCodPaciente() {
		return codPaciente;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}
	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}
	public void setCspSeq(Short cspSeq) {
		this.cspSeq = cspSeq;
	}
	public Short getCspSeq() {
		return cspSeq;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getDescricaoOrigem() {
		return descricaoOrigem;
	}

	public void setDescricaoOrigem(String descricaoOrigem) {
		this.descricaoOrigem = descricaoOrigem;
	}

	public boolean isOrigemSolicitacaoExames() {
		return origemSolicitacaoExames;
	}

	public void setOrigemSolicitacaoExames(boolean origemSolicitacaoExames) {
		this.origemSolicitacaoExames = origemSolicitacaoExames;
	}

	public boolean isPacienteGMR() {
		return pacienteGMR;
	}

	public void setPacienteGMR(boolean pacienteGMR) {
		this.pacienteGMR = pacienteGMR;
	}
	
	
}