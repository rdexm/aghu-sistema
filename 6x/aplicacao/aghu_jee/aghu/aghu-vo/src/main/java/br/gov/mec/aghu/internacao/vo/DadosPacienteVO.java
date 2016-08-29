package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;

public class DadosPacienteVO {
	
	private Integer idadeAnos;
	private Integer idadeMeses;
	private Byte cspSeq;
	private Short cnvCodigo;
	private Integer codigo;
	private Integer prontuario;
	private String nome;
	private String nomeMae;
	private Date dtNascimento;
	private String data;
	private DominioSexo sexo;
	private String convenioPlano;
	private DominioSimNao indPermissaoInternacao;
	
	/**
	 * @return the idadeAnos
	 */
	public Integer getIdadeAnos() {
		return idadeAnos;
	}
	/**
	 * @param idadeAnos the idadeAnos to set
	 */
	public void setIdadeAnos(Integer idadeAnos) {
		this.idadeAnos = idadeAnos;
	}
	/**
	 * @return the idadeMeses
	 */
	public Integer getIdadeMeses() {
		return idadeMeses;
	}
	/**
	 * @param idadeMeses the idadeMeses to set
	 */
	public void setIdadeMeses(Integer idadeMeses) {
		this.idadeMeses = idadeMeses;
	}
	/**
	 * @return the seq
	 */
	public Byte getCspSeq() {
		return cspSeq;
	}
	/**
	 * @param seq the seq to set
	 */
	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}
	/**
	 * @return the cnvCodigo
	 */
	public Short getCnvCodigo() {
		return cnvCodigo;
	}
	/**
	 * @param cnvCodigo the cnvCodigo to set
	 */
	public void setCnvCodigo(Short cnvCodigo) {
		this.cnvCodigo = cnvCodigo;
	}
	/**
	 * @return the codigo
	 */
	public Integer getCodigo() {
		return codigo;
	}
	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	/**
	 * @return the prontuario
	 */
	public Integer getProntuario() {
		return prontuario;
	}
	/**
	 * @param prontuario the prontuario to set
	 */
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}
	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}
	/**
	 * @return the nomeMae
	 */
	public String getNomeMae() {
		return nomeMae;
	}
	/**
	 * @param nomeMae the nomeMae to set
	 */
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	/**
	 * @return the dtNascimento
	 */
	public Date getDtNascimento() {
		return dtNascimento;
	}
	/**
	 * @param dtNascimento the dtNascimento to set
	 */
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	/**
	 * @return the sexo
	 */
	public DominioSexo getSexo() {
		return sexo;
	}
	/**
	 * @param sexo the sexo to set
	 */
	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}
	/**
	 * @return the convenioPlano
	 */
	public String getConvenioPlano() {
		return convenioPlano;
	}
	/**
	 * @param convenioPlano the convenioPlano to set
	 */
	public void setConvenioPlano(String convenioPlano) {
		this.convenioPlano = convenioPlano;
	}
	/**
	 * @return the indPermissaoInternacao
	 */
	public DominioSimNao getIndPermissaoInternacao() {
		return indPermissaoInternacao;
	}
	/**
	 * @param indPermissaoInternacao the indPermissaoInternacao to set
	 */
	public void setIndPermissaoInternacao(DominioSimNao indPermissaoInternacao) {
		this.indPermissaoInternacao = indPermissaoInternacao;
	}

	
}
