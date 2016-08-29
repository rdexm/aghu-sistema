package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtDadoDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDadoDescJnDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtDadoDescRN.DadoDescRNExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDadoDescJn;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtDadoDesc.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class PdtDadoDescRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtDadoDescRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDadoDescJnDAO pdtDadoDescJnDAO;

	@Inject
	private PdtDadoDescDAO pdtDadoDescDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -7148213688197693022L;
	
	public enum DadoDescRNExceptionCode implements BusinessExceptionCode {
		PDT_00130, PDT_00131, PDT_00132
	}
	
	/**
	 * Insere instância de PdtDadoDesc.
	 * 
	 * @param newDadoDesc
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void inserirDadoDesc(PdtDadoDesc newDadoDesc) 
			throws ApplicationBusinessException {
		
		executarAntesInserir(newDadoDesc);
		getPdtDadoDescDAO().persistir(newDadoDesc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_PDD_BRI
	 * 
	 * @param newDadoDesc
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesInserir(PdtDadoDesc newDadoDesc)
			throws ApplicationBusinessException {
		
		// TODO: criadoEm está como VARCHAR2(240) no banco, 
		// ver se é possivel alterar para TIMESTAMP e mudar mapeamento no POJO para Date
		newDadoDesc.setCriadoEm(new Date().toString());
		newDadoDesc.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
		// Se dthr_fim for diferente de null, esta deve ser maior que dthr_inicio
		verificarDatas(newDadoDesc);
		
		// Equipamento deve estar ATIVO
		verificarEquipamentoAtivo(newDadoDesc);
		
		// Tecnica deve estar ATIVO
		verificarTecnica(newDadoDesc);
	}
	
	/**
	 * Atualiza instância de PdtDadoDesc.
	 * 
	 * @param newDadoDesc
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public PdtDadoDesc atualizarDadoDesc(PdtDadoDesc newDadoDesc) throws ApplicationBusinessException {
		PdtDadoDesc oldDadoDesc = getPdtDadoDescDAO().obterOriginal(newDadoDesc);
		executarAntesAtualizar(oldDadoDesc, newDadoDesc);
		newDadoDesc = getPdtDadoDescDAO().atualizar(newDadoDesc);
		executarAposAtualizar(oldDadoDesc, newDadoDesc);
		return newDadoDesc;
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_PDD_BRU
	 * 
	 * @param oldDadoDesc
	 * @param newDadoDesc
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	private void executarAntesAtualizar(PdtDadoDesc oldDadoDesc,
			PdtDadoDesc newDadoDesc) throws ApplicationBusinessException {
		
		newDadoDesc.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
		// Se dthr_fim for diferente de null, esta deve ser maior que dthr_inicio
		verificarDatas(newDadoDesc);
		
		// Equipamento deve estar ATIVO
		if (CoreUtil.modificados(newDadoDesc.getPdtEquipamento(), oldDadoDesc.getPdtEquipamento())) {
			verificarEquipamentoAtivo(newDadoDesc);	
		}
		
		// Tecnica deve estar ATIVO
		if (CoreUtil.modificados(newDadoDesc.getPdtTecnica(), oldDadoDesc.getPdtTecnica())) {
			verificarTecnica(newDadoDesc);	
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_PDD_ARU
	 * 
	 * @param oldDadoDesc
	 * @param servidorLogado
	 */
	private void executarAposAtualizar(PdtDadoDesc oldDadoDesc, PdtDadoDesc newDadoDesc 
			) {
		
		if (CoreUtil.modificados(newDadoDesc.getSedacao(), oldDadoDesc.getSedacao())
				|| CoreUtil.modificados(newDadoDesc.getAsa(), oldDadoDesc.getAsa())
				|| CoreUtil.modificados(newDadoDesc.getTempoJejum(), oldDadoDesc.getTempoJejum())
				|| CoreUtil.modificados(newDadoDesc.getNroFilme(), oldDadoDesc.getNroFilme())
				|| CoreUtil.modificados(newDadoDesc.getDthrInicio(), oldDadoDesc.getDthrInicio())
				|| CoreUtil.modificados(newDadoDesc.getDthrFim(), oldDadoDesc.getDthrFim())
				|| CoreUtil.modificados(newDadoDesc.getCarater(), oldDadoDesc.getCarater())
				|| CoreUtil.modificados(newDadoDesc.getIntercorrencia(), oldDadoDesc.getIntercorrencia())
				|| CoreUtil.modificados(newDadoDesc.getObservacoesProc(), oldDadoDesc.getObservacoesProc())
				|| CoreUtil.modificados(newDadoDesc.getPdtDescricao(), oldDadoDesc.getPdtDescricao())
				|| CoreUtil.modificados(newDadoDesc.getMbcTipoAnestesias(), oldDadoDesc.getMbcTipoAnestesias())
				|| CoreUtil.modificados(newDadoDesc.getPdtTecnica(), oldDadoDesc.getPdtTecnica())
				|| CoreUtil.modificados(newDadoDesc.getRapServidores(), oldDadoDesc.getRapServidores())
				|| CoreUtil.modificados(newDadoDesc.getPdtEquipamento(), oldDadoDesc.getPdtEquipamento())
				|| CoreUtil.modificados(newDadoDesc.getIndUsoO2(), oldDadoDesc.getIndUsoO2())
				) {
			
			inserirJournal(oldDadoDesc, servidorLogadoFacade.obterServidorLogado(), DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * Remove instância de PdtDadoDesc.
	 * 
	 * @param dadoDesc
	 * @param servidorLogado
	 */
	public void removerDadoDesc(PdtDadoDesc dadoDesc) {
		dadoDesc = getPdtDadoDescDAO().obterPorChavePrimaria(dadoDesc.getDdtSeq());
		getPdtDadoDescDAO().remover(dadoDesc);
		executarAposRemover(dadoDesc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_PDD_ARD
	 * 
	 * @param oldDadoDesc
	 * @param servidorLogado
	 */
	private void executarAposRemover(PdtDadoDesc oldDadoDesc) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		inserirJournal(oldDadoDesc, servidorLogado, DominioOperacoesJournal.DEL);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: PDTK_PDD_RN.RN_PDDP_VER_DATAS
	 * 
	 * @param dthrInicio
	 * @param dthrFim
	 * @throws ApplicationBusinessException
	 */
	private void verificarDatas(PdtDadoDesc newDadoDesc) throws ApplicationBusinessException {
		Date dthrInicio = newDadoDesc.getDthrInicio();
		Date dthrFim = newDadoDesc.getDthrFim();
		
		if (dthrFim != null && (dthrInicio == null || dthrFim.before(dthrInicio))) {
			// Data fim da descrição deve ser maior que a Data Início !
			throw new ApplicationBusinessException(DadoDescRNExceptionCode.PDT_00130);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: PDTK_PDD_RN.RN_PDDP_VER_EQUIP
	 * 
	 * @param dadoDesc
	 * @throws ApplicationBusinessException
	 */
	private void verificarEquipamentoAtivo(PdtDadoDesc dadoDesc) throws ApplicationBusinessException {
		PdtEquipamento equipamento = dadoDesc.getPdtEquipamento();
		
		// Qdo incluir ou alterar o código do equipamento, este deve estar ATIVO.
		if (equipamento != null && DominioSituacao.I.equals(equipamento.getIndSituacao())) {
			// Equipamento deve estar ativo !
			throw new ApplicationBusinessException(DadoDescRNExceptionCode.PDT_00131);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: PDTK_PDD_RN.RN_PDDP_VER_TECNICA
	 * 
	 * @param dadoDesc
	 * @throws ApplicationBusinessException
	 */
	private void verificarTecnica(PdtDadoDesc dadoDesc) throws ApplicationBusinessException {
		PdtTecnica tecnica = dadoDesc.getPdtTecnica();
		
		if (tecnica != null && DominioSituacao.I.equals(tecnica.getIndSituacao())) {
			// Técnica deve estar ATIVO !
			throw new ApplicationBusinessException(DadoDescRNExceptionCode.PDT_00132);
		}
	}
	
	private void inserirJournal(final PdtDadoDesc oldDadoDesc, 
			final RapServidores servidorLogado, final DominioOperacoesJournal operacaoJournal) {
		
		PdtDadoDescJn jn = BaseJournalFactory.getBaseJournal(operacaoJournal, PdtDadoDescJn.class, servidorLogado.getUsuario());
		jn.setSedacao(oldDadoDesc.getSedacao());
		jn.setAsa(oldDadoDesc.getAsa());
		jn.setTempoJejum(oldDadoDesc.getTempoJejum());
		jn.setNroFilme(oldDadoDesc.getNroFilme());
		jn.setDthrInicio(oldDadoDesc.getDthrInicio());
		jn.setDthrFim(oldDadoDesc.getDthrFim());
		jn.setCarater(oldDadoDesc.getCarater());
		jn.setIntercorrencia(oldDadoDesc.getIntercorrencia());
		jn.setObservacoesProc(oldDadoDesc.getObservacoesProc());
		jn.setIndUsoO2(oldDadoDesc.getIndUsoO2());
		jn.setDdtSeq(oldDadoDesc.getDdtSeq());
		
		MbcTipoAnestesias tipoAnestesia = oldDadoDesc.getMbcTipoAnestesias();
		if (tipoAnestesia != null) {
			jn.setTanSeq(tipoAnestesia.getSeq());	
		}
		
		PdtTecnica tecnica = oldDadoDesc.getPdtTecnica();
		if (tecnica != null) {
			jn.setDteSeq(tecnica.getSeq());
		}
		
		RapServidoresId servidorId = oldDadoDesc.getRapServidores().getId();
		jn.setSerMatricula(servidorId.getMatricula());
		jn.setSerVinCodigo(servidorId.getVinCodigo());
		
		PdtEquipamento equipamento = oldDadoDesc.getPdtEquipamento();
		if (equipamento != null) {
			jn.setDeqSeq(equipamento.getSeq());	
		}
		
		getPdtDadoDescJnDAO().persistir(jn);
	}

	protected PdtDadoDescDAO getPdtDadoDescDAO() {
		return pdtDadoDescDAO;
	}
	
	protected PdtDadoDescJnDAO getPdtDadoDescJnDAO() {
		return pdtDadoDescJnDAO;
	}
}
