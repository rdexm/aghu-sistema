package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinAcomodacoesDAO;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de Acomodação.
 */
//
@Stateless
public class AcomodacaoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AcomodacaoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinAcomodacoesDAO ainAcomodacoesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5780584961086267675L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;



	private enum AcomodacaoCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_ACOMODACAO, ERRO_PERSISTIR_ACOMODACAO_CONSTRAINT, DESCRICAO_ACOMODACAO_OBRIGATORIO, DESCRICAO_ACOMODACAO_JA_EXISTENTE, ERRO_REMOVER_ACOMODACAO_CONSTRAINT_CONTAS_HOSPITAIS_DIA, ERRO_REMOVER_ACOMODACAO_CONSTRAINT_CONTAS_INTERNACOES, ERRO_REMOVER_ACOMODACAO_CONSTRAINT_CONTAS_HOSPITALARES, ERRO_REMOVER_ACOMODACAO_CONSTRAINT_CONV_PLANO_ACOMODACOES, ERRO_REMOVER_ACOMODACAO_CONSTRAINT_V_AIN_PESQ_LEITOS, ERRO_REMOVER_ACOMODACAO_CONSTRAINT_SOLICT_TRANSF_PACIENTES, ERRO_REMOVER_ACOMODACAO_CONSTRAINT_SOLICITACAO_INTERNACAO, ERRO_REMOVER_ACOMODACAO_CONSTRAINT_QUARTOS, ERRO_REMOVER_ACOMODACAO_CONSTRAINT_QMS_CTRL_BLOCK, ERRO_REMOVER_ACOMODACAO;
	}
	
	/**
	 * Método responsável pela persistência de uma Acomodação.
	 * 
	 * @dbtables AinAcomodacoes select,insert,update
	 * 
	 * @param acomodacao
	 * @throws ApplicationBusinessException
	 */
	public void persistirAcomodacoes(AinAcomodacoes acomodacao) throws ApplicationBusinessException {
		if (acomodacao.getSeq() == null) {
			// inclusão
			this.incluirAcomodacao(acomodacao);
		} else {
			// edição
			this.atualizarAcomodacao(acomodacao);
		}

	}

	/**
	 * Método responsável pela atualização de uma Acomodação.
	 * 
	 * @param acomodacao
	 * @throws ApplicationBusinessException
	 */

	private void atualizarAcomodacao(AinAcomodacoes acomodacao) throws ApplicationBusinessException {
		this.validarDadosAcomodacao(acomodacao);
		ainAcomodacoesDAO.atualizar(acomodacao);
		ainAcomodacoesDAO.flush();
	}

	/**
	 * Método responsável por incluir uma nova Acomodação.
	 * 
	 * @param acomodacao
	 * @throws ApplicationBusinessException
	 */
	private void incluirAcomodacao(AinAcomodacoes acomodacao) throws ApplicationBusinessException {
		this.validarDadosAcomodacao(acomodacao);

		try {
			AinAcomodacoesDAO ainAcomodacoesDAO = this.getAinAcomodacoesDAO();
			ainAcomodacoesDAO.persistir(acomodacao);
			ainAcomodacoesDAO.flush();
		} catch (Exception e) {
			logError("Erro ao incluir a acomodação.", e);
			throw new ApplicationBusinessException(AcomodacaoCRUDExceptionCode.ERRO_PERSISTIR_ACOMODACAO);
		}
	}

	/**
	 * Método responsável por validar campos.
	 * 
	 * @param acomodacao
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosAcomodacao(AinAcomodacoes acomodacao) throws ApplicationBusinessException {
		// validar se o campo descrição esta preenchido
		if (StringUtils.isBlank(acomodacao.getDescricao())) {
			throw new ApplicationBusinessException(AcomodacaoCRUDExceptionCode.DESCRICAO_ACOMODACAO_OBRIGATORIO);
		}
		// validar descrição duplicada
		List<AinAcomodacoes> acomodacoes = getAinAcomodacoesDAO().getAcomodacaoComMesmaDescricao(acomodacao);
		if (acomodacoes != null && !acomodacoes.isEmpty()) {
			throw new ApplicationBusinessException(AcomodacaoCRUDExceptionCode.DESCRICAO_ACOMODACAO_JA_EXISTENTE);
		}
	}
	
	/**
	 * Método responsável pela remoção de uma Acomodação.
	 *
	 * @dbtables AinAcomodacoes delete
	 * 
	 * @param acomodacao
	 * @throws ApplicationBusinessException
	 */
	public void removerAcomodacao(Integer codigo) throws ApplicationBusinessException {
		try {
			AinAcomodacoesDAO ainAcomodacoesDAO = this.getAinAcomodacoesDAO();
			AinAcomodacoes acomodacao = this.cadastrosBasicosInternacaoFacade
					.obterAcomodacao(codigo);
			ainAcomodacoesDAO.remover(acomodacao);
			ainAcomodacoesDAO.flush();
		} catch (PersistenceException e) {
			logError("Exceção capturada: ", e);
			Map<String,BusinessExceptionCode> mapConstraintNameCode = new HashMap<String, BusinessExceptionCode>();
			mapConstraintNameCode.put("AIN_QRT_ACM_FK1", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_QUARTOS);
			mapConstraintNameCode.put("FAT_VHD_ACM_FK1", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_CONTAS_HOSPITAIS_DIA);
			mapConstraintNameCode.put("FAT_VCH_ACM_FK1", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_CONTAS_INTERNACOES);
			mapConstraintNameCode.put("FAT_CTH_ACM_FK1", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_CONTAS_HOSPITALARES);
			mapConstraintNameCode.put("FAT_CPA_ACM_FK1", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_CONV_PLANO_ACOMODACOES);
			mapConstraintNameCode.put("AIN_STP_ACM_FK1", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_SOLICT_TRANSF_PACIENTES);
			mapConstraintNameCode.put("AIN_SIN_ACM_FK1", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_SOLICITACAO_INTERNACAO);
			mapConstraintNameCode.put("AIN_QMS$CTRL_ACM_FK2", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_QMS_CTRL_BLOCK);
			mapConstraintNameCode.put("AIN_QMS$CTRL_ACM_FK1", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_QMS_CTRL_BLOCK);
			mapConstraintNameCode.put("AIN_VPL_ACM_FK1", AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO_CONSTRAINT_V_AIN_PESQ_LEITOS);
//			validarConstraint(e, mapConstraintNameCode);
			CoreUtil.validarConstraint(e, mapConstraintNameCode);
				throw new ApplicationBusinessException(
						AcomodacaoCRUDExceptionCode.ERRO_REMOVER_ACOMODACAO);
			}

	}
	

	/**
	 * Retorna uma acomodação com base na chave primária.
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param seq
	 * @return
	 */
	public AinAcomodacoes obterAcomodacao(Integer ainAcomodacaoCodigo) {
		return getAinAcomodacoesDAO().obterPorChavePrimaria(ainAcomodacaoCodigo);
	}
	
	/**
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public List<AinAcomodacoes> pesquisarAcomodacoes(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoPesquisaAcomocadao, String descricaoPesquisaAcomodacao) {
		return getAinAcomodacoesDAO().pesquisarAcomodacoes(firstResult, maxResult, orderProperty, asc, codigoPesquisaAcomocadao, descricaoPesquisaAcomodacao);
	}

	/**
	 * 
	 * @dbtables AinAcomodacoes select
	 * 
	 * @param codigo
	 * @param tipo
	 * @return
	 */
	public Long pesquisaAcomodacoesCount(Integer codigoPesquisaAcomocadao,
			String descricaoPesquisaAcomodacao) {
		return getAinAcomodacoesDAO().pesquisaAcomodacoesCount(codigoPesquisaAcomocadao, descricaoPesquisaAcomodacao);
	}

	protected AinAcomodacoesDAO getAinAcomodacoesDAO() {
		return ainAcomodacoesDAO;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

}
