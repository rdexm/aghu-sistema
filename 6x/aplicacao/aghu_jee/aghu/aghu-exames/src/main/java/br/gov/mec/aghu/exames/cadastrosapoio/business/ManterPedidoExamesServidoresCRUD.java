package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelServidoresExameUnidDAO;
import br.gov.mec.aghu.model.AelServidoresExameUnid;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterPedidoExamesServidoresCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterPedidoExamesServidoresCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelServidoresExameUnidDAO aelServidoresExameUnidDAO;

@EJB
private IRegistroColaboradorFacade iRegistroColaboradorFacade;

@EJB
private IAghuFacade iAghuFacade;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3219502580716822777L;

	public enum ManterPedidoExamesServidoresExceptionCode implements BusinessExceptionCode {
		AEL_00444, SERV_EXA_JA_CAD;
	}

	public void persistirAelServidoresExameUnid(AelServidoresExameUnid aelServidoresExameUnid) throws ApplicationBusinessException {
		// inserir
		preInserirAelServidoresExameUnid(aelServidoresExameUnid);
		getAelServidoresExameUnidDAO().merge(aelServidoresExameUnid);
		getAelServidoresExameUnidDAO().flush();
		
	}

	/**
	 * ORADB aelk_seu_rn.rn_seup_ver_servidor
	 * @param aelServidoresExameUnid
	 *  
	 */
	protected void validaServidorConselho(AelServidoresExameUnid aelServidoresExameUnid) throws ApplicationBusinessException {
		Long servConselhoCount = obterVRapServidorConselhoPeloId(
					aelServidoresExameUnid.getId().getRapServidores().getId().getMatricula(),
					aelServidoresExameUnid.getId().getRapServidores().getId().getVinCodigo(),
					aelServidoresExameUnid.getId().getAelUnfExecutaExames().getId().getEmaExaSigla(),
					aelServidoresExameUnid.getId().getAelUnfExecutaExames().getId().getEmaManSeq()
				);
		
		if(servConselhoCount == null || servConselhoCount == 0){
			throw new ApplicationBusinessException(ManterPedidoExamesServidoresExceptionCode.AEL_00444);
		}
	}
	
	private Long obterVRapServidorConselhoPeloId(int matricula, short vinCodigo, String emaExaSigla, Integer emaExaManSeq) {
		return getRegistroColaboradorFacade().obterVRapServidorConselhoExamePeloId(matricula, vinCodigo, emaExaSigla, emaExaManSeq);
	}

	/**
	 * ORADB AELT_PUS_BRI
	 * @param aelServidoresExameUnid
	 * @throws ApplicationBusinessException
	 */
	protected void preInserirAelServidoresExameUnid(AelServidoresExameUnid aelServidoresExameUnid) throws ApplicationBusinessException {
		aelServidoresExameUnid.setCriadoEm(new Date());
		validaServidorConselho(aelServidoresExameUnid);
		verificaServidorExmameJaExistente(aelServidoresExameUnid);
	}
	
	/**
	 * @param aelServidoresExameUnid
	 * @throws ApplicationBusinessException
	 */
	protected void verificaServidorExmameJaExistente(AelServidoresExameUnid aelServidoresExameUnid)throws ApplicationBusinessException {

		AelServidoresExameUnid exaServExistente = getAelServidoresExameUnidDAO().buscarAelServidoresExameUnidPorId(
				aelServidoresExameUnid.getId().getAelUnfExecutaExames().getId().getEmaExaSigla(),
				aelServidoresExameUnid.getId().getAelUnfExecutaExames().getId().getEmaManSeq(), 
				aelServidoresExameUnid.getId().getAelUnfExecutaExames().getId().getUnfSeq().getSeq(), 
				aelServidoresExameUnid.getId().getRapServidores().getId().getMatricula(),
				aelServidoresExameUnid.getId().getRapServidores().getId().getVinCodigo());

		if(exaServExistente != null){
			throw new ApplicationBusinessException(ManterPedidoExamesServidoresExceptionCode.SERV_EXA_JA_CAD);
		}
	}

	public List<RapServidores> buscaListRapServidoresVAelPessoaServidor(Object pesquisa, String emaExaSigla, Integer emaManSeq) throws ApplicationBusinessException {
		return getRegistroColaboradorFacade().pesquisarServidorSuggestion(pesquisa, emaExaSigla, emaManSeq);
	}
	
	public void removerAelServidoresExameUnid(String ufeEmaExaSigla,Integer ufeEmaManSeq,Short ufeUnfSeq, Integer serMatricula, Short serVinCodigo) throws ApplicationBusinessException {
		AelServidoresExameUnid servidorRemover = getAelServidoresExameUnidDAO().buscarAelServidoresExameUnidPorId(ufeEmaExaSigla,ufeEmaManSeq,ufeUnfSeq,serMatricula,serVinCodigo);
		getAelServidoresExameUnidDAO().remover(servidorRemover);
		getAelServidoresExameUnidDAO().flush();
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}
	
	protected AelServidoresExameUnidDAO getAelServidoresExameUnidDAO(){
		return aelServidoresExameUnidDAO;
	}

}
