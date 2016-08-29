package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.paginator;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.ICidFacade;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;

public class CapitulosCidPaginator extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -3691047433210165701L;

	@EJB
	private ICidFacade cidFacade;

	private Short numero;
	private String descricao;
	private DominioSimNao indExigeCidSecundario;
	private DominioSituacao indSituacao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public CapitulosCidPaginator() {
		// setAtivo(false); // Desabilita a execução da pesquisa logo que entra
		// na tela.
	}

	@Override
	public Long recuperarCount() {
		return cidFacade.obterCapituloCidCount(numero, descricao, indExigeCidSecundario, indSituacao);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AghCapitulosCid> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cidFacade.pesquisar(firstResult, maxResult, numero, descricao, indExigeCidSecundario, indSituacao);
	}

	public Short getNumero() {
		return numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getIndExigeCidSecundario() {
		return indExigeCidSecundario;
	}

	public void setIndExigeCidSecundario(DominioSimNao indExigeCidSecundario) {
		this.indExigeCidSecundario = indExigeCidSecundario;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

}
