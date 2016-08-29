package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.monitor.FileEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.vo.RetornoSMSVO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @ORADB PROCEDURE P_LER_ARQ_ENVIADO
 */
@Stateless
public class RetornoSmsRN extends BaseBusiness{

	private static final String ENCODING = "UTF-8";

	private static final long serialVersionUID = 3187272244446580255L;

	private static final Log LOG = LogFactory.getLog(RetornoSmsRN.class);
	
	@Inject
	private FatContasHospitalaresDAO contasHospitalaresDAO;
	
	@EJB
	private RetornoSmsUtil smsUtil;
	
	private enum RetornoSmsRNExceptionCode implements BusinessExceptionCode {
		ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public List<RetornoSMSVO> lerArquivoEnviado(Integer pCthSeq, File file) throws BaseException {
		
		List<RetornoSMSVO> listRetorno =  new ArrayList<RetornoSMSVO>();
		Date dataDeEnvioSMS = obterDataDeEnvioSMS(pCthSeq);
		
		if( dataDeEnvioSMS == null ) {
			getLogger().info( "Data de Envio SMS não encontrada");
			
		} else {
				try {

					FileEntry fileEntry =  new FileEntry(new File(FilenameUtils.normalize(file.getAbsolutePath())));
					fileEntry.refresh(fileEntry.getFile());

					LineIterator it = FileUtils.lineIterator(fileEntry.getFile(), ENCODING);

					try {
					    while (it.hasNext()) {
					    	
					    	String linha = it.nextLine();
					    	
					    	RetornoSMSVO vo = new RetornoSMSVO();
					    	montarRegistroCont(linha, vo);
					    	
					    	definirInfoFile(fileEntry.getFile(), dataDeEnvioSMS, vo);

					    	if(!listRetorno.contains(vo)){
					    		montarRegistros(linha, vo);
					    		listRetorno.add(vo);
					    	}
					    }
					    
					} finally {
					    LineIterator.closeQuietly(it);
					}
					
				} catch (IOException e) {
					getLogger().info("Importação do arquivo " + file.getName() + " abortado.");
					throw new ApplicationBusinessException(RetornoSmsRNExceptionCode.ERRO_PROCESSAR_RETORNNO_ARQUIVO_SMS, e.getMessage());
				}

				processarRetornoSMS(listRetorno);
		}

		return listRetorno;
	}
	
	private void definirInfoFile(File file, Date dataDeEnvioSMS, RetornoSMSVO vo) throws ApplicationBusinessException {
			vo.setNomeArquivoImp(file.getName());
			vo.setDataArquivoImp(dataDeEnvioSMS);
	}

	private void processarRetornoSMS(List<RetornoSMSVO> listRetorno) throws ApplicationBusinessException {
		for (RetornoSMSVO vo : listRetorno) {
			 vo.setDataNasc(smsUtil.obterData(vo.getStrDtNas()));
			 vo.setDataInt(smsUtil.obterData(vo.getStrDtInt()));
			 vo.setLaudo(smsUtil.converterStringEmLong(vo.getStrLaudo()));
		}
	}

	private void montarRegistros (String linha, RetornoSMSVO vo){		
		montarRegistroTipo(linha, vo);
		montarRegistroLaudo(linha, vo);
		montarRegistroDataNas(linha, vo);
		montarRegistroDtInt(linha, vo);
	}
	
	private void montarRegistroCont(String linha, RetornoSMSVO vo) {
		// w_cont      := SUBSTR(reg_importacao, 8, 7);
		vo.setStrCont(StringUtils.substring(linha, 7, 14));
	 }

	private void montarRegistroTipo(String linha, RetornoSMSVO vo) {
		// w_tipo     := SUBSTR(reg_importacao, 1, 1);
		vo.setStrTipo(StringUtils.substring(linha, 0, 1));	
	}

	private void montarRegistroLaudo(String linha, RetornoSMSVO vo) {
		// w_laudo    := SUBSTR(reg_importacao, 2, 13);
		vo.setStrLaudo(StringUtils.substring(linha, 1, 14));
	}

	private void montarRegistroDataNas(String linha, RetornoSMSVO vo) {
		// w_dt_nas   := SUBSTR(reg_importacao, 66, 8);
		vo.setStrDtNas(StringUtils.substring(linha, 65, 73));
	}

	private void montarRegistroDtInt(String linha, RetornoSMSVO vo) {
		//w_dt_int	  := SUBSTR(reg_importacao,205,8);
		vo.setStrDtInt(StringUtils.substring(linha, 204, 212));
	}

	private Date obterDataDeEnvioSMS(Integer seqContaHospitalar) throws ApplicationBusinessException{
		FatContasHospitalares contasHospitalar = contasHospitalaresDAO.obterFatContaHospitalar(seqContaHospitalar);
		return contasHospitalar != null ? contasHospitalar.getDtEnvioSms() : null; 
	}
}
