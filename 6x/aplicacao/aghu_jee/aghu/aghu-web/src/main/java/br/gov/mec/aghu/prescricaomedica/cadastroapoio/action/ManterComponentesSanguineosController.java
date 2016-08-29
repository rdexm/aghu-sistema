package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

public class ManterComponentesSanguineosController extends ActionController{

	private static final long serialVersionUID = 2862928436768243397L;
	
	private static final Log LOG = LogFactory.getLog(ManterComponentesSanguineosController.class);
	
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final String PAGE_LIST = "prescricaomedica-pesquisarComponentesSanguineos";
	private static final String PESQUISAR_VALIDADE_AMOSTRA = "bancodesangue-pesquisarValidadeAmostra";
	private static final String PESQUISAR_EXAMES_DA_HEMOTERAPIA = "prescricaomedica-pesquisarExamesDaHemoterapia";
	private static final String JUSTIFICATIVAS_USO_HEMOTERAPICO = "bancodesangue-pesquisarJustificativasUsoHemoterapico";
	private static final String ORIENTACOES = "bancodesangue-pesquisarOrientacoes";
	private static final String PESQUISAR_PESO_FORNECEDOR = "bancodesangue-pesquisarPesoFornecedor";

	// 34742
	
	private Boolean chamarCancelar;
	
	private AbsComponenteSanguineo componentesSanguineo;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	private boolean editando = false;
	private String codigo;
	private Boolean situacao;
	
	private boolean obrigatoriedadeSigtap;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	// 38817

	@Inject
	private RelacionarPHISSMController relacionarPHISSMController;
	
	@EJB
	private IPermissionService permissionService;

	private Boolean exibirPainelInferior = false;
	private Boolean alterouItensProcedimento = false;
	private VFatConvPlanoGrupoProcedVO convenio;
	private List<FatConvGrupoItemProced> convGrupoItemProcedList;
	private List<FatConvGrupoItemProced> convGrupoItemProcedListOriginal;
	private List<FatConvGrupoItemProced> convGrupoItemProcedListRemover;
	private Integer phi;
	private FatConvenioSaudePlano plano;
	private FatConvGrupoItemProced fatConvGrupoItemProcedSelecionado;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
		fatConvGrupoItemProcedSelecionado = null;

		if(componentesSanguineo == null){
			componentesSanguineo = new AbsComponenteSanguineo();
		}
		if(componentesSanguineo.getIndSituacao() != null) {
			this.situacao = componentesSanguineo.getIndSituacao().isAtivo();
		}
		this.codigo = componentesSanguineo.getCodigo();
		
		getIniciarProcedimentosRelacionados();
    
	}
	
	private void getIniciarProcedimentosRelacionados() {
		obrigatoriedadeSigtap = !perfilOrdenadorDespesa();
		exibirPainelInferior = exibirPainelInferior();
		this.chamarCancelar = false;
		this.alterouItensProcedimento = false;
		this.phi = null;

		if (exibirPainelInferior){
			relacionarPHISSMController.inicio();
			convGrupoItemProcedList = new ArrayList<FatConvGrupoItemProced>();
			convGrupoItemProcedListOriginal = new ArrayList<FatConvGrupoItemProced>();
			convGrupoItemProcedListRemover = new ArrayList<FatConvGrupoItemProced>();
			if (getPhi() != null) {
				List<FatProcedHospInternos> listaProcedimentosInternos = this.faturamentoFacade.listarPhi(null, null, null, null, null, this.codigo, null, null, null, 1);
				if (listaProcedimentosInternos != null && !listaProcedimentosInternos.isEmpty()) {
					relacionarPHISSMController.setProcedimentoInterno(listaProcedimentosInternos.get(0));
				}
				relacionarPHISSMController.pesquisarProcedimento();
				convenio = relacionarPHISSMController.obterConvenioSUS();	
				convGrupoItemProcedList = relacionarPHISSMController.getLista();
				for (FatConvGrupoItemProced fatConvGrupoItemProced : convGrupoItemProcedList) {
					convGrupoItemProcedListOriginal.add(fatConvGrupoItemProced);
				}
			} else {
				convenio = relacionarPHISSMController.obterConvenioSUS();
			}
		}
	}
	
	private boolean perfilOrdenadorDespesa() {
		return this.permissionService
		.usuarioTemPermissao(this.obterLoginUsuarioLogado(), 
				"ordenadorDespesas", "gravar");
	}

	private Boolean exibirPainelInferior() {
		Boolean exibirPanelInferior = false;
		AghParametros pExibirPainelInferior = null;
		try {
			pExibirPainelInferior = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_OBRIGATORIEDADE_SIGTAP);
		} catch (ApplicationBusinessException e) {
			getLogger().error(e);
		}
		if (pExibirPainelInferior != null) {
			exibirPanelInferior = "S".equals(pExibirPainelInferior.getVlrTexto());
		}
		return exibirPanelInferior;
	}	
	
	public String gravar(){
		
		if (bancoDeSangueFacade.existeAbsComponenteSanguineo(componentesSanguineo.getCodigo())) {
			editando = true;	
		} else {
			editando = false;
		}
		
		if (exibirPainelInferior && obrigatoriedadeSigtap && (convGrupoItemProcedList == null || convGrupoItemProcedList.isEmpty()) && !editando) {
			if (!gravarRelacionamentoPHIePROCEDSUS(obrigatoriedadeSigtap)) {
				return null;
			}
		}
		
		try{
			bancoDeSangueFacade.gravarRegistro(componentesSanguineo, editando);
			
			if(editando){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_COMPONENTE_SANGUINEO",componentesSanguineo.getDescricao());
			}else{
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_COMPONENTE_SANGUINEO", componentesSanguineo.getDescricao());
			}
			this.codigo = componentesSanguineo.getCodigo();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
		if (exibirPainelInferior) {
			if (!gravarRelacionamentoPHIePROCEDSUS(obrigatoriedadeSigtap) && !editando) {
				return null;
			}
		}
		
		iniciarVariaveisProcedimentosHospInternos();
		this.alterouItensProcedimento = false;
		this.chamarCancelar = false;
		return null;
	}
	
	public String validadeAmostra(){
		return PESQUISAR_VALIDADE_AMOSTRA;
	}
	
	
	public String visualizarExames() {
		try{
			bancoDeSangueFacade.verificaInativo(componentesSanguineo);
			return PESQUISAR_EXAMES_DA_HEMOTERAPIA;
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		
	}
	
	public String justificativas(){
		try{
			bancoDeSangueFacade.verificaInativo(componentesSanguineo);
			bancoDeSangueFacade.verificaJustificativa(componentesSanguineo);
			return JUSTIFICATIVAS_USO_HEMOTERAPICO;
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		
	}
	
	public String orientacoes(){
		try{
			bancoDeSangueFacade.verificaInativo(componentesSanguineo);
			return ORIENTACOES;
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		
	}
	
	public String pesosBolsa(){
		try{
			bancoDeSangueFacade.verificaInativo(componentesSanguineo);
			return PESQUISAR_PESO_FORNECEDOR;
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		
	}
	
	public String cancelar(){
		
		if(alterouItensProcedimento && !getChamarCancelar()){
			setChamarCancelar(Boolean.TRUE);
			return null;
		} else {
			this.codigo = null;
	    	this.componentesSanguineo = new AbsComponenteSanguineo();
	    	this.situacao = null;
	    	
			this.alterouItensProcedimento = false;
			iniciarVariaveisProcedimentosHospInternos();
			componentesSanguineo = null;
			this.editando = false;
	    	return PAGE_LIST;
		}	
	}
	
	// -----------
		// -- 38817 -- 
		// -----------	

	private void iniciarVariaveisProcedimentosHospInternos() {
		relacionarPHISSMController.setConvGrupoItemProced(new FatConvGrupoItemProced());
		relacionarPHISSMController.setItemProcedHospSus(null);
		relacionarPHISSMController.setEdicao(false);
	}
	
	public void adicionar() {
		if (getConvenio() == null) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_ADICIONAR_PROCEDIMENTO_SUS",
					"Convênio");
			return;
		}
		if (relacionarPHISSMController.getItemProcedHospSus() == null) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_ADICIONAR_PROCEDIMENTO_SUS",
					"Procedimento SUS");
			return;
		}

		relacionarPHISSMController.setConvenio(convenio);

		if (relacionarPHISSMController.getEdicao()) {
			//Remove o Item da Lista e adiciona na lista de itens a serem removidos no momento da gravação.
			excluirProcedimentoRelacionado(fatConvGrupoItemProcedSelecionado);

			FatConvenioSaudePlanoId fatConvenioSaudePlanoId = new FatConvenioSaudePlanoId();
			fatConvenioSaudePlanoId.setSeq(plano.getId().getSeq());
			FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
			fatConvenioSaudePlano.setId(fatConvenioSaudePlanoId);
			fatConvenioSaudePlano.setDescricao(plano.getDescricao());
			
			FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
			fatConvenioSaude.setCodigo(convenio.getCphCspCnvCodigo());
			fatConvenioSaude.setDescricao(convenio.getCnvDescricao());
			fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);
			
			popularValoresPadroesConvGrupoItemProced();
			FatConvGrupoItemProced fatConvGrupoItemProced = new FatConvGrupoItemProced(relacionarPHISSMController.getConvGrupoItemProced());
			fatConvGrupoItemProced.setItemProcedHospitalar(relacionarPHISSMController.getItemProcedHospSus());
			fatConvGrupoItemProced.setConvenioSaudePlano(fatConvenioSaudePlano);
			convGrupoItemProcedList.add(fatConvGrupoItemProced);
		} else {
			boolean add;
			for (VFatConvPlanoGrupoProcedVO plano : relacionarPHISSMController.listarPlanos("")) {
				FatConvenioSaudePlanoId fatConvenioSaudePlanoId = new FatConvenioSaudePlanoId();
				fatConvenioSaudePlanoId.setSeq(plano.getCphCspSeq());
				FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
				fatConvenioSaudePlano.setId(fatConvenioSaudePlanoId);
				fatConvenioSaudePlano.setDescricao(plano.getCspDescricao());
				
				FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
				fatConvenioSaude.setCodigo(convenio.getCphCspCnvCodigo());
				fatConvenioSaude.setDescricao(convenio.getCnvDescricao());
				fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

				popularValoresPadroesConvGrupoItemProced();
				FatConvGrupoItemProced fatConvGrupoItemProced = new FatConvGrupoItemProced(relacionarPHISSMController.getConvGrupoItemProced());
				fatConvGrupoItemProced.setItemProcedHospitalar(relacionarPHISSMController.getItemProcedHospSus());
				add = true;
				for (FatConvGrupoItemProced iterable_element : convGrupoItemProcedList) {
					if (iterable_element.getItemProcedHospitalar().getCodTabela().equals(fatConvGrupoItemProced.getItemProcedHospitalar().getCodTabela())
							&& iterable_element.getConvenioSaudePlano().getId().getSeq().equals(plano.getCphCspSeq())) {
						add = false;
						break;
					}
				}
				if (add) {
					fatConvGrupoItemProced.setConvenioSaudePlano(fatConvenioSaudePlano);
					convGrupoItemProcedList.add(fatConvGrupoItemProced);
				} else {
					apresentarMsgNegocio(Severity.ERROR, "FAT_00073");
				}
			}
		}
		alterouItensProcedimento = true;
		iniciarVariaveisProcedimentosHospInternos();
	}
	
	private Boolean gravarRelacionamentoPHIePROCEDSUS(Boolean obrigatoriedadeSigtap) {
		if (convGrupoItemProcedList == null || convGrupoItemProcedList.isEmpty()) {
			if (obrigatoriedadeSigtap) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PROCEDIMENTO_OBRIGATORIO");
				return false;
			} else {
				for (FatConvGrupoItemProced fatConvGrupoItemProced : convGrupoItemProcedListRemover) {
					relacionarPHISSMController.removerProcedimento(fatConvGrupoItemProced);
				}
				relacionarPHISSMController.criarNotificacoesUsuarios(getPhi(), null);
				return true;
			}
		}
		try {
			List<FatConvGrupoItemProced> convGrupoItemProcedListClone = clonarConvGrupoItemProcedList();
			for (FatConvGrupoItemProced fatConvGrupoItemProced : convGrupoItemProcedListRemover) {
				if (fatConvGrupoItemProced.getId() != null) {
					relacionarPHISSMController.removerProcedimento(fatConvGrupoItemProced);
				}
				convGrupoItemProcedList.remove(fatConvGrupoItemProced);
			}
			List<FatConvGrupoItemProced> convGrupoItemDatabase = relacionarPHISSMController.getLista();
			for (Iterator<FatConvGrupoItemProced> iterator = convGrupoItemProcedListClone.iterator(); iterator.hasNext();) {
				FatConvGrupoItemProced fatConvGrupoItemProced = iterator.next();
				List<FatProcedHospInternos> listaProcedimentosInternos = this.faturamentoFacade.listarPhi(null, null, null, null, null, componentesSanguineo.getCodigo(), null, null, null, 1);
				if (!convGrupoItemDatabase.contains(fatConvGrupoItemProced)) {
					if (convenio != null) {
						relacionarPHISSMController.setConvenio(convenio);
					}
					VFatConvPlanoGrupoProcedVO plano = new VFatConvPlanoGrupoProcedVO();
					plano.setCphCspSeq(fatConvGrupoItemProced.getConvenioSaudePlano().getId().getSeq());
					plano.setCspDescricao(fatConvGrupoItemProced.getConvenioSaudePlano().getDescricao());
					relacionarPHISSMController.setPlano(plano);
					relacionarPHISSMController.setItemProcedHospSus(fatConvGrupoItemProced.getItemProcedHospitalar());
					if (listaProcedimentosInternos != null && !listaProcedimentosInternos.isEmpty()) {
						relacionarPHISSMController.setProcedHospInterno(listaProcedimentosInternos.get(0));
					}
					relacionarPHISSMController.setConvGrupoItemProced(fatConvGrupoItemProced);
					relacionarPHISSMController.adicionarProcedimento();
				}
			}
			relacionarPHISSMController.criarNotificacoesUsuarios(getPhi(),convGrupoItemProcedList);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return false;
		} catch (Exception e) {
			getLogger().error("Exceção capturada: ", e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_RELACIONAMENTO_PHI_SSM");
			return false;
		}
		return true;
	}
	
	private List<FatConvGrupoItemProced> clonarConvGrupoItemProcedList() {
		List<FatConvGrupoItemProced> convGrupoItemProcedListClone = new ArrayList<FatConvGrupoItemProced>();
		for (FatConvGrupoItemProced fatConvGrupoItemProced : convGrupoItemProcedList) {
			if (!convGrupoItemProcedListOriginal.contains(fatConvGrupoItemProced)) {
				FatConvGrupoItemProced item = new FatConvGrupoItemProced(fatConvGrupoItemProced);
				item.setItemProcedHospitalar(fatConvGrupoItemProced.getItemProcedHospitalar());
				item.setOperacao(DominioOperacoesJournal.INS);
				if (fatConvGrupoItemProced.getIndCobrancaFracionada() != null) {
					item.setIndCobrancaFracionada(fatConvGrupoItemProced.getIndCobrancaFracionada());
				} else {
					item.setIndCobrancaFracionada(false);
				}
				if (fatConvGrupoItemProced.getIndExigeAutorPrevia() != null) {
					item.setIndExigeAutorPrevia(fatConvGrupoItemProced.getIndExigeAutorPrevia());
				} else {
					item.setIndExigeAutorPrevia(false);
				}
				if (fatConvGrupoItemProced.getIndExigeJustificativa() != null) {
					item.setIndExigeJustificativa(fatConvGrupoItemProced.getIndExigeJustificativa());
				} else {
					item.setIndExigeJustificativa(false);
				}
				if (fatConvGrupoItemProced.getIndExigeNotaFiscal() != null) {
					item.setIndExigeNotaFiscal(fatConvGrupoItemProced.getIndExigeNotaFiscal());
				} else {
					item.setIndExigeNotaFiscal(false);
				}
				if (fatConvGrupoItemProced.getIndImprimeLaudo() != null) {
					item.setIndImprimeLaudo(fatConvGrupoItemProced.getIndImprimeLaudo());
				} else {
					item.setIndImprimeLaudo(false);
				}
				if (fatConvGrupoItemProced.getIndInformaTempoTrat() != null) {
					item.setIndInformaTempoTrat(fatConvGrupoItemProced.getIndInformaTempoTrat());
				} else {
					item.setIndInformaTempoTrat(false);
				}
				if (fatConvGrupoItemProced.getIndPaga() != null) {
					item.setIndPaga(fatConvGrupoItemProced.getIndPaga());
				} else {
					item.setIndPaga(false);
				}
				if (fatConvGrupoItemProced.getTempoValidade() != null) {
					item.setTempoValidade(fatConvGrupoItemProced.getTempoValidade());
				} else {
					item.setTempoValidade(Short.valueOf("0"));
				}
				item.setConvenioSaudePlano(fatConvGrupoItemProced.getConvenioSaudePlano());
				convGrupoItemProcedListClone.add(item);
			}
		}
		return convGrupoItemProcedListClone;
	}
	
	private void popularValoresPadroesConvGrupoItemProced() {
		relacionarPHISSMController.getConvGrupoItemProced().setIndCobrancaFracionada(false);
		relacionarPHISSMController.getConvGrupoItemProced().setIndExigeAutorPrevia(false);
		relacionarPHISSMController.getConvGrupoItemProced().setIndExigeJustificativa(false);
		relacionarPHISSMController.getConvGrupoItemProced().setIndExigeNotaFiscal(false);
		relacionarPHISSMController.getConvGrupoItemProced().setIndImprimeLaudo(false);
		relacionarPHISSMController.getConvGrupoItemProced().setIndInformaTempoTrat(false);
		relacionarPHISSMController.getConvGrupoItemProced().setIndPaga(false);
	}

	public void excluirProcedimentoRelacionado(FatConvGrupoItemProced item) {
		relacionarPHISSMController.setAlterou(true);
		convGrupoItemProcedListRemover.add(item);
		convGrupoItemProcedList.remove(item);
		alterouItensProcedimento = true;
	}

	public void editarProcedimentoRelacionado(FatConvGrupoItemProced item) {
		editando = true;
		fatConvGrupoItemProcedSelecionado = item;
		relacionarPHISSMController.setConvGrupoItemProced(item);
		relacionarPHISSMController.setProcedHospInterno(item.getProcedimentoHospitalarInterno());
		relacionarPHISSMController.setItemProcedHospSus(item.getItemProcedHospitalar());
		plano = item.getConvenioSaudePlano();
		
		FatConvenioSaude fatConvenioSaude = item.getConvenioSaudePlano().getConvenioSaude();
		VFatConvPlanoGrupoProcedVO vo = new VFatConvPlanoGrupoProcedVO();
		vo.setCphCspCnvCodigo(fatConvenioSaude.getCodigo());
		vo.setCnvDescricao(fatConvenioSaude.getDescricao());
		convenio = vo;
		relacionarPHISSMController.setEdicao(true);
	}
	
	public void cancelarEdicao() {
		editando = false;
		relacionarPHISSMController.cancelarEdicao();
		fatConvGrupoItemProcedSelecionado = null;

	}

	public List<VFatConvPlanoGrupoProcedVO> listarConvenios(String objPesquisa) {
		return  this.returnSGWithCount(this.faturamentoFacade.listarConvenios(objPesquisa, relacionarPHISSMController.getCpgGrcSeq(), relacionarPHISSMController.getTabela().getCphPhoSeq()),listarConveniosCount(objPesquisa));
	}

	public Long listarConveniosCount(String objPesquisa) {
		return this.faturamentoFacade.listarConveniosCount(objPesquisa, relacionarPHISSMController.getCpgGrcSeq(), relacionarPHISSMController.getTabela().getCphPhoSeq());
	}

	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(String objPesquisa){
		return  this.returnSGWithCount(relacionarPHISSMController.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(objPesquisa),listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa));
	}
	
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(String objPesquisa){
		return relacionarPHISSMController.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount((String)objPesquisa);
	}

	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setComponentesSanguineo(AbsComponenteSanguineo componentesSanguineo) {
		this.componentesSanguineo = componentesSanguineo;
	}

	public AbsComponenteSanguineo getComponentesSanguineo() {
		return componentesSanguineo;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public Boolean getChamarCancelar() {
		return chamarCancelar;
	}

	public void setChamarCancelar(Boolean chamarCancelar) {
		this.chamarCancelar = chamarCancelar;
	}
	
	public String alterarValorChamarCancelar(){
		this.setChamarCancelar(!chamarCancelar);
		return null;
	}

	public RelacionarPHISSMController getRelacionarPHISSMController() {
		return relacionarPHISSMController;
	}

	public Boolean getExibirPainelInferior() {
		return exibirPainelInferior;
	}

	public void setExibirPainelInferior(Boolean exibirPainelInferior) {
		this.exibirPainelInferior = exibirPainelInferior;
	}

	public Boolean getAlterouItensProcedimento() {
		return alterouItensProcedimento;
	}

	public void setAlterouItensProcedimento(Boolean alterouItensProcedimento) {
		this.alterouItensProcedimento = alterouItensProcedimento;
	}

	public VFatConvPlanoGrupoProcedVO getConvenio() {
		return convenio;
	}

	public void setConvenio(VFatConvPlanoGrupoProcedVO convenio) {
		this.convenio = convenio;
	}

	public List<FatConvGrupoItemProced> getConvGrupoItemProcedList() {
		return convGrupoItemProcedList;
	}

	public void setConvGrupoItemProcedList(
			List<FatConvGrupoItemProced> convGrupoItemProcedList) {
		this.convGrupoItemProcedList = convGrupoItemProcedList;
	}

	public List<FatConvGrupoItemProced> getConvGrupoItemProcedListOriginal() {
		return convGrupoItemProcedListOriginal;
	}

	public void setConvGrupoItemProcedListOriginal(
			List<FatConvGrupoItemProced> convGrupoItemProcedListOriginal) {
		this.convGrupoItemProcedListOriginal = convGrupoItemProcedListOriginal;
	}

	public List<FatConvGrupoItemProced> getConvGrupoItemProcedListRemover() {
		return convGrupoItemProcedListRemover;
	}

	public void setConvGrupoItemProcedListRemover(
			List<FatConvGrupoItemProced> convGrupoItemProcedListRemover) {
		this.convGrupoItemProcedListRemover = convGrupoItemProcedListRemover;
	}

	public Integer getPhi() {
		if (componentesSanguineo != null && !StringUtils.isEmpty(componentesSanguineo.getCodigo())) {
			List<FatProcedHospInternos> listaProcedimentosInternos = this.faturamentoFacade.listarPhi(null, null, null, null, null, componentesSanguineo.getCodigo(), null, null, null, 1);
			if (listaProcedimentosInternos != null && !listaProcedimentosInternos.isEmpty()) {
				phi = listaProcedimentosInternos.get(0).getSeq();
			}
		}
		return phi;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}

	public FatConvenioSaudePlano getPlano() {
		return plano;
	}

	public void setPlano(FatConvenioSaudePlano plano) {
		this.plano = plano;
	}

	public FatConvGrupoItemProced getFatConvGrupoItemProcedSelecionado() {
		return fatConvGrupoItemProcedSelecionado;
	}

	public void setFatConvGrupoItemProcedSelecionado(
			FatConvGrupoItemProced fatConvGrupoItemProcedSelecionado) {
		this.fatConvGrupoItemProcedSelecionado = fatConvGrupoItemProcedSelecionado;
	}

	public boolean isObrigatoriedadeSigtap() {
		return obrigatoriedadeSigtap;
	}

	public void setObrigatoriedadeSigtap(boolean obrigatoriedadeSigtap) {
		this.obrigatoriedadeSigtap = obrigatoriedadeSigtap;
	}

	public IPermissionService getPermissionService() {
		return permissionService;
	}
	
	public FatItensProcedHospitalar getItemProcedHosp() {
		return relacionarPHISSMController.getItemProcedHosp();
	}

	public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
		relacionarPHISSMController.setItemProcedHosp(itemProcedHosp);
	}

	public FatItensProcedHospitalar getItemProcedHospSus() {
		return relacionarPHISSMController.getItemProcedHospSus();
	}

	public void setItemProcedHospSus(FatItensProcedHospitalar itemProcedHospSus) {
		relacionarPHISSMController.setItemProcedHospSus(itemProcedHospSus);
	}
}