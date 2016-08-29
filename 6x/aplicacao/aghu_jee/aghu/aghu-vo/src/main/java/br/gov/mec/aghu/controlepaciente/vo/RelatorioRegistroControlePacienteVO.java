package br.gov.mec.aghu.controlepaciente.vo;

import java.io.Serializable;
import java.util.Date;

public class RelatorioRegistroControlePacienteVO implements Serializable {
	private static final long serialVersionUID = 8095682654010532934L;
    //cabeçalho	
	private Integer pacCodigo;
	private Date    dataHoraInicial;
	private Date    dataHoraFinal;
	private String  nome;
	private String  prontuario;
	private String  periodo;
	//linha
    private Date    dataHora;
    //coluna
    private String  siglaUnidade;
    //celula
    private String  valor;
    private String  anotacoes;
    private Integer ordem;
	private Boolean foraLimiteNormal;

	//Identificação do paciente no rodape
	private String nomeRodape;
	private String localRodape;
	private String prontuarioRodape;
 
	private String profissionais;
	
	// construtores
	public RelatorioRegistroControlePacienteVO() {
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Date getDataHoraInicial() {
		return dataHoraInicial;
	}

	public void setDataHoraInicial(Date dataHoraInicial) {
		this.dataHoraInicial = dataHoraInicial;
	}

	public Date getDataHoraFinal() {
		return dataHoraFinal;
	}

	public void setDataHoraFinal(Date dataHoraFinal) {
		this.dataHoraFinal = dataHoraFinal;
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

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public String getSiglaUnidade() {
		return siglaUnidade;
	}

	public void setSiglaUnidade(String siglaUnidade) {
		this.siglaUnidade = siglaUnidade;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getAnotacoes() {
		return anotacoes;
	}

	public void setAnotacoes(String anotacoes) {
		this.anotacoes = anotacoes;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Boolean getForaLimiteNormal() {
		return foraLimiteNormal;
	}

	public void setForaLimiteNormal(Boolean foraLimiteNormal) {
		this.foraLimiteNormal = foraLimiteNormal;
	}

	public String getNomeRodape() {
		return nomeRodape;
	}

	public void setNomeRodape(String nomeRodape) {
		this.nomeRodape = nomeRodape;
	}

	public String getLocalRodape() {
		return localRodape;
	}

	public void setLocalRodape(String localRodape) {
		this.localRodape = localRodape;
	}

	public String getProntuarioRodape() {
		return prontuarioRodape;
	}

	public void setProntuarioRodape(String prontuarioRodape) {
		this.prontuarioRodape = prontuarioRodape;
	}
	
	public String getProfissionais() {
		return profissionais;
	}

	public void setProfissionais(String profissionais) {
		this.profissionais = profissionais;
	}

}
