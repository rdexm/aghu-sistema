package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciFatorPredisponentesDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciFatorPredisponentesJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoFatorPredisponentesDAO;
import br.gov.mec.aghu.model.MciFatorPredisponentes;
import br.gov.mec.aghu.model.MciFatorPredisponentesJn;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciFatorPredisponentesRN extends BaseBusiness {

/**
 * 
 */
private static final long serialVersionUID = -5235936111146333915L;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@Inject
private MciFatorPredisponentesDAO mciFatorPredisponentesDAO;

@Inject
private MciMvtoFatorPredisponentesDAO mciMvtoFatorPredisponentesDAO;


private static final String ESPACO = " ";


@Inject
private MciFatorPredisponentesJnDAO mciFatorPredisponentesJnDAO;

private enum MciFatorPredisponentesExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RESTRICAO_EXCLUSAO_FATOR_PREDISPONENTE,
		MENSAGEM_VALIDACAO_PESOS,
		MENSAGEM_VALIDACAO_GRAU_RISCO;
	}
	public void gravarFatorPredisponente(MciFatorPredisponentes mciFatorPredisponentes) throws ApplicationBusinessException{
		if (mciFatorPredisponentes.getSeq() == null) {
			inserirFatorPredisponente(mciFatorPredisponentes);
		} else {
			atualizarFatorPredisponentes(mciFatorPredisponentes);
		}
	}
	private void inserirFatorPredisponente(MciFatorPredisponentes mciFatorPredisponentes) throws ApplicationBusinessException{
		preInserirFatorPredisponente(mciFatorPredisponentes);
		this.mciFatorPredisponentesDAO.persistir(mciFatorPredisponentes);
	}
	private void persistirJournal(MciFatorPredisponentes obj, DominioOperacoesJournal operacao, RapServidores servidorLogado) throws ApplicationBusinessException{
		final MciFatorPredisponentesJn journal = BaseJournalFactory.getBaseJournal(operacao, MciFatorPredisponentesJn.class, servidorLogado.getUsuario());
		journal.setSeq(obj.getSeq());
		journal.setDescricao(obj.getDescricao());
		journal.setIndSituacao(obj.getIndSituacao());
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setAlteradoEm(obj.getAlteradoEm());
		journal.setServidor(obj.getServidor());
		journal.setCgpSeq(obj.getCgpSeq());
		journal.setGrauRisco(obj.getGrauRisco());                 
		journal.setServidorMovimentado(obj.getServidorMovimentado()); 
		journal.setPesoInicial(obj.getPesoInicial());               
		journal.setPesoFinal(obj.getPesoFinal());                 
		journal.setIndIsolamento(obj.getIndIsolamento());             
		journal.setProcedureNotificacaoExames(obj.getProcedureNotificacaoExames());
		journal.setIndNotificacaoSms(obj.getIndNotificacaoSms());         
		journal.setIndUsoMascara(obj.getIndUsoMascara());             
		journal.setIndUsoAvental(obj.getIndUsoAvental());             
		journal.setIndTecnicaAsseptica(obj.getIndTecnicaAsseptica());       

		this.mciFatorPredisponentesJnDAO.persistir(journal);	
	}
	private void preInserirFatorPredisponente(MciFatorPredisponentes mciFatorPredisponentes) throws ApplicationBusinessException{
		validarPesos(mciFatorPredisponentes);
		validarGrauRisco(mciFatorPredisponentes);
		setarValoresPadraoInsercao(mciFatorPredisponentes);
	}
	private void validarGrauRisco(MciFatorPredisponentes mciFatorPredisponentes) throws ApplicationBusinessException{
		if (mciFatorPredisponentes.getGrauRisco().byteValue() < 0 || mciFatorPredisponentes.getGrauRisco().byteValue() > 5) {
			throw new ApplicationBusinessException(MciFatorPredisponentesExceptionCode.MENSAGEM_VALIDACAO_GRAU_RISCO);
		}
	}

	private void setarValoresPadraoInsercao(MciFatorPredisponentes mciFatorPredisponentes) {
		mciFatorPredisponentes.setIndUsoAvental(Boolean.FALSE);
		mciFatorPredisponentes.setIndUsoMascara(Boolean.FALSE);
		mciFatorPredisponentes.setIndNotificacaoSms(Boolean.FALSE);
		mciFatorPredisponentes.setIndIsolamento(Boolean.FALSE);
		mciFatorPredisponentes.setIndTecnicaAsseptica(Boolean.FALSE);
		mciFatorPredisponentes.setCgpSeq(null);
		mciFatorPredisponentes.setProcedureNotificacaoExames(null);
		mciFatorPredisponentes.setCriadoEm(new Date());
		mciFatorPredisponentes.setServidor(servidorLogadoFacade.obterServidorLogado());
				
	}
	
	private void setarValoresPadraoAtualizacao(MciFatorPredisponentes mciFatorPredisponentes, RapServidores servidorLogado) {
		mciFatorPredisponentes.setAlteradoEm(new Date());
		mciFatorPredisponentes.setServidorMovimentado(servidorLogado);
	}

	private void validarPesos(MciFatorPredisponentes mciFatorPredisponentes) throws ApplicationBusinessException{
		if (!((mciFatorPredisponentes.getPesoInicial() == null && mciFatorPredisponentes.getPesoFinal() == null)
				|| ((mciFatorPredisponentes.getPesoInicial() != null && mciFatorPredisponentes.getPesoInicial().doubleValue() > 0 && mciFatorPredisponentes.getPesoInicial().doubleValue() < 10)
					&& (mciFatorPredisponentes.getPesoFinal() != null && mciFatorPredisponentes.getPesoFinal().doubleValue() > 0 && mciFatorPredisponentes.getPesoFinal().doubleValue() < 10)))) {
			throw new ApplicationBusinessException(MciFatorPredisponentesExceptionCode.MENSAGEM_VALIDACAO_PESOS);
		}
	}

	private void atualizarFatorPredisponentes(MciFatorPredisponentes mciFatorPredisponentes) throws ApplicationBusinessException{
		MciFatorPredisponentes mciFatorPredisponentesOriginal = mciFatorPredisponentesDAO.obterOriginal(mciFatorPredisponentes);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		preAtualizarFatorPredisponente(mciFatorPredisponentes, servidorLogado);
		this.mciFatorPredisponentesDAO.merge(mciFatorPredisponentes);
		posAtualizarFatorPredisponentes(mciFatorPredisponentesOriginal,mciFatorPredisponentes,servidorLogado);
		
	}	

	private void posAtualizarFatorPredisponentes(MciFatorPredisponentes mciFatorPredisponentesOriginal, MciFatorPredisponentes mciFatorPredisponentes, RapServidores servidorLogado) throws ApplicationBusinessException{
		if (!mciFatorPredisponentesOriginal.equals(mciFatorPredisponentes)) {
			persistirJournal(mciFatorPredisponentesOriginal, DominioOperacoesJournal.UPD, servidorLogado);
		}
	}

	private void preExcluirFatorPredisponente(MciFatorPredisponentes mciFatorPredisponentes) throws ApplicationBusinessException {
		List<MciMvtoFatorPredisponentes> lista = mciMvtoFatorPredisponentesDAO.listarMovimentosFatoresPredisponentes(mciFatorPredisponentes.getSeq());
		if (lista != null && !lista.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			int count = 0;
			for (MciMvtoFatorPredisponentes item : lista) {
				if (count > 0) {
					builder.append(',').append(ESPACO);
				}
				count++;
				builder.append(item.getSeq());
			}
			throw new ApplicationBusinessException(MciFatorPredisponentesExceptionCode.MENSAGEM_RESTRICAO_EXCLUSAO_FATOR_PREDISPONENTE, builder.toString());
		}
	}

	public void excluirFatorPredisponente(Short seq) throws ApplicationBusinessException {
		MciFatorPredisponentes mciFatorPredisponentesOriginal = mciFatorPredisponentesDAO.obterPorChavePrimaria(seq);
		this.preExcluirFatorPredisponente(mciFatorPredisponentesOriginal);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		mciFatorPredisponentesDAO.remover(mciFatorPredisponentesOriginal);
		persistirJournal(mciFatorPredisponentesOriginal, DominioOperacoesJournal.DEL, servidorLogado);
	}
	
	private void preAtualizarFatorPredisponente(MciFatorPredisponentes mciFatorPredisponentes, RapServidores servidorLogado)  throws ApplicationBusinessException{
		validarPesos(mciFatorPredisponentes);
		validarGrauRisco(mciFatorPredisponentes);
		setarValoresPadraoAtualizacao(mciFatorPredisponentes, servidorLogado);
	}

	protected MciFatorPredisponentesDAO getMciFatorPredisponentesDAO() {
		return mciFatorPredisponentesDAO;
	}
	
	@Override
	protected Log getLogger() {
		return LogFactory.getLog(MciFatorPredisponentesRN.class);
	}
}

