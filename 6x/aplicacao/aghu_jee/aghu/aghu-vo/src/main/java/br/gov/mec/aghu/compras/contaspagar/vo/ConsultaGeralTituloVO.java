package br.gov.mec.aghu.compras.contaspagar.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * @author lucas lima
 *
 */
public class ConsultaGeralTituloVO implements BaseBean {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6904437303541189557L;

	
	private Integer ntdGndCodigo;
	
	private Byte ntdCodigo;
	
	private Integer ttlSeq;
	
	private Date dataVencimentoInicial;
	
	private Date dataVencimentoFinal;
	
	private String gndDescricao;
	
	private String ntdDescricao;
	
	private String cltClassifcacao;
	
	private Integer frnNumero;
	
	private Long frnCnpj;
	
	private Long frnCpf;
	
	private String razaoSocial;
	
	private String descricaoTipo;
	
	private Date dataVencimento;
	
	private DominioSituacaoTitulo situacaoTitulo;
	
	private Double valor;
	
	private Short tptCodigo;
	
	private DominioModalidadeEmpenho modalidadeEmpenho;
	
	
//    FRN.RAZAO_SOCIAL as "Credor",
//    TPT.DESCRICAO as "Tipo",
//    TTL.DT_VENCIMENTO as "Dt. Vencimento",
//    TTL.IND_SITUACAO as "Situação",
//    TTL.VALOR as "Valor"

	
	public ConsultaGeralTituloVO(){
		
	}

	public Integer getNtdGndCodigo() {
		return ntdGndCodigo;
	}

	public void setNtdGndCodigo(Integer ntdGndCodigo) {
		this.ntdGndCodigo = ntdGndCodigo;
	}



	public Date getDataVencimentoInicial() {
		return dataVencimentoInicial;
	}

	public void setDataVencimentoInicial(Date dataVencimentoInicial) {
		this.dataVencimentoInicial = dataVencimentoInicial;
	}



	public Date getDataVencimentoFinal() {
		return dataVencimentoFinal;
	}

	public void setDataVencimentoFinal(Date dataVencimentoFinal) {
		this.dataVencimentoFinal = dataVencimentoFinal;
	}



	public Byte getNtdCodigo() {
		return ntdCodigo;
	}

	public void setNtdCodigo(Byte ntdCodigo) {
		this.ntdCodigo = ntdCodigo;
	}



	public String getGndDescricao() {
		return gndDescricao;
	}

	public void setGndDescricao(String gndDescricao) {
		this.gndDescricao = gndDescricao;
	}



	public Integer getTtlSeq() {
		return ttlSeq;
	}

	public void setTtlSeq(Integer ttlSeq) {
		this.ttlSeq = ttlSeq;
	}



	public String getCltClassifcacao() {
		return cltClassifcacao;
	}

	public void setCltClassifcacao(String cltClassifcacao) {
		this.cltClassifcacao = cltClassifcacao;
	}



	public Integer getFrnNumero() {
		return frnNumero;
	}

	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}



	public Long getFrnCnpj() {
		return frnCnpj;
	}

	public void setFrnCnpj(Long frnCnpj) {
		this.frnCnpj = frnCnpj;
	}



	public Long getFrnCpf() {
		return frnCpf;
	}

	public void setFrnCpf(Long frnCpf) {
		this.frnCpf = frnCpf;
	}

	public String contacCpfCnpj(){
		StringBuffer retorno = new StringBuffer("");
		if(this.frnCnpj!=null){
			retorno.append(this.frnCnpj.toString());
		}
		if(this.frnCpf!=null){
			retorno.append(this.frnCpf.toString());
		}
		return retorno.toString();
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getDescricaoTipo() {
		return descricaoTipo;
	}

	public void setDescricaoTipo(String descricaoTipo) {
		this.descricaoTipo = descricaoTipo;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	
	public DominioSituacaoTitulo getSituacaoTitulo() {
		return situacaoTitulo;
	}

	public void setSituacaoTitulo(DominioSituacaoTitulo situacaoTitulo) {
		this.situacaoTitulo = situacaoTitulo;
	} 
	
	public Short getTptCodigo() {
		return tptCodigo;
	}

	public void setTptCodigo(Short tptCodigo) {
		this.tptCodigo = tptCodigo;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public String getNtdDescricao() {
		return ntdDescricao;
	}

	public void setNtdDescricao(String ntdDescricao) {
		this.ntdDescricao = ntdDescricao;
	}

	public enum Fields {
		NTD_GND_CODIGO("ntdGndCodigo"),
		DT_INICIAL("dataVencimentoInicial"),
		DT_FINAL("dataVencimentoFinal"),
		DATA_GERACAO("dataGeracao"),
		TTL_SEQ("ttlSeq"),
		GND_DESCRICAO("gndDescricao"),
		NTD_CODIGO("ntdCodigo"),
		NTD_DESCRICAO("ntdDescricao"),
		CLT_DESCRICAO("cltClassifcacao"),
		MODALIDADE_EMPENHO("modalidadeEmpenho"),
		FRN_NUMERO("frnNumero"),
		FRN_CPF("frnCpf"),
		FRN_CNPJ("frnCnpj"),
		DESCRICAO_TIPO_TITULO("descricaoTipo"),
		RAZAO_SOCIAL("razaoSocial"),
		DT_VENCIMENTO("dataVencimento"),
		SITUACAO("situacaoTitulo"),
		VALOR_TITULO("valor"),
		TPT_CODIGO("tptCodigo");
		
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