package br.gov.mec.aghu.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghDocumentoContingenciaDAO;
import br.gov.mec.aghu.model.AghDocumentoContingencia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author aghu
 * 
 */
@Stateless
public class DocumentoContingenciaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DocumentoContingenciaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AghDocumentoContingenciaDAO aghDocumentoContingenciaDAO;

	//@EJB
	//private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2736108430719126816L;

	/**
	 * Método responsável por criar um novo documento de contingênica, além de
	 * deletar os documentos com prazo vencido.
	 * 
	 * 
	 * @param documentoContingencia
	 */
	public void salvarDocumentoContigencia(
			AghDocumentoContingencia documentoContingencia)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		documentoContingencia.setDthrCriacao(new Date());
		documentoContingencia.setLoginUsuario(servidorLogado.getUsuario());

		AghDocumentoContingenciaDAO aghDocumentoContingenciaDAO = getAghDocumentoContingenciaDAO();

		aghDocumentoContingenciaDAO.persistir(documentoContingencia);

		//TODO arrumar apos a modularizacao do parametro sistemas.
		Integer diasArmazenamentoRelatorio = 10; 
//			getParametroFacade()
//				.obterAghParametro(
//						AghuParametrosEnum.P_AGHU_TOTAL_DIAS_CONTINGENCIA_DOCUMENTOS)
//				.getVlrNumerico().intValue();

		Date dataLimite = DateUtil.adicionaDias(new Date(),
				diasArmazenamentoRelatorio * -1);

		aghDocumentoContingenciaDAO
				.deletarDocumentosContingenciaPorData(dataLimite);

	}

	/**
	 * 
	 * @return
	 */
	private AghDocumentoContingenciaDAO getAghDocumentoContingenciaDAO() {
		return aghDocumentoContingenciaDAO;

	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}