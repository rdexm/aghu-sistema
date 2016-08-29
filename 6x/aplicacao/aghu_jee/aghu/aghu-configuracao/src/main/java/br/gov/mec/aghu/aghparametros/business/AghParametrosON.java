package br.gov.mec.aghu.aghparametros.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghModulosParametros;
import br.gov.mec.aghu.model.AghModulosParametrosId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.parametrosistema.dao.AghModulosParametrosDAO;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AghParametrosON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6160626024991265399L;
	
	private enum RegistrarGestacaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_OBTER_PARAMETRO;
	}
	
	private static final Log LOG = LogFactory.getLog(AghParametrosON.class);
	
	
	@Inject
	private AghParametrosDAO  aghParametrosDAO;
	
	@Inject
	private AghModulosParametrosDAO aghModulosParametrosDAO;
	
	/**
	 * ORADB Procedure AGHP_GET_PARAMETRO
	 * 
	 * @param aghParametroVO
	 * @throws ApplicationBusinessException
	 * 
	 *             Este método é uma versão mais de acordo com a arquitetura do
	 *             método abaixo. verificar necessidade de substituição
	 *             posteriormente.
	 */
	public AghParametros buscarAghParametro(AghuParametrosEnum nome)
			throws ApplicationBusinessException {

		AghParametros aghParametro = obterAghParametroPorNome(nome);

		if (aghParametro == null) {
			throw new ApplicationBusinessException(
					AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE,
					nome);
		}
		return aghParametro;

	}
	
	/**
	 * ORADB Procedure AGHP_GET_PARAMETRO
	 * 
	 * @param aghParametroVO
	 * @throws ApplicationBusinessExceptionSemRollback
	 * 
	 */
	public AghParametros obterAghParametro(AghuParametrosEnum nome)
			throws ApplicationBusinessException {

		AghParametros aghParametro = obterAghParametroPorNome(nome);

		if (aghParametro == null) {
			throw new ApplicationBusinessException(
					AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE,
					nome);
		}
		return aghParametro;

	}

	/**
	 * Verifica a existência de um AGH parâmetro. Este método não
	 * correponde a uma procedure em particular.<br>
	 * 
	 * @param nome
	 * @return true se encontrar um parametro com o <b>nome</b> informado. Caso contrario retorna false.
	 * 
	 */
	public Boolean verificarExisteAghParametro(AghuParametrosEnum nome) {
		Boolean retorno = false;

		AghParametros aghParametro = obterAghParametroPorNome(nome);

		if (aghParametro != null) {
			retorno = true;
		}
		return retorno;

	}
	
	/**
	 * Verifica a existência de um AGH parâmetro e seu respectivo valor. Este método não
	 * correponde a uma procedure em particular.<br>
	 * 
	 * @param nome
	 * @return true se encontrar um parametro e seu valor. Caso contrario retorna false.
	 * 
	 */
	public Boolean verificarExisteAghParametroValor(AghuParametrosEnum nome) {
		Boolean retorno = false;

		AghParametros aghParametro = obterAghParametroPorNome(nome);

		if (aghParametro != null 
				&& (aghParametro.getVlrData() != null 
				|| aghParametro.getVlrNumerico() != null 
				|| aghParametro.getVlrTexto() != null)) {
			retorno = true;
		}
		return retorno;

	}

	/**
	 * Obtem um aghParametro por nome.
	 * 
	 * @param nome
	 * @return
	 */
	private AghParametros obterAghParametroPorNome(AghuParametrosEnum nome) {
		AghParametros aghParametro = aghParametrosDAO.obterAghParametroPorNome(nome.toString());
		//TODO MIGRAÇÃO: Aguardando migração AghParametrosDAOCache 
		//AghParametros aghParametro = this.getAghParametrosDAOCache().obterAghParametroPorNome(nome.toString());
		return aghParametro;
	}
	
	public Object obterAghParametroPorNome(String nome, String tipo) throws ApplicationBusinessException {

		AghParametros aghParametros = aghParametrosDAO.obterAghParametroPorNome(nome);

		Object retorno = null;

		if (aghParametros != null) {

			switch (tipo) {
			case "vlrData":
				retorno = aghParametros.getVlrData();
				break;
			case "vlrNumerico":
				retorno = aghParametros.getVlrNumerico();
				break;
			case "vlrTexto":
				retorno = aghParametros.getVlrTexto();
				break;
			case "rotinaConsistencia":
				retorno = aghParametros.getRotinaConsistencia();
				break;
			case "exemploUso":
				retorno = aghParametros.getExemploUso();
				break;
			case "tipoDado":
				retorno = aghParametros.getTipoDado();
				break;
			case "vlrDataPadrao":
				retorno = aghParametros.getVlrDataPadrao();
				break;
			case "vlrNumericoPadrao":
				retorno = aghParametros.getVlrNumericoPadrao();
				break;
			case "vlrTextoPadrao":
				retorno = aghParametros.getVlrTextoPadrao();
				break;
			default:
				throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_ERRO_OBTER_PARAMETRO, nome);
			}
		}

		return retorno;
	}

	/**
	 * TODO: Verificar a necessidade de um VO para os parâmetros. Possivelmente
	 * este método será substituido pela sua versão mais a cima.
	 * 
	 * ORADB Procedure AGHP_GET_PARAMETRO
	 * 
	 * @param aghParametroVO
	 * @throws ApplicationBusinessException
	 */
	public void getAghpParametro(AghParametrosVO aghParametroVO)
			throws ApplicationBusinessException {
		try {
			AghParametros aghParametro = aghParametrosDAO.obterAghParametroPorNome(aghParametroVO.getNome());
			//TODO MIGRAÇÃO: Aguardando migração AghParametrosDAOCache 
			//AghParametros aghParametro = this.getAghParametrosDAOCache().obterAghParametroPorNome(nome.toString());

			aghParametroVO.setMsg(null);

			if (aghParametro == null) {
				throw new ApplicationBusinessException(
						AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE,
						aghParametroVO.getNome());
			} else {
				aghParametroVO.setVlrData(aghParametro.getVlrData());
				aghParametroVO.setVlrNumerico(aghParametro.getVlrNumerico());
				aghParametroVO.setVlrTexto(aghParametro.getVlrTexto());
			}
	
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			aghParametroVO.setVlrData(null);
			aghParametroVO.setVlrNumerico(null);
			aghParametroVO.setVlrTexto(null);
			aghParametroVO.setMsg("Erro na aghpGetParametro");

			throw new ApplicationBusinessException(
					AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE,
					aghParametroVO.getNome());
		}
	}

	/**
	 * ORADB Procedure AGHP_SET_PARAMETRO
	 * 
	 * @param aghParametroVO
	 * @param modulo
	 * @return boolean
	 */
	public boolean setAghpParametro(AghParametrosVO aghParametroVO,
			String modulo, boolean commit) {
	
		
		List<AghParametros> lista = aghParametrosDAO.pesquisarAghParametroPorNome(aghParametroVO.getNome());

		if (lista != null && lista.size() == 1) {
			// Recupera AGH_PARAMETRO para atualização.
			AghParametros aghParam = lista.get(0);

			if (aghParametroVO.getVlrData() != null) {
				aghParam.setVlrData(aghParametroVO.getVlrData());
			}
			if (aghParametroVO.getVlrNumerico() != null) {
				aghParam.setVlrNumerico(aghParametroVO.getVlrNumerico());
			}
			if (aghParametroVO.getVlrTexto() != null) {
				aghParam.setVlrTexto(aghParametroVO.getVlrTexto());
			}

			aghParametrosDAO.atualizar(aghParam);

			// Atualiza tabela AGH_MODULOS_PARAMETROS se necessário.
			List<AghModulosParametros> lista2 = aghModulosParametrosDAO.pesquisarPorPsiSeqModulo(modulo, aghParam.getSeq());
			
			if (lista2 == null || lista.size() == 0) {
				modulo = modulo.substring(0, 30);
				AghModulosParametros aghModParams = new AghModulosParametros(
						new AghModulosParametrosId(aghParam.getSeq(), modulo));

				aghModulosParametrosDAO.persistir(aghModParams);
			}
		}

		if (commit) {
			aghModulosParametrosDAO.flush();
		}

		return false;
	}

	/**
	 * Cria o parametro no banco
	 * 
	 * @param aghParam
	 */
	public void insereAghParametro(AghParametros aghParam) {
		aghParametrosDAO.persistir(aghParam);
		aghParametrosDAO.flush();
	}

	/**
	 * Realiza update em AghParametro
	 * 
	 * @param aghParam
	 */
	public void setAghpParametro(AghParametros aghParam) {
		aghParametrosDAO.atualizar(aghParam);
		aghParametrosDAO.flush();
	}

	/**
	 * Recupera o caminho do logo do hospital
	 * @return
	 */
	public String recuperarCaminhoLogo() {
		
		AghParametros parametros = aghParametrosDAO.obterAghParametroPorNome("P_AGHU_LOGO_HOSPITAL_JEE7");
		//TODO MIGRAÇÃO: Aguardando migração AghParametrosDAOCache 
		//AghParametros aghParametro = this.getAghParametrosDAOCache().obterAghParametroPorNome(nome.toString());
		if (parametros != null && parametros.getVlrTexto() != null) {
			return parametros.getVlrTexto();
		}
		return "";
	}
	
	/**
	 * Recupera o caminho do logo do hospital
	 * @return
	 */
	public String recuperarCaminhoLogo2() {
		AghParametros parametros = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_RELATIVO_JEE7.toString());
		if (parametros != null && parametros.getVlrTexto() != null) {
			return parametros.getVlrTexto();
		}
		return "";
	}

	public String recuperarCaminhoLogo2Relativo() {
		AghParametros parametros = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL2_RELATIVO_JEE7.toString());
		if (parametros != null && parametros.getVlrTexto() != null) {
			return parametros.getVlrTexto();
		}
		return "";
	}

	public String recuperarCaminhoLogoRelativo() {
		AghParametros parametros = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_RELATIVO_BW_JEE7.toString());
		if (parametros != null && parametros.getVlrTexto() != null) {
			return parametros.getVlrTexto();
		}
		return "";
	}

	public BigDecimal buscarValorNumerico(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		BigDecimal result = this.buscarAghParametro(parametrosEnum).getVlrNumerico();
		if(result == null){
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.PARAMETRO_INVALIDO, parametrosEnum);
		}
		return result;
	}
	
	public Integer buscarValorInteiro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.buscarValorNumerico(parametrosEnum).intValue(); 
	}

	public Byte buscarValorByte(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.buscarValorNumerico(parametrosEnum).byteValue(); 
	}

	public Short buscarValorShort(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.buscarValorNumerico(parametrosEnum).shortValue(); 
	}

	public Long buscarValorLong(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		return this.buscarValorNumerico(parametrosEnum).longValue(); 
	}

	public String buscarValorTexto(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String result = this.buscarAghParametro(parametrosEnum).getVlrTexto();
		if(result == null){
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.PARAMETRO_INVALIDO, parametrosEnum);
		}
		return result;
	}
	
	public Date buscarValorData(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		Date result =  this.buscarAghParametro(parametrosEnum).getVlrData();
		if(result == null){
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.PARAMETRO_INVALIDO, parametrosEnum);
		}
		return result;
	}
	
	public String[] buscarValorArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String result = this.buscarValorTexto(parametrosEnum);
		return result.split("\\,");
	}
	
	public BigDecimal[] buscarValorBigDecimalArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String[] tmp = this.buscarValorArray(parametrosEnum);
		BigDecimal[] result = new BigDecimal[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			result[i] = new BigDecimal(tmp[i]);
		}
		return result;
	}
	
	public Integer[] buscarValorIntegerArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String[] tmp = this.buscarValorArray(parametrosEnum);
		Integer[] result = new Integer[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			result[i] = Integer.valueOf(tmp[i]);
		}
		return result;
	}
	
	public Byte[] buscarValorByteArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String[] tmp = this.buscarValorArray(parametrosEnum);
		Byte[] result = new Byte[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			result[i] = Byte.valueOf(tmp[i]);
		}
		return result;
	}
	
	public Short[] buscarValorShortArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String[] tmp = this.buscarValorArray(parametrosEnum);
		Short[] result = new Short[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			result[i] = Short.valueOf(tmp[i]);
		}
		return result;
	}
	
	public Long[] buscarValorLongArray(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		String[] tmp = this.buscarValorArray(parametrosEnum);
		Long[] result = new Long[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			result[i] = Long.valueOf(tmp[i]);
		}
		return result;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
//  TODO MIGRAÇÃO: Aguardando migração AghParametrosDAOCache 
//	protected AghParametrosDAOCache getAghParametrosDAOCache() {
//		return aghParametrosDAOCache;
//	}
}
