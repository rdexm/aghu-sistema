package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoJustificativaPreco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class JustificativasPrecoContratadoCadastroController extends ActionController {

	private static final long serialVersionUID = 1237194890613149890L;

	private ScoJustificativaPreco justificativa = new ScoJustificativaPreco();
	private Boolean situacaoAtivo;

	private Short codigo;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

	 

		if (this.getCodigo() != null) {
			this.justificativa = this.comprasCadastrosBasicosFacade.obterJustificativaPorCodigo(this.getCodigo());
			avaliaSituacao();
		} else {
			this.situacaoAtivo = true;
		}
	
	}
	

	private void avaliaSituacao() {
		if (null != this.justificativa.getIndSituacao()) {
			this.situacaoAtivo = DominioSituacao.A.equals(this.justificativa.getIndSituacao()) ? true : false;
		}
	}

	public String salvar() {
		this.justificativa.setIndSituacao(DominioSituacao.I);
		if (this.situacaoAtivo) {
			this.justificativa.setIndSituacao(DominioSituacao.A);
		}
		String mensagemSucesso = "MENSAGEM_SUCESSO_JUSTIFICATIVA_PRECO";
		if (this.justificativa.getCodigo() != null) {
			mensagemSucesso = "MENSAGEM_SUCESSO_ALTERACAO_JUSTIFICATIVA_PRECO";
		}
		this.comprasCadastrosBasicosFacade.persistirNivelJustificativaPreco(this.justificativa);

		apresentarMsgNegocio(Severity.INFO, mensagemSucesso);

		justificativa = new ScoJustificativaPreco();
		this.situacaoAtivo = null;

		return "justificativasPrecoContratado";
	}

	public String voltar() {
		justificativa = new ScoJustificativaPreco();
		this.situacaoAtivo = null;
		return "justificativasPrecoContratado";
	}

	public void setComprasCadastrosBasicosFacade(IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setJustificativa(ScoJustificativaPreco justificativa) {
		this.justificativa = justificativa;
	}

	public ScoJustificativaPreco getJustificativa() {
		return justificativa;
	}

	public void setSituacaoAtivo(Boolean situacaoAtivo) {
		this.situacaoAtivo = situacaoAtivo;
	}

	public Boolean getSituacaoAtivo() {
		return situacaoAtivo;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
}
