package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciGrupoReportRotinaCciDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciGrupoReportRotinaCciJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciParamReportGrupoDAO;
import br.gov.mec.aghu.model.MciGrupoReportRotinaCci;
import br.gov.mec.aghu.model.MciGrupoReportRotinaCciJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciGrupoReportRotinaCciRN extends BaseBusiness {

/**
	 * 
	 */
	private static final long serialVersionUID = 8304115633456871471L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MciGrupoReportRotinaCciDAO mciGrupoReportRotinaCciDAO;
	
	@Inject
	private	MciParamReportGrupoDAO mciParamReportGrupoDAO;
	
	@Inject
	private	MciGrupoReportRotinaCciJnDAO mciGrupoReportRotinaCciJnDAO;


	private enum MciGrupoReportRotinaCciExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RESTRICAO_EXCLUSAO_GRUPO_REPORT_ROTINA;
	}
	public void gravarGrupoReportRotinaCci(MciGrupoReportRotinaCci mciGrupoReportRotinaCci) throws ApplicationBusinessException{
		if (mciGrupoReportRotinaCci.getSeq() == null) {
			inserirGrupoReportRotinaCci(mciGrupoReportRotinaCci);
		} else {
			atualizarGrupoReportRotinaCci(mciGrupoReportRotinaCci);
		}
	}
	private void inserirGrupoReportRotinaCci(MciGrupoReportRotinaCci mciGrupoReportRotinaCci) throws ApplicationBusinessException{
		setarDadosPadraoInsercao(mciGrupoReportRotinaCci);
		this.mciGrupoReportRotinaCciDAO.persistir(mciGrupoReportRotinaCci);
	}

	private void setarDadosPadraoInsercao(MciGrupoReportRotinaCci mciGrupoReportRotinaCci) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		mciGrupoReportRotinaCci.setCriadoEm(new Date());
		mciGrupoReportRotinaCci.setRapServidoresByMciGrrSerFk1(servidorLogado);
		
	}
	private void atualizarGrupoReportRotinaCci(MciGrupoReportRotinaCci mciGrupoReportRotinaCci) throws ApplicationBusinessException{
		MciGrupoReportRotinaCci mciGrupoReportRotinaCciOriginal = mciGrupoReportRotinaCciDAO.obterOriginal(mciGrupoReportRotinaCci);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		setarDadosPadraoAlteracao(mciGrupoReportRotinaCci, servidorLogado);
		this.mciGrupoReportRotinaCciDAO.merge(mciGrupoReportRotinaCci);
		posAtualizarFatorPredisponentes(mciGrupoReportRotinaCciOriginal, mciGrupoReportRotinaCci, servidorLogado);
	}	
	private void setarDadosPadraoAlteracao(MciGrupoReportRotinaCci mciGrupoReportRotinaCci, RapServidores servidorLogado) {
		mciGrupoReportRotinaCci.setRapServidoresByMciGrrSerFk2(servidorLogado);
		mciGrupoReportRotinaCci.setAlteradoEm(new Date());
	}
	private void posAtualizarFatorPredisponentes(MciGrupoReportRotinaCci mciGrupoReportRotinaCciOriginal, MciGrupoReportRotinaCci mciGrupoReportRotinaCci, RapServidores servidorLogado) throws ApplicationBusinessException{
		if (!mciGrupoReportRotinaCciOriginal.equals(mciGrupoReportRotinaCci)) {
			persistirJournal(mciGrupoReportRotinaCciOriginal, DominioOperacoesJournal.UPD, servidorLogado);
		}
	}
	
	private void persistirJournal(MciGrupoReportRotinaCci obj, DominioOperacoesJournal operacao, RapServidores servidorLogado) throws ApplicationBusinessException{
		final MciGrupoReportRotinaCciJn journal = BaseJournalFactory.getBaseJournal(operacao, MciGrupoReportRotinaCciJn.class, servidorLogado.getUsuario());
		journal.setSeq(obj.getSeq());
		journal.setVersion(obj.getVersion());
		
		journal.setDescricao(obj.getDescricao());
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setAlteradoEm(obj.getAlteradoEm());
		journal.setRapServidoresByMciGrrSerFk1(obj.getRapServidoresByMciGrrSerFk1());
		journal.setRapServidoresByMciGrrSerFk2(obj.getRapServidoresByMciGrrSerFk2());
		journal.setIndSituacao(obj.getIndSituacao());                 
		journal.setIndSemanal(obj.getIndSemanal());             
		journal.setIndMensal(obj.getIndMensal());       
		this.mciGrupoReportRotinaCciJnDAO.persistir(journal);	
	}
	
	public void excluirGrupoReportRotinaCci(Short seq) throws ApplicationBusinessException {
		MciGrupoReportRotinaCci mciGrupoReportRotinaCciOriginal = mciGrupoReportRotinaCciDAO.obterPorChavePrimaria(seq);
		this.preExcluirGrupoReportRotinaCci(mciGrupoReportRotinaCciOriginal);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		mciGrupoReportRotinaCciDAO.remover(mciGrupoReportRotinaCciOriginal);
		persistirJournal(mciGrupoReportRotinaCciOriginal, DominioOperacoesJournal.DEL, servidorLogado);
	}
	private void preExcluirGrupoReportRotinaCci(MciGrupoReportRotinaCci mciGrupoReportRotinaCci) throws ApplicationBusinessException {
		Long count =  mciParamReportGrupoDAO.pesquisarParamReportGrupoPorSeqGrupoCount(mciGrupoReportRotinaCci.getSeq());
		if (count != null && count.longValue() > 0) {
			throw new ApplicationBusinessException(MciGrupoReportRotinaCciExceptionCode.MENSAGEM_RESTRICAO_EXCLUSAO_GRUPO_REPORT_ROTINA);
		}
	}

	protected MciGrupoReportRotinaCciDAO getMciGrupoReportRotinaCciDAO() {
		return mciGrupoReportRotinaCciDAO;
	}
	
	@Override
	protected Log getLogger() {
		return LogFactory.getLog(MciGrupoReportRotinaCciRN.class);
	}
}

