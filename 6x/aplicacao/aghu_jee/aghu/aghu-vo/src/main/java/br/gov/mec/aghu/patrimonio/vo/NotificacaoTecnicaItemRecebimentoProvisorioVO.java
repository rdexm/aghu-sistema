package br.gov.mec.aghu.patrimonio.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.CoreUtil;

public class NotificacaoTecnicaItemRecebimentoProvisorioVO {

	private Integer receb;
	private Integer item;
	private Integer esl;
	private Integer af;
	private Short complementoAf;
	private Integer quantidade;
	private Long qtdDisponivel;
	private Long qtdRetirada;
	private String fornecedor;
	private Long cgc;
	private Long cpf;
	private String cpfCnpj;
	private Long notaFiscal;
	private Integer codigoMaterial;
	private String descricaoMaterial;
	private Date dataNotificacao;
	private int status;
	private String tipoNotificacao;
	private String descricaoNotificacao;
	private String nomeTecnico;
	private Integer matriculaTecnico;
	private String nomeOficina;
	private String nomeCentroCusto;
	private Integer codigoCentroCusto;
	
	public NotificacaoTecnicaItemRecebimentoProvisorioVO() {
	}

	public NotificacaoTecnicaItemRecebimentoProvisorioVO(Integer receb,
			Integer item, Integer esl, Integer af, Short complementoAf,
			Integer quantidade, Long qtdDisponivel, 
			String fornecedor, Long cgc, Long cpf, 
			Long notaFiscal, Integer codigoMaterial, String descricaoMaterial,
			Date dataNotificacao, int status,
			String descricaoNotificacao, String nomeTecnico,
			Integer matriculaTecnico, String nomeOficina,
			String nomeCentroCusto, Integer codigoCentroCusto) {
		this.receb = receb;
		this.item = item;
		this.esl = esl;
		this.af = af;
		this.complementoAf = complementoAf;
		this.quantidade = quantidade;
		this.qtdDisponivel = qtdDisponivel;
		this.fornecedor = fornecedor;
		this.cgc = cgc;
		this.cpf = cpf;
		this.notaFiscal = notaFiscal;
		this.codigoMaterial = codigoMaterial;
		this.descricaoMaterial = descricaoMaterial;
		this.dataNotificacao = dataNotificacao;
		this.status = status;
		this.descricaoNotificacao = descricaoNotificacao;
		this.nomeTecnico = nomeTecnico;
		this.matriculaTecnico = matriculaTecnico;
		this.nomeOficina = nomeOficina;
		this.nomeCentroCusto = nomeCentroCusto;
		this.codigoCentroCusto = codigoCentroCusto;
	}
	
	public enum Fields {
		
		RECEBIMENTO("receb"),
		ESL("esl"),
		ITEM_RECEBIMENTO("item"),
		AF("af"),
		COMPLEMENTO("complementoAf"),
		CODIGO_MATERIAL("codigoMaterial"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		NOME_CENTRO_CUSTO("nomeCentroCusto"),
		CODIGO_CENTRO_CUSTO("codigoCentroCusto"),
		TECNICO_RESPONSAVEL("nomeTecnico"),
		NOTA_FISCAL("notaFiscal"),
		QUANTIDADE("quantidade"),
		QUANTIDADE_DISPONIVEL("qtdDisponivel"),
		QUANTIDADE_RETIRADA("qtdRetirada"),
		FORNECEDOR("fornecedor"),
		CGC("cgc"),
		CPF("cpf"),
		CPF_CNPJ("cpfCnpj"),
		DT_NOTIFICACAO("dataNotificacao"),
		STATUS("status"),
		DESCRICAO_NOTIFICACAO("descricaoNotificacao"),
		OFICINA("nomeOficina"),
		NATRICULA_TEC("matriculaTecnico");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getReceb() {
		return receb;
	}
	
	public void setReceb(Integer receb) {
		this.receb = receb;
	}
	
	public Integer getItem() {
		return item;
	}
	
	public void setItem(Integer item) {
		this.item = item;
	}
	
	public Integer getEsl() {
		return esl;
	}
	
	public void setEsl(Integer esl) {
		this.esl = esl;
	}
	
	public Integer getAf() {
		return af;
	}
	
	public void setAf(Integer af) {
		this.af = af;
	}
	
	public Short getComplementoAf() {
		return complementoAf;
	}
	
	public void setComplementoAf(Short complementoAf) {
		this.complementoAf = complementoAf;
	}
	
	public Integer getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	public Long getQtdDisponivel() {
		return qtdDisponivel;
	}
	
	public void setQtdDisponivel(Long qtdDisponivel) {
		this.qtdDisponivel = qtdDisponivel;
	}
	
	public Long getQtdRetirada() {
		if (this.quantidade != null) {
			if (this.qtdDisponivel != null) {
				qtdRetirada = this.quantidade - this.qtdDisponivel; 
			} else {
				qtdRetirada = this.quantidade.longValue();
			}
		} else {
			if (this.qtdDisponivel != null) {
				qtdRetirada = this.qtdDisponivel; 
			} else {
				qtdRetirada = 0l;
			}
		}
		return qtdRetirada;
	}
	
	public void setQtdRetirada(Long qtdRetirada) {
		this.qtdRetirada = qtdRetirada;
	}
	
	public String getFornecedor() {
		return fornecedor;
	}
	
	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
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
	
	public String getCpfCnpj() {
		if (this.cpf != null) {
			cpfCnpj = CoreUtil.formataCPF(this.cpf).toString();
		} else if(this.cgc != null) {
			cpfCnpj = CoreUtil.formatarCNPJ(this.cgc).toString();
		}
		return cpfCnpj;
	}
	
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	
	public Long getNotaFiscal() {
		return notaFiscal;
	}
	
	public void setNotaFiscal(Long notaFiscal) {
		this.notaFiscal = notaFiscal;
	}
	
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	
	public Date getDataNotificacao() {
		return dataNotificacao;
	}
	
	public void setDataNotificacao(Date dataNotificacao) {
		this.dataNotificacao = dataNotificacao;
	}
	
	public String getTipoNotificacao() {
		return tipoNotificacao;
	}
	
	public void setTipoNotificacao(String tipoNotificacao) {
		this.tipoNotificacao = tipoNotificacao;
	}
	
	public String getDescricaoNotificacao() {
		return descricaoNotificacao;
	}
	
	public void setDescricaoNotificacao(String descricaoNotificacao) {
		this.descricaoNotificacao = descricaoNotificacao;
	}
	
	public String getNomeTecnico() {
		return nomeTecnico;
	}
	
	public void setNomeTecnico(String nomeTecnico) {
		this.nomeTecnico = nomeTecnico;
	}
	
	public Integer getMatriculaTecnico() {
		return matriculaTecnico;
	}
	
	public void setMatriculaTecnico(Integer matriculaTecnico) {
		this.matriculaTecnico = matriculaTecnico;
	}
	
	public String getNomeOficina() {
		return nomeOficina;
	}
	
	public void setNomeOficina(String nomeOficina) {
		this.nomeOficina = nomeOficina;
	}
	
	public String getNomeCentroCusto() {
		return nomeCentroCusto;
	}
	
	public void setNomeCentroCusto(String nomeCentroCusto) {
		this.nomeCentroCusto = nomeCentroCusto;
	}
	
	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}
	
	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
