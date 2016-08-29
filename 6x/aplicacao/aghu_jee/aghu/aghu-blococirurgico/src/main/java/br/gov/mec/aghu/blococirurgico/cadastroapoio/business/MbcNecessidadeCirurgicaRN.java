package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaSolicEspecialDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcNecessidadeCirurgicaDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de BANCO para MBC_NECESSIDADE_CIRURGICAS
 * 
 * @author aghu
 * 
 */
@Stateless
public class MbcNecessidadeCirurgicaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcNecessidadeCirurgicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendaSolicEspecialDAO mbcAgendaSolicEspecialDAO;

	@Inject
	private MbcNecessidadeCirurgicaDAO mbcNecessidadeCirurgicaDAO;


	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7088709679944047733L;

	public enum MbcNecessidadeCirurgicaRNExceptionCode implements BusinessExceptionCode {
		MBC_00351, MBC_00350, MBC_00311, ERRO_EXCLUIR_NECESSIDADE_CIRURGICA;
	}

	/*
	 * Métodos para PERSISTIR
	 */

	/**
	 * Persistir MbcNecessidadeCirurgica
	 * 
	 * @param necessidadeCirurgica
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void persistirMbcNecessidadeCirurgica(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException {
		if (necessidadeCirurgica.getSeq() == null) { // Inserir
			this.inserirMbcNecessidadeCirurgica(necessidadeCirurgica);
		} else { // Atualizar
			this.atualizarMbcNecessidadeCirurgica(necessidadeCirurgica);
		}
	}
	
	public void removerMbcNecessidadeCirurgica(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException {
		MbcNecessidadeCirurgica necessidade = this.getMbcNecessidadeCirurgicaDAO().obterPorChavePrimaria(necessidadeCirurgica.getSeq());
		
		if (necessidade != null) {
			this.preRemoverMbcNecessidadeCirurgica(necessidade);
			this.getMbcNecessidadeCirurgicaDAO().remover(necessidade);
			this.getMbcNecessidadeCirurgicaDAO().flush();
		}
	}

	private void preRemoverMbcNecessidadeCirurgica (MbcNecessidadeCirurgica necessidadeCirurgica) throws ApplicationBusinessException {
		// Verifica se a necessidade não está sendo utilizada pela tabela MBC_AGENDA_SOLIC_ESPECIAIS
		Long agendaSolicEspCount = this.getMbcAgendaSolicEspecialDAO()
			.buscarMbcAgendaSolicEspecialPorNciSeqCount(necessidadeCirurgica.getSeq());
		
		if (agendaSolicEspCount > 0) {
			throw new ApplicationBusinessException(MbcNecessidadeCirurgicaRNExceptionCode.ERRO_EXCLUIR_NECESSIDADE_CIRURGICA);
		}
	}
	
	/*
	 * Métodos INSERIR
	 */

	/**
	 * ORADB TRIGGER MBCT_NCI_BRI (INSERT)
	 * 
	 * @param necessidadeCirurgica
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void preInserirMbcNecessidadeCirurgica(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException {
		necessidadeCirurgica.setCriadoEm(new Date()); // RN1
		this.verificarUnidadeExecutoraExames(necessidadeCirurgica); // RN3
	}

	/**
	 * Inserir MbcNecessidadeCirurgica
	 * 
	 * @param necessidadeCirurgica
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserirMbcNecessidadeCirurgica(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException {
		this.preInserirMbcNecessidadeCirurgica(necessidadeCirurgica);
		this.getMbcNecessidadeCirurgicaDAO().persistir(necessidadeCirurgica);
		this.getMbcNecessidadeCirurgicaDAO().flush();

	}

	/*
	 * Métodos ATUALIZAR
	 */

	/**
	 * ORADB TRIGGER MBCT_NCI_BRU (UPDATE)
	 * 
	 * @param necessidadeCirurgica
	 * @param servidorLogado
	 * @throws BaseException
	 */
	protected void preAtualizarMbcNecessidadeCirurgica(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException {

		// Verfica se a DESCRIÇÃO foi modificada
		this.verificarDescricaoModificada(necessidadeCirurgica);

		// Executas todas as regras do pré-inserir
		this.preInserirMbcNecessidadeCirurgica(necessidadeCirurgica);

	}

	/**
	 * Atualizar MbcNecessidadeCirurgica
	 * 
	 * @param necessidadeCirurgica
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void atualizarMbcNecessidadeCirurgica(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException {
		this.preAtualizarMbcNecessidadeCirurgica(necessidadeCirurgica);
		this.getMbcNecessidadeCirurgicaDAO().atualizar(necessidadeCirurgica);
		this.getMbcNecessidadeCirurgicaDAO().flush();
	}

	/*
	 * PROCEDURES
	 */

	/**
	 * ORADB PROCEDURE MBCK_NCI_RN.RN_NCIP_VER_UNID_EXE Verifica a unidade executa de exames
	 * 
	 * @param necessidadeCirurgica
	 * @throws BaseException
	 */
	public void verificarUnidadeExecutoraExames(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException {

		// Chamada para procedure AGHC_VER_CARACT_UNF
		if(necessidadeCirurgica.getAghUnidadesFuncionais()!=null){
			DominioSimNao simNao = getAghuFacade().verificarCaracteristicaDaUnidadeFuncional(necessidadeCirurgica.getAghUnidadesFuncionais().getSeq(),
					ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES);
			
			if (simNao.isSim()) { 
				if (necessidadeCirurgica.getIndExigeDescSolic()) {
					// Se unidade executora informada, indicador de exige descrição deve ser falso
					throw new ApplicationBusinessException(MbcNecessidadeCirurgicaRNExceptionCode.MBC_00350);
				}
			} else {
				// Unidade funcional deverá ser unidade executora de exames
				throw new ApplicationBusinessException(MbcNecessidadeCirurgicaRNExceptionCode.MBC_00351);
			}
		}
	}

	/**
	 * ORADB PROCEDURE RN_NCIP_VER_DESCR Verifica se a descrição foi modificada
	 * 
	 * @param necessidadeCirurgica
	 * @throws BaseException
	 */
	protected void verificarDescricaoModificada(MbcNecessidadeCirurgica necessidadeCirurgica) throws BaseException {

		MbcNecessidadeCirurgica original = getMbcNecessidadeCirurgicaDAO().obterOriginal(necessidadeCirurgica);

		if ((necessidadeCirurgica.getDescricao() != null && !necessidadeCirurgica.getDescricao().equals(original.getDescricao()))
				|| (original.getDescricao() != null && !original.getDescricao().equals(necessidadeCirurgica.getDescricao()))) {
			throw new ApplicationBusinessException(MbcNecessidadeCirurgicaRNExceptionCode.MBC_00311);
		}
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	private MbcNecessidadeCirurgicaDAO getMbcNecessidadeCirurgicaDAO() {
		return mbcNecessidadeCirurgicaDAO;
	}
	
	private MbcAgendaSolicEspecialDAO getMbcAgendaSolicEspecialDAO() {
		return mbcAgendaSolicEspecialDAO;
	}

	private IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

}
