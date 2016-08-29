package br.gov.mec.aghu.certificacaodigital.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.certificacaodigital.service.vo.DadosDocumentoVO;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
@Deprecated
public class CertificacaoDigitalServiceImpl implements ICertificacaoDigitalService {

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	/**
	 * #39011 - Seqs dos documentos atendidos
	 * 
	 * @param seqAtendimento
	 * @return
	 */
	@Override
	public List<Integer> buscarSeqDocumentosAtendidos(Integer seqAtendimento)
			throws ServiceException {
		try {
			return this.certificacaoDigitalFacade.buscarSeqDocumentosAtendidos(seqAtendimento);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * #38995 - Verifica se existe pendência de assinatura digital
	 * 
	 * @param seqAtendimento
	 * @return
	 */
	@Override
	public Boolean existePendenciaAssinaturaDigital(Integer seqAtendimento) throws ServiceException {
		try {
			return this.certificacaoDigitalFacade.existePendenciaAssinaturaDigital(seqAtendimento);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	
	/**
	 *  #39017 - Inativa versões de documentos
	 * @param seq
	 * @throws ServiceException 
	 */
	@Override
	public void inativarVersaoDocumento(Integer seq) throws ServiceException{
		try {
			this.certificacaoDigitalFacade.inativarVersaoDocumento(seq);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	
	}

	@Override
	public List<DadosDocumentoVO> obterAghVersaoDocumentoPorAtendimentoTipoDocumento(Integer atdSeq, String codigotipo) throws ServiceException {
		try {
			List<DadosDocumentoVO> result = new ArrayList<DadosDocumentoVO>();
			DominioTipoDocumento tipo = DominioTipoDocumento.valueOf(codigotipo);
			List<AghVersaoDocumento> list = this.certificacaoDigitalFacade.obterAghVersaoDocumentoPorAtendimentoTipoDocumento(atdSeq, tipo);
			if (list != null && !list.isEmpty()) {
				for (AghVersaoDocumento aghVersaoDocumento : list) {
					DadosDocumentoVO dadosDocumentoVO = new DadosDocumentoVO();
					dadosDocumentoVO.setVerSeq(aghVersaoDocumento.getSeq());
					dadosDocumentoVO.setDocSeq(aghVersaoDocumento.getAghDocumentos().getSeq());
					result.add(dadosDocumentoVO);
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void inativarVersaoDocumentos(List<Integer> listSeq) throws ServiceException {
		try {
			this.certificacaoDigitalFacade.inativarVersaoDocumentos(listSeq);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public Boolean verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado(){
		return this.certificacaoDigitalFacade.verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado();
	}
	
	@Override
	public Boolean verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado(Integer matricula, Short vinCodigo) throws ServiceException {
		try {
			return this.certificacaoDigitalFacade.verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado(matricula, vinCodigo);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}	
	}
}