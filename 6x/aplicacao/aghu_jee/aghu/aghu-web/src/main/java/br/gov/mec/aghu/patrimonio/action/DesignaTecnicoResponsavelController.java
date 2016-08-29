package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

import br.gov.mec.aghu.dominio.DominioIndResponsavel;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmTecnicoItemRecebimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class DesignaTecnicoResponsavelController extends ActionController {

	private static final long serialVersionUID = -7975336772073599646L;

	private static final String MENSAGEM_PERCENTUAL_PAG_ANTECIPADO = "MENSAGEM_PERCENTUAL_PAG_ANTECIPADO";
	private static final String MENSAGEM_NENHUM_TEC_SELECIONADO = "MENSAGEM_NENHUM_TEC_SELECIONADO";
	private static final String MENSAGEM_TEC_DESIGNADOS_SUCESSO = "MENSAGEM_TEC_DESIGNADOS_SUCESSO";
	private static final String PAGINA_SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";
	private static final String PAGINA_LISTAR_ACEITES_TECNICOS = "patrimonio-listarAceitesTecnicos";
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private boolean modoEdicao;
	private boolean pagamentoParcialCheck;
	private Integer pagamentoParcial;
	private AceiteTecnicoParaSerRealizadoVO aceiteTecnico;
	private String tecnicoResponsavel;
	private List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnico;
	private List<RapServidores> servidoresSource;
	private List<RapServidores> servidoresTarget;
	private List<RapServidores> servidoresInsert;
	private List<RapServidores> servidoresDelete;
	private DualListModel<String> tecnicos;
	private String paginaRetorno;

	@PostConstruct
	protected void inicializar() {
		begin(conversation);
	}
	
	/**
	 * Metodo invocado ao carregar a tela.
	 */
	public void iniciar() {
		if (this.aceiteTecnico != null) {
			this.tecnicoResponsavel = null;
			this.servidoresInsert = null;
			this.servidoresDelete = null;
			if (this.listaAceiteTecnico == null) { 
				this.listaAceiteTecnico = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
				this.listaAceiteTecnico.add(this.aceiteTecnico);
			}
			this.tecnicos = carregarDualList();
			this.pagamentoParcialCheck = (this.pagamentoParcial != null);
		} 
	}

	/**
	 * Carrega lista dupla com nomes dos servidores tecnicos. 
	 */
	private DualListModel<String> carregarDualList() {
		Set<String> source = carregarListaNomeServidoresSource();
		Set<String> target = carregarListaNomeServidoresTarget(source);
		
		if(this.aceiteTecnico.getAreaTecAvaliacao() == null){
			source = new HashSet<String>();
		}
		
		return new DualListModel<String>(new ArrayList<String>(source), new ArrayList<String>(target));
	}

	/**
	 * Carrega lista de nomes dos servidores da lista da esquerda do picklist. 
	 */
	private Set<String> carregarListaNomeServidoresSource() {
		Set<String> source = new HashSet<String>();
		this.servidoresSource = this.registroColaboradorFacade.obterServidorPorAreaTecnicaAvaliacao(this.aceiteTecnico.getAreaTecAvaliacao());
		if (this.servidoresSource != null && !this.servidoresSource.isEmpty()) {
			for (RapServidores servidor : this.servidoresSource) {
				source.add(servidor.getPessoaFisica().getNome());
			}
		}
		return source;
	}
	
	/**
	 * Carrega lista de nomes dos servidores da lista da direita do picklist.
	 */
	private Set<String> carregarListaNomeServidoresTarget(Set<String> source) {
		Set<String> target = new HashSet<String>();
		this.servidoresTarget = new ArrayList<RapServidores>();
		if (this.listaAceiteTecnico.size() == 1) {
			this.servidoresTarget = this.registroColaboradorFacade.obterServidorTecnicoPorItemRecebimento(this.aceiteTecnico.getRecebimento(), this.aceiteTecnico.getItemRecebimento());
			if (source != null && !source.isEmpty() && this.servidoresTarget != null && !this.servidoresTarget.isEmpty()) {
				for (RapServidores servidor : this.servidoresTarget) {
					target.add(servidor.getPessoaFisica().getNome());
					if (source.contains(servidor.getPessoaFisica().getNome())) {
						source.remove(servidor.getPessoaFisica().getNome());
					}
					this.tecnicoResponsavel = carregarTecnicoResponsavel(servidor);
				}
			}
			this.pagamentoParcial = this.patrimonioFacade.obterPagamentoParcialItemRecebimento(this.aceiteTecnico.getRecebimento(), this.aceiteTecnico.getItemRecebimento());
		} else {
			this.pagamentoParcial = null;
		}
		return target;
	}

	/**
	 * Verifica se lista possui tecnico cujo ind_responsavel = 1.
	 */
	private String carregarTecnicoResponsavel(RapServidores servidor) {
		if (servidor != null) {
			List<PtmTecnicoItemRecebimento> tecnicosItemRecebimento = this.patrimonioFacade.obterTecnicosPorServidor(
					servidor, this.aceiteTecnico.getRecebimento(), this.aceiteTecnico.getItemRecebimento());
			if (tecnicosItemRecebimento != null && !tecnicosItemRecebimento.isEmpty()) {
				for (PtmTecnicoItemRecebimento tecnico : tecnicosItemRecebimento) {
					if (DominioIndResponsavel.R.equals(tecnico.getIndResponsavel())) {
						return servidor.getPessoaFisica().getNome();
					}
				}
			}
		}
		return this.tecnicoResponsavel;
	}
	
	/**
	 * Evento ajax invocado durante a movimentação dos servidores selecionados entre as duas listas do picklist.
	 */
	public void onTransfer(TransferEvent event) {
		inicializarListaServidores();
		for (Object item : event.getItems()) {
			RapServidores servidor = obterServidorPorNome((String) item);
			if (this.servidoresTarget.contains(servidor)) {
				if (this.servidoresDelete.contains(servidor)) {
					this.servidoresDelete.remove(servidor);
				} else {
					this.servidoresDelete.add(servidor);
				}
			} else {
				if (this.servidoresInsert.contains(servidor)) {
					this.servidoresInsert.remove(servidor);
				} else {
					this.servidoresInsert.add(servidor);
				}
			}
        }
    }

	/**
	 * Se nulo, inicializa a respectiva lista.
	 */
	private void inicializarListaServidores() {
		if (this.servidoresTarget == null) {
			this.servidoresTarget = new ArrayList<RapServidores>();
		}
		if (this.servidoresInsert == null) {
			this.servidoresInsert = new ArrayList<RapServidores>();
		}
		if (this.servidoresDelete == null) {
			this.servidoresDelete = new ArrayList<RapServidores>();
		}
	} 
	
	/**
	 * Obtem Servidor com o nome selecionado no picklist.
	 */
	private RapServidores obterServidorPorNome(String nome) {
		RapServidores retorno = obterServidorPorNomeSource(nome);
		if (retorno == null) {
			retorno = obterServidorPorNomeTarget(nome);
		}
		return retorno;
	}
	
	/**
	 * Obtem Servidor com o nome selecionado na lista da esquerda do picklist.
	 */
	private RapServidores obterServidorPorNomeSource(String nome) {
		if (this.servidoresSource != null && !this.servidoresSource.isEmpty()) {
			for (RapServidores servidor : this.servidoresSource) {
				if (servidor.getPessoaFisica().getNome().equals(nome)) {
					return servidor;
				}
			}
		}
		return null;
	}
	
	/**
	 * Obtem Servidor com o nome selecionado na lista da direita do picklist. 
	 */
	private RapServidores obterServidorPorNomeTarget(String nome) {
		if (this.servidoresTarget != null && !this.servidoresTarget.isEmpty()) {
			for (RapServidores servidor : this.servidoresTarget) {
				if (servidor.getPessoaFisica().getNome().equals(nome)) {
					return servidor;
				}
			}
		}
		return null;
	}

	/**
	 * Valida se o elemento corresponde ao tecnico responsavel.
	 */
	public boolean tecnicoResponsavel(String nome) {
		return (this.tecnicoResponsavel != null && nome != null && this.tecnicoResponsavel.equals(nome));
	}
	
	/**
	 * Ação do checkbox do Pagamento Parcial.
	 */
	public void habilitarCampoPagamentoParcial() {
		this.pagamentoParcial = null;
	}
	
	/**
	 * Ação do botão Gravar.
	 */
	public String gravar() throws ApplicationBusinessException {
		if (this.tecnicos != null && this.tecnicos.getTarget().isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, MENSAGEM_NENHUM_TEC_SELECIONADO);
			return null;
		}
		if (this.pagamentoParcialCheck && this.pagamentoParcial == null) {
			apresentarMsgNegocio(Severity.ERROR, MENSAGEM_PERCENTUAL_PAG_ANTECIPADO);
			return null;
		}
		this.patrimonioFacade.designarTecnicoResponsavel(
				this.listaAceiteTecnico, this.servidoresDelete, this.servidoresInsert, this.pagamentoParcial, 
				this.registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado()));
		apresentarMsgNegocio(Severity.INFO, MENSAGEM_TEC_DESIGNADOS_SUCESSO);
		return voltar(); 
	}
	
	/**
	 * Ação do botão Voltar.
	 */
	public String voltar() {
		this.aceiteTecnico = null;
		this.listaAceiteTecnico = null;
		if (paginaRetorno == null) {
			return PAGINA_LISTAR_ACEITES_TECNICOS;
		} else {
			String retorno = paginaRetorno;
			paginaRetorno = null;
			return retorno;
		}
	}
	
	/**
	 * Encaminha para pagina de Solicitação de Compra.
	 */
	public String redirecionarSolicitacaoCompra() {
		return PAGINA_SOLICITACAO_COMPRA_CRUD;
	}
	
	/**
	 * Obtem o nome da Area Tecnica de Avaliação. 
	 */
	public String obterNomeAreaTecAvaliacao(Integer areaTecAvaliacao, Integer tamanho) {
		String retorno = "";
		if (areaTecAvaliacao != null) {
			PtmAreaTecAvaliacao ata = patrimonioFacade.obterAreaTecAvaliacaoPorChavePrimaria(areaTecAvaliacao);
			if (ata != null && ata.getNomeAreaTecAvaliacao() != null) {
				retorno = ata.getNomeAreaTecAvaliacao();
				if (retorno.length() > tamanho) {
					retorno = StringUtils.abbreviate(retorno, tamanho);
				}
			}
		}
		return retorno;
	}
	
	/**
	 * Obtem o nome do Tecnico Responsavel. 
	 */
	public String obterNomeTecnicoResponsavel(Integer tecnicoResponsavel, Short codigoTecnicoResponsavel, Integer tamanho) {
		String retorno = "";
		if (tecnicoResponsavel != null) {
			retorno = registroColaboradorFacade.buscarNomeResponsavelPorMatricula(codigoTecnicoResponsavel, tecnicoResponsavel);
			if (retorno.length() > tamanho) {
				retorno = StringUtils.abbreviate(retorno, tamanho);
			}
		}
		return retorno;
	}
	
	/**
	 * Obtem descrição truncada para o status do aceite técnico.
	 */
	public String obterDescricaoDominioStatusTruncado(Integer value, Integer tamanho) {
		String descricao = obterDescricaoDominioStatus(value);
		if (descricao.length() > tamanho) {
			descricao = StringUtils.abbreviate(descricao, tamanho);
		}
		return descricao;
	}
	
	/**
	 * Obtem descrição para o status do aceite técnico.
	 */
	public String obterDescricaoDominioStatus(Integer value) {
		return DominioStatusAceiteTecnico.obterDominioStatusAceiteTecnico(value).getDescricao();
	}
	
	/**
	 * GETs and SETs
	 */
	public List<AceiteTecnicoParaSerRealizadoVO> getListaAceiteTecnico() {
		return listaAceiteTecnico;
	}

	public void setListaAceiteTecnico(List<AceiteTecnicoParaSerRealizadoVO> listaAceiteTecnico) {
		this.listaAceiteTecnico = listaAceiteTecnico;
	}

	public DualListModel<String> getTecnicos() {
		return tecnicos;
	}

	public void setTecnicos(DualListModel<String> tecnicos) {
		this.tecnicos = tecnicos;
	}

	public AceiteTecnicoParaSerRealizadoVO getAceiteTecnico() {
		return aceiteTecnico;
	}

	public void setAceiteTecnico(AceiteTecnicoParaSerRealizadoVO aceiteTecnico) {
		this.aceiteTecnico = aceiteTecnico;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public boolean isPagamentoParcialCheck() {
		return pagamentoParcialCheck;
	}

	public void setPagamentoParcialCheck(boolean pagamentoParcialCheck) {
		this.pagamentoParcialCheck = pagamentoParcialCheck;
	}

	public Integer getPagamentoParcial() {
		return pagamentoParcial;
	}

	public void setPagamentoParcial(Integer pagamentoParcial) {
		this.pagamentoParcial = pagamentoParcial;
	}

	public List<RapServidores> getServidoresTarget() {
		return servidoresTarget;
	}

	public void setServidoresTarget(List<RapServidores> servidoresTarget) {
		this.servidoresTarget = servidoresTarget;
	}

	public String getTecnicoResponsavel() {
		return tecnicoResponsavel;
	}

	public void setTecnicoResponsavel(String tecnicoResponsavel) {
		this.tecnicoResponsavel = tecnicoResponsavel;
	}

	public List<RapServidores> getServidoresSource() {
		return servidoresSource;
	}

	public void setServidoresSource(List<RapServidores> servidoresSource) {
		this.servidoresSource = servidoresSource;
	}

	public List<RapServidores> getServidoresInsert() {
		return servidoresInsert;
	}

	public void setServidoresInsert(List<RapServidores> servidoresInsert) {
		this.servidoresInsert = servidoresInsert;
	}

	public List<RapServidores> getServidoresDelete() {
		return servidoresDelete;
	}

	public void setServidoresDelete(List<RapServidores> servidoresDelete) {
		this.servidoresDelete = servidoresDelete;
	}

	public String getPaginaRetorno() {
		return paginaRetorno;
	}

	public void setPaginaRetorno(String paginaRetorno) {
		this.paginaRetorno = paginaRetorno;
	}
	
}
