package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoPorGrupoDAO;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcProcedimentoPorGrupoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcProcedimentoPorGrupoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoPorGrupoDAO mbcProcedimentoPorGrupoDAO;


	private static final long serialVersionUID = -6320806656607859532L;
	
	private enum MbcProcedimentoPorGrupoRNExceptionCode implements BusinessExceptionCode {
		MBC_PROCEDIMENTO_POR_GRUPO_EXISTENTE
	}
	
	/**
	 * ORADB: MBCT_PGR_BRI, MBCT_PGR_BRU
	 * 
	 * @param mbcProcedimentoPorGrupo
	 * @throws ApplicationBusinessException
	 */
	public void persistirMbcProcedimentoPorGrupo(MbcProcedimentoPorGrupo mbcProcedimentoPorGrupo) throws ApplicationBusinessException {
		MbcProcedimentoPorGrupo existente = getMbcProcedimentoPorGrupoDAO().obterPorChavePrimaria(mbcProcedimentoPorGrupo.getId());

		if(existente != null){
			throw new ApplicationBusinessException(MbcProcedimentoPorGrupoRNExceptionCode.MBC_PROCEDIMENTO_POR_GRUPO_EXISTENTE, existente.getMbcProcedimentoCirurgicos().getDescricao());
		} else {
			mbcProcedimentoPorGrupo.setCriadoEm(new Date());
		}	

		this.getMbcProcedimentoPorGrupoDAO().persistir(mbcProcedimentoPorGrupo);
		this.getMbcProcedimentoPorGrupoDAO().flush();
		
	}
	
	public void removerMbcProcedimentoPorGrupo(MbcProcedimentoPorGrupo mbcProcedimentoPorGrupo) {
		MbcProcedimentoPorGrupo proc = this.getMbcProcedimentoPorGrupoDAO().obterPorChavePrimaria(mbcProcedimentoPorGrupo.getId());
		
		if (proc != null) {
			this.getMbcProcedimentoPorGrupoDAO().remover(proc);
			this.getMbcProcedimentoPorGrupoDAO().flush();
		}
	}
	
	protected MbcProcedimentoPorGrupoDAO getMbcProcedimentoPorGrupoDAO(){
		return mbcProcedimentoPorGrupoDAO;
	}
}