package br.gov.mec.aghu.certificacaodigital.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.certificacaodigital.dao.AghVersaoDocumentoDAO;
import br.gov.mec.aghu.certificacaodigital.vo.DocumentosPendentesVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class CertificadoDigitalON extends BaseBusiness {
	
	@EJB
	private ListarPendenciasAssinaturaON listarPendenciasAssinaturaON;
	
	private static final Log LOG = LogFactory.getLog(CertificadoDigitalON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AghVersaoDocumentoDAO aghVersaoDocumentoDAO;

	private static final long serialVersionUID = 6845239252063363819L;

	public boolean verificarNecessidadeResolverPendencias() {
		boolean result = false;
		
		if (this.obterParametroCertificacaoDigitalHabilitada()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if (servidorLogado != null) {
				int diasMax = this.obterParametroDiasPendCertif();
				int diasAcum = this.obterDiasPendenciaMaisAntiga();
				int docsMax = this.obterParametroDocsPendCertif();
				Long docsAcum = this.obterQtdDocPendentes(servidorLogado);
				result = (diasAcum > diasMax) || (docsAcum > docsMax);			
			} else {
				logInfo("Nao foi possivel recuperar o servidor logado");
			}
		}
		
		return result;
	}
	
	public int obterDiasPendenciaMaisAntiga() {
		
		int result = 0;

		if (this.obterParametroCertificacaoDigitalHabilitada()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if (servidorLogado != null) {
				DocumentosPendentesVO pendAntiga = this
						.getListarPendenciasAssinaturaON()
						.pesquisarPendenciaMaisAntiga(servidorLogado);
				
				if (pendAntiga != null) {
					Date hoje = new Date();
					Date pend = pendAntiga.getCriadoEm();
					Float diff = DateUtil.diffInDays(pend, hoje);
					if (diff != null) {
						result = diff.intValue();
					}
				}				
				
			} else {
				logInfo("Nao foi possivel recuperar o servidor logado");
			}
		}

		return result;
	}
	
	public Long obterQuantidadeCertificadosPendentes() {
		Long result = 0l;
		if (this.obterParametroCertificacaoDigitalHabilitada()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if (servidorLogado != null) {
				result = this.obterQtdDocPendentes(servidorLogado);			
			} else {
				logInfo("Nao foi possivel recuperar o servidor logado");
			}
		}
		
		return result;
	}

	/**
	 * @author gandriotti
	 * @param resp
	 * @return
	 */
	private Long obterQtdDocPendentes(final RapServidores resp) {
		return this.listarPendenciasAssinaturaCount(resp);
	}

	/**
	 * @author gandriotti
	 * @return
	 */
	private int obterParametroDiasPendCertif() {
		int result = 0;
		AghuParametrosEnum param = null;
		
		param = AghuParametrosEnum.P_DIAS_PEND_CERTIF;
		result = this.obterParametroNum(param);
		
		return result;
	}
	
	/**
	 * @author gandriotti
	 * @return
	 */
	private int obterParametroDocsPendCertif() {
		int result = 0;
		AghuParametrosEnum param = null;
		
		param = AghuParametrosEnum.P_DOCS_PEND_CERTIF;
		result = this.obterParametroNum(param);
		
		return result;
	}
	
	/**
	 * @author gandriotti
	 * @return
	 */
	private int obterParametroNum(final AghuParametrosEnum p) {
		BigDecimal result = null;
		AghParametros param = null;
		
		try {
			param = this.getParametroFacade().buscarAghParametro(p);
			result = param.getVlrNumerico();
			if (result == null) {
				throw new IllegalArgumentException("Parametro: " + p + " nao contem qualquer valor numerico.");
			}
		} catch (final ApplicationBusinessException e) {
			result = BigDecimal.ZERO;
		}
		
		return result.intValue();
	}
	
	/**
	 * @author gandriotti
	 * @return
	 */
	public boolean obterParametroCertificacaoDigitalHabilitada() {
		boolean result = false;
		AghuParametrosEnum param = null;		
		AghParametros p = null;
		String val = null;
		
		param = AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL;
		try {
			p = this.getParametroFacade().buscarAghParametro(param);
			val = p.getVlrTexto();
			if (val == null) {
				throw new IllegalArgumentException("Parametro: " + p + " nao contem qualquer valor texto.");
			}
			result = DominioSimNao.S.toString().equalsIgnoreCase(val);
		} catch (final ApplicationBusinessException e) {
			logInfo(
					"Problemas recuperando parametro: " + param.name() 
					+ " e: " + e.getLocalizedMessage());
		}
		
		return result;
	}
	
	/**
	 *  #39017 - Inativa versões de documentos
	 * @param seq
	 */
	public void inativarVersaoDocumento(Integer seq){
		List<AghVersaoDocumento> listaVersaoDocumento = new LinkedList<AghVersaoDocumento>();
		listaVersaoDocumento = getAghVersaoDocumentoDAO().obterAghVersaoDocumentoPorSeqTipoSumari0Alta(seq);
		for (AghVersaoDocumento aghVersaoDocumento : listaVersaoDocumento) {
			aghVersaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.I);
			getAghVersaoDocumentoDAO().atualizar(aghVersaoDocumento);
		}
	}
	
	public Long listarPendenciasAssinaturaCount(final RapServidores responsavel) {
		return this.getAghVersaoDocumentoDAO().count(responsavel);
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	} 

	protected ListarPendenciasAssinaturaON getListarPendenciasAssinaturaON() {
		return listarPendenciasAssinaturaON;
	}
	
	protected AghVersaoDocumentoDAO getAghVersaoDocumentoDAO() {
		return aghVersaoDocumentoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	/**
	 * Inativa  registro em AGH_VERSOES_DOCUMENTOS
	 * 
	 * @param listSeq
	 */
	public void inativarVersaoDocumentos(List<Integer> listSeq) {
		if (listSeq != null && !listSeq.isEmpty()) {
			for (Integer seq : listSeq) {
				AghVersaoDocumento aghVersaoDocumento = getAghVersaoDocumentoDAO().obterPorChavePrimaria(seq);
				aghVersaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.I);
				getAghVersaoDocumentoDAO().atualizar(aghVersaoDocumento);
			}
		}
	}
}
