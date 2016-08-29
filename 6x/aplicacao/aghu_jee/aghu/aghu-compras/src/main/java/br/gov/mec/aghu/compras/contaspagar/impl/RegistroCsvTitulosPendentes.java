package br.gov.mec.aghu.compras.contaspagar.impl;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

public class RegistroCsvTitulosPendentes extends AbstractRegistroCsv {
	
	protected enum RegistroCsvTitulosPendentesEnum implements CamposEnum {

		TITULO("titulo", "Título"),
		DT_VENCIMENTO("dtVencimento","Vencimento"),
		NRO_PARCELA("nroParcela", "Parcela"),
		VALOR("valor","Valor"),
		NUMERO_NF("numeroSerie", "Doc. Fiscal"),
		DT_EMISSAO("dtEmissao","Dt Emissão Doc. Fiscal"),
		NUMERO_FORNECEDOR("numeroFornecedor","Num. Fornecedor"),
		CPF_FORNECEDOR("cpfFornecedor", "CNPJ"),
		CGC_FORNECEDOR("cgcFornecedor", "CGC"),
		RAZAO_SOCIAL_FORNECEDOR("razaoSocialFornecedor","Razão Social"),
		LICITACAO("licitacao","Licitação/Compl"),
		IND_DOCUMENTO("indDocumentacao", "Tem Doc.?"),
		IND_SITUACAO("indSituacao", "Situação");

		private final String desc;
		private final String campo;

		private RegistroCsvTitulosPendentesEnum(final String campo, final String desc) {
			this.campo = campo;
			this.desc = desc;
		}

		@Override
		public int getIndice() {
			return this.ordinal();
		}

		@Override
		public String getDescricao() {
			return this.desc;
		}

		@Override
		public String getCampo() {
			return this.campo;
		}
	}

	private String numeroSerie;
	private String licitacao;
	private Long cgcFornecedor;
	private Long cpfFornecedor;
	private Integer numeroFornecedor;
	private String razaoSocialFornecedor;
	private Integer titulo;
	private Short nroParcela;
	private Date dtEmissao;
	private Date dtVencimento;
	private Double valor;
	private Integer numeroNF;
	private String indDocumentacao;
	private DominioSituacaoTitulo indSituacao;
	
	public RegistroCsvTitulosPendentes() {
		super(NOME_TEMPLATE_LINHA_PONTO_VIRGULA);
	}

	@Override
	public CamposEnum[] obterCampos() {
		// TODO Auto-generated method stub
		return RegistroCsvTitulosPendentesEnum.values();
	}

	@Override
	protected Object obterRegistro(CamposEnum campo) {
		
		Object result = null;
		
		// Colocar tratamentos das colunas aqui
		if(campo != null && campo.equals(RegistroCsvTitulosPendentesEnum.CGC_FORNECEDOR)){
			if(this.cgcFornecedor != null){
				result = CoreUtil.formatarCNPJ(this.cgcFornecedor);
			} else {
				result = EMPTY_STR; // nao deveria ocorrer
			}			
		} else if(campo != null && campo.equals(RegistroCsvTitulosPendentesEnum.CPF_FORNECEDOR)){
			if(this.cpfFornecedor != null){
				result = CoreUtil.formataCPF(this.cpfFornecedor);
			} else {
				result = EMPTY_STR; // nao deveria ocorrer
			}
		}else if(campo == RegistroCsvTitulosPendentesEnum.VALOR) {
			if(this.valor != null) {
				result = String.format("%.2f", this.valor);
			} 
		} else if(campo == RegistroCsvTitulosPendentesEnum.DT_EMISSAO){
			if(this.dtEmissao != null){
			   result = DateFormatUtil.fomataDiaMesAno(this.dtEmissao);
			}
		} else if(campo == RegistroCsvTitulosPendentesEnum.DT_VENCIMENTO){
			if(this.dtVencimento != null){
			   result = DateFormatUtil.fomataDiaMesAno(this.dtVencimento);
			}
		} else {
			result = super.obterRegistro(campo);
		}
		
		return result;
	}

	public String getNumeroSerie() {
		return numeroSerie;
	}


	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}


	public String getLicitacao() {
		return licitacao;
	}


	public void setLicitacao(String licitacao) {
		this.licitacao = licitacao;
	}


	public Long getCgcFornecedor() {
		return cgcFornecedor;
	}


	public void setCgcFornecedor(Long cgcFornecedor) {
		this.cgcFornecedor = cgcFornecedor;
	}


	public Long getCpfFornecedor() {
		return cpfFornecedor;
	}


	public void setCpfFornecedor(Long cpfFornecedor) {
		this.cpfFornecedor = cpfFornecedor;
	}


	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}


	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}


	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}


	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}


	public Integer getTitulo() {
		return titulo;
	}


	public void setTitulo(Integer titulo) {
		this.titulo = titulo;
	}


	public Short getNroParcela() {
		return nroParcela;
	}


	public void setNroParcela(Short nroParcela) {
		this.nroParcela = nroParcela;
	}


	public Date getDtEmissao() {
		return dtEmissao;
	}


	public void setDtEmissao(Date dtEmissao) {
		this.dtEmissao = dtEmissao;
	}


	public Date getDtVencimento() {
		return dtVencimento;
	}


	public void setDtVencimento(Date dtVencimento) {
		this.dtVencimento = dtVencimento;
	}


	public Double getValor() {
		return valor;
	}


	public void setValor(Double valor) {
		this.valor = valor;
	}


	public Integer getNumeroNF() {
		return numeroNF;
	}


	public void setNumeroNF(Integer numeroNF) {
		this.numeroNF = numeroNF;
	}


	public String getIndDocumentacao() {
		return indDocumentacao;
	}


	public void setIndDocumentacao(String indDocumentacao) {
		this.indDocumentacao = indDocumentacao;
	}


	public DominioSituacaoTitulo getIndSituacao() {
		return indSituacao;
	}


	public void setIndSituacao(DominioSituacaoTitulo indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	
}