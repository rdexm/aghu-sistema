package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAfEmpenhada;

/**
 * @author dilceia.alves
 *
 */
public class ParcelasAutFornPedidoVO {
	
	private Short numeroIAF;
	
	private Integer parcela;
	private Integer fatorConversaoIAF;
	private Integer qtde;
	private Integer qtdeEntregue;
	private Integer codGrupoMaterial;
	private Integer codGrupoServico;
	private Integer codMaterial;
	private Integer codServico;

	private String codUnidMedIAF;
	private String nomeGrupoMaterial;
	private String nomeGrupoServico;
	private String nomeMaterial;
	private String nomeServico;
	private String descrMaterial;
	private String descrServico;
	private String codUnidMedMaterial;

	private Date dataPrevEntrega;
	private Date dataEnvioFornecedor;
	
	private Double valorTotal;
	
	private DominioAfEmpenhada indEmpenhada;
	private Boolean indPlanejamento;
	private Boolean indAssinatura;
	private Boolean indImpressa;
	private Boolean indEnvioFornecedor;
	
	// Getters/Setters
	public Short getNumeroIAF() {
		return numeroIAF;
	}
	
	public void setNumeroIAF(Short numeroIAF) {
		this.numeroIAF = numeroIAF;
	}
	
	public Integer getParcela() {
		return parcela;
	}
	
	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}
	
	public Integer getFatorConversaoIAF() {
		return fatorConversaoIAF;
	}
	
	public void setFatorConversaoIAF(Integer fatorConversaoIAF) {
		this.fatorConversaoIAF = fatorConversaoIAF;
	}
	
	public Integer getQtde() {
		return qtde;
	}
	
	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}
	
	public Integer getQtdeEntregue() {
		return qtdeEntregue;
	}
	
	public void setQtdeEntregue(Integer qtdeEntregue) {
		this.qtdeEntregue = qtdeEntregue;
	}
	
	public Integer getCodGrupoMaterial() {
		return codGrupoMaterial;
	}
	
	public void setCodGrupoMaterial(Integer codGrupoMaterial) {
		this.codGrupoMaterial = codGrupoMaterial;
	}
	
	public Integer getCodGrupoServico() {
		return codGrupoServico;
	}
	
	public void setCodGrupoServico(Integer codGrupoServico) {
		this.codGrupoServico = codGrupoServico;
	}
	
	public Integer getCodMaterial() {
		return codMaterial;
	}
	
	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}
	
	public Integer getCodServico() {
		return codServico;
	}
	
	public void setCodServico(Integer codServico) {
		this.codServico = codServico;
	}
	
	public String getCodUnidMedIAF() {
		return codUnidMedIAF;
	}
	
	public void setCodUnidMedIAF(String codUnidMedIAF) {
		this.codUnidMedIAF = codUnidMedIAF;
	}
			
	public String getNomeGrupoMaterial() {
		return nomeGrupoMaterial;
	}
	
	public void setNomeGrupoMaterial(String nomeGrupoMaterial) {
		this.nomeGrupoMaterial = nomeGrupoMaterial;
	}

	public String getNomeGrupoServico() {
		return nomeGrupoServico;
	}
	
	public void setNomeGrupoServico(String nomeGrupoServico) {
		this.nomeGrupoServico = nomeGrupoServico;
	}
	
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public String getNomeServico() {
		return nomeServico;
	}
	
	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}
	
	public String getDescrMaterial() {
		return descrMaterial;
	}
	
	public void setDescrMaterial(String descrMaterial) {
		this.descrMaterial = descrMaterial;
	}

	public String getDescrServico() {
		return descrServico;
	}
	
	public void setDescrServico(String descrServico) {
		this.descrServico = descrServico;
	}

	public String getCodUnidMedMaterial() {
		return codUnidMedMaterial;
	}

	public void setCodUnidMedMaterial(String codUnidMedMaterial) {
		this.codUnidMedMaterial = codUnidMedMaterial;
	}
	
	public Date getDataPrevEntrega() {
		return dataPrevEntrega;
	}

	public void setDataPrevEntrega(Date dataPrevEntrega) {
		this.dataPrevEntrega = dataPrevEntrega;
	}
	
	public Date getDataEnvioFornecedor() {
		return dataEnvioFornecedor;
	}

	public void setDataEnvioFornecedor(Date dataEnvioFornecedor) {
		this.dataEnvioFornecedor = dataEnvioFornecedor;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public DominioAfEmpenhada getIndEmpenhada() {
		return indEmpenhada;
	}

	public void setIndEmpenhada(DominioAfEmpenhada indEmpenhada) {
		this.indEmpenhada = indEmpenhada;
	}
	
	public Boolean getIndPlanejamento() {
		return indPlanejamento;
	}

	public void setIndPlanejamento(Boolean indPlanejamento) {
		this.indPlanejamento = indPlanejamento;
	}
	
	public Boolean getIndAssinatura() {
		return indAssinatura;
	}

	public void setIndAssinatura(Boolean indAssinatura) {
		this.indAssinatura = indAssinatura;
	}

	public Boolean getIndImpressa() {
		return indImpressa;
	}

	public void setIndImpressa(Boolean indImpressa) {
		this.indImpressa = indImpressa;
	}
	
	public Boolean getIndEnvioFornecedor() {
		return indEnvioFornecedor;
	}

	public void setIndEnvioFornecedor(Boolean indEnvioFornecedor) {
		this.indEnvioFornecedor = indEnvioFornecedor;
	}

	/** Campos **/
	public enum Fields {
		NUMERO_IAF("numeroIAF"),
		PARCELA("parcela"),
		FATOR_CONVERSAO_IAF("fatorConversaoIAF"),
		QTDE("qtde"),
		QTDE_ENTREGUE("qtdeEntregue"),
		COD_GRP_MATERIAL("codGrupoMaterial"),
		COD_GRP_SERVICO("codGrupoServico"),
		COD_MATERIAL("codMaterial"),
		COD_SERVICO("codServico"),
		COD_UNID_MED_IAF("codUnidMedIAF"),
		NOME_GRP_MATERIAL("nomeGrupoMaterial"),
		NOME_GRP_SERVICO("nomeGrupoServico"),
		NOME_MATERIAL("nomeMaterial"),
		NOME_SERVICO("nomeServico"),
		DESCR_MATERIAL("descrMaterial"),
		DESCR_SERVICO("descrServico"),
		COD_UNID_MED_MATERIAL("codUnidMedMaterial"),
		DT_PREV_ENTREGA("dataPrevEntrega"),
		DT_ENVIO_FORNECEDOR("dataEnvioFornecedor"),
		VALOR_TOTAL("valorTotal"),
		IND_EMPENHADA("indEmpenhada"),
		IND_PLANEJAMENTO("indPlanejamento"),
		IND_ASSINATURA("indAssinatura"),
		IND_IMPRESSA("indImpressa"),
		IND_ENVIO_FORNECEDOR("indEnvioFornecedor");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}