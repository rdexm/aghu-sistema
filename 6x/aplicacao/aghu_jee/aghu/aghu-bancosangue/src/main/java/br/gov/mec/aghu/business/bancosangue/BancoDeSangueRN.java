package br.gov.mec.aghu.business.bancosangue;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsItemSolicitacaoHemoterapicaJustificativaDAO;
import br.gov.mec.aghu.bancosangue.vo.AtualizaCartaoPontoVO;
import br.gov.mec.aghu.bancosangue.vo.BuscaJustificativaLaudoCsaVO;
import br.gov.mec.aghu.bancosangue.vo.BuscaJustificativaLaudoPheVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Implementação da package 
 * ORADB ABSK_ABS_RN.
 */

@Stateless
public class BancoDeSangueRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(BancoDeSangueRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AbsItemSolicitacaoHemoterapicaJustificativaDAO absItemSolicitacaoHemoterapicaJustificativaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5731956008498813777L;

	public enum BancoSangueRNExceptionCode implements
		BusinessExceptionCode {
		ABS_00166;
	}
	
	/**
	 * ORADB Procedure ABSK_ABS_RN.RN_ABSP_ATU_SERVIDOR.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public AtualizaCartaoPontoVO atualizaCartaoPontoServidor()
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AtualizaCartaoPontoVO cartaoPontoVO = new AtualizaCartaoPontoVO();

		if (servidorLogado.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(
					BancoSangueRNExceptionCode.ABS_00166);
		} else {
			cartaoPontoVO.setMatricula(servidorLogado.getId().getMatricula());
			cartaoPontoVO.setVinCodigo(servidorLogado.getId().getVinCodigo());
		}

		return cartaoPontoVO;
	}

	public String buscaJustificativaLaudoCsa(Integer atdSeq, Integer phiSeq) {
		List<BuscaJustificativaLaudoCsaVO> listaVO = getAbsItemSolicitacaoHemoterapicaJustificativaDAO()
				.buscaJustificativaLaudoCsa(atdSeq, phiSeq);

		StringBuilder justificativa = new StringBuilder();
		if (listaVO != null && !listaVO.isEmpty()) {
			Integer seqSolicitacaoHemoterapica = null;
			for (BuscaJustificativaLaudoCsaVO vo : listaVO) {
				if (seqSolicitacaoHemoterapica == null) {
					seqSolicitacaoHemoterapica = vo.getSheSeq();
				} else if (seqSolicitacaoHemoterapica != vo.getSheSeq()) {
					break;
				}

				if (justificativa.length() != 0) {
					justificativa.append("; ");
				}

				justificativa.append(vo.getJustificativa());
			}
		}

		return justificativa.toString();
	}

	public String buscaJustificativaLaudoPhe(Integer atdSeq, Integer phiSeq) {
		List<BuscaJustificativaLaudoPheVO> listaVO = getAbsItemSolicitacaoHemoterapicaJustificativaDAO()
				.buscaJustificativaLaudoPhe(atdSeq, phiSeq);

		StringBuilder justificativa = new StringBuilder();
		if (listaVO != null && !listaVO.isEmpty()) {
			Integer seqSolicitacaoHemoterapica = null;
			for (BuscaJustificativaLaudoPheVO vo : listaVO) {
				if (seqSolicitacaoHemoterapica == null) {
					seqSolicitacaoHemoterapica = vo.getSheSeq();
				} else if (seqSolicitacaoHemoterapica != vo.getSheSeq()) {
					break;
				}

				if (justificativa.length() != 0) {
					justificativa.append("; ");
				}

				justificativa.append(vo.getJustificativa());
			}
		}

		return justificativa.toString();
	}
	
	protected AbsItemSolicitacaoHemoterapicaJustificativaDAO getAbsItemSolicitacaoHemoterapicaJustificativaDAO() {
		return absItemSolicitacaoHemoterapicaJustificativaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
