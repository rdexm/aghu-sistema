package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.vo.ArquivoSUSVO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Created by renan_boni on 24/12/14.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class GeracaoArquivoParcialCompetenciaInternacaoON extends BaseBMTBusiness implements Serializable {

    private static final Log LOG = LogFactory.getLog(GeracaoArquivoParcialCompetenciaInternacaoON.class);
    public final static int TRANSACTION_TIMEOUT_24_HORAS = 60 * 60 * 24; //= 1 dia

    @EJB
    private GeracaoArquivoFaturamentoCompetenciaInternacaoRN geracaoArquivoFaturamentoCompetenciaInternacaoRN;

    @Override
    @Deprecated
    protected Log getLogger() {
        return LOG;
    }

    public ArquivoURINomeQtdVO gerarArquivoParcialNew(final FatCompetencia competencia, final Date dataEncIni, final Date dataEncFinal) throws BaseException, IOException {
    	ArquivoURINomeQtdVO arquivoVO = null;
    	try {
    		beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
    		LOG.info("Iniciado processo longo gerarArquivoParcialNew()");
    		final List<ArquivoSUSVO> arqs = getGeracaoArquivoFaturamentoCompetenciaInternacaoRN().
    				gerarArquivoFaturamentoParcialSUSNew(competencia, dataEncIni, dataEncFinal);
    		
    		arquivoVO = this.gerarArquivoFaturamentoParcialSUSNew(competencia, arqs);
    		LOG.info("Terminado processo longo gerarArquivoParcialNew()");
    		commitTransaction();
		} catch (ApplicationBusinessException e) {
			rollbackTransaction();
			throw e;
		}
        return arquivoVO;
    }

    public ArquivoURINomeQtdVO gerarArquivoFaturamentoParcialSUSNew(final FatCompetencia competencia, final List<ArquivoSUSVO> arqs) throws IOException{
        File file = null;
        File zipFile = null;
        ZipOutputStream zip = null;
        byte[] buf = new byte[1024];

        //GERAR ZIP
        zipFile = new File("Arquivo_Competencia_" + String.format("%02d-%04d",competencia.getId().getMes(), competencia.getId().getAno()) + ".zip");
        zip = new ZipOutputStream(new FileOutputStream(zipFile));


        for (ArquivoSUSVO arquivoSUSVO : arqs) {
            file = new File(arquivoSUSVO.getNomeArquivo() + ".txt");

            final Writer out = new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1");
            for (String linha : arquivoSUSVO.getLinhas()) {
                out.write(linha+"\n");
            }

            out.flush();
            out.close();

            FileInputStream in = new FileInputStream(file);
            zip.putNextEntry(new ZipEntry(file.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
            zip.closeEntry();
            in.close();
        }

        zip.close();

        return new ArquivoURINomeQtdVO(zipFile.toURI(), zipFile.getName(), 10, 1);
    }

    protected GeracaoArquivoFaturamentoCompetenciaInternacaoRN getGeracaoArquivoFaturamentoCompetenciaInternacaoRN() {

        return geracaoArquivoFaturamentoCompetenciaInternacaoRN;
    }
}
