package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.action.ProcedimentoCirurgicoController.ProcedimentoCirurgicoControllerExceptionCode;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvGrupoItemProcedId;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;

public class RelacionarPHISSMController extends ActionController{

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final long serialVersionUID = 7464603320972858537L;

	private static final Log LOG = LogFactory.getLog(RelacionarPHISSMController.class);
	
	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICentralPendenciaFacade centralPendenciaFacade;
	
	private FatConvGrupoItemProced convGrupoItemProced;

	//Carregados por parametro
	private Short cpgCphCspCnvCodigo; 
	private Byte cpgCphCspSeq;
	private Short cpgCphPhoSeq;
	private Short cpgGrcSeq;
	
	private String cpgGrcDescricao;
	
	private Long codTabela;
	
	//usados nas suggestions
	private FatProcedHospInternos procedHospInterno; 
	private VFatConvPlanoGrupoProcedVO tabela;
	private VFatConvPlanoGrupoProcedVO convenio;
	private VFatConvPlanoGrupoProcedVO plano;
	//usado na suggestion ## ITEM PROCEDIMENTO ##
	private FatItensProcedHospitalar itemProcedHosp;
	private FatItensProcedHospitalar itemProcedHospSus;
	private FatProcedHospInternos procedimentoInterno;
	
	private VFatConvPlanoGrupoProcedVO convenioPendencia;
	private VFatConvPlanoGrupoProcedVO planoPendencia;
	
	//Usado para exibir a listagem.
	private Boolean exibirPanelInferior = false;
	private List<FatConvGrupoItemProced> lista = new ArrayList<FatConvGrupoItemProced>(0);
	private List<FatConvGrupoItemProced> listaOriginal = new ArrayList<FatConvGrupoItemProced>(0);
	private List<FatConvGrupoItemProced> listaClones = new ArrayList<FatConvGrupoItemProced>(0);

	private Boolean edicao = false;
	private Boolean alterou = false;
	private Boolean voltar = false;
	private Boolean vincularApac = Boolean.FALSE;
	
	private Boolean exibirModalInclusao;
	private Boolean exibirMsgError = Boolean.FALSE;
	private Integer phi;
	private Boolean telaPendencia = false;
	
	public enum RelacionarPHISSMControllerExceptionCode implements BusinessExceptionCode {
		RELACIONAMENTO_PHI_SSM_JA_ASSOCIADO, RELACIONAMENTO_PHI_SSM_GRAVADOS_COM_SUCESSO, ERRO_RELACIONAMENTO_PHI_SSM, SELECIONAR_PROCED_SUS_OU_PROCED_INTERNO, EXCLUSAO_NOTIFICACAO_COM_SUCESSO;
	}


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() {
		
		try {
			AghParametros pTabela = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			this.cpgCphPhoSeq = pTabela.getVlrNumerico().shortValue();
			List<VFatConvPlanoGrupoProcedVO> listaTabela = this.faturamentoFacade.listarTabelas(this.cpgCphPhoSeq.toString()); 
			if(listaTabela != null && !listaTabela.isEmpty()){
				this.tabela = listaTabela.get(0);
				
				this.cpgGrcSeq = this.tabela.getGrcSeq(); 
				this.cpgGrcDescricao = this.tabela.getGrcDescricao();
				
			}
			
			if (!telaPendencia) {
				AghParametros convenio = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
				this.cpgCphCspCnvCodigo = convenio.getVlrNumerico().shortValue();
				List<VFatConvPlanoGrupoProcedVO> listaConvenio = this.faturamentoFacade.listarConvenios(this.cpgCphCspCnvCodigo.toString(), 
						this.tabela != null ? this.tabela.getGrcSeq() : null, this.cpgCphPhoSeq); 
				if(listaConvenio != null && !listaConvenio.isEmpty()){
					this.convenio = listaConvenio.get(0);
				}
			}
			
//			AghParametros plano = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
//			this.cpgCphCspSeq = plano.getVlrNumerico().byteValue();
//			List<VFatConvPlanoGrupoProcedVO> listaPlano = this.faturamentoFacade.listarPlanos(this.cpgCphCspSeq.toString(), this.tabela != null ? this.tabela.getGrcSeq() : null, 
//					this.tabela != null ? this.tabela.getCphPhoSeq(): null, this.convenio != null ? this.convenio.getCphCspCnvCodigo() : null); 
//			if(listaPlano != null && !listaPlano.isEmpty()){
//				this.plano = listaPlano.get(0);
//			}		
			
			if (codTabela != null) {
				List<FatItensProcedHospitalar> listaItemProcedHosp = listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq((String) codTabela.toString());
				if (!listaItemProcedHosp.isEmpty()) {
					this.itemProcedHosp = listaItemProcedHosp.get(0);
				}
			}
			
			if (phi != null) {
				procedimentoInterno = faturamentoFacade.obterProcedimentoHospitalarInterno(phi);
				pesquisar();
			}

            inicializarConvGrupoItemProced();
		} catch(Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,  RelacionarPHISSMControllerExceptionCode.ERRO_RELACIONAMENTO_PHI_SSM.toString());
			LOG.error(EXCECAO_CAPTURADA, e);
		}
	}
	public String cancelar() {
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}
	public VFatConvPlanoGrupoProcedVO obterConvenio() {
		VFatConvPlanoGrupoProcedVO convenio = null;
		if (lista != null && !lista.isEmpty()) {
			this.cpgCphCspCnvCodigo = lista.get(0).getId().getCpgCphCspCnvCodigo();
			List<VFatConvPlanoGrupoProcedVO> listaConvenio = this.faturamentoFacade.listarConvenios(this.cpgCphCspCnvCodigo.toString(),
					this.tabela != null ? this.tabela.getGrcSeq() : null, this.cpgCphPhoSeq);
			if (listaConvenio != null && !listaConvenio.isEmpty()) {
				convenio = listaConvenio.get(0);
			}
		}
		return convenio;
	}
	
	public VFatConvPlanoGrupoProcedVO obterConvenioSUS() {
		VFatConvPlanoGrupoProcedVO convenio = null;
		try {
			AghParametros convenioParam = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			this.cpgCphCspCnvCodigo = convenioParam.getVlrNumerico().shortValue();
			List<VFatConvPlanoGrupoProcedVO> listaConvenio = this.faturamentoFacade.listarConvenios(this.cpgCphCspCnvCodigo.toString(),
					this.tabela != null ? this.tabela.getGrcSeq() : null, this.cpgCphPhoSeq);
			if (listaConvenio != null && !listaConvenio.isEmpty()) {
				convenio = listaConvenio.get(0);
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		return convenio;
	}
	
	public void pesquisarProcedimento() {
		try {
			lista = faturamentoFacade.listarFatConvGrupoItensProced((itemProcedHosp != null) ? itemProcedHosp.getId().getPhoSeq() : null,
					(itemProcedHosp != null) ? itemProcedHosp.getId().getSeq() : null, null, null, tabela.getCphPhoSeq(), cpgGrcSeq,
					(procedimentoInterno != null) ? procedimentoInterno.getSeq() : null);
			listaOriginal = new ArrayList<FatConvGrupoItemProced>(lista);
			for (FatConvGrupoItemProced conv : lista) {
//				if (conv.getConvenioSaudePlano() == null){
//					faturamentoFacade.refresh(conv);
//				}
				listaClones.add(faturamentoFacade.clonarGrupoItemConvenio(conv));
			}
			exibirPanelInferior = true;
		} catch (BaseException e) {
			this.apresentarMsgNegocio(Severity.ERROR, 
					RelacionarPHISSMControllerExceptionCode
					.ERRO_RELACIONAMENTO_PHI_SSM.toString());
		} catch (Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			this.apresentarMsgNegocio(Severity.ERROR, 
					RelacionarPHISSMControllerExceptionCode
					.ERRO_RELACIONAMENTO_PHI_SSM.toString());
		}
	}
	
	public void adicionarProcedimento() throws BaseException {
		FatConvGrupoItemProcedId id = new FatConvGrupoItemProcedId(plano.getCphCspSeq(), convenio.getCphCspCnvCodigo(), tabela.getCphPhoSeq(), cpgGrcSeq,
				itemProcedHospSus.getId().getSeq(), itemProcedHospSus.getId().getPhoSeq(), procedHospInterno.getSeq());
		FatConvGrupoItemProced convGrupoItemProced = new FatConvGrupoItemProced();
		convGrupoItemProced.setId(id);
		convGrupoItemProced.setProcedimentoHospitalarInterno(procedHospInterno);
		convGrupoItemProced.setItemProcedHospitalar(itemProcedHospSus);
		if (this.convGrupoItemProced.getIndCobrancaFracionada() != null) {
			convGrupoItemProced.setIndCobrancaFracionada(this.convGrupoItemProced.getIndCobrancaFracionada());
		} else {
			convGrupoItemProced.setIndCobrancaFracionada(false);
		}
		if (this.convGrupoItemProced.getIndExigeAutorPrevia() != null) {
			convGrupoItemProced.setIndExigeAutorPrevia(this.convGrupoItemProced.getIndExigeAutorPrevia());
		} else {
			convGrupoItemProced.setIndExigeAutorPrevia(false);
		}
		if (this.convGrupoItemProced.getIndExigeJustificativa() != null) {
			convGrupoItemProced.setIndExigeJustificativa(this.convGrupoItemProced.getIndExigeJustificativa());
		} else {
			convGrupoItemProced.setIndExigeJustificativa(false);
		}
		if (this.convGrupoItemProced.getIndExigeNotaFiscal() != null) {
			convGrupoItemProced.setIndExigeNotaFiscal(this.convGrupoItemProced.getIndExigeNotaFiscal());
		} else {
			convGrupoItemProced.setIndExigeNotaFiscal(false);
		}
		if (this.convGrupoItemProced.getIndImprimeLaudo() != null) {
			convGrupoItemProced.setIndImprimeLaudo(this.convGrupoItemProced.getIndImprimeLaudo());
		} else {
			convGrupoItemProced.setIndImprimeLaudo(false);
		}
		if (this.convGrupoItemProced.getIndInformaTempoTrat() != null) {
			convGrupoItemProced.setIndInformaTempoTrat(this.convGrupoItemProced.getIndInformaTempoTrat());
		} else {
			convGrupoItemProced.setIndInformaTempoTrat(false);
		}
		if (this.convGrupoItemProced.getIndPaga() != null) {
			convGrupoItemProced.setIndPaga(this.convGrupoItemProced.getIndPaga());
		} else {
			convGrupoItemProced.setIndPaga(false);
		}
		if (this.convGrupoItemProced.getTempoValidade() != null) {
			convGrupoItemProced.setTempoValidade(this.convGrupoItemProced.getTempoValidade());
		} else {
			convGrupoItemProced.setTempoValidade(Short.valueOf("0"));
		}
		edicao = false;
		alterou = true;
		faturamentoFacade.persistirGrupoItemConvenio(convGrupoItemProced, null, DominioOperacoesJournal.INS);
	}
	
	public void removerProcedimento(FatConvGrupoItemProced convGrupoItemProced) {
		try {
			if (convGrupoItemProced.getId() != null) {
				faturamentoFacade.excluirGrupoItemConvenio(convGrupoItemProced);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			this.apresentarMsgNegocio(Severity.ERROR, 
					RelacionarPHISSMControllerExceptionCode
					.ERRO_RELACIONAMENTO_PHI_SSM.toString());
		}
	}
	
	
	
	private void validarCamposObrigorios() {
		if (phi == null) {
			if (convenio == null) {
				this.apresentarMsgNegocio(Severity.ERROR,
						ProcedimentoCirurgicoControllerExceptionCode.ERRO_ADICIONAR_PROCEDIMENTO_SUS.toString(), "Convênio");
				this.exibirMsgError = Boolean.TRUE;
			}
			if (plano == null) {
				this.apresentarMsgNegocio(Severity.ERROR,
						ProcedimentoCirurgicoControllerExceptionCode.ERRO_ADICIONAR_PROCEDIMENTO_SUS.toString(), "Plano");
				this.exibirMsgError = Boolean.TRUE;
			}
		}	
	}
	
	public void pesquisar() {
		this.exibirMsgError = Boolean.FALSE;
		validarCamposObrigorios();
		try {
			// VALIDAR PROCED SUS E PROC INTERNO
			if (procedimentoInterno == null && itemProcedHosp == null) {
				this.apresentarMsgNegocio(Severity.ERROR,RelacionarPHISSMControllerExceptionCode.SELECIONAR_PROCED_SUS_OU_PROCED_INTERNO.toString());
				return;
			} else {
				itemProcedHospSus = itemProcedHosp;
				procedHospInterno = procedimentoInterno;
			}
			if(this.exibirMsgError){
				return;
			}
			Short itemProcedHospPhoSeq = null;
			if (itemProcedHosp != null) {
				itemProcedHospPhoSeq = itemProcedHosp.getId().getPhoSeq();
			}
			Integer itemProcedHospSeq = null;
			if (itemProcedHosp != null) {
				itemProcedHospSeq = itemProcedHosp.getId().getSeq();
			}
			Byte planoCphCspSeq = null;
			if (plano != null) {
				planoCphCspSeq = plano.getCphCspSeq();
			}
			Integer procedimentoInternoSeq = null;
			if (procedimentoInterno != null) {
				procedimentoInternoSeq = procedimentoInterno.getSeq();
			}
			lista = faturamentoFacade.listarFatConvGrupoItensProced(itemProcedHospPhoSeq, itemProcedHospSeq,
					convenio != null ? convenio.getCphCspCnvCodigo() : null, planoCphCspSeq, tabela.getCphPhoSeq(), cpgGrcSeq,
					procedimentoInternoSeq);
			listaOriginal = new ArrayList<FatConvGrupoItemProced>(lista);
			for (FatConvGrupoItemProced conv : lista) {
				listaClones.add(faturamentoFacade.clonarGrupoItemConvenio(conv));
			}
			exibirPanelInferior = true;
		} catch(ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR,  RelacionarPHISSMControllerExceptionCode.ERRO_RELACIONAMENTO_PHI_SSM.toString());
		} catch(Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			this.apresentarMsgNegocio(Severity.ERROR,  RelacionarPHISSMControllerExceptionCode.ERRO_RELACIONAMENTO_PHI_SSM.toString());
		}
	}
	
	public void limparPesquisa() {
		if(!this.vincularApac){
			tabela = null;
			cpgGrcSeq = null;
			cpgGrcDescricao = null;
			convenio = null;
			procedimentoInterno = null;
		}
		
		itemProcedHosp = null;	
		lista = null;
		listaOriginal = null;
		listaClones = new ArrayList<FatConvGrupoItemProced>(0);
		exibirPanelInferior = false;
		edicao = false;
		procedHospInterno = null;
		itemProcedHospSus = null;
		plano = null;
        inicializarConvGrupoItemProced();
		alterou = false;
		phi = null;
		telaPendencia = false;
		planoPendencia = null;
		convenioPendencia = null;
		exibirMsgError = Boolean.FALSE;
        inicio();
	}

    private void inicializarConvGrupoItemProced() {
        convGrupoItemProced = new FatConvGrupoItemProced();
        convGrupoItemProced.setIndExigeJustificativa(Boolean.FALSE);
        convGrupoItemProced.setIndCobrancaFracionada(Boolean.FALSE);
        convGrupoItemProced.setIndExigeAutorPrevia(Boolean.FALSE);
        convGrupoItemProced.setIndExigeNotaFiscal(Boolean.FALSE);
        convGrupoItemProced.setIndImprimeLaudo(Boolean.FALSE);
        convGrupoItemProced.setIndPaga(Boolean.FALSE);
    }
	
	public void editar(FatConvGrupoItemProced item) {
		convGrupoItemProced = item;	
		procedHospInterno = item.getProcedimentoHospitalarInterno();
		itemProcedHospSus = item.getItemProcedHospitalar();
		if (phi != null) {
			VFatConvPlanoGrupoProcedVO vPlanoPendencia = new VFatConvPlanoGrupoProcedVO();
			vPlanoPendencia.setCphCspSeq(item.getConvenioSaudePlano().getId().getSeq());
			vPlanoPendencia.setCspDescricao(item.getConvenioSaudePlano().getDescricao());
			VFatConvPlanoGrupoProcedVO vConvenioPendencia = new VFatConvPlanoGrupoProcedVO();
			vConvenioPendencia.setCphCspCnvCodigo(item.getConvenioSaudePlano().getConvenioSaude().getCodigo());
			vConvenioPendencia.setCnvDescricao(item.getConvenioSaudePlano().getConvenioSaude().getDescricao());
			planoPendencia = vPlanoPendencia;
			convenioPendencia = vConvenioPendencia;
		}
		edicao = true;
	}
	
	public void adicionar() {
		if (phi != null) {
			if (convenioPendencia == null) {
				this.apresentarMsgNegocio(Severity.ERROR,
						ProcedimentoCirurgicoControllerExceptionCode.ERRO_ADICIONAR_PROCEDIMENTO_SUS.toString(), "Convênio");
				return;
			}
			if (planoPendencia == null) {
				this.apresentarMsgNegocio(Severity.ERROR,
						ProcedimentoCirurgicoControllerExceptionCode.ERRO_ADICIONAR_PROCEDIMENTO_SUS.toString(), "Plano");
				return;
			}
		}
//		plano = new VFatConvPlanoGrupoProcedVO();
//		plano.setCphCspSeq(planoPendencia.getCphCspSeq());
//		plano.setCspDescricao(planoPendencia.getCspDescricao());
//		convenio = new VFatConvPlanoGrupoProcedVO();
//		convenio.setCphCspCnvCodigo(convenioPendencia.getCphCspCnvCodigo());
//		convenio.setCnvDescricao(convenioPendencia.getCnvDescricao());
		alterar();
	}
	
	public void alterar() {
		
		boolean erro = false;

		FatConvGrupoItemProcedId id = new FatConvGrupoItemProcedId(plano.getCphCspSeq(), convenio.getCphCspCnvCodigo(), tabela.getCphPhoSeq(), cpgGrcSeq, itemProcedHospSus.getId().getSeq(), itemProcedHospSus.getId().getPhoSeq(), procedHospInterno.getSeq());
		convGrupoItemProced.setId(id);
		convGrupoItemProced.setProcedimentoHospitalarInterno(procedHospInterno);
		convGrupoItemProced.setItemProcedHospitalar(itemProcedHospSus);
        if (!validaConvGrupoItemProcedNaoDuplicado(convGrupoItemProced)) {
            return;
        }

		if(edicao && !DominioOperacoesJournal.INS.equals(convGrupoItemProced.getOperacao())) {
			convGrupoItemProced.setOperacao(DominioOperacoesJournal.UPD);
		}
		else {
			if(lista.indexOf(convGrupoItemProced) == -1) { 				
				convGrupoItemProced.setOperacao(DominioOperacoesJournal.INS);
				lista.add(convGrupoItemProced);
				exibirModalInclusao = true;
			} else if(DominioOperacoesJournal.INS.equals(convGrupoItemProced.getOperacao())){
				lista.remove(convGrupoItemProced);
				lista.add(convGrupoItemProced);
				exibirModalInclusao = true;
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,  "FAT_00073");
				erro = true;
			}
		}
		if(!erro) {
			if(listaOriginal.indexOf(convGrupoItemProced) == -1) {
				listaOriginal.add(convGrupoItemProced);
			}
			else {
				listaOriginal.remove(convGrupoItemProced);
				listaOriginal.add(convGrupoItemProced);			
			}
			
			itemProcedHospSus = itemProcedHosp;
			procedHospInterno = procedimentoInterno;
			edicao = false;
			alterou = true;
			this.gravar();
			if (phi != null) {
				planoPendencia = null;
				convenioPendencia = null;
			}
            inicializarConvGrupoItemProced();
		}
	}

    private boolean validaConvGrupoItemProcedNaoDuplicado(FatConvGrupoItemProced itemProced) {
        if(lista != null) {
            for (FatConvGrupoItemProced item : lista) {
                if (item.getProcedimentoHospitalarInterno().equals(itemProced.getProcedimentoHospitalarInterno())
                        && item.getItemProcedHospitalar().equals(itemProced.getItemProcedHospitalar())) {
                    this.apresentarMsgNegocio(Severity.ERROR,  RelacionarPHISSMControllerExceptionCode
                            .RELACIONAMENTO_PHI_SSM_JA_ASSOCIADO.toString());
                    return false;
                }
            }
        }
        return true;
    }
	
	public void fecharModalInclusao() {
		exibirModalInclusao = false;
	}	
	
	public void cancelarEdicao() {
		itemProcedHospSus = itemProcedHosp;
		procedHospInterno = procedimentoInterno;
        inicializarConvGrupoItemProced();
		if (phi != null) {
			convenioPendencia = null;
			planoPendencia = null;
		}
		edicao = false;		
	}
	
	public void excluir(FatConvGrupoItemProced item) {
		alterou = true;
		FatConvGrupoItemProced itemLista = lista.get(lista.indexOf(item));
		lista.remove(itemLista);
		itemLista = listaOriginal.get(listaOriginal.indexOf(item));
		itemLista.setOperacao(DominioOperacoesJournal.DEL);	
		this.gravar();
	}
	
	public void gravar() {
		try {
			for (FatConvGrupoItemProced proced : listaOriginal) {
				if(DominioOperacoesJournal.DEL.equals(proced.getOperacao())) {
					faturamentoFacade.excluirGrupoItemConvenio(proced);
				} else if(DominioOperacoesJournal.UPD.equals(proced.getOperacao())) {
					faturamentoFacade.persistirGrupoItemConvenio(proced, listaClones.get(listaClones.indexOf(proced)), DominioOperacoesJournal.UPD);
				} else if(DominioOperacoesJournal.INS.equals(proced.getOperacao())) {
					faturamentoFacade.persistirGrupoItemConvenio(proced, null, DominioOperacoesJournal.INS);
				}
			}

			Iterator<FatConvGrupoItemProced> i = listaOriginal.iterator();
			while(i.hasNext()) {
				FatConvGrupoItemProced proced = (FatConvGrupoItemProced)i.next();
				if(DominioOperacoesJournal.DEL.equals(proced.getOperacao())) {
					listaClones.remove(proced);
					i.remove();
				} else if(DominioOperacoesJournal.INS.equals(proced.getOperacao())) {
					if (phi != null) {
						FatConvenioSaudePlano plano = faturamentoFacade.obterFatConvenioSaudePlano(convenioPendencia.getCphCspCnvCodigo(), planoPendencia.getCphCspSeq());
						proced.setConvenioSaudePlano(plano);
					}
					listaClones.add(faturamentoFacade.clonarGrupoItemConvenio(proced));
					proced.setOperacao(DominioOperacoesJournal.UPD);
				}								
			}
			
			for (FatConvGrupoItemProced proced : lista) {
				if(DominioOperacoesJournal.INS.equals(proced.getOperacao())) {
					proced.setOperacao(DominioOperacoesJournal.UPD);
				}				
			}

			alterou = false;
			this.apresentarMsgNegocio(Severity.INFO,  RelacionarPHISSMControllerExceptionCode.RELACIONAMENTO_PHI_SSM_GRAVADOS_COM_SUCESSO.toString());
		} catch(ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch(Exception e) {
            LOG.error(EXCECAO_CAPTURADA, e);
			this.apresentarMsgNegocio(Severity.ERROR,  RelacionarPHISSMControllerExceptionCode.ERRO_RELACIONAMENTO_PHI_SSM.toString());
		}
	}
	
	public void criarNotificacoesUsuarios(Integer phi, List<FatConvGrupoItemProced> convGrupoItemProcedList) {
		if (phi != null){
			StringBuilder mensagem = new StringBuilder(50);
			mensagem.append("O Procedimento Interno ").append(phi);
	
			if (convGrupoItemProcedList != null && !convGrupoItemProcedList.isEmpty()) {
				mensagem.append(", foi associado aos seguintes Procedimentos Relacionados: ");
				StringBuilder convenioPlano = new StringBuilder();
				for (FatConvGrupoItemProced convItemProced : convGrupoItemProcedList) {
					convenioPlano.append(", ")
					.append(convItemProced.getConvenioSaudePlano().getConvenioSaude().getDescricao())
					.append(" - ")
					.append(convItemProced.getConvenioSaudePlano().getDescricao())
					.append(" - ")
					.append(convItemProced.getItemProcedHospitalar().getDescricao());
				}
				mensagem.append(convenioPlano.toString().substring(1));
			} else {
				mensagem.append(", não foi associado à Procedimentos Relacionados.");
			}
	
			AghParametros pNotificacaoVinculacaoProcedimento = null;
			try {
				pNotificacaoVinculacaoProcedimento = parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOTIFICACAO_VINCULACAO_PROCEDIMENTOS);
			} catch (ApplicationBusinessException e) {
				LOG.error(null, e);
			}
	
			if (pNotificacaoVinculacaoProcedimento != null) {
				List<RapServidores> listaServidores = faturamentoFacade.buscaUsuariosPorCCusto(pNotificacaoVinculacaoProcedimento
						.getVlrNumerico().intValue());
				try {
					removerPendencias(listaServidores, phi);
					centralPendenciaFacade.adicionarPendenciaAcao(mensagem.toString(),
							"/faturamento/cadastroapoio/tabelas/relacionarPHIaSSM.seam?telaPendencia=true&phi=" + phi, "Relacionar PHI",
							listaServidores, false);
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}
	
	private void removerPendencias(List<RapServidores> listaServidores, Integer phi) {
		try {
			String pendencia = "O Procedimento Interno " + phi;
			for (PendenciaVO pendenciaVO : centralPendenciaFacade.getListaPendencias()) {
				if (pendenciaVO.getMensagem().startsWith(pendencia)) {
					centralPendenciaFacade.excluirPendencia(pendenciaVO.getSeqCaixaPostal());
				}
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(null, e);
		}
	}
	
	public void removerTodasNotificacoes() {
		AghParametros pNotificacaoVinculacaoProcedimento = null;
		try {
			pNotificacaoVinculacaoProcedimento = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOTIFICACAO_VINCULACAO_PROCEDIMENTOS);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}

		if (pNotificacaoVinculacaoProcedimento != null) {
			List<RapServidores> listaServidores = faturamentoFacade.buscaUsuariosPorCCusto(pNotificacaoVinculacaoProcedimento
					.getVlrNumerico().intValue());
			removerPendencias(listaServidores, phi);
		}
		
		apresentarMsgNegocio(Severity.INFO,  RelacionarPHISSMControllerExceptionCode.EXCLUSAO_NOTIFICACAO_COM_SUCESSO.toString());
	}
	
	public String getSimNao(Boolean b) {
		if(b != null) {
			return DominioSimNao.getInstance(b).getDescricao();	
		}
		return null;
	}
	
	public String voltarCadExames(){
		this.vincularApac = Boolean.FALSE;
        limparPesquisa();
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}
	public String voltarItemPrincipal() {
        limparPesquisa();
		return "faturamento-manterItemPrincipal";
	}
	
	//#############################################//
	//### Metodos abaixo usados nas suggestions ###//
	//#############################################//

	//Suggestion: Procedimento Hospitalar Interno
	public List<FatProcedHospInternos> listarPhis(String objPesquisa){
		return this.faturamentoFacade.listarPhisAtivosPorSeqEDescricao(objPesquisa);
	}
	
	public Long listarPhisCount(String objPesquisa){
		return this.faturamentoFacade.listarPhisAtivosPorSeqEDescricaoCount(objPesquisa);
	}

	//Suggestions: Tabelas e Tabela Itens
	public List<VFatConvPlanoGrupoProcedVO> listarTabelas(String objPesquisa){
		return this.faturamentoFacade.listarTabelas(objPesquisa);
	}
	
	public Long listarTabelasCount(String objPesquisa){
		return this.faturamentoFacade.listarTabelasCount(objPesquisa);
	}

	
	//Auxiliar para a suggestion: Tabelas
	public void executarAposLimparSuggestionTabela(){
		 this.cpgGrcSeq = null;
		 this.cpgGrcDescricao = null;
		 this.convenio = null;
		 this.plano = null;
	}

	public void executarAposSelecionarSuggestionTabela(){
		this.cpgGrcSeq = this.tabela.getGrcSeq(); 
		this.cpgGrcDescricao = this.tabela.getGrcDescricao();
	}
	
	//Auxiliar para a suggestion: Tabela Itens
	public void executarAposLimparSuggestionTabelaItens(){
		 this.itemProcedHosp = null;
	}
	
	
	//Suggestion: Convenios
	public List<VFatConvPlanoGrupoProcedVO> listarConvenios(Object objPesquisa){
		return this.faturamentoFacade.listarConvenios(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq());
	}

	public Long listarConveniosCount(Object objPesquisa){
		return this.faturamentoFacade.listarConveniosCount(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq());
	}

	public void executarAposLimparSuggestionConvenio(){
		if (phi != null) {
			planoPendencia = null;
		}
		this.plano = null;
	}
	
	//Suggestion: Planos
	public List<VFatConvPlanoGrupoProcedVO> listarPlanos(String objPesquisa){
        return this.returnSGWithCount(listarPlanosSemCount(objPesquisa), listarPlanosCount(objPesquisa));
	}

    /**
	 * MEtodo sem o count é utilizado pela tela de procedimentos cirurgicos, 
	 * e lá estava mostrando mensagem com o total de registros o que não era o comportamento esperado pela tela
	 * @param objPesquisa
	 * @return
	 */
	public List<VFatConvPlanoGrupoProcedVO> listarPlanosSemCount(String objPesquisa){
		return this.faturamentoFacade.listarPlanos(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq(), this.convenio.getCphCspCnvCodigo());
	}
	
	public Long listarPlanosCount(String objPesquisa){
		return this.faturamentoFacade.listarPlanosCount(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq(), this.convenio.getCphCspCnvCodigo());
	}
	
	public List<VFatConvPlanoGrupoProcedVO> listarPlanosPendencia(String objPesquisa){
		return this.faturamentoFacade.listarPlanos(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq(), this.convenioPendencia.getCphCspCnvCodigo());
	}
	
	public Long listarPlanosPendenciaCount(String objPesquisa){
		return this.faturamentoFacade.listarPlanosCount(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq(), this.convenioPendencia.getCphCspCnvCodigo());
	}
	
	//Suggestion: Itens Procecimento Hospitalar
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(Object objPesquisa) {
		return this.faturamentoFacade.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeq(objPesquisa, this.tabela.getCphPhoSeq());
	}
	
	public Long listarFatItensProcedHospitalarCount(Object objPesquisa) {
		return this.faturamentoFacade.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa, this.tabela.getCphPhoSeq());	
	}

	//Suggestion: Itens Procecimento Hospitalar Ativos
	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(String objPesquisa){
		return this.returnSGWithCount(this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(
                objPesquisa, this.tabela.getCphPhoSeq()), listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa));
	}
	
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(String objPesquisa){
		return this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa, this.tabela.getCphPhoSeq());
	}

	//Suggestion: Procedimento Hospitalares Internos
	public List<FatProcedHospInternos> listarPhisAtivosPorSeqEDescricao(String objPesquisa){
		return this.returnSGWithCount(this.faturamentoFacade.listarPhisAtivosPorSeqEDescricao(objPesquisa),
                                        listarPhisAtivosPorSeqEDescricaoCount(objPesquisa));
	}
	
	public Long listarPhisAtivosPorSeqEDescricaoCount(String objPesquisa){
		return this.faturamentoFacade.listarPhisAtivosPorSeqEDescricaoCount(objPesquisa);
	}

	public Short getCpgCphCspCnvCodigo() {
		return cpgCphCspCnvCodigo;
	}

	public void setCpgCphCspCnvCodigo(Short cpgCphCspCnvCodigo) {
		this.cpgCphCspCnvCodigo = cpgCphCspCnvCodigo;
	}

	public Byte getCpgCphCspSeq() {
		return cpgCphCspSeq;
	}

	public void setCpgCphCspSeq(Byte cpgCphCspSeq) {
		this.cpgCphCspSeq = cpgCphCspSeq;
	}

	public Short getCpgCphPhoSeq() {
		return cpgCphPhoSeq;
	}

	public void setCpgCphPhoSeq(Short cpgCphPhoSeq) {
		this.cpgCphPhoSeq = cpgCphPhoSeq;
	}

	public Short getCpgGrcSeq() {
		return cpgGrcSeq;
	}

	public void setCpgGrcSeq(Short cpgGrcSeq) {
		this.cpgGrcSeq = cpgGrcSeq;
	}

	public String getCpgGrcDescricao() {
		return cpgGrcDescricao;
	}

	public void setCpgGrcDescricao(String cpgGrcDescricao) {
		this.cpgGrcDescricao = cpgGrcDescricao;
	}

	public VFatConvPlanoGrupoProcedVO getTabela() {
		return tabela;
	}

	public void setTabela(VFatConvPlanoGrupoProcedVO tabela) {
		this.tabela = tabela;
	}

	public VFatConvPlanoGrupoProcedVO getConvenio() {
		return convenio;
	}

	public void setConvenio(VFatConvPlanoGrupoProcedVO convenio) {
		this.convenio = convenio;
	}

	public VFatConvPlanoGrupoProcedVO getPlano() {
		return plano;
	}

	public void setPlano(VFatConvPlanoGrupoProcedVO plano) {
		this.plano = plano;
	}

	public FatItensProcedHospitalar getItemProcedHosp() {
		return itemProcedHosp;
	}

	public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
		this.itemProcedHosp = itemProcedHosp;
	}

	public Boolean getExibirPanelInferior() {
		return exibirPanelInferior;
	}

	public void setExibirPanelInferior(Boolean exibirPanelInferior) {
		this.exibirPanelInferior = exibirPanelInferior;
	}

	public FatProcedHospInternos getProcedHospInterno() {
		return procedHospInterno;
	}

	public void setProcedHospInterno(FatProcedHospInternos procedHospInterno) {
		this.procedHospInterno = procedHospInterno;
	}

	public FatConvGrupoItemProced getConvGrupoItemProced() {
		return convGrupoItemProced;
	}

	public void setConvGrupoItemProced(FatConvGrupoItemProced convGrupoItemProced) {
		this.convGrupoItemProced = convGrupoItemProced;
	}

	public List<FatConvGrupoItemProced> getLista() {
		return lista;
	}

	public void setLista(List<FatConvGrupoItemProced> lista) {
		this.lista = lista;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getAlterou() {
		return alterou;
	}

	public void setAlterou(Boolean alterou) {
		this.alterou = alterou;
	}

	public Boolean getVoltar() {
		return voltar;
	}

	public void setVoltar(Boolean voltar) {
		this.voltar = voltar;
	}
	

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public Boolean getExibirModalInclusao() {
		return exibirModalInclusao;
	}

	public void setExibirModalInclusao(Boolean exibirModalInclusao) {
		this.exibirModalInclusao = exibirModalInclusao;
	}

	public FatProcedHospInternos getProcedimentoInterno() {
		return procedimentoInterno;
	}

	public void setProcedimentoInterno(FatProcedHospInternos procedimentoInterno) {
		this.procedimentoInterno = procedimentoInterno;
	}

	public FatItensProcedHospitalar getItemProcedHospSus() {
		return itemProcedHospSus;
	}

	public void setItemProcedHospSus(FatItensProcedHospitalar itemProcedHospSus) {
		this.itemProcedHospSus = itemProcedHospSus;
	}
	
	public Integer getPhi() {
		return phi;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}

	public Boolean getTelaPendencia() {
		return telaPendencia;
	}

	public void setTelaPendencia(Boolean telaPendencia) {
		this.telaPendencia = telaPendencia;
	}

	public VFatConvPlanoGrupoProcedVO getConvenioPendencia() {
		return convenioPendencia;
	}

	public void setConvenioPendencia(VFatConvPlanoGrupoProcedVO convenioPendencia) {
		this.convenioPendencia = convenioPendencia;
	}

	public VFatConvPlanoGrupoProcedVO getPlanoPendencia() {
		return planoPendencia;
	}

	public void setPlanoPendencia(VFatConvPlanoGrupoProcedVO planoPendencia) {
		this.planoPendencia = planoPendencia;
	}

	public Boolean getVincularApac() {
		return vincularApac;
	}

	public void setVincularApac(Boolean vincularApac) {
		this.vincularApac = vincularApac;
	}
}
