package br.gov.mec.aghu.casca.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.dao.PerfilApiDAO;
import br.gov.mec.aghu.casca.dao.PerfilApiJnDAO;
import br.gov.mec.aghu.casca.model.PerfilApi;
import br.gov.mec.aghu.casca.model.PerfilApiJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PerfilApiON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(PerfilApiON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private PerfilApiJnDAO perfilApiJnDAO;
	
	@Inject
	private PerfilApiDAO perfilApiDAO;
	
	@EJB
	TokenApiON tokenApiOn;
	
	private static final long serialVersionUID = -269643020564432693L;
	
	protected enum PerfilApiONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_PERFILNAO_INFORMADO, 
		CASCA_PERFIL_NOME_EXISTENTE
	}

	public void salvarPerfilApi(PerfilApi perfilApi) throws ApplicationBusinessException {
		if (perfilApi == null) {
			throw new ApplicationBusinessException(
					PerfilApiONExceptionCode.CASCA_MENSAGEM_PERFILNAO_INFORMADO);
		}
				
		if (perfilApi.getId() == null) {
			verificarPerfilNomeExistente(perfilApi);			
			
			
			perfilApi.setDataCriacao(new Date());
			perfilApiDAO.persistir(perfilApi);
			
			PerfilApiJn perfilApiJn = this.criarJournal(perfilApi, DominioOperacoesJournal.INS);
			perfilApiJnDAO.persistir(perfilApiJn);

		} else {
			PerfilApi perfilOriginal = perfilApiDAO.obterOriginal(perfilApi);
			PerfilApi pApi = perfilApiDAO.obterPorChavePrimaria(perfilApi.getId());
			if (pApi != null) {
				
				pApi.setNome(perfilApi.getNome());
				pApi.setDescricao(perfilApi.getDescricao());
				pApi.setDescricaoResumida(perfilApi.getDescricaoResumida());
				pApi.setDataCriacao(perfilApi.getDataCriacao());
				pApi.setSituacao(perfilApi.getSituacao());
				
				PerfilApiJn perfilApiJn = null;
				boolean alterado = this.alterado(pApi, perfilOriginal);
							
				perfilApiDAO.persistir(pApi);
				
				if(alterado){
					perfilApiJn = this.criarJournal(perfilOriginal, DominioOperacoesJournal.UPD);
					perfilApiJnDAO.persistir(perfilApiJn);
				}
			}
		}
	}
	
	private void verificarPerfilNomeExistente(PerfilApi usuarioApi) throws ApplicationBusinessException {
		Long qtd = perfilApiDAO.pesquisarPerfilApiCount(usuarioApi.getNome(), null, null);
		if (qtd > 0L){
			throw new ApplicationBusinessException(
					PerfilApiONExceptionCode.CASCA_PERFIL_NOME_EXISTENTE);
		}		
	}

	private boolean alterado(PerfilApi perfil, PerfilApi perfilOriginal){
		if(perfil != null && perfilOriginal != null){
			if(CoreUtil.modificados(perfil.getNome(), perfil.getNome()) ||
				CoreUtil.modificados(perfil.getDescricao(), perfilOriginal.getDescricao()) ||
				CoreUtil.modificados(perfil.getDescricaoResumida(), perfilOriginal.getDescricaoResumida()) ||
				CoreUtil.modificados(perfil.getSituacao(), perfilOriginal.getSituacao())){
				return true;
			}
			return false;
		}
		return false;
	}
	
	private PerfilApiJn criarJournal(PerfilApi perfilApi, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		 
		PerfilApiJn perfilJn = BaseJournalFactory.getBaseJournal(operacao, PerfilApiJn.class, servidorLogado.getUsuario());
		perfilJn.setId(perfilApi.getId());
		perfilJn.setNome(perfilApi.getNome());
		perfilJn.setDescricao(perfilApi.getDescricao());
		perfilJn.setDescricaoResumida(perfilApi.getDescricaoResumida());
		perfilJn.setSituacao(perfilApi.getSituacao());
		perfilJn.setDataCriacao(perfilApi.getDataCriacao());
		return perfilJn;
	}
}