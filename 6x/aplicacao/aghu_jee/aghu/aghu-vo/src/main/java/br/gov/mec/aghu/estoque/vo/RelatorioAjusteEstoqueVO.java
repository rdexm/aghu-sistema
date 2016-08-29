package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
/**
 * Classe responsável por apresentar os dados do relatório
 * de ajuste de estoque
 * @author clayton.bras
 *
 */
public class RelatorioAjusteEstoqueVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5106751784146018957L;
	private Date dataGeracao;
	private Integer codigoGrupoMaterial;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer quantidadeAjuste;
	private Double valorUnitario;
	private Double valorAjuste;
	private String tipoAjuste;
	private String motivoAjuste;
	private String siglaTipoMovimento;
	private String descricaoMotivoMovimento;
	
	public enum Fields{
		DATA_GERACAO("dataGeracao"),
		CODIGO_GRUPO_MATERIAL("codigoGrupoMaterial"),
		CODIGO_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		QUANTIDADE_AJUSTE("quantidadeAjuste"),		
		VALOR_UNITARIO("valorUnitario"),
		VALOR_AJUSTE("valorAjuste"),
		TIPO_AJUSTE("tipoAjuste"),
		MOTIVO_AJUSTE("motivoAjuste"),
		SIGLA_TIPO_MOVIMENTO("siglaTipoMovimento"),
		DESCRICAO_MOTIVO_MOVIMENTO("descricaoMotivoMovimento");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public Date getDataGeracao() {
		return dataGeracao;
	}


	public void setDataGeracao(Date dataGeracao) {
		if(getDataGeracao()!=null && dataGeracao!=null){
			this.dataGeracao = br.gov.mec.aghu.core.utils.DateUtil.truncaData(dataGeracao);
		}
		this.dataGeracao = dataGeracao;
	}


	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}


	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}


	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}


	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}


	public String getNomeMaterial() {
		return nomeMaterial;
	}


	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}


	public Integer getQuantidadeAjuste() {
		return quantidadeAjuste;
	}


	public void setQuantidadeAjuste(Integer quantidadeAjuste) {
		this.quantidadeAjuste = quantidadeAjuste;
	}


	public Double getValorUnitario() {
		return valorUnitario;
	}


	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}


	public Double getValorAjuste() {
		return valorAjuste;
	}


	public void setValorAjuste(Double valorAjuste) {
		this.valorAjuste = valorAjuste;
	}


	public String getTipoAjuste() {
		return tipoAjuste;
	}


	public void setTipoAjuste(String tipoAjuste) {
		this.tipoAjuste = tipoAjuste;
	}


	public String getMotivoAjuste() {
		return motivoAjuste;
	}


	public void setMotivoAjuste(String motivoAjuste) {
		this.motivoAjuste = motivoAjuste;
	}


	public String getSiglaTipoMovimento() {
		return siglaTipoMovimento;
	}


	public void setSiglaTipoMovimento(String siglaTipoMovimento) {
		this.siglaTipoMovimento = siglaTipoMovimento;
	}


	public String getDescricaoMotivoMovimento() {
		return descricaoMotivoMovimento;
	}


	public void setDescricaoMotivoMovimento(String descricaoMotivoMovimento) {
		this.descricaoMotivoMovimento = descricaoMotivoMovimento;
	}

	
	/**
	 * Método estático para auxiliar a totalização de motivos ajustes durante o processamento do relatório jasper.
	 * O objetivo dessa abordagem foi o de não ser necessário criar uma consulta a mais de forma desnecessária
	 * nem iterar o resultado do relatório antes do seu processamento, ganhando em desempenho.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })	
	public static Map adicionarContagemMotivoAjuste(Map mapContadorMotivoAjuste, String descricaoMotivoAjuste, Double valorMotivoAjuste){
		if(mapContadorMotivoAjuste != null){
			if(mapContadorMotivoAjuste.containsKey(descricaoMotivoAjuste)){
				((TotalizadorMotivoAjusteVO) mapContadorMotivoAjuste.get(descricaoMotivoAjuste)).contabilizarMotivoAjuste(valorMotivoAjuste);
			}else{
				mapContadorMotivoAjuste.put(descricaoMotivoAjuste, new TotalizadorMotivoAjusteVO(descricaoMotivoAjuste, valorMotivoAjuste));
			}
		}
		return mapContadorMotivoAjuste;
	}	
}
