package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAfpPublicado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;

/**
 * @author dilceia.alves
 *
 */
public class AutorizacaoFornPedidosVO {
	
	private Integer afnNumero;
	private Integer numeroAFP;
	private Integer lctNumero;
	private Integer numeroFornecedor;
	
	private Short numeroComplemento;
		
	private Date dtEnvioFornecedor;

	private String razaoSocial;
	private Long cgc;
	private Long cpf;
	private String nomeGestor;
	private String nomeServidor;
	private String origem; 	// DominioTipoFaseSolicitacao.S -> "Serviço"; .C -> "Material"; Senão "Comunicação Uso Material"
	private String hintOrigem;
	private String empenho;
	private String corFundoOrigem;
	private String corFundoEmpenho;
	
	private Boolean selecionado;
	private ScoPropostaFornecedor scoPropostaFornecedor;
	private ScoFornecedor fornecedor;
	
	private DominioAfpPublicado indPublicado;
	private Date dtPublicacao;
	
	
	// Getters/Setters
	public Integer getAfnNumero() {
		return afnNumero;
	}
	
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}
	
	public Integer getNumeroAFP() {
		return numeroAFP;
	}
	
	public void setNumeroAFP(Integer numeroAFP) {
		this.numeroAFP = numeroAFP;
	}
	
	public Integer getLctNumero() {
		return lctNumero;
	}
	
	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}
	
	public Short getNumeroComplemento() {
		return numeroComplemento;
	}
	
	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}
	
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}
	
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}
	
	public Date getDtEnvioFornecedor() {
		return dtEnvioFornecedor;
	}
	
	public void setDtEnvioFornecedor(Date dtEnvioFornecedor) {
		this.dtEnvioFornecedor = dtEnvioFornecedor;
	}
	
	public String getRazaoSocial() {
		return razaoSocial;
	}
	
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	
	public String getNomeGestor() {
		return nomeGestor;
	}
	
	public void setNomeGestor(String nomeGestor) {
		this.nomeGestor = nomeGestor;
	}
			
	public String getOrigem() {
		return origem;
	}
	
	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getHintOrigem() {
		return hintOrigem;
	}
	
	public void setHintOrigem(String hintOrigem) {
		this.hintOrigem = hintOrigem;
	}

	public String getEmpenho() {
		return empenho;
	}

	public void setEmpenho(String empenho) {
		this.empenho = empenho;
	}
	
	public String getCorFundoOrigem() {
		return corFundoOrigem;
	}

	public void setCorFundoOrigem(String corFundoOrigem) {
		this.corFundoOrigem = corFundoOrigem;
	}

	public String getCorFundoEmpenho() {
		return corFundoEmpenho;
	}

	public void setCorFundoEmpenho(String corFundoEmpenho) {
		this.corFundoEmpenho = corFundoEmpenho;
	}
	
	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	public ScoPropostaFornecedor getScoPropostaFornecedor() {
		return scoPropostaFornecedor;
	}

	public void setScoPropostaFornecedor(ScoPropostaFornecedor scoPropostaFornecedor) {
		this.scoPropostaFornecedor = scoPropostaFornecedor;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	
	public DominioAfpPublicado getIndPublicado() {
		return indPublicado;
	}

	public void setIndPublicado(DominioAfpPublicado indPublicado) {
		this.indPublicado = indPublicado;
	}

	public Date getDtPublicacao() {
		return dtPublicacao;
	}

	public void setDtPublicacao(Date dtPublicacao) {
		this.dtPublicacao = dtPublicacao;
	}

	/** Campos **/
	public enum Fields {
		NUMERO_AFN("afnNumero"),
		NUMERO_AFP("numeroAFP"),
		NUMERO_PAC("lctNumero"),
		NUM_COMPLEMENTO_AF("numeroComplemento"),
		NUM_FORNECEDOR("numeroFornecedor"),		
		DT_ENVIO_FORNECEDOR("dtEnvioFornecedor"),
		RAZAO_SOCIAL_FORNECEDOR("razaoSocial"), 
		NOME_GESTOR("nomeGestor"),
		CGC("cgc"),
		CPF("cpf"),
		NOME_SERVIDOR("nomeServidor"),
		FORNECEDOR("fornecedor"),
		IND_PUBLICADO("indPublicado"),
		DT_PUBLICACAO("dtPublicacao");
		
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
