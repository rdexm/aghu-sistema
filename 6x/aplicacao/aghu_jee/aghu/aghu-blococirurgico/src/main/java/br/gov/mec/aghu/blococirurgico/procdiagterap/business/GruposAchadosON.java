package br.gov.mec.aghu.blococirurgico.procdiagterap.business;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtAchadoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtGrupoDAO;
import br.gov.mec.aghu.model.PdtAchado;
import br.gov.mec.aghu.model.PdtAchadoId;
import br.gov.mec.aghu.model.PdtGrupo;
import br.gov.mec.aghu.model.PdtGrupoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GruposAchadosON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GruposAchadosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtAchadoDAO pdtAchadoDAO;

	@Inject
	private PdtGrupoDAO pdtGrupoDAO;


	@EJB
	private GruposAchadosRN gruposAchadosRN;

	private static final long serialVersionUID = 5722305668331041985L;
	 
	protected enum GruposAchadosONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DESCRICAO_GRUPOS_ACHADOS_GRUPO_JA_CADASTRADA, MENSAGEM_DESCRICAO_GRUPOS_ACHADOS_ACHADO_JA_CADASTRADA
		;
	}
	
	public String gravarPdtGrupo(PdtGrupo grupo, Integer dptSeq) throws ApplicationBusinessException {
		final PdtGrupoDAO pdtGrupoDAO = getPdtGrupoDAO();
		
		if(grupo.getId() == null){
			/**
			 * PROCEDURE CHK_PDT_DGR_UK
			 */
			if(pdtGrupoDAO.obterPdtGrupoPorDescricao(grupo.getDescricao(), dptSeq) != null){
				throw new ApplicationBusinessException(GruposAchadosONExceptionCode.MENSAGEM_DESCRICAO_GRUPOS_ACHADOS_GRUPO_JA_CADASTRADA);
			}
			getGruposAchadosRN().setarPdtGrupoCriadoEm(grupo);
			getGruposAchadosRN().setarPdtGrupoServidorLogado(grupo);
			
			grupo.setId(new PdtGrupoId(dptSeq, pdtGrupoDAO.nextSeqpPdtGrupo(dptSeq))); 
			getPdtGrupoDAO().persistir(grupo);
			return "MENSAGEM_GRUPOS_ACHADOS_GRUPO_INSERCAO_COM_SUCESSO";			
		}else{
			getGruposAchadosRN().setarPdtGrupoServidorLogado(grupo);
			getGruposAchadosRN().verificarDescricaoAlteradaPdtGrupo(grupo);
			getPdtGrupoDAO().atualizar(grupo);
			getGruposAchadosRN().posUpdatePdtGrupo(grupo);
			return "MENSAGEM_GRUPOS_ACHADOS_GRUPO_ALTERACAO_COM_SUCESSO";
		}
	}
	
	public String gravarPdtAchado(PdtGrupo grupo, PdtAchado achado) throws ApplicationBusinessException {
		final PdtAchadoDAO pdtAchadoDAO = getPdtAchadoDAO();
		
		if(achado.getId() == null){
			/**
			 * PROCEDURE CHK_PDT_DGR_UK1
			 */
			if(pdtAchadoDAO.obterPdtAchadoPorDescricao(achado.getDescricao(),grupo.getId().getDptSeq(), grupo.getId().getSeqp()) != null){
				throw new ApplicationBusinessException(GruposAchadosONExceptionCode.MENSAGEM_DESCRICAO_GRUPOS_ACHADOS_ACHADO_JA_CADASTRADA);
			}
			getGruposAchadosRN().setarPdtAchadoCriadoEm(achado);
			getGruposAchadosRN().setarPdtAchadoServidorLogado(achado);
			
			achado.setId(new PdtAchadoId(grupo.getId().getDptSeq(), grupo.getId().getSeqp(), 
					pdtAchadoDAO.nextSeqpPdtAchado(grupo.getId().getDptSeq(), grupo.getId().getSeqp()))); 
			pdtAchadoDAO.persistir(achado);
			return "MENSAGEM_GRUPOS_ACHADOS_ACHADO_INSERCAO_COM_SUCESSO";			
		}else{
			getGruposAchadosRN().setarPdtAchadoServidorLogado(achado);
			getGruposAchadosRN().verificarDescricaoAlteradaPdtAchado(achado);
			pdtAchadoDAO.atualizar(achado);
			getGruposAchadosRN().posUpdatePdtAchado(achado);
			return "MENSAGEM_GRUPOS_ACHADOS_ACHADO_ALTERACAO_COM_SUCESSO";
		}
	}	

	public void refreshPdtAchado(List<PdtAchado> listaAchados) {
		final PdtAchadoDAO pdtAchadoDAO = getPdtAchadoDAO();
		
		for(PdtAchado item : listaAchados){
			if(pdtAchadoDAO.contains(item)){
				pdtAchadoDAO.refresh(item);
			}
		}		
	}
	
	protected GruposAchadosRN getGruposAchadosRN() {
		return gruposAchadosRN;
	}
	

	protected PdtAchadoDAO getPdtAchadoDAO() {
		return pdtAchadoDAO;
	}

	protected PdtGrupoDAO getPdtGrupoDAO() {
		return pdtGrupoDAO;
	}

}
