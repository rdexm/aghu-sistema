package br.gov.mec.aghu.compras.vo;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FornecedoresComEntregasPendentesVO {

	
	private VScoFornecedor fornecedor;
	private Long cnpj;
	private Long cpf;
	private Integer nf;
	private Date dtEmissao;
	private Date dtEntrada;
	private String docPendente;
	private List<String> indPreRecebimento;
	private String codigoBarra;
	private Integer af;
	private Short cp;
	private Integer esl;
	private Integer cum;
	private SceDocumentoFiscalEntrada notaFiscal;
	private ScoMaterial material;
	private ScoServico servico;
	private String color;
	private boolean sharp;
	private Boolean flagOcorrencia;
	private Boolean flagPreRecebimento;
	
	public FornecedoresComEntregasPendentesVO(VScoFornecedor fornecedor, Long cnpj, Long cpf, Integer nf, Date dtEmissao, Date dtEntrada,
			String docPendente, List<String> indPreRecebimento) {
		this.fornecedor = fornecedor;
		this.cnpj = cnpj;
		this.cpf = cpf;
		this.nf = nf;
		this.dtEmissao = dtEmissao;
		this.dtEntrada = dtEntrada;
		this.docPendente = docPendente;
		this.indPreRecebimento = indPreRecebimento;
	}
	
	public FornecedoresComEntregasPendentesVO() {
	}

	public FornecedoresComEntregasPendentesVO(Integer numero, String razaoSocial, Long cnpj, Long cpf) {
		this.fornecedor = new VScoFornecedor();
		this.fornecedor.setNumero(numero);
		this.fornecedor.setRazaoSocial(razaoSocial);
		validarCnpjCpf(cnpj, cpf);
		this.cnpj = cnpj;
		this.cpf = cpf;
	}

	public void validarCnpjCpf(Long cnpj, Long cpf) {
		if(cnpj != null){
			this.fornecedor.setCgcCpf(cnpj);
		} else {
			this.fornecedor.setCgcCpf(cpf);
		}
	}


	public String receberCpfCnpjFormatado() {
		if (this.cpf == null && this.cnpj == null) {
				return StringUtils.EMPTY;
		} else if(this.cpf != null && this.cnpj == null ){
			return CoreUtil.formataCPF(this.cpf);
		} else if(this.cpf == null && this.cnpj != null ){
			return CoreUtil.formatarCNPJ(this.cnpj);
		} else {
			return CoreUtil.formatarCNPJ(this.cnpj).concat("/").concat(CoreUtil.formataCPF(this.cpf));
		}
	}
	
	public String receberPreRecebimentoFormatado(){
		
		StringBuilder retorno = new  StringBuilder(StringUtils.EMPTY);
		
		if(indPreRecebimento == null){
			return StringUtils.EMPTY;
		} else {
			for(String list : this.indPreRecebimento){
				retorno.append(list);
				retorno.append(" /");
			}
		}
		return retorno.toString();
	}
	
	public enum Fields {

		FORNECEDOR("fornecedor"),
		FORNECEDOR_RAZAO_SOCIAL("fornecedor.razaoSocial"),
		FORNECEDOR_NUMERO("fornecedor.numero"),
		CNPJ("cnpj"),
		CPF("cpf"),
		NF("nf"),
		DT_EMISSAO("dtEmissao"),
		DT_ENTRADA("dtEntrada"),
		DOC_PENDENTE("docPendente"),
		IND_PRE_RECEBIMENTO("indPreRecebimento"),
		CODIGO_BARRA("codigoBarra"),
		AF("af"),
		CP("cp"),
		ESL("esl"),
		CUM("cum"),
		NOTA_FISCAL("notaFiscal"),
		MATERIAL("material"),
		SERVICO("servico"),
		MATERIAL_COD("material.codigo"),
		SERVICO_COD("servico.codigo");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public VScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(VScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Integer getNf() {
		return nf;
	}

	public void setNf(Integer nf) {
		this.nf = nf;
	}

	public Date getDtEmissao() {
		return dtEmissao;
	}

	public void setDtEmissao(Date dtEmissao) {
		this.dtEmissao = dtEmissao;
	}

	public Date getDtEntrada() {
		return dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}

	public String getDocPendente() {
		return docPendente;
	}

	public void setDocPendente(String docPendente) {
		this.docPendente = docPendente;
	}

	public List<String> getIndPreRecebimento() {
		return indPreRecebimento;
	}

	public void setIndPreRecebimento(List<String> indPreRecebimento) {
		this.indPreRecebimento = indPreRecebimento;
	}

	public String getCodigoBarra() {
		return codigoBarra;
	}

	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
	}

	public Integer getAf() {
		return af;
	}

	public void setAf(Integer af) {
		this.af = af;
	}

	public Short getCp() {
		return cp;
	}

	public void setCp(Short cp) {
		this.cp = cp;
	}

	public Integer getEsl() {
		return esl;
	}

	public void setEsl(Integer esl) {
		this.esl = esl;
	}

	public Integer getCum() {
		return cum;
	}

	public void setCum(Integer cum) {
		this.cum = cum;
	}

	public SceDocumentoFiscalEntrada getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(SceDocumentoFiscalEntrada notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isSharp() {
		return sharp;
	}

	public void setSharp(boolean sharp) {
		this.sharp = sharp;
	}

	public Boolean getFlagOcorrencia() {
		return flagOcorrencia;
	}

	public void setFlagOcorrencia(Boolean flagOcorrencia) {
		this.flagOcorrencia = flagOcorrencia;
	}

	public Boolean getFlagPreRecebimento() {
		return flagPreRecebimento;
	}

	public void setFlagPreRecebimento(Boolean flagPreRecebimento) {
		this.flagPreRecebimento = flagPreRecebimento;
	}

}
