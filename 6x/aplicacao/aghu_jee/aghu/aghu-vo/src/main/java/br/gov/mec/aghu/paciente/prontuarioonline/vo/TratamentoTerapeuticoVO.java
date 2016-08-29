package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;


public class TratamentoTerapeuticoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6549452713497412989L;
	private Short espSeq;
	private Short serVinCodigo;
	private Integer serMatricula;
	private Short unfSeq;
	private Integer cctCodigo;
	
	private Date dthrInicio;
	private Date dthrFim;
	private String nome;
	private String especialidade;
	private String tipoSessao;
	
	private Integer matricula;
	private Short vinculo;
	
	public Date getDthrInicio() {
		return dthrInicio;
	}
	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
	public Date getDthrFim() {
		return dthrFim;
	}
	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getTipoSessao() {
		return tipoSessao;
	}
	public void setTipoSessao(String tipoSessao) {
		this.tipoSessao = tipoSessao;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinculo() {
		return vinculo;
	}
	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	
}
