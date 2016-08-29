package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;
import br.gov.mec.aghu.core.utils.FileUtil;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghArquivoProcessamentoLog;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusUtil extends BaseBMTBusiness{
	
	protected static final long serialVersionUID = 7439743103164192223L;

	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusUtil.class);

	protected static final int REFRESH; // segundos
	protected static final int MAX_TENTATIVAS;
	protected static final String ENCODE;
	protected static final String ALTERA;
	protected static final String INCLUI;

	@EJB
	private IAghuFacade iAghuFacade;
	
	@Inject
	private DataAccessService dataAcessService;
	
	static {
		REFRESH = 5; 
		ALTERA = "ALTERA";
		INCLUI = "INCLUI";
		MAX_TENTATIVAS = 3;
		ENCODE="ISO-8859-1";
	}
	
	/**
	 * ORADB: FFCC_ORDENA_STRING_CBO
	 * 
	 * Efetuara a ordenação da string primeiro separando-a em valores de acordo com o tamanho solicitado, 
	 * após ordenando-a de acordo com a ordem natural....
	 * 
	 * @param aux String a ser ordenada
	 * @param tam Tamanho em que a String será quebrada
	 * @author eschweigert
	 */
	public Set<String> ordenarStringCBO(final String aux, final int tam) {
		Set<String> tree = new TreeSet<String>(); 
		tree.addAll(Arrays.asList(aux.split("(?<=\\G.{"+tam+"})")));
		//return StringUtils.join( tree.toArray() );
		return tree;
	}
	

	public AghArquivoProcessamento atualizarArquivo( AghArquivoProcessamento aghArquivo, 
													 final Date date, final Integer percent, final Integer peso, 
													 final int tentativas, final Date fimProcessamento, 
													 final ControleProcessadorArquivosImportacaoSus controle ) {
		try {
			
			super.beginTransaction();
			aghArquivo = iAghuFacade.obterAghArquivoProcessamentoPorChavePrimaria(aghArquivo.getSeq());
			aghArquivo.setDthrUltimoProcessamento(date);
			
			if (percent == null) {
				final int perc = (int) ((double) controle.getNrRegistrosProcessados() / (double) controle.getNrRegistrosProcesso() * peso) + controle.getPartial();
				aghArquivo.setPercentualProcessado(perc > 100 ? 99 : Integer.valueOf(perc));
			} else {
				aghArquivo.setPercentualProcessado(percent > 100 ? 100 : Integer.valueOf(percent));
			}
			
			if (fimProcessamento != null) {
				aghArquivo.setDthrFimProcessamento(fimProcessamento);
			}
			iAghuFacade.atualizarAghArquivoProcessamento(aghArquivo.getSeq(), aghArquivo.getDthrUltimoProcessamento(), aghArquivo.getPercentualProcessado(), aghArquivo.getDthrFimProcessamento());
			// desatachando objetos (performance)
			// this.entityManager.clear();
			super.commitTransaction();
			
		} catch (final Exception e) {
			super.rollbackTransaction();
//			userTx = reIniciarTransacao(userTx);
//			userTx = obterUserTransaction(null);
			LOG.error(e.getMessage());
			LOG.error(e.getMessage(), e);
			if (tentativas < MAX_TENTATIVAS + 1) {
				return atualizarArquivo(aghArquivo, date, percent, peso, (tentativas + 1), fimProcessamento, controle
						   );
			}
		}
		gravarLog(controle, aghArquivo);
		
		return aghArquivo;
	}

	public AghArquivoProcessamento atualizarArquivo( final AghArquivoProcessamento aghArquivo, 
														final Date inicio, final Date date,
													    final int refreshTime /* segundos */, 
													    final int peso, final int tentativas,
													    final ControleProcessadorArquivosImportacaoSus controle
			) {
		if ((((date.getTime() / 1000)) - (inicio.getTime() / 1000)) % refreshTime == 0) {
			return atualizarArquivo(aghArquivo, date, null, peso, tentativas, null, controle);
		}
		return aghArquivo;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void gravarLog(final ControleProcessadorArquivosImportacaoSus controle, final AghArquivoProcessamento aghArquivo) {
		if (aghArquivo != null && controle != null && controle.getLogRetorno().toString().trim().length() > 0) {
			super.beginTransaction();
			final AghArquivoProcessamentoLog arquivoProcessamentoLog = new AghArquivoProcessamentoLog(aghArquivo.getSeq(), controle.getLogRetorno().toString());
			dataAcessService.persist(arquivoProcessamentoLog);
			dataAcessService.flush();
			super.commitTransaction();
			controle.getLogRetorno().setLength(0);
		}
	}
	
	public BufferedReader abrirArquivo(final AghArquivoProcessamento nomeArquivo) {
		final InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(nomeArquivo.getArquivo()),
				Charset.forName("ISO-8859-1"));
		return new BufferedReader(inputStreamReader);
	}
	
	public BufferedReader abrirArquivo(final String arquivo) throws FileNotFoundException {
		InputStream inputStream = new BufferedInputStream(new FileInputStream(arquivo));
	    Reader reader = new InputStreamReader(inputStream, Charset.forName(ENCODE));
		return new BufferedReader(reader);
	}

	public void tratarExcecaoNaoLancada(final Exception e, final StringBuilder logRetorno, final String msg, final Object... args) {
		String erro = msg;
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				erro = erro.replaceAll("\\{" + i + "\\}", args[i].toString());
			}
		}

		logRetorno.append(erro);
		if(LOG.isDebugEnabled()){
			LOG.debug(erro);
		}
	}

	public String [] split(final String str, final int tam) {
		if(str == null || tam <= 0 ){
			throw new IllegalArgumentException();
		}
		return str.split("(?<=\\G.{"+tam+"})");
	}



	public List<String> processaRegistrosProcedimento( final AghArquivoProcessamento arquivo, final List<String> listaNomeArquivos,final Date inicio, 
													   final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException {
		
		String nomeArquivoProcedimentos = null;
		try {
			nomeArquivoProcedimentos = FileUtil.arquivoExiste(listaNomeArquivos, arquivo.getNome());
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(e1);
		}
		
		BufferedReader br = null;
		final List<String> linhasArquivo = new ArrayList<String>();
		
		try { // Ocupação
			br = abrirArquivo(nomeArquivoProcedimentos);
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(strLine)) {
					controle.incrementaNrRegistrosProcesso();
					linhasArquivo.add(strLine);
					if (controle.getNrRegistrosProcesso() % 500 == 0) {
						atualizarArquivo(arquivo, inicio, new Date(),
										 ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
										 ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle
										 );
					}
				}
			}
		} catch (final IOException io) {
			LOG.warn("Problemas na leitura dos arquivos.");
			throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusUtilExceptionCode.ARQUIVO_NAO_ENCONTRADO);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (final IOException e) {
				LOG.error(e.getMessage());
				LOG.error(e.getMessage(), e);
			}
		}
		return linhasArquivo;
	}
	
	protected enum ProcessadorArquivosImportacaoSusUtilExceptionCode implements BusinessExceptionCode {
		ARQUIVO_NAO_ENCONTRADO
	}


	@Override
	protected Log getLogger() {
		return LOG;
	}
	
}
