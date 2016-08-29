package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcPorEquipeDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcPorEquipeJnDAO;
import br.gov.mec.aghu.model.MbcProcPorEquipe;
import br.gov.mec.aghu.model.MbcProcPorEquipeJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de BANCO para MBC_PROC_POR_EQUIPES
 * 
 * @author aghu
 * 
 */
@Stateless
public class ManterProcedimentosUsadosEquipeRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterProcedimentosUsadosEquipeRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MbcProcPorEquipeJnDAO mbcProcPorEquipeJnDAO;

	@Inject
	private MbcProcPorEquipeDAO mbcProcPorEquipeDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 3222500847490874728L;

	public enum ManterProcedimentosUsadosEquipeRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_REGISTRO_EXISTENTE;
	}

	/*
	 * Métodos INSERIR
	 */

	/**
	 * ORADB TRIGGER MBCT_PXQ_BRI (INSERIR)
	 * 
	 * @param procPorEquipe
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void preInserirMbcProcPorEquipe(MbcProcPorEquipe procPorEquipe) throws BaseException {
		this.verificarExistenciaRegistro(procPorEquipe); // RN1
		procPorEquipe.setCriadoEm(new Date()); // RN2
		// ORADB MBCK_MBC_RN.RN_MBCP_ATU_SERVIDOR
		procPorEquipe.setRapServidoresByMbcPxqSerFk2(servidorLogadoFacade.obterServidorLogado()); // RN3
	}

	/**
	 * RN1: Verifica se já existe registro com os mesmos atributos
	 * 
	 * @param procPorEquipe
	 * @throws BaseException
	 */
	public void verificarExistenciaRegistro(MbcProcPorEquipe procPorEquipe) throws BaseException {

		final Boolean existeProcedimentoUsadoEquipe = this.getMbcProcPorEquipeDAO().existeProcedimentoUsadoEquipe(procPorEquipe);

		if (existeProcedimentoUsadoEquipe) {
			throw new ApplicationBusinessException(ManterProcedimentosUsadosEquipeRNExceptionCode.MENSAGEM_ERRO_REGISTRO_EXISTENTE);
		}

	}

	/**
	 * Inserir MbcProcPorEquipe
	 * 
	 * @param procPorEquipe
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserirMbcProcPorEquipe(MbcProcPorEquipe procPorEquipe) throws BaseException {
		this.preInserirMbcProcPorEquipe(procPorEquipe);
		this.getMbcProcPorEquipeDAO().persistir(procPorEquipe);
		this.getMbcProcPorEquipeDAO().flush();
	}

	/*
	 * Métodos REMOVER
	 */

	/**
	 * Remover MbcProcPorEquipe
	 * 
	 * @param procPorEquipe
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void removerMbcProcPorEquipe(MbcProcPorEquipe procPorEquipe) throws BaseException {
		MbcProcPorEquipe excluir = this.getMbcProcPorEquipeDAO().obterPorChavePrimaria(procPorEquipe.getId());
		if(excluir != null){
			this.getMbcProcPorEquipeDAO().remover(excluir);
			this.getMbcProcPorEquipeDAO().flush();
			this.posRemoverMbcProcPorEquipe(excluir);
		}
	}

	/**
	 * ORADB TRIGGER MBCT_PXQ_ARD
	 * 
	 * @param procPorEquipe
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void posRemoverMbcProcPorEquipe(MbcProcPorEquipe procPorEquipe) throws BaseException {
		this.inserirMbcProcPorEquipeJn(procPorEquipe); // TODO USUÁRIO LOGADO
	}

	/**
	 * Insere MbcProcPorEquipeJn após remoção
	 * 
	 * @param procPorEquipe
	 * @throws BaseException
	 */
	public void inserirMbcProcPorEquipeJn(MbcProcPorEquipe procPorEquipe) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		MbcProcPorEquipeJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, MbcProcPorEquipeJn.class, servidorLogado.getUsuario());

		jn.setCriadoEm(new Date());
		jn.setPciSeq(procPorEquipe.getMbcProcedimentoCirurgicos().getSeq());
		jn.setSerMatricula(servidorLogado.getId().getMatricula());
		jn.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		jn.setSerMatriculaPrf(procPorEquipe.getRapServidoresByMbcPxqSerFk1().getId().getMatricula());
		jn.setSerVinCodigoPrf(procPorEquipe.getRapServidoresByMbcPxqSerFk1().getId().getVinCodigo());
		jn.setUnfSeq(procPorEquipe.getAghUnidadesFuncionais().getSeq());

		// Inserir Journal
		this.getMbcProcPorEquipeJnDAO().persistir(jn);
		this.getMbcProcPorEquipeJnDAO().flush();

	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcProcPorEquipeDAO getMbcProcPorEquipeDAO() {
		return mbcProcPorEquipeDAO;
	}

	protected MbcProcPorEquipeJnDAO getMbcProcPorEquipeJnDAO() {
		return mbcProcPorEquipeJnDAO;
	}

}
