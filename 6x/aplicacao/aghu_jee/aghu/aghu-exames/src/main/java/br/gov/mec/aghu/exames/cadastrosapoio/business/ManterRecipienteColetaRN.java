package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelRecipienteColetaDAO;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ManterRecipienteColetaRN extends BaseBusiness {

	
	@EJB
	private ManterTipoAmostraExameON manterTipoAmostraExameON;
	
	private static final Log LOG = LogFactory.getLog(ManterRecipienteColetaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelRecipienteColetaDAO aelRecipienteColetaDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8441855310409138104L;

	public enum ManterRecipienteColetaRNExceptionCode implements
			BusinessExceptionCode {

		AEL_00342, AEL_00343, AEL_00344, AEL_00346, AEL_00369, AEL_01102, ERRO_UK_DESCRICAO_RECIPIENTE_COLETA, ERRO_GENERICO_BD_RECIPIENTE_COLETA;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	/**
	 * Insere um registro na tabela AEL_RECIPIENTES_COLETA.
	 * 
	 * @param recipienteColeta
	 * @throws ApplicationBusinessException
	 */
	public void inserirRecipienteColeta(AelRecipienteColeta recipienteColeta) 
		throws BaseException {
		
		this.preInserirRecipienteColeta(recipienteColeta);
		this.getAelRecipienteColetaDAO().persistir(recipienteColeta);
		this.getAelRecipienteColetaDAO().flush();
	}
	
	
	/**
	 * Atualiza um registro na tabela AEL_RECIPIENTES_COLETA.
	 */
	public void atualizarRecipienteColeta(AelRecipienteColeta recipienteColeta) 
	 	throws ApplicationBusinessException {
		
		this.preAtualizarRecipienteColeta(recipienteColeta);
		this.getAelRecipienteColetaDAO().merge(recipienteColeta);
		this.getAelRecipienteColetaDAO().flush();
	}
	
	/**
	 * Remove um registro na tabela AEL_RECIPIENTES_COLETA.
	 */
	public void removerRecipienteColeta(Integer seqAelRecipienteColeta)  throws ApplicationBusinessException {
		AelRecipienteColeta recipienteColeta = aelRecipienteColetaDAO.obterPorChavePrimaria(seqAelRecipienteColeta);
		
		if (recipienteColeta == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.preRemoverRecipienteColeta(recipienteColeta);
		String descRecipiente = recipienteColeta.getDescricao();
		try {
			this.getAelRecipienteColetaDAO().remover(recipienteColeta);
			this.getAelRecipienteColetaDAO().flush();
		} catch (PersistenceException e) {
			LOG.error("Exceção capturada: ", e);
			ManterRecipienteColetaRNExceptionCode.ERRO_GENERICO_BD_RECIPIENTE_COLETA.throwException(descRecipiente);
		}
	}
	
	
	/**
	 * @ORADB Trigger AELT_RCO_BRI
	 * 
	 * EXECUTA ANTES DE FAZER INSERT.
	 */
	private void preInserirRecipienteColeta(AelRecipienteColeta recipienteColeta) 
		throws BaseException {
		
		//insere uma data no momento criação de um registro.
		recipienteColeta.setCriadoEm(new Date());
		
		if (descricaoExiste(recipienteColeta)) {
			ManterRecipienteColetaRNExceptionCode.ERRO_UK_DESCRICAO_RECIPIENTE_COLETA.throwException();
		}
		
	}
	
	
	/**
	 * @ORADB Trigger AELT_RCO_BRU
	 * 
	 * EXECUTA ANTES DE FAZER UPDATE.
	 * 
	 * @param recipienteColeta
	 * @throws ApplicationBusinessException
	 */
	protected void preAtualizarRecipienteColeta(AelRecipienteColeta recipienteColeta)
		throws ApplicationBusinessException {
		
		//verifica se a descrição foi alterada
		AelRecipienteColeta recipienteColetaOld = getAelRecipienteColetaDAO()
			.obterOriginal(recipienteColeta.getSeq());

		if(!StringUtils.equalsIgnoreCase(recipienteColeta.getDescricao().trim(), recipienteColetaOld.getDescricao().trim())) {
			ManterRecipienteColetaRNExceptionCode.AEL_00346.throwException();
		}
		
		//verificar alteracao para matricula, codigo servidor ou data de criacao
		if(!DateUtil.isDatasIguais(recipienteColeta.getCriadoEm(), recipienteColetaOld.getCriadoEm())) {
			ManterRecipienteColetaRNExceptionCode.AEL_00369.throwException();
		}
		
		if(recipienteColeta.getServidor() != null && recipienteColeta.getServidor().getId() != null
				&& (!recipienteColeta.getServidor().getId().getMatricula().equals(recipienteColetaOld.getServidor().getId().getMatricula())
						|| !recipienteColeta.getServidor().getId().getVinCodigo().equals(recipienteColetaOld.getServidor().getId().getVinCodigo()))){
			
			ManterRecipienteColetaRNExceptionCode.AEL_00369.throwException();
		}
			
		
		//RN05 - recipiente de coleta está sendo desativado
		if(DominioSituacao.A == recipienteColetaOld.getIndSituacao() 
				&& DominioSituacao.I == recipienteColeta.getIndSituacao()) {

			if(getManterTipoAmostraExameON().temTipoAmostraExame(recipienteColeta)){
				ManterRecipienteColetaRNExceptionCode.AEL_01102.throwException();
			}
		}
		
		if (descricaoExiste(recipienteColeta)) {
			ManterRecipienteColetaRNExceptionCode.ERRO_UK_DESCRICAO_RECIPIENTE_COLETA.throwException();
		}
	}
	
	
	/**
	 * @ORADB Trigger AELT_RCO_BRD
	 * 
	 * EXECUTA ANTES DE FAZER REMOVE.
	 * 
	 * @param recipienteColeta
	 * @throws ApplicationBusinessException
	 */
	private void preRemoverRecipienteColeta(AelRecipienteColeta recipienteColeta)
		throws ApplicationBusinessException {
				
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		
		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar
					.getInstance().getTime(), recipienteColeta.getCriadoEm());
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(
						ManterRecipienteColetaRNExceptionCode.AEL_00343);
			}
		} else {
			throw new ApplicationBusinessException(
					ManterRecipienteColetaRNExceptionCode.AEL_00342);

		}
	}
	
	/**
	 * Indica se a descrição do recipiente passado por parâmetro ja existe no sistema.
	 * @param recipienteNew
	 * @return
	 */
	
	private boolean descricaoExiste(AelRecipienteColeta recipienteNew) {
		AelRecipienteColeta recipiente = new AelRecipienteColeta();
		//Seta a descrição em um novo objeto para pesquisar
		recipiente.setDescricao(recipienteNew.getDescricao());
		//Verifica se não existe um recipiente com esta descrição
		List<AelRecipienteColeta> recipientes = getAelRecipienteColetaDAO().pesquisarDescricao(recipiente);
		//Se a lista de recipientes de retorno tiver dados itera a lista
		if (recipientes != null && !recipientes.isEmpty()) {
			for(AelRecipienteColeta aelRecipientes : recipientes) {
				//Se o recipiente não for igual ao atual retorna que a descrição ja existe. 
				if (!aelRecipientes.equals(recipienteNew)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected AelRecipienteColetaDAO getAelRecipienteColetaDAO() {
		return aelRecipienteColetaDAO;
	}
	
	protected ManterTipoAmostraExameON getManterTipoAmostraExameON() {
		return manterTipoAmostraExameON;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
}
