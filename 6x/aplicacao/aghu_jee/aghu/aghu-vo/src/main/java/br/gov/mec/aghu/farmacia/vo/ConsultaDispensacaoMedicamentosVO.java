package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.core.utils.StringUtil;


public class ConsultaDispensacaoMedicamentosVO implements Serializable{

	private static final long serialVersionUID = 5357165051211418412L;

	private String leito;
	private Integer prontuario;
	private String nome;
	private String origem;
	private String numeroPrescricao;
	private Date dataInicio;
	private Date dataFim;
	private Integer atdSeq;
	
	public String getNomeTrunc(Long size) {
		return StringUtil.trunc(getNome(), true, size);
	}
	
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public String getNumeroPrescricao() {
		return numeroPrescricao;
	}
	public void setNumeroPrescricao(String numeroPrescricao) {
		this.numeroPrescricao = numeroPrescricao;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}
}