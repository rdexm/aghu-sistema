	package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipPaises;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.paciente.dao.AipPaisesDAO;
import br.gov.mec.aghu.paciente.dao.AipUfsDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PaisCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PaisCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipUfsDAO aipUfsDAO;

@Inject
private AipPaisesDAO aipPaisesDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7463645432797332476L;

	private enum PaisExceptionCode implements BusinessExceptionCode {
		AIP_00039, AIP_00040, UFS_DEPENDENTES;
	}
	
	public void persistirPais(AipPaises pais, boolean edicao) throws ApplicationBusinessException {
		this.existePaisComSiglaOuNome(pais, edicao);
		if(edicao){
			getAipPaisesDAO().merge(pais);
		}else{
			this.salvarPais(pais);
		}
	}
	
	private void existePaisComSiglaOuNome(AipPaises pais , boolean edicao)throws ApplicationBusinessException{
		AipPaises p = null;
		if(!edicao){
			p = getAipPaisesDAO().obterPorChavePrimaria(pais.getSigla());
			if(p != null){
				throw new ApplicationBusinessException(PaisExceptionCode.AIP_00040);
			}
		}
		p = this.getAipPaisesDAO().obterPaisPorNome(pais.getNome());
		if((p != null && !edicao ) ||(edicao && p != null && !p.getSigla().equalsIgnoreCase(pais.getSigla()))){
			throw new ApplicationBusinessException(PaisExceptionCode.AIP_00039);
		}
		 
	}
	
	private void salvarPais(AipPaises pais){
		getAipPaisesDAO().persistir(pais);
	}
	
	public void removerPais(String sigla) throws ApplicationBusinessException{
		AipPaises pais = getAipPaisesDAO().obterPorChavePrimaria(sigla);
		
		if (pais == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.verificarUfsDependentes(pais);
		getAipPaisesDAO().remover(pais);
	}
	
	public void verificarUfsDependentes(AipPaises pais) throws ApplicationBusinessException{
		List<AipUfs> listaUfs = getAipUfsDAO().pesquisarUfsPorPais(pais);
		if (!listaUfs.isEmpty()){
			throw new ApplicationBusinessException(PaisExceptionCode.UFS_DEPENDENTES);
		}
	}
	
	protected AipPaisesDAO getAipPaisesDAO(){
		return aipPaisesDAO;
	}
	
	protected AipUfsDAO getAipUfsDAO(){
		return aipUfsDAO;
	}
}
