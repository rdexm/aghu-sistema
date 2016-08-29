package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipCidadesDistritoSanitario;
import br.gov.mec.aghu.model.AipDistritoSanitarios;
import br.gov.mec.aghu.paciente.dao.AipCidadesDistritoSanitarioDAO;
import br.gov.mec.aghu.paciente.dao.AipDistritoSanitariosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class DistritoSanitarioCRUD extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(DistritoSanitarioCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AipDistritoSanitariosDAO aipDistritoSanitariosDAO;
	
	@Inject
	private AipCidadesDistritoSanitarioDAO aipCidadesDistritoSanitarioDAO;
	
	private static final long serialVersionUID = -1142640950792486582L;

	public AipDistritoSanitarios obterDistritoSanitarioPorCodigo(Short codigo) {
		return aipDistritoSanitariosDAO.obterPorChavePrimaria(codigo);
	}

	public AipDistritoSanitarios obterDistritoSanitarioPorCodigo(Short codigo, Enum ...enums) {
		return aipDistritoSanitariosDAO.obterPorChavePrimaria(codigo, enums);
	}
	
	/**
	 * Método de persistencia de Distrito sanitário que define entre inclusão
	 * ou alteração
	 */
	public void persistDistritoSanitario (AipDistritoSanitarios distritoSanitario,
			List<AipCidadesDistritoSanitario> cidadesInseridas, List<AipCidadesDistritoSanitario> cidadesExcluidas)
								throws ApplicationBusinessException {
		
		if (distritoSanitario.getCodigo() == null){
			this.incluirDistritoSanitario(distritoSanitario, cidadesInseridas);
			
		}else{
			atualizarDistritoSanitario(distritoSanitario, cidadesInseridas, cidadesExcluidas);
		}
	}

	public void atualizarDistritoSanitario ( AipDistritoSanitarios distritoSanitario,
											 List<AipCidadesDistritoSanitario> cidadesInseridas, List<AipCidadesDistritoSanitario> cidadesExcluidas)
													 						throws ApplicationBusinessException {
		aipDistritoSanitariosDAO.atualizar(distritoSanitario);
		
		for (AipCidadesDistritoSanitario cdsList : cidadesExcluidas) {
			AipCidadesDistritoSanitario cds = aipCidadesDistritoSanitarioDAO.obterPorChavePrimaria(cdsList.getId());
			
			if(cds != null){
				aipCidadesDistritoSanitarioDAO.remover(cds);
			}
		}
		
		aipCidadesDistritoSanitarioDAO.flush();
		
		for (AipCidadesDistritoSanitario cdsList : cidadesInseridas) {
			cdsList.setAipDistritoSanitarios(distritoSanitario);
			aipCidadesDistritoSanitarioDAO.persistir(cdsList);
		}
		//aipCidadesDistritoSanitarioDAO.flush();
	}
	
	/**
	 * Método para inclusão de distrito sanitário
	 */
	public void incluirDistritoSanitario (AipDistritoSanitarios distritoSanitario, List<AipCidadesDistritoSanitario> cidadesInseridas)
																		throws ApplicationBusinessException {
		aipDistritoSanitariosDAO.persistir(distritoSanitario);
		aipDistritoSanitariosDAO.flush();
		
		for (AipCidadesDistritoSanitario aipCidadesDistritoSanitario : cidadesInseridas) {
			aipCidadesDistritoSanitario.getId().setDstCodigo(distritoSanitario.getCodigo());
			aipCidadesDistritoSanitario.setAipDistritoSanitarios(distritoSanitario);
			aipCidadesDistritoSanitarioDAO.persistir(aipCidadesDistritoSanitario);
		}
	}

	
	/**
	 * Método para remoção de distrito sanitário 
	 */
	
	public void removerDistritoSanitario(AipDistritoSanitarios distritoSanitario) throws ApplicationBusinessException{
		distritoSanitario = aipDistritoSanitariosDAO.merge(distritoSanitario);
		aipDistritoSanitariosDAO.remover(distritoSanitario);
	}

	protected AipDistritoSanitariosDAO getAipDistritoSanitariosDAO(){
		return aipDistritoSanitariosDAO;
	}

	public AipCidadesDistritoSanitarioDAO getAipCidadesDistritoSanitarioDAO() {
		return aipCidadesDistritoSanitarioDAO;
	}
			
}
