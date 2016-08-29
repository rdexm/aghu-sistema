package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioDiaSemanaColetaExames;
import br.gov.mec.aghu.exames.dao.AelHorarioColetaExameDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioColetaExameJnDAO;
import br.gov.mec.aghu.model.AelExameHorarioColeta;
import br.gov.mec.aghu.model.AelExameHorarioColetasJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class ManterHorarioColetaExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterHorarioColetaExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelHorarioColetaExameJnDAO aelHorarioColetaExameJnDAO;
	
	@Inject
	private AelHorarioColetaExameDAO aelHorarioColetaExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3664285146025360581L;


	public enum ManterHorarioColetaExamesRNExceptionCode implements BusinessExceptionCode {
		AEL_02679;
	}
	
	public AelExameHorarioColeta inserir(AelExameHorarioColeta horarioExame) throws ApplicationBusinessException {
		//Regras pré-insert
		preInsert(horarioExame);
		
		//getHorarioColetaExameDAO().obterValorSequencialId(horarioExame);
		
		verificarTodosDias(horarioExame);
		
		if(!horarioExame.getDiaSemana().equals(DominioDiaSemanaColetaExames.TODOS)) {
			//Insert
			preInsert(horarioExame);
			getHorarioColetaExameDAO().persistir(horarioExame);
			getHorarioColetaExameDAO().flush();
			
		}
		return horarioExame;
	}
	
	private void verificarTodosDias(AelExameHorarioColeta horarioExame) throws ApplicationBusinessException {

		if (horarioExame.getDiaSemana().equals(DominioDiaSemanaColetaExames.TODOS)) {
			List<DominioDiaSemanaColetaExames> listaDiasSemana = popularTodosDiasLista();

			for (DominioDiaSemanaColetaExames dominioDiaSemanaColetaExames : listaDiasSemana) {

				AelExameHorarioColeta novoHorario = new AelExameHorarioColeta();
				novoHorario.setExamesMaterialAnalise(horarioExame.getExamesMaterialAnalise());
				novoHorario.setHorarioInicial(horarioExame.getHorarioInicial());
				novoHorario.setHorarioFinal(horarioExame.getHorarioFinal());
				novoHorario.setIndSituacao(horarioExame.getIndSituacao());
				novoHorario.setDiaSemana(dominioDiaSemanaColetaExames);
				novoHorario.setCriadoEm(horarioExame.getCriadoEm());
				novoHorario.setServidor(horarioExame.getServidor());
				getHorarioColetaExameDAO().obterValorSequencialId(novoHorario);
				preInsert(horarioExame);
				getHorarioColetaExameDAO().persistir(novoHorario);
				getHorarioColetaExameDAO().flush();

			}
		}
	}
	
	private List<DominioDiaSemanaColetaExames> popularTodosDiasLista() {
		List<DominioDiaSemanaColetaExames> listaDiasSemana = new ArrayList<DominioDiaSemanaColetaExames>();

		listaDiasSemana.add(DominioDiaSemanaColetaExames.DOM);
		listaDiasSemana.add(DominioDiaSemanaColetaExames.SEG);
		listaDiasSemana.add(DominioDiaSemanaColetaExames.TER);
		listaDiasSemana.add(DominioDiaSemanaColetaExames.QUA);
		listaDiasSemana.add(DominioDiaSemanaColetaExames.QUI);
		listaDiasSemana.add(DominioDiaSemanaColetaExames.SEX);
		listaDiasSemana.add(DominioDiaSemanaColetaExames.SAB);
		return listaDiasSemana;
	}

	/**
	 * @throws ApplicationBusinessException  
	 * 
	 */
	protected void preInsert(AelExameHorarioColeta horarioExame) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Atribui ao exame a data de criação do horário como o dia corrente
		horarioExame.setCriadoEm(new Date());
		
		//Valida Data Início maior que Data Fim.
		validaHorarioColeta(horarioExame);
		
		//Seta ser_matricula e ser_vin_codigo 
		horarioExame.setServidor(servidorLogado);
		
	}
	
	/**
	 * Remove objeto do banco
	 * @param horarioColetaExame
	 * @throws ApplicationBusinessException 
	 */
	public void remover(AelExameHorarioColeta horarioColetaExame) throws ApplicationBusinessException {
		horarioColetaExame = getHorarioColetaExameDAO().obterPorChavePrimaria(horarioColetaExame.getId());
		getHorarioColetaExameDAO().remover(horarioColetaExame);
		getHorarioColetaExameDAO().flush();
		posRemover(horarioColetaExame);
	}
	
	public void validaHorarioColeta(AelExameHorarioColeta horarioExame) throws ApplicationBusinessException{
		// AEL-02679
		if( horarioExame.getHorarioInicial().after(horarioExame.getHorarioFinal())  ){
			throw new ApplicationBusinessException(ManterHorarioColetaExamesRNExceptionCode.AEL_02679);
		}
	}
	
	protected AelHorarioColetaExameDAO getHorarioColetaExameDAO() {
		return aelHorarioColetaExameDAO;
	}
	
	/**
	 * ORADB AELK_REX_RN.RN_REXP_VER_UPDATE
	 * @param horarioExame
	 *  
	 */
	public void atualizar(AelExameHorarioColeta novoHorarioExame) throws ApplicationBusinessException {
		
		AelExameHorarioColeta horarioExame = getHorarioColetaExameDAO().obterPorChavePrimaria(novoHorarioExame.getId());
		AelExameHorarioColeta horarioExameAux = new AelExameHorarioColeta();
		
		try {
		
			BeanUtils.copyProperties(horarioExameAux, horarioExame);
		
		} catch (IllegalAccessException e) {
		
			this.logError(e.getMessage());
		
		} catch (InvocationTargetException e) {
	
			this.logError(e.getMessage());
		
		}
		
		//Valida Data Início maior que Data Fim.
		validaHorarioColeta(novoHorarioExame);
		
		getHorarioColetaExameDAO().merge(novoHorarioExame);
		getHorarioColetaExameDAO().flush();
		posAtualizar(horarioExameAux, novoHorarioExame);
		
	}
	
	
	/**
	 * Cria/atualiza o journal
	 * @param horarioExame
	 * @param novoHorarioExame
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizar(AelExameHorarioColeta horarioExame, AelExameHorarioColeta novoHorarioExame) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(horarioExame.getId().getEmaExaSigla(), novoHorarioExame.getId().getEmaExaSigla())
				|| CoreUtil.modificados(horarioExame.getId().getEmaManSeq(), novoHorarioExame.getId().getEmaManSeq())
				|| CoreUtil.modificados(horarioExame.getId().getSeqp(), novoHorarioExame.getId().getSeqp())
				|| CoreUtil.modificados(horarioExame.getServidor(), novoHorarioExame.getServidor())
				|| CoreUtil.modificados(horarioExame.getDiaSemana(), novoHorarioExame.getDiaSemana())
				|| CoreUtil.modificados(horarioExame.getHorarioFinal(), novoHorarioExame.getHorarioFinal())
				|| CoreUtil.modificados(horarioExame.getIndSituacao(), novoHorarioExame.getIndSituacao())) {

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelExameHorarioColetasJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelExameHorarioColetasJn.class, servidorLogado.getUsuario());
			jn.setSeqp(novoHorarioExame.getId().getSeqp());
			jn.setDiaSemana(novoHorarioExame.getDiaSemana());
			jn.setHorarioFinal(novoHorarioExame.getHorarioFinal());
			jn.setHorarioInicial(novoHorarioExame.getHorarioInicial());
			jn.setIndSituacao(novoHorarioExame.getIndSituacao());
			jn.setEmaExaSigla(novoHorarioExame.getId().getEmaExaSigla());
			jn.setEmaManSeq(novoHorarioExame.getId().getEmaManSeq());
			jn.setSerMatricula(novoHorarioExame.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(novoHorarioExame.getServidor().getId().getVinCodigo());
			jn.setCriadoEm(novoHorarioExame.getCriadoEm());
			
			getAelHorarioColetaExameJnDAO().persistir(jn);
			getAelHorarioColetaExameJnDAO().flush();
			
		}
		
	}
	
	/**
	 * Atualiza o journal após remoção
	 * @param horarioExame
	 * @param novoHorarioExame
	 * @throws ApplicationBusinessException 
	 */
	private void posRemover(AelExameHorarioColeta horarioExame) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
			AelExameHorarioColetasJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelExameHorarioColetasJn.class, servidorLogado.getUsuario());
			jn.setSeqp(horarioExame.getId().getSeqp());
			jn.setDiaSemana(horarioExame.getDiaSemana());
			jn.setHorarioFinal(horarioExame.getHorarioFinal());
			jn.setHorarioInicial(horarioExame.getHorarioInicial());
			jn.setIndSituacao(horarioExame.getIndSituacao());
			jn.setEmaExaSigla(horarioExame.getId().getEmaExaSigla());
			jn.setEmaManSeq(horarioExame.getId().getEmaManSeq());
			jn.setSerMatricula(horarioExame.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(horarioExame.getServidor().getId().getVinCodigo());
			jn.setCriadoEm(horarioExame.getCriadoEm());
			
			getAelHorarioColetaExameJnDAO().persistir(jn);
			getAelHorarioColetaExameJnDAO().flush();
		
	}
	
	protected AelHorarioColetaExameJnDAO getAelHorarioColetaExameJnDAO() {
		return aelHorarioColetaExameJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
