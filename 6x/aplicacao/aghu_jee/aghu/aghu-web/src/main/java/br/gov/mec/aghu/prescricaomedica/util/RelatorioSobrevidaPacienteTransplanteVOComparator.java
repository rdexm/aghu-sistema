package br.gov.mec.aghu.prescricaomedica.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.transplante.vo.RelatorioSobrevidaPacienteTransplanteVO;

public class RelatorioSobrevidaPacienteTransplanteVOComparator implements Comparator<RelatorioSobrevidaPacienteTransplanteVO> {

	private String propriedade;
	
	private final String DATA = "dataTransplante";
	private final String SOBREVIDA = "sobrevida";
	private static final Log LOG = LogFactory.getLog(RelatorioSobrevidaPacienteTransplanteVOComparator.class);
	
	public RelatorioSobrevidaPacienteTransplanteVOComparator(String propriedade){
		this.propriedade = propriedade;
	}


	@Override
	public int compare(RelatorioSobrevidaPacienteTransplanteVO r1,
			RelatorioSobrevidaPacienteTransplanteVO r2) {
		if(propriedade.equals(DATA)){
			SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
			int retorno = 0;
			try {
				if((r1.getDataTransplante() != null && !"".equals(r1.getDataTransplante()))
						&& (r2.getDataTransplante() != null && !"".equals(r2.getDataTransplante()))){
					Date d1 = d.parse(r1.getDataTransplante());
					Date d2 = d.parse(r2.getDataTransplante());
				retorno  = d1.before(d2) ? -1 : 1;
				}else{
					return 0;
				}
			} catch (ParseException e) {
				LOG.error(e.getMessage());
			}
			return retorno;
		}else if(propriedade.equals(SOBREVIDA)){
			if((r1.getSobrevida() != null && !"".equals(r1.getSobrevida())) && (r2.getSobrevida() != null && !"".equals(r2.getSobrevida()))){
				if( Integer.valueOf(r1.getSobrevida()).compareTo(Integer.valueOf(r2.getSobrevida())) >  0 ) {
					return 1;
				}else { 
					return -1;
				}
			}
		}
		return 0;
	}
	
	
}
