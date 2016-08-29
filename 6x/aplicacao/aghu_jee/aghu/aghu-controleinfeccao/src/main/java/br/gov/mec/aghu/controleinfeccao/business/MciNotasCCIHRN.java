package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciNotasCCIHDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotasCCIHJnDAO;
import br.gov.mec.aghu.model.MciNotasCCIH;
import br.gov.mec.aghu.model.MciNotasCCIHJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciNotasCCIHRN extends BaseBusiness {

private static final long serialVersionUID = -3619932782639549444L;

private static final Log LOG = LogFactory.getLog(MciNotasCCIHRN.class);
	
@Override
@Deprecated
protected Log getLogger() {
	return LOG;
}

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@Inject
private MciNotasCCIHDAO mciNotasCCIHDAO;

@Inject
private MciNotasCCIHJnDAO mciNotasCCIHJnDAO;

private enum MciNotasCCIHRNExceptionCode implements BusinessExceptionCode {
	MENSAGEM_NOTA_CCIH_ERRO_DATA;
}

	public void persistirNotaCCIH(MciNotasCCIH mciNotasCCIHNew) throws ApplicationBusinessException {
		
		this.validarDatas(mciNotasCCIHNew);
		
		if (mciNotasCCIHNew.getSeq() == null) {
			inserir(mciNotasCCIHNew);
		} else {
			atualizar(mciNotasCCIHNew);
		}
	}
	
	private void inserir(MciNotasCCIH mciNotasCCIHNew) {
		mciNotasCCIHNew.setCriadoEm(new Date());
		mciNotasCCIHNew.setServidor(servidorLogadoFacade.obterServidorLogado());
		this.mciNotasCCIHDAO.persistir(mciNotasCCIHNew);
	}
	
	private void atualizar(MciNotasCCIH mciNotasCCIHNew) {
		mciNotasCCIHNew.setAlteradoEm(new Date());
		mciNotasCCIHNew.setServidorMovimentado(servidorLogadoFacade.obterServidorLogado());
		this.mciNotasCCIHDAO.merge(mciNotasCCIHNew);
		this.persistirMciNotasCCIHJournal(mciNotasCCIHNew, DominioOperacoesJournal.UPD);
	}

	public void excluir(Integer seq) {
		MciNotasCCIH notaCCIH = this.mciNotasCCIHDAO.obterPorChavePrimaria(seq);
		this.mciNotasCCIHDAO.remover(notaCCIH);
		this.persistirMciNotasCCIHJournal(notaCCIH, DominioOperacoesJournal.DEL);
	}
	
	private void validarDatas(MciNotasCCIH mciNotasCCIHNew) throws ApplicationBusinessException {
		
		if (mciNotasCCIHNew.getDtHrEncerramento() != null && mciNotasCCIHNew.getDtHrEncerramento().before(mciNotasCCIHNew.getDtHrInicio())) {
			throw new ApplicationBusinessException(MciNotasCCIHRNExceptionCode.MENSAGEM_NOTA_CCIH_ERRO_DATA);
		}
	}
	
	private void persistirMciNotasCCIHJournal(MciNotasCCIH obj, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MciNotasCCIHJn journal = BaseJournalFactory.getBaseJournal(operacao, MciNotasCCIHJn.class, servidorLogado.getUsuario());
		journal.setSeq(obj.getSeq());
		journal.setPaciente(obj.getPaciente());
		journal.setDescricao(obj.getDescricao());
		journal.setDtHrInicio(obj.getDtHrInicio());
		journal.setDtHrEncerramento(obj.getDtHrEncerramento());
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setServidor(obj.getServidor());
		journal.setAlteradoEm(obj.getAlteradoEm());
		journal.setServidorMovimentado(obj.getServidorMovimentado());
		this.mciNotasCCIHJnDAO.persistir(journal);
	}

}
