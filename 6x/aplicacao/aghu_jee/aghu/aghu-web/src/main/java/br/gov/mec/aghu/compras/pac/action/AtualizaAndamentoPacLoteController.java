package br.gov.mec.aghu.compras.pac.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.AlteracaoAndamentoPacVO;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Controller responsável pela atualização em lote da localização de um oumais
 * PAC's.
 */
public class AtualizaAndamentoPacLoteController extends ActionController {

	private static final Log LOG = LogFactory.getLog(AtualizaAndamentoPacLoteController.class);
	private static final long serialVersionUID = 4334485838625487183L;

	@EJB
	private IPacFacade pacFacade;

	/** Local a ser alterado nos PAC's. */
	private ScoLocalizacaoProcesso local;

	/** ID do PAC a ter local alterado. */
	private Integer pacId;

	/** Locais de PAC's a serem alterados. */
	private List<AlteracaoAndamentoPacVO> andamentosPacs;

	/** Flag responsável por indicar se lote foi gravado. */
	private boolean gravado;

	/**
	 * Flag responsável por suspender o método limpar.
	 * 
	 * Usada quando é necessário limpar apenas o filtro ou exibir as mensagens
	 * de gravação dos andamentos dos PAC's.
	 */
	private Boolean suspendeLimpar;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@PostConstruct
	protected void inicializar() {
		andamentosPacs = new ArrayList<AlteracaoAndamentoPacVO>();
		this.begin(conversation);
	}

	/**
	 * Pesquisa localizações de processo ativas.
	 * 
	 * @param filter
	 *            Filtro (código ou descrição).
	 * @return Localizações
	 */
	public List<ScoLocalizacaoProcesso> pesquisarLocalizacoes(String filter) {
		return pacFacade.pesquisarScoLocalizacaoProcesso(filter, 100);
	}

	/**
	 * Pesquisa PAC's por número ou descrição.
	 * 
	 * @param filter
	 *            Filtro por número ou descrição.
	 * @return PAC's
	 */
	public List<ScoLicitacao> pesquisarPacs(Object filter) {
		return pacFacade.pesquisarLicitacoesPorNumeroDescricao(filter);
	}

	/**
	 * Limpa o filtro de pesquisa e a grade digitada.
	 */
	public void limpar() {
		if (Boolean.TRUE.equals(suspendeLimpar)) {
			suspendeLimpar = false;
			return;
		} else {
			local = null;
			pacId = null;
			limparAndamentosPacs();
		}
	}

	/**
	 * Limpa a grade digitada para nova digitação e mantém o filtro de pesquisa.
	 */
	public void limparAndamentosPacs() {
		andamentosPacs = new ArrayList<AlteracaoAndamentoPacVO>();
		gravado = false;
		suspendeLimpar = true;
	}

	/**
	 * Adiciona um PAC com local a ser alterado.
	 */
	public void addAndamentoPac() {
		if (pacId == null || this.gravado) {
			return;
		}

		AlteracaoAndamentoPacVO vo = new AlteracaoAndamentoPacVO();
		vo.setPacId(pacId);
		vo.setLocal(local);
		vo.setMostraAlerta(Boolean.FALSE);		
		andamentosPacs.add(vo);
		pacId = null;
	}

	private Boolean verificarAlerta(String msg) {
		return (msg.equals(getBundle().getString("MENSAGEM_LICITACAO_NAO_PERTENCE_AO_CADASTRO")) || msg.equals(getBundle()
				.getString("MENSAGEM_PROCESSO_JA_SE_ENCONTRA_NO_LOCAL_INFORMADO")));
	}

	/**
	 * Grava alteração de local dos PAC's.
	 */
	public void gravar() {
		for (AlteracaoAndamentoPacVO vo : andamentosPacs) {
			ScoLicitacao pac = pacFacade.obterLicitacao(vo.getPacId());

			if (pac != null) {
				ScoAndamentoProcessoCompra andamento = new ScoAndamentoProcessoCompra();
				andamento.setLicitacao(pac);
				andamento.setLocalizacaoProcesso(vo.getLocal());
				andamento.setServidor(servidorLogadoFacade.obterServidorLogado());
				andamento.setDtEntrada(new Date());

				try {
					pacFacade.incluir(andamento);
					vo.setSituacao(getBundle().getString("ANDAMENTO_GRAVADO_COM_SUCESSO"));
				} catch (ApplicationBusinessException e) {
					vo.setSituacao(getBundle().getString(e.getMessage()));
				}
			} else {
				vo.setSituacao(getBundle().getString("MENSAGEM_LICITACAO_NAO_PERTENCE_AO_CADASTRO"));
			}

			vo.setMostraAlerta(this.verificarAlerta(vo.getSituacao()));

			LOG.info(String.format("Situação da alteração da localização do PAC %d: %s", vo.getPacId(), vo.getSituacao()));
		}

		gravado = true;
		suspendeLimpar = true;
	}

	// Getters/Setters

	public boolean isGravado() {
		return gravado;
	}

	public List<AlteracaoAndamentoPacVO> getAndamentosPacs() {
		return andamentosPacs;
	}

	public ScoLocalizacaoProcesso getLocal() {
		return local;
	}

	public void setLocal(ScoLocalizacaoProcesso local) {
		this.local = local;
	}

	public Integer getPacId() {
		return pacId;
	}

	public void setPacId(Integer pacId) {
		this.pacId = pacId;
	}
}