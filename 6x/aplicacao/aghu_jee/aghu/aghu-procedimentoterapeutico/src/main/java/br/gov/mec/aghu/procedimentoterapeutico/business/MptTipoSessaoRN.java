package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTipoSessaoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoJnDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HistoricoSessaoJnVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MptTipoSessaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MptTipoSessaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private MptDiaTipoSessaoRN mptDiaTipoSessaoRN;
	
	@EJB
	private MptTurnoTipoSessaoRN mptTurnoTipoSessaoRN;
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
	
	@Inject
	private MptTipoSessaoJnDAO mptTipoSessaoJnDAO;
	
	@Inject
	private MptCaracteristicaTipoSessaoDAO mptCaracteristicaTipoSessaoDAO;

	private static final long serialVersionUID = 2797194818319163103L;
	
	public enum MtpTipoSessaoRNExceptionCode implements BusinessExceptionCode {
		VINCULO_EXISTE
	}
	
	public void inserirMptTipoSessao(MptTipoSessao mptTipoSessao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		mptTipoSessao.setIndApac(true);
		mptTipoSessao.setIndFrequencia(true);
		mptTipoSessao.setIndConsentimento(true);
		mptTipoSessao.setServidor(servidorLogado);
		mptTipoSessao.setCriadoEm(new Date());
		mptTipoSessao.setIndSituacao(DominioSituacao.A);	
		getMptTipoSessaoDAO().persistir(mptTipoSessao);
	}
	
	public void atualizarMptTipoSessao(MptTipoSessao mptTipoSessao) throws ApplicationBusinessException {
		MptTipoSessao mptTipoSessaoOriginal = this.getMptTipoSessaoDAO().obterOriginal(mptTipoSessao.getSeq());
		this.inserirJournalMptTipoSessao(mptTipoSessaoOriginal, DominioOperacoesJournal.UPD);
		
		this.getMptTipoSessaoDAO().merge(mptTipoSessao);
	}
	
	public void excluirMptTipoSessao(MptTipoSessao mptTipoSessao) throws ApplicationBusinessException {
		MptTipoSessao mptTipoSessaoOriginal = this.getMptTipoSessaoDAO().obterOriginal(mptTipoSessao.getSeq());
		this.inserirJournalMptTipoSessao(mptTipoSessaoOriginal, DominioOperacoesJournal.DEL);
		// Exclui todos os registros relacionados na tabela MPT_DIA_TIPO_SESSAO
		this.getMptDiaTipoSessaoRN().excluirMptDiaTipoSessao(mptTipoSessao.getSeq());
		
		// Exclui todos os registros relacionados na tabela MPT_TURNO_TIPO_SESSAO
		this.getMptTurnoTipoSessaoRN().excluirMptTurnoTipoSessao(mptTipoSessao.getSeq());
		
		validarConstraintsMptTipoSessao(mptTipoSessao.getSeq());
		
		MptTipoSessao excluir = this.getMptTipoSessaoDAO().obterPorChavePrimaria(mptTipoSessao.getSeq());
		
		this.getMptTipoSessaoDAO().remover(excluir);
	}
	
	public void validarConstraintsMptTipoSessao(Short tpsSeq) throws ApplicationBusinessException{
		if(mptCaracteristicaTipoSessaoDAO.validarExclusaoTipoSessao(tpsSeq) > 0){
			throw new ApplicationBusinessException(MtpTipoSessaoRNExceptionCode.VINCULO_EXISTE);
		}		
	}
	
	private void inserirJournalMptTipoSessao(MptTipoSessao mptTipoSessaoOriginal, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		
		MptTipoSessaoJn mptTipoSessaoJn = BaseJournalFactory.getBaseJournal(
				operacao, MptTipoSessaoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		mptTipoSessaoJn.setIndApac(true);
		mptTipoSessaoJn.setIndConsentimento(true);
		mptTipoSessaoJn.setIndFrequencia(true);
		mptTipoSessaoJn.setSeq(mptTipoSessaoOriginal.getSeq());
		mptTipoSessaoJn.setDescricao(mptTipoSessaoOriginal.getDescricao());
		mptTipoSessaoJn.setServidor(mptTipoSessaoOriginal.getServidor());
		mptTipoSessaoJn.setUnidadeFuncional(mptTipoSessaoOriginal.getUnidadeFuncional());
		mptTipoSessaoJn.setTipoAgenda(mptTipoSessaoOriginal.getTipoAgenda());
		mptTipoSessaoJn.setTempoFixo(mptTipoSessaoOriginal.getTempoFixo());
		mptTipoSessaoJn.setTempoDisponivel(mptTipoSessaoOriginal.getTempoDisponivel());
		mptTipoSessaoJn.setIndApac(mptTipoSessaoOriginal.getIndApac());
		mptTipoSessaoJn.setIndConsentimento(mptTipoSessaoOriginal.getIndConsentimento());
		mptTipoSessaoJn.setIndFrequencia(mptTipoSessaoOriginal.getIndFrequencia());
		mptTipoSessaoJn.setIndSituacao(mptTipoSessaoOriginal.getIndSituacao());
		this.getMptTipoSessaoJnDAO().persistir(mptTipoSessaoJn);
	}
	
	public List<MptTipoSessao> buscarTipoSessao() {
		
		return mptTipoSessaoDAO.buscarTipoSessao();
	}

	public List<HistoricoSessaoJnVO> obterHistoricoSessaoJn(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short sessaoSeq){
		String descricaoTipoSessao = mptTipoSessaoDAO.obterDescricaoPorSeq(sessaoSeq);
		List<HistoricoSessaoJnVO> lista = mptTipoSessaoJnDAO.obterHistoricoSessaoJn(firstResult, maxResults, orderProperty, asc, sessaoSeq);
		for (HistoricoSessaoJnVO vo : lista) {
			vo.setDescricao(descricaoTipoSessao);
		}
		return lista;
	}

	public MptTipoSessao obterTipoSessaoComUnidadeFuncionalPreenchida(Short sessaoSeq){
		MptTipoSessao tipoSessao = mptTipoSessaoDAO.obterMptTipoSessaoPorSeq(sessaoSeq);
		return tipoSessao;
	}
	
	public String buscarNomeResponsavel(String nome1, String nome2) {
		if(nome1 != null){
			if(!nome1.isEmpty()){
				return nome1;				
			}else{
				return nome2;
			}			
		}else{
			return nome2;
		}
	}	
	
	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public MptDiaTipoSessaoRN getMptDiaTipoSessaoRN() {
		return mptDiaTipoSessaoRN;
	}

	public MptTurnoTipoSessaoRN getMptTurnoTipoSessaoRN() {
		return mptTurnoTipoSessaoRN;
	}

	public MptTipoSessaoDAO getMptTipoSessaoDAO() {
		return mptTipoSessaoDAO;
	}

	public MptTipoSessaoJnDAO getMptTipoSessaoJnDAO() {
		return mptTipoSessaoJnDAO;
	}
}
