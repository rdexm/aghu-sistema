package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.text.ParseException;

import javax.swing.text.MaskFormatter;

import br.gov.mec.aghu.core.utils.StringUtil;


public class ProcedimentoRealizadoDadosOPMVO implements Serializable {

	private static final long serialVersionUID = 3247714924282685557L;
	
	/**
	 * DADOS VO #36464 C7/C8
	 */
	private Short seqArqSus;
	private Long iphCodSusProcedimento;
	private Short indEquipe;
	private Long cpfCns;
	private String cbo;
	private Long cgc;
	private Short quantidade;
	private String competenciaUti;
	private String descricao;
	private Long iphCodSusGrupo;
	private String cnesCnpj;
	private String agrupValor;
	
	private String cnpjFornecedorFormatado;
	private String regAnvisaFormatado;
	private String cnpjFabricanteFormatado;
	private String iphCodSusProcedimentoFormatado;
	
	
	/**
	 * DADOS VO #36464 C7
	 */
	private Integer notaFiscal;
	private String loteOpm;
	private String serieOpm;
	private String regAnvisaOpm;
	private Long cnpjRegAnvisa;
	private String cboFormatado;
	private String compFormatado;
	
	
	public String getIphCodSusProcedimentoFormatado() {
		
		iphCodSusProcedimentoFormatado = "";		
		
		if (this.iphCodSusProcedimento != null) {
			iphCodSusProcedimentoFormatado = StringUtil.adicionaZerosAEsquerda(this.iphCodSusProcedimento, 10);
		}
		
		return iphCodSusProcedimentoFormatado;
	}

	public String getCnpjFabricanteFormatado() {
		cnpjFabricanteFormatado = "";
		if (this.cnpjRegAnvisa != null && this.cnpjRegAnvisa != 0) {			
			String cnpj = StringUtil.adicionaZerosAEsquerda(this.cnpjRegAnvisa, 14);			
			try {
				MaskFormatter mf = new MaskFormatter("##.###.###/####-##");
				mf.setValueContainsLiteralCharacters(false);
				cnpjFabricanteFormatado = mf.valueToString(cnpj);
			} catch (ParseException e) {
				cnpjFabricanteFormatado = "";
			}  
		}
		
		return cnpjFabricanteFormatado;
	}
	
	public String getRegAnvisaFormatado() {
		regAnvisaFormatado = "";
		if (this.regAnvisaOpm != null && !this.regAnvisaOpm.equals("0")) {
			regAnvisaFormatado = this.regAnvisaOpm;
		}
		
		return regAnvisaFormatado;
	}
	
	public String getCnpjFornecedorFormatado() {
		cnpjFornecedorFormatado = "";		
		if (this.cgc != null) {			
			String cnpj = StringUtil.adicionaZerosAEsquerda(this.cgc, 14);			
			try {
				MaskFormatter mf = new MaskFormatter("##.###.###/####-##");
				mf.setValueContainsLiteralCharacters(false);
				cnpjFornecedorFormatado = mf.valueToString(cnpj);
			} catch (ParseException e) {
				cnpjFornecedorFormatado = "";
			}  
		}
		
		return cnpjFornecedorFormatado;
	}
	
	public String getCboFormatado() {
		cboFormatado = "";
		
		if(indEquipe != null){
			
			if (indEquipe == 1 || indEquipe == 6) {
				cboFormatado = cbo + "(" + indEquipe + ")";
			} else {
				cboFormatado = cbo;
			}
		}else{
			if(cbo != null){
				cboFormatado = cbo;
			}
		}
		return cboFormatado;
	}
	
	public String obterGrupo(){
		String retorno;
		if(iphCodSusGrupo != null){
			retorno = this.iphCodSusGrupo.toString();
			for(int i = this.iphCodSusGrupo.toString().length(); i < 10; i++){
				retorno = "0".concat(retorno);
			}
		}else{
			retorno = "";
		}
		
		return retorno.substring(0,2);
	}
	
	public String getCompFormatado(){
		String ano = this.competenciaUti.substring(0,4);
		String mes = this.competenciaUti.substring(4,6);
		
		compFormatado = mes.concat("/").concat(ano);	
		
		return compFormatado;
	}
	
	public enum Fields {

		SEQ_ARQ_SUS("seqArqSus"),
		IPH_COD_SUS_PROCEDIMENTO("iphCodSusProcedimento"),
		IND_EQUIPE("indEquipe"),
		CPF_CNS("cpfCns"),
		CBO("cbo"),
		CGC("cgc"),
		QUANTIDADE("quantidade"),
		COMPETENCIA_UTI("competenciaUti"),
		DESCRICAO("descricao"),
		IPH_COD_SUS_GRUPO("iphCodSusGrupo"),
		NOTA_FISCAL("notaFiscal"),
		LOTE_OPM("loteOpm"),
		SERIE_OPM("serieOpm"),
		REG_ANVISA_OPM("regAnvisaOpm"),
		CNPJ_REG_ANVISA("cnpjRegAnvisa");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Short getSeqArqSus() {
		return seqArqSus;
	}

	public void setSeqArqSus(Short seqArqSus) {
		this.seqArqSus = seqArqSus;
	}

	public Long getIphCodSusProcedimento() {
		return iphCodSusProcedimento;
	}

	public void setIphCodSusProcedimento(Long iphCodSusProcedimento) {
		this.iphCodSusProcedimento = iphCodSusProcedimento;
	}

	public Short getIndEquipe() {
		return indEquipe;
	}

	public void setIndEquipe(Short indEquipe) {
		this.indEquipe = indEquipe;
	}

	public Long getCpfCns() {
		return cpfCns;
	}

	public void setCpfCns(Long cpfCns) {
		this.cpfCns = cpfCns;
	}

	public String getCbo() {
		return cbo;
	}

	public void setCbo(String cbo) {
		this.cbo = cbo;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Short getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	public String getCompetenciaUti() {
		return competenciaUti;
	}

	public void setCompetenciaUti(String competenciaUti) {
		this.competenciaUti = competenciaUti;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getIphCodSusGrupo() {
		return iphCodSusGrupo;
	}

	public void setIphCodSusGrupo(Long iphCodSusGrupo) {
		this.iphCodSusGrupo = iphCodSusGrupo;
	}

	public Integer getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(Integer notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public String getLoteOpm() {
		return loteOpm;
	}

	public void setLoteOpm(String loteOpm) {
		this.loteOpm = loteOpm;
	}

	public String getSerieOpm() {
		return serieOpm;
	}

	public void setSerieOpm(String serieOpm) {
		this.serieOpm = serieOpm;
	}

	public String getRegAnvisaOpm() {
		return regAnvisaOpm;
	}

	public void setRegAnvisaOpm(String regAnvisaOpm) {
		this.regAnvisaOpm = regAnvisaOpm;
	}

	public Long getCnpjRegAnvisa() {
		return cnpjRegAnvisa;
	}

	public void setCnpjRegAnvisa(Long cnpjRegAnvisa) {
		this.cnpjRegAnvisa = cnpjRegAnvisa;
	}

	public String getAgrupValor() {
		return agrupValor;
	}

	public void setAgrupValor(String agrupValor) {
		this.agrupValor = agrupValor;
	}

	public String getCnesCnpj() {
		return cnesCnpj;
	}

	public void setCnesCnpj(String cnesCnpj) {
		this.cnesCnpj = cnesCnpj;
	}
	
}
