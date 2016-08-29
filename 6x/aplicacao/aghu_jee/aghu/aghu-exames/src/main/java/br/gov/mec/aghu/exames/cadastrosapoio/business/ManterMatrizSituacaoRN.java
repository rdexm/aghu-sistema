package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelAutorizacaoAlteracaoSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.model.AelAutorizacaoAlteracaoSituacao;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterMatrizSituacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterMatrizSituacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAutorizacaoAlteracaoSituacaoDAO aelAutorizacaoAlteracaoSituacaoDAO;

@Inject
private AelMatrizSituacaoDAO aelMatrizSituacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2881863380328678696L;

	public enum ManterMatrizSituacaoRNExceptionCode implements BusinessExceptionCode {
		AEL_00479,
		AEL_00480,
		AEL_00481,
		AEL_00482;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	public AelMatrizSituacao inserir(AelMatrizSituacao matriz) throws ApplicationBusinessException {
	

		//Regras pré-insert
		preInsert(matriz);

		//Insert
		getAelMatrizSituacaoDAO().persistir(matriz);
		
		//Insert mapeamento perfil
		for(AelAutorizacaoAlteracaoSituacao perfilMapeado : matriz.getAutorizacaoAlteracaoSituacoes()) {
			perfilMapeado.getId().setMasSeq(matriz.getSeq());
			getAelAutorizacaoAlteracaoSituacaoDAO().persistir(perfilMapeado);
		}
		
		return matriz;
	}

	/**
	 * @ORADB Trigger AELT_MAS_BRI
	 * 
	 */
	protected void preInsert(AelMatrizSituacao matriz) throws ApplicationBusinessException {
		//Verifica se código da situação final existe.
		if(matriz.getSituacaoItemSolicitacaoPara() == null) {
			ManterMatrizSituacaoRNExceptionCode.AEL_00480.throwException();
		}
		
		//Verifica se código da situação inicial e final são diferentes
		if((matriz.getSituacaoItemSolicitacao() == null && matriz.getSituacaoItemSolicitacaoPara() == null)
				|| (matriz.getSituacaoItemSolicitacao() != null
						&& matriz.getSituacaoItemSolicitacao().equals(matriz.getSituacaoItemSolicitacaoPara()))
				) {
			ManterMatrizSituacaoRNExceptionCode.AEL_00479.throwException();
		}
		
		//Verifica se a situação inicial está ativa
		if(matriz.getSituacaoItemSolicitacao() != null 
				&& matriz.getSituacaoItemSolicitacao().getIndSituacao() != DominioSituacao.A) {
			ManterMatrizSituacaoRNExceptionCode.AEL_00481.throwException();
		}
		
		//Verifica se a situação final está ativa
		if(matriz.getSituacaoItemSolicitacaoPara().getIndSituacao() != DominioSituacao.A) {
			ManterMatrizSituacaoRNExceptionCode.AEL_00482.throwException();
		}
	}

	public AelMatrizSituacao atualizar(AelMatrizSituacao matriz) throws ApplicationBusinessException {
		//Recupera o objeto antigo
		AelMatrizSituacao matrizOld = getAelMatrizSituacaoDAO().obterOriginal(matriz.getSeq());

		//Regras pré-update
		preUpdate(matrizOld, matriz);

		//Atualização na lista de matrizes
		List<AelAutorizacaoAlteracaoSituacao> perfisMapeadosBD = getAelAutorizacaoAlteracaoSituacaoDAO().listarPorMatriz(matriz);

		List<AelAutorizacaoAlteracaoSituacao> novos = new ArrayList<AelAutorizacaoAlteracaoSituacao>();
		for(AelAutorizacaoAlteracaoSituacao perfilMapeado : matriz.getAutorizacaoAlteracaoSituacoes()) {
			boolean novo = true;
			for(AelAutorizacaoAlteracaoSituacao perfilMapeadoBD : perfisMapeadosBD) {
				if(perfilMapeado.getId().equals(perfilMapeadoBD.getId())) {
					novo = false;
					break;
				}
			}
			if(novo){
				novos.add(perfilMapeado);
			}
		}

		List<AelAutorizacaoAlteracaoSituacao> removidos = new ArrayList<AelAutorizacaoAlteracaoSituacao>();
		for(AelAutorizacaoAlteracaoSituacao perfilMapeadoBD : perfisMapeadosBD) {
			boolean removido = true;
			for(AelAutorizacaoAlteracaoSituacao perfilMapeado : matriz.getAutorizacaoAlteracaoSituacoes()) {
				if(perfilMapeado.getId().equals(perfilMapeadoBD.getId())) {
					removido = false;
					break;
				}
			}
			if(removido){
				removidos.add(perfilMapeadoBD);
			}
		}

		for(AelAutorizacaoAlteracaoSituacao perfilMapeado : novos) {
			//Inserts perfis mapeados
			perfilMapeado.getId().setMasSeq(matriz.getSeq());
			getAelAutorizacaoAlteracaoSituacaoDAO().persistir(perfilMapeado);
		}
		for(AelAutorizacaoAlteracaoSituacao perfilMapeado : removidos) {
			//Deletes perfis mapeados
			getAelAutorizacaoAlteracaoSituacaoDAO().remover(perfilMapeado);
		}
		
		return matriz;
	}

	/**
	 * @ORADB Trigger AELT_MAS_BRU
	 * 
	 */
	protected void preUpdate(AelMatrizSituacao matrizOld, AelMatrizSituacao matrizNew) throws ApplicationBusinessException {
		//Verifica se a situação inicial ou final foram alteradas
		if((matrizOld.getSituacaoItemSolicitacao() == null && matrizNew.getSituacaoItemSolicitacao() != null)	
			|| (matrizOld.getSituacaoItemSolicitacao() != null && matrizNew.getSituacaoItemSolicitacao() == null)
			|| (matrizOld.getSituacaoItemSolicitacao() != null && !matrizOld.getSituacaoItemSolicitacao().equals(matrizNew.getSituacaoItemSolicitacao()))
			|| !matrizOld.getSituacaoItemSolicitacaoPara().equals(matrizNew.getSituacaoItemSolicitacaoPara())) {
			preInsert(matrizNew);
		}
	}
	
	public void remover(Short seq) {
		//Recupera o objeto
		AelMatrizSituacao matriz = getAelMatrizSituacaoDAO().obterPorId(seq);
		
		getAelMatrizSituacaoDAO().remover(matriz);
	}

	//--------------------------------------------------
	//Getters
	
	protected AelMatrizSituacaoDAO getAelMatrizSituacaoDAO() {
		return aelMatrizSituacaoDAO;
	}
	
	protected AelAutorizacaoAlteracaoSituacaoDAO getAelAutorizacaoAlteracaoSituacaoDAO() {
		return aelAutorizacaoAlteracaoSituacaoDAO;
	}
}
