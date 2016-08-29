package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelAutorizacaoAlteracaoSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.model.AelAutorizacaoAlteracaoSituacao;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterSituacaoExameRN extends BaseBusiness {

	@EJB
	private ManterMatrizSituacaoRN manterMatrizSituacaoRN;
	
	private static final Log LOG = LogFactory.getLog(ManterSituacaoExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
	@Inject
	private AelAutorizacaoAlteracaoSituacaoDAO aelAutorizacaoAlteracaoSituacaoDAO;
	
	@Inject
	private AelMatrizSituacaoDAO aelMatrizSituacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 119248014126419879L;

	public enum ManterSituacaoExameRNExceptionCode implements BusinessExceptionCode {
		AEL_00346,
		AEL_00369,
		AEL_00199;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	public AelSitItemSolicitacoes inserir(AelSitItemSolicitacoes situacao) throws ApplicationBusinessException {
	

		//Regras pré-insert
		preInsert(situacao);
		
		//Insert
		getAelSitItemSolicitacoesDAO().persistir(situacao);

		//Insert das Matrizes
		for(AelMatrizSituacao matriz : situacao.getMatrizesSituacao()) {
			getManterMatrizSituacaoRN().inserir(matriz);
		}
		
		return situacao;
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB Trigger AELT_SIT_BRI
	 * 
	 */
	protected void preInsert(AelSitItemSolicitacoes situacao) throws ApplicationBusinessException {
		//Verifica unicidade do código
		if(getAelSitItemSolicitacoesDAO().obterPeloId(situacao.getCodigo()) != null) {
			ManterSituacaoExameRNExceptionCode.AEL_00199.throwException();
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Atribui o usuário logado
		situacao.setServidor(servidorLogado);
		
		//Atribui a situação a data de criação como o dia corrente
		situacao.setCriadoEm(new Date());
	}

	public AelSitItemSolicitacoes atualizar(AelSitItemSolicitacoes situacao) throws ApplicationBusinessException {
		//Recupera o objeto antigo
		AelSitItemSolicitacoes situacaoOld = getAelSitItemSolicitacoesDAO().obterOriginal(situacao.getCodigo());

		//Regras pré-update situação
		preUpdate(situacaoOld, situacao);
		
		//Verifica os dados alterados
		verificarAlteracoesSituacao(situacao);
		
		//Atualização na lista de matrizes
		List<AelMatrizSituacao> matrizesBD = getAelMatrizSituacaoDAO().listarPorSituacaoItemSolicitacaoPara(situacao);

		List<AelMatrizSituacao> novos = new ArrayList<AelMatrizSituacao>();
		for(AelMatrizSituacao matriz : situacao.getMatrizesSituacao()) {
			if(matriz.getSeq() == null) {
				novos.add(matriz);
			}
		}

		List<AelMatrizSituacao> removidos = new ArrayList<AelMatrizSituacao>();
		List<AelMatrizSituacao> atualizados = new ArrayList<AelMatrizSituacao>();
		for(AelMatrizSituacao matrizBD : matrizesBD) {
			boolean removido = true;
			for(AelMatrizSituacao matriz : situacao.getMatrizesSituacao()) {
				if(matriz.getSeq() != null && matriz.getSeq().equals(matrizBD.getSeq())) {
					removido = false;
					if(CoreUtil.modificados(matriz.getSituacaoItemSolicitacao(), matrizBD.getSituacaoItemSolicitacao())
						|| CoreUtil.modificados(matriz.getSituacaoItemSolicitacaoPara(), matrizBD.getSituacaoItemSolicitacaoPara())
						|| CoreUtil.modificados(matriz.getIndExigeMotivoCanc(), matrizBD.getIndExigeMotivoCanc()) 
						|| perfisAlterados(matriz.getAutorizacaoAlteracaoSituacoes(), getAelAutorizacaoAlteracaoSituacaoDAO().listarPorMatriz(matrizBD))) {
						atualizados.add(matriz);
					}
					
					break;
				}
			}
			if(removido){
				removidos.add(matrizBD);
			}
		}

		for(AelMatrizSituacao matriz : novos) {
			//Inserts matrizes
			getManterMatrizSituacaoRN().inserir(matriz);
		}
		for(AelMatrizSituacao matriz : removidos) {
			//Deletes matrizes
			getManterMatrizSituacaoRN().remover(matriz.getSeq());
		}
		for(AelMatrizSituacao matriz : atualizados) {
			//Updates matrizes
			AelMatrizSituacao matrizSessao = getAelMatrizSituacaoDAO().obterPorId(matriz.getSeq());
			
			matrizSessao.setSituacaoItemSolicitacao(matriz.getSituacaoItemSolicitacao());
			matrizSessao.setSituacaoItemSolicitacaoPara(matriz.getSituacaoItemSolicitacaoPara());
			matrizSessao.setIndExigeMotivoCanc(matriz.getIndExigeMotivoCanc());
			matrizSessao.setAutorizacaoAlteracaoSituacoes(matriz.getAutorizacaoAlteracaoSituacoes());
			
			getManterMatrizSituacaoRN().atualizar(matrizSessao);
		}

		return situacao;
	}

	private void verificarAlteracoesSituacao(AelSitItemSolicitacoes situacao) {
		AelSitItemSolicitacoes situacaoTeste = getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(situacao.getCodigo());
		if(CoreUtil.modificados(situacao.getIndSituacao(), situacaoTeste.getIndSituacao())
				|| CoreUtil.modificados(situacao.getIndPermiteManterResultadoDominio(), situacaoTeste.getIndPermiteManterResultadoDominio())
				|| CoreUtil.modificados(situacao.getIndMostraSolicitarExamesDominio(), situacaoTeste.getIndMostraSolicitarExamesDominio())
				|| CoreUtil.modificados(situacao.getIndAlertaExameJaSolicDominio(), situacaoTeste.getIndAlertaExameJaSolicDominio())){
			
			situacaoTeste.setIndSituacao(situacao.getIndSituacao());
			situacaoTeste.setIndPermiteManterResultadoDominio(situacao.getIndPermiteManterResultadoDominio());
			situacaoTeste.setIndMostraSolicitarExamesDominio(situacao.getIndMostraSolicitarExamesDominio());
			situacaoTeste.setIndAlertaExameJaSolicDominio(situacao.getIndAlertaExameJaSolicDominio());
			getAelSitItemSolicitacoesDAO().persistir(situacaoTeste);
		}
	}
	
	private boolean perfisAlterados(Collection<AelAutorizacaoAlteracaoSituacao> novos, Collection<AelAutorizacaoAlteracaoSituacao> antigos) {
		if(novos.size() != antigos.size()) {
			return true;
		}
		
		for(AelAutorizacaoAlteracaoSituacao perfilNovo : novos) {
			boolean achado = false;
			for(AelAutorizacaoAlteracaoSituacao perfilVelho : antigos) {
				if(perfilNovo.getPerfil().equals(perfilVelho.getPerfil())) {
					achado = true;
					break;
				}
			}
			if(!achado) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @ORADB Trigger AELT_SIT_BRU
	 * 
	 */
	protected void preUpdate(AelSitItemSolicitacoes situacaoOld, AelSitItemSolicitacoes situacaoNew) throws ApplicationBusinessException {
		//Verifica se a descrição é diferente da já existente
		if(!situacaoNew.getDescricao().trim().equalsIgnoreCase(situacaoOld.getDescricao().trim())) {
			ManterSituacaoExameRNExceptionCode.AEL_00346.throwException();
		}

		//Verifica se a data de criação é diferente da já existente
		if(!situacaoNew.getCriadoEm().equals(situacaoOld.getCriadoEm())) {
			ManterSituacaoExameRNExceptionCode.AEL_00369.throwException();
		}

		//Verifica se o servidor é diferente da já existente
		if(!situacaoNew.getServidor().getId().equals(situacaoOld.getServidor().getId())) {
			ManterSituacaoExameRNExceptionCode.AEL_00369.throwException();
		}
	}

	//--------------------------------------------------
	//Getters
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}
	
	protected AelMatrizSituacaoDAO getAelMatrizSituacaoDAO() {
		return aelMatrizSituacaoDAO;
	}
	
	protected ManterMatrizSituacaoRN getManterMatrizSituacaoRN() {
		return manterMatrizSituacaoRN;
	}
	
	protected AelAutorizacaoAlteracaoSituacaoDAO getAelAutorizacaoAlteracaoSituacaoDAO() {
		return aelAutorizacaoAlteracaoSituacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
