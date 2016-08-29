/**
 * 
 */
package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;

/**
 * @author lessandro.lucas
 *
 */

public class RelatorioMensalMateriaisClassificacaoAbcVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -145483766474176985L;

	public enum Fields{
		GRUPO_MATERIAL("grupoMaterial"),
		CODIGO("codigo"),
		NOME_MATERIAL("nomeMaterial"),
		ESTOCAVEL("indEstocavel"),
		CONSUMO_MES("consumoMes"),
		SUBCLASSIFICACAO_ABC("subClassificacaoAbc"),
		CLASSIFICACAO_ABC("classificacaoAbc"),
		SUBCLASSIFICACAO_ABC_CL_ATUAL("subClassificacaoAbcClAtual"),
		CLASSIFICACAO_ABC_CL_ATUAL("classificacaoAbcClAtual"), 
		VALOR_TRIMESTRE("valorTrimestre");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	private ScoGrupoMaterial grupoMaterial;	
	private Integer codigo;
	private String nomeMaterial;
	private Boolean indEstocavel;
	private Double consumoMes;
	private DominioClassifABC subClassificacaoAbc;
	private DominioClassifABC classificacaoAbc;
	private DominioClassifABC subClassificacaoAbcClAtual;
	private DominioClassifABC classificacaoAbcClAtual;
	private Double valorTrimestre;
	private Double percentTotal;
	private Double percentAcum;

	public Integer getGrupoMaterialStr() {
		return grupoMaterial.getCodigo();
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public DominioSimNao getEstocavel() {
		return DominioSimNao.getInstance(indEstocavel);
	}

	public Double getConsumoMes() {
		return consumoMes;
	}

	public void setConsumoMes(Double consumoMes) {
		this.consumoMes = consumoMes;
	}

	public DominioClassifABC getSubClassificacaoAbc() {
		return subClassificacaoAbc;
	}

	public void setSubClassificacaoAbc(DominioClassifABC subClassificacaoAbc) {
		this.subClassificacaoAbc = subClassificacaoAbc;
	}

	public DominioClassifABC getClassificacaoAbc() {
		return classificacaoAbc;
	}

	public void setClassificacaoAbc(DominioClassifABC classificacaoAbc) {
		this.classificacaoAbc = classificacaoAbc;
	}

	public DominioClassifABC getSubClassificacaoAbcClAtual() {
		return subClassificacaoAbcClAtual;
	}

	public void setSubClassificacaoAbcClAtual(DominioClassifABC subClassificacaoAbcClAtual) {
		this.subClassificacaoAbcClAtual = subClassificacaoAbcClAtual;
	}

	public DominioClassifABC getClassificacaoAbcClAtual() {
		return classificacaoAbcClAtual;
	}

	public void setClassificacaoAbcClAtual(DominioClassifABC classificacaoAbcClAtual) {
		this.classificacaoAbcClAtual = classificacaoAbcClAtual;
	}

	public Double getValorTrimestre() {
		return valorTrimestre;
	}

	public void setValorTrimestre(Double valorTrimestre) {
		this.valorTrimestre = valorTrimestre;
	}

	public Double getPercentTotal() {
		return percentTotal;
	}

	public void setPercentTotal(Double percentTotal) {
		this.percentTotal = percentTotal;
	}

	public Double getPercentAcum() {
		return percentAcum;
	}

	public void setPercentAcum(Double percentAcum) {
		this.percentAcum = percentAcum;
	}

	public Boolean getIndEstocavel() {
		return indEstocavel;
	}

	public void setIndEstocavel(Boolean indEstocavel) {
		this.indEstocavel = indEstocavel;
	}

	public String getMedAtual() {
		StringBuffer strMedAtual = new StringBuffer();
		
		if(getClassificacaoAbcClAtual() != null){
			strMedAtual.append(getClassificacaoAbcClAtual());
		}
		if(getSubClassificacaoAbcClAtual() != null){
			strMedAtual.append(getSubClassificacaoAbcClAtual());
		}
		return strMedAtual.toString();
	}
	
	public String getMedAnterior() {
		StringBuffer strMedAnterior = new StringBuffer();
		
		if(getClassificacaoAbc() != null){
			strMedAnterior.append(getClassificacaoAbc());
		}
		if(getSubClassificacaoAbc() != null){
			strMedAnterior.append(getSubClassificacaoAbc());
		}
		return strMedAnterior.toString();
	}
	
	public String getEstocavelStr() {
		if(getEstocavel() != null){
			if(getIndEstocavel()){
				return "E";	
			}
			else{
				return "D";
			}					
		}else{
			return null;
		}
	}
}