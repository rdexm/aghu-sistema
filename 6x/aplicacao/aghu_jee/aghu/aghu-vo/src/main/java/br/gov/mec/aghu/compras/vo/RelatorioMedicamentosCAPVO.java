package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class RelatorioMedicamentosCAPVO {
	
	private String descricao;
	private Integer codigo;
	private String unidadeMedidaCodigo;
	private Boolean estocavel;
	private DominioSimNao indConfaz;
	private DominioSimNao indCapCmed;
	private Date dataAlteracaoCap;
	private Date dataAlteracaoConfaz;
	private String nome;
	
	public enum Fields {  
		CODIGO("codigo"), UNIDADE_MEDIDA_CODIGO("unidadeMedidaCodigo"), SITUACAO("indSituacao"), 
		IND_ESTOCAVEL("estocavel"), DESCRICAO("descricao"), DT_ALTERACAO_CAP("dataAlteracaoCap"),
		DT_ALTERACAO_CONFAZ("dataAlteracaoConfaz"),IND_CAP_CMED("indCapCmed"),IND_CONFAZ("indConfaz"),NOME("nome");
		private String fields;
		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getUnidadeMedidaCodigo() {
		return unidadeMedidaCodigo;
	}
	public void setUnidadeMedidaCodigo(String unidadeMedidaCodigo) {
		this.unidadeMedidaCodigo = unidadeMedidaCodigo;
	}
	public Boolean getEstocavel() {
		return estocavel;
	}
	public void setEstocavel(Boolean estocavel) {
		this.estocavel = estocavel;
	}
	public DominioSimNao getIndConfaz() {
		return indConfaz;
	}
	public void setIndConfaz(DominioSimNao indConfaz) {
		this.indConfaz = indConfaz;
	}
	public DominioSimNao getIndCapCmed() {
		return indCapCmed;
	}
	public void setIndCapCmed(DominioSimNao indCapCmed) {
		this.indCapCmed = indCapCmed;
	}
	public Date getDataAlteracaoCap() {
		return dataAlteracaoCap;
	}
	public void setDataAlteracaoCap(Date dataAlteracaoCap) {
		this.dataAlteracaoCap = dataAlteracaoCap;
	}
	public Date getDataAlteracaoConfaz() {
		return dataAlteracaoConfaz;
	}
	public void setDataAlteracaoConfaz(Date dataAlteracaoConfaz) {
		this.dataAlteracaoConfaz = dataAlteracaoConfaz;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

}
