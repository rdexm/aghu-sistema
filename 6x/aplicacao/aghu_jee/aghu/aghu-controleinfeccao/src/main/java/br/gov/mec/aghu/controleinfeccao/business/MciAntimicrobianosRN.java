package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.controleinfeccao.dao.MciAntimicrobianosDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciAntimicrobianosJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciCriterioGmrDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MciAntimicrobianos;
import br.gov.mec.aghu.model.MciAntimicrobianosJn;
import br.gov.mec.aghu.model.MciCriterioGmr;
import br.gov.mec.aghu.model.MciNotificacaoGmr;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciAntimicrobianosRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(MciAntimicrobianosRN.class);
	
@Override
@Deprecated
protected Log getLogger() {
	return LOG;
}

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@EJB
private IParametroFacade parametroFacade;

@Inject
private MciAntimicrobianosDAO mciAntimicrobianosDAO;

@Inject
private MciAntimicrobianosJnDAO mciAntimicrobianosJnDAO;

@Inject
private MciCriterioGmrDAO mciCriterioGmrDAO;

@Inject 
private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1053255394476553480L;

	private enum ManterAntimicrobianosExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ANTIMICROBIANO_RESTRICAO_EXCLUSAO, MENSAGEM_ANTIMICROBIANO_DADOS_INCOMPLETOS, MENSAGEM_ANTIMICROBIANO_PERIODO,
		MENSAGEM_ANTIMICROBIANO_RESTRICAO_CRITERIOS, MENSAGEM_ANTIMICROBIANO_RESTRICAO_NOTIFICACOES, MENSAGEM_ANTIMICROBIANO_REGISTRO_DUPLICADO;
	}
	
	public void persistirMciAntimicrobiano(MciAntimicrobianos mciAntimicrobianoNew) throws ApplicationBusinessException {
		
		if (mciAntimicrobianoNew.getSeq() == null) {
			validarDescricao(mciAntimicrobianoNew.getDescricao());
			inserir(mciAntimicrobianoNew);		
		} else {
			atualizar(mciAntimicrobianoNew);			
		}
	}
	
	// #37922 RN1
	public void excluirMciAntimicrobiano(Integer seq) throws ApplicationBusinessException, BaseListException {
		MciAntimicrobianos mciAntimicrobiano = mciAntimicrobianosDAO.obterPorChavePrimaria(seq);
		validarDataCriacaoAntimicrobianos(mciAntimicrobiano);
		
		verificarRestricaoExclusao(seq);		
		
		persistirMciAntimicrobianosJournal(mciAntimicrobiano, DominioOperacoesJournal.DEL);
		mciAntimicrobianosDAO.remover(mciAntimicrobiano);
	}
	
	private void verificarRestricaoExclusao(Integer ambSeq) throws BaseListException {
		List<MciCriterioGmr> criterios = this.mciCriterioGmrDAO.pesquisarCriterioGrmPorAmbSeq(ambSeq);
		List<MciNotificacaoGmr> notificacoes = this.mciNotificacaoGmrDAO.pesquisarNotificacaoGrmPorAmbSeq(ambSeq);
		
		boolean habilitarException = false;
		BaseListException listaDeErros = new BaseListException();
		listaDeErros.add(new ApplicationBusinessException(ManterAntimicrobianosExceptionCode.MENSAGEM_ANTIMICROBIANO_RESTRICAO_EXCLUSAO));
		
		if (criterios.size() > 0 || notificacoes.size() > 0) {
			if(criterios.size() > 0){
				habilitarException =  true;
				int count = 1;
				StringBuilder paramsCriterios = new StringBuilder();
				for (MciCriterioGmr item : criterios) {
					paramsCriterios.append(item.getId().getBmrSeq());
					if (count < criterios.size()){
						paramsCriterios.append(", ");
					}
					count++;
				}
				listaDeErros.add(new ApplicationBusinessException(ManterAntimicrobianosExceptionCode.MENSAGEM_ANTIMICROBIANO_RESTRICAO_CRITERIOS, paramsCriterios));
			}
			if(notificacoes.size() > 0){
				habilitarException =  true;
				int count = 1;
				StringBuilder paramsNotificacoes = new StringBuilder();
				for (MciNotificacaoGmr item : notificacoes) {
					paramsNotificacoes.append(item.getSeq());
					if (count < notificacoes.size()){
						paramsNotificacoes.append(", ");
					}
					count++;
				}
				listaDeErros.add(new ApplicationBusinessException(ManterAntimicrobianosExceptionCode.MENSAGEM_ANTIMICROBIANO_RESTRICAO_NOTIFICACOES, paramsNotificacoes));
			}
			if (habilitarException && listaDeErros.hasException()) {
				throw listaDeErros;
			}
		}	
	}
	
	// #37922 RN2
	private void atualizar(MciAntimicrobianos mciAntimicrobianoNew) throws ApplicationBusinessException {
		mciAntimicrobianoNew.setAlteradoEm(new Date());
		mciAntimicrobianoNew.setServidorMovimentado(servidorLogadoFacade.obterServidorLogado());		
		persistirMciAntimicrobianosJournal(mciAntimicrobianoNew, DominioOperacoesJournal.UPD);
		mciAntimicrobianosDAO.merge(mciAntimicrobianoNew);
	}
	
	// #37922 RN3
	private void inserir(MciAntimicrobianos mciAntimicrobianoNew) throws ApplicationBusinessException {
		mciAntimicrobianoNew.setCriadoEm(new Date());
		mciAntimicrobianoNew.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		mciAntimicrobianosDAO.persistir(mciAntimicrobianoNew);
	}
	

	private void persistirMciAntimicrobianosJournal(MciAntimicrobianos obj, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MciAntimicrobianosJn journal = BaseJournalFactory.getBaseJournal(operacao, MciAntimicrobianosJn.class, servidorLogado.getUsuario());
		journal.setSeqInt(obj.getSeq().intValue());
		journal.setDescricao(obj.getDescricao());
		journal.setIndSituacao(obj.getSituacao());
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setAlteradoEm(obj.getAlteradoEm());
		journal.setSerVinCodigo(obj.getRapServidores().getId().getVinCodigo());
		journal.setSerMatricula(obj.getRapServidores().getId().getMatricula());

		if(obj.getServidorMovimentado() != null){
			journal.setSerVinCodigoMovimentado(obj.getServidorMovimentado().getId().getVinCodigo());
			journal.setSerMatriculaMovimentado(obj.getServidorMovimentado().getId().getMatricula());
		}
		mciAntimicrobianosJnDAO.persistir(journal);	
	}
	
	// #37922 RN4
	private void validarDataCriacaoAntimicrobianos(MciAntimicrobianos mciAntimicrobiano) throws ApplicationBusinessException {
		
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		Float qtDias = CoreUtil.diferencaEntreDatasEmDias(new Date(), mciAntimicrobiano.getCriadoEm());
		
		if (qtDias > parametro.getVlrNumerico().floatValue()) {
			throw new ApplicationBusinessException(ManterAntimicrobianosExceptionCode.MENSAGEM_ANTIMICROBIANO_PERIODO);
		}
		
	}
	
	private void validarDescricao(String descricao) throws ApplicationBusinessException {
		if (descricao == null) {
			throw new ApplicationBusinessException(ManterAntimicrobianosExceptionCode.MENSAGEM_ANTIMICROBIANO_DADOS_INCOMPLETOS);
		}
		if (this.mciAntimicrobianosDAO.verificarDescricaoExiste(descricao)) {
			throw new ApplicationBusinessException(ManterAntimicrobianosExceptionCode.MENSAGEM_ANTIMICROBIANO_REGISTRO_DUPLICADO);
		}
	}
	
}
