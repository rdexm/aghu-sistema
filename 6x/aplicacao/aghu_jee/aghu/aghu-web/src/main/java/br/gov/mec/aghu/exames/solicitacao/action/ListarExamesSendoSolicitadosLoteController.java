package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.business.SelecionarRadioDefaultManager;
import br.gov.mec.aghu.exames.solicitacao.business.TipoCampoDataHoraISE;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.LoteDefaultVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.TipoLoteVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
@SelectionQualifier
public class ListarExamesSendoSolicitadosLoteController extends ListarExamesSendoSolicitadosController {

	private static final long serialVersionUID = -4990868492102512373L;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	private DominioSolicitacaoExameLote radioTipoLote;
	private TipoLoteVO tipoLoteVO;
	private List<TipoLoteVO> listaLotes;
	private List<UnfExecutaSinonimoExameVO> listaExamesLote = null;
	private List<UnfExecutaSinonimoExameVO> listaExamesLoteSemPermissao = null;
	private Short tipoLoteSeq;
	//	
	//	@In(create = true, value = "listarExamesSendoSolicitadosController")
	//	private ListarExamesSendoSolicitadosController listarExamesSendoSolicitadosController;
	
	@Inject
	private SolicitacaoExameController solicitacaoExameController;

	private Map<String, ArrayList<UnfExecutaSinonimoExameVO>> exames = new HashMap<String, ArrayList<UnfExecutaSinonimoExameVO>>();
	private String exameSiglaSelecionado;
	private boolean habilitarListExames = false;

	//filtros aba situação
	//40284 - Por padrão deverá trazer o checkbox urgente como true.
	private Boolean checkUrgente = Boolean.FALSE;
	private Date dataProgr = new Date();
	private AelSitItemSolicitacoes situacao;
	private Boolean calendar = Boolean.TRUE;

	//#2253
	private Integer numeroAmostra;
	private Date intervaloHoras;
	private Integer intervaloDias;
		
	//#31396 LABEL_MODAL_EXAMES_SEM_PERMISSAO
	private Boolean exibirModalExamesSemPermissao;
	private String modalMessage;
	private String modalListaExames;
	
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void renderAbaPorLote(SolicitacaoExameVO solicEx) {
		this.habilitarListExames=false;
		this.initController(solicEx);
		this.initRadios();
	}

	private void initRadios() {
		SelecionarRadioDefaultManager selecionarRadioDefault = new SelecionarRadioDefaultManager();

		LoteDefaultVO loteDefaultVO = selecionarRadioDefault.getRadioDefault(getExamesFacade(), getItemSolicitacaoExameVo().getSolicitacaoExameVO());
		this.setRadioTipoLote(loteDefaultVO.getTipoLoteDefault());

		valueChangeRadioTipoLote();//Carrega os lotes na inicialização

		setTipoLoteSeq(loteDefaultVO.getLoteDefault() == null ? null : loteDefaultVO.getLoteDefault().getSeq());

		if (getTipoLoteSeq() != null) {
			Integer seqAtendimento = null;
			if (this.getSolicitacaoExameVo() != null && this.getSolicitacaoExameVo().getAtendimento() != null && this.getSolicitacaoExameVo().getAtendimento().getSeq() != null){
				seqAtendimento = this.getSolicitacaoExameVo().getAtendimento().getSeq();
			}				
			listaExamesLote = this.solicitacaoExameFacade.pesquisaUnidadeExecutaSinonimoExameLote(getTipoLoteSeq(), seqAtendimento, solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem(),solicitacaoExameController.isOrigemInternacao() );
		}		
		this.setExibirModalExamesSemPermissao(false);
		this.setModalMessage(null);
		this.setExibirModalExamesSemPermissao(false);
		this.setModalMessage(null);

	}

	public DominioSolicitacaoExameLote getRadioTipoLote() {
		return radioTipoLote;
	}

	public void setRadioTipoLote(DominioSolicitacaoExameLote radioTipoLote) {
		this.radioTipoLote = radioTipoLote;
	}

	public void valueChangeRadioTipoLote() {
		setTipoLoteSeq(null);
		initController();

		listaLotes = solicitacaoExameFacade.getDadosLote(this.getRadioTipoLote());
		clearExames();
	}

	public String getLabelComboTipoLote() {
		String labelComboTipoLote = "";
		switch (getRadioTipoLote()) {
		case E:
			labelComboTipoLote = this.getBundle().getString("LABEL_COMBO_TIPO_LOTE_ESPECIALIDADES");			
			break;
		case U:
			labelComboTipoLote = this.getBundle().getString("LABEL_COMBO_TIPO_LOTE_UNIDADES");			
			break;
		case G:
			labelComboTipoLote = this.getBundle().getString("LABEL_COMBO_TIPO_LOTE_GRUPOS");			
			break;
		default:
			break;
		}
		return labelComboTipoLote;
	}

	public String getTitleComboTipoLote() {
		String message = "";
		switch (getRadioTipoLote()) {
		case E:
			message = this.getBundle().getString("TITLE_COMBO_TIPO_LOTE_ESPECIALIDADES");			
			break;
		case U:
			message = this.getBundle().getString("TITLE_COMBO_TIPO_LOTE_UNIDADES");			
			break;
		case G:
			message = this.getBundle().getString("TITLE_COMBO_TIPO_LOTE_GRUPOS");			
			break;
		default:
			break;
		}
		return message;
	}

	public void reloadExames() {
		Boolean isSus;
		Short seqUnidade;
		clearExames();
		if (tipoLoteSeq != null && tipoLoteSeq != -1) {
			// Vericar convenio SUS e pegar valor de Unidade Funcional
			if(this.getSolicitacaoExameVo() != null){
				if(this.getSolicitacaoExameVo().getUnidadeFuncional() != null){
					seqUnidade = this.getSolicitacaoExameVo().getUnidadeFuncional().getSeq();
					isSus = this.solicitacaoExameFacade.verificaConvenioSus(this.getSolicitacaoExameVo().getAtendimento(), this.getSolicitacaoExameVo().getAtendimentoDiverso());
					Integer seqAtendimento = null;
					if (this.getSolicitacaoExameVo() != null && this.getSolicitacaoExameVo().getAtendimento() != null && this.getSolicitacaoExameVo().getAtendimento().getSeq() != null){
						seqAtendimento = this.getSolicitacaoExameVo().getAtendimento().getSeq();
					}				
					listaExamesLote = this.solicitacaoExameFacade.pesquisaUnidadeExecutaSinonimoExameLote(tipoLoteSeq,seqUnidade,isSus, seqAtendimento, solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem(),solicitacaoExameController.isOrigemInternacao());
					listaExamesLoteSemPermissao = this.solicitacaoExameFacade.pesquisaUnidadeExecutaSinonimoExameLoteSemPermissoes(tipoLoteSeq,listaExamesLote, seqAtendimento, solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem(),solicitacaoExameController.isOrigemInternacao());
					if(listaExamesLote.size() > 0){
						obterMessagemListaExamesSemPermissao();
						this.setExibirModalExamesSemPermissao(true);
					}else{
						this.setModalMessage(null);
						this.setModalListaExames(null);
						this.setExibirModalExamesSemPermissao(false);
					}
				}
			}	
		}
		
		List<ItemSolicitacaoExameVO> listaItemSolicitacaoExame = solicitacaoExameController.getListaItemSolicitacaoExame();
		List<UnfExecutaSinonimoExameVO> listaAuxiliar = new ArrayList<UnfExecutaSinonimoExameVO>(); 
		listaAuxiliar.addAll(listaExamesLote);
		
		if (listaItemSolicitacaoExame != null && !listaItemSolicitacaoExame.isEmpty()
				&& listaExamesLote!=null && !listaExamesLote.isEmpty()) {
			
			for (UnfExecutaSinonimoExameVO itemUnfExecutaSinonimoExameVO : listaAuxiliar) {
				for (ItemSolicitacaoExameVO itemSolicitacaoExameVO : listaItemSolicitacaoExame) {
					if (itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().equals(itemUnfExecutaSinonimoExameVO.getUnfExecutaExame())) {
						listaExamesLote.remove(itemUnfExecutaSinonimoExameVO);
					}
				}
			}
		}

		if(listaExamesLote!=null && !listaExamesLote.isEmpty()){
			habilitarListExames = true;
		}else{
			habilitarListExames = false;
			
			if (solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem() && getTipoLoteSeq() != null){
				apresentarMsgNegocio(Severity.INFO, this.getBundle().getString("USUARIO_SEM_PROTOCOLO_EXAMES_GRUPO"));
			}
		}

	}

	private void obterMessagemListaExamesSemPermissao() {
		
		String mensagem = "";
		String loteExame = "";		
		StringBuffer listaExamesSemPermissao = new StringBuffer();
		for (TipoLoteVO tipoLoteVO : this.getListaLotes()) {
			if(tipoLoteVO != null){
				if(tipoLoteVO.getLeuSeq().intValue() == tipoLoteSeq.intValue()){
					loteExame = tipoLoteVO.getDescricao();
				}
			}
		}		
		Integer qtdExames = 0;
		
		for (UnfExecutaSinonimoExameVO  unfExecutaSinonimoExameVO : listaExamesLoteSemPermissao) {
			boolean achou = false;
			for (UnfExecutaSinonimoExameVO vo : listaExamesLote) {
				if (vo.getDescricaoExameFormatada().equalsIgnoreCase(unfExecutaSinonimoExameVO.getDescricaoExameFormatada())){
					achou = true;
				}
			}
			if (!achou){
				listaExamesSemPermissao.append(unfExecutaSinonimoExameVO.getDescricaoExameFormatada());
				listaExamesSemPermissao.append("; \n ");
				qtdExames++;
			}
		}
		mensagem = this.getBundle().getString("LABEL_MODAL_EXAMES_SEM_PERMISSAO").replace("{0}", loteExame).
				replace("{1}", qtdExames.toString() != null?qtdExames.toString(): "").
				replace("{2}", getSolicitacaoExameVo().getUnidadeFuncional().getSeq() != null? getSolicitacaoExameVo().getUnidadeFuncional().getSeq().toString() + " - " +getSolicitacaoExameVo().getUnidadeFuncional().getDescricao(): ""); 
		this.setModalMessage(mensagem);
		this.setModalListaExames(listaExamesSemPermissao.toString());		
	}

	public void ocultarModalExamesSemPermissao(){
		setExibirModalExamesSemPermissao(Boolean.FALSE);
	}
	
	@Override
	/**
	 * Escolhe as abas que irá validar.
	 * @return
	 */
	protected List<ISECamposObrigatoriosValidator> getAbasValidators() {
		List<ISECamposObrigatoriosValidator> validators = new ArrayList<ISECamposObrigatoriosValidator>();

		if (getItemSolicitacaoExameVo().getMostrarAbaConcentO2()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaConcentO2LoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaIntervColeta()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaIntervColetaLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaNoAmostras()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaNoAmostrasLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaRegMatAnalise()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaRegMatAnaliseLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaTipoTransporte()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaTipoTransporteLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaQuestionario()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaQuestionarioValidator(getItemSolicitacaoExameVo(), this.getBundle(), 1);
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaQuestionarioSismama()) {
			ItemSolicitacaoExameVO itemVO = new ItemSolicitacaoExameVO();
			itemVO.setQuestionarioSismama(getQuestionarioSismama());
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaQuestionarioSismamaValidator(getItemSolicitacaoExameVo(),
					this.getBundle(), "1");
			validators.add(itemSolicitacaoExameValidator);
		}

		return validators;
	}

	/**
	 * Metodo para adicionar item de solicitacao exame na lista.
	 * @throws MECBaseException 
	 */
	//@Restrict("#{s:hasPermission('solicitarExamesLote','executar')}")
	public void adicionarItemSolicitacaoExameLote() throws BaseException {
		ocultarModalExamesSemPermissao();
		
		if (Boolean.FALSE.equals(this.isExisteItemExameLoteSelecionado())) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_AO_ADICIONAR_EXAME_LOTE");
		}
		//Se adicionou remove da lista todos os sinonimos daquele exame
		List<UnfExecutaSinonimoExameVO> listaAuxiliar = new ArrayList<UnfExecutaSinonimoExameVO>();
		List<UnfExecutaSinonimoExameVO> listaAuxiliar2 = new ArrayList<UnfExecutaSinonimoExameVO>();
		boolean adicionou = false;
		if (getUnfExecutaExame() != null && listaExamesLote != null) {
			listaAuxiliar.addAll(listaExamesLote);
			for (UnfExecutaSinonimoExameVO unfExeExameVO : listaExamesLote) {
				if (unfExeExameVO.isSelecionado()) {
					super.setUnfExecutaExame(unfExeExameVO, false);
					super.getItemSolicitacaoExameVo().setUrgente(unfExeExameVO.getCheckUrgente());
					super.getItemSolicitacaoExameVo().setDataProgramada(unfExeExameVO.getDataProgr()!=null?unfExeExameVO.getDataProgr():new Date());
					super.getItemSolicitacaoExameVo().setNumeroAmostra(unfExeExameVO.getNumeroAmostra());
					super.getItemSolicitacaoExameVo().setIntervaloHoras(unfExeExameVO.getIntervaloHoras());
					super.getItemSolicitacaoExameVo().setIntervaloDias(unfExeExameVO.getIntervaloDias());
					AelSitItemSolicitacoes situacaoExame = unfExeExameVO.getSituacao();
					if(situacaoExame==null){
						    situacaoExame = this.solicitacaoExameFacade.obterSituacaoExameSugestao(this.getSolicitacaoExameVo().getUnidadeFuncional(),
							this.getSolicitacaoExameVo().getAtendimento(), this.getSolicitacaoExameVo().getAtendimentoDiverso(), this.getItemSolicitacaoExameVo()
							.getUnfExecutaExame().getUnfExecutaExame(), obterUnidadeTrabalho(), this.getItemSolicitacaoExameVo(), getSolicitacaoExameVo());
					}
					super.getItemSolicitacaoExameVo().setSituacaoCodigo(situacaoExame);
					//			listaAuxiliar.remove(unfExeExameVO);
					adicionou = super.adicionarItemSolicitacaoExame();
					if(adicionou){//só exclui da lista de exames se adicionou com sucesso.
						listaAuxiliar2.add(unfExeExameVO);
					}
				}
			}
		}

		/**
		 *  Remove da lista principal os exames que já estão sendo solicitados.
		 *  Existe exames que tem exames dependentes que já estão sendo solicitados por isso é removido da listagem principal.
		 */
		for (UnfExecutaSinonimoExameVO unfExeExameVO2 : listaAuxiliar2) {
			for (UnfExecutaSinonimoExameVO unfExeExameVO : listaExamesLote) {
				if(unfExeExameVO2.getUnfExecutaExame().equals(unfExeExameVO.getUnfExecutaExame())){
					listaAuxiliar.remove(unfExeExameVO);
				}
			}
		}
		if (!listaAuxiliar2.isEmpty()) {
			listaExamesLote.clear();
			listaExamesLote.addAll(listaAuxiliar);
		}

        desmarcaExameDaListaQueNaoCampoObrigatorioPreenchido();
	}

    private void desmarcaExameDaListaQueNaoCampoObrigatorioPreenchido() {

        if(listaExamesLote != null){
            for (UnfExecutaSinonimoExameVO unfExeExameVO : this.listaExamesLote) {
                if (unfExeExameVO.isSelecionado()) {
                    unfExeExameVO.setSelecionado(false);
                }
            }
        }

    }

	
	private boolean isExisteItemExameLoteSelecionado() {
		if(listaExamesLote != null){
			for (UnfExecutaSinonimoExameVO unfExeExameVO : this.listaExamesLote) {
				if (unfExeExameVO.isSelecionado()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected ISECamposObrigatoriosValidator getValidatorISE() {
		return new ItemSolicitacaoExameLoteValidator(getItemSolicitacaoExameVo(), this.getBundle());
	}

	private void clearExames() {
		listaExamesLote = new ArrayList<UnfExecutaSinonimoExameVO>();
		setUnfExecutaExame(null);
		initController();
	}


	@Override
	public String getAbaId(){
		return "ABA2_";
	}	


	public TipoLoteVO getTipoLoteVO() {
		return tipoLoteVO;
	}

	public void setTipoLoteVO(TipoLoteVO tipoLoteVO) {
		this.tipoLoteVO = tipoLoteVO;
	}

	public void setTipoLoteSeq(Short tipoLoteSeq) {
		this.tipoLoteSeq = tipoLoteSeq;
	}

	public Short getTipoLoteSeq() {
		return this.tipoLoteSeq;
	}

	public List<TipoLoteVO> getListaLotes() {
		return listaLotes;
	}

	public void setListaLotes(List<TipoLoteVO> listaLotes) {
		this.listaLotes = listaLotes;
	}

	public List<UnfExecutaSinonimoExameVO> getListaExamesLote() {
		return listaExamesLote;
	}

	public void setListaExamesLote(List<UnfExecutaSinonimoExameVO> listaExamesLote) {
		this.listaExamesLote = listaExamesLote;
	}

	public void selecionaItemExame(String codigoSoeSelecionado, UnfExecutaSinonimoExameVO vo) {
		super.setUnfExecutaExame(vo, true);
		this.setExameSiglaSelecionado(codigoSoeSelecionado);
		setFiltrosAbaSituacao(vo);
		
		if(this.exames.containsKey(vo)){
			if(this.exames.get(codigoSoeSelecionado).contains(vo)){

				this.exames.get(codigoSoeSelecionado).remove(vo);

				if(this.exames.get(codigoSoeSelecionado).size()==0){
					this.exames.remove(codigoSoeSelecionado);
				}
			}else{
				this.exames.get(codigoSoeSelecionado).add(vo);
			}
		}else{
			this.exames.put(codigoSoeSelecionado, new ArrayList<UnfExecutaSinonimoExameVO>());
			this.exames.get(codigoSoeSelecionado).add(vo);
		}
	}

	private void setFiltrosAbaSituacao(UnfExecutaSinonimoExameVO vo) {
		if(vo.isEditado()){
			setCheckUrgente(vo.getCheckUrgente());
			setDataProgr(vo.getDataProgr()!=null?vo.getDataProgr():new Date()); 
			setSituacao(vo.getSituacao());
			setCalendar(vo.getCalendar());
		}else{
			//#40284 - Setar valores para todos os exames somente ao alterar um item.
			//setCheckUrgente(super.getItemSolicitacaoExameVo().getUrgente()!=null?super.getItemSolicitacaoExameVo().getUrgente():false);
			//setDataProgr(super.getItemSolicitacaoExameVo().getDataProgramada()!=null?super.getItemSolicitacaoExameVo().getDataProgramada():new Date()); 
			//setSituacao(super.getItemSolicitacaoExameVo().getSituacaoCodigo()!=null?super.getItemSolicitacaoExameVo().getSituacaoCodigo():vo.getSituacao());
			//setCalendar(super.getItemSolicitacaoExameVo().getCalendar()!=null?super.getItemSolicitacaoExameVo().getCalendar():vo.getCalendar());
			alterar();
		}
	}

	@Override
	public void setUnfExecutaExame(UnfExecutaSinonimoExameVO unfExecutaExame) {
		if (unfExecutaExame == null) {
			super.setUnfExecutaExame(null);
			super.posDeleteActionSbExames();
		}
	}

	public void validarExame(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			UnfExecutaSinonimoExameVO unfExecutaExame = (UnfExecutaSinonimoExameVO)event.getNewValue();
			super.setUnfExecutaExame(unfExecutaExame, true);
		}
	}

	public void alterar(){

		if (getUnfExecutaExame() != null && listaExamesLote != null) {
			for (UnfExecutaSinonimoExameVO unfExeExameVO : listaExamesLote) {
				if(unfExeExameVO.isSelecionado()){
					unfExeExameVO.setSituacao(situacao);
					unfExeExameVO.setCheckUrgente(checkUrgente);
					unfExeExameVO.setDataProgr(dataProgr);
					unfExeExameVO.setEditado(Boolean.TRUE);
					unfExeExameVO.setCalendar(getCalendar());
				}
			}
		}
	}
	
	
	/**
	 * Método invocado quando o usuário marca/desmarca o checkbox "Urgente".
	 */
	public void checkUrgenteLote() {
		if (this.getItemSolicitacaoExameVo().getUnfExecutaExame() != null) {
			try {
				getItemSolicitacaoExameVo().setUrgente(getCheckUrgente());
				getItemSolicitacaoExameVo().setDataProgramada(new Date());
				setDataProgr(new Date());
				AelSitItemSolicitacoes situacao = this.solicitacaoExameFacade.obterSituacaoExameSugestao(this.getSolicitacaoExameVo().getUnidadeFuncional(),
						this.getSolicitacaoExameVo().getAtendimento(), this.getSolicitacaoExameVo().getAtendimentoDiverso(), this.getItemSolicitacaoExameVo()
						.getUnfExecutaExame().getUnfExecutaExame(), obterUnidadeTrabalho(), this.getItemSolicitacaoExameVo(), getSolicitacaoExameVo());
				this.getItemSolicitacaoExameVo().setSituacaoCodigo(situacao);
				setSituacao(situacao);
				reLoadDataHoraProgramadaLote();
				alterar();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void reLoadDataHoraProgramadaLote() {
		if (this.getItemSolicitacaoExameVo().getUnfExecutaExame() != null) {
			try {
				this.desenhaTipoCampoDataLote();

				//Se estiver sendo colocado calendar, atualizar para data atual.
				if (getItemSolicitacaoExameVo().getCalendar()) {
					setDataProgr(new Date());
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	private void desenhaTipoCampoDataLote() throws BaseException {
		if (getItemSolicitacaoExameVo() != null && getSolicitacaoExameVo() != null) {
			Boolean calendar = Boolean.TRUE;
			calendar = (solicitacaoExameFacade.verificarCampoDataHora(getItemSolicitacaoExameVo(), getSolicitacaoExameVo().getUnidadeFuncional()) == TipoCampoDataHoraISE.CALENDAR);
			getItemSolicitacaoExameVo().setCalendar(calendar);
			setCalendar(calendar);
		}

		if (!getItemSolicitacaoExameVo().getCalendar()) {
			sugerirUltimoHorarioRotinaLote();
		}
	}

	private void sugerirUltimoHorarioRotinaLote() {
		if (getItemSolicitacaoExameVoSugestao() != null && !this.getItemSolicitacaoExameVo().getCalendar()) {
			setDataProgr(getItemSolicitacaoExameVoSugestao().getDataProgramada());
		}
	}
	
	public void zerarController() {
		radioTipoLote = null;
		tipoLoteVO = null;
		listaLotes = null;
		listaExamesLote = null;
		listaExamesLoteSemPermissao = null;
		tipoLoteSeq = null;

		exames = new HashMap<String, ArrayList<UnfExecutaSinonimoExameVO>>();
		exameSiglaSelecionado = null;
		habilitarListExames = false;

		//filtros aba situação
		//40284 - Por padrão deverá trazer o checkbox urgente como true.
		checkUrgente = Boolean.TRUE;
		dataProgr = new Date();
		situacao = null;
		calendar = Boolean.TRUE;

		//#2253
		numeroAmostra = null;
		intervaloHoras = null;
		intervaloDias = null;
			
		//#31396 LABEL_MODAL_EXAMES_SEM_PERMISSAO
		exibirModalExamesSemPermissao = null;
		modalMessage = null;
		modalListaExames = null;
	}
	
	public Map<String, ArrayList<UnfExecutaSinonimoExameVO>> getExames() {
		return exames;
	}

	public void setExames(Map<String, ArrayList<UnfExecutaSinonimoExameVO>> exames) {
		this.exames = exames;
	}

	public String getExameSiglaSelecionado() {
		return exameSiglaSelecionado;
	}

	public void setExameSiglaSelecionado(String exameSiglaSelecionado) {
		this.exameSiglaSelecionado = exameSiglaSelecionado;
	}

	public boolean isHabilitarListExames() {
		return habilitarListExames;
	}

	public void setHabilitarListExames(boolean habilitarListExames) {
		this.habilitarListExames = habilitarListExames;
	}

	public Boolean getCheckUrgente() {
		return checkUrgente;
	}

	public void setCheckUrgente(Boolean checkUrgente) {
		this.checkUrgente = checkUrgente;
	}

	public Date getDataProgr() {
		return dataProgr;
	}

	public void setDataProgr(Date dataProgr) {
		this.dataProgr = dataProgr;
	}

	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}

	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}

	public Boolean getCalendar() {
		return calendar;
	}

	public void setCalendar(Boolean calendar) {
		this.calendar = calendar;
	}
	
	public Integer getNumeroAmostra() {
		return numeroAmostra;
	}

	public void setNumeroAmostra(Integer numeroAmostra) {
		this.numeroAmostra = numeroAmostra;
	}

	public Date getIntervaloHoras() {
		return intervaloHoras;
	}

	public void setIntervaloHoras(Date intervaloHoras) {
		this.intervaloHoras = intervaloHoras;
	}

	public Integer getIntervaloDias() {
		return intervaloDias;
	}

	public void setIntervaloDias(Integer intervaloDias) {
		this.intervaloDias = intervaloDias;
	}

	public String getModalMessage() {
		return modalMessage;
	}

	public void setModalMessage(String modalMessage) {
		this.modalMessage = modalMessage;
	}

	public String getModalListaExames() {
		return modalListaExames;
	}

	public void setModalListaExames(String modalListaExames) {
		this.modalListaExames = modalListaExames;
	}

	public Boolean getExibirModalExamesSemPermissao() {
		return exibirModalExamesSemPermissao;
	}

	public void setExibirModalExamesSemPermissao(
			Boolean exibirModalExamesSemPermissao) {
		this.exibirModalExamesSemPermissao = exibirModalExamesSemPermissao;
		if(!exibirModalExamesSemPermissao){
			this.modalMessage = null;
		}
	}	
}
