package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.internacao.dao.AinDiariasAutorizadasDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de Acomodação.
 */
@SuppressWarnings("deprecation")
@Stateless
public class DiariaAutorizadaCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(DiariaAutorizadaCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;

@Inject
private AinDiariasAutorizadasDAO ainDiariasAutorizadasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6968790854030291698L;
	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	private enum DiariaAutorizadaCRUDExceptionCode implements BusinessExceptionCode {
		 ERRO_PERSISTIR_DIARIA_CONSTRAINT, ERRO_PERSISTIR_DIARIA, ERRO_ATUALIZAR_DIARIA, SENHA_OBRIGATORIA, QUANT_DIAS_OBRIGATORIOS, 
		 SENHA_E_QUANT_DIAS_OBRIGATORIOS, ERRO_REMOVER_DIARIA_AUTORIZADA_INTERNACAO;
	}

	//Obter a Internação passada por parametro/codigo
	public AinInternacao obterInternacao(Integer codigo) {
		return this.getAinInternacaoDAO().obterPorChavePrimaria(codigo);
	}
	
	/**
	 * Retorna uma lista de Diarias Hospitalares recebendo o codigo da internação
	 * como parametro
	 * 
	 * @param seqInternacao
	 * @return
	 */
	public List<AinDiariasAutorizadas> pesquisarDiariaPorCodigoInternacao(Integer seqInternacao) {
		return this.getAinDiariasAutorizadasDAO().pesquisarDiariaPorCodigoInternacao(seqInternacao);
	}

	public void atualizarListaDiariasAutorizadas(List<AinDiariasAutorizadas> listaDiariasAutorizadas,
			List<AinDiariasAutorizadas> listaDiariasOld) throws ApplicationBusinessException {
		AinDiariasAutorizadasDAO ainDiariasAutorizadasDAO = this.getAinDiariasAutorizadasDAO();

		for (AinDiariasAutorizadas diariaExcluir : listaDiariasOld) {
			boolean necessitaExcluir = true; 
			
			for (AinDiariasAutorizadas diarias : listaDiariasAutorizadas) {
				if(diariaExcluir.getId() == diarias.getId()){
					necessitaExcluir = false;
					break;
				}
			}
			
			if(necessitaExcluir){
				AinDiariasAutorizadas item = ainDiariasAutorizadasDAO.obterPorChavePrimaria(diariaExcluir.getId());
				try {
					if (item != null) {
						ainDiariasAutorizadasDAO.remover(item);
					}
				} catch (Exception e) {
					logError("Exceção capturada: ", e);
					Throwable cause = ExceptionUtils.getCause(e);
					if (cause instanceof ConstraintViolationException) {
						throw new ApplicationBusinessException(DiariaAutorizadaCRUDExceptionCode.ERRO_REMOVER_DIARIA_AUTORIZADA_INTERNACAO,
								((ConstraintViolationException) cause).getConstraintName());
					} else {
						throw new ApplicationBusinessException(DiariaAutorizadaCRUDExceptionCode.ERRO_REMOVER_DIARIA_AUTORIZADA_INTERNACAO, "");
					}
				}
			}
		}

		boolean isNew = false;
		for (AinDiariasAutorizadas diaria : listaDiariasAutorizadas) {

			if (diaria.getQuantDiarias() == null) {
				throw new ApplicationBusinessException(DiariaAutorizadaCRUDExceptionCode.QUANT_DIAS_OBRIGATORIOS);
			}
			if (diaria.getId().getSeq() == null) {
				diaria.getId().setSeq(ainDiariasAutorizadasDAO.obterSeqAinDiariaInternacao(diaria.getId().getIntSeq()));
				isNew = true;
			}
			if (isNew) {
				try {
					ainDiariasAutorizadasDAO.persistir(diaria);
				} catch (Exception e) {
					logError("Erro ao inserir Diarias", e);
					if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
						throw new ApplicationBusinessException(DiariaAutorizadaCRUDExceptionCode.ERRO_ATUALIZAR_DIARIA, e);
					}
				}
			} else {
				try {
					ainDiariasAutorizadasDAO.atualizar(diaria);
				} catch (Exception e) {
					logError("Erro ao atualizar Diarias", e);
					if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
						throw new ApplicationBusinessException(DiariaAutorizadaCRUDExceptionCode.ERRO_ATUALIZAR_DIARIA, e);
					}
				}
			}
		}
	}
	
	/**
	 * Método usado para remover as diárias da internação.
	 * 
	 * @param acompanhantesInternacaos
	 * @throws ApplicationBusinessException
	 */
	public void removerDiariasAutorizada(
			List<AinDiariasAutorizadas> diariasAutorizadas)
			throws ApplicationBusinessException{
		try {
			AinDiariasAutorizadasDAO ainDiariasAutorizadasDAO = this.getAinDiariasAutorizadasDAO();

			for (AinDiariasAutorizadas diaria : diariasAutorizadas) {
				AinDiariasAutorizadas item = ainDiariasAutorizadasDAO.obterPorChavePrimaria(diaria.getId());
				ainDiariasAutorizadasDAO.remover(item);
				ainDiariasAutorizadasDAO.flush();
			}
		}
		catch (Exception e) {		
			logError("Exceção capturada: ", e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
						DiariaAutorizadaCRUDExceptionCode.ERRO_REMOVER_DIARIA_AUTORIZADA_INTERNACAO, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						DiariaAutorizadaCRUDExceptionCode.ERRO_REMOVER_DIARIA_AUTORIZADA_INTERNACAO, "");
			}
		}
	}

	protected AinDiariasAutorizadasDAO getAinDiariasAutorizadasDAO() {
		return ainDiariasAutorizadasDAO;
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

}
