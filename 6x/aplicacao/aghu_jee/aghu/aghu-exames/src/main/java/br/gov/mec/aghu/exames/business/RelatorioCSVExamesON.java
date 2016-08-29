package br.gov.mec.aghu.exames.business;

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
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.vo.RelatorioMapaLaminasVO;
import br.gov.mec.aghu.model.AelCestoPatologia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioCSVExamesON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioCSVExamesON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IExamesPatologiaFacade examesPatologiaFacade;

	private static final long serialVersionUID = 4062266481609587577L;
	
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioMapaLaminasVO(final Date dtReferencia, final AelCestoPatologia cesto) throws IOException, ApplicationBusinessException{
		List<RelatorioMapaLaminasVO> colecao = getExamesPatologiaFacade().pesquisarRelatorioMapaLaminasVO(dtReferencia, cesto);

		final File file = File.createTempFile(DominioNomeRelatorio.AELR_MAPA_LAMINA.toString(), EXTENSAO);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		String auxCesto=null;
		for (final RelatorioMapaLaminasVO vo : colecao) {
			
			if(!vo.getCesto().equals(auxCesto)){
				out.write("Cesto:"+SEPARADOR+vo.getCesto() + getLineSeparator() +
						"Nº AP"+SEPARADOR+"Nº Cápsulas"+SEPARADOR+"Nº Fragmentos"+SEPARADOR+"Descrição"+SEPARADOR+"Coloração"+SEPARADOR+"Residente"+SEPARADOR+"Observação"+SEPARADOR+getLineSeparator()
						 );
				auxCesto = vo.getCesto();
			}
			
			out.write( (vo.getNumeroAp() != null ? vo.getNumeroAp() : "") + SEPARADOR +
					   (vo.getNumeroCapsula() != null ? vo.getNumeroCapsula() : "" )		+ SEPARADOR +
					   (vo.getNumeroFragmento() != null ? vo.getNumeroFragmento() : "")		+ SEPARADOR +
					   (vo.getDescricao() != null ? vo.getDescricao() : "" )	 	 	    + SEPARADOR +
					   (vo.getColoracao() != null ? vo.getColoracao() : "" ) 			    + SEPARADOR +
					   (vo.getResidente() != null ? vo.getResidente() : "" )              	+ SEPARADOR +
                       (vo.getObservacao() != null ? vo.getObservacao() : "" )             	+ SEPARADOR + getLineSeparator()
					 );
		}

		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return this.examesPatologiaFacade;
	}
	
	private String getLineSeparator() {
		String lineSeparator = System.getProperty("line.separator");
		if (lineSeparator == null) {
			lineSeparator = "\n";
		}
		return lineSeparator;
	}
}
