package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.vo.NomeArquivoVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ClinicaPorProcedimentoZIPRN extends BaseBusiness {

	private static final long serialVersionUID = -4326347352606314297L;
	
	private static final Log LOG = LogFactory.getLog(ClinicaPorProcedimentoZIPRN.class);
	
	private static final int TAMANHO_BUFFER = 2048; // 2Kb
	private static final String EXTENSAO_ZIP = ".zip";
	
	@EJB
	private ClinicaPorProcedimentoCSVRN clinicaPorProcedimentoCSVRN;
	
	@EJB
	private TotalGeralClinicaPorProcedimentoCSVRN totalGeralClinicaPorProcedimentoCSVRN;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum ClinicaPorProcedimentoZIPRNExceptionCode implements BusinessExceptionCode {
		ERRO_REL_CLIN_POR_PROCED_GERAR_CSV_ZIP;
	}	
	
	public String gerarClinicaPorProcedimentoZIP(FatCompetencia competencia, DominioModuloCompetencia modulo, Byte codAtoOPM) throws BaseException {
		
		File file = null;
		File zipFile = null;
		ZipOutputStream zipOutputStream = null;
		
		final String fileName = DominioNomeRelatorio.FATR_INT_CLC_PROCED.toString() + "_" + String.format("%02d", competencia.getId().getMes()) + "_"
				+ competencia.getId().getAno() + EXTENSAO_ZIP;
				
		try{

			zipFile = new File(fileName);

			zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
			
			List<NomeArquivoVO> listaNomeArquivo = new ArrayList<NomeArquivoVO>();
			
			listaNomeArquivo.add(clinicaPorProcedimentoCSVRN
					.geraRelCSVClinicaPorProcedimento(competencia.getId().getDtHrInicio(), 
							modulo, competencia.getId().getMes().byteValue(), 
							competencia.getId().getAno().shortValue(), codAtoOPM));

			listaNomeArquivo.add(totalGeralClinicaPorProcedimentoCSVRN
					.geraRelCSVTotalGeralClinicaPorProcedimento(modulo, 
							competencia.getId().getMes(), 
							competencia.getId().getAno(), 
							competencia.getId().getDtHrInicio(), codAtoOPM));
			 
			
			for (NomeArquivoVO nomeArquivo : listaNomeArquivo) {
				
				file = new File(nomeArquivo.getFileName());
				InputStream fileIS = new FileInputStream(file);
				zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
	
				byte[] dados = new byte[TAMANHO_BUFFER];
				
	            int len = 0;
	            while((len = fileIS.read(dados, 0, TAMANHO_BUFFER)) > 0) {
	            	zipOutputStream.write(dados, 0, len);
	            }
	                
				zipOutputStream.closeEntry();
				fileIS.close();
				file.delete(); // para deletar do tmp
			}
			
			zipOutputStream.close();
			
			return zipFile.getAbsolutePath();
			
		} catch (IOException e) {
			getLogger().error(e.getMessage(), e);
			throw new ApplicationBusinessException(ClinicaPorProcedimentoZIPRNExceptionCode.ERRO_REL_CLIN_POR_PROCED_GERAR_CSV_ZIP);
		}	
	}
	
}
