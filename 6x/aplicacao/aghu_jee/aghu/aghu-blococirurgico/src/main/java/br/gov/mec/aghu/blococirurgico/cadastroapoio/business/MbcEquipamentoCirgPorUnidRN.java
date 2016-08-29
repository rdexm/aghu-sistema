package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirgPorUnidDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirurgicoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcEquipamentoCirgPorUnid;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcEquipamentoCirgPorUnidRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcEquipamentoCirgPorUnidRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcEquipamentoCirgPorUnidDAO mbcEquipamentoCirgPorUnidDAO;

	@Inject
	private MbcEquipamentoCirurgicoDAO mbcEquipamentoCirurgicoDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3798591818409349974L;

	
	public enum MbcEquipamentoCirgPorUnidRNExceptionCode implements
			BusinessExceptionCode {

		MBC_00530, //
		MBC_00531, //
		CONSTRAINT_MBC_EQUIP_PK;
		;
	}
	
	
	/**
	 * Altera um registro na tabela<br>
	 * MBC_EQUIPAMENTO_CIRG_POR_UNIDS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void atualizar(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		this.getMbcEquipamentoCirgPorUnidDAO().atualizar(elemento);
	}
	
	
	/**
	 * Insere um registro na tabela<br>
	 * MBC_EQUIPAMENTO_CIRG_POR_UNIDS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void inserir(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		this.verificarConstraints(elemento);
		this.preInserir(elemento);
		this.getMbcEquipamentoCirgPorUnidDAO().persistir(elemento);
	}

	
	/**
	 * ORADB TRIGGER MBCT_ECU_BRI
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void preInserir(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		//RN1
		elemento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
		//RN2
		this.verificarUnidadeFuncionalAtiva(elemento);
		
		//RN3
		this.verificarEquipamentoAtivo(elemento);
		
		//RN4
		elemento.setCriadoEm(new Date());
	}
	
	
	/**
	 * ORADB PROCEDURE RN_ECUP_VER_UNIDADE
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void verificarUnidadeFuncionalAtiva(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		if(this.getIAghuFacade()
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(
						elemento.getAghUnidadesFuncionais().getSeq(), Boolean.TRUE).isEmpty()) {
			throw new ApplicationBusinessException(
					MbcEquipamentoCirgPorUnidRNExceptionCode.MBC_00530);
		}
	}
	
	
	
	/**
	 * ORADB PROCEDURE RN_ECUP_VER_EQUIPAM
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void verificarEquipamentoAtivo(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		if(this.getMbcEquipamentoCirurgicoDAO().obterEquipamentoCirurgico(
				 null, elemento.getMbcEquipamentoCirurgico().getSeq(), DominioSituacao.A) == null) {
			throw new ApplicationBusinessException(MbcEquipamentoCirgPorUnidRNExceptionCode.MBC_00531);
		}
	}
	
	
	
	/**
	 * Remove um registro na<br>
	 * tabela MBC_EQUIPAMENTO_CIRG_POR_UNIDS.
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void remover(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		MbcEquipamentoCirgPorUnid elementoOriginal = this.getMbcEquipamentoCirgPorUnidDAO().obterPorChavePrimaria(elemento.getId());
		this.getMbcEquipamentoCirgPorUnidDAO().remover(elementoOriginal);
	}
	
	
	
	protected void verificarConstraints(MbcEquipamentoCirgPorUnid elemento) throws BaseException {
		if(this.getMbcEquipamentoCirgPorUnidDAO()
				.obterEquipamentoCirugCadastrado(
						elemento.getMbcEquipamentoCirurgico().getSeq(), 
						elemento.getAghUnidadesFuncionais().getSeq()) != null) {
			throw new ApplicationBusinessException(
					MbcEquipamentoCirgPorUnidRNExceptionCode.CONSTRAINT_MBC_EQUIP_PK,
					elemento.getMbcEquipamentoCirurgico().getDescricao());
		}
	}
	
	
	/** GET **/
	protected MbcEquipamentoCirgPorUnidDAO getMbcEquipamentoCirgPorUnidDAO() {
		return mbcEquipamentoCirgPorUnidDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	protected IAghuFacade getIAghuFacade() {
		return iAghuFacade;
	}
	
	protected MbcEquipamentoCirurgicoDAO getMbcEquipamentoCirurgicoDAO() {
		return mbcEquipamentoCirurgicoDAO;
	}
}
