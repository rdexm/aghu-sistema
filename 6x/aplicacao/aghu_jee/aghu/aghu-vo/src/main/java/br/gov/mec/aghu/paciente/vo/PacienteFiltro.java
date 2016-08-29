package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Filtro para busca de paciente através do web service.
 * 
 * @author ihaas
 * 
 */
public class PacienteFiltro implements Serializable {

	private static final long serialVersionUID = -4430746314063751488L;

	private Integer prontuario;
	private Integer codigo;
	private String nome;
	private String nomeMae;
	private Date dtNascimento;
	private Integer firstResult;
	private Integer maxResults;
	private Boolean respeitarOrdem;

	public PacienteFiltro() {
		super();
	}

	public PacienteFiltro(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Integer getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public Boolean getRespeitarOrdem() {
		return respeitarOrdem;
	}

	public void setRespeitarOrdem(Boolean respeitarOrdem) {
		this.respeitarOrdem = respeitarOrdem;
	}

}