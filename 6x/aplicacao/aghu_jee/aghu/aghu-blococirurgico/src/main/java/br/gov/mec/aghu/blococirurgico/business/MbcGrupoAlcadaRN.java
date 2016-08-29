package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaAvalOpmsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcGrupoAlcadaJnDAO;
import br.gov.mec.aghu.blococirurgico.vo.HistoricoAlteracoesGrupoAlcadaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.dominio.DominioTipoObrigatoriedadeOpms;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpmsJn;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de BANCO para MBC_GRUPO_ALCADA
 * 
 * @author aghu
 * 
 */
@Stateless
public class MbcGrupoAlcadaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcGrupoAlcadaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	

	@Inject
	private MbcGrupoAlcadaDAO mbcGrupoAlcadaDAO;
	
	@Inject
	private MbcGrupoAlcadaJnDAO mbcGrupoAlcadaJnDAO;
	
	@Inject
	private MbcGrupoAlcadaAvalOpmsDAO mbcGrupoAlcadaAvalOpmsDAO;

	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7088709679944047733L;

	public enum MbcGrupoAlacadaRNExceptionCode implements BusinessExceptionCode {
		ERRO_EXCLUIR_GRUPO_ALCADA, MENSAGEM_ERRO_EXCLUSAO_GRUPO_ALCADA_POSSUI_ALCADAS;
	}

	protected enum MbcGrupoAlcadaRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_EXCLUSAO_ALCADA_JA_UTILIZADA
	}
	
	/*
	 * Métodos para PERSISTIR
	 */

	/**
	 * Persistir MbcGrupoAlcadaAvalOpms
	 * 
	 * @param necessidadeCirurgica
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void persistirMbcGrupoAlcadaAvalOpms(MbcGrupoAlcadaAvalOpms grupoAlcadaOpms) throws BaseException {
		if (grupoAlcadaOpms.getSeq() == null) { // Inserir
			MbcGrupoAlcadaAvalOpms entity = mbcGrupoAlcadaAvalOpmsDAO.obterPorChavePrimaria(grupoAlcadaOpms.getSeq());
			this.inserirMbcGrupoAlcada(entity);
			getMbcGrupoAlcadaDAO().flush();
		} else { // Atualizar
			MbcGrupoAlcadaAvalOpms entity = mbcGrupoAlcadaAvalOpmsDAO.obterPorChavePrimaria(grupoAlcadaOpms.getSeq());
			this.atualizarMbcGrupoAlcada(entity);
			getMbcGrupoAlcadaDAO().flush();
		}
	}
	
	public void removerMbcGrupoAlcadaAvalOpms(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) throws BaseException {
		MbcGrupoAlcadaAvalOpms entity = mbcGrupoAlcadaAvalOpmsDAO.obterPorChavePrimaria(grupoAlcadaAvalOpms.getSeq());
		this.preRemoverMbcGrupoAlcada(entity);
		this.getMbcGrupoAlcadaDAO().remover(entity);
		this.posRemoverMbcGrupoAlcada(entity);
		getMbcGrupoAlcadaDAO().flush();
	}

	public void preRemoverMbcGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) throws ApplicationBusinessException {
		// Verifica se o grupo alcada existe em alcada aval
		Long alcadaAvalCount = this.getMbcGrupoAlcadaDAO().buscarMbcAlcadaAval(grupoAlcadaAvalOpms);
		
		if (alcadaAvalCount > 0) {
			throw new ApplicationBusinessException(MbcGrupoAlacadaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_GRUPO_ALCADA_POSSUI_ALCADAS);
		}
	}
	
	protected void posRemoverMbcGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) {
		inserirJournal(grupoAlcadaAvalOpms, DominioOperacoesJournal.DEL);
	}

	public void preInserirMbcGrupoAlcadaAvalOpms(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) throws BaseException {
		MbcGrupoAlcadaAvalOpms entity = mbcGrupoAlcadaAvalOpmsDAO.obterPorChavePrimaria(grupoAlcadaAvalOpms.getSeq());
		entity.setCriadoEm(new Date()); // RN1
	}

	/**
	 * Inserir MbcGrupoAlcadaAvalOpms
	 * 
	 * @throws BaseException
	 */
	public void inserirMbcGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) throws BaseException {
		MbcGrupoAlcadaAvalOpms entity = mbcGrupoAlcadaAvalOpmsDAO.obterPorChavePrimaria(grupoAlcadaAvalOpms.getSeq());
		this.preInserirMbcGrupoAlcadaAvalOpms(entity);
		this.getMbcGrupoAlcadaDAO().persistir(entity);
		getMbcGrupoAlcadaDAO().flush();
	}

	public void preAtualizarMbcGrupoAlcadaAvalOpms(MbcGrupoAlcadaAvalOpms necessidadeCirurgica) throws BaseException {
		// Executas todas as regras do pré-inserir
		this.preInserirMbcGrupoAlcadaAvalOpms(necessidadeCirurgica);

	}

	/**
	 * Atualizar MbcGrupoAlcadaAvalOpms
	 * 
	 * @param necessidadeCirurgica
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void atualizarMbcGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) throws BaseException {
		this.preAtualizarMbcGrupoAlcadaAvalOpms(grupoAlcadaAvalOpms);
		this.getMbcGrupoAlcadaDAO().atualizar(grupoAlcadaAvalOpms);
		flush();
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcGrupoAlcadaDAO getMbcGrupoAlcadaDAO() {
		return mbcGrupoAlcadaDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	public List<MbcGrupoAlcadaAvalOpms> listarGrupoAlcadaFiltro(Short grupoAlcadaSeq, 
																AghEspecialidades aghEspecialidades,
																DominioTipoConvenioOpms tipoConvenioOpms,
																DominioTipoObrigatoriedadeOpms tipoObrigatoriedadeOpms,
																Short versao,	
																DominioSituacao situacao) {
		return getMbcGrupoAlcadaDAO().listarGrupoAlcadaFiltro(grupoAlcadaSeq,
				aghEspecialidades, 
				tipoConvenioOpms, 
				tipoObrigatoriedadeOpms, 
				versao, 
				situacao);
	}

	public AghEspecialidades obterAghEspecialidadesPorChavePrimaria(Short seq) {
		return getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seq);
	}
	
	protected void posInserirMbcGrupoAlcadaAvalOpms(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) {
		inserirJournal(grupoAlcadaAvalOpms, DominioOperacoesJournal.INS);
	}
	
	protected void posAtualizarMbcGrupoAlcadaAvalOpms(MbcGrupoAlcadaAvalOpms grupoAlcadaAvalOpms) {
		MbcGrupoAlcadaAvalOpms oldGupoAlcadaAvalOpms = this.getMbcGrupoAlcadaDAO().obterOriginal(grupoAlcadaAvalOpms);

		if(CoreUtil.modificados(grupoAlcadaAvalOpms.getSituacao(), oldGupoAlcadaAvalOpms.getSituacao()) ||
				CoreUtil.modificados(grupoAlcadaAvalOpms.getTipoObrigatoriedade(), oldGupoAlcadaAvalOpms.getTipoObrigatoriedade())){
			inserirJournal(grupoAlcadaAvalOpms, DominioOperacoesJournal.UPD);
		}
	}
	
	
	protected void inserirJournal(MbcGrupoAlcadaAvalOpms elemento, DominioOperacoesJournal operacao) {
		MbcGrupoAlcadaAvalOpmsJn journal = BaseJournalFactory.getBaseJournal(operacao, MbcGrupoAlcadaAvalOpmsJn.class, obterLoginUsuarioLogado());

		journal.setSeq(elemento.getSeq());
		journal.setTipoConvenio(elemento.getTipoConvenio());
		journal.setAghEspecialidades(elemento.getAghEspecialidades());
		journal.setTipoObrigatoriedade(elemento.getTipoObrigatoriedade());
		journal.setVersao(elemento.getVersao());
		journal.setSituacao(elemento.getSituacao());
		journal.setCriadoEm(elemento.getCriadoEm());
		journal.setModificadoEm(elemento.getModificadoEm());
		journal.setRapServidores(elemento.getRapServidores());
		journal.setRapServidoresModificacao(elemento.getRapServidoresModificacao());	
		
		this.mbcGrupoAlcadaJnDAO.persistir(journal);
		flush();
	}

	public List<HistoricoAlteracoesGrupoAlcadaVO> buscarHistoricoGrupoAlcada(Short seq) {
		
		List<HistoricoAlteracoesGrupoAlcadaVO> result = new ArrayList<HistoricoAlteracoesGrupoAlcadaVO>();
		
		result.addAll(this.getMbcGrupoAlcadaDAO().buscarHistoricoGrupoAlcadaUnion1(seq));
		result.addAll(this.getMbcGrupoAlcadaDAO().buscarHistoricoGrupoAlcadaUnion2(seq));
		result.addAll(this.getMbcGrupoAlcadaDAO().buscarHistoricoGrupoAlcadaUnion3(seq));
		
		ordernarLista(result);
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private void ordernarLista(List<HistoricoAlteracoesGrupoAlcadaVO> lista) {
		final ComparatorChain comparatorChain = new ComparatorChain();
		
		final BeanComparator seqComparator = new BeanComparator(
				HistoricoAlteracoesGrupoAlcadaVO.Fields.SEQ.toString(), new NullComparator(false));
		
		final BeanComparator nivelComparator = new BeanComparator(
				HistoricoAlteracoesGrupoAlcadaVO.Fields.DESCRICAO_NIVEL.toString(), new NullComparator(false));
		
		final BeanComparator servidorComparator = new BeanComparator(
				HistoricoAlteracoesGrupoAlcadaVO.Fields.DESCRICAO_SERVIDOR.toString(), new NullComparator(false));
		
		
		comparatorChain.addComparator(seqComparator);
		comparatorChain.addComparator(nivelComparator);
		comparatorChain.addComparator(servidorComparator);
		
		Collections.sort(lista, comparatorChain);
		
	}

}
