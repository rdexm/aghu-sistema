package br.gov.mec.aghu.exames.vo;




public class TicketExamesPacienteVO implements Comparable<TicketExamesPacienteVO>{

	private String itemSolicitacaoExameSeqP;
	private String ufeUnfSeq;
	private String solicitacaoExameSeq;
	private String atdSeq;	
	private String origem;
	private String criadoEm;
	private String nomeResp;
	private String informacoesClinicas;
	private String prontuario;
	private String nome;
	private String nascimento;
	private String idade;
	private String descricaoUsual;
	private String unfSeq;
	private String natureza;
	private String sigla;
	private String manSeq;
	private String tempoJejumNpo;
	private String descricaoMaterial;
	private String descricaoZona;
	private String descricaoUnidadeSolicitante;
	private String convenio;
	private String cnvCodigo;
	private String cspSeq;
	private Integer tempoRealizacaoDias;
	private String unfSeqComparece;
	private String projeto;
	private String unidSolic;
	
	private String recomendacoes;
	
	private String agendamento;
	
	private String textoComparecer;
	
	private String maiorTempoRealizacao;
	
	private String codigoBarraItem;
	
	private String examesAgendados;

	private String localizador;

	private String infoGerais;
	
	private Short tempoJejum;
	
	private String maiorTempoJejum;
	
	public int compareTo(TicketExamesPacienteVO other) {
		int result = this.getTextoComparecer().compareTo(
				other.getTextoComparecer());
//		if (result == 0) {
//			if (this.getDescricaoMaterial() != null
//					&& other.getDescricaoMaterial() != null) {
//				result = this.getDescricaoMaterial().compareTo(
//						other.getDescricaoMaterial());
//				if (result == 0) {
//					if (this.getItemSolicitacaoExameSeqP() != null
//							&& other.getItemSolicitacaoExameSeqP() != null) {
//						result = this.getItemSolicitacaoExameSeqP().compareTo(
//								other.getItemSolicitacaoExameSeqP());
//						if (result == 0) {
//							if (this.getDescricaoUsual() != null
//									&& other.getDescricaoUsual() != null) {
//								return this.getDescricaoUsual().compareTo(
//										other.getDescricaoUsual());
//							}
//						}
//					}
//
//				}
//			}
//		}
		return result;
	}
	
	public String getItemSolicitacaoExameSeqP() {
		return itemSolicitacaoExameSeqP;
	}
	public void setItemSolicitacaoExameSeqP(String itemSolicitacaoExameSeqP) {
		this.itemSolicitacaoExameSeqP = itemSolicitacaoExameSeqP;
	}
	public String getUfeUnfSeq() {
		return ufeUnfSeq;
	}
	public void setUfeUnfSeq(String ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}
	public String getSolicitacaoExameSeq() {
		return solicitacaoExameSeq;
	}
	public void setSolicitacaoExameSeq(String solicitacaoExameSeq) {
		this.solicitacaoExameSeq = solicitacaoExameSeq;
	}
	public String getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(String atdSeq) {
		this.atdSeq = atdSeq;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public String getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getNomeResp() {
		return nomeResp;
	}
	public void setNomeResp(String nomeResp) {
		this.nomeResp = nomeResp;
	}
	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}
	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getNascimento() {
		return nascimento;
	}
	public void setNascimento(String nascimento) {
		this.nascimento = nascimento;
	}
	public String getIdade() {
		return idade;
	}
	public void setIdade(String idade) {
		this.idade = idade;
	}
	public String getDescricaoUsual() {
		return descricaoUsual;
	}
	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}
	public String getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(String unfSeq) {
		this.unfSeq = unfSeq;
	}
	public String getNatureza() {
		return natureza;
	}
	public void setNatureza(String natureza) {
		this.natureza = natureza;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getManSeq() {
		return manSeq;
	}
	public void setManSeq(String manSeq) {
		this.manSeq = manSeq;
	}
	public String getTempoJejumNpo() {
		return tempoJejumNpo;
	}
	public void setTempoJejumNpo(String tempoJejumNpo) {
		this.tempoJejumNpo = tempoJejumNpo;
	}
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	public String getDescricaoZona() {
		return descricaoZona;
	}
	public void setDescricaoZona(String descricaoZona) {
		this.descricaoZona = descricaoZona;
	}
	public String getDescricaoUnidadeSolicitante() {
		return descricaoUnidadeSolicitante;
	}
	public void setDescricaoUnidadeSolicitante(String descricaoUnidadeSolicitante) {
		this.descricaoUnidadeSolicitante = descricaoUnidadeSolicitante;
	}
	public String getConvenio() {
		return convenio;
	}
	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}
	public String getCnvCodigo() {
		return cnvCodigo;
	}
	public void setCnvCodigo(String cnvCodigo) {
		this.cnvCodigo = cnvCodigo;
	}
	public String getCspSeq() {
		return cspSeq;
	}
	public void setCspSeq(String cspSeq) {
		this.cspSeq = cspSeq;
	}
	public Integer getTempoRealizacaoDias() {
		return tempoRealizacaoDias;
	}
	public void setTempoRealizacaoDias(Integer tempoRealizacaoDias) {
		this.tempoRealizacaoDias = tempoRealizacaoDias;
	}
	public String getUnfSeqComparece() {
		return unfSeqComparece;
	}
	public void setUnfSeqComparece(String unfSeqComparece) {
		this.unfSeqComparece = unfSeqComparece;
	}
	public String getProjeto() {
		return projeto;
	}
	public void setProjeto(String projeto) {
		this.projeto = projeto;
	}
	public String getUnidSolic() {
		return unidSolic;
	}
	public void setUnidSolic(String unidSolic) {
		this.unidSolic = unidSolic;
	}
	public String getRecomendacoes() {
		return recomendacoes;
	}
	public void setRecomendacoes(String recomendacoes) {
		this.recomendacoes = recomendacoes;
	}
	public String getAgendamento() {
		return agendamento;
	}
	public void setAgendamento(String agendamento) {
		this.agendamento = agendamento;
	}
	public String getTextoComparecer() {
		return textoComparecer;
	}
	public void setTextoComparecer(String textoComparecer) {
		this.textoComparecer = textoComparecer;
	}
	public String getMaiorTempoRealizacao() {
		return maiorTempoRealizacao;
	}
	public void setMaiorTempoRealizacao(String maiorTempoRealizacao) {
		this.maiorTempoRealizacao = maiorTempoRealizacao;
	}
	public String getCodigoBarraItem() {
		return codigoBarraItem;
	}
	public void setCodigoBarraItem(String codigoBarraItem) {
		this.codigoBarraItem = codigoBarraItem;
	}
	public String getExamesAgendados() {
		return examesAgendados;
	}
	public void setExamesAgendados(String examesAgendados) {
		this.examesAgendados = examesAgendados;
	}
	public String getLocalizador() {
		return localizador;
	}
	public void setLocalizador(String localizador) {
		this.localizador = localizador;
	}
	public String getInfoGerais() {
		return infoGerais;
	}
	public void setInfoGerais(String infoGerais) {
		this.infoGerais = infoGerais;
	}

	public Short getTempoJejum() {
		return tempoJejum;
	}

	public void setTempoJejum(Short tempoJejum) {
		this.tempoJejum = tempoJejum;
	}

	public String getMaiorTempoJejum() {
		return maiorTempoJejum;
	}

	public void setMaiorTempoJejum(String maiorTempoJejum) {
		this.maiorTempoJejum = maiorTempoJejum;
	}
}
