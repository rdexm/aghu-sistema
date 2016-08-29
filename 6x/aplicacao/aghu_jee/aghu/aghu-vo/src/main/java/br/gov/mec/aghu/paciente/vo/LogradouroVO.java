package br.gov.mec.aghu.paciente.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class LogradouroVO implements BaseBean {
	
	private static final long serialVersionUID = -2999679242480827635L;
	private Integer codigoLogradouro;
	private String descricaoTipoLogradouro;
	private String descricaoTituloLogradouro;
	private String nomeLogradouro;
	private Integer cepCidade;
	private Integer aipCLILgrCodigo;
	private Integer aipCLICep;
	private String nroInicial;
	private String nroFinal;
	private String lado;
	private Integer codigoBairro;
	private String descricaoBairro;

	/**
	 * Construtor padrão.
	 */
	public LogradouroVO() {}
	
	public LogradouroVO(Integer codigoLogradouro,
			String descricaoTipoLogradouro, String descricaoTituloLogradouro,
			String nomeLogradouro, Integer cepCidade, Integer aipCLILgrCodigo,
			Integer aipCLICep, String nroInicial, String nroFinal,
			String lado, Integer codigoBairro, String descricaoBairro) {
		super();
		this.codigoLogradouro = codigoLogradouro;
		this.descricaoTipoLogradouro = descricaoTipoLogradouro;
		this.descricaoTituloLogradouro = descricaoTituloLogradouro;
		this.nomeLogradouro = nomeLogradouro;
		this.cepCidade = cepCidade;
		this.aipCLILgrCodigo = aipCLILgrCodigo;
		this.aipCLICep = aipCLICep;
		this.nroInicial = nroInicial;
		this.nroFinal = nroFinal;
		this.lado = lado;
		this.codigoBairro = codigoBairro;
		this.descricaoBairro = descricaoBairro;
	}

	public Integer getCodigoLogradouro() {
		return codigoLogradouro;
	}

	public void setCodigoLogradouro(Integer codigoLogradouro) {
		this.codigoLogradouro = codigoLogradouro;
	}

	public String getDescricaoTipoLogradouro() {
		return descricaoTipoLogradouro;
	}

	public void setDescricaoTipoLogradouro(String descricaoTipoLogradouro) {
		this.descricaoTipoLogradouro = descricaoTipoLogradouro;
	}

	public String getDescricaoTituloLogradouro() {
		return descricaoTituloLogradouro;
	}

	public void setDescricaoTituloLogradouro(String descricaoTituloLogradouro) {
		this.descricaoTituloLogradouro = descricaoTituloLogradouro;
	}

	public String getNomeLogradouro() {
		return nomeLogradouro;
	}

	public void setNomeLogradouro(String nomeLogradouro) {
		this.nomeLogradouro = nomeLogradouro;
	}

	public Integer getCepCidade() {
		return cepCidade;
	}

	public void setCepCidade(Integer cepCidade) {
		this.cepCidade = cepCidade;
	}

	public Integer getAipCLILgrCodigo() {
		return aipCLILgrCodigo;
	}

	public void setAipCLILgrCodigo(Integer aipCLILgrCodigo) {
		this.aipCLILgrCodigo = aipCLILgrCodigo;
	}

	public Integer getAipCLICep() {
		return aipCLICep;
	}

	public void setAipCLICep(Integer aipCLICep) {
		this.aipCLICep = aipCLICep;
	}

	public String getNroInicial() {
		return nroInicial;
	}

	public void setNroInicial(String nroInicial) {
		this.nroInicial = nroInicial;
	}

	public String getNroFinal() {
		return nroFinal;
	}

	public void setNroFinal(String nroFinal) {
		this.nroFinal = nroFinal;
	}

	public String getLado() {
		return lado;
	}

	public void setLado(String lado) {
		this.lado = lado;
	}

	public Integer getCodigoBairro() {
		return codigoBairro;
	}

	public void setCodigoBairro(Integer codigoBairro) {
		this.codigoBairro = codigoBairro;
	}

	public String getDescricaoBairro() {
		return descricaoBairro;
	}

	public void setDescricaoBairro(String descricaoBairro) {
		this.descricaoBairro = descricaoBairro;
	}

}
