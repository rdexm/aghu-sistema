package br.gov.mec.aghu.estoque.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceValidadesRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(SceValidadesRN.class);

@EJB
private IEstoqueFacade estoqueFacade;




@Inject
private SceValidadeDAO sceValidadeDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2273452190705508663L;

	public enum SceValidadesRNExceptionCode implements BusinessExceptionCode {
		SCE_00292, SCE_00327, SCE_VAL_EXISTENTE;
	}
	
	/**
	 * ORADB SCEK_VAL_RN.RN_VALP_VER_EAL_ATIV
	 * @param sceValidade
	 * @throws BaseException
	 */
	private void preInserir(SceValidade sceValidade) throws BaseException{
		this.validarValidadeUnica(sceValidade);
		this.validarEstoqueAlmoxarifadoAtivo(sceValidade);
		this.validarDataValidade(sceValidade);
	}

	
	/**
	 * Inserir SceValidade
	 * @param validade
	 * @throws BaseException
	 */
	public void inserir(SceValidade validade) throws BaseException{
		this.preInserir(validade);		
		this.getSceValidadesDAO().persistir(validade);
		this.getSceValidadesDAO().flush();
	}
	
	/**
	 * Valida a situação do estoque almoxarifado da validade
	 * @param validade
	 * @throws BaseException
	 */
	protected void validarEstoqueAlmoxarifadoAtivo(SceValidade validade) throws BaseException {
		
		SceEstoqueAlmoxarifado eal = getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoPorId(validade.getId().getEalSeq());
		
		if(!eal.getIndSituacao().equals(DominioSituacao.A)){
			throw new ApplicationBusinessException(SceValidadesRNExceptionCode.SCE_00292);
		}
	}
	
	/**
	 * Verifica a exclusividade da validade
	 * @param validade
	 * @throws BaseException
	 */
	protected void validarValidadeUnica(SceValidade validade) throws BaseException {
		final SceValidade validadeIdentica = this.getSceValidadesDAO().obterPorChavePrimaria(validade.getId());
		if(validadeIdentica != null){
			throw new ApplicationBusinessException(SceValidadesRNExceptionCode.SCE_VAL_EXISTENTE);
		}
	}
	
	/**
	 * Valida a data de validade
	 * @param validade
	 * @throws BaseException
	 */
	protected void validarDataValidade(SceValidade validade) throws BaseException{
		
		Date objDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		try{
			objDate = sdf.parse(sdf.format(new Date()));
		}catch (ParseException e) {
			LOG.error(e.getMessage(),e);
		}
		
		if(CoreUtil.isMenorOuIgualDatas(validade.getId().getData(), objDate)){
			Integer codigoMaterial = getCodigoMaterial(validade);
			throw new ApplicationBusinessException(SceValidadesRNExceptionCode.SCE_00327, codigoMaterial.toString());
		}
	}
	
	/**
	 * Recupera o código do material a partir da validade.
	 * @param validade
	 * @return codigoMaterial
	 */
	protected Integer getCodigoMaterial(SceValidade validade) {
		SceEstoqueAlmoxarifado estoqueAlmoxarifado = this.getEstoqueFacade().obterSceEstoqueAlmoxarifadoPorChavePrimaria(validade.getId().getEalSeq());
		return estoqueAlmoxarifado.getMaterial().getCodigo();
	}

	/**
	 * Atualizar SceValidade
	 * @param validade
	 * @throws BaseException
	 */
	public void atualizar(SceValidade validade) throws ApplicationBusinessException{
		this.getSceValidadesDAO().atualizar(validade);
		this.getSceValidadesDAO().flush();
	}
	
	public void preAtualizar(SceValidade validade) throws BaseException{
		this.validarDataValidade(validade);
		this.atualizar(validade);
	}

	/**
	 * Remove SceValidade
	 * @param validade
	 */
	public void remover(SceValidade validade) {
		validade = this.getSceValidadesDAO().merge(validade);
		this.getSceValidadesDAO().remover(validade);
		this.getSceValidadesDAO().flush();
	}
	
	/**
	 * Getters para RNs e DAOs
	 */
	
	protected SceValidadeDAO getSceValidadesDAO() {
		return sceValidadeDAO;
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}


	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}


	protected void setEstoqueFacade(IEstoqueFacade estoqueFacade) {
		this.estoqueFacade = estoqueFacade;
	}


	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
