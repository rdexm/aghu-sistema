package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.monitor.FileEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatRetornaAihDAO;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @ORADB PROCEDURE P_RECEBE_AIHS
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class RecebimentoAihsON extends BaseBMTBusiness {

	private static final String ENCODING = "UTF-8";

	private static final long serialVersionUID = -5042617781680815005L;
	
	private static final Log LOG = LogFactory.getLog(RecebimentoAihsON.class);
	
	public final static int TRANSACTION_TIMEOUT_24_HORAS = 60 * 60 * 24; //= 1 dia
	
	@Inject
	private FatRetornaAihDAO fatRetornaAihDAO;
	
	@EJB
	private RecebimentoAihsRN recebimentoAihsRN;
	
	@EJB
	private AtualizaAihRN atualizaAihRN;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum RecebimentoAihsONExceptionCode implements BusinessExceptionCode {
		ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS
	}

	private File convertInputStreamToFile(InputStream inputStream) throws ApplicationBusinessException {
		try {
			File f = new File("AGHU_SMS.TXT");
			OutputStream out = new FileOutputStream(f);
			
			byte buf[] = new byte[1024];
			int len;
			
			while ((len = inputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			
			out.close();
			inputStream.close();
			f.createNewFile();
			
			return f;
			
		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
			throw new ApplicationBusinessException(RecebimentoAihsONExceptionCode.ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS, e.getMessage());
		}
	}
	
	public void recebimentoAihs(InputStream inputStream, String nomeMicrocomputador) throws BaseException {
		this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
		
		Integer vQtdConta = 0;
		
		File arquivo = convertInputStreamToFile(inputStream);
		
		existeArquivo(arquivo.getAbsolutePath());
		removerTodosRegistrosFatRetornaAihs();
		
		FileEntry fileEntry =  new FileEntry(new File(FilenameUtils.normalize(arquivo.getAbsolutePath())));
		
		try {
			LineIterator it = FileUtils.lineIterator(fileEntry.getFile(), ENCODING);

			try {
			    while (it.hasNext()) {
			    	
			    	String linha = it.nextLine();
			    	
			    	if(isLeituraOk(linha)){
			    		vQtdConta += recebimentoAihsRN.inserir(linha, fileEntry.getFile());
			    	}
			    }
			} finally {
			    LineIterator.closeQuietly(it);
			}
			
		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
			throw new ApplicationBusinessException(RecebimentoAihsONExceptionCode.ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS, e.getMessage());
		}
		
		if (vQtdConta > 0 ){
			atualizaAihRN.processarAtualizacaoAih(nomeMicrocomputador);
			getLogger().info("Autorizadas " + vQtdConta + "contas");
		} else {
			getLogger().info("Falha na autorização.");
		}
		this.commitTransaction();
	}

	private void existeArquivo(String arquivo) throws ApplicationBusinessException{
		if(!obterArquivo(arquivo).isExists()){
			throw new ApplicationBusinessException(RecebimentoAihsONExceptionCode.ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS);
		}
	}

	private FileEntry obterArquivo(String arquivo) {
		FileEntry fileEntry =  new FileEntry(new File(FilenameUtils.normalize(arquivo)));
		fileEntry.refresh(fileEntry.getFile());
		return fileEntry;
	}

	private void removerTodosRegistrosFatRetornaAihs(){
		fatRetornaAihDAO.removeAll();
	}
	
	/**
	 * @oradb FUNCTION LEITURA_OK RETURN BOOLEAN
	 * @param arquivo
	 * @return
	 * @throws ApplicationBusinessException
	 */
 	private Boolean isLeituraOk(String linha) throws ApplicationBusinessException{
		if(StringUtils.isNotBlank(linha)){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
}
