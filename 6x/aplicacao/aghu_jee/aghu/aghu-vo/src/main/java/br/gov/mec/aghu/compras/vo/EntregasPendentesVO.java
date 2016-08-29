package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.VScoFornecedor;

public class EntregasPendentesVO {
	
	//FILTRO
	private VScoFornecedor fornecedor;
	private Integer af;
	private String modlPac;
	private Integer modlEmpenho;
	private Date vctoContrato;
	private String tipo;
	private Integer esl;
	private Short cp;
	private String tipoMovimento;
	private ScoMaterial material;
	private Integer cum;
	private ScoServico servico;
	//GRID
	private Integer afnNumero;
	private Integer pfrLctNumero;
	private Short nroComplemento;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private String mlcCodigo;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private Date dtVenctoContrato;
	private String tipoMaterial;
	
	public EntregasPendentesVO() {
	}
	
	public EntregasPendentesVO(Integer esl, String tipoMovimento) {
		this.esl = esl;
		this.tipoMovimento = tipoMovimento;
	}
	
	public EntregasPendentesVO(Integer afnNumero, Integer pfrLctNumero, Short nrComplemento,  DominioSituacaoAutorizacaoFornecimento situacao, 
			String mlcCodigo, DominioModalidadeEmpenho modEmpenho, Date dtVencContrato, String tipoMaterial) {
		this.afnNumero = afnNumero;
		this.pfrLctNumero = pfrLctNumero;
		this.nroComplemento = nrComplemento;
		this.situacao = situacao;
		this.mlcCodigo = mlcCodigo;
		this.modalidadeEmpenho = modEmpenho;
		this.dtVenctoContrato = dtVencContrato;
		this.tipoMaterial = tipoMaterial;
	}
	
	public String receberModlEmpenhoFormatado(){
		String retorno = StringUtils.EMPTY;
		if(this.modalidadeEmpenho != null){
			if(this.modalidadeEmpenho == DominioModalidadeEmpenho.ORDINARIO){
				retorno = "1 - Ordinário";
			} else if (this.modalidadeEmpenho == DominioModalidadeEmpenho.ESTIMATIVA){
				retorno = "3 - Estimativo";
			} else if (this.modalidadeEmpenho == DominioModalidadeEmpenho.CONTRATO){
				retorno = "5 - Contrato";
			}
		}
		return retorno;
	}
	
	public enum Fields {

		AF("af"),
		IND_SITUACAO("situacao"),
		MODL_PAC("modlPac"),
		MODL_EMPENHO("modlEmpenho"),
		VCTO_CONTRATO("vctoContrato"),
		TIPO("tipo"),
		ESL("esl"),
		TIPO_MOVIMENTO("tipoMovimento"),
		FORNECEDOR("fornecedor"),
		COMPLEMENTO("cp"),
		MATERIAL("material"),
		CUM("cum"),
		AFN_NUMERO("afnNumero"),
		PFR_LCT_NUMERO("pfrLctNumero"),
		NUMERO_COMPLEMENTO("nroComplemento"),
		MLC_CODIGO("mlcCodigo"),
		MODALIDADE_EMPENHO("modalidadeEmpenho"),
		DATA_VENC_CONTRATO("dtVenctoContrato"),
		TIPO_MATERIAL("tipoMaterial");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	
	public Integer getAf() {
		return af;
	}
	
	public void setAf(Integer af) {
		this.af = af;
	}
	
	public String getModlPac() {
		return modlPac;
	}
	
	public void setModlPac(String modlPac) {
		this.modlPac = modlPac;
	}
	
	public Integer getModlEmpenho() {
		return modlEmpenho;
	}
	
	public void setModlEmpenho(Integer modlEmpenho) {
		this.modlEmpenho = modlEmpenho;
	}
	
	public Date getVctoContrato() {
		return vctoContrato;
	}
	
	public void setVctoContrato(Date vctoContrato) {
		this.vctoContrato = vctoContrato;
	}
	
	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public Integer getEsl() {
		return esl;
	}
	
	public void setEsl(Integer esl) {
		this.esl = esl;
	}
	
	public String getTipoMovimento() {
		return tipoMovimento;
	}
	
	public void setTipoMovimento(String tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	public VScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(VScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Short getCp() {
		return cp;
	}

	public void setCp(Short cp) {
		this.cp = cp;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public Integer getCum() {
		return cum;
	}

	public void setCum(Integer cum) {
		this.cum = cum;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Integer getPfrLctNumero() {
		return pfrLctNumero;
	}

	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}

	public String getMlcCodigo() {
		return mlcCodigo;
	}

	public void setMlcCodigo(String mlcCodigo) {
		this.mlcCodigo = mlcCodigo;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public Date getDtVenctoContrato() {
		return dtVenctoContrato;
	}

	public void setDtVenctoContrato(Date dtVenctoContrato) {
		this.dtVenctoContrato = dtVenctoContrato;
	}

	public String getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(String tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}
}
