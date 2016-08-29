package br.gov.mec.aghu.perinatologia.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class EscalaLeitoRecemNascidoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4184795271779494323L;

	private String leito;
	private Short vinculo;
	private Integer matricula;
	private String nome;
	private String nomeUsual;

	public EscalaLeitoRecemNascidoVO(String leito, Short vinculo,
			Integer matricula, String nome, String nomeUsual) {
		super();
		this.leito = leito;
		this.vinculo = vinculo;
		this.matricula = matricula;
		this.nome = nome;
		this.nomeUsual = nomeUsual;
	}

	public EscalaLeitoRecemNascidoVO() {
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeUsual() {
		return nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}
	
	public enum Fields {
		LEITO("leito"),
		VINCULO("vinculo"),
		MATRICULA("matricula"),
		NOME("nome"),
		NOMEUSUAL("nomeUsual");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

}
