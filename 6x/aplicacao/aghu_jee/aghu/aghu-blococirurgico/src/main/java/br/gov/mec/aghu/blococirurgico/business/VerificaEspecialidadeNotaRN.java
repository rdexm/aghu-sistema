package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ORADB PROCEDURE RN_CRGP_VER_ESP_NOTA
 * 
 * @author aghu
 * 
 */
@Stateless
public class VerificaEspecialidadeNotaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(VerificaEspecialidadeNotaRN.class);

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;


	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5179115164213572153L;

	public enum VerificaEspecialidadeNotaRNExceptionCode implements BusinessExceptionCode {
		MBC_00522, MBC_00523, MBC_00587;
	}

	/**
	 * ORADB PROCEDURE RN_CRGP_VER_ESP_NOTA
	 * 
	 * <p>
	 * Garante que especialidade do procedimento principal e profissional principal é a mesma da cirurgia
	 * <p>
	 * 
	 * @param digitaNotaSalaNovo
	 * @param crgSeqNovo
	 * @param situacaoNovo
	 * @param espSeqNovo
	 * @throws BaseException
	 */
	public void verificarEspecialidadeNota(final Boolean digitaNotaSalaNovo, final Integer crgSeqNovo, final DominioSituacaoCirurgia situacaoNovo, final Short espSeqNovo)
	throws BaseException {

		Short valorEspSeq = null;
		Short valorEspMaeCirg = null;

		// Busca a ESPECIALIDADE MÃE do procedimento principal
		AghEspecialidades especialidadeMae = this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(espSeqNovo);
		valorEspMaeCirg = especialidadeMae.getEspecialidade() != null ? especialidadeMae.getEspecialidade().getSeq() : especialidadeMae.getSeq();

		if (Boolean.TRUE.equals(digitaNotaSalaNovo)) {

			if (!DominioSituacaoCirurgia.CANC.equals(situacaoNovo)) {

				// Busca a especialidade do procedimento principal
				List<MbcProcEspPorCirurgias> listaProcEspPorCirurgias = this.getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosEspecialidadePrincipalCirurgia(crgSeqNovo);

				if (listaProcEspPorCirurgias.isEmpty()) {
					// Cirurgia com este código não encontrada
					throw new ApplicationBusinessException(VerificaEspecialidadeNotaRNExceptionCode.MBC_00522);
				} else {

					// Busca a ESPECIALIDADE MÃE do procedimento principal. Atenção na migração de FET_PPC.EPR_ESP_SEQ
					AghEspecialidades especialidadePrincipal = this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(listaProcEspPorCirurgias.get(0).getId().getEprEspSeq());
					valorEspSeq = especialidadePrincipal.getEspecialidade() != null ? especialidadePrincipal.getEspecialidade().getSeq() : especialidadePrincipal.getSeq();

					if (!CoreUtil.igual(valorEspSeq, valorEspMaeCirg)) {
						// Cirurgia com este código não encontrada
						throw new ApplicationBusinessException(VerificaEspecialidadeNotaRNExceptionCode.MBC_00523);
					}
				}

				// CURSOR CUR_ESP
				List<AghProfEspecialidades> listaEspecialidadeMae = this.getAghuFacade().pesquisarEspecialidadeMesmaEspecialidadeCirurgia(crgSeqNovo, valorEspMaeCirg);

				if (listaEspecialidadeMae.isEmpty()) {
					// Especialidade do profissional difere da especialidade da cirurgia.
					throw new ApplicationBusinessException(VerificaEspecialidadeNotaRNExceptionCode.MBC_00587);
				}

			}

		}

	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	public IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	public MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

}
