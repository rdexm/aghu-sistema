package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamExtratoLaudoAihDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLaudoAihDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteLaudoAih;
import br.gov.mec.aghu.dominio.DominioIndSituacaoLaudoAih;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamExtratoLaudoAih;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class LaudoAihRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(LaudoAihRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AacConsultasDAO aacConsultasDAO;

	@Inject
	private MamLaudoAihDAO mamLaudoAihDAO;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@Inject
	private MamExtratoLaudoAihDAO mamExtratoLaudoAihDAO;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private LaudoAihON laudoAihON;
	
	@EJB
	private AtendimentoPacientesAgendadosON atendimentoPacientesAgendadosON;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5889292908157270213L;

	public enum LaudoAihRNExceptionCode implements BusinessExceptionCode {
		MAM_00628, MAM_00629, MAM_03126;
	}

	/**
	 * RN1 - ORADB MAMK_EMG_CONCLUIR.MAMP_EMG_CONC_AIH
	 * 
	 * @throws BaseException
	 */
	public void concluirLaudoAih(long rgtSeq, Date dataHoraMovto, RapServidores servidorLogado, Long seq) throws BaseException {
		List<MamLaudoAih> listaLaudos = getMamLaudoAihDAO().obterLaudoPorRgtSeqLaiSeqIndPendente(rgtSeq, seq);

		for (MamLaudoAih laudo : listaLaudos) {
			if (laudo.getIndPendente().equals(DominioIndPendenteLaudoAih.R)) {
				tratarRascunho(laudo.getSeq(), laudo.getMamLaudoAihs() == null ? null : laudo.getMamLaudoAihs().getSeq(), servidorLogado);

			} else if (laudo.getIndPendente().equals(DominioIndPendenteLaudoAih.P)) {
				tratarPendente(laudo.getSeq(), laudo.getTrgSeq(), dataHoraMovto, servidorLogado, null);

			} else if (laudo.getIndPendente().equals(DominioIndPendenteLaudoAih.E)) {
				tratarExclusao(laudo.getSeq(), dataHoraMovto, servidorLogado);
			}
		}
	}

	public void tratarRascunho(Long seq, Long laiSeq, RapServidores servidorLogado) throws ApplicationBusinessException, ApplicationBusinessException {
		// RN2
		verificarLaudoJaValidado(seq);

		MamLaudoAih laudoExclusao = getMamLaudoAihDAO().obterPorChavePrimaria(seq);
		getMamLaudoAihDAO().remover(laudoExclusao);
		getMamLaudoAihDAO().flush();
		if (laiSeq != null) {
			MamLaudoAih laudoAlterado = getMamLaudoAihDAO().obterPorChavePrimaria(seq);
			laudoAlterado.setDthrMvto(null);
			laudoAlterado.setServidorValidaMvto(null);
			// RN3
			preAtualizarLaudoParte1(laudoAlterado, servidorLogado);
		}
	}

	/**
	 * RN2
	 * 
	 * @param seq
	 * @throws ApplicationBusinessException
	 */
	public void verificarLaudoJaValidado(Long seq) throws ApplicationBusinessException {
		MamLaudoAih laudoOriginal = getMamLaudoAihDAO().obterOriginal(seq);
		if (laudoOriginal.getIndPendente().equals(DominioIndPendenteLaudoAih.V)) {
			throw new ApplicationBusinessException(LaudoAihRNExceptionCode.MAM_00629);
		}
	}

	public void tratarPendente(Long seq, Long trgSeq, Date dataHoraMovto, RapServidores servidorLogado, Integer pacCodigo) throws BaseException {

		DominioIndSituacaoLaudoAih situacaoIntegracao;
		MamLaudoAih laudoAih = getMamLaudoAihDAO().obterLaudoPorTrgSeqDominioSituacaoPacCodigo(trgSeq, pacCodigo);

		MamLaudoAih laudoAlterado = getMamLaudoAihDAO().obterPorChavePrimaria(seq);
		laudoAlterado.setIndPendente(DominioIndPendenteLaudoAih.V);
		laudoAlterado.setDthrValida(dataHoraMovto);
		laudoAlterado.setServidorValida(servidorLogado);
		laudoAlterado.setMamLaudoAihs(laudoAih);
		// RN3
		preAtualizarLaudoParte1(laudoAlterado, servidorLogado);

		if (laudoAih != null && laudoAih.getSeq() != null) {
			if ((laudoAih.getIndPendente().equals(DominioIndPendenteLaudoAih.V))
					&& (laudoAih.getIndSituacao().equals(DominioIndSituacaoLaudoAih.G) || laudoAih.getIndSituacao().equals(DominioIndSituacaoLaudoAih.H))) {

				situacaoIntegracao = DominioIndSituacaoLaudoAih.C;
				if (pacCodigo != null) {
					// ON13
					this.getLaudoAihON().desbloquearDocAssinaturaDigital(laudoAih.getSeq(), servidorLogado);
				}
			} else {
				situacaoIntegracao = DominioIndSituacaoLaudoAih.P;
			}
			MamLaudoAih laudoAlteradoAux = getMamLaudoAihDAO().obterPorChavePrimaria(laudoAih.getSeq());
			laudoAlteradoAux.setIndSituacao(situacaoIntegracao);
			laudoAlteradoAux.setDthrMvto(dataHoraMovto);
			laudoAlteradoAux.setDthrValidaMvto(dataHoraMovto);
			laudoAlteradoAux.setServidorValidaMvto(servidorLogado);
			// RN3
			preAtualizarLaudoParte1(laudoAlteradoAux, servidorLogado);
		}
	}

	public void tratarExclusao(Long seq, Date dataHoraMovto, RapServidores servidorLogado) throws ApplicationBusinessException, ApplicationBusinessException {
		MamLaudoAih laudoAlterado = getMamLaudoAihDAO().obterPorChavePrimaria(seq);
		laudoAlterado.setIndPendente(DominioIndPendenteLaudoAih.V);
		laudoAlterado.setIndSituacao(DominioIndSituacaoLaudoAih.C);
		laudoAlterado.setDthrValida(dataHoraMovto);
		laudoAlterado.setServidorValida(servidorLogado);
		// RN3
		preAtualizarLaudoParte1(laudoAlterado, servidorLogado);
	}

	/**
	 * RN3 - ORADB TRIGGER "AGH"."MAMT_LAI_BRU"
	 * 
	 * MÉTODO DIVIDIDO PARA EVITAR NPATH
	 * 
	 * @param laudoAlterado
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	protected void preAtualizarLaudoParte1(MamLaudoAih laudoAlterado, RapServidores servidorLogado) throws ApplicationBusinessException, ApplicationBusinessException {
		MamLaudoAih laudoOriginal = getMamLaudoAihDAO().obterOriginal(laudoAlterado.getSeq());

		if ((laudoAlterado.getServidorValida() != null) && CoreUtil.modificados(laudoAlterado.getServidorValida(), laudoOriginal.getServidorValida())) {

			Boolean podeValidar = this.permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "manterLaudoAih", "assinar");

			if (podeValidar.equals(Boolean.FALSE)) {
				throw new ApplicationBusinessException(LaudoAihRNExceptionCode.MAM_03126);
			}
		}

		if (laudoOriginal.getIndPendente().equals(DominioIndPendenteLaudoAih.V)) {
			if (CoreUtil.modificados(laudoAlterado.getSinaisSintomas(), laudoOriginal.getSinaisSintomas()) || CoreUtil.modificados(laudoAlterado.getCondicoes(), laudoOriginal.getCondicoes())
					|| CoreUtil.modificados(laudoAlterado.getResultadosProvas(), laudoOriginal.getResultadosProvas()) || CoreUtil.modificados(laudoAlterado.getAghCid(), laudoOriginal.getAghCid())) {

				throw new ApplicationBusinessException(LaudoAihRNExceptionCode.MAM_00629);
			}
		}
		// Continuação do método.
		preAtualizarLaudoParte2(laudoAlterado, servidorLogado, laudoOriginal);
	}

	private void preAtualizarLaudoParte2(MamLaudoAih laudoAlterado, RapServidores servidorLogado, MamLaudoAih laudoOriginal) {
		if (laudoAlterado.getCodigoCentral() == null) {
			// RN5
			laudoAlterado.setCodigoCentral(obterCodigoCentral(laudoAlterado.getPaciente().getCodigo(), laudoAlterado.getEspecialidade().getSeq()));
		}

		if ((laudoAlterado.getIndPendente().equals(DominioIndPendenteLaudoAih.V) && laudoAlterado.getDthrValidaMvto() == null) || laudoAlterado.getIndPendente().equals(DominioIndPendenteLaudoAih.E)) {

			if (CoreUtil.modificados(laudoAlterado.getIndSituacao(), laudoOriginal.getIndSituacao())
					|| CoreUtil.modificados(laudoAlterado.getParecerRevisaoMedica(), laudoOriginal.getParecerRevisaoMedica())
					|| CoreUtil.modificados(laudoAlterado.getCodigoCentral(), laudoOriginal.getCodigoCentral())
					|| CoreUtil.modificados(laudoAlterado.getObsRevisaoMedica(), laudoOriginal.getObsRevisaoMedica()) || laudoOriginal.getIndPendente().equals(DominioIndPendenteLaudoAih.P)) {

				// RN6
				gerarExtratoMovimentacoes(laudoAlterado, servidorLogado);
			}
		}

		if (laudoAlterado.getSinaisSintomas().length() < 4) {
			laudoAlterado.setSinaisSintomas("Sinais: " + laudoAlterado.getSinaisSintomas());
		}
		if (laudoAlterado.getCondicoes().length() < 4) {
			laudoAlterado.setCondicoes("Condicoes: " + laudoAlterado.getCondicoes());
		}
		if (laudoAlterado.getResultadosProvas().length() < 4) {
			laudoAlterado.setResultadosProvas("Resultado: " + laudoAlterado.getResultadosProvas());
		}

		if (laudoAlterado.getInternacaoGsh() != null && laudoAlterado.getIndSituacao().equals(DominioIndSituacaoLaudoAih.R) && !laudoOriginal.getIndSituacao().equals(DominioIndSituacaoLaudoAih.R)) {

			Integer seqAtendimento = getAghuFacade().obterAtendimentoPorConNumero(laudoAlterado.getConNumero());
			if (seqAtendimento == null) {
				laudoAlterado.setIndSituacao(DominioIndSituacaoLaudoAih.O);
				// RN6
				gerarExtratoMovimentacoes(laudoAlterado, servidorLogado);
			}
		}

		getMamLaudoAihDAO().atualizar(laudoAlterado);
		getMamLaudoAihDAO().flush();

	}

	/**
	 * RN5 - ORADB FUNCTION RN_LAIC_GET_COD_CENTRAL
	 * 
	 * @param pacCodigo
	 * @param espSeq
	 */
	private Integer obterCodigoCentral(Integer pacCodigo, Short espSeq) {
		List<AacConsultas> listConsultas = getAacConsultasDAO().obterCodCentralPorPacEsp(pacCodigo, espSeq);

		if (listConsultas.size() > 0) {
			return listConsultas.get(0).getCodCentral().intValue();
		} else {
			return null;
		}

	}

	/**
	 * RN6 - ORADB PROCEDURE RN_LAIP_EXTRATO_MOVIMENTACAO
	 * 
	 * @param laudoAlterado
	 */
	private void gerarExtratoMovimentacoes(MamLaudoAih laudoAlterado, RapServidores servidorLogado) {
		MamLaudoAih laudoAih = getMamLaudoAihDAO().obterPorChavePrimaria(laudoAlterado.getSeq());

		String indSituacao = laudoAlterado.getIndSituacao().toString();
		String parecerRevisaoMedica = laudoAlterado.getParecerRevisaoMedica() != null ? laudoAlterado.getParecerRevisaoMedica().toString() : null;
		Integer codigoCentral = laudoAlterado.getCodigoCentral();
		String observacaoRevisaoMedica = laudoAlterado.getObsRevisaoMedica();

		MamExtratoLaudoAih extratoLaudoAih = new MamExtratoLaudoAih();
		// RN7 - ORADB TRIGGER AGH.MAMT_EIH_BRI
		extratoLaudoAih.setCriadoEm(new Date());
		extratoLaudoAih.setRapServidores(servidorLogado);
		// FIM RN7
		extratoLaudoAih.setMamLaudoAih(laudoAih);
		extratoLaudoAih.setIndSituacao(indSituacao);
		extratoLaudoAih.setParecerRevisaoMedica(parecerRevisaoMedica);
		extratoLaudoAih.setCodigoCentral(codigoCentral);
		extratoLaudoAih.setObservacaoRevisaoMedica(observacaoRevisaoMedica);

		getMamExtratoLaudoAihDAO().persistir(extratoLaudoAih);
		getMamExtratoLaudoAihDAO().flush();
	}

	/**
	 * RN8 - TRIGGER "AGH"."MAMT_LAI_BRI"
	 * 
	 * @param laudoAih
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void preInserirLaudo(MamLaudoAih laudoAih, String servidorLogado) throws ApplicationBusinessException, ApplicationBusinessException {
		if (laudoAih.getCodigoCentral() == null) {
			// RN5
			laudoAih.setCodigoCentral(this.obterCodigoCentral(laudoAih.getPaciente().getCodigo(), laudoAih.getEspecialidade().getSeq()));
		}

		if (laudoAih.getIndPendente().equals(DominioIndPendenteLaudoAih.V) && laudoAih.getConNumero() != null) {
			throw new ApplicationBusinessException(LaudoAihRNExceptionCode.MAM_00629);
		}

		// RN9 ORADB PROCEDURE RN_LAIP_VER_CID_ATIV
		if (laudoAih.getAghCid().getSituacao().equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(LaudoAihRNExceptionCode.MAM_00628);
		}
		// FIM RN9

		laudoAih.setDthrCriacao(new Date());

		if (laudoAih.getServidor() == null) {
			laudoAih.setServidor(this.getRegistroColaboradorFacade().obterServidorPorUsuario(servidorLogado));
		}

		if (laudoAih.getSinaisSintomas().length() < 4) {
			laudoAih.setSinaisSintomas("Sinais: " + laudoAih.getSinaisSintomas());
		}
		if (laudoAih.getCondicoes().length() < 4) {
			laudoAih.setCondicoes("Condicoes: " + laudoAih.getCondicoes());
		}
		if (laudoAih.getResultadosProvas().length() < 4) {
			laudoAih.setResultadosProvas("Resultado: " + laudoAih.getResultadosProvas());
		}
	}

	protected MamLaudoAihDAO getMamLaudoAihDAO() {
		return mamLaudoAihDAO;
	}

	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	protected MamExtratoLaudoAihDAO getMamExtratoLaudoAihDAO() {
		return mamExtratoLaudoAihDAO;
	}

	protected AtendimentoPacientesAgendadosON getAtendimentoPacientesAgendadosON() {
		return atendimentoPacientesAgendadosON;
	}

	protected LaudoAihON getLaudoAihON() {
		return laudoAihON;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

}