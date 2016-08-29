package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;

public class AtendimentoSolicExameVO implements Serializable {

    private static final long serialVersionUID = 753496473323519912L;

    private Integer atdSeq;
    private Integer prontuario;
    private String nomePaciente;
    private Integer idade;
    private Date dataAtendimento;
    private String nomeEspecialidade;
    private String origem;
    private String localDescricao;
    private String leito;
    private Integer codPaciente;
    private String indTransplantado;
    private Integer numeroConsulta;
    private Short unfSeq;
    private String responsavel;
    private String isSus;

    public Integer getAtdSeq() {
	return atdSeq;
    }

    public void setAtdSeq(Integer atdSeq) {
	this.atdSeq = atdSeq;
    }

    public Integer getProntuario() {
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

    public String getNomeEspecialidade() {
	return nomeEspecialidade;
    }

    public void setNomeEspecialidade(String nomeEspecialidade) {
	this.nomeEspecialidade = nomeEspecialidade;
    }

    public String getOrigem() {
	return origem;
    }

    public void setOrigem(String origem) {
	this.origem = origem;
    }

    public String getLocalDescricao() {
	return localDescricao;
    }

    public void setLocalDescricao(String localDescricao) {
	this.localDescricao = localDescricao;
    }

    public String getLeito() {
	return leito;
    }

    public void setLeito(String leito) {
	this.leito = leito;
    }

    public Integer getCodPaciente() {
	return codPaciente;
    }

    public void setCodPaciente(Integer codPaciente) {
	this.codPaciente = codPaciente;
    }

    public String getIndTransplantado() {
	return indTransplantado;
    }

    public void setIndTransplantado(String indTransplantado) {
	this.indTransplantado = indTransplantado;
    }

    public Integer getNumeroConsulta() {
	return numeroConsulta;
    }

    public void setNumeroConsulta(Integer numeroConsulta) {
	this.numeroConsulta = numeroConsulta;
    }

    public Short getUnfSeq() {
	return unfSeq;
    }

    public void setUnfSeq(Short unfSeq) {
	this.unfSeq = unfSeq;
    }

    public String getResponsavel() {
	return responsavel;
    }

    public void setResponsavel(String responsavel) {
	this.responsavel = responsavel;
    }

    
    public String getIsSus() {
        return isSus;
    }

    
    public void setIsSus(String isSus) {
        this.isSus = isSus;
    }
}
