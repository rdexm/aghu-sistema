package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaJustificativaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRefCodeDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.MbcAgendaJustificativa;
import br.gov.mec.aghu.model.MbcRefCode;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável por prover os métodos de negócio de
 * MbcAgendasJustificativa.
 * 
 */
@Stateless
public class MbcAgendasJustificativaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendasJustificativaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcRefCodeDAO mbcRefCodeDAO;

	@Inject
	private MbcAgendaJustificativaDAO mbcAgendaJustificativaDAO;


	@EJB
	private MbcAgendaHistoricoRN mbcAgendaHistoricoRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8251231684869334202L;

	public void persistir(MbcAgendaJustificativa agJustificativa
			) throws BaseException {

		if (agJustificativa.getId().getSeqp() == null) {
			preInserir(agJustificativa);
			agJustificativa.getId().setSeqp(getMbcAgendaJustificativaDAO().buscarProximoSeqp(agJustificativa.getId().getAgdSeq()));
		} else {
			preAtualizar(agJustificativa);
		}

		getMbcAgendaJustificativaDAO().persistir(agJustificativa);
	}
	
	/**
	 * @ORADB MBCT_AGJ_BRU
	 */
	private void preAtualizar(MbcAgendaJustificativa agJustificativa
			) throws BaseException {
		StringBuilder descricaoHistorico = new StringBuilder(36);
		MbcRefCode mbcRefOriginal, mbcRefNovo;
		MbcAgendaJustificativa agOriginal = getMbcAgendaJustificativaDAO().obterOriginal(agJustificativa);

		mbcRefOriginal = getMbcRefCodeDAO().buscarRefCodePorDominioEJustificativa("TIPO_JUSTIF_AGEND", agOriginal.getTipo().toString());
		mbcRefNovo = getMbcRefCodeDAO().buscarRefCodePorDominioEJustificativa("TIPO_JUSTIF_AGEND", agJustificativa.getTipo().toString());
				
		// se tipo modificado
		if (CoreUtil.modificados(agJustificativa.getTipo(), agOriginal.getTipo())) {		
			descricaoHistorico.append("Tipo de justificativa trocada de " + mbcRefOriginal.getId().getRvMeaning() + " para " 
					+ mbcRefNovo.getId().getRvMeaning());
		} else {
			descricaoHistorico.append("Justificativa para ").append(mbcRefNovo.getId().getRvMeaning());
		}

		// se justificativa modificada
		if (CoreUtil.modificados(agJustificativa.getJustificativa(), agOriginal.getJustificativa())){				
			descricaoHistorico.append(", alterada de ").append(agOriginal.getJustificativa()).append(" para ").append(agJustificativa.getJustificativa());
		} else {
			descricaoHistorico.append(", justificativa: ").append(agJustificativa.getJustificativa());
		}

		getMbcAgendaHistoricoRN().inserir(agJustificativa.getId().getAgdSeq(), DominioSituacaoAgendas.LE, DominioOrigem.J, descricaoHistorico.toString(), DominioOperacaoAgenda.A);
	}

	/**
	 * @throws ApplicationBusinessException
	 * @ORADB MBCT_AGJ_BRI
	 */
	private void preInserir(MbcAgendaJustificativa agJustificativa
			) throws ApplicationBusinessException {
		agJustificativa.setCriadoEm(new Date());
		agJustificativa.setRapServidores(servidorLogadoFacade.obterServidorLogado());
	}

	/**
	 * @throws BaseException
	 * @ORADB MBCT_AGJ_BRD
	 */	
	@SuppressWarnings("ucd")
	public void preExcluir(MbcAgendaJustificativa agJustificativa	) throws BaseException {
		StringBuilder descricao = new StringBuilder(19);
		MbcRefCode mbcRef = new MbcRefCode();
		mbcRef = getMbcRefCodeDAO().buscarRefCodePorDominioEJustificativa("TIPO_JUSTIF_AGEND", agJustificativa.getTipo().toString());

		descricao.append("Justificativa para ").append(mbcRef.getId().getRvMeaning());

		getMbcAgendaHistoricoRN().inserir(agJustificativa.getId().getAgdSeq(), agJustificativa.getMbcAgendas().getIndSituacao(), DominioOrigem.J, descricao.toString(), DominioOperacaoAgenda.E);

	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	protected MbcAgendaJustificativaDAO getMbcAgendaJustificativaDAO() {
		return mbcAgendaJustificativaDAO;
	}

	protected MbcRefCodeDAO getMbcRefCodeDAO() {
		return mbcRefCodeDAO;
	}

	protected MbcAgendaHistoricoRN getMbcAgendaHistoricoRN() {
		return mbcAgendaHistoricoRN;
	}
}