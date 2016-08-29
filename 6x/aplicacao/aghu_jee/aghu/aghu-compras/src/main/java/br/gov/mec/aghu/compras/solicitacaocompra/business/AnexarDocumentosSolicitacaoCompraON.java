package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoArquivoAnexoDAO;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.model.ScoArquivoAnexo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AnexarDocumentosSolicitacaoCompraON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AnexarDocumentosSolicitacaoCompraON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@Inject
private ScoArquivoAnexoDAO scoArquivoAnexoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7506818095185100884L;
	
	public enum ScoArquivoAnexoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ARQUIVO_OBRIGATORIO, MENSAGEM_TAMANHO_DO_ARQUIVO_ACIMA_PERMITIDO
	}
	
	public List<ScoArquivoAnexo> pesquisarArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento origem, Integer numero) {		
		return getScoArquivoAnexoDAO().pesquisarArquivosPorNumeroOrigem(origem, numero);
	}
	
	public Long pesquisarArquivosPorNumeroOrigemCount(DominioOrigemSolicitacaoSuprimento origem, Integer numero) {		
		return getScoArquivoAnexoDAO().pesquisarArquivosPorNumeroOrigemCount(origem, numero);
	}
	
	public Boolean verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento origem, Integer numero){
		return getScoArquivoAnexoDAO().verificarExistenciaArquivosPorNumeroOrigem(origem, numero);
	}
	
	/**
	 * Inclui um anexo a determinada fase do processo de compras
	 * @param arquivoAnexo
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void incluirArquivoAnexo(ScoArquivoAnexo arquivoAnexo)
			throws ApplicationBusinessException {	
		validaArquivoAnexo(arquivoAnexo);
		arquivoAnexo.setArquivo(trataNomeArquivo(arquivoAnexo.getArquivo()));
		getScoArquivoAnexoDAO().persistir(arquivoAnexo);
	}
	
	private String trataNomeArquivo(String nomeArquivoAnexo) {
		if (nomeArquivoAnexo.length() <= 100) {
			return nomeArquivoAnexo;
		} else {
			nomeArquivoAnexo = nomeArquivoAnexo.trim().replaceAll("\\s+", " ");
			if (nomeArquivoAnexo.length() <= 100) {
				return nomeArquivoAnexo;
			} else {
				nomeArquivoAnexo = Normalizer.normalize(nomeArquivoAnexo, Form.NFD).replaceAll("[^\\p{ASCII}]", "");
				if (nomeArquivoAnexo.length() <= 100) {
					return nomeArquivoAnexo;
				} else {
					if (nomeArquivoAnexo.contains(".")) {
						String fileExt = nomeArquivoAnexo.substring(nomeArquivoAnexo.lastIndexOf('.'));
						nomeArquivoAnexo = nomeArquivoAnexo.substring(0, (99 - fileExt.length())).concat(fileExt);
						return nomeArquivoAnexo;
					} else {
						return nomeArquivoAnexo.substring(0, 99);
					}
				}
			}
		}
	}

	/**
	 * Exclui um anexo de determinada fase do processo de compras
	 * @param seqArquivo
	 * @throws ApplicationBusinessException
	 */
	public void excluirArquivoAnexo(Long seqArquivo) throws ApplicationBusinessException {
		ScoArquivoAnexo arquivoAnexo = getScoArquivoAnexoDAO().obterPorChavePrimaria(seqArquivo);
		
		if (arquivoAnexo != null){
			getScoArquivoAnexoDAO().remover(arquivoAnexo);
		}
	}
	
	/**
	 * Altera um anexo de determinada fase do processo de compras
	 * @param arquivoAnexo
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void alterarArquivoAnexo(ScoArquivoAnexo arquivoAnexo) throws ApplicationBusinessException {		
		if (arquivoAnexo != null){
			getScoArquivoAnexoDAO().atualizar(arquivoAnexo);
		}		
	}
	
	/**
	 * Retorna o conteudo do parametro tamanhoMaximoPermitido para arquivos anexos
	 * @return BigDecimal Tamanho máximo de arquivo em bytes
	 * @throws ApplicationBusinessException Se não encontrar o parametro
	 */
	public BigDecimal tamanhoMaximoPermitido() throws ApplicationBusinessException{
		return this.getParametroFacade().buscarValorNumerico(AghuParametrosEnum.P_TAMANHO_MAX_ARQUIVO);
	}
		
	protected void validaArquivoAnexo(ScoArquivoAnexo arquivoAnexo) throws ApplicationBusinessException{		
		if (arquivoAnexo.getAnexo() == null || arquivoAnexo.getAnexo().length == 0){
			throw new ApplicationBusinessException(ScoArquivoAnexoONExceptionCode.MENSAGEM_ARQUIVO_OBRIGATORIO);
		}else{
			BigDecimal tamanhoArquivo = new BigDecimal(arquivoAnexo.getAnexo().length);
			BigDecimal tamanhoMaximoArquivo =  tamanhoMaximoPermitido();
			
			int testaTamanho = tamanhoArquivo.compareTo(tamanhoMaximoArquivo);
			
			if (testaTamanho > 0){
				throw new ApplicationBusinessException(ScoArquivoAnexoONExceptionCode.MENSAGEM_TAMANHO_DO_ARQUIVO_ACIMA_PERMITIDO);
			}
		}		
	}

	protected ScoArquivoAnexoDAO getScoArquivoAnexoDAO() {
		return scoArquivoAnexoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
		
}
