package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.vo.HistoricoAlteracoesGrupoAlcadaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.dominio.DominioTipoObrigatoriedadeOpms;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaGruposAlcadaPaginatorController extends ActionController {

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	 //this.iniciar();
	}

	private static final Log LOG = LogFactory.getLog(PesquisaGruposAlcadaPaginatorController.class);
	
	private static final long serialVersionUID = -4615760901841389986L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	

	private static final String MENSAGEM_SUCESSO_ALTERACAO_GRUPO_ALCADA_AVAL_OPME = "MENSAGEM_SUCESSO_ALTERACAO_GRUPO_ALCADA_AVAL_OPME";
	
	private static final String CADASTRO_GRUPO_ALCADA ="blococirurgico-iniciarInclusao";
	
	private static final String INICIAR_EDICAO_GRUPO_ALCADA = "blococirurgico-iniciarInclusao";
	
	private static final String PESQUISAR_NIVEIS_ALCADA ="blococirurgico-pesquisaNiveisAlcadaAprovacao";

	private DominioTipoObrigatoriedadeOpms tipoObrigatoriedadeOpms;
	
	private MbcGrupoAlcadaAvalOpms grupoAlcada = new MbcGrupoAlcadaAvalOpms();
	
	private List<HistoricoAlteracoesGrupoAlcadaVO> historicoGrupoAlcada;
	
	private List<MbcGrupoAlcadaAvalOpms> listaPesquisada;
	
	// Campos de filtro
	private DominioTipoConvenioOpms tipoConvenioOpms;
	
	private AghEspecialidades aghEspecialidades;

	private DominioSituacao dominioSituacao;

	private MbcGrupoAlcadaAvalOpms itemExclusao;
	
	private MbcGrupoAlcadaAvalOpms grupoAtivo = new MbcGrupoAlcadaAvalOpms();

	
	// Parametros Recebidos de view (pesquisaGruposAlcadaPaginatorController)
	
	private Short grupoAlcadaSeq;
	
	private String tipoConvenioFiltro;
	
	private Short especialidadeSeqFiltro;
	
	private Short versaoFiltro;
	
	private String situacaoFiltro;
	
		/*
		grupoAlcada.codigoGrupo
		
		grupoAlcada.tipoConvenio
		
		grupoAlcada.situacao
		
		grupoAlcada.aghEspecialidade.nomeEspecialidade
		
		grupoAlcada.aghEspecialidade.seq
		
		grupoAlcada.versao
		
		*/	
	//
	
	private boolean retornoDeOutraPagina = true;
	
	private Boolean atualizaPesquisa = true;
	
	private Short versao;
	
	private Short especialidadeSeq;
	
	private boolean mostrarBotaoNovo;
	
	private boolean mostrarMensagemRegistroNaoLocalizado;
	
	private String situacao;
	
	private Boolean exibeConfirmacao;
	
	private String tipoObrigatoriedadeFiltro;
	
	private Short grupoAtivoSeq;
	
	public void iniciar() {
		if (retornoDeOutraPagina) {
			if (atualizaPesquisa) {
				if (grupoAlcadaSeq != null) {
					MbcGrupoAlcadaAvalOpms grupo = blocoCirurgicoCadastroApoioFacade
							.buscaGrupoAlcadaPorSequencial(grupoAlcadaSeq);
					this.aghEspecialidades = grupo.getAghEspecialidades();
					this.tipoConvenioOpms = grupo.getTipoConvenio();
					this.tipoObrigatoriedadeOpms = grupo
							.getTipoObrigatoriedade();
					this.versao = grupo.getVersao();
					this.dominioSituacao = grupo.getSituacao();
				} else if (this.especialidadeSeq != null) {
					this.aghEspecialidades = blocoCirurgicoFacade
							.buscaAghEspecialidadesPorSeq(this.especialidadeSeq);
				}
			}
			pesquisar();
		} else {
			limparPesquisa();
		}
		
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.listaPesquisada = recuperarListaPaginada();
		this.mostrarBotaoNovo = Boolean.TRUE;
		this.mostrarMensagemRegistroNaoLocalizado = (this.listaPesquisada != null && this.listaPesquisada.isEmpty());
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.tipoConvenioOpms = null;
		this.tipoObrigatoriedadeOpms = null;
		this.aghEspecialidades = null;
		this.versao = null;
		this.situacao = null;
		this.dominioSituacao = null;
		this.listaPesquisada = null;
		this.aghEspecialidades = null;
		this.mostrarBotaoNovo = Boolean.FALSE;
		this.mostrarMensagemRegistroNaoLocalizado = Boolean.FALSE;
		this.exibeConfirmacao = Boolean.FALSE;
	}

	public String redirecionaNiveisAlcada(MbcGrupoAlcadaAvalOpms grupoAlcada) {
		this.grupoAlcada = grupoAlcada;
		this.tipoConvenioFiltro = this.tipoConvenioOpms != null ? this.tipoConvenioOpms.name() : null;
		this.especialidadeSeqFiltro = this.aghEspecialidades != null ? this.aghEspecialidades.getSeq() : null;
		this.tipoObrigatoriedadeFiltro = this.tipoObrigatoriedadeOpms != null ? this.tipoObrigatoriedadeOpms.name() : null;
		this.versaoFiltro = this.versao;
		this.situacaoFiltro = this.dominioSituacao != null ? this.dominioSituacao.name() : null;
		
		return PESQUISAR_NIVEIS_ALCADA;
	}
	/* Parâmetros Necessários enviados XHTML: grupoAlcada.seq, limpaCampos = false */
	
	public String redirecionaManterGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcada) {
//		this.grupoAlcada = grupoAlcada;
//		this.grupoAtivoSeq = grupoAlcada.getSeq();
//		this.tipoConvenioFiltro = this.tipoConvenioOpms != null ? this.tipoConvenioOpms.name() : null;
//		this.especialidadeSeqFiltro = this.aghEspecialidades != null ? this.aghEspecialidades.getSeq() : null;
//		this.tipoObrigatoriedadeFiltro = this.tipoObrigatoriedadeOpms != null ? this.tipoObrigatoriedadeOpms.name() : null;
//		this.versaoFiltro = this.versao;
//		this.situacaoFiltro = this.dominioSituacao != null ? this.dominioSituacao.name() : null;
		

		//verificaAghEspecialidades();

	 return INICIAR_EDICAO_GRUPO_ALCADA;
	}

//	private void verificaAghEspecialidades() {
//		if (grupoAlcada.getAghEspecialidades() != null) {
//			this.especialidadeSeq = grupoAlcada.getAghEspecialidades()
//					.getEpcSeq();
//		}
//	}

	public void ativar() {
		try {
			RapServidores rapServidores = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(
							this.obterLoginUsuarioLogado(), new Date());

			this.grupoAtivo = blocoCirurgicoCadastroApoioFacade
					.buscarGrupoAlcadaAtivo(grupoAlcada.getTipoConvenio(),
							grupoAlcada.getAghEspecialidades());

			blocoCirurgicoCadastroApoioFacade.alterarSituacao(this.grupoAtivo,
					rapServidores);

			blocoCirurgicoCadastroApoioFacade.alterarSituacao(grupoAlcada,
					rapServidores);

			this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_ALTERACAO_GRUPO_ALCADA_AVAL_OPME );

			listaPesquisada = recuperarListaPaginada();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void ativar(MbcGrupoAlcadaAvalOpms grupoAlcada) {
		try {

			RapServidores rapServidores = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(
							this.obterLoginUsuarioLogado(), new Date());

			blocoCirurgicoCadastroApoioFacade.alterarSituacao(grupoAlcada,
					rapServidores);

			this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_ALTERACAO_GRUPO_ALCADA_AVAL_OPME);

			listaPesquisada = recuperarListaPaginada();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void desativar(MbcGrupoAlcadaAvalOpms grupoAlcada) {
		try {
			RapServidores rapServidores = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(
							this.obterLoginUsuarioLogado(), new Date());

			blocoCirurgicoCadastroApoioFacade.alterarSituacao(grupoAlcada,
					rapServidores);

			this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_ALTERACAO_GRUPO_ALCADA_AVAL_OPME);

			listaPesquisada = recuperarListaPaginada();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void verificarGrupoAtivo(MbcGrupoAlcadaAvalOpms grupoAlcada) {

		this.grupoAlcada = grupoAlcada;

		this.grupoAtivo = blocoCirurgicoCadastroApoioFacade
				.buscarGrupoAlcadaAtivo(grupoAlcada.getTipoConvenio(),
						grupoAlcada.getAghEspecialidades());
		if (grupoAtivo != null) {
			this.exibeConfirmacao = Boolean.TRUE;
		}else {
			ativar(grupoAlcada);
			this.exibeConfirmacao = Boolean.FALSE;
		}
	}

	/**
	 * Obtem especialidade ativa executora de cirurgias
	 */
	public List<AghEspecialidades> obterEspecialidades(String filtro) {
		return this.returnSGWithCount(this.aghuFacade
				.pesquisarEspecialidadesSemEspSeq((String) filtro),
														   obterEspecialidadesCount(filtro));
	}

	public long obterEspecialidadesCount(Object filtro) {
		return this.aghuFacade.pesquisarEspecialidadesSemEspSeqCount((String) filtro);
	}

	public void carregaHistoricoAlteracoesGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms){
		this.historicoGrupoAlcada = blocoCirurgicoCadastroApoioFacade.buscarHistoricoGrupoAlcada(grupoAlcadaAvalOpms.getSeq());
	}
	
	public void selecionaLinha(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) {
		this.itemExclusao = grupoAlcadaAvalOpms;
	}
	
	public void excluir() {
		try {
			blocoCirurgicoFacade.removerMbcGrupoAlcada(itemExclusao);
			this.listaPesquisada = recuperarListaPaginada();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_GRUPO_ALCADA_AVAL_OPME");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String iniciarInclusao() {
		this.situacao = this.dominioSituacao != null ? this.dominioSituacao.name() : null;
		this.grupoAlcadaSeq = null;
		return CADASTRO_GRUPO_ALCADA;
	}

	protected List<MbcGrupoAlcadaAvalOpms> recuperarListaPaginada() {
		return this.blocoCirurgicoFacade.listarGrupoAlcadaFiltro(null,
				this.aghEspecialidades, 
				this.tipoConvenioOpms, 
				this.tipoObrigatoriedadeOpms, 
				this.versao,
				this.dominioSituacao);
	}

	// Getters and Setters
	
	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public DominioTipoConvenioOpms getTipoConvenioOpms() {
		return tipoConvenioOpms;
	}

	public void setTipoConvenioOpms(DominioTipoConvenioOpms tipoConvenioOpms) {
		this.tipoConvenioOpms = tipoConvenioOpms;
	}

	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	public DominioSituacao getDominioSituacao() {
		return dominioSituacao;
	}

	public void setDominioSituacao(DominioSituacao dominioSituacao) {
		this.dominioSituacao = dominioSituacao;
	}

	public MbcGrupoAlcadaAvalOpms getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(MbcGrupoAlcadaAvalOpms itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public boolean isRetornoDeOutraPagina() {
		return retornoDeOutraPagina;
	}

	public void setRetornoDeOutraPagina(boolean retornoDeOutraPagina) {
		this.retornoDeOutraPagina = retornoDeOutraPagina;
	}

	public Boolean getAtualizaPesquisa() {
		return atualizaPesquisa;
	}

	public void setAtualizaPesquisa(Boolean atualizaPesquisa) {
		this.atualizaPesquisa = atualizaPesquisa;
	}

	public Short getGrupoAlcadaSeq() {
		return grupoAlcadaSeq;
	}

	public void setGrupoAlcadaSeq(Short grupoAlcadaSeq) {
		this.grupoAlcadaSeq = grupoAlcadaSeq;
	}

	public DominioTipoObrigatoriedadeOpms getTipoObrigatoriedadeOpms() {
		return tipoObrigatoriedadeOpms;
	}

	public void setTipoObrigatoriedadeOpms(
			DominioTipoObrigatoriedadeOpms tipoObrigatoriedadeOpms) {
		this.tipoObrigatoriedadeOpms = tipoObrigatoriedadeOpms;
	}

	public Short getVersao() {
		return versao;
	}

	public void setVersao(Short versao) {
		this.versao = versao;
	}

	public Short getEspecialidadeSeq() {
		return especialidadeSeq;
	}

	public void setEspecialidadeSeq(Short especialidadeSeq) {
		this.especialidadeSeq = especialidadeSeq;
	}

	public List<MbcGrupoAlcadaAvalOpms> getListaPesquisada() {
		return listaPesquisada;
	}

	public void setListaPesquisada(List<MbcGrupoAlcadaAvalOpms> listaPesquisada) {
		this.listaPesquisada = listaPesquisada;
	}

	public boolean isMostrarBotaoNovo() {
		return mostrarBotaoNovo;
	}

	public void setMostrarBotaoNovo(boolean mostrarBotaoNovo) {
		this.mostrarBotaoNovo = mostrarBotaoNovo;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Boolean getExibeConfirmacao() {
		return exibeConfirmacao;
	}

	public void setExibeConfirmacao(Boolean exibeConfirmacao) {
		this.exibeConfirmacao = exibeConfirmacao;
	}

	public MbcGrupoAlcadaAvalOpms getGrupoAlcada() {
		return grupoAlcada;
	}

	public void setGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcada) {
		this.grupoAlcada = grupoAlcada;
	}

	public String getTipoConvenioFiltro() {
		return tipoConvenioFiltro;
	}

	public void setTipoConvenioFiltro(String tipoConvenioFiltro) {
		this.tipoConvenioFiltro = tipoConvenioFiltro;
	}

	public Short getEspecialidadeSeqFiltro() {
		return especialidadeSeqFiltro;
	}

	public void setEspecialidadeSeqFiltro(Short especialidadeSeqFiltro) {
		this.especialidadeSeqFiltro = especialidadeSeqFiltro;
	}

	public String getTipoObrigatoriedadeFiltro() {
		return tipoObrigatoriedadeFiltro;
	}

	public void setTipoObrigatoriedadeFiltro(String tipoObrigatoriedadeFiltro) {
		this.tipoObrigatoriedadeFiltro = tipoObrigatoriedadeFiltro;
	}

	public Short getVersaoFiltro() {
		return versaoFiltro;
	}

	public void setVersaoFiltro(Short versaoFiltro) {
		this.versaoFiltro = versaoFiltro;
	}

	public String getSituacaoFiltro() {
		return situacaoFiltro;
	}

	public void setSituacaoFiltro(String situacaoFiltro) {
		this.situacaoFiltro = situacaoFiltro;
	}

	public Short getGrupoAtivoSeq() {
		return grupoAtivoSeq;
	}

	public void setGrupoAtivoSeq(Short grupoAtivoSeq) {
		this.grupoAtivoSeq = grupoAtivoSeq;
	}

	public MbcGrupoAlcadaAvalOpms getGrupoAtivo() {
		return grupoAtivo;
	}

	public void setGrupoAtivo(MbcGrupoAlcadaAvalOpms grupoAtivo) {
		this.grupoAtivo = grupoAtivo;
	}

	public List<HistoricoAlteracoesGrupoAlcadaVO> getHistoricoGrupoAlcada() {
		return historicoGrupoAlcada;
	}

	public void setHistoricoGrupoAlcada(
			List<HistoricoAlteracoesGrupoAlcadaVO> historicoGrupoAlcada) {
		this.historicoGrupoAlcada = historicoGrupoAlcada;
	}

	public static Log getLog() {
		return LOG;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isMostrarMensagemRegistroNaoLocalizado() {
		return mostrarMensagemRegistroNaoLocalizado;
	}

	public void setMostrarMensagemRegistroNaoLocalizado(
			boolean mostrarMensagemRegistroNaoLocalizado) {
		this.mostrarMensagemRegistroNaoLocalizado = mostrarMensagemRegistroNaoLocalizado;
	}
	
}