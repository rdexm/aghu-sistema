package br.gov.mec.aghu.ambulatorio.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntity;

/**
 * Classe para carregar dados da pesquisa de consultas pendentes #12316 C1
 * 
 */
public class PesquisarConsultasPendentesVO implements BaseEntity {

	private static final long serialVersionUID = 2852890830920363046L;

	private Integer prioridade;
	private Integer numero;
	private Date dtConsulta;
	private Date dtHrInicioConsulta;
	private Short caaSeq;
	private String nome;
	private Integer prontuario;
	private Short serVinCodigo;
	private Integer serMatricula;
	private String micNome;
	private Integer grdSeq;
	private Date hrInicioAtendimento;
	private Byte salaMicro;
	private String responsavel;
	private String ordem;
	private String profissional;
	private String especialidade;
	private String equipe;
	private boolean reaberto;

	public Integer getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Integer prioridade) {
		this.prioridade = prioridade;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Date getDtConsulta() {
		return dtConsulta;
	}

	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}

	public Date getDtHrInicioConsulta() {
		return dtHrInicioConsulta;
	}

	public void setDtHrInicioConsulta(Date dtHrInicioConsulta) {
		this.dtHrInicioConsulta = dtHrInicioConsulta;
	}

	public Short getCaaSeq() {
		return caaSeq;
	}

	public void setCaaSeq(Short caaSeq) {
		this.caaSeq = caaSeq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public String getMicNome() {
		return micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}

	public Integer getGrdSeq() {
		return grdSeq;
	}

	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}

	public Date getHrInicioAtendimento() {
		return hrInicioAtendimento;
	}

	public void setHrInicioAtendimento(Date hrInicioAtendimento) {
		this.hrInicioAtendimento = hrInicioAtendimento;
	}

	public Byte getSalaMicro() {
		return salaMicro;
	}

	public void setSalaMicro(Byte salaMicro) {
		this.salaMicro = salaMicro;
	}

	public String getOrdem() {
		return ordem;
	}

	public void setOrdem(String ordem) {
		this.ordem = ordem;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getProfissional() {
		return profissional;
	}

	public void setProfissional(String profissional) {
		this.profissional = profissional;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {

		if (!(other instanceof PesquisarConsultasPendentesVO)) {
	        return false;
		}
		
		PesquisarConsultasPendentesVO castOther = (PesquisarConsultasPendentesVO) other;
		
	    return new EqualsBuilder().append(this.prioridade, castOther.getPrioridade())
	    		.append(this.numero, castOther.getNumero())
	    		.append(this.dtConsulta, castOther.getDtConsulta())
	    		.append(this.dtHrInicioConsulta, castOther.getDtHrInicioConsulta())
	    		.append(this.caaSeq, castOther.getCaaSeq())
	    		.append(this.nome, castOther.getNome())
	    		.append(this.prontuario, castOther.getProntuario())
	    		.append(this.serVinCodigo, castOther.getSerVinCodigo())
	    		.append(this.serMatricula, castOther.getSerMatricula())
	    		.append(this.micNome, castOther.getMicNome())
	    		.append(this.grdSeq, castOther.getGrdSeq())
	    		.append(this.hrInicioAtendimento, castOther.getHrInicioAtendimento())
	    		.append(this.salaMicro, castOther.getSalaMicro())
	    		.append(this.responsavel, castOther.getResponsavel())
	    		.append(this.ordem, castOther.getOrdem())
	    		.append(this.profissional, castOther.getProfissional())
	    		.append(this.especialidade, castOther.getEspecialidade())
	    		.append(this.equipe, castOther.getEquipe())
	                .isEquals();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return new HashCodeBuilder().append(this.prioridade)
	    		.append(this.numero)
	    		.append(this.dtConsulta)
	    		.append(this.dtHrInicioConsulta)
	    		.append(this.caaSeq)
	    		.append(this.nome)
	    		.append(this.prontuario)
	    		.append(this.serVinCodigo)
	    		.append(this.serMatricula)
	    		.append(this.micNome)
	    		.append(this.grdSeq)
	    		.append(this.hrInicioAtendimento)
	    		.append(this.salaMicro)
	    		.append(this.responsavel)
	    		.append(this.ordem)
	    		.append(this.profissional)
	    		.append(this.especialidade)
	    		.append(this.equipe)
				.toHashCode();
	}

	public boolean isReaberto() {
		return reaberto;
	}

	public void setReaberto(boolean reaberto) {
		this.reaberto = reaberto;
	}
	
}