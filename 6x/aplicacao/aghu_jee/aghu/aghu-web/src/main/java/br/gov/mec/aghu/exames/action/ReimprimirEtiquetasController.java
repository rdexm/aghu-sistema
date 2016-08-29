package br.gov.mec.aghu.exames.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.view.VAelArcoSolicitacaoAghu;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ReimprimirEtiquetasController extends ActionController {

	private static final long serialVersionUID = -6364759035909512468L;
	private static final Log LOG = LogFactory.getLog(ReimprimirEtiquetasController.class);

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private AelUnidExecUsuario usuarioUnidadeExecutora;

	private VAelArcoSolicitacaoAghu solicitacaoExame;
	
	private Long valorEntradaCampoSolicitacao;
	
	private Integer amostraSoeSeqSelecionada;
	private Short amostraSeqpSelecionada;
	
	private List<AelAmostrasVO> listaAmostras;
	private List<AelAmostraItemExames> listaExamesAmostras;
	
	/*
	 * Essa instancia armazena a descricao do material de analise da amostra
	 * logo reutilizada durante a listagem de exames amostra ou items de exame
	 * amostra. Vide coluna: Exames + "/" + Amostra no XHTML
	 */
	private String amostraMaterialAnalise;
	
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private boolean pesquisar;
	
	private String mensagemConfirmacaoImpressaoEtiquetas;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		// Obtem o usuario da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora=null;
		}

		// Resgata a unidade executora associada ao usuario
		if (this.usuarioUnidadeExecutora != null) {
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}

		if (this.valorEntradaCampoSolicitacao != null && this.isPesquisar()) {
			this.pesquisar();
			this.pesquisar = false;
		}
	
	}

	public void reimprimirAmostra(String nomeImpressora) throws ApplicationBusinessException, UnknownHostException {
		try {
			String nomeMicro = (nomeImpressora == null) ? getEnderecoRedeHostRemoto() : null;
			nomeImpressora = this.solicitacaoExameFacade.reimprimirAmostra(this.unidadeExecutora,
					this.amostraSoeSeqSelecionada, this.amostraSeqpSelecionada, nomeMicro, nomeImpressora);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ETIQUETAS_IMPRESSAS_SUCESSO_REIMPRIMIR_ETIQUETA",
					nomeImpressora);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			javax.faces.application.FacesMessage.Severity severity = WebUtil.getSeverity(Severity.ERROR);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			context.addMessage("Messages", new FacesMessage(severity, e.getMessage(), e.getMessage()));
		}
	}
	
	public void reimprimirTodasAmostras() {
		try {
			String nomeImpressora = this.solicitacaoExameFacade.reimprimirAmostra(this.unidadeExecutora, this.amostraSoeSeqSelecionada, null, getEnderecoRedeHostRemoto(), null);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ETIQUETAS_IMPRESSAS_SUCESSO_REIMPRIMIR_ETIQUETA", nomeImpressora);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			javax.faces.application.FacesMessage.Severity severity = WebUtil.getSeverity(Severity.ERROR);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			context.addMessage("Messages", new FacesMessage(severity, e.getMessage(), e.getMessage()));
		}
	}

	public void reimprimirAmostra() throws ApplicationBusinessException, UnknownHostException {
        reimprimirAmostra(null);
    }

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.getUnidadeExecutora());
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void pesquisar() {

		if (this.getValorEntradaCampoSolicitacao() == null || this.getValorEntradaCampoSolicitacao() <= 0) {
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_SOLICITACAO_AMOSTRA_OBRIGATORIA_REIMPRIMIR_ETIQUETA");
		} else {
			this.limparDadosSomenteLeituraSolicitacaoExame();
			this.setListaAmostras(null);
			this.setListaExamesAmostras(null);

			this.setAmostraSoeSeqSelecionada(this.getValorEntradaCampoSolicitacao().intValue());

			// Resgata a solicitacao de exame atraves da id
			this.setSolicitacaoExame(this.solicitacaoExameFacade.obterVAelArcoSolicitacaoAghuPeloId(this.getAmostraSoeSeqSelecionada()));

			if (this.getSolicitacaoExame() == null) {
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUMA_SOLICITACAO_EXAME_ENCONTRADA_REIMPRIMIR_ETIQUETA", this.getAmostraSoeSeqSelecionada().toString());
			} else {
				// Pesquisa amostras
				try {
					this.setListaAmostras(this.examesFacade.buscarAmostrasVOPorSolicitacaoExame(this.getSolicitacaoExame().getSeq(), null));
				} catch (final BaseException e) {
					apresentarExcecaoNegocio(e);
				}

				// Realiza a pesquisa automatica dos itens de exames amostras
				if (this.getListaAmostras() != null && !this.getListaAmostras().isEmpty()) {

					// Obtem o primeiro item da lista...
					AelAmostrasVO itemSelecionadoListaAmostras = null;
					/*
					 * Caso nenhum item da lista de amostras esteja selecionado: A
					 * consulta de items de amostra de exame tera como criterio o
					 * primeiro item da lista
					 */
					if (this.getAmostraSeqpSelecionada() == null) {
						itemSelecionadoListaAmostras = this.getListaAmostras().get(0);
					} else {
						/*
						 * Caso algum item da lista de amostras esteja selecionado:
						 * A consulta de items de amostra de exame tera com criterio
						 * o item selecionado
						 */
						for (final AelAmostrasVO item : getListaAmostras()) {
							if (item.getSeqp().equals(this.getAmostraSeqpSelecionada())) {
								itemSelecionadoListaAmostras = item;
								break;
							}
						}
					}

					// Pesquisa de exames amostra (items de amostra de exame)
					this.pesquisarExamesAmostra(itemSelecionadoListaAmostras);
				} else {
					this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_SOLICITACAO_EXAME_SEM_AMOSTRAS_REIMPRIMIR_ETIQUETA", this.getAmostraSoeSeqSelecionada().toString());
				}
			}
		}
	}
	
	public void limparPesquisa() {

		if (this.usuarioUnidadeExecutora != null) {
			// Reseta a unidade executora associada ao usuario
			this.setUnidadeExecutora(this.usuarioUnidadeExecutora.getUnfSeq());
		}

		// Limpa campos READY ONLY exbidos no fieldset de solicitacao de exame
		this.limparDadosSomenteLeituraSolicitacaoExame();
		this.setValorEntradaCampoSolicitacao(null);
		this.setListaAmostras(null);
		this.setAmostraSeqpSelecionada(null);

	}

	private void limparDadosSomenteLeituraSolicitacaoExame() {
//		this.setValorEntradaCampoSolicitacao(null);
		this.setAmostraSeqpSelecionada(null);
	}
	
	/**
	 * Pesquisa de exames amostra (items de amostra de exame)
	 */
	public void pesquisarExamesAmostra(final AelAmostrasVO vo) {

		this.setAmostraSoeSeqSelecionada(vo.getSoeSeq());
		this.setAmostraSeqpSelecionada(vo.getSeqp());

		this.setAmostraMaterialAnalise(vo.getMaterialAnalise());
		this.setListaExamesAmostras(this.examesFacade.buscarAelAmostraItemExamesPorAmostra(vo.getSoeSeq(), vo.getSeqp().intValue()));
	}
	
	public void calcularNumeroImpressoes(final Short seqp) {
		this.mensagemConfirmacaoImpressaoEtiquetas = new StringBuffer("A etiqueta da amostra ").append(this.amostraSoeSeqSelecionada).append(seqp)
				.append(", ser\u00E1 impressa. Confirmar a impress\u00E3o da etiqueta?").toString();
	}

	public void mensagemImpressoes() {
		this.mensagemConfirmacaoImpressaoEtiquetas = new StringBuffer("Todas as etiquetas da solicita\u00E7\u00E3o ").append(this.amostraSoeSeqSelecionada)
				.append(", ser\u00E3o impressas. Confirmar a impress\u00E3o das etiquetas?").toString();
	}

	public boolean isPesquisar() {
		return pesquisar;
	}

	public String voltar() {
		return "voltar";
	}

	/*
	 * Metodos para Suggestion Box
	 */

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(final String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}
	
	// ### GETs e SETs ###
	
	public void setUnidadeExecutora(final AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setSolicitacaoExame(final VAelArcoSolicitacaoAghu solicitacaoExame) {
		this.solicitacaoExame = solicitacaoExame;
	}

	public VAelArcoSolicitacaoAghu getSolicitacaoExame() {
		return solicitacaoExame;
	}

	public void setValorEntradaCampoSolicitacao(final Long valorEntradaCampoSolicitacao) {
		this.valorEntradaCampoSolicitacao = valorEntradaCampoSolicitacao;
	}

	public Long getValorEntradaCampoSolicitacao() {
		return valorEntradaCampoSolicitacao;
	}

	public void setAmostraSoeSeqSelecionada(final Integer amostraSoeSeqSelecionada) {
		this.amostraSoeSeqSelecionada = amostraSoeSeqSelecionada;
	}

	public Integer getAmostraSoeSeqSelecionada() {
		return amostraSoeSeqSelecionada;
	}

	public void setAmostraSeqpSelecionada(final Short amostraSeqpSelecionada) {
		this.amostraSeqpSelecionada = amostraSeqpSelecionada;
	}

	public Short getAmostraSeqpSelecionada() {
		return amostraSeqpSelecionada;
	}

	public void setListaAmostras(final List<AelAmostrasVO> listaAmostras) {
		this.listaAmostras = listaAmostras;
	}

	public List<AelAmostrasVO> getListaAmostras() {
		return listaAmostras;
	}

	public void setListaExamesAmostras(final List<AelAmostraItemExames> listaExamesAmostras) {
		this.listaExamesAmostras = listaExamesAmostras;
	}

	public List<AelAmostraItemExames> getListaExamesAmostras() {
		return listaExamesAmostras;
	}

	public void setAmostraMaterialAnalise(final String amostraMaterialAnalise) {
		this.amostraMaterialAnalise = amostraMaterialAnalise;
	}

	public String getAmostraMaterialAnalise() {
		return amostraMaterialAnalise;
	}

	public void setMensagemConfirmacaoImpressaoEtiquetas(String mensagemConfirmacaoImpressaoEtiquetas) {
		this.mensagemConfirmacaoImpressaoEtiquetas = mensagemConfirmacaoImpressaoEtiquetas;
	}

	public String getMensagemConfirmacaoImpressaoEtiquetas() {
		return mensagemConfirmacaoImpressaoEtiquetas;
	}


}
