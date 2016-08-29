package br.gov.mec.aghu.estoque.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.model.ScoSiasgServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;

public class ScoServicoVO {

	private Integer codigo;
	private ScoGrupoServico grupoServico;
	private String nome;
	private DominioSituacao situacao;
	private Boolean indContrato;
	private String descricao;
	private Date dtDigitacao;
	private Date dtAlteracao;
	private Date dtDesativacao;
	private String observacao;
	private RapServidores servidor;
	private RapServidores servidorDesativado;
	private Integer version;
	private List<ScoServicoSicon> servicoSicon;
	private List<ScoSolicitacaoServico> solicitacoesServico;
	private FsoNaturezaDespesa naturezaDespesa;
	private ScoSiasgServico catSer;

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Boolean getIndContrato() {
		return indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDtDigitacao() {
		return dtDigitacao;
	}

	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	public Date getDtAlteracao() {
		return dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	public Date getDtDesativacao() {
		return dtDesativacao;
	}

	public void setDtDesativacao(Date dtDesativacao) {
		this.dtDesativacao = dtDesativacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidorDesativado() {
		return servidorDesativado;
	}

	public void setServidorDesativado(RapServidores servidorDesativado) {
		this.servidorDesativado = servidorDesativado;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<ScoServicoSicon> getServicoSicon() {
		return servicoSicon;
	}

	public void setServicoSicon(List<ScoServicoSicon> servicoSicon) {
		this.servicoSicon = servicoSicon;
	}

	public List<ScoSolicitacaoServico> getSolicitacoesServico() {
		return solicitacoesServico;
	}

	public void setSolicitacoesServico(List<ScoSolicitacaoServico> solicitacoesServico) {
		this.solicitacoesServico = solicitacoesServico;
	}

	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	public ScoSiasgServico getCatSer() {
		return catSer;
	}

	public void setCatSer(ScoSiasgServico catSer) {
		this.catSer = catSer;
	}

}
