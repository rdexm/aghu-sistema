package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController.RelacionarPHISSMControllerExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Controller para a tela de cadastro de Procedimentos Cirúrgicos.
 * 
 * @author dpacheco
 *
 */
public class ProcedimentoCirurgicoController extends ActionController {

    private static final long serialVersionUID = -2382706923234791448L;
    
    private static final Log LOG = LogFactory.getLog(ProcedimentoCirurgicoController.class);

	private static final String PCI_LIST = "procedimentoCirurgicoList";
	private static final Integer TAMANHO_MAXIMO_STRING_HORAS_MINUTOS = 5;
    
    public enum ProcedimentoCirurgicoControllerExceptionCode implements BusinessExceptionCode {
        ERRO_ADICIONAR_PROCEDIMENTO_SUS, ERRO_PROCEDIMENTO_SUS_JA_CADASTRADO
    }
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
    @EJB
    private IFaturamentoFacade faturamentoFacade;
    @EJB
    private IParametroFacade parametroFacade;

    @Inject
    private RelacionarPHISSMController relacionarPHISSMController;
	
	private Integer seq;
	private String descricao;
	private Boolean situacaoAtiva;
	private String cuidadosPreOper;
	private DominioIndContaminacao indContaminacao;
	private DominioTipoProcedimentoCirurgico tipo;
	private DominioRegimeProcedimentoCirurgicoSus regimeProcedSus;
	private Short tempoMinimo;
	private Byte numeroDoadores;
	private Boolean indProcRealizadoLeito;
	private Boolean indProcMultiplo;
	private Boolean indNsSemPront;
	private Boolean indTipagemSangue;
	private Boolean indAplicacaoQuimio;
	private Boolean indInteresseCcih;
	private Boolean indGeraImagensPacs;
	private Boolean ladoCirurgia;
	private Date criadoEm;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private List<FatConvGrupoItemProced> convGrupoItemProcedList;
	private List<FatConvGrupoItemProced> convGrupoItemProcedListOriginal;
	private List<FatConvGrupoItemProced> convGrupoItemProcedListRemover;
	private Integer phi;
	private VFatConvPlanoGrupoProcedVO convenio;
	private Boolean exibirPainelInferior = true;
	private FatConvenioSaudePlano plano;
	private FatConvGrupoItemProced fatConvGrupoItemProcedSelecionado;
	private String strTempoMinimo;
	
    @PostConstruct
    protected void inicializar(){
        this.begin(conversation);
    }

    public void iniciarInclusao() {
        this.seq = null;
    }
    
	public void iniciar() {
		relacionarPHISSMController.inicio();
		if (seq != null) {
			procedimentoCirurgico = blocoCirurgicoCadastroApoioFacade.obterProcedimentoCirurgico(seq);
			carregarCamposProcedimentoCirurgico(procedimentoCirurgico);
		} else {
			procedimentoCirurgico = null;
			carregarValoresDefault();
		}
		convGrupoItemProcedList = new ArrayList<FatConvGrupoItemProced>();
		convGrupoItemProcedListOriginal = new ArrayList<FatConvGrupoItemProced>();
		convGrupoItemProcedListRemover = new ArrayList<FatConvGrupoItemProced>();
		if (getPhi() != null) {
			List<FatProcedHospInternos> procedimentosIternos = faturamentoFacade.pesquisarProcedimentosInternosPeloSeqProcCirg(procedimentoCirurgico.getSeq());
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
	
	public void converterTempoMinimo() {
		try {
            this.tempoMinimo = 0;
			if (StringUtils.isNotBlank(this.strTempoMinimo) && this.strTempoMinimo.length() <= TAMANHO_MAXIMO_STRING_HORAS_MINUTOS) {
				String[] arrayHorasMinutos = this.strTempoMinimo.split(":");
				if (arrayHorasMinutos.length == 2) {
					this.tempoMinimo = Short.valueOf(arrayHorasMinutos[0].concat(arrayHorasMinutos[1]));
                }
			}
		} catch (Exception e) {
			LOG.error("Tempo mínimo incorreto no cadastro de procedimento cirúrgico: " + strTempoMinimo, e);
		}
	}
	private Boolean exibirPainelInferior() {
		Boolean exibirPanelInferior = false;
		AghParametros pExibirPainelInferior = null;
		try {
			pExibirPainelInferior = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_OBRIGATORIEDADE_SIGTAP);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		if (pExibirPainelInferior != null) {
			exibirPanelInferior = "S".equals(pExibirPainelInferior.getVlrTexto());
		}
		return exibirPanelInferior;
	}
	
	private void carregarValoresDefault() {
        seq = null;
		descricao = null;
		situacaoAtiva = Boolean.TRUE;
		cuidadosPreOper = null;
		indContaminacao = null;
		tipo = null;
		regimeProcedSus = null;
		tempoMinimo = 0;
        strTempoMinimo = null;
		numeroDoadores = null;
		indProcRealizadoLeito = Boolean.FALSE;
		indProcMultiplo = Boolean.FALSE;
		indNsSemPront = Boolean.FALSE;
		indTipagemSangue = Boolean.FALSE;
		indAplicacaoQuimio = Boolean.FALSE;
		indInteresseCcih = Boolean.FALSE;
		indGeraImagensPacs = Boolean.FALSE;
		ladoCirurgia = Boolean.FALSE;
        phi = null;
	}	
	
	public String gravarProcedimentoCirurgicoSemSigtap() {
		return gravarProcedimentoCirurgico(false);
	}
	
	public String gravarProcedimentoCirurgico() {
		return gravarProcedimentoCirurgico(true);
	}
	
	public String gravarProcedimentoCirurgico(Boolean obrigatoriedadeSigtap) {
        
		try {
			if (procedimentoCirurgico == null || !procedimentoCirurgico.getDescricao().equals(descricao)) {
				blocoCirurgicoCadastroApoioFacade.validarDescricaoProcedimentoCirurgico(descricao);	
			}
			blocoCirurgicoCadastroApoioFacade.validarTempoMinimoProcedimentoCirurgico(tempoMinimo);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		if (seq == null) {
			procedimentoCirurgico = new MbcProcedimentoCirurgicos();
		}
        
		procedimentoCirurgico.setDescricao(descricao);
		procedimentoCirurgico.setIndSituacao(DominioSituacao.getInstance(situacaoAtiva));
		procedimentoCirurgico.setCuidadosPreOper(cuidadosPreOper);
		procedimentoCirurgico.setIndContaminacao(indContaminacao);
		procedimentoCirurgico.setTipo(tipo);
		procedimentoCirurgico.setRegimeProcedSus(regimeProcedSus);
		procedimentoCirurgico.setTempoMinimo(tempoMinimo);
		procedimentoCirurgico.setNumeroDoadores(numeroDoadores);
		procedimentoCirurgico.setIndProcRealizadoLeito(indProcRealizadoLeito);		
		procedimentoCirurgico.setIndProcMultiplo(indProcMultiplo);
		procedimentoCirurgico.setIndNsSemPront(indNsSemPront);
		procedimentoCirurgico.setIndTipagemSangue(indTipagemSangue);
		procedimentoCirurgico.setIndAplicacaoQuimio(indAplicacaoQuimio);
		procedimentoCirurgico.setIndInteresseCcih(indInteresseCcih);
		procedimentoCirurgico.setIndGeraImagensPacs(indGeraImagensPacs);
		procedimentoCirurgico.setLadoCirurgia(ladoCirurgia);
		procedimentoCirurgico.setIndSalaEspecial(Boolean.FALSE); // Valor default
		procedimentoCirurgico.setServidorAlterado(servidorLogadoFacade.obterServidorLogado());

		if (exibirPainelInferior && obrigatoriedadeSigtap && (convGrupoItemProcedList == null || convGrupoItemProcedList.isEmpty())) {
			if (!gravarRelacionamentoPHIePROCEDSUS(obrigatoriedadeSigtap)) {
				return null;
			}
		}
		
		try {
			blocoCirurgicoCadastroApoioFacade.persistirProcedimentoCirurgico(procedimentoCirurgico);
			blocoCirurgicoCadastroApoioFacade.validarProcedimentoHospitarInternoRelacionado(getPhi(), convGrupoItemProcedList);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		if (exibirPainelInferior) {
			if (!gravarRelacionamentoPHIePROCEDSUS(obrigatoriedadeSigtap)) {
				return null;
			}
		}
		
		seq = procedimentoCirurgico.getSeq();
		
		this.apresentarMsgNegocio(Severity.INFO, 
				"MENSAGEM_SUCESSO_GRAVAR_PROCEDIMENTO_CIRURGICO");		
		
		iniciarVariaveisProcedimentosHospInternos();
		return PCI_LIST;
	}

	private void iniciarVariaveisProcedimentosHospInternos() {
		relacionarPHISSMController.setConvGrupoItemProced(new FatConvGrupoItemProced());
		relacionarPHISSMController.setItemProcedHospSus(null);
		relacionarPHISSMController.setEdicao(false);
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
				List<FatProcedHospInternos> procedimentosIternos = faturamentoFacade.pesquisarProcedimentosInternosPeloSeqProcCirg(procedimentoCirurgico
						.getSeq());
				if (procedimentosIternos != null && !procedimentosIternos.isEmpty()) {
					relacionarPHISSMController.setProcedHospInterno(procedimentosIternos.get(0));
					relacionarPHISSMController.adicionarProcedimento();
				} 
				relacionarPHISSMController.setConvGrupoItemProced(fatConvGrupoItemProced);
			}
			relacionarPHISSMController.criarNotificacoesUsuarios(getPhi(),convGrupoItemProcedList);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return false;
		} catch (Exception e) {
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
	
	public String cancelar() {
        carregarValoresDefault();
		return PCI_LIST;
	}
	
	public Integer getPhi() {
		if (procedimentoCirurgico != null) {
			List<FatProcedHospInternos> procedimentosIternos = faturamentoFacade.pesquisarProcedimentosInternosPeloSeqProcCirg(procedimentoCirurgico.getSeq());
			if (procedimentosIternos != null && !procedimentosIternos.isEmpty()) {
				phi = procedimentosIternos.get(0).getSeq();
			}
		}
		return phi;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}
	
	private void carregarCamposProcedimentoCirurgico(MbcProcedimentoCirurgicos procedimentoCirurgico) {
		if (procedimentoCirurgico != null) {
			descricao = procedimentoCirurgico.getDescricao();
			situacaoAtiva = procedimentoCirurgico.getIndSituacao().isAtivo();
			cuidadosPreOper = procedimentoCirurgico.getCuidadosPreOper();
			indContaminacao = procedimentoCirurgico.getIndContaminacao();
			tipo = procedimentoCirurgico.getTipo();
			regimeProcedSus = procedimentoCirurgico.getRegimeProcedSus();
			tempoMinimo = procedimentoCirurgico.getTempoMinimo();
            strTempoMinimo = tempoMinimo != null ? formatarTempo(tempoMinimo) : null;
			numeroDoadores = procedimentoCirurgico.getNumeroDoadores();
			indProcRealizadoLeito = procedimentoCirurgico.getIndProcRealizadoLeito();
			indProcMultiplo = procedimentoCirurgico.getIndProcMultiplo();
			indNsSemPront = procedimentoCirurgico.getIndNsSemPront();
			indTipagemSangue = procedimentoCirurgico.getIndTipagemSangue();
			indAplicacaoQuimio = procedimentoCirurgico.getIndAplicacaoQuimio();
			indInteresseCcih = procedimentoCirurgico.getIndInteresseCcih();
			indGeraImagensPacs = procedimentoCirurgico.getIndGeraImagensPacs();
			ladoCirurgia = procedimentoCirurgico.getLadoCirurgia();
			criadoEm = procedimentoCirurgico.getCriadoEm();			
		}
	}

    /**
     * Exemplos:
     * formatarTempo(402) -> 04:02
     * formatarTempo(2157) -> 21:57
     */
    private String formatarTempo(Short tempo) {          
        return String.format("%02d:%02d", ((int) tempo / 100), tempo % 100);
    }

	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(String objPesquisa){
		return this.returnSGWithCount(
				this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(objPesquisa, relacionarPHISSMController.getTabela().getCphPhoSeq()),
				listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa)
				);
	}
	
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(String objPesquisa){
		return this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa, relacionarPHISSMController.getTabela().getCphPhoSeq());
	}

    
    public List<VFatConvPlanoGrupoProcedVO> listarConvenios(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarConvenios(objPesquisa, relacionarPHISSMController.getCpgGrcSeq(), 
				relacionarPHISSMController.getTabela().getCphPhoSeq()), listarConveniosCount(objPesquisa));
	}

	public Long listarConveniosCount(String objPesquisa) {
		return this.faturamentoFacade.listarConveniosCount(objPesquisa, relacionarPHISSMController.getCpgGrcSeq(), relacionarPHISSMController.getTabela().getCphPhoSeq());
	}
	
	public void executarAposLimparSuggestionConvenio(){
		convGrupoItemProcedListRemover.addAll(convGrupoItemProcedList);
		convGrupoItemProcedList = new ArrayList<FatConvGrupoItemProced>();
	}
	
	public void adicionar() {
		if (getConvenio() == null) {
			apresentarMsgNegocio(Severity.ERROR, ProcedimentoCirurgicoControllerExceptionCode.ERRO_ADICIONAR_PROCEDIMENTO_SUS.toString(),
					"Convênio");
			return;
		}
		if (relacionarPHISSMController.getItemProcedHospSus() == null) {
			apresentarMsgNegocio(Severity.ERROR, ProcedimentoCirurgicoControllerExceptionCode.ERRO_ADICIONAR_PROCEDIMENTO_SUS.toString(),
					"Procedimento SUS");
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
			for (VFatConvPlanoGrupoProcedVO plano : relacionarPHISSMController.listarPlanosSemCount("")) {
				FatConvenioSaudePlanoId fatConvenioSaudePlanoId = new FatConvenioSaudePlanoId();
				fatConvenioSaudePlanoId.setSeq(plano.getCphCspSeq());
				FatConvenioSaudePlano fatConvenioSaudePlano = new FatConvenioSaudePlano();
				fatConvenioSaudePlano.setId(fatConvenioSaudePlanoId);
				fatConvenioSaudePlano.setDescricao(plano.getCspDescricao());
				
				FatConvenioSaude fatConvenioSaude = new FatConvenioSaude();
				fatConvenioSaude.setCodigo(convenio.getCphCspCnvCodigo());
				fatConvenioSaude.setDescricao(convenio.getCnvDescricao());
				fatConvenioSaudePlano.setConvenioSaude(fatConvenioSaude);

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
					apresentarMsgNegocio(Severity.ERROR,  "FAT_00073");
				}
			}
		}
		iniciarVariaveisProcedimentosHospInternos();
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
	}
	
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getSituacaoAtiva() {
		return situacaoAtiva;
	}

	public void setSituacaoAtiva(Boolean situacaoAtiva) {
		this.situacaoAtiva = situacaoAtiva;
	}

	public String getCuidadosPreOper() {
		return cuidadosPreOper;
	}

	public void setCuidadosPreOper(String cuidadosPreOper) {
		this.cuidadosPreOper = cuidadosPreOper;
	}

	public DominioIndContaminacao getIndContaminacao() {
		return indContaminacao;
	}

	public void setIndContaminacao(DominioIndContaminacao indContaminacao) {
		this.indContaminacao = indContaminacao;
	}

	public DominioTipoProcedimentoCirurgico getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoProcedimentoCirurgico tipo) {
		this.tipo = tipo;
	}

	public DominioRegimeProcedimentoCirurgicoSus getRegimeProcedSus() {
		return regimeProcedSus;
	}

	public void setRegimeProcedSus(
			DominioRegimeProcedimentoCirurgicoSus regimeProcedSus) {
		this.regimeProcedSus = regimeProcedSus;
	}

	public Short getTempoMinimo() {
		return tempoMinimo;
	}

	public void setTempoMinimo(Short tempoMinimo) {
		this.tempoMinimo = tempoMinimo;
	}

	public Byte getNumeroDoadores() {
		return numeroDoadores;
	}

	public void setNumeroDoadores(Byte numeroDoadores) {
		this.numeroDoadores = numeroDoadores;
	}

	public Boolean getIndProcRealizadoLeito() {
		return indProcRealizadoLeito;
	}

	public void setIndProcRealizadoLeito(Boolean indProcRealizadoLeito) {
		this.indProcRealizadoLeito = indProcRealizadoLeito;
	}

	public Boolean getIndProcMultiplo() {
		return indProcMultiplo;
	}

	public void setIndProcMultiplo(Boolean indProcMultiplo) {
		this.indProcMultiplo = indProcMultiplo;
	}

	public Boolean getIndNsSemPront() {
		return indNsSemPront;
	}

	public void setIndNsSemPront(Boolean indNsSemPront) {
		this.indNsSemPront = indNsSemPront;
	}

	public Boolean getIndTipagemSangue() {
		return indTipagemSangue;
	}

	public void setIndTipagemSangue(Boolean indTipagemSangue) {
		this.indTipagemSangue = indTipagemSangue;
	}

	public Boolean getIndAplicacaoQuimio() {
		return indAplicacaoQuimio;
	}

	public void setIndAplicacaoQuimio(Boolean indAplicacaoQuimio) {
		this.indAplicacaoQuimio = indAplicacaoQuimio;
	}

	public Boolean getIndInteresseCcih() {
		return indInteresseCcih;
	}

	public void setIndInteresseCcih(Boolean indInteresseCcih) {
		this.indInteresseCcih = indInteresseCcih;
	}

	public Boolean getIndGeraImagensPacs() {
		return indGeraImagensPacs;
	}

	public void setIndGeraImagensPacs(Boolean indGeraImagensPacs) {
		this.indGeraImagensPacs = indGeraImagensPacs;
	}

	public Boolean getLadoCirurgia() {
		return ladoCirurgia;
	}

	public void setLadoCirurgia(Boolean ladoCirurgia) {
		this.ladoCirurgia = ladoCirurgia;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public List<FatConvGrupoItemProced> getConvGrupoItemProcedList() {
		return convGrupoItemProcedList;
	}

	public void setConvGrupoItemProcedList(List<FatConvGrupoItemProced> convGrupoItemProcedList) {
		this.convGrupoItemProcedList = convGrupoItemProcedList;
	}

	public VFatConvPlanoGrupoProcedVO getConvenio() {
		return convenio;
	}

	public void setConvenio(VFatConvPlanoGrupoProcedVO convenio) {
		this.convenio = convenio;
	}

	public FatItensProcedHospitalar getItemProcedHospSus() {
		return relacionarPHISSMController.getItemProcedHospSus();
	}

	public void setItemProcedHospSus(FatItensProcedHospitalar itemProcedHospSus) {
		relacionarPHISSMController.setItemProcedHospSus(itemProcedHospSus);
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

	public FatConvGrupoItemProced getConvGrupoItemProced() {
		return relacionarPHISSMController.getConvGrupoItemProced();
	}

	public void setConvGrupoItemProced(FatConvGrupoItemProced convGrupoItemProced) {
		relacionarPHISSMController.setConvGrupoItemProced(convGrupoItemProced);
	}
	
	public String getSimNao(Boolean b) {
		if(b != null) {
			return DominioSimNao.getInstance(b).getDescricao();	
		}
		return null;
	}

	public Boolean getExibirPainelInferior() {
		return exibirPainelInferior;
	}

	public void setExibirPainelInferior(Boolean exibirPainelInferior) {
		this.exibirPainelInferior = exibirPainelInferior;
	}

	public String getStrTempoMinimo() {
		return strTempoMinimo;
	}

	public void setStrTempoMinimo(String strTempoMinimo) {
		this.strTempoMinimo = strTempoMinimo;
	}
	
}