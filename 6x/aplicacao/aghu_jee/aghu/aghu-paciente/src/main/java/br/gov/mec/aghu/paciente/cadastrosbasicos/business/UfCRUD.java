package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.paciente.dao.AipCidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipUfsDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de UF.
 */
@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
@Stateless
public class UfCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(UfCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipUfsDAO aipUfsDAO;

@Inject
private AipCidadesDAO aipCidadesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2393600212018578819L;

	private enum UFCRUDExceptionCode implements BusinessExceptionCode {
		SIGLA_UF_JA_EXISTENTE, ERRO_REMOVER_UF, ERRO_PERSISTIR_UF, SIGLA_UF_OBRIGATORIO, 
		NOME_UF_OBRIGATORIO, PAIS_UF_OBRIGATORIO, UF_POSSUI_CIDADES,MENSAGEM_REMOVER_UF_RELACIONADO_PACIENTE,
		MENSAGEM_REMOVER_UF_RELACIONADO_OUTRA_TABELA;
	}

	
	/**
	 * Método responsável pela persistência de uma UF.
	 * 
	 * @param uf
	 * @throws ApplicationBusinessException
	 */
	public void salvarUF(AipUfs uf) throws ApplicationBusinessException {
		this.validarDadosUF(uf, true);
		this.getAipUfsDAO().persistir(uf);
	}

	/**
	 * Método responsável pela alteração de uma UF.
	 * 
	 * @param uf
	 * @throws ApplicationBusinessException
	 */
	public void alterarUF(AipUfs uf) throws ApplicationBusinessException {
		this.validarDadosUF(uf, false);
		this.getAipUfsDAO().atualizar(uf);
	}
	
	/**
	 * Apaga uma UF do banco de dados.
	 * 
	 * @param uf
	 *            UF a ser removida.
	 * @throws ApplicationBusinessException 
	 */
	public void removerUF(String sigla) throws ApplicationBusinessException{
		AipUfs uf = getAipUfsDAO().obterUF(sigla);
		this.testarUfPossuiCidades(uf);
		try{
		   this.getAipUfsDAO().remover(this.getAipUfsDAO().obterUF(uf.getSigla()));
		}
		catch (PersistenceException e) {
			logError("Erro ao remover a UF.", e);
			
			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause()
							.getClass())) {
				if (StringUtils.containsIgnoreCase(
						((ConstraintViolationException) e.getCause())
								.getConstraintName(), "AIP_PDS_UF_FK1")) {
					// possui dependencia em AIP_PACIENTES_DADOS_CNS
					throw new ApplicationBusinessException(
							UFCRUDExceptionCode.MENSAGEM_REMOVER_UF_RELACIONADO_PACIENTE,uf.getNome());
				}
			}
			throw new ApplicationBusinessException(
					UFCRUDExceptionCode.MENSAGEM_REMOVER_UF_RELACIONADO_OUTRA_TABELA, uf.getNome());

		}

	}
	
	

	
	/**
	 * Verifica se a UF possui cidades associadas
	 * @param uf
	 * @throws ApplicationBusinessException
	 */
	protected void testarUfPossuiCidades(AipUfs uf) throws ApplicationBusinessException{
		List<AipCidades> listaCidades = getAipCidadesDAO().pesquisarCidadesPorUf(uf);
		if (!listaCidades.isEmpty()){
			throw new ApplicationBusinessException(UFCRUDExceptionCode.UF_POSSUI_CIDADES);
		}
	}
	

	/**
	 * Método responsável pelas validações dos dados de UF. Método utilizado
	 * para inclusão e atualização de UFs.
	 * 
	 * @param uf
	 * @param novaUF
	 *            se está criando uma nova UF deve ser validado se a sigla da
	 *            mesma já existe para outra UF.
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosUF(AipUfs uf, boolean novaUF)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(uf.getSigla())) {
			throw new ApplicationBusinessException(
					UFCRUDExceptionCode.SIGLA_UF_OBRIGATORIO);
		}

		if (StringUtils.isBlank(uf.getNome())) {
			throw new ApplicationBusinessException(
					UFCRUDExceptionCode.NOME_UF_OBRIGATORIO);
		}

		if (uf.getPais() == null
				|| StringUtils.isBlank(uf.getPais().getSigla())) {
			throw new ApplicationBusinessException(
					UFCRUDExceptionCode.PAIS_UF_OBRIGATORIO);
		}

		// Na criação, valida se já existe uma UF com a mesma sigla
		if (novaUF) {
			AipUfs ufAux = getAipUfsDAO().obterUF(uf.getSigla());
			if (ufAux != null) {
				throw new ApplicationBusinessException(
						UFCRUDExceptionCode.SIGLA_UF_JA_EXISTENTE);
			}
		}
	}

	
	protected AipUfsDAO getAipUfsDAO(){
		return aipUfsDAO;
	}
	
	protected AipCidadesDAO getAipCidadesDAO(){
		return aipCidadesDAO;
	}

	
}
