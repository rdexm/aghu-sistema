package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigComunicacaoEventos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterComunicacaoEventoController extends ActionController {

	private static final String PESQUISAR_COMUNICACAO_EVENTO = "pesquisarComunicacaoEvento";

	private static final Log LOG = LogFactory.getLog(ManterComunicacaoEventoController.class);

	private static final long serialVersionUID = -5924562909324182978L;

	private Integer seqComunicacaoEvento;
	private boolean recarregarLista = false;
	private SigComunicacaoEventos sigComunicacaoEventos;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if (this.getSeqComunicacaoEvento() != null) {
			this.setSigComunicacaoEventos(this.custosSigCadastrosBasicosFacade.obterComunicacaoEvento(this.getSeqComunicacaoEvento()));
		} else {
			if (this.getSigComunicacaoEventos() == null) {
				this.setSigComunicacaoEventos(new SigComunicacaoEventos());
				this.getSigComunicacaoEventos().setSituacao(DominioSituacao.A);
			}
		}
	
	}

	public String gravar() {
		try {
			boolean alteracao = this.getSigComunicacaoEventos().getSeq() != null;
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			this.getSigComunicacaoEventos().setServidorCadastro(servidorLogado);
			this.custosSigCadastrosBasicosFacade.persistComunicacaoEvento(this.getSigComunicacaoEventos());
			if (alteracao) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_COMUNICACAO_EVENTO",
						this.getSigComunicacaoEventos().getTipoEvento().getDescricao(),
						this.getSigComunicacaoEventos().getServidor().getPessoaFisica().getNome());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_COMUNICACAO_EVENTO",
						this.getSigComunicacaoEventos().getTipoEvento().getDescricao(),
						this.getSigComunicacaoEventos().getServidor().getPessoaFisica().getNome());
			}

			this.setSigComunicacaoEventos(null);
			this.setSeqComunicacaoEvento(null);
			this.setRecarregarLista(true);
			return PESQUISAR_COMUNICACAO_EVENTO;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar() {
		this.setSigComunicacaoEventos(null);
		this.setRecarregarLista(true);
		this.setSeqComunicacaoEvento(null);
		return PESQUISAR_COMUNICACAO_EVENTO;
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		List<FccCentroCustos> listaResultado = new ArrayList<FccCentroCustos>();
		listaResultado = this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
		return  listaResultado;
	}

	public void limparCentroCusto() {
		this.getSigComunicacaoEventos().setFccCentroCustos(null);
	}

	public List<RapServidores> pesquisarServidor(String paramPesquisa) throws BaseException {
		List<RapServidores> listaResultado = new ArrayList<RapServidores>();
		listaResultado = this.registroColaboradorFacade.pesquisarServidorVinculoAtivoEProgramadoAtual(paramPesquisa);
		return  listaResultado;
	}

	public void limparServidor() {
		this.getSigComunicacaoEventos().setServidor(null);
	}

	public Integer getSeqComunicacaoEvento() {
		return seqComunicacaoEvento;
	}

	public void setSeqComunicacaoEvento(Integer seqComunicacaoEvento) {
		this.seqComunicacaoEvento = seqComunicacaoEvento;
	}

	public SigComunicacaoEventos getSigComunicacaoEventos() {
		return sigComunicacaoEventos;
	}

	public void setSigComunicacaoEventos(SigComunicacaoEventos sigComunicacaoEventos) {
		this.sigComunicacaoEventos = sigComunicacaoEventos;
	}

	public boolean isRecarregarLista() {
		return recarregarLista;
	}

	public void setRecarregarLista(boolean recarregarLista) {
		this.recarregarLista = recarregarLista;
	}

}
