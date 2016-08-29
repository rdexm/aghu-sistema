/**
 * 
 */
package br.gov.mec.aghu.parametrosistema.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AghModulosParametros;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghParametrosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.dao.AghModulosParametrosDAO;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosJnDAO;
import br.gov.mec.aghu.parametrosistema.dao.AghPerfilParametroDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * @author rcorvalao
 * 
 */
@Stateless
public class ParametroSistemaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ParametroSistemaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AghParametrosJnDAO aghParametrosJnDAO;
	
	@Inject
	private AghPerfilParametroDAO aghPerfilParametroDAO;
	
	@Inject
	private AghModulosParametrosDAO aghModulosParametrosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7085870830329386215L;

	public enum ParametroSistemaRNExceptionCode implements BusinessExceptionCode {
		ERRO_PARAMETRO_POSSUI_DEPENDENCIAS
	}

	/**
	 * Ao deletar um parâmetro deve ser verificado, antes de executar a deleção, 
	 * se o objeto a ser excluído não é chave estrangeira nas tabelas AGH_PERFIS_PARAMETRO
	 * 
	 * ORADB Oracle Forms Procedure <b>CHK_AGH_PARAMETROS</b> 
	 * rcorvalao 30/08/2010
	 * @param p
	 *  
	 */
	public void verificarDependenciaPerfilParametro(AghParametros p) throws ApplicationBusinessException {
		List<AghModulosParametros> lista = getAghPerfilParametroDAO().pesquisarDependenciaPerfilParametro(p.getSeq());
		if (!lista.isEmpty()) {
			throw new ApplicationBusinessException(ParametroSistemaRNExceptionCode.ERRO_PARAMETRO_POSSUI_DEPENDENCIAS, "Perfis Parâmetro");
		}
	}
	
	/**
	 * 
	 * ORADB Trigger <b>AGHT_PSI_ARU</b>
	 * Ao atualizar um parâmetro, se a flag de manter histórico (JN) do parâmetro for verdadeira, 
	 * deve-se verificar se algum dos campos foram modificados em relação as antigas informações do mesmo, 
	 * no caso de positivo, deve-se gravar os dados antigos do registro na tabela AGH_PARAMETROS_JN
	 * 
	 * ORADB Trigger <b>AGHT_PSI_ARD</b>
	 * Ao deletar um parâmetro, o mesmo deve ser incluído na tabela AGH_PARAMETROS_JN
	 * 
	 * rcorvalao
	 * 30/08/2010
	 * @param p
	 * @param operacao
	 */
	public void inserirJournal(AghParametros p, DominioOperacoesJournal operacao) {
		if (DominioOperacoesJournal.INS == operacao || DominioOperacoesJournal.UPD == operacao) {
			if (p.getMantemHistorico().isSim()) {
				this.inserirAghParametrosJn(p, operacao);
			}	
		} else {
			this.inserirAghParametrosJn(p, operacao);
		}
	}
	
	private void inserirAghParametrosJn(AghParametros p, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghParametrosJn jn = BaseJournalFactory.getBaseJournal(operacao, AghParametrosJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		p.asJournal(jn);
		
		AghParametrosJnDAO aghParametrosJnDAO = this.getAghParametrosJnDAO();
		aghParametrosJnDAO.persistir(jn);
		aghParametrosJnDAO.flush();
	}

	/**
	 * Ao deletar um parâmetro deve ser verificado, antes de executar a deleção, 
	 * se o objeto a ser excluído não é chave estrangeira na tabela AGH_MODULOS_PARAMETROS
	 * 
	 * ORADB Oracle Forms Procedure <b>CHK_AGH_PARAMETROS</b>
	 * rcorvalao 30/08/2010
	 * @param p
	 *  
	 */
	public void verificarDependenciaModuloParametro(AghParametros p) throws ApplicationBusinessException {
		List<AghModulosParametros> lista = getAghModulosParametrosDAO().pesquisarDependenciaModuloParametro(p.getSeq());
		if (!lista.isEmpty()) {
			throw new ApplicationBusinessException(ParametroSistemaRNExceptionCode.ERRO_PARAMETRO_POSSUI_DEPENDENCIAS, "Módulos Parâmetros");
		}
	}
	
	protected AghParametrosJnDAO getAghParametrosJnDAO(){
		return aghParametrosJnDAO;
	}
	
	protected AghPerfilParametroDAO getAghPerfilParametroDAO(){
		return aghPerfilParametroDAO;
	}
	
	private AghModulosParametrosDAO getAghModulosParametrosDAO(){
		return aghModulosParametrosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
