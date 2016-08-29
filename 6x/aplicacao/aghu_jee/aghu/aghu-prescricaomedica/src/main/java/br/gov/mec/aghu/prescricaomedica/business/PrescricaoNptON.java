package br.gov.mec.aghu.prescricaomedica.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmJustificativaNpt;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricao;
import br.gov.mec.aghu.prescricaomedica.dao.AfaFormulaNptPadraoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmParamCalculoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaDadosAtendimentoUrgenciaVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaDadosHospitalDiaVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaDadosInternacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmComposicaoPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PrescricaoNptON extends BaseBusiness {

	private static final long serialVersionUID = 7786363886652454754L;

	private static final Log LOG = LogFactory.getLog(PrescricaoNptON.class);
	
	private enum PrescricaoNptONExceptionCode implements BusinessExceptionCode {
		MPM_03597, MPM_03714, MPM_03602, MPM_03599,
	}

	@Inject
	private AfaFormulaNptPadraoDAO formulaNptPadraoDAO;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private PrescricaoMedicaON prescricaoMedicaON;

	@Inject
	private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;

	@Inject
	private MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO;

	@Inject
	private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;

	@EJB
	private PrescricaoNptRN prescricaoNptRN;

	@Inject
	private MpmParamCalculoPrescricaoDAO mpmParamCalculoPrescricaoDAO;
	
	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;

	/**
	 * #990 - PC6
	 * 
	 * @param atdSeq
	 * @return
	 */
	public AfaFormulaNptPadrao obterFormulaPediatricao(Integer atdSeq) {
		AfaFormulaNptPadrao formula = new AfaFormulaNptPadrao();
		AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorSeq(atdSeq);
		if (atendimento != null && atendimento.getIndPacPediatrico() == Boolean.TRUE) {
			formula = formulaNptPadraoDAO.obterFormulaPediatrica(atdSeq);
		}
		return formula;
	}

	/**
	 * #990 - PC7
	 * 
	 * @ORADB MPMP_MONTA_TEXTO_NPT
	 * @param atdSeq
	 * @return
	 */
	public String montarMensagemPrescricao(Integer atdSeq) {
		AfaFormulaNptPadrao formula = formulaNptPadraoDAO.obterFormulaPediatrica(atdSeq);
		if (formula == null) {
			return "OLIGOELEMENTOS,MULTIVITAMINAS E ELETRÓLITOS USE A PRESCRIÇÃO DE MEDICAMENTOS";
		} else {
			return "PARA PRESCREVER MULTIVITAMINAS USE A PRESCRIÇÃO DE MEDICAMENTOS";
		}

	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	public Boolean isFormulaPadraoOuLivre(Short seq) {
		if (seq == null) {
			return false;
		}
		String indPadrao = formulaNptPadraoDAO.verificarFormulaLivreOuPadrao(seq);
		return DominioSimNao.valueOf(indPadrao).isSim();
	}

	public void persistirPrescricaoNpt(Short fnpSeq, String descricaoFormula, MpmJustificativaNpt justificativa, PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoNptVO prescricaoNptVO, String nomeMicrocomputador, Boolean desconsiderarItensNulos) throws ApplicationBusinessException {
		// item novo
		if (prescricaoNptVO.getSeq() == null) {
			prepararPrescricaoNova(fnpSeq, descricaoFormula, justificativa, prescricaoMedicaVO, prescricaoNptVO);
			this.prescricaoNptRN.persistir(prescricaoNptVO, nomeMicrocomputador, desconsiderarItensNulos, false);
		} else { // edicao
			if (DominioIndPendenteItemPrescricao.P.equals(prescricaoNptVO.getIndPendente())) {
				montarVOPadrao(fnpSeq, descricaoFormula, justificativa, prescricaoMedicaVO, prescricaoNptVO);
				verificarDuracaoTratamento(prescricaoMedicaVO.getId().getAtdSeq(), prescricaoNptVO);
				this.prescricaoNptRN.persistir(prescricaoNptVO, nomeMicrocomputador, desconsiderarItensNulos, true);
			} else if (DominioIndPendenteItemPrescricao.N.equals(prescricaoNptVO.getIndPendente())) {
				try {
					// 1 passo
					MpmPrescricaoNptVO novoItem = (MpmPrescricaoNptVO) BeanUtils.cloneBean(prescricaoNptVO);
					novoItem.setPnpSeq(prescricaoNptVO.getSeq());
					novoItem.setPnpAtdSeq(prescricaoNptVO.getAtdSeq());
					prepararPrescricaoNova(fnpSeq, descricaoFormula, justificativa, prescricaoMedicaVO, novoItem);
					this.prescricaoNptRN.persistir(true, novoItem, nomeMicrocomputador, desconsiderarItensNulos, false);

					// 2 passo
					prescricaoNptVO.setIndPendente(DominioIndPendenteItemPrescricao.A);
					prescricaoNptVO.setPrescricaoMedica(prescricaoMedicaVO.getPrescricaoMedica());
					prescricaoNptVO.setAlteradoEm(new Date());
					if (prescricaoMedicaON.isPrescricaoVigente(prescricaoMedicaVO.getPrescricaoMedica())) {
						prescricaoNptVO.setDthrFim(prescricaoMedicaVO.getPrescricaoMedica().getDthrMovimento());
					} else {
						prescricaoNptVO.setDthrFim(prescricaoMedicaVO.getPrescricaoMedica().getDthrInicio());
					}
					this.prescricaoNptRN.persistir(prescricaoNptVO, nomeMicrocomputador, desconsiderarItensNulos, true);
				} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
					new ApplicationBusinessException(PrescricaoNptRN.PrescricaoNptExceptionCode.ERRO_LEITURA_VO);
				}

			}
		}

	}

	/**
	 * PROCEDURE EVT_PRE_INSERT
	 * @param fnpSeq
	 * @param descricaoFormula
	 * @param justificativa
	 * @param prescricaoMedicaVO
	 * @param prescricaoNptVO
	 * @throws ApplicationBusinessException
	 */
	private void prepararPrescricaoNova(Short fnpSeq, String descricaoFormula, MpmJustificativaNpt justificativa, PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoNptVO prescricaoNptVO) throws ApplicationBusinessException {

		montarVOPadrao(fnpSeq, descricaoFormula, justificativa, prescricaoMedicaVO, prescricaoNptVO);
		verificarDuracaoTratamento(prescricaoMedicaVO.getId().getAtdSeq(), prescricaoNptVO);

		prescricaoNptVO.setIndPendente(DominioIndPendenteItemPrescricao.P);
		if (prescricaoMedicaON.isPrescricaoVigente(prescricaoMedicaVO.getPrescricaoMedica())) {
			prescricaoNptVO.setDthrInicio(prescricaoMedicaVO.getPrescricaoMedica().getDthrMovimento());
		} else {
			prescricaoNptVO.setDthrInicio(prescricaoMedicaVO.getPrescricaoMedica().getDthrInicio());
		}
		prescricaoNptVO.setDthrFim(prescricaoMedicaVO.getPrescricaoMedica().getDthrFim());
	}

	/**
	 * 
	 * @param fnpSeq
	 * @param descricaoFormula
	 * @param justificativa
	 * @param prescricaoMedicaVO
	 * @param prescricaoNptVO
	 * @throws ApplicationBusinessException
	 */
	private void montarVOPadrao(Short fnpSeq, String descricaoFormula, MpmJustificativaNpt justificativa, PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoNptVO prescricaoNptVO) throws ApplicationBusinessException {
		// Chamada para PROCEDURE MPMP_MOVE_PARAM_CALCULO
		moverParametroCalculo(prescricaoMedicaVO.getId().getAtdSeq(), prescricaoNptVO);

		prescricaoNptVO.setPedSeq(parametroFacade.buscarValorShort(AghuParametrosEnum.P_PROCED_NPT));
		prescricaoNptVO.setAtdSeq(prescricaoMedicaVO.getId().getAtdSeq());
		prescricaoNptVO.setPmeAtdSeq(prescricaoMedicaVO.getId().getAtdSeq());
		prescricaoNptVO.setPmeSeq(prescricaoMedicaVO.getId().getSeq());
		prescricaoNptVO.setFnpSeq(fnpSeq);
		prescricaoNptVO.setDescricaoFormula(descricaoFormula);
		prescricaoNptVO.setJnpSeq(justificativa.getSeq());
		prescricaoNptVO.setJustificativa(justificativa.getDescricao());
		prescricaoNptVO.setPrescricaoMedica(prescricaoMedicaVO.getPrescricaoMedica());
	}

	public boolean temComposicoesComponentesNulos(MpmPrescricaoNptVO vo) {
		for (MpmComposicaoPrescricaoNptVO composicao : vo.getComposicoes()) {
			if (composicao.getVelocidadeAdministracao() == null) {
				return true;
			}
			for (MpmItemPrescricaoNptVO item : composicao.getComponentes()) {
				if (item.getQtdePrescrita() == null) {
					return true;
				}
			}
		}
		return false;
	}

	public MpmPrescricaoNptVO buscarDadosPrescricaoNpt(Integer atdSeq, Integer pnpSeq) {
		MpmPrescricaoNptVO prescricaoNptVO = this.mpmPrescricaoNptDAO.buscarDadosPrescricaoNpt(atdSeq, pnpSeq);
		if (prescricaoNptVO != null) {
			prescricaoNptVO.setComposicoes(mpmComposicaoPrescricaoNptDAO.buscarComposicoesNptDescritaPorId(pnpSeq, atdSeq));
			for (MpmComposicaoPrescricaoNptVO item : prescricaoNptVO.getComposicoes()) {
				item.setComponentes(mpmItemPrescricaoNptDAO.buscarComponentesComposicaoNptDescritaPorId(atdSeq, pnpSeq, item.getSeqp()));
			}
		}
		return prescricaoNptVO;
	}

	/**
	 * ORADB PROCEDURE MPMP_VER_DURACAO_TRAT
	 * 
	 * @param atdSeq
	 * @throws ApplicationBusinessException 
	 */
	private void verificarDuracaoTratamento(Integer atdSeq, final MpmPrescricaoNptVO pnp) throws ApplicationBusinessException {

		// CURSOR C_ATD
		AghAtendimentos atd = this.aghuFacade.obterAghAtendimentoPorSeq(atdSeq);
		if (atd == null) {
			throw new ApplicationBusinessException(PrescricaoNptONExceptionCode.MPM_03597);
		}

		/*
		 * Busca endereço do convênio e primeiro e último evento da internação, hospital dia ou atend. urgência que originou o atendimento.
		 */
		Short cpgCphCspCnvCodigo = null;
		Byte cpgCphCspSeq = null;

		if (atd.getInternacao() != null) {
			// MPMK_RN.RN_MPMP_VER_INT( R_ATD.INT_SEQ, V_CSP_CNV_CODIGO, V_CSP_SEQ, V_DTHR_PRIMEIRO_EVENTO, V_DTHR_ULTIMO_EVENTO);
			BuscaDadosInternacaoVO dadosInternacao = this.prescricaoMedicaRN.buscaDadosInternacao(atd.getInternacao().getSeq());
			cpgCphCspCnvCodigo = dadosInternacao.getCodigoConvenioSaude();
			cpgCphCspSeq = dadosInternacao.getSeqConvenioSaudePlano();
		} else if (atd.getAtendimentoUrgencia() != null) {
			// MPMK_RN.RN_MPMP_VER_ATU( R_ATD.ATU_SEQ, V_CSP_CNV_CODIGO, V_CSP_SEQ);
			BuscaDadosAtendimentoUrgenciaVO atendimentoUrgencia = this.prescricaoMedicaRN.buscaDadosAtendimentoUrgencia(atd.getAtendimentoUrgencia().getSeq());
			cpgCphCspCnvCodigo = atendimentoUrgencia.getCodigoConvenioSaude();
			cpgCphCspSeq = atendimentoUrgencia.getCspSeq();
		} else if (atd.getHospitalDia() != null) {
			// MPMK_RN.RN_MPMP_VER_HOD( R_ATD.HOD_SEQ, V_CSP_CNV_CODIGO, V_CSP_SEQ, V_DTHR_PRIMEIRO_EVENTO, V_DTHR_ULTIMO_EVENTO);
			BuscaDadosHospitalDiaVO dadosHospitalDia = this.prescricaoMedicaRN.buscaDadosHospitalDia(atd.getHospitalDia().getSeq());
			cpgCphCspCnvCodigo = dadosHospitalDia.getCodigoConvenioSaude();
			cpgCphCspSeq = dadosHospitalDia.getCspSeq();
		}

		// CURSOR C_PHI
		FatProcedHospInternos phi = this.faturamentoFacade.obterFatProcedHospInternosPorProcedEspecialDiversos(pnp.getPedSeq()); // PNP.PED_SEQ

		// CURSOR C_CONV
		Boolean indInformaTempoTrat = null;
		List<FatConvGrupoItemProced> conv = this.faturamentoFacade.pesquisarConvenioVerificarDuracaoTratamento(cpgCphCspCnvCodigo, cpgCphCspSeq, phi.getSeq());
		if (!conv.isEmpty()) {
			FatConvGrupoItemProced itemConv = conv.get(0);
			indInformaTempoTrat = itemConv.getIndInformaTempoTrat();
		}

		if (pnp.getDuracaoTratSolicitado() == null) { // Campo duracao_trat_solicitado de MpmPrescricaoNptsVO (ID06)
			if (Boolean.TRUE.equals(indInformaTempoTrat)) {
				throw new ApplicationBusinessException(PrescricaoNptONExceptionCode.MPM_03602);
			}
		} else {
			if (Boolean.FALSE.equals(indInformaTempoTrat)) {
				throw new ApplicationBusinessException(PrescricaoNptONExceptionCode.MPM_03599);
			}

		}
	}

	/**
	 * ORADB PROCEDURE MPMP_MOVE_PARAM_CALCULO
	 * 
	 * @param atdSeq
	 * @param pnp
	 * @throws ApplicationBusinessException
	 */
	private void moverParametroCalculo(final Integer atdSeq, final MpmPrescricaoNptVO pnp) throws ApplicationBusinessException {
		if (pnp.getPcaAtdSeq() == null && pnp.getCriadoEm() == null) {
			List<MpmParamCalculoPrescricao> listaPca = this.mpmParamCalculoPrescricaoDAO.pesquisarMoverParametroCalculo(atdSeq);
			if (!listaPca.isEmpty()) {
				MpmParamCalculoPrescricao pca = listaPca.get(0);
				pnp.setPcaAtdSeq(pca.getId().getAtdSeq());
				pnp.setPcaCriadoEm(pca.getId().getCriadoEm());
			} else {
				throw new ApplicationBusinessException(PrescricaoNptONExceptionCode.MPM_03714);
			}
		}
	}

}
