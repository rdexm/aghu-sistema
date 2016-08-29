package br.gov.mec.aghu.aghparametros.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.parametrosistema.vo.ParametroAgendaVO;


@Stateless
@Modulo(ModuloEnum.CONFIGURACAO)
public class ParametroFacade extends BaseFacade implements IParametroFacade {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1634676846129159711L;
	
	@Inject
	private AghParametrosDAO aghParametrosDAO;
	
	@EJB
	private AghParametrosON aghParametrosON;
	

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.aghparametros.IParametroFacade#recuperarCaminhoLogo()
	 */
	@Override
	public String recuperarCaminhoLogo() {
		return aghParametrosON.recuperarCaminhoLogo();
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.aghparametros.IParametroFacade#recuperarCaminhoLogo2()
	 */
	@Override
	public String recuperarCaminhoLogo2() {
		return aghParametrosON.recuperarCaminhoLogo2();
	}
	
	@Override
	public String recuperarCaminhoLogo2Relativo() {
		return aghParametrosON.recuperarCaminhoLogo2Relativo();
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.aghparametros.IParametroFacade#buscarAghParametro(br.gov.mec.aghu.util.AghuParametrosEnum)
	 */
	@Override
	public AghParametros buscarAghParametro(AghuParametrosEnum nome) throws ApplicationBusinessException {
		return aghParametrosON.buscarAghParametro(nome);
	}

	@Override
	public BigDecimal buscarValorNumerico(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorNumerico(parametrosEnum);
	}

	@Override
	public Integer buscarValorInteiro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorInteiro(parametrosEnum);
	}

	@Override
	public Byte buscarValorByte(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorByte(parametrosEnum);
	}

	@Override
	public Short buscarValorShort(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorShort(parametrosEnum);
	}

	@Override
	public Long buscarValorLong(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorLong(parametrosEnum);
	}

	@Override
	public String buscarValorTexto(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorTexto(parametrosEnum);
	}

	@Override
	public Date buscarValorData(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorData(parametrosEnum);
	}

	@Override
	public String[] buscarValorArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorArray(parametrosEnum);
	}

	@Override
	public BigDecimal[] buscarValorBigDecimalArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorBigDecimalArray(parametrosEnum);
	}

	@Override
	public Integer[] buscarValorIntegerArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorIntegerArray(parametrosEnum);
	}

	@Override
	public Byte[] buscarValorByteArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorByteArray(parametrosEnum);
	}

	@Override
	public Short[] buscarValorShortArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorShortArray(parametrosEnum);
	}

	@Override
	public Long[] buscarValorLongArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return aghParametrosON.buscarValorLongArray(parametrosEnum);
	}
	

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.aghparametros.IParametroFacade#getAghpParametro(br.gov.mec.aghu.paciente.vo.AghParametrosVO)
	 */
	@Override
	public void getAghpParametro(AghParametrosVO aghParametroVO) throws ApplicationBusinessException {
		aghParametrosON.getAghpParametro(aghParametroVO);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.aghparametros.IParametroFacade#setAghpParametro(br.gov.mec.aghu.model.AghParametros)
	 */
	@Override
	public void setAghpParametro(AghParametros aghParam) {
		aghParametrosON.setAghpParametro(aghParam);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.aghparametros.IParametroFacade#insereAghParametro(br.gov.mec.aghu.model.AghParametros)
	 */
	@Override
	public void insereAghParametro(AghParametros aghParam) {
		aghParametrosON.insereAghParametro(aghParam);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.aghparametros.IParametroFacade#setAghpParametro(br.gov.mec.aghu.paciente.vo.AghParametrosVO, java.lang.String, boolean)
	 */
	@Override
	public boolean setAghpParametro(AghParametrosVO aghParametroVO, String modulo, boolean commit) {
		return aghParametrosON.setAghpParametro(aghParametroVO, modulo, commit);
	}

	@Override
	public String recuperarCaminhoLogoRelativo() {
		return aghParametrosON.recuperarCaminhoLogoRelativo();
	}
	
	/**
	 * Verifica a existência de um AGH parâmetro. Este método não
	 * correponde a uma procedure em particular.<br>
	 * 
	 * @param param
	 * @return true se encontrar um parametro com o <b>nome</b> informado. Caso contrario retorna false.
	 * 
	 * 
	 * @see br.gov.mec.aghu.aghparametros.business.IParametroFacade#getAghParametro(br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum)
	 * @see br.gov.mec.aghu.aghparametros.business.IParametroFacade#verificarExisteAghParametro(br.gov.mec.aghu.aghparametros.util.AghuParetrosEnum)
	 */
	@Override
	public Boolean verificarExisteAghParametro(AghuParametrosEnum param) {
		return aghParametrosON.verificarExisteAghParametro(param);
	}

	/**
	 * Verifica a existencia de um parâmetro e de um valor para este parâmetro no AGHU.
	 * 
	 * @param param
	 * @return true se encontrar um parametro e seu respectivo valor for diferente de nulo. Caso contrario retorna false.
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarExisteAghParametroValor(AghuParametrosEnum param) {
		return aghParametrosON.verificarExisteAghParametroValor(param);
	}
	
	/**
	 * ORADB Procedure AGHP_GET_PARAMETRO
	 * 
	 * Obtem um aghParametro por nome.<br>
	 * Nao encontrando parametro com o nome retorna nulo.<br>
	 * Deve ser usado em conjunto com o metodo verificarExisteAghParametro.<br>
	 * 
	 * 
	 * @param nome
	 * @return
	 * @throws ApplicationBusinessExceptionSemRollback
	 * 
	 */
	@Override
	public AghParametros obterAghParametro(AghuParametrosEnum nome) throws ApplicationBusinessException {
		return aghParametrosON.obterAghParametro(nome);
	}
	
	/**
	 * Obtem um aghParametro por nome.<br>
	 * Nao encontrando parametro com o nome retorna nulo.<br>
	 * Deve ser usado em conjunto com o metodo verificarExisteAghParametro.<br>
	 * 
	 * @param nome
	 * @return
	 * 
	 * @see br.gov.mec.aghu.aghparametros.business.IParametroFacade#getAghParametro(br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum)
	 * @see br.gov.mec.aghu.aghparametros.business.IParametroFacade#verificarExisteAghParametro(br.gov.mec.aghu.aghparametros.util.AghuParetrosEnum)
	 */
	@Override
	public AghParametros getAghParametro(AghuParametrosEnum nome) {
		AghParametros aghParametro = aghParametrosDAO.obterAghParametroPorNome(nome.toString());
		// TODO MIGRAÇÃO: Aguardando migração da classe AghParametrosDAOCache
		//AghParametros aghParametro = this.getAghParametrosDAOCache().obterAghParametroPorNome(nome.toString());
		return aghParametro;
	}
	
	/**
	 * Método que obtem o valor do campo vlrNumerico da tabela AGH_PARAMETROS de
	 * acordo com o nome informado
	 * 
	 * @param nome
	 * @return
	 */
	@Override
	public BigDecimal obterValorNumericoAghParametros(String nome) {
		return aghParametrosDAO.obterValorNumericoAghParametros(nome);
	}

	@Override
	public AghParametros obterAghParametroPorNome(String nome) {
		return aghParametrosDAO.obterAghParametroPorNome(nome);
	}
	
	@Override
	public List<AghParametros> obterPorVariosNomes(String[] nomeParametros) {
		return aghParametrosDAO.obterPorVariosNomes(nomeParametros);
	}
	
		
// 	TODO MIGRAÇÃO: Aguardando migração da classe AghParametrosDAOCache
//	protected AghParametrosDAOCache getAghParametrosDAOCache() {
//		return this.aghParametrosDAOCache;
//	}
	
	@Override
	public ParametroAgendaVO consultarParametrosParaAgenda() {
		return this.aghParametrosDAO.consultarParametros();
	}
	
	public Object obterAghParametroPorNome(String nome, String tipo) throws ApplicationBusinessException {
		return aghParametrosON.obterAghParametroPorNome(nome, tipo);
	}

}
