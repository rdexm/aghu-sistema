package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.vo.AihFaturadaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioAihFaturadaON extends BaseBusiness {

private static final String DD_MM_YYYY = "dd/mm/yyyy";

private static final Log LOG = LogFactory.getLog(RelatorioAihFaturadaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade faturamentoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3969534301387807364L;
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";

	public String gerarCSV(Integer cthSeq, Integer prontuario, Integer mes, Integer ano, 
			Date dthrInicio, Long codTabelaIni, Long codTabelaFim, String iniciais, Boolean reapresentada) throws IOException, BaseException{
		
		List<AihFaturadaVO> aihs = getFaturamentoFacade().listarEspelhoAihFaturada(cthSeq, prontuario, mes, ano, dthrInicio, codTabelaIni, codTabelaFim, iniciais, reapresentada);
		
		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_AIH_POR_SSM.toString(), EXTENSAO);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		Long iphCodSusRealiz = null;
		Integer prontuarioAnterior = null;
		int nroProntuarios = 0;
		int nroTotalProntuarios = 0;
		int total = 0;
		
		for(AihFaturadaVO aih : aihs) {
			total++;
			if(iphCodSusRealiz == null || aih.getIphCodSusRealiz().equals(iphCodSusRealiz)) {
				out.write(aih.getProntuario()+SEPARADOR+aih.getPacNome()+SEPARADOR+aih.getCidade()+SEPARADOR+DateUtil.obterDataFormatada(aih.getDataIternacao(), DD_MM_YYYY)+SEPARADOR+DateUtil.obterDataFormatada(aih.getDataAlta(), DD_MM_YYYY)+SEPARADOR+aih.getNroAih()+SEPARADOR+aih.getIphCodSusRealiz()+"\n");
				if(prontuarioAnterior == null || (aih.getProntuario() != null && !aih.getProntuario().equals(prontuarioAnterior))) {
					nroProntuarios++;
					nroTotalProntuarios++;
				}
				prontuarioAnterior = aih.getProntuario();
				iphCodSusRealiz = aih.getIphCodSusRealiz();
			}
			else {
				if(aih.getProntuario() != null && !aih.getProntuario().equals(prontuarioAnterior)) {
					nroTotalProntuarios++;
				}				
				if(total > 1) {
					out.write("Total por procedimento"+SEPARADOR+nroProntuarios+"\n");
				}
				out.write("Procedimento"+SEPARADOR+aih.getDescricaoProcedimento()+"\n");
				out.write("Prontuário"+SEPARADOR+"Paciente"+SEPARADOR+"Cidade"+SEPARADOR+"Internação"+SEPARADOR+"Alta"+SEPARADOR+"Nro AIH"+SEPARADOR+"SSM\n");
				out.write(aih.getProntuario()+SEPARADOR+aih.getPacNome()+SEPARADOR+aih.getCidade()+SEPARADOR+DateUtil.obterDataFormatada(aih.getDataIternacao(), DD_MM_YYYY)+SEPARADOR+DateUtil.obterDataFormatada(aih.getDataAlta(), DD_MM_YYYY)+SEPARADOR+aih.getNroAih()+SEPARADOR+aih.getIphCodSusRealiz()+"\n");
				prontuarioAnterior = aih.getProntuario();
				iphCodSusRealiz = aih.getIphCodSusRealiz();
				nroProntuarios = 1;
			}
			
			if(total == aihs.size()) {
				out.write("Total por procedimento"+SEPARADOR+nroProntuarios+"\n");
				out.write("Total de contas"+SEPARADOR+nroTotalProntuarios+"\n");
			}
		}
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

}
