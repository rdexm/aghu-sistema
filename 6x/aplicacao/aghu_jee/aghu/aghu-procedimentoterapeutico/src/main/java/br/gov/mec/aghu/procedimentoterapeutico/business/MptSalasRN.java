package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.vo.HistoricoSalaJnVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptSalasJn;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSalasDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSalasJnDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MptSalasRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8329460621046534629L;

	private static final Log LOG = LogFactory.getLog(MptSalasRN.class);
	
	public enum MptSalasRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SALA_POSSUI_LOCAIS_ACOMODACOES;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private MptSalasDAO mptSalasDAO;
	
	@Inject
	private MptLocalAtendimentoDAO mptLocalAtendimentoDAO;	

	@Inject
	private MptSalasJnDAO mptSalasJnDAO;
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
	
	public void persistirMptSala(MptSalas mptSala) throws ApplicationBusinessException {
		if(mptSala.getSeq() != null){
			this.atualizarMptSalas(mptSala);
		}else{
			this.inserirMptSala(mptSala);
		}
	}
	
	public void inserirMptSala(MptSalas mptSala) throws ApplicationBusinessException {
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());

		mptSala.setServidor(servidorLogado);
		mptSala.setCriadoEm(new Date());

		getMptSalasDAO().persistir(mptSala);
	}

	public void atualizarMptSalas(MptSalas mptSala) throws ApplicationBusinessException {
		MptSalas mptSalasOriginal = this.getMptSalasDAO().obterOriginal(mptSala.getSeq());
		this.inserirJournalMptSalas(mptSalasOriginal, DominioOperacoesJournal.UPD);
		
		this.getMptSalasDAO().merge(mptSala);
	}
	
	public void excluirMptSalas(MptSalas mptSala) throws ApplicationBusinessException {
		
		if(mptLocalAtendimentoDAO.verificarExisteLocalAtendimentoParaSala(mptSala.getSeq())) {
			throw new ApplicationBusinessException(MptSalasRNExceptionCode.MENSAGEM_SALA_POSSUI_LOCAIS_ACOMODACOES);
		}
		
		MptSalas mptSalaOriginal = this.getMptSalasDAO().obterOriginal(mptSala.getSeq());
		this.inserirJournalMptSalas(mptSalaOriginal, DominioOperacoesJournal.DEL);
		
		MptSalas excluir = this.getMptSalasDAO().obterPorChavePrimaria(mptSala.getSeq());
		this.getMptSalasDAO().remover(excluir);
	}
	
	private void inserirJournalMptSalas(MptSalas mptSalaOriginal, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		
		MptSalasJn mptSalasJn = BaseJournalFactory.getBaseJournal(
				operacao, MptSalasJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		mptSalasJn.setSeq(mptSalaOriginal.getSeq());
		mptSalasJn.setDescricao(mptSalaOriginal.getDescricao());
		mptSalasJn.setCriadoEm(mptSalaOriginal.getCriadoEm());
		mptSalasJn.setIndSituacao(mptSalaOriginal.getIndSituacao());
		mptSalasJn.setEspSeq(mptSalaOriginal.getEspecialidade() != null ? mptSalaOriginal.getEspecialidade().getSeq() : null);
		mptSalasJn.setTpsSeq(mptSalaOriginal.getTipoSessao() != null ? mptSalaOriginal.getTipoSessao().getSeq() : null);
		mptSalasJn.setSerMatricula(mptSalaOriginal.getServidor().getId().getMatricula());
		mptSalasJn.setSerVinCodigo(mptSalaOriginal.getServidor().getId().getVinCodigo());
		this.getMptSalasJnDAO().persistir(mptSalasJn);
	}
	
	public List<HistoricoSalaJnVO> obterHistoricoSalaJn(Integer firstResult, Integer maxResults, String orderProperty, 
			boolean asc, Short localSeq) {
		
		List<HistoricoSalaJnVO> lista = this.mptSalasJnDAO.obterHistoricoSalaJn(firstResult, maxResults, orderProperty, asc, localSeq);
		
		if (!lista.isEmpty()) {
			
			for (HistoricoSalaJnVO historicoSalaJnVO : lista) {
				AghEspecialidades especialidade = this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(historicoSalaJnVO.getEspSeq());
				MptTipoSessao tipoSessao = this.mptTipoSessaoDAO.obterMptTipoSessaoPorSeq(historicoSalaJnVO.getTpSeq());
				
				historicoSalaJnVO.setNomeEsp(especialidade.getNomeEspecialidade());
				historicoSalaJnVO.setDescTpSessao(tipoSessao.getDescricao());
			}			
		}		
		
		return lista;
	}
	
	
	public List<MptSalas> buscarSala(Short codigoTipoSessao) {
		
		return mptSalasDAO.buscarSala(codigoTipoSessao);
	}
	

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public MptSalasJnDAO getMptSalasJnDAO() {
		return mptSalasJnDAO;
	}

	public MptSalasDAO getMptSalasDAO() {
		return mptSalasDAO;
	}

}
