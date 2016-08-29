package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.internacao.vo.PesquisaCidVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterDiagnosticosAtendimentoController extends ActionController {

	private static final long serialVersionUID = 8577216834712068129L;
	private static final Log LOG = LogFactory.getLog(ManterDiagnosticosAtendimentoController.class);
	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "manterPrescricaoMedica";
	private static final String PAGINA_PESQUISA_CID = "internacao-pesquisaCid";
	private static final String PAGINA_CONSULTAR_HISTORICO_DIAGNOSTICOS_ATENDIMENTO = "consultarHistoricoDiagnosticosAtendimento";
	

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private PrescricaoMedicaVO prescricaoMedicaVO;
	
	private String idConversacaoAnterior;

	private RapServidores servidor;

	// parâmetros
	private Integer cidSeq;

	private AghAtendimentos atendimento = null;
	private MpmCidAtendimento mpmCidAtendimento = new MpmCidAtendimento();
	
	private List<MpmCidAtendimento> listaMpmCidAtendimentos = new ArrayList<MpmCidAtendimento>();
	private AghCid aghCid;
	private String complementoPendente, complemento;

	private boolean insertMode = true;
	private boolean confirmaVoltar = false;
	private boolean confirmaNovaEdicao = false;
	private boolean confirmaRedirecionaHistorico = false;
	private boolean changed = false;
	private MpmCidAtendimento diagnosticoAEditar;
	
	private Boolean exibirMensagemCIDObrigatorioConfirmacao = false;

	private static MpmCidAtendimentoComparator mpmCidAtendimentoComparator = new MpmCidAtendimentoComparator();

	private static CidComparator cidComparator = new CidComparator();
	
	@Inject
	@SelectionQualifier
	private Instance<PesquisaCidVO> pesquisaCidRetorno;
	
	private boolean pesquisouCid;

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void carregar() {
	 

		
		if (this.pesquisouCid){
			if(this.pesquisaCidRetorno != null) {
				PesquisaCidVO pesquisaCidVO = pesquisaCidRetorno.get();
				this.aghCid = pesquisaCidVO.getCid();
				this.selecionouCid();
			}
			pesquisouCid = false;
		}
		
		if (exibirMensagemCIDObrigatorioConfirmacao){
			apresentarMsgNegocio(Severity.ERROR,"DIAGNOSTICOS_ATENDIMENTO");
			this.exibirMensagemCIDObrigatorioConfirmacao = false;
		}
		
		
		this.setServidor(this.servidorLogadoFacade.obterServidorLogado());
		if (this.prescricaoMedicaVO != null) {
			Integer atdSeq = this.prescricaoMedicaVO.getPrescricaoMedica().getId().getAtdSeq();
			this.atendimento = this.aghuFacade.obterAtendimentoPeloSeq(atdSeq);
		} else {
			apresentarMsgNegocio(Severity.ERROR, "ATENDIMENTO_NULO");
			LOG.warn("ATENDIMENTO_NULO");
		}
		// carregar a lista de mpmCidAtendimentos
		try {
			this.setListaMpmCidAtendimentos(this.getPrescricaoMedicaFacade().buscarMpmCidsPorAtendimento(this.getAtendimento()));
			Collections.sort(this.getListaMpmCidAtendimentos(), mpmCidAtendimentoComparator);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}

		this.setConfirmaVoltar(false);
		this.setConfirmaNovaEdicao(false);
		this.setConfirmaRedirecionaHistorico(false);
		this.setChanged(false);

		// preencher o cid
		if (this.cidSeq != null) {
			this.setAghCid(this.aghuFacade.obterAghCidsPorChavePrimaria(this.cidSeq));
			this.selecionouCid();
		}
	
	}

	/**
	 * Salva as alterações
	 * 
	 * @return
	 */
	public void salvar() {
		// setAtendimento(getPrescricaoMedicaFacade().buscarAtendimento(getAtendimentoSeq()));

		this.getMpmCidAtendimento().setAtendimento(this.getAtendimento());
		this.getMpmCidAtendimento().setComplemento(this.getComplemento());

		getMpmCidAtendimento().setAtendimento(getAtendimento());
		getMpmCidAtendimento().setComplemento(getComplemento() != null ? getComplemento().trim() : null);

		try {
			
			if (this.insertMode) {
				// chama o metodo insert
				// atendimento.setSeq(getAtendimentoSeq());
				// mpmCidAtendimento.setAtendimento(atendimento);
				this.getPrescricaoMedicaFacade().inserirMpmCidAtendimento(
						this.getMpmCidAtendimento(), this.getServidor());
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_DIAGNOSTICO_INCLUIDO_SUCESSO");
			} else {
				// chama o metodo update
				this.getMpmCidAtendimento().setComplemento(complementoPendente);
				this.getPrescricaoMedicaFacade().atualizarMpmCidAtendimento(
						this.getMpmCidAtendimento(), this.getServidor());

				// cria um novo objeto com os dados do atual. Deve alterar
				// somente o SEQ e o complemento.

				MpmCidAtendimento novoAtd = new MpmCidAtendimento();
				novoAtd.setAtendimento(this.getMpmCidAtendimento()
						.getAtendimento());
				novoAtd.setCid(this.getMpmCidAtendimento().getCid());
				novoAtd.setPrioridadeInicio(DominioPrioridadeCid.N);
				novoAtd.setPrincipalAlta(false);
				novoAtd.setComplemento(complemento);

				// chama o método insert para criar um novo registro com o
				// complemento alterado
				this.getPrescricaoMedicaFacade().inserirMpmCidAtendimento(
						novoAtd, this.getServidor());
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_DIAGNOSTICO_ALTERADO_SUCESSO");
			}
			this.limpar();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
	}

	/**
	 * Retorna os valores da tela ao conteúdo do banco de dados
	 */
	public void limpar() {
		this.setMpmCidAtendimento(new MpmCidAtendimento());
		this.setAghCid(null);
		this.setCidSeq(null);
		this.setInsertMode(true);
		this.setConfirmaNovaEdicao(false);
		this.setConfirmaVoltar(false);
		this.setComplemento(null);
		this.setComplementoPendente(null);
		this.setConfirmaRedirecionaHistorico(false);
		this.setListaMpmCidAtendimentos(new ArrayList<MpmCidAtendimento>());
		this.carregar();
		this.setChanged(false);
	}

	/**
	 * Método da suggestion box para pesquisa de CIDs a incluir na lista Ignora
	 * a pesquisa caso o parametro seja o próprio valor selecionado
	 * anteriormente (contorna falha de pesquisa múltipla na suggestion box)
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghCid> pesquisarCids(String parametro) {
		String paramString = (String) parametro;
		LOG.debug("ManterDiagnosticosAtendimentoController.pesquisarCids()(): parametro = ["+ parametro + "].");
		List<AghCid> result = new ArrayList<AghCid>();
		if (this.mpmCidAtendimento.getCid() == null
				|| !(StringUtils.equalsIgnoreCase(paramString,
						this.mpmCidAtendimento.getCid().getCodigo()) || StringUtils
						.equalsIgnoreCase(paramString, this.mpmCidAtendimento
								.getCid().getDescricao()))) {
				result = aghuFacade.obterCidPorNomeCodigoAtivaPaginado(paramString);
		} else {
			// adiciona a especialidade selecionada para nao mostrar mensagens
			// erradas na tela
			result.add(this.mpmCidAtendimento.getCid());
		}
		Collections.sort(result, cidComparator);
		return this.returnSGWithCount(result,pesquisarCidsCount(parametro));
	}

	public Long pesquisarCidsCount(String parametro) {
		return aghuFacade.obterCidPorNomeCodigoAtivaCount((String) parametro);
	}
	
	/**
	 * Método que exclui um mpmcid da lista em memória Ignora nulos
	 * 
	 * @param cidParaExcluir
	 */
	public void excluirMpmCid(MpmCidAtendimento mpmCidParaExcluir) {
		
		LOG.debug("ManterDiagnosticosAtendimentoController.excluirMpmCid(): Entrando.");
		
		if (mpmCidParaExcluir != null) {
			
				LOG.debug("ManterDiagnosticosAtendimentoController.excluirMpmCid(): mpmCid = ["
							+ (mpmCidParaExcluir != null ? mpmCidParaExcluir
									.getSeq() : "") + "].");
			try {
				this.getPrescricaoMedicaFacade().excluirMpmCidAtendimento(
						mpmCidParaExcluir, this.getServidor());
				if (mpmCidParaExcluir.equals(this.mpmCidAtendimento)) {
					this.limpar();
				} else {
					this.carregar();
				}
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
				LOG.error("Erro", e);
			}
		}
		
		LOG.debug("ManterDiagnosticosAtendimentoController.excluirMpmCid(): Saindo.");
	}

	/**
	 * Edita um diagnóstico. Se já houver um diagnóstico em edição, solicita
	 * confirmação do usuário.
	 * 
	 * @param mpmCidEditavel
	 * @return
	 */
	public void editarMpmCid(MpmCidAtendimento mpmCidEditavel) {
		LOG.debug("ManterDiagnosticosAtendimentoController.editarMpmCid(): Entrando.");
		if (!hasChanged()) {
			setDiagnostico(mpmCidEditavel);
			LOG.debug("ManterDiagnosticosAtendimentoController.editarMpmCid(): Saindo.");
		} else {
			this.diagnosticoAEditar = mpmCidEditavel;
			this.setConfirmaNovaEdicao(true);
			LOG.debug("ManterDiagnosticosAtendimentoController.editarMpmCid(): Saindo.");
		}
	}
	
	/**
	 * Efetua confirmação do usuário para edição de um diagnóstico,
	 * descartando alterações do diagnóstico atualmente em edição, se houver.
	 * 
	 */
	public void confirmaEditar() {
		setDiagnostico(diagnosticoAEditar);
		this.setConfirmaNovaEdicao(false);
		diagnosticoAEditar = null;
		// Fecha a modal de confirmação
		RequestContext.getCurrentInstance().execute("PF('modalConfirmacaoEditarWG').hide();");
	}
	
	
	public String pesquisarCidCapitulo(){
		this.pesquisouCid = true;
		return PAGINA_PESQUISA_CID;
	}

	/**
	 * Seta valores de um diagnóstico no formulário.
	 * 
	 * @param mpmCidEditavel
	 */
	private void setDiagnostico(MpmCidAtendimento mpmCidEditavel) {
		this.setMpmCidAtendimento(mpmCidEditavel);
		this.setAghCid(mpmCidEditavel.getCid());
		this.setInsertMode(false);
		this.setComplementoPendente(mpmCidEditavel.getComplemento());
		this.setComplemento(mpmCidEditavel.getComplemento());
		this.setChanged(false);
	}
	
	public void selecionouCid() {
		this.setChanged();
		this.getMpmCidAtendimento().setCid(this.getAghCid());
	}

	public String verificaPendencias() {
		if (this.isInsertMode()) {
			this.setConfirmaVoltar(this.getAghCid() != null
					|| StringUtils.isNotBlank(StringUtils.trim(this
							.getComplemento())));
		} else {
			this.setConfirmaVoltar(this.hasChanged());
		}
		if (this.isConfirmaVoltar()) {
			return null;
		}
		return this.voltar();
	}
	
	private Boolean hasChanged() {
		return changed;
	}
	
	public void setChanged() {
		setChanged(true);
	}
	
	public void setChanged(Boolean flag) {
		changed = flag;
	}

	public String voltar() {
		limpar();
		return PAGINA_MANTER_PRESCRICAO_MEDICA;
	}
	
	public String redirecionaHistorico() {
		if (hasChanged()) {
			setConfirmaRedirecionaHistorico(true);
			// Exibe a modal de confirmação
			RequestContext.getCurrentInstance().execute("PF('modalRedirecionaHistoricoWG').show();");
			return null;
		} else {
			return redirecionaHistoricoConfirmado();
		}
	}
	
	public String redirecionaHistoricoConfirmado() {
		return PAGINA_CONSULTAR_HISTORICO_DIAGNOSTICOS_ATENDIMENTO;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public AghAtendimentos getAtendimento() {
		return this.atendimento;
	}

	public void setMpmCidAtendimento(MpmCidAtendimento mpmCidAtendimento) {
		this.mpmCidAtendimento = mpmCidAtendimento;
	}

	public MpmCidAtendimento getMpmCidAtendimento() {
		return this.mpmCidAtendimento;
	}

	public void setListaMpmCidAtendimentos(
			List<MpmCidAtendimento> listaMpmCidAtendimentos) {
		this.listaMpmCidAtendimentos = listaMpmCidAtendimentos;
	}

	public List<MpmCidAtendimento> getListaMpmCidAtendimentos() {
		return this.listaMpmCidAtendimentos;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	public AghCid getAghCid() {
		return this.aghCid;
	}

	public void setInsertMode(boolean insertMode) {
		this.insertMode = insertMode;
	}

	public boolean isInsertMode() {
		return this.insertMode;
	}

	public boolean isNotInsertMode() {
		return !this.isInsertMode();
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public Integer getCidSeq() {
		return this.cidSeq;
	}

	public void setConfirmaVoltar(boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	public boolean isConfirmaVoltar() {
		return this.confirmaVoltar;
	}
	
	public boolean isConfirmaNovaEdicao() {
		return confirmaNovaEdicao;
	}

	public void setConfirmaNovaEdicao(boolean confirmaNovaEdicao) {
		this.confirmaNovaEdicao = confirmaNovaEdicao;
	}
	
	public boolean isConfirmaRedirecionaHistorico() {
		return confirmaRedirecionaHistorico;
	}

	public void setConfirmaRedirecionaHistorico(boolean confirmaRedirecionaHistorico) {
		this.confirmaRedirecionaHistorico = confirmaRedirecionaHistorico;
	}

	public void setComplementoPendente(String complementoPendente) {
		this.complementoPendente = complementoPendente;
	}

	public String getComplementoPendente() {
		return this.complementoPendente;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getComplemento() {
		return this.complemento;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

    private static class MpmCidAtendimentoComparator implements
			Comparator<MpmCidAtendimento> {
		@Override
		public int compare(MpmCidAtendimento a1, MpmCidAtendimento a2) {
			int result = a1.getCid().getCodigo().compareToIgnoreCase(
					a2.getCid().getCodigo());
			return result;
		}
	}

	private static class CidComparator implements Comparator<AghCid> {
		@Override
		public int compare(AghCid a1, AghCid a2) {
			int result = a1.getCodigo().compareToIgnoreCase(a2.getCodigo());
			return result;
		}
	}

	public String getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(String idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}
	
	public Boolean getExibirMensagemCIDObrigatorioConfirmacao() {
		return exibirMensagemCIDObrigatorioConfirmacao;
	}

	public void setExibirMensagemCIDObrigatorioConfirmacao(
			Boolean exibirMensagemCIDObrigatorioConfirmacao) {
		this.exibirMensagemCIDObrigatorioConfirmacao = exibirMensagemCIDObrigatorioConfirmacao;
	}

    public MpmCidAtendimento getDiagnosticoAEditar() {
        return diagnosticoAEditar;
    }

    public void setDiagnosticoAEditar(MpmCidAtendimento diagnosticoAEditar) {
        this.diagnosticoAEditar = diagnosticoAEditar;
    }

}