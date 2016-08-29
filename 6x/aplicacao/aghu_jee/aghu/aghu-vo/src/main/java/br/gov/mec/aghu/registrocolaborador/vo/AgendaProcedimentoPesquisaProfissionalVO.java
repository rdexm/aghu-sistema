package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;

/**
 * VO da SuggestionBox para pesquisa de profissionais na estória #22460 – Agendar procedimentos eletivo, urgência ou emergência
 * 
 * @author aghu
 * 
 */
public class AgendaProcedimentoPesquisaProfissionalVO implements Serializable {

	private static final long serialVersionUID = 2413111669697273575L;

	private Integer matricula;
	private Short vinCodigo;
	private String nroRegConselho;
	private String cprSigla;
	private String nome;
	private DominioFuncaoProfissional funcao;
	private String especialidade;

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

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public String getCprSigla() {
		return cprSigla;
	}

	public void setCprSigla(String cprSigla) {
		this.cprSigla = cprSigla;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioFuncaoProfissional getFuncao() {
		return funcao;
	}

	public void setFuncao(DominioFuncaoProfissional funcao) {
		this.funcao = funcao;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.cprSigla);
		umHashCodeBuilder.append(this.matricula);
		umHashCodeBuilder.append(this.nome);
		umHashCodeBuilder.append(this.funcao);
		umHashCodeBuilder.append(this.nroRegConselho);
		umHashCodeBuilder.append(this.vinCodigo);
		umHashCodeBuilder.append(this.especialidade);
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AgendaProcedimentoPesquisaProfissionalVO other = (AgendaProcedimentoPesquisaProfissionalVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.cprSigla, other.cprSigla);
		umEqualsBuilder.append(this.matricula, other.matricula);
		umEqualsBuilder.append(this.nome, other.nome);
		umEqualsBuilder.append(this.funcao, other.funcao);
		umEqualsBuilder.append(this.nroRegConselho, other.nroRegConselho);
		umEqualsBuilder.append(this.vinCodigo, other.vinCodigo);
		umEqualsBuilder.append(this.especialidade, other.especialidade);
		return umEqualsBuilder.isEquals();
	}

}
