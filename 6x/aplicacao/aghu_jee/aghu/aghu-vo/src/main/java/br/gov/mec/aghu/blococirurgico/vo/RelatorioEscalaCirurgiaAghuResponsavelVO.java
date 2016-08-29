package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * VO da pesquisa de médicos/amestesistas responsáveis em #27566 - Escala de Cirurgias (Não existente no AGH)
 * 
 * @author aghu
 * 
 */
public class RelatorioEscalaCirurgiaAghuResponsavelVO implements Serializable, Comparable<RelatorioEscalaCirurgiaAghuResponsavelVO> {

	private static final long serialVersionUID = -1195141946409377311L;

	private DominioFuncaoProfissional pucIndFuncaoProf;
	private Short vinCodigo;
	private Integer matricula;
	private String nome;
	private String nomeUsual;

	public DominioFuncaoProfissional getPucIndFuncaoProf() {
		return pucIndFuncaoProf;
	}

	public void setPucIndFuncaoProf(DominioFuncaoProfissional pucIndFuncaoProf) {
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
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

	/**
	 * Descrição para o relatório no formato: Função do profissional + : + Nome ou Nome Usual
	 * 
	 * @return
	 */
	public String getDescricaoRelatorio() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getPucIndFuncaoProf()).append(": ");
		if (this.nomeUsual == null) {
			sb.append(StringUtils.substring(this.nome, 0, 15));
		} else {
			sb.append(this.nomeUsual);
		}
		return sb.toString();
	}

	/**
	 * Descrição formatada para a ordenação em listas
	 * 
	 * @return
	 */
	public String getDescricaoOrdenacao() {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtil.adicionaZerosAEsquerda(this.vinCodigo, 3))
		.append(StringUtil.adicionaZerosAEsquerda(this.matricula, 7).concat("-"));
		if (this.nomeUsual == null) {
			sb.append(StringUtils.substring(this.nome, 0, 15));
		} else {
			sb.append(this.nomeUsual);
		}
		return sb.toString();
	}

	public enum Fields {
		PUC_IND_FUNCAO_PROF("pucIndFuncaoProf"), VIN_CODIGO("vinCodigo"), MATRICULA("matricula"), NOME("nome"), NOME_USUAL("nomeUsual");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int compareTo(RelatorioEscalaCirurgiaAghuResponsavelVO other) {
		return other.getDescricaoOrdenacao().compareTo(this.getDescricaoOrdenacao());
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();

		hashCodeBuilder.append(this.pucIndFuncaoProf);
		hashCodeBuilder.append(this.vinCodigo);
		hashCodeBuilder.append(this.matricula);
		hashCodeBuilder.append(this.nome);
		hashCodeBuilder.append(this.nomeUsual);

		return hashCodeBuilder.toHashCode();
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

		RelatorioEscalaCirurgiaAghuResponsavelVO other = (RelatorioEscalaCirurgiaAghuResponsavelVO) obj;

		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.pucIndFuncaoProf, other.pucIndFuncaoProf);
		umEqualsBuilder.append(this.vinCodigo, other.vinCodigo);
		umEqualsBuilder.append(this.matricula, other.matricula);
		umEqualsBuilder.append(this.nome, other.nome);
		umEqualsBuilder.append(this.nomeUsual, other.nomeUsual);

		return umEqualsBuilder.isEquals();
	}

}
