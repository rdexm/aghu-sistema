package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface IDigitalizacaoPOLFacade extends Serializable  {

	/**
	 * Método que recupera uma lista de Urls de documentos ativos no sistema GED
	 * 
	 * @param parametros
	 * @return
	 * @throws ApplicationBusinessException
	 */
	List<DocumentoGEDVO> buscaUrlsDocumentosGEDAtivos(ParametrosGEDVO parametros) throws ApplicationBusinessException;
	
	/**
	 * Método que recupera uma lista de Urls de documentos inativos no sistema GED
	 * 
	 * @param parametros
	 * @return
	 * @throws ApplicationBusinessException
	 */
	List<DocumentoGEDVO> buscaUrlsDocumentosGEDInativos(ParametrosGEDVO parametros) throws ApplicationBusinessException;
	
	/**
	 * Método que recupera uma lista de Urls de documentos administrativos no sistema GED
	 * 
	 * @param parametros
	 * @return
	 * @throws AGHUNegocioException
	 */
	List<DocumentoGEDVO> buscaUrlsDocumentosGEDAdminstrativos(ParametrosGEDVO parametros) throws ApplicationBusinessException;

}