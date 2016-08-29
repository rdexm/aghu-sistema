package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;

public class ParcelasAFPendEntVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;

	private Integer numeroAF;
	private Short complemento;
	private Integer numeroAFP;
	private Integer numeroAFN;
	private String numeroEditalAno;
	private DominioSituacaoAutorizacaoFornecimento indSituacao;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private String descricaoModalidadeEmp;
	private ScoFornecedor fornecedor;
	private String enviado;
	private String portalForn;
	private ScoContatoFornecedor contato;
	private Date dtEnvioForn;
	private ScoLicitacao licitacao;
	private RapPessoasFisicas pessoaGestor;
	private String razaoSocialNumeroFornecedor;
	private Date dtPublicacao;
	private String publicacaoAFP;
	private Boolean ativo;
	private String situacaoEntrega;
	private String corSituacaoEntrega;
	private String hintSituacaoEntrega;
	private ScoMaterial material;
	private ScoGrupoMaterial grupoMaterial;
	private Integer indiceLista;
	private boolean atrasado;
	private Date periodoEntregaInicio;
	private Date periodoEntregaFim;
	private DominioSimNao entregaAtrasada;
	private DominioSimNao empenhada;
	private DominioSimNao recebido;
	private RapServidores gestor;
	private Integer afeAfn;
	private boolean botaoParcelas;
	
	public boolean isAtrasado() {
		return atrasado;
	}
	public void setAtrasado(boolean atrasado) {
		this.atrasado = atrasado;
	}
	public Integer getNumeroAF() {
		return numeroAF;
	}
	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}
	public Short getComplemento() {
		return complemento;
	}
	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}
	public Integer getNumeroAFP() {
		return numeroAFP;
	}
	public void setNumeroAFP(Integer numeroAFP) {
		this.numeroAFP = numeroAFP;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}
	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public String getEnviado() {
		return enviado;
	}
	public void setEnviado(String enviado) {
		this.enviado = enviado;
	}
	public String getPortalForn() {
		return portalForn;
	}
	public void setPortalForn(String portalForn) {
		this.portalForn = portalForn;
	}
	public String getSituacaoEntrega() {
		return situacaoEntrega;
	}
	public void setSituacaoEntrega(String situacaoEntrega) {
		this.situacaoEntrega = situacaoEntrega;
	}
	public void setNumeroAFN(Integer numeroAFN) {
		this.numeroAFN = numeroAFN;
	}
	public Integer getNumeroAFN() {
		return numeroAFN;
	}
	public void setContato(ScoContatoFornecedor contato) {
		this.contato = contato;
	}
	public ScoContatoFornecedor getContato() {
		return contato;
	}
	public void setNumeroEditalAno(String numeroEditalAno) {
		this.numeroEditalAno = numeroEditalAno;
	}
	public String getNumeroEditalAno() {
		return numeroEditalAno;
	}
	
	public void setIndSituacao(DominioSituacaoAutorizacaoFornecimento indSituacao) {
		this.indSituacao = indSituacao;
	}
	public DominioSituacaoAutorizacaoFornecimento getIndSituacao() {
		return indSituacao;
	}
	public void setDtEnvioForn(Date dtEnvioForn) {
		this.dtEnvioForn = dtEnvioForn;
	}
	public Date getDtEnvioForn() {
		return dtEnvioForn;
	}
	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}
	public void setPessoaGestor(RapPessoasFisicas pessoaGestor) {
		this.pessoaGestor = pessoaGestor;
	}
	public RapPessoasFisicas getPessoaGestor() {
		return pessoaGestor;
	}
	
	public void setDescricaoModalidadeEmp(String descricaoModalidadeEmp) {
		this.descricaoModalidadeEmp = descricaoModalidadeEmp;
	}
	public String getDescricaoModalidadeEmp() {
		return descricaoModalidadeEmp;
	}
	public void setDtPublicacao(Date dtPublicacao) {
		this.dtPublicacao = dtPublicacao;
	}
	public Date getDtPublicacao() {
		return dtPublicacao;
	}
	public void setPublicacaoAFP(String publicacaoAFP) {
		this.publicacaoAFP = publicacaoAFP;
	}
	public String getPublicacaoAFP() {
		return publicacaoAFP;
	}
	
	public void setCorSituacaoEntrega(String corSituacaoEntrega) {
		this.corSituacaoEntrega = corSituacaoEntrega;
	}
	public String getCorSituacaoEntrega() {
		return corSituacaoEntrega;
	}
	public void setHintSituacaoEntrega(String hintSituacaoEntrega) {
		this.hintSituacaoEntrega = hintSituacaoEntrega;
	}
	public String getHintSituacaoEntrega() {
		return hintSituacaoEntrega;
	}
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public ScoMaterial getMaterial() {
		return material;
	}
	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}
	public void setIndiceLista(Integer indiceLista) {
		this.indiceLista = indiceLista;
	}
	public Integer getIndiceLista() {
		return indiceLista;
	}
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	public Boolean getAtivo() {
		return ativo;
	}
	
	public String getCorPadrao() {
		return "#EEE8AA";
	}
	public void setGestor(RapServidores gestor) {
		this.gestor = gestor;
	}
	public RapServidores getGestor() {
		return gestor;
	}
	public void setAfeAfn(Integer afeAfn) {
		this.afeAfn = afeAfn;
	}
	public Integer getAfeAfn() {
		return afeAfn;
	}
	public void setRazaoSocialNumeroFornecedor(
			String razaoSocialNumeroFornecedor) {
		this.razaoSocialNumeroFornecedor = razaoSocialNumeroFornecedor;
	}
	public String getRazaoSocialNumeroFornecedor() {
		return razaoSocialNumeroFornecedor;
	}
	
	public boolean isBotaoParcelas() {
		return botaoParcelas;
	}
	public void setBotaoParcelas(boolean botaoParcelas) {
		this.botaoParcelas = botaoParcelas;
	}
	public Date getPeriodoEntregaInicio() {
		return periodoEntregaInicio;
	}
	public void setPeriodoEntregaInicio(Date periodoEntregaInicio) {
		this.periodoEntregaInicio = periodoEntregaInicio;
	}
	public Date getPeriodoEntregaFim() {
		return periodoEntregaFim;
	}
	public void setPeriodoEntregaFim(Date periodoEntregaFim) {
		this.periodoEntregaFim = periodoEntregaFim;
	}
	public DominioSimNao getEntregaAtrasada() {
		return entregaAtrasada;
	}
	public void setEntregaAtrasada(DominioSimNao entregaAtrasada) {
		this.entregaAtrasada = entregaAtrasada;
	}
	public DominioSimNao getEmpenhada() {
		return empenhada;
	}
	public void setEmpenhada(DominioSimNao empenhada) {
		this.empenhada = empenhada;
	}
	public DominioSimNao getRecebido() {
		return recebido;
	}
	public void setRecebido(DominioSimNao recebido) {
		this.recebido = recebido;
	}
}
