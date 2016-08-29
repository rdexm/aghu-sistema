package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RapServidoresEspecialidadesVO implements BaseBean{
	
	private static final long serialVersionUID = -1365908600760112438L;
	/**
	 * Número da matrícula do servidor do HCPA.
	 */
	private Integer matricula;
	/**
	 * Código do vínculo que o servidor tem com o HCPA
	 */
	private Short vinCodigo;
	/**
	 * 
	 */
	private String nome;
	
	private Long countProfEspecialidades;

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCountProfEspecialidades() {
		return countProfEspecialidades;
	}

	public void setCountProfEspecialidades(Long countProfEspecialidades) {
		this.countProfEspecialidades = countProfEspecialidades;
	}
	
	
}
