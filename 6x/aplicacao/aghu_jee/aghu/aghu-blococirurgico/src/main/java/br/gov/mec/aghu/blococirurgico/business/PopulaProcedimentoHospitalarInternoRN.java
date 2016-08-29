package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.RegistroCirurgiaRealizadaON.RegistroCirurgiaRealizadaONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.CursoPopulaProcedimentoHospitalarInternoVO;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * ORADB PROCEDURE MBCP_POPULA_PHI
 * 
 * @author aghu
 * 
 */
@Stateless
public class PopulaProcedimentoHospitalarInternoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PopulaProcedimentoHospitalarInternoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;


	@EJB
	private IInternacaoFacade iInternacaoFacade;

	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private PopulaCidRN populaCidRN;

	@EJB
	private ValorCadastralProcedimentoSusRN valorCadastralProcedimentoSusRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private DataCompetenciaRegistroCirurgiaRealizadoRN dataCompetenciaRegistroCirurgiaRealizadoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8893249542849155496L;

	/**
	 * ORADB PROCEDURE MBCP_POPULA_PHI
	 * 
	 * @param procedimentoVO
	 * @param cirurgia
	 * @throws BaseException
	 */
	public void popularProcedimentoHospitalarInterno(CirurgiaTelaProcedimentoVO procedimentoVO, MbcCirurgias cirurgia) throws BaseException {

		Date valorDtComp = getDataCompetenciaRegistroCirurgiaRealizadoRN().getValorDataCompetencia(cirurgia.getOrigemPacienteCirurgia());
		
		// Rotina para buscar valor da tabela de parâmetros que define o procedimento como parto ou cesarea
		AghParametros pTipoGrupoContaSus = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		final Short valorpTipoGrupoContaSus = pTipoGrupoContaSus.getVlrNumerico().shortValue();

		// CURSOR CUR_SSM
		List<CursoPopulaProcedimentoHospitalarInternoVO> listaSsm1 = this.getFaturamentoFacade().obterCursorSSM(
				procedimentoVO.getId().getEprPciSeq(),
				valorDtComp, 
				cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(), 
				cirurgia.getConvenioSaudePlano().getId().getSeq(), 
				valorpTipoGrupoContaSus);

		if (listaSsm1.isEmpty()) {

			resetCamposCGNBT(procedimentoVO);

			boolean isLancaItemAmbulatorio = this.getInternacaoFacade().verificarCaracteristicaEspecialidade(cirurgia.getEspecialidade().getSeq(),
					DominioCaracEspecialidade.LANCA_ITEM_AMBULATORIO);

			// Chamada para PROCEDURE MBCC_PCI_AMBULATORIO
			Integer pciAmbulatorio = this.obterProcedimentoCirurgicoAmbulatorio(procedimentoVO.getId().getEprPciSeq());
			boolean isProcedimentoAmbulatorio = procedimentoVO.getId().getEprPciSeq().equals(pciAmbulatorio);
			boolean isOrigemPacienteInternacao = DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia());

			if (isLancaItemAmbulatorio && isProcedimentoAmbulatorio && isOrigemPacienteInternacao) {

				Short valorConvenioSusPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO).getVlrNumerico().shortValue();
				Byte valorSusPlanoAmbulatorio = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO).getVlrNumerico().byteValue();

				// CURSOR CUR_SSM
				List<CursoPopulaProcedimentoHospitalarInternoVO> listaSsm2 = this.getFaturamentoFacade().obterCursorSSM(procedimentoVO.getId().getEprPciSeq(),
						new Date(), valorConvenioSusPadrao, valorSusPlanoAmbulatorio, valorpTipoGrupoContaSus);

				if (listaSsm2.isEmpty()) {
					throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.MSG_ERRO_PROCED_HOSP_DATA_COMPET);
				}

			} else {
				// Metodo chamado para procedimentos sem valores : exemplo 527
				this.popularProcedimentoHospitalarInternoParte2(procedimentoVO, cirurgia, valorDtComp, valorpTipoGrupoContaSus, null);
			}
		}
		
		// Chamada para PARTE 2 passando a listaSsm1 (EVITAR VIOLAÇÕES DE PMD: NPATH COMPLEXITY)
		this.popularProcedimentoHospitalarInternoParte2(procedimentoVO, cirurgia, valorDtComp, valorpTipoGrupoContaSus, listaSsm1);

	}

	/**
	 * 
	 * CONTINUAÇÃO DA PROCEDURE MBCP_POPULA_PHI PARA EVITAR VIOLAÇÕES DE PMD (NPATH COMPLEXITY)
	 * <p>
	 * PARTE 2
	 * <p>
	 * 
	 * @param procedimentoVO
	 * @param cirurgia
	 * @param valorDtComp
	 * @param valorpTipoGrupoContaSus
	 * @param listaSsm1
	 * @throws BaseException
	 */
	private void popularProcedimentoHospitalarInternoParte2(CirurgiaTelaProcedimentoVO procedimentoVO, MbcCirurgias cirurgia, final Date valorDtComp,
			final Short valorpTipoGrupoContaSus, final List<CursoPopulaProcedimentoHospitalarInternoVO> listaSsm) throws BaseException {

		//Procedimentos com valor
		if(listaSsm != null && listaSsm.size() > 0){
			CursoPopulaProcedimentoHospitalarInternoVO itemSmm = listaSsm.get(0);
			this.populaCamposCGNBT(procedimentoVO,  itemSmm.getCodTabela().toString(), 
					this.obterValorTotal(cirurgia, itemSmm), itemSmm.getQuantDiasFaturamento());	
		}else {
		//Procedimentos sem valor
			final Short procedimentoSemValorPCI3 = 0;
			final BigDecimal cgnbtEprPciSeq2 = BigDecimal.ZERO;
			this.populaCamposCGNBT(procedimentoVO,  null,	cgnbtEprPciSeq2, procedimentoSemValorPCI3);
		}

		// Chamada para PARTE 3 (EVITAR VIOLAÇÕES DE PMD: NPATH COMPLEXITY)
		this.popularProcedimentoHospitalarInternoParte3(procedimentoVO, cirurgia, valorDtComp, valorpTipoGrupoContaSus, listaSsm);

	}

	/**
	 * 
	 * CONTINUAÇÃO DA PROCEDURE MBCP_POPULA_PHI PARA EVITAR VIOLAÇÕES DE PMD (NPATH COMPLEXITY)
	 * <p>
	 * PARTE 3
	 * <p>
	 * 
	 * @param procedimentoVO
	 * @param cirurgia
	 * @param valorDtComp
	 * @param valorpTipoGrupoContaSus
	 * @param listaSsm1
	 * @throws BaseException
	 */
	private void popularProcedimentoHospitalarInternoParte3(CirurgiaTelaProcedimentoVO procedimentoVO, MbcCirurgias cirurgia, final Date valorDtComp,
			final Short valorpTipoGrupoContaSus, List<CursoPopulaProcedimentoHospitalarInternoVO> listaSsm) throws BaseException {

		Integer valorPhiSeq = this.getFaturamentoFacade().obterProcedimentoCirurgicoPopularProcedimentoHospitalarInterno(null, null, procedimentoVO.getId().getEprPciSeq(),
				valorDtComp, cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(), cirurgia.getConvenioSaudePlano().getId().getSeq(), valorpTipoGrupoContaSus);

		//Adicionado para procedimentos sem valores
		if(listaSsm == null){
			listaSsm =  new ArrayList<CursoPopulaProcedimentoHospitalarInternoVO>();
		}
				
		if (listaSsm.isEmpty() && procedimentoVO.getPhiSeq() == null) {
			procedimentoVO.setPhiSeq(valorPhiSeq);
			// Chamada para PROCEDURE MBCP_POPULA_CID
			this.getPopulaCidRN().popularCid(procedimentoVO, cirurgia.getSeq(), valorPhiSeq, cirurgia.getOrigemPacienteCirurgia());
			
		} else {
			if (procedimentoVO.getPhiSeq() == null) {
				// Popula campos CGNBT				
				populaCamposCGNBT(procedimentoVO, 
						getResourceBundleValue("ESCOLHER"), BigDecimal.ZERO, (short) 0);
			} else {
				// CURSOR CUR_PHI
				List<CursoPopulaProcedimentoHospitalarInternoVO> listaPhi = this
						.getFaturamentoFacade().obterCursorPHI(
								procedimentoVO.getId().getEprPciSeq(), valorDtComp, procedimentoVO.getPhiSeq(), valorpTipoGrupoContaSus);

				if (listaPhi.isEmpty()) {
					// Reseta campos CGNBT
					this.resetCamposCGNBT(procedimentoVO);
				} else {
					CursoPopulaProcedimentoHospitalarInternoVO itemPhi = listaPhi.get(0);
				
					populaCamposCGNBT(procedimentoVO, 
							itemPhi.getCodTabela().toString(),
							this.obterValorTotal(cirurgia, itemPhi),
							itemPhi.getQuantDiasFaturamento());

					valorPhiSeq = procedimentoVO.getPhiSeq();
					// Chamada para PROCEDURE MBCP_POPULA_CID
					//this.getPopulaCidRN().popularCid(procedimentoVO, cirurgia.getSeq(), valorPhiSeq, cirurgia.getOrigemPacienteCirurgia());
				}
			}
		}
	}
	
	private void populaCamposCGNBT(CirurgiaTelaProcedimentoVO procedimentoVO, String cgnbtEprPciseq, BigDecimal cgnbtEprPciSeq2, Short cgnbtEprPciseq3) {
		procedimentoVO.setCgnbtEprPciseq(cgnbtEprPciseq);
		procedimentoVO.setCgnbtEprPciSeq2(cgnbtEprPciSeq2);
		procedimentoVO.setCgnbtEprPciseq3(cgnbtEprPciseq3);		
	}

	private void resetCamposCGNBT(CirurgiaTelaProcedimentoVO procedimentoVO) {	
		populaCamposCGNBT(procedimentoVO, null, null, null);
	}

	private BigDecimal obterValorTotal(MbcCirurgias cirurgia, CursoPopulaProcedimentoHospitalarInternoVO itemPhi) {

		return getValorCadastralProcedimentoSusRN()
				.getValorCadastralProcedimentoSus(
						cirurgia.getOrigemPacienteCirurgia(),
						itemPhi.getVlrServHospitalar(),
						itemPhi.getVlrServProfissional(), itemPhi.getVlrSadt(),
						itemPhi.getVlrProcedimento(),
						itemPhi.getVlrAnestesista());

	}
	
	/**
	 * ORADB FUNCTION MBCC_PCI_AMBULATORIO
	 * 
	 * @param pciSeq
	 * @return
	 */
	public Integer obterProcedimentoCirurgicoAmbulatorio(Integer pciSeq) {
		MbcProcedimentoCirurgicos procedimentoCirurgico = this.getMbcProcedimentoCirurgicoDAO().obterProcedimentoLancaAmbulatorio(pciSeq);
		return procedimentoCirurgico != null ? procedimentoCirurgico.getSeq() : null;
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.iInternacaoFacade;
	}

	protected PopulaCidRN getPopulaCidRN() {
		return populaCidRN;
	}

	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}
	
	protected DataCompetenciaRegistroCirurgiaRealizadoRN getDataCompetenciaRegistroCirurgiaRealizadoRN() {
		return dataCompetenciaRegistroCirurgiaRealizadoRN;
	}
	
	protected ValorCadastralProcedimentoSusRN  getValorCadastralProcedimentoSusRN(){
		return valorCadastralProcedimentoSusRN;
	}

}
