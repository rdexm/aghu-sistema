package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ReceitasGeralEspecialVO implements Serializable {

	private static final long serialVersionUID = 4423741358727131666L;
	
	private Integer atdSeq;
	private Short espSeq;
	private Integer pacCodigo;
	private Integer prontuario;
	private Date dataHoraCriacao;
	private Integer conNumero;
	private String nomeReduzido;
	private String nomeEspecialidade;
	private Short vinculo;
	private Integer matricula;
	private String nome;
	private String dataOrd;
	private String tipo;
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}
	
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
	public Date getDataHoraCriacao() {
		return dataHoraCriacao;
	}
	
	public void setDataHoraCriacao(Date dataHoraCriacao) {
		this.dataHoraCriacao = dataHoraCriacao;
	}
	
	public Integer getConNumero() {
		return conNumero;
	}
	
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
	
	public String getNomeReduzido() {
		return nomeReduzido;
	}
	
	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}
	
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getDataOrd() {
		return dataOrd;
	}

	public void setDataOrd(String dataOrd) {
		this.dataOrd = dataOrd;
	}

	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
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
	
	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public enum Fields {

		ATD_SEQ("atdSeq"),
		ESP_SEQ("espSeq"),
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO("prontuario"),
		DATA_HORA_CRIACAO("dataHoraCriacao"),
		CON_NUMERO("conNumero"),
		NOME_REDUZIDO("nomeReduzido"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		MATRICULA("matricula"),
		VINCULO("vinculo"),
		NOME("nome"),
		DATA_ORD("dataOrd"),
		TIPO("tipo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
		// ##### GeradorEqualsHashCodeMain #####
		@Override
		public int hashCode() {
			HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
			umHashCodeBuilder.append(this.getAtdSeq());
			umHashCodeBuilder.append(this.getEspSeq());
			umHashCodeBuilder.append(this.getPacCodigo());
			umHashCodeBuilder.append(this.getProntuario());
			umHashCodeBuilder.append(this.getDataHoraCriacao());
			umHashCodeBuilder.append(this.getNomeReduzido());
			umHashCodeBuilder.append(this.getNomeEspecialidade());
			umHashCodeBuilder.append(this.getMatricula());
			umHashCodeBuilder.append(this.getVinculo());
			umHashCodeBuilder.append(this.getNome());
			umHashCodeBuilder.append(this.getDataOrd());
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
			if (!(obj instanceof ReceitasGeralEspecialVO)) {
				return false;
			}
			ReceitasGeralEspecialVO other = (ReceitasGeralEspecialVO) obj;
			EqualsBuilder umEqualsBuilder = new EqualsBuilder();
			umEqualsBuilder.append(this.getAtdSeq(), other.getAtdSeq());
			umEqualsBuilder.append(this.getEspSeq(), other.getEspSeq());
			umEqualsBuilder.append(this.getPacCodigo(), other.getPacCodigo());
			umEqualsBuilder.append(this.getProntuario(), other.getProntuario());
			umEqualsBuilder.append(this.getDataHoraCriacao(), other.getDataHoraCriacao());
			umEqualsBuilder.append(this.getNomeReduzido(), other.getNomeReduzido());
			umEqualsBuilder.append(this.getNomeEspecialidade(), other.getNomeEspecialidade());
			umEqualsBuilder.append(this.getMatricula(), other.getMatricula());
			umEqualsBuilder.append(this.getVinculo(), other.getVinculo());
			umEqualsBuilder.append(this.getNome(), other.getNome());
			umEqualsBuilder.append(this.getDataOrd(), other.getDataOrd());
			umEqualsBuilder.append(this.getTipo(), other.getTipo());
			return umEqualsBuilder.isEquals();
		}
		// ##### GeradorEqualsHashCodeMain #####
}