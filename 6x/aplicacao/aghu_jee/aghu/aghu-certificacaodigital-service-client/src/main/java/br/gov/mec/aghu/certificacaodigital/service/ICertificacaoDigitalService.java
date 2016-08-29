package br.gov.mec.aghu.certificacaodigital.service;

import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.certificacaodigital.service.vo.DadosDocumentoVO;
import br.gov.mec.aghu.service.ServiceException;

/**
 * 
 * @author frocha
 * 
 */
@Local
@Deprecated
public interface ICertificacaoDigitalService {
	
	/**
	 * #39011 - Seqs dos documentos atendidos
	 * 
	 * @param seqAtendimento
	 * @return
	 */
	List<Integer> buscarSeqDocumentosAtendidos(Integer seqAtendimento) throws ServiceException;
	
	/**
	 * #38995 - Verifica se existe pendência de assinatura digital
	 * 
	 * @param seqAtendimento
	 * @return
	 */
	Boolean existePendenciaAssinaturaDigital(Integer seqAtendimento) throws ServiceException;
	
	/**
	 *  #39017 - Inativa versões de documentos
	 * @param seq
	 */
	void inativarVersaoDocumento(Integer seq) throws ServiceException;

	/**
	 * Web Service #39100
	 * 
	 * Buscar o identificador de um documento e sua versão para determinado tipo de documento e atendimento
	 * 
	 * @param atdSeq
	 * @param codTipoDocumento
	 * @return
	 */
	List<DadosDocumentoVO> obterAghVersaoDocumentoPorAtendimentoTipoDocumento(Integer atdSeq, String codigotipo) throws ServiceException;
	
	/**
	 * Inativa  registro em AGH_VERSOES_DOCUMENTOS
	 * 
	 * @param listSeq
	 */
	void inativarVersaoDocumentos(List<Integer> listSeq) throws ServiceException;
	
	Boolean verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado();
	
	Boolean verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado(Integer matricula, Short vinCodigo) throws ServiceException;
	
}
