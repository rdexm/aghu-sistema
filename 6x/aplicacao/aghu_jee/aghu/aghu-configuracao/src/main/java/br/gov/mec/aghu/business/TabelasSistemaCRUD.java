package br.gov.mec.aghu.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghTabelasSistemaDAO;
import br.gov.mec.aghu.model.AghTabelasSistema;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TabelasSistemaCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TabelasSistemaCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghTabelasSistemaDAO aghTabelasSistemaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1973313225773709548L;
	
	private enum TabelasSistemaCRUDExceptionCode implements BusinessExceptionCode {
		TABELA_EXISTENTE, MENSAGEM_ERRO_PERIODICIDADE_NUMERO_NAO_INFORMADO, MENSAGEM_ERRO_PERIODICIDADE_PERIODO_NAO_INFORMADO
	}
	
	/**
	 * Método para incluir/atualizar um objeto de cidade
	 */
	public void persistirTabelaSistema(AghTabelasSistema tabelaSistema) throws BaseException {
		
		this.verificarTabelaJaExistente(tabelaSistema);
		
		if(tabelaSistema.getPeriodicidadeNro() != null && 
				tabelaSistema.getPeriodicidadeTipo() == null){
			throw new ApplicationBusinessException (
					TabelasSistemaCRUDExceptionCode.MENSAGEM_ERRO_PERIODICIDADE_PERIODO_NAO_INFORMADO);
			
		} else if(tabelaSistema.getPeriodicidadeTipo() != null && 
					tabelaSistema.getPeriodicidadeNro() == null){
			throw new ApplicationBusinessException (
					TabelasSistemaCRUDExceptionCode.MENSAGEM_ERRO_PERIODICIDADE_NUMERO_NAO_INFORMADO);
		}
		
		if (tabelaSistema.getSeq() == null) {
			getAghTabelasSistemaDAO().persistir(tabelaSistema);
		}
		else{
			this.getAghTabelasSistemaDAO().atualizar(tabelaSistema);			
		}
	}
	
	/**
	 * Verifica se já não existe uma tabela cadastrada com o nome informado
	 * @param tabelaSistema
	 *  
	 */
	private void verificarTabelaJaExistente(AghTabelasSistema tabelaSistema) throws ApplicationBusinessException{
		if (getAghTabelasSistemaDAO().verificarTabelaExistente(tabelaSistema.getSeq(), tabelaSistema.getNome())){
			throw new ApplicationBusinessException (
					TabelasSistemaCRUDExceptionCode.TABELA_EXISTENTE);
		}
	}

	public void removerTabelaSistema(AghTabelasSistema tabelaSistema) throws BaseException {
		AghTabelasSistema tabela = getAghTabelasSistemaDAO().obterPorChavePrimaria(tabelaSistema.getSeq());
		if (tabela != null) {
			getAghTabelasSistemaDAO().remover(tabela);
			getAghTabelasSistemaDAO().flush();
		}
	}
	
	private AghTabelasSistemaDAO getAghTabelasSistemaDAO(){
		return aghTabelasSistemaDAO;
	}

}
