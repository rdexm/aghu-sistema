package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAelExameAp;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelExameApItemSolicDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoExameApDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelOcorrenciaExameApDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaDAO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.AelExtratoExameApId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelNotaAdicionalId;
import br.gov.mec.aghu.model.AelOcorrenciaExameAp;
import br.gov.mec.aghu.model.AelOcorrenciaExameApId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
/**
 * 
 *	Classe que implementa as procedures de AELK_LUX_RN
 * 
 */
@Stateless
public class PatologiaRN extends BaseBusiness {


@EJB
private AelExtratoExameApRN aelExtratoExameApRN;

@EJB
private AelOcorrenciaExameApRN aelOcorrenciaExameApRN;

private static final Log LOG = LogFactory.getLog(PatologiaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExtratoExameApDAO aelExtratoExameApDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelExameApItemSolicDAO aelExameApItemSolicDAO;

@Inject
private AelExameApDAO aelExameApDAO;

@Inject
private AelPatologistaDAO aelPatologistaDAO;

@EJB
private IExamesFacade examesFacade;

@Inject
private AelOcorrenciaExameApDAO aelOcorrenciaExameApDAO;

@Inject
private AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO;

	private static final long serialVersionUID = -798406697870064652L;

	private static final String descricao = "Ocorrência efetuada pelo Sistema de Exames";
	
	public enum PatologiaRNExceptionCode implements BusinessExceptionCode {
		AEL_02621,
		AEL_02676,
		AEL_02630,
		AEL_02631,
		AEL_02632,
		AEL_02677
		;
	}
	
	/**
	 * ORADB Procedure AELK_LUX_RN.RN_LUXP_VER_NUM_AP 
	 * @param lumSeq
	 * @param nroApOrigem
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuxpVerNumAp(AelExameAp exameAp) throws ApplicationBusinessException {
		Long lumSeq = exameAp.getAelAnatomoPatologicos().getSeq();

		Long nroApOrigem = null;
		if(exameAp.getAelAnatomoPatologicoOrigem() != null){
			nroApOrigem = exameAp.getAelAnatomoPatologicoOrigem().getNumeroAp();
		}
				
		if (nroApOrigem != null) {
			AelAnatomoPatologico aelAnatomoPatologico = getAelAnatomoPatologicoDAO()
					.obterAelAnatomoPatologicoPorSeqNumeroAp(
							lumSeq, exameAp.getConfigExLaudoUnico(), nroApOrigem);
			
			if (aelAnatomoPatologico != null) {
				// --Nº AP Origem deve ser diferente do Nº do Ap!
				throw new ApplicationBusinessException(PatologiaRNExceptionCode.AEL_02621);
			}
			
			aelAnatomoPatologico = getAelAnatomoPatologicoDAO()
					.obterAelAnatomoPatologicoByNumeroAp(
							nroApOrigem, exameAp.getConfigExLaudoUnico().getSeq());
			
			if (aelAnatomoPatologico == null) {
				// --Número AP Origem deve existir como Número Ap!
				throw new ApplicationBusinessException(PatologiaRNExceptionCode.AEL_02676);
			}
		}
	}

	/**
	 * ORADB Procedure AELK_LUX_RN.RN_LUXP_VER_IND_LIB
	 * 
	 */
	public void rnLuxpVerIndLib(final RapServidores servidor) throws ApplicationBusinessException {

		final Long count = getAelPatologistaDAO().listarPatologistasCount(null, null, null, Boolean.TRUE, DominioSituacao.A, 
																		  servidor != null ? servidor.getId() : null, 
																		  null);
		if (CoreUtil.igual(count, 0)) {
			//Quando Etapa Laudo é Laudo Assinado(LA), o Indicador permite liberar laudo deve estar selecionado no cadastro de Patologia!
			throw new ApplicationBusinessException(PatologiaRNExceptionCode.AEL_02630);
		}
	}
	
	/**
	 * ORADB Procedure AELK_LUX_RN.RN_LUXP_VER_DTHR_IMP 
	 * 
	 * @param indImpresso
	 * @param dthrImpressao
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuxpVerDthrImp(final Boolean indImpresso, final Date dthrImpressao) throws ApplicationBusinessException {
		if (Boolean.TRUE.equals(indImpresso) && dthrImpressao == null) {
			throw new ApplicationBusinessException(PatologiaRNExceptionCode.AEL_02631);
		}
		
		if (Boolean.FALSE.equals(indImpresso) && dthrImpressao != null) {
			throw new ApplicationBusinessException(PatologiaRNExceptionCode.AEL_02632);
		}
	}
	
	/**
	 * ORADB Procedure AELK_LUX_RN.RN_LUXP_ATU_EXTRATO
	 * 
	 * @param luxSeq
	 * @param etapasLaudo
	 * @param servidor
	 * @throws ApplicationBusinessException  
	 */
	public void rnLuxpAtuExtrato(Long luxSeq, DominioSituacaoExamePatologia etapasLaudo, RapServidores servidor) throws ApplicationBusinessException {
		AelExtratoExameApRN extratoExameApRN = getAelExtratoExameApRN();
		AelExameApDAO exameApDAO = getAelExameApDAO();
		AelExtratoExameApDAO extratoExameApDAO = getAelExtratoExameApDAO();
		
		AelExtratoExameAp extratoExameAp = new AelExtratoExameAp();
		AelExtratoExameApId id = new AelExtratoExameApId();
		id.setLuxSeq(luxSeq);
		id.setSeqp(extratoExameApDAO.geraSeqp(luxSeq));
		extratoExameAp.setId(id);
		
		extratoExameAp.setCriadoEm(new Date());
		extratoExameAp.setEtapasLaudo(etapasLaudo);
		extratoExameAp.setAelExameAp(exameApDAO.obterPorChavePrimaria(luxSeq));
		extratoExameAp.setRapServidores(servidor);
		
		extratoExameApRN.inserirAelExtratoExameAp(extratoExameAp);
	}
	

	/**
	 * ORADB Procedure AELK_LUX_RN.RN_LUXP_ATU_OCORRENC
	 * 
	 * @param luxSeq
	 * @param etapasLaudo
	 * @param servidor
	 * @throws ApplicationBusinessException  
	 */
	public void rnLuxpAtuOcorrenc(Long luxSeq, DominioSituacaoAelExameAp etapasLaudo,
			RapServidores servidor) throws ApplicationBusinessException {
		
		AelOcorrenciaExameApRN ocorrenciaExameApRN = getAelOcorrenciaExameApRN();
		AelExameApDAO exameApDAO = getAelExameApDAO();
		AelOcorrenciaExameApDAO ocorrenciaApDAO = getAelOcorrenciaExameApDAO();
		
		AelOcorrenciaExameAp ocorrenciaExameAp = new AelOcorrenciaExameAp();
		AelOcorrenciaExameApId id = new AelOcorrenciaExameApId();
		id.setLuxSeq(luxSeq);
		id.setSeqp(ocorrenciaApDAO.geraSeqp(luxSeq));
		ocorrenciaExameAp.setId(id);
		
		ocorrenciaExameAp.setCriadoEm(new Date());
		ocorrenciaExameAp.setAelExameAp(exameApDAO.obterPorChavePrimaria(luxSeq));
		ocorrenciaExameAp.setRapServidores(servidor);
		ocorrenciaExameAp.setDescricao(descricao);
		ocorrenciaExameAp.setSituacao(etapasLaudo);
		
		ocorrenciaExameApRN.inserirAelOcorrenciaExameAp(ocorrenciaExameAp);
	}

	/**
	 * ORADB Procedure AELK_LUX_RN.RN_LUXP_INS_NOTA
	 * 
	 * @param luxSeq
	 * @param nroApOrigem
	 * @throws BaseException 
	 */
	public void rnLuxpInsNota(AelExameAp exameAp) throws BaseException {
		
		Long nroApOrigem = null;
		
		if (exameAp.getAelAnatomoPatologicoOrigem() != null) {
			nroApOrigem = exameAp.getAelAnatomoPatologicoOrigem().getNumeroAp();
		}
		
		AelAnatomoPatologico ap = getAelAnatomoPatologicoDAO()
				.obterAelAnatomoPatologicoByNumeroAp(
						nroApOrigem, exameAp.getConfigExLaudoUnico().getSeq());
		
		List<AelExameApItemSolic> listaExameApItemSolic = getAelExameApItemSolicDAO()
				.listarAelExameApItemSolicPorLumSeq(ap.getSeq());
		
		for(AelExameApItemSolic exameApItemSolic : listaExameApItemSolic) {
			String notasAdicionaisText = "Há laudo de revisão em "
					+ DateUtil.dataToString(ap.getCriadoEm(), "dd/MM/yyyy")
					+ ", Nº solicitação "
					+ exameApItemSolic.getId().getIseSoeSeq()
					+ ", Config. Exame " + ap.getConfigExame().getSigla()
					+ " e Nº do AP " + ap.getNumeroAp() + ".";
			
			AelNotaAdicional notaAdicional = new AelNotaAdicional();
			AelNotaAdicionalId id = new AelNotaAdicionalId();
			id.setIseSoeSeq(exameApItemSolic.getId().getIseSoeSeq());
			id.setIseSeqp(exameApItemSolic.getId().getIseSeqp());
			notaAdicional.setId(id);
			
			notaAdicional.setItemSolicitacaoExame(getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(new AelItemSolicitacaoExamesId(exameApItemSolic.getId().getIseSoeSeq(), exameApItemSolic.getId().getIseSeqp())));
			notaAdicional.setNotasAdicionais(notasAdicionaisText);
			notaAdicional.setCriadoEm(new Date());
			
			getExamesFacade().inserirNotaAdicional(notaAdicional);
		}
	}

	/**
	 * ORADB Procedure AELK_LUX_RN.RN_LUXP_VER_EXAME
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuxpVerExame(Long luxSeq) throws ApplicationBusinessException {
		List<AelExameApItemSolic> listaExameApItemSolic = getAelExameApItemSolicDAO().listarAelExameApItemSolicPorLuxObrigatorio(luxSeq);
		if (!listaExameApItemSolic.isEmpty()) {
			// --Informe o Número Ap Origem! O indicador de obrigatório está selecionado no cadastro de Exames.
			throw new ApplicationBusinessException(PatologiaRNExceptionCode.AEL_02677);
		}
		
	}	
	
	protected AelAnatomoPatologicoDAO getAelAnatomoPatologicoDAO() {
		return aelAnatomoPatologicoDAO;
	}
	
	protected AelPatologistaDAO getAelPatologistaDAO() {
		return aelPatologistaDAO;
	}
	
	protected AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}
	
	protected AelExtratoExameApRN getAelExtratoExameApRN() {
		return aelExtratoExameApRN;
	}
	
	protected AelExtratoExameApDAO getAelExtratoExameApDAO() {
		return aelExtratoExameApDAO;
	}
	
	protected AelOcorrenciaExameApRN getAelOcorrenciaExameApRN() {
		return aelOcorrenciaExameApRN;
	}
	
	protected AelOcorrenciaExameApDAO getAelOcorrenciaExameApDAO() {
		return aelOcorrenciaExameApDAO;
	}

	protected AelExameApItemSolicDAO getAelExameApItemSolicDAO() {
		return aelExameApItemSolicDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
}