package br.gov.mec.aghu.bancosangue.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController.RelacionarPHISSMControllerExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;


public class ManterProcedimentosHemoterapicosController extends ActionController {

	private static final long serialVersionUID = 3217818308513020992L;
	private static final Log LOG = LogFactory.getLog(ManterProcedimentosHemoterapicosController.class);
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private RelacionarPHISSMController relacionarPHISSMController;
	
	private enum TargetProcedimentoHemoterapicoEnum{
		VALIDADE_AMOSTRA("bancodesangue-pesquisarValidadeAmostra"),		
		VISUALIZA_EXAME("prescricaomedica-pesquisarExamesDaHemoterapia"),
		JUSTIFICATIVAS("bancodesangue-pesquisarJustificativasUsoHemoterapico"),
		PESQUISAR_PROCED_HEMOTERAPICO("bancodesangue-pesquisarProcedHemoterapico");
		
		private final String descricao;

		private TargetProcedimentoHemoterapicoEnum(String descricao) {
			this.descricao = descricao;
		}
	}

	private AbsProcedHemoterapico procedimento;
	private Boolean indAtivo;
	private Boolean indAmostra;
	private Boolean indJustificativa;
	private Boolean permitirBotoes = false;
	private Boolean modoEdicao;
	private Boolean exibirPainelInferior = true;
	private VFatConvPlanoGrupoProcedVO convenio;
	private List<FatConvGrupoItemProced> convGrupoItemProcedList;
	private List<FatConvGrupoItemProced> convGrupoItemProcedListOriginal;
	private List<FatConvGrupoItemProced> convGrupoItemProcedListRemover;
	private Integer phi;
	private FatConvenioSaudePlano plano;
	private FatConvGrupoItemProced fatConvGrupoItemProcedSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
		fatConvGrupoItemProcedSelecionado = null;

		//Se for editar, carrega o objeto
		if(procedimento != null && procedimento.getCodigo() != null){
			procedimento = bancoDeSangueFacade.obterProcedHemoterapicoPorCodigo(procedimento.getCodigo());
			if(procedimento != null){
				permitirBotoes = true;//Para habilitar botoes de ações
				setModoEdicao(Boolean.TRUE);
				if(DominioSituacao.A.equals(procedimento.getIndSituacao())){
					indAtivo = true;
				}
				else{
					indAtivo = false;
				}
				indAmostra = procedimento.getIndAmostra();
				indJustificativa = procedimento.getIndJustificativa();
			}
		}
		//Se nao, instancia o objeto
		if(procedimento == null){
			inicializaVariaveis();
		}

		relacionarPHISSMController.inicio();
		convGrupoItemProcedList = new ArrayList<FatConvGrupoItemProced>();
		convGrupoItemProcedListOriginal = new ArrayList<FatConvGrupoItemProced>();
		convGrupoItemProcedListRemover = new ArrayList<FatConvGrupoItemProced>();
		if (getPhi() != null) {
			List<FatProcedHospInternos> procedimentosIternos = faturamentoFacade.pesquisarProcedimentosInternosPeloCodProcHem(procedimento.getCodigo());
			if (procedimentosIternos != null && !procedimentosIternos.isEmpty()) {
				relacionarPHISSMController.setProcedimentoInterno(procedimentosIternos.get(0));
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
		exibirPainelInferior = exibirPainelInferior();
	
	}
	
	
	private Boolean exibirPainelInferior() {
		Boolean exibirPanelInferior = false;
		AghParametros pExibirPainelInferior = null;
		try {
			pExibirPainelInferior = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_OBRIGATORIEDADE_SIGTAP);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
		}
		if (pExibirPainelInferior != null) {
			exibirPanelInferior = "S".equals(pExibirPainelInferior.getVlrTexto());
		}
		return exibirPanelInferior;
	}	
	private void inicializaVariaveis() {		
		setProcedimento(new AbsProcedHemoterapico());
		setIndAtivo(Boolean.TRUE);
		setIndAmostra(Boolean.TRUE);
		setIndJustificativa(Boolean.TRUE);
		setModoEdicao(Boolean.FALSE);
	}
	
	public String gravarProcedimentoHemoterapicoSemSigtap() {
		return gravarProcedimentoHemoterapico(false);
	}
	
	public String gravarProcedimentoHemoterapico() {
		return gravarProcedimentoHemoterapico(true);
	}
	
	public String gravarProcedimentoHemoterapico(Boolean obrigatoriedadeSigtap ) {
		//Seta a situacao do procedimento
		if(procedimento != null){
			if(indAtivo){
				procedimento.setIndSituacao(DominioSituacao.A);
			}
			else{
				procedimento.setIndSituacao(DominioSituacao.I);
			}
			
			procedimento.setIndAmostra(indAmostra);
		
			procedimento.setIndJustificativa(indJustificativa);
		
			if (exibirPainelInferior && obrigatoriedadeSigtap && (convGrupoItemProcedList == null || convGrupoItemProcedList.isEmpty())) {
				if (!gravarRelacionamentoPHIePROCEDSUS(obrigatoriedadeSigtap)) {
					return null;
				}
			}
			//Gravar
			try {
				//Seta a situacao do procedimento
				if(procedimento != null){
					if(indAtivo){
						procedimento.setIndSituacao(DominioSituacao.A);
					}
					else{
						procedimento.setIndSituacao(DominioSituacao.I);
					}
					
					procedimento.setIndAmostra(indAmostra);
				
					procedimento.setIndJustificativa(indJustificativa);
				
					bancoDeSangueFacade.persistirProcedimentoHemoterapico(procedimento);
					permitirBotoes =true;
				}
			} catch (BaseException  e) {
				this.apresentarExcecaoNegocio(e);
			}
			
			if (exibirPainelInferior) {
				if (!gravarRelacionamentoPHIePROCEDSUS(obrigatoriedadeSigtap)) {
					return "";
				}
			}
			this.apresentarMsgNegocio(Severity.INFO, ManterProcedHemoterapicoPaginatorController.ProcedimentoHemoterapicoMessages.MSG_PROCEDIMENTO_GRAVADO_SUCESSO.toString());

			iniciarVariaveisProcedimentosHospInternos();
		}
		
		return null;
	}
	
	private void iniciarVariaveisProcedimentosHospInternos() {
		relacionarPHISSMController.setConvGrupoItemProced(new FatConvGrupoItemProced());
		relacionarPHISSMController.setItemProcedHospSus(null);
		relacionarPHISSMController.setEdicao(false);
	}

	public void adicionar() {
		if (getConvenio() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_ADICIONAR_PROCEDIMENTO_SUS", "Convênio");
			return;
		}
		if (relacionarPHISSMController.getItemProcedHospSus() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_ADICIONAR_PROCEDIMENTO_SUS", "Procedimento SUS");
			return;
		}

		relacionarPHISSMController.setConvenio(convenio);

		if (relacionarPHISSMController.getEdicao()) {
			//Remove o Item da Lista e adiciona na lista de itens a serem removidos no momento da gravação.
			excluir(fatConvGrupoItemProcedSelecionado);

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
					this.apresentarMsgNegocio(Severity.ERROR,  "FAT_00073");
				}
			}
		}
		iniciarVariaveisProcedimentosHospInternos();
	}

	private Boolean gravarRelacionamentoPHIePROCEDSUS(Boolean obrigatoriedadeSigtap) {
		if (convGrupoItemProcedList == null || convGrupoItemProcedList.isEmpty()) {
			if (obrigatoriedadeSigtap) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PROCEDIMENTO_OBRIGATORIO");
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
				relacionarPHISSMController.removerProcedimento(fatConvGrupoItemProced);
			}
			for (Iterator<FatConvGrupoItemProced> iterator = convGrupoItemProcedListClone.iterator(); iterator.hasNext();) {
				FatConvGrupoItemProced fatConvGrupoItemProced = iterator.next();
				if (convenio != null) {
					relacionarPHISSMController.setConvenio(convenio);
				}
				VFatConvPlanoGrupoProcedVO plano = new VFatConvPlanoGrupoProcedVO();
				plano.setCphCspSeq(fatConvGrupoItemProced.getConvenioSaudePlano().getId().getSeq());
				plano.setCspDescricao(fatConvGrupoItemProced.getConvenioSaudePlano().getDescricao());
				relacionarPHISSMController.setPlano(plano);
				relacionarPHISSMController.setItemProcedHospSus(fatConvGrupoItemProced.getItemProcedHospitalar());
				List<FatProcedHospInternos> procedimentosIternos = faturamentoFacade.pesquisarProcedimentosInternosPeloCodProcHem(procedimento.getCodigo());
				if (procedimentosIternos != null && !procedimentosIternos.isEmpty()) {
					relacionarPHISSMController.setProcedHospInterno(procedimentosIternos.get(0));
				}
				relacionarPHISSMController.setConvGrupoItemProced(fatConvGrupoItemProced);
				relacionarPHISSMController.adicionarProcedimento();
			}
			relacionarPHISSMController.criarNotificacoesUsuarios(getPhi(),convGrupoItemProcedList);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return false;
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			this.apresentarMsgNegocio(Severity.ERROR, RelacionarPHISSMControllerExceptionCode.ERRO_RELACIONAMENTO_PHI_SSM.toString());
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

	public void excluir(FatConvGrupoItemProced item) {
		relacionarPHISSMController.setAlterou(true);
		convGrupoItemProcedListRemover.add(item);
		convGrupoItemProcedList.remove(item);
	}

	public void editar(FatConvGrupoItemProced item) {
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
		relacionarPHISSMController.cancelarEdicao();
		fatConvGrupoItemProcedSelecionado = null;
	}

	public List<VFatConvPlanoGrupoProcedVO> listarConvenios(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarConvenios(objPesquisa, relacionarPHISSMController.getCpgGrcSeq(), relacionarPHISSMController.getTabela().getCphPhoSeq()),listarConveniosCount(objPesquisa));
	}

	public Long listarConveniosCount(String objPesquisa) {
		return this.faturamentoFacade.listarConveniosCount(objPesquisa, relacionarPHISSMController.getCpgGrcSeq(), relacionarPHISSMController.getTabela().getCphPhoSeq());
	}

	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(String objPesquisa){
		return this.returnSGWithCount(relacionarPHISSMController.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(objPesquisa),listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa));
	}
	
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(String objPesquisa){
		return relacionarPHISSMController.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa);
	}
	
	public Boolean getEdicao() {
		return relacionarPHISSMController.getEdicao();
	}

	public void setEdicao(Boolean edicao) {
		relacionarPHISSMController.setEdicao(edicao);
	}

	public FatItensProcedHospitalar getItemProcedHosp() {
		return relacionarPHISSMController.getItemProcedHosp();
	}

	public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
		relacionarPHISSMController.setItemProcedHosp(itemProcedHosp);
	}

	public AbsProcedHemoterapico getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(AbsProcedHemoterapico procedimento) {
		this.procedimento = procedimento;
	}
	
	public FatItensProcedHospitalar getItemProcedHospSus() {
		return relacionarPHISSMController.getItemProcedHospSus();
	}

	public void setItemProcedHospSus(FatItensProcedHospitalar itemProcedHospSus) {
		relacionarPHISSMController.setItemProcedHospSus(itemProcedHospSus);
	}

	public VFatConvPlanoGrupoProcedVO getConvenio() {
		return convenio;
	}

	public void setConvenio(VFatConvPlanoGrupoProcedVO convenio) {
		this.convenio = convenio;
	}

	public Integer getPhi() {
		if (procedimento != null) {
			List<FatProcedHospInternos> procedimentosIternos = faturamentoFacade.pesquisarProcedimentosInternosPeloCodProcHem(procedimento.getCodigo());
			if (procedimentosIternos != null && !procedimentosIternos.isEmpty()) {
				phi = procedimentosIternos.get(0).getSeq();
			}
		}
		return phi;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}

	public List<FatConvGrupoItemProced> getConvGrupoItemProcedList() {
		return convGrupoItemProcedList;
	}

	public void setConvGrupoItemProcedList(List<FatConvGrupoItemProced> convGrupoItemProcedList) {
		this.convGrupoItemProcedList = convGrupoItemProcedList;
	}

	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}

	
	public Boolean isValidadeAmostraReadOnly(){
		Boolean result = true;
		if(permitirBotoes){
			if(indAtivo){
				result = false;
			}
		}
		return result;
	}
	
	public Boolean isVisualizaExamesReadOnly(){
		Boolean result = true;
		if(permitirBotoes){
			if(indAtivo){
				result = false;
			}
		}
		return result;
	}
	
	public Boolean isJustificativasReadOnly(){
		Boolean result = true;
		if(permitirBotoes){
			if(indAtivo && indJustificativa){
				result = false;
			}
		}
		return result;
	}
	
	public String abrirValidadeAmostra(){
		return TargetProcedimentoHemoterapicoEnum.VALIDADE_AMOSTRA.descricao;
	}
	
	public String abrirVisualizarExames(){
		return TargetProcedimentoHemoterapicoEnum.VISUALIZA_EXAME.descricao;
	}
	
	public String abrirJustificativas(){
		return TargetProcedimentoHemoterapicoEnum.JUSTIFICATIVAS.descricao;
	}

	public String cancelar(){
		inicializaVariaveis();
		return TargetProcedimentoHemoterapicoEnum.PESQUISAR_PROCED_HEMOTERAPICO.descricao;
	}

	public Boolean getIndAmostra() {
		return indAmostra;
	}

	public void setIndAmostra(Boolean indAmostra) {
		this.indAmostra = indAmostra;
	}

	public Boolean getIndJustificativa() {
		return indJustificativa;
	}

	public void setIndJustificativa(Boolean indJustificativa) {
		this.indJustificativa = indJustificativa;
	}
	
	public Boolean getModoEdicao() {
		return modoEdicao;
	}
	
	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
	
	public Boolean getExibirPainelInferior() {
		return exibirPainelInferior;
	}

	public void setExibirPainelInferior(Boolean exibirPainelInferior) {
		this.exibirPainelInferior = exibirPainelInferior;
	}

	public FatConvGrupoItemProced getFatConvGrupoItemProcedSelecionado() {
		return fatConvGrupoItemProcedSelecionado;
	}

	public void setFatConvGrupoItemProcedSelecionado(
			FatConvGrupoItemProced fatConvGrupoItemProcedSelecionado) {
		this.fatConvGrupoItemProcedSelecionado = fatConvGrupoItemProcedSelecionado;
	}
}
