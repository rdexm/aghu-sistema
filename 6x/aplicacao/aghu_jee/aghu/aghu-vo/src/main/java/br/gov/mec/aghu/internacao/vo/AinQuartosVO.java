package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AinQuartosVO implements BaseBean {

	private static final long serialVersionUID = -6781192858641168901L;

	private Short numero;
	private String descricao;
	private String descricaoAla;
	private String descricaoClinica;
	private String descricaoUnidade;
	private String descricaoAcomodacao;
	private Short capacInternacao;
	private DominioSexoDeterminante sexoDeterminante;
	private DominioSimNao indConsClin;
	private DominioSimNao indExclusivInfeccao;
	private DominioSimNao indConsSexo;

	private Integer qtQuartos;


	public enum Fields {
		NUMERO("numero"),
		DESCRICAO("descricao"), 	
		DESCRICAO_ALA("descricaoAla"),
		DESCRICAO_CLINICA("descricaoClinica"),
		DESCRICAO_UNIDADE("descricaoUnidade"),
		DESCRICAO_ACOMODACAO("descricaoAcomodacao"),
		CAPAC_INTERNACAO("capacInternacao"),
		SEXO_DETERMINANTE("sexoDeterminante"), 	 
		IND_CONS_CLIN("indConsClin"),
		IND_EXCLUSIV_INFECCAO("indExclusivInfeccao"),
		IND_CON_SEXO("indConsSexo"),
		QT_QUARTOS("qtQuartos");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Short getNumero() {
		return numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricaoAla() {
		return descricaoAla;
	}

	public void setDescricaoAla(String descricaoAla) {
		this.descricaoAla = descricaoAla;
	}

	public String getDescricaoClinica() {
		return descricaoClinica;
	}

	public void setDescricaoClinica(String descricaoClinica) {
		this.descricaoClinica = descricaoClinica;
	}

	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}

	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}

	public String getDescricaoAcomodacao() {
		return descricaoAcomodacao;
	}

	public void setDescricaoAcomodacao(String descricaoAcomodacao) {
		this.descricaoAcomodacao = descricaoAcomodacao;
	}

	public Short getCapacInternacao() {
		return capacInternacao;
	}

	public void setCapacInternacao(Short capacInternacao) {
		this.capacInternacao = capacInternacao;
	}

	public DominioSexoDeterminante getSexoDeterminante() {
		return sexoDeterminante;
	}

	public void setSexoDeterminante(DominioSexoDeterminante sexoDeterminante) {
		this.sexoDeterminante = sexoDeterminante;
	}

	public DominioSimNao getIndConsClin() {
		return indConsClin;
	}

	public void setIndConsClin(DominioSimNao indConsClin) {
		this.indConsClin = indConsClin;
	}

	public DominioSimNao getIndExclusivInfeccao() {
		return indExclusivInfeccao;
	}

	public void setIndExclusivInfeccao(DominioSimNao indExclusivInfeccao) {
		this.indExclusivInfeccao = indExclusivInfeccao;
	}

	public DominioSimNao getIndConsSexo() {
		return indConsSexo;
	}

	public void setIndConsSexo(DominioSimNao indConsSexo) {
		this.indConsSexo = indConsSexo;
	}

	public Integer getQtQuartos() {
		return qtQuartos;
	}

	public void setQtQuartos(Integer qtQuartos) {
		this.qtQuartos = qtQuartos;
	}
}
