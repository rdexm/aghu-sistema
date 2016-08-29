package br.gov.mec.aghu.prescricaomedica.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoEspecial;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ModoUsoProcedimentoEspecialVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ProcedimentoEspecialVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para: Prescrever Procedimentos Especiais<br/>
 * 
 * 
 * @author rcorvalao
 */

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterProcedimentoEspecialController extends ActionController {

	private static final long serialVersionUID = 8737578289598144240L;
	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	private static final Log LOG = LogFactory.getLog(ManterProcedimentoEspecialController.class);
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private Integer procSeqAtendimento;
	private Long procSeq;

	private ProcedimentoEspecialVO procedimento = new ProcedimentoEspecialVO();

	private ModoUsoProcedimentoEspecialVO modoUsoProc = new ModoUsoProcedimentoEspecialVO();

	private List<ItemPrescricaoMedicaVO> listaProcedimentoEspecial = new LinkedList<ItemPrescricaoMedicaVO>();

	private List<ModoUsoProcedimentoEspecialVO> listaModoUsoProdedimentoEspecial = new LinkedList<ModoUsoProcedimentoEspecialVO>();
	private List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao;

	private Boolean ehTipoEspecialDiverso;
	private Boolean ehTipoProcedimentosLeito;
	private Boolean ehTipoOrtesesProteses;
	private Boolean ehModoUsoObrigatorio = Boolean.FALSE;
	
	private int idConversacaoAnterior;

	private boolean formChanged;

	private boolean itemFormChanged;
	
	private boolean itemsChanged;

	private ItemPrescricaoMedicaVO procedimento2Edit;

	private ModoUsoProcedimentoEspecialVO item2Edit;
	
	private PrescricaoMedicaVO prescricaoMedicaVO;

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

		reset();
		
		if ((this.prescricaoMedicaVO != null)
				&& (this.prescricaoMedicaVO.getId() != null)) {
			this.setListaProcedimentoEspecial(this.prescricaoMedicaFacade
					.buscaListaProcedimentoEspecialPorPrescricaoMedica(this.prescricaoMedicaVO
							.getId()));
		}

		if (this.getProcedimento().getTipo() == null) {
			this.procedimento
					.setTipo(DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS);
		}

		if ((this.getProcSeqAtendimento() != null)
				&& (this.getProcSeq() != null)) {
			MpmPrescricaoProcedimentoId idPrescProc = new MpmPrescricaoProcedimentoId(
					this.getProcSeqAtendimento(), this.getProcSeq());
			this.procedimento = this.prescricaoMedicaFacade
					.buscaPrescricaoProcedimentoEspecialVOPorId(idPrescProc);
			if(procedimento == null || procedimento.isEmpty()){
				//controle caso o item tenha sido excluído por outro usuário
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			}else{
				this.listaModoUsoProdedimentoEspecial = this.procedimento
						.getListaModoUsoProdedimentoEspecialVO();
			}
		}

		this.doTipoProcedimentoEspecial();
	
	}
	
	private void reset() {
		this.setFormChanged(false);
		this.setItemsChanged(false);
		this.resetItems();
	}
	
	private void resetItems() {
		this.setItemFormChanged(false);
	}

	public void editarProcedimento(ItemPrescricaoMedicaVO itemProcedimento) {
		if (isFormChanged()) {
			procedimento2Edit = itemProcedimento;
			this.openDialog("confirmEditarModalWG");
			return;
		}
		
		this.initController();

		this.setProcSeqAtendimento(itemProcedimento.getAtendimentoSeq());
		this.setProcSeq(itemProcedimento.getItemSeq());

		this.inicio();
	}

	/**
	 * Confirma edição de um procedimento.
	 */
	public void editarProcedimento() {
		reset();
		editarProcedimento(procedimento2Edit);
		procedimento2Edit = null;
	}
	
	public void valueChangeTipoProcedimentoEspecial() {
		this.doTipoProcedimentoEspecial();
		this.setFormChanged(true);
	}

	private void doTipoProcedimentoEspecial() {
		this.setEhTipoEspecialDiverso((DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS == this
				.getProcedimento().getTipo()));
		this.setEhTipoProcedimentosLeito((DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO == this
				.getProcedimento().getTipo()));
		this.setEhTipoOrtesesProteses((DominioTipoProcedimentoEspecial.ORTESES_PROTESES == this
				.getProcedimento().getTipo()));
	}

	public void gravar() {
		if (isItemFormChanged()) {
			this.openDialog("confirmGravarModalWG");
			return;
		}
		
		try {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}




			MpmPrescricaoProcedimento prescProc = this.getModel();
			this.prescricaoMedicaFacade.gravarPrescricaoProcedimentoEspecial(
					prescProc, this.getListaModoUsoParaExclusao(),
					this.procedimento.getTipo(), nomeMicrocomputador, formChanged);

            this.prescricaoMedicaFacade.removerModoDeUso(this.getListaModoUsoParaExclusao());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_PERSISTIR_PROCEDIMENTO");
			this.limpar();
		
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarExcecaoNegocio(e);
		} catch (BaseRuntimeException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarExcecaoNegocio(e);			
		}
	}
	
	public void confirmaGravar() {
		setItemFormChanged(false);
		gravar();		
	}

	public void removerSelecionados() {
		if (isFormChanged()) {
			this.openDialog("confirmExcluirModalWG");
			return;
		}
		
		try {
			List<ItemPrescricaoMedicaVO> itens = this
					.getListaProcedimentoEspecial();
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			this.prescricaoMedicaFacade.excluirSelecionados(
					this.prescricaoMedicaVO.getPrescricaoMedica(), itens, nomeMicrocomputador);

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_REMOCAO_PROCEDIMENTO");
			this.limpar();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void confirmaRemoverSelecionados() {
		reset();
		removerSelecionados();
	}
	
	/**
	 * Inicializa a tela para uma nova insercao.
	 */
	public void limpar() {
		this.initController();
		this.inicio();
	}

	private void initController() {
		this.setProcSeqAtendimento(null);
		this.setProcSeq(null);

		this.setProcedimento(new ProcedimentoEspecialVO());
		this.getProcedimento().setTipo(
				DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS);
		this.initModoUsoProc();
		this.setListaProcedimentoEspecial(new LinkedList<ItemPrescricaoMedicaVO>());
		this.setListaModoUsoProdedimentoEspecial(new LinkedList<ModoUsoProcedimentoEspecialVO>());
		this.listaModoUsoParaExclusao = null;
	}

	/**
	 * Recupera os dados de Modelo com as alteracoes
	 * 
	 * @return
	 */
	private MpmPrescricaoProcedimento getModel() {
		MpmPrescricaoProcedimento procedimentoNovo = this.getProcedimento().getModel();
		procedimentoNovo.setPrescricaoMedica(this.getPrescricaoMedicaVO().getPrescricaoMedica());
		RapServidores servidorLogado = null;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			LOG.error("Problema ao tentar obter servidor pelo usuário logado.", e);
		}
		procedimentoNovo.setServidor(servidorLogado);

		if (this.getProcedimento().isProcedimentoEspecialDiverso()) {
			// Eh Especial Diverso, trata os Modos de Uso.
			for (ModoUsoProcedimentoEspecialVO modoUsoVO : this.getListaModoUsoProdedimentoEspecial()) {
				MpmModoUsoPrescProced modoUso = modoUsoVO.getModel();
				if (procedimentoNovo.getId() != null) {
					// eh alteracao de procedimento
					if (modoUso.getId() == null) {
						// adiciona apenas quando for item novo
						procedimentoNovo.addModoUso(modoUso);
					}
				} else {
					// eh inclusao de procedimento.
					// adiciona o item sem restricao
					procedimentoNovo.addModoUso(modoUso);
				}
			}
		} else {
			// NAO eh Especial Diverso, se necessario zera os Modos de Uso.
			if (this.getListaModoUsoProdedimentoEspecial() != null && !this.getListaModoUsoProdedimentoEspecial().isEmpty()) {
				this.removerTodosModoUso();
			}
		}

		return procedimentoNovo;
	}

	/**
	 * .
	 */
	public void adicionarModoUso() {
		try {
			if (this.getModoUsoProc().isValidaAdicaoEdicao()) {
				
				MpmModoUsoPrescProced modoUsoProc = this.getModoUsoProc()
						.getModel();
				
				prescricaoMedicaFacade.verificarTipoModoUsoProcedimento(
						modoUsoProc.getTipoModUsoProcedimento().getId()
								.getSeqp(), modoUsoProc
								.getTipoModUsoProcedimento().getId()
								.getPedSeq(), modoUsoProc.getQuantidade());
				
				this.getListaModoUsoProdedimentoEspecial().add(
						this.getModoUsoProc());
				
				this.initModoUsoProc();
				this.setItemsChanged(true);
				this.resetItems();
				
			} else {
				apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Modo Uso");
				this.setEhModoUsoObrigatorio(Boolean.TRUE);
			}

		} catch (BaseException e) {

			apresentarMsgNegocio(Severity.WARN,
					e.getMessage());

		}
	}

	private void initModoUsoProc() {
		this.setModoUsoProc(new ModoUsoProcedimentoEspecialVO());
		this.setEhModoUsoObrigatorio(Boolean.FALSE);
	}

	/**
	 * .
	 * @throws ApplicationBusinessException
	 */
	public void alterarModoUso() throws ApplicationBusinessException {
		try {
			
			if (this.getModoUsoProc().isValidaAdicaoEdicao()) {
				
				MpmModoUsoPrescProced modoUsoProc = this.getModoUsoProc()
						.getModel();
				
				prescricaoMedicaFacade.verificarTipoModoUsoProcedimento(
						modoUsoProc.getTipoModUsoProcedimento().getId()
								.getSeqp(), modoUsoProc
								.getTipoModUsoProcedimento().getId()
								.getPedSeq(), modoUsoProc.getQuantidade());
				
				this.modoUsoProc.setEmEdicao(Boolean.FALSE);
				
				ModoUsoProcedimentoEspecialVO atualModoUsoVO = getModoUsoProcedimentoEspecialVO();
				atualModoUsoVO.atualizarProperties(this.modoUsoProc);
				
				this.initModoUsoProc();
				this.setItemsChanged(true);
				this.resetItems();
				
			} else {
				
				apresentarMsgNegocio(Severity.WARN,
						"MENSAGEM_ADVERTENCIA_MODO_USO_CAMPOS_OBRIGATORIOS");
				
			}

		} catch (BaseException e) {

			apresentarMsgNegocio(Severity.WARN,
					e.getMessage());

		}
		
	}

	/**
	 * Busca VO na lista através do id.
	 * @return {ModoUsoProcedimentoEspecialVO}
	 */
	private ModoUsoProcedimentoEspecialVO getModoUsoProcedimentoEspecialVO() {
		
		ModoUsoProcedimentoEspecialVO modoUsoProcedimentoEspecialVO = null;
		
		for (ModoUsoProcedimentoEspecialVO vo : listaModoUsoProdedimentoEspecial) {
			
			if (vo.getEmEdicao().booleanValue()) {
				
				modoUsoProcedimentoEspecialVO = vo;
				
			}
			
		}
		
		return modoUsoProcedimentoEspecialVO;
		
	}

	public void cancelarAlteracaoModoUso() {
		ModoUsoProcedimentoEspecialVO atualModoUsoVO = getModoUsoProcedimentoEspecialVO();
		if (atualModoUsoVO != null) {
			atualModoUsoVO.setEmEdicao(Boolean.FALSE);
			this.modoUsoProc.setEmEdicao(Boolean.FALSE);
			this.initModoUsoProc();
		}	
	}

	public void editarModoUso(ModoUsoProcedimentoEspecialVO modoUsoVO) {
		if (isItemFormChanged()) {
			item2Edit = modoUsoVO;
			this.openDialog("confirmEditarItemModalWG");
			return;
		}
		
		cancelarAlteracaoModoUso();
		modoUsoVO.setEmEdicao(Boolean.TRUE);
		ModoUsoProcedimentoEspecialVO novoModoUsoVO = new ModoUsoProcedimentoEspecialVO(modoUsoVO);
		this.setModoUsoProc(novoModoUsoVO);
	}
	
	public void editarModoUso() {
		resetItems();
		cancelarAlteracaoModoUso();
		editarModoUso(item2Edit);
		item2Edit = null;
	}

	public void excluirModoUso(ModoUsoProcedimentoEspecialVO modoUsoVO) throws ApplicationBusinessException {
		if (modoUsoVO.getEmEdicao()) {
			this.initModoUsoProc();
		}
		this.removerModoUso(modoUsoVO);
		this.getListaModoUsoParaExclusao().add(modoUsoVO);
		this.setItemsChanged(true);
	}

	private void removerModoUso(ModoUsoProcedimentoEspecialVO modoUsoVO) {
		modoUsoVO.setEmEdicao(Boolean.TRUE);
		for (int i = this.getListaModoUsoProdedimentoEspecial().size() - 1; i >= 0; i--) {
			ModoUsoProcedimentoEspecialVO vo = this
					.getListaModoUsoProdedimentoEspecial().get(i);
			if (vo.getEmEdicao()) {
				this.getListaModoUsoProdedimentoEspecial().remove(i);
				break;
			}
		}
		modoUsoVO.setEmEdicao(Boolean.FALSE);
	}

	// Metódo para SuggestionAction da SB de Especiais Diversos
	public List<MpmProcedEspecialDiversos> obterProcedEspecialDiversos(
			String objPesquisa) {
		return this.prescricaoMedicaFacade
				.getListaProcedEspecialDiversos(objPesquisa);
	}

	// Metódo para SuggestionAction da SB de Modo Uso Procedimentos Diversos
	public List<MpmTipoModoUsoProcedimento> obterTipoModoUsoProcedimento(
			String objPesquisa) throws ApplicationBusinessException {
		if (this.procedimento.getProcedEspecial() != null) {
			return this.prescricaoMedicaFacade
					.getListaTipoModoUsoProcedEspeciaisDiversos(objPesquisa,
							this.procedimento.getProcedEspecial());
		} else {
			return new ArrayList<MpmTipoModoUsoProcedimento>();
		}
	}

	// Metódo para SuggestionAction da SB de Procedimentos Realizados no Leito
	public List<MbcProcedimentoCirurgicos> obterProcedRealizadosLeito(
			String objPesquisa) {
		return this.prescricaoMedicaFacade
				.getListaProcedimentoCirurgicosRealizadosNoLeito(objPesquisa);
	}

	// private MpmModoUsoPrescProcedId makeMpmModoUsoPrescProcedId(Integer seqp)
	// {
	// return new MpmModoUsoPrescProcedId(this.getProcSeqAtendimento(),
	// this.getProcSeq(), seqp);
	// }

	// Metódo para SuggestionAction da SB de Ortese e Protese
	public List<ScoMaterial> obterOrteseseProteses(String objPesquisa)
			throws ApplicationBusinessException {


        BigDecimal paramVlNumerico = null;

        AghParametros parametro = this.parametroFacade.buscarAghParametro(
                AghuParametrosEnum.GRPO_MAT_ORT_PROT);

        if (parametro != null) {
            paramVlNumerico = parametro.getVlrNumerico();
        }
        List<ScoMaterial> result = this.prescricaoMedicaFacade.obterMateriaisOrteseseProtesesPrescricao(
                paramVlNumerico, objPesquisa);
        return result;
	}

	public void limparModoUsoSelecionado() throws ApplicationBusinessException {

		this.modoUsoProc = new ModoUsoProcedimentoEspecialVO();
		this.removerTodosModoUso();

	}
	
	private void removerTodosModoUso() {
		for (ModoUsoProcedimentoEspecialVO vo : listaModoUsoProdedimentoEspecial) {
			if (vo.getItemId() != null) {
				this.getListaModoUsoParaExclusao().add(vo);
			}			
		}
		this.setListaModoUsoProdedimentoEspecial(new LinkedList<ModoUsoProcedimentoEspecialVO>());		
	}

	public String verificaPendencias() {
		if (isFormChanged()) {
			this.openDialog("modalConfirmacaoPendenciaWG");
			return null;
		}
		return this.voltar();
	}

	public String voltar() {
		limpar();
		return PAGINA_MANTER_PRESCRICAO_MEDICA;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return this.prescricaoMedicaVO;
	}

	public void setProcSeqAtendimento(Integer procSeqAtendimento) {
		this.procSeqAtendimento = procSeqAtendimento;
	}

	public Integer getProcSeqAtendimento() {
		return this.procSeqAtendimento;
	}

	public void setProcSeq(Long procSeq) {
		this.procSeq = procSeq;
	}

	public Long getProcSeq() {
		return this.procSeq;
	}

	public void setListaProcedimentoEspecial(
			List<ItemPrescricaoMedicaVO> listaProcedimentoEspecial) {
		this.listaProcedimentoEspecial = listaProcedimentoEspecial;
	}

	public List<ItemPrescricaoMedicaVO> getListaProcedimentoEspecial() {
		return this.listaProcedimentoEspecial;
	}

	public void setListaModoUsoProdedimentoEspecial(
			List<ModoUsoProcedimentoEspecialVO> listaModoUsoProdedimentoEspecial) {
		this.listaModoUsoProdedimentoEspecial = listaModoUsoProdedimentoEspecial;
	}

	public List<ModoUsoProcedimentoEspecialVO> getListaModoUsoProdedimentoEspecial() {
		return this.listaModoUsoProdedimentoEspecial;
	}

	public ModoUsoProcedimentoEspecialVO getModoUsoProc() {
		return this.modoUsoProc;
	}

	public void setModoUsoProc(ModoUsoProcedimentoEspecialVO modoUsoProc) {
		this.modoUsoProc = modoUsoProc;
	}

	public void setProcedimento(ProcedimentoEspecialVO procedimento) {
		this.procedimento = procedimento;
	}

	public ProcedimentoEspecialVO getProcedimento() {
		return this.procedimento;
	}

	private List<ModoUsoProcedimentoEspecialVO> getListaModoUsoParaExclusao() {
		if (this.listaModoUsoParaExclusao == null) {
			this.listaModoUsoParaExclusao = new LinkedList<ModoUsoProcedimentoEspecialVO>();
		}
		return this.listaModoUsoParaExclusao;
	}

	public void setEhTipoEspecialDiverso(Boolean ehTipoEspecialDiverso) {
		this.ehTipoEspecialDiverso = ehTipoEspecialDiverso;
	}

	public Boolean getEhTipoEspecialDiverso() {
		return this.ehTipoEspecialDiverso;
	}

	public void setEhTipoProcedimentosLeito(Boolean ehTipoProcedimentosLeito) {
		this.ehTipoProcedimentosLeito = ehTipoProcedimentosLeito;
	}

	public Boolean getEhTipoProcedimentosLeito() {
		return this.ehTipoProcedimentosLeito;
	}

	public void setEhTipoOrtesesProteses(Boolean ehTipoOrtesesProteses) {
		this.ehTipoOrtesesProteses = ehTipoOrtesesProteses;
	}

	public Boolean getEhTipoOrtesesProteses() {
		return this.ehTipoOrtesesProteses;
	}

	public void setEhModoUsoObrigatorio(Boolean ehModoUsoObrigatorio) {
		this.ehModoUsoObrigatorio = ehModoUsoObrigatorio;
	}

	public Boolean getEhModoUsoObrigatorio() {
		return this.ehModoUsoObrigatorio;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public boolean isFormChanged() {
		return formChanged || isItemFormChanged() || isItemsChanged();
	}

	public void setFormChanged(boolean formChanged) {
		this.formChanged = formChanged;
	}

	public void setFormChanged() {
		setFormChanged(true);
	}

	public boolean isItemFormChanged() {
		return itemFormChanged;
	}

	public void setItemFormChanged(boolean itemFormChanged) {
		this.itemFormChanged = itemFormChanged;
	}	

	public void setItemFormChanged() {
		setItemFormChanged(true);
	}

	public boolean isItemsChanged() {
		return itemsChanged;
	}

	public void setItemsChanged(boolean itemsChanged) {
		this.itemsChanged = itemsChanged;
	}
}
