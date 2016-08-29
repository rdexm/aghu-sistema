package br.gov.mec.aghu.orcamento.cadastrosbasicos.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioIndicadorParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioLimiteValorPatrimonio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class FsoParametrosOrcamentoResultVO implements BaseBean {
	private static final long serialVersionUID = -1802080319857700214L;
	
	private Integer seq;
	private String regra;
	private String descricaoRegra;
	private DominioIndicadorParametrosOrcamento indicador;
	private Integer grupoMaterialId;
	private String grupoMaterialDesc;
	private Integer materialId;
	private String materialDesc;
	private Integer grupoServicoId;
	private String grupoServicoDesc;
	private Integer servicoId;
	private String servicoDesc;
	private DominioLimiteValorPatrimonio limite;
	private BigDecimal valorLimite;
	private Integer centroCustoId;
	private String centroCustoDesc;
	private DominioSituacao situacao;
	
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}	

	public String getRegra() {
		return regra;
	}

	public void setRegra(String regra) {
		this.regra = regra;
	}

	public String getDescricaoRegra() {
		return descricaoRegra;
	}

	public void setDescricaoRegra(String descricaoRegra) {
		this.descricaoRegra = descricaoRegra;
	}

	public DominioIndicadorParametrosOrcamento getIndicador() {
		return indicador;
	}
	
	public void setIndicador(DominioIndicadorParametrosOrcamento indicador) {
		this.indicador = indicador;
	}
	
	public Integer getGrupoMaterialId() {
		return grupoMaterialId;
	}
	
	public void setGrupoMaterialId(Integer grupoMaterialId) {
		this.grupoMaterialId = grupoMaterialId;
	}
	
	public String getGrupoMaterialDesc() {
		return grupoMaterialDesc;
	}
	
	public void setGrupoMaterialDesc(String grupoMaterialDesc) {
		this.grupoMaterialDesc = grupoMaterialDesc;
	}
	
	public Integer getMaterialId() {
		return materialId;
	}
	
	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}
	
	public String getMaterialDesc() {
		return materialDesc;
	}
	
	public void setMaterialDesc(String materialDesc) {
		this.materialDesc = materialDesc;
	}
	
	public Integer getGrupoServicoId() {
		return grupoServicoId;
	}

	public void setGrupoServicoId(Integer grupoServicoId) {
		this.grupoServicoId = grupoServicoId;
	}

	public String getGrupoServicoDesc() {
		return grupoServicoDesc;
	}

	public void setGrupoServicoDesc(String grupoServicoDesc) {
		this.grupoServicoDesc = grupoServicoDesc;
	}

	public Integer getServicoId() {
		return servicoId;
	}

	public void setServicoId(Integer servicoId) {
		this.servicoId = servicoId;
	}

	public String getServicoDesc() {
		return servicoDesc;
	}

	public void setServicoDesc(String servicoDesc) {
		this.servicoDesc = servicoDesc;
	}

	public DominioLimiteValorPatrimonio getLimite() {
		return limite;
	}
	
	public void setLimite(DominioLimiteValorPatrimonio limite) {
		this.limite = limite;
	}
	
	public BigDecimal getValorLimite() {
		return valorLimite;
	}

	public void setValorLimite(BigDecimal valorLimite) {
		this.valorLimite = valorLimite;
	}

	public Integer getCentroCustoId() {
		return centroCustoId;
	}
	
	public void setCentroCustoId(Integer centroCustoId) {
		this.centroCustoId = centroCustoId;
	}
	
	public String getCentroCustoDesc() {
		return centroCustoDesc;
	}
	
	public void setCentroCustoDesc(String centroCustoDesc) {
		this.centroCustoDesc = centroCustoDesc;
	}
	
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public enum Fields {
		SEQ("seq"),
		REGRA("regra"),
		DESCRICAO_REGRA("descricaoRegra"),
		IND_GRUPO("indicador"),
		GRUPO_MATERIAL_ID("grupoMaterialId"),
		GRUPO_MATERIAL_DESC("grupoMaterialDesc"),
		MATERIAL_ID("materialId"),
		MATERIAL_DESC("materialDesc"),
		GRUPO_SERVICO_ID("grupoServicoId"),
		GRUPO_SERVICO_DESC("grupoServicoDesc"),
		SERVICO_ID("servicoId"),
		SERVICO_DESC("servicoDesc"),
		TP_LIMITE("limite"),
		VLR_LIMITE_PATRIMONIO("valorLimite"),
		CENTRO_CUSTO_ID("centroCustoId"),
		CENTRO_CUSTO_DESC("centroCustoDesc"),
		IND_SITUACAO("situacao");

		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}
}