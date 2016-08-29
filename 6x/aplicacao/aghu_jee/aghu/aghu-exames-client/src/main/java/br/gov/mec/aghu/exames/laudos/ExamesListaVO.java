package br.gov.mec.aghu.exames.laudos;

import java.io.Serializable;
import java.util.List;

/**
 * Dados para impressão de resultado de exames.
 * 
 * @author cvagheti
 * 
 */
public class ExamesListaVO implements Serializable {

	private static final long serialVersionUID = 3251860700051653835L;

	private String nomePaciente;
	private Integer prontuario;
	private Integer prontuarioMae; 

	private String nomeInstituicao;
	private EnderecoContatosVO enderecoContatos;

	private List<ExameVO> exames;

	/**
	 * Resultados de exames.
	 * 
	 * @return
	 */
	public List<ExameVO> getExames() {
		return exames;
	}

	public void setExames(List<ExameVO> exames) {
		this.exames = exames;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomeInstituicao() {
		return nomeInstituicao;
	}

	public void setNomeInstituicao(String nomeInstituicao) {
		this.nomeInstituicao = nomeInstituicao;
	}

	public EnderecoContatosVO getEnderecoContatos() {
		return enderecoContatos;
	}

	public void setEnderecoContatos(EnderecoContatosVO enderecoContatos) {
		this.enderecoContatos = enderecoContatos;
	}

	
	public Integer getProntuarioMae() {
		return prontuarioMae;
	}

	
	public void setProntuarioMae(Integer prontuarioMae) {
		this.prontuarioMae = prontuarioMae;
	}

}
