package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcDiagnosticoDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtCidDescDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * PROCEDURE MBCP_POPULA_CID
 * 
 * @author ihaas
 * 
 */
@Stateless
public class PopulaCidRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PopulaCidRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDiagnosticoDescricaoDAO mbcDiagnosticoDescricaoDAO;

	@Inject
	private PdtCidDescDAO pdtCidDescDAO;


	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3078078076266680391L;

	/**
	 * Popula CID
	 * 
	 * @param procedimentoVO
	 * @param crgSeq
	 * @param phiSeq
	 * @param origem
	 * @throws BaseException
	 */
	public void popularCid(CirurgiaTelaProcedimentoVO procedimentoVO, Integer crgSeq, Integer phiSeq, DominioOrigemPacienteCirurgia origem) throws BaseException {

		DominioTipoPlano validade = DominioTipoPlano.I;
		if (origem.equals(DominioOrigemPacienteCirurgia.A)) {
			validade = DominioTipoPlano.A;
		}
		FatProcedHospIntCid procedimentoCid = getFaturamentoFacade().pesquisarFatProcedHospIntCidPorPhiSeqValidade(phiSeq, validade);

		if (procedimentoCid != null) {
			Integer cidSeqDdc = getMbcDiagnosticoDescricaoDAO().buscarCidSeqMbcDiagnosticosDescricoes(crgSeq, phiSeq, validade);

			if (cidSeqDdc != null) {
				AghCid aghCidDdc = getAghuFacade().obterAghCidPorChavePrimaria(cidSeqDdc);
				procedimentoVO.setCid(aghCidDdc);

			} else {
				Integer cidSeqPid = getPdtCidDescDAO().buscarCidSeqPdtCidDesc(crgSeq, phiSeq, validade);

				if (cidSeqPid != null) {
					AghCid aghCidPid = getAghuFacade().obterAghCidPorChavePrimaria(cidSeqPid);
					procedimentoVO.setCid(aghCidPid);
				}
			}
		}
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected MbcDiagnosticoDescricaoDAO getMbcDiagnosticoDescricaoDAO() {
		return mbcDiagnosticoDescricaoDAO;
	}

	protected PdtCidDescDAO getPdtCidDescDAO() {
		return pdtCidDescDAO;
	}

}
