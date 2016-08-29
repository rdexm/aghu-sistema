package br.gov.mec.aghu.sig.custos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterCentroProducaoController extends ActionController {

	

	private static final String PESQUISAR_CENTRO_PRODUCAO = "pesquisarCentroProducao";

	private static final Log LOG = LogFactory.getLog(ManterCentroProducaoController.class);

	private static final long serialVersionUID = 1503865513174449446L;
	private DominioSituacao situacao;
	private Integer seq;
	private boolean recarregarLista = false;
	private SigCentroProducao centroProducao;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("Begin conversation");
		this.begin(conversation);
	}
	
	public void inicio() {
	 

		// Se Edição
		if (seq != null) {
			this.centroProducao = this.custosSigCadastrosBasicosFacade.obterSigCentroProducao(seq);
		} else {// Se novo
			if (centroProducao == null) {
				this.centroProducao = new SigCentroProducao();
				this.centroProducao.setIndSituacao(DominioSituacao.A);
			}
		}
	
	}

	public String salvar() {
		// Se Edição
		try {
			if (seq != null) {
				this.custosSigCadastrosBasicosFacade.alterarCentroProducao(this.centroProducao);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CENTRO_PRODUCAO", centroProducao.getNome());
			} else {// Se novo
				this.custosSigCadastrosBasicosFacade.inserirCentroProducao(this.centroProducao);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_CENTRO_PRODUCAO", centroProducao.getNome());
			}
			this.setCentroProducao(null);
			this.setRecarregarLista(true);
			seq = null;
			return PESQUISAR_CENTRO_PRODUCAO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String cancelar() {
		this.setCentroProducao(null);
		this.setRecarregarLista(false);
		seq = null;
		return PESQUISAR_CENTRO_PRODUCAO;
	}

	// GETTERS E SETTERS----------------------------------------------

	public SigCentroProducao getCentroProducao() {
		return centroProducao;
	}

	public void setCentroProducao(SigCentroProducao centroProducao) {
		this.centroProducao = centroProducao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public boolean isRecarregarLista() {
		return recarregarLista;
	}

	public void setRecarregarLista(boolean recarregarLista) {
		this.recarregarLista = recarregarLista;
	}
}