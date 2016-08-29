package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.RegistroCirurgiaRealizadaON.RegistroCirurgiaRealizadaONExceptionCode;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * ORADB PROCEDURE MBCP_CODIGO_SSM
 * 
 * @author aghu
 * 
 */
@Stateless
public class PopulaCodigoSsmRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PopulaCodigoSsmRN.class);

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	


	@EJB
	private IInternacaoFacade iInternacaoFacade;

	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private PopulaProcedimentoHospitalarInternoRN populaProcedimentoHospitalarInternoRN;

	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2482078151249549764L;

	/**
	 * ORADB PROCEDURE MBCP_CODIGO_SSM
	 * 
	 * @param procedimentoVO
	 * @param cirurgia
	 * @throws BaseException
	 */
	public void popularCodigoSsm(CirurgiaTelaProcedimentoVO procedimentoVO, MbcCirurgias cirurgia) throws BaseException {

		DominioOrigemPacienteCirurgia v_origem_valida = null;

		boolean isLancaItemAmbulatorio = this.getInternacaoFacade().verificarCaracteristicaEspecialidade(cirurgia.getEspecialidade().getSeq(),
				DominioCaracEspecialidade.LANCA_ITEM_AMBULATORIO);

		// Chamada para PROCEDURE MBCC_PCI_AMBULATORIO
		Integer pciAmbulatorio = this.getPopulaProcedimentoHospitalarInternoRN().obterProcedimentoCirurgicoAmbulatorio(procedimentoVO.getId().getEprPciSeq());
		boolean isProcedimentoAmbulatorio = procedimentoVO.getId().getEprPciSeq().equals(pciAmbulatorio);
		boolean isOrigemPacienteInternacao = DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia());

		if (isLancaItemAmbulatorio && isProcedimentoAmbulatorio && isOrigemPacienteInternacao) {
			v_origem_valida = DominioOrigemPacienteCirurgia.A;
		} else {
			v_origem_valida = cirurgia.getOrigemPacienteCirurgia();
		}

		// Parte extraída da consulta no CURSOR CUR_CPE
		DominioModuloCompetencia modulo = null;
		if (DominioOrigemPacienteCirurgia.I.equals(v_origem_valida)) {
			modulo = DominioModuloCompetencia.INT;
		} else if (DominioOrigemPacienteCirurgia.A.equals(v_origem_valida)) {
			modulo = DominioModuloCompetencia.AMB;
		}

		Date valorDtComp = null;

		// CURSOR CUR_CPE
		List<FatCompetencia> competencia = this.getFaturamentoFacade().pesquisarCompetenciaProcedimentoHospitalarInternoPorModulo(modulo);

		if (!competencia.isEmpty()) {

			Calendar calendar = Calendar.getInstance();

			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, competencia.get(0).getId().getMes());
			calendar.set(Calendar.YEAR, competencia.get(0).getId().getAno());

			calendar.set(Calendar.HOUR_OF_DAY, 00);
			calendar.set(Calendar.MINUTE, 00);
			calendar.set(Calendar.SECOND, 00);
			calendar.set(Calendar.MILLISECOND, 00);

			valorDtComp = calendar.getTime();

		} else {
			throw new ApplicationBusinessException(RegistroCirurgiaRealizadaONExceptionCode.FAT_00564);
		}

		// Rotina para buscar valor da tabela de parâmetros que define o procedimento como parto ou cesarea
		AghParametros pTipoGrupoContaSus = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		final Short valorpTipoGrupoContaSus = pTipoGrupoContaSus.getVlrNumerico().shortValue();

		// CURSOR CUR_SSM
		List<FatVlrItemProcedHospComps> listaSsm1 = this.getFaturamentoFacade().pesquisarGruposPopularProcedimentoHospitalarInterno(procedimentoVO.getId().getEprPciSeq(),
				valorDtComp, cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(), cirurgia.getConvenioSaudePlano().getId().getSeq(), valorpTipoGrupoContaSus);

		if (listaSsm1.isEmpty()) {

			procedimentoVO.setCgnbtEprPciseq(null);
			procedimentoVO.setCgnbtEprPciSeq2(null);
			procedimentoVO.setCgnbtEprPciseq3(null);

			if (isLancaItemAmbulatorio && isProcedimentoAmbulatorio && isOrigemPacienteInternacao) {

				Short valorConvenioSusPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO).getVlrNumerico().shortValue();
				Byte valorSusPlanoAmbulatorio = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO).getVlrNumerico().byteValue();

				// CURSOR CUR_SSM
				List<FatVlrItemProcedHospComps> listaSsm2 = this.getFaturamentoFacade().pesquisarGruposPopularProcedimentoHospitalarInterno(procedimentoVO.getId().getEprPciSeq(),
						new Date(), valorConvenioSusPadrao, valorSusPlanoAmbulatorio, valorpTipoGrupoContaSus);

				if (listaSsm2.isEmpty()) {
					procedimentoVO.setCgnbtEprPciseq(null);
					procedimentoVO.setCgnbtEprPciSeq2(null);
					procedimentoVO.setCgnbtEprPciseq3(null);
					return;
				}
				// Chamada para PARTE 2 passando a listaSsm2 (EVITAR VIOLAÇÕES DE PMD: NPATH COMPLEXITY)
				this.popularCodigoSsmParte2(procedimentoVO, cirurgia, valorDtComp, valorpTipoGrupoContaSus, listaSsm2);

			} else {
				return;
			}

		} else {
			// Chamada para PARTE 2 passando a listaSsm1 (EVITAR VIOLAÇÕES DE PMD: NPATH COMPLEXITY)
			this.popularCodigoSsmParte2(procedimentoVO, cirurgia, valorDtComp, valorpTipoGrupoContaSus, listaSsm1);
		}
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
	private void popularCodigoSsmParte2(CirurgiaTelaProcedimentoVO procedimentoVO, MbcCirurgias cirurgia, final Date valorDtComp, final Short valorpTipoGrupoContaSus,
			final List<FatVlrItemProcedHospComps> listaSsm) throws BaseException {

		FatVlrItemProcedHospComps itemSmm = listaSsm.get(0);

		procedimentoVO.setCgnbtEprPciseq(itemSmm.getFatItensProcedHospitalar().getCodTabela().toString());
		procedimentoVO.setCgnbtEprPciseq3(itemSmm.getFatItensProcedHospitalar().getQuantDiasFaturamento());

		BigDecimal valorTotal = BigDecimal.ZERO;

		if (DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia())) {

			// Faz o NVL do cursor aqui
			BigDecimal vlrServHospitalar = itemSmm.getVlrServHospitalar() != null ? itemSmm.getVlrServHospitalar() : BigDecimal.ZERO;
			BigDecimal vlrServProfissional = itemSmm.getVlrServProfissional() != null ? itemSmm.getVlrServProfissional() : BigDecimal.ZERO;
			BigDecimal vlrSadt = itemSmm.getVlrSadt() != null ? itemSmm.getVlrSadt() : BigDecimal.ZERO;
			BigDecimal vlrAnestesista = itemSmm.getVlrAnestesista() != null ? itemSmm.getVlrAnestesista() : BigDecimal.ZERO;

			valorTotal = valorTotal.add(vlrServHospitalar).add(vlrServProfissional).add(vlrSadt).add(vlrAnestesista);

		} else {
			valorTotal = itemSmm.getVlrProcedimento() != null ? itemSmm.getVlrProcedimento() : BigDecimal.ZERO;
		}

		procedimentoVO.setCgnbtEprPciSeq2(valorTotal);

		// Chamada para PARTE 3 (EVITAR VIOLAÇÕES DE PMD: NPATH COMPLEXITY)
		this.popularCodigoSsmParte3(procedimentoVO, cirurgia, valorDtComp, valorpTipoGrupoContaSus, listaSsm);

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
	private void popularCodigoSsmParte3(CirurgiaTelaProcedimentoVO procedimentoVO, MbcCirurgias cirurgia, final Date valorDtComp, final Short valorpTipoGrupoContaSus,
			final List<FatVlrItemProcedHospComps> listaSsm) throws BaseException {
		// Só executa caso exista mais de um código SUS para o procedimento.
		if (!listaSsm.isEmpty() && listaSsm.size() > 1) {

			if (procedimentoVO.getPhiSeq() == null) {
				// Popula campos CGNBT
				procedimentoVO.setCgnbtEprPciseq("*ESCOLHER*");
				procedimentoVO.setCgnbtEprPciSeq2(BigDecimal.ZERO);
				procedimentoVO.setCgnbtEprPciseq3((short) 0);
			} else {

				// CURSOR CUR_PHI
				List<FatVlrItemProcedHospComps> listaPhi = this.getFaturamentoFacade().pesquisarProcedimentosConvenioPopularProcedimentoHospitalarInterno(
						procedimentoVO.getId().getEprPciSeq(), valorDtComp, cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(),
						cirurgia.getConvenioSaudePlano().getId().getSeq(), procedimentoVO.getPhiSeq(), valorpTipoGrupoContaSus);

				if (listaPhi.isEmpty()) {
					// Reseta campos CGNBT
					procedimentoVO.setCgnbtEprPciseq(null);
					procedimentoVO.setCgnbtEprPciSeq2(null);
					procedimentoVO.setCgnbtEprPciseq3(null);
				} else {

					FatVlrItemProcedHospComps itemPhi = listaPhi.get(0);

					procedimentoVO.setCgnbtEprPciseq(itemPhi.getFatItensProcedHospitalar().getCodTabela().toString());
					procedimentoVO.setCgnbtEprPciseq3(itemPhi.getFatItensProcedHospitalar().getQuantDiasFaturamento());

					BigDecimal valorTotal = BigDecimal.ZERO;

					if (DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia())) {

						// Faz o NVL do cursor aqui
						BigDecimal vlrServHospitalar = itemPhi.getVlrServHospitalar() != null ? itemPhi.getVlrServHospitalar() : BigDecimal.ZERO;
						BigDecimal vlrServProfissional = itemPhi.getVlrServProfissional() != null ? itemPhi.getVlrServProfissional() : BigDecimal.ZERO;
						BigDecimal vlrSadt = itemPhi.getVlrSadt() != null ? itemPhi.getVlrSadt() : BigDecimal.ZERO;
						BigDecimal vlrAnestesista = itemPhi.getVlrAnestesista() != null ? itemPhi.getVlrAnestesista() : BigDecimal.ZERO;

						valorTotal = valorTotal.add(vlrServHospitalar).add(vlrServProfissional).add(vlrSadt).add(vlrAnestesista);

					} else {
						valorTotal = itemPhi.getVlrProcedimento() != null ? itemPhi.getVlrProcedimento() : BigDecimal.ZERO;
					}

					procedimentoVO.setCgnbtEprPciSeq2(valorTotal);
				}
			}
		}

	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	public IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}

	public IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	public IInternacaoFacade getInternacaoFacade() {
		return this.iInternacaoFacade;
	}

	public PopulaProcedimentoHospitalarInternoRN getPopulaProcedimentoHospitalarInternoRN() {
		return populaProcedimentoHospitalarInternoRN;
	}

}
