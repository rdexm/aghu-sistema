package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioTipoReceituario;

/**
 * Representa uma receita médica normal ou especial.<br>
 * Haverá instâncias para vias e para cada mês no caso de validade por mais de
 * um mês.
 * 
 * @author cvagheti
 * 
 */

public class ReceitaMedicaVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1588512796965668742L;
	private Long receituarioSeq;
	private String nome;
	private String endereco;

	private String ufConselho;
	private String cidadeHU;
	private String ufHU;
	
	private String nomeMedico;
	private String especialidade;

	private List<ItemReceitaMedicaVO> itens;

	private String tipo;
	private Byte via;
	private Integer mesDeUso;
	private String mesDeUsoFormatado;
	private Integer inicio;
	private String siglaConselho;
	private String registroConselho;
	private Byte validadeMeses;
	private Date partirDe;
	
	private String infValidadeMeses;

	public ReceitaMedicaVO() {

	}

	public ReceitaMedicaVO(String nome) {
		this.nome = nome;
	}

	/**
	 * Construtor
	 * 
	 * @param nome
	 *            do paciente
	 * @param endereco
	 *            do paciente
	 */
	public ReceitaMedicaVO(String nome, String endereco) {
		this.nome = nome;
		this.endereco = endereco;
	}

	public Long getReceituarioSeq() {
		return receituarioSeq;
	}

	public void setReceituarioSeq(Long receituarioSeq) {
		this.receituarioSeq = receituarioSeq;
	}

	/**
	 * Retorna o nome do paciente.
	 * 
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * Retorna o endereço do paciente.
	 * 
	 * @return
	 */
	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	/**
	 * Retorna os itens da receita.
	 * 
	 * @return
	 */
	public List<ItemReceitaMedicaVO> getItens() {
		return itens;
	}

	public void setItens(List<ItemReceitaMedicaVO> itens) {
		this.itens = itens;
	}

	/**
	 * Retorna a sequencia do mês de uso da receita.<br>
	 * Se uma receita tem validade para 6 meses, por exemplo.
	 * 
	 * @return
	 */
	public Integer getMesDeUso() {
		return mesDeUso;
	}

	public void setMesDeUso(Integer mesDeUso) {
		this.mesDeUso = mesDeUso;
	}

	public Integer getInicio() {
		return inicio;
	}

	public void setInicio(Integer inicio) {
		this.inicio = inicio;
	}

	/**
	 * Retorna a sigla do conselho profissional.
	 * 
	 * @return
	 */
	public String getSiglaConselho() {
		return siglaConselho;
	}

	public void setSiglaConselho(String siglaConselho) {
		this.siglaConselho = siglaConselho;
	}

	/**
	 * Retorna o registro no conselho do profissional que assina a receita.
	 * 
	 * @return
	 */
	public String getRegistroConselho() {
		return registroConselho;
	}

	public void setRegistroConselho(String registroConselho) {
		this.registroConselho = registroConselho;
	}

	/**
	 * Retorna o número da via.
	 */
	public Byte getVia() {
		return via;
	}

	public void setVia(Byte via) {
		this.via = via;
	}

	public Byte getValidadeMeses() {
		return validadeMeses;
	}

	public void setValidadeMeses(Byte validadeMeses) {
		this.validadeMeses = validadeMeses;
	}

	/**
	 * Retorna o tipo.
	 * 
	 * @return
	 */
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * Retorna true se é especial.
	 * 
	 * @return
	 */
	public boolean isEspecial() {
		return DominioTipoReceituario.E.getDescricao().equals(this.tipo);
	}

	/**
	 * Retorna o nome do médico, a sigla do conselho profissional e o registro.
	 * 
	 * @return
	 */
	public String getDescricaoMedico() {
		if (this.nomeMedico == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder(this.getNomeMedico());
		if (this.getSiglaConselho() != null){
		builder.append(" - ") //
				.append(StringUtils.stripToEmpty(this.getSiglaConselho())) //
				.append(' ') //
				.append(StringUtils.stripToEmpty(this.getRegistroConselho()));
		}

		return builder.toString();
	}

	/**
	 * Retorna a data a partir da qual os medicamentos podem ser retirados no
	 * posto.
	 * 
	 * @return
	 */
	public Date getPartirDe() {
		return partirDe;
	}

	public void setPartirDe(Date aPartirDe) {
		this.partirDe = aPartirDe;
	}
	
	public String getUfConselho() {
		return ufConselho;
	}

	public void setUfConselho(String ufConselho) {
		this.ufConselho = ufConselho;
	}

	public String getCidadeHU() {
		return cidadeHU;
	}

	public void setCidadeHU(String cidadeHU) {
		this.cidadeHU = cidadeHU;
	}

	public String getUfHU() {
		return ufHU;
	}

	public void setUfHU(String ufHU) {
		this.ufHU = ufHU;
	}
	
	
	public String getInfValidadeMeses() {
		infValidadeMeses = "Esta receita foi elaborada no dia " +
		org.apache.commons.lang3.time.DateFormatUtils.format(new java.util.Date(),"dd/MM/yyyy");
		
		if (getValidadeMeses() != null && getValidadeMeses() > 1){
			infValidadeMeses += " com validade de " + getValidadeMeses() + " meses.";			
		}
		
		return infValidadeMeses;
	}

	public String getMesDeUsoFormatado() {
		mesDeUsoFormatado = "Para retirada a partir de " + org.apache.commons.lang3.time.DateFormatUtils.format(getPartirDe(),"dd/MM/yyyy");
		return mesDeUsoFormatado;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("nome", this.nome).toString();
	}

}
