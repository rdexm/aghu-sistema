package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioMbcCirurgia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoItemContaApac;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.MbcCirurgiaVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatLogInterface;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.MbcExtratoCirurgiaId;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceItemRmpsId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ORADB TRIGGER MBCP_ENFORCE_CRG_RULES
 * tabela MBC_CIRURGIAS
 * 
 * @author aghu
 *
 */
@Stateless
public class FatPaTuTrCnVmBcRN extends BaseBusiness {
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(FatPaTuTrCnVmBcRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private ObjetosOracleDAO objetosOracleDAO;


	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private IEstoqueFacade iEstoqueFacade;

	@EJB
	private ISolicitacaoExameFacade iSolicitacaoExameFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private MbcExtratoCirurgiaRN mbcExtratoCirurgiaRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6123726937951344836L;

	
	private enum FatPaTuTrCnVmBcRNExceptionCode implements BusinessExceptionCode {
		ERRO_ESTORNAR_ITEM_APAC,//
		ERRO_ESTORNAR_ITEM_AMBULATORIO,//
		;
	}
	
	
	public void posAtualizar(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws BaseException{
		//RN1
		// #35280 - Apenas inserir quando alterar a situação da cirurgia 
		if (cirurgia.getSituacao()!=null && cirurgiaOld.getSituacao()!=null &&
				!cirurgia.getSituacao().equals(cirurgiaOld.getSituacao())){
			this.inserirExtratoCirurgia(cirurgia);
		}	
		try {
			//RN2
			this.executarFaturamentoTrocaConvenioPlano(cirurgia, cirurgiaOld);
		} catch(InactiveModuleException e) {
			logWarn(e.getMessage());
			this.getObjetosOracleDAO().executarFaturamentoTrocaConvenioPlano(
					cirurgia.getAtendimento().getSeq(), cirurgia.getSeq(),
					cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(),
					cirurgia.getConvenioSaudePlano().getId().getSeq(),
					cirurgiaOld.getConvenioSaudePlano().getId().getCnvCodigo(),
					cirurgiaOld.getConvenioSaudePlano().getId().getSeq(),
					servidorLogadoFacade.obterServidorLogado().getUsuario());
		}		
		
		this.verificarProfissionalResponsavel(cirurgia, cirurgiaOld);
		this.cancelarExameProvaCruzadaTransfusional(cirurgia, cirurgiaOld, null);
	}
	
	private void verificarProfissionalResponsavel(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws BaseException {
		RapServidores responsavel = getBlocoCirurgicoFacade().buscaRapServidorDeMbcProfCirurgias(cirurgia.getSeq(), DominioSimNao.S);		
		
		if(responsavel != null && cirurgiaOld.getAtendimento() == null && cirurgia.getAtendimento() != null 
				&& DominioPacAtendimento.S.equals(cirurgia.getAtendimento().getIndPacAtendimento())
				&& !cirurgia.getSituacao().equals(DominioSituacaoCirurgia.RZDA)
				&& !cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC)) {
			try {
				getSolicitacaoExameFacade().gerarExameProvaCruzadaTransfusional(cirurgia.getAtendimento(), cirurgia, null, responsavel, Boolean.TRUE);
			} catch(InactiveModuleException e) {
				logWarn(e.getMessage());
				this.getObjetosOracleDAO()
						.gerarExameProvaCruzadaTransfusional(
								cirurgia.getAtendimento().getSeq(), cirurgia.getSeq(), servidorLogadoFacade.obterServidorLogado(), responsavel, DominioSimNao.S.toString());
			}
		}
	}
	
	private void cancelarExameProvaCruzadaTransfusional(MbcCirurgias cirurgia,
			MbcCirurgias cirurgiaOld, final String nomeMicrocomputador) throws BaseException {

		if(cirurgia.getAtendimento() != null && cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC)
				&& !cirurgiaOld.getSituacao().equals(DominioSituacaoCirurgia.CANC)) {		
			try {
				getSolicitacaoExameFacade().cancelarExameProvaCruzadaTransfusional(
						cirurgia.getAtendimento(), cirurgia, nomeMicrocomputador);
			} catch (InactiveModuleException e) {
				logWarn(e.getMessage());
				this.getObjetosOracleDAO().cancelarExameProvaCruzadaTransfusional(cirurgia.getAtendimento().getSeq(), cirurgia.getSeq(), servidorLogadoFacade.obterServidorLogado());
			}
		}
	}
	
	private ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	/**
	 * ORADB PROCEDURE RN_CRGP_ATU_EXTRATO
	 * 
	 * @param cirurgia
	 * @param usuarioLogado
	 * @throws BaseException
	 */
	public void inserirExtratoCirurgia(MbcCirurgias cirurgia) throws BaseException {
		MbcExtratoCirurgia extratoCirurgia = new MbcExtratoCirurgia();
		extratoCirurgia.setSituacaoCirg(cirurgia.getSituacao());
		extratoCirurgia.setCriadoEm(null);
		
		MbcExtratoCirurgiaId id = new MbcExtratoCirurgiaId();
		id.setCrgSeq(cirurgia.getSeq());
		extratoCirurgia.setId(id);
		
		extratoCirurgia.setCirurgia(cirurgia);
			
		this.getMbcExtratoCirurgiaRN().inserir(extratoCirurgia, servidorLogadoFacade.obterServidorLogado().getUsuario());
	}
	
	
	/**
	 * ORADB PROCEDURE fatk_sus_rn.rn_fatp_atu_trcnvmbc
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @throws BaseException 
	 */
	public void executarFaturamentoTrocaConvenioPlano(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws BaseException {
		
		Short valorCodConv = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_COD_CONV_75).getVlrNumerico().shortValue();
		
		
		//AghParametros phiCateterDuplo = this.getParametroFacade().buscarAghParametro(
			//	AghuParametrosEnum.P_PHI_IMPL_CAT_DUPLO_J);
		
		
		DominioMbcCirurgia vGrupoNew = null;
		DominioMbcCirurgia vGrupoOld = null;
		DominioMbcCirurgia vPlanoNew = null;
		DominioMbcCirurgia vPlanoOld = null;
		DominioMbcCirurgia vEvento 	= null;
		
		if(cirurgia.getConvenioSaudePlano() != null 
				&& CoreUtil.modificados(
						cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(),
						cirurgiaOld.getConvenioSaudePlano().getId().getCnvCodigo())
				|| CoreUtil.modificados(
						cirurgia.getConvenioSaudePlano().getId().getSeq(),
						cirurgiaOld.getConvenioSaudePlano().getId().getSeq())) {
									
			final FatConvenioSaudePlano convenioSaudePlano = cirurgia.getConvenioSaudePlano();
			final FatConvenioSaudePlano convenioSaudePlanoOld = cirurgiaOld.getConvenioSaudePlano();
			
			if(convenioSaudePlano != null && convenioSaudePlano.getConvenioSaude() != null) {
				final DominioGrupoConvenio grupoConvenio = 
					convenioSaudePlano.getConvenioSaude().getGrupoConvenio();
				
				vGrupoNew = this.obterGrupoDominio(grupoConvenio);
				
				DominioMbcCirurgia dominioMbcCirurgia = this.getDominioMbcCirurgiaPorDominioTipoPlano(convenioSaudePlano.getIndTipoPlano());
	
				vPlanoNew = (DominioMbcCirurgia) CoreUtil.nvl(
						dominioMbcCirurgia, DominioMbcCirurgia.N);
			}
			
			if(convenioSaudePlanoOld != null && convenioSaudePlanoOld.getConvenioSaude() != null) {
				final DominioGrupoConvenio grupoConvenioOld = 
					convenioSaudePlanoOld.getConvenioSaude().getGrupoConvenio();
								
				vGrupoOld = this.obterGrupoConvenio(grupoConvenioOld);

				DominioMbcCirurgia dominioMbcCirurgia = this.getDominioMbcCirurgiaPorDominioTipoPlano(convenioSaudePlanoOld.getIndTipoPlano());

				vPlanoOld = (DominioMbcCirurgia) CoreUtil.nvl(
						dominioMbcCirurgia, DominioMbcCirurgia.N);
			}
			
			// Se entrar em 75 cobranca dos dados somente em AMBULATORIO
		    // exclui da ICH e inclui na PMR, independentemente do CNV anterior
			if(cirurgia.getConvenioSaudePlano().getId().getCnvCodigo().equals(valorCodConv)
					&& !cirurgiaOld.getConvenioSaudePlano().getId().getCnvCodigo().equals(valorCodConv)) {
				vEvento = DominioMbcCirurgia.EI;
				vPlanoOld = DominioMbcCirurgia.I;
			}else if(!cirurgia.getConvenioSaudePlano().getId().getCnvCodigo().equals(valorCodConv)
					&& cirurgiaOld.getConvenioSaudePlano().getId().getCnvCodigo().equals(valorCodConv)) {
				
				if(DominioMbcCirurgia.S.equals(vGrupoNew)) {
					if(DominioMbcCirurgia.A.equals(vPlanoNew)){
						vEvento = DominioMbcCirurgia.A;
					}else if(DominioMbcCirurgia.I.equals(vPlanoNew)) {
						vEvento = DominioMbcCirurgia.EI;
						vPlanoOld = DominioMbcCirurgia.A;
					}
				}else {
					vEvento = DominioMbcCirurgia.E;
					vPlanoOld = DominioMbcCirurgia.A;
				}
			}else if(DominioMbcCirurgia.S.equals(vGrupoNew) && DominioMbcCirurgia.N.equals(vGrupoOld)){
				vEvento = DominioMbcCirurgia.I;
			}else if(DominioMbcCirurgia.S.equals(vGrupoNew) && DominioMbcCirurgia.S.equals(vGrupoOld)){
				vEvento = DominioMbcCirurgia.EI;
			}else if(DominioMbcCirurgia.N.equals(vGrupoNew) && DominioMbcCirurgia.S.equals(vGrupoOld)
					|| DominioMbcCirurgia.N.equals(vGrupoNew) && DominioMbcCirurgia.N.equals(vPlanoNew)){
				vEvento = DominioMbcCirurgia.E;
			}
		}
		
		// ajusta conv/plano ambulatorio internacao nao precisa pois o cnv/pla e da CTH
		if("A".equals(vEvento) && "A".equals(vPlanoOld)) {
			this.atualizarFatProcedAmbRealizado(cirurgia, cirurgiaOld);
		}
				
		//ESTORNAR DE FAT_PROCED_AMB_REALIZADOS OR FAT_ITENS_CONTA_APAC
		this.estornarFatProcedAmbRealizados(cirurgia, vEvento, vPlanoNew, vPlanoOld);
		
	
		 //--- INSERE LOG_INTERFACES
		final FatLogInterface logInterface = new FatLogInterface();

		logInterface.setSistema(this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SIGLA_ESTOQUE).getVlrTexto());
		logInterface.setDthrChamada(new Date());
		logInterface.setCirurgia(cirurgia);
		logInterface.setMbcQtd(Integer.valueOf(1));
		logInterface.setMensagem("TROCA CONV");
		logInterface.setLinProcedure("RN_FATP_ATU_TRC");

		this.getFaturamentoFacade().inserirFatLogInterface(logInterface);
	}

	private DominioMbcCirurgia getDominioMbcCirurgiaPorDominioTipoPlano(DominioTipoPlano tipoPlano) {
		if (DominioTipoPlano.A.equals(tipoPlano)) {
			return DominioMbcCirurgia.A;
		} else if (DominioTipoPlano.I.equals(tipoPlano)) {
			return DominioMbcCirurgia.I;
		}
		return null;
	}
	
	public void estornarFatProcedAmbRealizados(MbcCirurgias cirurgia, DominioMbcCirurgia vEvento, DominioMbcCirurgia vPlanoNew, DominioMbcCirurgia vPlanoOld) throws BaseException {
		Integer phiImplDuploJ = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_PHI_IMPL_CAT_DUPLO_J).getVlrNumerico().intValue();
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(DominioMbcCirurgia.E.equals(vEvento) || DominioMbcCirurgia.EI.equals(vEvento)) {
			List<FatItemContaApac> fatItemContaApacList = null;
			if(DominioMbcCirurgia.A.equals(vPlanoOld)){
				fatItemContaApacList = 
					this.getFaturamentoFacade().listarFatItemContaApacPorPpcCrgSeq(cirurgia.getSeq());
				try {
					for (FatItemContaApac elemento : fatItemContaApacList) {
						this.getFaturamentoFacade().removerFatItemContaApac(elemento);
					}
				}catch (ApplicationBusinessException e) {
					fatItemContaApacList = this.getFaturamentoFacade().listarFatItemContaApac(cirurgia.getSeq());
					if(fatItemContaApacList.isEmpty()) {
						throw new ApplicationBusinessException(FatPaTuTrCnVmBcRNExceptionCode.ERRO_ESTORNAR_ITEM_APAC);
					}
					for (FatItemContaApac elemento : fatItemContaApacList) {
						elemento.setIndSituacao(DominioSituacaoItemContaApac.C);
						this.getFaturamentoFacade().atualizarFatItemContaApac(elemento, null, Boolean.FALSE);
					}
				}
				
				List<FatProcedAmbRealizado> fatProcedAmbRealizadoList = 
					this.getFaturamentoFacade().listarFatProcedAmbRealizadoPorCrgSeq(cirurgia.getSeq());
				try {
					for (FatProcedAmbRealizado elemento : fatProcedAmbRealizadoList) {
						this.getFaturamentoFacade().removerFatProcedAmbRealizado(elemento);
					}
					
				}catch (ApplicationBusinessException e) {
					fatProcedAmbRealizadoList = this.getFaturamentoFacade()
						.listarFatProcedAmbRealizadoPorCrgSeqSituacao(cirurgia.getSeq());
					if(fatProcedAmbRealizadoList.isEmpty()) {
						throw new ApplicationBusinessException(FatPaTuTrCnVmBcRNExceptionCode.ERRO_ESTORNAR_ITEM_AMBULATORIO);
					}
					for (FatProcedAmbRealizado elemento : fatProcedAmbRealizadoList) {
						elemento.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
						this.getFaturamentoFacade().atualizarFatProcedAmbRealizado(elemento);
					}
				}
			}else if(DominioMbcCirurgia.I.equals(vPlanoOld)) {
				//ESTORNAR DE FAT_ITENS_CONTA HOSPITALAR
		       //ESTORNA ITENS DE CIRURGIA E MATERIAIS UTILIZADOS NA CIRURGIA
				List<FatItemContaHospitalar> fatItemContaHospitalarList = 
					this.getFaturamentoFacade().listarContaHospitalarPorCirurgia(cirurgia.getSeq());
				
				for (FatItemContaHospitalar elemento : fatItemContaHospitalarList) {
					this.getFaturamentoFacade().removerItemContaHospitalar(elemento);
				}
				
				List<FatItemContaHospitalar> listFatItemPorSceRmrPacientes = 
					this.getFaturamentoFacade().listarContaHospitalarPorSceRmrPacientes(cirurgia.getSeq());
				for (FatItemContaHospitalar elemento : listFatItemPorSceRmrPacientes) {
					this.getFaturamentoFacade().removerItemContaHospitalar(elemento);
				}
			}
		}
		
		if(DominioMbcCirurgia.I.equals(vEvento) || DominioMbcCirurgia.EI.equals(vEvento)) {
			//INSERIR EM FAT_PROCED_AMB_REALIZADOS
			if(DominioMbcCirurgia.A.equals(vPlanoNew)) {
				
				List<MbcCirurgiaVO> listaVO = this.getBlocoCirurgicoFacade()
					.listarMbcProcEspPorCirurgiasPorFatProcedAmbRealizado(cirurgia.getSeq(), null, Boolean.FALSE);
				
				for (MbcCirurgiaVO vo : listaVO) {
					FatProcedAmbRealizado elemento = new FatProcedAmbRealizado();
					elemento.setDthrRealizado(vo.getDataFimCirg());
					elemento.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
					if(vo.getQuantidade() != null) {
						elemento.setQuantidade(vo.getQuantidade().shortValue());
					}
					elemento.setProcedimentoHospitalarInterno(
							this.getFaturamentoFacade().obterProcedimentoHospitalarInterno(vo.getPhiSeq()));
					elemento.setPaciente(this.getPacienteFacade()
							.obterAipPacientesPorChavePrimaria(vo.getPacCodigo()));
					elemento.setIndOrigem(DominioOrigemProcedimentoAmbulatorialRealizado.CIA);
					elemento.setLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B);
					elemento.setPpcIndRespProc(vo.getIndRespProc());
					elemento.setPpcEprEspSeq(vo.getEprEspSeq());
					elemento.setPpcEprPciSeq(vo.getEprPciSeq());
					elemento.setPpcCrgSeq(vo.getCrgSeq());
					elemento.setEspecialidade(this.getAghuFacade()
							.obterAghEspecialidadesPorChavePrimaria(vo.getEprEspSeq()));
					elemento.setUnidadeFuncional(this.getAghuFacade().obterUnidadeFuncional(vo.getUnfSeq()));
					elemento.setServidor(cirurgia.getServidor());
					elemento.setConvenioSaudePlano(cirurgia.getConvenioSaudePlano());
					
					/**
					 *  insert into fat_proced_amb_realizados  (FatProcedAmbRealizado)
					 */
					this.getFaturamentoFacade().inserirProcedimentosAmbulatoriaisRealizados(elemento, null, null);
				}
				
				//--verifica se o item é impl de cateter e insere o cateter ETB 260203
				
				List<MbcCirurgiaVO> listaCateterVO = this.getBlocoCirurgicoFacade()
					.listarMbcProcEspPorCirurgiasPorFatProcedAmbRealizado(cirurgia.getSeq(), phiImplDuploJ, Boolean.FALSE);
				
				for (MbcCirurgiaVO vo : listaCateterVO) {
					FatProcedAmbRealizado elemento = new FatProcedAmbRealizado();
					elemento.setDthrRealizado(vo.getDataFimCirg());
					elemento.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
					elemento.setLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B);
					if(vo.getQuantidade() != null) {
						elemento.setQuantidade(vo.getQuantidade().shortValue());
					}
					elemento.setProcedimentoHospitalarInterno(
							this.getFaturamentoFacade().obterProcedimentoHospitalarInterno(phiImplDuploJ));
					elemento.setUnidadeFuncional(this.getAghuFacade().obterUnidadeFuncional(vo.getUnfSeq()));
					elemento.setEspecialidade(this.getAghuFacade()
							.obterAghEspecialidadesPorChavePrimaria(vo.getEprEspSeq()));
					elemento.setPaciente(this.getPacienteFacade()
							.obterAipPacientesPorChavePrimaria(vo.getPacCodigo()));
					elemento.setServidor(cirurgia.getServidor());
					elemento.setIndOrigem(DominioOrigemProcedimentoAmbulatorialRealizado.CIA);
					elemento.setPpcCrgSeq(vo.getCrgSeq());
					elemento.setPpcEprEspSeq(vo.getEprEspSeq());
					elemento.setPpcEprPciSeq(vo.getEprPciSeq());
					elemento.setPpcIndRespProc(vo.getIndRespProc());
					elemento.setAtendimento(this.getAghuFacade()
							.obterAghAtendimentoPorChavePrimaria(vo.getAtdSeq()));
					
					/**
					 *  insert into fat_proced_amb_realizados 
					 */
					this.getFaturamentoFacade().inserirProcedimentosAmbulatoriaisRealizados(elemento, null, null);
				
				}
				
			}else if(DominioMbcCirurgia.I.equals(vPlanoNew)) {
				List<MbcCirurgiaVO> listaItemCirgVO = this.getBlocoCirurgicoFacade()
					.listarMbcProcEspPorCirurgiasPorFatProcedAmbRealizado(cirurgia.getSeq(), null, Boolean.TRUE);
				/*--- pega a data final da cirurgia **/
				MbcCirurgias mbcCirurgia = 
					this.getBlocoCirurgicoFacade().obterMbcCirurgiaPorSituacaoRealizada(cirurgia.getSeq());
				
				Integer maxSeqConta = null;
				if(mbcCirurgia != null) {
					maxSeqConta = this.getFaturamentoFacade().obterMaxSeqConta(
							mbcCirurgia.getDataFimCirurgia(), this.obterAtendimentoSeq(cirurgia.getAtendimento()), 
							this.obterCnvCodigo(cirurgia.getConvenioSaudePlano()), 
							this.obterCnvSeq(cirurgia.getConvenioSaudePlano()));
				}
				
				if(maxSeqConta != null) {
					//BUSCA PROXIMO SEQ NA ICH
					Short ichSeqp = this.getFaturamentoFacade().obterProximoSeqFatItensContaHospitalar(maxSeqConta);
									
					for (MbcCirurgiaVO vo : listaItemCirgVO) {
						FatItemContaHospitalar elemento = new FatItemContaHospitalar();
						FatItemContaHospitalarId id = new FatItemContaHospitalarId(maxSeqConta, ichSeqp);
						elemento.setId(id);
						elemento.setIchType(DominioItemConsultoriaSumarios.ICH);
						elemento.setDthrRealizado(vo.getDataFimCirg());
						elemento.setIndSituacao(DominioSituacaoItenConta.A);
						elemento.setQuantidade(vo.getQuantidade().shortValue());
						elemento.setQuantidadeRealizada(vo.getQuantidade().shortValue());
						elemento.setProcedimentoHospitalarInterno(
								this.getFaturamentoFacade().obterFatProcedHospInternosPorChavePrimaria(
										vo.getCrgSeq()));
						elemento.setUnidadesFuncional(this.getAghuFacade().obterUnidadeFuncional(vo.getUnfSeq()));
						elemento.setIndOrigem(DominioIndOrigemItemContaHospitalar.BCC);
						elemento.setProcEspPorCirurgias(
								this.getBlocoCirurgicoFacade().obterMbcProcEspPorCirurgiasPorChavePrimaria(
										new MbcProcEspPorCirurgiasId(
												vo.getCrgSeq()
												, vo.getEprPciSeq()
												, vo.getEprEspSeq()
												, vo.getIndRespProc()
										)
								)
						);
						
						this.getFaturamentoFacade().persistirItemContaHospitalar(elemento, null,servidorLogado, 
								 null);
					}
					
					//c_itens_opm
					List<MbcCirurgiaVO> itensOpm = this.getBlocoCirurgicoFacade().listarItensOrteseProtese(cirurgia.getSeq());
					for (MbcCirurgiaVO vo : itensOpm) {
						//-- INSERIR EM FAT_ITENS_CONTA_HOSPITALAR
						FatItemContaHospitalar elemento = new FatItemContaHospitalar();
						FatItemContaHospitalarId id = new FatItemContaHospitalarId(maxSeqConta, ichSeqp);
						elemento.setId(id);
						elemento.setIchType(DominioItemConsultoriaSumarios.ICH);
						elemento.setDthrRealizado(vo.getDataFimCirg());
						elemento.setIndSituacao(DominioSituacaoItenConta.A);
						elemento.setQuantidade(vo.getQuantidadeIps().shortValue());
						elemento.setQuantidadeRealizada(vo.getQuantidadeIps().shortValue());
						elemento.setProcedimentoHospitalarInterno(
								this.getFaturamentoFacade().obterFatProcedHospInternosPorChavePrimaria(
										vo.getPhiSeq()));
						elemento.setUnidadesFuncional(this.getAghuFacade().obterUnidadeFuncional(vo.getUnfSeq()));
						elemento.setIndOrigem(DominioIndOrigemItemContaHospitalar.BCC);
						elemento.setItemRmps(this.getEstoqueFacade()
								.obterSceItemRmpsPorChavePrimaria(new SceItemRmpsId(vo.getRmpSeq(), vo.getNumero())));
								
						this.getFaturamentoFacade().persistirItemContaHospitalar(elemento, null,servidorLogado, null);
					}
				}
			}
		}
	}
	
	
	private Short obterCnvCodigo(FatConvenioSaudePlano cnvSaudePlano) {
		if(cnvSaudePlano != null) {
			return cnvSaudePlano.getId().getCnvCodigo();
		}
		return null;
	}
	
	private Byte obterCnvSeq(FatConvenioSaudePlano cnvSaudePlano) {
		if(cnvSaudePlano != null) {
			return cnvSaudePlano.getId().getSeq();
		}
		return null;
	}
	
	private Integer obterAtendimentoSeq(AghAtendimentos atendimento) {
		if(atendimento != null) {
			return atendimento.getSeq();
		}
		return null;
	}
	
	
	public DominioMbcCirurgia obterGrupoConvenio(DominioGrupoConvenio grupoConvenioOld) {
		if(DominioGrupoConvenio.S == grupoConvenioOld) {
			return DominioMbcCirurgia.S;
		}else {
			return DominioMbcCirurgia.N;
		}
	}
	
	public DominioMbcCirurgia obterGrupoDominio(DominioGrupoConvenio grupoConvenio) {
		if(DominioGrupoConvenio.S == grupoConvenio) {
			return DominioMbcCirurgia.S;
		}else {
			return DominioMbcCirurgia.N;
		}
	}
	
	public void atualizarFatProcedAmbRealizado(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) {
		FatProcedAmbRealizado fatProcedAmbRealizado = 
			this.getFaturamentoFacade().obterFatProcedAmbRealizadoPorCrgSeq(
					cirurgia.getSeq(), 
					cirurgiaOld.getConvenioSaudePlano().getId().getCnvCodigo(), 
					cirurgiaOld.getConvenioSaudePlano().getId().getSeq());
		fatProcedAmbRealizado.setConvenioSaudePlano(cirurgia.getConvenioSaudePlano());
		this.getFaturamentoFacade().atualizarProcedAmbRealizado(fatProcedAmbRealizado);
	}
	
	
	/** GET **/
	protected MbcExtratoCirurgiaRN getMbcExtratoCirurgiaRN() {
		return mbcExtratoCirurgiaRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return this.iPacienteFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.iEstoqueFacade;
	}
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return iSolicitacaoExameFacade;
	}
}