package br.gov.mec.aghu.aghparametros.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.parametrosistema.vo.ParametroAgendaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Local
public interface IParametroFacade extends Serializable {

	String recuperarCaminhoLogo();

	String recuperarCaminhoLogo2();

	AghParametros buscarAghParametro(AghuParametrosEnum nome) throws ApplicationBusinessException;

	BigDecimal buscarValorNumerico(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;

	Integer buscarValorInteiro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	Byte buscarValorByte(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	Short buscarValorShort(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	Long buscarValorLong(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	String buscarValorTexto(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	Date buscarValorData(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	String[] buscarValorArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	BigDecimal[] buscarValorBigDecimalArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	Integer[] buscarValorIntegerArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	Byte[] buscarValorByteArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	Short[] buscarValorShortArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;
	
	Long[] buscarValorLongArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException;

	void getAghpParametro(AghParametrosVO aghParametroVO)
			throws ApplicationBusinessException;

	void setAghpParametro(AghParametros aghParam);

	void insereAghParametro(AghParametros aghParam);

	boolean setAghpParametro(AghParametrosVO aghParametroVO, String modulo,
			boolean commit);

	/**
	 * Verifica a existência de um AGH parâmetro. Este método não
	 * correponde a uma procedure em particular.<br>
	 * 
	 * @param param
	 * @return true se encontrar um parametro com o <b>nome</b> informado. Caso contrario retorna false.
	 * 
 	 * @see br.gov.mec.aghu.aghparametros.business.IParametroFacade#getAghParametro(br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum)
	 * @see br.gov.mec.aghu.aghparametros.business.IParametroFacade#verificarExisteAghParametro(br.gov.mec.aghu.aghparametros.util.AghuParetrosEnum) 
	 */
	Boolean verificarExisteAghParametro(AghuParametrosEnum param);

	/**
	 * ORADB Procedure AGHP_GET_PARAMETRO
	 * 
	 * @param aghParametroVO
	 * @throws ApplicationBusinessExceptionSemRollback
	 * 
	 */
	AghParametros obterAghParametro(AghuParametrosEnum nome)
			throws ApplicationBusinessException;

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
	AghParametros getAghParametro(AghuParametrosEnum nome);

	String recuperarCaminhoLogoRelativo();
	
	String recuperarCaminhoLogo2Relativo();
	
	Boolean verificarExisteAghParametroValor(AghuParametrosEnum param);
	
	BigDecimal obterValorNumericoAghParametros(String nome);
	
	ParametroAgendaVO consultarParametrosParaAgenda();
	
	/**
	 * Buscar um parametro em AGH_PARAMETROS
	 * 
	 * #34780
	 * 
	 * @param nome
	 * @return
	 */
	AghParametros obterAghParametroPorNome(String nome);
	
	List<AghParametros> obterPorVariosNomes(String[] nomeParametros);
	
	public Object obterAghParametroPorNome(String nome, String tipo) throws ApplicationBusinessException;
	
}